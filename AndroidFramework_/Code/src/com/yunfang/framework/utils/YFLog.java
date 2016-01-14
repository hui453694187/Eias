package com.yunfang.framework.utils;

import android.util.Log;
/**
 * debug调试类
 * @author gorson
 *
 */
public class YFLog {
	private static final String TAG="yunfang";
	
	private static boolean isDebug=true;
	
	/**
	 *  是否处于调试模式
	 */
	public static boolean isDebug(){
		return isDebug;
	}
	/**
	 *   设置处于调试模式
	 */
	public static void setDebug(boolean debug){
		isDebug=debug;
	}
	
	/**
	 * 打印debug模式下调试语句，用统一的tag
	 * @param msg
	 */
	public static void d(String msg){
		if(isDebug){
			Log.d(TAG, msg);
		}
	}
	
	/**
	 * 打印debug模式下调试语句，自定义tag
	 * @param msg
	 */
	public static void d(String  tag,String msg ){
		if(isDebug){
			Log.d(tag, msg);
		}
	}
	
	/**
	 * 打印error 模式下下调试语句,统一的tag
	 * @param msg
	 */
	public static void e(String msg){
		if(isDebug){
			Log.e(TAG, msg);
		}
	}
	
	/**
	 * 打印error 模式下下调试语句,自定义的tag
	 * @param msg
	 */
	public static void e(String tag,String msg){
		if(isDebug){
			Log.e(tag, msg);
		}
	}
	
}
