package com.botech.wgxt.business;

import io.netty.channel.ChannelHandlerContext;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.business.Business;
import com.botech.wgxt.ywzx.redis.manager.RedisManager;

/**
 * 【客户端(调度单元、采集服务、第三方采集服务)请求redis模值和redis集群IP地址】------协议号:01010020
* @ClassName: RedisInfoBusiness 
* @author luanxd
* @date 2015-8-18 下午3:27:05 
 */
public class RedisInfoBusiness implements Business {
	
	@Override
	public BcPackage process(BcPackage msg, ChannelHandlerContext ctx) {
		String type = msg.getBctype();
		String cacheRedisInfo = RedisManager.getInstance().getCacheRedisInfo();//返回的redis模值是字符串    	 20:10.49.130.1,10.49.130.2,10.49.130.3,10.49.130.4
		
		BcPackage pack = BcPackage.newBuilder().setBctype(type)
			.setBcmessage(cacheRedisInfo)
			.build();
		
		return pack;
	}

}
