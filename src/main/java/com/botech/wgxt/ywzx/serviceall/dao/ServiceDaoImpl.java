package com.botech.wgxt.ywzx.serviceall.dao;

import java.sql.SQLException;

import com.botech.common.c3p0.JdbcUtils;
import com.botech.common.dbutils.IntHandler;

public class ServiceDaoImpl implements ServiceDao {

	@Override
	public boolean validateServiceIdAndIp(String serviceId, String ip) {
		String sql="select count(*) from service where UUID=? and ADDR=? ";
		try {
			return JdbcUtils.getQueryRunner().query(sql, new IntHandler(),serviceId,ip)>0 ? true:false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
