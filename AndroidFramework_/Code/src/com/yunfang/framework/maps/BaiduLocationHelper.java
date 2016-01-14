/**
 * 
 */
package com.yunfang.framework.maps;

import android.content.Context;

import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 
 * @author gorson
 *
 */
public class BaiduLocationHelper {
	/**
	 * 构造函数
	 * 
	 * @param context:操作界面
	 * @param showMessage:显示提示信息
	 */
	public BaiduLocationHelper(Context context,Boolean showMessage) {
		super();
		mShowMessage = showMessage;
		mContext = context;
		mLocationClient = new LocationClient(mContext);
		myListener = new MyLocationListenner();
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		mGeofenceClient = new GeofenceClient(mContext);
		startLocation();
	}

	//{{ 属性
	/**
	 * Context
	 */
	private Context mContext;

	/**
	 * 声明LocationClient类
	 */
	public LocationClient mLocationClient = null;

	/**
	 * 声明GeofenceClient类
	 */
	public GeofenceClient mGeofenceClient = null;

	/**
	 * 百度围栏信息
	 */
	public BDGeofence mBDGeofence = null;

	/**
	 * 定位信息类
	 */
	private BDLocation bdLocation;

	/**
	 * 声明MyLocationListenner监听类
	 */
	private MyLocationListenner myListener = null;

	/**
	 * 记录当前点击的坐标
	 */
	public LatLng currentLatLng;
	
	/**
	 * 是否显示提示信息
	 */
	public Boolean mShowMessage = false;
	
	/**
	 * 是否开启定位
	 */
	private Boolean startLocation = false;
	//}}

	//{{ 定位方法

	/**
	 * 设置相关参数
	 */
	public void startLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02,还有bd09
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为5000ms，小于1秒则一次定位;大于等于1秒则定时定位
		option.setPriority(LocationClientOption.NetWorkFirst);// 不设置，默认是gps优先
		option.setPoiNumber(5); // 最多返回POI个数
		option.disableCache(true);// 禁止启用缓存定位
		option.setNeedDeviceDirect(true);// 是否需要方向
		option.setPoiDistance(1000); // poi查询距离
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		startLocation = true;
	}

	/**
	 * 停止，减少资源消耗
	 */
	private void stopListener() {
		if (startLocation) {
			mLocationClient.stop();
		}
		if(operatorListener != null){
			operatorListener.onSelected(bdLocation);
		}
		startLocation = false;
	}

	/**
	 * 获取执行结果
	 * 
	 * @return
	 */
	public int resetGetloc() {
		int result = -1;
		if (!startLocation) {
			startLocation();
		}
		result = bdLocation.getLocType();
		if (result != 66 && result != 68 && result != 161) {
			getRequestLocation();
		}
		result = bdLocation.getLocType();
		if (result != 66 && result != 68 && result != 161) {
			getRequestPoi();
		}
		result = bdLocation.getLocType();
		if (result != 66 && result != 68 && result != 161) {
			getRequestOfflineLocation();
		}
		return result;
	}

	/**
	 * 发起定位请求
	 */
	public void getRequestLocation() {
		if (startLocation) {
			mLocationClient.requestLocation();
		}
	}

	/**
	 * 发起POI查询请求
	 */
	public void getRequestPoi() {
		if (startLocation) {
			mLocationClient.requestPoi();
		}
	}

	/**
	 * 发起离线定位请求
	 */
	public void getRequestOfflineLocation() {
		if (startLocation) {
			mLocationClient.requestOfflineLocation();
		}
	}

	//}}

	//{{ 返回事件
	/**
	 *  地图操作的派发事件，暂时包含： 1.返回地图操作的结果；
	 *  				    2.直接返回，什么都不操作，也不返回
	 */
	private BaiduLoactionOperatorListener operatorListener;

	/**
	 * 确定按钮监听器,返回得到的地图结果
	 * @author gorson
	 *
	 */
	public interface BaiduLoactionOperatorListener{
		/**
		 * 返回地图选中点的信息
		 * @param latLng：返回地图当前选中的点信息
		 */
		public void onSelected(BDLocation location);
		
		
	}
	
	/**
	 * 设置确定事件后的响应事件
	 * @param l
	 */
	public void setOperatorListener(BaiduLoactionOperatorListener l){
		this.operatorListener = l;
	}
	//}}

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			setLocInfo(location);
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			setLocInfo(poiLocation);
		}

		private void setLocInfo(BDLocation location) {

			String msg = "";
			if (location != null) {				
				msg = getLoc(location);
				stopListener();
			} else {
				if(mShowMessage){
					msg = "百度地图服务器连接失败";	
				}				
			}
			if(msg.length() > 0){
				ToastUtil.longShow(mContext, msg);	
			}			
		}

		/**
		 * @param 当前位置信息
		 * @return
		 */
		private String getLoc(BDLocation location) {
			String msg = "";
			if (location.getCity() == null) {
				try {
					mLocationClient.requestLocation();
				} catch (Exception e) {
					e.getMessage();
					if (mLocationClient != null) {
						mLocationClient.stop();
					}
				}
			}
			bdLocation = location;
			if (location.getLocType() != 66 && location.getLocType() != 161 && location.getLocType() != 68) {
				resetGetloc();
			}
			if (location.getLocType() == 66 || location.getLocType() == 161 || location.getLocType() == 68) {
				if(mShowMessage){
					msg = "坐标获取成功,坐标类型为:" + (bdLocation.getLocType() == 66 || bdLocation.getLocType() == 68 ? "GPS定位的百度坐标" : "网络定位的百度坐标");	
				}				
			} else {
				if(mShowMessage){
					//msg = "坐标获取失败,请重试,errorCode:" + bdLocation.getLocType();	
					msg = "坐标获取失败,请重试";
				}				
			}
			return msg;
		}
	}
}
