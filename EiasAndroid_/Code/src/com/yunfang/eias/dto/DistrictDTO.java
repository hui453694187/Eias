package com.yunfang.eias.dto;

import org.json.JSONObject;

/**
 * 
 * 项目名称：Estimate 
 * 类名称：DistrictDTO  
 * 类描述：行政区信息 
 * 创建人：贺隽 
 * 创建时间：2015-03-09
 * 
 * @version
 */
public class DistrictDTO extends DTOBase {
	// {{ 相关的属性
	
	/**
	 * 价格环比
	 * */
	public String Rate;
	
	/**
	 * 均价
	 */
	public String Price;
	
	/**
	 * 小区个数
	 */
	public String SubCount;
	
	/**
	 * 经度 y
	 */
	public String Longitude;
	
	/**
	 * 纬度 x
	 */
	public String Latitude;
	
	/**
	 * 城市名称
	 */
	public String Name;
	// }}
	
	//{{构造函数
	/**
	 * 无参构造，设置默认值
	 * */
	public DistrictDTO() {
		Rate = "";
		Price = "";
		SubCount = "";
		Longitude = "";
		Latitude = "";
		Name = "";
	}
	
	/**
	 * 构造函数
	 * @param obj JsonObject对象
	 */
	public DistrictDTO(JSONObject obj){
		this.Rate = obj.optString("rate");
		this.Price = obj.optString("price");
		this.SubCount = obj.optString("num");
		this.Latitude = obj.optString("x");
		this.Longitude = obj.optString("y");
		this.Name = obj.optString("name");
	}
	
	// }}
}
