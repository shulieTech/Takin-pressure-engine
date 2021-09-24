/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.flpt.pressure.engine.plugin.jmeter.script;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.flpt.pressure.engine.api.ability.SupportedPressureModeAbilities;
import io.shulie.flpt.pressure.engine.api.ability.model.*;
import io.shulie.flpt.pressure.engine.api.annotation.GlobalParamKey;
import io.shulie.flpt.pressure.engine.api.annotation.HttpHeaderParamKey;
import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;
import io.shulie.flpt.pressure.engine.api.entity.BusinessActivity;
import io.shulie.flpt.pressure.engine.api.entity.GlobalUserVariables;
import io.shulie.flpt.pressure.engine.api.entity.HttpHeaderVariables;
import io.shulie.flpt.pressure.engine.api.enums.EnginePressureMode;
import io.shulie.flpt.pressure.engine.api.enums.PressureTestMode;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.plugin.jmeter.consts.JmeterConstants;
import io.shulie.flpt.pressure.engine.plugin.jmeter.util.JmeterPluginUtil;
import io.shulie.flpt.pressure.engine.plugin.jmeter.util.XpathUtils;
import io.shulie.flpt.pressure.engine.util.JsonUtils;
import io.shulie.flpt.pressure.engine.util.NumberUtils;
import io.shulie.flpt.pressure.engine.util.StringUtils;
import io.shulie.flpt.pressure.engine.util.SystemResourceUtil;
import io.shulie.flpt.pressure.engine.util.TryUtils;
import io.shulie.flpt.pressure.engine.util.http.HttpNotifyTakinCloudUtils;
import io.shulie.jmeter.tool.redis.RedisConfig;
import io.shulie.jmeter.tool.redis.RedisUtil;
import io.shulie.takin.constants.TakinRequestConstant;
import org.dom4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by xuyh at 2020/5/12 21:32.
 */
@SuppressWarnings("all")
public class ScriptModifier {
    private static Logger logger = LoggerFactory.getLogger(ScriptModifier.class);

    //全局参数是否已经添加
    private static boolean globalArgumentsAdded = false;

    //后端监听器参数是否已经添加
    private static boolean backendListenerAdded = false;

    //校验包含
    private static boolean arrayContains(int[] arrays, int value) {
        if(arrays != null && arrays.length > 0) {
            for(int arr : arrays) {
                if(arr == value) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改脚本
     *
     * @param document
     * @param context
     * @param pressurePlugin
     */
    public static void modifyDocument(Document document, PressureContext context
            , SupportedPressureModeAbilities supportedPressureModeAbilities) {
        //场景id string
        String sceneIdString = context.getSceneId() + "";
        String reportIdString = context.getReportId() + "";
        String customerIdString = context.getCustomerId() + "";

        //当前引擎压测模式
        EnginePressureMode currentEnginePressureMode = context.getCurrentEnginePressureMode();

        //处理压测数据
        List<Map<String, Object>> csvConfigs = context.getDataFileSets();

        //解析脚本
        Element root = document.getRootElement();
        List<String> jarFilePathList = context.getJarFilePathList();
        // 第一层
        List<Element> hashTreeElements = root.elements("hashTree");

        forbidResultCollector(hashTreeElements);

        for (Element hashTreeElement : hashTreeElements) {
            // *********************************TestPlan********************************
            List<Element> testPlanElements = hashTreeElement.elements("TestPlan");
            for (Element testPlanElement : testPlanElements) {
                List<Element> stringPropElements = testPlanElement.elements("stringProp");
                for (Element stringPropElement : stringPropElements) {
                    Attribute nameAttr = stringPropElement.attribute("name");
                    String nameAttrValue = nameAttr.getValue();
                    if (nameAttrValue != null && nameAttrValue.equals("TestPlan.user_define_classpath")) {
                        // jar
                        if (jarFilePathList != null && !jarFilePathList.isEmpty()) {
                            try {
                                stringPropElement.setText(buildJarFilePathListString(jarFilePathList));
                            } catch (Exception e) {
                                logger.warn(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
            // *********************************TestPlan********************************
            // 第二层
            List<Element> hashTree2Elements = hashTreeElement.elements("hashTree");
            // HTTP信息头管理器 第二层以上包括第二层的xml中
            for (Element hashTree2Element : hashTree2Elements) {
                // 添加线程组
                threadGroupModify(hashTree2Element, context, supportedPressureModeAbilities);

                // 添加 Local file
                if(csvConfigs != null) {
                    csvPathModify(csvConfigs, hashTree2Element, context.getPodCount());
                }

                // 获取第三层
                List<Element> hashTree3Elements = hashTree2Element.elements("hashTree");
                for (Element hashTree3Element : hashTree3Elements) {
                    // todo 这里过滤下，只在多线程组中增加后端监控器
                    addBackEndListener(hashTree3Element, sceneIdString
                        , reportIdString, customerIdString, context);

                    // 只有巡检模式添加固定定时器
                    if (EnginePressureMode.INSPECTION_MODE == currentEnginePressureMode) {
                        logger.info("组装巡检模式添加固定定时器和断言");
                        // 循环时间
                        //addFixedTimer(hashTree3Element, enginePressureParams.get("fixed_timer"));
                        //modify by lipeng 20210609
                        // 固定定时器只能按每个请求等待固定时间，这里巡检的需求是每个线程组每隔固定时间请求，所以这里改为flow controller action组件
                        addFlowControllerAction(hashTree3Element, context.getEnginePressureParams().get("fixed_timer"));
                        //modify end;
                        // 增加判断断言
                        addFeanShellAssertion(hashTree3Element);
                    }
                    // add by lipeng  添加试跑模式
                    else if (EnginePressureMode.TRY_RUN == currentEnginePressureMode) {
                        logger.info("试跑模式开启..");
                    }
                }

                // 添加全局参数 add by lipeng
                // 全局参数只用添加一个，均可获取到
                if (!globalArgumentsAdded) {
                    addGlobalArguments(hashTree2Element, context.getGlobalUserVariables(), currentEnginePressureMode);
                    // add by lipeng  添加traceId生成和添加到http header
                    // 全局参数只用添加一个，均可获取到
                    // 添加BeanShell 预处理程序 增加traceId生成
                    //不使用beanshell改为程序内，beanshell性能较差
                    //addTraceIdBeanShellPreProcessor(hashTree2Element, currentEnginePressureMode);
                    // 添加请求头管理器
                    addHttpHeaderParams(hashTree2Element, context.getHttpHeaderVariables(), currentEnginePressureMode);
                    // add end
                    globalArgumentsAdded = true;
                }
                // add end
            }
        }

        // add by lipeng 如果是TPS模式 存在多个业务活动，需要添加吞吐量控制器
        if (EnginePressureMode.TPS == currentEnginePressureMode) {
            List<BusinessActivity> businessActivities = context.getBusinessActivities();
            //只有是业务流程 也就是业务活动大于1个的时候才需要添加吞吐量控制器
            if (null == businessActivities || businessActivities.size()<=0) {
                return;
            }
            int tpsThreadMode = NumberUtils.parseInt(context.getEnginePressureParams().get("tpsThreadMode"));
            if (0 == tpsThreadMode) {
                //总的目标tps
                double tpsTargetLevel = NumberUtils.parseDouble(context.getEnginePressureParams().get("tpsTargetLevel"));
                //目标增大因子（保证tps在目标之上）,默认0.1即增大10%
                double tpsTargetLevelFactor = NumberUtils.parseDouble(context.getEnginePressureParams().get("tpsTargetLevelFactor"), 0.1d);
                //添加常量吞吐量控制器
                addConstantsThroughputControl(root, tpsTargetLevel, businessActivities, tpsTargetLevelFactor);
            } else {
                addThroughputControl(root, businessActivities);
            }
        }
        // add end

        //add by zhaoyong 如果是流量调试模式，将所有压测标去除掉
        if (EnginePressureMode.FLOW_DEBUG == currentEnginePressureMode){
            updateJmxHttpPressTestTags(document);
            updateXmlDubboPressTestTags(document);
        }
    }


    /**
     * 给请求添加常量吞吐量控制器
     *
     * @param root               rootElement
     * @param businessActivities 所有业务活动信息
     * @author yuanba
     */
    public static void addConstantsThroughputControl(Element root, double tpsTargetLevel,  List<BusinessActivity> businessActivities, double tpsTargetLevelFactor) {
        // elementTestName对应的百分比
        Map<String, String> businessActivityMap = businessActivities.stream().filter(Objects::nonNull)
            .collect(Collectors.toMap(BusinessActivity::getElementTestName,BusinessActivity::getThroughputPercent));

        // 需要的所有属性值
        List<String> testNameValues = businessActivities
            .stream().map(BusinessActivity::getElementTestName).collect(Collectors.toList());

        // 根据需要的testname属性的属性值 获取所有满足element
        List<Element> sampleElements = getAllElementByAttribute(root, "testname", testNameValues);
        // 找到数据才做处理
        if (sampleElements != null && sampleElements.size() > 0) {
            for (Element sampleElement : sampleElements) {
                String testNameValue = sampleElement.attributeValue("testname");
                //当前业务活动tps占比
                double throughputPercent = NumberUtils.parseDouble(businessActivityMap.get(testNameValue))/100d;
                //求1分钟的并发数,1.1是原来的目标的基础上加10%
                double throughput = tpsTargetLevel * 60 * throughputPercent;
                //如果上浮因子大于5，则表示固定上浮这个数，小于等于5表示上浮百分比
                throughput += tpsTargetLevelFactor > 5 ? tpsTargetLevelFactor : throughput * tpsTargetLevelFactor;
                //给每一个采样器添加常量吞吐量控制器
                addEachConstantsThroughputControl(sampleElement, testNameValue, throughput, throughputPercent, tpsTargetLevelFactor);
            }
        } else {
            logger.warn("根据testname未找到对应的采样器元素。");
        }
    }

    /**
     * 给每一个sampleElement添加常量吞吐量控制器
     *
     * 逻辑：
     * 1. 校验采样器是否存在
     * 2. 根据采样器获取其父节点，也就是采样器所在的hashTree
     * 3. 获取采样器的子节点信息的hashTree
     * 4. 在采样器父节点下面创建吞吐量控制器
     * 5. 在采样器父节点下面再创建吞吐量控制器的hashTree
     * 6. 在吞吐量控制器的hashTree下添加采样器和采样器的hashTree的克隆副本
     * 7. 将原先在采样器父节点下的采样器和采样器的hashTree移除
     *
     * @param sampleElement  sampleElement是传来的采样器，一般是HTTPSamplerProxy 或者 dubbo kafka之类的。
     * @param sampleTestname 取样器testname
     * @param throughput     每分钟常量吞吐量
     */
    public static void addEachConstantsThroughputControl(Element sampleElement, String sampleTestname, Double throughput, Double throughputPercent, Double tpsFactor) {
        // 1. 校验采样器是否存在
        if (sampleElement == null) {
            logger.error("sampleElement is null");
            return;
        }
        // 2. 根据采样器获取其父节点，也就是采样器所在的hashTree。
        Element sampleParent = sampleElement.getParent();

        // 3. 获取采样器的子节点信息的hashTree
        boolean isSampleElementHashTree = false;
        Element sampleElementHashTree = null;
        for (Object obj : sampleParent.elements()) {
            Element ele = (Element)obj;
            if (ele == null) {
                continue;
            }
            if (isSampleElementHashTree) {
                sampleElementHashTree = ele;
                break;
            }
            //如果元素是我们传来的元素 那么下一个遍历的将会是他的hashTree
            if (Objects.equals(ele.attributeValue("testname"), sampleTestname)) {
                isSampleElementHashTree = true;
            }
        }
        if (sampleElementHashTree == null) {
            logger.warn("脚本文件有误，请排查");
            // 4. 在采样器父节点下面创建hashTree
            sampleElementHashTree = sampleParent.addElement("hashTree");
        }

        // 5. 在采样器父节点下面创建常量吞吐量控制器
        Element constantThroughputTimer = sampleElementHashTree.addElement("ConstantThroughputTimer");
        constantThroughputTimer.addAttribute("guiclass", "TestBeanGUI");
        constantThroughputTimer.addAttribute("testclass", "ConstantThroughputTimer");
        constantThroughputTimer.addAttribute("testname", getSampleThroughputControllerTestname(sampleTestname));
        constantThroughputTimer.addAttribute("enabled", "true");

        addPropElement(constantThroughputTimer, "calcMode", 3);
        addPropElement(constantThroughputTimer, "throughput", throughput);
        if (null != throughputPercent) {
            addPropElement(constantThroughputTimer, "percent", throughputPercent);
        }
        if (null != tpsFactor) {
            addPropElement(constantThroughputTimer, "tpsFactor", tpsFactor);
        }
    }

    /**
     * 添加prop元素
     */
    public static void addPropElement(Element container, String name, Object value) {
        if (null == value) {
            return;
        }
        if (value instanceof Double || value instanceof Float) {
            Element node = container.addElement("doubleProp");
            node.addElement("name").setText(name);
            node.addElement("value").setText(StringUtils.valueOf(value));
        } else if (value instanceof Integer || value instanceof Long) {
            container.addElement("intProp").addAttribute("name", name).setText(StringUtils.valueOf(value));
        } else if (value instanceof String) {
            container.addElement("stringProp").addAttribute("name", name).setText(StringUtils.valueOf(value));
        }
    }


    /**
     * 添加http header
     */
    private static void addHttpHeaderParams(Element element, HttpHeaderVariables httpHeaderVariables,
        EnginePressureMode currentEnginePressureMode) {
        //节点属性
        Map<String, String> elementAttributes = Maps.newHashMap();
        elementAttributes.put("guiclass", "HeaderPanel");
        elementAttributes.put("testclass", "HeaderManager");
        elementAttributes.put("testname", "HTTP-Header-Manager");
        elementAttributes.put("enabled", "true");
        Element traceIdHttpHeader = createElement(element
            , "HeaderManager"
            , elementAttributes, null);
        //collection
        Element collectionProp = traceIdHttpHeader.addElement("collectionProp");
        collectionProp.addAttribute("name", "HeaderManager.headers");
        if (httpHeaderVariables != null) {
            Field[] declaredFields = httpHeaderVariables.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                HttpHeaderParamKey anno = declaredField.getAnnotation(HttpHeaderParamKey.class);
                //未标注GlobalParamKey的字段不作处理
                if (anno != null) {
                    //校验是否专属参数
                    EnginePressureMode[] assignForModes = anno.assignForMode();
                    //只有指定了专属参数属性才进行校验
                    if (assignForModes.length > 0) {
                        //校验本次引擎压测模式是否在指定引擎压测模式中
                        boolean inAssign = Arrays.asList(assignForModes)
                            .contains(currentEnginePressureMode);
                        //如果属性不在指定参数内 则过滤掉
                        if (!inAssign) {
                            continue;
                        }
                    }
                    //先取注解值为key
                    String paramKey = anno.value();
                    //如果注解值为空则取属性名
                    if (StringUtils.isBlank(paramKey)) {
                        paramKey = declaredField.getName();
                    }
                    String value = "";
                    try {
                        declaredField.setAccessible(true);
                        Object v = declaredField.get(httpHeaderVariables);
                        if (Objects.isNull(v)) {
                            continue;
                        }
                        value = String.valueOf(v);
                    } catch (IllegalAccessException e) {
                        logger.warn("HttpHeaderVariables 参数转换异常，参数名 - {}", declaredField.getName());
                    }

                    //http header添加参数
                    Element elementProp = collectionProp.addElement("elementProp");
                    elementProp.addAttribute("name", EngineConstants.EMPTY_TEXT);
                    elementProp.addAttribute("elementType", "Header");
                    //每个elementProp有两个stringProp
                    Element headNameProp = elementProp.addElement("stringProp");
                    headNameProp.addAttribute("name", "Header.name");
                    headNameProp.setText(paramKey);
                    Element headValueProp = elementProp.addElement("stringProp");
                    headValueProp.addAttribute("name", "Header.value");
                    headValueProp.setText(value);
                }
            }
        }

        //添加hashtree
        element.addElement("hashTree");
    }

    /**
     * 添加traceId生成器
     *
     * @param hashTree3Element
     */
    private static void addTraceIdBeanShellPreProcessor(Element element, EnginePressureMode currentEnginePressureMode) {
        //节点属性
        Map<String, String> elementAttributes = Maps.newHashMap();
        elementAttributes.put("guiclass", "TestBeanGUI");
        elementAttributes.put("testclass", "BeanShellPreProcessor");
        elementAttributes.put("testname", "BeanShell-预处理程序");
        elementAttributes.put("enabled", "true");
        //节点prop
        List<ElementProp> elementProps = Lists.newArrayList();
        ElementProp filenameProp = ElementProp.create("stringProp", "filename", "");
        ElementProp parametersProp = ElementProp.create("stringProp", "parameters", "");
        ElementProp resetInterpreterProp = ElementProp.create("boolProp", "resetInterpreter", "false");
        //正常生成traceId
        String scriptString = "JmeterTraceIdGenerator.generate()";
        //如果是试跑模式，则调用生成全采样traceId方法
        if (EnginePressureMode.TRY_RUN == currentEnginePressureMode) {
            scriptString = "JmeterTraceIdGenerator.generateAllSampled()";
        }
        ElementProp scriptProp = ElementProp.create("stringProp", "script"
            , "import org.apache.jmeter.shulie.util.JmeterTraceIdGenerator;\n" +
                "vars.put(" + JmeterPluginUtil.QUOTE_REPLACEMENT + "pradarTraceId" + JmeterPluginUtil.QUOTE_REPLACEMENT + ", "
                + scriptString + ");");
        elementProps.add(filenameProp);
        elementProps.add(parametersProp);
        elementProps.add(resetInterpreterProp);
        elementProps.add(scriptProp);
        //创建traceIdBeanShellPreProcessor
        Element traceIdBeanShellPreProcessor = createElement(element
            , "BeanShellPreProcessor"
            , elementAttributes
            , elementProps);
        //添加一个hashTree
        element.addElement("hashTree");
    }

    /**
     * 创建新的节点
     *
     * @param parent
     * @param elementName
     * @param elementAttributes
     * @return
     */
    private static Element createElement(Element parent
        , String elementName
        , Map<String, String> elementAttributes
        , List<ElementProp> elementProps) {
        Element ele = parent.addElement(elementName);
        //元素属性
        if (elementAttributes != null && elementAttributes.size() > 0) {
            for (Map.Entry<String, String> entry : elementAttributes.entrySet()) {
                ele.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        //元素内prop
        if (elementProps != null && elementProps.size() > 0) {
            for (ElementProp prop : elementProps) {
                Element eleProp = ele.addElement(prop.getPropName());
                eleProp.addAttribute("name", prop.getName());
                eleProp.setText(prop.getValue());
            }
        }
        return ele;
    }

    private static void addFeanShellAssertion(Element element) {
        Element beanShellAssertion = element.addElement("BeanShellAssertion");
        beanShellAssertion.addAttribute("guiclass", "BeanShellAssertionGui");
        beanShellAssertion.addAttribute("testclass", "BeanShellAssertion");
        beanShellAssertion.addAttribute("testname", "BeanShell Assertion");
        beanShellAssertion.addAttribute("enabled", "true");
        Element bbeanShellStringProp1 = beanShellAssertion.addElement("stringProp");
        bbeanShellStringProp1.addAttribute("name", "BeanShellAssertion.query");
        //modify by lipeng 特殊标记改为QUOTE_REPLACEMENT
        bbeanShellStringProp1.setText("log.info(" + JmeterPluginUtil.QUOTE_REPLACEMENT + "Current Sample Is Requested..."
            + JmeterPluginUtil.QUOTE_REPLACEMENT + ")");
        //        bbeanShellStringProp1.setText("log.info(\"hahahaha...\")");
        Element bbeanShellStringProp2 = beanShellAssertion.addElement("stringProp");
        bbeanShellStringProp2.addAttribute("name", "BeanShellAssertion.filename");
        Element bbeanShellStringProp3 = beanShellAssertion.addElement("stringProp");
        bbeanShellStringProp3.addAttribute("name", "BeanShellAssertion.parameters");
        Element bbeanShellStringProp4 = beanShellAssertion.addElement("boolProp");
        bbeanShellStringProp4.addAttribute("name", "BeanShellAssertion.resetInterpreter");
        bbeanShellStringProp4.setText("false");
        element.addElement("hashTree");
    }

    private static void addFixedTimer(Element element, Object delayTime) {
        Element constantTimer = element.addElement("ConstantTimer");
        constantTimer.addAttribute("guiclass", "ConstantTimerGui");
        constantTimer.addAttribute("testclass", "ConstantTimer");
        constantTimer.addAttribute("testname", "固定定时器");
        constantTimer.addAttribute("enabled", "true");
        Element elementProp = constantTimer.addElement("stringProp");
        elementProp.addAttribute("name", "ConstantTimer.delay");
        elementProp.setText(String.valueOf(delayTime == null ? "5000" : delayTime));
        element.addElement("hashTree");
    }

    /**
     * 添加flow controller action
     *
     * @param element
     * @param delayTime
     */
    private static void addFlowControllerAction(Element element, Object delayTime) {
        Element flowControllerAction = element.addElement("TestAction");
        flowControllerAction.addAttribute("guiclass", "TestActionGui");
        flowControllerAction.addAttribute("testclass", "TestAction");
        flowControllerAction.addAttribute("testname", "Flow-Control-Action");
        flowControllerAction.addAttribute("enabled", "true");
        Element intProp1 = flowControllerAction.addElement("intProp");
        intProp1.addAttribute("name", "ActionProcessor.action");
        intProp1.setText("1"); //线程等待
        Element intProp2 = flowControllerAction.addElement("intProp");
        intProp2.addAttribute("name", "ActionProcessor.target");
        intProp2.setText("0"); //不杀死线程
        Element stringProp = flowControllerAction.addElement("stringProp");
        stringProp.addAttribute("name", "ActionProcessor.duration");
        //默认等待5s
        stringProp.setText(String.valueOf(delayTime == null ? "5000" : delayTime));
        element.addElement("hashTree");
    }

    /**
     * 添加全局参数
     *
     * @param hashTree2Element    第二层的hashTree
     * @param globalUserVariables 参数信息
     * @author lipeng
     */
    private static void addGlobalArguments(Element hashTree2Element
        , GlobalUserVariables globalUserVariables
        , EnginePressureMode currentEnginePressureMode) {
        //如果参数为空 直接返回
        if (globalUserVariables == null) {
            logger.warn("添加Jmeter全局参数失败，jmeterGlobalUserVariables is null");
            return;
        }
        Element argumentsPanel = hashTree2Element.addElement("Arguments");
        argumentsPanel.addAttribute("guiclass", "ArgumentsPanel");
        argumentsPanel.addAttribute("testclass", "Arguments");
        argumentsPanel.addAttribute("testname", "engineGlobalArguments");
        argumentsPanel.addAttribute("enabled", "true");
        Element collectionProp = argumentsPanel.addElement("collectionProp");
        collectionProp.addAttribute("name", "Arguments.arguments");
        Field[] declaredFields = globalUserVariables.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            GlobalParamKey anno = declaredField.getAnnotation(GlobalParamKey.class);
            //未标注GlobalParamKey的字段不作处理
            if (anno != null) {
                //校验是否专属参数
                EnginePressureMode[] assignForModes = anno.assignForMode();
                //只有指定了专属参数属性才进行校验
                if (assignForModes.length > 0) {
                    //校验本次引擎压测模式是否在指定引擎压测模式中
                    boolean inAssign = Arrays.asList(assignForModes)
                        .contains(currentEnginePressureMode);
                    //如果属性不在指定参数内 则过滤掉
                    if (!inAssign) {
                        continue;
                    }
                }
                //先取注解值为key
                String paramKey = anno.value();
                //如果注解值为空则取属性名
                if (StringUtils.isBlank(paramKey)) {
                    paramKey = declaredField.getName();
                }
                String value = "";
                try {
                    declaredField.setAccessible(true);
                    Object v = declaredField.get(globalUserVariables);
                    if (Objects.isNull(v)) {
                        continue;
                    }
                    value = String.valueOf(v);
                } catch (IllegalAccessException e) {
                    logger.warn("JmeterGlobalUserVariables 参数转换异常，参数名 - {}", declaredField.getName());
                }

                //jmeter脚本添加全局参数
                Element elementProp = collectionProp.addElement("elementProp");
                elementProp.addAttribute("name", paramKey);
                elementProp.addAttribute("elementType", "Argument");
                //每个elementProp有三个stringProp
                Map<String, String> stringProps = Maps.newHashMap();
                stringProps.put("Argument.name", paramKey);
                stringProps.put("Argument.value", value);
                stringProps.put("Argument.metadata", "=");
                for (Map.Entry<String, String> entryStringProp : stringProps.entrySet()) {
                    Element stringProp = elementProp.addElement("stringProp");
                    stringProp.addAttribute("name", entryStringProp.getKey());
                    stringProp.setText(entryStringProp.getValue());
                }
            }
        }
        hashTree2Element.addElement("hashTree");
    }

    /**
     * 给请求添加吞吐量控制器
     *
     * @param root               rootElement
     * @param businessActivities 所有业务活动信息
     * @author lipeng
     */
    public static void addThroughputControl(Element root, List<BusinessActivity> businessActivities) {
        // elementTestName对应的百分比
        Map<String, String> businessActivityMap = businessActivities.stream()
            .collect(Collectors.toMap(row -> row.getElementTestName()
                , row -> row.getThroughputPercent()));

        // 需要的所有属性值
        List<String> testNameValues = businessActivities
            .stream().map(m -> m.getElementTestName()).collect(Collectors.toList());

        // 根据需要的testname属性的属性值 获取所有满足element
        List<Element> sampleElements = getAllElementByAttribute(root, "testname", testNameValues);
        // 找到数据才做处理
        if (sampleElements != null && sampleElements.size() > 0) {
            for (Element sampleElement : sampleElements) {
                String testNameValue = sampleElement.attributeValue("testname");
                //给每一个采样器添加吞吐量控制器
                addEachThroughputControl(sampleElement, testNameValue, businessActivityMap.get(testNameValue));
            }
        } else {
            logger.warn("根据testname未找到对应的采样器元素。");
        }
    }

    /**
     * 给每一个sampleElement添加吞吐量控制器
     *
     * 逻辑：
     * 1. 校验采样器是否存在
     * 2. 根据采样器获取其父节点，也就是采样器所在的hashTree
     * 3. 获取采样器的子节点信息的hashTree
     * 4. 在采样器父节点下面创建吞吐量控制器
     * 5. 在采样器父节点下面再创建吞吐量控制器的hashTree
     * 6. 在吞吐量控制器的hashTree下添加采样器和采样器的hashTree的克隆副本
     * 7. 将原先在采样器父节点下的采样器和采样器的hashTree移除
     *
     * @param sampleElement  sampleElement是传来的采样器，一般是HTTPSamplerProxy 或者 dubbo kafka之类的。
     * @param sampleTestname 取样器testname
     * @param percent        吞吐量百分比
     */
    public static void addEachThroughputControl(Element sampleElement, String sampleTestname, String percent) {
        // 1. 校验采样器是否存在
        if (sampleElement == null) {
            logger.error("sampleElement is null");
            return;
        }
        // 2. 根据采样器获取其父节点，也就是采样器所在的hashTree。
        Element sampleParent = sampleElement.getParent();

        // 3. 获取采样器的子节点信息的hashTree
        boolean isSampleElementHashTree = false;
        Element sampleElementHashTree = null;
        for (Object obj : sampleParent.elements()) {
            Element ele = (Element)obj;
            if (ele == null) {
                continue;
            }
            if (isSampleElementHashTree) {
                sampleElementHashTree = ele;
                break;
            }
            //如果元素是我们传来的元素 那么下一个遍历的将会是他的hashTree
            if (Objects.equals(ele.attributeValue("testname"), sampleTestname)) {
                isSampleElementHashTree = true;
            }
        }
        if (sampleElementHashTree == null) {
            logger.warn("脚本文件有误，请排查");
            return;
        }

        // 4. 在采样器父节点下面创建吞吐量控制器
        Element throughputController = sampleParent.addElement("ThroughputController");
        throughputController.addAttribute("guiclass", "ThroughputControllerGui");
        throughputController.addAttribute("testclass", "ThroughputController");
        //吞吐量名称为取样器名称+"-tc"
        throughputController.addAttribute("testname", getSampleThroughputControllerTestname(sampleTestname));
        throughputController.addAttribute("enabled", "true");
        throughputController.addElement("intProp")
            .addAttribute("name", "ThroughputController.style")
            .setText("1");
        throughputController.addElement("boolProp")
            .addAttribute("name", "ThroughputController.perThread")
            .setText("false");
        throughputController.addElement("intProp")
            .addAttribute("name", "ThroughputController.maxThroughput")
            .setText("1");
        Element floatProperty = throughputController.addElement("FloatProperty");
        floatProperty.addElement("name").setText("ThroughputController.percentThroughput");
        floatProperty.addElement("value").setText(percent + "");
        floatProperty.addElement("savedValue").setText("0.0");

        // 5. 在采样器父节点下面再创建吞吐量控制器的hashTree
        Element throughputControllerHashTree = sampleParent.addElement("hashTree");
        // 6. 在吞吐量控制器的hashTree下添加采样器和采样器的hashTree
        throughputControllerHashTree.elements().add(sampleElement.clone());
        throughputControllerHashTree.elements().add(sampleElementHashTree.clone());
        // 7. 将原先在采样器父节点下的采样器和采样器的hashTree移除
        sampleParent.remove(sampleElement);
        sampleParent.remove(sampleElementHashTree);
    }

    /**
     * 获取取样器的吞吐量控制器的testname  规则是sampleTestname+"-tc"
     *
     * @param sampleTestname
     * @return
     */
    public static String getSampleThroughputControllerTestname(String sampleTestname) {
        return sampleTestname + "-tc";
    }

    private static String buildJarFilePathListString(List<String> jarFilePathList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String path : jarFilePathList) {
            stringBuilder.append(path);
            stringBuilder.append(",");
        }
        String str = stringBuilder.toString();
        if (str.endsWith(",")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static void csvPathModify(List<Map<String, Object>> csvConfigs, Element parent, int podCount) {
        List<Element> children = parent.elements();
        for (Element child : children) {
            if (child.getName().equalsIgnoreCase("CSVDataSet")) {
                replaceCsvPath(child, csvConfigs, podCount);
            }
            csvPathModify(csvConfigs, child, podCount);
        }
    }

    private static void replaceCsvPath(Element currentElement, List<Map<String, Object>> csvConfigs, int podCount) {
        String csvFileName = null;
        List<Element> csvPropertyElements = currentElement.elements("stringProp");
        for (Element csvPropertyElement : csvPropertyElements) {
            Attribute attribute = csvPropertyElement.attribute("name");
            String value = attribute.getValue();
            if (value != null && value.equalsIgnoreCase("filename")) {
                csvFileName = csvPropertyElement.getText();
            }
        }
        Map<String, Object> csvConfig = nameMatch(csvConfigs, csvFileName);
        if (csvConfig != null) {
            // 这里的podNum 对应的是序号，存在环境变量中，不部署总数
            String podNum = System.getProperty("pod.number");
            Boolean split = TryUtils.tryOperation(() -> Boolean.parseBoolean(String.valueOf(csvConfig.get("split"))));
            String path = String.valueOf(csvConfig.get("path"));
            if (path != null) {
                List<Element> strPropElements = currentElement.elements("stringProp");
                for (Element strPropElement : strPropElements) {
                    Attribute attribute = strPropElement.attribute("name");
                    if (attribute != null && attribute.getValue() != null && attribute.getValue().equalsIgnoreCase(
                        "filename")) {
                        strPropElement.setText(path);
                    }
                }
            }
        }
    }

    private static List<MQDataConfig> planCsvSelect(List<Map<String, Object>> csvConfigs, Element planHashTree) {
        List<MQDataConfig> globalMQDataConfig = new ArrayList<>();
        List<Element> tobeRemovedElements = new ArrayList<>();
        List<Element> elements = planHashTree.elements();
        for (int i = 0; i < elements.size(); i++) {
            Element currentElement = elements.get(i);
            if (currentElement.getName().equalsIgnoreCase("CSVDataSet")) {
                String csvFileName = null;
                String variableNames = null;
                List<Element> csvPropertyElements = currentElement.elements("stringProp");
                for (Element csvPropertyElement : csvPropertyElements) {
                    Attribute attribute = csvPropertyElement.attribute("name");
                    String value = attribute.getValue();
                    if (value != null && value.equalsIgnoreCase("filename")) {
                        csvFileName = csvPropertyElement.getText();
                    }
                    if (value != null && value.equalsIgnoreCase("variableNames")) {
                        variableNames = csvPropertyElement.getText();
                    }
                }
                Map<String, Object> csvConfig = nameMatch(csvConfigs, csvFileName);
                if (csvConfig != null) {
                    MQDataConfig mqDataConfig = new MQDataConfig();
                    mqDataConfig.config = csvConfig;
                    mqDataConfig.variableNames = variableNames;
                    globalMQDataConfig.add(mqDataConfig);
                }
                tobeRemovedElements.add(currentElement);
                if (i + 1 < elements.size()) {
                    Element next = elements.get(i + 1);
                    if (next.getName().equalsIgnoreCase("hashTree")) {
                        tobeRemovedElements.add(next);
                    }
                }
            }
        }
        tobeRemovedElements.forEach(planHashTree::remove);
        return globalMQDataConfig;
    }

    private static void threadGroupCsvSelect(List<Map<String, Object>> csvConfigs,
        List<MQDataConfig> globalMQDataConfig, Element planHashTree) {
        List<Element> elements = planHashTree.elements();
        for (int i = 0; i < elements.size(); i++) {
            Element current = elements.get(i);
            if (current.getName().contains("ThreadGroup")) {
                if (i + 1 < elements.size()) {
                    Element threadGroupHashTree = elements.get(i + 1);
                    if (threadGroupHashTree.getName().equalsIgnoreCase("hashTree")) {
                        threadGroupCsvModify(csvConfigs, globalMQDataConfig, threadGroupHashTree);
                    }
                }
            }
        }
    }

    private static void threadGroupCsvModify(List<Map<String, Object>> csvConfigs,
        List<MQDataConfig> globalMQDataConfig, Element threadGroupHashTree) {
        List<Element> elements = threadGroupHashTree.elements();
        //self append to head
        List<Element> tobeRemovedElements = new ArrayList<>();
        List<MQDataConfig> innerMqDataConfig = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Element currentElement = elements.get(i);
            if (currentElement.getName().equalsIgnoreCase("CSVDataSet")) {
                String csvFileName = null;
                String variableNames = null;
                List<Element> csvPropertyElements = currentElement.elements("stringProp");
                for (Element csvPropertyElement : csvPropertyElements) {
                    Attribute attribute = csvPropertyElement.attribute("name");
                    String value = attribute.getValue();
                    if (value != null && value.equalsIgnoreCase("filename")) {
                        csvFileName = csvPropertyElement.getText();
                    }
                    if (value != null && value.equalsIgnoreCase("variableNames")) {
                        variableNames = csvPropertyElement.getText();
                    }
                }
                Map<String, Object> csvConfig = nameMatch(csvConfigs, csvFileName);
                if (csvConfig != null) {
                    MQDataConfig mqDataConfig = new MQDataConfig();
                    mqDataConfig.config = csvConfig;
                    mqDataConfig.variableNames = variableNames;
                    innerMqDataConfig.add(mqDataConfig);
                }
                tobeRemovedElements.add(currentElement);
                if (i + 1 < elements.size()) {
                    Element next = elements.get(i + 1);
                    if (next.getName().equalsIgnoreCase("hashTree")) {
                        tobeRemovedElements.add(next);
                    }
                }
            }
        }
        if (innerMqDataConfig != null && !innerMqDataConfig.isEmpty()) {
            for (int i = 0; i < innerMqDataConfig.size(); i++) {
                MQDataConfig config = innerMqDataConfig.get(i);
                String nameSrvAddr = TryUtils.tryOperation(() -> String.valueOf(config.config.get("nameSrvAddr")));
                String topic = TryUtils.tryOperation(() -> String.valueOf(config.config.get("topic")));
                String group = TryUtils.tryOperation(() -> String.valueOf(config.config.get("group")));
                Element javaSampler = createJavaSamplerElement(nameSrvAddr, topic, group, 30_000L);
                Element hashTree = createHashTreeAfterJavaSamplerElement(config.variableNames);
                elements.add(i, javaSampler);
                elements.add(i + 1, hashTree);
            }
        }
        tobeRemovedElements.forEach(threadGroupHashTree::remove);

        //global append to head
        elements = threadGroupHashTree.elements();
        if (globalMQDataConfig != null && !globalMQDataConfig.isEmpty()) {
            for (int i = 0; i < globalMQDataConfig.size(); i++) {
                MQDataConfig config = globalMQDataConfig.get(i);
                String nameSrvAddr = TryUtils.tryOperation(() -> String.valueOf(config.config.get("nameSrvAddr")));
                String topic = TryUtils.tryOperation(() -> String.valueOf(config.config.get("topic")));
                String group = TryUtils.tryOperation(() -> String.valueOf(config.config.get("group")));
                Element javaSampler = createJavaSamplerElement(nameSrvAddr, topic, group, 30_000L);
                Element hashTree = createHashTreeAfterJavaSamplerElement(config.variableNames);
                elements.add(i, javaSampler);
                elements.add(i + 1, hashTree);
            }
        }
    }

    private static Element createJavaSamplerElement(String nameServAddr,
        String topic,
        String group,
        Long pollTimeout) {
        Element javaSampler = DocumentHelper.createElement("JavaSampler");
        javaSampler.addAttribute("guiclass", "JavaTestSamplerGui");
        javaSampler.addAttribute("testclass", "JavaSampler");
        javaSampler.addAttribute("testname", "MQ-data");
        javaSampler.addAttribute("enabled", "true");
        Element elementProp = javaSampler.addElement("elementProp");
        elementProp.addAttribute("name", "arguments");
        elementProp.addAttribute("elementType", "Arguments");
        elementProp.addAttribute("guiclass", "ArgumentsPanel");
        elementProp.addAttribute("testclass", "Arguments");
        elementProp.addAttribute("enabled", "true");
        Element collectionProp = elementProp.addElement("collectionProp");
        collectionProp.addAttribute("name", "Arguments.arguments");
        Element elementProp1 = collectionProp.addElement("elementProp");
        elementProp1.addAttribute("name", "group");
        elementProp1.addAttribute("elementType", "Argument");
        Element stringProp11 = elementProp1.addElement("stringProp");
        stringProp11.addAttribute("name", "Argument.name");
        stringProp11.setText("group");
        Element stringProp12 = elementProp1.addElement("stringProp");
        stringProp12.addAttribute("name", "Argument.value");
        stringProp12.setText(group);
        Element stringProp13 = elementProp1.addElement("stringProp");
        stringProp13.addAttribute("name", "Argument.metadata");
        stringProp13.setText("=");
        Element elementProp2 = collectionProp.addElement("elementProp");
        elementProp2.addAttribute("name", "nameSrvAddr");
        elementProp2.addAttribute("elementType", "Argument");
        Element stringProp21 = elementProp2.addElement("stringProp");
        stringProp21.addAttribute("name", "Argument.name");
        stringProp21.setText("nameSrvAddr");
        Element stringProp22 = elementProp2.addElement("stringProp");
        stringProp22.addAttribute("name", "Argument.value");
        stringProp22.setText(nameServAddr);
        Element stringProp23 = elementProp2.addElement("stringProp");
        stringProp23.addAttribute("name", "Argument.metadata");
        stringProp23.setText("=");
        Element elementProp3 = collectionProp.addElement("elementProp");
        elementProp3.addAttribute("name", "topic");
        elementProp3.addAttribute("elementType", "Argument");
        Element stringProp31 = elementProp3.addElement("stringProp");
        stringProp31.addAttribute("name", "Argument.name");
        stringProp31.setText("topic");
        Element stringProp32 = elementProp3.addElement("stringProp");
        stringProp32.addAttribute("name", "Argument.value");
        stringProp32.setText(topic);
        Element stringProp33 = elementProp3.addElement("stringProp");
        stringProp33.addAttribute("name", "Argument.metadata");
        stringProp33.setText("=");
        Element elementProp4 = collectionProp.addElement("elementProp");
        elementProp4.addAttribute("name", "pollTimeout");
        elementProp4.addAttribute("elementType", "Argument");
        Element stringProp41 = elementProp4.addElement("stringProp");
        stringProp41.addAttribute("name", "Argument.name");
        stringProp41.setText("pollTimeout");
        Element stringProp42 = elementProp4.addElement("stringProp");
        stringProp42.addAttribute("name", "Argument.value");
        stringProp42.setText(pollTimeout + "");
        Element stringProp43 = elementProp4.addElement("stringProp");
        stringProp43.addAttribute("name", "Argument.metadata");
        stringProp43.setText("=");
        Element stringProp = javaSampler.addElement("stringProp");
        stringProp.addAttribute("name", "classname");
        stringProp.setText("io.shulie.flpt.pressure.engine.jmeter.plugin.java.RocketMqDataConsumePlugin");
        return javaSampler;
    }

    private static Element createHashTreeAfterJavaSamplerElement(String variableNames) {
        Element hashTree = DocumentHelper.createElement("hashTree");
        Element beanShellPostProcessor = hashTree.addElement("BeanShellPostProcessor");
        beanShellPostProcessor.addAttribute("guiclass", "TestBeanGUI");
        beanShellPostProcessor.addAttribute("testclass", "BeanShellPostProcessor");
        beanShellPostProcessor.addAttribute("testname", "MQ data processor");
        beanShellPostProcessor.addAttribute("enabled", "true");
        Element boolProp = beanShellPostProcessor.addElement("boolProp");
        boolProp.addAttribute("name", "resetInterpreter");
        boolProp.setText("false");
        Element stringProp1 = beanShellPostProcessor.addElement("stringProp");
        stringProp1.addAttribute("name", "parameters");
        Element stringProp2 = beanShellPostProcessor.addElement("stringProp");
        stringProp2.addAttribute("name", "filename");
        Element stringProp3 = beanShellPostProcessor.addElement("stringProp");
        stringProp3.addAttribute("name", "script");
        String[] varNameArray = variableNames.split(",");
        StringBuilder scriptCode = new StringBuilder();
        scriptCode.append("String response = prev.getResponseDataAsString();\r\n");
        scriptCode.append("String[] array = response.split(\",\");\r\n");
        for (int i = 0; i < varNameArray.length; i++) {
            String key = varNameArray[i];
            scriptCode.append("vars.put(\"" + key + "\", array[" + i + "]);\r\n");
        }
        stringProp3.setText(scriptCode.toString());
        hashTree.addElement("hashTree");
        return hashTree;
    }

    private static Map<String, Object> nameMatch(List<Map<String, Object>> csvConfigs, String rawFileName) {
        String fileNameShort = rawFileName;
        if (fileNameShort.contains("/")) {
            fileNameShort = fileNameShort.substring(fileNameShort.lastIndexOf("/") + 1, fileNameShort.length());
        }
        if (fileNameShort.contains("\\")) {
            fileNameShort = fileNameShort.substring(fileNameShort.lastIndexOf("\\") + 1, fileNameShort.length());
        }
        for (Map<String, Object> csvConfig : csvConfigs) {
            String originalFileName = TryUtils.tryOperation(() -> String.valueOf(csvConfig.get("name")));
            if (originalFileName != null && originalFileName.equalsIgnoreCase(fileNameShort)) {
                return csvConfig;
            }
        }
        return null;
    }

    /**
     * 重组共用的线程组属性
     *
     * @param threadGroupElement 线程组元素
     * @param pressureTestMode   压力模式
     * @param rampUp
     * @param steps
     * @param holdTime
     */
    private static void rebuildCommonThreadGroupSubElements(Element threadGroupElement
        , PressureTestMode pressureTestMode
        , String rampUp
        , String steps
        , String holdTime) {
        threadGroupElement.addElement("elementProp")
            .addAttribute("name", "ThreadGroup.main_controller")
            .addAttribute("elementType", "com.blazemeter.jmeter.control.VirtualUserController");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.on_sample_error")
            .setText("continue");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "RampUp")
            .setText(pressureTestMode == PressureTestMode.FIXED ? "0" : rampUp+"");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "Steps")
            .setText(pressureTestMode == PressureTestMode.STAIR ? steps+"" : "0");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "Hold")
            .setText(holdTime+"");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "LogFilename")
            .setText("");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "Iterations")
            .setText("");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "Unit")
            .setText("S");
    }

    private static void threadGroupModify(Element element, PressureContext context
            , SupportedPressureModeAbilities supportedPressureModeAbilities) {

        //测试计划的hashTree下所有节点
        List<Element> elementsList = element.elements();
        //压力模式
        PressureTestMode pressureTestMode = PressureTestMode.getMode(context.getPressureMode());
        for (Element hashTreeSubElement : elementsList) {
            //只要线程组元素 根据引擎压测模式更换线程组
            if (hashTreeSubElement.getName().endsWith("ThreadGroup")) {
                //获取enabledValue 如果是false 就不予处理
                String enabledValue = hashTreeSubElement.attributeValue("enabled");
                if ("false".equals(enabledValue)) {
                    continue;
                }
                //并发线程组
                EnginePressureMode currentEnginePressureMode = context.getCurrentEnginePressureMode();
                //并发模式
                if (EnginePressureMode.CONCURRENCY == currentEnginePressureMode) {
                    ConcurrencyAbility concurrencyAbility = supportedPressureModeAbilities
                            .getPressureModeAbility(EnginePressureMode.CONCURRENCY);
                    //具备并发模式能力
                    if(concurrencyAbility != null) {
                        hashTreeSubElement.setName(concurrencyAbility.getAbilityName());
                        Map<String, String> elementAttributes = concurrencyAbility.getExtraAttributes();
                        if(elementAttributes != null) {
                            for(Map.Entry<String, String> entry : elementAttributes.entrySet()) {
                                hashTreeSubElement.addAttribute(entry.getKey()
                                        , entry.getValue());
                            }
                        }
                        //将其下方内容清空
                        hashTreeSubElement.clearContent();
                        //重填内容
                        rebuildCommonThreadGroupSubElements(hashTreeSubElement
                                , pressureTestMode, StringUtils.valueOf(concurrencyAbility.getRampUp())
                                , StringUtils.valueOf(concurrencyAbility.getSteps()), StringUtils.valueOf(concurrencyAbility.getHoldTime()));
                        //添加目标值
                        hashTreeSubElement.addElement("stringProp")
                                .addAttribute("name", "TargetLevel")
                                .setText(concurrencyAbility.getExpectThroughput() + "");
                    } else{
                        HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "压力引擎不具备并发模式能力");
                        logger.error("unable to concurrency mode, please implement EnginePressureModeAble#enableConcurrencyMode");
                        System.exit(-1);
                    }
                }
                //TPS线程组
                if (EnginePressureMode.TPS == currentEnginePressureMode) {
                    TPSAbility tpsAbility = supportedPressureModeAbilities
                            .getPressureModeAbility(EnginePressureMode.TPS);
                    //具备tps模式能力
                    if(tpsAbility != null) {
                        String steps = StringUtils.valueOf(tpsAbility.getSteps());
                        String rampUp = StringUtils.valueOf(tpsAbility.getRampUp());
                        int tpsThreadMode = NumberUtils.parseInt(
                            context.getEnginePressureParams().get("tpsThreadMode"));
                        if (0 == tpsThreadMode) {
                            hashTreeSubElement.setName(Constants.CONCURRENCY_THREAD_GROUP_NAME);
                            hashTreeSubElement.addAttribute("guiclass",
                                "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui");
                            hashTreeSubElement.addAttribute("testclass",
                                "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup");
                            hashTreeSubElement.addAttribute("testname", "shulie - ConcurrencyThreadGroup");
                            hashTreeSubElement.addAttribute("enabled", "true");
                            //将其下方内容清空
                            hashTreeSubElement.clearContent();
                            //采用阶梯递增模式，起始并发为tps数，每2秒递增1次
                            int maxThreadNum = NumberUtils.parseInt(
                                context.getEnginePressureParams().get("maxThreadNum"));
                            if (maxThreadNum <= 0) {
                                maxThreadNum = SystemResourceUtil.getMaxThreadNum();
                            }
                            double tpsTargetLevel = NumberUtils.parseDouble(
                                context.getEnginePressureParams().get("tpsTargetLevel"));
                            if (tpsTargetLevel > 0) {
                                pressureTestMode = PressureTestMode.STAIR;
                                int stepsNum = (int)Math.ceil(maxThreadNum / tpsTargetLevel);
                                steps = StringUtils.valueOf(stepsNum);
                                rampUp = StringUtils.valueOf((int)Math.floor(stepsNum * 1.2));
                            }

                            //重填内容
                            rebuildCommonThreadGroupSubElements(hashTreeSubElement, pressureTestMode, rampUp, steps,
                                StringUtils.valueOf(tpsAbility.getHoldTime()));
                            hashTreeSubElement.addElement("stringProp")
                                .addAttribute("name", "TargetLevel")
                                //这里的值只是在脚本里显示，真实值会从redis取
                                .setText(StringUtils.valueOf(maxThreadNum));
                        } else {
                            hashTreeSubElement.setName(tpsAbility.getAbilityName());
                            Map<String, String> elementAttributes = tpsAbility.getExtraAttributes();
                            if (elementAttributes != null) {
                                for (Map.Entry<String, String> entry : elementAttributes.entrySet()) {
                                    hashTreeSubElement.addAttribute(entry.getKey()
                                        , entry.getValue());
                                }
                            }
                            //将其下方内容清空
                            hashTreeSubElement.clearContent();
                            //重填内容
                            rebuildCommonThreadGroupSubElements(hashTreeSubElement
                                , pressureTestMode, rampUp, steps, StringUtils.valueOf((tpsAbility.getHoldTime())));
                            //添加限制并发数 这里不需要限制
                            hashTreeSubElement.addElement("stringProp")
                                .addAttribute("name", "ConcurrencyLimit")
                                .setText(Constants.TPS_MODE_CONCURRENCY_LIMIT); //TPS模式下并发限制500 如果不限制可能并发会很高 导致系统资源不足
                            //添加目标值
                            hashTreeSubElement.addElement("stringProp")
                                .addAttribute("name", "TargetLevel")
                                //这里的值只是在脚本里显示，真实值会从redis取
                                .setText(tpsAbility.getTargetTps());
                        }
                    } else{
                        HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "压力引擎不具备TPS模式能力");
                        logger.error("unable to tps mode, please implement EnginePressureModeAble#enableTPSMode");
                        System.exit(-1);
                    }
                }
                //流量调试
                if (EnginePressureMode.FLOW_DEBUG == currentEnginePressureMode) {
                    FlowDebugAbility flowDebugAbility = supportedPressureModeAbilities
                            .getPressureModeAbility(EnginePressureMode.FLOW_DEBUG);
                    //具备流量调试能力
                    if(flowDebugAbility != null) {
                        hashTreeSubElement.setName(flowDebugAbility.getAbilityName());
                        Map<String, String> elementAttributes = flowDebugAbility.getExtraAttributes();
                        if(elementAttributes != null) {
                            for(Map.Entry<String, String> entry : elementAttributes.entrySet()) {
                                hashTreeSubElement.addAttribute(entry.getKey()
                                        , entry.getValue());
                            }
                        }
                        //将其下方内容清空
                        hashTreeSubElement.clearContent();
                        //构建流量调试模式线程组，目前为普通线程组，固定单线程发1K条
                        rebuildFlowDebugThreadGroupSubElements(hashTreeSubElement, flowDebugAbility.getLoops());
                    } else{
                        HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "压力引擎不具备流量调试模式能力");
                        logger.error("unable to flow debug mode, please implement EnginePressureModeAble#enableFlowDebugMode");
                        System.exit(-1);
                    }
                }
                // 巡检模式
                if (EnginePressureMode.INSPECTION_MODE == currentEnginePressureMode) {
                    InspectionAbility inspectionAbility = supportedPressureModeAbilities
                            .getPressureModeAbility(EnginePressureMode.INSPECTION_MODE);
                    //具备巡检能力
                    if(inspectionAbility != null) {
                        //目前这里直接填为固定值
                        hashTreeSubElement.setName(inspectionAbility.getAbilityName());
                        Map<String, String> elementAttributes = inspectionAbility.getExtraAttributes();
                        if(elementAttributes != null) {
                            for(Map.Entry<String, String> entry : elementAttributes.entrySet()) {
                                hashTreeSubElement.addAttribute(entry.getKey()
                                        , entry.getValue());
                            }
                        }
                        //将其下方内容清空
                        hashTreeSubElement.clearContent();
                        //构建巡检模式线程组，目前为普通线程组，以一定周期（5秒、10秒，可配置）、小流量（1并发）发起巡检请求
                        rebuildInspectionModeThreadGroupSubElements(hashTreeSubElement, inspectionAbility.getLoops());
                    } else {
                        HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "压力引擎不具备巡检能力");
                        logger.error("unable to try run mode, please implement EnginePressureModeAble#enableTryRunMode");
                        System.exit(-1);
                    }
                }
                //添加试跑模式 add by lipeng
                if (EnginePressureMode.TRY_RUN == currentEnginePressureMode) {
                    TryRunAbility tryRunAbility = supportedPressureModeAbilities
                            .getPressureModeAbility(EnginePressureMode.TRY_RUN);
                    //具备脚本调试能力
                    if(tryRunAbility != null) {
                        // 试跑模式
                        //目前这里直接填为固定值
                        hashTreeSubElement.setName(tryRunAbility.getAbilityName());
                        Map<String, String> elementAttributes = tryRunAbility.getExtraAttributes();
                        if(elementAttributes != null) {
                            for(Map.Entry<String, String> entry : elementAttributes.entrySet()) {
                                hashTreeSubElement.addAttribute(entry.getKey()
                                        , entry.getValue());
                            }
                        }
                        //将其下方内容清空
                        hashTreeSubElement.clearContent();
                        //构建试跑模式线程组，目前为普通线程组，小流量（loop_nums并发） expectThroughput并发数量  发起试跑
                        rebuildTryRunModeThreadGroupSubElements(hashTreeSubElement, tryRunAbility.getLoops()
                                , tryRunAbility.getExpectThroughput());
                    } else{
                        HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "压力引擎不具备脚本调试能力");
                        logger.error("unable to try run mode, please implement EnginePressureModeAble#enableTryRunMode");
                        System.exit(-1);
                    }
                }
                //自定义不需要更改
                else {

                }
            }
        }

    }

    /**
     * 填充试跑模式线程组
     *
     * @param threadGroupElement
     */
    private static void rebuildTryRunModeThreadGroupSubElements(Element threadGroupElement,
        Long loops, Long expectThroughput) {
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.on_sample_error")
            .setText("continue");

        threadGroupElement.addElement("elementProp")
            .addAttribute("name", "ThreadGroup.main_controller")
            .addAttribute("elementType", "LoopController")
            .addAttribute("guiclass", "LoopControlPanel")
            .addAttribute("testclass", "LoopController")
            .addAttribute("testname", "循环控制器")
            .addAttribute("enabled", "true");
        Element elementProp = threadGroupElement.element("elementProp");
        elementProp.addElement("boolProp")
            .addAttribute("name", "LoopController.continue_forever")
            .setText("false");
        //试跑次数
        int tryRunTimes = Math.toIntExact(loops);
        //不支持的试跑次数
        if(!arrayContains(TryRunAbility.TRY_RUN_SUPPORTED_TIMES, tryRunTimes)) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED
                    , "不支持的调试次数，调试次数目前支持[1, 10, 100, 1000, 10000]条的调试");
            logger.error("不支持的调试次数，调试次数目前支持[1, 10, 100, 1000, 10000]条的调试");
            System.exit(-1);
        }
        //并发数
        int concurrencyNum = Math.toIntExact(expectThroughput);
        //不支持的并发数量
        if(!arrayContains(TryRunAbility.TRY_RUN_SUPPORTED_CONURRENT_NUM, concurrencyNum)) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED
                    , "不支持的调试并发数量，调试并发数量目前支持[1, 5, 10, 20, 50, 100]并发的调试");
            logger.error("不支持的调试并发数量，调试并发数量目前支持[1, 5, 10, 20, 50, 100]并发的调试");
            System.exit(-1);
        }
        //并发数量不能大于试跑次数
        if(concurrencyNum > tryRunTimes) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED
                    , "脚本调试并发数量不能大于调试次数，调试次数为："+tryRunTimes+", 并发数量为："+concurrencyNum);
            logger.error("脚本调试并发数量不能大于调试次数，调试次数为："+tryRunTimes+", 并发数量为："+concurrencyNum);
            System.exit(-1);
        }
        //循环次数 等于 试跑次数除以并发数量
        int loopCount = tryRunTimes / concurrencyNum;
        elementProp.addElement("stringProp").addAttribute("name", "LoopController.loops")
            .setText(loopCount+"");
        threadGroupElement.addElement("stringProp")
                .addAttribute("name", "ThreadGroup.num_threads").setText(concurrencyNum+"");
        threadGroupElement.addElement("stringProp")
                .addAttribute("name", "ThreadGroup.ramp_time").setText("1");
        threadGroupElement.addElement("boolProp")
                .addAttribute("name", "ThreadGroup.scheduler").setText("false");
        threadGroupElement.addElement("stringProp").addAttribute("name", "ThreadGroup.duration");
        threadGroupElement.addElement("stringProp").addAttribute("name", "ThreadGroup.delay");
    }

    /**
     * 填充巡检模式线程组
     *
     * @param threadGroupElement
     */
    private static void rebuildInspectionModeThreadGroupSubElements(Element threadGroupElement,
        Long loops) {
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.on_sample_error")
            .setText("continue");

        threadGroupElement.addElement("elementProp")
            .addAttribute("name", "ThreadGroup.main_controller")
            .addAttribute("elementType", "LoopController")
            .addAttribute("guiclass", "LoopControlPanel")
            .addAttribute("testclass", "LoopController")
            .addAttribute("testname", "循环控制器")
            .addAttribute("enabled", "true");
        Element elementProp = threadGroupElement.element("elementProp");
        elementProp.addElement("boolProp")
            .addAttribute("name", "LoopController.continue_forever")
            .setText("false");

        elementProp.addElement("stringProp").addAttribute("name", "LoopController.loops")
            .setText(loops + "");
        threadGroupElement.addElement("stringProp").addAttribute("name", "ThreadGroup.num_threads").setText("1");
        threadGroupElement.addElement("stringProp").addAttribute("name", "ThreadGroup.ramp_time").setText("1");
        threadGroupElement.addElement("boolProp").addAttribute("name", "ThreadGroup.scheduler").setText("false");
        threadGroupElement.addElement("stringProp").addAttribute("name", "ThreadGroup.duration");
        threadGroupElement.addElement("stringProp").addAttribute("name", "ThreadGroup.delay");
    }

    /**
     * 充填流量调试模式线程组
     *
     * @param hashTreeSubElement
     */
    private static void rebuildFlowDebugThreadGroupSubElements(Element threadGroupElement, Long loops) {
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.on_sample_error")
            .setText("continue");

        threadGroupElement.addElement("elementProp")
            .addAttribute("name", "ThreadGroup.main_controller")
            .addAttribute("elementType", "LoopController")
            .addAttribute("guiclass", "LoopControlPanel")
            .addAttribute("testclass", "LoopController")
            .addAttribute("testname", "循环控制器")
            .addAttribute("enabled", "true");
        Element elementProp = threadGroupElement.element("elementProp");
        elementProp.addElement("boolProp")
            .addAttribute("name", "LoopController.continue_forever")
            .setText("false");
        elementProp.addElement("stringProp")
            .addAttribute("name", "LoopController.loops")
            .setText(loops+"");

        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.num_threads")
            .setText("1");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.ramp_time")
            .setText("1");
        threadGroupElement.addElement("boolProp")
            .addAttribute("name", "ThreadGroup.scheduler")
            .setText("false");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.duration");
        threadGroupElement.addElement("stringProp")
            .addAttribute("name", "ThreadGroup.delay");
    }

    public static void headerManagerModify(Document document, String sceneId, String reportId, String customerId) {
        ///collectionProp/elementProp
        List<Node> nodes = XpathUtils.searchNodeByXPath(document,
            "//HeaderManager");
        // 一定存在的情况处理
        if (nodes != null && nodes.size() > 0) {
            for (Node node : nodes) {
                Element elementProp = (Element)node;
                Element stringProp11 = elementProp.addElement("stringProp");
                stringProp11.addAttribute("name", "Header.name");
                stringProp11.setText(TakinRequestConstant.CLUSTER_TEST_SCENE_HEADER_VALUE);
                Element stringProp12 = elementProp.addElement("stringProp");
                stringProp12.addAttribute("name", "Header.value");
                stringProp12.setText(sceneId);
                // add reportId
                Element stringProp21 = elementProp.addElement("stringProp");
                stringProp21.addAttribute("name", "Header.name");
                stringProp21.setText(TakinRequestConstant.CLUSTER_TEST_TASK_HEADER_VALUE);
                Element stringProp22 = elementProp.addElement("stringProp");
                stringProp22.addAttribute("name", "Header.value");
                stringProp22.setText(reportId);
                // add customerId
                Element stringProp31 = elementProp.addElement("stringProp");
                stringProp31.addAttribute("name", "Header.name");
                stringProp31.setText(TakinRequestConstant.CLUSTER_TEST_CUSTOMER_HEADER_VALUE);
                Element stringProp32 = elementProp.addElement("stringProp");
                stringProp32.addAttribute("name", "Header.value");
                stringProp32.setText(customerId);
            }
        }
    }

    private static void addBackEndListener(Element element, String sceneId, String reportId,
        String customerId, PressureContext context) {
        Element backendListener = element.addElement("BackendListener");
        backendListener.addAttribute("guiclass", "BackendListenerGui");
        backendListener.addAttribute("testclass", "BackendListener");
        backendListener.addAttribute("testname", "后端监听器-" + sceneId + "-" + reportId + "-" + customerId);
        backendListener.addAttribute("enabled", "true");

        Element elementProp = backendListener.addElement("elementProp");
        elementProp.addAttribute("name", "arguments");
        elementProp.addAttribute("elementType", "Arguments");
        elementProp.addAttribute("guiclass", "ArgumentsPanel");
        elementProp.addAttribute("testclass", "Arguments");
        elementProp.addAttribute("enabled", "true");

        Element collectionProp = elementProp.addElement("collectionProp");
        collectionProp.addAttribute("name", "Arguments.arguments");
        Element elementProp1 = collectionProp.addElement("elementProp");
        elementProp1.addAttribute("name", "influxdbMetricsSender");
        elementProp1.addAttribute("elementType", "Argument");
        Element stringProp11 = elementProp1.addElement("stringProp");
        stringProp11.addAttribute("name", "Argument.name");
        stringProp11.setText("influxdbMetricsSender");
        Element stringProp12 = elementProp1.addElement("stringProp");
        stringProp12.addAttribute("name", "Argument.value");
        stringProp12.setText("org.apache.jmeter.visualizers.backend.influxdb.HttpJsonMetricsSender");
        Element stringProp13 = elementProp1.addElement("stringProp");
        stringProp13.addAttribute("name", "Argument.metadata");
        stringProp13.setText("=");

        /****************influxDB****************/
        Element elementProp2 = collectionProp.addElement("elementProp");
        elementProp2.addAttribute("name", "influxdbUrl");
        elementProp2.addAttribute("elementType", "Argument");

        Element stringProp21 = elementProp2.addElement("stringProp");
        stringProp21.addAttribute("name", "Argument.name");
        stringProp21.setText("influxdbUrl");

        Element stringProp22 = elementProp2.addElement("stringProp");
        stringProp22.addAttribute("name", "Argument.value");
        stringProp22.setText(context.getMetricCollectorUrl());

        Element stringProp23 = elementProp2.addElement("stringProp");
        stringProp23.addAttribute("name", "Argument.metadata");
        stringProp23.setText("=");
        /****************influxDB****************/

        /****************application****************/
        Element elementProp3 = collectionProp.addElement("elementProp");
        elementProp3.addAttribute("name", "application");
        elementProp3.addAttribute("elementType", "Argument");

        Element stringProp31 = elementProp3.addElement("stringProp");
        stringProp31.addAttribute("name", "Argument.name");
        stringProp31.setText("application");

        Element stringProp32 = elementProp3.addElement("stringProp");
        stringProp32.addAttribute("name", "Argument.value");
        stringProp32.setText("jmeter_test");
        Element stringProp33 = elementProp3.addElement("stringProp");
        stringProp33.addAttribute("name", "Argument.metadata");
        stringProp33.setText("=");
        /****************application****************/

        /****************measurement****************/
        Element elementProp4 = collectionProp.addElement("elementProp");
        elementProp4.addAttribute("name", "measurement");
        elementProp4.addAttribute("elementType", "Argument");
        Element stringProp41 = elementProp4.addElement("stringProp");
        stringProp41.addAttribute("name", "Argument.name");
        stringProp41.setText("measurement");
        Element stringProp42 = elementProp4.addElement("stringProp");
        stringProp42.addAttribute("name", "Argument.value");
        stringProp42.setText("jmeter_test");
        Element stringProp43 = elementProp4.addElement("stringProp");
        stringProp43.addAttribute("name", "Argument.metadata");
        stringProp43.setText("=");
        /****************measurement****************/

        /****************summaryOnly****************/
        Element elementProp5 = collectionProp.addElement("elementProp");
        // flase的情况下，输出每条数据的详情报
        elementProp5.addAttribute("name", "summaryOnly");
        elementProp5.addAttribute("elementType", "Argument");
        Element stringProp51 = elementProp5.addElement("stringProp");
        stringProp51.addAttribute("name", "Argument.name");
        stringProp51.setText("summaryOnly");
        Element stringProp52 = elementProp5.addElement("stringProp");
        stringProp52.addAttribute("name", "Argument.value");
        stringProp52.setText("false");

        Element stringProp53 = elementProp5.addElement("stringProp");
        stringProp53.addAttribute("name", "Argument.metadata");
        stringProp53.setText("=");
        /****************summaryOnly****************/

        /****************samplersRegex****************/
        Element elementProp6 = collectionProp.addElement("elementProp");
        elementProp6.addAttribute("name", "samplersRegex");
        elementProp6.addAttribute("elementType", "Argument");
        Element stringProp61 = elementProp6.addElement("stringProp");
        stringProp61.addAttribute("name", "Argument.name");
        stringProp61.setText("samplersRegex");
        Element stringProp62 = elementProp6.addElement("stringProp");
        stringProp62.addAttribute("name", "Argument.value");
        stringProp62.setText(".*");
        Element stringProp63 = elementProp6.addElement("stringProp");
        stringProp63.addAttribute("name", "Argument.metadata");
        stringProp63.setText("=");
        /****************samplersRegex****************/

        /****************percentiles****************/
        Element elementProp7 = collectionProp.addElement("elementProp");
        elementProp7.addAttribute("name", "percentiles");
        elementProp7.addAttribute("elementType", "Argument");
        Element stringProp71 = elementProp7.addElement("stringProp");
        stringProp71.addAttribute("name", "Argument.name");
        stringProp71.setText("percentiles");
        Element stringProp72 = elementProp7.addElement("stringProp");
        stringProp72.addAttribute("name", "Argument.value");
        stringProp72.setText("90;95;99");
        Element stringProp73 = elementProp7.addElement("stringProp");
        stringProp73.addAttribute("name", "Argument.metadata");
        stringProp73.setText("=");
        /****************percentiles****************/

        /****************testTitle****************/
        Element elementProp8 = collectionProp.addElement("elementProp");
        elementProp8.addAttribute("name", "testTitle");
        elementProp8.addAttribute("elementType", "Argument");
        Element stringProp81 = elementProp8.addElement("stringProp");
        stringProp81.addAttribute("name", "Argument.name");
        stringProp81.setText("testTitle");
        Element stringProp82 = elementProp8.addElement("stringProp");
        stringProp82.addAttribute("name", "Argument.value");
        stringProp82.setText("Test name");
        Element stringProp83 = elementProp8.addElement("stringProp");
        stringProp83.addAttribute("name", "Argument.metadata");
        stringProp83.setText("=");
        /****************testTitle****************/

        /****************eventTags****************/
        Element elementProp9 = collectionProp.addElement("elementProp");
        elementProp9.addAttribute("name", "eventTags");
        elementProp9.addAttribute("elementType", "Argument");

        Element stringProp91 = elementProp9.addElement("stringProp");
        stringProp91.addAttribute("name", "Argument.name");
        stringProp91.setText("eventTags");
        Element stringProp92 = elementProp9.addElement("stringProp");
        stringProp92.addAttribute("name", "Argument.value");
        stringProp92.setText("");
        Element stringProp93 = elementProp9.addElement("stringProp");
        stringProp93.addAttribute("name", "Argument.metadata");
        stringProp93.setText("=");
        /****************eventTags****************/

        /****************businessMap****************/
        Element elementProp13 = collectionProp.addElement("elementProp");
        elementProp13.addAttribute("name", "businessMap");
        elementProp13.addAttribute("elementType", "Argument");

        Element stringProp131 = elementProp13.addElement("stringProp");
        stringProp131.addAttribute("name", "Argument.name");
        stringProp131.setText("businessMap");

        Element stringProp132 = elementProp13.addElement("stringProp");
        stringProp132.addAttribute("name", "Argument.value");
        Map<String, String> map = Maps.newHashMap();
        if (context.getBusinessMap() != null) {
            for (Map.Entry<String, String> entry : context.getBusinessMap().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                //todo 写死rt,后续可以根据配置
                map.put(key + "_rt", value);
            }
        }
        stringProp132.setText(JsonUtils.obj2Json(map));
        Element stringProp133 = elementProp13.addElement("stringProp");
        stringProp133.addAttribute("name", "Argument.metadata");
        stringProp133.setText("=");
        /****************businessMap****************/

        String podNum = context.getPodCount() == 1 ? "1" : System.getProperty("pod.number");

        /***************结束*****************/
        Element stringProp = backendListener.addElement("stringProp");
        stringProp.addAttribute("name", "classname");
        stringProp.setText("org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient");
        //添加队列长度
        Element queneStringProp = backendListener.addElement("stringProp");
        queneStringProp.addAttribute("name", "QUEUE_SIZE");
        //队列长度
        queneStringProp.setText(context.getPressureEngineBackendQueueCapacity());
        element.addElement("hashTree");
    }

    private static class MQDataConfig {
        public Map<String, Object> config;
        public String variableNames;
    }

    /**
     * 根据属性值获取元素
     *
     * @param root
     * @param attributeKey
     * @param attributeValues
     * @return
     */
    private static List<Element> getAllElementByAttribute(Element root, String attributeKey,
        List<String> attributeValues) {
        List<Element> result = new ArrayList<>();
        selectElement(result, root.elements(), attributeKey, attributeValues);
        return result;
    }

    /**
     * 递归获取元素
     *
     * @param result
     * @param elements
     * @param attributeKey
     * @param attributeValues
     */
    private static void selectElement(List<Element> result, List elements, String attributeKey,
        List<String> attributeValues) {
        if (elements == null || elements.size() == 0) {
            return;
        }
        for (Iterator it = elements.iterator(); it.hasNext(); ) {
            Element element = (Element)it.next();
            //获取test
            if (attributeValues.contains(element.attributeValue(attributeKey))) {
                result.add(element);
            }
            List childElements = element.elements();
            selectElement(result, childElements, attributeKey, attributeValues);
        }
    }

    private static void updateXmlDubboPressTestTags(Document document) {
        List<Element> allElement = getAllElement("io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample", document);
        for (Element element : allElement) {
            List<Element> stringPropList = new ArrayList<>();
            selectElement("stringProp", element.elements(), stringPropList);
            if (stringPropList != null && stringPropList.size() != 0) {
                String attachmentArgsValue = "";
                for (Element ele : stringPropList) {
                    if (ele.attributeValue("name") != null && ele.attributeValue("name").startsWith(
                            "FIELD_DUBBO_ATTACHMENT_ARGS_KEY")
                            && "p-pradar-cluster-test".equals(ele.getText())) {
                        String attributeValue = ele.attributeValue("name");
                        attachmentArgsValue = attributeValue.replace("KEY", "VALUE");
                    }
                }
                if (StringUtils.isNotBlank(attachmentArgsValue)) {
                    Element dubboAttachmentValue = selectElementByEleNameAndAttr("stringProp", "name",
                            attachmentArgsValue, element.elements());
                    if (dubboAttachmentValue != null && "true".equals(dubboAttachmentValue.getText())) {
                        dubboAttachmentValue.setText("false");
                    }
                }
            }
        }
    }

    private static List<Element> getAllElement(String elementName, Document document) {
        List<Element> result = new ArrayList<>();
        Element rootElement = document.getRootElement();
        selectElement(elementName, rootElement.elements(), result);
        return result;
    }

    private static Element selectElementByEleNameAndAttr(String elementName, String attributeName, String attributeValue,
                                                  List elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        for (Object it : elements) {
            Element element = (Element)it;
            if (element.getName().equals(elementName) && attributeValue.equals(element.attributeValue(attributeName))) {
                return element;
            }
            Element childElement = selectElementByEleNameAndAttr(elementName, attributeName, attributeValue,
                    element.elements());
            if (childElement != null) {
                return childElement;
            }
        }
        return null;
    }
    private static void updateJmxHttpPressTestTags(Document document) {
        List<Element> allElement = getAllElement("HeaderManager", document);
        if (allElement != null && allElement.size() != 0) {
            List<Element> allElementProp = new ArrayList<>();
            for (Element headerElement : allElement) {
                selectElement("elementProp", headerElement.elements(), allElementProp);
            }
            if (allElementProp != null && allElementProp.size() != 0) {
                for (Element elementProp : allElementProp) {
                    Element nameElement = selectElementByEleNameAndAttr("stringProp", "name", "Header.name",
                            elementProp.elements());
                    Element valueElement = selectElementByEleNameAndAttr("stringProp", "name", "Header.value",
                            elementProp.elements());
                    if (nameElement != null && valueElement != null && "User-Agent".equals(nameElement.getText())
                            && "PerfomanceTest".equals(valueElement.getText())) {
                        valueElement.setText("FlowDebug");
                    }
                }
            }
        }
    }


    private static void selectElement(String elementName, List elements, List<Element> result) {
        if (elements == null || elements.size() == 0) {
            return;
        }
        for (Iterator it = elements.iterator(); it.hasNext(); ) {
            Element element = (Element)it.next();
            if (element.getName().equals(elementName)) {
                result.add(element);
            }
            List childElements = element.elements();
            selectElement(elementName, childElements, result);
        }
    }

    /**
     * 将tps写入redis
     *
     * @param enginePressureParams
     * @param sceneId
     * @param reportId
     * @param customerId
     */
    private static void writeTpsTargetToRedis(Map<String, Object> enginePressureParams, Long sceneId, Long reportId,
                                       Long customerId) {
        //单个pod目标tps
        String tpsTargetLevel = String.valueOf(enginePressureParams.get("tpsTargetLevel"));
        if (StringUtils.isBlank(tpsTargetLevel)) {
            logger.error("TPS模式下，tpsTargetLevel不能为空。");
            //  通知下
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED,
                    "TPS模式下，tpsTargetLevel不能为空。");
            System.exit(-1);
            return;
        }
        String engineRedisAddress = String.valueOf(enginePressureParams.get("engineRedisAddress"));
        String engineRedisPassword = String.valueOf(enginePressureParams.get("engineRedisPassword"));
        String engineRedisPort = String.valueOf(enginePressureParams.get("engineRedisPort"));
        String engineRedisSentinelNodes = String.valueOf(enginePressureParams.get("engineRedisSentinelNodes"));
        String engineRedisSentinelMaster = String.valueOf(enginePressureParams.get("engineRedisSentinelMaster"));
        String sceneIdString = sceneId + "";
        String reportIdString = reportId + "";
        String customerIdString = customerId + "";

        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setNodes(engineRedisSentinelNodes);
        redisConfig.setMaster(engineRedisSentinelMaster);
        redisConfig.setHost(engineRedisAddress);
        redisConfig.setPort(Integer.parseInt(engineRedisPort));
        redisConfig.setPassword(engineRedisPassword);
        redisConfig.setMaxIdle(1);
        redisConfig.setMaxTotal(1);
        redisConfig.setTimeout(3000);

        RedisUtil redisUtil = null;
        try {
            redisUtil = RedisUtil.getInstance(redisConfig);

            //改为hset 将压测实例所有信息存储到一个hash中
            redisUtil.hset(String.format(
                    JmeterConstants.PRESSURE_ENGINE_INSTANCE_REDIS_KEY_FORMAT
                    , sceneIdString
                    , reportIdString
                    , customerIdString), JmeterConstants.REDIS_TPS_LIMIT_FIELD, tpsTargetLevel);
            //获取所有业务活动
            List<Map<String, String>> businessActivities = (List<Map<String, String>>)enginePressureParams
                    .get("businessActivities");
            //只有是业务流程 也就是业务活动大于1个的时候才需要在redis添加吞吐量的百分比
            if (businessActivities != null && businessActivities.size() > 1) {
                // elementTestName对应的百分比
                Map<String, String> businessActivityMap = businessActivities.stream()
                        .collect(Collectors.toMap(map -> map.get("elementTestName")
                                , map -> map.get("throughputPercent")));
                for (Map.Entry<String, String> entry : businessActivityMap.entrySet()) {
                    redisUtil.setex(String.format(
                            JmeterConstants.REDIS_ACTIVITY_PERCENTAGE_KEY_FORMAT
                            , sceneIdString
                            , reportIdString
                            , customerIdString
                            , ScriptModifier.getSampleThroughputControllerTestname(entry.getKey())
                    ), JmeterConstants.REDIS_TPS_LIMIT_KEY_EXPIRES, entry.getValue());
                }
            }
        } catch (Exception e) {
            logger.error("Redis 连接失败，redisAddress is {}， redisPort is {}， encryptRedisPassword is {}"
                    , engineRedisAddress, engineRedisPort, engineRedisPassword);
            logger.error("失败详细错误栈：", e);
            //  通知下
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED,
                    "TPS模式下，Redis 连接失败，" + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * 禁用脚本自带ResultCollector
     *
     * @param elements
     */
    private static void forbidResultCollector(List<Element> elements) {
        if (null != elements && elements.size() > 0) {
            for (Element element : elements) {
                List<Element> resultCollector = element.elements("ResultCollector");
                if (null != resultCollector && !resultCollector.isEmpty()) {
                    resultCollector.forEach(item -> {
                        item.addAttribute("enabled", "false");
                    });
                }
                List<Element> nextLevel = element.elements("hashTree");
                if (nextLevel != null && nextLevel.size() > 0) {
                    forbidResultCollector(nextLevel);
                }
            }
        }
    }
}
