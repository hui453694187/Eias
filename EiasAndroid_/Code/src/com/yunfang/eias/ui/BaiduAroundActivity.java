package com.yunfang.eias.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.yunfang.eias.R;
import com.yunfang.eias.enumObj.IntroductionTypeEnum;
import com.yunfang.eias.view.PoiOverlay;
import com.yunfang.eias.viewmodel.BaiduAroundViewModel;

/**
 * 百度地图获取周边信息
 *
 * @author zhu
 *
 * 2016年3月28日 下午6:15:35
 *
 */
public class BaiduAroundActivity extends Activity {
	
	/**
	 * 搜索的半径
	 * 单位(m)
	 */
	private static final int mapRadius = 2000;
	
	// 定位相关
	public LocationClient mLocClient;
	
	public MyLocationListenner myListener = new MyLocationListenner();

	private LocationMode mCurrentMode;

	BitmapDescriptor mCurrentMarker;

	private MapView mMapView;
	
	private BaiduMap mBaiduMap;
	/**
	 * 搜索模块，也可去掉地图模块独立使用
	 */
	private GeoCoder mGeoCoderSearch = null; // 

	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true; // 是否首次定位

	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;

	private LinearLayout btn_search;
	/**
	 * 城市名称
	 */
	private String cityName = "广州市";
	/**
	 * 详细地址
	 */
	private String address;
	/**
	 * 搜素的关键字
	 */
	private String searchKeywork = "学校";

	/**
	 * 传递过来的关键字
	 */
	private String intentKeyWord = null;

	/**
	 * 请求结果的页数
	 */
	private int load_Index = 0;
	/**
	 * 当前的点
	 */
	private LatLng currentLatlan = null;
	/**
	 * 定位的点
	 */
	private LatLng locationLatlan = null;

	/**
	 * 搜索返回的结果集
	 */
	private PoiResult nowPoiResult = null;

	/**
	 *  初始化全局 bitmap 信息，不用时及时 recycle
	 */
	private BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_focus_marka);

	/**
	 * 搜索的中心点的Marker
	 */
	private Marker mMarkerSearckBase;

	/**
	 * 搜索的点
	 */
	private List<OverlayOptions> overlayOptions = null;
	private BaiduAroundViewModel bdModel;
	/**
	 * 传递过来的任务ID
	 */
	public static final String INTENT_TASK_TARGET_ADDRESS = "INTENT_TASK_TARGET_ADDRESS";
	/**
	 * 传递过来的关键字
	 */
	public static final String INTENT_KEY_NAME = "INTENT_KEY_NAME";
	/**
	 * 返回的值
	 */
	public static final String INTENT_RESULT_VLAUE = "INTENT_RESULT_VLAUE";
	/**
	 * requestCode
	 */
	public static final int INTENT_REQUESTCODE = 110;
	/**
	 * resultCode
	 */
	public static final int INTENT_RESULTCODE = 111;

	private static final String TAG = "BaiduAroundActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baidu_around);

		//初始化数据
		initData();

		initUI();
		//定位
		initLocation();

		//搜索
		initSearch();

		//获取传递过来的数据
		getIntentData();

		
		//展示popuWindowd
//		btn_map_help.post(new Runnable() {
//			@Override
//			public void run() {
//				bdModel.getPopupWindow();
//				bdModel.popupWindow.showAsDropDown(btn_map_help);
//			}
//		});
	}

	private void initData() {
		bdModel = new BaiduAroundViewModel(this);

	}

	/**
	 * 获取传递过来的数据
	 */
	private void getIntentData() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			//关键字
			intentKeyWord = extras.getString(INTENT_KEY_NAME, null);
			edit_search_keyword.setText((intentKeyWord != null) ? intentKeyWord : "");
			//地址
			String taAddress = extras.getString(INTENT_TASK_TARGET_ADDRESS, null);
			if (taAddress != null) {
				et_search_address.setText(taAddress);
				//跳转到目标地点
				actionClickGetGeoCode();
			}
		}
	}

	/**
	 * 点击地图生成Marker
	 * 创建目标点
	 * 
	 * 
	 */
	private void createNowOverlay() {
		MarkerOptions ooA = new MarkerOptions().position(currentLatlan).icon(bdA).zIndex(9).draggable(true);
		//生长动画
		ooA.animateType(MarkerAnimateType.grow);
		mMarkerSearckBase = (Marker) (mBaiduMap.addOverlay(ooA));

		// 添加圆
		OverlayOptions ooCircle = new CircleOptions().fillColor(0x000000FF).center(currentLatlan).stroke(new Stroke(4, 0xAA00AA00)).radius(mapRadius);
		mBaiduMap.addOverlay(ooCircle);

	}

	/**
	 * 反向搜索
	 * @param llA
	 */
	private void searchGeoCode(LatLng llA) {
		// 反Geo搜索
		mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(llA));

	}

	/**
	 * 地图点击的方法
	 */
	private OnMapClickListener myOnMapClickListener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng llA) {
			currentLatlan = llA;
			mBaiduMap.clear();
			createNowOverlay();
			searchGeoCode(llA);
		}

		@Override
		public boolean onMapPoiClick(MapPoi mapPoi) {
			currentLatlan = mapPoi.getPosition();
			mBaiduMap.clear();
			createNowOverlay();
			searchGeoCode(mapPoi.getPosition());
			editCity.setText(mapPoi.getName());
			return true;
		}

	};

	/**
	 * 搜索
	 */
	private void initSearch() {

		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(myOnGetPoiSearchResultListener);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(myOnGetSuggestionResultListener);

		// 从名称获得地理坐标
		mGeoCoderSearch = GeoCoder.newInstance();
		mGeoCoderSearch.setOnGetGeoCodeResultListener(myOnGetGeoCodeResultListener);
	}

	/**
	 * 周边搜索回调
	 */
	private OnGetPoiSearchResultListener myOnGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiResult(PoiResult result) {

			if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				Toast.makeText(getApplicationContext(), "未找到结果", Toast.LENGTH_LONG).show();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				setReturnListValue(result);

				mBaiduMap.clear();
				PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result);
				overlay.addToMap();
				overlay.zoomToSpan();
				overlayOptions = overlay.getOverlayOptions();

				//创建当前点
				createNowOverlay();
				//生成文本
				actionClickCreateText2();
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

				// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
				String strInfo = "关键字在本市没有找到,建议搜索 ";
				for (CityInfo cityInfo : result.getSuggestCityList()) {
					strInfo += cityInfo.city;
					strInfo += ",";
				}
				strInfo += " ";
				Toast.makeText(getApplicationContext(), strInfo, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {

			if (result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(getApplicationContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
			} else {
				createPopWindow(result.getLocation(), result.getName());

			}
		}
	};

	/**
	 * 创建pop窗口
	 * @param ll
	 * @param name
	 */
	private void createPopWindow(LatLng ll, String name) {
		Button button = new Button(getApplicationContext());
		button.setBackgroundResource(R.drawable.popup_bg_mylocal_default);
		OnInfoWindowClickListener listener = null;

		button.setText(name);
		listener = new OnInfoWindowClickListener() {
			public void onInfoWindowClick() {

			}
		};

		InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	/**
	 * 填充返回的数据
	 * @param result
	 */
	private void setReturnListValue(PoiResult result) {
		nowPoiResult = result;

	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);

			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
			// }
			return true;
		}

	}

	private OnGetSuggestionResultListener myOnGetSuggestionResultListener = new OnGetSuggestionResultListener() {

		@Override
		public void onGetSuggestionResult(SuggestionResult arg0) {

		}
	};

	/**
	 * 点击搜索
	 */
	private void actionClickSearch() {

		if (currentLatlan == null) {
			showToast("定位失败,请手动输入地址或者在地图上点击");
//			mLocClient.start();
			return;
		}
		if (edit_search_keyword.getText() == null || TextUtils.isEmpty(edit_search_keyword.getText().toString())) {
			showToast("搜索关键字为空");
			return;
		}
		searchKeywork = edit_search_keyword.getText().toString().trim();

		PoiNearbySearchOption poiNearby = new PoiNearbySearchOption();
		poiNearby.location(currentLatlan);
		poiNearby.keyword(searchKeywork);
		//设置检索的半径范围 检索半径 单位： m
		poiNearby.radius(mapRadius);
		//现在不允许修改了 ,直接强制为0页
		poiNearby.pageNum(0);
		//poiNearby.pageNum(load_Index);
		poiNearby.pageCapacity(10);
		mPoiSearch.searchNearby(poiNearby);

	}

	/**
	 * 搜索下一页
	 */
	private void actionClickSearchNextPage() {
		load_Index++;
		actionClickSearch();
	}

	/**
	 * 定位
	 */
	private void initLocation() {
		//		MyWindowManager.createSmallWindow(this);

		mCurrentMode = LocationMode.NORMAL;
		// 传入null则，恢复默认图标
		mCurrentMarker = null;

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		//隐藏缩放控件
		hideZoomControls();
		//设置间距
		mBaiduMap.setPadding(0, 0, 0, 500);
		//地图点击监听
		mBaiduMap.setOnMapClickListener(myOnMapClickListener);

		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 隐藏缩放控件
	 */
	private void hideZoomControls() {
		// 隐藏缩放控件
		int childCount = mMapView.getChildCount();
		View zoom = null;
		for (int i = 0; i < childCount; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				zoom = child;
				break;
			}
		}
		zoom.setVisibility(View.GONE);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			Log.i(TAG, "location ！= null " + location.getCity());

			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);

			//单纯的定位,不需要移动试图
			//			if (isFirstLoc) {
			//				isFirstLoc = false;
			//				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			//				moveToLatlng(ll);
			//			}
			//定位成功
			locationLatlan = new LatLng(location.getLatitude(), location.getLongitude());

			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/**
	 * 移动地图到点
	 * @param ll
	 */
	private void moveToLatlng(LatLng ll) {
		MapStatus.Builder builder = new MapStatus.Builder();
		builder.target(ll).zoom(18.0f);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {

		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//销毁popuWindow
		if (bdModel.popupWindow != null) {
			bdModel.popupWindow.dismiss();
		}

		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		mGeoCoderSearch.destroy();
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	private OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_search:
				//请求页做为1
				load_Index = 1;
				//清空生成的文本
				lastCreateText = null;
				edit_create_text.setText("");

				actionClickSearch();
				break;
			case R.id.imageView_search_address:
				actionClickGetGeoCode();
				break;
			case R.id.btn_search_next_page:
				actionClickSearchNextPage();
				break;
			case R.id.btn_create_text:
				actionClickCreateText();
				break;
			case R.id.linearLayout_buttom_view: //下面的所有view
				//只是占据点击事件，防止点到地图
				break;
			case R.id.relativelayout_back://确认返回
				actionClickBackResult();
				break;
			case R.id.image_back_me://回到定位点
				actionClickBackMe();
				break;
			case R.id.text_change_map_mode://切换卫星hue普通地图
				actionClickChangeMapMode();
				break;
			case R.id.btn_back://点击返回
				finish();
				break;
			case R.id.image_main_icon_zoomin://点击放大地图
				actionClickZoomIn();
				break;
			case R.id.image_main_icon_zoomout://点击缩小地图
				actionClickZoomOut();
				break;
			case R.id.btn_map_help://点击开始帮助
				actionClickHelp();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 点击开始帮助
	 */
	private void actionClickHelp() {
		Intent intent = new Intent(this, IntroductionActivity.class);
		intent.putExtra(IntroductionActivity.INTENT_WHO_SHOW_IN_HERE, IntroductionTypeEnum.MapAroundOther.getIndex());
		startActivity(intent);
	}

	/**
	 * 点击缩小地图
	 */
	private void actionClickZoomOut() {
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
	}


	/**
	 * 点击放大地图
	 */
	private void actionClickZoomIn() {
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
	}

	/**
	 * 带着结果返回
	 */
	private void actionClickBackResult() {
		//获取生成结果
		if (edit_create_text.getText() == null || TextUtils.isEmpty(edit_create_text.getText().toString().trim())) {
			Spanned fromHtml = Html.fromHtml(getString(R.string.map_not_result));
			showToast(fromHtml);
			return;
		}
		//传递过来的关键字为空
		if (intentKeyWord == null) {
			showToast("好像没有把关键字传过来,建议退出本页面再进来  (╥╯^╰╥)");
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(BaiduAroundActivity.INTENT_RESULT_VLAUE, edit_create_text.getText().toString().trim());
		intent.putExtra(BaiduAroundActivity.INTENT_KEY_NAME, intentKeyWord);
		setResult(INTENT_RESULTCODE, intent);
		finish();
	}

	/**
	 * 切换卫星hue普通地图
	 */
	private void actionClickChangeMapMode() {
		if (text_change_map_mode.getText().toString().equals("卫星")) {
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			text_change_map_mode.setText("普通");
		} else {
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			text_change_map_mode.setText("卫星");
		}

	}

	/**
	 * 回到定位点
	 */
	private void actionClickBackMe() {
		moveToLatlng(locationLatlan);

	}

	/**
	 * 上一次生成的文字
	 */
	private String lastCreateText = null;

	/**
	 * 点击生成文字
	 */
	private void actionClickCreateText() {
		if (nowPoiResult != null && nowPoiResult.getAllPoi() != null && nowPoiResult.getAllPoi().size() > 0) {
			List<PoiInfo> allPoi = nowPoiResult.getAllPoi();
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < allPoi.size(); i++) {
				PoiInfo poiInfo = allPoi.get(i);
				builder.append(poiInfo.name);
				if (i < allPoi.size() - 1) {
					builder.append(",");
				}
			}
			//			if (lastCreateText != null) {
			//				lastCreateText = lastCreateText + "," + builder.toString();
			//			} else {
			//				lastCreateText = builder.toString();
			//			}
			lastCreateText = builder.toString();
			edit_create_text.setText(lastCreateText);
		} else {
			showToast("没有搜索到结果");
		}
	}

	/**
	 * 点击生成文字2
	 */
	private void actionClickCreateText2() {
		if (nowPoiResult != null && nowPoiResult.getAllPoi() != null && nowPoiResult.getAllPoi().size() > 0) {
			List<PoiInfo> allPoi = nowPoiResult.getAllPoi();
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < allPoi.size(); i++) {
				PoiInfo poiInfo = allPoi.get(i);
				builder.append(poiInfo.name);
				if (i < allPoi.size() - 1) {
					builder.append("、");
				}
			}
			lastCreateText = builder.toString();
			edit_create_text.setText(lastCreateText);
		} else {
			showToast("没有搜索到结果");
		}
	}


	private EditText editCity;
	private EditText et_search_address;
	private Button btn_search_next_page;
	private EditText edit_search_keyword;
	private Button btn_create_text;
	private EditText edit_create_text;
	private ImageView imageView_search_address;
	private LinearLayout linearLayout_buttom_view;
	private RelativeLayout relativelayout_back;
	private ImageView image_back_me;
	private TextView text_change_map_mode;
	private Button btn_back;
	private ImageView image_main_icon_zoomout;
	private ImageView image_main_icon_zoomin;
	private ImageView btn_map_help;

	private void initUI() {
		linearLayout_buttom_view = (LinearLayout) findViewById(R.id.linearLayout_buttom_view);
		relativelayout_back = (RelativeLayout) findViewById(R.id.relativelayout_back);

		text_change_map_mode = (TextView) findViewById(R.id.text_change_map_mode);
		editCity = (EditText) findViewById(R.id.city);
		et_search_address = (EditText) findViewById(R.id.et_search_address);
		edit_search_keyword = (EditText) findViewById(R.id.edit_search_keyword);
		edit_create_text = (EditText) findViewById(R.id.edit_create_text);

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_create_text = (Button) findViewById(R.id.btn_create_text);
		btn_search_next_page = (Button) findViewById(R.id.btn_search_next_page);
		imageView_search_address = (ImageView) findViewById(R.id.imageView_search_address);
		image_back_me = (ImageView) findViewById(R.id.image_back_me);
		btn_search = (LinearLayout) findViewById(R.id.btn_search);

		//放大缩小
		image_main_icon_zoomin = (ImageView) findViewById(R.id.image_main_icon_zoomin);
		image_main_icon_zoomout = (ImageView) findViewById(R.id.image_main_icon_zoomout);
		//帮助按钮
		btn_map_help = (ImageView) findViewById(R.id.btn_map_help);

		btn_search.setOnClickListener(myOnClickListener);
		imageView_search_address.setOnClickListener(myOnClickListener);
		btn_search_next_page.setOnClickListener(myOnClickListener);
		image_back_me.setOnClickListener(myOnClickListener);
		btn_create_text.setOnClickListener(myOnClickListener);
		text_change_map_mode.setOnClickListener(myOnClickListener);
		relativelayout_back.setOnClickListener(myOnClickListener);
		btn_back.setOnClickListener(myOnClickListener);
		image_main_icon_zoomin.setOnClickListener(myOnClickListener);
		image_main_icon_zoomout.setOnClickListener(myOnClickListener);
		btn_map_help.setOnClickListener(myOnClickListener);

		edit_search_keyword.setOnEditorActionListener(myOnEditorActionListener);

		//设置提示语
		edit_create_text.setHint(Html.fromHtml(getString(R.string.new_read_me)));
	}

	/**
	 * 输入完成点击回车键搜索
	 */
	private OnEditorActionListener myOnEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				actionClickSearch();
			}
			return false;
		}
	};

	/**
	 * 点击获取地址
	 */
	private void actionClickGetGeoCode() {
		//		if (editCity.getText() == null || TextUtils.isEmpty(editCity.getText().toString())) {
		//			Toast.makeText(getApplicationContext(), "城市名为空", Toast.LENGTH_LONG).show();
		//			return;
		//		}

		if (et_search_address.getText() == null || TextUtils.isEmpty(et_search_address.getText().toString())) {
			showToast(getString(R.string.please_enter_address));
			return;
		}

		//		cityName = editCity.getText().toString().trim();
		address = et_search_address.getText().toString().trim();
		//搜索结果页数变为第一页
		load_Index = 1;
		// Geo搜索
		mGeoCoderSearch.geocode(new GeoCodeOption().city(cityName).address(address));
	}

	/**
	 * 反编译地址获得坐标
	 * 或坐标获得地址
	 */
	private OnGetGeoCoderResultListener myOnGetGeoCodeResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				showToast("抱歉，未能找到结果");
				return;
			}
			et_search_address.setText(result.getAddress());
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				showToast("没有找到目标地址,请检查地址是否正确,或者直接在地图上选点");
				return;
			}
			mBaiduMap.clear();
			//			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
			//			String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
			//			Toast.makeText(getApplicationContext(), strInfo, Toast.LENGTH_LONG).show();
			showToast("目标定位成功,请确认位置");
			//刷新目标坐标点
			currentLatlan = result.getLocation();
			moveToLatlng(currentLatlan);
			createNowOverlay();
		}
	};

	public void showToast(CharSequence text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}
