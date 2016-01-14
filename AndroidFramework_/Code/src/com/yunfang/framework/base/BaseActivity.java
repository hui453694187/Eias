package com.yunfang.framework.base;

import com.yunfang.framework.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

/**
 * 具备UI线程的刷新处理功能
 * 
 * @author gorson
 * 
 */
@SuppressLint("HandlerLeak")
public class BaseActivity extends Activity {

	//{{ 相关属性start
	
	/**
	 * 异步消息处理的线程
	 * */
	protected HandlerThread mHandlerThread;

	//}}相关属性
	
	/**
	 * Activity的生命周期：创建
	 * */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//加入Activity栈
		BaseApplication application = (BaseApplication) this.getApplication(); 
		application.getActivityManager().pushActivity(this); 
		BaseApplication.runningActivity.add(this);
	}

	/**
	 * 异步消息处理
	 * */
	protected Handler mUiHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handleUiMessage(msg);
		};

	};

	/**
	 * 处理UI线程
	 * 
	 * @param msg
	 */
	protected void handleUiMessage(Message msg) {

	}

	/**
	 * 发送信息给UI线程
	 * 
	 * @param msg
	 */
	protected void sendUiMessage(Message msg) {
		mUiHandler.sendMessage(msg);

	}

	/**
	 * 发送空信息给UI线程
	 * 
	 * @param msg
	 */
	protected void sendEmptyMessage(int what) {
		mUiHandler.sendEmptyMessage(what);
	}

	/**
	 * 根据指定字符串显示信息提示
	 * 
	 * @param msg ：指定的字符串信息
	 */
	public void showToast(String msg) {
		ToastUtil.longShow(this, msg);
	}

	/**
	 * 根据指定资源ID显示信息提示 
	 * 
	 * @param resId：资源ID
	 */
	public void showToast(int resId) {		
		ToastUtil.longShow(this, resId);
	}

	/**
	 * 隐藏软件键盘
	 * 
	 * @param msg
	 */
	protected void hideSoftInput(Context context) {
		InputMethodManager manager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (BaseActivity.this.getCurrentFocus() != null) {
			manager.hideSoftInputFromInputMethod(BaseActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 打开或者关闭软件键盘
	 * 
	 * @param context
	 */
	protected void showsoftInput(Context context) {
		InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	/**
	 * 销毁
	 * */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//退出Activity栈
		BaseApplication application = (BaseApplication) this.getApplication(); 
		application.getActivityManager().removeActivityOnList(this); 
		BaseApplication.runningActivity.remove(this);
	}

	@Override
	protected void onPostResume() {
		try {
			super.onPostResume();
		} catch (Exception e) {
		    BaseApplication.getLogArchitecture().insertLog(this.getComponentName().getClassName()+" onPostResume Errer" + e.getMessage());		    
		}		
	}

	@Override
	protected void onRestart() {
		try {
			super.onRestart();
		} catch (Exception e) {
		    BaseApplication.getLogArchitecture().insertLog(this.getComponentName().getClassName()+" onRestart Errer" + e.getMessage());		    
		}
	}

	@Override
	protected void onResume() {
		try {
			super.onResume();
		} catch (Exception e) {
		    BaseApplication.getLogArchitecture().insertLog(this.getComponentName().getClassName()+" onResume Errer" + e.getMessage());		    
		}
	}
	
	
}
