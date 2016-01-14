package com.yunfang.eias.beta.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.dto.TaskCategoryInfoDTO;
import com.yunfang.eias.beta.dto.TaskDataItemDTO;
import com.yunfang.framework.db.TableWorkerBase;
import com.yunfang.framework.utils.DateTimeUtil;

/**
 * 
 * 项目名称：WaiCai 类名称：TaskCategoryInfo 类描述：勘察任务分类项信息表对应的实体类 创建人：lihc 创建时间：2014-4-9
 * 上午10:43:33
 * 
 * @version
 */
@SuppressWarnings("serial")
public class TaskCategoryInfo extends TableWorkerBase {

	// {{ 相关的属性
	/**
	 * 自增ID
	 * */
	public int ID;

	/**
	 * 勘察任务ID号，后台管理系统中对应任务编号的任务ID
	 * */
	public int TaskID;

	/**
	 * 标识名称，同一勘察任务下，不可重复
	 * */
	public String RemarkName;

	/**
	 * 勘察任务所属于勘察表的可重复分类项的ID，对应后台管理系统中的ID值
	 * */
	public int BaseCategoryID;

	/**
	 * Android端中此任务对应的ID值
	 * */
	public int CategoryID;

	/**
	 * 创建时间
	 * */
	public String CreatedDate;

	/**
	 * 当前勘察属性已完成数量
	 * */
	public int DataDefineFinishCount;

	/**
	 * 当前勘察属性总共数量
	 * */
	public int DataDefineTotal;

	/**
	 * 勘察任务属性数据记录表集合
	 * */
	public ArrayList<TaskDataItem> Items = new ArrayList<TaskDataItem>();

	// }}

	// {{构造函数
	/**
	 * 无参构造，设置默认值
	 * */
	public TaskCategoryInfo() {
		super();
		TaskID = -1;
		RemarkName = "";
		BaseCategoryID = -1;
		CategoryID = -1;
		DataDefineTotal = -1;
		DataDefineFinishCount = -1;
		CreatedDate = DateTimeUtil.getCurrentTime();// 当前时间
	}

	public TaskCategoryInfo(JSONObject obj) throws JSONException {
		super();
		ID = -1;
		TaskID = obj.optInt("TaskID");
		BaseCategoryID = obj.optInt("ID");
		CategoryID = obj.optInt("CategoryID");
		DataDefineTotal = (obj.has("DataDefineTotal") ? obj.optInt("DataDefineTotal") : -1);
		DataDefineFinishCount = (obj.has("DataDefineFinishCount") ? obj.optInt("DataDefineFinishCount") : -1);
		CreatedDate = obj.optString("CreatedDate");
		RemarkName = obj.optString("RemarkName");
		if (obj.has("Items")) {
			String itemsStr = obj.getString("Items");
			if (itemsStr != null && itemsStr.length() > 0 && 
					!itemsStr.equals(EIASApplication.DefaultNullString)) {
				ArrayList<TaskDataItem> tempFields = new ArrayList<TaskDataItem>();
				JSONArray arr = new JSONArray(itemsStr);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					tempFields.add(new TaskDataItem(temp));
				}
				Items = tempFields;
			}
		}
	}

	/**
	 * 有参数构造
	 * */
	public TaskCategoryInfo(Integer taskID, String remarkName, Integer dataDefineTotal, Integer dataDefineFinishCount, Integer baseCategoryID, Integer categoryID, String createdDate) {
		super();
		TaskID = taskID;
		RemarkName = remarkName;
		DataDefineTotal = dataDefineTotal;
		DataDefineFinishCount = dataDefineFinishCount;
		BaseCategoryID = baseCategoryID;
		CategoryID = categoryID;
		CreatedDate = createdDate;
	}

	// }}

	// {{ getContentValues 获取勘察任务分类项信息表（TaskCategoryInfo）要插入的参数
	/**
	 * 将勘察任务分类项信息表所有的参数封装成ContentValues
	 * */
	private ContentValues taskCategoryInfo_values;

	/**
	 * 获取勘察任务分类项信息表（TaskCategoryInfo）要插入的参数
	 * */
	@Override
	public ContentValues getContentValues() {

		taskCategoryInfo_values = new ContentValues();
		taskCategoryInfo_values.put("TaskID", TaskID);
		taskCategoryInfo_values.put("RemarkName", RemarkName);
		taskCategoryInfo_values.put("BaseCategoryID", BaseCategoryID);
		taskCategoryInfo_values.put("DataDefineTotal", DataDefineTotal);
		taskCategoryInfo_values.put("DataDefineFinishCount", DataDefineFinishCount);
		taskCategoryInfo_values.put("CategoryID", CategoryID);
		taskCategoryInfo_values.put("CreatedDate", CreatedDate);
		return taskCategoryInfo_values;
	}

	// }}

	@Override
	public String getTableName() {
		return "TaskCategoryInfo";
	}

	@Override
	public String toString() {
		return "TaskCategoryInfo [ID=" + ID 
				+ ", TaskID=" + TaskID 
				+ ", RemarkName=" + RemarkName 
				+ ", DataDefineTotal=" + DataDefineTotal
				+ ", DataDefineFinishCount=" + DataDefineFinishCount 
				+ ", BaseCategoryID=" + BaseCategoryID 
				+ ", CategoryID=" + CategoryID 
				+ ", CreatedDate=" + CreatedDate 
				+ ", taskCategoryInfo_values=" + taskCategoryInfo_values + "]";
	}

	@Override
	public void setValueByCursor(Cursor cursor) {
		this.ID = cursor.getInt(cursor.getColumnIndex("ID"));
		this.TaskID = cursor.getInt(cursor.getColumnIndex("TaskID"));
		this.RemarkName = cursor.getString(cursor.getColumnIndex("RemarkName"));
		this.DataDefineTotal = cursor.getInt(cursor.getColumnIndex("DataDefineTotal"));
		this.DataDefineFinishCount = cursor.getInt(cursor.getColumnIndex("DataDefineFinishCount"));
		this.BaseCategoryID = cursor.getInt(cursor.getColumnIndex("BaseCategoryID"));
		this.CategoryID = cursor.getInt(cursor.getColumnIndex("CategoryID"));
		this.CreatedDate = cursor.getString(cursor.getColumnIndex("CreatedDate"));
	}
	
	/**
	 * 有参数构造
	 * */
	public TaskCategoryInfo(TaskCategoryInfoDTO category) {
		super();
		ID = (int) category.ID;
		BaseCategoryID = (int) category.BaseCategoryID;
		TaskID = (int) category.TaskID;
		RemarkName = category.RemarkName;
		CategoryID = (int) category.CategoryID;
		CreatedDate = category.CreatedDate;
		for (TaskDataItemDTO dataItem : category.Items) {
			Items.add(new TaskDataItem(dataItem));
		}
	}
}
