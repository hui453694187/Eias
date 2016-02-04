package com.yunfang.eias.dto;

import org.json.JSONObject;

/**
 * 
 * 项目名称：Estimate 
 * 类名称：DTOBase  
 * 类描述：基类
 * 创建人：贺隽 
 * 创建时间：2015-03-09
 * 
 * @version
 */
public class DTOBase {
	// {{ 相关的属性
	
	/**
	 * 成功标识
	 * */
	public String Flag;
	
	// }}
	
	//{{构造函数
	
	/**
	 * 无参构造，设置默认值
	 * */
	public DTOBase() {
		Flag = "";
	}
	
	/**
	 * 构造函数
	 * @param obj JsonObject对象
	 */
	public DTOBase(JSONObject obj){
		this.Flag = obj.optString("flag");
	}
	
	// }}
}
