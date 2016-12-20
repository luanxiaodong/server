package com.botech.wgxt.ywzx.redis.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.c3p0.JdbcUtils;
import com.botech.wgxt.ywzx.redis.bean.Redis;

public class RedisDaoImpl implements RedisDao {
	private static final Logger log = LoggerFactory.getLogger(RedisDaoImpl.class);  
	
	//返回是XML
	@Override
	public List<Redis> getRedisInfo() {
		String sql="select sys_key,sys_value from ywzx_sysparam t where t.sys_key in ('redismz','redisdz') order by sys_key desc";
		try {
			return JdbcUtils.getQueryRunner().query(sql, new ResultSetHandler<List<Redis>>(){

				@Override
				public List<Redis> handle(ResultSet rs) throws SQLException {
					ArrayList<Redis> list = new ArrayList<Redis>();
					while(rs.next()){
						Redis redis=new Redis();
						redis.setKey(rs.getString("sys_key"));
						redis.setValue(rs.getString("sys_value"));
						list.add(redis);
					}
					return list;
				}
				
			});
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return new ArrayList<Redis>();
	}

	//返回的redis模值是字符串    	 20:10.49.130.1,10.49.130.2,10.49.130.3,10.49.130.4
	@Override
	public String getRedisStr() {
		String sql="select sys_value from ywzx_sysparam t where t.sys_key in ('redismz','redisdz') order by sys_key desc";
		try {
			return JdbcUtils.getQueryRunner().query(sql, new ResultSetHandler<String>(){
				@Override
				public String handle(ResultSet rs) throws SQLException {
					StringBuilder sb=new StringBuilder();
					int i = 0;
					while(rs.next()){
						i++;
						sb.append(rs.getString("sys_value"));
						if(i<2){
							sb.append("|");
						}
					}
					return sb.toString();
				}
				
			});
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return "";
	}
}
