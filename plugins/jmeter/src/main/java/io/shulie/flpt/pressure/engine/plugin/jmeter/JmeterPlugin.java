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

package io.shulie.flpt.pressure.engine.plugin.jmeter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.gson.internal.LinkedTreeMap;
import io.shulie.flpt.pressure.engine.api.ability.EnginePressureModeAbility;
import io.shulie.flpt.pressure.engine.api.ability.SupportedPressureModeAbilities;
import io.shulie.flpt.pressure.engine.api.entity.BusinessActivityConfig;
import io.shulie.flpt.pressure.engine.api.entity.EnginePressureConfig;
import io.shulie.flpt.pressure.engine.api.entity.EnginePtlLogConfig;
import io.shulie.flpt.pressure.engine.api.enums.EngineType;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.api.plugin.PressurePlugin;
import io.shulie.flpt.pressure.engine.api.plugin.response.StopResponse;
import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.plugin.jmeter.script.ScriptModifier;
import io.shulie.flpt.pressure.engine.plugin.jmeter.util.DomUtils;
import io.shulie.flpt.pressure.engine.plugin.jmeter.util.JmeterPluginUtil;
import io.shulie.flpt.pressure.engine.util.*;
import io.shulie.flpt.pressure.engine.util.http.HttpNotifyTakinCloudUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Create by xuyh at 2020/4/19 23:05.
 */
@SuppressWarnings("all")
public class JmeterPlugin implements PressurePlugin {

    private static Logger logger = LoggerFactory.getLogger(JmeterPlugin.class);
    private String finalJmxFilePathName;
    private Process jmeterProcess;

    /**
     * 之后废弃
     *
     * @return
     */
    @Deprecated
    private static String[] metricArgsProcess(PressureContext context, String[] argsRaw) {
        List<String> argsList = new ArrayList<>();
        for (String arg : argsRaw) {
            argsList.add(arg);
        }
//        Map<String, Object> params = context.getTaskParams();
        String backend_listener_scene_id_Value = String.valueOf(context.getSceneId());
        String backend_listener_report_id_Value = String.valueOf(context.getReportId());
        // 新增 客户id 一定有
        String backend_listener_customer_id_Value = String.valueOf(context.getCustomerId());
        Map<String, BusinessActivityConfig> businessMap = context.getBusinessMap();
//        Map<String, Object> businessMap = TryUtils.tryOperation(() -> (Map<String, Object>)params.get("businessMap"));
        if (backend_listener_scene_id_Value != null && !backend_listener_scene_id_Value.equals("null")) {
            argsList.add("-Dbackend_listener_scene_id=" + backend_listener_scene_id_Value);
        }
        if (backend_listener_report_id_Value != null && !backend_listener_report_id_Value.equals("null")) {
            argsList.add("-Dbackend_listener_report_id=" + backend_listener_report_id_Value);
        }
        // todo 回传
        if (backend_listener_customer_id_Value != null && !backend_listener_customer_id_Value.equals("null")) {
            argsList.add("-Dbackend_listener_customer_id=" + backend_listener_customer_id_Value);
        }
        EnginePressureConfig pressureConfig = context.getPressureConfig();
        EnginePtlLogConfig ptlLogConfig = pressureConfig.getPtlLogConfig();
        if (null != ptlLogConfig) {
            if (null != ptlLogConfig.getPtlUploadFrom()){
                argsList.add("-Dptl.ptlUploadFrom=" + ptlLogConfig.getPtlUploadFrom());
            }
            if (null != ptlLogConfig.getPtlFileEnable()) {
                argsList.add("-Dptl.ptlFileEnable=" + ptlLogConfig.getPtlFileEnable());
            }
            if (null != ptlLogConfig.getPtlFileErrorOnly()) {
                argsList.add("-Dptl.ptlFileErrorOnly=" + ptlLogConfig.getPtlFileErrorOnly());
            }
            if (null != ptlLogConfig.getPtlFileTimeoutOnly()) {
                argsList.add("-Dptl.ptlFileTimeoutOnly=" + ptlLogConfig.getPtlFileTimeoutOnly());
            }
            if (null != ptlLogConfig.getTimeoutThreshold()) {
                argsList.add("-Dptl.timeoutThreshold=" + ptlLogConfig.getTimeoutThreshold());
            }
            if (null != ptlLogConfig.getLogCutOff()) {
                argsList.add("-Dptl.logCutOff=" + ptlLogConfig.getLogCutOff());
            }
        }
        //设置上传队列大小
        argsList.add("-DlogQueueSize=" + pressureConfig.getLogQueueSize());
        argsList.add("-DzkServers=" + pressureConfig.getZkServers());
        argsList.add("-DengineRedisAddress=" + pressureConfig.getEngineRedisAddress());
        argsList.add("-DengineRedisPort=" + pressureConfig.getEngineRedisPort());
        argsList.add("-DengineRedisSentinelNodes=" + pressureConfig.getEngineRedisSentinelNodes());
        argsList.add("-DengineRedisSentinelMaster=" + pressureConfig.getEngineRedisSentinelMaster());
        argsList.add("-DengineRedisPassword=" + pressureConfig.getEngineRedisPassword());


        String[] args = new String[argsList.size()];
        for (int i = 0; i < argsList.size(); i++) {
            args[i] = argsList.get(i);
        }
        return args;
    }

    @Override
    public EngineType engineType() {
        return EngineType.JMETER;
    }

    /**
     * 初始化压力引擎压力模式能力
     *
     * @return
     */
    @Override
    public EnginePressureModeAbility initialEnginePressureModeAbility() {
        return new JmeterPressureModeAbility();
    }

    /**
     * 关闭引擎
     */
    @Override
    public StopResponse stopPressureTest(PressureContext context) {
        //  sh  /home/opt/flpt/pressure-engine/engines/jmeter/bin/shutdown.sh
        String binDir = System.getProperty("jmeter.home") + File.separator + "bin";
        StringBuilder cmd = new StringBuilder();
        cmd.append(binDir).append("/shutdown.sh");
        AtomicReference<String> msg = new AtomicReference<>("");
        int exitValue = ProcessUtils.stop(
                cmd.toString(), binDir, 10L,
                process -> System.out.println(""),
                message -> {
                    logger.info(message);
                    msg.set(message);
                }
        );
        logger.info("Jmeter interrupt finished, exit value: {}", exitValue);
        return StopResponse.build(exitValue, msg.get());
    }

    /**
     * 初始化引擎插件  比如支持dubbo或者kafka的插件
     *
     * @param context
     * @return
     * @author lipeng
     */
    @Override
    public void initializeEnginePlugins(PressureContext context) {
        //获取路径
        List<String> enginePluginsFilePath = context.getEnginePluginsFilePath();

        //目录未设置说明不需要额外插件
        if (enginePluginsFilePath == null || enginePluginsFilePath.size() == 0) {
            logger.warn("no extra engine plugins loaded !");
            return;
        }

        //将插件复制jmeter插件目录
        enginePluginsFilePath.forEach(item -> JmeterPluginUtil.copyPluginsToJmeter(item));

    }

    /**
     * 修改压测脚本
     *
     * @param context {@link PressureContext}
     * @param supportedPressureModeAbilities 支持的压力模式
     * @return true: 准备成功 false: 失败，停止进程
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean doModifyScript(PressureContext context
            , SupportedPressureModeAbilities supportedPressureModeAbilities) {
        //  获取数据 组装header
        Long sceneId = context.getSceneId();
        Long reportId = context.getReportId();
        Long customerId = context.getCustomerId();

        // 解析jmx
        String jmxFileContent = FileUtils.readTextFileContent(context.getScriptFile());
        // 处理特殊字符
        jmxFileContent = JmeterPluginUtil.specialCharRepBefore(jmxFileContent);
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new ByteArrayInputStream(jmxFileContent.getBytes()));
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        //最终脚本文件
        finalJmxFilePathName = context.getResourcesDir() + File.separator + "final" + File.separator + "test.jmx";
        if (document == null) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "No jmx file found");
            FileUtils.writeTextFile(jmxFileContent, finalJmxFilePathName);
            logger.error("找不到jmx文件,或者文件内容为空：file="+context.getScriptFile().getAbsolutePath());
            System.exit(-100);
        }

        //修改脚本
        boolean modifySuccess = ScriptModifier.modifyDocument(document, context, supportedPressureModeAbilities);
        if (!modifySuccess) {
            logger.error("jmx文件内容不符合预期，请检测jmx文件内容");
            System.exit(-101);
        }

        // xpath 实现 HTTP信息头管理器 插入 或者 header add 流量染色
        //ScriptModifier.headerManagerModify(document, sceneId + "", reportId + "", customerId + "");

        //modify start by lipeng  使用dom4j修改jmx文件  将sceneId,reportId,customerId写入头
        //这里之前的实现是在下面保存文件后 使用jmeter源码的工具类，原来的实现是将xml转为java对象，所以对对象有强依赖
        //而我们这里可能需要集成第三方插件，比如dubbo，这些依赖包我们是直接集成到jmeter中的而非这个项目
        //如果使用上面的实现需要将对应的插件包引入此项目，那么就可能出现比如多个dubbo版本的jar包冲突问题，
        //这里改用dom4j去解析node节点，根据节点名（字符串）的形式找到对应的节点去更改sceneId,reportId,customerId即可。
        // 2021/03/26 mark by lipeng
        // 这里用dom修改 不做保存文件操作，之前那种读取文件修改后再保存会将文件中的比如shell脚本中的脚本进行压缩
        // 压缩成一行代码 这里如果出现单行注释的情况就会把本来多行未注释的代码也一并注释 造成问题
        // 所以这里改为只修改document 不写入文件，之后修改完再写入文件即可保留shell脚本中的代码格式。
        // mark end
        DomUtils.headerManagerModify(document, sceneId + "", reportId + "", customerId + "");
        //modify end

        //写入最终文件
        String finalStr = JmeterPluginUtil.writeToFinalFile(document, finalJmxFilePathName);

        // add by lipeng  将上传的额外文件复制到resourceDir  处理压测上传接口
        String extraUploadFilePath = Constants.ENGINE_NFS_MOUNTED_PATH + File.separator
            + sceneId + File.separator + "attachments";
        File extraUploadFileFolder = new File(extraUploadFilePath);
        String finalJmxFolder = context.getResourcesDir() + File.separator + "final" + File.separator;
        if (extraUploadFileFolder.exists() && extraUploadFileFolder.isDirectory()) {
            try {
                for (File extraFile : extraUploadFileFolder.listFiles()) {
                    FileUtils.copyFileToDirectory(extraFile, finalJmxFolder);
                }
            } catch (IOException e) {
                logger.warn("拷贝额外上传文件失败。", e);
            }
        }
        // add end

        //将最终的jmx文件写入共享目录
        try {
            logger.info("final jmx file content:"+finalStr);
            String saveFinalJmxPath = context.getLogDir() + File.separator + "test-" + (StringUtils.isNotBlank(
                context.getPodNumber()) ? "-" + context.getPodNumber() : System.currentTimeMillis()) + ".jmx";
            FileUtils.writeTextFile(finalStr, saveFinalJmxPath);
        } catch (Exception e) {
            logger.error("组装的jmx文件写入共享目录失败", e);
        }
        return true;
    }

    @Override
    public void doPressureTest(PressureContext context) {
        // 参数执行 日志目录指定 jmeter占用端口指定
        String jmeterLogFilePath = context.getLogDir() + File.separator + "jmeter-" + System.currentTimeMillis()
                + ".log";
        String portRule = "-Jbeanshell.server.port=" + JmeterPluginUtil.availablePortAcquire(10200);
        //打印jtl日志
        String ptlPath = context.getPtlDir() + File.separator + "pressure-" + (StringUtils.isNotBlank(context.getPodNumber())
            ? context.getPodNumber() : System.currentTimeMillis()) + ".jtl";
        //id三兄弟
        String sceneId = context.getSceneId() + "";
        String reportId = context.getReportId() + "";
        String customerId = context.getCustomerId() + "";
        //podnum
        String podNum = context.getPodCount() == 1 ? "1" : context.getPodNumber();
        //采样率 默认1 全部
        Integer traceSampling = context.getTraceSampling();

        logger.info(" >>>>> 当前场景ID为[{}], 任务ID[{}], 采样率为[{}]", sceneId, reportId, traceSampling);
        String[] args = new String[] { "-Duser.timezone=Asia/Shanghai", "-Djava.net.preferIPv4Stack=true"
            , "-Djava.net.preferIPv4Addresses=true"
            , "-Dengine.perssure.mode=" + context.getPressureScene().getCode()
            ,"-Dpod.number=" + podNum, "-DSceneId=" + sceneId, "-DReportId=" + reportId
            , "-DCustomerId=" + customerId, "-DCallbackUrl=" + context.getCloudCallbackUrl()
            , "-DSamplingInterval=" + traceSampling};
        String[] jmeterParam = new String[]{"-n", "-t", finalJmxFilePathName, " -l " + ptlPath
                , "-j", jmeterLogFilePath, portRule};
        //组装后端监听器参数
        args = metricArgsProcess(context, args);
        String startMode = context.getStartMode();
        if (startMode != null && startMode.equalsIgnoreCase("single")) {
            startInCurrentProcess(context, args ,jmeterParam);
        } else {
            Boolean jmeterDebug = TryUtils.tryOperation(() -> System.getProperty("jmeter.debug") == null ? false
                    : Boolean.parseBoolean(System.getProperty("jmeter.debug")));
            startNewJmeterProcess(context, jmeterDebug, args, jmeterParam);
        }
    }

    private void startInCurrentProcess(PressureContext context, String[] args,String[] jmeterParam) {
        JmeterRunner.run(context, args);
    }

    private void startNewJmeterProcess(PressureContext context, boolean jmeterDebug, String[] args,String[] jmeterParam) {
        Integer duration = context.getDuration();
        Long timeout = null;
        if (null != duration) {
            timeout = duration.longValue();
        }
        String binDir = System.getProperty("jmeter.home") + File.separator + "bin";
        StringBuilder cmd = new StringBuilder();
        cmd.append("java");//jmeter
        if (jmeterDebug) {
            cmd.append(" -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6005");
        }
        if (context.getMemSetting() != null && !context.getMemSetting().isEmpty()) {
            cmd.append(" ");
            cmd.append(context.getMemSetting());
        }
        for (String arg : args) {
            cmd.append(" ");
            cmd.append(arg);
        }
        cmd.append(" -jar ApacheJMeter.jar");
        for (String arg : jmeterParam){
            cmd.append(" ");
            cmd.append(arg);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook proceed, shutdown.");
            if (jmeterProcess != null && jmeterProcess.isAlive()) {
                TryUtils.retry(() -> {
                    jmeterProcess.destroyForcibly();
                    sleep(3_000);
                    if (jmeterProcess.isAlive()) {
                        throw new RuntimeException("Jmeter process close failed.");
                    }
                }, 3);
            }
        }));
        logger.info("cmd:");
        logger.info(cmd.toString());
        int exitValue = ProcessUtils.run(
                cmd.toString(), binDir, timeout + 10,
                process -> jmeterProcess = process,
                message -> logger.info(message)
        );
        //TODO 这里如果exitValue不为0，有可能是timeout超时了，
        // 但是由于是非正常关闭，jmeter逻辑没有通知takin-cloud结束通知，所以这里需要通知一下takin-cloud结束了
        // mark by lipeng
        logger.info("Jmeter run finished, exit value: {}", exitValue);
        TryUtils.retry(() -> {
            jmeterProcess.destroyForcibly();
            sleep(3_000);
            if (jmeterProcess.isAlive()) {
                throw new RuntimeException("Jmeter process close failed.");
            }
        }, 3);
    }

    private static void sleep(long timeMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeMillis);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * 解析压测数据
     *
     * @param pressureData
     */
    @Override
    public void doResolvePressureData(PressureContext context, List<Map<String, Object>> pressureData) {
        // 这里的podNum 对应的是序号，存在环境变量中，不是总数
        String podNum = context.getPodNumber();
        int podIndex = 1;
        if (StringUtils.isNotBlank(podNum)) {
            podIndex = Integer.parseInt(podNum);
        }
        logger.info("当前POD序号为 >>> [{}]", podIndex);
        //csv参数
        //多个文件，获取当前pod需要读取的文件名及文件de开始结束位置
        //处理csv逻辑分片
        if (pressureData != null) {
            //多个文件，获取当前pod需要读取的文件名及文件de开始结束位置
            JSONObject variablesJson = new JSONObject();
            for (Map<String, Object> csvConfig : pressureData) {
                Object obj = csvConfig.get("startEndPositions");
                String fileName = csvConfig.get("name").toString();
                if (obj != null) {
                    Map<Integer, Object> pairMap = (Map<Integer, Object>)obj;
                    Object o = pairMap.get(podIndex - 1);
                    logger.info("获取到文件读取位置信息：{}",o.toString());
                    if (o instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray)o;
                        if (!jsonArray.isEmpty()){
                            JSONObject position = jsonArray.getJSONObject(0);
                            variablesJson.put(fileName,position.toJSONString());
                        }
                        //ArrayList<Map<String, String>> positionList = (ArrayList<Map<String, String>>)o;
                        //if (!positionList.isEmpty()) {
                        //    Map<String, String> positionMap = positionList.get(0);
                        //    variablesJson.put(fileName, JSONObject.toJSONString(positionMap));
                        //}
                    }
                    //兼容之前的分片数据
                    else if (o instanceof Map) {
                        LinkedTreeMap<String, String> positionMap = (LinkedTreeMap<String, String>)o;
                        variablesJson.put(fileName, JSONObject.toJSONString(positionMap));
                    }
                }
            }
            context.getGlobalUserVariables().setGlobalVariablesMap(variablesJson.toJSONString());
        }
    }

}
