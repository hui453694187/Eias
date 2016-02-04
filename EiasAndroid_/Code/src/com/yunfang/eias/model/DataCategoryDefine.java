package com.yunfang.eias.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.framework.db.TableWorkerBase;
import com.yunfang.framework.utils.StringUtil;

/**   
 *    
 * 项目名称：WaiCai   
 * 类名称：DataCategoryDefine   
 * 类描述：勘察配置表分类项信息表(DataCategoryDefine)对应的实体类
 * 创建人：lihc   
 * 创建时间：2014-4-3 上午9:46:41   
 * @version        
 */ 
@SuppressWarnings("serial")
public class DataCategoryDefine extends TableWorkerBase {

	//{{相关的属性
	/**
	 * 自增ID
	 * */
	public int ID;

	/**
	 * 分类项的类型：图片集、位置、常规、视频集、音频集（枚举类型）
	 * */
	public CategoryType ControlType;

	/**
	 * 对所属勘察配置表的ID
	 * */
	public int DDID;

	/**
	 * 是否默认显示(枚举类型)
	 * */
	public Boolean DefaultShow;

	/**
	 * 是否公开可见(枚举类型)
	 * */
	public Boolean Public;

	/**
	 * 分类项名称：相同勘察配置表下名称不可重复
	 * */
	public String Name;

	/**
	 *是否可以重复(枚举类型)
	 * */
	public Boolean Repeat;

	/**
	 *最大可重复数，默认值为0，0为不限制
	 * */
	public int RepeatMax;

	/**
	 *最小重复数，默认值为0，0为不限制
	 * */
	public int RepeatLimit;

	/**
	 *版本号(以后扩展使用)
	 * */
	public int VersionNumber;

	/**
	 *是否可用(枚举类型)
	 * */
	public Boolean Active;

	/**
	 *后台管理系统中对应当前勘察表下对应名称分类项的ID值
	 * */
	public int CategoryID;

	/**
	 *属于当前分类项下的属性总数
	 * */
	public int Total;

	/**
	 *分类项在任务列表中的顺序号
	 * */
	public int IOrder;

	/**
	 * 当前分类项下所有分类属性项
	 */
	public ArrayList<DataFieldDefine> Fields = new ArrayList<DataFieldDefine>();
	//}}

	//{{ 构造函数
	/**
	 * 无参数构造，设置默认值
	 * */
	public DataCategoryDefine() {
		super();
		ControlType = CategoryType.Normal;
		DDID = -1;
		DefaultShow = true;
		Public = true;
		Name = "";
		Repeat = true;
		RepeatMax = 0;
		RepeatLimit = 0;
		VersionNumber = 1;
		Active = true;
		CategoryID = -1;
		Total = -1;
		IOrder = -1;
	}


	public DataCategoryDefine(JSONObject obj) throws JSONException {
		super();
		ID = -1;
		ControlType = CategoryType.getEnumByName(obj.optString("ControlType"));
		DDID = obj.optInt("DDID");
		DefaultShow = StringUtil.parseBoolean(obj.optString("DefaultShow"));
		Public = StringUtil.parseBoolean(obj.optString("Public"));
		Name = obj.optString("Name");
		Repeat = StringUtil.parseBoolean(obj.optString("Repeat"));
		RepeatMax = obj.optInt("RepeatMax");
		RepeatLimit = obj.optInt("RepeatLimit");
		VersionNumber = obj.optInt("VersionNumber");
		Active = StringUtil.parseBoolean(obj.optString("Active"));
		CategoryID = obj.optInt("ID");
		Total = obj.optInt("Total");
		IOrder = obj.optInt("IOrder");
		if (obj.has("Fields")) {
			String fieldsStr = obj.optString("Fields");
			if (fieldsStr != null
					&& fieldsStr.length() > 0
					&& !fieldsStr.equals(EIASApplication.DefaultNullString)) {
				ArrayList<DataFieldDefine> tempFields = new ArrayList<DataFieldDefine>();
				JSONArray arr = new JSONArray(fieldsStr);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					tempFields.add(new DataFieldDefine(temp));
				}
				Fields = tempFields;
			}
		}
	}

	/**
	 * 有参数构造
	 * */
	public DataCategoryDefine(Integer controlType, Integer dDID,
			Boolean defaultShow, Boolean public1, String name, Boolean repeat,
			Integer repeatMax, Integer repeatLimit, Integer versionNumber,
			Boolean active, Integer categoryID, Integer total, Integer iOrder) {
		ControlType = CategoryType.getEnumByValue(controlType);
		DDID = dDID;
		DefaultShow = defaultShow;
		Public = public1;
		Name = name;
		Repeat = repeat;
		RepeatMax = repeatMax;
		RepeatLimit = repeatLimit;
		VersionNumber = versionNumber;
		Active = active;
		CategoryID = categoryID;
		Total = total;
		IOrder = iOrder;
		Fields = null;
	}
	//}}

	//{{ getContentValues  获取勘察配置表分类项信息表(DataCategoryDefine)要插入的参数
	/**
	 * 将勘察配置表分类项信息表所有的参数封装成ContentValues
	 * */
	private ContentValues DataCategoryDefine_values;

	/**
	 * 获取勘察配置表分类项信息表(DataCategoryDefine)要插入的参数
	 * */
	@Override
	public ContentValues getContentValues() {

		DataCategoryDefine_values = new ContentValues();
		DataCategoryDefine_values.put("ControlType", ControlType.getIndex());
		DataCategoryDefine_values.put("DDID", DDID);
		DataCategoryDefine_values.put("DefaultShow", DefaultShow);
		DataCategoryDefine_values.put("Public", Public);
		DataCategoryDefine_values.put("Name", Name);
		DataCategoryDefine_values.put("Repeat", Repeat);
		DataCategoryDefine_values.put("RepeatMax", RepeatMax);
		DataCategoryDefine_values.put("RepeatLimit", RepeatLimit);
		DataCategoryDefine_values.put("VersionNumber", VersionNumber);
		DataCategoryDefine_values.put("Active", Active);
		DataCategoryDefine_values.put("CategoryID", CategoryID);
		DataCategoryDefine_values.put("Total", Total);
		DataCategoryDefine_values.put("IOrder", IOrder);
		return DataCategoryDefine_values;
	}
	//}}

	@Override
	public String toString() {
		return "DataCategoryDefine [ID=" + ID + ", ControlType=" + ControlType
				+ ", DDID=" + DDID + ", DefaultShow=" + DefaultShow
				+ ", Public=" + Public + ", Name=" + Name + ", Repeat="
				+ Repeat + ", RepeatMax=" + RepeatMax + ", RepeatLimit="
				+ RepeatLimit + ", VersionNumber=" + VersionNumber
				+ ", Active=" + Active + ", CategoryID=" + CategoryID
				+ ", Total=" + Total + ", IOrder=" + IOrder + "]";
	}

	@Override
	public String getTableName() {
		return "DataCategoryDefine";
	}

	@Override
	public void setValueByCursor(Cursor cursor){		
		this.ControlType = CategoryType.getEnumByValue(cursor.getInt(cursor
				.getColumnIndex("ControlType")));
		this.DDID = cursor.getInt(cursor
				.getColumnIndex("DDID"));
		this.DefaultShow = StringUtil.parseBoolean(cursor.getString(cursor
				.getColumnIndex("DefaultShow")));
		this.Public = StringUtil.parseBoolean(cursor.getString(cursor
				.getColumnIndex("Public")));
		this.Name = cursor.getString(cursor
				.getColumnIndex("Name"));
		this.Repeat = StringUtil.parseBoolean(cursor.getString(cursor
				.getColumnIndex("Repeat")));
		this.RepeatMax = cursor.getInt(cursor
				.getColumnIndex("RepeatMax"));
		this.RepeatLimit = cursor.getInt(cursor
				.getColumnIndex("RepeatLimit"));
		this.VersionNumber = cursor.getInt(cursor
				.getColumnIndex("VersionNumber"));
		this.Active = StringUtil.parseBoolean(cursor.getString(cursor
				.getColumnIndex("Active")));
		this.ID = cursor.getInt(cursor
				.getColumnIndex("ID"));
		this.CategoryID = cursor.getInt(cursor
				.getColumnIndex("CategoryID"));
		this.Total = cursor.getInt(cursor
				.getColumnIndex("Total"));
		this.IOrder = cursor.getInt(cursor
				.getColumnIndex("IOrder"));
	}
}
