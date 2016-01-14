package com.yunfang.framework.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yunfang.framework.R;
import com.yunfang.framework.R.id;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.maps.MapPointBase;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.ToastUtil;

//import com.yunfang.framework.utils.FileUtil;

/**
 * 百度地图控件
 * 
 * @author gorson
 * 
 */
@SuppressLint("DefaultLocale")
public class BaibuMapView extends LinearLayout implements OnGetGeoCoderResultListener, MKOfflineMapListener {

	// {{ 构造函数

	/**
	 * @param context
	 * @param attrs
	 */
	public BaibuMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView("", attrs);
	}

	/**
	 * @param context
	 */
	public BaibuMapView(Context context, String latLng) {
		super(context);
		mContext = context;
		initView(latLng, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public BaibuMapView(Context context, AttributeSet attrs, String latLng) {
		super(context, attrs);
		mContext = context;
		initView(latLng, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public BaibuMapView(Context context, AttributeSet attrs, Double lat, Double lng) {
		super(context, attrs);
		mContext = context;
		currentLatLng = new LatLng(lat, lng);
		initView("", attrs);
	}

	// }}

	// {{ 函数

	/**
	 * 响应自定义属性
	 * 
	 * @param attrs
	 */
	@SuppressLint("Recycle")
	private void getCustomerAttrs(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray customerAttrs = mContext.obtainStyledAttributes(attrs, R.styleable.baiduMapAttr);
			showAddressControl = customerAttrs.getBoolean(R.styleable.baiduMapAttr_showAddressControl, false);
			selectPoint = customerAttrs.getBoolean(R.styleable.baiduMapAttr_selectPoint, true);
			showZoomControls = customerAttrs.getBoolean(R.styleable.baiduMapAttr_showZoomControls, true);
			showBaiduLogo = customerAttrs.getBoolean(R.styleable.baiduMapAttr_showBaiduLogo, false);
			mapZoomSize = customerAttrs.getInteger(R.styleable.baiduMapAttr_mapZoomSize, 18);
			mapMaxZoomSize = customerAttrs.getFloat(R.styleable.baiduMapAttr_mapMaxZoomSize, mBaiduMap.getMaxZoomLevel());
			mapMinZoomSize = customerAttrs.getFloat(R.styleable.baiduMapAttr_mapMinZoomSize, mBaiduMap.getMinZoomLevel());
			showScaleControl = customerAttrs.getBoolean(R.styleable.baiduMapAttr_showScaleControl, false);
			showResetloc = customerAttrs.getBoolean(R.styleable.baiduMapAttr_showResetloc, false);
			showlocFollowing = customerAttrs.getBoolean(R.styleable.baiduMapAttr_showlocFollowing, false);
		}
		initAttrs();
	}

	boolean isTouch = false;
	float distance = 0;

	// /** * 计算两点距离 * * @param event * @return */
	// private float spacing(MotionEvent event) {
	// float x = event.getX(0) - event.getX(1);
	// float y = event.getY(0) - event.getY(1);
	// return FloatMath.sqrt(x * x + y * y);
	// }

	/**
	 * 获取百度控件对象时，对相关的参数进行设置后，需要调用此方法对界面进行重新渲染
	 */
	public void initAttrs() {
		if (mMapView != null) {
			setAddressControl(showAddressControl);
			setshowResetloc(showResetloc);
			int count = mMapView.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = mMapView.getChildAt(i);
				// 隐藏百度logo ZoomControl
				if (!showBaiduLogo && child instanceof ImageView) {
					child.setVisibility(View.INVISIBLE);
				}
				if (!showZoomControls && child instanceof ZoomControls) {
					child.setVisibility(View.INVISIBLE);
				}
				if (!showScaleControl && child instanceof RelativeLayout) {
					child.setVisibility(View.INVISIBLE);
				}
			}

			// mMapView.setOnTouchListener(new OnTouchListener() {
			//
			// @Override
			// public boolean onTouch(View v, MotionEvent event) {
			// float i = mBaiduMap.getMapStatus().zoom;
			// switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// case MotionEvent.ACTION_DOWN:
			// break;
			// case MotionEvent.ACTION_POINTER_DOWN:
			// isTouch = true;
			// distance = 0;
			// break;
			// case MotionEvent.ACTION_POINTER_UP:
			// isTouch = false;
			// break;
			// case MotionEvent.ACTION_UP:
			// isTouch = false;
			// break;
			// case MotionEvent.ACTION_MOVE:
			// if (isTouch) { // 判断多点移动
			// float mill = spacing(event); // 两点距离
			// if (mill > distance) {
			// if (i > mapMaxZoomSize || i < mapMinZoomSize) { // 超出范围
			// distance = mill;
			// return true;
			// }
			// }
			// distance = mill;
			// }
			// break;
			// default:
			// break;
			// }
			// return false;
			// }
			// });

			mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

				/**
				 * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
				 * 
				 * @param status
				 *            地图状态改变开始时的地图状态
				 */
				@Override
				public void onMapStatusChangeStart(MapStatus arg0) {

				}

				/**
				 * 地图状态变化结束
				 * 
				 * @param status
				 *            地图状态改变结束时的地图状态
				 */
				@Override
				public void onMapStatusChangeFinish(MapStatus arg0) {

				}

				/**
				 * 地图状态变化中
				 * 
				 * @param status
				 *            当前地图状态
				 */
				@Override
				public void onMapStatusChange(MapStatus arg0) {
					// float i = mBaiduMap.getMapStatus().zoom;
					// if (i > mapMaxZoomSize) {
					// MapStatusUpdate u = MapStatusUpdateFactory
					// .zoomTo(mapMaxZoomSize);
					// mBaiduMap.setMapStatus(u);
					// // ToastUtil.shortShow(mContext, "已经是可缩放的最大级别");
					// } else if (i < mapMinZoomSize) {
					// MapStatusUpdate u = MapStatusUpdateFactory
					// .zoomTo(mapMinZoomSize);
					// mBaiduMap.setMapStatus(u);
					// // ToastUtil.shortShow(mContext, "已经是可缩放的最小级别");
					// }
					if (operatorListener != null) {
						operatorListener.onMapStatusChange(arg0);
					}
				}
			});
		}
	}

	/**
	 * 绑定相关的控件
	 */
	@SuppressLint("InflateParams")
	private void initView(String latLng, AttributeSet attrs) {
		try {
			if (latLng.length() > 0 && latLng.contains(",")) {
				String[] latLngInfo = latLng.split(",");
				currentLatLng = new LatLng(Double.valueOf(latLngInfo[0]), Double.valueOf(latLngInfo[1]));
			}
			mLocationClient = new LocationClient(mContext);
			myListener = new MyLocationListenner();
			mLocationClient.registerLocationListener(myListener); // 注册监听函数
			mGeofenceClient = new GeofenceClient(mContext);

			// 注册 SDK 广播监听者
			ArrayList<String> actions = new ArrayList<String>();
			actions.add(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
			actions.add(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
			myBroadcastReceiver = new BaseBroadcastReceiver(mContext, actions);
			myBroadcastReceiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String s = intent.getAction();
					if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
						ToastUtil.longShow(mContext, "百度地图 key 验证出错!");
					} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
						ToastUtil.longShow(mContext, "网络出错");
					}
				}
			});

			LayoutInflater inflater = LayoutInflater.from(mContext);
			mView = inflater.inflate(R.layout.baidu_map, this);
			mMarkerInfoRL = (RelativeLayout) mView.findViewById(R.id.marker_info);
			btn_resetloc = (Button) mView.findViewById(R.id.btn_resetloc);
			btn_back = (Button) mView.findViewById(R.id.btn_back);
			btn_back.setOnClickListener(btnListeners);
			btn_save = (Button) mView.findViewById(R.id.btn_save);
			btn_save.setOnClickListener(btnListeners);
			btn_resetloc.setOnClickListener(btnListeners);
			lbl_address = (TextView) mView.findViewById(R.id.lbl_address);
			setshowScaleControl(showScaleControl);
			mMapView = ((MapView) mView.findViewById(R.id.bmapView));

			mBaiduMap = mMapView.getMap();
			mUiSettings = mBaiduMap.getUiSettings();
			initMap();
			getCustomerAttrs(attrs);
		} catch (Exception e) {
			throw e;
		}
	}

	// }}

	// {{ 地图操作
	private void initMap() {
		// 加载离线地图
		mOffilneBaiduMapSDK();
		// 构建Marker图标
		mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.base_map_tag_ic_normal);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 初始化搜索模块，注册事件监听
		mGeoCoderSearch = GeoCoder.newInstance();
		// 绑定通过坐标或者地址 找到 地址或者坐标
		mGeoCoderSearch.setOnGetGeoCodeResultListener(this);

		// 点击地图的事件
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				if (selectPoint) {
					setLatlng(point);
				}
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});

		// 地图加载完后的事件
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				startLocation();
			}
		});
		
		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {
			}
			@Override
			public void onMarkerDragEnd(Marker marker) {
				//标注拖动结束
				setLatlng(marker.getPosition());
			}
			@Override
			public void onMarkerDrag(Marker marker) {
				
			}
		});
	}

	/**
	 * 判断离线包
	 */
	private void mOffilneBaiduMapSDK() {
		mMKOfflineMap = new MKOfflineMap();
		mMKOfflineMap.init(this);
		mMKOfflineMap.importOfflineData(true);
		// String msg = "";
		ArrayList<MKOLUpdateElement> searchRecords = mMKOfflineMap.getAllUpdateInfo();
		if (searchRecords == null || searchRecords.size() <= 0) {
			// File baiduMapSDK = new File(FileUtil.getRootPath() +
			// File.separator
			// + "BaiduMapSDK" + File.separator + "vmp");
			// msg = "在" + baiduMapSDK.getAbsolutePath()
			// + "中没有找到离线包,请在放入离线包之后断开与电脑连接重启软件,系统会提示成功导入";
		} else {
			String s = "";
			for (MKOLUpdateElement mkolSearchRecord : searchRecords) {
				s += mkolSearchRecord.cityName + ",";
			}
			if (s.length() > 0) {
				s = s.substring(0, s.length() - 1);
			}
			// msg = "成功导入" + searchRecords.size() + "个离线包,其中包含:" + s;
		}
		// ToastUtil.longShow(getContext(), msg);
	}

	/**
	 * @param 点击之后设置坐标
	 */
	public void setLatlng(LatLng point) {
		lbl_address.setText("定位中...");

		if (currentLocation == null) {
			currentLocation = new BDLocation();
		}
		if (currentLocation != null) {
			currentLocation.setLatitude(point.latitude);
			currentLocation.setLongitude(point.longitude);
		}

		resetOverlay(point);

		if (isFirstLoc) {
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(mapZoomSize);
			mBaiduMap.animateMapStatus(u);
			isFirstLoc = false;
		}
		mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
	}

	/**
	 * 设置坐标
	 * 
	 * @param latitude
	 *            坐标纬度
	 * @param longitude
	 *            坐标经度
	 */
	public void setLatlng(double latitude, double longitude) {
		LatLng point = new LatLng(latitude, longitude);
		setLatlng(point);
	}

	/**
	 * 设置相关参数
	 */
	private void startLocation() {
		if (currentLatLng == null) {
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
			mLocationClient.setLocOption(option);
			mLocationClient.start();
		} else {
			setLatlng(currentLatLng);
		}
	}

	/**
	 * 重置坐标点
	 * 
	 * @param mLatLng
	 *            :坐标
	 */
	private void resetOverlay(LatLng mLatLng) {
		// mBaiduMap.clear();
		currentLatLng = mLatLng;
		MyLocationData locData = null;
		if (currentLatLngMarker != null) {
			currentLatLngMarker.remove();
			// currentLatLngMarker = (Marker) (mBaiduMap
			// .addOverlay(new MarkerOptions().position(mLatLng)
			// .icon(currentLatLngMarker.getIcon())
			// .zIndex(currentLatLngMarker.getZIndex())));
		}
		if (showlocFollowing) {
			locData = new MyLocationData.Builder().accuracy(150f)
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(0).latitude(mLatLng.latitude).longitude(mLatLng.longitude).build();
		} else {
			currentLatLngMarker = (Marker) (mBaiduMap.addOverlay(new MarkerOptions().position(mLatLng).icon(mBitmapDescriptor).zIndex(5).draggable(true)));
		}

		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(mLatLng));

		if (showlocFollowing) {
			mBaiduMap.setMyLocationData(locData);
		}
		showlocFollowing = false;
	}

	/**
	 * 添加多个标记点在地图上
	 * 
	 * @param points
	 *            添加的标记点集合
	 * @param clearOthers
	 *            是否要清除当前地图上的其他点
	 * @param showGPSCurrentPoint
	 *            是否要显示当前定位的坐标点
	 */
	public <T extends MapPointBase> void setPoints(List<T> points, Boolean clearOthers, Boolean showGPSCurrentPoint) {
		if (clearOthers) {
			mBaiduMap.clear();
			mMarkers = new ArrayList<Marker>();
		}
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		Marker marker = null;
		for (T info : points) {
			// 位置
			latLng = new LatLng(info.Latitude, info.Longitude);
			// 图标
			overlayOptions = new MarkerOptions().position(latLng).icon(info.Icon == null ? mBitmapDescriptor : info.Icon).zIndex(5);
			marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			Bundle bundle = new Bundle();
			bundle.putSerializable("pointInfo", info);
			marker.setExtraInfo(bundle);
			mMarkers.add(marker);
		}
		if (currentLatLngMarker != null && showGPSCurrentPoint) {
			overlayOptions = new MarkerOptions().position(currentLatLngMarker.getPosition()).icon(currentLatLngMarker.getIcon())
					.zIndex(currentLatLngMarker.getZIndex());
			mBaiduMap.addOverlay(overlayOptions);
		}
		// 将地图移到到最后一个经纬度位置
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(u);
	}

	/**
	 * 设置地图锚点被点击的响应事件
	 * 
	 * @param l
	 */
	public void setMarkerClickEvent(OnMarkerClickListener l) {
		if (l != null && mBaiduMap != null) {
			mBaiduMap.setOnMarkerClickListener(l);
		}
	}

	/**
	 * 删除所有的点
	 */
	public void clearPoints() {
		mBaiduMap.clear();
	}

	/**
	 * 切换城市
	 * 
	 * @param cityName
	 *            城市名称
	 */
	public void changeCity(String cityName) {
		mGeoCoderSearch.geocode(new GeoCodeOption().city(cityName).address(cityName));
	}

	// }}

	// {{ 属性
	public View mView;

	/**
	 * 当前控件视图
	 */
	private MapView mMapView;

	public Context getMapContext() {
		return mContext;
	}

	/**
	 * 当前定位的模式
	 */
	// private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode
	// mCurrentMode =
	// com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.FOLLOWING;

	private UiSettings mUiSettings;

	/**
	 * 详细信息的 布局
	 */
	private RelativeLayout mMarkerInfoRL;

	/**
	 * 所有的点
	 */
	private List<Marker> mMarkers = new ArrayList<Marker>();

	/**
	 * 当前坐标的标记点
	 */
	private Marker currentLatLngMarker = null;

	/**
	 * 声明MyLocationListenner监听类
	 */
	private MyLocationListenner myListener = null;

	/**
	 * 是否首次定位
	 */
	public boolean isFirstLoc = true;

	/**
	 * 地图默认的显示尺寸，从3-19可选
	 */
	public float mapZoomSize = 17;

	/**
	 * 地图最大放大尺寸
	 */
	public float mapMaxZoomSize = 17;

	/**
	 * 地图最小放大尺寸
	 */
	public float mapMinZoomSize = 10;

	/**
	 * 是否显示放大缩小图标，默认为显示
	 */
	public Boolean showZoomControls = true;

	/**
	 * 是否显示百度的Logo，默认为不显示
	 */
	public Boolean showBaiduLogo = false;

	/**
	 * 是否显示选择点的地址信息，默认为显示
	 */
	public Boolean showAddressControl = false;

	/**
	 * 是否允许选点操作
	 */
	public Boolean selectPoint = true;

	/**
	 * 百度地图中的标尺，默认不显示
	 */
	public Boolean showScaleControl = false;

	/**
	 * 是否允许选点操作
	 */
	public Boolean showResetloc = false;

	/**
	 * 是否显示跟随圆框
	 */
	public Boolean showlocFollowing = false;

	/**
	 * 监控百度地图使用的Key是否正确，百度地图是否能够正常通过网络访问
	 */
	private BaseBroadcastReceiver myBroadcastReceiver;

	/**
	 * 获取百度地图控件
	 * 
	 * @return
	 */
	public BaiduMap getBaiduMap() {
		return mBaiduMap;
	}

	public RelativeLayout getMarkerInfoRelativeLayout() {
		return mMarkerInfoRL;
	}

	/**
	 * 是否显示地址栏
	 * 
	 * @param show
	 */
	public void setAddressControl(Boolean show) {
		RelativeLayout rlAddress = (RelativeLayout) findViewById(id.rl_Address);
		rlAddress.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/**
	 * 是否显示百度地图中的标尺
	 * 
	 * @param show
	 */
	public void setshowScaleControl(Boolean showScaleControl) {
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.scaleControlEnabled(showScaleControl);
	}

	/**
	 * 是否显示缩放按钮
	 * 
	 * @param show
	 */
	public void setshowzoomControls(Boolean showScaleControl) {
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.zoomControlsEnabled(showScaleControl);
	}

	/**
	 * 是否显示定位
	 * 
	 * @param show
	 */
	public void setshowResetloc(Boolean show) {
		btn_resetloc.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/**
	 * 是否启用缩放手势
	 * 
	 * @param v
	 */
	public void setZoomEnable(Boolean enable) {
		mUiSettings.setZoomGesturesEnabled(enable);
	}

	/**
	 * 是否启用平移手势
	 * 
	 * @param v
	 */
	public void setScrollEnable(Boolean enable) {
		mUiSettings.setScrollGesturesEnabled(enable);
	}

	/**
	 * 是否启用旋转手势
	 * 
	 * @param v
	 */
	public void setRotateEnable(Boolean enable) {
		mUiSettings.setRotateGesturesEnabled(enable);
	}

	/**
	 * 是否启用俯视手势
	 * 
	 * @param v
	 */
	public void setOverlookEnable(Boolean enable) {
		mUiSettings.setOverlookingGesturesEnabled(enable);
	}

	/**
	 * 是否启用指南针图层
	 * 
	 * @param v
	 */
	public void setCompassEnable(Boolean enable) {
		mUiSettings.setCompassEnabled(enable);
	}

	// }}

	// {{ 控件 和 标准值
	/**
	 * 返回
	 */
	private Button btn_back;

	/**
	 * 保存数据
	 */
	private Button btn_save;

	/**
	 * 重新定位
	 */
	private Button btn_resetloc;

	/**
	 * 地址显示框
	 */
	private TextView lbl_address;

	/**
	 * 显示地址的默认值
	 */
	private final String locAddressTips = "定位中...";

	/**
	 * 百度地图对象
	 */
	private BaiduMap mBaiduMap;

	/**
	 * 离线地图
	 */
	private MKOfflineMap mMKOfflineMap = null;

	/**
	 * 通过地址找坐标 或者通过坐标找地址的对象
	 */
	private GeoCoder mGeoCoderSearch = null;

	/**
	 * 在地图上显示的标注
	 */
	private BitmapDescriptor mBitmapDescriptor;

	/**
	 * 记录当前点击的坐标
	 */
	public LatLng currentLatLng;

	/**
	 * 当前点的信息
	 */
	public BDLocation currentLocation;

	/**
	 * 声明LocationClient类
	 */
	private LocationClient mLocationClient = null;

	/**
	 * Context
	 */
	private Context mContext;

	/**
	 * 声明GeofenceClient类
	 */
	public GeofenceClient mGeofenceClient = null;

	// }}

	// {{ 按钮事件

	/**
	 * 按钮的点击事件
	 */
	private View.OnClickListener btnListeners = new OnClickListener() {

		@Override
		public void onClick(View v) {

			// switch (v.getId()) {
			// case R.id.btn_back:
			// if (operatorListener != null) {
			// operatorListener.onCancel();
			// }
			// break;
			// case R.id.btn_save:
			// if (operatorListener != null) {
			// if (locAddressTips.equals(lbl_address.getText())) {
			// DialogUtil.showConfirmationDialog(getContext(),
			// "还在定位地址中，您确认直接保存坐标信息吗?",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// dialog.cancel();
			// operatorListener
			// .onSelected(currentLocation);
			// }
			// });
			// } else {
			// operatorListener.onSelected(currentLocation);
			// }
			// }
			// case R.id.btn_resetloc:
			// isFirstLoc = true;
			// currentLatLng = null;
			// startLocation();
			// break;
			// default:
			// break;
			// }
			if (v.getId() == R.id.btn_back) {
				if (operatorListener != null) {
					operatorListener.onCancel();
				}
			}
			if (v.getId() == R.id.btn_save) {
				if (operatorListener != null) {
					if (locAddressTips.equals(lbl_address.getText())) {
						DialogUtil.showConfirmationDialog(getContext(), "还在定位地址中，您确认直接保存坐标信息吗?", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								operatorListener.onSelected(currentLocation);
							}
						});
					} else {
						operatorListener.onSelected(currentLocation);
					}
				}
			}
			if (v.getId() == R.id.btn_resetloc) {
				isFirstLoc = true;
				currentLatLng = null;
				startLocation();
			}
		}
	};

	// }}

	// {{ 继承事件

	/**
	 * 加载离线包之后的操作
	 */
	@Override
	public void onGetOfflineMapState(int arg0, int arg1) {

	}

	/**
	 * 地址转经纬度
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ToastUtil.longShow(mContext, "抱歉，未能找到结果");
		} else {
			if (this.operatorListener != null) {
				this.operatorListener.onGetGeoCodeResult(result);
			} else {
				// String strInfo = String.format("纬度：%f 经度：%f",
				// result.getLocation().latitude,
				// result.getLocation().longitude);
				// ToastUtil.longShow(mContext, strInfo);
			}
			resetOverlay(result.getLocation());
		}
	}

	/**
	 * 经纬度转地址,这个方法在地图上选择某个点之后被触发
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ToastUtil.longShow(mContext, "抱歉，未能找到结果");
		} else {
			lbl_address.setText(result.getAddress());
			// ToastUtil.longShow(mContext, result.getAddress());
		}
	}

	// }}

	/**
	 * 控件注销
	 */
	public void onDestroy() {
		// 取消监听 SDK 广播
		myBroadcastReceiver.unregisterReceiver();
	}

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
				currentLocation = location;
				getMapLoc(location);
				stopListener();
			} else {
				msg = "百度地图服务器连接失败";
			}
			if (msg.length() > 0) {
				ToastUtil.longShow(mContext, msg);
			}
		}

		/**
		 * 停止，减少资源消耗
		 */
		private void stopListener() {
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.stop();
			}
		}

		/**
		 * @param 当前位置信息
		 */
		private void getMapLoc(BDLocation location) {
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				resetOverlay(mLatLng);
				lbl_address.setText(location.getAddrStr());
				MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(mapZoomSize);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}

	// {{ 对外封装的接口事件
	/**
	 * 地图操作的派发事件，暂时包含： 1.返回地图操作的结果； 2.直接返回，什么都不操作，也不返回
	 */
	private OperatorListener operatorListener;

	/**
	 * 确定按钮监听器,返回得到的地图结果
	 * 
	 * @author gorson
	 * 
	 */
	public interface OperatorListener {
		/**
		 * 返回地图选中点的信息
		 * 
		 * @param latLng
		 *            ：返回地图当前选中的点信息
		 */
		public void onSelected(BDLocation location);

		/**
		 * 取消操作，直接返回 ，什么数据也不返回
		 */
		public void onCancel();

		/**
		 * 地址转经纬度
		 * 
		 * @param result
		 */
		public void onGetGeoCodeResult(GeoCodeResult result);

		/**
		 * 地图状态变化功能
		 * 
		 * @param arg0
		 */
		public void onMapStatusChange(MapStatus arg0);
	}

	/**
	 * 设置确定事件后的响应事件
	 * 
	 * @param l
	 */
	public void setOperatorListener(OperatorListener l) {
		this.operatorListener = l;
	}
	// }}
}