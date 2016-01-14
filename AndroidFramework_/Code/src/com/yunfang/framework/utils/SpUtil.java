package com.yunfang.framework.utils;

import com.yunfang.framework.base.BaseApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * sharepreferenced 封装工具类
 * @author gorson
 *
 */
public class SpUtil {

	//================相关参数start======================//
	private static SpUtil spUtil;
	private static SharedPreferences mSharedPreferences;
	//================相关参数start======================//

	/**
	 * 获取一个SpUtil
	 * @param spName 存储节点的名称
	 * @return
	 */
	public static SpUtil getInstance(String spName){
		//if(spUtil==null){
		spUtil=new SpUtil();
		mSharedPreferences=  BaseApplication.getInstance().getApplicationContext()
				.getSharedPreferences(spName, Context.MODE_PRIVATE);
		//}

		return spUtil;	
	}

	/**
	 * 取得SharedPreferences的编辑器
	 * @return
	 */
	public Editor getEdit(){
		return mSharedPreferences.edit();
	}

	/**
	 * 向SharedPreferences插入Integer类型的数据并提交
	 * @param key
	 * @param defValue
	 * @return
	 */
	public boolean putInt(String key,int defValue){
		return getEdit().putInt(key, defValue).commit();
	}

	/**
	 * 向SharedPreferences插入String类型的数据并提交
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean putString(String key,String value){
		return getEdit().putString(key, value).commit();
	}

	/**
	 * 向SharedPreferences插入Boolean类型的数据并提交
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean putBoolean(String key,boolean value){
		return getEdit().putBoolean(key, value).commit();
	}

	/**
	 * 向SharedPreferences插入Float类型的数据并提交
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean putFloat(String key ,float value){
		return getEdit().putFloat(key, value).commit();
	}

	/**
	 * 向SharedPreferences插入Double类型的数据并提交
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean putDouble(String key,long value){
		return getEdit().putLong(key, value).commit();
	}

	/**
	 * 向SharedPreferences插入Integer类型的数据
	 * @param key
	 * @param defValue
	 * @return
	 */
	public int getInt(String key,int defValue){
		return mSharedPreferences.getInt(key, defValue);
	}

	/**
	 * 向SharedPreferences插入String类型的数据
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String getString(String key,String defValue){
		return mSharedPreferences.getString(key, defValue);
	}

	/**
	 * 向SharedPreferences插入Boolean类型的数据
	 * @param key
	 * @param defValue
	 * @return
	 */
	public boolean getBoolean(String key,boolean defValue){
		return mSharedPreferences.getBoolean(key, defValue);
	}

	/**
	 * 向SharedPreferences插入Long类型的数据
	 * @param key
	 * @param defValue
	 * @return
	 */
	public long getLong(String key,long defValue){
		return mSharedPreferences.getLong(key, defValue);
	}

	/**
	 * 向SharedPreferences插入Float类型的数据
	 * @param key
	 * @param defValue
	 * @return
	 */
	public float getFloat(String key,float defValue){
		return mSharedPreferences.getFloat(key, defValue);
	}


}
