package com.botech.common.dbutils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.dbutils.annotation.DbField;

/**
 * @author luanxiaodong
 * @param <T>
 */
public class BeanListHandler<T> implements ResultSetHandler<List<T>> {
	private static final Logger log = LoggerFactory.getLogger(BeanListHandler.class);
	
	@SuppressWarnings("unused")
	private BeanListHandler(){}
	private Class<T> beanClass;
	public BeanListHandler(Class<T> beanClass){
		this.beanClass=beanClass;
	}
	public  List<T> handle(ResultSet rs) throws SQLException {
		List<T> list=new ArrayList<T>();
		try {
			T bean = null;
			while(rs.next()){
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
						//log.error("",e);
						continue;
					}
				}
				list.add(bean);
			}
		} catch (Exception e) {
			log.warn("",e);
			e.printStackTrace();
		}
		return list;
	}

}
