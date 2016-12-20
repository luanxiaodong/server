package com.botech.wgxt.ywzx.gather.bean;

import com.botech.common.dbutils.annotation.DbField;

public class ServiceEqupItem {
	private String service_uuid ;
	private String detect ;
	private String uuid ;
	private String ip ;
	@DbField("ssxt")
	private String system ;
	private String item ;
	private Object time_value ;
	public String getService_uuid() {
		return service_uuid;
	}
	public void setService_uuid(String service_uuid) {
		this.service_uuid = service_uuid;
	}
	public String getDetect() {
		return detect;
	}
	public void setDetect(String detect) {
		this.detect = detect;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public Object getTime_value() {
		return time_value;
	}
	public void setTime_value(Object time_value) {
		this.time_value = time_value;
	}
}
