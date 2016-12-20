package com.botech.io.netty.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 *【心跳机制】
* @ClassName: HeartBeatReqHandler 
* @author luanxd
* @date 2015-7-28 下午2:49:39 
*
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(HeartBeatReqHandler.class);
	//用户超时处理(测试的时候可能要把超时机制去掉)
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            //System.out.println(e.state());
            //如果服务器长时间没有收到客户发来消息，则进行处理
            if (e.state() == IdleState.READER_IDLE 
            		|| e.state() == IdleState.WRITER_IDLE || e.state() == IdleState.ALL_IDLE
            		) {
            	try {
            		ctx.writeAndFlush(BcPackage.newBuilder()
            				.setBctype("7")
            				.setClusteredid(config.getClusteredid())
            				.setBcmessage("Your heartbeat timeout.")
            				.build());
            		log.warn("心跳超时关闭对象是:"+ctx);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
            	ctx.close();
				//service.removeByCtx(ctx);
            }
        }else{
        	ctx.fireUserEventTriggered(evt);
        }
	}

	private volatile ScheduledFuture<?> heartBeat;
	private ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		BcPackageBuilder.BcPackage pack=(BcPackageBuilder.BcPackage)msg;
		
		if("true".equals(config.getEnableHeartBeat()) && //配置文件里面启用心跳机制
				null!=pack && "0".equals(pack.getBctype())){//如果登录成功，则启动心跳定时器。
			//8秒心跳一次
			//ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
			ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, config.getHeartBeatTime(), TimeUnit.MILLISECONDS);
		}else 
			
		if(null!=pack && "2".equals(pack.getBctype())){//2．	心跳请求
			BcPackage packResp = BcPackageBuilder.BcPackage.newBuilder()
					.setBctype("3").build();//3．	心跳应答
			ctx.writeAndFlush(packResp);
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
			//2.心跳请求
			BcPackage pack = BcPackageBuilder.BcPackage.newBuilder()
					.setBctype("2").build();
			ctx.writeAndFlush(pack);
		}
		
	}
}
