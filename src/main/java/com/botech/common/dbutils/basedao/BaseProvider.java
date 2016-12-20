package com.botech.common.dbutils.basedao;

import java.sql.Connection;
import java.util.List;

import com.botech.page.bean.PageBean;

/**
 * @author luanxiaodong
 * @param <T>
 */
public interface BaseProvider<T> {
	
	//add
	public int insert(T obj);
	public int insert(Connection conn,T obj);
	
	//delete
	public int delete(String id);
	public int delete(Connection conn,String id);
	
	//update
	public int update(T obj);
	public int update(Connection conn,T obj);
	
	//search
	public T getBeanById(String id);
	public List<T> getAll();
	public PageBean<T> getPageBean(PageBean<T> pb,Object...params);
}
