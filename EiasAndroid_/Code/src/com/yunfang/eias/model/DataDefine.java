package com.yunfang.eias.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yunfang.framework.db.TableWorkerBase;
import com.yunfang.framework.utils.StringUtil;

import android.content.ContentValues;
import android.database.Cursor;


/**   
 *    
 * 项目名称：WaiCai   
 * 类名称：DataDefine   
 * 类描述：勘察配置基本信 息表(datadefine)对应的实体类   
 * 创建人：lihc   
 * 创建时间：2014-4-3 上午9:41:16   
 * @version        
 */  
@SuppressWarnings("serial")
public class DataDefine extends TableWorkerBase{

	//{{相关的属性
	/**
	 * 自增ID
	 * */
	public int ID;

	/**
	 * 勘察配置表名称
	 * */
	public String Name;

	/**
	 * 勘察表类型
	 */
	public String DefineType;

	/**
	 * 对应后台管理系统中此勘察配置表的ID值
	 * */
	public int DDID;

	/**
	 * 版本号
	 * */
	public int Version;

	/**
	 * 所在公司的ID值
	 * */
	public int CompanyID;

	/**
	 * 
	 * */
	public Boolean IsDefault;
	
	/**
	 * 存储CRM中的部门编号 由多个编号组成 用,分隔
	 */
	public String CrmDepId;
	
	/**
	 * 勘察表分类对象
	 */
	public ArrayList<DataCategoryDefine> Categories = new ArrayList<DataCategoryDefine>();
	//}}

	//{{构造函数
	/**
	 * 无参构造，设置默认的值
	 * */
	public DataDefine() {
		super();
		Name = "DataDefine";
		DDID = -1;
		Version = -1;
		CompanyID = -1;
		IsDefault = false;
		DefineType = "";
	}

	/**
	 * 有参数构造
	 * */
	public DataDefine(String name, Integer dDID, int version,
			int companyID,Boolean isDefault,String defineType) {
		Name = name;
		DDID = dDID;
		Version = version;
		CompanyID = companyID;
		IsDefault = isDefault;
		DefineType = defineType;
	}
	
	/**
	 * 构建对象
	 * @param obj
	 * @throws JSONException 
	 */
	public DataDefine(JSONObject obj) throws JSONException{	
		Name = obj.optString("Name");
		DDID = obj.optInt("ID");
		Version = obj.optInt("Version");
		CompanyID = obj.optInt("CompanyID");
		IsDefault = StringUtil.parseBoolean(obj.optString("IsDefault"));
		DefineType = obj.optString("TargetType");
		CrmDepId = obj.optString("CrmDepId");
		if(obj.has("Categories")){
			String categoriesStr = obj.getString("Categories");
			if(categoriesStr!= null && categoriesStr.length()>0){
				ArrayList<DataCategoryDefine> tempCategories = new ArrayList<DataCategoryDefine>();
				JSONArray arr = new JSONArray(categoriesStr);  
				for (int i = 0; i < arr.length(); i++) {  
					JSONObject temp = (JSONObject) arr.get(i);  
					tempCategories.add(new DataCategoryDefine(temp));					
				}  
				Categories = tempCategories;
			}
		}
	}
	//}}

	//{{ getContentValues  获取勘察配置基本信息表（Datadefine）要插入的参数
	/**
	 * 将勘察配置基本信息表所有的参数封装成ContentValues
	 * */
	private ContentValues Datadefine_values;

	/**
	 * 获取勘察配置基本信息表（Datadefine）要插入的参数
	 * */
	public ContentValues getContentValues() {
		Datadefine_values = new ContentValues();
		Datadefine_values.put("Name", Name);
		Datadefine_values.put("DDID", DDID);
		Datadefine_values.put("Version", Version);
		Datadefine_values.put("CompanyID", CompanyID);
		Datadefine_values.put("IsDefault", IsDefault);
		Datadefine_values.put("DefineType", DefineType);
		Datadefine_values.put("CrmDepId", CrmDepId);		
		return Datadefine_values;
	}
	//}}

	@Override
	public String getTableName() {
		return "DataDefine";
	}

	@Override
	public String toString() {
		return "DataDefine [ID=" + ID + ", Name=" + Name + ", DDID=" + DDID
				+ ", Version=" + Version + ", CompanyID=" + CompanyID
				+ ", CrmDepId=" + CrmDepId
				+ ", IsDefault=" + IsDefault + ", DefineType=" + DefineType+ "]";
	}

	@Override
	public void setValueByCursor(Cursor cursor){		
		this.ID = cursor.getInt(cursor.getColumnIndex("ID"));
		this.Name = cursor.getString(cursor
				.getColumnIndex("Name"));
		this.DDID = cursor.getInt(cursor
				.getColumnIndex("DDID"));
		this.Version = cursor.getInt(cursor
				.getColumnIndex("Version"));
		this.CompanyID = cursor.getInt(cursor
				.getColumnIndex("CompanyID"));
		this.IsDefault = StringUtil.parseBoolean(cursor.getString(cursor
				.getColumnIndex("IsDefault")));
		this.DefineType = cursor.getString(cursor
				.getColumnIndex("DefineType"));
		this.CrmDepId = cursor.getString(cursor
				.getColumnIndex("CrmDepId"));
	}
}
