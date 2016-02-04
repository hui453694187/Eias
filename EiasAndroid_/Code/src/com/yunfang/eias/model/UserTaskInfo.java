package com.yunfang.eias.model;

import org.json.JSONObject;

/**
 * 用户任务信息表
 * @author gorson
 *
 */
public class UserTaskInfo {
	/**
	 * 待提交总数
	 */
	public int ReceivedTotals=0;
	
	/**
	 * 待提交中正常任务数量
	 */
	public int ReceivedNormal=0;
	
	/**
	 * 待提交中紧急任务数量
	 */
	public int ReceivedUrgent=0;
	
	/**
	 * 待领取总数
	 */
	public int NonReceivedTotals=0;
	
	/**
	 * 待领取中正常任务数量
	 */
	public int NonReceivedNormal=0;
	
	/**
	 * 待领取中紧急任务数量
	 */
	public int NonReceivedUrgent=0;
	
	//{{ 构造函数
	/**
	 * 构造函数
	 */
	public UserTaskInfo(){
		
	}
	/**
	 * 初始一个用户信息
	 * @param obj   UserInfo的JsonObject对象
	 */
	public UserTaskInfo(JSONObject obj){	
		this.ReceivedTotals = obj.optInt("ReceivedTotals");
		this.ReceivedNormal = obj.optInt("ReceivedNormal");
		this.ReceivedUrgent = obj.optInt("ReceivedUrgent");
		this.NonReceivedTotals = obj.optInt("NonReceivedTotals");
		this.NonReceivedNormal = obj.optInt("NonReceivedNormal");
		this.NonReceivedUrgent = obj.optInt("NonReceivedUrgent");
	}
	//}}
}