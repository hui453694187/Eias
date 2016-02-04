package com.yunfang.eias.dto;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.model.DataLog;
import com.yunfang.framework.utils.DateTimeUtil;

/**   
 *    
 * 项目名称：外业采集项目   
 * 类名称：DataLogDto   
 * 类描述：日志记录 用于给后台传递的参数对象 必须要和 Visual Studio 中的DataLogDTO 一致
 * 创建人：陈惠森 
 * 创建时间：2014-7-17
 * @version 1.0.0.1
 */ 
public class DataLogDTO {
	// {{相关的属性
	/**
	 * 用户Token
	 * */
	public String Token;

	/**
	 * 日志操作记录
	 * */
	public String LogContent;

	/**
	 * 产生日志的时间
	 * */
	public String CreatedDate;

	/**
	 * 操作类型 
	 * 
	 * */
	public String OperatorType;

	/**
	 * 操作界面
	 * 
	 * */
	public String Target;

	// }}

	//{{ 构造函数
	/**
	 * 构造函数
	 * @param log
	 */
	public DataLogDTO(DataLog log){
		this.CreatedDate = log.CreatedDate;
		this.LogContent = log.LogContent;
		this.OperatorType = log.OperatorType.getName();
		this.Target = "安卓客户端界面";
		this.Token = EIASApplication.getCurrentUser().Token;
	}

	public DataLogDTO(){
		this.Token = EIASApplication.getCurrentUser().Token;
		this.CreatedDate = DateTimeUtil.getCurrentTime();
	}
	//}}
}
