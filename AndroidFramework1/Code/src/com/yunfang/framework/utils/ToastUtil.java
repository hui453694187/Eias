package com.yunfang.framework.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * @author gorson
 *
 */
public class ToastUtil {
	/**
	 * 根据内容显示
	 * @param context
	 * @param text
	 */
	public static void longShow(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/**
	 * 根据 res显示
	 * @param context
	 * @param resId
	 */
	public static void longShow(Context context ,int resId){
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 根据内容显示
	 * @param context
	 * @param text
	 */
	public static void shortShow(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 根据 res显示
	 * @param context
	 * @param resId
	 */
	public static void shortShow(Context context ,int resId){
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示提示信息
	 * @param context
	 * @param resId
	 * @param duration
	 */
	public static void show(Context context ,int resId,int duration){
		Toast.makeText(context, resId, duration).show();
	}
	
	/**
	 * 根据内容显示
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void show(Context context,String text,int duration){
		Toast.makeText(context, text, duration).show();
	}

}
