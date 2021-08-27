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

package io.shulie.flpt.pressure.engine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import io.shulie.flpt.pressure.engine.api.ability.EnginePressureModeAbility;
import io.shulie.flpt.pressure.engine.api.ability.SupportedPressureModeAbilities;
import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;
import io.shulie.flpt.pressure.engine.api.entity.*;
import io.shulie.flpt.pressure.engine.api.enums.EnginePressureMode;
import io.shulie.flpt.pressure.engine.api.enums.EngineType;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.api.plugin.PressurePlugin;
import io.shulie.flpt.pressure.engine.api.plugin.response.StopResponse;
import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.util.FileUtils;
import io.shulie.flpt.pressure.engine.util.JsonUtils;
import io.shulie.flpt.pressure.engine.util.StringUtils;
import io.shulie.flpt.pressure.engine.util.TryUtils;
import io.shulie.flpt.pressure.engine.util.http.HttpNotifyTakinCloudUtils;
import io.shulie.takin.constants.TakinRequestConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Create by xuyh at 2020/4/17 21:59.
 */
@SuppressWarnings("unchecked")
public class Bootstrap {

    private static Logger logger;

    private static String workDir;
    private static PressureContext context;
    private static String engineType;

    //调度默认延时时间 单位秒
    private static final int SCHEDULED_INITIAL_DELAY = 5;

    //调度默认周期时间 单位秒
    private static final int SCHEDULED_PERIOD = 1;

    //调度默认核心线程数 单位秒
    private static final int SCHEDULED_THREAD_CORE_SIZE = 1;

    //线程名格式化格式
    private static final String THREAD_NAME_FORMAT = "thread-call-runner-%d";

    //默认采样率
    private static final String DEFAULT_TRACE_SAMPLING = "1";

    public static void main(String[] args) {
        // 初始化公共方法
        HttpNotifyTakinCloudUtils.init();
        logger = LoggerFactory.getLogger(Bootstrap.class);
        workDir = System.getenv(Constants.WORK_DIR_KEY);
        if (workDir == null || workDir.isEmpty()) {
            workDir = System.getProperty(Constants.WORK_DIR_KEY);
        }
        if (workDir == null || workDir.isEmpty()) {
            workDir = System.getProperty("user.dir");
        }
        engineType = System.getProperty("engine.type");
        String configurationsFile = System.getProperty("configurations");

        String configurations = TryUtils.tryOperation(
                () -> FileUtils.readTextFileContent(new File(configurationsFile)));
        if (configurations == null || configurations.isEmpty()) {
            logger.warn("No configuration found in config file: {}", configurationsFile);
            //  通知下
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED,
                    "No configuration found in config file");
            System.exit(-1);
        }

        logger.info("Bootstrap startup, args length: {} args: {}", args.length, args);

        logger.info("Task config acquired, config: {}", configurations);
        Map<String, Object> taskInfoMap = JsonUtils.json2Obj(configurations, Map.class);
        //初始化上下文 add by lipeng
        initialPressureContext(taskInfoMap);
        //执行压测
        doPressureTest();

        System.exit(0);
    }

    private static void doPressureTest() {
        // 调用对应插件
        EngineType type = EngineType.getByType(engineType);
        List<File> pluginJarFiles = FileUtils.getDirectoryFiles(workDir + File.separator + "plugins", ".jar");

        if (pluginJarFiles == null || pluginJarFiles.isEmpty()) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED,
                    "No configuration found in config file");
            logger.warn("No plugin found for pressure engine.");
            System.exit(-1);
        }
        URL[] urls = new URL[pluginJarFiles.size()];
        for (int i = 0; i < pluginJarFiles.size(); i++) {
            try {
                File file = pluginJarFiles.get(i);
                String pathName = file.getCanonicalPath();
                String urlStr = "file:" + pathName;
                urls[i] = new URL(urlStr);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        URLClassLoader urlClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        ServiceLoader<PressurePlugin> serviceLoader = ServiceLoader.load(PressurePlugin.class, urlClassLoader);
        Iterator<PressurePlugin> iterator = serviceLoader.iterator();
        PressurePlugin pressurePluginTemp = null;
        while (iterator.hasNext()) {
            PressurePlugin plugin = iterator.next();
            if (plugin.engineType().equals(type)) {
                pressurePluginTemp = plugin;
                break;
            }
        }

        if (pressurePluginTemp == null) {
            logger.warn("No pressure plugin found for engine type: {}", engineType);
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED,
                    String.format("No pressure plugin found for engine type: {}", engineType));
            System.exit(-1);
        }

        //定义引擎插件
        final PressurePlugin pressurePlugin = pressurePluginTemp;

        //添加中断监听 modify by lipeng
        //用于检测cloud是否进行了中断操作
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(THREAD_NAME_FORMAT).build();
        ScheduledExecutorService scheduExec = new ScheduledThreadPoolExecutor(SCHEDULED_THREAD_CORE_SIZE, threadFactory);
        scheduExec.scheduleAtFixedRate(() -> {
            String result = HttpNotifyTakinCloudUtils.getTakinCloud(EngineStatusEnum.INTERRUPT);
            logger.info("获取中断状态：{}", result);
            JsonObject jsonObject = JsonUtils.json2Obj(result, JsonObject.class);
            if (jsonObject != null && jsonObject.get("data").getAsBoolean()) {
                //发生中断  获取事件
                StopResponse response = pressurePlugin.stopPressureTest(context);
                if (response != null) {
                    if (response.getExitValue() > -1) {
                        HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.INTERRUPT_SUCCEED, response.getMessage());
                        //销毁线程池
                        scheduExec.shutdown();
                    } else {
                        HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.INTERRUPT_FAILED, response.getMessage());
                    }
                }
            }
        }, SCHEDULED_INITIAL_DELAY, SCHEDULED_PERIOD, TimeUnit.SECONDS);

        //add start by lipeng 初始化引擎插件 (非必须，只有第三方插件需要支持)
        try {
            logger.info("Pressure initializeEnginePlugins starting..");
            pressurePlugin.initializeEnginePlugins(context);
            logger.info("Pressure initializeEnginePlugins finished");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        //add end

        //处理脚本路径
        handleScriptPath(context);

        //处理压测数据
        try {
            pressurePlugin.doResolvePressureData(context, context.getDataFileSets());
        } catch(Exception e) {
            logger.warn("Pressure doResolvePressureData failed.");
        }

        //支持的压力模式
        EnginePressureModeAbility enginePressureModeAbility = pressurePlugin.initialEnginePressureModeAbility();
        if(enginePressureModeAbility == null) {
            logger.error("unsupported any pressure mode, are u sure to implement PressurePlugin#initialEnginePressureModeAbility ?");
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "没有支持任何压力模式，启动失败");
            System.exit(-1);
        }
        SupportedPressureModeAbilities supportedPressureModeAbilities = SupportedPressureModeAbilities.build(context)
                .initialize(enginePressureModeAbility);

        //处理脚本
        boolean modifyScriptResult = false;
        try {
            modifyScriptResult = pressurePlugin.doModifyScript(context, supportedPressureModeAbilities);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        if (!modifyScriptResult) {
            logger.warn("Pressure doModifyScript failed.");
            System.exit(-1);
        }

        //开始压测
        try {
            pressurePlugin.doPressureTest(context);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        //完成压测
        boolean finishResult = false;
        try {
            finishResult = pressurePlugin.finish(context);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        if (!finishResult) {
            logger.warn("Pressure finish failed");
            System.exit(-1);
        }
    }

    private static void handleScriptPath(PressureContext context) {
        //获取脚本路径
        String testScriptFilePath = context.getScriptPath();
        File scriptFile = new File(testScriptFilePath);
        //获取最终写入路径
        String resourceDir = context.getResourcesDir();
        String scriptDir = TryUtils.tryOperation(
                () -> testScriptFilePath.substring(0, testScriptFilePath.lastIndexOf("/")));
        if (resourceDir == null) {
            File parent = scriptFile.getParentFile();
            if (parent != null && parent.isDirectory()) {
                try {
                    resourceDir = parent.getCanonicalPath();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        //获取所有jar路径
        List<String> jarFilePathList = new ArrayList<>();
        if (resourceDir != null) {
            List<File> files = FileUtils.getDirectoryFiles(resourceDir, null);
            List<File> jarFileList = FileUtils.selectFiles(files, ".jar");
            if (jarFileList != null && !jarFileList.isEmpty()) {
                for (File file : jarFileList) {
                    try {
                        jarFilePathList.add(file.getCanonicalPath());
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
            List<File> filesScriptDir = FileUtils.getDirectoryFiles(scriptDir, null);
            List<File> jarFileListScriptDir = FileUtils.selectFiles(filesScriptDir, ".jar");
            if (jarFileListScriptDir != null && !jarFileListScriptDir.isEmpty()) {
                for (File file : jarFileListScriptDir) {
                    try {
                        jarFilePathList.add(file.getCanonicalPath());
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        }

        //  脚本未找到 回传
        if (scriptFile == null || !scriptFile.exists()) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED,
                    "No script file found, 请检查配置目录是否正确");
            logger.warn("No script file found, 请检查配置目录是否正确");
            System.exit(-1);
        }

        context.setScriptFile(scriptFile);
        context.setJarFilePathList(jarFilePathList);
    }

    private static void initialPressureContext(Map<String, Object> taskInfoMap) {
        context = new PressureContext();

        context.setMemSetting(
                taskInfoMap.containsKey("memSetting") ? String.valueOf(taskInfoMap.get("memSetting")) : null);

        context.setTaskParams(taskInfoMap);

        Long sceneId = TryUtils.tryOperation(() -> Long.parseLong(StringUtils
                .removePoint(String.valueOf(taskInfoMap.get(TakinRequestConstant.CLUSTER_TEST_SCENE_HEADER_VALUE)))));
        if(sceneId == null || sceneId == 0) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "无效的场景id参数");
            logger.error("invalid pressure sceneId, sceneId is '{}'", sceneId);
            System.exit(-1);
        }
        context.setSceneId(sceneId);
        Long reportId = TryUtils.tryOperation(() -> Long.parseLong(StringUtils
                .removePoint(String.valueOf(taskInfoMap.get(TakinRequestConstant.CLUSTER_TEST_TASK_HEADER_VALUE)))));
        if(reportId == null || reportId == 0) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "无效的报告id参数");
            logger.error("invalid pressure reportId, reportId is '{}'", reportId);
            System.exit(-1);
        }
        context.setReportId(reportId);
        Long customerId = TryUtils.tryOperation(() -> Long.parseLong(StringUtils
                .removePoint(String.valueOf(taskInfoMap.get(TakinRequestConstant.CLUSTER_TEST_CUSTOMER_HEADER_VALUE)))));
        context.setCustomerId(customerId);

        String taskDir = StringUtils.formatStr(Constants.PRESSURE_TASK_DIR);
        //压测引擎log路径
        String logDir = TryUtils.tryOperation(() -> String.valueOf(taskInfoMap.get("pressureEnginePathUrl")));
        if(StringUtils.isBlank(logDir)) {
            logDir = StringUtils.formatStr(Constants.PRESSURE_LOG_DIR);
        }
        //logs
        String logPath = logDir + "logs";
        logPath = logPath + File.separator + sceneId + File.separator + reportId;
        System.setProperty(Constants.TASK_LOG_DIR_KEY, logPath);
        // ptl
        String ptlPath = logDir + "ptl";
        ptlPath = ptlPath + File.separator + sceneId + File.separator + reportId;
        System.setProperty(Constants.TASK_JTL_DIR_KEY, ptlPath);

        context.setTaskDir(taskDir);
        context.setLogDir(logPath);
        context.setPtlDir(ptlPath);
        context.setResourcesDir(taskDir + "resources");

        //脚本路径
        String scriptPath = TryUtils.tryOperation(() -> String.valueOf(taskInfoMap.get("scriptPath")));
        context.setScriptPath(scriptPath);
        //获取引擎插件路径
        List<String> enginePluginsFilePath = TryUtils.tryOperation(
                () -> (List<String>)taskInfoMap.get("enginePluginsFilePath"));
        context.setEnginePluginsFilePath(enginePluginsFilePath);
        //施压模式
        context.setPressureMode(TryUtils.tryOperation(() -> String.valueOf(taskInfoMap.get("pressureMode"))));
        //压测时长
        //modify by lipeng  gson会讲数字都转为double科学计数法存储，这里转换回long
        Double continuedTimeDouble = TryUtils.tryOperation(
                () -> (Double)taskInfoMap.get("continuedTime"));
        Long continuedTime = new BigDecimal(continuedTimeDouble).longValue();
        context.setDuration(continuedTime);
        //modify end
        //目标期望值
        context.setExpectThroughput(TryUtils.tryOperation(
                () -> Long.parseLong(StringUtils.removePoint(String.valueOf(taskInfoMap.get("expectThroughput"))))));
        //递增时长
        context.setRampUp(TryUtils.tryOperation(
                () -> Long.parseLong(StringUtils.removePoint(String.valueOf(taskInfoMap.get("rampUp"))))));
        //阶梯层数
        context.setSteps(TryUtils.tryOperation(
                () -> Long.parseLong(StringUtils.removePoint(String.valueOf(taskInfoMap.get("steps"))))));
        //metrics数据上报地址
        String metricCollectorUrl = TryUtils.tryOperation(() -> String.valueOf(taskInfoMap.get("consoleUrl")));
        if (StringUtils.isBlank(metricCollectorUrl)) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "No found metricCollectorUrl");
            System.exit(-1);
        }
        context.setMetricCollectorUrl(metricCollectorUrl);
        //businessMap
        context.setBusinessMap(TryUtils.tryOperation(
                () -> JsonUtils.json2Obj(String.valueOf(taskInfoMap.get("businessMap")), Map.class)));
        //数据文件集合
        context.setDataFileSets(TryUtils.tryOperation(
                () -> (List<Map<String, Object>>)taskInfoMap.get("fileSets")));
        //podCount
        context.setPodCount(TryUtils.tryOperation(
                () -> Integer.parseInt(StringUtils.removePoint(String.valueOf(taskInfoMap.get("podCount"))))));
        //获取引擎压测参数
        Map<String, Object> enginePressureParams = TryUtils.tryOperation(
                () -> (Map<String, Object>)taskInfoMap.get("enginePressureParams"));
        context.setEnginePressureParams(enginePressureParams);
        //引擎压力模式
        String modeValue = String.valueOf(enginePressureParams.get("enginePressureMode"));
        context.setEnginePressureMode(modeValue);
        //引擎压力模式枚举
        //当前引擎压测模式  默认并发模式
        EnginePressureMode currentEnginePressureMode = EnginePressureMode.CONCURRENCY;
        if (StringUtils.isNotBlank(modeValue)) {
            currentEnginePressureMode = EnginePressureMode.getMode(modeValue);
            if (currentEnginePressureMode == null) {
                logger.warn("当前引擎压测模式参数'{}'不在支持的范围内，已将引擎压测模式更改为并发模式", modeValue);
                currentEnginePressureMode = EnginePressureMode.CONCURRENCY;
            }
        }
        context.setCurrentEnginePressureMode(currentEnginePressureMode);
        //启动模式
        context.setStartMode(System.getProperty("start.mode"));
        //采样率
        //采样率 默认1 全部
        String traceSampling = DEFAULT_TRACE_SAMPLING;
        if (enginePressureParams != null && !enginePressureParams.isEmpty()) {
            traceSampling = TryUtils.tryOperation(() -> String.valueOf(enginePressureParams.get("traceSampling")));
            if (traceSampling == null || "null".equals(traceSampling) || StringUtils.isBlank(traceSampling)) {
                traceSampling = DEFAULT_TRACE_SAMPLING;
            }
        }
        context.setTraceSampling(traceSampling);
        //cloud回调地址
        String takinCloudCallbackUrl = TryUtils.tryOperation(() -> String.valueOf(taskInfoMap.get("takinCloudCallbackUrl")));
        if(StringUtils.isBlank(takinCloudCallbackUrl)) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "cloud回调地址不能为空");
            logger.error("cloud callback url can not be empty");
            System.exit(-1);
        }
        context.setCloudCallbackUrl(takinCloudCallbackUrl);
        //初始化全局参数
        // 组装GlobalUserVariables
        GlobalUserVariables globalUserVariables = new GlobalUserVariables();
        globalUserVariables.setSceneId(sceneId+"");
        globalUserVariables.setReportId(reportId+"");
        globalUserVariables.setCustomerId(customerId+"");
        globalUserVariables.setTakinCloudCallbackUrl(takinCloudCallbackUrl);
        globalUserVariables.setEnginePressureMode(context.getCurrentEnginePressureMode().getCode());
        context.setGlobalUserVariables(globalUserVariables);
        //初始化请求头参数
        //组装HttpHeader信息
        HttpHeaderVariables httpHeaderVariables = new HttpHeaderVariables();
        httpHeaderVariables.setPradarTraceId(EngineConstants.GENERATE_TRACE_ID_VALUE);
        //将reportId传递到head
        httpHeaderVariables.setPradarUserdata(context.getReportId() + "");
        //header需要透传rpcid  目前为0
        httpHeaderVariables.setPradarRpcId("0");
        context.setHttpHeaderVariables(httpHeaderVariables);
        //业务活动信息
        //处理业务活动
        List<Map<String, String>> businessActivities = (List<Map<String, String>>)context.getEnginePressureParams()
                .get("businessActivities");
        List<BusinessActivity> listBusinessActivities = new ArrayList<>();
        if(businessActivities != null && businessActivities.size() > 0) {
            for(Map<String, String> map : businessActivities) {
                listBusinessActivities.add(BusinessActivity
                        .build(map.get("elementTestName"), map.get("throughputPercent")));
            }
        }
        context.setBusinessActivities(listBusinessActivities);
        //后端监听器对列长度
        String pressureEngineBackendQueueCapacity = String.valueOf(enginePressureParams
                .get("pressureEngineBackendQueueCapacity"));
        context.setPressureEngineBackendQueueCapacity(pressureEngineBackendQueueCapacity);
        //循环次数
        Long loops = -1L;
        try {
            loops = Long.parseLong(String.valueOf(context.getEnginePressureParams()
                .get("loops_num") == null ? "-1" : context.getEnginePressureParams().get("loops_num")));
        } catch (Exception e) {
            logger.warn("loops_num is invalid, reset to -1");
        }
        context.setLoops(loops);
    }

}