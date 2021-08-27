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

import io.shulie.flpt.pressure.engine.util.FileUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * Create by xuyh at 2020/5/13 09:56.
 */
public class ScriptModifierTest {
    private static Logger logger = LoggerFactory.getLogger(ScriptModifierTest.class);

    public static void main(String[] args) throws Exception {
        File jmxFile = new File("~/test/3/resources/nested.jmx");
        SAXReader reader = new SAXReader();
        Document document = reader.read(jmxFile);
//        List<Element> threadGroups = document.selectNodes("//ThreadGroup");
//        for(Element threadGroup : threadGroups) {
//            threadGroup.setName("com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup");
//            threadGroup.clearContent();
//            threadGroup.addAttribute("guiclass", "com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroupGui");
//            threadGroup.addAttribute("testclass", "com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup");
//            threadGroup.addAttribute("testname", "bzm - Arrivals Thread Group");
//            threadGroup.addAttribute("enabled", "true");
//            threadGroup.addElement("elementProp")
//                    .addAttribute("name", "ThreadGroup.main_controller")
//                    .addAttribute("elementType", "com.blazemeter.jmeter.control.VirtualUserController");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "ThreadGroup.on_sample_error")
//                    .setText("continue");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "TargetLevel")
//                    .setText("12");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "RampUp")
//                    .setText("10");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "Steps")
//                    .setText("5");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "Hold")
//                    .setText("60");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "LogFilename")
//                    .setText("");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "Iterations")
//                    .setText("");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "Unit")
//                    .setText("S");
//            threadGroup.addElement("stringProp")
//                    .addAttribute("name", "ConcurrencyLimit")
//                    .setText("50000");
//        }
//        List<Element> groups = document.getRootElement().elements();
//        System.out.println(groups.size());
        //
//           Element ele =  document.elementByID("abc");
//           if(ele!=null) {
//               return;
//           }
//        List<Element> groups = document.selectNodes("//HTTPSamplerProxy");
          document.elementByID("abc").addAttribute("guiclass","123");
//        Map<String, String> testMap = Maps.newHashMap();
//        testMap.put("abc", "20.0");
//        testMap.put("bcd", "80.0");
//        for(Map.Entry<String, String> entry : testMap.entrySet()) {
//            ScriptModifier.addThroughputControl(document.getRootElement(), entry.getKey(), entry.getValue());
//        }
//        ScriptModifier.addThroughputControl(samples.get(0), 9.0D);
//        ScriptModifier.modifyDocument(document, null, null,
//                PressureTestMode.STAIR.getCode(),
//                "100",
//                "20",
//                "50",
//                "10",
//                "http://localhost:10010/takin-web/api/collector/receive?scenId=%sreportId=%s",
//                null,
//                1,
//                Maps.newHashMap());

        FileWriter writer = null;
        try {
            writer = new FileWriter(FileUtils.createFileDE("~/test/3/resources/jpgc-final.jmx"));
            document.write(writer);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }

        /*List<Map<String, Object>> csvConfigs = JsonUtils.json2Obj("[\n" +
                "    {\n" +
                "        \"name\":\"abc.csv\",\n" +
                "        \"path\":\"/opt/test.csv\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\":\"def.csv\",\n" +
                "        \"path\":\"/opt/cmk.csv\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\":\"ghi.csv\",\n" +
                "        \"path\":\"/opt/lop.csv\"\n" +
                "    }\n" +
                "]", List.class);*/
        //ScriptModifier.modifyDocument(document, null, null,
        //        PressureTestMode.STAIR.getCode(),
        //        "100",
        //        "20",
        //        "50",
        //        "10",
        //        "http://localhost:10010/takin-web/api/collector/receive?scenId=%sreportId=%s");

//        FileWriter writer = null;
//        try {
//            writer = new FileWriter(FileUtils.createFileDE("~/k8s.xml"));
//            document.write(writer);
//        } catch (Exception e) {
//            logger.warn(e.getMessage(), e);
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (Exception e) {
//                    logger.warn(e.getMessage(), e);
//                }
//            }
//        }
    }
}
