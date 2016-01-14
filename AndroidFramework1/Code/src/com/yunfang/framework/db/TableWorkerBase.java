package com.yunfang.framework.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.yunfang.framework.model.IEntity;
import com.yunfang.framework.utils.YFLog;


/**
 * 数据操作,基本的增、删、改、查都基本，需要特别的操作，则在子类实现
 * @author Gorson
 *
 */
@SuppressWarnings("serial")
public class TableWorkerBase implements IEntity{

	//{{ 数据库属性
	/**
	 * 管理和操作数据库的核心类
	 * */
	private SQLiteDatabase db;
	//}}
	
	/**
	 * 
	 */
	public TableWorkerBase() {
		db = SQLiteHelper.getReadableDB();	
	}
	
	/**
	 * 查找数据
	 * @param columns：返回的列
	 * @param where:SQL中的WHERE子句（不包括WHERE）
	 * @return
	 * @time 2011-6-24 下午12:02:26 
	 * @author:Gorson
	 */
	public Cursor onSelect(String[] columns, String where){
		Cursor cursor = db.query(getTableName(), columns, where, null, null, null, null);
		return cursor;
	}
	
	/**
	 *  查找数据
	 * @param columns：返回的列
	 * @param select:SQL中的WHERE子句（不包括WHERE）
	 * @param selectArgs：相关参数
	 * @return
	 */
	public Cursor onSelect(String[] columns,String where, String[] selectArgs){
		Cursor cursor = db.query(getTableName(), columns, where, selectArgs, null, null, null);
		return cursor;
	}
	
	/**
	 * 插入数据，以ContentValues的格式
	 * @param values
	 * @return
	 * @time 2011-6-24 上午11:58:49 
	 * @author:Gorson
	 */
	public synchronized long onInsert(){
		long id = db.insert(getTableName(), "", getContentValues());
		return id;
	}
	
	/**
	 * 更新数据
	 * @param values
	 * @param where：SQL中的WHERE子句（不包括WHERE）
	 * @return
	 * @time 2011-6-24 下午12:06:06 
	 * @author:Gorson
	 */
	public synchronized int onUpdate(String where){	
		int id = db.update(getTableName(),getContentValues(), where, null);
		YFLog.d("TableName:"+id);
		return id;
	}
	
	/**
	 * 根据id,删除某条信息
	 * @param _id：要删除数据的id
	 * @return
	 * @time 2011-6-24 下午01:25:17 
	 * @author:Gorson
	 */
	public synchronized int onDelete(String _id){
		int id = 0;
		String where = getPrimaryKeyName() + " = "+_id;
		id = db.delete(getTableName(),where , null);
		return id;
	}
	
	/**
	 * 根据WHERE子句删除某条记录
	 * @param where
	 * @return
	 */
	public synchronized int delete(String where){
		int id = db.delete(getTableName(),where , null);
		return id;
	}
	
	/**
	 * 删除表里的所有数据
	 * @return
	 * @time 2011-6-24 下午01:25:11 
	 * @author:Gorson
	 */
	public boolean onDeleteAll(){		
		try{
			db.execSQL("DELETE FROM "+getTableName());
		}catch(SQLException e){
			return false;
		}
		return true;
	}
	
	/**
	 * 删除该表
	 * 
	 * @time 2011-6-24 下午01:25:02 
	 * @author:Gorson
	 */
	public synchronized void onDelTable(){
		try{
			db.execSQL("DROP TABLE " + getTableName());
		}catch(SQLException e){
		}
	}
	
	/**
	 * 执行普通sql语句
	 * @param sql
	 * @time 2011-6-24 下午01:24:47 
	 * @author:Gorson
	 */
	public synchronized void onExcute(String sql){
		try{
			db.execSQL(sql);
		}catch(SQLException e){
		}
	}
	/**
	 * 关闭
	 */
	public void onClose(){
		try{
			db.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取SQLiteDatabase
	 * @return
	 */
	public SQLiteDatabase getDb(){
		return this.db;
	}

	/**
	 * 
	 */
	@Override
	public String getTableName() {
		return null;
	}

	/**
	 * 
	 */
	public String getPrimaryKeyName() {
		return "ID";
	}

	/**
	 * 
	 */
	@Override
	public ContentValues getContentValues() {
		return null;
	}

	/**
	 * 
	 */
	@Override
	public void setValueByCursor(Cursor cursor) {
		
	}	
}
