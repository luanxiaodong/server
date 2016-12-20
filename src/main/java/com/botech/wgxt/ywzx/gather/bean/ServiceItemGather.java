package com.botech.wgxt.ywzx.gather.bean;

import com.botech.common.dbutils.annotation.DbField;

//@DbTable("service_detect_set")
public class ServiceItemGather {
	@DbField("ITEM")
	private String item;
	@DbField("DETECT")
	private String detect;
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getDetect() {
		return detect;
	}
	public void setDetect(String detect) {
		this.detect = detect;
	}
}
