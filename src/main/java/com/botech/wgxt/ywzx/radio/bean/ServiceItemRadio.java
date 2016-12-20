package com.botech.wgxt.ywzx.radio.bean;

import com.botech.common.dbutils.annotation.DbField;

//视频服务器检测项
public class ServiceItemRadio {
	@DbField("item")
	private String item;
	
	@DbField("itemTimes")
	private String itemTimes;
	
	@DbField("time_value")
	private int time_value;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItemTimes() {
		return itemTimes;
	}

	public void setItemTimes(String itemTimes) {
		this.itemTimes = itemTimes;
	}

	public int getTime_value() {
		return time_value;
	}

	public void setTime_value(int time_value) {
		this.time_value = time_value;
	}

}
