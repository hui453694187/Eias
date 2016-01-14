package com.yunfang.framework.view;

import com.yunfang.framework.R;
import com.yunfang.framework.base.ILoadingUtil;
import com.yunfang.framework.utils.DialogUtil;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadingUtil implements ILoadingUtil {
	// {{ loading 相关控件
	/**
	 * 点击登陆按钮的动画效果相关
	 */
	Dialog dialog_loading;

	/**
	 * loading的视图
	 */
	View view_loading;

	/**
	 * 图片
	 */
	ImageView iv_loading;

	/**
	 * 动态
	 */
	Animation animation;

	/**
	 * 加载字体
	 */
	TextView loadingTxt;

	// }}

	// {{ loading 显示与关闭
	/**
	 * 显示loading框
	 * 
	 * @param loadingText
	 *            :加载框显示的文字信息
	 */
	public void showLoading(String loadingText) {
		if (!dialog_loading.isShowing()) {
			loadingTxt.setText(loadingText);
			iv_loading.startAnimation(animation);
			dialog_loading.show();
		}
	}

	/**
	 * 关闭loading框
	 */
	public void closeLoading() {
		if (dialog_loading.isShowing()) {
			dialog_loading.dismiss();
			iv_loading.clearAnimation();
		}
	}

	/**
	 * 显示loading框
	 * 
	 */
	public void startAnimation() {
		iv_loading.startAnimation(animation);
	}

	/**
	 * 关闭loading框
	 */
	public void clearAnimation() {
		iv_loading.clearAnimation();
	}

	// }}

	/**
	 * 设置当前Context
	 * 
	 * @param context
	 */
	@SuppressLint("InflateParams")
	public void setContext(Context context) {
		animation = AnimationUtils
				.loadAnimation(context, R.anim.progress_round);
		view_loading = LayoutInflater.from(context).inflate(
				R.layout.loading_layout, null);
		view_loading.getBackground().setAlpha(200);
		iv_loading = (ImageView) view_loading.findViewById(R.id.iv_loading);
		loadingTxt = (TextView) view_loading.findViewById(R.id.loadingTxt);
		dialog_loading = null;
		dialog_loading = DialogUtil.loadingDialog(context, view_loading);
	}

	/**
	 * 图片加载动画
	 * 
	 * @param context
	 *            :所在界面
	 */
	public LoadingUtil() {

	}

	/**
	 * 图片加载动画
	 * 
	 * @param context
	 *            :所在界面
	 * @param imageView
	 *            :需要设置动画的图片
	 * @param duration
	 *            :没多少毫秒转动1圈
	 */
	@SuppressLint("InflateParams")
	public LoadingUtil(Context context, ImageView imageView, long duration) {
		animation = AnimationUtils
				.loadAnimation(context, R.anim.progress_round);
		animation.setDuration(duration);
		view_loading = LayoutInflater.from(context).inflate(
				R.layout.loading_layout, null);
		view_loading.getBackground().setAlpha(200);
		iv_loading = imageView;
	}
}
