package com.yunfang.framework.model;

import com.baidu.location.BDLocation;

/**
 * 地图坐标信息
 * 
 * @author 贺隽
 * 
 */
public class CoordinateInfo {

	/**
	 * 构造方法
	 * 
	 * @param mBDLocation
	 */
	public CoordinateInfo(BDLocation mBDLocation) {
		if(mBDLocation != null){			
			time = mBDLocation.getTime();
			locType = mBDLocation.getLocType();
			latitude = mBDLocation.getLatitude();
			longitude = mBDLocation.getLongitude();
			radius = mBDLocation.getRadius();
			if (mBDLocation.getLocType() == BDLocation.TypeGpsLocation) {
				speed = mBDLocation.getSpeed();
				satelliteNumber = mBDLocation.getSatelliteNumber();
				direction = mBDLocation.getDirection();
			} else if (mBDLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				address = mBDLocation.getAddrStr();
				operators = mBDLocation.getOperators();
			}
			poi = mBDLocation.getPoi();
			province = mBDLocation.getProvince();
			city = mBDLocation.getCity();
			district = mBDLocation.getDistrict();
		}
	}

	/**
	 * 设置坐标的时间
	 */
	public String time = "";

	/**
	 * 设置坐标的结果反馈信息 61 ： GPS定位结果 62 ： 扫描整合定位依据失败。此时定位结果无效。 63 ：
	 * 网络异常，没有成功向服务器发起请求。此时定位结果无效。 65 ： 定位缓存的结果。 66 ：
	 * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 67 ：
	 * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
	 * 161： 表示网络定位结果 162~167： 服务端定位失败。
	 */
	public int locType = 0;

	/**
	 * 获取坐标的纬度
	 */
	public Double latitude = 0.0;

	/**
	 * 获取坐标的经度
	 */
	public Double longitude = 0.0;

	/**
	 * 获取坐标的半径
	 */
	public float radius = 0;

	/**
	 * 获取省份信息
	 */
	public String province = "";

	/**
	 * 获取城市信息
	 */
	public String city = "";

	/**
	 * 获取区县信息
	 */
	public String district = "";

	/**
	 * 获取坐标所在地址
	 */
	public String address = "";

	/**
	 * 获取可返回的POI数值
	 */
	public String poi = "";

	/**
	 * 用时
	 */
	public float speed;

	/**
	 * 连接数量
	 */
	public int satelliteNumber;

	/**
	 * 方向
	 */
	public float direction;

	/**
	 * 服务供应商
	 */
	public int operators;
}
