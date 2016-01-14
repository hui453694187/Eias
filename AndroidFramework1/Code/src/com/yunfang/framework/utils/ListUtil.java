package com.yunfang.framework.utils;

import java.util.List;

/**
 * List的辅助类
 * @author gorson
 *
 */
public class ListUtil {
	/**
	 * 判断一个集合是否有值，首个对象如果为null也视为没有值
	 * @param list
	 * @return
	 */
	public static <T> Boolean hasData(List<T> list){
		Boolean result = false;
		
		if(list != null && list.size()>0 && list.get(0) != null){
			result = true;
		}
		
		return result;
	}
}
