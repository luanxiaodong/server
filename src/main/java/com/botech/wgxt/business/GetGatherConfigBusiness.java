package com.botech.wgxt.business;

import io.netty.channel.ChannelHandlerContext;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.business.Business;
import com.botech.wgxt.ywzx.gather.manager.GatherManager;
import com.thoughtworks.xstream.XStream;

/**
 * 【 客户端向中心服务请求采集配置信息】--------协议号：01010001
* @ClassName: GetGatherConfigBusiness 
* @author luanxd
* @date 2015-8-18 下午2:44:59 
*
*客户端：bctype=01010001,bcmessage=clientId
*服务器：bctype=01010001,bcmessage=XML
 */
public class GetGatherConfigBusiness implements Business {
	private static final Logger log = LoggerFactory.getLogger(GetGatherConfigBusiness.class);
	
	@Override
	public BcPackage process(BcPackage msg, ChannelHandlerContext ctx) {
		String bctype = msg.getBctype();
		String xmlMessage = msg.getBcmessage();
		
		String clientId="";
		try {
			MessasgeGather message =(MessasgeGather) stream.fromXML(xmlMessage);
			clientId=message.getID();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
			try {
				DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			    domFactory.setNamespaceAware(true); // never forget this!
			    DocumentBuilder builder = domFactory.newDocumentBuilder();
			    Document doc = builder.parse(new InputSource(new StringReader(xmlMessage)));
			    XPathFactory factory = XPathFactory.newInstance();
			    XPath xpath = factory.newXPath();
		    	XPathExpression ipExpr = xpath.compile("/message/ID");
		    	Node ip = (Node) ipExpr.evaluate(doc, XPathConstants.NODE);
//		    	System.out.println(ip.getTextContent());
		    	clientId=ip.getTextContent();
			} catch (Exception e2) {
				e2.printStackTrace();
				log.error("",e2);
			}
		}
		
		String xmlByServiceId="";
		if("".equals(clientId)){
			StringBuilder sb=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<message>");
				sb.append("<action>put</action>");//
				sb.append("<ID>"+clientId+"</ID>");//if error
				sb.append("<type>01010001</type>");
				sb.append("<success>1</success>");
				sb.append("<exception>clientId not right.</exception>");
			sb.append("</message>");
			xmlByServiceId=sb.toString();
		}else{
			//拿出采集的xml配置
			xmlByServiceId = GatherManager.getInstance().getXmlByServiceId(clientId);
		}
		
		
		BcPackage pack = BcPackage.newBuilder().setBctype(bctype)
		.setBcmessage(xmlByServiceId)
		.build();
		
		return pack;
	}
	
	//下面大多是测试
	public static void main(String[] args) {
		
		//客户端请求XML
		String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<message>"+
				"<action>getData</action>"+
				"<ID>唯一标识</ID>"+
				"<type>01010001</type>"+
				"</message>"
				;
		
		//第一种方式
		MessasgeGather obj =(MessasgeGather) stream.fromXML(xml);
		System.out.println(obj.getID());
		
		//第二种方式
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true); // never forget this!
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse(new InputSource(new StringReader(xml)));
		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
	    	XPathExpression ipExpr = xpath.compile("/message/ID");
	    	Node ip = (Node) ipExpr.evaluate(doc, XPathConstants.NODE);
	    	System.out.println(ip.getTextContent());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("config error",e);
		}
		
	}
	
	private static XStream stream = new XStream();
	static{
		stream.alias("message", MessasgeGather.class);
	}
	
	public static class MessasgeGather{
		private String action;
		private String ID;
		private String type;
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getID() {
			return ID;
		}
		public void setID(String iD) {
			ID = iD;
		}
	}
}
