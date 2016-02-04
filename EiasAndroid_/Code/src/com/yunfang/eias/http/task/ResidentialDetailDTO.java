package com.yunfang.eias.http.task;

import org.json.JSONObject;

import com.yunfang.eias.dto.DTOBase;

/**
 * 
 * 项目名称：Estimate 
 * 类名称：ResidentialDetailDTO  
 * 类描述：小区详细信息 
 * 创建人：贺隽 
 * 创建时间：2015-03-09
 * 
 * @version
 */
public class ResidentialDetailDTO extends DTOBase {
	// {{ 相关的属性
	
	/**
	 * 片区名
	 * */
	public String AreaName;
	
	/**
	 * 开发商
	 */
	public String Developer;
	
	/**
	 * 经度 x
	 */
	public String Longitude;
	
	/**
	 * 纬度 y
	 */
	public String Latitude;
		
	/**
	 * 小区地址
	 */
	public String Address;
	
	/**
	 * 绿化率
	 */
	public String GreeningRate;
	
	/**
	 * 住宅类型
	 */
	public String HouseType;
	
	/**
	 * 建成年代
	 */
	public String BuiltIn;
	
	/**
	 * 行政区
	 */
	public String DistrictName;
	
	/**
	 * 物业费
	 */
	public String PropertyFee;
	
	/**
	 * 容积率 是指一个小区的地上总建筑面积与用地面积的比率
	 */
	public String VolumeFraction;
	
	/**
	 * 小区名称
	 */
	public String Name;
	// }}
	
	//{{构造函数
	
	/**
	 * 无参构造，设置默认值
	 * */
	public ResidentialDetailDTO() {
		AreaName = "";
		Developer = "";
		Longitude = "";
		Latitude = "";		
		Address = "";		
		GreeningRate = "";
		HouseType = "";
		BuiltIn = "";
		DistrictName = "";
		PropertyFee = "";
		VolumeFraction = "";
		Name = "";
	}
	
	/**
	 * 构造函数
	 * @param obj JsonObject对象
	 */
	public ResidentialDetailDTO(JSONObject obj){
		this.AreaName = obj.optString("pianqu");
		this.Developer = obj.optString("kaifashang");
		//this.Latitude = obj.optString("x");
		//this.Longitude = obj.optString("y");
		this.Latitude = obj.optString("y");
		this.Longitude = obj.optString("x");
		this.Address = obj.optString("address");
		this.GreeningRate = obj.optString("lvhualv");
		this.HouseType = obj.optString("zhuzhaileixing");
		this.BuiltIn = obj.optString("jianchengniandai");
		this.DistrictName = obj.optString("xingzhengqu");		
		this.PropertyFee = obj.optString("wuyefei");
		this.VolumeFraction = obj.optString("rongjilv");		
		this.Name = obj.optString("name");
	}
	
	// }}
}
