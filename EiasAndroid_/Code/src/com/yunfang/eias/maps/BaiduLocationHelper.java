/**
 *
 */
package com.yunfang.eias.maps;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.yunfang.framework.utils.ToastUtil;

import java.util.List;

/**
 *
 * @author gorson
 *
 */
public class BaiduLocationHelper {

	public LocationClient mLocationClient = null;
	/**
	 * 定位监听
	 */
	public BDLocationListener myListener = new MyLocationListener();
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
//		mLocationClient = new LocationClient(mContext);
//		myListener = new MyLocationListenner();
//		mLocationClient.registerLocationListener(myListener); // 注册监听函数
//		mGeofenceClient = new GeofenceClient(mContext);

		mLocationClient = new LocationClient(context);     //声明LocationClient类
		mLocationClient.registerLocationListener(myListener);    //注册监听函数

		//配置定位SDK参数
		initLocation();

		//开始定位
		startLocation();
	}

	/**
	 * 配置定位SDK参数
	 */
	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy
		);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span=1000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	//{{ 属性
	/**
	 * Context
	 */
	private Context mContext;



	/**
	 * 声明GeofenceClient类
	 */
//	public GeofenceClient mGeofenceClient = null;
//
//	/**
//	 * 百度围栏信息
//	 */
//	public BDGeofence mBDGeofence = null;

	/**
	 * 定位信息类
	 */
	private BDLocation bdLocation;

	/**
	 * 声明MyLocationListenner监听类
	 */
//	private MyLocationListenner myListener = null;

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
	 * 开始定位
	 */
	public void startLocation() {
		//开始定位
		mLocationClient.start();
		startLocation = true;


//		LocationClientOption option = new LocationClientOption();
//		option.setLocationMode(LocationMode.Hight_Accuracy);
//		option.setOpenGps(true);
//		option.setAddrType("all");// 返回的定位结果包含地址信息
//		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02,还有bd09
////		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
////		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为5000ms，小于1秒则一次定位;大于等于1秒则定时定位
////		option.setPriority(LocationClientOption.NetWorkFirst);// 不设置，默认是gps优先
////		option.setPoiNumber(5); // 最多返回POI个数
////		option.disableCache(true);// 禁止启用缓存定位
////		option.setNeedDeviceDirect(true);// 是否需要方向
////		option.setPoiDistance(1000); // poi查询距离
//        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
//		mLocationClient.setLocOption(option);
//		mLocationClient.start();


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
//			mLocationClient.requestPoi();
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
//		 * @param latLng ：返回地图当前选中的点信息
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

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			List<Poi> list = location.getPoiList();// POI数据
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}
			Log.i("BaiduLocationApiDem", sb.toString());

            setLocInfo(location);
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
			 * @param location 当前位置信息
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



	//}}

//	/**
//	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
//	 */
//	private class MyLocationListenner implements BDLocationListener {
//
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			setLocInfo(location);
//		}
//
//		@Override
//		public void onReceivePoi(BDLocation poiLocation) {
//			setLocInfo(poiLocation);
//		}
//
//		private void setLocInfo(BDLocation location) {
//
//			String msg = "";
//			if (location != null) {
//				msg = getLoc(location);
//				stopListener();
//			} else {
//				if(mShowMessage){
//					msg = "百度地图服务器连接失败";
//				}
//			}
//			if(msg.length() > 0){
//				ToastUtil.longShow(mContext, msg);
//			}
//		}
//
//		/**
//		 * @param 当前位置信息
//		 * @return
//		 */
//		private String getLoc(BDLocation location) {
//			String msg = "";
//			if (location.getCity() == null) {
//				try {
//					mLocationClient.requestLocation();
//				} catch (Exception e) {
//					e.getMessage();
//					if (mLocationClient != null) {
//						mLocationClient.stop();
//					}
//				}
//			}
//			bdLocation = location;
//			if (location.getLocType() != 66 && location.getLocType() != 161 && location.getLocType() != 68) {
//				resetGetloc();
//			}
//			if (location.getLocType() == 66 || location.getLocType() == 161 || location.getLocType() == 68) {
//				if(mShowMessage){
//					msg = "坐标获取成功,坐标类型为:" + (bdLocation.getLocType() == 66 || bdLocation.getLocType() == 68 ? "GPS定位的百度坐标" : "网络定位的百度坐标");
//				}
//			} else {
//				if(mShowMessage){
//					//msg = "坐标获取失败,请重试,errorCode:" + bdLocation.getLocType();
//					msg = "坐标获取失败,请重试";
//				}
//			}
//			return msg;
//		}
//	}
}
