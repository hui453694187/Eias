/**
 * 
 */
package com.yunfang.eias.viewmodel;

import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.model.ViewModelBase;

/**
 * @author Administrator
 *
 */
public class UserInfoViewModel extends ViewModelBase
{
	/**
	 * 用户信息
	 */
	public UserInfo userInfo;

	/**
	 * 用户文件夹路径
	 */
	public String userDirectory;

	/**
	 * 在屏幕开始碰触的水平位置
	 * */
	public float touchStartX;

	/**
	 * 在屏幕开始碰触的垂直位置
	 * */
	public float touchStartY;

	/**
	 * 移动的X位置
	 */
	public float moveX = 0;

	/**
	 * 移动的Y位置
	 */
	public float moveY = 0;
	
	/**
	 * 允许在屏幕触发左滑动和右滑动的滑动距离
	 * */
	public float TOUCH_DISTANCE =-1;
}
