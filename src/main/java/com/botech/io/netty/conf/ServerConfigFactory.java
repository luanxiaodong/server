package com.botech.io.netty.conf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.botech.io.netty.business.Business;
import com.botech.io.netty.business.DefaultBusiness;


public class ServerConfigFactory {
	private static final Logger log = LoggerFactory.getLogger(ServerConfigFactory.class);
	private ServerConfigFactory(){
		try {
			config=new ServerConfig();
			
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true); // never forget this!
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    
		    /*
		    Document doc = null;
		    String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		    File file=new File(path);
		    File parentFile = null;
		    if(file.exists()){
		    	parentFile = file.getParentFile();
		    }
		    if(parentFile!=null && parentFile.exists() && parentFile.isDirectory()){
		    	String absolutePath = parentFile.getAbsolutePath();
		    	System.out.println(absolutePath);
		    	
		    	String firstServerConfig=absolutePath+File.separator+"conf"+File.separator+"server-config.xml";
		    	File serverFile=new File(firstServerConfig);
		    	if(serverFile.exists()){
		    		System.out.println(serverFile.getAbsolutePath());
		    		doc = builder.parse(new FileInputStream(new File(serverFile.getAbsolutePath())));
		    	}else{
		    		doc = builder.parse(ServerConfigFactory.class.getResourceAsStream("/server-config.xml"));
		    	}
		    }
		    System.out.println(path);
		    */
		    Document doc = builder.parse(ServerConfigFactory.class.getResourceAsStream("/server-config.xml"));
		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();

		    
//------------------------netty server config---------------------------------------------------begin		    
		    try {
		    	XPathExpression ipExpr = xpath.compile("/config/ip");
		    	Node ip = (Node) ipExpr.evaluate(doc, XPathConstants.NODE);
		    	//System.out.println(ip.getTextContent());
		    	config.setIp(ip.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
		    

		    try {
		    	XPathExpression portExpr = xpath.compile("/config/port");
		    	Node port = (Node) portExpr.evaluate(doc, XPathConstants.NODE);
		    	//System.out.println(port.getTextContent());
		    	config.setPort(Integer.valueOf(port.getTextContent()));
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}    

		    try {
		    	XPathExpression clusterExpr1 = xpath.compile("/config/clusteredid");
		    	Node clusteredid = (Node) clusterExpr1.evaluate(doc, XPathConstants.NODE);
		    	//System.out.println(clusteredid.getTextContent());
		    	config.setClusteredid(clusteredid.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}

		    try {
		    	XPathExpression openIpValidateExpr1 = xpath.compile("/config/openIpValidate");
		    	Node openIpValidateid = (Node) openIpValidateExpr1.evaluate(doc, XPathConstants.NODE);
		    	config.setOpenIpValidate(openIpValidateid.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}

		    try {
		    	XPathExpression messageserviceExpr = xpath.compile("/config/messageservice");
		    	Node messageservice = (Node) messageserviceExpr.evaluate(doc, XPathConstants.NODE);
		    	//System.out.println(messageservice.getTextContent());
		    	config.setMessageservice(messageservice.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
//------------------------netty server config---------------------------------------------------end		    
		    

		    try {//日志相关：是否开启日志
		    	XPathExpression enableLogserviceExpr = xpath.compile("/config/LogService/enable");
		    	Node enableLogervice = (Node) enableLogserviceExpr.evaluate(doc, XPathConstants.NODE);
		    	config.setEnablelogservice(enableLogervice.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
		    
		    
//-------------------------webservice---------------------------------------------------------------------------begin		
		    try {
		    	XPathExpression enableWebserviceExp = xpath.compile("/config/webservice/enable");
		    	Node enableWebserviceNode = (Node) enableWebserviceExp.evaluate(doc, XPathConstants.NODE);
		    	config.setEnableWebservice(enableWebserviceNode.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
		    
		    try {
		    	XPathExpression webserviceIPExp = xpath.compile("/config/webservice/ip");
		    	Node webserviceIPNode = (Node) webserviceIPExp.evaluate(doc, XPathConstants.NODE);
		    	config.setWebserviceIp(webserviceIPNode.getTextContent());
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	log.error("config error",e);
		    }
		    
		    try {
		    	XPathExpression webservicePortExp = xpath.compile("/config/webservice/port");
		    	Node webservicePortNode = (Node) webservicePortExp.evaluate(doc, XPathConstants.NODE);
		    	try {
		    		config.setWebservicePort(Integer.valueOf(webservicePortNode.getTextContent()));
				} catch (Exception e) {
					e.printStackTrace();
					log.error("config error",e);
					config.setWebservicePort(7499);
				}
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	log.error("config error",e);
		    }
		    
		    try {
		    	XPathExpression webserviceNameExp = xpath.compile("/config/webservice/name");
		    	Node webserviceNameNode = (Node) webserviceNameExp.evaluate(doc, XPathConstants.NODE);
		    	config.setWebserviceName( 
		    			(null==webserviceNameNode.getTextContent() || "".equals(webserviceNameNode.getTextContent()))
		    				? "webservice":webserviceNameNode.getTextContent()
		    			);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	log.error("config error",e);
		    }
		    
//-----------------------webservice--------------------------------------------------------------------------end		    

		    
		    
//-------------------------clusterclient------------------------------------------------------------------------------------begin		
		    try {
		    	//clusterclient相关
		    	XPathExpression clusterclientenable = xpath.compile("/config/clusterclient/enable");
		    	Node clusterclientenableNode = (Node) clusterclientenable.evaluate(doc, XPathConstants.NODE);
		    	config.setEnableClusterClient(clusterclientenableNode.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
		    

		    try {
		    	XPathExpression clusterclientserverip = xpath.compile("/config/clusterclient/serverip");
		    	Node clusterclientserveripNode = (Node) clusterclientserverip.evaluate(doc, XPathConstants.NODE);
		    	config.setServerip(clusterclientserveripNode.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
		    

		    try {
		    	XPathExpression clusterclientserverport = xpath.compile("/config/clusterclient/serverport");
		    	Node clusterclientserverportNode = (Node) clusterclientserverport.evaluate(doc, XPathConstants.NODE);
		    	try {
		    		config.setServerport(Integer.valueOf( clusterclientserverportNode.getTextContent() ));
		    	} catch (Exception e) {config.setServerport(7397);}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
		    

		    try {
		    	XPathExpression clusterclientserverinterval = xpath.compile("/config/clusterclient/interval");
		    	Node clusterclientintervalNode1 = (Node) clusterclientserverinterval.evaluate(doc, XPathConstants.NODE);
		    	try {
		    		config.setClientConnectInterval(Long.valueOf(clusterclientintervalNode1.getTextContent()));
		    	} catch (Exception e) {config.setClientConnectInterval(5000);}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
//-------------------------clusterclient------------------------------------------------------------------------------------end
		    
		    

//-------------------------heartbeat------------------------------------------------------------------------------------begin
		    try {
		    	//heartbeat相关
		    	XPathExpression heartBeatEnable = xpath.compile("/config/heartbeat/enable");
		    	Node heartBeatEnableNode = (Node) heartBeatEnable.evaluate(doc, XPathConstants.NODE);
		    	config.setEnableHeartBeat(heartBeatEnableNode.getTextContent());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}

		    try {
		    	XPathExpression heartBeatTime = xpath.compile("/config/heartbeat/interval");
		    	Node heartBeatTimeNode = (Node) heartBeatTime.evaluate(doc, XPathConstants.NODE);
		    	try {
		    		config.setHeartBeatTime(Long.valueOf(heartBeatTimeNode.getTextContent()));
		    	} catch (Exception e) {config.setHeartBeatTime(5000);}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("config error",e);
			}
//-------------------------heartbeat------------------------------------------------------------------------------------end
		    
		    

		    //业务相关
		    XPathExpression codesExpr = xpath.compile("/config/business/code");
		    Object codes = codesExpr.evaluate(doc, XPathConstants.NODESET);
		    NodeList nodes = (NodeList) codes;
		    Set<String> set=new HashSet<String>();
		    for (int i = 0;nodes!=null && i < nodes.getLength(); i++) {
		    	NamedNodeMap attr = nodes.item(i).getAttributes();
		        //System.out.println(attr.getNamedItem("id").getTextContent()); 
		        //System.out.println(attr.getNamedItem("class").getTextContent()); 
		    	String id = attr.getNamedItem("id").getTextContent();
		    	String className = attr.getNamedItem("class").getTextContent();
		    	set.add(id);
		    	
		    	Business busi=null;
		    	try {
		    		if(null!=className && !"".equals(className)){
		    			busi = (Business) Class.forName(className).newInstance();
		    		}else{
		    			busi=new DefaultBusiness();
		    		}
					businessMap.put(id, busi);
				} catch (Exception e) {
					//e.printStackTrace();
					log.error("businiess initil error:",e);
					busi=new DefaultBusiness();
					businessMap.put(id, busi);
					continue;
				}
		    }
		    config.setBusiness(set);
		    
		} catch (Exception e) {
			e.printStackTrace();
			log.error("config error",e);
		}
	}
	private static ServerConfigFactory instance=new ServerConfigFactory();
	public static ServerConfigFactory getInstance(){
		return instance;
	}
	
	private ServerConfig config;//配置
	private Map<String,Business> businessMap=new HashMap<String,Business>();
	
	public ServerConfig getConfig() {//配置
		return config;
	}

	public Map<String, Business> getBusinessMap() {//业务map
		return businessMap;
	}
	
}
