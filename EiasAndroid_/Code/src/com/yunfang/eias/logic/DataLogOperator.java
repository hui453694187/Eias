package com.yunfang.eias.logic;

import java.util.ArrayList;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.LogType;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.http.task.UploadLogInfoTask;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.DataLog;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataLogWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.ListUtil;

/**
 * 
 * 项目名称：外采系统 类名称：DataLogOperator 类描述：日志操作逻辑类 创建人：贺隽 创建时间：2014-6-19 10:10 修改人：贺隽
 * 修改时间：2014-6-23 10:10
 * 
 * @version 1.0.0.2
 */
public class DataLogOperator {

	private static String unKownError = "未知错误";

	// {{
	/***
	 * 
	 * @author kevin
	 * @date 2015-11-12 下午4:16:55
	 * @Description: 上传本地未捕获异常日志 
	 * @param userInfo  当前用户  
	 * @return void    返回类型 
	 */
	public static void upLoadUnCrashExection4Db(UserInfo userInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		//获取未捕获异常日志
		ResultInfo<ArrayList<DataLog>> dataLags = DataLogWorker.getDataLogsByType(userInfo,null);
		ArrayList<DataLog> successDataLogs = new ArrayList<DataLog>();
		if (dataLags.Success && ListUtil.hasData(dataLags.Data)) {//查询成功，并且有数据
			// 提交数据的task
			UploadLogInfoTask setLogTask = new UploadLogInfoTask();
			if (EIASApplication.IsNetworking) {
				for (DataLog dataLog : dataLags.Data) {
					ResultInfo<Boolean> uploadInfo = setLogTask.request(userInfo, dataLog);
					if (uploadInfo.Success && uploadInfo.Data) {
						// TODO 提交数据成功 删除本条记录
						successDataLogs.add(dataLog);
						dataLog.delete("ID='"+dataLog.ID+"'");
					}
				}
				
			}
		} else {
			result.Success = false;
			result.Message = "数据库没有未捕获异常信息！";
		}

	}

	/**
	 * 获取日志列表数据
	 * 
	 * @param pageIndex
	 *            :当前第几页
	 * @param pageSize
	 *            :每页显示多少
	 * @param queryStr
	 *            ：查询条件
	 * @param logType
	 *            :日志类型
	 * @return
	 */
	public static ResultInfo<ArrayList<DataLog>> getLogs(int pageIndex, int pageSize, String queryStr, int logTypeIndex) {
		ResultInfo<ArrayList<DataLog>> result = new ResultInfo<ArrayList<DataLog>>();
		try {
			result = DataLogWorker.getDataLogs(pageIndex, pageSize, queryStr.trim(), EIASApplication.getCurrentUser(), logTypeIndex);
		} catch (Exception e) {
			result.Success = false;
			result.Message = "获取日志列表数据失败";
		}
		return result;
	}

	/**
	 * 删除当前用户的日志信息
	 * 
	 * @return
	 */
	public static ResultInfo<Boolean> deleteCurrentUserLog() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			if (EIASApplication.getCurrentUser() != null) {
				DataLogWorker.deleteByUserId(EIASApplication.getCurrentUser().Account);
				result.Data = true;
				result.Success = true;
				result.Message = "删除日志成功";
			}
		} catch (Exception e) {
			result.Data = false;
			result.Success = false;
			result.Message = "删除日志失败 ";
		}
		return result;
	}

	// }}

	// {{ 同步相关记录

	/**
	 * 任务数据匹配
	 * 
	 * @param source
	 *            :选择任务
	 * @param target
	 *            :查找后选中的任务
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskDataMatching(TaskInfo source, TaskInfo target, String errorInfo) {
		if (source != null && target != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + source.TaskNum + "]匹配了任务[" + target.TaskNum + "]出错,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + source.TaskNum + "]匹配了任务[" + target.TaskNum + "]中的数据信息";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskDataMatching,LogType.UserOperation);
		}
	}

	/**
	 * 任务同步
	 * 
	 * @param source
	 *            :选择任务
	 * @param target
	 *            :查找后选中的任务
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskDataSynchronization(TaskInfo source, String errorInfo) {
		if (source != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + source.TaskNum + "]同步失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + source.TaskNum + "]同步成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskDataSynchronization,LogType.UserOperation);
		}
	}

	/**
	 * 勘察数据同步
	 * 
	 * @param dataDefine
	 *            :单独的勘察数据
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void dataDefineDataSynchronization(DataDefine dataDefine, String errorInfo) {
		if (dataDefine != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "勘察表中的[" + dataDefine.Name + "]数据同步失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "勘察表中的[" + dataDefine.Name + "]数据同步完成";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.DataDefineDataSynchronization,LogType.UserOperation);
		}
	}

	// }}

	// {{ 任务相关记录

	/**
	 * 新建任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskCreated(String taskNum, String errorInfo) {
		if (taskNum.length() > 0) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "本地任务[" + taskNum + "]创建失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "本地任务[" + taskNum + "]创建成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskCreate,LogType.UserOperation);
		}
	}

	/**
	 * 删除任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskDeleted(TaskInfo data, String errorInfo) {
		if (data != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "本地任务[" + data.TaskNum + "]删除失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "本地任务[" + data.TaskNum + "]删除成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskDelete,LogType.UserOperation);
		}
	}

	/**
	 * 领取任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskReceive(TaskInfo data, String errorInfo) {
		if (data != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + data.TaskNum + "]领取失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + data.TaskNum + "]领取成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskReceive,LogType.UserOperation);
		}
	}

	/**
	 * 收费任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskFeeModify(TaskInfo data, String errorInfo) {
		if (data != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + data.TaskNum + "]修改费用失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + data.TaskNum + "]修改费用为<" + data.Fee + ">收据号为<" + data.ReceiptNo + ">";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskFeeModify,LogType.UserOperation);
		}
	}

	/**
	 * 暂停任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskPause(TaskInfo data, String errorInfo) {
		if (data != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + data.TaskNum + "]暂停失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + data.TaskNum + "]暂停成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskPause,LogType.UserOperation);
		}
	}

	/**
	 * 提交任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskSubmit(TaskInfo data, String errorInfo) {
		if (data != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + data.TaskNum + "]提交失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + data.TaskNum + "]提交成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskSubmit,LogType.UserOperation);
		}
	}

	/**
	 * 重新提交任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskReSubmit(TaskInfo data, String errorInfo) {
		if (data != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + data.TaskNum + "]重新提交失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + data.TaskNum + "]重新提交成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskReSubmit,LogType.UserOperation);
		}
	}

	/**
	 * 回退任务记录
	 * 
	 * @param data
	 *            :任务信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskRollback(TaskInfo data, String errorInfo) {
		if (data != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + data.TaskNum + "]回退失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + data.TaskNum + "]回退成功";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskReSubmit,LogType.UserOperation);
		}
	}

	/**
	 * 复制任务记录
	 * 
	 * @param copyData
	 *            :复制的任务信息
	 * @param pastedData
	 *            :粘贴的任务信息
	 * @param categories
	 *            :选择的分类项
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskDataCopy(TaskInfo copyData, TaskInfo pastedData, ArrayList<TaskCategoryInfo> categories, String errorInfo) {
		if (copyData != null && pastedData != null && ListUtil.hasData(categories)) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + copyData.TaskNum + "]复制失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + copyData.TaskNum + "]成功复制了<" + categories.size() + ">个分类项到任务[" + pastedData.TaskNum + "]中,包含";
				for (TaskCategoryInfo item : categories) {
					concent += item.RemarkName + "、";
				}
				concent = concent.substring(0, concent.length() - 1);
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskDataCopy,LogType.UserOperation);
		}
	}

	/**
	 * 复制任务到新建任务中记录
	 * 
	 * @param copyData
	 *            :复制的任务信息
	 * @param pastedData
	 *            :粘贴的任务信息
	 * @param categories
	 *            :选择的分类项
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskDataCopyToNew(TaskInfo copyData, TaskInfo pastedData, ArrayList<TaskCategoryInfo> categories, String errorInfo) {
		if (copyData != null && pastedData != null && ListUtil.hasData(categories)) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + copyData.TaskNum + "]复制到新建任务失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + copyData.TaskNum + "]成功复制了<" + categories.size() + ">个分类项到新建任务[" + pastedData.TaskNum + "]中,包含";
				for (TaskCategoryInfo item : categories) {
					concent += item.RemarkName + "、";
				}
				concent = concent.substring(0, concent.length() - 1);
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskDataCopyToNew,LogType.UserOperation);
		}
	}

	// }}

	// {{ 分类项相关

	/**
	 * 分类项创建
	 * 
	 * @param task
	 *            :任务信息
	 * @param category
	 *            :分类项信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void categoryDefineCreated(TaskInfo task, TaskCategoryInfo category, String errorInfo) {
		if (task != null && category != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + task.TaskNum + "]创建分类项失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + task.TaskNum + "]成功创建了<" + category.RemarkName + ">分类项";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.CategoryDefineCreated,LogType.UserOperation);
		}
	}

	/**
	 * 复制分类项
	 * 
	 * @param task
	 *            :任务信息
	 * @param copyCategory
	 *            :复制分类项信息
	 * @param pastedCategory
	 *            :粘贴分类项信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void categoryDefineDataCopy(TaskInfo task, TaskCategoryInfo copyCategory, TaskCategoryInfo pastedCategory, String errorInfo) {
		if (task != null && copyCategory != null && pastedCategory != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + task.TaskNum + "]复制分类项失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + task.TaskNum + "]成功复制了<" + copyCategory.RemarkName + ">分类项粘贴到了<" + pastedCategory.RemarkName + ">分类项中";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.CategoryDefineDataCopy,LogType.UserOperation);
		}
	}

	/**
	 * 复制到新建分类项中
	 * 
	 * @param task
	 *            :任务信息
	 * @param copyCategory
	 *            :复制分类项信息
	 * @param pastedCategory
	 *            :粘贴分类项信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void categoryDefineDataCopyToNew(TaskInfo task, TaskCategoryInfo copyCategory, TaskCategoryInfo pastedCategory, String errorInfo) {
		if (task != null && copyCategory != null && pastedCategory != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + task.TaskNum + "]复制新建的分类项失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + task.TaskNum + "]成功复制了<" + copyCategory.RemarkName + ">分类项粘贴到了新建的<" + pastedCategory.RemarkName + ">分类项中";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.CategoryDefineDataCopyToNew,LogType.UserOperation);
		}
	}

	/**
	 * 清空分类项
	 * 
	 * @param task
	 *            :任务信息
	 * @param category
	 *            :分类项信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void categoryDefineDataReset(TaskInfo task, TaskCategoryInfo sourceCategory, String errorInfo) {
		if (task != null && sourceCategory != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + task.TaskNum + "]分类项<" + sourceCategory.RemarkName + ">清空失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + task.TaskNum + "]成功清空了<" + sourceCategory.RemarkName + ">分类项下面的值";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.CategoryDefineDataReset,LogType.UserOperation);
		}
	}

	/**
	 * 修改分类项的名称
	 * 
	 * @param task
	 *            :任务信息
	 * @param category
	 *            :分类项信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void categoryDefineNameModified(TaskInfo task, TaskCategoryInfo sourceCategory, String oldName, String errorInfo) {
		if (task != null && sourceCategory != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + task.TaskNum + "]分类项<" + sourceCategory.RemarkName + ">名称修改失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + task.TaskNum + "]分类项名称成功的由<" + oldName + ">修改为<" + sourceCategory.RemarkName + ">";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.CategoryDefineNameModified,LogType.UserOperation);
		}
	}

	/**
	 * 删除分类项
	 * 
	 * @param task
	 *            :任务信息
	 * @param category
	 *            :分类项信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void categoryDefineDeleted(TaskInfo task, TaskCategoryInfo sourceCategory, String errorInfo) {
		if (task != null) {
			String concent = "";
			if (errorInfo == null)
				errorInfo = unKownError;
			if (errorInfo.length() > 0) {
				concent = "任务[" + task.TaskNum + "]删除分类项<" + sourceCategory.RemarkName + ">失败,详细信息如下:\n" + errorInfo;
			} else {
				concent = "任务[" + task.TaskNum + "]成功删除了名称为<" + sourceCategory.RemarkName + ">的分类项";
			}
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.CategoryDefineDeleted,LogType.UserOperation);
		}
	}

	// }}

	// {{ 用户

	/**
	 * 用户登录
	 * 
	 * @param concent
	 *            :记录内容
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void userLogin(Boolean isOffline, String errorInfo) {
		String concent = "";
		if (errorInfo == null)
			errorInfo = unKownError;
		if (errorInfo.length() > 0) {
			concent = "登录失败,详细信息如下:\n" + errorInfo;
		} else {
			concent = "登录成功";
		}
		if (isOffline) {
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), "离线" + concent, OperatorTypeEnum.UserLogin,LogType.UserOperation);
		} else {
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), "在线" + concent, OperatorTypeEnum.UserLogin,LogType.UserOperation);
		}
	}

	/**
	 * 用户退出
	 * 
	 * @param concent
	 *            :记录内容
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void userLogout(String errorInfo) {
		String concent = "";
		if (errorInfo == null)
			errorInfo = unKownError;
		if (errorInfo.length() > 0) {
			concent = "登出失败,详细信息如下:\n" + errorInfo;
		} else {
			concent = "登出成功";
		}
		DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.UserLogout,LogType.UserOperation);
	}

	// }}

	// {{记录文件上传和访问后台错误的日志

	/**
	 * 文件上传记录 成功只需要显示数量 失败每一个都需要记录
	 * 
	 * @param taskInfo
	 *            :上传的任务信息
	 * @param fileType
	 *            :文件类型 如图片、音频、视频
	 * @param count
	 *            :成功数量
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void fileUpload(TaskInfo taskInfo, String fileType, String count, String errorInfo) {
		String concent = "";
		if (errorInfo == null)
			errorInfo = unKownError;
		if (errorInfo.length() > 0) {
			concent = "上传文件失败,详细信息如下:\n" + errorInfo;
		} else {
			concent = "任务[" + taskInfo.TaskNum + "]成功上传了<" + count + ">个" + fileType + "文件";
		}
		DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.FileUpload,LogType.UserOperation);
	}

	/**
	 * 文件上传记录 成功只需要显示数量 失败每一个都需要记录
	 * 
	 * @param subInfo
	 *            :前缀信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void taskHttp(String subInfo, String errorInfo) {
		String concent = "";
		if (errorInfo == null)
			errorInfo = unKownError;
		if (errorInfo.length() > 0) {
			concent = subInfo + ",详细信息如下:\n" + errorInfo;
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.TaskHttp,LogType.UserOperation);
		}
	}

	// }}

	// {{ 版本更新
	/**
	 * 记录 版本更新
	 * 
	 * @param subInfo
	 *            :前缀信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void version(String errorInfo) {
		String concent = "";
		if (errorInfo == null)
			errorInfo = unKownError;
		if (errorInfo.length() > 0) {
			concent = "最新版本下载失败,详细信息如下:\n" + errorInfo;
		} else {
			concent = "已经从版本[" + EIASApplication.version.LocalVersionName + "]升级到[" + EIASApplication.version.ServerVersionName + "]成功";
		}
		DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), concent, OperatorTypeEnum.VersionUpdate,LogType.UserOperation);
	}

	// }}

	// {{ 其他异常
	/**
	 * 记录 其他异常
	 * 
	 * @param subInfo
	 *            :前缀信息
	 * @param errorInfo
	 *            :错误信息
	 */
	public static void other(String errorInfo) {
		if (errorInfo == null)
			errorInfo = unKownError;
		if (errorInfo.length() > 0) {
			DataLogWorker.createDataLog(EIASApplication.getCurrentUser(), "详细信息如下:\n" + errorInfo, OperatorTypeEnum.Other,LogType.UserOperation);
		}
	}
	// }}
}
