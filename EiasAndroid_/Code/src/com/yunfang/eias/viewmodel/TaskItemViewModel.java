package com.yunfang.eias.viewmodel;

import java.util.ArrayList;

import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.ui.TaskInfoActivity;
import com.yunfang.eias.ui.Adapter.SearchAdapter;
import com.yunfang.framework.model.ViewModelBase;

/**
 * 任务完整信息的视图
 * 
 * @author gorson
 * 
 */
public class TaskItemViewModel extends ViewModelBase
{

	/**
	 * Activity对象
	 */
	public TaskInfoActivity taskInfoActivity;
	
	/**
	 * 任务信息，包含任务下的分类项信息
	 */
	public TaskInfo currentTask;

	/**
	 * 当前选中的分类项
	 */
	public TaskCategoryInfo currentCategory = null;
	
	/**
	 * 需要复制的分类项
	 */
	public TaskCategoryInfo copyCategory = null;

	/**
	 * 当前任务对应的勘察表数据
	 */
	public DataDefine currentDataDefine = null;

	/**
	 * 当前点击的勘察配置表分类项信息
	 */
	public DataCategoryDefine currentDataCategoryDefine = null;	
	
	/**
	 * 勘察任务分类项
	 */
	public ArrayList<TaskCategoryInfo> currentTaskCategoryInfos = null;
	
	/**
	 * 子项的下拉列表数据
	 */
	public ArrayList<String> currentDropDownListData = null;
		
	/**
	 * 当前任务列表的状态
	 */
	public TaskStatus taskStatus;
	
	/**
	 * 是否刷新
	 */
	public Boolean reload;
	
	/** 是否已经选中的图片类型  *//*
	public boolean isSelectPicType=true;
	
	*//** 默认选中的分类想，即顶部AutoCompTextView下拉框中的值 */
	public String selectPicType;
	
	/**
	 * 选中的分类项子项的位置信息
	 */
	public int position;
	
	/**
	 * 用于资源信息的类型过滤数据适配
	 */
	@SuppressWarnings("rawtypes")
	public SearchAdapter searchAdapter;
}
