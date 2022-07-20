package io.shulie.flpt.pressure.engine.plugin.jmeter;

import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;
import io.shulie.flpt.pressure.engine.api.entity.EnginePressureConfig;
import io.shulie.flpt.pressure.engine.api.entity.EngineRunConfig;
import io.shulie.flpt.pressure.engine.api.entity.GlobalUserVariables;
import io.shulie.flpt.pressure.engine.api.entity.HttpHeaderVariables;
import io.shulie.flpt.pressure.engine.api.enums.PressureSceneEnum;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.util.FileUtils;
import io.shulie.flpt.pressure.engine.util.JsonUtils;
import io.shulie.flpt.pressure.engine.util.StringUtils;
import io.shulie.flpt.pressure.engine.util.TryUtils;
import io.shulie.flpt.pressure.engine.util.http.HttpNotifyTakinCloudUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuyh
 */
@Slf4j
public class JmeterPluginPrepareTest {

    private static PressureContext context;

    private static final int DEFAULT_TRACE_SAMPLING = 1;

    private static String configurations= "{\n" +
            "\t\"bindByXpathMd5\": true,\n" +
            "\t\"businessMap\": {\n" +
            "\t\t\"e9e3864368fad5af14c371791ac66f97\": {\n" +
            "\t\t\t\"activityName\": \"/data-manager/pub/hello\",\n" +
            "\t\t\t\"bindRef\": \"e9e3864368fad5af14c371791ac66f97\",\n" +
            "\t\t\t\"rate\": 0.31,\n" +
            "\t\t\t\"rt\": 5,\n" +
            "\t\t\t\"tps\": 500\n" +
            "\t\t},\n" +
            "\t\t\"14b83f6218c31f3c17d2573698d468fb\": {\n" +
            "\t\t\t\"activityName\": \"/test-manager/pub/hello\",\n" +
            "\t\t\t\"bindRef\": \"14b83f6218c31f3c17d2573698d468fb\",\n" +
            "\t\t\t\"rate\": 0.06,\n" +
            "\t\t\t\"rt\": 5,\n" +
            "\t\t\t\"tps\": 100\n" +
            "\t\t},\n" +
            "\t\t\"a7592be62cd2feb259126dceeeba634c\": {\n" +
            "\t\t\t\"activityName\": \"/ptcs/test/get\",\n" +
            "\t\t\t\"bindRef\": \"a7592be62cd2feb259126dceeeba634c\",\n" +
            "\t\t\t\"rate\": 0.63,\n" +
            "\t\t\t\"rt\": 5,\n" +
            "\t\t\t\"tps\": 1000\n" +
            "\t\t}\n" +
            "\t},\n" +
            "\t\"callbackUrl\": \"http://10.207.23.32:10010/takin-cloud/api/engine/callback\",\n" +
            "\t\"consoleUrl\": \"http://10.207.23.32:10010/takin-cloud/api/collector/receive?sceneId=4263&reportId=4621&tenantId=2\",\n" +
            "\t\"continuedTime\": 180,\n" +
            "\t\"customerId\": 2,\n" +
            "\t\"enginePluginsFiles\": [],\n" +
            "\t\"memSetting\": \"-Xmx2048m -Xms2048m -Xss256K -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m\",\n" +
            "\t\"podCount\": 3,\n" +
            "\t\"pressureConfig\": {\n" +
            "\t\t\"engineRedisAddress\": \"Mk789B4i-1.cachesit.sfcloud.local:8080,Mk789B4i-2.cachesit.sfcloud.local:8080,Mk789B4i-3.cachesit.sfcloud.local:8080,Mk789B4i-4.cachesit.sfcloud.local:8080,Mk789B4i-5.cachesit.sfcloud.local:8080,Mk789B4i-6.cachesit.sfcloud.local:8080\",\n" +
            "\t\t\"engineRedisPassword\": \"PTbmfUDn9vy4dBVuY8h0Zzuh\",\n" +
            "\t\t\"engineRedisPort\": \"6379\",\n" +
            "\t\t\"engineRedisSentinelMaster\": \"\",\n" +
            "\t\t\"engineRedisSentinelNodes\": \"\",\n" +
            "\t\t\"logQueueSize\": 25000,\n" +
            "\t\t\"maxThreadNum\": 500,\n" +
            "\t\t\"pressureEngineBackendQueueCapacity\": \"5000\",\n" +
            "\t\t\"ptlLogConfig\": {\n" +
            "\t\t\t\"logCutOff\": false,\n" +
            "\t\t\t\"ptlFileEnable\": true,\n" +
            "\t\t\t\"ptlFileErrorOnly\": false,\n" +
            "\t\t\t\"ptlFileTimeoutOnly\": false,\n" +
            "\t\t\t\"ptlUploadFrom\": \"cloud\"\n" +
            "\t\t},\n" +
            "\t\t\"threadGroupConfigMap\": {\n" +
            "\t\t\t\"863aea38bc7de5cd3e855f0567f856d1\": {\n" +
            "\t\t\t\t\"mode\": 1,\n" +
            "\t\t\t\t\"type\": 1\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"totalTpsTargetLevel\": 1600,\n" +
            "\t\t\"tpsTargetLevel\": 533.33,\n" +
            "\t\t\"tpsTargetLevelFactor\": 0.1,\n" +
            "\t\t\"tpsThreadMode\": 0,\n" +
            "\t\t\"traceSampling\": 100,\n" +
            "\t\t\"zkServers\": \"10.206.129.208:2181,10.206.129.209:2181,10.206.129.210:2181\"\n" +
            "\t},\n" +
            "\t\"pressureScene\": 0,\n" +
            "\t\"sceneId\": 4263,\n" +
            "\t\"scriptFile\": \"/Users/phine/Downloads/4617.jmx\",\n" +
            "\t\"scriptFileDir\": \"/etc/engine/script/\",\n" +
            "\t\"taskId\": 4621\n" +
            "}";
    public static void main(String[] args) {
        EngineRunConfig config = JsonUtils.parseObject(configurations, EngineRunConfig.class);
        initialPressureContext(config);
        handleScriptPath(context);
        //        context.setTaskParams(taskParam);
        System.out.println(new JmeterPlugin().doModifyScript(context, null));
    }

    private static void initialPressureContext(EngineRunConfig config) {
        Long sceneId = config.getSceneId();
        if (null == sceneId || 0 == sceneId) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "无效的场景id参数");
            log.error("无效的sceneId.[{}]", sceneId);
            System.exit(-1);
        }
        Long reportId = config.getTaskId();
        if (null == reportId || 0 == reportId) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "无效的报告id参数");
            log.error("无效的reportId.[{}]", reportId);
            System.exit(-1);
        }
        EnginePressureConfig pressureConfig = config.getPressureConfig();
        if (null == pressureConfig) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "施压参数为空");
            log.error("无效的引擎配置, 引擎配置为空。");
            System.exit(-1);
        }
        //metrics数据上报地址
        String metricCollectorUrl = config.getConsoleUrl();
        if (StringUtils.isBlank(metricCollectorUrl)) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "没有找到metrics数据上报地址");
            System.exit(-1);
        }
        String takinCloudCallbackUrl = config.getCallbackUrl();
        if (StringUtils.isBlank(takinCloudCallbackUrl)) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, "cloud回调地址不能为空");
            log.error("cloud回调地址不能为空");
            System.exit(-1);
        }
        Long customerId = config.getCustomerId();

        context = new PressureContext();
        context.setSceneId(sceneId);
        context.setReportId(reportId);
        context.setCustomerId(customerId);
        context.setMemSetting(config.getMemSetting());
        context.setMetricCollectorUrl(metricCollectorUrl);
        context.setLogDir(config.getScriptFileDir());
        if (StringUtils.isBlank(context.getLogDir())) {
            context.setLogDir(StringUtils.formatStr(Constants.PRESSURE_LOG_DIR));
        }

        String taskDir = "/Users/phine/Downloads/task/";
        //压测引擎log路径
        String logDir = "/Users/phine/Downloads/task/";
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
        context.setScriptPath(config.getScriptFile());
        //获取引擎插件路径
        context.setEnginePluginsFilePath(config.getEnginePluginsFiles());
        context.setOldVersion(BooleanUtils.isFalse(config.getBindByXpathMd5()));
        //引擎压力模式
        Integer pressureScene = config.getPressureScene();
        context.setEnginePressureMode(pressureScene);
        //引擎压力模式枚举
        //当前引擎压测模式  默认并发模式
        //TODO 压测引起模式需要改造成：常规模式、试跑模式、巡检模式
        PressureSceneEnum pressureSceneEnum = PressureSceneEnum.value(pressureScene);
        if (null == pressureSceneEnum) {
            log.warn("当前引擎压测模式参数'{}'不在支持的范围内，已将引擎压测模式更改为并发模式", pressureScene);
            pressureSceneEnum = PressureSceneEnum.DEFAULT;
        }
        if (pressureSceneEnum == PressureSceneEnum.TRY_RUN || pressureSceneEnum == PressureSceneEnum.INSPECTION_MODE) {
            context.setFix(config.getPressureConfig().getFixedTimer());
            context.setLoops(config.getPressureConfig().getLoopsNum());
        }
        context.setPressureScene(pressureSceneEnum);

        //压测时长
        context.setDuration(config.getContinuedTime());
        //目标期望值
        context.setExpectThroughput(config.getExpectThroughput());
        //业务活动目标配置
        context.setBusinessMap(config.getBusinessMap());
        context.setBindByXpathMd5(config.getBindByXpathMd5());
        //数据文件集合
        context.setDataFileSets(config.getFileSets());
        //podCount
        context.setPodCount(config.getPodCount());
        //压测配置信息
        context.setPressureConfig(config.getPressureConfig());
        //启动模式
        context.setStartMode(System.getProperty("start.mode"));
        //pod序号
        context.setPodNumber(System.getProperty("pod.number"));

        //采样率 默认1 全部
        int traceSampling = DEFAULT_TRACE_SAMPLING;
        if (null != pressureConfig.getTraceSampling()) {
            traceSampling = pressureConfig.getTraceSampling();
        }
        context.setTraceSampling(traceSampling);
        //cloud回调地址
        context.setCloudCallbackUrl(takinCloudCallbackUrl);

        //初始化全局参数
        // 组装GlobalUserVariables
        GlobalUserVariables globalUserVariables = new GlobalUserVariables();
        globalUserVariables.setSceneId(sceneId + "");
        globalUserVariables.setReportId(reportId + "");
        globalUserVariables.setCustomerId(customerId + "");
        globalUserVariables.setTakinCloudCallbackUrl(takinCloudCallbackUrl);
        context.setGlobalUserVariables(globalUserVariables);
        //初始化请求头参数
        //组装HttpHeader信息
        HttpHeaderVariables httpHeaderVariables = new HttpHeaderVariables();
        httpHeaderVariables.setPradarTraceId(EngineConstants.GENERATE_TRACE_ID_VALUE);
        //将reportId传递到head
        httpHeaderVariables.setPradarUserdata(reportId + "");
        //header需要透传rpcId  目前为0
        httpHeaderVariables.setPradarRpcId("0");
        context.setHttpHeaderVariables(httpHeaderVariables);
        //后端监听器对列长度
        String pressureEngineBackendQueueCapacity = pressureConfig.getPressureEngineBackendQueueCapacity();

        context.setPressureEngineBackendQueueCapacity(pressureEngineBackendQueueCapacity);
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
                    log.warn(e.getMessage(), e);
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
                        log.warn(e.getMessage(), e);
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
                        log.warn(e.getMessage(), e);
                    }
                }
            }
        }

        //  脚本未找到 回传
        if (!scriptFile.exists()) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED,
                    "No script file found, 请检查配置目录是否正确");
            log.warn("No script file found, 请检查配置目录是否正确");
            System.exit(-1);
        }

        context.setScriptFile(scriptFile);
        context.setJarFilePathList(jarFilePathList);
    }
}
