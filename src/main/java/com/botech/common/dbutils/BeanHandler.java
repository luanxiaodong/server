package com.botech.common.dbutils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.dbutils.annotation.DbField;


/**
 * @author luanxiaodong
 * @param <T>
 */
@SuppressWarnings("unused")
public class BeanHandler<T> implements ResultSetHandler<T> {
	private static final Logger log = LoggerFactory.getLogger(BeanHandler.class);
	
	
	private BeanHandler(){}
	private Class<T> beanClass;
	public BeanHandler(Class<T> beanClass){
		this.beanClass=beanClass;
	}
	
	
	public T handle(ResultSet rs) throws SQLException {
		T bean=null;
		try {
			if(!rs.next()){
				return null;
			}
			bean = beanClass.newInstance();
			Field[] fields = beanClass.getDeclaredFields();
			for(int j=0;fields!=null && j<fields.length;j++){
				try {
					fields[j].setAccessible(true);
					DbField dbField = fields[j].getAnnotation(DbField.class);
					if(null!=dbField && !"".equals(dbField.value()[0])){
						try {
							Object value = rs.getObject(dbField.value()[0]);
							fields[j].set(bean , value);
						} catch (Exception e) {
							fields[j].set(bean, rs.getObject(fields[j].getName()));
						}
					}else{
						fields[j].set(bean, rs.getObject(fields[j].getName()));
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			log.warn("",e);
			e.printStackTrace();
		}
		return bean;
	}
	
}
