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

package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import com.google.common.collect.Lists;
import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;
import io.shulie.flpt.pressure.engine.plugin.jmeter.consts.DOMNodeConstants;
import io.shulie.takin.constants.TakinRequestConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dom
 *
 * @author lipeng
 * @date 2021-01-11 11:20 上午
 */
public abstract class DomUtils {

    private static Logger logger = LoggerFactory.getLogger(DomUtils.class);

    /**
     * 找当前节点下的所有子节点和子节点的子节点
     */
    public static List<Element> findAllChildElement(Element element) {
        return findAllChildElement(Lists.newArrayList(element));
    }

    /**
     * 找当前节点下的所有子节点和子节点的子节点
     */
    public static List<Element> findAllChildElement(List<Element> elements) {
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        List<Element> children = elements.stream().filter(Objects::nonNull)
                .map(DomUtils::findChildrenContainerElement)
                .filter(Objects::nonNull)
                .map(DomUtils::elements)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(children)) {
            List<Element> shunzi = findAllChildElement(children);
            if (CollectionUtils.isNotEmpty(shunzi)) {
                children.addAll(shunzi);
            }
        }
        return children;
    }

    /**
     * 从元素中获取唯一标识，老版本是testname，新版是testname中的MD5
     */
    public static String getTransaction(Element element) {
        String testName = element.attributeValue("testname");
        if (StringUtils.isBlank(testName)) {
            return null;
        }
        int splitPos = testName.lastIndexOf(EngineConstants.TEST_NAME_MD5_SPLIT);
        String transaction = testName;
        if (-1 != splitPos) {
            transaction = testName.substring(splitPos + EngineConstants.TEST_NAME_MD5_SPLIT.length());
        }
        return transaction;
    }

    /**
     * 返回不含MD5的testname
     */
    public static String getTestName(Element element) {
        String testName = element.attributeValue("testname");
        int splitPos = testName.lastIndexOf(EngineConstants.TEST_NAME_MD5_SPLIT);
        if (-1 != splitPos) {
            testName = testName.substring(0, splitPos);
        }
        return testName;
    }

    /**
     * 添加prop元素
     * @param container 父容器
     * @param name      prop元素name值
     * @param value     prop元素value值，value类型决定了元素的名称，支持4种类型：string，int，double, float
     */
    public static void addBasePropElement(Element container, String name, Object value) {
        if (null == value) {
            return;
        }
        if (value instanceof Double || value instanceof Float) {
            Element node = container.addElement("doubleProp");
            node.addElement("name").setText(name);
            node.addElement("value").setText(io.shulie.flpt.pressure.engine.util.StringUtils.valueOf(value));
            node.addElement("savedValue").setText("0.0");
        } else if (value instanceof Integer) {
            container.addElement("intProp").addAttribute("name", name).setText(io.shulie.flpt.pressure.engine.util.StringUtils.valueOf(value));
        } else if (value instanceof Long) {
            container.addElement("longProp").addAttribute("name", name).setText(io.shulie.flpt.pressure.engine.util.StringUtils.valueOf(value));
        } else if (value instanceof String) {
            container.addElement("stringProp").addAttribute("name", name).setText(io.shulie.flpt.pressure.engine.util.StringUtils.valueOf(value));
        } else if (value instanceof Boolean) {
            container.addElement("boolProp").addAttribute("name", name).setText(io.shulie.flpt.pressure.engine.util.StringUtils.valueOf(value));
        }
    }

    /**
     * 获取Element的子元素
     */
    public static List<Element> elements(Element element) {
        return elements(element, null);
    }

    public static List<Element> elements(Element element, String elementName) {
        if (null == element) {
            return null;
        }
        List<?> elements = null;
        if (StringUtils.isBlank(elementName)) {
            elements = element.elements();
        } else {
            elements = element.elements(elementName);
        }
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        List<Element> list = Lists.newArrayList();
        for (Object o : elements) {
            if (o instanceof Element) {
                list.add((Element) o);
            }
        }
        return list;
    }

    /**
     * 寻找当前节点子节点，即当前节点的hashTree节点
     */
    public static Element findChildrenContainerElement(Element element) {
        if (null == element) {
            return null;
        }
        Element hashTree = null;
        if (element.isRootElement()) {
            hashTree = element.element("hashTree");
        } else {
            List<Element> elements = elements(element.getParent());
            for (int i=0; i<elements.size(); i++) {
                Element e = elements.get(i);
                if (e == element && (i+1)<elements.size()) {
                    Element next = elements.get(i+1);
                    if ("hashTree".equals(next.getName())) {
                        hashTree = next;
                    }
                    break;
                }
            }
        }
        return hashTree;
    }

    /**
     * 判断一个元素是否启用
     */
    public static boolean isNotEnabled(Element element) {
        if (null == element) {
            return true;
        }
        return !Boolean.parseBoolean(element.attributeValue("enabled"));
    }

    public static void headerManagerModify(Document document, String sceneId, String reportId, String customerId) {
        //查找所有HeaderManager节点下的collectionProp节点
        List<Element> elements = document.selectNodes("//HeaderManager/collectionProp");
        if(elements.size() > 0) {
            Map<String, String> headsMap = new HashMap<>();
            headsMap.put(TakinRequestConstant.CLUSTER_TEST_SCENE_HEADER_VALUE, sceneId);
            headsMap.put(TakinRequestConstant.CLUSTER_TEST_TASK_HEADER_VALUE, reportId);
            headsMap.put(TakinRequestConstant.CLUSTER_TEST_CUSTOMER_HEADER_VALUE, customerId);
            for(Element ele : elements) {
                addSubElements(ele, headsMap);
            }
        }
    }

    /**
     * 给每一个HeadManager下的CollectionProp添加sceneId，reportId，customerId
     *
     * @param headManagerCollectionProp HeadManager下的CollectionProp节点对象
     * @param headsMap 头信息map
     */
    private static void addSubElements(Element headManagerCollectionProp, Map<String, String> headsMap) {
        if(headManagerCollectionProp == null || headsMap == null) {
            logger.warn("headManagerCollectionProp or headsMap is empty");
            return;
        }
        for(Map.Entry<String, String> headsEntry : headsMap.entrySet()) {
            addSubElement(headManagerCollectionProp, headsEntry.getKey(), headsEntry.getValue());
        }
    }

    /**
     * 创建子节点
     *
     * @param headManagerCollectionProp
     * @param key    节点key
     * @param value  节点value
     */
    private static void addSubElement(Element headManagerCollectionProp, String key, String value) {
        Element elementProp = headManagerCollectionProp.addElement(DOMNodeConstants.NODE_NAME_ELEMENT_PROP);
        elementProp.addAttribute(DOMNodeConstants.NODE_ATTRIBUTE_NAME, key);
        elementProp.addAttribute(DOMNodeConstants.NODE_ATTRIBUTE_ELEMENT_TYPE, DOMNodeConstants.NODE_VALUE_HEADER);
        Element subElementName = elementProp.addElement(DOMNodeConstants.NODE_NAME_STRING_PROP);
        subElementName.addAttribute(DOMNodeConstants.NODE_ATTRIBUTE_NAME, DOMNodeConstants.NODE_KEY_HEADER_NAME);
        subElementName.setText(key);
        Element subElementValue = elementProp.addElement(DOMNodeConstants.NODE_NAME_STRING_PROP);
        subElementValue.addAttribute(DOMNodeConstants.NODE_ATTRIBUTE_NAME, DOMNodeConstants.NODE_KEY_HEADER_VALUE);
        subElementValue.setText(value);
    }

    /**
     * 保存修改的文件
     *
     * @param file
     * @param doc
     */
    public static void saveFile(File file, Document doc, String... encoding) throws IOException {
        //格式化为缩进格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        //设置编码格式
        format.setEncoding(encoding.length > 0 ? encoding[0] : "utf-8");
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(file),format);
            //写入数据
            writer.write(doc);
        } finally {
            try {
                if(writer != null) {
                    writer.close();
                }
            } catch(IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) throws DocumentException {


    }

    public static void close(Closeable... cloes) {
        try {
            for(Closeable clo : cloes) {
                if(clo!=null) {
                    clo.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
