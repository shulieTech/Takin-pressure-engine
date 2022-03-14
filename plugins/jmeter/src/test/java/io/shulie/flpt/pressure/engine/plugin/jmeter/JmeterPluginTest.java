package io.shulie.flpt.pressure.engine.plugin.jmeter;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @author xuyh
 */
@Slf4j
public class JmeterPluginTest {

    public static void main(String[] args) {
        File jmxFile = new File("/Users/johnson/job-workspace/prada/pradar-splits/pressure-engine/plugins/jmeter/src/main/resources/test.xml");
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(jmxFile);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
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
                        System.out.println(stringPropElement);
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
                            System.out.println(stringPropElement);
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
                            System.out.println(stringPropElement);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            System.out.println(stringPropElement);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.delay")) {
                            // duration
                            System.out.println(stringPropElement);
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
                            System.out.println(stringPropElement);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            System.out.println(stringPropElement);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.delay")) {
                            // duration
                            System.out.println(stringPropElement);
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
                            System.out.println(stringPropElement);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            System.out.println(stringPropElement);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.delay")) {
                            // duration
                            System.out.println(stringPropElement);
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
                            System.out.println(stringPropElement);
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
                            System.out.println(stringPropElement);
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
                            System.out.println(stringPropElement);
                        }
                    }
                }
            }

        }
    }
}
