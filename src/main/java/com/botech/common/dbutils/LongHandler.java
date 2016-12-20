package com.botech.common.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * @author luanxiaodong
 */
public class LongHandler implements ResultSetHandler<Long> {
	public Long handle(ResultSet rs) throws SQLException {
		return rs.next() ? rs.getLong(1) : 0L;
	}
}
