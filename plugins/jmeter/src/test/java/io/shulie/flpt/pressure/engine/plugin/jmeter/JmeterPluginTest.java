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
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Create by xuyh at 2020/4/20 22:37.
 */
public class JmeterPluginTest {
    private static Logger logger = LoggerFactory.getLogger(JmeterPluginTest.class);

    public static void main(String[] args) {
        File jmxFile = new File("/Users/johnson/job-workspace/prada/pradar-splits/pressure-engine/plugins/jmeter/src/main/resources/test.xml");
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(jmxFile);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        if (document == null) {
            return;
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
                        // jar
                        System.out.println(stringPropElement.toString());
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
                            // csv
                            System.out.println(stringPropElement.toString());
                        }
                    }
                }

                List<Element> threadGroupElements = hashTree2Element.elements("ThreadGroup");
                for (Element threadGroupElement : threadGroupElements) {
                    List<Element> stringPropElements = threadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.num_threads")) {
                            // concurrent
                            System.out.println(stringPropElement.toString());
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            System.out.println(stringPropElement.toString());
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.delay")) {
                            // duration
                            System.out.println(stringPropElement.toString());
                        }
                    }
                }

                List<Element> postThreadGroupElements = hashTree2Element.elements("PostThreadGroup");
                for (Element postThreadGroupElement : postThreadGroupElements) {
                    List<Element> stringPropElements = postThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.num_threads")) {
                            // concurrent
                            System.out.println(stringPropElement.toString());
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            System.out.println(stringPropElement.toString());
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.delay")) {
                            // duration
                            System.out.println(stringPropElement.toString());
                        }
                    }
                }

                List<Element> setupThreadGroupElements = hashTree2Element.elements("SetupThreadGroup");
                for (Element setupThreadGroupElement : setupThreadGroupElements) {
                    List<Element> stringPropElements = setupThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.num_threads")) {
                            // concurrent
                            System.out.println(stringPropElement.toString());
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            System.out.println(stringPropElement.toString());
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.delay")) {
                            // duration
                            System.out.println(stringPropElement.toString());
                        }
                    }
                }

                List<Element> arrivalsThreadGroupElements = hashTree2Element.elements("com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup");
                for (Element arrivalsThreadGroupElement : arrivalsThreadGroupElements) {
                    List<Element> stringPropElements = arrivalsThreadGroupElement.elements("stringProp");
                    for (Element stringPropElement : stringPropElements) {
                        Attribute nameAttr = stringPropElement.attribute("name");
                        String nameAttrValue = nameAttr.getValue();
                        if (nameAttrValue != null && nameAttrValue.equals("TargetLevel")) {
                            // concurrent
                            System.out.println(stringPropElement.toString());
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
                            // concurrent
                            System.out.println(stringPropElement.toString());
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
                            // concurrent
                            System.out.println(stringPropElement.toString());
                        }
                    }
                }
            }

        }
    }
}
