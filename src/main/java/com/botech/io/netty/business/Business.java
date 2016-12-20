package com.botech.io.netty.business;

import io.netty.channel.ChannelHandlerContext;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;

/**
 * 【业务接口】
* @ClassName: Business 
* @Description: TODO
* @author luanxd
* @date 2015-7-29 上午11:25:13 
*
 */
public interface Business {
	/**
	 * 【业务主体方法】
	* @Title: business 
	* @param @param msg【从客户端发过来的请求业务包】
	* @param @return
	* @return BcPackage 返回类型 【返回给客户端的请求业务包】
	* @throws
	 */
	public BcPackage process(BcPackage msg,ChannelHandlerContext ctx);
}
