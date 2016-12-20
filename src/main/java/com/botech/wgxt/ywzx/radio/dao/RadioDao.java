package com.botech.wgxt.ywzx.radio.dao;

import java.util.List;
import com.botech.wgxt.ywzx.radio.bean.EquipmentRadio;
import com.botech.wgxt.ywzx.radio.bean.ServiceItemRadio;
import com.botech.wgxt.ywzx.radio.bean.TimeInterval;

public interface RadioDao {
	
	/**
	 * 【是否存在这个视频的UUID】   
	     * @Title: isExistRadioServiceId   
	     * @Description: TODO  
	     * @param: @param uuid
	     * @param: @return      
	     * @return: boolean      
	     * @throws
	 */
	boolean isExistRadioServiceId(String uuid);
	
	/**
	 * 【jqz:拿到视频质量检测的配置信息】
	* @Title: getSpzd_Server 
	* @param @return 
	* @return String    返回类型 
	* @throws
	 */
	List<String> getSpzd_Server();
	
	/**
	 * 【拿视频服务ID相关的所有检测项】
	* @Title: getItemsAllForRadio 
	* @param @return 
	* @return List<ServiceItemRadio>    返回类型 
	* @throws
	 */
	List<ServiceItemRadio> getItemsByServiceId(String serviceId);
	
	/**
	 * 【拿设备ID的所有检测项】
	 * @Title: getItemsAllForRadio 
	 * @param @return 
	 * @return List<ServiceItemRadio>    返回类型 
	 * @throws
	 */
	List<ServiceItemRadio> getItemsByEquipId(String serviceId);

	/**
	 * 【通过serviceId拿出所有设备】
	* @Title: getEquipsByServiceId 
	* @param @param serviceId
	* @param @return 
	* @return List<EquipmentRadio>    返回类型 
	* @throws
	 */
	List<EquipmentRadio> getEquipsByServiceId(String serviceId);

	/**
	 * 【拿出扫描间隔小时数】
	* @Title: getScanningInterval 
	* @param @return 
	* @return int    返回类型 
	* @throws
	 */
	int getScanningInterval();

	/**
	 * 【拿出间隔时间的开始时间与结束时间】
	* @Title: getTimeInterval 
	* @param @return 
	* @return TimeInterval    返回类型 
	* @throws
	 */
	TimeInterval getTimeInterval();
	
	/**
	 * 【通过服务ID找出当前服务的IP】   
	     * @Title: getServiceIpByServiceId   
	     * @Description: TODO  
	     * @param: @param serviceId
	     * @param: @return      
	     * @return: String      
	     * @throws
	 */
	String getServiceIpByServiceId(String serviceId);
	
}
