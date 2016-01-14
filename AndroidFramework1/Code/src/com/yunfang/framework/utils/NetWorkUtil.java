package com.yunfang.framework.utils;

import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.enumObj.NetType;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 获取当前设备网络类型
 * @author gorson
 *
 */
public class NetWorkUtil {

	private NetWorkUtil() {

	}

	/**
	 * 网络类型
	 * 
	 */
	public static class NetworkType {

	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvaliable() {
		return (NetType.Type_unknown.getName().endsWith(getNetworkType()
				.getName()));
	}

	/**
	 * 判断网络类型　２G，３G　，WIFI
	 * 
	 * @param context
	 * @return
	 */
	public static NetType getNetworkType() {
		ConnectivityManager cm = (ConnectivityManager) BaseApplication
				.getInstance().getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null) {
			return NetType.getEnumByValue(4);
		}
		if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return NetType.getEnumByValue(3);
		}

		if (netInfo.getType() == TelephonyManager.NETWORK_TYPE_LTE) {
			return NetType.getEnumByValue(2);
		}

		TelephonyManager tm = (TelephonyManager) BaseApplication.getInstance()
				.getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		int netType = tm.getNetworkType();
		if (netType == TelephonyManager.NETWORK_TYPE_GPRS
				|| netType == TelephonyManager.NETWORK_TYPE_EDGE
				|| netType == TelephonyManager.NETWORK_TYPE_CDMA
				|| netType == TelephonyManager.NETWORK_TYPE_1xRTT
				|| netType == 11) {
			return NetType.getEnumByValue(0);
		}
		return NetType.getEnumByValue(1);
	}

}
