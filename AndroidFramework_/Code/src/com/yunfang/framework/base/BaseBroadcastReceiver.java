/**
 * 
 */
package com.yunfang.framework.base;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author gorson
 *
 */
public class BaseBroadcastReceiver {

	/**
	 * 当前上下文
	 */
	private Context currentContext;

	/**
	 * 广播的Action值
	 */
	private ArrayList<String> actionStr;

	/**
	 * 广播消息接收响应事件
	 */
	private afterReceiveBroadcast myBroadcastReceiver;

	/**
	 * 构造函数
	 * @param context
	 */
	public BaseBroadcastReceiver(Context context,ArrayList<String> actionString){
		currentContext = context;
		actionStr = actionString;
		registerBroadCast();
	}

	public BaseBroadcastReceiver(Context context,String actionString){
		currentContext = context;
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(actionString);
		actionStr = temp;
		registerBroadCast();
	}

	/**
	 * 注册广播事件
	 */
	private void registerBroadCast() {
		IntentFilter filter =  new IntentFilter();
		if(actionStr != null || actionStr.size()>0){
			for(String action : actionStr){
				filter.addAction(action);
			}
		}
		currentContext.registerReceiver(mReceier, filter);
	}

	/**
	 * 网络变化时，图票发生相应的改变
	 */
	private BroadcastReceiver mReceier = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent){
			if(myBroadcastReceiver!=null){
				myBroadcastReceiver.onReceive(currentContext, intent);
			}
		}
	};

	/**
	 * 移除监听事件
	 */
	public void unregisterReceiver(){
		currentContext.unregisterReceiver(mReceier);
	}

	/**
	 * 确定按钮监听器,返回得到的地图结果
	 * @author gorson
	 *
	 */
	public interface afterReceiveBroadcast{
		/**
		 * 返回广播接受的内容
		 */
		public void onReceive(Context context, Intent intent);
	}

	/**
	 * 设置确定事件后的响应事件
	 * @param r 响应事件
	 */
	public void setAfterReceiveBroadcast(afterReceiveBroadcast r){
		this.myBroadcastReceiver = r;
	}
}
