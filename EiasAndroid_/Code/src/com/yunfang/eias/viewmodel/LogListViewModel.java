package com.yunfang.eias.viewmodel;

import java.util.ArrayList;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.model.DataLog;
import com.yunfang.eias.ui.HomeActivity;
import com.yunfang.framework.model.ViewModelBase;

/**   
 *    
 * 项目名称：外业采集项目   
 * 类名称：LogListViewModel   
 * 类描述： 日志的ViewModel
 * 创建人：贺隽   
 * 创建时间：2014-6-18 下午3:29:30   
 * @version 1.0.0.1
 */ 
public class LogListViewModel extends ViewModelBase {

	/**
	 * 任务信息
	 * */
	public ArrayList<DataLog> logs = new ArrayList<DataLog>();

	/**
	 * 当前页码
	 */
	public int currentIndex=1;

	/**
	 * 本地自建项目
	 */
	public int localTotal = 0;
	
	/**
	 * 每页显示数量
	 */
	public int pageSize = EIASApplication.PageSize <= 6 ? 15 : EIASApplication.PageSize;

	/**
	 * 记录列表当前的位置
	 */
	public int listItemCurrentPosition=0;

	/**
	 * 当前任务信息，点击或长按时获取
	 */
	public DataLog currentSelectedTask;

	/**
	 * 是否重新加载
	 */
	public boolean reload = true;
	
	/**
	 * HomeActivity
	 */
	public HomeActivity homeActivity;
}
