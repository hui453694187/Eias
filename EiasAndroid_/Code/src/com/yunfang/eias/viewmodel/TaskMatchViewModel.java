/**
 * 
 */
package com.yunfang.eias.viewmodel;

import java.util.ArrayList;

import com.yunfang.eias.model.TaskInfo;

/**
 * @author Administrator
 * @author sen
 */
public class TaskMatchViewModel {	

	/**
	 * 任务编号
	 */
	public String taskNum;

	/**
	 * 勘察表类型
	 */
	public int ddId;

	/**
	 * 页码
	 */
	public int pageIndex = 1;

	/**
	 * 页数
	 */
	public int pageSize = 10;

	/**
	 * 数据总数
	 */
	public int total = 0;

	/**
	 * 记录列表当前的位置
	 */
	public int listItemCurrentPosition;

	/**
	 * 任务列表
	 */
	public ArrayList<TaskInfo> taskInfos = new ArrayList<TaskInfo>();

	/**
	 * 选中的 服务器端任务信息 不包含 分类项和子项
	 */
	public TaskInfo serverTaskInfo;

	/**
	 * 安卓端 任务信息 不包含 分类项和子项
	 */
	public TaskInfo locTaskInfo;	
}
