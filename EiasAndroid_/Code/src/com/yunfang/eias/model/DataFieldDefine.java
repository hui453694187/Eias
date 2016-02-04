package com.yunfang.eias.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor; 
import com.yunfang.eias.enumObj.DataItemType;
import com.yunfang.framework.db.TableWorkerBase;
import com.yunfang.framework.utils.StringUtil;

/**   
 * 项目名称：WaiCai   
 * 类名称：DataFieldDefine   
 * 类描述：勘察配置表属性信息表（DataFieldDefine）对应的实体类   
 * 创建人：lihc   
 * 创建时间：2014-4-3 上午10:00:22   
 * @version        
 */ 
@SuppressWarnings("serial")
public class DataFieldDefine extends TableWorkerBase{

	//{{相关属性
	/**
	 * 自增ID
	 * */
	public int ID;

	/**
	 * 分类项的ID（对应该DataCategoryDefine表中的CategoryID值）
	 * */
	public int CategoryID;

	/**
	 * 所属勘察配置表的ID，即属性项在后台管理系统中对应的ID值
	 * */
	public int DDID;

	/**
	 * 内容：如果是选项的话，选项值都这里
	 * */
	public String Content;

	/**
	 * （枚举类型）
	 * 分类项的类型：文本、多行文本、自定义文本、
	 * 下拉框、多选框、图片、时间、GPS、当前用户名、
	 * 当前用户联系方式
	 * */
	public DataItemType ItemType;

	/**
	 * 属性名称
	 * */
	public String Name;

	/**
	 *属性默认值
	 * */
	public String Value;

	/**
	 *输入内容的格式，例如：数字、字符串、时间等
	 * */
	public String InputFormat;

	/**
	 *输入值范围
	 * */
	public String InputRange;

	/**
	 *是否为必填项
	 * */
	public Boolean Required;

	/**
	 *属性在所在分类项中的顺序号
	 * */
	public int IOrder;

	/**
	 * 对应后台服务器中当前属性对应的ID值
	 */
	public int BaseID;
	
	/**
	 * 子项输入提示
	 */
	public String Hint;
	
	/**
	 * 手机端是否显示
	 */
	public Boolean ShowInPhone;

	//}}

	//{{ 构造函数
	/**
	 * 无参数构造，设置默认值
	 * */
	public DataFieldDefine() {
		super();
		CategoryID = -1;
		DDID = -1;
		Content = "";
		ItemType = DataItemType.Text;
		Name = "";
		Value = "";
		InputFormat = "";
		InputRange = "";
		Required = true;
		ShowInPhone = true;
		IOrder = -1;
		BaseID = -1;
		Hint = "";
	}

	/**
	 * 有参数构造
	 * */
	public DataFieldDefine(Integer categoryID, Integer dDID, String content,
			String itemType, String name, String value, String inputFormat,
			String inputRange, Boolean required, Integer iOrder,Integer baseID,String hint,Boolean showInPhone) {
		CategoryID = categoryID;
		DDID = dDID;
		Content = content;
		ItemType = DataItemType.getEnumByName(itemType);
		Name = name;
		Value = value;
		InputFormat = inputFormat;
		InputRange = inputRange;
		Required = required;
		IOrder = iOrder;
		BaseID = baseID;
		Hint =hint;
		ShowInPhone = showInPhone;
	}

	/**
	 * Json对象转成对象
	 * @param obj
	 * @throws JSONException
	 */
	public DataFieldDefine(JSONObject obj) throws JSONException{
		ID=-1;
		CategoryID = obj.optInt("CategoryID");
		DDID= obj.optInt("DDID");
		Content = obj.optString("Content");		
		ItemType = DataItemType.getEnumByName(obj.optString("ItemType"));
		if(ItemType == null){
			ItemType = DataItemType.Text;
		}
		Name = obj.optString("Name");
		Value = obj.optString("Value");
		InputFormat = obj.optString("InputFormat");
		InputRange = obj.optString("InputRange");
		Required = StringUtil.parseBoolean(obj.optString("Required"));
		IOrder = obj.optInt("ItemOrder");
		BaseID = obj.optInt("ID");
		Hint = obj.optString("Hint");
		ShowInPhone = StringUtil.parseBoolean(obj.optString("ShowInPhone"));
	}
	//}}

	//{{ getContentValues 获取勘察配置表属性信息表（DataFieldDefine）要插入的参数
	/**
	 * 将勘察配置表属性信息表所有的参数封装成ContentValues
	 * */
	private ContentValues DataFieldDefine_values;

	/**
	 * 获取勘察配置表属性信息表（DataFieldDefine）要插入的参数
	 * */
	public ContentValues getContentValues() {

		DataFieldDefine_values = new ContentValues();
		DataFieldDefine_values.put("CategoryID", CategoryID);
		DataFieldDefine_values.put("DDID", DDID);
		DataFieldDefine_values.put("Content", Content);
		DataFieldDefine_values.put("ItemType", ItemType.getIndex());
		DataFieldDefine_values.put("Name", Name);
		DataFieldDefine_values.put("Value", Value);
		DataFieldDefine_values.put("InputFormat", InputFormat);
		DataFieldDefine_values.put("InputRange", InputRange);
		DataFieldDefine_values.put("Required", Required);
		DataFieldDefine_values.put("IOrder", IOrder);
		DataFieldDefine_values.put("BaseID", BaseID);
		DataFieldDefine_values.put("Hint", Hint);
		DataFieldDefine_values.put("ShowInPhone", ShowInPhone);
		return DataFieldDefine_values;
	}
	//}}

	@Override
	public String toString() {
		return "DataFieldDefine [ID=" + ID + ", CategoryID=" + CategoryID
				+ ", DDID=" + DDID + ", Content=" + Content + ", ItemType="
				+ ItemType + ", Name=" + Name + ", Value=" + Value
				+ ", InputFormat=" + InputFormat + ", InputRange=" + InputRange
				+ ", Required=" + Required + ", IOrder=" + IOrder + ",BaseID="+BaseID+ ",Hint="+Hint+",ShowInPhone="+ShowInPhone+"]";
	}

	@Override
	public String getTableName() {
		return "DataFieldDefine";
	}	

	@Override
	public void setValueByCursor(Cursor cursor){		
		this.CategoryID = cursor.getInt(cursor
				.getColumnIndex("CategoryID"));
		this.DDID = cursor.getInt(cursor
				.getColumnIndex("DDID"));
		this.Content = cursor.getString(cursor
				.getColumnIndex("Content"));
		this.ItemType = DataItemType.getEnumByValue(cursor.getInt(cursor
				.getColumnIndex("ItemType")));
		this.Name = cursor.getString(cursor
				.getColumnIndex("Name"));
		this.Value = cursor.getString(cursor
				.getColumnIndex("Value"));
		this.InputFormat = cursor.getString(cursor
				.getColumnIndex("InputFormat"));
		this.InputRange = cursor.getString(cursor
				.getColumnIndex("InputRange"));
		this.Required = StringUtil.parseBoolean(cursor.getString(cursor
				.getColumnIndex("Required")));
		this.ID = cursor.getInt(cursor.getColumnIndex("ID"));
		this.IOrder = cursor.getInt(cursor
				.getColumnIndex("IOrder"));
		this.BaseID = cursor.getInt(cursor
				.getColumnIndex("BaseID"));
		this.Hint = cursor.getString(cursor
				.getColumnIndex("Hint"));
		this.ShowInPhone = StringUtil.parseBoolean(cursor.getString(cursor
				.getColumnIndex("ShowInPhone")));
	}
}
