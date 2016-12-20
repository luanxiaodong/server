package com.botech.ui.servlet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.botech.io.netty.server.NettyServer;

public class InitialServlet extends HttpServlet {
	private static final long serialVersionUID = -46578054530521799L;
	private static ExecutorService pool = Executors.newFixedThreadPool(5);
	

	public void init() throws ServletException {
		NettyServer.main(new String[]{});
	}


	@Override
	public void destroy() {
		pool.shutdown();
		super.destroy();
	}
	
	

}
