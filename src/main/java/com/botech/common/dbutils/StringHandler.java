package com.botech.common.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * @author luanxiaodong
 */
public class StringHandler implements ResultSetHandler<String> {
	public String handle(ResultSet rs) throws SQLException {
		return rs.next() ? rs.getString(1) : null;
	}
}
