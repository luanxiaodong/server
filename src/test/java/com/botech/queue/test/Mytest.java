package com.botech.queue.test;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Mytest {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(4);
		
		final BlockingQueue<String> queue=new ArrayBlockingQueue<String>(10);
		
		for(int i=0;i<4;i++){
			pool.execute(new Runnable(){
				@Override
				public void run() {
					while(true)
					try {
						String take = queue.take();
						System.out.println(take);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			});
		}
		
		for(int i=0;i<1000;i++){
			try {
				queue.put(i+"");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		pool.shutdown();
	}
}
