package com.botech.page.bean;

import java.util.List;

/**
 * @author luanxiaodong
 */
public class PageBean<T> {
	public PageBean(){}
	
	//1.main field
		private String cmd;	
		private String id;//uuid
		private String type;
		private String searchParam="";

		//data
		private List<T> list;
		private Integer currentPage=1;//default 1
		private Integer totalSize=0;//default 0
		private Integer pageSize=15;//default 15
		
	//2.calculate
		private Integer totalPage=1;//defaut 1
		private int previouspage;//
		private int nextpage;//
		private int startIndex;//
	
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getSearchParam() {
		return searchParam.trim();//trim
	}
	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = Integer.valueOf(currentPage);
	}
	public Integer getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotalPage() {//
		if(this.totalSize%this.pageSize==0){
			this.totalPage = this.totalSize/this.pageSize;
		}else{
			this.totalPage = this.totalSize/this.pageSize + 1;
		}
		return totalPage;
	}
	public int getPreviouspage() {//
		if(this.currentPage-1>1){
			this.previouspage = this.currentPage-1;
		}else{
			this.previouspage = 1;
		}
		return previouspage;
	}
	public int getNextpage() {//
		if(this.currentPage+1>this.totalPage){
			this.nextpage = this.totalPage;
		}else{
			this.nextpage = this.currentPage + 1;
		}
		return nextpage;
	}
	public int getStartIndex() {
		startIndex=(currentPage-1)*pageSize;
		return startIndex;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
