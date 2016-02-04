package com.yunfang.eias.dto;

import org.json.JSONException;
import org.json.JSONObject;

import com.yunfang.eias.model.TaskDataItem;

/**
 * 项目名称：外业采集项目 类名称：TaskDataItemDTO 类描述：勘察任务分类子项 用于给后台传递的参数对象 创建人：贺隽
 * 创建时间：2014-6-25
 * 
 * @version 1.0.0.1
 */
public class TaskDataItemDTO {
	// {{ 属性信息

	/**
	 * 编号
	 */
	public String TID;

	/**
	 * 名称
	 */
	public String Name;

	/**
	 * 值
	 */
	public String Value;

	/**
	 * 任务编号
	 */
	public long OriginalDataTID;

	/**
	 * 所属父级分类编号
	 */
	public long ParentCategoryID;

	/**
	 * 所属分类编号
	 */
	public long CategoryID;

	/**
	 * 后台TaskDataItem的Id
	 */
	public long BaseID;

	/**
	 * 类型
	 */
	public String ItemType;

	/**
	 * 内容
	 */
	public String Content;

	/**
	 * 编号
	 */
	public long OriginalItemsId;

	/**
	 * 任务编号
	 */
	public int IOrder;

	/**
	 * 任务编号
	 */
	public String TaskCategoryRemarkName;

	// }}

	// {{构造函数

	/**
	 * 有参数构造
	 */
	public TaskDataItemDTO(TaskDataItem dataItem) {
		super();
		TID = String.valueOf(dataItem.BaseID);
		Name = dataItem.Name;
		Value = dataItem.Value;
		OriginalDataTID = dataItem.TaskID;
		IOrder = dataItem.IOrder;
		ParentCategoryID = 0;
		CategoryID = dataItem.CategoryID;
		BaseID = dataItem.BaseID;
		ItemType = "";
		Content = "";
		TaskCategoryRemarkName = "";
	}

	/**
	 * Json对象转成对象
	 * 
	 * @param obj
	 * @throws JSONException
	 */
	public TaskDataItemDTO(JSONObject obj) throws JSONException {
		CategoryID = obj.getInt("CategoryID");
		Content = obj.getString("Content");
		IOrder = obj.getInt("IOrder");
		ItemType = obj.getString("ItemType");
		Name = obj.getString("Name");
		OriginalDataTID = obj.getInt("OriginalDataTID");
		OriginalItemsId = obj.getInt("OriginalItemsId");
		ParentCategoryID = obj.getInt("ParentCategoryID");
		TID = obj.getString("TID");
		TaskCategoryRemarkName = obj.getString("TaskCategoryRemarkName");
		Value = obj.getString("Value");
		BaseID = obj.getInt("BaseID");
	}
	// }}
}
