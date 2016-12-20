package com.botech.io.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.sender.MessageUtil;

/**
 * 【消息发送】
* @ClassName: MessageHandler 
* @author luanxd
* @date 2015-7-30 下午1:29:35 
*
 */
public class MessageHandler extends ChannelInboundHandlerAdapter {
	//private MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		BcPackageBuilder.BcPackage pack=(BcPackageBuilder.BcPackage)msg;
		if(null!=pack && "5".equals(pack.getBctype())){//
			
			//发消息
			MessageUtil.getInstance().sendAll(pack);

		}else{
			ctx.fireChannelRead(msg);
			//ReferenceCountUtil.release(msg);
		}
	}
}
