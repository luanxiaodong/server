package com.botech.io.netty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;
import java.util.Set;

/**
 *【(存储主管道与客户端集合管道)】 
 * @author luanxd
 */
public interface MsgHandleService{
	/**
	 * 【全局事件的主管道】
	 * 主管道里面包含多个具体管道，可以往主管道添加子管道，也可以子管道。
	 * 全局事件监听机制
	 * @author luanxd
	 * @return
	 */
	ChannelGroup getChannelGroup();
	
	/**
	 * 【通过客户端ID添加ctx集合】
	* @Title: putByClientIdAndCtx 
	* @param @param clientId
	* @param @param ctx 
	* @return void    返回类型 
	* @throws
	 */
	void putByClientIdAndCtx(String clientId,ChannelHandlerContext ctx);
	
	/**
	 * 【通过clientId拿到ctx】
	* @Title: getCtxByClientId 
	* @param @param clientId
	* @param @return 
	* @return ChannelHandlerContext    返回类型 
	* @throws
	 */
	ChannelHandlerContext getCtxByClientId(String clientId);
	
	/**
	 * 【拿到整个ctxMap集合】
	* @Title: getCtxMap 
	* @param @return 
	* @return Map<String,ChannelHandlerContext>    返回类型 
	* @throws
	 */
	Map<String,ChannelHandlerContext> getCtxMap();
	
	/**
	 * 【通过客户端ID删除所有集合】
	* @Title: removeByClientId 
	* @param @param clientId 
	* @return void    返回类型 
	* @throws
	 */
	void removeByClientId(String clientId);
	
	/**
	 * 【通过ctx删除所有集合】
	* @Title: removeByCtx 
	* @param @param ctx 
	* @return void    返回类型 
	* @throws
	 */
	@Deprecated
	void removeByCtx(ChannelHandlerContext ctx);
	
	
	

	/**
	 * 【网管系统netty客户端类型:wgxtnettyclient   ，netty服务器客户端类型:nettyserverclient:netty   总集合】
	* @Title: getTypeMapSet 
	* @param @return 
	* @return Map<String,Set<String>>    返回类型 
	* @throws
	 */
	Map<String, Set<String>> getTypeMapSet();
}
