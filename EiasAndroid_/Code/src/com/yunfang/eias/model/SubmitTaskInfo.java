package com.yunfang.eias.model;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.TaskUploadStatusEnum;

/**
 * 
 * @author 贺隽
 *
 */
public class SubmitTaskInfo {

	//{{ 相关属性
	
	/**
	 * 标识编号
	 * */
	public int ID;
	
	/**
	 * 任务信息
	 * */
	public TaskInfo CurrentTaskInfo;
		
	/**
	 * 任务状态
	 */
	public TaskUploadStatusEnum SubmitTaskStatus;
	
	/**
	 * 提交时间
	 */
	public String SubmitDateTime;
	
	/**
	 * 完成时间
	 */
	public String FinishDateTime;
	
	/**
	 * 提交人
	 */
	public String UserName;
	
	//}}
	
	//{{  构造函数
	
	/**
	 * 构造函数
	 * @param CurrentTaskInfo:当前需要操作的任务
	 * @param SubmitTaskStatus:提交状态
	 */
	public SubmitTaskInfo(TaskInfo CurrentTaskInfo,
			TaskUploadStatusEnum SubmitTaskStatus) {
		if(CurrentTaskInfo != null){
			this.ID = CurrentTaskInfo.ID;
			this.CurrentTaskInfo = CurrentTaskInfo;
			this.SubmitTaskStatus = SubmitTaskStatus;
			this.SubmitDateTime = "";
			this.FinishDateTime = "";
			this.UserName = EIASApplication.getCurrentUser().toString();
		}
	}
	
	//}}
	
}
