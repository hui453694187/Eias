package com.yunfang.framework.db;

import java.util.ArrayList;

/**
 * 数据库框架脚本类，用于数据库表的结构定义与修改
 * @author gorson
 *
 */
public interface IDBArchitecture {
	/**
	 * 获取数据库文件名称
	 * @return
	 */
	String getDBName();
	
	/**
	 * 获取当前版本号
	 * @return
	 */
	int getCurrentVersion();
	
	/**
	 * 获取数据库中创建所有表的SQL脚本
	 * @return
	 */
	ArrayList<String> getGenTableScripts();
	
	/**
	 * 获取数据库中对各版本间升级的SQL脚本
	 * @return
	 */
	ArrayList<String> getDBUpdateScripts(int oldVersion,int newVersion);
}
