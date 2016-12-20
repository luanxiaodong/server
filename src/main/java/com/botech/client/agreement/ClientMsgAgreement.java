package com.botech.client.agreement;


import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;

public class ClientMsgAgreement {

	private ClientMsgAgreement(){}
	private static ClientMsgAgreement instance=new ClientMsgAgreement();
	public static ClientMsgAgreement getInstance(){
		return instance;
	}
	
	
	/**
	 * 【通过用户、密码登录】
	 * get login agreement Group 
	 * @param userName
	 * @param userPwd:【终端类型:
	 * 	nettyserverclient:netty服务器附属客户端类型的登录
	 * 	wgxtnettyclient:网关系统netty客户端类型的登录
	 * 】
	 * @return InformationPacket.Group
	 */
	public BcPackageBuilder.BcPackage doGetLoginInfoPacket(String userName,String userPwd){
		
		BcPackageBuilder.BcPackage msg = BcPackage.newBuilder()
				.setBctype("0")
				.setBcmessage(userName)
				.setClusteredid(userPwd)
				.build();
		return msg;
	}
	
	/**
	 * 【通过人名向所有人发送消息.】
	 * get group send info packet
	 * @param userName
	 * @param msgStr
	 * @return
	 */
	public BcPackageBuilder.BcPackage doGetGroupSendInfoPacket(String userName,String msgStr){
		
		BcPackageBuilder.BcPackage msg = BcPackage.newBuilder()
				.setBctype("5")
				.setClusteredid(userName)
				.setBcmessage(msgStr)
				.build();
		return msg;
		
	}
	
	
}
