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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.dom4j.Document;
import org.dom4j.Node;
import org.w3c.dom.NodeList;

/**
 * @author 何仲奇
 * @Package io.shulie.flpt.pressure.engine.plugin.jmeter.util
 * @date 2020/9/22 10:56 上午
 */
public class XpathUtils {

    private static XPath xPath = XPathFactory.newInstance().newXPath();

    /**
     * 根据xPathExpr 查找节点
     * @param document
     * @param xPathExpr
     * @return
     */
    public static List<Node> searchNodeByXPath(Document document,String xPathExpr) {
        List<Node> nodeList = new ArrayList<>();
        if (document != null) {
            try {
                XPathExpression expr = xPath.compile(xPathExpr);
                NodeList nodes = (NodeList) expr.evaluate(document,XPathConstants.NODESET);
                if (nodes != null) {
                    for (int i = 0;i < nodes.getLength();i++) {
                        nodeList.add((Node)nodes.item(i));
                    }
                    return nodeList;
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        return nodeList;
    }
}
