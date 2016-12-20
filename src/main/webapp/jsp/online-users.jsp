<%@page import="com.botech.io.netty.sender.MessageUtil"%>
<%@page import="io.netty.channel.ChannelHandlerContext"%>
<%@page import="com.botech.io.netty.bean.BcPackageBuilder"%>
<%@page import="com.botech.io.netty.page.OnlinePager"%>
<%@page import="com.botech.common.utils.WebUtils"%>
<%@page import="com.botech.page.bean.PageBean"%>
<%@page import="com.botech.io.netty.service.MsgHandleService"%>
<%@page import="com.botech.io.netty.service.MsgHandleServiceFactory"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
PageBean<Map.Entry<String, ChannelHandlerContext>> pb=WebUtils.request2Bean(PageBean.class, request);
MsgHandleService service=MsgHandleServiceFactory.getInstance().getService();
  	
  	if("sendMessage".equals(pb.getCmd())){
  		String msg=request.getParameter("msg");
  		String[] ids=request.getParameterValues("ids");

		MessageUtil.getInstance().send(
			BcPackageBuilder.BcPackage.newBuilder()
  			.setBctype("5")
  			.setClusteredid("server")
  			.setBcmessage(msg)
  			.build(), ids);
  		
 
  		response.sendRedirect(basePath+"jsp/online-users.jsp");
  	}
  	
  	
  	if("kickout".equals(pb.getCmd())){
  		String[] ids=request.getParameterValues("ids");
		for(int i=0;ids!=null && i<ids.length;i++){
  			MessageUtil.getInstance().kickOutByClientId(ids[i]);
  		}
  		
		MessageUtil.getInstance().refreshAllClientList();
		
		response.sendRedirect(basePath+"jsp/online-users.jsp");
  	}
  	
  	if("kickoutAll".equals(pb.getCmd())){
  		MessageUtil.getInstance().kickOutAll();
  		response.sendRedirect(basePath+"jsp/online-users.jsp");
  	}
  	
  	if("sendMessageAll".equals(pb.getCmd())){
  		String msg=request.getParameter("msgAll");
	    
	    MessageUtil.getInstance().sendAll(	
	    	BcPackageBuilder.BcPackage.newBuilder()
  			.setBctype("5")
  			.setClusteredid("server")
  			.setBcmessage(msg)
  			.build()
  			);
	    
	    response.sendRedirect(basePath+"jsp/online-users.jsp");
  	}
  	
  	
  	
  	pb.setPageSize(10);
  	if("search".equals(pb.getCmd())){
	}
  	pb=OnlinePager.getInstance().getUserList(pb);
  
  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'online-users.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<%=basePath%>css/lxd.css" />
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.11.3.min.js"></script>
	<script type="text/javascript">
		function search(currentPage){
			$("#myform").attr("action","<%=basePath%>jsp/online-users.jsp");
			$("#cmd").val("search");
			$("#currentPage").val(currentPage);
			
			$("#myform").submit();
		}
		
		function sendMessage(){
	   		
			var myallchecks=document.getElementsByName('ids');
			var j=0;
			for(var i=0;i<myallchecks.length;i++){
				if(myallchecks[i].checked){
					j++;
				}
			}
			if(j==0){
				alert("没有选择！");
				return ;
			}
			
			$("#myform").attr("action","<%=basePath%>jsp/online-users.jsp");
			$("#cmd").val("sendMessage");
			
	        
	   	 	$("#myform").submit();
		
			
	   	}
	   	
		function sendMessageAll(){
	   		
			
			$("#myform").attr("action","<%=basePath%>jsp/online-users.jsp");
			$("#cmd").val("sendMessageAll");
	        
	   	 	$("#myform").submit();
		
			
	   	}
	   	
	   	function kickOut(){
	   		var myallchecks=document.getElementsByName('ids');
			var j=0;
			for(var i=0;i<myallchecks.length;i++){
				if(myallchecks[i].checked){
					j++;
				}
			}
			if(j==0){
				alert("没有选择！");
				return ;
			}
			
			$("#myform").attr("action","<%=basePath%>jsp/online-users.jsp");
			$("#cmd").val("kickout");
			
	        
	   	 	$("#myform").submit();
	   	}
	   	
	   	function kickOutAll(){
	   		$("#myform").attr("action","<%=basePath%>jsp/online-users.jsp");
			$("#cmd").val("kickoutAll");
	   	 	$("#myform").submit();
	   	}
	   	
		function myCheckAll(){
			var mycheck = document.getElementById('checkAll');
			var myallchecks=document.getElementsByName('ids');
			for(var i=0;i<myallchecks.length;i++){
				myallchecks[i].checked=mycheck.checked;
			}
		}
	</script>
  </head>

  <body>
  <h1>服务器管理客户端在线列表</h1>
  <form action="" id="myform" method="post">
  		<input type="hidden" id="cmd" name="cmd" />
		<input type="hidden" id="id" name="id" value=""/>
		<input type="hidden" id="currentPage" name="currentPage" value="<%=pb.getCurrentPage()%>"/>
  
  
    <table class="table_border">
    <tr>
    	<td><input type="checkbox" id="checkAll" onclick="myCheckAll()"  /></td>
    	<td>管道ID</td>
    	<td>用户信息</td>
    </tr>
		<%
		for(int i=0;null!=pb.getList() && i<pb.getList().size();i++){
    	Map.Entry<String, ChannelHandlerContext> entry=pb.getList().get(i);
    	%>
    	<tr>
    		<td><input type="checkbox" value="<%=entry.getKey() %>" name="ids" /></td>
    		<td><%=entry.getKey() %></td>
    		<td><%=MsgHandleServiceFactory.getInstance().getService().getCtxMap().get(entry.getKey()) %></td>
    	</tr>
    <%} %>
    <%=WebUtils.pageRow(pb, "search", 3) %>
    </table>
    
    <button class="button" id="myButton" name="myButton" onclick="sendMessage();" >发送</button>
    <input type="text" id="msg" name="msg" />
    
    <br>
    <br>
    <button class="button" id="myButtonAll" name="myButtonAll" onclick="sendMessageAll();" >发送所有</button>
    <input type="text" id="msgAll" name="msgAll" />
    <br>
    <br>
    <button class="button" onclick="kickOut();" >踢出用户</button>
    <br>
    <br>
    <button class="button" onclick="kickOutAll();" >踢出全部</button>
    </form>
  </body>
</html>
