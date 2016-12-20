package com.botech.wgxt.ywzx.serviceall.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.common.utils.ClassUtils;
import com.botech.wgxt.ywzx.serviceall.dao.ServiceDao;

/**
 * 【】
* @ClassName: ServiceManager 
* @author luanxd
* @date 2015-8-18 下午4:21:31 
*
 */
public class ServiceManager {
	private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    //包装里面一个容器类来模似出管理类，做的这一切都是为了更好的解耦
    private static class ServiceManagerContainer {
        private static ServiceManager instance = new ServiceManager();
    }
    public static ServiceDao getServiceDao() {
        return ServiceManagerContainer.instance.Dao;
    }

    /**
     * 返回一个单例的管理
     */
    public static ServiceManager getInstance() {
        return ServiceManagerContainer.instance;
    }
    private ServiceDao Dao;

    /**
     * 构造管理类的时候去初始化DAO
     */
    public ServiceManager() {
        // Load an  Dao.
        initDao();
    }

    /**
     * 初始化DAO
     */
    private void initDao() {

        String className = "com.botech.wgxt.ywzx.serviceall.dao.ServiceDaoImpl";
        // Check if we need to reset the Dao class        
        if (Dao == null || !className.equals(Dao.getClass().getName())) {
	        try {
	            Class c = ClassUtils.forName(className);
	            Dao = (ServiceDao) c.newInstance();
	        }
	        catch (Exception e) {
	            log.error("Error loading ServiceDao Dao: " + className, e);
	        }
        }
    }

    //拿到dao
	public ServiceDao getDao() {
		return Dao;
	}
	
}
