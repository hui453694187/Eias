package com.yunfang.framework.base;

import com.yunfang.framework.utils.ObjectUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 描述:可处理耗时操作的activity
 * 
 * @author chenys
 * @since 2013-7-29 上午10:54:00
 */
public abstract class BaseWorkerFragmentActivity extends BaseFragmentActivity {

	//{{相关属性
	/**
	 * 异步消息处理线程
	 * */
	private HandlerThread mHandlerThread;
	
	/**
	 * 后台消息处理
	 * */
	protected BackgroundHandler mBackgroundHandler;
	
	/**
	 * 加载类
	 */
	public ILoadingUtil loadingWorker;
	//}}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandlerThread = new HandlerThread("activity worker:" + getClass().getSimpleName());
		mHandlerThread.start();
		mBackgroundHandler = new BackgroundHandler(mHandlerThread.getLooper());

		loadingWorker = (ILoadingUtil) ObjectUtil.createInstance(BaseApplication.getInstance().getLoadingWorkerType());
		loadingWorker.setContext(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBackgroundHandler != null && mBackgroundHandler.getLooper() != null) {
			mBackgroundHandler.getLooper().quit();
		}
	}

	/**
	 * 处理后台操作
	 */
	protected abstract void handleBackgroundMessage(Message msg);

	/**
	 * 发送后台操作
	 * 
	 * @param msg
	 */
	protected void sendBackgroundMessage(Message msg) {
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 发送后台操作
	 * 
	 * @param what
	 */
	protected void sendEmptyBackgroundMessage(int what) {
		mBackgroundHandler.sendEmptyMessage(what);
	}

	/**
	 * 自定义异步消息处理类，用于后台消息的处理
	 * */
	public class BackgroundHandler extends Handler {

		BackgroundHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handleBackgroundMessage(msg);
		}
	}
}
