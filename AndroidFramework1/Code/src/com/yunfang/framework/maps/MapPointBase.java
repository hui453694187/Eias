package com.yunfang.framework.maps;

import java.io.Serializable;

import com.baidu.mapapi.map.BitmapDescriptor;

/**
 * 地图点基本信息对象
 * 
 * @author gorson
 * 
 */
public class MapPointBase implements Serializable {
	private static final long serialVersionUID = -758459502806858414L;

	// {{ 构造函数
	/**
	 * 构造函数
	 */
	public MapPointBase() {

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
	 */
	public MapPointBase(double latitude, double longitude, String pointDesc) {
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
	public MapPointBase(double latitude, double longitude, String pointDesc,
			BitmapDescriptor icon) {
		this.Latitude = latitude;
		this.Longitude = longitude;
		this.PointDesc = pointDesc;
		this.Icon = icon;
	}

	// }}

	// {{ 属性
	/**
	 * 纬度
	 */
	public double Latitude;

	/**
	 * 经度
	 */
	public double Longitude;

	/**
	 * 点描述
	 */
	public String PointDesc;

	/**
	 * 图标点，如果不赋值，使用系统默认图标
	 */
	public BitmapDescriptor Icon;

	// }}
}
