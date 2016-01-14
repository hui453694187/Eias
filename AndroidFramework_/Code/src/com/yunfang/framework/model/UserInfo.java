package com.yunfang.framework.model;

import org.json.JSONObject;

import com.yunfang.framework.dto.UserInfoDTO;

public class UserInfo {
	//{{ 相关的属性
	/**
	 * 用户ID
	 */
	public String ID = "";

	/**
	 * 用户账号信息
	 */
	public String Account = "";

	/**
	 * 用户密码
	 */
	public String Password = "";

	/**
	 * 用户名称
	 */
	public String Name = "";

	/**
	 * 用户联系方式
	 */
	public String Mobile ="";

	/**
	 * 公司名称
	 */
	public String CompanyName = "";

	/**
	 * 公司ID
	 */
	public int CompanyID = -1;

	/**
	 * 用户类型
	 */
	public String UserType = "普通用户";

	/**
	 * 用户登录次数，只记录Android客户端
	 */
	public int LoginTimes = 0;

	/**
	 * 是否自动登录
	 */
	public boolean IsAuto = false;

	/**
	 * 是否记住密码
	 */
	public boolean IsRememberPwd = false;

	/**
	 * 最后一次登录时的服务器地址
	 */
	public String LatestServer = "";
	
	/**
	 * 最后一次登录时的服务器名称
	 */
	public String LatestServerName = "";

	/**
	 * 用户的Token值，用于与后台交互时的身份论证
	 */
	public String Token="";

	/**
	 * 当前用户头像存储地址
	 */
	public String ImagePath="";

	/**
	 * 用户版本号
	 */
	public String userVersion = "";

	/**
	 * 资源库服务器地址 
	 */
	public String ResourceLibraryDomainName = "";
	
	/**
	 * 用户在CRM中的部门编号
	 */
	public long CrmDepId;
	//}}

	public UserInfo(){

	}	

	/**
	 * 初始一个用户信息
	 * @param account 登录账号
	 * @param pwd  登录密码
	 * @param name 用户名称
	 * @param mobile 用户手机号
	 * @param companyName 公司名称
	 * @param companyID 公司ID
	 * @param userType 用户类型
	 * @param loginTimes 用户登录次数
	 * @param isAuto 是否自动登录
	 * @param latestServer 最后一次登录的服务器地址
	 * @param token 用户Token值
	 */
	public UserInfo(String ID,String account,String pwd,String name,String mobile,String companyName,Integer companyID,
			long crmDepId, String userType,int loginTimes,boolean isAuto,String latestServer,String token,String imagePath){	
		this.Account = account;
		this.Password = pwd;
		this.Name = name;
		this.Mobile = mobile;
		this.CompanyName = companyName;
		this.CompanyID=companyID;
		this.UserType = userType;
		this.LoginTimes = loginTimes;
		this.IsAuto = isAuto;
		this.LatestServer = latestServer;
		this.Token = token;
		this.ImagePath = imagePath;
		this.ID = ID;
		this.CrmDepId = crmDepId;
	}

	/**
	 * 初始一个用户信息
	 * @param obj   UserInfo的JsonObject对象
	 */
	public UserInfo(JSONObject obj){	
		this.Account = obj.optString("UserAccount");
		this.Name =  obj.optString("UserName");
		this.Mobile = obj.optString("UserMobile");
		this.CompanyName = obj.optString("CompanyName");
		this.CompanyID=obj.optInt("CompanyID");
		this.UserType = obj.optInt("UserType") == 0 ? "普通用户" : "管理员";
		this.Token = obj.optString("Token");
		this.userVersion = obj.optString("UserVersion");
		this.ID = obj.optString("ID");
		this.ResourceLibraryDomainName = obj.optString("ResourceLibraryDomainName");
		this.CrmDepId = obj.optLong("CrmDepId");
	}

	/**
	 * 用DTO转为对象
	 * @param userInfoDTO
	 */
	public UserInfo(UserInfoDTO userInfoDTO){	
		this.Account = userInfoDTO.UserAccount;
		this.Name =  userInfoDTO.UserName;
		this.Mobile = userInfoDTO.UserMobile;
		this.CompanyName = userInfoDTO.CompanyName;
		this.CompanyID=userInfoDTO.CompanyID;
		this.UserType = userInfoDTO.UserType == 0 ? "普通用户" : "管理员";
		this.Token = userInfoDTO.Token;
		this.userVersion = userInfoDTO.UserVersion;
		this.ID = userInfoDTO.ID.toString();
		this.ImagePath=userInfoDTO.ImagePath.toString();
		this.ResourceLibraryDomainName = userInfoDTO.ResourceLibraryDomainName;
		this.CrmDepId = userInfoDTO.CrmDepId;
	}
}
