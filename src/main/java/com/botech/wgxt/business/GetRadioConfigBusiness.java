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
import com.botech.wgxt.ywzx.radio.manager.RadioManager;
import com.thoughtworks.xstream.XStream;

/**
 * 【 客户端向中心服务请求采集配置信息】--------协议号：01010016
* @ClassName: GetGatherConfigBusiness 
* @author luanxd
* @date 2015-8-18 下午2:44:59 
*
*客户端：bctype=01010016,bcmessage=clientId
*服务器：bctype=01010016,bcmessage=XML
 */
public class GetRadioConfigBusiness implements Business {
	private static final Logger log = LoggerFactory.getLogger(GetRadioConfigBusiness.class);
	
	@Override
	public BcPackage process(BcPackage msg, ChannelHandlerContext ctx) {
		String bctype = msg.getBctype();
		String xmlMessage = msg.getBcmessage();
		
		String clientId="";
		try {
			RootRadio obj =(RootRadio) stream.fromXML(xmlMessage);
			clientId=obj.getQueryCondition().getSzUser();
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
		    	XPathExpression ipExpr = xpath.compile("/root/QueryCondition/szUser");
		    	Node ip = (Node) ipExpr.evaluate(doc, XPathConstants.NODE);
		    	clientId=ip.getTextContent();
			} catch (Exception e2) {
				e2.printStackTrace();
				log.error("",e2);
			}
		}
		
		String xmlByServiceId="";
		if("".equals(clientId)){
			StringBuilder sb=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<root>");
				sb.append("<head>");//if error
				sb.append("<success>fail</success>");
				sb.append("<exception>clientId not right.</exception>");
				sb.append("</head>");
				sb.append("<body>");
				sb.append("</body>");
			sb.append("</root>");
			xmlByServiceId=sb.toString();
		}else{
			//拿出采集的xml配置
			xmlByServiceId = RadioManager.getInstance().getXmlByEquipId(clientId);
			
		}
		
		BcPackage pack = BcPackage.newBuilder().setBctype(bctype)
		.setBcmessage(xmlByServiceId)
		.build();
		
		return pack;
	}
	
	//下面大多是测试
	public static void main(String[] args) {
		
		//客户端请求XML
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
				"<root>"+
				"<QueryCondition>"+
				"<szUser>视频质量标识</szUser>"+
				"</QueryCondition>"+
				"</root>"
				;
		
		//第一种方式
		RootRadio obj =(RootRadio) stream.fromXML(xml);
		System.out.println(obj.getQueryCondition().getSzUser());
		
		
		//第二种方式
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true); // never forget this!
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse(new InputSource(new StringReader(xml)));
		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
	    	XPathExpression ipExpr = xpath.compile("/root/QueryCondition/szUser");
	    	Node ip = (Node) ipExpr.evaluate(doc, XPathConstants.NODE);
	    	System.out.println(ip.getTextContent());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("config error",e);
		}
	}
	
	private static XStream stream = new XStream();
	static{
		stream.alias("root", RootRadio.class);
		stream.alias("QueryCondition", QueryCondition.class);
	}
	
	public static class RootRadio{
		private QueryCondition QueryCondition;
		public QueryCondition getQueryCondition() {
			return QueryCondition;
		}
		public void setQueryCondition(QueryCondition queryCondition) {
			QueryCondition = queryCondition;
		}
	}
	public static class QueryCondition{
		private String szUser;
		public String getSzUser() {
			return szUser;
		}
		public void setSzUser(String szUser) {
			this.szUser = szUser;
		}
	}
}
