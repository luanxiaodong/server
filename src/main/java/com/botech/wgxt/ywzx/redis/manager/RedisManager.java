package com.botech.wgxt.ywzx.redis.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.utils.ClassUtils;
import com.botech.wgxt.ywzx.redis.dao.RedisDao;

/**
 * 【】
* @ClassName: RedisManager 
* @author luanxd
* @date 2015-8-18 下午4:21:31 
*
 */
public class RedisManager {
	private static final Logger log = LoggerFactory.getLogger(RedisManager.class);

    //包装里面一个容器类来模似出管理类，做的这一切都是为了更好的解耦
    private static class RedisManagerContainer {
        private static RedisManager instance = new RedisManager();
    }
    public static RedisDao getRedisDao() {
        return RedisManagerContainer.instance.Dao;
    }

    /**
     * 返回一个单例的redis管理
     */
    public static RedisManager getInstance() {
        return RedisManagerContainer.instance;
    }
    private RedisDao Dao;

    /**
     * 构造管理类的时候去初始化DAO
     */
    public RedisManager() {
        // Load an  Dao.
        initDao();
    }

    /**
     * 初始化DAO
     */
    private void initDao() {

        String className = "com.botech.wgxt.ywzx.redis.dao.RedisDaoImpl";
        // Check if we need to reset the Dao class        
        if (Dao == null || !className.equals(Dao.getClass().getName())) {
	        try {
	            Class c = ClassUtils.forName(className);
	            Dao = (RedisDao) c.newInstance();
	        }
	        catch (Exception e) {
	            log.error("Error loading RedisDao Dao: " + className, e);
	        }
        }
    }

    //拿到dao
	public RedisDao getDao() {
		return Dao;
	}
	
	private static String redisStr="";
	
	public void reloadRedisStr(){//刷新缓存
		redisStr=Dao.getRedisStr();
	}
	
	//拿到redis信息
	public String getCacheRedisInfo(){
		/*
		if(!"".equals(redisStr)){
			return redisStr;
		}else{
		*/
			redisStr=Dao.getRedisStr();
			return redisStr;
		/*
		}
		*/
	}
	
	
	public static void main(String[] args) {//测试
		String cacheRedisInfo = getInstance().getCacheRedisInfo();
		System.out.println(cacheRedisInfo);
	}
	
}
