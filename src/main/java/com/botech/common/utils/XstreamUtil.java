package com.botech.common.utils;

import java.util.List;

import com.botech.page.bean.PageBean;
import com.botech.wgxt.ywzx.redis.bean.Redis;
import com.thoughtworks.xstream.XStream;

/**
 * 【将java bean转换成XML的工具，全局用这同一个】
* @ClassName: XstreamUtil 
* @author luanxd
* @date 2015-8-14 上午9:13:22 
*
 */
public class XstreamUtil {
//	private static final Logger log = LoggerFactory.getLogger(XstreamUtil.class);
	private static XStream stream = new XStream();
	static{
//		stream.alias("BeanInfo", BeanInfo.class);
		
		//分页业务相关
		stream.alias("PageBean", PageBean.class);
		stream.alias("Redis", Redis.class);
		stream.alias("List", List.class);
		
		//采集业务相关
		/*
		stream.alias("message", Message.class);
		stream.alias("equipment", Equipment.class);
		stream.alias("Equipmentstatus", Equipmentstatus.class);
		*/
		
		
	}
	public static XStream getStream() {
		return stream;
	}
	/*
	//xstream 高级使用示例如下
	 
	@XStreamAlias("class")
	public class Classes {
		@XStreamAsAttribute
	    @XStreamAlias("名称")
	    private String name;
	    
	    @XStreamOmitField
    	private int number;
    	
    	
    	@XStreamImplicit(itemFieldName = "Students")
    	private List<Student> students;
    	
    	
	    @SuppressWarnings("unused")
	    @XStreamConverter(SingleValueCalendarConverter.class)
	    private Calendar created = new GregorianCalendar();
	}
	
	//SingleValueCalendarConverter类是自定义的，如下：
	public class SingleValueCalendarConverter implements Converter {
     public void marshal(Object source, HierarchicalStreamWriter writer,
                MarshallingContext context) {
            Calendar calendar = (Calendar) source;
            writer.setValue(String.valueOf(calendar.getTime().getTime()));
        }
 
        public Object unmarshal(HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(new Date(Long.parseLong(reader.getValue())));
            return calendar;
        }
 
        @SuppressWarnings("unchecked")
        public boolean canConvert(Class type) {
            return type.equals(GregorianCalendar.class);
        }
	 }
	 */
}
