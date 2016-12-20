package com.botech.io.netty.queue;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 【统一业务队列】
* @ClassName: BusinessQueue 
* @Description: TODO
* @author luanxd
* @date 2015-7-28 下午4:29:36 
*
 */
public class BusinessQueue {
	private BusinessQueue(){}
	public static BusinessQueue instance=new BusinessQueue();
	public static BusinessQueue getInstance(){
		return instance;
	}
	
	//登录队列
	private BlockingQueue<QueueTask> queueLogin=new LinkedBlockingQueue<QueueTask>(50000);
	
	//登出队列
	private BlockingQueue<ChannelHandlerContext> queueLogOut=new LinkedBlockingQueue<ChannelHandlerContext>(50000);
	
	//业务队列
	private BlockingQueue<QueueTask> queueBusiness=new LinkedBlockingQueue<QueueTask>(100000);
	
	public BlockingQueue<QueueTask> getQueueBusiness() {
		return queueBusiness;
	}

	public BlockingQueue<QueueTask> getQueueLogin() {
		return queueLogin;
	}

	public BlockingQueue<ChannelHandlerContext> getQueueLogOut() {
		return queueLogOut;
	}
	
}
