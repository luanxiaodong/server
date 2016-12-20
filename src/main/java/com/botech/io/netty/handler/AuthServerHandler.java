package com.botech.io.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.queue.BusinessQueue;
import com.botech.io.netty.queue.QueueTask;
import com.botech.io.netty.sender.MessageUtil;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 【登录验证】
* @ClassName: AuthServerHandler 
* @author luanxd
* @date 2015-7-28 下午1:41:27 
*
 */
public class AuthServerHandler extends ChannelInboundHandlerAdapter {
	private MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
	
	private static final Logger log = LoggerFactory.getLogger(AuthServerHandler.class);  
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BcPackageBuilder.BcPackage pack=(BcPackageBuilder.BcPackage)msg;
		
		//如果是:0 客户端验证请求
		if(null!=pack && "0".equals(pack.getBctype())){
			BusinessQueue.getInstance().getQueueLogin().put(new QueueTask(ctx, pack));
		}else if(null!=pack && "7".equals(pack.getBctype())){//7．	客户端退出
			ctx.close();
		}else{
			//如果登录就往下走。(未写判断条件)
			ctx.fireChannelRead(msg);
		}
		

		
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	//退出时处理
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		/*
		 AttributeKey<String> clientIdAttr = AttributeKey.valueOf("clientId");
	     String clientId = ctx.attr(clientIdAttr).get();
	     if(null==clientId){
	    	 service.removeByCtx(ctx);//清除集合时未加队列(未写)
	     }else 
	     if(null!=clientId){
	    	 service.removeByClientId(clientId);
	     }
	     */
		BusinessQueue.getInstance().getQueueLogOut().put(ctx);
		MessageUtil.getInstance().refreshAllClientList();//发送用户列表
	}
	
	//发生异常时处理
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
	
}
