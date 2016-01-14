package com.yunfang.framework.base;

import com.yunfang.framework.utils.ObjectUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
/**
 * 可处理耗时的工作
 * @author gorson
 *
 */
public abstract class BaseWorkerFragment extends BaseFragment {

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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mHandlerThread=new HandlerThread("activity worker:"+getClass().getSimpleName());
		mHandlerThread.start();
		mBackgroundHandler=new BackgroundHandler(mHandlerThread.getLooper());
		loadingWorker = (ILoadingUtil) ObjectUtil.createInstance(BaseApplication.getInstance().getLoadingWorkerType());
		loadingWorker.setContext(this.mActivity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mBackgroundHandler!=null&&mBackgroundHandler.getLooper()!=null){
			mBackgroundHandler.getLooper().quit();
		}
	}


	/**
	 * 后台任务处理
	 */
	protected abstract void handlerBackgroundHandler(Message msg);

	/**
	 * 发送信息到后台
	 * @author gorson
	 *
	 */
	protected void sendBackgroundMessage(Message msg){
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 发送信息到后台处理
	 * @author gorson
	 *
	 */

	protected void sendEmptyMessage(int what){
		mBackgroundHandler.sendEmptyMessage(what);
	}

	/**
	 * 自定义异步消息处理类，用于后台消息的处理
	 * */
	protected class BackgroundHandler extends Handler{

		BackgroundHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handlerBackgroundHandler(msg);
		}
	}
}
