package com.yunfang.eias.viewmodel;

import java.util.ArrayList;

import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.UserTaskInfo;
import com.yunfang.framework.model.ViewModelBase;

/**
 * 对应HomeActivty的视图
 * @author gorson
 *
 */
public class HomeViewModel extends ViewModelBase {	
	/**
	 * 当前用户的任务信息值
	 */
	public UserTaskInfo currentUserTaskInfo = null;
	
	/**
	 * 当前指定的升级的勘察匹配表信息
	 */
	public DataDefine currentUpdateDataDefine = null;
	
	/**
	 * 当用需要同步升级的配置表信息
	 */
	public ArrayList<DataDefine> currentUpdateDataDefines = null;
}
