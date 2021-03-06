package com.skstravel.common.utils;

import java.util.UUID;

public class UUIDUtils {
	/**
	 * 随机生成id
	 * @return
	 */
	public static String getId(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	
	/**
	 * 随机生成一个令牌
	 * @return
	 */
	public static String getCode(){
		return getId();
	}
	
	
	public static void main(String[] args) {
		System.out.println(getId()+getId());
	}
}
