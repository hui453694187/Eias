package com.yunfang.framework.model;

import org.json.JSONObject;

/**
 * 版本信息
 * @author 贺隽
 *
 */
public class VersionInfo {
	
	//{{ 相关的属性
	/**
	 * android端获取版本信息时间
	 */
	public String GetVersionLocalTime = "";
	
	/**
	 * android端最后更新时间
	 */
	public String UpdatedTime = "";
	
	/**
	 * android端版本信息
	 */
	public String LocalVersionDescription = "";
	
	/**
	 * android端包名
	 */
	public String LocalPackageName = "";
	
	/**
	 * android端版本名称
	 */
	public String LocalVersionName = "";
	
	/**
	 * android端版本编号
	 */
	public String LocalVersionCode ="";
	
	/**
	 * web端发布时间
	 */
	public String ServerReleasedTime = "";
	
	/**
	 * web端版本信息
	 */
	public String ServerVersionDescription = "";
	
	/**
	 * web端版本名称
	 */
	public String ServerVersionName = "";
	
	/**
	 * web端版本编号
	 */
	public String ServerVersionCode ="";
	//}}
	
	/**
	 * 无参数构造函数
	 */
	public VersionInfo(){
		
	}	
	
	/**
	 * 带参数构造函数
	 */
	public VersionInfo(String androidGetVersionDateTime,String androidUpdateDateTime,String androidVersionInfo,String androidVersionName,
			String androidVersionCode,String webReleaseDateTime,String webVersionInfo,String webVersionName,String webVersionCode){	
		this.GetVersionLocalTime = androidGetVersionDateTime;
		this.UpdatedTime = androidUpdateDateTime;
		this.LocalVersionDescription = androidVersionInfo;
		this.LocalVersionName = androidVersionName;
		this.LocalVersionCode = androidVersionCode;
		this.ServerReleasedTime = webReleaseDateTime;
		this.ServerVersionDescription = webVersionInfo;
		this.ServerVersionName = webVersionName;
		this.ServerVersionCode = webVersionCode;
	}
	
	/**
	 * 初始一个用户信息
	 * @param obj   UserInfo的JsonObject对象
	 */
	public VersionInfo(JSONObject obj){	
		this.GetVersionLocalTime = obj.optString("GetVersionLocalTime");
		this.UpdatedTime =  obj.optString("UpdatedTime");
		this.LocalVersionDescription = obj.optString("LocalVersionDescription");
		this.LocalVersionName = obj.optString("LocalVersionName");
		this.LocalVersionCode = obj.optString("LocalVersionCode");
		this.ServerReleasedTime = obj.optString("ServerReleasedTime");
		this.ServerVersionDescription = obj.optString("ServerVersionDescription");
		this.ServerVersionName = obj.optString("ServerVersionName");
		this.ServerVersionCode = obj.optString("ServerVersionCode");
	}
}
