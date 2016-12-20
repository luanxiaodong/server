package com.botech.collection.test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client {
	private String channelId;
	private String userId;
	private String userName;
	private String pwd;
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	static final BoundedBuffer bb = new BoundedBuffer();
	
	public static void main(String[] args) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<100;i++){
					try {
						bb.put(i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
		while(true){
			try {
				bb.take();
				//System.out.println(bb.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	 static class BoundedBuffer {
		   final Lock lock = new ReentrantLock();
		   final Condition notFull  = lock.newCondition(); 
		   final Condition notEmpty = lock.newCondition(); 

		   final Object[] items = new Object[10];
		   int putptr, takeptr, count;

		   public void put(Object x) throws InterruptedException {
		     lock.lock();
		     try {
		       while (count == items.length){
		    	   //System.out.println("放满啦!");
		    	   notFull.await();
		       }
		       items[putptr] = x;
		       if (++putptr == items.length) {
		    	   putptr = 0;
		       }
		       ++count;
		       System.out.println("put count:"+count);
		       notEmpty.signal();
		     } finally {
		       lock.unlock();
		     }
		   }

		   public Object take() throws InterruptedException {
		     lock.lock();
		     try {
		       while (count == 0){
		    	   //System.out.println("拿完啦!!");
		    	   notEmpty.await();
		       }
		       Object x = items[takeptr];
		       if (++takeptr == items.length) takeptr = 0;
		       System.out.println("take count:"+count);
		       --count;
		       notFull.signal();
		       return x;
		     } finally {
		       lock.unlock();
		     }
		   }
		 }
		 

}
