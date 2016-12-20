package com.botech.wgxt.ywzx.redis.dao;

import java.util.List;

import com.botech.wgxt.ywzx.redis.bean.Redis;

/**
 * 【拿redis信息】
* @ClassName: RedisDao 
* @author luanxd
* @date 2015-8-18 下午3:54:44 
*
 */
public interface RedisDao {
	
	//拿redis的xml信息
	List<Redis> getRedisInfo();
	String getRedisStr();
}
