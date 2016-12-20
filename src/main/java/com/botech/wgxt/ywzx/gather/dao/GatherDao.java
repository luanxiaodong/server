package com.botech.wgxt.ywzx.gather.dao;

import java.util.List;

import com.botech.wgxt.ywzx.gather.bean.EquipmentGather;
import com.botech.wgxt.ywzx.gather.bean.EquipmentstatusGather;
import com.botech.wgxt.ywzx.gather.bean.ServiceEqupItem;

public interface GatherDao {
	//通过serviceID查出所有的设备
	List<EquipmentGather> getEquipmentsByServiceId(String serviceId);
	
	
	//通过设备ID将所有检测项拿出来
	List<EquipmentstatusGather> getEquipmentItemsByEquipId(String equipId);
	
	//通过serviceId查出所有
	List<ServiceEqupItem> getServiceEquipItemsByServiceId(String serviceId);
}
