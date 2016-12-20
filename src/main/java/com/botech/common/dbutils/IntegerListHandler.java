package com.botech.common.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * @author luanxiaodong
 */
public class IntegerListHandler implements ResultSetHandler<List<Integer>> {
	public List<Integer> handle(ResultSet rs) throws SQLException {
		List<Integer> list = new ArrayList<Integer>();
		while(rs.next()){list.add(rs.getInt(1));}
		return list;
	}
}
