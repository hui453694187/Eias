package com.yunfang.eias.dto;

import org.json.JSONObject;

import com.yunfang.eias.impl.ContactItemInterface;

/**
 * 
 * 项目名称：Estimate 
 * 类名称：CityDTO  
 * 类描述：城市信息 
 * 创建人：贺隽 
 * 创建时间：2015-03-09
 * 
 * @version
 */
public class CityDTO extends DTOBase implements ContactItemInterface {
	// {{ 相关的属性
	
	/**
	 * 省份名称
	 * */
	public String ProviceName;
	
	/**
	 * 城市名称
	 */
	public String Name;
	
	/**
	 * 城市拼音
	 */
	public String Pinyin;
	
	/**
	 * 是否开放
	 */
	public Boolean IsOpen;
	// }}
	
	//{{构造函数
	/**
	 * 无参构造，设置默认值
	 * */
	public CityDTO() {
		ProviceName = "";
		Name = "";
		IsOpen = false;
	}
	
	/**
	 * 构造函数
	 * @param obj JsonObject对象
	 */
	public CityDTO(JSONObject obj){
		this.ProviceName = obj.optString("desc");
		this.Name = obj.optString("name");
		this.IsOpen = obj.optBoolean("isopen"); 
	}

	/**
	 * 根据该字段来排序
	 * @return
	 */
	@Override
	public String getItemForIndex() {
		return Pinyin;
	}

	/**
	 * 该字段用来显示出来
	 * @return
	 */
	@Override
	public String getDisplayInfo() {
		return Name;
	}
	
	// }}
}
