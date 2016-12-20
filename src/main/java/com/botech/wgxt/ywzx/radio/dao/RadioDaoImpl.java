package com.botech.wgxt.ywzx.radio.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.c3p0.JdbcUtils;
import com.botech.common.dbutils.BeanListHandler;
import com.botech.common.dbutils.IntHandler;
import com.botech.common.dbutils.StringHandler;
import com.botech.wgxt.ywzx.radio.bean.EquipmentRadio;
import com.botech.wgxt.ywzx.radio.bean.ServiceItemRadio;
import com.botech.wgxt.ywzx.radio.bean.TimeInterval;

public class RadioDaoImpl implements RadioDao {
	private static final Logger log = LoggerFactory.getLogger(RadioDaoImpl.class);
	
	@Override
	public List<String> getSpzd_Server() {
		String sql="  select sys_value from ywzx_sysparam t where t.sys_key in( 'jqz', 'pic' )  ";
		try {
			return JdbcUtils.getQueryRunner().query(sql, new ResultSetHandler<List<String>>(){
				@Override
				public List<String> handle(ResultSet rs) throws SQLException {
					List<String> list=new ArrayList<String>();
					while(rs.next()){
						list.add(rs.getString("sys_value"));
					}
					return list;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return new ArrayList<String>();
	}

	@Override
	public List<ServiceItemRadio> getItemsByServiceId(String serviceId) {
		String sql=
//	 "select unique item,time_value from (  "+  //去掉time_value，传过去的全为<value>0</value>
	 "select unique item from (  "+
	      " select  "+
		  " se.service_uuid as uuid, "+
          " equip.logic_no  as logicNo, "+
          " equip.system as system,  "+
          " detect_item.item, "+
          " decode(edi.item_times,null,detect_item.item_times,edi.item_times) itemTimes, "+
          " decode(edi.time_value,null,detect_item.time_value,edi.time_value) time_value  "+
       " from( "+
       "      select service_uuid,equip_uuid from service_equip where  delflag = 1 and service_uuid = ?    "+
       " )se "+
//       " join (select uuid,logic_no,system from equip where delflag = 1 ) equip on (equip.uuid  = se.equip_uuid) "+
       " join (select uuid,logic_no,system from equip where 1=1 ) equip on (equip.uuid  = se.equip_uuid) "+
       " join (select uuid, item from service where  delflag = 1) s on (s.uuid = se.service_uuid) "+
       " left join (select item,item_times,time_value from detect_item where delflag = 1) detect_item on (instr(','||s.item||',',','||detect_item.item||',') !=0 ) "+
       " left join (select item,equip_uuid,service_uuid,item_times,time_value from equip_detect_item  where delflag = 1 ) edi on (edi.service_uuid = se.service_uuid and instr(','||edi.equip_uuid||',', se.equip_uuid) != 0  and edi.item  = detect_item.item ) " +
	" ) "  ;
		try {
			return JdbcUtils.getQueryRunner().query(sql, new BeanListHandler<ServiceItemRadio>(ServiceItemRadio.class),serviceId);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return new ArrayList<ServiceItemRadio>();
	}
	
	//这个方法可能会有重复的
	@Override
	public List<ServiceItemRadio> getItemsByEquipId(String equipId) {
		String sql=
				" select  "+
						" se.service_uuid as uuid, "+
						" equip.logic_no  as logicNo, "+
						" equip.system as system,  "+
				        " detect_item.item, "+
				        " decode(edi.item_times,null,detect_item.item_times,edi.item_times) itemTimes, "+
				        " decode(edi.time_value,null,detect_item.time_value,edi.time_value) time_value  "+
						" from( "+
						"      select service_uuid,equip_uuid from service_equip where  delflag = 1 and equip_uuid = ?    "+
						" )se "+
						" join (select uuid,logic_no,system from equip where delflag = 1 ) equip on (equip.uuid  = se.equip_uuid) "+
						" join (select uuid, item from service where  delflag = 1) s on (s.uuid = se.service_uuid) "+
						" left join (select item,item_times,time_value from detect_item where delflag = 1) detect_item on (instr(','||s.item||',',','||detect_item.item||',') !=0 ) "+
						" left join (select item,equip_uuid,service_uuid,item_times,time_value from equip_detect_item  where delflag = 1 ) edi on (edi.service_uuid = se.service_uuid and instr(','||edi.equip_uuid||',', se.equip_uuid) != 0  and edi.item  = detect_item.item ) ";
		
		try {
			return JdbcUtils.getQueryRunner().query(sql, new BeanListHandler<ServiceItemRadio>(ServiceItemRadio.class),equipId);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return new ArrayList<ServiceItemRadio>();
	}

	@Override
	public List<EquipmentRadio> getEquipsByServiceId(String serviceId) {
		String sql="     select system,uuid,LOGIC_NO,e.SSXT from equip e join service_equip se on  "+
				" ( e.uuid=se.equip_uuid and se.service_uuid=?  "+
//				" and e.state=1 and e.delflag=1  "+//设备状态为启用且没有删除
				" and e.state=1  "+//设备状态为启用且没有删除
				" and se.type=2 and se.delflag=1 ) ";//视频类型为2的且没有删除的
		try {
			return JdbcUtils.getQueryRunner().query(sql, new BeanListHandler<EquipmentRadio>(EquipmentRadio.class),serviceId);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return new ArrayList<EquipmentRadio>();
	}

	@Override
	public int getScanningInterval() {
		String sql=" select subinfo from sym.sy_t_codevalue t where type_id = 135 and code_value='lx' ";
		try {
			String intervalStr = JdbcUtils.getQueryRunner().query(sql, new StringHandler());
			return Integer.valueOf(intervalStr);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
		}
		return 2;
	}

	@Override
	public TimeInterval getTimeInterval() {
		String sql=" select sys_key,sys_value from ywzx_sysparam t where t.sys_key in ('spjckssj','spjcjssj') ";
		try {
			return JdbcUtils.getQueryRunner().query(sql, new ResultSetHandler<TimeInterval>(){
				@Override
				public TimeInterval handle(ResultSet rs) throws SQLException {
					TimeInterval ti = new TimeInterval();
					while(rs.next()){
						String key = rs.getString("sys_key");
						String value = rs.getString("sys_value");
						if("spjckssj".equals(key)){
							ti.setStarttime(value);
						}else if("spjcjssj".equals(key)){
							ti.setEndtime(value);
						}
					}
					return ti;
				}
				
			});
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return new TimeInterval();
	}

	@Override
	public String getServiceIpByServiceId(String serviceId) {
		String sql=" select ip from equip e where e.uuid in (select s.equip_uuid from service s where s.uuid=? ) ";
		try {
			String intervalStr = JdbcUtils.getQueryRunner().query(sql, new StringHandler(),serviceId);
			return ((null==intervalStr || "".equals(intervalStr))?"127.0.0.1":intervalStr);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
		}
		return "127.0.0.1";
	}

	@Override
	public boolean isExistRadioServiceId(String uuid) {
		String sql="  select count(1) from service s where s.uuid=? and s.type=2 and s.state=1 and s.delflag=1 ";
		try {
			int count = JdbcUtils.getQueryRunner().query(sql, new IntHandler(),uuid);
			return count>0?true:false;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
		}
		return false;
	}
	
}
