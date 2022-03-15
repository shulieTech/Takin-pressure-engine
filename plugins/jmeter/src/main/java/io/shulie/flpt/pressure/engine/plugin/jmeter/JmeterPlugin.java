package io.shulie.flpt.pressure.engine.plugin.jmeter;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import com.google.gson.internal.LinkedTreeMap;

import io.shulie.flpt.pressure.engine.util.*;
import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.api.enums.EngineType;
import io.shulie.flpt.pressure.engine.api.plugin.PressurePlugin;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.plugin.jmeter.util.DomUtils;
import io.shulie.flpt.pressure.engine.api.entity.EnginePtlLogConfig;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.api.entity.EnginePressureConfig;
import io.shulie.flpt.pressure.engine.api.plugin.response.StopResponse;
import io.shulie.flpt.pressure.engine.plugin.jmeter.script.ScriptModifier;
import io.shulie.flpt.pressure.engine.plugin.jmeter.util.JmeterPluginUtil;
import io.shulie.flpt.pressure.engine.util.http.HttpNotifyTakinCloudUtils;
import io.shulie.flpt.pressure.engine.api.ability.EnginePressureModeAbility;
import io.shulie.flpt.pressure.engine.api.ability.SupportedPressureModeAbilities;

/**
 * JMeter插件
 *
 * @author xuyh
 */
@Slf4j
public class JmeterPlugin implements PressurePlugin {
    private String finalJmxFilePathName;
    private Process jmeterProcess;

    /**
     * 之后废弃
     *
     * @return -
     */
    @Deprecated
    private static String[] metricArgsProcess(PressureContext context, String[] argsRaw) {
        List<String> argsList = new ArrayList<>();
        Collections.addAll(argsList, argsRaw);
        String backendListenerSceneIdValue = String.valueOf(context.getSceneId());
        String backendListenerReportIdValue = String.valueOf(context.getReportId());
        // 新增 客户id 一定有
        String backendListenerCustomerIdValue = String.valueOf(context.getCustomerId());
        if (!"null".equals(backendListenerSceneIdValue)) {
            argsList.add("-D\"backend_listener_scene_id\"=" + backendListenerSceneIdValue);
        }
        if (!"null".equals(backendListenerReportIdValue)) {
            argsList.add("-D\"backend_listener_report_id\"=" + backendListenerReportIdValue);
        }
        // todo 回传
        if (!"null".equals(backendListenerCustomerIdValue)) {
            argsList.add("-D\"backend_listener_customer_id\"=" + backendListenerCustomerIdValue);
        }
        EnginePressureConfig pressureConfig = context.getPressureConfig();
        EnginePtlLogConfig ptlLogConfig = pressureConfig.getPtlLogConfig();
        if (null != ptlLogConfig) {
            if (null != ptlLogConfig.getPtlUploadFrom()) {
                argsList.add("-D\"ptl.ptlUploadFrom\"=" + ptlLogConfig.getPtlUploadFrom());
            }
            if (null != ptlLogConfig.getPtlFileEnable()) {
                argsList.add("-D\"ptl.ptlFileEnable\"=" + ptlLogConfig.getPtlFileEnable());
            }
            if (null != ptlLogConfig.getPtlFileErrorOnly()) {
                argsList.add("-D\"ptl.ptlFileErrorOnly\"=" + ptlLogConfig.getPtlFileErrorOnly());
            }
            if (null != ptlLogConfig.getPtlFileTimeoutOnly()) {
                argsList.add("-D\"ptl.ptlFileTimeoutOnly\"=" + ptlLogConfig.getPtlFileTimeoutOnly());
            }
            if (null != ptlLogConfig.getTimeoutThreshold()) {
                argsList.add("-D\"ptl.timeoutThreshold\"=" + ptlLogConfig.getTimeoutThreshold());
            }
            if (null != ptlLogConfig.getLogCutOff()) {
                argsList.add("-D\"ptl.logCutOff\"=" + ptlLogConfig.getLogCutOff());
            }
        }
        //设置上传队列大小
        argsList.add("-D\"logQueueSize\"=" + pressureConfig.getLogQueueSize());
        argsList.add("-D\"zkServers\"=" + pressureConfig.getZkServers());
        argsList.add("-D\"engineRedisAddress\"=" + pressureConfig.getEngineRedisAddress());
        argsList.add("-D\"engineRedisPort\"=" + pressureConfig.getEngineRedisPort());
        argsList.add("-D\"engineRedisSentinelNodes\"=" + pressureConfig.getEngineRedisSentinelNodes());
        argsList.add("-D\"engineRedisSentinelMaster\"=" + pressureConfig.getEngineRedisSentinelMaster());
        argsList.add("-D\"engineRedisPassword\"=" + pressureConfig.getEngineRedisPassword());

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
     * @return -
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
            cmd.toString(), binDir, 10L, null,
            message -> {
                log.info(message);
                msg.set(message);
            }
        );
        log.info("Jmeter interrupt finished, exit value: {}", exitValue);
        return StopResponse.build(exitValue, msg.get());
    }

    /**
     * 初始化引擎插件  比如支持dubbo或者kafka的插件
     *
     * @param context 压测上下文
     * @author 李鹏
     */
    @Override
    public void initializeEnginePlugins(PressureContext context) {
        //获取路径
        List<String> enginePluginsFilePath = context.getEnginePluginsFilePath();

        //目录未设置说明不需要额外插件
        if (enginePluginsFilePath == null || enginePluginsFilePath.size() == 0) {
            log.warn("no extra engine plugins loaded !");
            return;
        }

        //将插件复制jmeter插件目录
        enginePluginsFilePath.forEach(JmeterPluginUtil::copyPluginsToJmeter);
    }

    /**
     * 修改压测脚本
     *
     * @param context                        {@link PressureContext}
     * @param supportedPressureModeAbilities 支持的压力模式
     * @return true: 准备成功 false: 失败，停止进程
     */
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
            log.warn(e.getMessage(), e);
        }
        //最终脚本文件
        finalJmxFilePathName = context.getResourcesDir() + File.separator + "final" + File.separator + "test.jmx";
        if (document == null) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "No jmx file found");
            FileUtils.writeTextFile(jmxFileContent, finalJmxFilePathName);
            log.error("找不到jmx文件,或者文件内容为空：file=" + context.getScriptFile().getAbsolutePath());
            System.exit(-100);
        }

        //修改脚本
        boolean modifySuccess = ScriptModifier.modifyDocument(document, context, supportedPressureModeAbilities);
        if (!modifySuccess) {
            log.error("jmx文件内容不符合预期，请检测jmx文件内容");
            System.exit(-101);
        }
        // modify start
        /* by 李鹏  使用dom4j修改jmx文件  将sceneId,reportId,customerId写入头
        这里之前的实现是在下面保存文件后 使用jmeter源码的工具类，原来的实现是将xml转为java对象，所以对对象有强依赖
        而我们这里可能需要集成第三方插件，比如dubbo，这些依赖包我们是直接集成到jmeter中的而非这个项目
        如果使用上面的实现需要将对应的插件包引入此项目，那么就可能出现比如多个dubbo版本的jar包冲突问题，
        这里改用dom4j去解析node节点，根据节点名（字符串）的形式找到对应的节点去更改sceneId,reportId,customerId即可。
        */
        /*
         2021/03/26 mark by 李鹏
         这里用dom修改 不做保存文件操作，之前那种读取文件修改后再保存会将文件中的比如shell脚本中的脚本进行压缩
         压缩成一行代码 这里如果出现单行注释的情况就会把本来多行未注释的代码也一并注释 造成问题
         所以这里改为只修改document 不写入文件，之后修改完再写入文件即可保留shell脚本中的代码格式。
         */
        DomUtils.headerManagerModify(document, sceneId + "", reportId + "", customerId + "");
        // modify end

        //写入最终文件
        String finalStr = JmeterPluginUtil.writeToFinalFile(document, finalJmxFilePathName);

        // add by 李鹏  将上传的额外文件复制到resourceDir  处理压测上传接口
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
                log.warn("拷贝额外上传文件失败。", e);
            }
        }
        // add end

        //将最终的jmx文件写入共享目录
        try {
            log.info("final jmx file content:" + finalStr);
            String saveFinalJmxPath = context.getLogDir() + File.separator + "test-" + (StringUtils.isNotBlank(
                context.getPodNumber()) ? "-" + context.getPodNumber() : System.currentTimeMillis()) + ".jmx";
            FileUtils.writeTextFile(finalStr, saveFinalJmxPath);
        } catch (Exception e) {
            log.error("组装的jmx文件写入共享目录失败", e);
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
        //pod number
        String podNum = context.getPodCount() == 1 ? "1" : context.getPodNumber();
        //采样率 默认1 全部
        Integer traceSampling = context.getTraceSampling();

        log.info(" >>>>> 当前场景ID为[{}], 任务ID[{}], 采样率为[{}]", sceneId, reportId, traceSampling);
        String[] args = new String[] {
            "-D\"user.timezone\"=Asia/Shanghai",
            "-D\"java.net.preferIPv4Stack\"=true",
            "-D\"java.net.preferIPv4Addresses\"=true",
            "-D\"engine.pressure.mode\"=" + context.getPressureScene().getCode(),
            "-D\"pod.number\"=" + podNum,
            "-D\"SceneId\"=" + sceneId,
            "-D\"ReportId\"=" + reportId,
            "-D\"CustomerId\"=" + customerId,
            "-D\"CallbackUrl\"=" + context.getCloudCallbackUrl(),
            "-D\"SamplingInterval\"=" + traceSampling};
        String[] jmeterParam = new String[] {"-n", "-t", finalJmxFilePathName, " -l " + ptlPath
            , "-j", jmeterLogFilePath, portRule};
        //组装后端监听器参数
        args = metricArgsProcess(context, args);
        String startMode = context.getStartMode();
        if ("single".equalsIgnoreCase(startMode)) {
            startInCurrentProcess(context, args, jmeterParam);
        } else {
            Boolean jmeterDebug = TryUtils.tryOperation(() -> Boolean.parseBoolean(System.getProperty("jmeter.debug")));
            startNewJmeterProcess(context, jmeterDebug, args, jmeterParam);
        }
    }

    private void startInCurrentProcess(PressureContext context, String[] args, String[] jmeterParam) {
        JmeterRunner.run(context, args);
    }

    private void startNewJmeterProcess(PressureContext context, boolean jmeterDebug, String[] args, String[] jmeterParam) {
        Integer duration = context.getDuration();
        Long timeout = null;
        if (null != duration) {
            timeout = duration.longValue();
        }
        String binDir = System.getProperty("jmeter.home") + File.separator + "bin";
        StringBuilder cmd = new StringBuilder();
        cmd.append("java");
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
        for (String arg : jmeterParam) {
            cmd.append(" ");
            cmd.append(arg);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook proceed, shutdown.");
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
        log.info("cmd:");
        log.info(cmd.toString());
        // 关闭引擎对于JMeter运行时的超时检测
        {
            //if (timeout != null) {timeout += 10;}
            timeout = -1L;
        }
        int exitValue = ProcessUtils.run(
            cmd.toString(), binDir, timeout,
            process -> jmeterProcess = process,
            log::info
        );
        //TODO 这里如果exitValue不为0，有可能是timeout超时了，
        // 但是由于是非正常关闭，jmeter逻辑没有通知takin-cloud结束通知，所以这里需要通知一下takin-cloud结束了
        // mark by 李鹏
        log.info("Jmeter run finished, exit value: {}", exitValue);
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
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 解析压测数据
     *
     * @param pressureData 压测数据
     */
    @Override
    public void doResolvePressureData(PressureContext context, List<Map<String, Object>> pressureData) {
        // 这里的podNum 对应的是序号，存在环境变量中，不是总数
        String podNum = context.getPodNumber();
        int podIndex = 1;
        if (StringUtils.isNotBlank(podNum)) {
            podIndex = Integer.parseInt(podNum);
        }
        log.info("当前POD序号为 >>> [{}]", podIndex);
        /*
        csv参数
        多个文件，获取当前pod需要读取的文件名及文件de开始结束位置
        处理csv逻辑分片
         */
        if (pressureData != null) {
            //多个文件，获取当前pod需要读取的文件名及文件de开始结束位置
            JSONObject variablesJson = new JSONObject();
            for (Map<String, Object> csvConfig : pressureData) {
                Object obj = csvConfig.get("startEndPositions");
                String fileName = csvConfig.get("name").toString();
                if (obj != null) {
                    Map<Integer, Object> pairMap = (Map<Integer, Object>)obj;
                    Object o = pairMap.get(podIndex - 1);
                    log.info("获取到文件读取位置信息：{}", o.toString());
                    if (o instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray)o;
                        if (!jsonArray.isEmpty()) {
                            JSONObject position = jsonArray.getJSONObject(0);
                            variablesJson.put(fileName, position.toJSONString());
                        }
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
