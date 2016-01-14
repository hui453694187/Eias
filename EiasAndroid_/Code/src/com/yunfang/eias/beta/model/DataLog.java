package com.yunfang.eias.beta.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.yunfang.eias.beta.enumObj.LogType;
import com.yunfang.eias.beta.enumObj.OperatorTypeEnum;
import com.yunfang.framework.db.TableWorkerBase;
import com.yunfang.framework.utils.DateTimeUtil;

/**
 * 
 * 项目名称：外采系统 类名称：日志记录表 类描述：日志记录表对应的实体类 创建人：贺隽 创建时间：2014-6-17 上午11:42:59
 * 
 * @version 1.0.0.1
 */
@SuppressWarnings("serial")
public class DataLog extends TableWorkerBase {

	// {{相关的属性
	/**
	 * 自增ID
	 * */
	public int ID;

	/**
	 * 用户ID
	 * */
	public String UserID;

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
	public OperatorTypeEnum OperatorType;

	/** 记录日志类型 */
	public LogType logType;

	// }}

	// {{构造函数
	/**
	 * 无参构造，设置默认值
	 * */
	public DataLog() {
		super();
		UserID = "";
		LogContent = "";
		CreatedDate = DateTimeUtil.getCurrentTime();
		logType = LogType.getEnumByIndex(1);// 默认为用户操作
	}

	/**
	 * 构建对象
	 * 
	 * @param obj
	 * @throws JSONException
	 */
	public DataLog(JSONObject obj) throws JSONException {
		UserID = obj.optString("UserID");
		LogContent = obj.optString("LogContent");
		CreatedDate = obj.optString("CreatedDate");
		OperatorType = OperatorTypeEnum.getEnumByValue(obj
				.optInt("OperatorType"));
		logType = LogType.getEnumByIndex(obj.optInt("LogType"));
	}

	/**
	 * 有参数构造
	 * */
	public DataLog(String userID, String logContent, String createdDate,
			Integer operatorType, LogType logType) {
		super();
		UserID = userID;
		LogContent = logContent;
		CreatedDate = createdDate;
		OperatorType = OperatorTypeEnum.getEnumByValue(operatorType);
		this.logType = logType;
	}

	// }}

	// {{ getContentValues 获取勘察任务信息表（TaskInfo）要插入的参数
	/**
	 * 将勘察任务信息表所有的参数封装成ContentValues
	 * */
	private ContentValues taskInfo_values;

	/**
	 * 获取勘察任务信息表（TaskInfo）要插入的参数
	 * */
	@Override
	public ContentValues getContentValues() {
		taskInfo_values = new ContentValues();
		taskInfo_values.put("UserID", UserID);
		taskInfo_values.put("LogContent", LogContent);
		taskInfo_values.put("OperatorType", OperatorType.getIndex());
		taskInfo_values.put("CreatedDate", CreatedDate);
		taskInfo_values.put("LogType", logType.getIndex());
		return taskInfo_values;
	}

	// }}

	@Override
	public String getTableName() {
		return "DataLog";
	}

	@Override
	public String toString() {
		return "DataLog [ID=" + ID + ", UserID=" + UserID + ", LogContent="
				+ LogContent + ", CreatedDate=" + CreatedDate
				+ ", OperatorType=" + OperatorType + ",LogType=" + logType
				+ "]";
	}

	@Override
	public String getPrimaryKeyName() {
		return "ID";
	}

	@Override
	public void setValueByCursor(Cursor cursor) {
		this.ID = cursor.getInt(cursor.getColumnIndex("ID"));
		this.UserID = cursor.getString(cursor.getColumnIndex("UserID"));
		this.LogContent = cursor.getString(cursor.getColumnIndex("LogContent"));
		this.CreatedDate = cursor.getString(cursor
				.getColumnIndex("CreatedDate"));
		this.OperatorType = OperatorTypeEnum.getEnumByValue(cursor
				.getInt(cursor.getColumnIndex("OperatorType")));
		this.logType = LogType.getEnumByIndex(cursor.getInt(cursor
				.getColumnIndex("LogType")));
	}
}
