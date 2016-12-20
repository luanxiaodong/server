package com.botech.wgxt.ywzx.gather.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.utils.ClassUtils;
import com.botech.wgxt.ywzx.gather.bean.EquipmentGather;
import com.botech.wgxt.ywzx.gather.bean.EquipmentstatusGather;
import com.botech.wgxt.ywzx.gather.bean.ServiceEqupItem;
import com.botech.wgxt.ywzx.gather.dao.GatherDao;

/**
 * 【采集管理】
* @ClassName: GatherManager 
* @author luanxd
* @date 2015-8-18 下午4:21:31 
*
 */
public class GatherManager {
	private static final Logger log = LoggerFactory.getLogger(GatherManager.class);

    //包装里面一个容器类来模似出管理类，做的这一切都是为了更好的解耦
    private static class GatherManagerContainer {
        private static GatherManager instance = new GatherManager();
    }
    public static GatherDao getGatherDao() {
        return GatherManagerContainer.instance.Dao;
    }

    /**
     * 返回一个单例的redis管理
     */
    public static GatherManager getInstance() {
        return GatherManagerContainer.instance;
    }
    private GatherDao Dao;

    /**
     * 构造管理类的时候去初始化DAO
     */
    public GatherManager() {
        // Load an  Dao.
        initDao();
    }

    /**
     * 初始化DAO
     */
    private void initDao() {

        String className = "com.botech.wgxt.ywzx.gather.dao.GatherDaoImpl";
        // Check if we need to reset the Dao class        
        if (Dao == null || !className.equals(Dao.getClass().getName())) {
	        try {
	            Class c = ClassUtils.forName(className);
	            Dao = (GatherDao) c.newInstance();
	        }
	        catch (Exception e) {
	            log.error("Error loading GatherDao Dao: " + className, e);
	        }
        }
    }

    //拿到dao
	public GatherDao getDao() {
		return Dao;
	}
	
	
	//通过设备ID查出该设备的xml配置信息.
	public String getXmlByServiceId(String serviceId){
		
		StringBuilder sb=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<message>");
			sb.append("<action>put</action>");//
			sb.append("<ID>"+serviceId+"</ID>");//---------------1.服务相关
			sb.append("<type>01010001</type>");
			sb.append("<success>0</success>");
			sb.append("<exception></exception>");
			
			List<ServiceEqupItem> all = Dao.getServiceEquipItemsByServiceId(serviceId);
			Map<String,List<ServiceEqupItem>> map=new HashMap<String,List<ServiceEqupItem>>();
			if(null!=all)
			for(ServiceEqupItem one:all){
				String uuid = one.getUuid();
				if(null==uuid || null==one.getItem()){
					continue;
				}
				List<ServiceEqupItem> list = map.get(uuid);
				if(list==null){
					list=new ArrayList<ServiceEqupItem>();
					map.put(uuid,list);
				}
				list.add(one);
			}
			//2.equipment
			if(null!=map)
			for(Map.Entry<String,List<ServiceEqupItem>> entry:map.entrySet()){
				String key = entry.getKey();
				List<ServiceEqupItem> itemList = entry.getValue();
				sb.append("<equipment>");//------------------------------2.设备相关
				sb.append("<ID>"+itemList.get(0).getSystem()+"</ID>");//系统标识SYSTEM
				sb.append("<EID>"+key+"</EID>");//设备UUID
				sb.append("<IP>"+((null==itemList.get(0).getIp())?"":itemList.get(0).getIp())+"</IP>");
				
				sb.append("<Equipmentstatus>");//-----------------3.检测项相关
				
				Map<String , ServiceEqupItem> itemMap=new HashMap<String , ServiceEqupItem>();
				for(ServiceEqupItem item:itemList){//去重复功能
					itemMap.put(item.getItem(), item);
				}
				for(Map.Entry<String, ServiceEqupItem> en:itemMap.entrySet()){
					ServiceEqupItem item = en.getValue();
					sb.append("<"+item.getItem()+">"+(  (null==item.getDetect())?"":item.getDetect() )+"</"+item.getItem()+">");
					sb.append("<"+item.getItem()+"-rate>"+item.getTime_value()+"</"+item.getItem()+"-rate>");
				}
					
					
					
				sb.append("</Equipmentstatus>");
				
				sb.append("</equipment>");
			}
			//3.Equipmentstatus list inner many
		
		
		sb.append("</message>");
		
//		return stream.toXML(message);//将message转成xml
		return sb.toString();
	}
	/**服务器返回格式如下
	<?xml version=\"1.0\" encoding=\"UTF-8\"?>
	<message>
	 <ID>唯一标识</ID>  //表示网管采集服务唯一标识（调度单元采用用户名，其他的采用服务标识）
	 <type>00010001</type>  //用于区分不同的查询请求，以返回对应结果
	 <success>0</success>  //0 成功，其他数值为错误代码
	 <exception>XXXX</exception> //当返回结果不为0时，将失败原因具体描述放在此处
	 <equipment>
	  <ID>系统标识</ID>
	  <EID>设备编号</EID>
	  <IP>设备IP地址</IP>
	  <Equipmentstatus>
	   <检测类型>OID</检测类型> //OID（字典表中的附属内容）
	   <检测类型:rate>采集频率</检测类型:rate> //单位：秒
	   ••• •••
	  </Equipmentstatus>
	 </equipment>
	</message>
	*/
	/*	
	//2.equipment
	List<EquipmentGather> list = Dao.getEquipmentsByServiceId(serviceId);//所有设备
		if(null!=list)
		for(EquipmentGather equip:list){
			sb.append("<equipment>");//------------------------------2.设备相关
			sb.append("<ID>"+equip.getID()+"</ID>");//系统标识SYSTEM
			sb.append("<EID>"+equip.getEID()+"</EID>");//设备UUID
			sb.append("<IP>"+((null==equip.getIP())?"":equip.getIP())+"</IP>");
			
			sb.append("<Equipmentstatus>");//-----------------3.检测项相关
			
			List<EquipmentstatusGather> itemList = Dao.getEquipmentItemsByEquipId(equip.getEID());//查出与设备相关的检测项
				if(null!=itemList)
				for(EquipmentstatusGather item:itemList){
					sb.append("<"+item.getName()+">"+(  (null==item.getOid())?"":item.getOid() )+"</"+item.getName()+">");
					sb.append("<"+item.getName()+"-rate>"+item.getRate()+"</"+item.getName()+"-rate>");
				}
				
			sb.append("</Equipmentstatus>");
			
			sb.append("</equipment>");
		}
	//3.Equipmentstatus list inner many
	*/
	public static void main(String[] args) {
		/*
		StringBuilder sb=new StringBuilder();
		GatherDao Dao = getGatherDao();
		List<EquipmentstatusGather> itemList = Dao.getEquipmentItemsByEquipId("1e785f85ec3a2816e053b803a8c037dc");//查出与设备相关的检测项
		if(null!=itemList)
		for(EquipmentstatusGather item:itemList){
			sb.append("<"+item.getName()+">"+(  (null==item.getOid())?"":item.getOid() )+"</"+item.getName()+">");
			sb.append("<"+item.getName()+":rate>"+item.getRate()+"</"+item.getName()+":rate>");
		}
		System.err.println(sb.toString());
		*/
//		String xmlAll = getInstance().getXmlByServiceId("12345678");
		String xmlAll = getInstance().getXmlByServiceId("e0c0072debca421c875eba0e3c163258");
		System.err.println(xmlAll);
	}
}
