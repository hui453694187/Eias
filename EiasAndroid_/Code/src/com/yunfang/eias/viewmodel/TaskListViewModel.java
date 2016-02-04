package com.yunfang.eias.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yunfang.eias.enumObj.SortType;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.ui.HomeActivity;
import com.yunfang.framework.model.ViewModelBase;

/**   
 *    
 * 项目名称：WaiCai   
 * 类名称：TaskModel   
 * 类描述： 任务的ViewModel
 * 创建人：lihc   
 * 创建时间：2014-4-17 下午2:29:30   
 * @version        
 */ 
public class TaskListViewModel extends ViewModelBase {

	/**
	 * 任务信息
	 * */
	public ArrayList<TaskInfo> taskInfoes = new ArrayList<TaskInfo>();

	/**
	 * 当前页码
	 */
	public int currentIndex=1;

	/**
	 * 每页显示数据
	 */
	public int pageSize = 20;//EIASApplication.PageSize;

	/**
	 * 远程总页数
	 */
	public int remoteTotal = 1;
	
	/** 任务总数 */
	public int remoteTaskCount=0;

	/**
	 * 本地自建项目
	 */
	public int localTotal = 0;

	/**
	 * 记录列表当前的位置
	 */
	public int listItemCurrentPosition=0;

	/**
	 * HomeActivity
	 */
	public HomeActivity homeActivity;

	/**
	 * 当前任务列表的状态
	 */
	public TaskStatus taskStatus;

	/**
	 * 当前任务信息，点击或长按时获取
	 */
	public TaskInfo currentSelectedTask;

	/**
	 * 被复制的任务信息
	 */
	public TaskInfo currentCopiedTask;

	/**
	 * 被复制的任务分类信息
	 */
	public ArrayList<TaskCategoryInfo> currentCopiedTaskCategories;

	/**
	 * 是否重新加载
	 */
	public boolean reload = true;

	/**
	 * 选中的复制项
	 * */
	public HashMap<String, String> selectedCategoryItems;	
	
	/**
	 * 选中的复制项子项
	 * */
	public HashMap<String, String[]> selectedCategoryChildItems;
	
	/**
	 * 记录上一个Fragment的名称 用于返回
	 */
	public String beforeFragmentName;
	
	/**
	 * 隐藏断网时的提示信息(’提交中‘提交完成时会一起刷新已完成和提交中,当断网情况下提交中的信息会以通知的形式写出，此时隐藏掉刷新'已完成''待提交'列表中服务器返回的断网信息)
	 */
	public Boolean hideOfflineMsg = false;
	
	/**
	 * 在已完成任务列表中只显示完成报告的任务
	 */
	public Boolean onlyReportFinish = false;
	
	//排序方式
	public SortType sortType;
	
	//{{ 用户自定义排序

	public RadioGroup search_bar_rdg;
	
	/**
	 * 按创建日期
	 */
	public RadioButton order_by_create;
	
	/**
	 * 按领取时间
	 */
	public RadioButton order_by_receive;
	
	/**
	 * 按状态
	 */
	public RadioButton order_by_status;
	
	/**
	 * 按完成日期
	 */
	public RadioButton order_by_finish;
	
	public ImageView create_img;
	public ImageView receive_img;
	public ImageView status_img;
	public ImageView finish_img;
	
	
	
	//}}
}
