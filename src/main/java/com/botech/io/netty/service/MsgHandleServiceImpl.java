package com.botech.io.netty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;

/**
* @ClassName: MsgHandleServiceImpl 
* @Description: TODO(存储主管道与客户端集合管道) 
* @author luanxd
* @date 2015-7-28 下午1:33:23 
*
 */
public class MsgHandleServiceImpl implements MsgHandleService{
	private static final Logger log = LoggerFactory.getLogger(MsgHandleServiceImpl.class);
	private MsgHandleServiceImpl(){}
	private static MsgHandleService instance=new MsgHandleServiceImpl();
	public static MsgHandleService getInstance(){
		return instance;
	}
	
	//1. 主管道;
	private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	//2. 管道Map集合: key=channelId,	value=ChannelHandlerContext;
	private Map<String, ChannelHandlerContext> clientIdCtxMap = new ConcurrentHashMap<String, ChannelHandlerContext>(50000);
	
	//网管系统netty客户端类型:wgxtnettyclient   ，netty服务器客户端类型:nettyserverclient:netty   总集合。
	private Map<String,Set<String>> typeMapSet=new ConcurrentHashMap<String,Set<String>>();
	
	private ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	
	@Override
	public Map<String, Set<String>> getTypeMapSet() {
		return typeMapSet;
	}
	@Override
	public ChannelGroup getChannelGroup() {
		return channelGroup;
	}
	@Override
	public void removeByClientId(String clientId) {
		ChannelHandlerContext ctx = clientIdCtxMap.get(clientId);
		try {
			
			//1.
			channelGroup.remove(ctx.channel());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("channelGroup.remove(ctx.channel()); Exception:",e);
		}
		
		//2.
		clientIdCtxMap.remove(clientId);
		
		//last
		ctx.close();
	}
	@Override
	public void removeByCtx(ChannelHandlerContext ctx) {
		//1.删除默认组中的通道
		channelGroup.remove(ctx.channel());
		
		//2.找出通道并且删除。---------------（####  效率不行	  ####）
		for(Map.Entry<String , ChannelHandlerContext> entry:clientIdCtxMap.entrySet()){
			String key = entry.getKey();
			ChannelHandlerContext value = entry.getValue();
			if(value.equals(ctx)){
				clientIdCtxMap.remove(key);
				
				//日志--登录--登出
				log.warn("登出的客户端是clientId="+key+",服务器现有:"+getCtxMap().size()+"客户端,退出的对象是:"+value);
				
				if("true".equals(config.getEnablelogservice())){
					String loginfo="退出客户端ID是:"+key+",服务器现有:"+getCtxMap().size()+"客户端,退出的对象是:"+value;
					log.error(loginfo);
					System.out.println(loginfo);
				}
			}
		}
		
		//last
		ctx.close();
	}
	@Override
	public void putByClientIdAndCtx(String clientId, ChannelHandlerContext ctx) {
		//1.
		channelGroup.add(ctx.channel());
		
		//2.
		clientIdCtxMap.put(clientId, ctx);
		
	}
	@Override
	public ChannelHandlerContext getCtxByClientId(String clientId) {
		return clientIdCtxMap.get(clientId);
	}
	
	@Override
	public Map<String, ChannelHandlerContext> getCtxMap() {
		return clientIdCtxMap;
	}
	


}
