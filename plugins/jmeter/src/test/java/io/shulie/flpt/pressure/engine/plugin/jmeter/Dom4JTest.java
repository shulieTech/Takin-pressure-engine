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

import cn.hutool.core.io.FileUtil;
import io.shulie.flpt.pressure.engine.util.FileUtils;
import io.shulie.flpt.pressure.engine.util.StringWriter;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.dom.DOMText;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Create by xuyh at 2020/4/21 18:10.
 */
public class Dom4JTest {
    private static Logger logger = LoggerFactory.getLogger(Dom4JTest.class);

    public static void main(String[] args) {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(FileUtil.file("/Users/johnson/job-workspace/prada/pradar-splits/pressure-engine/plugins/jmeter/src/main/resources/test.xml"));
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        Element root = document.getRootElement();
        List<Element> hashTreeElements = root.elements("hashTree");
        for (Element hashTreeElement : hashTreeElements) {
            List<Element> testPlanElements = hashTreeElement.elements("TestPlan");
            for (Element testPlanElement : testPlanElements) {
                List<Element> stringPropElements = testPlanElement.elements("stringProp");
                for (Element stringPropElement : stringPropElements) {
                    Attribute nameAttr = stringPropElement.attribute("name");
                    String nameAttrValue = nameAttr.getValue();
                    if (nameAttrValue != null && nameAttrValue.equals("TestPlan.user_define_classpath")) {
                    }
                }
            }

            List<Element> hashTree2Elements = hashTreeElement.elements("hashTree");
            for (Element hashTree2Element : hashTree2Elements) {
                List<Element> csvElements = hashTree2Element.elements("CSVDataSet");
                for (Element csvElement : csvElements) {
                    List<Element> stringPropElements = csvElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("filename")) {

                        }
                    }
                }

                List<Element> threadGroupElements = hashTree2Element.elements("ThreadGroup");
                for (Element threadGroupElement : threadGroupElements) {
                    List<Element> stringPropElements = threadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();

                    }
                }

                List<Element> postThreadGroupElements = hashTree2Element.elements("PostThreadGroup");
                for (Element postThreadGroupElement : postThreadGroupElements) {
                    List<Element> stringPropElements = postThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();

                    }
                }

                List<Element> setupThreadGroupElements = hashTree2Element.elements("SetupThreadGroup");
                for (Element setupThreadGroupElement : setupThreadGroupElements) {
                    List<Element> stringPropElements = setupThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();

                    }
                }

                List<Element> arrivalsThreadGroupElements = hashTree2Element.elements("com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup");
                for (Element arrivalsThreadGroupElement : arrivalsThreadGroupElements) {
                    List<Element> stringPropElements = arrivalsThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("TargetLevel")) {

                        }
                    }
                }

                List<Element> concurrencyThreadGroupElements = hashTree2Element.elements("com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup");
                for (Element concurrencyThreadGroupElement : concurrencyThreadGroupElements) {
                    List<Element> stringPropElements = concurrencyThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("TargetLevel")) {

                        }
                    }
                }

                List<Element> steppingThreadGroupElements = hashTree2Element.elements("kg.apc.jmeter.threads.SteppingThreadGroup");
                for (Element steppingThreadGroupElement : steppingThreadGroupElements) {
                    List<Element> stringPropElements = steppingThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.num_threads")) {

                        }
                    }
                }

                String backEndLisnerElementText = "<hashTree/>\n" +
                        "                <BackendListener guiclass=\"BackendListenerGui\" testclass=\"BackendListener\" testname=\"后端监听器\"\n" +
                        "                                 enabled=\"true\">\n" +
                        "                    <elementProp name=\"arguments\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\"\n" +
                        "                                 testclass=\"Arguments\" enabled=\"true\">\n" +
                        "                        <collectionProp name=\"Arguments.arguments\">\n" +
                        "                            <elementProp name=\"influxdbMetricsSender\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">influxdbMetricsSender</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">\n" +
                        "                                    org.apache.jmeter.visualizers.backend.influxdb.HttpJsonMetricsSender\n" +
                        "                                </stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"influxdbUrl\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">influxdbUrl</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">\n" +
                        "                                    http://localhost:10010/takin-web/api/collector/receive?scenId=%s&amp;reportId=%s\n" +
                        "                                </stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"application\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">application</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">jmeter_test</stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"measurement\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">measurement</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">jmeter_test</stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"summaryOnly\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">summaryOnly</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">false</stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"samplersRegex\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">samplersRegex</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">.*</stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"percentiles\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">percentiles</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">90;95;99</stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"testTitle\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">testTitle</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\">Test name</stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                            <elementProp name=\"eventTags\" elementType=\"Argument\">\n" +
                        "                                <stringProp name=\"Argument.name\">eventTags</stringProp>\n" +
                        "                                <stringProp name=\"Argument.value\"></stringProp>\n" +
                        "                                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
                        "                            </elementProp>\n" +
                        "                        </collectionProp>\n" +
                        "                    </elementProp>\n" +
                        "                    <stringProp name=\"classname\">\n" +
                        "                        org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient\n" +
                        "                    </stringProp>\n" +
                        "                </BackendListener>";
                Text text = new DOMText(backEndLisnerElementText);


                hashTree2Element.add(text);
            }

        }


        StringWriter stringWriter = null;
        try {
            stringWriter = new StringWriter();
            document.write(stringWriter);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (stringWriter != null) {
                try {
                    stringWriter.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }

        String finalStr = stringWriter.getString();
        finalStr = finalStr.replaceAll("&lt;", "<");
        finalStr = finalStr.replaceAll("&gt;", ">");

        /*
         * 写入最终压测文件
         */
        File file = FileUtils.createFileDE("/Users/johnson/job-workspace/prada/pradar-splits/pressure-engine/plugins/jmeter/src/main/resources/test-modi.xml");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(finalStr);
            writer.flush();
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
    }
}
