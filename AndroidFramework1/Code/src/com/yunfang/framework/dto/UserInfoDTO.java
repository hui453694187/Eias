package com.yunfang.framework.dto;

import org.json.JSONObject;

/**   
 *    
 * 项目名称：外业采集项目   
 * 类名称：UserInfoDTO
 * 类描述：用户信息DTO,用于与服务器进行转换。
 * 创建人：陈惠森
 * 创建时间：2014-7-22 
 * @version 1.0.0.1
 */ 
public class UserInfoDTO {
	// {{ 相关的属性

	/**
	 * 唯一标示
	 */
	public Integer ID;

	/**
	 * 用户账号信息
	 */
	public String UserName;

	/**
	 * 用户账号信息
	 */
	public String UserAccount;

	/**
	 * 用户手机
	 */
	public String UserMobile;

	/**
	 * 用户Token
	 */
	public String Token;

	/**
	 * 创建时间
	 */
	public String CreateTime;

	/**
	 * 最后登录时间
	 */
	public String LastLoginTime;

	/**
	 * 登录次数
	 */
	public Integer LoginTimes;

	/** 
	 * 用户类型
	 */
	public Integer UserType;

	/**
	 * 公司ID
	 */
	public Integer CompanyID;
	
	/**
	 * 用户在CRM的所属部门编号
	 */
	public long CrmDepId;

	/**
	 * 用户账号信息
	 */
	public String CompanyName;
	/**
	 * 有效日期开始
	 */
	public String ValidityDateForm;
	/**
	 * 有效日期结束
	 */
	public String ValidityDateTo;

	/**
	 * 用户账号信息
	 */
	public String UserVersion;

	/**
	 * 图片路径
	 */
	public String ImagePath;

	/**
	 * 用户账号信息
	 */
	public String ImageData;

	/**
	 * 用户密码
	 */
	public String UserPwd;

	/**
	 * 用户旧密码
	 */
	public String UserPastPwd;

	/** 资源库服务器地址 */
	public String ResourceLibraryDomainName;
	//}}

	//{{ 构造函数
	/**
	 * 构造函数
	 * @param obj   UserInfo的JsonObject对象
	 */
	public UserInfoDTO(JSONObject obj){
		this.UserAccount = obj.optString("UserAccount");
		this.UserName =  obj.optString("UserName");
		this.UserMobile = obj.optString("UserMobile");
		this.CompanyName = obj.optString("CompanyName");
		this.CompanyID=obj.optInt("CompanyID");
		this.UserType = obj.optInt("UserType");
		this.Token = obj.optString("Token");
		this.ImageData = obj.optString("ImageData");
		this.ImagePath=obj.optString("ImagePath");
		this.UserVersion = obj.optString("UserVersion");
		this.ID = obj.optInt("ID");
		this.ResourceLibraryDomainName = obj.optString("ResourceLibraryDomainName");
		this.CrmDepId = obj.optLong("CrmDepId");
		
	}

	/**
	 * 构造函数
	 */
	public UserInfoDTO(){
		this.UserAccount = "";
		this.UserName = "";
		this.UserMobile = "";
		this.CompanyName = "";
		this.CompanyID = 0;
		this.UserType = 0;
		this.Token = "";
		this.ImageData = "";
		this.ImagePath="";
		this.UserVersion = "";
		this.ID = 0;
		this.LoginTimes = 0;
		this.ResourceLibraryDomainName = "";
		this.CrmDepId = -1;
	}
	//}}
}
