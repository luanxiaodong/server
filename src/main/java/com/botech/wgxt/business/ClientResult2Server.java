package com.botech.wgxt.business;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.business.Business;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;

import io.netty.channel.ChannelHandlerContext;

/**
 * 【C++客户端执行的结果返回到   网管系统的附属客户端  】
* @ClassName: ClientResult2Server 
* @author luanxd
* @date 2015-8-28 上午9:28:45 
*
 */
public class ClientResult2Server  implements Business {
	private static final Logger log = LoggerFactory.getLogger(ClientResult2Server.class);
	@Override
	public BcPackage process(BcPackage msg, ChannelHandlerContext ctx) {
		MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
		String clientId = msg.getClusteredid();//终端ID
		String clientXmlResult = msg.getBcmessage();//xml执行结果
		
		//000001
		//返回给网管系统的包
		BcPackage pack = BcPackage.newBuilder()
			.setBctype("000001")
			.setClusteredid(clientId)
			.setBcmessage(clientXmlResult)
			.build();
		
		//网管客户端告诉netty该给哪些客户端发送变更业务
		log.warn("网管客户端通知netty业务转发type="+pack.getBctype()+",clusterid="+pack.getClusteredid()+",message="+pack.getBcmessage());
		
		int sendCount=0;
		
		Map<String, Set<String>> typeMapSet = service.getTypeMapSet();
		Set<String> set = typeMapSet.get("wgxtnettyclient");
		if(null!=set && set.size()>0){
			for(String str:set){
				try {
					ChannelHandlerContext wgxtClientCtx = service.getCtxByClientId(str);
					if(wgxtClientCtx!=null){
						wgxtClientCtx.writeAndFlush(pack);
						log.warn("netty业务转发成功clientId="+str);
						sendCount++;
					}else{
						log.warn("netty业务转发不在线clientId="+str);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("",e);
					continue;
				}
			}
		}
		
		if(sendCount==0){
			//想要转发的客户端都不存在.
			log.warn("netty业务转发时,发现所有客户端都不在线,向所有在线用户发送!");
			service.getChannelGroup().writeAndFlush(pack);
		}
		
		return null;//必须是返回空，因为返回空的不做转发.
	}

}
