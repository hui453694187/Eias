package com.yunfang.eias.tables;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.framework.db.SQLiteHelper;
import com.yunfang.framework.model.ResultInfo;

/**
 * 
 * 项目名称：WaiCai 类名称：DataDefineWorker 类描述：勘察表数据操作 创建人：lihc 创建时间：2014-4-9
 * 上午10:32:57
 * 
 * @version
 */
public class DataDefineWorker {
	// {{ fillCompleteDataDefindInfos
	/**
	 * 填充完成整个勘察表数据（事务操作）
	 * 
	 * @param dataDefine
	 *            ：勘察配置基本信息表对象，包含勘察配置表分类项信息表对象的集合 ,每个分类下包含其下属的分类属性信息
	 * @return
	 */
	public static ResultInfo<Long> fillCompleteDataDefindInfos(DataDefine dataDefine) {
		ResultInfo<Long> resultInfo = new ResultInfo<Long>();
		// 插入数据是否成功的标志（大于0成功）
		long ddID = 0;
		SQLiteDatabase db = null;
		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();
			// 重置勘察配置基本信息
			ddID = resetDataDefine(db, dataDefine);
			if (dataDefine.Categories != null && dataDefine.Categories.size() > 0) {
				// 向勘察配置表分类项信息表(DataCategoryDefine)插入多条数据
				for (DataCategoryDefine dataCategoryDefine : dataDefine.Categories) {
					resetDataCategoryDefine(db, dataCategoryDefine);

					if (dataCategoryDefine.Fields != null && dataCategoryDefine.Fields.size() > 0) {
						// 向勘察配置表属性信息表（DataFieldDefine）插入多条数据
						for (DataFieldDefine dataFieldDefine : dataCategoryDefine.Fields) {
							resetDataFieldDefine(db, dataFieldDefine);
						}
						// 删除多出的子项
						deleteExcessFields(db, dataCategoryDefine);
					}
				}
			}
			// 删除多出的分类项
			deleteExcessCategories(db, dataDefine);
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			resultInfo.Data = ddID;// 此id为最后插入行的id值
		} catch (Exception e) {
			resultInfo.Message = "请重试";
			resultInfo.Success = false;
			resultInfo.Data = (long) -1;
		} finally {

		}
		return resultInfo;
	}

	/**
	 * 删除服务器已删除的勘察表子项
	 * 
	 * @param db
	 *            :数据操作对象
	 * @param dataCategoryDefine
	 *            :勘察表分类项（包含子项信息）
	 */
	private static void deleteExcessFields(SQLiteDatabase db, DataCategoryDefine dataCategoryDefine) {
		// 查询该分类项下的所有子项
		ResultInfo<ArrayList<DataFieldDefine>> fields = queryDataFieldDefineByID(dataCategoryDefine.DDID, dataCategoryDefine.CategoryID);
		// 循环服务器中取下来的子项，与本地的对比以找出服务器已经删除的子项。并在本地中删除。
		if (fields.Data != null) {
			for (DataFieldDefine localField : fields.Data) {
				boolean isContains = false;
				for (DataFieldDefine serverField : dataCategoryDefine.Fields) {
					if (localField.BaseID == serverField.BaseID) {
						isContains = true;
					}
				}
				if (!isContains) {
					db.delete(new DataFieldDefine().getTableName(), "BaseID=?", new String[] { String.valueOf(localField.BaseID) });
				}
			}
		}
	}

	/**
	 * 重置勘察配置表属性信息
	 * 
	 * @param db
	 *            ：操作数据对象
	 * @param dataDefine
	 *            ：勘察配置表属性信息
	 */
	private static void resetDataFieldDefine(SQLiteDatabase db, DataFieldDefine dataFieldDefine) {
		// 准备数据
		ContentValues taskDataFieldDefine = dataFieldDefine.getContentValues();

		// 获取当前勘察任务分类项信息
		Cursor dataFieldDefineCursor = db.query(dataFieldDefine.getTableName(), null, "CategoryID=? and DDID=? and BaseID=?",
				new String[] { String.valueOf(dataFieldDefine.CategoryID), String.valueOf(dataFieldDefine.DDID), String.valueOf(dataFieldDefine.BaseID) }, null, null, null);

		if (dataFieldDefineCursor != null && dataFieldDefineCursor.moveToFirst()) {
			DataFieldDefine dbItem = new DataFieldDefine();
			dbItem.setValueByCursor(dataFieldDefineCursor);
			db.update(dataFieldDefine.getTableName(), taskDataFieldDefine, "ID=?", new String[] { String.valueOf(dbItem.ID) });
		} else {
			dataFieldDefine.ID = (int) db.insert(dataFieldDefine.getTableName(), null, taskDataFieldDefine);
		}
		 dataFieldDefineCursor.close();
	}

	/**
	 * 删除服务器端已经删除的勘察表分类项
	 * 
	 * @param db
	 *            ：数据操作对象
	 * @param dataDefine
	 *            ：勘察表对象(包含分类项信息)
	 */
	private static void deleteExcessCategories(SQLiteDatabase db, DataDefine dataDefine) {
		// 查询该勘察表下所有分类项
		ResultInfo<ArrayList<DataCategoryDefine>> categorys = queryDataCategoryDefineByDDID(dataDefine.DDID);
		if (categorys.Data != null) {
			// 循环服务器中取下来的分类项，与本地的对比以找出服务器已经删除的分类项。并在本地中删除。
			for (DataCategoryDefine localCategory : categorys.Data) {
				boolean isContains = false;
				for (DataCategoryDefine serverCategory : dataDefine.Categories) {
					if (localCategory.CategoryID == serverCategory.CategoryID) {
						isContains = true;
					}
				}
				if (!isContains) {
					// 删除分类项
					db.delete(new DataCategoryDefine().getTableName(), "CategoryID=? and  DDID=?", new String[] { String.valueOf(localCategory.CategoryID), String.valueOf(localCategory.DDID) });
					// 查询该分类项下的所有子项
					ResultInfo<ArrayList<DataFieldDefine>> fields = queryDataFieldDefineByID(localCategory.DDID, localCategory.CategoryID);
					if (fields.Data != null) {
						for (DataFieldDefine localField : fields.Data) {
							db.delete(new DataFieldDefine().getTableName(), "BaseID=?", new String[] { String.valueOf(localField.BaseID) });
						}
					}
				}
			}
		}
	}

	/**
	 * 重置勘察配置表分类项信息
	 * 
	 * @param db
	 *            ：操作数据对象
	 * @param dataDefine
	 *            ：勘察配置表分类项信息
	 */
	private static void resetDataCategoryDefine(SQLiteDatabase db, DataCategoryDefine dataCategoryDefine) {
		// 准备插入的参数
		ContentValues dataCategoryDefineValues = dataCategoryDefine.getContentValues();

		// 获取当前勘察任务分类项信息
		Cursor dataCategoryDefineCursor = db.query(dataCategoryDefine.getTableName(), null, "DDID=? and CategoryID=?",
				new String[] { String.valueOf(dataCategoryDefine.DDID), String.valueOf(dataCategoryDefine.CategoryID) }, null, null, null);

		if (dataCategoryDefineCursor != null && dataCategoryDefineCursor.moveToFirst()) {
			DataCategoryDefine dbItem = new DataCategoryDefine();
			dbItem.setValueByCursor(dataCategoryDefineCursor);
			db.update(dataCategoryDefine.getTableName(), dataCategoryDefineValues, "ID=?", new String[] { String.valueOf(dbItem.ID) });
		} else {
			dataCategoryDefine.ID = (int) db.insert(dataCategoryDefine.getTableName(), null, dataCategoryDefineValues);
		}
		 dataCategoryDefineCursor.close();
	}

	/**
	 * 重置勘察配置基本信息
	 * 
	 * @param db
	 *            ：操作数据对象
	 * @param dataDefine
	 *            ：勘察配置基本信息
	 * @return 勘察配置基本信息编号
	 */
	private static long resetDataDefine(SQLiteDatabase db, DataDefine dataDefine) {
		long ddID;
		// 获取当前勘察任务分类项信息
		Cursor dataDefineCursor = db.query(new DataDefine().getTableName(), null, "DDID=?", new String[] { String.valueOf(dataDefine.DDID) }, null, null, null);

		// 准备插入的参数
		ContentValues datadefineValues = dataDefine.getContentValues();
		if (dataDefineCursor != null && dataDefineCursor.moveToFirst()) {
			DataDefine dbItem = new DataDefine();
			dbItem.setValueByCursor(dataDefineCursor);
			ddID = db.update(dataDefine.getTableName(), datadefineValues, "ID=?", new String[] { String.valueOf(dbItem.ID) });
		} else {
			ddID = db.insert(dataDefine.getTableName(), null, datadefineValues);
			dataDefine.ID = (int) ddID;
		}
		 dataDefineCursor.close();
		return ddID;
	}

	// }}

	/**
	 * 根据勘察配置表ID删除完整的勘察配置表数据（事务操作）
	 * 
	 * @param ddID
	 *            ：对应后台管理系统中勘察配置表的的ID值
	 * @return
	 */
	public static ResultInfo<Integer> deleteCompleteDataDefindInfos(int ddid) {
		ResultInfo<Integer> resultInfo = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		try {
			int rowNum = 0;// 删除数据影响的行数
			db = SQLiteHelper.getWritableDB();
			db.beginTransaction();// 开启事务
			rowNum = db.delete(new DataDefine().getTableName(), "DDID=?", new String[] { String.valueOf(ddid) });
			rowNum += db.delete(new DataCategoryDefine().getTableName(), "DDID=?", new String[] { String.valueOf(ddid) });
			rowNum += db.delete(new DataFieldDefine().getTableName(), "DDID=?", new String[] { String.valueOf(ddid) });
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			// 结束事务，默认是回滚
			db.endTransaction();
			resultInfo.Data = rowNum;// 此rowNum为删除影响的行数
		} catch (Exception e) {
			resultInfo.Message = "删除失败，请重试";
			resultInfo.Data = -1;
			resultInfo.Success = false;
		} finally {
			// db.close();
		}
		return resultInfo;
	}

	
	
	/**
	 * 根据指定id查询勘察配置基本信息表的数据
	 * 
	 * @param dDID
	 *            ：对应后台管理系统中此勘察配置表的ID值
	 * @param companyID
	 *            ：所在公司的ID值
	 * @return
	 */
	public static ResultInfo<ArrayList<DataDefine>> queryDataDefineByDDIDOrCompanyID(int ddid, int companyID) {
		ResultInfo<ArrayList<DataDefine>> resultInfo = new ResultInfo<ArrayList<DataDefine>>();
		ArrayList<DataDefine> data = null;
		SQLiteDatabase db = null;
		// IDFiled:对应ID值的字段
		String IDFiled = ddid > 0 ? "DDID" : "CompanyID";
		try {
			db = SQLiteHelper.getWritableDB();
			data = new ArrayList<DataDefine>();
			Cursor cursor = db.query(new DataDefine().getTableName(), null, IDFiled + "=?", new String[] { String.valueOf(ddid > 0 ? ddid : companyID) }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					DataDefine dataDefine = new DataDefine();
					dataDefine.setValueByCursor(cursor);
					data.add(dataDefine);
				}
			}
			cursor.close();
			resultInfo.Data = data;
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;// 如果失败，data的值为null
			resultInfo.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/***
	 * 删除指定DDID 的勘察表， 和 相关联的子表
	 * @return
	 */
	public static boolean deletDataDefneByDDID(List<String> ddidList){
		
		SQLiteDatabase db=null;
		try{
			db = SQLiteHelper.getReadableDB();
			db.beginTransaction();
			for(String ddid:ddidList){
				db.delete(new DataDefine().getTableName(),
						"DDID=?", 
						new String[] { ddid});
				db.delete(new DataFieldDefine().getTableName(),
						"DDID=?",
						new String[]{ddid});
				db.delete(new DataCategoryDefine().getTableName(),
						"DDID=?",
						new String[]{ddid});
			}
			
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * 根据CompanyID查询勘察配置基本信息表的数据
	 * 
	 * @param companyID
	 *            ：所在公司的ID值
	 * @return
	 */
	public static ResultInfo<ArrayList<DataDefine>> queryDataDefineByCompanyID(int companyID) {				
		ResultInfo<ArrayList<DataDefine>> resultInfo = new ResultInfo<ArrayList<DataDefine>>();
		SQLiteDatabase db = null;
		DataDefine dataDefine = null;
		ArrayList<DataDefine> data = new ArrayList<DataDefine>();
		Cursor cursor = null;
		try {
			String dep = "%["+ EIASApplication.getCurrentUser().CrmDepId +"]%";
			db = SQLiteHelper.getReadableDB();
			cursor = db.query(new DataDefine().getTableName(), null, "CompanyID=? OR CrmDepId like ?", new String[] { String.valueOf(companyID),dep }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					dataDefine = new DataDefine();
					dataDefine.setValueByCursor(cursor);
					data.add(dataDefine);
				}
				resultInfo.Data = data;
			}

		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;// 如果失败，Data设为null
			resultInfo.Success = false;
		} finally {
			cursor.close();
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 根据DDID查询勘察配置基本信息表的数据
	 * 
	 * @param dDID
	 *            ：对应后台管理系统中此勘察配置表的ID值
	 * @return
	 */
	public static ResultInfo<DataDefine> queryDataDefineByDDID(int ddid) {
		ResultInfo<DataDefine> resultInfo = new ResultInfo<DataDefine>();
		SQLiteDatabase db = null;
		DataDefine dataDefine = null;
		try {
			dataDefine = new DataDefine();
			db = SQLiteHelper.getReadableDB();
			Cursor cursor = db.query(new DataDefine().getTableName(), null, "DDID=?", new String[] { String.valueOf(ddid) }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					dataDefine.setValueByCursor(cursor);
					resultInfo.Data = dataDefine;
				}
			}
			cursor.close();
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;// 如果失败，Data为设为null
			resultInfo.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 根据DDID查询对应的勘察配置表分类项信息表数据
	 * 
	 * @param DDID
	 *            ：对应后台管理系统中此勘察配置表的ID值
	 * @return
	 */
	public static ResultInfo<ArrayList<DataCategoryDefine>> queryDataCategoryDefineByDDID(int ddid) {
		ResultInfo<ArrayList<DataCategoryDefine>> resultInfo = new ResultInfo<ArrayList<DataCategoryDefine>>();
		SQLiteDatabase db = null;
		ArrayList<DataCategoryDefine> data = null;
		try {
			data = new ArrayList<DataCategoryDefine>();
			db = SQLiteHelper.getReadableDB();
			Cursor cursor = db.query(new DataCategoryDefine().getTableName(), null, "DDID=?", new String[] { String.valueOf(ddid) }, null, null, "IOrder");
			if (cursor != null) {
				while (cursor.moveToNext()) {
					DataCategoryDefine dataCategoryDefine = new DataCategoryDefine();
					dataCategoryDefine.setValueByCursor(cursor);
					data.add(dataCategoryDefine);
				}
				resultInfo.Data = data;
			}
			cursor.close();
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;// 如果失败，data的值为null
			resultInfo.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 根据DDID查询勘察配置表属性信息表的数据
	 * 
	 * @param dDID
	 *            ：所属勘察配置表的ID，即属性项在后台管理系统中对应的ID值
	 * @param categoryID
	 *            ：属于哪个分类项的属性列表信息
	 * @return
	 */
	public static ResultInfo<ArrayList<DataFieldDefine>> queryDataFieldDefineByID(int ddid, int categoryID) {
		ResultInfo<ArrayList<DataFieldDefine>> resultInfo = new ResultInfo<ArrayList<DataFieldDefine>>();
		SQLiteDatabase db = null;
		ArrayList<DataFieldDefine> data = null;
		try {
			data = new ArrayList<DataFieldDefine>();
			db = SQLiteHelper.getReadableDB();
			Cursor cursor = db.query(new DataFieldDefine().getTableName(), null, "DDID=? And CategoryID=?", new String[] { String.valueOf(ddid), String.valueOf(categoryID) }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					DataFieldDefine dataFieldDefine = new DataFieldDefine();
					dataFieldDefine.setValueByCursor(cursor);
					data.add(dataFieldDefine);
				}
				resultInfo.Data = data;
			}
			cursor.close();
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;// 如果失败，data为null
			resultInfo.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 根据勘察表分类ID取得勘察表分类项
	 * 
	 * @param CategoryId
	 * @return
	 */
	public static ResultInfo<DataCategoryDefine> getDataCategoryDefineByCategoryId(Integer CategoryID) {
		ResultInfo<DataCategoryDefine> result = new ResultInfo<DataCategoryDefine>();
		SQLiteDatabase db = null;
		DataCategoryDefine dataCategoryDefine = null;
		try {
			dataCategoryDefine = new DataCategoryDefine();
			db = SQLiteHelper.getReadableDB();
			Cursor cursor = db.query(new DataCategoryDefine().getTableName(), null, "CategoryID=?", new String[] { String.valueOf(CategoryID) }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					dataCategoryDefine.setValueByCursor(cursor);
					result.Data = dataCategoryDefine;
				}
				cursor.close();
			}
		} catch (Exception e) {
			result.Message = e.getMessage();
			result.Data = null;// 如果失败，Data为设为null
			result.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return result;
	}

	/**
	 * 获取完整的勘察配置表信息，包括分类和分类项的子项信息
	 * 
	 * @param ddid
	 *            :勘察配置表的ID值
	 * @return
	 */
	public static ResultInfo<DataDefine> getCompleteDataDefine(Integer ddid) {
		ResultInfo<DataDefine> resultInfo = new ResultInfo<DataDefine>();
		SQLiteDatabase db = null;
		DataDefine dataDefine = null;
		try {
			dataDefine = new DataDefine();
			db = SQLiteHelper.getReadableDB();
			// 数据的勘察信息
			Cursor cursor = db.query(new DataDefine().getTableName(), null, "DDID=?", new String[] { String.valueOf(ddid) }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					// 勘察表的信息
					dataDefine.setValueByCursor(cursor);

					Cursor cursorByDataCategoryDefine = db.query(new DataCategoryDefine().getTableName(), null, "DDID=?", new String[] { String.valueOf(ddid) }, null, null, "IOrder");
					if (cursorByDataCategoryDefine != null) {
						while (cursorByDataCategoryDefine.moveToNext()) {
							DataCategoryDefine dataCategoryDefine = new DataCategoryDefine();
							dataCategoryDefine.setValueByCursor(cursorByDataCategoryDefine);

							// 勘察表的子项信息
							Cursor cursorByDataFieldDefine = db.query(new DataFieldDefine().getTableName(), null, "DDID=? And CategoryID=?",
									new String[] { String.valueOf(ddid), String.valueOf(dataCategoryDefine.CategoryID) }, null, null, "IOrder");
							if (cursorByDataFieldDefine != null) {
								while (cursorByDataFieldDefine.moveToNext()) {
									DataFieldDefine dataFieldDefine = new DataFieldDefine();
									dataFieldDefine.setValueByCursor(cursorByDataFieldDefine);
									dataCategoryDefine.Fields.add(dataFieldDefine);
								}
								cursorByDataFieldDefine.close();
							}
							dataDefine.Categories.add(dataCategoryDefine);
						}
						cursorByDataCategoryDefine.close();
					}
					resultInfo.Data = dataDefine;
				}
				cursor.close();
			}
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;// 如果失败，Data为设为null
			resultInfo.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 获取有默认值的勘察表子项列表
	 * @param ddid任务使用的勘察表id
	 * @return
	 */
	public static ResultInfo<ArrayList<DataFieldDefine>> getHasDefaultItem(int ddid) {
		ResultInfo<ArrayList<DataFieldDefine>> result = new ResultInfo<ArrayList<DataFieldDefine>>();
		try {
			DataFieldDefine field = new DataFieldDefine();
			Cursor fieldCur = field.onSelect(null, " DDID = " + ddid + " and value != 'null' and length(value) > 0");
			if (fieldCur != null) {
				ArrayList<DataFieldDefine> lstField = new ArrayList<DataFieldDefine>();
				while (fieldCur.moveToNext()) {
					DataFieldDefine tempField = new DataFieldDefine();
					tempField.setValueByCursor(fieldCur);
					lstField.add(tempField);
				}
				result.Data = lstField;
				result.Success = true;
			}
		} catch (Exception e) {
			result.Data = null;
			result.Success = false;
		}
		return result;
	}
}
