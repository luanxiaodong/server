package com.botech.wgxt.ywzx.gather.bean;

import java.util.List;

import com.botech.common.dbutils.annotation.DbField;

public class EquipmentGather {
	
	@DbField("SYSTEM")
	private String ID;//<ID>系统标识</ID>
	
	@DbField("UUID")
	private String EID;//<EID>设备编号</EID>
	
	@DbField("IP")
	private String IP;//<IP>设备IP地址</IP>
	
	private List<EquipmentstatusGather> list;
	
	public List<EquipmentstatusGather> getList() {
		return list;
	}
	public void setList(List<EquipmentstatusGather> list) {
		this.list = list;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getEID() {
		return EID;
	}
	public void setEID(String eID) {
		EID = eID;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
}
