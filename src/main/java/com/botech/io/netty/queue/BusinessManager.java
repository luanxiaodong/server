package com.botech.io.netty.queue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;
import com.botech.io.netty.business.Business;
import com.botech.io.netty.conf.ServerConfig;
import com.botech.io.netty.conf.ServerConfigFactory;
import com.botech.io.netty.sender.MessageUtil;
import com.botech.io.netty.service.MsgHandleService;
import com.botech.io.netty.service.MsgHandleServiceFactory;
import com.botech.wgxt.ywzx.serviceall.dao.ServiceDao;
import com.botech.wgxt.ywzx.serviceall.manager.ServiceManager;

import io.netty.channel.ChannelHandlerContext;

/**
 * 【整体处理业务管理】
* @ClassName: BusinessManager 
* @Description:
* @author luanxd
* @date 2015-7-28 下午4:35:47 
*
 */
public class BusinessManager {
	private static final Logger log = LoggerFactory.getLogger(BusinessManager.class);
	
	private BusinessManager(){}
	public static BusinessManager instance=new BusinessManager();
	public static BusinessManager getInstance(){
		return instance;
	}
	
	//重复登录次数限制
	private Map<String,Integer> map=new ConcurrentHashMap<String, Integer>();
	
	//处理业务队列
	public void doBusiness(QueueTask task){
		try {
			BcPackage msg = task.getMsg();
			ChannelHandlerContext ctx = task.getCtx();
			
			//客户端所有业务请求入口
			log.warn("客户端业务请求type="+msg.getBctype()+",message="+msg.getBcmessage());
			
			Business business = ServerConfigFactory.getInstance().getBusinessMap().get(msg.getBctype());
			if(null!=business){
				BcPackage process = business.process(msg,ctx);
				if(null!=process){
					ctx.writeAndFlush(process);
					log.warn("服务器业务返回type="+process.getBctype()+",message="+process.getBcmessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("BusinessManager doBusiness:",e);
		}
	}
	
	//连接数集合业务
	private MsgHandleService service = MsgHandleServiceFactory.getInstance().getService();
	
	
	//拿配置
	private ServerConfig config = ServerConfigFactory.getInstance().getConfig();
	
	//处理登录队列
	public void doLogin(QueueTask task){
		try {
			BcPackage pack = task.getMsg();
			ChannelHandlerContext ctx = task.getCtx();
			
			/*
			//获取客户端IP址--官方的说法，这个取得的IP可能不准确，可能为空，有时候客户端那后是经过好多层出来所以就算取到也不一定正确，所以要取IP地址需要客户端传IP
			String ipAddress="";
			InetSocketAddress remoteAddress=(InetSocketAddress)ctx.channel().remoteAddress();
			if(remoteAddress!=null){
				ipAddress=remoteAddress.getAddress().getHostAddress();
//				System.out.println(ipAddress);
			}
			*/
			
			//网管系统netty客户端类型:wgxtnettyclient   ，netty服务器客户端类型:nettyserverclient:netty   总集合。普通终端可不添
			String clusteredid = pack.getClusteredid();
			
			//如果是:0 客户端验证请求
			if(null!=pack && "0".equals(pack.getBctype())){
				
				String clientId = pack.getBcmessage();
				
				//终端ID合法 （在0-50之间)
				if(clientId!=null &&
						clientId.length()>0 
						&& clientId.length()<150){
					
					
					//日志--登录
					log.warn("客户端请求登录clientId="+clientId+",type="+pack.getBctype());
					
					
//--------------------------------------------------------------ip validate------------------------------begin	
			    if("true".equals(config.getOpenIpValidate())){
					String ip="127.0.0.1";
					if(clientId.contains(":")){
						String[] split = clientId.split("\\:");
						if(split.length>=2){
							clientId=split[0];
							ip=split[1];
						}
						ServiceDao dao = ServiceManager.getInstance().getDao();
						if(dao.validateServiceIdAndIp(clientId, ip)){//---------if--success----
							//验证成功，什么都不干，继续往下面走,Then look at:ChannelHandlerContext oldCtx = service.getCtxByClientId(clientId);
						}else{
							//终端ID与IP地址不合法，应该是这样的结构     clientId:IP address
							//登录失败
							ctx.writeAndFlush(BcPackage.newBuilder()
									.setBctype("1")
									.setClusteredid("Validate clientId and ip address error,it's not exists!")
									.setBcmessage("1")//0.成功，1失败
									.build());
							ctx.close();
						}
					}else{
						//终端ID与IP地址不合法，应该是这样的结构     clientId:IP address
						//登录失败
						ctx.writeAndFlush(BcPackage.newBuilder()
								.setBctype("1")
								.setClusteredid("Validate clientId and ip address error,it's must  clientId:ipaddress   !")
								.setBcmessage("1")//0.成功，1失败
								.build());
						ctx.close();
					}
			    }else if(clientId.contains(":")){
			    	clientId=clientId.split("\\:")[0];
			    }
//---------------------------------------------------------------ip validate------------------------------end	
					
					
					ChannelHandlerContext oldCtx = service.getCtxByClientId(clientId);
					
					if(null!=oldCtx){
						//登录大于5次就踢出本地集合，换最新的ctx
						Integer count = map.get(clientId);
						if(null==count){
							map.put(clientId, 1);
						}else{
							map.put(clientId, ++count);
						}
						if(count!=null && count>5){
							map.put(clientId, 1);
							MessageUtil.getInstance().kickOutByClientId(clientId);
							oldCtx = service.getCtxByClientId(clientId);
							if(oldCtx!=null){
								oldCtx=null;
							}
						}
					}
					
					if(null==oldCtx){//并且不是重复登录
						
						//将登录用户(客户端)加入路由表
						service.putByClientIdAndCtx(clientId, ctx);
						
						//日志--登录--加入集合与通道组
						log.warn("服务器新加入集合与通道组clientId="+clientId+",服务器现有:"+service.getCtxMap().size()+"客户端");
						
						if("true".equals(config.getEnablelogservice())){
							String loginfo="刚登录的客户端ID是："+clientId+",服务器现有:"+service.getCtxMap().size()+"客户端";
							log.error(loginfo);
							System.out.println(loginfo);
						}
						//登录客户端的分类:网管系统netty客户端类型:wgxtnettyclient   ，netty服务器客户端类型:nettyserverclient:netty   总集合。普通终端可不添。
						if(clusteredid!=null && clusteredid.length()>1){
							Map<String, Set<String>> typeMapSet = service.getTypeMapSet();
							Set<String> set = typeMapSet.get(clusteredid);
							if(set==null){
								set=new HashSet<String>();
							}
							set.add(clientId);
							typeMapSet.put(clusteredid, set);
						}
						
						
						//登录成功
						BcPackage ackSucess = BcPackage.newBuilder()
											.setBctype("1")
											.setClusteredid(config.getClusteredid())
											.setBcmessage("0").build();
						ctx.writeAndFlush(ackSucess);//0.成功，1失败
						
						//日志--登录--成功
						log.warn("服务器成功应答clientId="+clientId+",type="+ackSucess.getBctype()+",bcmessage="+ackSucess.getBcmessage()+",clusterid="+ackSucess.getClusteredid());
						
						//刷新用户列表
						MessageUtil.getInstance().refreshAllClientList();
						ctx.fireChannelRead(pack);
						
					}else{
						
						//重复登录返回 值:4
						BcPackageBuilder.BcPackage bcPackage = BcPackageBuilder.BcPackage.newBuilder()
																				.setBctype("4").build();
						ctx.writeAndFlush(bcPackage);
						
						//日志--登录--重复登录
						log.warn("重复登录clientId="+clientId+",type="+bcPackage.getBctype());
						
						ctx.close();
					}
				}else{
					//终端ID不合法
					//登录失败
					ctx.writeAndFlush(BcPackage.newBuilder()
							.setBctype("1")
							.setClusteredid("ClientId can not null!")
							.setBcmessage("1")//0.成功，1失败
							.build());
					
					
					//日志--登录--ID验证失败
					log.warn("服务器clientId验证失败clientId="+clientId);
					
					
					ctx.close();
				}
				
				
			}else if(null!=pack && "7".equals(pack.getBctype())){//7．	客户端退出
				ctx.close();
			}else{
				//权限认证（未写）
				ctx.fireChannelRead(pack);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("BusinessManager doLogin:",e);
		}
	}
	
	
	//处理登出队列
	public void doLogOut(ChannelHandlerContext ctx){
		service.removeByCtx(ctx);//清除缓存
	}
	
}
