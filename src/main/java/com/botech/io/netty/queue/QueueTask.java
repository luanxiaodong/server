package com.botech.io.netty.queue;

import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;

import io.netty.channel.ChannelHandlerContext;
/**
 * 
* @ClassName: QueueTask 
* @Description: TODO(队列的一个任务) 
* @author luanxd
* @date 2015-7-28 下午4:29:17 
*
 */
public class QueueTask {
	private ChannelHandlerContext ctx;
	private BcPackage msg;
	
	public QueueTask(ChannelHandlerContext ctx, BcPackage msg) {
		this.ctx = ctx;
		this.msg = msg;
	}
	
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	public BcPackage getMsg() {
		return msg;
	}
	public void setMsg(BcPackage msg) {
		this.msg = msg;
	}
}
