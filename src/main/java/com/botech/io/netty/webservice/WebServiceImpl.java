package com.botech.io.netty.webservice;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;

import com.botech.common.utils.XstreamUtil;
import com.botech.io.netty.page.OnlinePager;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;
import com.botech.page.bean.PageBean;
import com.thoughtworks.xstream.XStream;

@javax.jws.WebService
public class WebServiceImpl implements WebService {
	private static  XStream stream = XstreamUtil.getStream();
	
	@WebMethod
	@Override
	public String process(String pageBeanXML) {
		MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
		
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
		
		return stream.toXML(pb);
	}

}
