package com.botech.collection.test;

public class TwoTest {

	/** 【】
	 * @Title: main 
	 * @param @param args 
	 * @return void    返回类型 
	 * @throws 
	 */
	public static void main(String[] args) {
		int i=100;
		int j=999;
		
		i=i^j;
		j=i^j;
		i=j^i;
		
		System.out.println(i);
		System.out.println(j);
	}

}
