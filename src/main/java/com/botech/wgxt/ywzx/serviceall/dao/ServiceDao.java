package com.botech.wgxt.ywzx.serviceall.dao;

/**
 * 【】
* @ClassName: ServiceDao 
* @author luanxd
* @date 2015-8-25 上午11:40:10 
*
 */
public interface ServiceDao {
	
	/**
	 * 【通过设备的ID与IP地址来验证是不是存在这个设备，存在就近回true否则返回false】
	* @Title: validateServiceIdAndIp 
	* @param @param serviceId
	* @param @param ip
	* @param @return 
	* @return boolean    返回类型 
	* @throws
	 */
	public boolean validateServiceIdAndIp(String serviceId,String ip);
}
