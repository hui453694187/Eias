package com.yunfang.eias.beta.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.model.TaskCategoryInfo;
import com.yunfang.eias.beta.model.TaskDataItem;

/**   
 *    
 * 项目名称：外业采集项目   
 * 类名称：TaskCategoryInfoDto   
 * 类描述：勘察任务分类项 用于给后台传递的参数对象 必须要和 Visual Studio 中的TaskCategoryInfoDto 一致
 * 创建人：贺隽   
 * 创建时间：2014-6-25 
 * @version 1.0.0.1
 */ 
public class TaskCategoryInfoDTO{

	// {{ 相关的属性

	/**
	 * 任务编号
	 */
	public long TaskID;
	
	/**
	 * 服务器的TaskCategoryInfoes的ID
	 */
	public long BaseCategoryID;	
	
	/**
	 * 分类项名称
	 */
	public String RemarkName;

	/**
	 * 勘察任务所属于勘察表的可重复分类项的ID
	 */
	public long CategoryID;

	/**
	 * 自动增长编号
	 */
	public long ID;

	/**
	 * 自定义排序
	 */
	public int ItemOrder;

	/**
	 * 勘察配置表分类项是否可以重复
	 */
	public Boolean Repeat;

	/**
	 * 勘察配置表分类项重复最大
	 */
	public int RepeatMax;

	/**
	 * 勘察配置表分类项重复最少
	 */
	public int RepeatLimit;

	/**
	 * 勘察配置表分类项是否可用
	 */
	public Boolean Active;

	/**
	 * 勘察配置表分类项是否默认显示
	 */
	public Boolean DefaultShow;

	/**
	 * 创建时间
	 */
	public String CreatedDate;

	/**
	 * 勘察匹配中，每个属性项对应的类型值
	 */
	public String CategoryType;

	/**
	 * 勘察任务属性数据记录表集合
	 * */
	public List<TaskDataItemDTO> Items = new ArrayList<TaskDataItemDTO>();

	// }}

	// {{构造函数

	/**
	 * 有参数构造
	 * */
	public TaskCategoryInfoDTO(TaskCategoryInfo category) {
		super();
		ID = category.ID;
		BaseCategoryID = category.BaseCategoryID;
		TaskID = category.TaskID;
		RemarkName = category.RemarkName;
		CategoryID = category.CategoryID;
		CreatedDate = category.CreatedDate;
		ItemOrder =  0;
		Repeat = false;
		RepeatMax = 0;
		RepeatLimit = 0;
		Active = true;
		DefaultShow = true;
		for (TaskDataItem dataItem : category.Items) {
			Items.add(new TaskDataItemDTO(dataItem));
		}
	}

	/**
	 * 有参数构造
	 * */
	public TaskCategoryInfo getTaskCategoryInfo() {
		TaskCategoryInfo result = new TaskCategoryInfo();
		//result.TaskID = Integer.valueOf(String.valueOf(category.ID));
		result.TaskID = Integer.valueOf(String.valueOf(this.TaskID));
		result.RemarkName = this.RemarkName;
		result.CategoryID = (int) this.CategoryID;
		result.CreatedDate = this.CreatedDate;
		result.BaseCategoryID = Integer.valueOf(String.valueOf(this.ID)); 
		return result;
	}
	
	public TaskCategoryInfoDTO(JSONObject obj) throws JSONException {
		super();
		ID = obj.getInt("ID");
		TaskID = obj.getInt("TaskID");
		BaseCategoryID = obj.getInt("BaseCategoryID");
		CategoryID = obj.getInt("CategoryID");
		CreatedDate = obj.getString("CreatedDate");
		RemarkName = obj.getString("RemarkName");
		CategoryType = obj.getString("CategoryType");
		Active = obj.getBoolean("Active");
		DefaultShow = obj.getBoolean("DefaultShow");
		ItemOrder = obj.getInt("ItemOrder");
		if (obj.has("Items")) {
			String itemsStr = obj.getString("Items");
			if (itemsStr != null && itemsStr.length() > 0 && 
					!itemsStr.equals(EIASApplication.DefaultNullString)) {
				ArrayList<TaskDataItemDTO> tempFields = new ArrayList<TaskDataItemDTO>();
				JSONArray arr = new JSONArray(itemsStr);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					tempFields.add(new TaskDataItemDTO(temp));
				}
				Items = tempFields;
			}
		}
	}


	// }}

}
