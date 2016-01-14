package com.yunfang.framework.base;

import android.content.Context;

/**
 * 加载框接口
 * @author gorson
 *
 */
public interface ILoadingUtil {
	/**
	 * 显示loading框
	 * 
	 * @param loadingText
	 *            :加载框显示的文字信息
	 */
	public void showLoading(String loadingText);

	/**
	 * 关闭loading框
	 */
	public void closeLoading();

	/**
	 * 显示loading框
	 * 
	 */
	public void startAnimation();

	/**
	 * 关闭loading框
	 */
	public void clearAnimation();
	
	/**
	 * 设置当前Context
	 * @param context
	 */
	public void setContext(Context context);
}
