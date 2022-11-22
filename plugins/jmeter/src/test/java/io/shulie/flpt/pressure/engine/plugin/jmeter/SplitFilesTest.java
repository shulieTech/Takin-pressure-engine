package io.shulie.flpt.pressure.engine.plugin.jmeter;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by: hezhongqi
 * @Package io.shulie.flpt.pressure.engine.plugin.jmeter
 * @ClassName: SplitFilesTest
 * @Description: TODO
 * @Date: 2022/11/22 15:52
 */
@Slf4j
public class SplitFilesTest {
    private static String json = "[{\"taskId\": 1303, \"loopsNum\": null, \"zkServers\": \"47.98.147.197:2181,47.114.123.80:2181,47.111.180"
        + ".227:2181\", \"memSetting\": \"-Xmx2048m -Xms2048m -XX:MaxMetaspaceSize=256m\", \"resourceId\": 1488, \"businessMap\": "
        + "{\"581a45c4cc52963533aa1c40f4cb79ab\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": \"1\", \"bindRef\": "
        + "\"581a45c4cc52963533aa1c40f4cb79ab\", \"successRate\": \"1\", \"activityName\": \"581a45c4cc52963533aa1c40f4cb79ab\"}, "
        + "\"86ae2210cd654fef9ef26fdb3fbf9d69\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": \"1\", \"bindRef\": "
        + "\"86ae2210cd654fef9ef26fdb3fbf9d69\", \"successRate\": \"1\", \"activityName\": \"86ae2210cd654fef9ef26fdb3fbf9d69\"}, "
        + "\"fd53c8297048537c7af791008b1c8bac\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": \"1\", \"bindRef\": "
        + "\"fd53c8297048537c7af791008b1c8bac\", \"successRate\": \"1\", \"activityName\": \"fd53c8297048537c7af791008b1c8bac\"}}, "
        + "\"dataFileList\": [{\"name\": \"test_get.csv\", \"path\": \"1510/test_get.csv\", \"type\": 1, \"refId\": null, \"split\": true, "
        + "\"fileMd5\": null, \"ordered\": null, \"isBigFile\": null, \"startEndPositions\": {\"0\": [{\"end\": 590, \"start\": 0, \"partition\": "
        + "0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, \"start\": 1181, \"partition\": 2}], \"1\": [{\"end\": 590, "
        + "\"start\": 0, \"partition\": 0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, \"start\": 1181, \"partition\": 2}],"
        + " \"2\": [{\"end\": 590, \"start\": 0, \"partition\": 0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, \"start\": "
        + "1181, \"partition\": 2}]}}, {\"name\": \"仅一次控制器.jmx\", \"path\": \"1510/仅一次控制器.jmx\", \"type\": 0, \"refId\": null, \"split\": false, "
        + "\"fileMd5\": null, \"ordered\": null, \"isBigFile\": null, \"startEndPositions\": {}}], \"logQueueSize\": 25000, \"maxThreadNum\": null,"
        + " \"pressureType\": 0, \"ptlLogConfig\": {\"logCutOff\": false, \"ptlFileEnable\": true, \"ptlUploadFrom\": \"engine\", "
        + "\"ptlFileErrorOnly\": false, \"timeoutThreshold\": 3000, \"ptlFileTimeoutOnly\": false}, \"continuedTime\": 1200, \"tpsThreadMode\": 0, "
        + "\"traceSampling\": 1, \"bindByXpathMd5\": true, \"tpsTargetLevel\": null, \"expectThroughput\": null, \"backendQueueCapacity\": 5000, "
        + "\"threadGroupConfigMap\": {\"7dae7383a28b5c45069b528a454d1164\": {\"mode\": 1, \"type\": 0, \"steps\": null, \"rampUp\": null, "
        + "\"threadNum\": \"3\", \"rampUpUnit\": \"s\", \"estimateFlow\": null}}, \"tpsTargetLevelFactor\": 0.1},{\"taskId\": 1303, \"loopsNum\": "
        + "null, \"zkServers\": \"47.98.147.197:2181,47.114.123.80:2181,47.111.180.227:2181\", \"memSetting\": \"-Xmx2048m -Xms2048m "
        + "-XX:MaxMetaspaceSize=256m\", \"resourceId\": 1488, \"businessMap\": {\"581a45c4cc52963533aa1c40f4cb79ab\": {\"rt\": \"1\", \"sa\": "
        + "\"1\", \"tps\": \"1\", \"rate\": \"1\", \"bindRef\": \"581a45c4cc52963533aa1c40f4cb79ab\", \"successRate\": \"1\", \"activityName\": "
        + "\"581a45c4cc52963533aa1c40f4cb79ab\"}, \"86ae2210cd654fef9ef26fdb3fbf9d69\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": "
        + "\"1\", \"bindRef\": \"86ae2210cd654fef9ef26fdb3fbf9d69\", \"successRate\": \"1\", \"activityName\": "
        + "\"86ae2210cd654fef9ef26fdb3fbf9d69\"}, \"fd53c8297048537c7af791008b1c8bac\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": "
        + "\"1\", \"bindRef\": \"fd53c8297048537c7af791008b1c8bac\", \"successRate\": \"1\", \"activityName\": "
        + "\"fd53c8297048537c7af791008b1c8bac\"}}, \"dataFileList\": [{\"name\": \"test_get.csv\", \"path\": \"1510/test_get.csv\", \"type\": 1, "
        + "\"refId\": null, \"split\": true, \"fileMd5\": null, \"ordered\": null, \"isBigFile\": null, \"startEndPositions\": {\"0\": [{\"end\": "
        + "590, \"start\": 0, \"partition\": 0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, \"start\": 1181, \"partition\":"
        + " 2}], \"1\": [{\"end\": 590, \"start\": 0, \"partition\": 0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, "
        + "\"start\": 1181, \"partition\": 2}], \"2\": [{\"end\": 590, \"start\": 0, \"partition\": 0}, {\"end\": 1180, \"start\": 591, "
        + "\"partition\": 1}, {\"end\": 1766, \"start\": 1181, \"partition\": 2}]}}, {\"name\": \"仅一次控制器.jmx\", \"path\": \"1510/仅一次控制器.jmx\", "
        + "\"type\": 0, \"refId\": null, \"split\": false, \"fileMd5\": null, \"ordered\": null, \"isBigFile\": null, \"startEndPositions\": {}}], "
        + "\"logQueueSize\": 25000, \"maxThreadNum\": null, \"pressureType\": 0, \"ptlLogConfig\": {\"logCutOff\": false, \"ptlFileEnable\": true, "
        + "\"ptlUploadFrom\": \"engine\", \"ptlFileErrorOnly\": false, \"timeoutThreshold\": 3000, \"ptlFileTimeoutOnly\": false}, "
        + "\"continuedTime\": 1200, \"tpsThreadMode\": 0, \"traceSampling\": 1, \"bindByXpathMd5\": true, \"tpsTargetLevel\": null, "
        + "\"expectThroughput\": null, \"backendQueueCapacity\": 5000, \"threadGroupConfigMap\": {\"7dae7383a28b5c45069b528a454d1164\": {\"mode\": "
        + "1, \"type\": 0, \"steps\": null, \"rampUp\": null, \"threadNum\": \"3\", \"rampUpUnit\": \"s\", \"estimateFlow\": null}}, "
        + "\"tpsTargetLevelFactor\": 0.1},{\"taskId\": 1303, \"loopsNum\": null, \"zkServers\": \"47.98.147.197:2181,47.114.123.80:2181,47.111.180"
        + ".227:2181\", \"memSetting\": \"-Xmx2048m -Xms2048m -XX:MaxMetaspaceSize=256m\", \"resourceId\": 1488, \"businessMap\": "
        + "{\"581a45c4cc52963533aa1c40f4cb79ab\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": \"1\", \"bindRef\": "
        + "\"581a45c4cc52963533aa1c40f4cb79ab\", \"successRate\": \"1\", \"activityName\": \"581a45c4cc52963533aa1c40f4cb79ab\"}, "
        + "\"86ae2210cd654fef9ef26fdb3fbf9d69\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": \"1\", \"bindRef\": "
        + "\"86ae2210cd654fef9ef26fdb3fbf9d69\", \"successRate\": \"1\", \"activityName\": \"86ae2210cd654fef9ef26fdb3fbf9d69\"}, "
        + "\"fd53c8297048537c7af791008b1c8bac\": {\"rt\": \"1\", \"sa\": \"1\", \"tps\": \"1\", \"rate\": \"1\", \"bindRef\": "
        + "\"fd53c8297048537c7af791008b1c8bac\", \"successRate\": \"1\", \"activityName\": \"fd53c8297048537c7af791008b1c8bac\"}}, "
        + "\"dataFileList\": [{\"name\": \"test_get.csv\", \"path\": \"1510/test_get.csv\", \"type\": 1, \"refId\": null, \"split\": true, "
        + "\"fileMd5\": null, \"ordered\": null, \"isBigFile\": null, \"startEndPositions\": {\"0\": [{\"end\": 590, \"start\": 0, \"partition\": "
        + "0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, \"start\": 1181, \"partition\": 2}], \"1\": [{\"end\": 590, "
        + "\"start\": 0, \"partition\": 0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, \"start\": 1181, \"partition\": 2}],"
        + " \"2\": [{\"end\": 590, \"start\": 0, \"partition\": 0}, {\"end\": 1180, \"start\": 591, \"partition\": 1}, {\"end\": 1766, \"start\": "
        + "1181, \"partition\": 2}]}}, {\"name\": \"仅一次控制器.jmx\", \"path\": \"1510/仅一次控制器.jmx\", \"type\": 0, \"refId\": null, \"split\": false, "
        + "\"fileMd5\": null, \"ordered\": null, \"isBigFile\": null, \"startEndPositions\": {}}], \"logQueueSize\": 25000, \"maxThreadNum\": null,"
        + " \"pressureType\": 0, \"ptlLogConfig\": {\"logCutOff\": false, \"ptlFileEnable\": true, \"ptlUploadFrom\": \"engine\", "
        + "\"ptlFileErrorOnly\": false, \"timeoutThreshold\": 3000, \"ptlFileTimeoutOnly\": false}, \"continuedTime\": 1200, \"tpsThreadMode\": 0, "
        + "\"traceSampling\": 1, \"bindByXpathMd5\": true, \"tpsTargetLevel\": null, \"expectThroughput\": null, \"backendQueueCapacity\": 5000, "
        + "\"threadGroupConfigMap\": {\"7dae7383a28b5c45069b528a454d1164\": {\"mode\": 1, \"type\": 0, \"steps\": null, \"rampUp\": null, "
        + "\"threadNum\": \"3\", \"rampUpUnit\": \"s\", \"estimateFlow\": null}}, \"tpsTargetLevelFactor\": 0.1}]";
    public static void main(String[] args) {
        PressureContext context = new PressureContext();
        List<Map<String, Object>> pressureData = JSON.parseObject(json,new TypeReference<List<Map<String, Object>>>() {});
        context.setPodNumber("1");
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
                            JSONObject position = jsonArray.getJSONObject(podIndex - 1);
                            variablesJson.put(fileName, position.toJSONString());
                        }
                    }
                    //兼容之前的分片数据
                    else if (o instanceof Map) {
                        LinkedTreeMap<String, String> positionMap = (LinkedTreeMap<String, String>)o;
                        variablesJson.put(fileName, JSON.toJSONString(positionMap));
                    }
                }
            }
            context.getGlobalUserVariables().setGlobalVariablesMap(variablesJson.toJSONString());
        }
    }
}
