package com.botech.client.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

import com.botech.client.client.NettyClient;
import com.botech.client.service.ClientMsgHandleService;
import com.botech.io.netty.bean.BcPackageBuilder;


public class TimeClientHandler extends ChannelInboundHandlerAdapter{

	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ClientMsgHandleService.channel = ctx;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BcPackageBuilder.BcPackage pack = (BcPackageBuilder.BcPackage) msg;
		ClientMsgHandleService.doMsgForShunt(pack);
		ctx.fireChannelRead(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
		if(null!=ClientMsgHandleService.channel && !ClientMsgHandleService.channel.channel().isActive()){
			NettyClient.getInstance().reConnect();
		}
		
	}
	
	
	
}
