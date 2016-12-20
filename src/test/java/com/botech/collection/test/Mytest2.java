package com.botech.collection.test;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Mytest2 {
	public static void main(String[] args) {
		Map<String,String> map=new ConcurrentHashMap<String,String>();
		
		map.put("1", "123");
		map.put("2", "456");
		map.put("3", "789");
		map.put("4", "444");
		
		System.out.println(map.size());
	
		boolean remove = map.entrySet().remove("4");
		System.out.println(remove);
		
		System.out.println(map.size());
		
		for(Entry<String,String> entry:map.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			if("444".equals(value)){
				map.remove(key);
			}
		}
		System.out.println(map.size());
	}
}
