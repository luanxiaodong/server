package com.botech.wgxt.ywzx.radio.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.utils.ClassUtils;
import com.botech.wgxt.ywzx.radio.bean.EquipmentRadio;
import com.botech.wgxt.ywzx.radio.bean.ServiceItemRadio;
import com.botech.wgxt.ywzx.radio.bean.TimeInterval;
import com.botech.wgxt.ywzx.radio.dao.RadioDao;

/**
 * 【视频管理】
* @ClassName: RadioManager 
* @author luanxd
* @date 2015-8-18 下午4:21:31 
*
 */
public class RadioManager {
	private static final Logger log = LoggerFactory.getLogger(RadioManager.class);

    //包装里面一个容器类来模似出管理类，做的这一切都是为了更好的解耦
    private static class RadioManagerContainer {
        private static RadioManager instance = new RadioManager();
    }
    public static RadioDao getRadioDao() {
        return RadioManagerContainer.instance.Dao;
    }

    /**
     * 返回一个单例的管理
     */
    public static RadioManager getInstance() {
        return RadioManagerContainer.instance;
    }
    private RadioDao Dao;

    /**
     * 构造管理类的时候去初始化DAO
     */
    public RadioManager() {
        // Load an  Dao.
        initDao();
    }

    /**
     * 初始化DAO
     */
    private void initDao() {

        String className = "com.botech.wgxt.ywzx.radio.dao.RadioDaoImpl";
        // Check if we need to reset the Dao class        
        if (Dao == null || !className.equals(Dao.getClass().getName())) {
	        try {
	            Class c = ClassUtils.forName(className);
	            Dao = (RadioDao) c.newInstance();
	        }
	        catch (Exception e) {
	            log.error("Error loading RadioDao Dao: " + className, e);
	        }
        }
    }

    //拿到dao
	public RadioDao getDao() {
		return Dao;
	}
	
	
	//通过设备ID查出该设备的xml配置信息.
	public String getXmlByEquipId(String serviceId){
		
		StringBuilder sb=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<root>");
			sb.append("<head>");//if sucess
			sb.append("<success>success</success>");
			
			boolean existRadioServiceId = Dao.isExistRadioServiceId(serviceId);//是否存在这个视频服务的UUID
			
			if(!existRadioServiceId){
				sb.append("<exception>UUID is not Exsit</exception>");
				sb.append("</head>");
				sb.append("<body>");
				sb.append("</body>");
				sb.append("</root>");
				return sb.toString();
			}
			
			sb.append("<exception></exception>");
			sb.append("</head>");
			sb.append("<body>");
			
			
//-------------------------spzd_server----------------------------------------------------------------------begin			
			String serviceIp = Dao.getServiceIpByServiceId(serviceId);
			
				sb.append("<spzd_server>");
				List<String> confList = Dao.getSpzd_Server();//spzd_server-->record-->inner..
				
				
				boolean existPic=false;
				for(String str:confList){
					if(str.contains("<zdz>pic</zdz>")){
						existPic=true;     break;
					}
				}
				if(!existPic && existRadioServiceId){
					String pic="<zdz>pic</zdz><ip>http://192.168.3.243:80/videoQlty</ip><port></port><username></username><userpwd></userpwd>";
					confList.add(pic);
				}
				
					for(int i=0;confList!=null && i<confList.size();i++){
						String conf = confList.get(i);
						sb.append("<record id='"+(i+1)+"'>");
						sb.append("<s_id>"+(i+1)+"</s_id >");
						if(conf!=null && conf.contains("<zdz>pic</zdz>")){
							String regex = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b" ;
							if(!"127.0.0.1".equals(serviceIp)){
								conf=conf.replaceAll(regex, serviceIp);
							}
							conf=conf.replaceAll("</ip><port>", "?uuid="+serviceId+"</ip><port>");
							sb.append(conf);
						}else{
							sb.append(conf);
						}
						sb.append("</record>");
					}
				sb.append("</spzd_server>");
//-------------------------spzd_server----------------------------------------------------------------------end			
				
				
				
				
				
				StringBuilder radioItems=new StringBuilder();
//-------------------------interval------------------------------------------------------------------------begin			
				sb.append("<interval>");
					List<ServiceItemRadio> itemsAll = Dao.getItemsByServiceId(serviceId);//通过serviceId拿出所有检测项
					for(int i=0;itemsAll!=null && i<itemsAll.size();i++){
						ServiceItemRadio item = itemsAll.get(i);
						sb.append("<record id='"+(i+1)+"'>");
						sb.append("<zdz>"+item.getItem()+"</zdz>");
						sb.append("<value>0.1</value>");
						sb.append("<threshold>0</threshold>");
						sb.append("</record>");
						
						radioItems.append(item.getItem());//提前拿出这个： <detectitems>zd01,zd02</detectitems>//检测项目 ，以便于给下面那个使用
						if((i+1)!=itemsAll.size()){
							radioItems.append(",");
						}
					}
				sb.append("</interval>");
//-------------------------interval------------------------------------------------------------------------end			
				
				
				
//-------------------------spzd_camera------------------------------------------------------------------------begin			
				int interval=Dao.getScanningInterval();//间隔小时数，一般为2小时
				TimeInterval ti=Dao.getTimeInterval();//开始，结束时间
				String starttime = ti.getStarttime();
				String endtime = ti.getEndtime();
				String realStartTime=getRealStartTime(starttime,endtime,interval);//8:00,10:00,12:00,14:00,16:00,18:00
				
				sb.append("<spzd_camera>");
					List<EquipmentRadio> equipsList=Dao.getEquipsByServiceId(serviceId);//能过serviceId拿出所有设备
					for(int i=0;equipsList!=null && i<equipsList.size();i++){
						EquipmentRadio equip = equipsList.get(i);
						sb.append("<record id='"+(i+1)+"'>");
						sb.append("<uuid>"+equip.getUuid()+"</uuid>");
						sb.append("<cameid>"+equip.getCameid()+"</cameid>");
						sb.append("<starttime>"+realStartTime+"</starttime>");
						sb.append("<endtime>"+(endtime+":00")+"</endtime>");
						sb.append("<source>0</source>");
						sb.append("<loop>1</loop>");
						sb.append("<detectitems>"+radioItems.toString()+"</detectitems>");//<detectitems>zd01,zd02</detectitems>
						sb.append("<szusertype>"+equip.getSzusertype()+"</szusertype>");
						sb.append("</record>");
					}
				sb.append("</spzd_camera>");
//-------------------------spzd_camera------------------------------------------------------------------------end			
				
			sb.append("</body>");
		sb.append("</root>");
		
		return sb.toString();
	}
	
	/**服务器返回格式如下
  <?xml version="1.0" encoding="utf-8"?>
  <root>
   <head>
     <success>success</success>  //success 成功 fail失败，其他数值为错误代码
     <exception>XXXX</exception> //当返回结果不为fail时，将失败原因具体描述放在此处
   </head>
   <body>
   
   <spzd_server>//视频质量检测配置信息表
      <record id='1'>
        <s_id>1</s_id >
 <zdz>jqz</zdz> //视频监控系统鉴权服务器字典值
 <ip>192.168.3.203</ip>//视频监控系统鉴权服务器IP
 <port>8080</port>//视频监控系统鉴权服务器端口
 <username>admin</username>//视频监控系统鉴权服务器账号
 <userpwd>admin</userpwd>//视频监控系统鉴权服务器密码
      </record>
      <record id='2'>
       <s_id>2</s_id >
 <zdz>pic</zdz> //视频质量服务截图路径字典值
 <ip>待定</ip>//视频质量服务截图路径ip
 <port></port>
 <username></username>
 <userpwd></userpwd>
      </record>
    </spzd_server>
	    
	    
  <interval>//视频检测单元报警值间隔上报设置
      <record id='1'>
        <zdz>zd01</zdz>//检测类型字典值
        <value>0.1</value>
        <threshold>0</threshold>
      </record>
      <record id='2'>
        <zdz>zd16</zdz>//检测类型字典值
        <value>0.1</value>
        <threshold>0</threshold>
      </record>
    </interval>
	    
	    
	    <spzd_camera>//视频质量检测单元检测的摄像机信息表
	      <record id='1'>  //第一条任务记录
	  <uuid>设备ID</uuid>
	 <cameid>摄像机逻辑编号</cameid>//摄像机逻辑编号
	 <starttime>8:00,10:00,12:00,14:00,16:00,18:00</starttime>//开始检测时间
	 <endtime>18:00</endtime>//结束检测时间
	 <source>0</source>//视频源类型
	 <loop>1</loop>//最大循环次数，历史录像检测无效
	 <detectitems>zd01,zd02</detectitems>//检测项目
	 <szusertype>10</szusertype>//所属系统标识
	      </record>
	      <record id='2'>//第二条任务记录
	       ………………
	      </record>
	    </spzd_camera>
	    
	    
	  </body>
	</root>
	 */
	
	
	
	//<starttime>8:00,10:00,12:00,14:00,16:00,18:00</starttime>
		private static String getRealStartTime(String starttime, String endtime, int interval) {
			if(interval>24){
				interval=23;//间隔最多不能超过12，超过12，就是个圈啦
			}
			
			int start=8;
			try {start=Integer.valueOf(starttime);} catch (Exception e) {start=8;}
			
			int end=18;
			try {end=Integer.valueOf(endtime);} catch (Exception e) {end=18;}
			
			StringBuilder sb=new StringBuilder();
			
			if(start>end){
				start=start ^ end;
				end=start ^ end;
				start=start ^ end;
			}
			
			if(start<end){//当开始时间小于结束时间		8:00-18:00
				for(int i=start;i<=end;i+=interval){
					sb.append(i+":00,");
				}
			}
			
			if(start==end){
				for(int i=0;i<24;i+=interval){
					sb.append(i+":00,");
				}
			}
			
			String realStartTime="";
			String oldstartime = sb.toString();
			if(oldstartime!=null && oldstartime.contains(",")){
				realStartTime=oldstartime.substring(0, oldstartime.lastIndexOf(","));
			}
			
			return realStartTime;
		}
		
		public static void main2(String[] args) {
			
			String allXML = getInstance().getXmlByEquipId("4d0124d71a1640a98cb5915599fb164a");
		
			/*
			//getRealStartTime("8","18",5);
			RadioDao Dao = getRadioDao();
			StringBuilder sb=new StringBuilder();
			List<ServiceItemRadio> itemsAll = Dao.getItemsByServiceId("12345");//通过serviceId拿出所有检测项
			for(int i=0;itemsAll!=null && i<itemsAll.size();i++){
				ServiceItemRadio item = itemsAll.get(i);
				sb.append("<record id='"+(i+1)+"'>");
				sb.append("<zdz>"+item.getItem()+"</zdz>");
				sb.append("<value>0</value >");
				sb.append("</record>");
			}
			System.err.println(sb.toString());
			*/
			System.err.println(allXML);
		}
		
		public static void main(String[] args) {
			String serviceIp="192.168.8.88";
			String serviceId="4d0124d71a1640a98cb5915599fb164a";
			
			String conf="<zdz>pic</zdz><ip>http://192.168.3.243:80/videoQlty</ip><port></port><username></username><userpwd></userpwd>";
			
			String regex = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b" ;
			if(conf.contains("<zdz>pic</zdz>")){
				System.out.println("truee");;
			}
			conf=conf.replaceAll(regex, serviceIp);
			conf=conf.replaceAll("</ip><port>", "?uuid="+serviceId+"</ip><port>");
			System.out.println(conf);
		}
}
