package com.botech.client.service;


import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.ChannelHandlerContext;

import com.botech.client.agreement.ClientMsgAgreement;
import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;

public class ClientMsgHandleService {
	
	public static ChannelHandlerContext channel;
	
	private static ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	private static ClientMsgAgreement msgSend = ClientMsgAgreement.getInstance();
	private static AtomicLong atomicLong = new AtomicLong();
	
	public static void doMsgForShunt(BcPackageBuilder.BcPackage msg){
		if("0".equals(msg.getBctype())){
			System.out.println();
			channel.writeAndFlush(msgSend.doGetLoginInfoPacket(config.getClusteredid(), "nettyserverclient"));
		}else if("1".equals(msg.getBctype())){
			System.out.println("connect"+atomicLong.incrementAndGet()+"sucess!");
		}
	}
	
	//发消息的
	public static void send(BcPackageBuilder.BcPackage msg){
		channel.writeAndFlush(msg);
	}
	
}
