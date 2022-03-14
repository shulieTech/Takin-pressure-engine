package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import java.util.List;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.dom4j.Node;
import org.dom4j.Document;
import org.w3c.dom.NodeList;

/**
 * @author 何仲奇
 */
public class XpathUtils {

    private static final XPath X_PATH = XPathFactory.newInstance().newXPath();

    /**
     * 根据xmlPathExpr 查找节点
     *
     * @param document    文件
     * @param xmlPathExpr xml路径
     * @return 节点
     */
    public static List<Node> searchNodeByXmlPath(Document document, String xmlPathExpr) {
        List<Node> nodeList = new ArrayList<>();
        if (document != null) {
            try {
                XPathExpression expr = X_PATH.compile(xmlPathExpr);
                NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
                if (nodes != null) {
                    for (int i = 0; i < nodes.getLength(); i++) {
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
