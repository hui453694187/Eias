package com.yunfang.framework.base;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.iUtils.IDownloadResultHelper;
import com.yunfang.framework.iUtils.ILogHelper;
import com.yunfang.framework.utils.ScreenManager;

/**
 * Android系统的Application基类
 * @author gorson
 *
 */
public class BaseApplication extends Application {
	// {{
	/**
	 * 整个应用的上下文
	 * */
	private static BaseApplication mApplication;	
	
	/**
	 * 记录所有被打的Activity
	 */
	public static ArrayList<Activity> runningActivity = new ArrayList<Activity>();
	//{{ 系统加载框
	/**
	 * 系统加载框
	 */
	private Class<?> loadingWorkerType;

	/**
	 *  写日志辅助类实例
	 */
	private static ILogHelper logArchitecture;
	/**
	 *  下载辅助类实例
	 */
	public static IDownloadResultHelper downloadHelper;
	//{{定义Activity管理器 
	  private ScreenManager screenManager = null; 
	  public ScreenManager getActivityManager() { 
	        return screenManager; 
	    } 
	  public void ScreenManager(ScreenManager screenManager) { 
	        this.screenManager = screenManager; 
	    } 
	//}}
	/**
	 * 设置系统加载框实现类
	 * @param appLoadingWorker
	 */
	public void setLoadingWorkerType(Class<?> appLoadingWorkerType){
		this.loadingWorkerType = appLoadingWorkerType;
	}

	/**
	 * 获取系统加载框实现类
	 * @return
	 */
	public Class<?> getLoadingWorkerType(){
		return loadingWorkerType;
	}
	//}}

	@Override
	public void onCreate() {

		super.onCreate();
		mApplication = this;	
		
//		SDKInitializer.initialize(this);
		//初始化自定义Activity管理器 
		screenManager = ScreenManager.getScreenManager(); 
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.i("low memory", "low memory!");
	}

	/**
	 * 提供一个获取整个应用上下文的方法
	 * */
	public static BaseApplication getInstance() {
		return mApplication;
	} 
	
	/**
	 * 设置日志实现类
	 * @param entity
	 */
	public static void setILogArchitecture(ILogHelper entity){
		logArchitecture = entity;
	}
	
	/**
	 * 获取日志接口
	 * @return
	 */
	public  static  ILogHelper getLogArchitecture()
	{
		return logArchitecture;
	}

	/**
	 * 获取包信息。info.versionCode可以取得当前的版本号
	 * */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (info == null) {
			info = new PackageInfo();
		}
		return info;
	}

	//{{ 下载

		/**
		 * 安卓内置下载管理器  如果用户自己禁用了这个 将无法完成下载功能
		 */
		public static DownloadManager mgr = null;

		/**
		 * 下载完成
		 */
		private BaseBroadcastReceiver onDownloadComplete;

		/**
		 * 初始化下载管理器
		 */
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		protected void initDownloadManager(IDownloadResultHelper entity) {
			downloadHelper = entity;
			mgr = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);			
			// 声明消息接收对象
			ArrayList<String> temp = new ArrayList<String>();		
			temp.add(DownloadManager.ACTION_DOWNLOAD_COMPLETE);		
			temp.add(DownloadManager.ACTION_NOTIFICATION_CLICKED);
			onDownloadComplete = new BaseBroadcastReceiver(mApplication.getApplicationContext(),temp);
			onDownloadComplete.setAfterReceiveBroadcast(new afterReceiveBroadcast() {			
				@Override
				public void onReceive(final Context context, Intent intent) {
					String actionType =intent.getAction();		
					switch(actionType){
					case DownloadManager.ACTION_DOWNLOAD_COMPLETE: //下载完成
						if (downloadHelper!=null)
						{
							downloadHelper.downloadComplete();
						}
						break;
					case DownloadManager.ACTION_NOTIFICATION_CLICKED: //通知下载
						if (downloadHelper!=null)
						{
							downloadHelper.notificationClicked();
						}
						break;
					default:
						break;
					}
				}
			});
		}

		//}}
	
	/**
	 * App中所有分页控件中默认的PageSize值
	 */
	public static int PageSize = 20;

	//{{ 当前APP使用的PackageInfo，主要用于反射
	private ArrayList<String> appPackageInfoes = new ArrayList<String>();
	/**
	 *  用于设置当前APP使用的PackageInfo，主要用于反射
	 *  用在XML解释、Json解释中，一般记录DTO和Model所在的PackageInfo路径
	 */
	public void setPackageInfoNames(ArrayList<String> packageInfoes){
		appPackageInfoes = packageInfoes; 
	}
	/**
	 *	获取用户设定的当前APP使用的PackageInfo，主要用于反射
	 *  用在XML解释、Json解释中，一般记录DTO和Model所在的PackageInfo路径
	 */
	public ArrayList<String> getPackageInfoNames(){
		return appPackageInfoes;
	}
	//}}
}
