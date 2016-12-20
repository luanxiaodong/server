package com.botech.io.netty.service;

import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;

public class MsgHandleServiceFactory {
	
	private MsgHandleServiceFactory(){}
	private static MsgHandleServiceFactory instance=new MsgHandleServiceFactory();
	public static MsgHandleServiceFactory getInstance(){
		return instance;
	}
	
	
	ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	/**
	 * 【实现解耦合】
	 * 将来改变里面的类就可以改变实现
	 * @return
	 */
	public MsgHandleService getService(){
		if("1".equals(config.getMessageservice())){
			return MsgHandleServiceImpl.getInstance();
		}
		return MsgHandleServiceImpl.getInstance();
	}
	
}
