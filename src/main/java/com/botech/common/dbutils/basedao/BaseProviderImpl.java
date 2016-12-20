package com.botech.common.dbutils.basedao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.botech.common.c3p0.JdbcUtils;
import com.botech.common.dbutils.BeanHandler;
import com.botech.common.dbutils.BeanListHandler;
import com.botech.common.dbutils.annotation.DbField;
import com.botech.common.dbutils.annotation.DbTable;
import com.botech.common.dbutils.annotation.Like;
import com.botech.common.dbutils.annotation.OrderBy;

/**
 * @author luanxiaodong
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class BaseProviderImpl<T> implements BaseProvider<T> {
	private Class<T> clazz;
	private String tableName;
	private int fieldCount;
	//if many keys.
	private List<Integer> primarykeys=new ArrayList<Integer>();
	//if many likes.
	private List<String> likes=new ArrayList<String>();
	//if many order by .
	private Map<Integer,String[]> orderByMap=new TreeMap<Integer,String[]>();
	
	
	private Map<Integer,String[]> map=new HashMap<Integer,String[]>();
	public BaseProviderImpl(){
		//clazz
		ParameterizedType pt=(ParameterizedType)getClass().getGenericSuperclass();
		clazz=(Class<T>) pt.getActualTypeArguments()[0];
		
		//table
		DbTable tb = clazz.getAnnotation(DbTable.class);
		tableName=tb.value()[0];
		
		//dbfilecount
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0;fields!=null && i<fields.length;i++){
			try {
				fields[i].setAccessible(true);
				DbField field = fields[i].getAnnotation(DbField.class);
				//get all fields.
				if(field!=null){
					map.put(fieldCount++,field.value());
				}
				//only one primary key
				if(field.value().length>=3 && "primarykey".equals(field.value()[2])){
					primarykeys.add(i);
				}
				
				//get likes
				Like like = fields[i].getAnnotation(Like.class);
				if(like!=null){
					likes.add(like.value()[0]);
				}
				
				//get order by fields.
				OrderBy orderby = fields[i].getAnnotation(OrderBy.class);
				if(orderby!=null){
					String[] nameOrder=new String[]{fields[i].getName(),orderby.value()[1]};
					orderByMap.put(Integer.valueOf(orderby.value()[0]), nameOrder );
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
			
	}

	@Override
	public int insert(T obj) {
		//sql
		StringBuilder sql=new StringBuilder();
		sql.append("insert into "+tableName+" (");
		for(int i=0;i<fieldCount;i++){
			sql.append(map.get(i)[0]);
			if((i+1)!=fieldCount){
				sql.append(",");
			}
		}
		sql.append(") values(");
		for(int i=0;i<fieldCount;i++){
			sql.append("?");
			if((i+1)!=fieldCount){
				sql.append(",");
			}
		}
		sql.append(")");
		
		//params
		Object[] params=new Object[fieldCount];
		for(int i=0;i<fieldCount;i++){
			try{
				Field field = clazz.getDeclaredField(map.get(i)[0]);
				field.setAccessible(true);
				params[i]=field.get(obj);
			}catch(Exception e){
				//log.error(e);
				e.printStackTrace();
				continue;
			}
		}
		
		try {
			return JdbcUtils.getQueryRunner().update(sql.toString(), params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int insert(Connection conn, T obj) {
		//sql
		StringBuilder sql=new StringBuilder();
		sql.append("insert into "+tableName+" (");
		for(int i=0;i<fieldCount;i++){
			sql.append(map.get(i)[0]);
			if((i+1)!=fieldCount){
				sql.append(",");
			}
		}
		sql.append(") values(");
		for(int i=0;i<fieldCount;i++){
			sql.append("?");
			if((i+1)!=fieldCount){
				sql.append(",");
			}
		}
		sql.append(")");
		
		//params
		Object[] params=new Object[fieldCount];
		for(int i=0;i<fieldCount;i++){
			try{
				Field field = clazz.getDeclaredField(map.get(i)[0]);
				field.setAccessible(true);
				params[i]=field.get(obj);
			}catch(Exception e){
				//log.error(e);
				e.printStackTrace();
				continue;
			}
		}
		
		try {
			return JdbcUtils.getQueryRunner().update(conn,sql.toString(), params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int delete(String id) {
		String sql="delete from "+tableName+" where "+map.get(primarykeys.get(0))[0]+"=?";
		try {
			return JdbcUtils.getQueryRunner().update(sql, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int delete(Connection conn, String id) {
		String sql="delete from "+tableName+" where "+map.get(primarykeys.get(0))[0]+"=?";
		try {
			return JdbcUtils.getQueryRunner().update(conn,sql, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int update(T obj) {
		//set a=?,b=?
		StringBuilder updatesql=new StringBuilder();
		updatesql.append("update "+tableName+" set ");
		for(int i=0;i<fieldCount;i++){
			if(primarykeys.get(0)==i){
				continue;
			}
			updatesql.append(map.get(i)[0]+"=? ");
			if((i+1)!=fieldCount){
				updatesql.append(",");
			}
		}
		updatesql.append("  where "+map.get(primarykeys.get(0))[0]+"=? ");
		
		//params
		Object[] params=new Object[fieldCount];
		int j=0;
		for(int i=0;i<fieldCount;i++){
			try{
				if(primarykeys.get(0)==i){
					Field field = clazz.getDeclaredField(map.get(primarykeys.get(0))[0]);
					field.setAccessible(true);
					params[fieldCount-1]=field.get(obj);
					continue;
				}

				Field field = clazz.getDeclaredField(map.get(i)[0]);
				field.setAccessible(true);
				params[j++]=field.get(obj);
			}catch(Exception e){
				//log.error(e);
				e.printStackTrace();
				continue;
			}
		}
		try {
			return JdbcUtils.getQueryRunner().update(updatesql.toString(), params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int update(Connection conn, T obj) {
		//set a=?,b=?
		StringBuilder updatesql=new StringBuilder();
		updatesql.append("update "+tableName+" set ");
		for(int i=0;i<fieldCount;i++){
			if(primarykeys.get(0)==i){
				continue;
			}
			updatesql.append(map.get(i)[0]+"=? ");
			if((i+1)!=fieldCount){
				updatesql.append(",");
			}
		}
		updatesql.append("  where "+map.get(primarykeys.get(0))[0]+"=? ");
		
		//params
		Object[] params=new Object[fieldCount];
		int j=0;
		for(int i=0;i<=fieldCount;i++){
			try{
				if(primarykeys.get(0)==i){
					Field field = clazz.getDeclaredField(map.get(primarykeys.get(0))[0]);
					field.setAccessible(true);
					params[fieldCount-1]=field.get(obj);
					continue;
				}
			
				Field field = clazz.getDeclaredField(map.get(i)[0]);
				field.setAccessible(true);
				params[j++]=field.get(obj);
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		try {
			return JdbcUtils.getQueryRunner().update(conn,updatesql.toString(), params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public T getBeanById(String id) {
		StringBuilder selectsql=new StringBuilder();
		selectsql.append("select * from "+tableName+" where ");
		selectsql.append(map.get(primarykeys.get(0))+"=?");
		try {
			return JdbcUtils.getQueryRunner().query(selectsql.toString(),id, new BeanHandler<T>(clazz));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<T> getAll() {
		StringBuilder selectsql=new StringBuilder();
		selectsql.append("select * from "+tableName+" where 1=1");
		try {
			return JdbcUtils.getQueryRunner().query(selectsql.toString(), new BeanListHandler<T>(clazz));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}


}
