package com.yunfang.framework.base;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public abstract class BaseBackgroundService extends Service {

	//{{相关的属性
	/**
	 * 异步消息处理线程
	 * */
	private HandlerThread mHandlerThread;
	
	/**
	 * 后台消息处理
	 * */
	protected static BackgroundHandler mbackgroundHandler;	
	//}}

	@Override
	public void onCreate() {
		try {
			super.onCreate();
			mHandlerThread = new HandlerThread("service worker:" + getClass().getSimpleName());
			mHandlerThread.start();
			mbackgroundHandler = new BackgroundHandler(mHandlerThread.getLooper());
		} catch (Exception e) {
			BaseApplication.getLogArchitecture().insertLog(this.getPackageCodePath() +" onCreate Errer" + e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHandlerThread.getLooper() != null) {
			mHandlerThread.getLooper().quit();
		}
	}	
	
	/**
	 * 处理后台操作
	 * 
	 * @param msg
	 */
	protected abstract void handleBackgroundMessage(Message msg);

	/**
	 * 发送后台操作
	 * 
	 * @param msg
	 */

	protected void sendBackgroundMessage(Message msg) {
		mbackgroundHandler.sendMessage(msg);
	}

	/**
	 * 发送后台操作
	 */
	protected void sendEmptygroundMessage(int what) {
		mbackgroundHandler.sendEmptyMessage(what);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	/**
	 * 自定义异步消息处理类，用于后台消息的处理
	 * */
	protected class BackgroundHandler extends Handler {
		public BackgroundHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handleBackgroundMessage(msg);
		}
	}
}
