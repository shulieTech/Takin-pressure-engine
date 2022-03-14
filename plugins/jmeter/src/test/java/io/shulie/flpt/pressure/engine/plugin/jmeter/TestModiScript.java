package io.shulie.flpt.pressure.engine.plugin.jmeter;

import java.io.File;
import java.util.List;
import java.io.FileWriter;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.Attribute;
import org.dom4j.io.SAXReader;

import lombok.extern.slf4j.Slf4j;

import io.shulie.flpt.pressure.engine.util.FileUtils;
import io.shulie.flpt.pressure.engine.util.StringWriter;

/**
 * @author xuyh
 */
@Slf4j
public class TestModiScript {

    public static void main(String[] args) {
        File jmxFile = new File("/Users/johnson/Desktop/yunda-poc02.jmx");
        String jmxFileContent = FileUtils.readTextFileContent(jmxFile);
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(jmxFile);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        String finalJmxFilePathName = "/Users/johnson/Desktop/final-test.jmx";
        if (document == null) {
            FileUtils.writeTextFile(jmxFileContent, finalJmxFilePathName);
        }
        String concurrence = "10";
        String duration = "300";

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
                        try {
                            stringPropElement.setText("slslsl");
                        } catch (Exception e) {
                            log.warn(e.getMessage(), e);
                        }
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
                            try {
                                stringPropElement.setText("dadadad");
                            } catch (Exception e) {
                                log.warn(e.getMessage(), e);
                            }
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
                            stringPropElement.setText(concurrence);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            stringPropElement.setText(duration);
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
                            stringPropElement.setText(concurrence);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            stringPropElement.setText(duration);
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
                            stringPropElement.setText(concurrence);
                        }
                        if (nameAttrValue != null && nameAttrValue.equals("ThreadGroup.duration")) {
                            // duration
                            stringPropElement.setText(duration);
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
                            stringPropElement.setText(concurrence);
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
                            stringPropElement.setText(concurrence);
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
                            stringPropElement.setText(concurrence);
                        }
                    }
                }

                List<Element> hashTree3Elements = hashTree2Element.elements("hashTree");
                for (Element hashTree3Element : hashTree3Elements) {
                    addBackEndListener(hashTree3Element, "asasas");
                }
            }
        }

        StringWriter stringWriter = null;
        try {
            stringWriter = new StringWriter();
            document.write(stringWriter);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            if (stringWriter != null) {
                try {
                    stringWriter.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }

        String finalStr = stringWriter.getString();
        /*
         * 写入最终压测文件
         */
        File file = FileUtils.createFilePreDelete(finalJmxFilePathName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(finalStr);
            writer.flush();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    private static void addBackEndListener(Element element, String metricCollectorUrl) {
        Element backendListener = element.addElement("BackendListener");
        backendListener.addAttribute("guiclass", "BackendListenerGui");
        backendListener.addAttribute("testclass", "BackendListener");
        backendListener.addAttribute("testname", "后端监听器");
        backendListener.addAttribute("enabled", "true");
        Element elementProp = backendListener.addElement("elementProp");
        elementProp.addAttribute("name", "arguments");
        elementProp.addAttribute("elementType", "Arguments");
        elementProp.addAttribute("guiclass", "ArgumentsPanel");
        elementProp.addAttribute("testclass", "Arguments");
        elementProp.addAttribute("enabled", "true");
        Element collectionProp = elementProp.addElement("collectionProp");
        collectionProp.addAttribute("name", "Arguments.arguments");
        Element elementProp1 = collectionProp.addElement("elementProp");
        elementProp1.addAttribute("name", "influxdbMetricsSender");
        elementProp1.addAttribute("elementType", "Argument");
        Element stringProp11 = elementProp1.addElement("stringProp");
        stringProp11.addAttribute("name", "Argument.name");
        stringProp11.setText("influxdbMetricsSender");
        Element stringProp12 = elementProp1.addElement("stringProp");
        stringProp12.addAttribute("name", "Argument.value");
        stringProp12.setText("org.apache.jmeter.visualizers.backend.influxdb.HttpJsonMetricsSender");
        Element stringProp13 = elementProp1.addElement("stringProp");
        stringProp13.addAttribute("name", "Argument.metadata");
        stringProp13.setText("=");
        Element elementProp2 = collectionProp.addElement("elementProp");
        elementProp2.addAttribute("name", "influxdbUrl");
        elementProp2.addAttribute("elementType", "Argument");
        Element stringProp21 = elementProp2.addElement("stringProp");
        stringProp21.addAttribute("name", "Argument.name");
        stringProp21.setText("influxdbUrl");
        Element stringProp22 = elementProp2.addElement("stringProp");
        stringProp22.addAttribute("name", "Argument.value");
        stringProp22.setText(metricCollectorUrl);
        Element stringProp23 = elementProp2.addElement("stringProp");
        stringProp23.addAttribute("name", "Argument.metadata");
        stringProp23.setText("=");
        Element elementProp3 = collectionProp.addElement("elementProp");
        elementProp3.addAttribute("name", "application");
        elementProp3.addAttribute("elementType", "Argument");
        Element stringProp31 = elementProp3.addElement("stringProp");
        stringProp31.addAttribute("name", "Argument.name");
        stringProp31.setText("application");
        Element stringProp32 = elementProp3.addElement("stringProp");
        stringProp32.addAttribute("name", "Argument.value");
        stringProp32.setText("jmeter_test");
        Element stringProp33 = elementProp3.addElement("stringProp");
        stringProp33.addAttribute("name", "Argument.metadata");
        stringProp33.setText("=");
        Element elementProp4 = collectionProp.addElement("elementProp");
        elementProp4.addAttribute("name", "measurement");
        elementProp4.addAttribute("elementType", "Argument");
        Element stringProp41 = elementProp4.addElement("stringProp");
        stringProp41.addAttribute("name", "Argument.name");
        stringProp41.setText("measurement");
        Element stringProp42 = elementProp4.addElement("stringProp");
        stringProp42.addAttribute("name", "Argument.value");
        stringProp42.setText("jmeter_test");
        Element stringProp43 = elementProp4.addElement("stringProp");
        stringProp43.addAttribute("name", "Argument.metadata");
        stringProp43.setText("=");
        Element elementProp5 = collectionProp.addElement("elementProp");
        elementProp5.addAttribute("name", "summaryOnly");
        elementProp5.addAttribute("elementType", "Argument");
        Element stringProp51 = elementProp5.addElement("stringProp");
        stringProp51.addAttribute("name", "Argument.name");
        stringProp51.setText("summaryOnly");
        Element stringProp52 = elementProp5.addElement("stringProp");
        stringProp52.addAttribute("name", "Argument.value");
        stringProp52.setText("false");
        Element stringProp53 = elementProp5.addElement("stringProp");
        stringProp53.addAttribute("name", "Argument.metadata");
        stringProp53.setText("=");
        Element elementProp6 = collectionProp.addElement("elementProp");
        elementProp6.addAttribute("name", "samplersRegex");
        elementProp6.addAttribute("elementType", "Argument");
        Element stringProp61 = elementProp6.addElement("stringProp");
        stringProp61.addAttribute("name", "Argument.name");
        stringProp61.setText("samplersRegex");
        Element stringProp62 = elementProp6.addElement("stringProp");
        stringProp62.addAttribute("name", "Argument.value");
        stringProp62.setText(".*");
        Element stringProp63 = elementProp6.addElement("stringProp");
        stringProp63.addAttribute("name", "Argument.metadata");
        stringProp63.setText("=");
        Element elementProp7 = collectionProp.addElement("elementProp");
        elementProp7.addAttribute("name", "percentiles");
        elementProp7.addAttribute("elementType", "Argument");
        Element stringProp71 = elementProp7.addElement("stringProp");
        stringProp71.addAttribute("name", "Argument.name");
        stringProp71.setText("percentiles");
        Element stringProp72 = elementProp7.addElement("stringProp");
        stringProp72.addAttribute("name", "Argument.value");
        stringProp72.setText("90;95;99");
        Element stringProp73 = elementProp7.addElement("stringProp");
        stringProp73.addAttribute("name", "Argument.metadata");
        stringProp73.setText("=");
        Element elementProp8 = collectionProp.addElement("elementProp");
        elementProp8.addAttribute("name", "testTitle");
        elementProp8.addAttribute("elementType", "Argument");
        Element stringProp81 = elementProp8.addElement("stringProp");
        stringProp81.addAttribute("name", "Argument.name");
        stringProp81.setText("testTitle");
        Element stringProp82 = elementProp8.addElement("stringProp");
        stringProp82.addAttribute("name", "Argument.value");
        stringProp82.setText("Test name");
        Element stringProp83 = elementProp8.addElement("stringProp");
        stringProp83.addAttribute("name", "Argument.metadata");
        stringProp83.setText("=");
        Element elementProp9 = collectionProp.addElement("elementProp");
        elementProp9.addAttribute("name", "eventTags");
        elementProp9.addAttribute("elementType", "Argument");
        Element stringProp91 = elementProp9.addElement("stringProp");
        stringProp91.addAttribute("name", "Argument.name");
        stringProp91.setText("eventTags");
        Element stringProp92 = elementProp9.addElement("stringProp");
        stringProp92.addAttribute("name", "Argument.value");
        stringProp92.setText("");
        Element stringProp93 = elementProp9.addElement("stringProp");
        stringProp93.addAttribute("name", "Argument.metadata");
        stringProp93.setText("=");
        Element stringProp = backendListener.addElement("stringProp");
        stringProp.addAttribute("name", "classname");
        stringProp.setText("org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient");
        element.addElement("hashTree");
    }
}
