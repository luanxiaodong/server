package com.botech.io.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.client.client.NettyClient;
import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;
import com.botech.io.netty.handler.InitializerChannelImpl;
import com.botech.io.netty.queue.BusinessManager;
import com.botech.io.netty.queue.BusinessQueue;
import com.botech.io.netty.webservice.WebServicePublish;

/**
 * @author luanxd
 */
public class NettyServer {
	private static final Logger log = LoggerFactory.getLogger(NettyServer.class);  
	
	private NettyServer(){}
	private static NettyServer instance=new NettyServer();
	public static NettyServer getInstance(){
		return instance;
	}
	
	private ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	
	public void bind(int port) throws Exception{
		
		EventLoopGroup workerGroup=new NioEventLoopGroup();// (1)
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		try {
			ServerBootstrap b=new ServerBootstrap();// (2)
			b.group(workerGroup, bossGroup)
			.channel(NioServerSocketChannel.class)// (3)
			.childHandler(new InitializerChannelImpl())
			.option(ChannelOption.SO_BACKLOG, 1024);    // (5)
			//.childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
            // Bind and start to accept incoming connections.
            //ChannelFuture f = b.bind(port).sync(); // (7)
			ChannelFuture f = b.bind(new InetSocketAddress(  
	                config.getIp(), port)).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
	}
	
	//在web中InitialServlet中启动
	public void start() {
		//int port = 7397;
		NettyServer ns = NettyServer.getInstance();
		try {
			ns.bind(config.getPort());
		} catch (Exception e) {
			log.error("Server start Exception:",e);
			e.printStackTrace();
		}
	}
	
	
	private static ExecutorService pool = Executors.newFixedThreadPool(9);
	
	
	
	//测试专用
	public static void main(String[] args) {
		ServerConfig config = ServerConfigFactory.getInstance().getConfig();
		
		//jdk webservice
		if("true".equals(config.getEnableWebservice())){
			pool.execute(new Runnable() {
				@Override
				public void run() {
					WebServicePublish.main(new String[]{});
				}
			});
		}
		
		if("true".equals(config.getEnableClusterClient())){//----netty集群附属客户端
			//启动netty服务器附属客户端
			pool.execute(new Runnable() {
				@Override
				public void run() {
					NettyClient.main(new String[]{});
				}
			});
		}
		
		//启动netty服务器
		pool.execute(new Runnable() {
			@Override
			public void run() {
				NettyServer.getInstance().start();
			}
		});
		
		//处理  登录  队列---1线程
		pool.execute(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						BusinessManager.getInstance().doLogin(
								BusinessQueue.getInstance().getQueueLogin().take()
								);
					} catch (InterruptedException e) {
						e.printStackTrace();
						log.error("",e);
					}
				}
			}
		});
		
		//处理登出队列---1线程
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					try {
						BusinessManager.getInstance().doLogOut(
								BusinessQueue.getInstance().getQueueLogOut().take()
								);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("",e);
					}
				}
			}
		});
		
		//处理   业务队列	里面的请求数据。-----4线程
		for(int i=0;i<4;i++){
			pool.execute(new Runnable() {
				@Override
				public void run() {
					
					while(true){
						try {
							BusinessManager.getInstance().doBusiness(
									BusinessQueue.getInstance().getQueueBusiness().take()
									);
						} catch (InterruptedException e) {
							e.printStackTrace();
							log.error("",e);
						}
					}
					
				}
			});
		}
		
		/*
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
				System.out.println(service.getCtxMap().size());
			}
		}, 3000,3000);
		*/
	}
}
