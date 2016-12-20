package com.botech.io.netty.webservice;

import javax.xml.ws.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;

public class WebServicePublish {
	private static final Logger log = LoggerFactory.getLogger(WebServicePublish.class); 
	/** 【部署webservice的主类】----注意，使用的是jdk自己的webservice,jdk最好使用1.7及以上
	 * @Title: main 
	 * @param @param args 
	 * @return void    返回类型 
	 * @throws 
	 */
	public static void main(String[] args) {
		try {
			ServerConfig config = ServerConfigFactory.getInstance().getConfig();
			//http://127.0.0.1:7499/webservice
			String address = "http://"+config.getWebserviceIp()+":"+config.getWebservicePort()+"/"+config.getWebserviceName();
			//使用Endpoint类提供的publish方法发布WebService，发布时要保证使用的端口号没有被其他应用程序占用
			Endpoint.publish(address , new WebServiceImpl());
			System.out.println("web service start success!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("webservice error:",e);
		}
	}

}
