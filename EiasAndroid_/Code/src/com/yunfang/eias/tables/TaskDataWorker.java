package com.yunfang.eias.tables;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.dto.TaskCategoryInfoByTypeDTO;
import com.yunfang.eias.dto.TaskCategoryInfoDTO;
import com.yunfang.eias.dto.TaskDataItemDTO;
import com.yunfang.eias.dto.TaskInfoDTO;
import com.yunfang.eias.enumObj.SortType;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.enumObj.TaskUploadStatusEnum;
import com.yunfang.eias.enumObj.UrgentStatusEnum;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.model.UserTaskInfo;
import com.yunfang.framework.db.SQLiteHelper;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.StringUtil;

/**
 * 
 * 项目名称：WaiCai 类名称：TaskDataWorker 类描述：任务数据表数据操作 创建人：lihc 创建时间：2014-4-10
 * 上午10:31:47 修改人：贺隽 修改时间：2014-4-24 上午10:31:47
 * 
 * @version
 */
public class TaskDataWorker {

	/***
	 * 
	 * @param taskNum
	 * @param taskID
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskCategoryInfoByTypeDTO>> getTaskCatgoryInfoAndItem(
			String taskNum, Integer taskID) {
		ResultInfo<ArrayList<TaskCategoryInfoByTypeDTO>> result = new ResultInfo<ArrayList<TaskCategoryInfoByTypeDTO>>();

		// 查询完整的任务信息,子项有值就存在 Item
		ResultInfo<TaskInfo> taskInfo = getCompleteTaskInfoByTaskNum(taskNum);
		ArrayList<TaskCategoryInfoByTypeDTO> tciDtoList = new ArrayList<TaskCategoryInfoByTypeDTO>();
		for (TaskCategoryInfo taskCateInfo : taskInfo.Data.Categories) {
			// 获取当前分类项的，分类配置
			ResultInfo<DataCategoryDefine> cateDefin = DataDefineWorker
					.getDataCategoryDefineByCategoryId(taskCateInfo.CategoryID);
			if (cateDefin.Success) {
				TaskCategoryInfoByTypeDTO temp = new TaskCategoryInfoByTypeDTO();
				temp.setTaskCatgoyInfo(taskCateInfo);
				temp.setDataCategoryDefine(cateDefin.Data);
				tciDtoList.add(temp);
			}
		}
		result.Data = tciDtoList;
		if (tciDtoList.size() < 0) {
			result.Success = false;
			result.Message = "找不到该任务分类子项";
		}

		return result;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-9-23 上午11:34:13
	 * @Description: 获取数据库当前用户，待提交的任务信息
	 * @param currentUser
	 *            当前用户
	 * @return ResultInfo<ArrayList<TaskInfo>> 返回类型
	 * @version V1.0
	 */
	public static ResultInfo<ArrayList<TaskInfo>> getSubmmitWaitTask(
			UserInfo currentUser) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		SQLiteDatabase db = null;
		TaskInfo taskInfo = null;
		ArrayList<TaskInfo> taksInfos = new ArrayList<TaskInfo>();
		try {
			taskInfo = new TaskInfo();
			db = SQLiteHelper.getWritableDB();

			StringBuilder queryStr = new StringBuilder(taskInfo.getTableName()
					+ " where User='");
			queryStr.append(currentUser.Name + "'");// 当前用户
			// 状态未待提交
			int UploadStatus = TaskUploadStatusEnum.Submitwating.getIndex();
			queryStr.append(" and UploadStatus=" + UploadStatus);// 当前任务状态
			Cursor cursor = db.rawQuery("select * from " + queryStr.toString(),
					null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskInfo.setValueByCursor(cursor);
					taksInfos.add(taskInfo);
				}
			}
			result.Data = taksInfos;

		} catch (Exception e) {
			result.Success = false;
			result.Data = taksInfos;
			result.Message = "数据库操作失败！";
		}
		return result;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-9-18 下午12:59:17
	 * @Description: 同步服务器中的任务子项 value
	 * @param dataItem
	 * @return ResultInfo<Integer> 返回类型
	 * @version V1.0
	 */
	public static ResultInfo<Integer> updataTaskDataItems(
			List<TaskDataItem> dataItem) {
		ResultInfo<Integer> result = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		int resultCount = 0;
		try {
			db = SQLiteHelper.getReadableDB();
			for (TaskDataItem tempDataItem : dataItem) {
				// value 不为空值覆盖本地的值,
				if (!StringUtil.IsNullOrEmpty(tempDataItem.Value)) {
					TaskDataItem taskDataItem = new TaskDataItem();
					String baseId = String.valueOf(tempDataItem.ID);
					Cursor cursor = taskDataItem.onSelect(null, "BaseID='"
							+ baseId + "'");
					cursor.moveToNext();
					taskDataItem.setValueByCursor(cursor);
					// 媒体分类项的子项Value 不修改。
					if (taskDataItem.Value.contains(".jpg")
							|| taskDataItem.Value.contains(".amr")
							|| taskDataItem.Value.contains(".mp4")) {
						continue;
					}
					taskDataItem.Value = tempDataItem.Value;
					resultCount += taskDataItem.onUpdate("BaseID='" + baseId
							+ "'");// 更新本地本条数据
				}
			}
			result.Data = resultCount;
		} catch (Exception e) {
			result.Success = false;
			result.Data = 0;
			result.Message = "数据库操作失败！";
		} finally {
			db.close();
		}

		return result;
	}

	/**
	 * 检查是否有自建任务数据
	 * 
	 * @param currentUser
	 *            :用户信息
	 * @return
	 */
	public static ResultInfo<Integer> createdByUserTaskTotal(
			UserInfo currentUser) {
		ResultInfo<Integer> result = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		try {
			db = SQLiteHelper.getWritableDB();
			StringBuilder sqlBuilder = new StringBuilder(
					"select count(1) from TaskInfo where User='"
							+ currentUser.Name + "'");
			sqlBuilder.append(" and IsNew=1 and Status="
					+ TaskStatus.Doing.getIndex());
			// sqlBuilder.append(" and Status=" + TaskStatus.Doing.getIndex());

			result.Data = 0;
			Cursor cursor = db.rawQuery(sqlBuilder.toString(), null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					result.Data = cursor.getInt(0);
				}
			}
			cursor.close();
		} catch (Exception e) {
			result.Data = 0;
			result.Message = "操作失败";
		}

		return result;
	}

	/**
	 * 查询本地数据 taskInfo 离线勘察时，获取本地已领取任务信息 Normal("一般",0), Urgent("紧急",1) isNew =0
	 * 是否为新建，标记是否为用户自建任务，0为非自建，1为自建(枚举类型) Status =1
	 * 项目状态TaskStatus:待领取=0，待提交=1，已完成=2（自建任务时，状态默认为待提交，状态值为1）(枚举类型)
	 * 
	 * @author kevin
	 * @param currentUser
	 *            用户信息
	 * @return ResultInfo<UserTaskInfo>
	 */
	public static ResultInfo<UserTaskInfo> queryUserInfo(UserInfo currentUser) {
		ResultInfo<UserTaskInfo> result = new ResultInfo<UserTaskInfo>();
		UserTaskInfo userTaskInfo = new UserTaskInfo();
		SQLiteDatabase db = null;
		StringBuilder queryStr = new StringBuilder(
				"select * from TaskInfo where User=?");
		queryStr.append(" and IsNew=? and Status=?");
		try {
			db = SQLiteHelper.getWritableDB();
			Cursor cursor = db.rawQuery(queryStr.toString(), new String[] {
					currentUser.Name, "0", "1" });
			if (cursor != null) {
				while (cursor.moveToNext()) {
					// 统计数据
					int urgentStatus = cursor.getInt(cursor
							.getColumnIndex("UrgentStatus"));
					if (urgentStatus == UrgentStatusEnum.Normal.getIndex()) {// 正常任务
						userTaskInfo.ReceivedNormal++;
					} else if (urgentStatus == UrgentStatusEnum.Urgent
							.getIndex()) {// 异常任务
						userTaskInfo.ReceivedUrgent++;
					}
					userTaskInfo.ReceivedTotals++;// 已领取总数
				}
				result.Data = userTaskInfo;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			result.Data = null;
		}

		return result;
	}

	// {{ createLocalTask 新建勘察类型
	/**
	 * 新建勘察类型
	 * 
	 * @param categoryDefine
	 *            :勘察配置表分类项信息表
	 * @param taskID
	 *            :任务编号
	 * @param remarkName
	 *            :标识名称
	 * @return
	 */
	public static ResultInfo<TaskCategoryInfo> createCategory(
			DataCategoryDefine dataCategoryDefine, Integer taskID,
			Boolean isCreatedByUesr, Integer identityId, String remarkName) {
		ResultInfo<TaskCategoryInfo> result = new ResultInfo<TaskCategoryInfo>();
		SQLiteDatabase db = null;
		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();
			// new一个新项并赋值
			TaskCategoryInfo taskCategoryInfo = new TaskCategoryInfo();
			Integer dataDefineTotal = 0;
			for (DataFieldDefine item : dataCategoryDefine.Fields) {
				if (item.ShowInPhone) {
					dataDefineTotal += 1;
				}
			}
			taskCategoryInfo.TaskID = isCreatedByUesr ? identityId : taskID;
			taskCategoryInfo.RemarkName = remarkName;
			taskCategoryInfo.BaseCategoryID = dataCategoryDefine.ID;
			taskCategoryInfo.CreatedDate = DateTimeUtil.getCurrentTime();
			taskCategoryInfo.DataDefineFinishCount = -1;
			taskCategoryInfo.DataDefineTotal = dataDefineTotal > 0 ? dataDefineTotal
					: -1;
			taskCategoryInfo.CategoryID = dataCategoryDefine.CategoryID;
			long categoryId = taskCategoryInfo.onInsert();
			if (ListUtil.hasData(dataCategoryDefine.Fields)) {
				for (DataFieldDefine define : dataCategoryDefine.Fields) {
					TaskDataItem data = new TaskDataItem();
					data.BaseCategoryID = Integer.valueOf(String
							.valueOf(categoryId));
					data.BaseID = define.ID;
					data.CategoryID = define.CategoryID;
					data.IOrder = define.IOrder;
					data.Name = define.Name;
					data.TaskID = taskCategoryInfo.TaskID;
					data.Value = "";
					data.onInsert();
					taskCategoryInfo.Items.add(data);
				}
			}
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			result.Data = taskCategoryInfo;
		} catch (Exception e) {
			result.Message = e.getMessage();
			result.Success = false;
			result.Data = null;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return result;
	}

	// }}

	// {{ createLocalTask 新建本地任务

	/**
	 * 新建本地任务
	 * 
	 * @param isCreatedByUser
	 *            :是否为用户在本地创建
	 * @param taskNum
	 *            :任务编号
	 * @param address
	 *            :任务地址
	 * @param ddid
	 *            :任务所属的勘察表ID值
	 * @param ddVersion
	 *            :任务对应的版本号值
	 * @param copiedCategoryKeys
	 *            :复制项编号的集合（粘贴并创建新任务才会有，否则为空）
	 * @param copiedCategoryValues
	 *            :复制项名称的集合（粘贴并创建新任务才会有，否则为空）
	 * @param copied_task_id
	 *            :被复制任务的id（自建时为任务的自增id,否则为任务的taskID）
	 * @return
	 */
	public static ResultInfo<Long> createLocalTask(Boolean isCreatedByUser,
			String taskNum, String address, Integer ddid, Integer ddVersion,
			UserInfo currentUser) {

		ResultInfo<Long> result = new ResultInfo<Long>();
		ResultInfo<ArrayList<DataCategoryDefine>> categories = DataDefineWorker
				.queryDataCategoryDefineByDDID(ddid);
		if (categories.Success && ListUtil.hasData(categories.Data)) {
			SQLiteDatabase db = null;
			TaskInfo newTask = null;
			try {
				db = SQLiteHelper.getWritableDB();
				// 开启事务
				db.beginTransaction();
				newTask = new TaskInfo();
				newTask.TaskNum = taskNum;
				newTask.TargetAddress = address;
				newTask.Status = TaskStatus.Doing;
				newTask.DDID = ddid;
				newTask.DataDefineVersion = ddVersion;
				newTask.ID = (int) newTask.onInsert();

				if (categories.Data != null
						&& ListUtil.hasData(categories.Data)) {
					for (DataCategoryDefine category : categories.Data) {
						fillCategories(category, newTask.ID, 0);
					}
				}

				// 设置事务操作成功的标志
				db.setTransactionSuccessful();
				db.endTransaction();
				result.Data = (long) newTask.ID;
				DataLogOperator.taskCreated(newTask.TaskNum, "");
			} catch (Exception e) {
				result.Message = e.getMessage();
				result.Success = false;
				result.Data = (long) -1;
				DataLogOperator.taskCreated(newTask.TaskNum, result.Message);
			} finally {
				// 关闭数据库
				// db.close();
			}
		} else {
			result.Data = -1l;
			result.Message = "没有找到相关的分类项信息，无法创建任务";
		}
		return result;
	}

	// }}

	// {{ fillCompleteTaskDataInfos 填充 任务数据
	/**
	 * 删除完整的任务数据（事务操作）
	 * 
	 * @param taskID
	 *            :后台管理系统中对应任务编号的任务ID
	 * @param isNew
	 *            :是否为安卓端创建的任务
	 * @return
	 */
	public static ResultInfo<Integer> deleteCompleteTaskDataInfos(int taskID,
			Boolean isNew) {
		ResultInfo<Integer> resultInfo = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		// 删除数据影响的行数
		int rowNum = 0;
		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();
			rowNum = db.delete(new TaskInfo().getTableName(), isNew ? "ID=?"
					: "TaskID=?", new String[] { String.valueOf(taskID) });
			rowNum += db.delete(new TaskCategoryInfo().getTableName(),
					"TaskID=?", new String[] { String.valueOf(taskID) });
			rowNum += db.delete(new TaskDataItem().getTableName(), "TaskID=?",
					new String[] { String.valueOf(taskID) });
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			// 结束事务，默认是回滚
			db.endTransaction();
			resultInfo.Data = rowNum;// /此rowNum为删除影响的行数
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = -1;
			resultInfo.Success = false;
		} finally {
			// db.close();a0
		}
		return resultInfo;
	}

	/**
	 * 添加任务分类项数据，包含其下子项数据
	 * 
	 * @param categroyDefine
	 *            :勘察表分类项
	 * @param taskID
	 *            :任务编号
	 * @param loops
	 *            :循环次数
	 * @return
	 */
	private static ArrayList<TaskCategoryInfo> fillCategories(
			DataCategoryDefine categroyDefine, Integer taskID, Integer loops) {
		ArrayList<TaskCategoryInfo> result = new ArrayList<TaskCategoryInfo>();
		if (categroyDefine.DefaultShow) {
			// 是否默认显示
			loops = loops > 0 ? loops
					: (categroyDefine.RepeatLimit > 0 ? categroyDefine.RepeatLimit
							: 1); // 需要加载的次数
			ResultInfo<ArrayList<DataFieldDefine>> fileds = DataDefineWorker
					.queryDataFieldDefineByID(categroyDefine.DDID,
							categroyDefine.CategoryID);
			do {
				TaskCategoryInfo newTaskCategory = new TaskCategoryInfo();
				newTaskCategory.TaskID = taskID;
				newTaskCategory.RemarkName = categroyDefine.RepeatLimit < 2 ? categroyDefine.Name
						: categroyDefine.Name + loops;
				newTaskCategory.CategoryID = categroyDefine.CategoryID;
				newTaskCategory.ID = (int) newTaskCategory.onInsert();

				if (fileds.Success && fileds.Data != null
						&& fileds.Data.size() > 0) {
					int dataDefineFinishCount = 0;
					ArrayList<TaskDataItem> items = new ArrayList<TaskDataItem>();
					for (DataFieldDefine defineItem : fileds.Data) {
						TaskDataItem newTaskItem = new TaskDataItem();
						newTaskItem.BaseCategoryID = newTaskCategory.ID;// 由于是用户自定义任务，所以这里的BaseCategoryID对应TaskCategory的ID值
						newTaskItem.CategoryID = newTaskCategory.CategoryID;
						newTaskItem.TaskID = taskID;
						newTaskItem.IOrder = defineItem.IOrder;
						newTaskItem.Name = defineItem.Name;
						newTaskItem.Value = getTaskDataItemDefaultValue(defineItem);
						newTaskItem.ID = (int) newTaskItem.onInsert();

						if (newTaskItem.Value.length() > 0
								&& !newTaskItem.Value
										.equals(EIASApplication.DefaultDropDownListValue)) {
							dataDefineFinishCount += 1;
						}
						items.add(newTaskItem);
					}

					newTaskCategory.DataDefineFinishCount = dataDefineFinishCount;
					Integer dataDefineTotal = 0;
					for (DataFieldDefine item : fileds.Data) {
						if (item.ShowInPhone) {
							dataDefineTotal += 1;
						}
					}
					newTaskCategory.DataDefineTotal = dataDefineTotal;

					newTaskCategory.Items = items;
					result.add(newTaskCategory);
				}
				loops -= 1;

			} while (loops > 0);
		}
		return result;
	}

	/**
	 * 添加完整的任务数据（事务操作）
	 * 
	 * @param taskInfo
	 *            :勘察任务信息表,包含勘察任务分类项信息表集合 ,每个分类项中包含属于其的所有属性信息
	 * @return ResultInfo:操作结果
	 */
	public static ResultInfo<Long> fillCompleteTaskDataInfos(TaskInfo taskInfo) {
		ResultInfo<Long> resultInfo = new ResultInfo<Long>();
		// 插入数据是否成功的标志（大于0成功）
		long taskID = 0;
		SQLiteDatabase db = null;
		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();
			// 重置任务信息 如果没有就插入 有就更新
			taskID = resetTaskInfo(db, taskInfo);
			if (taskInfo.Categories != null && taskInfo.Categories.size() > 0) {
				// 向勘察任务分类项信息表(TaskCategoryInfo)插入多条数据
				for (TaskCategoryInfo taskCategoryInfo : taskInfo.Categories) {
					resetTaskCategoryInfo(db, taskCategoryInfo);
					if (taskCategoryInfo.Items != null
							&& taskCategoryInfo.Items.size() > 0
							&& taskCategoryInfo.Items.get(0) != null) {
						// 勘察任务属性数据记录表(TaskDataItem)插入多条数据
						for (TaskDataItem taskDataItem : taskCategoryInfo.Items) {
							resetTaskDataItem(db, taskDataItem);
						}
					}
				}
			}
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			resultInfo.Data = taskID;// 此id大于0为成功
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Success = false;
			resultInfo.Data = (long) -1;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 
	 * 获取完整勘察子项的数量
	 * 
	 * @param dataCategoryDefines
	 *            完整的勘察数量
	 * @param categoryID
	 *            勘察编号
	 * @return
	 */
	private static Integer getDefineTotal(
			ArrayList<DataCategoryDefine> dataCategoryDefines,
			Integer categoryID) {
		Integer result = 0;

		if (dataCategoryDefines != null
				&& ListUtil.hasData(dataCategoryDefines)) {
			for (DataCategoryDefine defineItem : dataCategoryDefines) {
				if (defineItem.CategoryID == categoryID) {
					result = defineItem.Fields.size();
					break;
				}
			}
		}

		return result;
	}

	// }}

	/**
	 * 获取任务中已经存在的分类藉项信息
	 * 
	 * @param taskID
	 * @return
	 */
	public static ArrayList<TaskCategoryInfo> getTaskCategories(Integer taskID) {
		ArrayList<TaskCategoryInfo> result = new ArrayList<TaskCategoryInfo>();

		SQLiteDatabase db = SQLiteHelper.getReadableDB();
		Cursor cursor = db.query(new TaskCategoryInfo().getTableName(), null,
				"TaskID=?", new String[] { String.valueOf(taskID) }, null,
				null, "CategoryID,RemarkName");
		if (cursor != null) {
			while (cursor.moveToNext()) {
				TaskCategoryInfo taskCategoryInfo = new TaskCategoryInfo();
				taskCategoryInfo.setValueByCursor(cursor);
				result.add(taskCategoryInfo);
			}
		}
		cursor.close();
		return setOrderTaskCategoryInfos(taskID, result);
	}

	/**
	 * 根据勘察的子项获取默认值
	 * 
	 * @param defineItem
	 *            :勘察子项信息
	 * @return
	 */
	private static String getTaskDataItemDefaultValue(DataFieldDefine defineItem) {
		String result = "";
		if (defineItem.Value != null && !defineItem.Value.equals("null")
				&& defineItem.Value.length() > 0) {
			switch (defineItem.ItemType) {
			case UserName:
				result = EIASApplication.getCurrentUser().Name;
				break;
			case UserTel:
				result = EIASApplication.getCurrentUser().Mobile;
				break;
			default:
				result = defineItem.Value;
			}
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public static TaskCategoryInfo getTaskCategoryInfo(Boolean isCreatedByUesr,
			Integer identityId, Integer taskId, Integer categoryId,
			String remarkName) {
		// 得到要修改的勘察表分类项
		TaskCategoryInfo taskCategoryInfo = new TaskCategoryInfo();
		Cursor cursor = null;
		if (isCreatedByUesr) {
			cursor = taskCategoryInfo.onSelect(
					null,
					"ID= ? and CategoryID=? and RemarkName=?",
					new String[] { String.valueOf(identityId),
							String.valueOf(categoryId), remarkName });
		} else {
			cursor = taskCategoryInfo.onSelect(
					null,
					"TaskID= ? and CategoryID=? and RemarkName=?",
					new String[] { String.valueOf(taskId),
							String.valueOf(categoryId), remarkName });
		}
		if (cursor.moveToFirst()) {
			taskCategoryInfo.setValueByCursor(cursor);
		}
		cursor.close();
		return taskCategoryInfo;
	}

	/**
	 * 根据任务编号获取用户对象信息
	 * 
	 * @param taskId
	 *            :安卓本地自动增长编号，远程服务器任务编号
	 * @param isCreatedByUser
	 *            :是否为用户创建的任务
	 * @return
	 */
	public static ResultInfo<TaskInfo> getTaskInfoById(Integer taskId,
			Boolean isCreatedByUser) {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();
		TaskInfo taskInfo = new TaskInfo();
		Cursor cursor = null;
		if (isCreatedByUser) {
			cursor = taskInfo.onSelect(null, "ID= ?",
					new String[] { String.valueOf(taskId) });
		} else {
			cursor = taskInfo.onSelect(null, "TaskID= ?",
					new String[] { String.valueOf(taskId) });
		}
		if (cursor.moveToFirst()) {
			taskInfo.setValueByCursor(cursor);
		}
		cursor.close();
		result.Success = true;
		result.Data = taskInfo;
		return result;
	}

	/**
	 * 判断任务输入项目中是否已经添加过该项
	 * 
	 * @param newTaskItem
	 *            子项信息
	 * @param isCreatedByUser
	 *            是否在安卓端创建的任务
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Boolean hasTaskDataItem(TaskDataItem newTaskItem,
			Boolean isCreatedByUser) {
		Boolean result = false;
		int taskID = isCreatedByUser ? newTaskItem.ID : newTaskItem.TaskID;
		ResultInfo<ArrayList<TaskDataItem>> isExits = TaskDataWorker
				.queryTaskDataItemsByID(taskID, newTaskItem.CategoryID,
						newTaskItem.BaseCategoryID, false);
		if (isExits != null && isExits.Data != null && isExits.Data.size() > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * 获取用户自定义的任务列表
	 * 
	 * @param currentUser
	 *            :当前用户
	 * @param pageIndex
	 *            :当前页码
	 * @param pageSize
	 *            :当前页行数
	 * @param status
	 *            :查询任务的状态
	 * @param skipCount
	 *            :跳过指定条数取得数据
	 * @param queryStr
	 *            :过滤信息
	 * @param isNew
	 *            :是否为本地创建 只能为1或是0 1为本地创建 0为服务器创建
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskInfo>> queryCreatedByUserTaskInfoes(
			UserInfo currentUser, Integer pageIndex, Integer pageSize,
			TaskStatus status, Integer skipCount, String queryStr,
			String isNew, SortType sortType) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();

		SQLiteDatabase db = null;
		try {
			db = SQLiteHelper.getReadableDB();

			StringBuilder sqlBuilder = new StringBuilder(
					"from TaskInfo where User='");
			sqlBuilder.append(currentUser.Name + "'");
			sqlBuilder.append(" and Status in ('" + status.getIndex() + "','"
					+ -99 + "')");
			if (isNew.length() > 0) {
				sqlBuilder.append(" and IsNew=1 ");
			}
			if (queryStr.trim().length() > 0) {
				sqlBuilder.append(" AND (TaskNum");
				sqlBuilder.append(" like '%" + queryStr);
				sqlBuilder.append("%' or TargetAddress");
				sqlBuilder.append(" like '%" + queryStr);
				sqlBuilder.append("%')");
			}
			Cursor cursor = db.rawQuery(
					"select count(1) " + sqlBuilder.toString(), null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					result.Others = cursor.getInt(0);
				}
			}
			String sortStr = setSortStr(sortType);

			sqlBuilder.append(sortStr);
			if (sortType == null) {// 有排序条件，则不分页查询
				sqlBuilder.append(" limit " + ((pageIndex - 1) * pageSize)
						+ "," + (pageSize));
			}
			cursor = db.rawQuery("select * " + sqlBuilder.toString(), null);
			if (cursor != null) {
				TaskInfo taskInfo;
				ArrayList<TaskInfo> data = new ArrayList<TaskInfo>();
				while (cursor.moveToNext()) {
					taskInfo = new TaskInfo();
					taskInfo.setValueByCursor(cursor);
					data.add(taskInfo);
				}
				result.Data = data;
			}
			cursor.close();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "数据库操作失败!";
		}

		return result;
	}

	/***
	 * 拼接排序SQL语句
	 * 
	 * @param sqlBuilder
	 * @return
	 */
	private static String setSortStr(SortType sortType) {
		if (sortType == null) {
			return " order by ContactPerson desc,BookedDate,BookedTime";
		}
		StringBuilder sortStr = new StringBuilder();
		String isAsc = sortType.isAsc() ? "asc" : "desc";
		sortStr.append(" order by " + sortType.getFieldName() + " " + isAsc);
		
		return sortStr.toString();
	}

	/**
	 * 根据自增id查询相应的勘察任务分类项信息表（TaskCategoryInfo）数据
	 * 
	 * @param ID
	 *            :任务分类项信息
	 * @return
	 */
	public static ResultInfo<TaskCategoryInfo> queryOneTaskCategoryInfo(int ID) {
		ResultInfo<TaskCategoryInfo> resultInfo = new ResultInfo<TaskCategoryInfo>();
		TaskCategoryInfo taskCategoryInfo = null;
		SQLiteDatabase db = null;
		try {
			db = SQLiteHelper.getReadableDB();
			Cursor cursor = db.query(new TaskCategoryInfo().getTableName(),
					null, "ID=?", new String[] { String.valueOf(ID) }, null,
					null, null);

			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskCategoryInfo = new TaskCategoryInfo();
					taskCategoryInfo.setValueByCursor(cursor);
					resultInfo.Data = taskCategoryInfo;
				}
			}
			cursor.close();
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;
			resultInfo.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 查询某个任务下的分类项信息 每次查询时，需要与勘察表进行对比，如果不相符，需要进行调整
	 * 
	 * @param taskID
	 *            :自建任务为Android端ID值，非自建任务为远程服务端ID值
	 * @param isCreatedByUser
	 *            :是否为自建任务
	 * @param isGetAllByTaskId
	 *            :是否
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskCategoryInfo>> queryTaskCategories(
			Integer taskID, Boolean isCreatedByUser,
			Boolean isGetAllTaskDataItemByTaskId) {
		ResultInfo<ArrayList<TaskCategoryInfo>> resultInfo = new ResultInfo<ArrayList<TaskCategoryInfo>>();

		try {
			// 任务信息的勘察信息版本
			int taskCategoryVersion = -1;
			// 勘察表的信息版本
			int defineCategoryVersion = -1;

			// 获取任务信息
			ResultInfo<TaskInfo> taskResultInfo = getTaskInfoById(taskID,
					isCreatedByUser);
			taskResultInfo.Data.IsNew = isCreatedByUser;
			if (taskResultInfo.Data != null) {
				taskCategoryVersion = taskResultInfo.Data.DataDefineVersion;
			}
			// 获取勘察信息
			ResultInfo<DataDefine> defineCategory = DataDefineWorker
					.queryDataDefineByDDID(taskResultInfo.Data.DDID);
			if (defineCategory.Data == null) { // 本地找不到这张勘察表， 认为这张勘察表在服务器被删除了，
												// 直接返回
				resultInfo.Success = true;
				resultInfo.Data = new ArrayList<TaskCategoryInfo>();// 这里不可以为NULL
				return resultInfo;
			} else {
				defineCategoryVersion = defineCategory.Data.Version;
			}
			// 获取之前的勘察信息
			ArrayList<TaskCategoryInfo> beforeTaskCategoryInfos = getTaskCategories(taskID);

			// 如果任务的勘察信息和 勘察表的版本不匹配就重置一下勘察信息
			if (taskCategoryVersion != defineCategoryVersion) {
				taskResultInfo.Data.DataDefineVersion = defineCategoryVersion;
				resetDataDefine(taskID, resultInfo, taskResultInfo,
						beforeTaskCategoryInfos, isGetAllTaskDataItemByTaskId);
			} else {
				setDataDefineFinishCount(taskResultInfo,
						beforeTaskCategoryInfos, isGetAllTaskDataItemByTaskId);
				resultInfo.Data = beforeTaskCategoryInfos;
			}
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = null;// 如果失败，data为null
			resultInfo.Success = false;
		} finally {

		}
		return resultInfo;
	}

	// {{ 查询某个任务下的分类项信息 每次查询时，需要与勘察表进行对比，如果不相符，需要进行调整

	/**
	 * 根据id查询该勘察任务分类项下所有勘察任务属性数据记录表（TaskDataItem）的数据
	 * 
	 * @param taskID
	 *            :任务ID值
	 * @param categoryID
	 *            :分类项标识，用于区分可重复项中的哪一个具体的重复值，对应TaskCategoryInfos表中的ID值
	 * @param baseCategoryID
	 *            :勘察配置表下分类项的ID，对应TaskCategoryInfos表中的BaseCategoryID值
	 * @param RemarkName
	 *            :标识名称，同一勘察任务下，不可重复
	 * @param isCreateByUser
	 *            :是否用户自建的项目
	 * @param isGetAllTaskDataItemByTaskId
	 *            :是否获取任务下所有的勘察信息
	 * 
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskDataItem>> queryTaskDataItemsByID(
			int taskID, int categoryID, int baseCategoryID,
			Boolean isGetAllTaskDataItemByTaskId) {
		ResultInfo<ArrayList<TaskDataItem>> resultInfo = new ResultInfo<ArrayList<TaskDataItem>>();
		ArrayList<TaskDataItem> data = null;
		TaskDataItem taskDataItem = null;
		SQLiteDatabase db = null;
		try {
			taskDataItem = new TaskDataItem();
			data = new ArrayList<TaskDataItem>();
			db = SQLiteHelper.getReadableDB();
			Cursor cursor;

			if (isGetAllTaskDataItemByTaskId) {
				cursor = db.query(taskDataItem.getTableName(), null,
						"TaskID=?", new String[] { String.valueOf(taskID) },
						null, null, null);
			} else {
				// cursor = db.query(taskDataItem.getTableName(), null,
				// "TaskID=? AND baseCategoryID=? AND CategoryID=?",
				// new String[] { String.valueOf(taskID),
				// String.valueOf(baseCategoryID), String.valueOf(categoryID) },
				// null, null, null);
				cursor = db.query(
						taskDataItem.getTableName(),
						null,
						"TaskID=? AND CategoryID=?",
						new String[] { String.valueOf(taskID),
								String.valueOf(categoryID) }, null, null, null);
				/*
				 * 自建任务 baseCategoryID 是-1 导致查不出数据，清除不了子项，修改为 CategoryID 查。
				 * cursor = db.query(taskDataItem.getTableName(), null,
				 * "TaskID=? AND baseCategoryID=?", new String[] {
				 * String.valueOf(taskID), String.valueOf(baseCategoryID) },
				 * null, null, null);
				 */
			}

			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskDataItem = new TaskDataItem();
					taskDataItem.setValueByCursor(cursor);
					data.add(taskDataItem);
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
	 * 根据id查询该勘察任务分类项下所有勘察任务属性数据记录表（TaskDataItem）的数据
	 * 
	 * @param taskID
	 *            :任务ID值
	 * @param categoryID
	 *            :分类项标识，用于区分可重复项中的哪一个具体的重复值，对应TaskCategoryInfos表中的ID值
	 * @param baseCategoryID
	 *            :勘察配置表下分类项的ID，对应TaskCategoryInfos表中的BaseCategoryID值
	 * @param RemarkName
	 *            :标识名称，同一勘察任务下，不可重复
	 * @param isCreateByUser
	 *            :是否用户自建的项目
	 * @param isGetAllTaskDataItemByTaskId
	 *            :是否获取任务下所有的勘察信息
	 * 
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskDataItem>> queryTaskDataItemsBybaseCategoryID(
			int taskID, int categoryID, int baseCategoryID,
			Boolean isGetAllTaskDataItemByTaskId) {
		ResultInfo<ArrayList<TaskDataItem>> resultInfo = new ResultInfo<ArrayList<TaskDataItem>>();
		ArrayList<TaskDataItem> data = null;
		TaskDataItem taskDataItem = null;
		SQLiteDatabase db = null;
		try {
			taskDataItem = new TaskDataItem();
			data = new ArrayList<TaskDataItem>();
			db = SQLiteHelper.getReadableDB();
			Cursor cursor;

			if (isGetAllTaskDataItemByTaskId) {
				cursor = db.query(taskDataItem.getTableName(), null,
						"TaskID=?", new String[] { String.valueOf(taskID) },
						null, null, null);
			} else {
				// cursor = db.query(taskDataItem.getTableName(), null,
				// "TaskID=? AND baseCategoryID=? AND CategoryID=?",
				// new String[] { String.valueOf(taskID),
				// String.valueOf(baseCategoryID), String.valueOf(categoryID) },
				// null, null, null);

				// 自建任务 baseCategoryID 是-1 导致查不出数据，清除不了子项，修改为 CategoryID 查。
				cursor = db.query(
						taskDataItem.getTableName(),
						null,
						"TaskID=? AND baseCategoryID=?",
						new String[] { String.valueOf(taskID),
								String.valueOf(baseCategoryID) }, null, null,
						null);

			}

			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskDataItem = new TaskDataItem();
					taskDataItem.setValueByCursor(cursor);
					data.add(taskDataItem);
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
	 * 获取某一个任务信息
	 * 
	 * @param taskId
	 *            :任务编号 用isCreateByUser判断是否为用户在安卓端创建
	 * @param isCreateByUser
	 *            :判断是否为用户在安卓端创建
	 * @return
	 */
	public static ResultInfo<TaskInfo> queryTaskInfo(Integer taskId,
			Boolean isCreateByUser) {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();
		TaskInfo taskInfo = new TaskInfo();
		String whereStr = "";

		if (!isCreateByUser) {
			whereStr = "taskId = " + taskId;
		} else {
			whereStr = "Id = " + taskId;
		}
		try {
			Cursor cursor = taskInfo.onSelect(null, whereStr);
			if (cursor.moveToFirst()) {
				taskInfo.setValueByCursor(cursor);
				result.Data = taskInfo;
			}
			cursor.close();
		} catch (Exception e) {
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 获取某一个任务信息
	 * 
	 * @param tasknum任务编码
	 * @return
	 */
	public static ResultInfo<TaskInfo> queryTaskInfoByTaskNum(String tasknum) {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();
		TaskInfo taskInfo = new TaskInfo();
		try {
			Cursor cursor = taskInfo.onSelect(null, "TaskNum = '" + tasknum
					+ "'");
			if (cursor.moveToFirst()) {
				taskInfo.setValueByCursor(cursor);
				result.Data = taskInfo;
			}
			cursor.close();
		} catch (Exception e) {
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 根据任务编号或者地址查询勘察任务信息表（TaskInfo）
	 * 
	 * @param selectStr
	 *            :任务编号或地址查询的相关条件
	 * @param currentUser
	 *            ：指定查询用户的信息，即当前用户信息
	 * @param status
	 *            :指定查询哪一种状态的任务信息，只支持 待提交和已完成这两种状态的查询
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            ：每页行数
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskInfo>> queryTaskInfoes(
			int pageIndex, int pageSize, String selectStr,
			UserInfo currentUser, TaskStatus status, Boolean onlyReportTask,SortType sortType) {
		ResultInfo<ArrayList<TaskInfo>> resultInfo = new ResultInfo<ArrayList<TaskInfo>>();
		ArrayList<TaskInfo> data = null;
		TaskInfo taskInfo = null;
		SQLiteDatabase db = null;
		try {
			data = new ArrayList<TaskInfo>();
			taskInfo = new TaskInfo();
			db = SQLiteHelper.getReadableDB();
			StringBuilder sqlBuilder = new StringBuilder(
					"from TaskInfo where User='");
			sqlBuilder.append(currentUser.Name + "'");
			sqlBuilder.append(" and Status=" + status.getIndex());
			if (selectStr.trim().length() > 0) {
				sqlBuilder.append(" AND (TaskNum");
				sqlBuilder.append(" like '%" + selectStr);
				sqlBuilder.append("%' or TargetAddress");
				sqlBuilder.append(" like '%" + selectStr);
				sqlBuilder.append("%')");
			}
			if (onlyReportTask) {
				sqlBuilder.append(" and InworkReportFinish=1");
			}
			/*String descString = "";
			if (status == TaskStatus.Done) {
				descString = "desc";
			}*/
			Cursor cursor = db.rawQuery(
					"select count(1) " + sqlBuilder.toString(), null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					resultInfo.Others = cursor.getInt(0) + ",0";
				}
			}
			
			String sortStr="";
			
			sortStr=setSortStr(sortType);
			if(sortType==null){
				sortStr=sortStr+" desc";
			}
			sqlBuilder.append(sortStr
							+ "  limit "
							+ (pageIndex - 1)
							* pageSize + "," + pageSize);
			cursor = db.rawQuery("select * " + sqlBuilder.toString(), null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskInfo = new TaskInfo();
					taskInfo.setValueByCursor(cursor);
					data.add(taskInfo);
				}
				resultInfo.Data = data;
				cursor.close();
			}
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Success = false;
			resultInfo.Data = null;// 如果失败，data为null
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 查询单个子项的对象
	 * 
	 * @param taskID
	 *            :任务编号
	 * @param categoryID
	 *            :任务勘察编号
	 * @param dataItemName
	 *            :子项名称
	 * @return
	 */
	public static ResultInfo<TaskDataItem> queryOneTaskDataItems(int taskID,
			int categoryID, String dataItemName) {
		ResultInfo<TaskDataItem> resultInfo = new ResultInfo<TaskDataItem>();
		SQLiteDatabase db = null;
		TaskDataItem taskDataItem = null;
		try {
			taskDataItem = new TaskDataItem();
			db = SQLiteHelper.getReadableDB();
			Cursor cursor = db.query(taskDataItem.getTableName(), null,
					"TaskID=? and CategoryID=? and Name = ?", new String[] {
							String.valueOf(taskID), String.valueOf(categoryID),
							String.valueOf(dataItemName), }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskDataItem = new TaskDataItem();
					taskDataItem.setValueByCursor(cursor);
				}
				resultInfo.Data = taskDataItem;
				cursor.close();
			}
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
	 * 版本号不一样，需要重新加载分类项
	 * 
	 * @param taskID
	 *            :任务编号
	 * @param resultInfo
	 *            :需要返回的结果详细
	 * @param taskResultInfo
	 *            :任务结果详细
	 * @param beforeTaskCategoryInfos
	 *            :之前的分类项详细
	 */
	private static ResultInfo<Boolean> resetDataDefine(Integer taskID,
			ResultInfo<ArrayList<TaskCategoryInfo>> resultInfo,
			ResultInfo<TaskInfo> taskResultInfo,
			ArrayList<TaskCategoryInfo> beforeTaskCategoryInfos,
			Boolean isGetAllTaskDataItemByTaskId) {

		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			// 获取勘察表的分类项
			ResultInfo<ArrayList<DataCategoryDefine>> dataCategoryDefines = DataDefineWorker
					.queryDataCategoryDefineByDDID(taskResultInfo.Data.DDID);
			// 获取数据库对象
			SQLiteDatabase db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();
			// 记录需要新的分类项和子项
			ArrayList<TaskCategoryInfo> newTaskCategoryInfos = new ArrayList<TaskCategoryInfo>();
			for (DataCategoryDefine categroyDefine : dataCategoryDefines.Data) {
				// 存放临时勘察信息
				ArrayList<TaskCategoryInfo> tempTaskCategoryInfos = new ArrayList<TaskCategoryInfo>();
				for (TaskCategoryInfo taskCategoryInfoItem : beforeTaskCategoryInfos) {
					if (taskCategoryInfoItem.CategoryID == categroyDefine.CategoryID) {
						// 记录临时的勘察信息
						tempTaskCategoryInfos.add(taskCategoryInfoItem);
						// 记录新的勘察信息
						newTaskCategoryInfos.add(taskCategoryInfoItem);
					}
				}
				// 在之前的勘察信息中移除临时的 剩下的就是需要删除的
				beforeTaskCategoryInfos.removeAll(tempTaskCategoryInfos);
				// 如果没有找到匹配的项就加入原来的
				updateTaskCategoryInfos(taskID, db, newTaskCategoryInfos,
						categroyDefine, tempTaskCategoryInfos);
			}
			// 如果之前的数据有数据 就删除
			if (ListUtil.hasData(beforeTaskCategoryInfos)) {
				for (TaskCategoryInfo item : beforeTaskCategoryInfos) {
					db.delete(item.getTableName(), "TaskID=?",
							new String[] { String.valueOf(taskID) });
				}
			}

			// 设置子项
			setDataDefineFinishCount(taskResultInfo, newTaskCategoryInfos,
					isGetAllTaskDataItemByTaskId);

			// 更新版本号
			if (taskResultInfo.Data.ID > 0) {
				taskResultInfo.Data.onUpdate("ID = " + taskResultInfo.Data.ID);
			} else {
				taskResultInfo.Data.onInsert();
			}

			// 保存分类项的子项的完成数量和总数量
			for (TaskCategoryInfo taskCategoryInfo : newTaskCategoryInfos) {
				if (taskCategoryInfo.ID > 0) {
					taskCategoryInfo.onUpdate("ID = " + taskCategoryInfo.ID);
				} else {
					taskCategoryInfo.onInsert();
				}
			}

			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			resultInfo.Data = newTaskCategoryInfos;

			result.Data = true;
			result.Success = true;
		} catch (Exception e) {
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 如果没有找到匹配的项就加入原来的
	 * 
	 * @param taskID
	 *            :任务编号
	 * @param db
	 *            :数据库对象
	 * @param newTaskCategoryInfos
	 *            :新的任务勘察分类信息
	 * @param categroyDefine
	 *            :勘察分类信息
	 * @param tempTaskCategoryInfos
	 *            :临时的勘察分类信息
	 * @return
	 */
	private static ResultInfo<Boolean> updateTaskCategoryInfos(Integer taskID,
			SQLiteDatabase db,
			ArrayList<TaskCategoryInfo> newTaskCategoryInfos,
			DataCategoryDefine categroyDefine,
			ArrayList<TaskCategoryInfo> tempTaskCategoryInfos) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			if (tempTaskCategoryInfos.size() == 0) {
				newTaskCategoryInfos.addAll(fillCategories(categroyDefine,
						taskID, 0));
			} else {// 否则就是新增的
					// 如果限定值大于0 并且大于分类项的数量
				if (categroyDefine.RepeatLimit > 0
						&& categroyDefine.RepeatLimit > tempTaskCategoryInfos
								.size()) {
					// 创建新的勘察分类子项
					categroyDefine.Name = categroyDefine.Name + "-新建";
					newTaskCategoryInfos.addAll(fillCategories(categroyDefine,
							taskID, categroyDefine.RepeatLimit
									- tempTaskCategoryInfos.size()));
				}
				// 如果还大于最大值
				if (categroyDefine.RepeatMax > 0
						&& tempTaskCategoryInfos.size() > categroyDefine.RepeatMax) {
					TaskDataItem tempItem = new TaskDataItem();
					// 删除多余的
					for (int i = categroyDefine.RepeatMax; i < tempTaskCategoryInfos
							.size(); i++) {
						TaskCategoryInfo temp = tempTaskCategoryInfos.get(i);
						newTaskCategoryInfos.remove(temp);
						db.delete(temp.getTableName(), "ID=" + temp.ID, null);
						db.delete(
								tempItem.getTableName(),
								"BaseCategoryID="
										+ (temp.BaseCategoryID > 0 ? temp.BaseCategoryID
												: temp.ID), null);
					}
				}
			}
			result.Data = true;
			result.Success = true;

		} catch (Exception e) {
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 重置勘察任务分类项信息 如果没有就插入 有就更新
	 * 
	 * @param db
	 *            :数据库对象
	 * @param taskCategoryInfo
	 *            :勘察任务分类项信息
	 */
	private static ResultInfo<Boolean> resetTaskCategoryInfo(SQLiteDatabase db,
			TaskCategoryInfo taskCategoryInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			// 获取当前勘察任务分类项信息 添加BaseCategoryID
			Cursor taskCategoryInfoCursor = db.query(
					taskCategoryInfo.getTableName(),
					null,
					"TaskID=? and CategoryID=? and BaseCategoryID=?",
					new String[] { String.valueOf(taskCategoryInfo.TaskID),
							String.valueOf(taskCategoryInfo.CategoryID),
							String.valueOf(taskCategoryInfo.BaseCategoryID) },
					null, null, null);

			// 准备数据
			ContentValues taskCategoryInfoValues = taskCategoryInfo
					.getContentValues();
			// 将分类项名称按照勘察表名称赋值一次
			ResultInfo<DataCategoryDefine> dataCategoryDefine = DataDefineWorker
					.getDataCategoryDefineByCategoryId(taskCategoryInfo.CategoryID);
			if (dataCategoryDefine.Data != null) {
				DataCategoryDefine categoryDefine = (DataCategoryDefine) dataCategoryDefine.Data;
				// 过滤重复项
				if (!categoryDefine.Repeat) {
					taskCategoryInfoValues.put("RemarkName",
							categoryDefine.Name);
				}
			}

			if (taskCategoryInfoCursor != null
					&& taskCategoryInfoCursor.moveToFirst()) {
				TaskCategoryInfo dbItem = new TaskCategoryInfo();
				dbItem.setValueByCursor(taskCategoryInfoCursor);
				db.update(taskCategoryInfo.getTableName(),
						taskCategoryInfoValues, "ID=?",
						new String[] { String.valueOf(dbItem.ID) });
			} else {
				db.insert(taskCategoryInfo.getTableName(), null,
						taskCategoryInfoValues);
			}
			taskCategoryInfoCursor.close();
			result.Data = true;
			result.Success = true;
		} catch (Exception e) {
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 重置勘察任务分类项信息 如果没有就插入 有就更新
	 * 
	 * @param db
	 *            :数据库对象
	 * @param taskDataItem
	 *            :勘察任务属性数据记录
	 */
	private static ResultInfo<Boolean> resetTaskDataItem(SQLiteDatabase db,
			TaskDataItem taskDataItem) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			// 获取当前勘察任务分类项信息 添加BaseCategoryID
			Cursor taskDataItemCursor = db
					.query(taskDataItem.getTableName(),
							null,
							"TaskID=? and CategoryID=? and BaseCategoryID=? and Name=?",
							new String[] {
									String.valueOf(taskDataItem.TaskID),
									String.valueOf(taskDataItem.CategoryID),
									String.valueOf(taskDataItem.BaseCategoryID),
									String.valueOf(taskDataItem.Name) }, null,
							null, null);

			// 准备数据
			ContentValues taskDataItemValues = taskDataItem.getContentValues();
			if (taskDataItemCursor != null && taskDataItemCursor.moveToFirst()) {
				TaskDataItem dbItem = new TaskDataItem();
				dbItem.setValueByCursor(taskDataItemCursor);

				db.update(taskDataItem.getTableName(), taskDataItemValues,
						"ID=?", new String[] { String.valueOf(dbItem.ID) });
			} else {
				db.insert(taskDataItem.getTableName(), null, taskDataItemValues);
			}
			taskDataItemCursor.close();
			result.Data = true;
			result.Success = true;
		} catch (Exception e) {
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 重置任务信息 如果没有就插入 有就更新
	 * 
	 * @param db
	 *            :数据库对象
	 * @param taskInfo
	 *            :勘察任务信息表
	 * @return 任务编号
	 */
	private static long resetTaskInfo(SQLiteDatabase db, TaskInfo taskInfo) {
		long taskID;
		// 准备插入的参数
		ContentValues taskInfoValues = taskInfo.getContentValues();
		// 获取当前任务
		Cursor taskInfoCursor = db.query(new TaskInfo().getTableName(), null,
				"TaskID=?", new String[] { String.valueOf(taskInfo.TaskID) },
				null, null, null);
		if (taskInfoCursor != null && taskInfoCursor.moveToFirst()) {
			TaskInfo dbItem = new TaskInfo();
			dbItem.setValueByCursor(taskInfoCursor);
			taskID = db.update(taskInfo.getTableName(), taskInfoValues,
					"TaskID=?", new String[] { String.valueOf(dbItem.TaskID) });
		} else {
			taskID = db.insert(taskInfo.getTableName(), null, taskInfoValues);
		}
		taskInfoCursor.close();
		return taskID;
	}

	/**
	 * 保存或者更新多条勘察任务分类项信息表（TaskCategoryInfo）数据（需要事务）
	 * 
	 * @param taskCategoryInfoList
	 *            :任务分类项数据
	 * @return
	 */
	public static ResultInfo<Long> saveManyTaskCategoryInfo(
			ArrayList<TaskCategoryInfo> taskCategoryInfoList) {
		ResultInfo<Long> resultInfo = new ResultInfo<Long>();
		SQLiteDatabase db = null;
		long id = 0;// 保存或更新是否成功的标志(大于0保存成功或者更新成功)
		try {
			db = SQLiteHelper.getWritableDB();
			db.beginTransaction();// 开启事务
			for (TaskCategoryInfo taskCategoryInfo : taskCategoryInfoList) {
				if (taskCategoryInfo.ID == 0) {// 保存
					ContentValues taskCategoryInfoValues = taskCategoryInfo
							.getContentValues();
					id = db.insert(taskCategoryInfo.getTableName(), null,
							taskCategoryInfoValues);
				} else {// 更新
					ContentValues taskCategoryInfoValues = taskCategoryInfo
							.getContentValues();
					id += db.update(
							taskCategoryInfo.getTableName(),
							taskCategoryInfoValues,
							"ID=?",
							new String[] { String.valueOf(taskCategoryInfo.ID) });
				}
			}
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			resultInfo.Data = id;// 此id为保存成功时最后插入行的ID值，或者更新影响的行数
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = (long) -1;
			resultInfo.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	// }}

	/**
	 * 保存或者更新多条勘察任务属性数据记录表（TaskDataItem）数据（需要事务）
	 * 
	 * @param taskDataItemList
	 *            :分类项下面所有子项的信息集合
	 * @return
	 */
	public static ResultInfo<Integer> saveManyTaskDataItem(
			ArrayList<TaskDataItem> taskDataItemList) {
		ResultInfo<Integer> resultInfo = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		Integer id = 0;// 保存或更新是否成功的标志(大于0保存成功或者更新成功)
		try {
			db = SQLiteHelper.getWritableDB();
			db.beginTransaction();// 开启事务
			for (TaskDataItem taskDataItem : taskDataItemList) {
				if (taskDataItem.ID <= 0) {// 保存
					Cursor cursor = taskDataItem.onSelect(null,
							" BaseCategoryID = " + taskDataItem.BaseCategoryID
									+ " and Name = '" + taskDataItem.Name
									+ "' and TaskID = " + taskDataItem.TaskID
									+ " and CategoryID = "
									+ taskDataItem.CategoryID);
					if (cursor != null && cursor.moveToNext()) {// 更新
						ContentValues taskDataItemValues = taskDataItem
								.getContentValues();
						db.update(
								taskDataItem.getTableName(),
								taskDataItemValues,
								"ID=?",
								new String[] { String.valueOf(taskDataItem.ID) });
					} else {
						ContentValues taskDataItemValues = taskDataItem
								.getContentValues();
						db.insert(taskDataItem.getTableName(), null,
								taskDataItemValues);
					}
				} else {// 更新
					ContentValues taskDataItemValues = taskDataItem
							.getContentValues();
					db.update(taskDataItem.getTableName(), taskDataItemValues,
							"ID=?",
							new String[] { String.valueOf(taskDataItem.ID) });
				}
				id += 1;
			}
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			resultInfo.Data = id;// 此id为保存成功时最后插入行的ID值，或者更新影响的行数
			resultInfo.Message = "";
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = 0;
			resultInfo.Success = false;
			resultInfo.Message = "保存失败";
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 设置任务看下分类项子项信息
	 * 
	 * @param taskResultInfo
	 *            :任务信息
	 * @param categories
	 *            :分类项信息
	 */
	private static ResultInfo<Boolean> setDataDefineFinishCount(
			ResultInfo<TaskInfo> taskResultInfo,
			ArrayList<TaskCategoryInfo> categories,
			Boolean isGetAllTaskDataItemByTaskId) {

		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;

		try {
			if (taskResultInfo.Data != null) {
				ResultInfo<DataDefine> dataDefine = DataDefineWorker
						.getCompleteDataDefine(taskResultInfo.Data.DDID);
				for (TaskCategoryInfo taskCategoryInfo : categories) {
					if (taskCategoryInfo.Items == null
							|| taskCategoryInfo.Items.size() <= 0) {
						// 取得勘察表子项列表
						ResultInfo<ArrayList<DataFieldDefine>> dataFieldDefines = DataDefineWorker
								.queryDataFieldDefineByID(
										taskResultInfo.Data.DDID,
										taskCategoryInfo.CategoryID);
						// 这里只需要根据 安卓端任务编号 或者 是服务器任务编号 查询的输入项
						ResultInfo<ArrayList<TaskDataItem>> taskDataItems = queryTaskDataItemsByID(
								taskResultInfo.Data.IsNew ? taskResultInfo.Data.ID
										: taskResultInfo.Data.TaskID,
								taskCategoryInfo.CategoryID,
								taskCategoryInfo.BaseCategoryID > 0 ? taskCategoryInfo.BaseCategoryID
										: taskCategoryInfo.ID,
								// taskResultInfo.Data.IsNew ?
								// taskCategoryInfo.ID :
								// taskCategoryInfo.BaseCategoryID,
								// taskCategoryInfo.ID,
								// taskCategoryInfo.BaseCategoryID,
								isGetAllTaskDataItemByTaskId);
						// 过滤勘察表中不存在的任务子项
						ArrayList<DataFieldDefine> fieldDefines = new ArrayList<DataFieldDefine>();
						if (dataFieldDefines.Data != null) {
							fieldDefines = dataFieldDefines.Data;
						}
						ArrayList<TaskDataItem> filterTaskDataItems = filterDataItem(
								taskDataItems.Data, fieldDefines);

						int dataDefineFinishCount = 0;
						if (ListUtil.hasData(filterTaskDataItems)) {

							for (TaskDataItem defineItem : filterTaskDataItems) {
								if (defineItem.Value != null
										&& !defineItem.Value
												.equals(EIASApplication.DefaultNullString)
										&& defineItem.Value.trim().length() > 0
										&& !defineItem.Value
												.equals(EIASApplication.DefaultDropDownListValue)) {
									// ShowInPhone 为ture时,才在分类项中添加一个数量
									Boolean isAdd = false;
									for (int i = 0; i < fieldDefines.size(); i++) {
										if (defineItem.Name.equals(fieldDefines
												.get(i).Name)) {
											if (fieldDefines.get(i).ShowInPhone) {
												isAdd = true;
											}
										}
									}
									if (isAdd) {
										dataDefineFinishCount += 1;
									}
									// dataDefineFinishCount += 1;
								}
							}
							taskCategoryInfo.Items = filterTaskDataItems;
						}
						taskCategoryInfo.DataDefineFinishCount = dataDefineFinishCount;
						taskCategoryInfo.DataDefineTotal = getDefineTotal(
								dataDefine.Data.Categories,
								taskCategoryInfo.CategoryID);

						// 如果是一次查询出任务下所有勘察信息就直接跳出循环
						if (isGetAllTaskDataItemByTaskId)
							break;
					}
				}
				result.Data = true;
				result.Success = true;
			}

		} catch (Exception e) {
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 过滤在勘察子项中已经删除了的任务子项
	 * 
	 * @param taskDataItems
	 * @param dataFieldDefines
	 */
	public static ArrayList<TaskDataItem> filterDataItem(
			ArrayList<TaskDataItem> taskDataItems,
			ArrayList<DataFieldDefine> dataFieldDefines) {
		ArrayList<TaskDataItem> result = new ArrayList<TaskDataItem>();
		if (dataFieldDefines.size() > 0) {
			for (TaskDataItem taskDataItem : taskDataItems) {
				Boolean isExit = false;
				for (DataFieldDefine fieldDefine : dataFieldDefines) {
					// 若子项名称相同则添加
					if (taskDataItem.Name.equals(fieldDefine.Name)) {
						isExit = true;
					}
				}
				if (isExit) {
					result.add(taskDataItem);
				}
			}
		}
		return result;
	}

	/**
	 * 排序任务下的勘察分类
	 * 
	 * @param taskID任务编号
	 * @param taskCategorys任务下的分类项
	 * @return
	 */
	private static ArrayList<TaskCategoryInfo> setOrderTaskCategoryInfos(
			Integer taskID, ArrayList<TaskCategoryInfo> taskCategorys) {
		ArrayList<TaskCategoryInfo> result = new ArrayList<TaskCategoryInfo>();

		TaskInfo taskInfo = new TaskInfo();
		Cursor cursor = null;
		cursor = taskInfo.onSelect(null, "TaskID = " + taskID);
		if (cursor == null || !cursor.moveToFirst()) {
			cursor = taskInfo.onSelect(null, "ID = " + taskID);
		}
		if (cursor != null && cursor.moveToFirst()) {
			taskInfo.setValueByCursor(cursor);
		}
		ResultInfo<ArrayList<DataCategoryDefine>> dataCategoryDefines = DataDefineWorker
				.queryDataCategoryDefineByDDID(taskInfo.DDID);
		if (dataCategoryDefines != null
				&& ListUtil.hasData(dataCategoryDefines.Data)) {
			for (DataCategoryDefine categoryItem : dataCategoryDefines.Data) {
				for (TaskCategoryInfo taskCategoryItem : taskCategorys) {
					if (taskCategoryItem.CategoryID == categoryItem.CategoryID) {
						result.add(taskCategoryItem);
					}
				}
			}
		}
		cursor.close();
		if (!ListUtil.hasData(result)) {
			return taskCategorys;
		}
		return result;
	}

	/**
	 * 更新一条勘察任务分类项信息表数据
	 * 
	 * @param taskCategoryInfo
	 *            :任务分类项目信息
	 * @return
	 */
	public static ResultInfo<Integer> updateOneTaskCategoryInfo(
			TaskCategoryInfo taskCategoryInfo) {
		ResultInfo<Integer> resultInfo = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		// 更新数据是否成功的标志
		int rowNum = 0;
		try {
			db = SQLiteHelper.getWritableDB();
			rowNum = db.update(taskCategoryInfo.getTableName(),
					taskCategoryInfo.getContentValues(), "ID=?",
					new String[] { String.valueOf(taskCategoryInfo.ID) });
			resultInfo.Data = rowNum;
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = -1;
			resultInfo.Success = false;
		} finally {
			// db.close();
		}
		return resultInfo;
	}

	// {{ 获取任务信息，包含分类项信息和分类项子项信息

	/**
	 * 获得完整的任务信息
	 * 
	 * @param taskId
	 *            :安卓本地自动增长编号，远程服务器任务编号
	 * @param isCreatedByUser
	 *            :是否为用户创建的任务
	 * @return
	 */
	public static ResultInfo<TaskInfo> getCompleteTaskInfoById(Integer taskId,
			Boolean isCreatedByUser) {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();
		try {
			TaskInfo taskInfo = new TaskInfo();
			Cursor cursor = null;
			if (isCreatedByUser) {
				cursor = taskInfo.onSelect(null, "ID= ?",
						new String[] { String.valueOf(taskId) });
			} else {
				cursor = taskInfo.onSelect(null, "TaskID= ?",
						new String[] { String.valueOf(taskId) });
			}
			if (cursor.moveToFirst()) {
				taskInfo.setValueByCursor(cursor);
			}
			if (taskInfo.TaskID > 0 || taskInfo.ID > 0) {
				taskInfo.Categories = getCompleteTaskCategories(
						isCreatedByUser ? taskInfo.ID : taskInfo.TaskID,
						taskInfo.IsNew);
			}
			result.Data = taskInfo;
			result.Success = true;
			cursor.close();
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 获得完整的任务信息
	 * 
	 * @param taskNum
	 *            :任务编号
	 * @return
	 */
	public static ResultInfo<TaskInfo> getCompleteTaskInfoByTaskNum(
			String taskNum) {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();
		try {
			TaskInfo taskInfo = new TaskInfo();
			Cursor cursor = taskInfo.onSelect(null, "TaskNum= ?",
					new String[] { taskNum });
			if (cursor.moveToFirst()) {
				taskInfo.setValueByCursor(cursor);
			}
			if (taskInfo.TaskID > 0 || taskInfo.ID > 0) {
				taskInfo.Categories = getCompleteTaskCategories(
						taskInfo.IsNew ? taskInfo.ID : taskInfo.TaskID,
						taskInfo.IsNew);
			}
			cursor.close();
			result.Data = taskInfo;
			result.Success = true;
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 取得任务分类项，包含任务分类项下的分类子项
	 * 
	 * @param taskID
	 *            任务ID
	 * @return
	 */
	private static ArrayList<TaskCategoryInfo> getCompleteTaskCategories(
			Integer taskID, Boolean isCreateByUser) {
		ArrayList<TaskCategoryInfo> result = new ArrayList<TaskCategoryInfo>();
		SQLiteDatabase db = SQLiteHelper.getReadableDB();
		Cursor cursor = db.query(new TaskCategoryInfo().getTableName(), null,
				"TaskID=?", new String[] { String.valueOf(taskID) }, null,
				null, "CategoryID,RemarkName");
		if (cursor != null) {
			while (cursor.moveToNext()) {
				TaskCategoryInfo taskCategoryInfo = new TaskCategoryInfo();
				taskCategoryInfo.setValueByCursor(cursor);
				// 取得分类项子项信息
				Integer taskId = taskCategoryInfo.TaskID;
				// Integer baseCategoryId = taskCategoryInfo.ID;
				Integer baseCategoryId = taskCategoryInfo.BaseCategoryID > 0 ? taskCategoryInfo.BaseCategoryID
						: taskCategoryInfo.ID;
				Integer categoryId = taskCategoryInfo.CategoryID;
				ResultInfo<ArrayList<TaskDataItem>> resultInfo = queryTaskDataItemsBybaseCategoryID(
						taskId, categoryId, baseCategoryId, false);
				if (resultInfo.Data != null
						&& ListUtil.hasData(resultInfo.Data)) {
					taskCategoryInfo.Items = resultInfo.Data;
				}
				result.add(taskCategoryInfo);
			}
		}
		cursor.close();
		return result;
	}

	// }}

	// {{ 删除分类项和子项

	/**
	 * 删除完整的任务数据（事务操作）
	 * 
	 * @param taskID
	 *            :后台管理系统中对应任务编号的任务ID
	 * @param isNew
	 *            :是否为安卓端创建的任务
	 * @return
	 */
	public static ResultInfo<Integer> deleteCategoryInfo(TaskInfo task,
			TaskCategoryInfo category) {
		ResultInfo<Integer> resultInfo = new ResultInfo<Integer>();
		SQLiteDatabase db = null;
		// 删除数据影响的行数
		Integer result = 0;
		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();
			result += db.delete(new TaskCategoryInfo().getTableName(), "ID=?",
					new String[] { String.valueOf(category.ID) });
			result += db.delete(new TaskDataItem().getTableName(),
					"BaseCategoryID=?",
					new String[] { String.valueOf(category.ID) });
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			// 结束事务，默认是回滚
			db.endTransaction();
			resultInfo.Data = result; // 此rowNum为删除影响的行数
			DataLogOperator.categoryDefineDeleted(task, category, "");
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Data = result;
			resultInfo.Success = false;
			DataLogOperator.categoryDefineDeleted(task, category,
					resultInfo.Message);
		} finally {
			// db.close();
		}
		return resultInfo;
	}

	// }}

	// {{ 提交之后返回的任务信息 需要更新到android端的数据库里面

	/**
	 * 提交之后返回的任务信息 需要更新到android端的数据库里面
	 * 
	 * @param taskDto
	 *            :完整的任务信息DTO
	 * @param isSuccess
	 *            :服务器是否有保存成功
	 */
	public static synchronized ResultInfo<Boolean> submitedUpdateTaskInfo(
			TaskInfoDTO taskDto, Boolean isSuccess) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		SQLiteDatabase db = null;

		try {
			db = SQLiteHelper.getWritableDB();
			TaskInfo taskInfo = new TaskInfo();
			Cursor cursor = taskInfo.onSelect(null, "TaskNum = ?",
					new String[] { taskDto.TaskNum });
			if (cursor != null && cursor.moveToFirst()) {
				taskInfo.setValueByCursor(cursor);
				taskInfo.TaskID = Integer.valueOf(String.valueOf(taskDto.ID));
				if (isSuccess) {
					taskInfo.UploadStatusEnum = TaskUploadStatusEnum.Submited;
					taskInfo.Status = TaskStatus.Done;
					taskInfo.DoneDate = DateTimeUtil.getCurrentTime();
				} else {
					taskInfo.UploadStatusEnum = TaskUploadStatusEnum.SubmitFailure;
				}

				taskInfo.UploadTimes = (taskInfo.UploadTimes > 0 ? taskInfo.UploadTimes
						: 0);
				taskInfo.LatestUploadDate = DateTimeUtil.getCurrentTime();
				db.beginTransaction();// 开启事务
				taskInfo.onUpdate(" ID = " + taskInfo.ID);

				taskInfo.Categories = getCompleteTaskCategories(
						taskInfo.IsNew ? taskInfo.ID : taskInfo.TaskID,
						taskInfo.IsNew);
				for (TaskCategoryInfo category : taskInfo.Categories) {
					for (TaskCategoryInfoDTO categoryDto : taskDto.Categories) {
						if (category.RemarkName.equals(categoryDto.RemarkName)) {
							for (TaskDataItem item : category.Items) {
								for (TaskDataItemDTO itemDto : categoryDto.Items) {
									if (item.Name.equals(itemDto.Name)) {
										item.BaseID = Integer
												.valueOf(itemDto.TID);
										// 新添加的这句
										item.BaseCategoryID = (int) categoryDto.BaseCategoryID;
										item.onUpdate(" ID = " + item.ID);
										break;
									}
								}
							}
							category.BaseCategoryID = (int) categoryDto.BaseCategoryID;
							category.onUpdate(" ID = " + category.ID);
							break;
						}
					}
				}
			}
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			result.Data = true;
			result.Success = false;
			cursor.close();
		} catch (Exception e) {
			result.Message = e.getMessage();
			result.Data = false;
			result.Success = false;
		} finally {
			// 关闭数据库
			// db.close();
		}
		return result;
	}

	// }}

	/**
	 * 获取当前用户最后同步完成任务时间点
	 * 
	 * @param currentUser
	 *            当前用户
	 * @return
	 */
	public static ResultInfo<String> getLastDateByReport(UserInfo currentUser) {
		ResultInfo<String> resultInfo = new ResultInfo<String>();
		TaskInfo taskInfo = null;
		SQLiteDatabase db = null;
		try {
			db = SQLiteHelper.getReadableDB();
			StringBuilder sqlBuilder = new StringBuilder(
					"from TaskInfo where Status = 2 and User='");
			sqlBuilder.append(currentUser.Name + "'");
			sqlBuilder
					.append(" order by InworkReportFinishDate desc limit 0,1");
			Cursor cursor = db.rawQuery("select * " + sqlBuilder.toString(),
					null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskInfo = new TaskInfo();
					taskInfo.setValueByCursor(cursor);
					break;
				}
				resultInfo.Data = taskInfo.InworkReportFinishDate;
				resultInfo.Success = true;
				cursor.close();
			} else {
				resultInfo.Success = false;
			}
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Success = false;
			resultInfo.Data = "";// 如果失败，data为null
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 同步完成任务时间点
	 * 
	 * @param currentUser
	 *            当前用户
	 * @return
	 */
	public static ResultInfo<Boolean> synchroReportInfo(String tasknum,
			String date) {
		ResultInfo<Boolean> resultInfo = new ResultInfo<Boolean>();
		TaskInfo taskInfo = null;
		SQLiteDatabase db = null;
		try {
			taskInfo = null;
			db = SQLiteHelper.getReadableDB();
			StringBuilder sqlBuilder = new StringBuilder(
					"from TaskInfo where TaskNum='");
			sqlBuilder.append(tasknum + "'");
			Cursor cursor = db.rawQuery("select * " + sqlBuilder.toString(),
					null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					taskInfo = new TaskInfo();
					taskInfo.setValueByCursor(cursor);
					break;
				}
				if (taskInfo != null) {
					taskInfo.InworkReportFinish = true;
					taskInfo.InworkReportFinishDate = date;
					resultInfo.Data = taskInfo.onUpdate("TaskNum = '" + tasknum
							+ "'") > 0;
				}
				cursor.close();
			}
		} catch (Exception e) {
			resultInfo.Message = e.getMessage();
			resultInfo.Success = false;
			resultInfo.Data = false;// 如果失败，data为null
		} finally {
			// 关闭数据库
			// db.close();
		}
		return resultInfo;
	}

	/**
	 * 获取本地的服务器任务
	 * 
	 * @param currentUser
	 *            当前用户
	 * @return
	 */
	public static ResultInfo<String> getServerTaskByLoc(UserInfo currentUser) {
		ResultInfo<String> result = new ResultInfo<String>();
		SQLiteDatabase db = null;
		try {
			TaskInfo taskInfo = new TaskInfo();
			db = SQLiteHelper.getWritableDB();
			String serverTask = "";
			StringBuilder queryStr = new StringBuilder(taskInfo.getTableName()
					+ " where User='");
			queryStr.append(currentUser.Name + "'");// 当前用户
			queryStr.append(" and IsNew = 0");// 服务器任务
			queryStr.append(" and Status in ('1','-99')");// 当前任务状态
			Cursor cursor = db.rawQuery(
					"select TaskNum from " + queryStr.toString(), null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					serverTask += cursor.getString(cursor
							.getColumnIndex("TaskNum")) + ",";
				}
			}
			result.Data = serverTask;
		} catch (Exception e) {
			result.Success = false;
			result.Data = "";
			result.Message = "数据库操作失败！";
		}
		return result;
	}

	/**
	 * 同步本地任务
	 * 
	 * @param taskInfo
	 *            任务信息
	 * @param currentUser
	 *            当前用户
	 * @return
	 */
	public static ResultInfo<Long> syncLocTask(TaskInfoDTO taskInfoDto,
			UserInfo currentUser) {
		ResultInfo<Long> result = new ResultInfo<Long>();
		SQLiteDatabase db = null;

		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();

			TaskInfo taskInfo = new TaskInfo(taskInfoDto, false);
			//if (taskInfoDto.Categories.size() > 0) {
				taskInfo.IsNew = false;
				taskInfo.ID = (int) taskInfo.onInsert();
				for (TaskCategoryInfoDTO categoryItem : taskInfoDto.Categories) {
					TaskCategoryInfo category = new TaskCategoryInfo(
							categoryItem, false);
					category.CategoryID = (int) categoryItem.CategoryID;
					category.BaseCategoryID = (int) categoryItem.ID;
					category.ID = (int) category.onInsert();
					for (TaskDataItemDTO item : categoryItem.Items) {
						TaskDataItem taskDataItem = new TaskDataItem(item);
						taskDataItem.TaskID = taskInfo.TaskID;
						taskDataItem.BaseCategoryID = (int) categoryItem.ID;
						taskDataItem.BaseID = Integer.parseInt(item.TID);
						taskDataItem.CategoryID = (int) categoryItem.CategoryID;
						taskDataItem.ID = (int) taskDataItem.onInsert();
					}
				}
			//}

			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			result.Data = (long) taskInfo.ID;
			DataLogOperator.taskCreated(taskInfoDto.TaskNum, "");
		} catch (Exception e) {
			result.Message = e.getMessage();
			result.Success = false;
			result.Data = (long) -1;
			DataLogOperator.taskCreated(taskInfoDto.TaskNum, result.Message);
		} finally {
			// 关闭数据库
			// db.close();
		}
		return result;
	}

	/**
	 * 修改制定任务的任务状态
	 * 
	 * @param taskInfo
	 *            任务信息
	 * @param currentUser
	 *            当前用户
	 * @param status 需要修改成的状态           
	 * @return
	 */
	public static ResultInfo<Long> upDateLocTask(String[] taskNums,
			UserInfo currentUser) {
		ResultInfo<Long> result = new ResultInfo<Long>();
		SQLiteDatabase db = null;
		result.Data=-1l;
		try {
			db = SQLiteHelper.getWritableDB();
			// 开启事务
			db.beginTransaction();

			ContentValues values = new ContentValues();
			StringBuilder taskNum = new StringBuilder();
			for (String set : taskNums) {
				taskNum.append("'");
				taskNum.append(set);
				taskNum.append("',");
			}
			String taskStr=taskNum.toString();
			taskStr=taskStr.substring(0, taskStr.length()-1);
			values.put("Status", "0");
			int cursor = db.update(new TaskInfo().getTableName(), values,
					"TaskNum in (" + taskStr + ") and Status = 1", new String[] {});
			// 设置事务操作成功的标志
			db.setTransactionSuccessful();
			db.endTransaction();
			result.Data = (long) cursor;

		} catch (Exception e) {
			result.Message = e.getMessage();
			result.Success = false;
			result.Data = (long) -1;
			DataLogOperator.taskCreated("", result.Message);
		} finally {
			// 关闭数据库
			// db.close();
		}
		return result;
	}
}
