package com.yunfang.eias.beta.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.db.DBTableScripts;
import com.yunfang.eias.beta.http.task.BackgroundServiceTask;
import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.logic.LoginInfoOperator;
import com.yunfang.eias.beta.model.DataDefine;
import com.yunfang.eias.beta.ui.SystemSettingActivity;
import com.yunfang.eias.beta.utils.CrashHandler;
import com.yunfang.eias.beta.utils.DownloadResultHelper;
import com.yunfang.eias.beta.utils.LogHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.db.SQLiteHelper;
import com.yunfang.framework.maps.BaiduLocationHelper;
import com.yunfang.framework.maps.BaiduLocationHelper.BaiduLoactionOperatorListener;
import com.yunfang.framework.model.DeviceInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.model.VersionInfo;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.SpUtil;
import com.yunfang.framework.utils.StringUtil;
import com.yunfang.framework.utils.ToastUtil;
import com.yunfang.framework.view.LoadingUtil;

/**
 * 
 * @author 贺隽
 * 
 */
public class EIASApplication extends BaseApplication {

	// {{ 全局使用的常量

	/**
	 * 是否捕获全局异常
	 */
	public final boolean isCrashGlobalExce = true;

	/** 当前定位城市名，默认北京 */
	public static String currentCity = "北京";

	/**
	 * 网络连接错误
	 */
	public final static String tipsNetworkLinkError = "网络连接错误";

	/**
	 * APK的名称
	 */
	public final static String DownLoadApkName = "eias.apk";

	/**
	 * 导出Json文件名称
	 */
	public final static String exportJsonName = "ObjectJson.txt";

	/**
	 * 图片
	 */
	public final static String photo = "photo";

	/**
	 * 音频
	 */
	public final static String audio = "audio";

	/**
	 * 视频
	 */
	public final static String video = "video";

	/**
	 * 百度地图按钮默认值 定位坐标
	 */
	public final static String DefaultBaiduMapTipsValue = "定位坐标";

	/**
	 * 百度地图按钮默认值 (未定位)
	 */
	public final static String DefaultBaiduMapUnLocTipsValue = "(未定位)";

	/**
	 * 下拉列表的默认值 -请选择-
	 */
	public final static String DefaultDropDownListValue = "- - - -请选择- - - -";

	/**
	 * 默认值 -- 双横线
	 */
	public final static String DefaultHorizontalLineValue = "--";

	/**
	 * 默认值null字符串
	 */
	public final static String DefaultNullString = "null";

	/**
	 * 头像文件名称
	 */
	public final static String USER_IMAGE_NAME = "faceImage.jpg";

	/**
	 * 需要使用的服务器地址
	 */
	public final static LinkedHashMap<String, String> Services = new LinkedHashMap<String, String>();
	// }}

	// {{ 全局使用的变量

	/**
	 * 提交任务重复次数(初始值，用户可在系统设置中这只该值)
	 */
	private static int repeatTime = 2;

	/**
	 * 拍摄图片的最大size(设定后拍摄的照片不会大于这个值)(初始值，用户可在系统设置中这只该值) 单位：M
	 */
	private static long maxImageSize = 4;

	/**
	 * SDCard小于100M需要提示用户 空间不足
	 */
	private static long sdcardSizeTips = 200;

	/**
	 * 公司名称
	 */
	public static String company = "";

	/**
	 * 项目名称
	 */
	public static String project = "";

	/**
	 * 缩略图根目录
	 */
	public static String thumbnailRoot = "";

	/**
	 * 任务根目录
	 */
	public static String projectRoot = "";

	/**
	 * 用户信息根目录
	 */
	public static String userRoot = "";

	/**
	 * 导出根目录
	 */
	public static String exportRoot = "";

	/**
	 * 导入根目录
	 */
	public static String importRoot = "";

	/**
	 * 外采默认根路径 sdcard/yunfang/eias/
	 */
	public static String root = "";

	/**
	 * 整个应用的上下文
	 * */
	private static BaseApplication mApplication;

	/**
	 * 是否联网
	 */
	public static Boolean IsNetworking = false;

	/**
	 * 是否离线登录
	 */
	public static boolean IsOffline = true;

	/**
	 * 当前正在上传的任务编码 如 P00000001
	 */
	public static String SubmitingTaskNum = "";

	/**
	 * 设备信息
	 */
	public static DeviceInfo deviceInfo;

	/**
	 * 版本信息
	 */
	public static VersionInfo versionInfo;

	/**
	 * 下载完整的文件名称
	 */
	public static String downloadFileName = FileUtil.getSDPath()
			+ Environment.DIRECTORY_DOWNLOADS + File.separator
			+ EIASApplication.DownLoadApkName;

	/**
	 * 是否为导入中
	 */
	public static boolean isImporting = false;

	/**
	 * 当用需要同步升级的配置表信息
	 */
	public static ArrayList<DataDefine> currentUpdateDataDefines = new ArrayList<DataDefine>();

	public static byte[] myPhoto = null;

	// }}

	// {{ 主程序运行时执行的

	/**
	 * 从缓存中取得系统设置参数方法
	 * 
	 * @param settingType
	 *            参数类型(在BroadRecordType的系统设置常量中选择)
	 * @return 设置的参数值,若无设置则取原始默认值
	 */
	public static String getSystemSetting(String settingType) {
		SpUtil sp = SpUtil.getInstance(BroadRecordType.KEY_SETTINGS);
		String resultStr = "";
		switch (settingType) {
		case BroadRecordType.KEY_SETTING_REPEATTIME:
			resultStr = sp
					.getString(BroadRecordType.KEY_SETTING_REPEATTIME, "");
			if (resultStr == "") {
				resultStr = String.valueOf(repeatTime);
			}
			break;
		case BroadRecordType.KEY_SETTING_MAXIMAGESIZE:
			resultStr = sp.getString(BroadRecordType.KEY_SETTING_MAXIMAGESIZE,
					"");
			if (resultStr == "") {
				resultStr = String.valueOf(maxImageSize);
			}
			break;
		case BroadRecordType.KEY_SETTING_SDCARDSIZE:
			resultStr = sp
					.getString(BroadRecordType.KEY_SETTING_SDCARDSIZE, "");
			if (resultStr == "") {
				resultStr = String.valueOf(sdcardSizeTips);
			}
			break;
		case BroadRecordType.KEY_SETTING_PICTURECOPYORPASTE:
			resultStr = sp.getString(
					BroadRecordType.KEY_SETTING_PICTURECOPYORPASTE, "");
			// 默认是复制
			if (resultStr == "") {
				resultStr = SystemSettingActivity.copyTypeChooseItem[0];
			}
			break;
		case BroadRecordType.KEY_SETTING_PHOTOTYPE:
			resultStr = sp.getString(BroadRecordType.KEY_SETTING_PHOTOTYPE,
					"系统自带相机");
			// 默认是复制
			if (resultStr == "") {
				resultStr = SystemSettingActivity.photoTypeChooseItem[1];
			}
			break;
		}
		return resultStr;
	}

	/**
	 * 地图定位
	 */
	public static BaiduLocationHelper locationHelper = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		setLoadingWorkerType(LoadingUtil.class);
		SQLiteHelper.setIDBArchitecture(new DBTableScripts());
		SQLiteHelper.getInstance();
		initFinalString();
		createHideDir();
		setILogArchitecture(new LogHelper());
		getDeviceInfo();
		getAndroidVersionInfo();
		initDownloadManager(new DownloadResultHelper());
		startService();
		receiverMainServerCreated();

		initImageLoader();
		
		/** 异常捕捉处理类 */
		if (isCrashGlobalExce) {
			CrashHandler crashHandler = CrashHandler.getInstance();
			crashHandler.init(this);
		}
	}

	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)//
				.threadPriority(Thread.NORM_PRIORITY - 2)//
				.denyCacheImageMultipleSizesInMemory()//
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())//
				.diskCacheSize(100 * 1024 * 1024).diskCacheFileCount(300)//
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 
	 */
	private void initFinalString() {

		/*
		 * Services.put("本地测试服务器", "http://192.168.3.66:8099");
		 * Services.put("大连服务器", "http://124.93.240.144:18099");
		 * Services.put("外业测试服务器", "http://182.92.219.161:18098");
		 */
		//Services.put("外业演示版服务器", "http://101.200.145.92:9092");
		Services.put("外业测试服务器", "http://eiastest.yunfangdata.com");
		Services.put("采图测试服务器", "http://182.92.161.16:18099");
		Services.put("外业正式服务器", "http://waicai.yunfangdata.com");

		company = getString(R.string.app_name_company);
		project = getString(R.string.app_name_en);
		root = FileUtil.getSysPath(company, project);
		exportRoot = root + getString(R.string.export_dir) + File.separator;
		importRoot = root + getString(R.string.import_dir) + File.separator;
		thumbnailRoot = root + getString(R.string.thumbnail_dir)
				+ File.separator;
		projectRoot = root + getString(R.string.project_dir) + File.separator;
		userRoot = root + getString(R.string.user_image_dir) + File.separator;
		FileUtil.mkDir(root);
		FileUtil.mkDir(exportRoot);
		FileUtil.mkDir(importRoot);
		FileUtil.mkDir(thumbnailRoot);
		FileUtil.mkDir(projectRoot);
		FileUtil.mkDir(userRoot);
	}

	/**
	 * 创建隐藏文件
	 */
	private void createHideDir() {
		String sysRoot = Environment.getExternalStorageDirectory() + "";
		String company = BaseApplication.getInstance().getString(
				R.string.app_name_company);
		String dirString = sysRoot + File.separator + company + File.separator
				+ ".nomedia";
		File file = new File(dirString);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取屏幕完整高度和宽度
	 */
	private void getDeviceInfo() {
		deviceInfo = new DeviceInfo();

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		// 获得屏幕的高宽（用来适配分辨率）
		deviceInfo.ScreenWeight = dm.widthPixels;
		deviceInfo.ScreenHeight = dm.heightPixels;

		EIASApplication.getInstance().getApplicationContext();
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceInfo.DeviceId = tm.getDeviceId();
		deviceInfo.DeviceSoftwareVersion = tm.getDeviceSoftwareVersion();
		deviceInfo.Line1Number = tm.getLine1Number();
		deviceInfo.NetworkCountryIso = tm.getNetworkCountryIso();
		deviceInfo.NetworkOperator = tm.getNetworkOperator();
		deviceInfo.NetworkOperatorName = tm.getNetworkOperatorName();
		deviceInfo.NetworkType = tm.getNetworkType();
		deviceInfo.PhoneType = tm.getPhoneType();
		deviceInfo.SimCountryIso = tm.getSimCountryIso();
		deviceInfo.SimOperator = tm.getSimOperator();
		deviceInfo.SimOperatorName = tm.getSimOperatorName();
		deviceInfo.SimSerialNumber = tm.getSimSerialNumber();
		deviceInfo.SimState = tm.getSimState();
		deviceInfo.SubscriberId = tm.getSubscriberId();
		deviceInfo.VoiceMailNumber = tm.getVoiceMailNumber();
	}

	/**
	 * 提供一个获取整个应用上下文的方法
	 * */
	public static BaseApplication getInstance() {
		return mApplication;
	}

	/**
	 * 获取版本信息
	 */
	public void getAndroidVersionInfo() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			versionInfo = new VersionInfo();
			versionInfo.LocalPackageName = info.packageName;
			versionInfo.LocalVersionCode = String.valueOf(info.versionCode);
			versionInfo.LocalVersionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	// }}

	// {{ 系统服务

	/**
	 * 获取当前用户登录信息
	 * 
	 * @return
	 */
	public static UserInfo getCurrentUser() {
		return LoginInfoOperator.getCurrentUser();
	}

	/**
	 * 启动服务
	 */
	private void startService() {
		try {
			Intent serviceIntent = new Intent();
			serviceIntent.setClass(this, com.yunfang.eias.beta.base.MainService.class);
			startService(serviceIntent);
			if (!isServiceRunning()) {
				ToastUtil.longShow(this, "服务开启失败");
			}
		} catch (Exception e) {
			ToastUtil.longShow(this, "服务开启失败:" + e.getMessage());
		}

	}

	/**
	 * 指定服务是否开启
	 * 
	 * @return
	 */
	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (MainService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 停止服务
	 */
	protected void StopService() {
		Intent serviceIntent = new Intent();
		serviceIntent.setAction(MainService.class.getName());
		stopService(serviceIntent);
	}

	// }}

	// {{ 推送坐标

	/**
	 * 主服务创建完成
	 */
	private BaseBroadcastReceiver mainServerCreatedReceiver;

	/**
	 * 响应主服务器创建完成
	 */
	public void receiverMainServerCreated() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(BroadRecordType.MAINSERVER_CREATED);
		mainServerCreatedReceiver = new BaseBroadcastReceiver(
				getApplicationContext(), temp);
		mainServerCreatedReceiver
				.setAfterReceiveBroadcast(new afterReceiveBroadcast() {
					@Override
					public void onReceive(final Context context, Intent intent) {
						String actionType = intent.getAction();
						switch (actionType) {
						case BroadRecordType.MAINSERVER_CREATED:
							BackgroundServiceTask task = new BackgroundServiceTask(
									MainService.TIMER_PUSH, null);
							MainService.setTask(task);
							break;
						default:
							break;
						}
					}
				});
	}

	/**
	 * 开启定位
	 */
	public static void initLocationHelper() {
		locationHelper = new BaiduLocationHelper(EIASApplication.getInstance()
				.getApplicationContext(), false);
		locationHelper.setOperatorListener(new BaiduLoactionOperatorListener() {
			@Override
			public void onSelected(BDLocation location) {
				try {
					pushLatlag(location.getLatitude(), location.getLongitude(),
							location.getCity());
				} catch (Exception e) {
					DataLogOperator.taskHttp("pushLatlag=>位置推推送，异常！",
							e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 推送坐标
	 */
	public static void pushLatlag(double latitude, double longitude, String city)
			throws Exception {
		if (EIASApplication.IsNetworking
				&& EIASApplication.getCurrentUser() != null
				&& EIASApplication.locationHelper != null) {
			if (StringUtil.IsNullOrEmpty(city)) {
				city = currentCity;
			} else {
				city = city.replaceAll("市", "");
			}
			// 设置任务参数
			HashMap<String, Object> para = new HashMap<String, Object>();
			para.put("latitude", latitude);
			para.put("longitude", longitude);
			para.put("city", city);
			currentCity = city;
			// 设置后台运行的任务
			BackgroundServiceTask task = new BackgroundServiceTask(
					MainService.PUSH_LATLNG, para);
			// 添加到任务池中
			MainService.setTask(task);
		}
	}

	// }}

	// {{ 获取触摸距离
	/**
	 * 获取滑动距离
	 * 
	 * @return
	 */
	public static float getTouch_DISTANCE() {
		float result = -1;
		if (!isTablet(EIASApplication.getInstance().getApplicationContext())) {
			result = 20;
		} else {
			result = 30;
		}
		return result;
	}

	/**
	 * 判断是否是平板
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	// }}

	// {{通用提示信息

	/**
	 * 连接服务器失败
	 */
	public static String tips_unconcention = "连接服务器失败";

	// }}
}
