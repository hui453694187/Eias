/************************************
 * 
 * Copyright 2011 u6 to MeYou
 * 
 * @Description:sqlite操作helper
 * @Date 2011-05-17 14:26
 * @Author linyg
 * @Version v2.0
 */
package com.yunfang.framework.db;

import java.util.ArrayList;

import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.utils.YFLog;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库帮助类
 * @author gorson
 * Updated by Gorson
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	//=================相关的属性start====================================//
 
	/**
	 * 数据库帮助类
	 * */
	private static SQLiteHelper db;

	/**
	 * 数据库脚本辅助类实例
	 */
	private static IDBArchitecture dbArchitecture;
	
	//=================相关的属性end======================================//

	/**
	 * 构造方法创建数据库，并取得SQLiteDatabase对象
	 * @param context
	 */
	private SQLiteHelper() {		
		super(BaseApplication.getInstance().getApplicationContext(), 
				dbArchitecture.getDBName(), 
				null, 
				dbArchitecture.getCurrentVersion());
		//this.getWritableDatabase();
	}
	
	/**
	 * 设置数据库脚本辅助类的实例
	 * @param entity
	 */
	public static void setIDBArchitecture(IDBArchitecture entity){
		dbArchitecture = entity;
	}
	
	/**
	 * 直接获取一个可写的数据库
	 * @return
	 */
	public static SQLiteDatabase getWritableDB(){
		if(db==null){
			db = new SQLiteHelper();
		}
		return db.getWritableDatabase();
	}
	
	/**
	 * 直接获取一个可读的数据库
	 * @return
	 */
	public static synchronized SQLiteDatabase getReadableDB(){
		if(db==null){
			db = new SQLiteHelper();
		}
		return db.getReadableDatabase();
	}
	
	/**
	 * 提供一个获取SQLiteHelper对象的方法
	 * @param context
	 * @return
	 */
	public static synchronized SQLiteHelper getInstance() {
		if (db == null) {
			db = new SQLiteHelper();
			db.getReadableDatabase();
		}
		return db;
	}

	/**
	 * 创建表
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			ArrayList<String> scripts = dbArchitecture.getGenTableScripts();
			if(scripts.size()>0){
				for (String sql : scripts) {
					db.execSQL(sql);
				}
			}
		} catch (Exception e) {	
			if(YFLog.isDebug()){
				YFLog.e("SQLiteHelper_onCreate",
						e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 更新表（升级版本）
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			ArrayList<String> scripts = dbArchitecture.getDBUpdateScripts(oldVersion, newVersion);
			if(scripts.size()>0){
				for (String sql : scripts) {
					db.execSQL(sql);
				}
			}
		} catch (Exception e) {	
			if(YFLog.isDebug()){
				YFLog.e("SQLiteHelper_onUpgrade",
						e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 更新表（降级版本）
	 * */
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	/**
	 * 关闭数据库
	 * */
	@Override
	public synchronized void close() {
		super.close();
		db = null;
	}
}
