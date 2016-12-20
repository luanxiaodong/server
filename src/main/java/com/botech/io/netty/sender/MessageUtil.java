package com.botech.io.netty.sender;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

/**
 * 【消息发送器】
* @ClassName: MessageUtil 
* @author luanxd
* @date 2015-7-30 上午8:49:44 
*
 */
public class MessageUtil {
	private MessageUtil(){}
	private static MessageUtil instance=new MessageUtil();
	public static MessageUtil getInstance(){
		return instance;
	}
	
	private static final Logger log = LoggerFactory.getLogger(MessageUtil.class);
	
	private MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
	
	/**
	 * 【向指定的客户端发送包】
	* @Title: send 
	* @param @param pack
	* @param @param clientIds    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void send(BcPackage msg,List<String> clientIds){
		if(null==msg || null==clientIds){
			return ;
		}
		for(String clientId:clientIds){
			ChannelHandlerContext ctx = service.getCtxByClientId(clientId);
			if(null!=ctx){
				ctx.writeAndFlush(msg);
			}
		}
	}
	//同上
	public void send(BcPackage msg,String[] clientIds){
		if(null==msg || null==clientIds){
			return ;
		}
		for(String clientId:clientIds){
			ChannelHandlerContext ctx = service.getCtxByClientId(clientId);
			if(null!=ctx){
				ctx.writeAndFlush(msg);
			}
		}
	}
	
	
	/**
	 * 【向所有在线的客户端发送消息】
	* @Title: sendAll 
	* @param @param msg    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void sendAll(BcPackage msg){
		ChannelGroup channelGroup = service.getChannelGroup();
		channelGroup.writeAndFlush(msg);
	}
	
	/**
	 * 【刷新所有客户端列表】
	* @Title: refreshAllClientList 
	* @param  
	* @return void    返回类型 
	* @throws
	 */
	public void refreshAllClientList(){
		/*
		//发送用户列表
		Map<String, String> channelIdClientIdMap = service.getChannelIdClientIdMap();
		StringBuilder sb=new StringBuilder();
		for(Map.Entry<String , String> entry:channelIdClientIdMap.entrySet()){
			sb.append(entry.getValue()+",");
		}
		service.getChannelGroup().writeAndFlush(BcPackage.newBuilder().setBctype("6").setBcmessage(sb.toString()).build());
		*/
	}
	
	/**
	 * 【通过客户端ID踢掉终端用户】
	* @Title: kickOut 
	* @param @param clientId 
	* @return void    返回类型 
	* @throws
	 */
	public void kickOutByClientId(String clientId){
			ChannelHandlerContext ctx=service.getCtxByClientId(clientId);
			if(ctx!=null){
				BcPackage bcPackage = BcPackage.newBuilder()
				.setBctype("7")
				.setClusteredid("server")
				.build();
				ctx.writeAndFlush(bcPackage);
				
				//日志--登录--重复登录
				log.warn("重复登录大于5次通知客户端正常退出clientId="+clientId+",type="+bcPackage.getBctype());
				
				service.removeByCtx(ctx);
			}
	}
	
	
	/**
	 * 【踢出所有用户】
	* @Title: kickOutAll 
	* @param  
	* @return void    返回类型 
	* @throws
	 */
	public void kickOutAll(){
		ChannelGroup channelGroup = service.getChannelGroup();
		channelGroup.writeAndFlush(BcPackage.newBuilder()
						.setBctype("7")
						.setClusteredid("server")
						.build());
		Map<String, ChannelHandlerContext> ctxMap = service.getCtxMap();
		for(Map.Entry<String, ChannelHandlerContext> entry:ctxMap.entrySet()){
			ChannelHandlerContext ctx = entry.getValue();
			ctx.close();
		}
	}
}
