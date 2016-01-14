package com.yunfang.framework.base;

import com.yunfang.framework.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 描述：所有Fragment 的父类，提供刷新UI的Handler
 * 
 * @author gorson ：
 */
@SuppressLint("HandlerLeak")
public class BaseFragment extends Fragment {

	// {{相关属性
	/**
	 * 布局视图
	 * */
	protected View mView;
	/**
	 * Activity
	 * */
	protected Activity mActivity;

	/**
	 * 异步消息处理
	 * */
	protected Handler mUiHandler;

	// }}

	/**
	 * 设置显示的Fragment的id
	 * 
	 * @param index
	 */
	public void setShowIndex(int index) {
		Bundle args = new Bundle();
		args.putInt("index", index);
		setArguments(args);
	}

	/**
	 * 获取显示的Fragment的id
	 * 
	 * @return
	 */
	public int getShowIndex() {
		return getArguments() == null ? 0 : getArguments().getInt("index");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mView = getView();
		mActivity = getActivity();
		mUiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (isAdded()) {
					super.handleMessage(msg);
					handUiMessage(msg);
				}
			}
		};

	}

	/**
	 * 发送UI更新任务
	 * 
	 * @param msg
	 */
	protected void handUiMessage(Message msg) {

	}

	/**
	 * 发送UI任务
	 * 
	 * @param msg
	 */
	protected void sendUiMessage(Message msg) {
		mUiHandler.sendMessage(msg);
	}

	/**
	 * 发送UI任务
	 * 
	 * @param what
	 */
	protected void sendEmptyMessage(int what) {
		mUiHandler.sendEmptyMessage(what);
	}

	/**
	 * 根据指定字符串显示信息
	 * 
	 * @param msg
	 *            :字符串信息
	 */
	public void showToast(final String msg) {
		if (getActivity() == null) {
			return;
		}
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ToastUtil.longShow(getActivity(), msg);
			}
		});
	}

	/**
	 * 根据资源id显示信息
	 * 
	 * @param resId
	 *            ：资源ID
	 */
	public void showToast(final int resId) {
		if (getActivity() == null) {
			return;
		}
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ToastUtil.longShow(getActivity(), resId);
			}
		});
	}
}
