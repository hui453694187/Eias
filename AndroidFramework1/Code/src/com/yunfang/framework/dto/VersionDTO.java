package com.yunfang.framework.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**   
 *    
 * 项目名称：外业采集项目   
 * 类名称：VersionDTO   
 * 类描述：版本数据对象
 * 创建人：贺隽
 * 创建时间：2014-7-25
 * @version 1.0.0.1
 */ 
public class VersionDTO {
	// {{相关的属性
	/**
	 * 应用名称
	 * */
	public String AppName;

	/**
	 * 应用程序名称
	 * */
	public String ApkName;

	/**
	 * 版本号
	 * */
	public String VersionCode;

	/**
	 * 版本名称
	 * 
	 * */
	public String VersionName;

	/**
	 * 当前版本更新内容
	 * 
	 * */
	public String UpdateContent;

	/**
	 * 最后更新时间
	 * 
	 * */
	public String LastUpdateTime;

	// }}

	//{{ 构造函数

	/**
	 * 构造函数
	 */
	public VersionDTO(){
	}

	//}}

	//{{构造对象

	/**
	 * 构建对象
	 * @param obj
	 * @throws JSONException 
	 */
	public VersionDTO(JSONObject obj) throws JSONException{	
		AppName = obj.optString("AppName");
		ApkName = obj.optString("ApkName");
		VersionCode = obj.optString("VersionCode");
		VersionName = obj.optString("VersionName");
		UpdateContent = obj.optString("UpdateContent");
		LastUpdateTime = obj.optString("LastUpdateTime");
	}

	//}}
}
