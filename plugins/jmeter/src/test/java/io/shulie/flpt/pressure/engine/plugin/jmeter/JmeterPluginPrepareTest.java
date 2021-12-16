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


import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by xuyh at 2020/7/8 17:31.
 */
public class JmeterPluginPrepareTest {
    public static void main(String[] args) {
        PressureContext context = new PressureContext();
        context.setResourcesDir("/Users/johnson/Downloads/jmeter-scripts");
        Map<String, Object> taskParam = new HashMap<>();
        taskParam.put("scriptPath", "/Users/johnson/Downloads/jmeter-scripts/test-plan.jmx");
        taskParam.put("extJarPath", "");
        taskParam.put("pressureMode", "fixed");
        taskParam.put("expectThroughput", 10);
        taskParam.put("rampUp", 0);
        taskParam.put("steps", 0);
        taskParam.put("continuedTime", 10);
        taskParam.put("consoleUrl", "http://localhost:10010/takin-web/api/collector/receive?scenId=%sreportId=%s");
        List<Map<String, Object>> fileSets = new ArrayList<>();

        Map<String, Object> file1 = new HashMap<>();
        file1.put("name", "tags.csv");
        file1.put("split", false);
        file1.put("path", "/Users/johnson/Downloads/jmeter-scripts/tags.csv");
        fileSets.add(file1);

        Map<String, Object> file2 = new HashMap<>();
        file2.put("name", "test1.csv");
        file2.put("split", false);
        file2.put("path", "/Users/johnson/Downloads/jmeter-scripts/test1.csv");
        fileSets.add(file2);

        Map<String, Object> file3 = new HashMap<>();
        file3.put("name", "test2.csv");
        file3.put("split", false);
        file3.put("path", "/Users/johnson/Downloads/jmeter-scripts/test2.csv");
        fileSets.add(file3);

        taskParam.put("fileSets", fileSets);
//        context.setTaskParams(taskParam);
        System.out.println(new JmeterPlugin().doModifyScript(context, null));
    }
}
