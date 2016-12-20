package com.botech.io.netty.page;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.botech.io.netty.service.MsgHandleServiceFactory;
import com.botech.page.bean.PageBean;

public class OnlinePager {
	private OnlinePager(){}
	private static OnlinePager instance=new OnlinePager();
	public static OnlinePager getInstance(){
		return instance;
	}
	//页面分页（web测试环境）
	public PageBean<Entry<String, ChannelHandlerContext>> getUserList(PageBean<Entry<String, ChannelHandlerContext>> pb){
		List<Entry<String, ChannelHandlerContext>> list=new ArrayList<Entry<String, ChannelHandlerContext>>();
		Map<String,ChannelHandlerContext> userListMap = MsgHandleServiceFactory.getInstance().getService().getCtxMap();
		
		List<Entry<String, ChannelHandlerContext>> userList=new ArrayList<Entry<String, ChannelHandlerContext>>(userListMap.entrySet());
	
		if(pb.getCurrentPage()<1){
			pb.setCurrentPage(1);
		}
		
		pb.setTotalSize(userListMap.size());
		if(pb.getCurrentPage()>pb.getTotalPage()){
			pb.setCurrentPage(pb.getTotalPage());
		}

		if(userList.size()==0)
			return pb;
		for (int i=(pb.getCurrentPage()-1)*pb.getPageSize();
				userList!=null && i< (((pb.getCurrentPage()-1)*pb.getPageSize()+pb.getPageSize())>userList.size()?userList.size():((pb.getCurrentPage()-1)*pb.getPageSize()+pb.getPageSize())) ;
				i++) {
			list.add(userList.get(i));
		}
		pb.setList(list);

		
		return pb;
	}
	
	/**
	 * 【分页】
	* @Title: getPageBean 
	* @param @param pb
	* @param @return 
	* @return PageBean<String>    返回类型 
	* @throws
	 */
	public PageBean<String> getPageBean(PageBean<String> pb){
		List<String> list=new ArrayList<String>();
		Map<String,ChannelHandlerContext> userListMap = MsgHandleServiceFactory.getInstance().getService().getCtxMap();
		
		List<Entry<String, ChannelHandlerContext>> userList=new ArrayList<Entry<String, ChannelHandlerContext>>(userListMap.entrySet());
		
		if(pb.getCurrentPage()<1){
			pb.setCurrentPage(1);
		}
		
		pb.setTotalSize(userListMap.size());
		if(pb.getCurrentPage()>pb.getTotalPage()){
			pb.setCurrentPage(pb.getTotalPage());
		}
		
		if(userList.size()==0)
			return pb;
		for (int i=(pb.getCurrentPage()-1)*pb.getPageSize();
				userList!=null && i< (((pb.getCurrentPage()-1)*pb.getPageSize()+pb.getPageSize())>userList.size()?userList.size():((pb.getCurrentPage()-1)*pb.getPageSize()+pb.getPageSize())) ;
				i++) {
			list.add(userList.get(i).getKey());
		}
		pb.setList(list);
		
		return pb;
	}
	
}
