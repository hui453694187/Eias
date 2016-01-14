package com.yunfang.framework.model;


/**
 * ViewModel的基类
 * @author gorson
 *
 */
public class ViewModelBase {
	/**
	 * 构造函数
	 */
	public ViewModelBase(){
		
	}
	
	/**
	 * 当前用户信息
	 */
	public UserInfo currentUser= null;
	
	/**
	 * 获取数据是否成功
	 */
	public boolean GetDataSuccess = true;
	
	/**
	 * 提示的字符串
	 */
	public String ToastMsg="";
}
