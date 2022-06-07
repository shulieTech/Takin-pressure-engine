package io.shulie.flpt.pressure.engine.plugin.jmeter;

import io.shulie.flpt.pressure.engine.api.entity.BusinessActivityConfig;
import io.shulie.flpt.pressure.engine.api.entity.EnginePressureConfig;
import io.shulie.flpt.pressure.engine.api.entity.EnginePtlLogConfig;
import io.shulie.flpt.pressure.engine.api.entity.ThreadGroupConfig;
import io.shulie.flpt.pressure.engine.api.enums.PressureSceneEnum;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuyh
 */
public class JmeterPluginPrepareTest {
    public static void main(String[] args) {
        PressureContext context = new PressureContext();
        context.setResourcesDir("/Users/phine/Downloads/jmeter-scripts");
        Map<String, Object> taskParam = new HashMap<>();
        context.setSceneId("1339");
        context.setBindByXpathMd5(true);
        context.setDuration(60);
        context.setMemSetting("-Xmx2048m -Xms2048m -XX:MaxMetaspaceSize=256m");
        context.setPressureScene(PressureSceneEnum.DEFAULT);
        context.setDynamicTaskTpsUrl("http://192.168.1.28:10181/api/config/dynamic/tps/get?taskId=875");
        context.setPodCount(1);
        context.setScriptFile(new File("/usr/local/apache-jmeter-5.4.1/jmx/BSP派送通知极效前置放行.jmx"));
        context.setPressureConfig(new EnginePressureConfig() {{
            setPtlLogConfig(new EnginePtlLogConfig() {{
                setPtlFileEnable(false);
                setPtlFileTimeoutOnly(false);
                setPtlFileErrorOnly(false);
                setLogCutOff(false);
            }});
            setTpsThreadMode(0);
            setTraceSampling(1);
            HashMap<String, ThreadGroupConfig> map = new HashMap<>();
            map.put("7dae7383a28b5c45069b528a454d1164", new ThreadGroupConfig() {{
                setMode(1);
                setRampUpUnit("s");
                setType(0);
                setThreadNum(1);
            }});
            setThreadGroupConfigMap(map);
        }});
        context.setMetricCollectorUrl("http://192.168.1.28:10010/takin-cloud/notify/metrics/upload_old?jobId=875");
        context.setCustomerId(1L);
        context.setCloudCallbackUrl("http://192.168.1.28:10181/api/resource/notify/state");
        context.setEnginePluginsFilePath(new ArrayList<>());
        HashMap<String, BusinessActivityConfig> busHashMap = new HashMap<>();
        busHashMap.put("ec0446f9165717b8c5da8cd7d030cfca", new BusinessActivityConfig() {{
            setRt(100);
            setRate(100.0);
            setTps(100);
            setActivityName("ec0446f9165717b8c5da8cd7d030cfca");
            setBindRef("ec0446f9165717b8c5da8cd7d030cfca");
        }});
        context.setBusinessMap(busHashMap);
        context.setCsvPositionUrl("http://192.168.1.28:10010/takin-cloud/usage/upload/file");
        context.setReportId(875L);
        context.setPressureEngineBackendQueueCapacity("5000");

        System.out.println(new JmeterPlugin().doModifyScript(context, null));
    }
}
