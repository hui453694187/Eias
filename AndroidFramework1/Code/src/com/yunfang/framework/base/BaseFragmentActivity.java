package com.yunfang.framework.base;

import com.yunfang.framework.utils.ObjectUtil;
import com.yunfang.framework.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * 描述：抽象FragmentActivity，提供刷新UI的Handler
 * 
 * @author gorson
 * 
 */
@SuppressLint("HandlerLeak")
public abstract class BaseFragmentActivity extends FragmentActivity {

	//{{相关属性
	/**
	 * 全部填充
	 */
	public LinearLayout.LayoutParams FILL_PARENT = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);

	/**
	 * 按父级大小填充
	 */
	public LinearLayout.LayoutParams WRAP_CONTENT = new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
	
	/**
	 * 加载类
	 */
	public ILoadingUtil loadingWorker;
	
	//}}

	/**
	 * 异步消息处理
	 * */
	protected Handler mUiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			handleUiMessage(msg);
		};
	};

	protected void onCreate(android.os.Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		loadingWorker = (ILoadingUtil) ObjectUtil.createInstance(BaseApplication.getInstance().getLoadingWorkerType());
		loadingWorker.setContext(this);
		//加入Activity栈
		BaseApplication application = (BaseApplication) this.getApplication(); 
		application.getActivityManager().pushActivity(this); 
		BaseApplication.runningActivity.add(this);
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//退出Activity栈
		BaseApplication application = (BaseApplication) this.getApplication(); 
		application.getActivityManager().removeActivityOnList(this); 
		BaseApplication.runningActivity.remove(this);
	}

	/**
	 * 发送更新UI任务
	 * 
	 * @param what
	 */
	protected void handleUiMessage(Message msg) {

	}

	/**
	 * 发送UI更新操作
	 * 
	 * @param what
	 */
	protected void sendUiMessage(Message msg) {
		mUiHandler.sendMessage(msg);
	}

	/**
	 * 发送UI更新操作
	 * 
	 * @param what
	 */
	protected void sendEmptyMessage(int what) {
		mUiHandler.sendEmptyMessage(what);

	}

	/**
	 * 根据指定的字符串显示消息
	 * 
	 * @param msg
	 */
	public void showToast(String msg) {		
		ToastUtil.longShow(getApplicationContext(),msg);
	}

	/**
	 * 根据指定的 资源id显示消息
	 * 
	 * @param resId
	 */
	public void showToast(int resId) {
		ToastUtil.longShow(getApplicationContext(),resId);
	}

	/**
	 * 控制软键盘的显示隐藏
	 * 
	 * @param context
	 */
	public void hideSoftInput(Context context) {
		InputMethodManager manager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (BaseFragmentActivity.this.getCurrentFocus() != null) {
			manager.hideSoftInputFromInputMethod(BaseFragmentActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}

	/**
	 * 控制软键盘的显示隐藏
	 */
	public void showSoftInput() {
		InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

}
