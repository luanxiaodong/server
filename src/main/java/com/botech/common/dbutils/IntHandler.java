package com.botech.common.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * @author luanxiaodong
 */
public class IntHandler implements ResultSetHandler<Integer> {
	public Integer handle(ResultSet rs) throws SQLException {
		return rs.next() ? rs.getInt(1) : 0 ;
	}
}
