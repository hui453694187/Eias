package com.yunfang.framework.utils;
import java.util.HashMap;
import java.util.Iterator;

import com.yunfang.framework.R;
import com.yunfang.framework.base.BaseApplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

/**
 *  消息通知类
 * @author  sen
 */
@SuppressLint("InlinedApi")
public class NotificationUtils {
	/**
	 * 通知构造器
	 */
	private  NotificationCompat.Builder mBuilder;

	/**
	 * 通知管理器
	 */
	private static NotificationManager notificationManager = null;

	/**
	 * 取得通知管理器
	 */
	public static NotificationManager getNotificationManager(){
		// 获取通知管理器的引用   
		String notificationService = Context.NOTIFICATION_SERVICE; 
		if(notificationManager==null){
			// 初始化通知管理器
			notificationManager = (NotificationManager) BaseApplication.getInstance().getSystemService(notificationService);  
		}
		return notificationManager;
	}

	/**
	 * 弹出一个普通通知（两行显示内容）
	 * @param notificationId 通知ID
	 * @param title 通知标题
	 * @param contentTop 通知上部内容
	 * @param contentBottom 通知下部内容
	 * @param tickerText弹出时手机上端的短暂提示
	 * @param canClickJump 点击是否跳转
	 * @param flagType 消息类型
	 * Notification.FLAG_AUTO_CANCEL:可移除 
	 * Notification.FLAG_NO_CLEAR:不可移除 
	 * Notification.FLAG_INSISTENT:让声音、振动无限循环，直到用户响应 （取消或者打开）
	 * @param turnActivity 跳转的activity（无跳转则填null值）
	 * @param turnParams 跳转activity时传递的参数（无跳转则填null值）
	 */
	public void showNotification(Integer notificationId, String title,
			String contentTop,String contentBottom, String tickerText,Boolean canClickJump,Integer flagType,Class<?> trunActivity,HashMap<String, String> trunParams){
		Context context = BaseApplication.getInstance();
		//先设定RemoteViews
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_view);
		remoteViews.setImageViewResource(R.id.logo_icon, R.drawable.yunfang_logo);
		remoteViews.setTextViewText(R.id.tv_custom_title,title);
		remoteViews.setTextViewText(R.id.tv_content_top, contentTop);
		remoteViews.setTextViewText(R.id.tv_content_bottom, contentBottom);		
		//点击的事件处理
		notifyNotification(notificationId,tickerText,remoteViews,context,canClickJump, flagType,trunActivity,trunParams);
	}

	/**
	 * 弹出一个普通通知（单行显示内容）
	 * @param notificationId 通知ID
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param tickerText 弹出时手机上端的短暂提示
	 * @param canClickJump 点击是否跳转
	 * @param flagType 消息类型
	 * Notification.FLAG_AUTO_CANCEL:可移除 
	 * Notification.FLAG_NO_CLEAR:不可移除 
	 * Notification.FLAG_INSISTENT:让声音、振动无限循环，直到用户响应 （取消或者打开）
	 * @param trunActivity 跳转的activity（无跳转则填null值）
	 * @param trunParams 跳转activity时传递的参数（无跳转则填null值）
	 */
	public void showNotification(Integer notificationId, String title,
			String content, String tickerText,Boolean canClickJump,Integer flagType,Class<?> trunActivity,HashMap<String, String> trunParams){
		Context context = BaseApplication.getInstance();
		//先设定RemoteViews
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_view);
		remoteViews.setImageViewResource(R.id.logo_icon, R.drawable.yunfang_logo);
		remoteViews.setTextViewText(R.id.tv_custom_title,title);
		remoteViews.setTextViewText(R.id.tv_content_top, content);
		remoteViews.setViewVisibility(R.id.tv_content_bottom, View.GONE);
		//remoteViews.setTextViewTextSize(R.id.tv_content_top, TypedValue.COMPLEX_UNIT_SP, 12);
		notifyNotification(notificationId,tickerText,remoteViews,context,canClickJump, flagType,trunActivity,trunParams);
	}

	/**
	 * 执行一个普通通知
	 * @param notificationId 通知ID
	 * @param title 通知标题
	 * @param tickerText 弹出时手机上端的短暂提示
	 * @param context 当前上下文
	 * @param canClickJump 点击是否跳转
	 * @param flagType 消息类型
	 * @param trunActivity 跳转的activity（无跳转则填null值）
	 * @param trunParams 跳转activity时传递的参数（无跳转则填null值）
	 */
	private void notifyNotification(Integer notificationId,String tickerText, RemoteViews remoteViews,Context context,Boolean canClickJump,Integer flagType,Class<?> trunActivity,HashMap<String, String> trunParams){
		mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setContent(remoteViews)
		.setWhen(System.currentTimeMillis()) // 通知产生的时间，会在通知信息里显示
		.setTicker(tickerText) // 弹出时手机上端的短暂提示
		.setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
		.setOngoing(false) //不是正在进行的   true为正在进行  效果和.flag一样
		.setSmallIcon(R.drawable.yunfang_logo);
		if(canClickJump){
			mBuilder.setContentIntent(getDefalutIntent(context,flagType,trunActivity,trunParams));// 点击后跳转
		}
		Notification notify = mBuilder.build();
		notify.contentView = remoteViews;
		notify.flags = flagType; 
		getNotificationManager().notify(notificationId, notify);
	}

	/**
	 * 取得点击后需要跳转的位置
	 * @param context 内容
	 * @param flags  标记
	 * @param turnActivity 跳转到的activity
	 * @param Inentmap 跳转时要传递的参数
	 * @return
	 */
	public PendingIntent getDefalutIntent(Context context, int flags,Class<?> trunActivity,HashMap<String, String> trunParams){
		// 若传入的activity为空时则点击跳到默认页
		Intent intent = new Intent();
		// 若传入的avtivity不为空时，则设置改intent的值并点击通知后跳到该activity，trunParams为传递时传递的参数
		if(trunActivity!=null){
			intent = new Intent(context,trunActivity);
			if(trunParams!=null){
				Iterator<String> itor = trunParams.keySet().iterator();  
				while(itor.hasNext())  {  
					String key = (String)itor.next();  
					String value = trunParams.get(key);  
					intent.putExtra(key,value);
				}  
			}
		}
		PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, intent, flags);
		return pendingIntent;
	}
}
