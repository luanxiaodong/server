package com.botech.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;


public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
	
	//用户超时处理(测试的时候可能要把超时机制去掉)
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            //如果客户端长时间没有收到服务器来消息，则进行处理
            if (e.state() == IdleState.READER_IDLE 
            		) {
            	try {
            		ctx.writeAndFlush(BcPackage.newBuilder()
            				.setBctype("7")
            				.setClusteredid("server")
            				.build());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
            }
            ctx.close();
        }else{
        	ctx.fireUserEventTriggered(evt);
        }
	}

	private volatile ScheduledFuture<?> heartBeat;
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		BcPackageBuilder.BcPackage pack=(BcPackageBuilder.BcPackage)msg;
		
		if(null!=pack && "0".equals(pack.getBctype())){//如果登录成功，则启动心跳定时器。
			//60秒心跳一次
			ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 60000, TimeUnit.MILLISECONDS);//一分钟心跳一次
		}else if(null!=pack && "3".equals(pack.getBctype())){//销毁心跳应答
			ReferenceCountUtil.release(msg);
		}else{
			ctx.fireChannelRead(msg);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(null!=heartBeat){
			heartBeat.cancel(true);
			heartBeat=null;
		}
		ctx.fireExceptionCaught(cause);
	}
	
	private class HeartBeatTask implements Runnable{
		private ChannelHandlerContext ctx;
		public HeartBeatTask(ChannelHandlerContext ctx){
			this.ctx=ctx;
		}
		@Override
		public void run() {
//			System.out.println("heatbeat run!!!");
			//2.心跳请求
			BcPackage pack = BcPackageBuilder.BcPackage.newBuilder()
					.setBctype("2").build();
			ctx.writeAndFlush(pack);
		}
		
	}
}
