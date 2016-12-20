package com.botech.common.c3p0;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 【拿数据库连接、拿连接池、拿QueryRunner的工具类】
* @ClassName: JdbcUtils 
* @author luanxd
* @date 2015-8-14 上午9:59:56 
*
 */
public class JdbcUtils {
	private static final Logger log = LoggerFactory.getLogger(JdbcUtils.class);  
    
    private static DataSource ds = null;
    //在静态代码块中创建数据库连接池
    static{
        try{
            //通过读取C3P0的xml配置文件创建数据源，C3P0的xml配置文件c3p0-config.xml必须放在src目录下
           ds = new ComboPooledDataSource("oracle1");//使用C3P0的命名配置来创建数据源
        }catch (Exception e) {
        	log.error("",e);
            throw new ExceptionInInitializerError(e);
        }
    }
    
    //拿数据源
    public static DataSource getDataSource() {
        return ds;
    }
    
    //从数据源中获取数据库连接
    public static Connection getConnection() throws SQLException{
        return ds.getConnection();
    }
    
    //释放资源
    public static void close(ResultSet rs,Statement ps,Connection conn){
    	if(null!=rs){ try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
    	if(null!=ps){ try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
    	if(null!=conn){ try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }
    
    //初始化QueryRunner.
    private static QueryRunner qr=new QueryRunner(ds);
    //直接拿QueryRunner.
    public static QueryRunner getQueryRunner(){
    	return qr;
    }
    
    public static void main(String[] args) throws Exception {
    	/*
    	Connection conn = getConnection();
		System.out.println(conn);
		conn.close();
		*/
		QueryRunner qr = getQueryRunner();
		String query = qr.query("select sys_key from ywzx_sysparam where param_key=? ", new ResultSetHandler<String>(){
			@Override
			public String handle(ResultSet rs) throws SQLException {
				
				return rs.next()? rs.getString(1) : null;
			}},"1");
		
		System.out.println(query);
	}
    

    //批量语句更新
	public static boolean executeBatch(List<String> list){
		boolean flag = false;
		Connection con = null;
        Statement stmt=null;
        int i = 0;
        try{
        con = getConnection();
        con.setAutoCommit(false);
        stmt = con.createStatement();
        if(null!=list)
		for (String sql : list) {
			stmt.addBatch(sql);
			i++;
			if(i % 1000 == 0){
				stmt.executeBatch();
				stmt.clearBatch();
			}
		}
		stmt.executeBatch();
		stmt.clearBatch();
		flag = true;
        }catch (Exception e) {
        	flag = false;
        	try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        	e.printStackTrace();
			log.error("executeBatch", e);
		}finally{
			 try {
				con.commit();
				con.setAutoCommit(true);
				if(null != con){
					con.close();
				}
		     } catch (SQLException e) {
				e.printStackTrace();
		     }catch (Exception ignored){}	
		}
        
        return flag;
	}
 

}