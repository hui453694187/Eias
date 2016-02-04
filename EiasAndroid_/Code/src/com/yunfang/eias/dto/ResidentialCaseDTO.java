package com.yunfang.eias.dto;

import org.json.JSONObject;

/**
 * 
 * 项目名称：Estimate 
 * 类名称：ResidentialCaseDTO  
 * 类描述：小区案例信息 
 * 创建人：贺隽 
 * 创建时间：2015-03-09
 * 
 * @version
 */
public class ResidentialCaseDTO extends DTOBase {
	// {{ 相关的属性
	
	/**
	 * 案例分类
	 * */
	public String CaseType;
	
	/**
	 * 建成年代
	 */
	public String BuiltIn;
	
	/**
	 * 朝向
	 */
	public String Toward;
	
	/**
	 * 租金
	 */
	public String Rent;
	
	/**
	 * 价格
	 */
	public String Price;
	
	/**
	 * 小区名称
	 */
	public String Name;
	
	/**
	 * 面积
	 */
	public String Area;
	
	/**
	 * 所在楼层
	 */
	public String Floor;
	
	/**
	 * 居室类型
	 */
	public String RoomType;
	
	/**
	 * 房屋类型
	 */
	public String HouseType;
	
	/**
	 * 总楼层
	 */
	public String MaxFloor;
	// }}
	
	//{{构造函数
	
	/**
	 * 无参构造，设置默认值
	 * */
	public ResidentialCaseDTO() {
		CaseType = "";
		BuiltIn = "";
		Toward = "";
		Rent = "";
		Price = "";
		Name = "";
		Area = "";
		Floor = "";
		RoomType = "";
		HouseType = "";
		MaxFloor = "";
	}
	
	/**
	 * 构造函数
	 * @param obj JsonObject对象
	 */
	public ResidentialCaseDTO(JSONObject obj){
		this.CaseType = obj.optString("fenlei");
		this.BuiltIn = obj.optString("niandai");
		this.Toward = obj.optString("chaoxiang");
		this.Rent = obj.optString("zujin");
		this.Price = obj.optString("jiage");
		this.Name = obj.optString("xiaoqu");
		this.Area = obj.optString("mianji");
		this.Floor = obj.optString("louceng");
		this.RoomType = obj.optString("jushi");		
		this.HouseType = obj.optString("ctype");
		this.MaxFloor = obj.optString("zonglouceng");
	}
	
	// }}
}
