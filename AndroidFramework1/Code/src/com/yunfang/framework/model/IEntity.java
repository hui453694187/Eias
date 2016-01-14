package com.yunfang.framework.model;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;
/**
 * 描述:实体类接口，简单继承Serializable
 * @author gorson
 *
 */
public interface IEntity extends Serializable {
	
	/**
	 * 获取表名
	 * @return
	 */
	String getTableName();
	
	/**
	 * 获取主键字段名称
	 * @return
	 */
	String getPrimaryKeyName();
	
	/**
	 * 获取对象需要插入的ContentValues值
	 * @return
	 */
	ContentValues getContentValues();
	
	/**
	 * 通过Cursor设置当前类的属性值
	 * @param cursor
	 * @return
	 */
	void setValueByCursor(Cursor cursor);
}
