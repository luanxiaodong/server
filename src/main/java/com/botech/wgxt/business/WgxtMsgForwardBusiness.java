package com.botech.wgxt.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.business.Business;
import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;
import com.botech.io.netty.server.NettyServer;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;
import com.botech.wgxt.ywzx.redis.manager.RedisManager;

/**
 * 【中心网管系统将信息包转发的功能】-------协议号：000001
* @ClassName: WgxtMsgForwardBusiness 
* @author luanxd
* @date 2015-8-17 下午2:57:25 
*
 */
public class WgxtMsgForwardBusiness implements Business {
	private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
	//private XStream stream = XstreamUtil.getStream();
	@Override
	public BcPackage process(BcPackage msg, ChannelHandlerContext ctx) {
		MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
		ServerConfig config = ServerConfigFactory.getInstance().getConfig();
		String businessId=msg.getClusteredid();//业务ID or 业务ID：clientId1,clientId2,clientId3
		String xmlData = msg.getBcmessage();//
		
		if(businessId!=null && businessId.contains("01020003")){//运维中心配置的redis模值发生变更时，中心服务向所有客户端推送最新的模值。
			RedisManager.getInstance().reloadRedisStr();
		}
		
		if(config.getBusiness().contains(businessId)){
			//return xml
			BcPackage pack = BcPackage.newBuilder().setBctype(businessId)
				.setBcmessage(xmlData)
				.build();
			service.getChannelGroup().writeAndFlush(pack);
		}else if(businessId!=null && businessId.contains(":")){// businessId:clientId1,clientId2,clientId3
			String[] businessIdandClients = businessId.split("\\:");
			if(2==businessIdandClients.length 
					&& businessIdandClients[1]!=null 
					&& config.getBusiness().contains(businessIdandClients[0])){
				
				BcPackage pack = BcPackage.newBuilder().setBctype(businessIdandClients[0])
					.setBcmessage(xmlData)
					.build();
				
				try {
					String[] clientIds = businessIdandClients[1].split(",");
					if(null!=clientIds)
					for(String clientId:clientIds){
						try {
							ChannelHandlerContext oldctx = service.getCtxByClientId(clientId);
							if(null!=oldctx){
								oldctx.writeAndFlush(pack);
							}
						} catch (Exception e) {
							log.error("",e);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("",e);
				}
			}
		}
		
		//return xml
		BcPackage pack = BcPackage.newBuilder().setBctype("000001")
			.setClusteredid(businessId) //推送的ID
			.setBcmessage("ok")  //means: already send
			.build();
		return pack;
	}

}
