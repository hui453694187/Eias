/**
 * 
 */
package com.yunfang.framework.utils;

/**
 * @author Administrator
 *
 */
import java.util.Stack; 

import android.app.Activity;
/**
 * activity管理器
 * 功能：递规退出系统，删除某个特定的activity
 * @author Administrator
 *
 */
public class ScreenManager { 
	//{{相关的属性start
    private static Stack<Activity> activityStack; 
    private static ScreenManager instance; 
	//}}
    private ScreenManager() { 
    } 
    
    public static ScreenManager getScreenManager() { 
        if (instance == null) { 
            instance = new ScreenManager(); 
        } 
        return instance; 
    } 
    
    /**
     * 退出栈顶Activity 
     * @param activity
     */
    public void popActivity(Activity activity) { 
        if (activity != null) { 
           //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作 
            activity.finish(); 
            activityStack.remove(activity); 
            activity = null; 
        } 
    } 
    /**
     * 仅删除栈中的Activity 
     * @param activity
     */
    public void removeActivityOnList(Activity activity) { 
        if (activity != null) { 
            activityStack.remove(activity);
        } 
    } 
    /**
     * 获得当前栈顶Activity 
     * @return
     */
    public Activity currentActivity() { 
        Activity activity = null; 
       if(!activityStack.empty()) 
         activity= activityStack.lastElement(); 
        return activity; 
    } 
    /**
     * 将当前Activity推入栈中 
     * @param activity
     */
    public void pushActivity(Activity activity) { 
        if (activityStack == null) { 
            activityStack = new Stack<Activity>(); 
        } 
        activityStack.add(activity); 
    } 
    /**
     * 退出栈中其他Activity 直到指定Activity 
     * @param cls
     */
    @SuppressWarnings("rawtypes")
	public void popAllActivityExceptOne(Class cls) { 
        while (true) { 
            Activity activity = currentActivity(); 
            if (activity == null) { 
                break; 
            } 
            if (activity.getClass().equals(cls)) { 
                break; 
            } 
            popActivity(activity); 
        } 
    }

	
	/**
	 * 结束所有的activity
	 */
	public void finishAllActivity(){
		for (Activity activity : activityStack) {
			if(activity!=null){
				activity.finish();
			}
		}
		activityStack.clear();
	}
} 
