package com.yunfang.eias.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StatFs;

import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.base.MainService;
import com.yunfang.eias.dto.AdditionalResource;
import com.yunfang.eias.dto.DialogTipsDTO;
import com.yunfang.eias.dto.TaskCategoryInfoDTO;
import com.yunfang.eias.dto.TaskDataItemDTO;
import com.yunfang.eias.dto.TaskInfoDTO;
import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.eias.enumObj.LogType;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.enumObj.SortType;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.enumObj.TaskUploadStatusEnum;
import com.yunfang.eias.http.task.BackgroundServiceTask;
import com.yunfang.eias.http.task.CheckTaskStatusTask;
import com.yunfang.eias.http.task.DeleteRelationResourceTask;
import com.yunfang.eias.http.task.GetFinishInworkReportTask;
import com.yunfang.eias.http.task.GetFinishTaskInfo;
import com.yunfang.eias.http.task.GetHomeInfoTask;
import com.yunfang.eias.http.task.GetTaskInfoTask;
import com.yunfang.eias.http.task.GetTaskListTask;
import com.yunfang.eias.http.task.SetAppointmentTask;
import com.yunfang.eias.http.task.SetTaskFeeTask;
import com.yunfang.eias.http.task.SetTaskPauseTask;
import com.yunfang.eias.http.task.SetTaskRestartTask;
import com.yunfang.eias.http.task.SynchroTaskInfoHttp;
import com.yunfang.eias.http.task.TaskRemarkTask;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.model.MediaDataInfo;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.model.UserTaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.DataLogWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.eias.ui.CreateTaskActivity;
import com.yunfang.eias.ui.TaskInfoActivity;
import com.yunfang.eias.utils.DataCheckHelper;
import com.yunfang.eias.viewmodel.TaskImportGuideViewModel;
import com.yunfang.framework.db.SQLiteHelper;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.CompressionAndDecompressionUtil;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.SpUtil;
import com.yunfang.framework.utils.StringUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 
 * 项目名称：外采系统 类名称：TaskDoingOperator 类描述：待领取任务逻辑类 创建人：lihc 创建时间：2014-4-17
 * 下午5:40:48
 * 
 * @version
 */
public class TaskOperator {

	// {{ 参数列表

	// {{ getTaskList
	/**
	 * 获取待勘察的任务信息
	 * 
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            :页行数
	 * @param queryStr
	 *            :查询值(暂时未使用)
	 * @param currentUser
	 *            :当前用户信息
	 * @return 返回的任务列表结果集
	 */
	private static ResultInfo<ArrayList<TaskInfo>> getTodoTaskInfoes(
			int pageIndex, int pageSize, String queryStr, UserInfo currentUser,
			SortType sortType) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();

		if (!EIASApplication.IsOffline && EIASApplication.IsNetworking) {
			GetTaskListTask task = new GetTaskListTask();
			result = task.request(currentUser, TaskStatus.Todo, queryStr,
					pageIndex, pageSize, sortType);
		}

		return result;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-10-28 下午3:57:36
	 * @Description: 添加任务备注
	 * @param currentuser
	 *            当前用户
	 * @param taskInfo
	 *            任务信息
	 * @param isSaveToLocal
	 *            是否同时保存备注信息到本地数据库
	 */
	public static ResultInfo<Boolean> taskRemark(UserInfo currentuser,
			TaskInfo taskInfo, Boolean isSaveToLocal)
			throws IllegalStateException, Exception {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		boolean toReques = true;
		if (isSaveToLocal) {// 只有待提交需要保存到本地
			int resultRow = taskInfo.onUpdate("TaskNum='" + taskInfo.TaskNum
					+ "'");
			if (resultRow < 0) {
				toReques = false;
			}
		}
		if (toReques) {// 发起网络请求
			if (!taskInfo.IsNew) {
				if (EIASApplication.IsNetworking) {// 只要有网络就可以提交备注
					TaskRemarkTask task = new TaskRemarkTask();
					result = task.request(currentuser, taskInfo);
				} else {
					result.Data = false;
					result.Success = false;
					result.Message = "本地修改成功";
					return result;
				}
			} else {
				result.Success = true;
				result.Data = true;
				result.Message = "本地任务备注成功";
			}

		} else {
			result.Success = false;
			result.Message = "保存备注出了点问题，稍后重试 (∩_∩)";
		}
		return result;
	}

	public static ResultInfo<Boolean> restartTask(UserInfo currentUser,
			TaskInfo taskInfo) {
		ResultInfo<Boolean> result = null;
		if (!EIASApplication.IsOffline) {
			// TODO 发起网络请求的任务
			SetTaskRestartTask task = new SetTaskRestartTask();
			result = task.request(currentUser, taskInfo);
			if (result.Data != null && result.Data) {// 启用成功 修改本地数据库状态
				taskInfo.Status = TaskStatus.Doing;
				int rowCount = taskInfo.onUpdate("TaskNum='" + taskInfo.TaskNum
						+ "'");
				if (!(rowCount > 0)) {
					result.Message = "客户端暂停失败，请刷新列表。";
				}
			} else {
				result.Success = false;
				result.Message = "无法启用任务！";
			}
		} else {
			if (result == null) {
				result = new ResultInfo<Boolean>();
			}
			result.Success = false;
			result.Message = "离线状态，无法启用任务！";
		}
		return result;
	}

	// {{ getDoingTaskInfoes
	/**
	 * 获取待提交的任务信息
	 * 
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            :页行数
	 * @param queryStr
	 *            :查询值
	 * @param currentUser
	 *            :当前用户信息
	 * @param remoteTotal
	 *            :远程数据总数
	 * @return 返回的任务列表结果集
	 */
	private static ResultInfo<ArrayList<TaskInfo>> getDoingTaskInfoes(
			Integer pageIndex, Integer pageSize, String queryStr,
			UserInfo currentUser, Integer remoteTotal, Boolean haslocal,
			Boolean onlyReportTask, SortType sortType) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();

		// if (EIASApplication.IsOffline || !EIASApplication.IsNetworking) {
		// result = TaskDataWorker.queryTaskInfoes(pageIndex, pageSize,
		// queryStr, currentUser, TaskStatus.Doing, onlyReportTask);
		// } else {
		// result = getLocData(pageIndex, pageSize, queryStr, currentUser,
		// remoteTotal, haslocal);
		// }
		result = TaskDataWorker.queryCreatedByUserTaskInfoes(currentUser,
				pageIndex, pageSize, TaskStatus.Doing, 0, queryStr, "",
				sortType);

		return result;
	}

	/**
	 * 
	 * @param pageIndex当前页码
	 * @param pageSize每页显示数量
	 * @param queryStr查询关键字
	 * @param currentUser当前用户
	 * @param remoteTotal远程总数
	 * @param haslocal是否有本地数据
	 * @return
	 */
	/*
	 * public static ResultInfo<ArrayList<TaskInfo>> getLocData(Integer
	 * pageIndex, Integer pageSize, String queryStr, UserInfo currentUser,
	 * Integer remoteTotal, Boolean haslocal) { ResultInfo<ArrayList<TaskInfo>>
	 * result; // 从远处拿数据更新到本地 // getRemoteData(pageIndex, pageSize, queryStr,
	 * currentUser); // result =
	 * TaskDataWorker.queryCreatedByUserTaskInfoes(currentUser, // pageIndex,
	 * pageSize, TaskStatus.Doing, 0, queryStr); // result.Others =
	 * result.Others; // {{ 为按预约信息排序修改的业务逻辑 if (pageIndex > 1 && (remoteTotal <=
	 * (pageIndex - 1) * pageSize)) {// 获取本地自建任务数据 // if (remoteTotal >0 &&
	 * (remoteTotal <= (pageIndex-1) * // pageSize)) {// 获取本地自建任务数据 int
	 * localPageIndex = ((pageIndex * pageSize) - remoteTotal) / pageSize;
	 * localPageIndex += ((pageIndex * pageSize) - remoteTotal) % pageSize > 0 ?
	 * 1 : 0; if (((pageIndex * pageSize) - remoteTotal) % pageSize != 0) {
	 * localPageIndex -= 1; } Integer skipCount = 0; if (remoteTotal != 0) {
	 * skipCount = remoteTotal % pageSize > 0 ? pageSize - remoteTotal %
	 * pageSize : 0; } result =
	 * TaskDataWorker.queryCreatedByUserTaskInfoes(currentUser, localPageIndex,
	 * pageSize, TaskStatus.Doing, skipCount, queryStr, "1"); result.Others =
	 * result.Others; } else { // 远程任务数据 result = getRemoteData(pageIndex,
	 * pageSize, queryStr, currentUser); if (result.Success && (remoteTotal < 1
	 * || (remoteTotal > (pageIndex - 1) * pageSize))) {//
	 * 表示最后一页，除了加载远程外，还需要补充部分本地数据 int remoteDateCount =
	 * Integer.parseInt(result.Others.toString()); if (haslocal) { int
	 * tempPageSize = 0; if (remoteTotal != 0) { tempPageSize = remoteTotal %
	 * pageSize > 0 ? (pageIndex * pageSize) - remoteTotal : 0; } else {
	 * tempPageSize = remoteDateCount % pageSize > 0 ? pageSize -
	 * remoteDateCount % pageSize : 0; } tempPageSize = tempPageSize > 0 ?
	 * tempPageSize : pageSize; ResultInfo<ArrayList<TaskInfo>> temp =
	 * TaskDataWorker.queryCreatedByUserTaskInfoes(currentUser, 1, tempPageSize,
	 * TaskStatus.Doing, 0, queryStr, "1"); if (temp.Success &&
	 * ListUtil.hasData(temp.Data)) { // 这条件该测测，当小于6条时，当有5条服务器的时，当有5条手机端时。 if
	 * ((remoteTotal != 0 && result.Data.size() != pageSize && tempPageSize !=
	 * 0) || (remoteTotal == 0 && result.Data.size() < pageSize && tempPageSize
	 * != 0)) { for (TaskInfo item : temp.Data) { result.Data.add(item); } }
	 * result.Others = temp.Others + "," + result.Others; } } else {
	 * result.Others = 0 + "," + result.Others; } } } // }} return result; }
	 */

	/**
	 * 获取远程服务端的数据
	 * 
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            :页行数
	 * @param queryStr
	 *            :查询值
	 * @param currentUser
	 *            :当前用户信息
	 * @return
	 */
	@SuppressWarnings("unused")
	private static ResultInfo<ArrayList<TaskInfo>> getRemoteData(int pageIndex,
			int pageSize, String queryStr, UserInfo currentUser) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		GetTaskListTask task = new GetTaskListTask();
		result = task.request(currentUser, TaskStatus.Doing, queryStr,
				pageIndex, pageSize, null);
		if (result.Success) {
			if (ListUtil.hasData(result.Data)) {
				// 对比是否具有相同的项，如果远程有，本地没有，则把远程的数据加载到本地
				TaskInfo remoteObj;
				for (int i = 0; i < result.Data.size(); i++) {
					remoteObj = result.Data.get(i);
					Cursor tempSelected = remoteObj.onSelect(null, "TaskNum='"
							+ remoteObj.TaskNum + "'");
					if (!(tempSelected != null && tempSelected.moveToFirst())) {// 本地数据库不存在，进行远程获取
						ResultInfo<Long> receiveInfo = receiveTask(currentUser,
								remoteObj);
						if (!(receiveInfo.Success && receiveInfo.Data > 0)) {
							result.Success = false;
							result.Message = "同步远程数据失败";
							break;
						}
					} else {
						if (tempSelected != null) {
							TaskInfo temp = new TaskInfo();
							temp.setValueByCursor(tempSelected);
							if (temp.Status != TaskStatus.Doing) {

								// 本地存在这几个属性 后台没有所有这里重新赋值
								if (temp.ContactPerson != null
										&& temp.ContactPerson.length() > 0) {
									remoteObj.ContactPerson = temp.ContactPerson;
								}
								if (temp.ContactTel != null
										&& temp.ContactTel.length() > 0) {
									remoteObj.ContactTel = temp.ContactTel;
								}
								remoteObj.BookedDate = temp.BookedDate;
								remoteObj.BookedTime = temp.BookedTime;
								remoteObj.BookedRemark = temp.BookedRemark;

								temp = remoteObj;
								temp.onUpdate("TaskID=" + remoteObj.TaskID);
							}
							result.Data.set(i, temp);
						}
					}
					tempSelected.close();
				}
			}
		}
		return result;
	}

	/**
	 * 同步远程任务信息到本地
	 * 
	 * @param pageIndex
	 *            :当前所在页码，目前没使用
	 * @param pageSize
	 *            :页行数，目前没使用
	 * @param queryStr
	 *            :查询值，目前没使用
	 * @param currentUser
	 *            :当前用户信息
	 * @return
	 */
	public static ResultInfo<Boolean> syncRemoteData(int pageIndex,
			int pageSize, String queryStr, UserInfo currentUser) {
		// 声明返回结果
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		// 获取本地的服务器任务
		ResultInfo<String> serverTask = TaskDataWorker
				.getServerTaskByLoc(currentUser);
		if (serverTask.Success) {

			// 声明获取远程任务服务
			SynchroTaskInfoHttp task = new SynchroTaskInfoHttp();
			// 获取服务器任务列表
			ResultInfo<ArrayList<TaskInfoDTO>> remoteTaskresult = task.request(
					currentUser, TaskStatus.Doing, serverTask.Data, queryStr,
					pageIndex, pageSize);
			// 获取成功后
			if (result.Success && ListUtil.hasData(remoteTaskresult.Data)) {
				// 本地没有就把任务同步到本地
				for (TaskInfoDTO taskItem : remoteTaskresult.Data) {
					TaskDataWorker.syncLocTask(taskItem, currentUser);
				}
				// 把服务器状态为待领取的任务修改本地待领取
				if (remoteTaskresult.Others != null
						&& !StringUtil.IsNullOrEmpty(remoteTaskresult.Others
								.toString())) {
					String reBackNum = remoteTaskresult.Others.toString();
					String numS[] = reBackNum.split(",");
					TaskDataWorker.upDateLocTask(numS, currentUser);
				}
			}
			// 检查服务器任务状态，同步到本地
			// 获取本地的服务器任务
			ResultInfo<String> locTask = TaskDataWorker
					.getServerTaskByLoc(currentUser);
			if (locTask.Success) {
				checkTaskStatus(locTask.Data);
			}

		}
		return result;
	}

	/***
	 * 检查本地任务在服务器的任务状态
	 * 
	 * @param taskNums
	 */
	private static void checkTaskStatus(String taskNums) {
		CheckTaskStatusTask tasks = new CheckTaskStatusTask();
		ResultInfo<HashMap<String, Integer>> result = tasks.request(taskNums);
		if (result != null && result.Success) {
			Set<String> keySet = result.Data.keySet();
			for (String key : keySet) {
				int status = result.Data.get(key);
				// 修改本地任务状态
				String taskNumber[] = key.split(",");
				if (taskNumber.length == 2) {// 检查本任务是否属于自己
					String taskUserId = taskNumber[1];
					String userId = EIASApplication.getCurrentUser().ID;
					if (!taskUserId.equals(userId)) {// 任务不属于当前用户，修改任务状态为 404
						status = 404;
					}
				}
				if (status != TaskStatus.Doing.getIndex()// 待提交任务
						&& status != TaskStatus.Pause.getIndex()) {// 暂停任务，则修改状态
					TaskStatus taskStatus = TaskStatus.getEnumByValue(status);
					if (taskStatus != null) {
						saveTaskUStatus(taskStatus, taskNumber[0]);
					}
				}

			}
		}
	}

	// }}

	/**
	 * 获取已完成的任务信息
	 * 
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            :页行数
	 * @param queryStr
	 *            :查询值
	 * @param currentUser
	 *            :当前用户信息
	 * @return 返回的任务列表结果集
	 */
	private static ResultInfo<ArrayList<TaskInfo>> getTaskDoneInfoes(
			int pageIndex, int pageSize, String queryStr, UserInfo currentUser,
			Boolean onlyReportTask, SortType sortType) {
		return TaskDataWorker.queryTaskInfoes(pageIndex, pageSize, queryStr,
				currentUser, TaskStatus.Done, onlyReportTask, sortType);
	}

	/**
	 * 获取提交中的任务信息 缓存中的数据
	 * 
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            :页行数
	 * @param queryStr
	 *            :查询值
	 * @param currentUser
	 *            :当前用户信息
	 * @return 返回的任务列表结果集
	 */
	private static ResultInfo<ArrayList<TaskInfo>> getSubmitingTask(
			int pageIndex, int pageSize, String queryStr, UserInfo currentUser) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		ArrayList<TaskInfo> taskinfos = new ArrayList<TaskInfo>();
		LinkedHashMap<String, TaskInfo> uploadTasks = MainService
				.getUploadTasks();
		for (TaskInfo taskinfo : uploadTasks.values()) {
			taskinfos.add(taskinfo);
		}
		result.Data = taskinfos;
		result.Others = taskinfos.size();
		return result;
	}

	/***
	 * @author kevin
	 * @date 2015-9-23 上午11:40:34
	 * @Description: 取当前用户本地待提交的任务
	 * @return ResultInfo<ArrayList<TaskInfo>> 返回类型
	 * @version V1.0
	 */
	public static ResultInfo<Boolean> getLocalSubmitingWaitTask(
			UserInfo currentUser) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		ResultInfo<ArrayList<TaskInfo>> taskInfos = TaskDataWorker
				.getSubmmitWaitTask(currentUser);
		// TODO 把查询到的任务加入到任务队列中，再次进行提交;
		if (taskInfos != null) {
			for (TaskInfo taskInfo : taskInfos.Data) {
				// 提交任务，同时任务加入待提交队列中
				taskSubmiting(taskInfo);
			}
		} else {
			result.Success = false;
			result.Message = "重新提交任务失败！";
		}

		return result;
	}

	/**
	 * 获取任务列表数据
	 * 
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            :页行数
	 * @param queryStr
	 *            :查询值
	 * @param status
	 *            :查询状态
	 * @param currentUser
	 *            :当前用户信息
	 * 
	 * @return 返回的任务列表结果集
	 */
	public static ResultInfo<ArrayList<TaskInfo>> getTaskInfoes(
			Integer pageIndex, Integer pageSize, String queryStr,
			TaskStatus status, UserInfo currentUser, Integer remoteTotal,
			Boolean haslocal, Boolean onlyReportTask, SortType sortType) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		try {
			switch (status) {
			case Todo:// 待领取
				result = getTodoTaskInfoes(pageIndex, pageSize, queryStr,
						currentUser, sortType);
				break;
			case Doing:// 待提交
				result = getDoingTaskInfoes(pageIndex, pageSize, queryStr,
						currentUser, remoteTotal, haslocal, onlyReportTask,
						sortType);
				break;
			case Done:// 已完成
				result = getTaskDoneInfoes(pageIndex, pageSize, queryStr,
						currentUser, onlyReportTask, sortType);
				break;
			case Submiting:// 提交中
				// result=getLocalSubmitingWaiteTask(currentUser);
				result = getSubmitingTask(pageIndex, pageSize, queryStr,
						currentUser);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = "请求超时，请重新登录！";
		}
		return result;
	}

	// }}

	/**
	 * 设置任务的收费信息，如果在线登录的话，会即时将收费信息上传到后台服务器中
	 * 
	 * @param currentUser
	 *            :当前登录用户
	 * @param taskInfo
	 *            :任务信息
	 * @return
	 */
	public static ResultInfo<Boolean> setTaskFee(Context context,
			UserInfo currentUser, TaskInfo taskInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		try {
			int execRow = taskInfo.onUpdate("TaskNum='" + taskInfo.TaskNum
					+ "'");
			if (execRow > 0) {
				if (!EIASApplication.IsOffline && EIASApplication.IsNetworking
						&& !taskInfo.IsNew) {
					SetTaskFeeTask task = new SetTaskFeeTask();
					result = task.request(currentUser, taskInfo);
				}
				result.Data = true;
				DataLogOperator.taskFeeModify(taskInfo, "");
			} else {
				result.Data = false;
				result.Message = "客户端保存失败";
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskFeeModify(taskInfo, result.Message);
		}

		return result;
	}

	/**
	 * 设置任务的预约信息
	 * 
	 * @param currentUser
	 *            :当前登录用户
	 * @param taskInfo
	 *            :任务信息
	 * @return
	 */
	public static ResultInfo<Boolean> setTaskAppointment(Context context,
			UserInfo currentUser, TaskInfo taskInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			// 查询数据库原来的任务
			TaskInfo data = new TaskInfo();
			Cursor cursor = data.onSelect(null, "TaskNum= ?",
					new String[] { taskInfo.TaskNum });
			if (cursor.moveToFirst()) {
				data.setValueByCursor(cursor);
			}
			cursor.close();
			if (taskInfo.BookedDate != null && taskInfo.BookedTime != null) {
				String formatTime = DateTimeUtil.dateFormat(taskInfo.BookedDate
						+ " " + taskInfo.BookedTime + ":00");
				String[] arrDateTime = formatTime.split(" ");
				// 修改预约信息
				data.BookedDate = arrDateTime[0];
				data.BookedTime = arrDateTime[1].replaceAll(":00", "");
				data.BookedRemark = taskInfo.BookedRemark;
				data.ContactPerson = taskInfo.ContactPerson;
				data.ContactTel = taskInfo.ContactTel;
				// 保存预约信息
				int execRow = data.onUpdate("TaskNum='" + taskInfo.TaskNum
						+ "'");
				if (execRow > 0) {

					if (!EIASApplication.IsOffline
							&& EIASApplication.IsNetworking) {
						SetAppointmentTask task = new SetAppointmentTask();
						task.request(currentUser, taskInfo.TaskNum,
								taskInfo.ContactPerson, taskInfo.ContactTel,
								formatTime, data.BookedRemark);
					}

					result.Data = true;
					result.Message = "预约信息保存成功";
				} else {
					result.Data = false;
					result.Message = "预约信息保存失败";
				}
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 设置任务无法勘察的原因，并更改状态为无法勘察
	 * 
	 * @param currentUser
	 * @param taskInfo
	 * @return
	 */
	public static ResultInfo<Boolean> setTaskRejectInfo(UserInfo currentUser,
			TaskInfo taskInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		try {
			TaskStatus status = taskInfo.Status;
			taskInfo.Status = TaskStatus.Pause;
			int execRow = taskInfo.onUpdate("TaskNum='" + taskInfo.TaskNum
					+ "'");
			if (execRow > 0) {
				if (!EIASApplication.IsOffline && !taskInfo.IsNew) {
					SetTaskPauseTask task = new SetTaskPauseTask();
					result = task.request(currentUser, taskInfo);
				} else {
					result.Success = false;
					result.Data = false;
					result.Message = "请检查网络";
				}
			} else {
				result.Success = false;
				result.Data = false;
				result.Message = "客户端保存失败";
			}
			if (!result.Success || !result.Data) {// 暂停任务失败，本地任务状态回滚
				taskInfo.Status = status;
				taskInfo.onUpdate("TaskNum='" + taskInfo.TaskNum + "'");
				DataLogOperator.taskPause(taskInfo, result.Message);
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskPause(taskInfo, e.getMessage());
		}

		return result;
	}

	/**
	 * 删除本地任务
	 * 
	 * @param currentUser
	 * @param taskInfo
	 * @return
	 */
	public static ResultInfo<Integer> deleteLoaclTask(UserInfo currentUser,
			TaskInfo taskInfo) {
		ResultInfo<Integer> result = new ResultInfo<Integer>();

		try {
			int taskId = taskInfo.IsNew ? taskInfo.ID : taskInfo.TaskID;
			result = TaskDataWorker.deleteCompleteTaskDataInfos(taskId,
					taskInfo.IsNew);
			if (result.Data > 0) {
				// 原图
				String delete1 = EIASApplication.projectRoot + taskInfo.TaskNum;
				// 缩略图
				String delete2 = EIASApplication.thumbnailRoot
						+ taskInfo.TaskNum;
				FileUtil.delDir(delete1);
				FileUtil.delDir(delete2);
				result.Success = true;
				DataLogOperator.taskDeleted(taskInfo, "");
			} else {
				result.Success = false;
				result.Message = "客户端执行失败";
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskDeleted(taskInfo, result.Message);
		}
		return result;
	}

	/**
	 * 接收任务数据
	 * 
	 * @param currentUser
	 *            :当前用户信息
	 * @param taskInfo
	 *            :后台服务器的ID值
	 * @return 返回Android中写入的任务ID值
	 */
	public static ResultInfo<Long> receiveTask(UserInfo currentUser,
			TaskInfo taskInfo) {
		ResultInfo<Long> result = new ResultInfo<Long>();
		try {
			GetTaskInfoTask task = new GetTaskInfoTask();
			ResultInfo<TaskInfo> data = task.request(currentUser, taskInfo);
			if (data.Success && data.Data != null) {
				TaskInfo fillTaskInfo = fillDefalultValue(data.Data);
				ResultInfo<Long> fillInfo = TaskDataWorker
						.fillCompleteTaskDataInfos(fillTaskInfo);
				if (fillInfo.Data > 0) {
					result.Data = fillInfo.Data;
				} else {
					result.Data = -1l;
					result.Success = true;
					result.Message = "任务数据在后台管理系统中领取成功，但任务数据写入当前设备出错";
					DataLogOperator.taskReceive(taskInfo, "");
				}
			} else {
				result.Data = -1l;
				result.Success = data.Success;
				result.Message = (data.Message.trim().length() > 0 ? data.Message
						: "任务数据获取失败");
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = "该任务正在被操作中！";// e.getMessage();
			DataLogOperator.taskReceive(taskInfo, result.Message);
		}

		return result;
	}

	/**
	 * @param taskInfo
	 * @return
	 */
	private static TaskInfo fillDefalultValue(TaskInfo taskInfo) {
		TaskInfo result = taskInfo;
		TaskDataItem taskDataItem = new TaskDataItem();
		Cursor itemCur = null;
		if (taskInfo.IsNew) {
			itemCur = taskDataItem.onSelect(null, " TaskID = " + taskInfo.ID);
		} else {
			itemCur = taskDataItem.onSelect(null, " TaskID = "
					+ taskInfo.TaskID);
		}
		// 判断当前任务是否有子项 如果没有，才把有默认值的勘察子项填充到任务子项中
		if (itemCur == null || !itemCur.moveToNext()) {
			ResultInfo<ArrayList<DataFieldDefine>> dataResult = DataDefineWorker
					.getHasDefaultItem(taskInfo.DDID);
			if (dataResult.Success && dataResult.Data != null
					&& dataResult.Data.size() > 0) {
				ArrayList<DataFieldDefine> data = dataResult.Data;
				for (TaskCategoryInfo category : taskInfo.Categories) {
					for (TaskDataItem item : category.Items) {

						for (DataFieldDefine dataFieldDefine : data) {
							if (dataFieldDefine.CategoryID == category.CategoryID
									&& dataFieldDefine.Name.equals(item.Name)) {
								item.Value = dataFieldDefine.Value;
								break;
							}
						}

					}
				}

			}
		}
		return result;
	}

	/**
	 * 生成任务编号
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String GenProjectID() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmsssss");
		String result = "U" + formatter.format(currentTime);
		return result;
	}

	/**
	 * 获取任务和任务分类项信息
	 * 
	 * @param taskId
	 *            :远程服务端ID值
	 * @param identityId
	 *            :Android端ID值
	 * @return
	 */
	public static ResultInfo<TaskInfo> getTaskInfo(Integer taskId,
			Integer identityId) {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();
		TaskInfo taskInfo = new TaskInfo();
		String whereStr = "";

		if (taskId > 0) {
			whereStr = "taskId = " + taskId;
		} else {
			whereStr = "Id = " + identityId;
		}
		Cursor cursor = null;
		try {
			cursor = taskInfo.onSelect(null, whereStr);
			if (cursor.moveToFirst()) {
				taskInfo.setValueByCursor(cursor);
				ResultInfo<ArrayList<TaskCategoryInfo>> categroiesInfo = TaskDataWorker
						.queryTaskCategories(taskInfo.IsNew ? identityId
								: taskId, taskInfo.IsNew, false);
				if (categroiesInfo.Success
						&& ListUtil.hasData(categroiesInfo.Data)) {
					taskInfo.Categories = categroiesInfo.Data;
					result.Data = taskInfo;
				} else if (categroiesInfo.Success
						&& categroiesInfo.Data == null) { // 处理当前任务勘察表被删除的情况
					result.Message = "此任务勘察表已在后台被删除";
				} else {
					result.Message = "勘察表未更新或此任务勘察表已在后台被删除 ";
				}
			}
		} catch (Exception e) {
			result.Message = "出错了";
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	// {{ pastedTaskInfo 勘察任务数据复制

	/**
	 * 勘察任务数据复制 kevin 2016-1-5 :粘贴时匹配分类项， 不只匹配分类项ID 还匹配 名字
	 * 
	 * @param copiedTaskInfo
	 *            ：之前已经复制的任务
	 * @param selectedCategoryItems
	 *            ：之前复制任务中，哪些分类数据是需要进行粘贴的
	 * @param pastedTaskInfo
	 *            ：选择粘贴数据的任务
	 * @param selectedCategoryChild
	 *            ：选择粘贴数据的分类项的子项
	 * @param operatorType
	 *            ：操作类型
	 * @return
	 */
	public static ResultInfo<Boolean> pastedTaskInfo2(TaskInfo copiedTask,
			HashMap<String, String> selectedCategoryItems,
			HashMap<String, String[]> selectedCategoryChild,
			TaskInfo pastedTask, OperatorTypeEnum operatorType) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;
		ResultInfo<ArrayList<TaskCategoryInfo>> pastedCategories = null;
		ArrayList<TaskCategoryInfo> copiedSelectCategories = null;
		try {
			ArrayList<TaskCategoryInfo> copiedTaskCategories = copiedTask.Categories;
			if (!ListUtil.hasData(copiedTaskCategories)) {
				// 获取复制的勘察输入信息
				ResultInfo<ArrayList<TaskCategoryInfo>> copiedCategories = TaskDataWorker
						.queryTaskCategories(copiedTask.IsNew ? copiedTask.ID
								: copiedTask.TaskID, copiedTask.IsNew, false);
				if (ListUtil.hasData(copiedCategories.Data)) {
					copiedTaskCategories = copiedCategories.Data;
				}
			}

			// 获取粘贴的勘察输入信息
			pastedCategories = TaskDataWorker.queryTaskCategories(
					pastedTask.IsNew ? pastedTask.ID : pastedTask.TaskID,
					pastedTask.IsNew, false);

			// 如果都有数据
			if (ListUtil.hasData(copiedTaskCategories)
					&& ListUtil.hasData(pastedCategories.Data)) {
				// 选中的勘察分类项信息
				copiedSelectCategories = new ArrayList<TaskCategoryInfo>();

				// 循环勘察分类
				for (TaskCategoryInfo category : copiedTaskCategories) {
					// 把选中的勘察分类添加到列表中， 这里修改为对比的是分类项的ID ,和分类项的名称，一样就可以粘贴
					if (selectedCategoryItems.containsKey(String
							.valueOf(category.ID))) {
						// 如果有媒体分类子项
						if (selectedCategoryChild != null
								&& selectedCategoryChild.size() > 0) {
							List<TaskDataItem> temp = new ArrayList<TaskDataItem>();
							if (selectedCategoryChild.containsKey(String
									.valueOf(category.ID))) {// 这个分类项有子项列表
								for (TaskDataItem item : category.Items) {
									String[] items = selectedCategoryChild
											.get(String.valueOf(category.ID));
									boolean hasItem = false;
									for (String itemName : items) {
										if (itemName.equals(item.Name)) {
											hasItem = true;// 有选中这个分类项 item
											break;
										}
									}
									if (!hasItem) {// 没选中这个分类子项，删除引用， 不复制
										temp.add(item); // 不需要复制的子项
									}
								}
							}
							if (ListUtil.hasData(temp)) {
								category.Items.removeAll(temp);
							}
						}
						copiedSelectCategories.add(category);
					}
				}
				// 记录复制的日志信息
				logTaskData(copiedTask, pastedTask, copiedSelectCategories,
						operatorType, "");
				// 获取勘察数据信息
				ResultInfo<DataDefine> dataDefine = DataDefineWorker
						.getCompleteDataDefine(pastedTask.DDID);
				pastedInfoes2(copiedSelectCategories, pastedCategories.Data,
						dataDefine.Data, copiedTask, pastedTask, operatorType);
				result.Data = true;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			// 记录复制的日志信息
			logTaskData(copiedTask, pastedTask, copiedSelectCategories,
					operatorType, result.Message);
		}
		return result;
	}

	/**
	 * 对任务分类项数据进行复制粘贴
	 * 
	 * @param copiedSelectCategories
	 *            ：复制的任务分类项数据
	 * @param pastedSelectCategories
	 *            ：粘贴的任务分类项数据
	 * @param dataDefine
	 *            ：被粘贴任务完整勘察表数据
	 * @param copiedTask
	 *            ：复制的任务数据
	 * @param pastedTask
	 *            ：粘贴的任务数据
	 * @param operatorType
	 *            :操作类型
	 */
	private static void pastedInfoes2(
			ArrayList<TaskCategoryInfo> copiedCategories,
			ArrayList<TaskCategoryInfo> pastedCategories,
			DataDefine dataDefine, TaskInfo copiedTask, TaskInfo pastedTask,
			OperatorTypeEnum operatorType) {
		DataCategoryDefine defineCategory = null;
		TaskCategoryInfo pastedCategory = null;
		// 获取数据库对象
		SQLiteDatabase db = SQLiteHelper.getWritableDB();
		// 开启事务
		db.beginTransaction();
		for (int i = 0; i < copiedCategories.size(); i++) {
			pastedCategory = null;
			defineCategory = null;
			// 对比被粘贴任务勘察表分类项ID ，分类项存在这个勘察表中赋值勘察表分类项
			/*
			 * for (DataCategoryDefine temp : dataDefine.Categories) { if
			 * (temp.CategoryID == copiedCategories.get(i).CategoryID) {
			 * defineCategory = temp; break; } }
			 */
			/* if (defineCategory != null) { */// 2016-1-5
												// 不再需要被复制勘察表与被粘贴勘察表属于同一张
			for (TaskCategoryInfo temp : pastedCategories) {
				// 被复制分类项ID 与 被粘贴分类项ID 是否一致, 或者分类项名称一样，则复制
				if (temp.CategoryID == copiedCategories.get(i).CategoryID
						|| temp.RemarkName
								.equals(copiedCategories.get(i).RemarkName)) {

					pastedCategory = pastedCategory == null ? temp
							: pastedCategory;
					// 被粘贴任务配置表分类项为空，复制

					for (DataCategoryDefine dcdTemp : dataDefine.Categories) {
						if (dcdTemp.CategoryID == temp.CategoryID) {
							defineCategory = dcdTemp;
							break;
						}
					}

					break;
					/*
					 * if (temp.RemarkName
					 * .equals(copiedCategories.get(i).RemarkName)) {
					 * pastedCategory = temp; break; }
					 */
				}
			}
			/* } */
			if (pastedCategory != null) {
				if (EIASApplication.isImporting) {
					mappingDataItemsByImport(copiedCategories.get(i),
							pastedCategory, defineCategory, copiedTask,
							pastedTask, operatorType);
				} else {
					mappingDataItems2(copiedCategories.get(i), pastedCategory,
							defineCategory, copiedTask, pastedTask,
							operatorType);
				}
				copiedCategories.remove(i);
				pastedCategories.remove(pastedCategory);
				i -= 1;
			}
		}
		// 设置事务操作成功的标志
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 复制数据 新建子项时， 赋值的分类项ID 为 被粘贴任务子项的分类项ID，支持不同勘察表的复制粘贴
	 * 
	 * @param copiedCategory
	 *            复制的分类项
	 * @param pastedCategory
	 *            粘贴的分类项
	 * @param defineCategory
	 *            勘察表分类项
	 * @param copiedTask
	 *            复制的任务数据
	 * @param pastedTask
	 *            粘贴的任务数据
	 * @param operatorType
	 *            操作类型
	 */
	public static void mappingDataItems2(TaskCategoryInfo copiedCategory,
			TaskCategoryInfo pastedCategory, DataCategoryDefine defineCategory,
			TaskInfo copiedTask, TaskInfo pastedTask,
			OperatorTypeEnum operatorType) {
		HashMap<String, TaskDataItem> pastedItems = new HashMap<String, TaskDataItem>();
		for (TaskDataItem item : pastedCategory.Items) {
			pastedItems.put(item.Name, item);
		}
		HashMap<String, DataFieldDefine> defineItems = new HashMap<String, DataFieldDefine>();
		for (DataFieldDefine item : defineCategory.Fields) {
			defineItems.put(item.Name, item);
		}

		boolean copyFiles = true;
		String copiedPath = "";
		String pastedPath = "";
		switch (defineCategory.ControlType) {
		case Normal:
			copyFiles = false;
			break;
		case VideoCollection:
			copiedPath = TaskItemControlOperator.mkResourceDir(
					copiedTask.TaskNum, EIASApplication.video);
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.video);
			break;
		case AudioCollection:
			copiedPath = TaskItemControlOperator.mkResourceDir(
					copiedTask.TaskNum, EIASApplication.audio);
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.audio);
			break;
		case PictureCollection:
			copiedPath = TaskItemControlOperator.mkResourceDir(
					copiedTask.TaskNum, EIASApplication.photo);
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.photo);
			break;
		case LocalPosition:
			break;
		default:
			break;
		}
		// 如果是任务匹配就不需要复制文件
		if (operatorType == OperatorTypeEnum.TaskDataMatching) {
			copyFiles = false;
		}
		TaskDataItem pastedItem = null;
		DataFieldDefine defineItem = null;
		String newValue = "";

		try {
			boolean isContiniu = false;
			// 需要复制的子项
			for (TaskDataItem copiedItem : copiedCategory.Items) {
				defineItem = null;
				pastedItem = null;
				// 勘察表子项
				if (defineItems.containsKey(copiedItem.Name)) {
					defineItem = defineItems.get(copiedItem.Name);
					// 粘贴子项
					if (pastedItems.containsKey(copiedItem.Name)) {
						pastedItem = pastedItems.get(copiedItem.Name);
						// 复制值，如果已经有值或者不为默认值则不进行值替换
						if (pastedItem.Value.equals(defineItem.Value)
								|| pastedItem.Value.trim().length() == 0) {
							// 若果是可重复项
							if (defineCategory.Repeat) {
								isContiniu = true;
							}
							// TODO 这里新增加逻辑 时间类型， 地图类型，坐标类型 用名类型，联系方式类型， 不粘贴
							pastedItem.Value = itemTypeSwitch(defineItem,
									copiedItem); // copiedItem.Value;
							pastedItem.onUpdate("ID=" + pastedItem.ID);
							newValue = copiedItem.Value;
						}
					} else {// 没有值时，进行新建
						pastedItem = new TaskDataItem();
						pastedItem.TaskID = pastedTask.IsNew ? pastedTask.ID
								: pastedTask.TaskID;
						;
						// pastedItem.TaskID = pastedTask.TaskID;
						// 需要不同勘察表的粘贴， 分类项ID 取被粘贴任务的分类ID
						/* pastedItem.CategoryID =copiedItem.CategoryID; */
						pastedItem.CategoryID = pastedCategory.CategoryID;
						pastedItem.IOrder = copiedItem.IOrder;
						pastedItem.Name = copiedItem.Name;
						pastedItem.Value = itemTypeSwitch(defineItem,
								copiedItem); // copiedItem.Value;
						pastedItem.BaseCategoryID = pastedCategory.ID > 0 ? pastedCategory.BaseCategoryID
								: pastedCategory.ID;// ?pastedCategory.BaseCategoryID:
						pastedItem.BaseID = -1;
						pastedItem.onInsert();
						newValue = copiedItem.Value;
					}
					if (copyFiles && newValue.trim().length() > 0) {
						String[] moveFiles = newValue.trim().split(
								MediaDataInfo.Semicolon);
						if (moveFiles != null && moveFiles.length > 0) {
							for (String moveFile : moveFiles) {
								FileUtil.copy(copiedPath + File.separator
										+ moveFile, pastedPath + File.separator
										+ moveFile);
							}
						}
					}
					newValue = "";
				}
				if (isContiniu) {
					break;
				}
			}
			logCategory(copiedCategory, pastedCategory, copiedTask, pastedTask,
					operatorType, "");
		} catch (Exception e) {
			logCategory(copiedCategory, pastedCategory, copiedTask, pastedTask,
					operatorType, e.getMessage());
		}
	}

	/**
	 * 勘察任务数据复制
	 * 
	 * @param copiedTaskInfo
	 *            ：之前已经复制的任务
	 * @param selectedCategoryItems
	 *            ：之前复制任务中，哪些分类数据是需要进行粘贴的
	 * @param pastedTaskInfo
	 *            ：选择粘贴数据的任务
	 * @param selectedCategoryChild
	 *            ：选择粘贴数据的分类项的子项
	 * @param operatorType
	 *            ：操作类型
	 * @return
	 */
	public static ResultInfo<Boolean> pastedTaskInfo(TaskInfo copiedTask,
			HashMap<String, String> selectedCategoryItems,
			HashMap<String, String[]> selectedCategoryChild,
			TaskInfo pastedTask, OperatorTypeEnum operatorType) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;
		ResultInfo<ArrayList<TaskCategoryInfo>> pastedCategories = null;
		ArrayList<TaskCategoryInfo> copiedSelectCategories = null;
		try {
			ArrayList<TaskCategoryInfo> copiedTaskCategories = copiedTask.Categories;
			if (!ListUtil.hasData(copiedTaskCategories)) {
				// 获取复制的勘察输入信息
				ResultInfo<ArrayList<TaskCategoryInfo>> copiedCategories = TaskDataWorker
						.queryTaskCategories(copiedTask.IsNew ? copiedTask.ID
								: copiedTask.TaskID, copiedTask.IsNew, false);
				if (ListUtil.hasData(copiedCategories.Data)) {
					copiedTaskCategories = copiedCategories.Data;
				}
			}

			// 获取粘贴的勘察输入信息
			pastedCategories = TaskDataWorker.queryTaskCategories(
					pastedTask.IsNew ? pastedTask.ID : pastedTask.TaskID,
					pastedTask.IsNew, false);

			// 如果都有数据
			if (ListUtil.hasData(copiedTaskCategories)
					&& ListUtil.hasData(pastedCategories.Data)) {
				// 选中的勘察分类项信息
				copiedSelectCategories = new ArrayList<TaskCategoryInfo>();

				// 循环勘察分类
				for (TaskCategoryInfo category : copiedTaskCategories) {
					// 把选中的勘察分类添加到列表中
					if (selectedCategoryItems.containsKey(String
							.valueOf(category.ID))) {
						// 如果有媒体分类子项
						if (selectedCategoryChild != null
								&& selectedCategoryChild.size() > 0) {
							List<TaskDataItem> temp = new ArrayList<TaskDataItem>();
							if (selectedCategoryChild.containsKey(String
									.valueOf(category.ID))) {// 这个分类项有子项列表
								for (TaskDataItem item : category.Items) {
									String[] items = selectedCategoryChild
											.get(String.valueOf(category.ID));
									boolean hasItem = false;
									for (String itemName : items) {
										if (itemName.equals(item.Name)) {
											hasItem = true;// 有选中这个分类项 item
											break;
										}
									}
									if (!hasItem) {// 没选中这个分类子项，删除引用， 不复制
										temp.add(item); // 不需要复制的子项
									}
								}
							}
							if (ListUtil.hasData(temp)) {
								category.Items.removeAll(temp);
							}
						}
						copiedSelectCategories.add(category);
					}
				}
				// 记录复制的日志信息
				logTaskData(copiedTask, pastedTask, copiedSelectCategories,
						operatorType, "");
				// 获取勘察数据信息
				ResultInfo<DataDefine> dataDefine = DataDefineWorker
						.getCompleteDataDefine(pastedTask.DDID);
				pastedInfoes(copiedSelectCategories, pastedCategories.Data,
						dataDefine.Data, copiedTask, pastedTask, operatorType);
				result.Data = true;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			// 记录复制的日志信息
			logTaskData(copiedTask, pastedTask, copiedSelectCategories,
					operatorType, result.Message);
		}
		return result;
	}

	/**
	 * 对任务分类项数据进行复制粘贴
	 * 
	 * @param copiedSelectCategories
	 *            ：复制的任务分类项数据
	 * @param pastedSelectCategories
	 *            ：粘贴的任务分类项数据
	 * @param dataDefine
	 *            ：完整勘察表数据
	 * @param copiedTask
	 *            ：复制的任务数据
	 * @param pastedTask
	 *            ：粘贴的任务数据
	 * @param operatorType
	 *            :操作类型
	 */
	private static void pastedInfoes(
			ArrayList<TaskCategoryInfo> copiedCategories,
			ArrayList<TaskCategoryInfo> pastedCategories,
			DataDefine dataDefine, TaskInfo copiedTask, TaskInfo pastedTask,
			OperatorTypeEnum operatorType) {
		DataCategoryDefine defineCategory = null;
		TaskCategoryInfo pastedCategory = null;
		// 获取数据库对象
		SQLiteDatabase db = SQLiteHelper.getWritableDB();
		// 开启事务
		db.beginTransaction();
		for (int i = 0; i < copiedCategories.size(); i++) {
			pastedCategory = null;
			defineCategory = null;
			// 对比勘察表分类项ID ，分类项存在这个勘察表中赋值勘察表分类项
			for (DataCategoryDefine temp : dataDefine.Categories) {
				if (temp.CategoryID == copiedCategories.get(i).CategoryID) {
					defineCategory = temp;
					break;
				}
			}
			if (defineCategory != null) {
				for (TaskCategoryInfo temp : pastedCategories) {
					// 被复制分类项ID 与 被粘贴分类项ID 是否一致
					if (temp.CategoryID == copiedCategories.get(i).CategoryID) {
						pastedCategory = (pastedCategory == null ? temp
								: pastedCategory);
						if (temp.RemarkName
								.equals(copiedCategories.get(i).RemarkName)) {
							pastedCategory = temp;
							break;
						}
					}
				}
			}
			if (pastedCategory != null) {
				if (EIASApplication.isImporting) {
					mappingDataItemsByImport(copiedCategories.get(i),
							pastedCategory, defineCategory, copiedTask,
							pastedTask, operatorType);
				} else {
					mappingDataItems(copiedCategories.get(i), pastedCategory,
							defineCategory, copiedTask, pastedTask,
							operatorType);
				}
				copiedCategories.remove(i);
				pastedCategories.remove(pastedCategory);
				i -= 1;
			}
		}
		// 设置事务操作成功的标志
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 复制分类项数据
	 * 
	 * @param copiedCategory
	 *            复制的分类项
	 * @param pastedCategory
	 *            粘贴的分类项
	 * @param defineCategory
	 *            勘察表分类项
	 * @param copiedTask
	 *            复制的任务数据
	 * @param pastedTask
	 *            粘贴的任务数据
	 * @param operatorType
	 *            操作类型
	 */
	public static void mappingDataItems(TaskCategoryInfo copiedCategory,
			TaskCategoryInfo pastedCategory, DataCategoryDefine defineCategory,
			TaskInfo copiedTask, TaskInfo pastedTask,
			OperatorTypeEnum operatorType) {
		HashMap<String, TaskDataItem> pastedItems = new HashMap<String, TaskDataItem>();
		for (TaskDataItem item : pastedCategory.Items) {
			pastedItems.put(item.Name, item);
		}
		HashMap<String, DataFieldDefine> defineItems = new HashMap<String, DataFieldDefine>();
		for (DataFieldDefine item : defineCategory.Fields) {
			defineItems.put(item.Name, item);
		}

		boolean copyFiles = true;
		String copiedPath = "";
		String pastedPath = "";
		switch (defineCategory.ControlType) {
		case Normal:
			copyFiles = false;
			break;
		case VideoCollection:
			copiedPath = TaskItemControlOperator.mkResourceDir(
					copiedTask.TaskNum, EIASApplication.video);
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.video);
			break;
		case AudioCollection:
			copiedPath = TaskItemControlOperator.mkResourceDir(
					copiedTask.TaskNum, EIASApplication.audio);
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.audio);
			break;
		case PictureCollection:
			copiedPath = TaskItemControlOperator.mkResourceDir(
					copiedTask.TaskNum, EIASApplication.photo);
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.photo);
			break;
		case LocalPosition:
			break;
		default:
			break;
		}
		// 如果是任务匹配就不需要复制文件
		if (operatorType == OperatorTypeEnum.TaskDataMatching) {
			copyFiles = false;
		}
		TaskDataItem pastedItem = null;
		DataFieldDefine defineItem = null;
		String newValue = "";

		try {
			boolean isContiniu = false;
			// 需要复制的子项
			for (TaskDataItem copiedItem : copiedCategory.Items) {
				defineItem = null;
				pastedItem = null;
				// 勘察表子项
				if (defineItems.containsKey(copiedItem.Name)) {
					defineItem = defineItems.get(copiedItem.Name);
					// 粘贴子项
					if (pastedItems.containsKey(copiedItem.Name)) {
						pastedItem = pastedItems.get(copiedItem.Name);
						// 复制值，如果已经有值且不为默认值则进行值替换
						if (pastedItem.Value.equals(defineItem.Value)
								|| pastedItem.Value.trim().length() == 0) {
							// 若果是可重复项
							if (defineCategory.Repeat) {
								isContiniu = true;
							}
							pastedItem.Value = itemTypeSwitch(defineItem,
									copiedItem); // copiedItem.Value;
							pastedItem.onUpdate("ID=" + pastedItem.ID);
							newValue = copiedItem.Value;
						}
					} else {// 没有值时，进行新建
						pastedItem = new TaskDataItem();
						pastedItem.TaskID = pastedTask.IsNew ? pastedTask.ID
								: pastedTask.TaskID;
						;
						// pastedItem.TaskID = pastedTask.TaskID;
						pastedItem.CategoryID = copiedItem.CategoryID;
						pastedItem.IOrder = copiedItem.IOrder;
						pastedItem.Name = copiedItem.Name;

						pastedItem.Value = itemTypeSwitch(defineItem,
								copiedItem);// copiedItem.Value;
						pastedItem.BaseCategoryID = pastedCategory.ID > 0 ? pastedCategory.BaseCategoryID
								: pastedCategory.ID;// ?pastedCategory.BaseCategoryID:
						pastedItem.BaseID = -1;
						pastedItem.onInsert();
						newValue = copiedItem.Value;
					}
					if (copyFiles && newValue.trim().length() > 0) {
						String[] moveFiles = newValue.trim().split(
								MediaDataInfo.Semicolon);
						if (moveFiles != null && moveFiles.length > 0) {
							for (String moveFile : moveFiles) {
								FileUtil.copy(copiedPath + File.separator
										+ moveFile, pastedPath + File.separator
										+ moveFile);
							}
						}
					}
					newValue = "";
				}
				if (isContiniu) {
					break;
				}
			}
			logCategory(copiedCategory, pastedCategory, copiedTask, pastedTask,
					operatorType, "");
		} catch (Exception e) {
			logCategory(copiedCategory, pastedCategory, copiedTask, pastedTask,
					operatorType, e.getMessage());
		}
	}

	/**
	 * 复制数据 导入任务数据时用
	 * 
	 * @param copiedCategory
	 *            ：复制的分类项
	 * @param pastedCategory
	 *            ：粘贴的分类项
	 * @param defineCategory
	 *            ：勘察表分类项
	 * @param copiedTask
	 *            ：复制的任务数据
	 * @param pastedTask
	 *            ：粘贴的任务数据
	 * @param operatorType
	 *            :操作类型
	 */
	public static void mappingDataItemsByImport(
			TaskCategoryInfo copiedCategory, TaskCategoryInfo pastedCategory,
			DataCategoryDefine defineCategory, TaskInfo copiedTask,
			TaskInfo pastedTask, OperatorTypeEnum operatorType) {
		HashMap<String, TaskDataItem> pastedItems = new HashMap<String, TaskDataItem>();
		for (TaskDataItem item : pastedCategory.Items) {
			pastedItems.put(item.Name, item);
		}
		HashMap<String, DataFieldDefine> defineItems = new HashMap<String, DataFieldDefine>();
		for (DataFieldDefine item : defineCategory.Fields) {
			defineItems.put(item.Name, item);
		}
		String copiedPath = "";
		String pastedPath = "";
		switch (defineCategory.ControlType) {
		case VideoCollection:
			copiedPath = EIASApplication.importRoot + copiedTask.TaskNum + "/"
					+ EIASApplication.video;
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.video);
			break;
		case AudioCollection:
			copiedPath = EIASApplication.importRoot + copiedTask.TaskNum + "/"
					+ EIASApplication.audio;
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.audio);
			break;
		case PictureCollection:
			copiedPath = EIASApplication.importRoot + copiedTask.TaskNum + "/"
					+ EIASApplication.photo;
			pastedPath = TaskItemControlOperator.mkResourceDir(
					pastedTask.TaskNum, EIASApplication.photo);
			break;
		case LocalPosition:
			break;
		default:
			break;
		}
		TaskDataItem pastedItem = null;

		try {
			switch (defineCategory.ControlType) {
			case Normal:
				// 需要复制的子项
				for (TaskDataItem copiedItem : copiedCategory.Items) {
					pastedItem = null;
					// 勘察表子项
					if (defineItems.containsKey(copiedItem.Name)) {
						// 粘贴子项
						if (pastedItems.containsKey(copiedItem.Name)) {
							pastedItem = pastedItems.get(copiedItem.Name);
							pastedItem.Value = copiedItem.Value;
							pastedItem.onUpdate("ID=" + pastedItem.ID);
						} else {
							pastedItem = new TaskDataItem();
							pastedItem.TaskID = pastedTask.IsNew ? pastedTask.ID
									: pastedTask.TaskID;
							pastedItem.CategoryID = copiedItem.CategoryID;
							pastedItem.IOrder = copiedItem.IOrder;
							pastedItem.Name = copiedItem.Name;
							pastedItem.Value = copiedItem.Value;
							pastedItem.BaseCategoryID = pastedCategory.ID > 0 ? pastedCategory.BaseCategoryID
									: pastedCategory.ID;
							pastedItem.BaseID = -1;
							pastedItem.onInsert();
						}
					}
				}
				break;
			case VideoCollection:
			case AudioCollection:
			case PictureCollection:
				// 需要复制的子项
				for (TaskDataItem copiedItem : copiedCategory.Items) {
					pastedItem = null;
					// 勘察表子项
					if (defineItems.containsKey(copiedItem.Name)) {
						// 粘贴子项
						if (pastedItems.containsKey(copiedItem.Name)) {
							pastedItem = pastedItems.get(copiedItem.Name);
							if (!copiedItem.Value.isEmpty()) {
								String[] moveFiles = copiedItem.Value.trim()
										.split(MediaDataInfo.Semicolon);
								for (String fileItem : moveFiles) {
									// 是否有相同文件
									if (pastedItem.Value.contains(fileItem)) {
										File copy = new File(copiedPath + "/"
												+ fileItem);
										File paste = new File(pastedPath + "/"
												+ fileItem);
										if (copy.exists() && paste.exists()) {
											if (!DataCheckHelper.fileEquals(
													copy, paste)) {
												if (!pastedItem.Value
														.contains(fileItem)) {
													pastedItem.Value += fileItem
															+ MediaDataInfo.Semicolon;
												}
											} else {
												copiedItem.Value = copiedItem.Value
														.replaceAll(
																fileItem
																		+ MediaDataInfo.Semicolon,
																"");
											}
										}
									} else {
										pastedItem.Value += fileItem
												+ MediaDataInfo.Semicolon;
									}
								}
								pastedItem.onUpdate("ID=" + pastedItem.ID);
								moveFiles = copiedItem.Value
										.split(MediaDataInfo.Semicolon);
								if (moveFiles != null && moveFiles.length > 0) {
									for (String moveFile : moveFiles) {
										if (moveFile.length() > 0) {
											FileUtil.copy(
													copiedPath + File.separator
															+ moveFile,
													pastedPath + File.separator
															+ moveFile);
										}
									}
								}
							}
						} else {
							pastedItem = new TaskDataItem();
							pastedItem.TaskID = pastedTask.IsNew ? pastedTask.ID
									: pastedTask.TaskID;
							pastedItem.CategoryID = copiedItem.CategoryID;
							pastedItem.IOrder = copiedItem.IOrder;
							pastedItem.Name = copiedItem.Name;
							pastedItem.Value = copiedItem.Value;
							pastedItem.BaseCategoryID = pastedCategory.ID > 0 ? pastedCategory.BaseCategoryID
									: pastedCategory.ID;
							pastedItem.BaseID = -1;
							pastedItem.onInsert();
						}
					}
				}
				break;
			case LocalPosition:
				break;
			default:
				break;
			}
			logCategory(copiedCategory, pastedCategory, copiedTask, pastedTask,
					operatorType, "");
		} catch (Exception e) {
			logCategory(copiedCategory, pastedCategory, copiedTask, pastedTask,
					operatorType, e.getMessage());
		}
	}

	/**
	 * 
	 * @param copiedTask
	 *            :复制的任务
	 * @param pastedTask
	 *            :粘贴的任务
	 * @param copiedSelectCategories
	 *            :选择的复制分类项
	 * @param operatorType
	 *            :操作类型
	 * @param errorMsg
	 */
	private static void logTaskData(TaskInfo copiedTask, TaskInfo pastedTask,
			ArrayList<TaskCategoryInfo> copiedSelectCategories,
			OperatorTypeEnum operatorType, String errorMsg) {
		switch (operatorType) {
		case TaskDataCopyToNew:
			DataLogOperator.taskDataCopyToNew(copiedTask, pastedTask,
					copiedSelectCategories, errorMsg);
			break;
		case TaskDataCopy:
			DataLogOperator.taskDataCopy(copiedTask, pastedTask,
					copiedSelectCategories, errorMsg);
			break;
		default:
			break;
		}
	}

	/**
	 * @param copiedCategory
	 *            :复制的分类项信息
	 * @param pastedCategory
	 *            :粘贴的分类项信息
	 * @param copiedTask
	 *            :复制的任务信息
	 * @param pastedTask
	 *            :粘贴的任务信息
	 * @param operatorType
	 *            :操作类型
	 * @param errorMsg
	 *            :错误信息
	 */
	private static void logCategory(TaskCategoryInfo copiedCategory,
			TaskCategoryInfo pastedCategory, TaskInfo copiedTask,
			TaskInfo pastedTask, OperatorTypeEnum operatorType, String errorMsg) {
		if (pastedTask == copiedTask) {
			switch (operatorType) {
			case TaskDataCopyToNew:
				DataLogOperator.categoryDefineDataCopyToNew(copiedTask,
						copiedCategory, pastedCategory, "");
				break;
			case TaskDataCopy:
				DataLogOperator.categoryDefineDataCopy(copiedTask,
						copiedCategory, pastedCategory, "");
				break;
			default:
				break;
			}
		}
	}

	// }}

	/**
	 * 粘贴并创建新任务的对话框
	 */
	public static ResultInfo<Boolean> showCreateNewTaskActivity(
			Context context, TaskInfo copiedTaskInfo,
			HashMap<String, String> selectedCategoryItems) {

		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;

		try {

			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> values = new ArrayList<String>();

			Iterator<Entry<String, String>> iterator = selectedCategoryItems
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> col = iterator.next();
				keys.add(col.getKey());// 任务分类项编号
				values.add(col.getValue());// 任务分类项名称
			}

			Intent intent = new Intent(context, CreateTaskActivity.class);
			intent.putExtra("name", getDataDefineName(copiedTaskInfo.DDID));
			intent.putExtra("address", copiedTaskInfo.TargetAddress);
			intent.putExtra("is_copied_to_new_task", true);
			intent.putExtra("is_created_by_user", copiedTaskInfo.IsNew);
			intent.putExtra("copied_task_id",
					copiedTaskInfo.IsNew ? copiedTaskInfo.ID
							: copiedTaskInfo.TaskID);
			intent.putStringArrayListExtra("keys", keys);
			intent.putStringArrayListExtra("values", values);

			context.startActivity(intent);
		} catch (Exception e) {
			result.Success = false;
			result.Message = "请重试";
		}

		return result;
	}

	/**
	 * 根据完整勘察表编号获取勘察名称
	 * 
	 * @param ddid
	 *            :勘察表编号
	 * @return
	 */
	public static String getDataDefineName(int ddid) {
		String result = "";
		ResultInfo<DataDefine> dataDefine = DataDefineWorker
				.queryDataDefineByDDID(ddid);
		if (dataDefine != null && dataDefine.Data != null) {
			result = dataDefine.Data.Name;
		}
		return result;
	}

	/**
	 * 获得所有分类项
	 * 
	 * @param taskInfo
	 * @return
	 */
	public static ResultInfo<ArrayList<DataCategoryDefine>> getAllCategories(
			TaskInfo taskInfo) {
		ResultInfo<ArrayList<DataCategoryDefine>> result = new ResultInfo<ArrayList<DataCategoryDefine>>();
		ArrayList<DataCategoryDefine> arrayList = new ArrayList<DataCategoryDefine>();
		try {
			// 自建任务为Android端ID值，非自建任务为远程服务端ID值
			Integer taskID = taskInfo.IsNew ? taskInfo.ID : taskInfo.TaskID;
			if (taskID > 0) {
				// 取得勘察配置表配置分类项信息/
				ResultInfo<ArrayList<DataCategoryDefine>> dataCategoryResult = DataDefineWorker
						.queryDataCategoryDefineByDDID(taskInfo.DDID);
				if (ListUtil.hasData(dataCategoryResult.Data)) {
					// 循环勘察分类项
					for (DataCategoryDefine dataCategory : dataCategoryResult.Data) {
						arrayList.add(dataCategory);
					}
				}
			}
			result.Data = arrayList;
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}
		return result;
	}

	// {{ getCanBeAddCategories
	/**
	 * 获取当前指定任务可以添加或可以删除的分类项信息
	 * 
	 * @param taskInfo
	 *            勘察任务信息表对应的实体类
	 * @param isAdd
	 *            （true表示取得可以添加的分类项信息，false表示取得可以删除的分类项信息）
	 * @return
	 */
	public static ResultInfo<ArrayList<DataCategoryDefine>> getCanBeAddOrDeleteCategories(
			TaskInfo taskInfo, Boolean isAdd) {
		ResultInfo<ArrayList<DataCategoryDefine>> result = new ResultInfo<ArrayList<DataCategoryDefine>>();

		ArrayList<DataCategoryDefine> arrayList = new ArrayList<DataCategoryDefine>();
		try {
			// 自建任务为Android端ID值，非自建任务为远程服务端ID值
			Integer taskID = taskInfo.IsNew ? taskInfo.ID : taskInfo.TaskID;
			if (taskID > 0) {
				// 取得勘察配置表配置分类项信息/
				ResultInfo<ArrayList<DataCategoryDefine>> dataCategoryResult = DataDefineWorker
						.queryDataCategoryDefineByDDID(taskInfo.DDID);
				if (ListUtil.hasData(dataCategoryResult.Data)) {
					// 过滤可重复和最小重复数大于0的勘察配置表配置分类项信息
					ArrayList<DataCategoryDefine> dataCategoryResultList = getDataCategoryDefine(dataCategoryResult.Data);
					// 查询某个任务下的分类项信息 每次查询时，需要与勘察表进行对比，如果不相符，需要进行调整
					ResultInfo<ArrayList<TaskCategoryInfo>> taskCategoryResult = TaskDataWorker
							.queryTaskCategories(taskID, taskInfo.IsNew, false);
					// 循环勘察分类项
					for (DataCategoryDefine dataCategory : dataCategoryResultList) {
						// 定义重复项数量
						int categoryCount = 0;
						// 循环任务分类项
						for (TaskCategoryInfo taskCategory : taskCategoryResult.Data) {
							if (taskCategory.CategoryID == dataCategory.CategoryID) {
								categoryCount++;
							}
						}
						if (isAdd) {
							// 可以新建的条件
							// 若重复数量 小于 最小重复数量
							// 若重复数量 大于等于 最小重复数量，且（重复数量 小于 最大重复数量 或者 最大重复数量 等于
							// 0）
							// 若最小重复数量 等于 0且（重复数量 小于 最大重复数量 或者 最大重复数量 等于 0）
							if (categoryCount < dataCategory.RepeatLimit
									|| (categoryCount >= dataCategory.RepeatLimit && (categoryCount < dataCategory.RepeatMax))
									|| (categoryCount == 0
											&& !dataCategory.Repeat && !dataCategory.DefaultShow)
									|| (dataCategory.RepeatLimit == 0
											&& (categoryCount < dataCategory.RepeatMax) || (dataCategory.Repeat
											&& dataCategory.RepeatLimit == 0 && dataCategory.RepeatMax == 0))) {
								arrayList.add(dataCategory);
							}
						} else {
							// 可以删除的条件
							// 若重复数量 大于 最小重复数量
							// 若最小重复数量等于0 且 最大重复数量 等于 0
							if ( // 不默认显示且重复可以全部删除
							(categoryCount < dataCategory.RepeatMax
									&& !dataCategory.DefaultShow && dataCategory.Repeat)
									||
									// 默认显示且重复至少留一个
									(categoryCount > 1
											&& categoryCount > dataCategory.RepeatLimit
											&& dataCategory.DefaultShow && dataCategory.Repeat)
									||
									// 不默认显示且不重复可以删除
									(categoryCount > 0
											&& !dataCategory.DefaultShow && !dataCategory.Repeat)) {
								arrayList.add(dataCategory);
							}
						}
					}
				}
				dataCategoryResult.Success = true;
				result.Data = arrayList;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 过滤可重复和最小重复数大于0的勘察配置表配置分类项信息
	 * 
	 * @param ddid
	 *            :勘察表编号
	 * @return
	 */
	private static ArrayList<DataCategoryDefine> getDataCategoryDefine(
			ArrayList<DataCategoryDefine> dataCategoryDefines) {
		ArrayList<DataCategoryDefine> rusult = new ArrayList<DataCategoryDefine>();

		for (DataCategoryDefine dataCategoryDefine : dataCategoryDefines) {
			// 若最小重复数量大于0以及可以重复
			if (dataCategoryDefine.Repeat || !dataCategoryDefine.DefaultShow) {
				rusult.add(dataCategoryDefine);
			}
		}

		return rusult;
	}

	// }}

	// {{ 保存媒体信息 saveMediaInfo

	/**
	 * 保存媒体信息
	 * 
	 * @param taskInfoActivity当前操作界面
	 * @param taskInfo任务信息
	 * @param taskCategoryInfo任务分类
	 * @param dataItemName当前操作图片描述类型
	 * @param dataItemValue当前操作图片值
	 * @param beforeType之前操作图片描述类型
	 * @param beforeValue之前操作图片值
	 * @param delete是否为删除操作
	 * @return
	 */
	public static ResultInfo<Boolean> saveMediaInfo(
			TaskInfoActivity taskInfoActivity, TaskInfo taskInfo,
			TaskCategoryInfo taskCategoryInfo, String dataItemName,
			String dataItemValue, String beforeType, String beforeValue,
			boolean delete) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		if (dataItemName == null || dataItemName.length() <= 0) {
			result.Message = "选择项不能为空";
			result.Data = true;
			result.Success = true;
			return result;
		} else if (dataItemName.equals(beforeType)) {
			result.Message = "操作成功";
			result.Data = true;
			result.Success = true;
			return result;
		}
		Integer tempTaskId = taskInfo.IsNew ? taskInfo.ID : taskInfo.TaskID;
		Integer baseCategoryId = taskCategoryInfo.BaseCategoryID > 0 ? taskCategoryInfo.BaseCategoryID
				: taskCategoryInfo.ID;
		try {
			if (taskCategoryInfo.ID > 0) {
				TaskDataItem item = new TaskDataItem();
				if (delete) {
					Cursor existCur = null;
					if (dataItemName.contains(".jpg")) {
						existCur = item
								.onSelect(
										null,
										"TaskId=? AND Name=? AND CategoryID=? AND BaseCategoryID=?",
										new String[] {
												String.valueOf(tempTaskId),
												dataItemName, "0",
												baseCategoryId + "" });
					} else {
						existCur = item
								.onSelect(
										null,
										"TaskId=? AND Name=? AND CategoryID=? AND BaseCategoryID=?",
										new String[] {
												String.valueOf(tempTaskId),
												dataItemName,
												taskCategoryInfo.CategoryID
														+ "",
												baseCategoryId + "" });
					}
					if (existCur != null && existCur.moveToFirst()) {
						item.setValueByCursor(existCur);
						if (item.CategoryID <= 0) {
							if (item.onDelete(item.ID + "") > 0) {
								result.Data = true;
								result.Success = true;
								result.Message = "操作成功";
							}
						} else {
							item.Value = dataItemValue;
							if (item.onUpdate("ID = " + item.ID) > 0) {
								result.Data = true;
								result.Success = true;
								result.Message = "操作成功";
								changeCache(taskInfoActivity, dataItemValue,
										item);
							}
						}
					}
				} else {
					String sql = "TaskID=? and CategoryID=? and BaseCategoryID=? and Name=?";
					Cursor cur = item
							.onSelect(
									null,
									sql,
									new String[] {
											String.valueOf(tempTaskId),
											String.valueOf(taskCategoryInfo.CategoryID),
											String.valueOf(baseCategoryId),
											dataItemName });
					if (cur != null && cur.moveToFirst()) {
						item.setValueByCursor(cur);
						if (item.ID > 0) {
							item.Value = dataItemValue;
							if (item.onUpdate("ID = " + item.ID) > 0) {
								result.Data = true;
								result.Success = true;
								result.Message = "操作成功";
								changeCache(taskInfoActivity, dataItemValue,
										item);
							}
							// 如果更文件的选项类型就把之前的value替换为空
							removeBeforeItem(taskCategoryInfo.CategoryID,
									beforeType, beforeValue, tempTaskId,
									baseCategoryId, sql, taskInfoActivity,
									result);
						}
					} else {// 不是安卓端创建if(!taskInfo.IsNew)
						DataFieldDefine field = new DataFieldDefine();
						Cursor cursor = field
								.onSelect(
										null,
										"CategoryID=? and DDID=? and Name=?",
										new String[] {
												String.valueOf(taskCategoryInfo.CategoryID),
												String.valueOf(taskInfo.DDID),
												dataItemName });
						long newId = 0;
						if (cursor != null && cursor.moveToFirst()) {
							field.setValueByCursor(cursor);
							item.IOrder = field.IOrder;
							item.CategoryID = field.CategoryID;
						} else {
							item.IOrder = 0;//
							item.CategoryID = 0;
						}
						item.BaseCategoryID = baseCategoryId;
						item.Name = dataItemName;
						item.Value = dataItemValue;
						item.TaskID = tempTaskId;
						item.BaseID = -1;
						newId = item.onInsert();
						if (newId > 0) {
							result.Data = true;
							result.Success = true;
							result.Message = "操作成功";
							if (item.CategoryID > 0) {
								taskInfoActivity.viewModel.currentCategory.Items
										.add(item);
							}
							// 改变当前上下文中的分类项缓存值（读取子项信息是从缓存中读取的，这才能同步所做操作）
							for (TaskCategoryInfo categoryInfo : taskInfoActivity.viewModel.currentTask.Categories) {
								if (categoryInfo.BaseCategoryID == item.BaseCategoryID) {
									categoryInfo.Items.add(item);
								}
							}
							// 如果更文件的选项类型就把之前的value替换为空
							removeBeforeItem(taskCategoryInfo.CategoryID,
									beforeType, beforeValue, tempTaskId,
									baseCategoryId, sql, taskInfoActivity,
									result);
						}
						cursor.close();
					}
					cur.close();
				}

			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		} finally {
			ResultInfo<TaskInfo> temp = TaskDataWorker
					.queryTaskInfoByTaskNum(taskInfo.TaskNum);
			temp.Data.HasResource = hasResource(taskInfo.TaskNum);
			temp.Data.onUpdate("TaskNum = '" + taskInfo.TaskNum + "'");
		}
		return result;
	}

	/**
	 * 判断当前任务是否有资源文件
	 * 
	 * @param taskNum任务编码
	 * @return
	 */
	private static Boolean hasResource(String taskNum) {
		Boolean result = false;
		ArrayList<String> files = FileUtil
				.getRecursion(EIASApplication.projectRoot + taskNum);
		if (files.size() > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * 如果更文件的选项类型就把之前的value替换为空
	 * 
	 * @param categoryID
	 *            当前分类项编号
	 * @param beforeType
	 *            修改之前的选择类型
	 * @param beforeValue
	 *            修改之前的文件名称
	 * @param tempTaskId
	 *            当前任务编号
	 * @param baseCategoryId
	 *            之前的分类项编号
	 * @param sql
	 *            需要查询的sql
	 * @param taskInfoActivity
	 *            需要操作的上下文
	 * @param result
	 *            结果信息
	 */
	private static void removeBeforeItem(Integer categoryID, String beforeType,
			String beforeValue, Integer tempTaskId, Integer baseCategoryId,
			String sql, TaskInfoActivity taskInfoActivity,
			ResultInfo<Boolean> result) {

		if (beforeType != null && beforeType.length() > 0) {
			if (beforeType.contains(MediaDataInfo.suffixJpg)) {
				if (!beforeType.substring(beforeType.length() - 1).equals(";")) {
					beforeType = beforeType + ";";
				}
			}
			TaskDataItem beforeitem = new TaskDataItem();
			Cursor beforCur = beforeitem.onSelect(null, sql, new String[] {
					String.valueOf(tempTaskId), String.valueOf(categoryID),
					String.valueOf(baseCategoryId), beforeType });
			if (beforCur != null && beforCur.moveToFirst()) {
				beforeitem.setValueByCursor(beforCur);
				if (beforeitem.ID > 0) {
					if (beforeValue.substring(beforeValue.length() - 1).equals(
							";")) {
						beforeitem.Value = beforeitem.Value.replace(
								beforeValue, "");
					} else {
						beforeitem.Value = beforeitem.Value.replace(beforeValue
								+ ";", "");
					}
					if (beforeitem.onUpdate("ID = " + beforeitem.ID) > 0) {
						result.Data = true;
						result.Success = true;
						result.Message = "操作成功";
						changeCache(taskInfoActivity, beforeitem.Value,
								beforeitem);
					}
				}
			} else {
				beforCur = beforeitem
						.onSelect(null, "TaskId=? AND Name=?", new String[] {
								String.valueOf(tempTaskId), beforeType });
				if (beforCur != null && beforCur.moveToFirst()) {
					beforeitem.setValueByCursor(beforCur);
					if (beforeitem.ID > 0) {
						if (beforeitem.onDelete(beforeitem.ID + "") > 0) {
							result.Data = true;
							result.Success = true;
							result.Message = "操作成功";
						}
					}
				}
			}
			beforCur.close();
		}
	}

	/**
	 * 更改缓存中的任务子项值（读取子项信息是从缓存中读取的，这才能同步所做操作）
	 * 
	 * @param taskInfoActivity
	 *            :当前上下文
	 * @param dataItemValue
	 *            :当前分类项需要保存的值
	 * @param item
	 *            :数据库中已改变的分类项的分类项
	 */
	private static void changeCache(TaskInfoActivity taskInfoActivity,
			String dataItemValue, TaskDataItem item) {
		// 更改 taskInfoActivity.viewModel.currentCategory.Items 中的值
		for (TaskDataItem dataItem : taskInfoActivity.viewModel.currentCategory.Items) {
			if (dataItem.Name.equals(item.Name)
					&& item.CategoryID == dataItem.CategoryID
					&& item.BaseCategoryID == dataItem.BaseCategoryID) {
				dataItem.Value = dataItemValue;
			}
		}
		// 更改 taskInfoActivity.viewModel.currentTask.Categories中的值
		for (TaskCategoryInfo categoryInfo : taskInfoActivity.viewModel.currentTask.Categories) {
			if (categoryInfo.BaseCategoryID == item.BaseCategoryID) {
				for (TaskDataItem dataItem : categoryInfo.Items) {
					if (dataItem.Name.equals(item.Name)
							&& item.CategoryID == dataItem.CategoryID
							&& item.BaseCategoryID == dataItem.BaseCategoryID) {
						dataItem.Value = dataItemValue;
					}
				}
			}
		}
	}

	// }}

	/**
	 * 判断是否存在该分类项
	 * 
	 * @param taskCategoryInfo
	 * @return
	 */
	public static Boolean isExitCategoryInfo(TaskCategoryInfo taskCategoryInfo) {
		Boolean result = false;
		Cursor cursor;
		// 检查数据库中是否存在该分类名称
		if (taskCategoryInfo.ID > 0) {
			cursor = taskCategoryInfo.onSelect(
					null,
					"TaskID=? and RemarkName=? and ID!=?",
					new String[] { String.valueOf(taskCategoryInfo.TaskID),
							taskCategoryInfo.RemarkName,
							String.valueOf(taskCategoryInfo.ID) });
		} else {
			cursor = taskCategoryInfo.onSelect(null,
					"TaskID=? and RemarkName=?",
					new String[] { String.valueOf(taskCategoryInfo.TaskID),
							taskCategoryInfo.RemarkName });
		}
		if (cursor != null && cursor.moveToFirst()) {
			result = true;
		}
		cursor.close();
		return result;
	}

	/**
	 * 保存或创建分类项信息，操作前会检查分类项名称是否有重复
	 * 
	 * @param taskCategoryInfo
	 * @return
	 */
	public static ResultInfo<Boolean> saveCategoryInfo(
			TaskCategoryInfo taskCategoryInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		Boolean isExitsCategoryInfo = isExitCategoryInfo(taskCategoryInfo);
		if (isExitsCategoryInfo) {
			result.Success = true;
			result.Data = false;
			result.Message = "分类名已经存在";
		} else {
			if (taskCategoryInfo.ID > 0) {
				int execRow = taskCategoryInfo.onUpdate("ID="
						+ taskCategoryInfo.ID);
				if (execRow > 0) {
					result.Data = true;
					result.Message = "保存成功";
				} else {
					result.Data = false;
					result.Message = "保存失败";
				}
			} else {
				long newId = taskCategoryInfo.onInsert();
				if (newId > 0) {
					result.Data = true;
					result.Message = "新建成功";
					result.Others = newId;
				} else {
					result.Data = false;
					result.Message = "新建失败";
				}
			}
		}

		return result;
	}

	/**
	 * 删除分类项
	 * 
	 * @param taskNum
	 *            :任务编号 用于记录日志
	 * @param remarkName
	 *            :分类项名称
	 * @param taskCategoryInfo
	 *            :任务分类项
	 * @return
	 */
	public static ResultInfo<Boolean> deleteCategorie(TaskInfo task,
			TaskCategoryInfo category) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		ResultInfo<Integer> deleteItem = TaskDataWorker.deleteCategoryInfo(
				task, category);
		if (deleteItem != null && deleteItem.Data > 0) {
			result.Data = true;
			result.Message = "删除成功";
		} else {
			result.Data = false;
			result.Message = "删除失败";
		}
		return result;
	}

	// {{ 清空子项 clearCategoryItemse
	/**
	 * 清空任务分类子项，设置为勘察表的默认值
	 * 
	 * @param taskNum任务编号用于记录日志
	 * @param isCreatedByUesr是否用户创建
	 * @param taskCategoryInfo任务分类项
	 * @return
	 */
	public static ResultInfo<Boolean> clearCategoryItems(TaskInfo task,
			TaskCategoryInfo taskCategoryInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		// 找到数据库中需要清空的项
		ArrayList<TaskDataItem> taskItems = TaskDataWorker
				.queryTaskDataItemsByID(taskCategoryInfo.TaskID,
						taskCategoryInfo.CategoryID).Data;
		/*
		 * .queryTaskDataItemsByID(taskCategoryInfo.TaskID,
		 * taskCategoryInfo.CategoryID, taskCategoryInfo.BaseCategoryID,
		 * false).Data;
		 */
		ArrayList<DataFieldDefine> defineFileds = DataDefineWorker
				.queryDataFieldDefineByID(task.DDID,
						taskCategoryInfo.CategoryID).Data;
		if (defineFileds != null & taskItems != null && taskItems.size() > 0) {
			try {
				for (int i = 0; i < taskItems.size(); i++) {
					for (int j = 0; i < defineFileds.size(); j++) {
						if (defineFileds.get(j).ShowInPhone) {
							if (taskItems.get(i).Name.equals(defineFileds
									.get(j).Name)) {
								if (taskItems.get(i).Name == "UserName") {
									taskItems.get(i).Value = EIASApplication
											.getCurrentUser().Name;
								} else if (taskItems.get(i).Name == "UserTel") {
									taskItems.get(i).Value = EIASApplication
											.getCurrentUser().Mobile;
								} else {
									taskItems.get(i).Value = defineFileds
											.get(j).Value;
								}
								// 保存到数据库
								taskItems.get(i).onUpdate(
										"ID=" + taskItems.get(i).ID);
								// defineFileds.remove(j); 加了这句导致子项没全部清除
								break;
							}
						}
					}
				}
				result.Data = true;
				result.Message = "清空任务分类子项成功";
				DataLogOperator.categoryDefineDataReset(task, taskCategoryInfo,
						"");
			} catch (Exception e) {
				DataLogOperator.categoryDefineDataReset(task, taskCategoryInfo,
						e.getMessage());
			}
		} else {
			result.Data = false;
			result.Message = "清空子项失败，没找到这个分类项的数据。";
			DataLogOperator
					.categoryDefineDataReset(task, taskCategoryInfo,
							"TaskDataWorker.queryTaskDataItemsByID（）调用该方法查询任务谋分类项子项时找不到数据");
		}
		return result;
	}

	// }}

	// {{ 从服务器拿的TaskInfo 复制到数据到选择的任务中 只复制数据 不复制文件

	/**
	 * 获取服务器的完整的任务信息 包含分类项和子项
	 * 
	 * @param task
	 * @return
	 */
	public static ResultInfo<TaskInfo> getServerTaskInfoComplete(TaskInfo task) {
		ResultInfo<TaskInfo> result = null;
		GetTaskInfoTask taskHttp = new GetTaskInfoTask();
		result = taskHttp.request(EIASApplication.getCurrentUser(), task);
		return result;
	}

	/**
	 * 获取安卓端获取完整的任务信息 包含分类项和子项
	 * 
	 * @param task
	 * @return
	 */
	public static TaskInfo getLocTaskInfoComplete(TaskInfo task) {
		TaskInfo result = new TaskInfo();
		Cursor cur = task.onSelect(null, " TaskNum = ?",
				new String[] { task.TaskNum });
		if (cur.moveToFirst()) {
			task.setValueByCursor(cur);
			ResultInfo<TaskInfo> tempTask = TaskDataWorker
					.getCompleteTaskInfoById(
							task.IsNew ? task.ID : task.TaskID, task.IsNew);
			if (tempTask.Data != null) {
				result = tempTask.Data;
			}
		}
		cur.close();
		return result;
	}

	/**
	 * 获取安卓端获取完整的任务信息 包含分类项和子项
	 * 
	 * @param task
	 * @return
	 */
	public static TaskInfo getLocTaskInfoCompleteByTaskNum(String taskNum) {
		TaskInfo result = new TaskInfo();
		ResultInfo<TaskInfo> tempTask = TaskDataWorker
				.getCompleteTaskInfoByTaskNum(taskNum);
		if (tempTask.Data != null) {
			result = tempTask.Data;
		}
		return result;
	}

	/**
	 * 把服务器数据匹配到安卓端的数据中
	 * 
	 * @param serverTaskInfo
	 *            :服务器的数据 不包含分类项和子项
	 * @param locTaskInfo
	 *            :安卓端的数据 不包含分类项和子项
	 * @return
	 */
	public static ResultInfo<Boolean> copyTaskInfoByServerData(
			TaskInfo serverTaskInfo, TaskInfo locTaskInfo) {
		// 记录结果
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;
		result.Success = false;

		String serverTaskNum = serverTaskInfo.TaskNum;
		String localTaskNum = locTaskInfo.TaskNum;

		// 在服务器获取完整的任务信息 包含分类项和子项
		ResultInfo<TaskInfo> serverResult = getServerTaskInfoComplete(serverTaskInfo);
		serverTaskInfo = serverResult.Data;
		// 在安卓端获取完整的任务信息 包含分类项和子项
		locTaskInfo = getLocTaskInfoComplete(locTaskInfo);

		try {
			if (serverTaskInfo == null) {
				if (serverResult.Message.length() > 0
						&& serverResult.Message != null
						&& !serverResult.Message.equals("null")) {
					result.Message = serverResult.Message;
				} else {
					result.Message = "在服务器中没有找到任务[" + serverTaskNum + "]";
				}
			} else if (locTaskInfo == null) {
				result.Message = "在本地数据中没有找到任务[" + localTaskNum + "]";
			} else {
				// 获取勘察数据信息
				ResultInfo<DataDefine> dataDefine = DataDefineWorker
						.getCompleteDataDefine(locTaskInfo.DDID);
				if (dataDefine.Success && dataDefine.Data != null) {
					// 复制分类项
					pastedInfoes(serverTaskInfo.Categories,
							locTaskInfo.Categories, dataDefine.Data,
							serverTaskInfo, locTaskInfo,
							OperatorTypeEnum.TaskDataMatching);
					// 修改返回结果
					result.Data = true;
					result.Success = true;
					result.Message = "匹配成功";
				} else {
					// 修改返回结果
					result.Data = false;
					result.Success = false;
					result.Message = "请检查本地任务勘察表";
				}
			}
		} catch (Exception e) {

			result.Message = "请检查本地任务勘察表";
		}
		DataLogOperator.taskDataMatching(serverTaskInfo, locTaskInfo,
				result.Message);
		return result;
	}

	// }}

	// {{ 上传文件到服务器

	/**
	 * 获取需要上传的媒体文件
	 * 
	 * @param taskInfo
	 *            ： 当前任务信息，一个完整的任务信息，包含所有分类项和其下面的子项内容
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskDataItem>> getUploadFiles(
			TaskInfo taskInfo) {
		ResultInfo<ArrayList<TaskDataItem>> result = new ResultInfo<ArrayList<TaskDataItem>>();
		try {
			// 媒体文件列表
			ArrayList<TaskDataItem> taskDataItems = new ArrayList<TaskDataItem>();

			// 获取勘察数据信息
			ResultInfo<DataDefine> dataDefine = DataDefineWorker
					.getCompleteDataDefine(taskInfo.DDID);

			for (DataCategoryDefine categoryDefine : dataDefine.Data.Categories) {
				if (categoryDefine.ControlType == CategoryType.PictureCollection
						|| categoryDefine.ControlType == CategoryType.VideoCollection
						|| categoryDefine.ControlType == CategoryType.AudioCollection) {
					for (TaskCategoryInfo category : taskInfo.Categories) {
						if (category.CategoryID == categoryDefine.CategoryID) {
							ArrayList<TaskDataItem> tempItem = getFileParam(
									category, categoryDefine);
							taskDataItems.addAll(tempItem);
						}
					}
				}
			}
			result.Data = taskDataItems;
			result.Success = true;
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 把每一个的文件都记录下来
	 * 
	 * @param category
	 * @param categoryDefine
	 * @return
	 */
	private static ArrayList<TaskDataItem> getFileParam(
			TaskCategoryInfo category, DataCategoryDefine categoryDefine) {
		ArrayList<TaskDataItem> result = new ArrayList<TaskDataItem>();

		if (ListUtil.hasData(category.Items)) {
			for (TaskDataItem dataItem : category.Items) {
				if (dataItem.CategoryID > 0 && dataItem.Value != null
						&& !dataItem.Value.equals("null")
						&& dataItem.Value.length() > 0) {
					result.add(dataItem);
				}
			}
		}

		return result;
	}

	/**
	 * 获取需要上传的媒体文件
	 * 
	 * @param taskInfo
	 *            ： 当前任务信息，一个完整的任务信息，包含所有分类项和其下面的子项内容
	 * @return
	 */
	public static ResultInfo<ArrayList<TaskDataItem>> getUploadFilesByAdditional(
			TaskInfo taskInfo) {
		ResultInfo<ArrayList<TaskDataItem>> result = new ResultInfo<ArrayList<TaskDataItem>>();
		try {
			// 媒体文件列表
			ArrayList<TaskDataItem> taskDataItems = new ArrayList<TaskDataItem>();

			// 获取勘察数据信息
			ResultInfo<DataDefine> dataDefine = DataDefineWorker
					.getCompleteDataDefine(taskInfo.DDID);

			for (DataCategoryDefine categoryDefine : dataDefine.Data.Categories) {
				if (categoryDefine.ControlType == CategoryType.PictureCollection) {
					for (TaskCategoryInfo category : taskInfo.Categories) {
						if (category.CategoryID == categoryDefine.CategoryID) {
							ArrayList<TaskDataItem> tempItem = getFileParamByAdditional(
									category, categoryDefine);
							taskDataItems.addAll(tempItem);
						}
					}
				}
			}
			result.Data = taskDataItems;
			result.Success = true;
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}
		return result;
	}

	/**
	 * 把每一个的文件都记录下来
	 * 
	 * @param category
	 * @param categoryDefine
	 * @return
	 */
	private static ArrayList<TaskDataItem> getFileParamByAdditional(
			TaskCategoryInfo category, DataCategoryDefine categoryDefine) {
		ArrayList<TaskDataItem> result = new ArrayList<TaskDataItem>();

		if (ListUtil.hasData(category.Items)) {
			for (TaskDataItem dataItem : category.Items) {
				if (dataItem.CategoryID > 0 && dataItem.Value != null
						&& !dataItem.Value.equals("null")
						&& dataItem.Value.length() > 0) {
					for (DataFieldDefine defineItem : categoryDefine.Fields) {
						if (defineItem.ShowInPhone
								&& defineItem.Name.equals(dataItem.Name)) {
							result.add(dataItem);
							break;
						}
					}
				}
			}
		}

		return result;
	}

	// }}

	// {{ 提交任务到服务器

	/**
	 * 检查必填项
	 * 
	 * @param ddid任务使用的勘察表是
	 * @param submitTaskInfo提交的任务
	 * @return
	 */
	public static ArrayList<TaskCategoryInfo> checkHasValue(Integer ddid,
			TaskInfo submitTaskInfo) {
		// 必填项为填写
		ArrayList<TaskCategoryInfo> unWriteItems = new ArrayList<TaskCategoryInfo>();
		// 获取勘察数据信息
		ResultInfo<DataDefine> dataDefine = DataDefineWorker
				.getCompleteDataDefine(ddid);
		// 循环判断哪些必填项没有填写
		if (dataDefine.Data != null && submitTaskInfo != null) {
			// 检测添加是否完整
			for (TaskCategoryInfo category : submitTaskInfo.Categories) {
				for (DataCategoryDefine define : dataDefine.Data.Categories) {
					if (category.CategoryID == define.CategoryID) {
						ArrayList<TaskDataItem> unWriteItem = taskSubmitVerify(
								category, define);
						if (unWriteItem.size() > 0) {
							category.Items = unWriteItem;
							unWriteItems.add(category);
						}
						break;
					}
				}
			}
		}
		return unWriteItems;
	}

	/**
	 * 提交任务中 通知后台服务提交任务
	 */
	public static void taskSubmiting(TaskInfo submitTaskInfo) {
		submitTaskInfo.Status = TaskStatus.Submiting;
		// 设置任务参数
		HashMap<String, Object> para = new HashMap<String, Object>();
		para.put("taskInfo", submitTaskInfo);
		// 设置后台运行的任务
		BackgroundServiceTask task = new BackgroundServiceTask(
				MainService.TASK_SUMIT, para);
		// 添加到任务池中
		MainService.setTask(task);
	}

	/**
	 * 验证输入的信息
	 * 
	 * @param category
	 *            :任务分类项
	 * @param field
	 *            :勘察分类项
	 * @return 提示信息
	 */
	private static ArrayList<TaskDataItem> taskSubmitVerify(
			TaskCategoryInfo category, DataCategoryDefine field) {
		ArrayList<TaskDataItem> result = new ArrayList<TaskDataItem>();
		for (DataFieldDefine defineItem : field.Fields) {
			if (defineItem.Required) {
				// 记录任务分类中不存在的子项
				DataFieldDefine unExitsItem = defineItem;

				for (TaskDataItem dataItem : category.Items) {
					if (defineItem.Name.equals(dataItem.Name)) {
						// 等于null说明存在
						unExitsItem = null;
						// if (dataItem.Value.trim().length() <= 0 ||
						// dataItem.Value.equals(EIASApplication.DefaultDropDownListValue)
						// ||
						// dataItem.Value.equals(EIASApplication.DefaultHorizontalLineValue)
						// ||
						// dataItem.Value.equals(EIASApplication.DefaultNullString)
						// ||
						// dataItem.Value.equals(EIASApplication.DefaultBaiduMapTipsValue
						// + EIASApplication.DefaultBaiduMapUnLocTipsValue)) {
						// result.add(dataItem);
						// }
						if (dataItem.Value.trim().length() <= 0
								|| dataItem.Value
										.equals(EIASApplication.DefaultDropDownListValue)
								|| dataItem.Value
										.equals(EIASApplication.DefaultHorizontalLineValue)
								|| dataItem.Value
										.equals(EIASApplication.DefaultNullString)
								|| dataItem.Value
										.equals(EIASApplication.DefaultBaiduMapTipsValue
												+ EIASApplication.DefaultBaiduMapUnLocTipsValue)) {
							result.add(dataItem);
						}
						break;
					}
				}

				if (unExitsItem != null) {
					TaskDataItem tempTaskDataItem = new TaskDataItem();
					tempTaskDataItem.Name = unExitsItem.Name;
					result.add(tempTaskDataItem);
				}
			}

		}
		return result;
	}

	// }}

	// {{ 判断当前操作的任务是否在提交

	/**
	 * 判断当前操作的任务是否在提交
	 * 
	 * @param taskNum
	 *            :当前提交的任务编码
	 * @return
	 */
	public static Boolean submiting(String taskNum) {
		Boolean result = false;
		if (taskNum.equals(EIASApplication.SubmitingTaskNum)
				&& EIASApplication.SubmitingTaskNum.length() > 0) {
			result = true;
		}
		return result;
	}

	// }}

	// {{ 设置任务提交状态

	/**
	 * 设置任务提交状态 修改数据库中此条任务的上传状态
	 * 
	 * @param taskUploadStatusEnum
	 *            提交任务状态枚举
	 * @param taskNum
	 *            任务编号
	 * @return 是否成功执行
	 */
	public static Boolean saveTaskUploadStatus(
			TaskUploadStatusEnum taskUploadStatusEnum, String taskNum) {
		Boolean result = false;
		SQLiteDatabase db = null;
		TaskInfo taskInfo = new TaskInfo();
		db = SQLiteHelper.getWritableDB();
		Cursor cursor = taskInfo.onSelect(null, "TaskNum = ?",
				new String[] { taskNum });
		if (cursor != null && cursor.moveToFirst()) {
			taskInfo.setValueByCursor(cursor);
			taskInfo.UploadStatusEnum = taskUploadStatusEnum;
			db.update(taskInfo.getTableName(), taskInfo.getContentValues(),
					"TaskNum=?", new String[] { taskNum });
			result = true;
		}
		cursor.close();
		return result;
	}

	/**
	 * 修改任务的Status 状态
	 * 
	 * @param taskStatus
	 *            任务状态枚举
	 * @param taskNum
	 *            任务编号
	 * @return 是否成功执行
	 */
	private static Boolean saveTaskUStatus(TaskStatus taskStatus, String taskNum) {
		Boolean result = false;
		SQLiteDatabase db = null;
		TaskInfo taskInfo = new TaskInfo();
		Cursor cursor = null;
		try {
			db = SQLiteHelper.getWritableDB();
			cursor = taskInfo.onSelect(null, "TaskNum = ?",
					new String[] { taskNum });
			if (cursor != null && cursor.moveToFirst()) {
				taskInfo.setValueByCursor(cursor);
				taskInfo.Status = taskStatus;
				db.update(taskInfo.getTableName(), taskInfo.getContentValues(),
						"TaskNum=?", new String[] { taskNum });
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

		return result;
	}

	// }}

	// {{ 添加任务提交次数

	/**
	 * 任务提交次数加1
	 * 
	 * @param taskNum
	 * @return
	 */
	public static Integer addTaskUploadTimes(String taskNum) {
		SQLiteDatabase db = null;
		TaskInfo taskInfo = new TaskInfo();
		db = SQLiteHelper.getWritableDB();
		Cursor cursor = taskInfo.onSelect(null, "TaskNum = ?",
				new String[] { taskNum });
		if (cursor != null && cursor.moveToFirst()) {
			taskInfo.setValueByCursor(cursor);
			taskInfo.UploadTimes = taskInfo.UploadTimes + 1;
			db.update(taskInfo.getTableName(), taskInfo.getContentValues(),
					"TaskNum=?", new String[] { taskNum });
		}
		cursor.close();
		return taskInfo.UploadTimes;
	}

	// }}

	// {{ 任务检查

	/**
	 * 检测图片格式
	 * 
	 * @param dto任务对象
	 * @param exportPath图片导出路径
	 * @return 图片是否可以正确读取
	 */
	public static ArrayList<DialogTipsDTO> taskCheckFormat(TaskInfo taskInfo,
			String exportPath) {
		TaskInfoDTO dto = new TaskInfoDTO(taskInfo);
		return taskCheckFormat(dto, exportPath);
	}

	private static ArrayList<DialogTipsDTO> taskCheckFormat(TaskInfoDTO dto,
			String exportPath) {
		ArrayList<DialogTipsDTO> result = new ArrayList<DialogTipsDTO>();
		// 取得存放图片文件目录
		String showMsg = "";
		try {
			if (dto != null && dto.Categories != null) {
				for (TaskCategoryInfoDTO category : dto.Categories) {
					if (category.Items != null
							&& (category.CategoryType.equals("MP")
									|| category.CategoryType.equals("AC") || category.CategoryType
										.equals("VC"))) {
						for (TaskDataItemDTO dataItem : category.Items) {
							if (dataItem.Value != null
									&& !dataItem.Value.equals("null")
									&& dataItem.Value.length() > 0) {
								String[] resources = dataItem.Value.split(";");
								for (String file : resources) {
									if (!file.equals("null")
											&& file.length() > 0) {
										String path = "";
										String actionMsg = "提示动作";
										String fileType = "文件类型";
										switch (category.CategoryType) {
										case "MP":
											path = exportPath + "/photo/";
											actionMsg = "拍摄";
											fileType = "图片";
											break;
										case "AC":
											path = exportPath + "/audio/";
											actionMsg = "录音";
											fileType = "音频";
											break;
										case "VC":
											path = exportPath + "/video/";
											actionMsg = "录制";
											fileType = "视频";
											break;
										default:
											break;
										}
										String fullPhotoPath = path + file;
										File exportFile = new File(
												fullPhotoPath);
										if (!exportFile.exists()
												&& fullPhotoPath
														.contains(MediaDataInfo.suffixJpg)) {
											showMsg = "[" + category.RemarkName
													+ "]下的[" + dataItem.Name
													+ "]" + fileType
													+ "对应文件找不到";
										}
										if (showMsg.length() > 0) {
											DialogTipsDTO dialog = new DialogTipsDTO();
											dialog.Category = CategoryType
													.getEnumByName(category.CategoryType);
											dialog.Concent = showMsg + "，建议重新"
													+ actionMsg + "。"
													+ fileType;
											result.add(dialog);
											showMsg = "";
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			DataLogOperator.other("taskCheckFormat=>" + e.getMessage());
		}
		return result;
	}

	// }}

	// {{任务数据导出

	/**
	 * 任务导出检查数据
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ResultInfo<ArrayList<DialogTipsDTO>> taskExportCheck(
			String taskNum) {
		ResultInfo<ArrayList<DialogTipsDTO>> result = new ResultInfo<ArrayList<DialogTipsDTO>>();
		ArrayList<DialogTipsDTO> data = new ArrayList<DialogTipsDTO>();
		File checkFile = new File(EIASApplication.projectRoot + taskNum);
		// 获取导出文件夹大小
		try {
			// 获取出厂内置SD卡 例如三星 和 小米
			File path = Environment.getExternalStorageDirectory();
			// 取得SDcard文件路径
			StatFs statfs = new StatFs(path.getPath());
			// 获取剩余大小
			int free = statfs.getFreeBlocks();
			// 获取block的SIZE
			long blockSize = statfs.getBlockSize();
			// 获取剩余容量小于指定大小时 提示
			String minSDCardSize = EIASApplication
					.getSystemSetting(BroadRecordType.KEY_SETTING_SDCARDSIZE);

			// 需要压缩的文件夹
			if (!checkFile.exists()) {
				checkFile.mkdirs();
			}
			long size = DataCheckHelper.getFileSize(checkFile);
			// 是否有文件可以上传 2015-10-27 没有资源文件也可以导出
			/*
			 * if (size == 0) { data.add(new DialogTipsDTO("没有可上传的文件")); }
			 */
			if ((free * blockSize / 1024 / 1024) < Integer
					.parseInt(minSDCardSize) || (free * blockSize) < size) {
				data.add(new DialogTipsDTO("剩余空间不足，请清理后再继续导出"));
			} else {
				ResultInfo<TaskInfo> task = TaskDataWorker
						.getCompleteTaskInfoByTaskNum(taskNum);
				if (task.Success && task.Data != null && task.Data.ID > 0) {
					ArrayList<DialogTipsDTO> currentCheckResult = taskCheckFormat(
							task.Data, checkFile.getAbsolutePath());
					data.addAll(currentCheckResult);
				} else {
					DialogTipsDTO dialog = new DialogTipsDTO(
							"非常抱歉，我们没有找到该任务，建议刷新后重试");
					data.add(dialog);
				}
			}
			if (data.size() > 0) {
				result.Success = false;
				result.Message = "提示信息";
			} else {
				data.add(new DialogTipsDTO("任务检查完成，可以导出"));
				result.Success = true;
				result.Message = "提示信息";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.Data = data;
		return result;
	}

	/**
	 * 任务导出
	 * 
	 * @return
	 */
	public static ResultInfo<ArrayList<DialogTipsDTO>> taskExport(String taskNum) {
		String msg = "";
		ArrayList<DialogTipsDTO> data = new ArrayList<DialogTipsDTO>();
		ResultInfo<ArrayList<DialogTipsDTO>> result = new ResultInfo<ArrayList<DialogTipsDTO>>();
		// 需要导出的文件路径
		File sourceDir = new File(EIASApplication.projectRoot + taskNum);
		File sourceJsonFullName = new File(sourceDir + "/"
				+ EIASApplication.exportJsonName);
		try {
			// 导出后的文件
			File targetFile = new File(EIASApplication.exportRoot);
			File targetFullName = new File(EIASApplication.exportRoot + taskNum
					+ ".zip");
			targetFullName.delete();
			// 在指定的文件夹中创建文件
			if (targetFile.exists() || targetFile.mkdirs()) {
				ResultInfo<TaskInfo> task = TaskDataWorker
						.getCompleteTaskInfoByTaskNum(taskNum);
				if (task.Success && task.Data.ID > 0) {
					TaskInfoDTO dto = new TaskInfoDTO(task.Data);
					String jsonStr = JSONHelper.toJSON(dto);
					// 检查父目录是否存在，不存在则创建。
					File file = new File(sourceJsonFullName.getParent());
					if (!file.exists()) {
						file.mkdirs();
					}
					sourceJsonFullName.createNewFile();
					OutputStream outstream = new FileOutputStream(
							sourceJsonFullName);
					OutputStreamWriter out = new OutputStreamWriter(outstream);
					out.write(jsonStr);
					out.close();
					CompressionAndDecompressionUtil.compressionZip(
							sourceDir.getAbsolutePath(),
							targetFullName.getAbsolutePath());
					if (targetFullName.exists()) {
						result.Message = "导出完成";
						msg = "路径:" + targetFullName.getAbsolutePath();
					} else {
						result.Message = "导出失败";
						msg = "建议联系管理员";
					}
					data.add(new DialogTipsDTO(msg));
				}
			}
		} catch (java.io.IOException e) {
			result.Message = "导出失败";
			msg = "请检查SD卡文件目录！";
			data.add(new DialogTipsDTO(msg));
			e.printStackTrace();
		} finally {
			sourceJsonFullName.delete();
		}
		result.Data = data;
		return result;
	}

	// }}

	// {{任务数据导入

	/**
	 * 导入任务
	 * 
	 * @param msg
	 * @param viewModel
	 * @return
	 */
	public static ResultInfo<ArrayList<DialogTipsDTO>> taskImport(
			String fileFullName, TaskImportGuideViewModel viewModel) {
		ResultInfo<ArrayList<DialogTipsDTO>> result = new ResultInfo<ArrayList<DialogTipsDTO>>();
		result.Success = false;
		result.Data = new ArrayList<DialogTipsDTO>();
		String output = EIASApplication.importRoot + viewModel.taskNum;
		// 创建解压文件
		File inputPath = new File(output);
		if (!inputPath.exists()) {
			inputPath.mkdir();
		}
		try {
			// 解压文件 文件是否可以成功解压
			CompressionAndDecompressionUtil.decompressionZip(fileFullName,
					inputPath.getAbsolutePath().toString());
			// JSON文件 是否存在 是否可以读
			String jsonPath = inputPath.getAbsolutePath().toString() + "/"
					+ EIASApplication.exportJsonName;
			File jsonFile = new File(jsonPath);
			if (!jsonFile.exists()) {
				result.Data.add(new DialogTipsDTO("没有找到压缩包中的文本文件，建议重新导出一份后重试"));
			} else {
				// 导入的任务对象
				String jsonConcent = DataCheckHelper.ReadTxtFile(jsonFile);
				JSONObject json = new JSONObject(jsonConcent);
				TaskInfoDTO importTaskInfoDto = new TaskInfoDTO(json);
				viewModel.zipTaskInfo = new TaskInfo(importTaskInfoDto);
				// 选择的任务对象
				viewModel.androidTaskInfo = TaskOperator
						.getLocTaskInfoCompleteByTaskNum(viewModel.taskNum);
				if (viewModel.androidTaskInfo != null
						&& viewModel.androidTaskInfo.ID > 0) {
					// 对比任务信息
					if (!viewModel.zipTaskInfo.TaskNum
							.equals(viewModel.androidTaskInfo.TaskNum)) {
						result.Data
								.add(new DialogTipsDTO("任务编号不一致，建议检查一下导入文件"));
					}
					if (!viewModel.zipTaskInfo.TargetAddress
							.equals(viewModel.androidTaskInfo.TargetAddress)) {
						result.Data
								.add(new DialogTipsDTO("任务地址不一致，建议检查一下导入文件"));
					}
					if (!viewModel.zipTaskInfo.ResidentialArea
							.equals(viewModel.androidTaskInfo.ResidentialArea)) {
						result.Data.add(new DialogTipsDTO(
								"任务小区名称不一致，建议检查一下导入文件"));
					}
					if (!viewModel.zipTaskInfo.TargetType
							.equals(viewModel.androidTaskInfo.TargetType)) {
						result.Data
								.add(new DialogTipsDTO("任务用途不一致，建议检查一下导入文件"));
					}
					if (viewModel.zipTaskInfo.DDID != viewModel.androidTaskInfo.DDID) {
						result.Data
								.add(new DialogTipsDTO("任务勘察表不一致，建议检查一下导入文件"));
					}
					result.Data.addAll(taskCheckFormat(importTaskInfoDto,
							output));
					// 如果没有文件就 开始导入 覆盖 把导入的文件覆盖到 android端 资源文件不覆盖 而是累加
					if (result.Data.size() <= 0) {
						HashMap<String, String> selectedCategoryItems = new HashMap<String, String>();
						if (ListUtil.hasData(viewModel.zipTaskInfo.Categories)) {
							for (TaskCategoryInfo item : viewModel.zipTaskInfo.Categories) {
								selectedCategoryItems.put(
										String.valueOf(item.ID),
										item.RemarkName);
							}
							EIASApplication.isImporting = true;
							ResultInfo<Boolean> importResult = pastedTaskInfo(
									viewModel.zipTaskInfo,
									selectedCategoryItems, null,
									viewModel.androidTaskInfo,
									OperatorTypeEnum.CategoryDefineDataCopy);
							if (importResult.Success) {
								result.Success = true;
								result.Message = "提示信息";
								result.Data.add(new DialogTipsDTO("导入成功"));
							} else {
								result.Message = "提示信息";
								result.Data.add(new DialogTipsDTO(
										"导入失败,建议重新导出一份文件后再导入"));
							}
						}
					}
				} else {
					DialogTipsDTO dialog = new DialogTipsDTO(
							"非常抱歉，我们没有找到该任务，建议刷新后重试");
					result.Data.add(dialog);
				}
			}
		} catch (IOException e) {
			result.Data.add(new DialogTipsDTO("文件解压失败，建议检查一下压缩包是否损坏"));
		} catch (Exception e) {
			result.Data.add(new DialogTipsDTO("文本文件格式可能有问题，建议重新导出一份后重试"));
		} finally {
			EIASApplication.isImporting = false;
			FileUtil.delDir(output);
		}
		return result;
	}

	// }}

	// {{ 资源补发

	public static final String KEY_ADDITIONAL = "additional";
	public static final String KEY_ADDITIONAL_RESOURCE = "additionalResource";

	/**
	 * 
	 * @param taskNum
	 * @return
	 */
	public static AdditionalResource getAdditional(String taskNum) {
		AdditionalResource result = null;
		SpUtil sp = SpUtil.getInstance(KEY_ADDITIONAL);
		String dataStr = sp.getString(KEY_ADDITIONAL_RESOURCE, "");
		if (dataStr.length() > 0) {
			AdditionalResource[] temp = JSONHelper.parseArray(dataStr,
					AdditionalResource.class);
			for (AdditionalResource item : temp) {
				if (item.TaskNum.equals(taskNum)) {
					result = item;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param resource
	 */
	public static void saveAdditional(String taskNum, String resourceId,
			boolean isRemove) {
		resourceId = resourceId.replaceAll(";", "");
		ArrayList<AdditionalResource> datas = null;
		SpUtil sp = SpUtil.getInstance(KEY_ADDITIONAL);
		String dataStr = sp.getString(KEY_ADDITIONAL_RESOURCE, "");
		if (dataStr.equals("[null]") || dataStr.length() <= 0) {
			dataStr = "";
			datas = new ArrayList<AdditionalResource>();
		} else {
			datas = (ArrayList<AdditionalResource>) JSONHelper.parseCollection(
					dataStr, ArrayList.class, AdditionalResource.class);
		}
		AdditionalResource exist = getAdditional(taskNum);
		// 不存在
		if (exist == null) {
			exist = new AdditionalResource();
			exist.TaskNum = taskNum;
			exist.Resources += resourceId + ";";
			datas.add(exist);
		} else {
			for (AdditionalResource additionalResource : datas) {
				if (additionalResource.TaskNum.equals(taskNum)
						|| additionalResource.TaskNum == taskNum) {
					if (isRemove) {
						if (additionalResource.Resources.contains(resourceId)) {
							additionalResource.Resources = additionalResource.Resources
									.replace(resourceId, "");
						}
					} else {
						if (!additionalResource.Resources.contains(resourceId)) {
							additionalResource.Resources += resourceId + ";";
						}
					}
				}
			}
		}
		sp.putString(KEY_ADDITIONAL_RESOURCE,
				JSONHelper.toJSON(datas.toArray()));
	}

	/**
	 * 
	 * @param resource
	 */
	public static void removeAdditional(String taskNum) {
		ArrayList<AdditionalResource> datas = null;
		SpUtil sp = SpUtil.getInstance(KEY_ADDITIONAL);
		String dataStr = sp.getString(KEY_ADDITIONAL_RESOURCE, "");
		if (dataStr.equals("[null]") || dataStr.length() <= 0) {
			dataStr = "";
			datas = new ArrayList<AdditionalResource>();
		} else {
			datas = (ArrayList<AdditionalResource>) JSONHelper.parseCollection(
					dataStr, ArrayList.class, AdditionalResource.class);
		}
		for (AdditionalResource deleteItem : datas) {
			if (deleteItem.TaskNum.equals(taskNum)
					|| deleteItem.TaskNum == taskNum) {
				datas.remove(deleteItem);
				break;
			}
		}
		sp.putString(KEY_ADDITIONAL_RESOURCE,
				JSONHelper.toJSON(datas.toArray()));
	}

	/**
	 * 提交任务中
	 */
	public static void additionalResource(TaskInfo submitTaskInfo) {
		AdditionalResource temp = getAdditional(submitTaskInfo.TaskNum);
		if (temp == null || temp.Resources.length() <= 0) {
			ToastUtil.longShow(EIASApplication.getInstance(), "请先添加补发文件");
		} else {
			// 设置任务参数
			HashMap<String, Object> para = new HashMap<String, Object>();
			para.put("taskInfo", submitTaskInfo);
			para.put("additional", true);
			// 设置后台运行的任务
			BackgroundServiceTask task = new BackgroundServiceTask(
					MainService.TASK_SUMIT, para);
			// 添加到任务池中
			MainService.setTask(task);
		}
	}

	// }}

	// {{ 点击任务时，如果没有任务子项，就把有默认值的勘察子项填充到任务子项中

	/**
	 * 
	 * @param taskInfo任务信息不需要包含子项
	 */
	public static void setItemDefaultValue(TaskInfo taskInfo) {
		TaskDataItem taskDataItem = new TaskDataItem();
		Cursor itemCur = null;
		if (taskInfo.IsNew) {
			itemCur = taskDataItem.onSelect(null, " TaskID = " + taskInfo.ID);
		} else {
			itemCur = taskDataItem.onSelect(null, " TaskID = "
					+ taskInfo.TaskID);
		}
		// 判断当前任务是否有子项 如果没有，才把有默认值的勘察子项填充到任务子项中
		if (itemCur == null || !itemCur.moveToNext()) {
			DataFieldDefine field = new DataFieldDefine();
			Cursor fieldCur = field.onSelect(null, " DDID = " + taskInfo.DDID
					+ " and value != 'null' and length(value) > 0");
			if (fieldCur != null) {
				ArrayList<DataFieldDefine> lstField = new ArrayList<DataFieldDefine>();
				while (fieldCur.moveToNext()) {
					DataFieldDefine tempField = new DataFieldDefine();
					tempField.setValueByCursor(fieldCur);
					lstField.add(tempField);
				}
				ArrayList<TaskCategoryInfo> listCategory = geTaskCategoryInfos(taskInfo);
				for (DataFieldDefine item : lstField) {
					if (item.Value != null && !item.Value.equals("null")
							&& item.Value.length() > 0) {
						TaskDataItem tempItem = new TaskDataItem();
						// 未提交之前是Android端TaskCategoryInfo的自增ID,提交后是后台的TaskCategoryInfo的自增ID
						tempItem.BaseCategoryID = getBaseCategoryID(
								listCategory, item.CategoryID);
						// 未提交之前是0 提交后是后台OriginalItem的自增ID
						tempItem.BaseID = 0;
						// 提交和未提交后都是后台的DataCategoryDefine的自增ID
						tempItem.CategoryID = item.CategoryID;
						tempItem.IOrder = item.IOrder;
						tempItem.Name = item.Name;
						tempItem.Value = item.Value.replaceAll("null", "");
						tempItem.TaskID = taskInfo.IsNew ? taskInfo.ID
								: taskInfo.TaskID;
						tempItem.onInsert();
					}
				}
				updateFinishCount(taskInfo);
			}
		}
	}

	private static void updateFinishCount(TaskInfo taskInfo) {
		// ResultInfo<ArrayList<DataCategoryDefine>> dataCategory =
		// getAllCategories(taskInfo);
		TaskInfo tempTaskInfo = getLocTaskInfoComplete(taskInfo);
		for (TaskCategoryInfo category : tempTaskInfo.Categories) {
			int finishCount = 0;
			for (TaskDataItem item : category.Items) {
				if (item.Value != null && !item.Value.equals("null")
						&& item.Value.length() > 0) {
					finishCount++;
				}
			}
			category.DataDefineFinishCount = finishCount;
			category.onUpdate(" ID = " + category.ID);
		}
	}

	private static ArrayList<TaskCategoryInfo> geTaskCategoryInfos(
			TaskInfo taskInfo) {
		ArrayList<TaskCategoryInfo> result = new ArrayList<TaskCategoryInfo>();
		TaskCategoryInfo tempCategory = new TaskCategoryInfo();
		Cursor categoryCur = null;
		if (taskInfo.IsNew) {
			categoryCur = tempCategory.onSelect(null, " TaskID = "
					+ taskInfo.ID);
		} else {
			categoryCur = tempCategory.onSelect(null, " TaskID = "
					+ taskInfo.TaskID);
		}
		if (categoryCur != null) {
			while (categoryCur.moveToNext()) {
				tempCategory = new TaskCategoryInfo();
				tempCategory.setValueByCursor(categoryCur);
				result.add(tempCategory);
			}
		}
		return result;
	}

	/**
	 * 根据后台的DataCategoryDefine的自增ID 获取Android端TaskCategoryInfo的自增ID
	 * 
	 * @param data指定任务下的TaskCategoryInfo列表
	 * @param key后台DataCategoryDefine的自增ID
	 * @return
	 */
	private static int getBaseCategoryID(ArrayList<TaskCategoryInfo> data,
			int key) {
		int result = 0;
		for (TaskCategoryInfo item : data) {
			if (item.CategoryID == key) {
				result = item.BaseCategoryID;
				break;
			}
		}
		return result;
	}

	// }}

	// {{ 删除已经完成报告的任务

	/**
	 * 删除已经完成报告的任务
	 * 
	 * @param taskInfo
	 */
	public static void removeTaskResource(TaskInfo taskInfo) {
		if (taskInfo.Status == TaskStatus.Done && taskInfo.InworkReportFinish) {
			FileUtil.delDir(EIASApplication.projectRoot + taskInfo.TaskNum);
			FileUtil.delDir(EIASApplication.thumbnailRoot + taskInfo.TaskNum);
		}
	}

	// }}

	/***
	 * 
	 * @author kevin
	 * @date 2015-9-18 下午2:21:39
	 * @Description: 同步远程已完成任务子项值
	 * @param currentSelectTask
	 * @return ResultInfo<Integer> 返回类型
	 * @version V1.0
	 */
	public static ResultInfo<Integer> syncDoneTaskInfo(
			TaskInfo currentSelectTask) {
		ResultInfo<Integer> result = null;
		GetFinishTaskInfo getFinishTaskInfo = new GetFinishTaskInfo();
		ResultInfo<List<TaskDataItem>> dataItems = null;
		dataItems = getFinishTaskInfo.request(EIASApplication.getCurrentUser(),
				currentSelectTask);
		// 对比 本地 TaskDataItem 更新服务器数据
		result = TaskDataWorker.updataTaskDataItems(dataItems.Data);

		return result;
	}

	/***
	 * @author kevin
	 * @date 2015-10-15 下午3:32:45
	 * @Description: 图片上传失败，请求资源库，回滚资源库图片，删除多余引用。
	 * @return ResultInfo<Boolean> 返回类型
	 */
	public static ResultInfo<Boolean> deleteRelationResource(
			UserInfo currenUser, TaskInfo taskInfo,
			Map<String, String> resInfoMap) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		DeleteRelationResourceTask httpTask = new DeleteRelationResourceTask();
		// 资源ID 列表
		List<String> resIDs = new ArrayList<String>();
		// 获取map 中的所有资源ID List
		for (String key : resInfoMap.keySet()) {
			String resIDstr = resInfoMap.get(key);
			String[] resID = resIDstr.split(";");
			for (String id : resID) {
				resIDs.add(id);
			}
		}
		// 循环请求删除资源库ID
		for (String resId : resIDs) {
			result = httpTask.request(currenUser.ResourceLibraryDomainName,
					taskInfo, resId);
		}

		return result;
	}

	/***
	 * 根据任务DDID 和任务对应的勘察表版本号， 比对服务器勘察表版本号是否一查
	 * 
	 * @param ddid
	 *            勘察表DDID
	 * @param dataDefinesVersion
	 *            对应勘察表版本号
	 * @return
	 */
	public static ResultInfo<Boolean> checkServerDataDefinesVersion(int ddid) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		GetHomeInfoTask getHomeInfoTask = new GetHomeInfoTask();
		Cursor cursor = new DataDefine().onSelect(new String[] { "Version" },
				"DDID =" + ddid);
		int version = -1;
		if (cursor != null && cursor.getColumnCount() > 0) {
			while (cursor.moveToNext())
				version = cursor.getInt(cursor.getColumnIndex("Version"));
		}

		// 发起请求，请求当前任务信息，Other 中有勘察表列表
		ResultInfo<UserTaskInfo> resultUser = getHomeInfoTask
				.request(EIASApplication.getCurrentUser());
		if (resultUser.Success && resultUser.Others != null) {
			try {
				@SuppressWarnings("unchecked")
				ArrayList<DataDefine> defines = (ArrayList<DataDefine>) resultUser.Others;
				for (DataDefine define : defines) {
					if (define.DDID != ddid) { // 不是本勘察表继续循环
						result.Data = false;
						result.Message = "服务器不存在这个勘察表";
						continue;
					}
					if (define.Version == version) { // 对比勘察表的版本号是否相同
						result.Data = true;
						result.Message = "";
					} else {
						result.Data = false;
						result.Message = "任务勘察表版本号与服务器不一致，请更新同步勘察表";
					}
					break;
				}
			} catch (Exception e) {
				result.Data = false;
				result.Message = "请检查网络！";
				DataLogWorker.createDataLog(EIASApplication.getCurrentUser(),
						"获取用户任务信息的勘察表数据错误", OperatorTypeEnum.Other,
						LogType.Exection);
			}
		} else {
			result.Data = false;
			result.Message = resultUser.Message;
		}
		return result;
	}

	/***
	 * 粘贴复制逻辑， 时间类型不粘贴 用户名， 电话不粘贴， 用当前用户数据
	 * 
	 * @param itemDefine
	 * @param copiedItem
	 * @return
	 */
	private static String itemTypeSwitch(DataFieldDefine itemDefine,
			TaskDataItem copiedItem) {
		String Value = "";
		switch (itemDefine.ItemType) {
		case DateTimeValue:// 时间类型
			Value = "";
			break;
		case Coordinate:// 坐标类型
			Value = "";
			break;
		case Map:// 地图类型
			Value = "";
			break;
		case UserName:// 用户名
			Value = EIASApplication.getCurrentUser().Name;
			break;
		case UserTel:// 用户电话
			Value = EIASApplication.getCurrentUser().Mobile;
			break;
		default:// 其他类型 直接复制
			Value = copiedItem.Value;
			break;
		}
		return Value;
	}

	/***
	 * 保存状态到本地SP
	 * 
	 * @param taskStatus
	 * @param sortType
	 */
	public static void saveSortStatus(TaskStatus taskStatus, SortType sortType) {
		SpUtil sp = SpUtil.getInstance(EIASApplication.SORT_STATUS_SP);
		// 保存状态
		sp.putString(taskStatus.getName(), sortType.getFieldName() + ","
				+ sortType.isAsc());

	}

	/***
	 * 获取上次存储的 排序
	 * 
	 * @param taskStatus
	 * @return
	 */
	public static SortType getSortStatus(TaskStatus taskStatus) {
		SortType sortType = null;
		SpUtil sp = SpUtil.getInstance(EIASApplication.SORT_STATUS_SP);
		String sortStr = sp.getString(taskStatus.getName(), "");
		if (!sortStr.equals("")) {
			String temp[] = sortStr.split(",");
			sortType = SortType.getTypeByName(temp[0]);
			if (sortType != null) {
				boolean isAsc = "true".equals(temp[1]);
				sortType.setAsc(isAsc);
			}
		}
		return sortType;
	}

	/***
	 * 同步报告已完成任务状态 删除任务图片资源
	 * 
	 * @param currentUser
	 */
	public static void synchroReportIsFinish(UserInfo currentUser) {
		// 获取最后的报告日期同步时间
		ResultInfo<String> date = TaskDataWorker
				.getLastDateByReport(currentUser);
		if (date.Success) {
			// 获取当前查询结果日期后的报告
			GetFinishInworkReportTask task = new GetFinishInworkReportTask();
			ResultInfo<ArrayList<String>> tasklst = task.request(currentUser,
					date.Data);
			if (tasklst.Success && tasklst.Data != null
					&& tasklst.Data.size() > 0) {
				for (String item : tasklst.Data) {
					String[] temp = item.split(",");
					if (temp.length == 2) {
						// 修改数据库状态
						ResultInfo<TaskInfo> result = TaskDataWorker
								.synchroReportInfo(temp[0], temp[1]);
						if (result.Success && result.Data != null) {
							// 删除任务资源
							removeTaskResource(result.Data);
							DataLogOperator.taskReportFinalsh("删除任务（" + temp[0]
									+ "）本地资源成功");
						} else {
							DataLogOperator.taskReportFinalsh("保存报告完成日期失败（"
									+ temp[0] + "）->" + result.Message);
						}
					}
				}
			}
		}
	}
}
