package com.botech.wgxt.business;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

import com.botech.common.utils.XstreamUtil;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.business.Business;
import com.botech.io.netty.page.OnlinePager;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;
import com.botech.page.bean.PageBean;
import com.thoughtworks.xstream.XStream;

/**
 * 【分页等相关】-------协议号：000000
* @ClassName: PageSearchCtrlBusiness 
* @author luanxd
* @date 2015-8-17 下午2:57:25 
*
 */
public class PageSearchCtrlBusiness implements Business {
	private static  XStream stream = XstreamUtil.getStream();
	@Override
	public BcPackage process(BcPackage msg, ChannelHandlerContext ctx) {
		MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
		//String cmd=msg.getClusteredid();//get cmd;
		String pageBeanXML = msg.getBcmessage();//拿到分页信息 pageBean XML
		
		PageBean<String> pb=(PageBean<String>)stream.fromXML(pageBeanXML);
		
		if("search".equals(pb.getCmd())){//页面查询
			pb = OnlinePager.getInstance().getPageBean(pb);
		}else if("searchone".equals(pb.getCmd())){//通过id，查询在不在线，在线就返回List数据
			ChannelHandlerContext oldctx = service.getCtxByClientId(pb.getId());
			if(null!=oldctx){
				List<String> list=new ArrayList<String>();
				list.add(pb.getId());
				pb.setList(list);//当查出来的ID放进list当中去
				pb.setCurrentPage(1);//当前页为1
				pb.setTotalSize(1);//总共一条
			}
		}else if("searchonline".equals(pb.getCmd())){//给一些终端，查询是否在线
			List<String> statusList=new ArrayList<String>();
			List<String> list = pb.getList();
			for(String clientId:list){
				ChannelHandlerContext myctx = service.getCtxByClientId(clientId);
				if(null!=myctx){
					statusList.add(clientId+":"+"1");
				}else{
					statusList.add(clientId+":"+"0");
				}
			}
			pb.setList(statusList);
		}
		
		//return xml
		BcPackage pack = BcPackage.newBuilder().setBctype("000000")
			.setBcmessage(stream.toXML(pb))
			.build();
		return pack;
	}

}
