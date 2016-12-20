package com.botech.client.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.client.handler.InitChildChannelHandler;
import com.botech.client.service.ClientMsgHandleService;
import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;

/**
 * 【服务器的附属客户端--具有重连接的机制】
* @ClassName: NettyClient 
* @author luanxd
* @date 2015-8-6 下午2:10:55 
*
 */
public class NettyClient {
	private static final Logger log = LoggerFactory.getLogger(NettyClient.class); 
	
	private static ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	
	private NettyClient(){}
	private static NettyClient instance=new NettyClient();
	public static NettyClient getInstance(){
		return instance;
	}
	
	
	public void connect(final String inetHost,final int inetPort) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			System.out.println("开始连接中....");
			
			Bootstrap b = new Bootstrap();
			
			b.group(group).channel(NioSocketChannel.class);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.handler(new InitChildChannelHandler());
			
			//发起异步连接操作
			ChannelFuture f = b.connect(inetHost, inetPort);
			
			//等待客户端链路关闭
			f.channel().closeFuture().sync();
			
		}finally{
			group.shutdownGracefully();
		}
	}
	
	public void reConnect() {
		System.out.println("重连接准备中...");
		try {
			//TimeUnit.SECONDS.sleep(2);//睡2秒
			Thread.sleep(config.getClientConnectInterval());
			try {
				connect(config.getServerip(), config.getServerport());//发起重连接操作
			} catch (Exception e) {
				e.printStackTrace();
				log.error("",e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
		}
	}
	
	
	public static boolean isOpen(){
		Socket socket=null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(config.getIp(), config.getPort()), 5000);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		} finally {
			    if (socket != null){
			    	try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					} 
			    }
			} 
	}
	
	private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	
	public static void main(String[] args) {
		
		while(true){
			if(isOpen()){
				System.out.println("端口开啦!!!!");
				if(null==ClientMsgHandleService.channel){
					System.out.println("reConnect");
					startPool();
				} if(null!=ClientMsgHandleService.channel && !ClientMsgHandleService.channel.channel().isActive()){
					System.out.println("startPool");
					NettyClient.getInstance().reConnect();
				}
			}else{
				System.out.println("端口没开什么都不干....");
			}
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void startPool(){
		
		pool.submit(new Runnable() {
			
			@Override
			public void run() {
				//one 
				NettyClient client = NettyClient.getInstance();
				try {
					client.connect(config.getIp(), config.getPort());
				} catch (Exception e) {
					e.printStackTrace();
					log.error("",e);
				}
			}
		});
		
	}
	
	
	
}
