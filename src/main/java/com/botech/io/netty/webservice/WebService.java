package com.botech.io.netty.webservice;

import javax.jws.WebMethod;

@javax.jws.WebService
public interface WebService {
	
	@WebMethod
	String process(String xmlStr);
}
