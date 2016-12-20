package com.botech.wgxt.ywzx.gather.dao;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.c3p0.JdbcUtils;
import com.botech.common.dbutils.BeanListHandler;
import com.botech.wgxt.ywzx.gather.bean.EquipmentGather;
import com.botech.wgxt.ywzx.gather.bean.EquipmentstatusGather;
import com.botech.wgxt.ywzx.gather.bean.ServiceEqupItem;

public class GatherDaoImpl implements GatherDao {
	private static final Logger log = LoggerFactory.getLogger(GatherDaoImpl.class);  
	
	
	/**
	 * 【通过服务器ID找出所有的设备】
	 * 表关联
	 * equip	service_equip
	 */
	@Override
	public List<EquipmentGather> getEquipmentsByServiceId(String serviceId) {
		String sql=" select system,uuid,ip from equip e join service_equip se on  "+
				" ( e.uuid=se.equip_uuid and se.service_uuid=?  "+
				" and e.state=1 and e.delflag=1  "+//状态是启用且没有删除的设备
				" and se.type=1 and se.delflag=1 ) ";//类型是采集且没有删除的设备
		try {
			return JdbcUtils.getQueryRunner().query(sql, new BeanListHandler<EquipmentGather>(EquipmentGather.class), serviceId);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return null;
	}

	/**
	 * 【通过设备ID将所有检测项拿出来】
	 */
	@Deprecated
	@Override
	public List<EquipmentstatusGather> getEquipmentItemsByEquipId(String equipId) {
		String sql=
		" select unique uuid,detect,item,time_value from ( "+
				" select "+
				" se.service_uuid,se.detect,se.uuid, se.ip,se.system , decode(edi.item,null,detect_item.item,edi.item) item ,decode(edi.time_value,null,detect_item.time_value,edi.time_value) time_value "+  
				" from( "+
				"  select "+
				"   sds.detect,se.service_uuid,equip.uuid,equip.ip,equip.system ,decode(fwqitem.item,null,sds.item,fwqitem.item ) item "+
				"  from ( "+
				"    select service_uuid,equip_uuid from service_equip where  delflag = 1 and equip_uuid = ? and type = 1 "+  
				"  ) se left join (   select uuid,'equip' bs, dev_type, system,ip from equip where state = 1 and delflag = 1 union all select uuid,'service' bs,N'' dev_type,system,N'' ip from service where state = 1 and delflag =1) equip on (equip.uuid = se.equip_uuid) "+
				"  left join (select item from detect_item where state = 1 and delflag = 1 and  big_type in ('fwq','jz') ) fwqitem on equip.bs = 'equip'  "+
				"  left join service_detect_set sds on(sds.service = equip.uuid and sds.delflag = 1 and equip.bs = 'service') "+
				"  )se "+
				" left join detect_item on ( se.item = detect_item.item and detect_item.delflag = 1 and detect_item.state = 1 ) "+
				"  left join equip_detect_item edi  on (edi.service_uuid = se.service_uuid and instr(','||edi.equip_uuid||',', se.uuid) != 0 and edi.delflag  = 1) " +
		" ) "	;
		try {
			return JdbcUtils.getQueryRunner().query(sql, new BeanListHandler<EquipmentstatusGather>(EquipmentstatusGather.class), equipId);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return null;
	}

	@Override
	public List<ServiceEqupItem> getServiceEquipItemsByServiceId(String serviceId) {
		String sql=
				" select " +
				" se.service_uuid,se.detect,se.uuid,se.ip,se.system,se.ssxt , decode(edi.item,null,detect_item.item,edi.item) item ,decode(edi.time_value,null,detect_item.time_value,edi.time_value) time_value " +  
				" from( " +
				" select " +
				"  sds.detect,se.service_uuid,equip.uuid,equip.ip,equip.system,equip.ssxt ,decode(fwqitem.item,null,sds.item,fwqitem.item ) item " +
				" from ( " +
				"  select service_uuid,equip_uuid from service_equip where  delflag = 1 and service_uuid =? and type = 1 " +  
				" ) se left join (   select uuid,'equip' bs, dev_type, system,ssxt,ip from equip where state = 1 and delflag = 1 " +
				" union all " +
				" select service.uuid,'service' bs,N'' dev_type,service.system,service.ssxt,ep.ip||':'||service.port ip from ( " +
				" select EQUIP_UUID,uuid,'service' bs,N'' dev_type,system,ssxt,port from service where state = 1 and delflag =1 " +
				" ) service " +
				" join equip ep on ep.uuid=service.EQUIP_UUID " +
				" ) equip on (equip.uuid = se.equip_uuid) " +
				" left join (select uuid,item from device_type where state = 1 and delflag = 1  ) fwqitem on (equip.bs = 'equip' and equip.dev_type = fwqitem.uuid) " +  
				" left join service_detect_set sds on(sds.service = equip.uuid and sds.delflag = 1 and equip.bs = 'service') " +
				" )se " +
				" left join detect_item on ( instr(','||se.item||',',','||detect_item.item||',') > 0    and detect_item.delflag = 1 and detect_item.state = 1 ) " +
				" left join equip_detect_item edi  on (edi.service_uuid = se.service_uuid and instr(','||edi.equip_uuid||',', se.uuid) != 0 and edi.delflag  = 1 and edi.item=se.item) " ;
		try {
			return JdbcUtils.getQueryRunner().query(sql,new BeanListHandler<ServiceEqupItem>(ServiceEqupItem.class), serviceId);//0bb0f745ee434e05823f109123100a23
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("",e);
		}
		return null;
	}


}
