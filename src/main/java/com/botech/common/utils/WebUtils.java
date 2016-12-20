package com.botech.common.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.page.bean.PageBean;



/**
 * 
 * @author luanxiaodong
 *
 */
public class WebUtils {
	private static final Logger log = LoggerFactory.getLogger(WebUtils.class);
	
	public static <T> T request2Bean(Class<T> clazz,HttpServletRequest request){
		T bean=null;
		Map map=request.getParameterMap();
		
		try {
			//for java.util.Date Type.
			ConvertUtils.register(new Converter(){
				
				public Object convert(Class type, Object value) {
					if(value==null || "".equals(value))
						return null;
					if(!(value instanceof String)){
						throw new RuntimeException("Supports only String type conversion!");
					}
					try {
						return new SimpleDateFormat("yyyy-MM-dd").parse((String) value);
					} catch (ParseException e) {
						log.error("",e);
						throw new RuntimeException(e);
					}
				}
				
			}, Date.class);
			
			//for java.lang.Integer Type.
			ConvertUtils.register(new Converter(){
				
				public Object convert(Class type, Object value) {
					if(value==null || "".equals(value))
						return 0;
					if(!(value instanceof String)){
						throw new RuntimeException("Supports only String type conversion!");
					}
					try {
						return Integer.parseInt((String) value);
					} catch (Exception e) {
						log.error("",e);
						//throw new RuntimeException(e);
						return 1;
					}
				}
				
			}, Integer.class);
			
			bean=clazz.newInstance();
			BeanUtils.populate(bean, map);
		} catch (Exception e) {
			log.error("",e);
			e.printStackTrace();
		}
		return bean;
	}


	
	public static String getMd5Value(String str){
		String result=null;
		if(null==str){
			return result;
		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(str.getBytes());
			result=toHex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}
	public static String getMd5Value(byte[] bytes){
		String result=null;
		if(null==bytes){
			return result;
		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(bytes);
			result=toHex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}
	private static String toHex(byte[] digest) {
		StringBuilder sBuilder=new StringBuilder();
		for(int i=0;i<digest.length;i++){
			int hi=digest[i] >> 4 & 0x0F;
			sBuilder.append(hi >= 10 ? (char)('a'+(hi-10)) : (char)('0'+hi));
			int lo=digest[i] & 0x0F;
			sBuilder.append(lo >= 10 ? (char)('a'+(lo-10)) : (char)('0'+lo));
		}
		return sBuilder.toString();
	}
	private static String toHex2(byte[] digest) {
		StringBuilder sBuilder=new StringBuilder();
		for(int i=0;i<digest.length;i++){
			sBuilder.append("0123456789abcdef".charAt(digest[i] >> 4 & 0x0F));
			sBuilder.append("0123456789abcdef".charAt(digest[i] & 0x0F));
		}
		return sBuilder.toString();
	}
	//get UUID
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * [the rows of pager.]
	 * @author luanxiaodong
	 * @param pb
	 * @param ajaxMethodName	the ajax method name,only one param.
	 * @param columns	the table columns.
	 * @return
	 */
	public static String pageRow(PageBean pb, String ajaxMethodName, int columns) {
		StringBuilder sb=new StringBuilder();
		if(pb.getTotalPage()>1){
			sb.append("<tr><td colspan='"+columns+"' style='text-align: center;'>"+"当前第");
			sb.append("<font color='red'>"+pb.getCurrentPage()+"</font>"+"页");
			sb.append("&nbsp;<a href='javascript:"+ajaxMethodName+"(1)'>"+"首页");
			sb.append("</a>&nbsp;<a href='javascript:"+ajaxMethodName+"("+pb.getPreviouspage()+")'>");
			sb.append("上一页");
			sb.append("</a>&nbsp;<a href='javascript:"+ajaxMethodName+"("+pb.getNextpage()+")'>");
			sb.append("下一页");
			sb.append("</a>&nbsp;<a href='javascript:"+ajaxMethodName+"("+pb.getTotalPage()+")'>");
			sb.append("末页"+"</a>&nbsp;");
			sb.append("总共"+"<font color='red'>"+pb.getTotalPage()+"</font>");
			sb.append("页"+"&nbsp;");
			sb.append("总共"+"<font color='red'>"+pb.getTotalSize()+"</font>");
			sb.append("条");
			sb.append("&nbsp;<input type='text' id='inputPageNum'style='height:20px;' class='pagerinput' maxlength='9' value='"+pb.getCurrentPage()+"'/>");
			sb.append("<input type='button' class='pagerbutton'  value='GO' onclick=\""+ajaxMethodName+"(document.getElementById('inputPageNum').value);\" /></td></tr>");
		}else if(pb.getTotalSize()==0)
			sb.append("<tr><td colspan='"+columns+"' style='text-align: center;'>"+"当前没有数据!"+"</td></tr>");
		return sb.toString();
	}
	public static String getCurrentTime() {
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		Date date = new Date();
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	public static String formatDateToString(Date date){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	
	public static String upperFirstName(String fildeName){
		return fildeName.substring(0,1).toUpperCase() + fildeName.substring(1); 
	}
}
