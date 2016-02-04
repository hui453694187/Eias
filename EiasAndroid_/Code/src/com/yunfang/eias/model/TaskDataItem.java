package com.yunfang.eias.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.yunfang.eias.dto.TaskDataItemDTO;
import com.yunfang.framework.db.TableWorkerBase;

/**
 * 
 * 项目名称：WaiCai 类名称：TaskDataItem 类描述：勘察任务属性数据记录表对应的实体类 创建人：lihc 创建时间：2014-4-9
 * 上午10:44:59
 * 
 * @version
 */
@SuppressWarnings("serial")
public class TaskDataItem extends TableWorkerBase
{

	// {{ 属性信息
	/**
	 * 自增ID
	 * */
	public int ID;

	/**
	 * 勘察配置表下分类项的ID，对应TaskCategoryInfos表中的BaseCategoryID值
	 * */
	public int BaseCategoryID;

	/**
	 * 对应后台管理系统中的ID值
	 */
	public int BaseID;

	/**
	 * 属性名称
	 * */
	public String Name;

	/**
	 * 采集值
	 * */
	public String Value;

	/**
	 * 属性在所在分类项中的顺序号
	 * */
	public int IOrder;

	/**
	 * 勘察任务ID值(后台管理端)
	 * */
	public int TaskID;

	/**
	 * 分类项标识，用于区分可重复项中的哪一个具体的重复值，对应TaskCategoryInfos表中的ID值
	 * */
	public int CategoryID;

	// }}

	// {{构造函数
	/**
	 * 无参构造，设置默认值
	 * */
	public TaskDataItem()
	{
		super();
		BaseCategoryID = -1;
		BaseID = -1;
		Name = "";
		Value = "";
		IOrder = -1;
		TaskID = -1;
		CategoryID = -1;
	}

	/**
	 * 有参数构造
	 * @param baseCategoryID
	 * @param name
	 * @param value
	 * @param iOrder
	 * @param taskID
	 * @param categoryID
	 * @param baseID
	 */
	public TaskDataItem(Integer baseCategoryID, String name, String value, Integer iOrder, Integer taskID, Integer categoryID,Integer baseID)
	{
		BaseCategoryID = baseCategoryID;
		Name = name;
		Value = value;
		IOrder = iOrder;
		TaskID = taskID;
		CategoryID = categoryID;
		BaseID = baseID;
	}

	/**
	 * Json对象转成对象
	 * @param obj
	 * @throws JSONException
	 */
	public TaskDataItem(JSONObject obj) throws JSONException{
		ID=-1;
		CategoryID = obj.optInt("CategoryID");
		BaseCategoryID = obj.optInt("ParentCategoryID");
		Name = obj.optString("Name");
		Value = obj.optString("Value");
		IOrder = obj.optInt("IOrder");
		BaseID = obj.optInt("TID");
		TaskID = obj.optInt("OriginalDataTID");
	}
	// }}

	// {{ getContentValues 获取勘察任务属性数据记录表（TaskDataItem）要插入的参数
	/**
	 * 将勘察任务属性数据记录表所有的参数封装成ContentValues
	 * */
	private ContentValues taskDataItem_values;

	/**
	 * 获取勘察任务属性数据记录表（TaskDataItem）要插入的参数
	 * */
	@Override
	public ContentValues getContentValues()
	{
		taskDataItem_values = new ContentValues();
		taskDataItem_values.put("BaseCategoryID", BaseCategoryID);
		taskDataItem_values.put("Name", Name);
		taskDataItem_values.put("Value", Value);
		taskDataItem_values.put("IOrder", IOrder);
		taskDataItem_values.put("TaskID", TaskID);
		taskDataItem_values.put("CategoryID", CategoryID);
		taskDataItem_values.put("BaseID", BaseID);
		return taskDataItem_values;
	}

	// }}

	@Override
	public String getTableName()
	{
		return "TaskDataItem";
	}

	@Override
	public String toString()
	{
		return "TaskDataItem [ID=" + ID + ", BaseCategoryID=" + BaseCategoryID + ", Name=" + Name + ", Value=" + Value + ", IOrder=" + IOrder + ", TaskID=" + TaskID + ", CategoryID=" + CategoryID + ", taskDataItem_values=" + taskDataItem_values + "]";
	}

	@Override
	public void setValueByCursor(Cursor cursor)
	{
		this.ID = cursor.getInt(cursor.getColumnIndex("ID"));
		this.BaseCategoryID = cursor.getInt(cursor.getColumnIndex("BaseCategoryID"));
		this.Name = cursor.getString(cursor.getColumnIndex("Name"));
		this.Value = cursor.getString(cursor.getColumnIndex("Value"));
		this.IOrder = cursor.getInt(cursor.getColumnIndex("IOrder"));
		this.TaskID = cursor.getInt(cursor.getColumnIndex("TaskID"));
		this.CategoryID = cursor.getInt(cursor.getColumnIndex("CategoryID"));
		this.BaseID = cursor.getInt(cursor.getColumnIndex("BaseID"));
	}
	
	/**
	 * 有参数构造
	 */
	public TaskDataItem(TaskDataItemDTO dataItem)
	{
		super();
		BaseID = Integer.parseInt(dataItem.TID);
		Name = dataItem.Name;
		Value = dataItem.Value; 
		TaskID = (int) dataItem.OriginalDataTID;
		IOrder = dataItem.IOrder;
		CategoryID = (int) dataItem.CategoryID;
		BaseCategoryID = (int) dataItem.ParentCategoryID;
	}
}
