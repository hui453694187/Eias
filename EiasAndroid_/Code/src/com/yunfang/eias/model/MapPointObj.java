package com.yunfang.eias.model;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.yunfang.framework.maps.MapPointBase;

public class MapPointObj extends MapPointBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// {{ 构造函数

	/**
	 * 构造函数
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @param pointDesc
	 *            点描述
	 */
	public MapPointObj(double latitude, double longitude, String pointDesc) {
		this.Latitude = latitude;
		this.Longitude = longitude;
		this.PointDesc = pointDesc;
	}

	/**
	 * 构造函数
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @param pointDesc
	 *            点描述
	 * @param icon
	 *            点样式图
	 */
	public MapPointObj(double latitude, double longitude, String pointDesc,
			BitmapDescriptor icon,String imageUrl) {
		this.Latitude = latitude;
		this.Longitude = longitude;
		this.PointDesc = pointDesc;
		this.Icon = icon;
		this.ImageUrl = imageUrl;
	}

	// }}

	// {{ 属性
	/**
	 * 图片路径
	 */
	public String ImageUrl;
	/**
	 * 小区名称
	 */
	public String  ResidentialName;
	/**
	 * 均价环比
	 */
	public String AvgRelative;
	/**
	 * 均价
	 */
	public String AvgPrice;
	/**
	 * 租金环比
	 */
	public String RentRelative;
	/**
	 * 租金
	 */
	public String Rent;
	/**
	 * 地址
	 */
	public String Address;
	// }}
}
