package com.yunfang.eias.logic;

import java.util.ArrayList;

import android.content.Context;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.dto.VersionDTO;
import com.yunfang.eias.http.task.CheckVersionTask;
import com.yunfang.eias.http.task.GetDataDefineDataTask;
import com.yunfang.eias.http.task.GetHomeInfoTask;
import com.yunfang.eias.http.task.GetReturnTask;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.UserTaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.NetWorkUtil;

/**
 * 主界面逻辑操作类
 * 
 * @author gorson
 * 
 */
public class HomeOperator {

	/**
	 * 获取用户任务信息
	 * 
	 * @return
	 */
	public static UserTaskInfo getCurrentTaskInfos() {
		UserTaskInfo result = new UserTaskInfo();

		result.NonReceivedNormal = 20;
		result.NonReceivedUrgent = 3;
		result.NonReceivedTotals = result.NonReceivedNormal
				+ result.NonReceivedUrgent;

		result.ReceivedNormal = 15;
		result.ReceivedUrgent = 1;
		result.ReceivedTotals = result.ReceivedNormal + result.ReceivedUrgent;

		return result;
	}

	/**
	 * 获取当前所用的网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetType(Context context) {
		String type = NetWorkUtil.getNetworkType().getName();
		return type;
	}

	/**
	 * 同步可以同步的所有勘察匹配表完整信息：分类项信息、分类项下属性信息列表
	 * 
	 * @param currentUserInfo
	 * @param dataDefines
	 * @return
	 */
	public static ResultInfo<ArrayList<DataDefine>> fillAllDataDefines(
			UserInfo currentUserInfo, ArrayList<DataDefine> dataDefines) {
		ResultInfo<ArrayList<DataDefine>> result = new ResultInfo<ArrayList<DataDefine>>();
		result.Data = new ArrayList<DataDefine>();
		if (dataDefines != null && dataDefines.size() > 0) {
			ResultInfo<Boolean> tempResult = new ResultInfo<Boolean>();
			for (DataDefine define : dataDefines) {
				tempResult = fillOneDataDefine(currentUserInfo, define);
				if (tempResult.Success && tempResult.Data) {
					result.Message = tempResult.Message;
					result.Data.add(define);
				}
			}
		}
		return result;
	}

	/**
	 * 同步某个勘察匹配表完整信息：分类项信息、分类项下属性信息列表
	 * 
	 * @param currentUserInfo
	 *            :当前用户信息
	 * @param dataDefine
	 *            :当前勘察表信息，拿ID做交互
	 * @return
	 */
	public static ResultInfo<Boolean> fillOneDataDefine(
			UserInfo currentUserInfo, DataDefine dataDefine) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;

		try {
			GetDataDefineDataTask task = new GetDataDefineDataTask();
			ResultInfo<DataDefine> data = task.request(currentUserInfo,
					dataDefine);
			if (data.Success && data.Data != null) {
				ResultInfo<Long> fillInfo = DataDefineWorker
						.fillCompleteDataDefindInfos(data.Data);
				if (fillInfo.Data > 0) {
					result.Data = true;
					DataLogOperator.dataDefineDataSynchronization(dataDefine,
							"");
				} else {
					result.Data = false;
					result.Success = true;
					result.Message = "勘察表数据写入设备出错";
					DataLogOperator.dataDefineDataSynchronization(dataDefine,
							result.Message);
				}
			} else {
				result.Data = false;
				result.Success = data.Success;
				result.Message = (data.Message.trim().length() > 0 ? data.Message
						: "勘察表数据获取失败");
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
		}

		return result;
	}

	/**
	 * 得到用户信息
	 * 
	 * @return
	 */
	public static UserInfo getUsrInfo() {
		UserInfo info = new UserInfo();

		return info;
	}

	/**
	 * 获取HomeFragment数据信息
	 * 
	 * @param userInfo
	 *            ：当前用户信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ResultInfo<UserTaskInfo> getHomeData(UserInfo userInfo) {
		ResultInfo<UserTaskInfo> result = new ResultInfo<UserTaskInfo>();
		try {
			if (!EIASApplication.IsOffline&&EIASApplication.IsNetworking) {
				GetHomeInfoTask task = new GetHomeInfoTask();
				result = task.request(userInfo);
				if (result.Success && result.Others != null) {
					//服务端勘察表
					ArrayList<DataDefine> returnDefines = (ArrayList<DataDefine>) result.Others;
					//本地服务器勘察表
					ResultInfo<ArrayList<DataDefine>> localDefines = DataDefineWorker
							.queryDataDefineByCompanyID(userInfo.CompanyID);
					ArrayList<DataDefine> defines = new ArrayList<DataDefine>();
					if (localDefines.Success) {
						if (localDefines.Data != null
								&& localDefines.Data.size() > 0) {
							boolean isDeal = false;
							for (DataDefine define : returnDefines) {
								isDeal = false;
								for (DataDefine localDefine : localDefines.Data) {
									if (define.DDID == localDefine.DDID) {
										if (define.Version != localDefine.Version) {
											defines.add(define);
										}
										isDeal = true;
										break;
									}
								}
								if (!isDeal) {
									defines.add(define);
								}
							}
							result.Others = defines;
							//2016-2-19 删除服务器返回的勘察表中不存在的勘察表
							DatadefinesOperator.deleteUnDealDefines(localDefines.Data,returnDefines);
						}
					}
				}
			} else {
				result = TaskDataWorker.queryUserInfo(userInfo);
				result.Others=new ArrayList<DataDefine>();
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = result.Message.length() > 0 ? result.Message : "获取失败！";
		}
		return result;
	}

	/**
	 * 未使用方法 2016-3-18
	 * 同步已经完成报告的任务信息　
	 *//*
	public static void synchroReportInfo(final UserInfo userInfo) {
		if (!EIASApplication.IsOffline) {
			new Thread() {
				public void run() {
					try {
						// 获取最后的报告日期同步时间
						ResultInfo<String> date = TaskDataWorker
								.getLastDateByReport(userInfo);
						if (date.Success) {
							// 获取当前查询结果日期后的报告
							GetFinishInworkReportTask task = new GetFinishInworkReportTask();
							ResultInfo<ArrayList<String>> tasklst = task
									.request(userInfo, date.Data);
							if (tasklst.Success && tasklst.Data != null
									&& tasklst.Data.size() > 0) {
								for (String item : tasklst.Data) {
									String[] temp = item.split(",");
									if (temp.length == 2) {
										TaskDataWorker.synchroReportInfo(
												temp[0], temp[1]);
									}
								}
							}
						}
					} catch (Exception e) {
						DataLogOperator.other("synchroReportInfo=>"
								+ e.getMessage());
					}
				}
			}.start();
		}
	}*/

	/***
	 * 刷新待提交列表同时，获取服务器最新版本号
	 * 和是否有勘察表需要同步，
	 * 
	 * @param currentUser
	 */
	public static boolean checkVersionAndDefinVersion(UserInfo currentUser) {
		boolean hasUpdateDatafine=false;
		try {
			// 获取需要更细的勘察表信息
			ResultInfo<UserTaskInfo> userTaskIfInfoResult = HomeOperator
					.getHomeData(currentUser);
			@SuppressWarnings("unchecked")
			ArrayList<DataDefine> defines = (ArrayList<DataDefine>) userTaskIfInfoResult.Others;
			if (ListUtil.hasData(defines)) {// 是否有未同步的勘察表
				hasUpdateDatafine= true;// 勘察表需要同步
			}
			checkoutVersion();//更新了缓存,和数据库中的版本号信息
		} catch (Exception e) {
			hasUpdateDatafine=false;
		}
		return hasUpdateDatafine;
	}

	/**
	 * 检查客户端是否最新版本
	 */
	private static void checkoutVersion() {
		CheckVersionTask taskHttp = new CheckVersionTask();
		ResultInfo<VersionDTO> checkResult = taskHttp.request(EIASApplication.getCurrentUser());
		if (checkResult.Success && checkResult.Data != null) {// 有新版本需要更新
			VersionDTO dto = (VersionDTO)checkResult.Data;
			EIASApplication.version.ServerReleasedTime = dto.LastUpdateTime;
			EIASApplication.version.ServerVersionCode = dto.VersionCode;
			EIASApplication.version.ServerVersionName = dto.VersionName;
			EIASApplication.version.ServerVersionDescription = dto.UpdateContent;
		}

	}

	/***
	 * 
	 * @return 暂停了的任务编号数组
	 */
	public static String[] getReturnTaskInfo() {
		String[] returnTaskNum = null;
		ResultInfo<String> result = new ResultInfo<String>();
		// 发起网络请求后去暂停了的任务信息
		GetReturnTask getReturnTask = new GetReturnTask();
		// 发起网络请求
		result = getReturnTask.getReturnTadkInfo(EIASApplication
				.getCurrentUser());
		if (result.Success) {
			if (result.Data.contains(";")) {
				returnTaskNum = result.Data.split(";");
			}
		}
		return returnTaskNum;
	}
}
