package com.yunfang.framework.utils;

import java.util.Stack;

import android.app.Activity;

/**
 * 功能：递规退出系统，删除某个特定的activity
 * @author gorson
 *
 */
public class AppManager {
	
	//==============相关的属性start===================//
	private  static Stack<Activity> activityStack;
	private static AppManager instance;
	//==============相关的属性end===================//
	
	public AppManager(){
		
	}
	/**
	 * 安全类单例模式
	 * @return
	 */
	public static AppManager getAppManager(){
		if(instance==null){
			synchronized (instance) {
				if(instance==null){
					instance=new AppManager();
				}
			}
			
		}
		return instance;
	}
	/**
	 * 添加Activity到stack中
	 * @param activity
	 */
	public static void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	/**
	 *  获取当前Activity（堆栈中最后一个压入的）
	 * @return
	 */
	public Activity currentActivity(){
		Activity lastElement = activityStack.lastElement();
		return lastElement;
	}
	
	/**
	 * 结束当前的activity
	 */
	public static void finishLastActivity(){
		Activity lastElement = activityStack.lastElement();
		if(lastElement!=null){
			lastElement.finish();
			activityStack.remove(lastElement);
		}
	}
	
	/**
	 *删除某一个特定的activity 
	 * @param activity
	 */
	public static void finishActivity(Activity activity){
		if(activity!=null){
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	
	/**
	 * 删除某一个指定的activity
	 * @param cls
	 */
	public static void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls)){
				activityStack.remove(activity);
				activity.finish();
				activity=null;
			}
		}
	}
	
	/**
	 * 结束所有的activity
	 */
	public static void finishAllActivity(){
		for (Activity activity : activityStack) {
			if(activity!=null){
				activity.finish();
			}
		}
		activityStack.clear();
	}
	

}
