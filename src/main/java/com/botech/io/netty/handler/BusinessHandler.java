package com.botech.io.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;
import com.botech.io.netty.queue.BusinessQueue;
import com.botech.io.netty.queue.QueueTask;

public class BusinessHandler extends ChannelInboundHandlerAdapter {
	private ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BcPackageBuilder.BcPackage pack=(BcPackageBuilder.BcPackage)msg;
		if(null!=pack && 
				config!=null && 
				config.getBusiness()!=null && 
				config.getBusiness().contains(pack.getBctype())){//如果所有的业务编号  true将来可以改写成一个业务ID列表是否包含这个业务类型的意思。
			BusinessQueue.getInstance().getQueueBusiness().put(new QueueTask(ctx, (BcPackage)msg));
		}else{
			ctx.fireChannelRead(msg);
		}
	}

}
