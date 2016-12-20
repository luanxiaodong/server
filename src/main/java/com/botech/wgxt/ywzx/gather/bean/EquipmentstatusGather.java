package com.botech.wgxt.ywzx.gather.bean;

import com.botech.common.dbutils.annotation.DbField;

//设备状态
public class EquipmentstatusGather {
	
	
	@DbField("detect")
	private String oid;//<检测类型>OID</检测类型> //OID（字典表中的附属内容）
	
	@DbField("item")
	private String name;//
	
	@DbField("time_value")
	private Object rate;//<检测类型:rate>采集频率</检测类型:rate> //单位：秒
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Object getRate() {
		return rate;
	}

	public void setRate(Object rate) {
		this.rate = rate;
	}

}
