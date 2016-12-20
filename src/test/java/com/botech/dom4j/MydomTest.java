package com.botech.dom4j;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MydomTest {
	public static void main(String[] args) throws Exception {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true); // never forget this!
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse(MydomTest.class.getResourceAsStream("/server-config.xml"));

		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    
		    XPathExpression ipExpr = xpath.compile("/config/ip");
		    Node ip = (Node) ipExpr.evaluate(doc, XPathConstants.NODE);
		    System.out.println(ip.getTextContent());
		    
		    XPathExpression portExpr = xpath.compile("/config/port");
		    Node port = (Node) portExpr.evaluate(doc, XPathConstants.NODE);
		    System.out.println(port.getTextContent());
		    
		    XPathExpression clusterExpr1 = xpath.compile("/config/clusteredid");
		    Node clusteredid = (Node) clusterExpr1.evaluate(doc, XPathConstants.NODE);
		    System.out.println(clusteredid.getTextContent());
		    
		    XPathExpression messageserviceExpr = xpath.compile("/config/messageservice");
		    Node messageservice = (Node) messageserviceExpr.evaluate(doc, XPathConstants.NODE);
		    System.out.println(messageservice.getTextContent());
		    
		    XPathExpression codesExpr = xpath.compile("/config/business/code");
		    Object codes = codesExpr.evaluate(doc, XPathConstants.NODESET);
		    NodeList nodes = (NodeList) codes;
		    for (int i = 0; i < nodes.getLength(); i++) {
		    	NamedNodeMap attr = nodes.item(i).getAttributes();
		        System.out.println(attr.getNamedItem("id").getTextContent()); 
		        System.out.println(attr.getNamedItem("class").getTextContent()); 
		    }
	}
}
