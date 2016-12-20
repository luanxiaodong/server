package com.botech.io.netty.conf;

import java.util.Set;
/**
 * 【服务器的配置】
* @ClassName: ServerConfig 
* @Description: TODO
* @author luanxd
* @date 2015-7-29 上午9:46:26 
*
 */
public class ServerConfig {
	private String ip;//服务器IP
	private int port;//服务器端口
	private String messageservice;//服务器存储客户端集合的服务
	private String clusteredid;//集群ID
	private String openIpValidate;//是否开启IP地址验证
	private Set<String> business;//总共业务数
	
	private String enablelogservice;//是否开启测试日志，如果客户端一连接就打印出客户端的连接总数，并且打印出客户端ID，退出也得打印出总连接数以及退出的客户端 -
	
	private String enableClusterClient;//是否启用集群客户端
	private String serverip;//客户端要连接中心服务器上的netty服务器,netty server ip.
	private int serverport;//端口号,center netty server port. 
	private long clientConnectInterval;//客户端连拉服务器间隔
	
	private String enableHeartBeat;//是否启用服务器心跳机制
	private long heartBeatTime;//服务器心跳间隔时间
	
	private String enableWebservice;//是否启webservice
	private String webserviceIp;//webservice ip
	private int webservicePort;//webservice port
	private String webserviceName;//webservice name,like:http://127.0.0.1:7499/webservice   , name=webservice
	
	public String getEnableWebservice() {
		return enableWebservice;
	}
	public void setEnableWebservice(String enableWebservice) {
		this.enableWebservice = enableWebservice;
	}
	public String getWebserviceIp() {
		return webserviceIp;
	}
	public void setWebserviceIp(String webserviceIp) {
		this.webserviceIp = webserviceIp;
	}
	public int getWebservicePort() {
		return webservicePort;
	}
	public void setWebservicePort(int webservicePort) {
		this.webservicePort = webservicePort;
	}
	public String getWebserviceName() {
		return webserviceName;
	}
	public void setWebserviceName(String webserviceName) {
		this.webserviceName = webserviceName;
	}
	public String getEnablelogservice() {
		return enablelogservice;
	}
	public void setEnablelogservice(String enablelogservice) {
		this.enablelogservice = enablelogservice;
	}
	public String getOpenIpValidate() {
		return openIpValidate;
	}
	public void setOpenIpValidate(String openIpValidate) {
		this.openIpValidate = openIpValidate;
	}
	public long getClientConnectInterval() {
		return clientConnectInterval;
	}
	public void setClientConnectInterval(long clientConnectInterval) {
		this.clientConnectInterval = clientConnectInterval;
	}
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public int getServerport() {
		return serverport;
	}
	public void setServerport(int serverport) {
		this.serverport = serverport;
	}
	public String getEnableClusterClient() {
		return enableClusterClient;
	}
	public void setEnableClusterClient(String enableClusterClient) {
		this.enableClusterClient = enableClusterClient;
	}
	public String getEnableHeartBeat() {
		return enableHeartBeat;
	}
	public void setEnableHeartBeat(String enableHeartBeat) {
		this.enableHeartBeat = enableHeartBeat;
	}
	public long getHeartBeatTime() {
		return heartBeatTime;
	}
	public void setHeartBeatTime(long heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}
	public String getClusteredid() {
		return clusteredid;
	}
	public void setClusteredid(String clusteredid) {
		this.clusteredid = clusteredid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getMessageservice() {
		return messageservice;
	}
	public void setMessageservice(String messageservice) {
		this.messageservice = messageservice;
	}
	public Set<String> getBusiness() {
		return business;
	}
	public void setBusiness(Set<String> business) {
		this.business = business;
	}
}
