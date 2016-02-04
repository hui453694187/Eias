package com.yunfang.eias.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**   
 *    
 * 项目名称：外业采集项目   
 * 类名称：LoginEasyDto   
 * 类描述：版本数据对象
 * 创建人：贺隽
 * 创建时间：2016-1-18
 * @version 1.0.0.1
 */ 
public class LoginEasyDto {
	// {{相关的属性
	
	/**
	 * 版本号
	 * */
	public String VersionCode;

	/**
	 * 版本名称
	 * */
	public String VersionName;

	/**
	 * 最后更新日期
	 */
	public String LastUpdateTime;
	
	/**
	 * 更新内容
	 */
	public String UpdateContent;
	
	/**
	 * 待领取常规任务数量
	 * */
	public String NormalTodoTask;

	/**
	 * 待领取紧急任务数量
	 * 
	 * */
	public String NormalDoingTask;

	/**
	 * 待提交常规任务数量
	 * 
	 * */
	public String UrgentTodoTask;
	
	/**
	 * 待提交紧急任务数量
	 * 
	 * */
	public String UrgentlDoingTask;

	// }}

	//{{ 构造函数
	
	/**
	 * 构造函数
	 */
	public LoginEasyDto(){
	}
		
	//}}
	
	//{{构造对象
	
	/**
	 * 构建对象
	 * @param obj
	 * @throws JSONException 
	 */
	public LoginEasyDto(JSONObject obj) throws JSONException{	
		VersionCode = obj.optString("VersionCode");
		VersionName = obj.optString("VersionName");
		LastUpdateTime = obj.optString("LastUpdateTime");
		UpdateContent = obj.optString("UpdateContent");
		NormalTodoTask = obj.optString("NormalTodoTask");
		NormalDoingTask = obj.optString("NormalDoingTask");
		UrgentTodoTask = obj.optString("UrgentTodoTask");
		UrgentlDoingTask = obj.optString("UrgentlDoingTask");
	}

	//}}
}
