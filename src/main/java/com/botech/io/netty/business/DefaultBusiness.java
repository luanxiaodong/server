package com.botech.io.netty.business;

import io.netty.channel.ChannelHandlerContext;

import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;

/**
 * 【默认业务】
* @ClassName: DefaultBusiness 
* @author luanxd
* @date 2015-7-29 上午11:51:20 
*
 */
public class DefaultBusiness implements Business {

	@Override
	public BcPackage process(BcPackage msg,ChannelHandlerContext ctx) {
		//1.从服务器发过的 BcPackage
		String bctype=msg.getBctype();//业务类型,例：01010001
		//int bcpriority = msg.getBcpriority();//消息级别(一般用不上)
		//String clusterid=msg.getClusteredid();//当前netty的集群ID(一般用不上)
		//String bcmessage = msg.getBcmessage();//业务请求,例：..<message>...<type>查询类型</type>...</message>
		
		//get xml from db.
		//set bcmessage = xml;
		
//		System.out.println(bcmessage);
		
		BcPackage msgRet = BcPackage.newBuilder()
		.setBctype(bctype)//业务类型原样返回
//		.setBcmessage(bcmessage)
		.setBcmessage("server return default result.")//业务内容，例：..<message>...<type>...</type>..<Forcollect>...</Forcollect>..</message>
		.build();
		return msgRet;
	}

}
