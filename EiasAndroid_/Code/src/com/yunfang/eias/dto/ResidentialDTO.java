package com.yunfang.eias.dto;

import org.json.JSONObject;

/**
 * 
 * 项目名称：Estimate 
 * 类名称：ResidentialDTO  
 * 类描述：小区信息 
 * 创建人：贺隽 
 * 创建时间：2015-03-09
 * 
 * @version
 */
public class ResidentialDTO extends DTOBase {
	// {{ 相关的属性
	
	/**
	 * 均价环比
	 * */
	public String AvgRelative;
	
	/**
	 * 小区均价
	 */
	public String AvgPrice;
	
	/**
	 * 小区地址
	 */
	public String Address;
	
	/**
	 * 租金环比
	 */
	public String RentRelative;
	
	/**
	 * 租金
	 */
	public String Rent;
	
	/**
	 * 经度 y
	 */
	public String Longitude;
	
	/**
	 * 纬度 x
	 */
	public String Latitude;
	
	/**
	 * 行政区
	 */
	public String DistrictName;
	
	/**
	 * 小区名称
	 */
	public String Name;
	// }}
	
	//{{构造函数
	
	/**
	 * 无参构造，设置默认值
	 * */
	public ResidentialDTO() {
		AvgRelative = "";
		AvgPrice = "";
		Address = "";
		RentRelative = "";
		Rent = "";
		Longitude = "";
		Latitude = "";
		DistrictName = "";
		Name = "";
	}
	
	/**
	 * 构造函数
	 * @param obj JsonObject对象
	 */
	public ResidentialDTO(JSONObject obj){
		this.AvgRelative = obj.optString("huanbi");
		this.AvgPrice = obj.optString("jiage");
		this.Address = obj.optString("address");
		this.RentRelative = obj.optString("huanbiz");
		this.Rent = obj.optString("jiagez");
		this.Latitude = obj.optString("x");
		this.Longitude = obj.optString("y");
		this.DistrictName = obj.optString("district");
		this.Name = obj.optString("name");
	}
	
	// }}
}
