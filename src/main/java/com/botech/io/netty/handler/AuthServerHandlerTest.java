package com.botech.io.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashSet;
import java.util.Set;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;

/**
 * 【测试专用】
* @ClassName: AuthServerHandler 
* @Description: TODO(登录验证) 
* @author luanxd
* @date 2015-7-28 下午1:41:27 
*
 */
public class AuthServerHandlerTest extends ChannelInboundHandlerAdapter {
	private MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
	
	private static Set<ChannelHandlerContext> ctxSet=new HashSet<ChannelHandlerContext>();
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		service.getChannelGroup().add(ctx.channel());
		ctxSet.add(ctx);
		System.out.println("服务器的客户端数:"+ctxSet.size());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BcPackageBuilder.BcPackage pack=(BcPackageBuilder.BcPackage)msg;
		
		//如果是:0 客户端验证请求
		if(null!=pack && "0".equals(pack.getBctype())){
			//BusinessQueue.getInstance().getQueueLogin().put(new QueueTask(ctx, pack));
			ctx.writeAndFlush(BcPackage.newBuilder().setBctype("1").build());
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
		service.getChannelGroup().remove(ctx.channel());
		ctxSet.remove(ctx);
	}
	
	//发生异常时处理
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
	
}
