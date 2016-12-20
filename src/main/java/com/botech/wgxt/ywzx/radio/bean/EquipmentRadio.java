package com.botech.wgxt.ywzx.radio.bean;

import com.botech.common.dbutils.annotation.DbField;

public class EquipmentRadio {
	
	@DbField("UUID")
	private String uuid;//
	
	@DbField("LOGIC_NO")
	private String cameid;//
	
//	@DbField("loop")
//	private String loop;//
	
	@DbField("SSXT")
	private String szusertype;//

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCameid() {
		return cameid;
	}

	public void setCameid(String cameid) {
		this.cameid = cameid;
	}

	public String getSzusertype() {
		return szusertype;
	}

	public void setSzusertype(String szusertype) {
		this.szusertype = szusertype;
	}
}
