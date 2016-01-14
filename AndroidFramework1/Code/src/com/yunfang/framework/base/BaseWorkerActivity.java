package com.yunfang.framework.base;

import com.yunfang.framework.utils.ObjectUtil;
//import com.yunfang.framework.view.LoadingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 具备后台线程和UI线程更新
 * @author gorson
 *
 */
public abstract class  BaseWorkerActivity extends BaseActivity {

	//{{相关属性
	/**
	 * 异步消息处理线程
	 * */
	protected HandlerThread mHandlerThread;

	/**
	 * 后台消息处理
	 * */
	protected BackgroundHandler mBackgroundHandler;

	/**
	 * 加载类
	 */
	public ILoadingUtil loadingWorker;
	//}}	

	/**
	 * 
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandlerThread=new HandlerThread("activity worker:"+getClass().getSimpleName());
		mHandlerThread.start();
		mBackgroundHandler=new BackgroundHandler(mHandlerThread.getLooper());

		loadingWorker = (ILoadingUtil) ObjectUtil.createInstance(BaseApplication.getInstance().getLoadingWorkerType());
		loadingWorker.setContext(this);
		
		
	}

	/**
	 * 处理后台操作
	 * @param msg
	 */
	protected abstract void handleBackgroundMessage(Message msg);

	/**
	 * 
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBackgroundHandler!=null&&mBackgroundHandler.getLooper()!=null){
			mBackgroundHandler.getLooper().quit();
		}
	}

	/**
	 * 发送后台操作
	 * @param msg
	 */
	protected void sendBackgroundMessage(Message msg){
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 发送后台操作
	 * @param what
	 */
	protected void sendEmptyBackgroundMessage(int what){
		mBackgroundHandler.sendEmptyMessage(what);
	}

	/**
	 * 自定义异步消息处理类，用于处理后台消息
	 * */
	protected class BackgroundHandler extends Handler{
		/**
		 * 
		 * @param looper
		 */
		public BackgroundHandler(Looper looper) {
			super(looper);
		}

		/**
		 * 
		 */
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handleBackgroundMessage(msg);
		}		
	}
}
