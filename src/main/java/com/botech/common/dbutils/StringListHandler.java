package com.botech.common.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * @author luanxiaodong
 */
public class StringListHandler implements ResultSetHandler<List<String>> {
	public List<String> handle(ResultSet rs) throws SQLException {
		List<String> list = new ArrayList<String>();
		while(rs.next()){list.add(rs.getString(1));}
		return list;
	}
}
