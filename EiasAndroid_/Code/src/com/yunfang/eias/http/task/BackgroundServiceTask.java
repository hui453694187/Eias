package com.yunfang.eias.http.task;

import java.util.Map;

/**
 * 后台任务类
 * 
 * @author 贺隽
 * 
 */
public class BackgroundServiceTask
{
	//{{ 属性相关
	/**
	 * 执行任务方式
	 */
	private int serviceTaskId;

	/**
	 * 执行任务需要的参数
	 */
	private Map<String, Object> taskParam;
	
	//}}
	
	//{{ 构造方法

	/**
	 * 构造函数
	 * @param serviceTaskId 执行任务编号
	 * @param param 执行任务需要的参数
	 */
	public BackgroundServiceTask(int serviceTaskId,Map<String, Object> param)
	{
		this.serviceTaskId = serviceTaskId;
		this.taskParam = param;
	}
		
	//}}

	//{{ 获取变量的方法
	/**
	 * 获取执行任务方式
	 * @return
	 */
	public int getServiceTaskId()
	{
		return this.serviceTaskId;
	}

	/**
	 * 获取参数
	 * @return
	 */
	public Map<String, Object> getTaskParam()
	{
		return taskParam;
	}

	/**
	 * 设置参数
	 * @param taskPrarm
	 */
	public void setTaskParam(Map<String, Object> taskPrarm)
	{
		this.taskParam = taskPrarm;
	}
	
	//}}
	
	//{{ 任务参数
	
	/**
	 * 任务编号
	 */
	public static final String PARAM_TASK_ID = "TASK_ID";
	//}}

}
