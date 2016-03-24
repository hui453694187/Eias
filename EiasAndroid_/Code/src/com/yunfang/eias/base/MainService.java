package com.yunfang.eias.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.yunfang.eias.dto.AdditionalResource;
import com.yunfang.eias.dto.TaskInfoDTO;
import com.yunfang.eias.enumObj.LogType;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.enumObj.TaskUploadStatusEnum;
import com.yunfang.eias.http.task.BackgroundServiceTask;
import com.yunfang.eias.http.task.GetResLibConfigTask;
import com.yunfang.eias.http.task.PushCoordinateTask;
import com.yunfang.eias.http.task.SubmitTaskInfoTask;
import com.yunfang.eias.http.task.UploadFile2ResourceTask;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataLogWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.framework.base.BaseBackgroundService;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DownLoadFileUtil;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * @author 贺隽
 * 
 */
public class MainService extends BaseBackgroundService {

	// {{ 服务处理的类型
	/**
	 * 提交任务
	 */
	public static final int TASK_SUMIT = 1;

	/**
	 * 重新启动任务
	 */
	public static final int RESTARTTASK_SUMIT = 2;

	/**
	 * 定时器
	 */
	public static final int TIMER_PUSH = 4;

	/**
	 * 推送坐标
	 */
	public static final int PUSH_LATLNG = 5;

	/**
	 * 下载最新版本
	 */
	public static final int DOWN_LAST_VERSION = 6;

	/**
	 * 连接断开信息提示
	 */
	public static final int CONNECTIONBACK_MSG = -1;

	/*
	 * 提交方法是否正在执行
	 */
	public static boolean THREADRUNING = false;

	// }}

	// {{ 相关属性

	// }}

	// {{ 后台处理方法

	@Override
	public void onCreate() {
		super.onCreate();
		if (mbackgroundHandler != null) {
			sendBroadcastByMainServerCreated();
			standbyLatlagDelayed();
		}
	}

	/**
	 * 主服务创建完成之后派发是事件通知可以启动 地图推送等一些操作
	 */
	private void sendBroadcastByMainServerCreated() {
		Intent intent = new Intent();
		intent.setAction(BroadRecordType.MAINSERVER_CREATED);
		sendBroadcast(intent);
	}

	@Override
	protected void handleBackgroundMessage(Message msg) {
		try {
			BackgroundServiceTask task = (BackgroundServiceTask) msg.obj;
			boolean additional = false;
			Map<String, Object> params = task.getTaskParam();
			/** 加上空值判断 ，防止抛出异常 */
			if (params != null && params.size() > 1
					&& params.containsKey("additional")) {
				additional = (boolean) params.get("additional");
			}
			switch (msg.what) {
			case TASK_SUMIT:
				boolean isCall = false;
				if (uploadTasks.size() == 1) {
					isCall = true;
				}
				if (isCall) {
					uploadTaskInfo(additional);
				}
				break;
			case RESTARTTASK_SUMIT:// 重新上传未上传完的任务
				if (uploadTasks.size() > 0) {
					uploadTaskInfo(additional);
				}
				break;
			case TIMER_PUSH:
				pushLatlagDelayed();
			case PUSH_LATLNG:
				setCoordinateTaskRequest(task);
				break;
			case DOWN_LAST_VERSION:
				downLoadFile();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.obj = "FAIL";
			msg.what = -100;
		}
	}

	// {{ 添加一个后台服务任务
	/**
	 * 添加一个后台任务到任务池
	 * 
	 * @param task
	 */
	public static void setTask(BackgroundServiceTask task) {
		if (beforeBackgroundHandler(task)) {
			// 建立任务
			Message msg = new Message();
			msg.what = task.getServiceTaskId();
			msg.obj = task;
			mbackgroundHandler.sendMessage(msg);
		}
	}

	/**
	 * 在任务操作执行前的处理
	 * 
	 * @param task
	 *            ：服务操作的任务信息，不是勘察任务，是服务的任务
	 * @return 需要继续执行返回true，不需要继续执行返回false
	 */
	private static Boolean beforeBackgroundHandler(BackgroundServiceTask task) {
		Boolean result = true;

		switch (task.getServiceTaskId()) {
		// 提交任务
		case TASK_SUMIT:

			TaskInfo temp = (TaskInfo) task.getTaskParam().get("taskInfo");
			if (temp != null && !uploadTasks.containsKey(temp.TaskNum)) {
				temp.UploadStatusEnum = TaskUploadStatusEnum.Submitwating;
				TaskOperator.saveTaskUploadStatus(
						TaskUploadStatusEnum.Submitwating, temp.TaskNum);
				// 这里改变缓存中任务状态 为提交中
				temp.Status = TaskStatus.Submiting;
				uploadTasks.put(temp.TaskNum, temp);
				Intent myIntent = new Intent();// 创建Intent对象
				myIntent.setAction(BroadRecordType.WAIT_TO_SUBMIT);
				// 通知任务列表 刷新数据状态
				EIASApplication.getInstance().sendBroadcast(myIntent);
				ToastUtil.longShow(EIASApplication.getInstance()
						.getApplicationContext(), "后台服务处理中...");
				removeFailTasksMap(temp.TaskNum);
			} else {
				result = false;
				ToastUtil.longShow(EIASApplication.getInstance()
						.getApplicationContext(), "任务正在提交或找不到该任务...");
			}
			break;
		case RESTARTTASK_SUMIT:
			if (EIASApplication.IsNetworking && uploadTasks.size() > 0) {
				ToastUtil.longShow(EIASApplication.getInstance()
						.getApplicationContext(), "已继续提交提交中的任务...");
			}
			break;
		default:
			break;
		}
		return result;
	}

	// }}

	// }}

	// {{ 任务数据提交方法处理
	/**
	 * 获取任务上传队列
	 * 
	 * @return
	 */
	public static LinkedHashMap<String, TaskInfo> getUploadTasks() {
		String repeatTimeStr = EIASApplication
				.getSystemSetting(BroadRecordType.KEY_SETTING_REPEATTIME);
		int repeatTime = Integer.parseInt(repeatTimeStr);
		LinkedHashMap<String, TaskInfo> showTasks = new LinkedHashMap<String, TaskInfo>();
		showTasks.putAll(uploadTasks);
		for (int i = 0; i < failTasksTime.size(); i++) {
			Integer tempTime = failTasksTime.get(failTasksTime.keySet()
					.toArray()[i]);
			if (tempTime == repeatTime) {
				TaskInfo tempTask = failTasks.get(failTasksTime.keySet()
						.toArray()[i]);
				String key = (String) failTasksTime.keySet().toArray()[i];
				showTasks.put(key, tempTask);
			}
		}
		return showTasks;
	}

	/**
	 * 记录任务队列数据
	 */
	private static LinkedHashMap<String, TaskInfo> uploadTasks = new LinkedHashMap<String, TaskInfo>();

	/**
	 * 失败列表
	 */
	private static LinkedHashMap<String, TaskInfo> failTasks = new LinkedHashMap<String, TaskInfo>();

	/**
	 * 失败次数
	 */
	private static LinkedHashMap<String, Integer> failTasksTime = new LinkedHashMap<String, Integer>();

	/**
	 * 上传任务操作
	 */
	private void uploadTaskInfo(boolean additional) {

		// 没有在执行上传过程时，才重新跳入上传任务的循环，否则不跳入循环。
		if (!THREADRUNING) {
			boolean newResourceLibrary = getResUrl();

			while (uploadTasks.size() > 0) {
				THREADRUNING = true;
				TaskInfo tempTask = null;
				// 是否重新提交
				Boolean isResubmit = false;
				tempTask = uploadTasks.get(uploadTasks.keySet().toArray()[0]);
				// 若网络断开则跳出循环
				if (isContinue(tempTask))
					break;
				try {
					// 需要停一下才可以正确显示提交状态。原因不明
					// Thread.sleep(500);
					// 这里改变缓存中任务状态 为提交中
					tempTask.Status = TaskStatus.Submiting;
					tempTask.UploadStatusEnum = TaskUploadStatusEnum.Submiting;
					uploadTasks.put(tempTask.TaskNum, tempTask);
					TaskOperator.saveTaskUploadStatus(
							TaskUploadStatusEnum.Submiting, tempTask.TaskNum);
					Intent myIntent = new Intent();// 创建Intent对象
					myIntent.setAction(BroadRecordType.WAIT_TO_SUBMIT);
					sendBroadcast(myIntent);

					// 获取当前正在执行上传的任务编号并赋值给全局变量
					EIASApplication.SubmitingTaskNum = tempTask.TaskNum;
					ResultInfo<TaskInfo> getInfo = TaskDataWorker
							.getCompleteTaskInfoById(
									tempTask.IsNew ? tempTask.ID
											: tempTask.TaskID, tempTask.IsNew);
					if (getInfo.Success && getInfo.Data.ID > 0) {
						ResultInfo<ArrayList<TaskDataItem>> taskDataItems = new ResultInfo<>();
						// 记录上传媒体文件
						// ResultInfo<Boolean> fileUploadSuccess = new
						// ResultInfo<Boolean>();
						// UploadFileTask taskHttp = new UploadFileTask();

						// 直接上传图片到资源库
						ResultInfo<Map<String, String>> fileUploadSuccess = new ResultInfo<Map<String, String>>();
						fileUploadSuccess.Data = new HashMap<String, String>();
						UploadFile2ResourceTask taskHttpRes = new UploadFile2ResourceTask(
								EIASApplication.getCurrentUser().ResourceLibraryDomainName,
								newResourceLibrary);

						// 是否为补发文件
						if (additional) {
							taskDataItems = TaskOperator
									.getUploadFilesByAdditional(getInfo.Data);
							if (ListUtil.hasData(taskDataItems.Data)) {
								AdditionalResource addItems = TaskOperator
										.getAdditional(tempTask.TaskNum);
								ArrayList<TaskDataItem> handleItems = taskDataItems.Data;
								for (TaskDataItem taskDataItem : handleItems) {
									String[] dataResource = taskDataItem.Value
											.split(";");
									for (String subItem : dataResource) {
										if (!addItems.Resources
												.contains(subItem)) {
											taskDataItem.Value = taskDataItem.Value
													.replace(subItem + ";", "");
										}
									}
								}

								// 修改上传逻辑,直接同步资源库
								fileUploadSuccess = taskHttpRes.request2(
										EIASApplication.getCurrentUser(),
										getInfo.Data, handleItems, additional);

								// 由外采后台同步资源库
								// fileUploadSuccess = taskHttp.request(
								// EIASApplication.getCurrentUser(),
								// getInfo.Data, handleItems, additional);
							}
						} else {
							taskDataItems = TaskOperator
									.getUploadFiles(getInfo.Data);
							if (ListUtil.hasData(taskDataItems.Data)) {

								fileUploadSuccess = taskHttpRes.request2(
										EIASApplication.getCurrentUser(),
										getInfo.Data, taskDataItems.Data,
										additional);

								// fileUploadSuccess = taskHttp.request(
								// EIASApplication.getCurrentUser(),
								// getInfo.Data, taskDataItems.Data,
								// additional);
							}
						}
						// 图片上传成功或者没有文件 ，可以提交任务
						if (fileUploadSuccess.Success
								|| !ListUtil.hasData(taskDataItems.Data)) {

							// 资源上成功， 替换 分类项子项Value
							setDataItemValue(getInfo.Data,
									fileUploadSuccess.Data);

							TaskInfoDTO dto = new TaskInfoDTO(getInfo.Data);
							SubmitTaskInfoTask sumbitTaskInfo = new SubmitTaskInfoTask();
							// 修改内存中的 TaskDataItem.Value
							ResultInfo<TaskInfoDTO> result = sumbitTaskInfo
									.request(EIASApplication.getCurrentUser(),
											dto, additional);
							// 若网络连接断开则直接提示并返回
							if (isContinue(tempTask))
								break;

							TaskDataWorker.submitedUpdateTaskInfo(result.Data,
									result.Success);
							// 提交结果生成一个通知
							if (result.Success) {
								showNotification(tempTask.ID, tempTask.TaskNum,
										"提交完成", "地址：" + tempTask.TargetAddress,
										tempTask.TaskNum + "提交完成");
								TaskOperator.saveTaskUploadStatus(
										TaskUploadStatusEnum.Submited,
										tempTask.TaskNum);
								tempTask.UploadStatusEnum = TaskUploadStatusEnum.Submited;
								if (additional) {
									TaskOperator
											.removeAdditional(tempTask.TaskNum);
								}
								isResubmit = false;
							} else {
								// 若网络连接断开则直接提示并返回
								if (isContinue(tempTask))
									break;
								// 提交失败，缓存中的任务状态改变为 待提交
								tempTask.Status = TaskStatus.Doing;
								uploadTasks.put(tempTask.TaskNum, tempTask);
								// 网络正常则提示任务提交失败
								String errMsg = result.Message.trim().length() > 0 ? result.Message
										: "提交失败:服务器繁忙";
								showNotification(tempTask.ID, tempTask.TaskNum,
										errMsg, "地址：" + tempTask.TargetAddress,
										tempTask.TaskNum + "提交失败");
								TaskOperator.saveTaskUploadStatus(
										TaskUploadStatusEnum.SubmitFailure,
										tempTask.TaskNum);
								tempTask.UploadStatusEnum = TaskUploadStatusEnum.SubmitFailure;
								isResubmit = true;
							}
						} else {
							// 提交失败，缓存中的任务状态改变为 待提交
							tempTask.Status = TaskStatus.Doing;
							uploadTasks.put(tempTask.TaskNum, tempTask);
							// 通知，图片上传失败， 修改数据库任务状态， 和缓存状态
							showNotification(tempTask.ID, tempTask.TaskNum,
									"图片上传失败:请检查网络", "地址："
											+ tempTask.TargetAddress,
									tempTask.TaskNum + "提交失败");
							TaskOperator.saveTaskUploadStatus(
									TaskUploadStatusEnum.SubmitFailure,
									tempTask.TaskNum);
							tempTask.UploadStatusEnum = TaskUploadStatusEnum.SubmitFailure;
							// 是否需要重新提交
							isResubmit = true;

							// 图片上传资源库失败， 删除失败任务在资源库的所有资源引用
							TaskOperator.deleteRelationResource(//
									EIASApplication.getCurrentUser(),//
									getInfo.Data, fileUploadSuccess.Data);

						}
					}
				} catch (Exception e) {
					DataLogWorker.createDataLog(EIASApplication
							.getCurrentUser(),
							tempTask == null ? "任务提交时从任务的队列池中获取任务出错"
									: "任务提交失败，后台服务调用失败，任务编号为："
											+ tempTask.TaskNum,
							OperatorTypeEnum.TaskSubmit, LogType.Exection);
					// 提交失败，缓存中的任务状态改变为 待提交 2015-11-12 这里抛异常，待提交状态没改变，修正
					tempTask.Status = TaskStatus.Doing;
					uploadTasks.put(tempTask.TaskNum, tempTask);
					// 提交结果生成一个通知
					showNotification(tempTask.ID, tempTask.TaskNum, "提交异常",
							"地址：" + tempTask.TargetAddress, tempTask.TaskNum
									+ "提交异常");
					TaskOperator.saveTaskUploadStatus(
							TaskUploadStatusEnum.SubmitFailure,
							tempTask.TaskNum);
					tempTask.UploadStatusEnum = TaskUploadStatusEnum.SubmitFailure;
					isResubmit = true;
				} finally {
					try {
						// 刷新提交中页面
						Intent myIntent = new Intent();// 创建Intent对象
						myIntent.setAction(String
								.valueOf(BroadRecordType.AFTER_SUBMITED));
						// 不断网情况下执行
						if (!isContinue(null)) {
							// 任务提交次数加壹
							tempTask.UploadTimes = TaskOperator
									.addTaskUploadTimes(tempTask.TaskNum);
							// 去除已经执行提交的任务
							uploadTasks
									.remove(uploadTasks.keySet().toArray()[0]);
							uploadTasks.remove(tempTask.TaskNum);
							// 添加需要重新提交的任务
							if (isResubmit) {
								putFailTasksMap(tempTask);
							}
						} else {
							myIntent.putExtra("hideOfflineMsg", "true");
						}
						// 将全局变量赋值回空
						EIASApplication.SubmitingTaskNum = "";
						sendBroadcast(myIntent);
					} catch (Exception ex2) {
						DataLogOperator.other("任务提交失败" + tempTask.TaskNum + ">"
								+ ex2.getMessage());
					}
				}
			}
			// 执行上传操作
			THREADRUNING = false;
		}
	}

	/***
	 * 
	 * @author kevin
	 * @date 2016-02-03 下午3:39:18
	 * @Description: 获取资源库url
	 * @return isNewRes
	 */
	private boolean getResUrl() {
		boolean isNewRes = false;
		GetResLibConfigTask task = new GetResLibConfigTask();
		ResultInfo<JSONObject> resultResLib = task.request();
		try {
			if (resultResLib.Success) {
				String resUrl = resultResLib.Data
						.getString("ResourceLibraryUrl");
				isNewRes = resultResLib.Data.getBoolean("NewResourceLibrary");
				EIASApplication.getCurrentUser().ResourceLibraryDomainName = resUrl;
			} else {
				EIASApplication.getCurrentUser().ResourceLibraryDomainName = "";
			}
		} catch (Exception e) {
			DataLogOperator.taskHttp("SetTaskPauseTask=>获取资源库配置(request)",
					"数据请求失败");
		}
		return isNewRes;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-10-19 下午3:39:18
	 * @Description: 文件上传成功后获取的 resId 替换原来的 DataItem 的Value
	 * @param taskInfo
	 *            任务信息
	 * @param resIdMap
	 *            资源ID
	 * @return TaskInfo 返回类型
	 */
	private TaskInfo setDataItemValue(TaskInfo taskInfo,
			Map<String, String> resIdMap) {
		if (resIdMap == null) {
			return taskInfo;
		}
		Set<String> keySet = resIdMap.keySet();

		for (String key : keySet) {
			String[] id = key.split(",");
			int BaseCategoryID = Integer.valueOf(id[0]);
			int dataItemId = Integer.valueOf(id[1]);
			for (TaskCategoryInfo categories : taskInfo.Categories) {
				if (categories.CategoryID == BaseCategoryID) {
					for (TaskDataItem item : categories.Items) {
						if (item.ID == dataItemId) {
							item.Value = resIdMap.get(key);
						}
					}
				}
			}
		}

		return taskInfo;
	}

	/**
	 * 是否继续执行文件上传操作
	 * 
	 * @param tempTask
	 *            任务信息
	 * @return
	 */
	private Boolean isContinue(TaskInfo tempTask) {
		if (!checkNetworkAvailable(EIASApplication.getInstance())) {
			if (tempTask != null) {
				showNotification(CONNECTIONBACK_MSG, tempTask.TaskNum,
						"因网络中断,任务停止提交,重连后将自动续传。", "", tempTask.TaskNum
								+ "已停止提交。");
				TaskOperator.saveTaskUploadStatus(
						TaskUploadStatusEnum.Submitwating, tempTask.TaskNum);
				tempTask.UploadStatusEnum = TaskUploadStatusEnum.Submitwating;
			}
			return true;
		} else {
			return false;
		}
	}

	// 检测网络是否连接
	public static boolean checkNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return true;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 把失败任务添加到失败列表
	 * 
	 * @param tempTask
	 *            任务
	 */
	private void putFailTasksMap(TaskInfo tempTask) {
		if (failTasksTime.get(tempTask.TaskNum) == null) {
			failTasks.put(tempTask.TaskNum, tempTask);
			failTasksTime.put(tempTask.TaskNum, 2);
			uploadTasks.put(tempTask.TaskNum, tempTask);
		} else {
			String repeatTimeStr = EIASApplication
					.getSystemSetting(BroadRecordType.KEY_SETTING_REPEATTIME);
			int repeatTime = Integer.parseInt(repeatTimeStr);
			Integer tempTime = failTasksTime.get(tempTask.TaskNum);
			if (tempTime < repeatTime) {
				failTasksTime.put(tempTask.TaskNum, tempTime + 1);
				uploadTasks.put(tempTask.TaskNum, tempTask);
			}
		}
	}

	/**
	 * 删除失败任务
	 * 
	 * @param tempTask
	 *            任务
	 */
	private static void removeFailTasksMap(String taskNum) {
		if (failTasksTime.containsKey(taskNum)
				&& failTasks.containsKey(taskNum)) {
			failTasksTime.remove(taskNum);
			failTasks.remove(taskNum);
		}
	}

	/**
	 * 删除上传任务
	 * 
	 * @param taskNum
	 *            任务编号
	 */
	public static void removeUploadTasks(String taskNum) {
		removeFailTasksMap(taskNum);
		if (uploadTasks.containsKey(taskNum)) {
			uploadTasks.remove(taskNum);
		}
		// 刷新提交中页面
		Intent myIntent = new Intent();// 创建Intent对象
		myIntent.setAction(String.valueOf(BroadRecordType.AFTER_SUBMITED));
		EIASApplication.getInstance().sendBroadcast(myIntent);
	}

	// {{ 定时器 发送坐标

	/**
	 * 待机或者开机
	 */
	private BaseBroadcastReceiver standbyReceiver;

	/**
	 * 定时器的时间间隔 单位毫秒 当前时间为5分钟 定位间隔时间改为 15 分钟一次
	 */
	private int TIME = 1000 * 60 * 15;

	/**
	 * 记录时间获取坐标
	 */
	private void pushLatlagDelayed() {
		handler.postDelayed(runnable, TIME);
	}

	/**
	 * 移除
	 */
	private void removeLatlagDelayed() {
		handler.removeCallbacks(runnable);
	}

	/**
	 * 句柄
	 */
	Handler handler = new Handler();

	/**
	 * 运行句柄
	 */
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				handler.postDelayed(this, TIME);
				if (EIASApplication.getCurrentUser() != null
						&& EIASApplication.IsNetworking) {
					if (EIASApplication.locationHelper == null) {
						EIASApplication.initLocationHelper();
					} else {
						EIASApplication.locationHelper.startLocation();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 开始发送信息到服务器APIS中记录坐标
	 * 
	 * @param task
	 */
	private void setCoordinateTaskRequest(final BackgroundServiceTask task) {
		if (task.getTaskParam() != null) {
			Double latitude = Double.valueOf(task.getTaskParam()
					.get("latitude").toString());
			Double longitude = Double.valueOf(task.getTaskParam()
					.get("longitude").toString());
			String city = task.getTaskParam().get("city").toString();
			PushCoordinateTask taskHttp = new PushCoordinateTask();
			taskHttp.request(EIASApplication.getCurrentUser(), latitude,
					longitude, city);
		}
	}

	/**
	 * 待机或者恢复
	 */
	public void standbyLatlagDelayed() {
		EIASApplication.mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		// 声明消息接收对象
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(Intent.ACTION_SCREEN_ON);
		temp.add(Intent.ACTION_SCREEN_OFF);
		standbyReceiver = new BaseBroadcastReceiver(getApplicationContext(),
				temp);
		standbyReceiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {
			@Override
			public void onReceive(final Context context, Intent intent) {
				String actionType = intent.getAction();
				switch (actionType) {
				case Intent.ACTION_SCREEN_ON: // 恢复
					pushLatlagDelayed();
					break;
				case Intent.ACTION_SCREEN_OFF: // 待机
					removeLatlagDelayed();
					break;
				default:
					break;
				}
			}
		});
	}

	// }}

	// {{ 下载
	/**
	 * 下载
	 */
	public void downLoadFile() {
		String httpUrl = EIASApplication.getCurrentUser().LatestServer
				+ "/apk/" + EIASApplication.DownLoadApkName;
		String title = "云房外采系统";
		String description = "正在下载最新版["
				+ EIASApplication.version.ServerVersionName + "]...";
		String downLoadApkName = EIASApplication.DownLoadApkName;
		DownLoadFileUtil.downLoadFile(EIASApplication.downloadFileName,
				httpUrl, title, description, downLoadApkName,
				EIASApplication.mgr);
	}

	// }}

	// }}

	// {{ 通知

	/**
	 * 任务上传结果通知
	 * 
	 * @param taskId
	 *            任务ID（用于标示通知ID）
	 * @param title
	 *            标题
	 * @param contentTop
	 *            上边的内容
	 * @param contentBottom
	 *            下边的内容
	 * @param tickerText
	 *            通知发出时短暂显示的标题
	 */
	private void showNotification(Integer taskId, String title,
			String contentTop, String contentBottom, String tickerText) {
		Intent intent = new Intent();// 创建Intent对象
		intent.putExtra("notificationId", taskId.toString());
		intent.putExtra("title", title);
		intent.putExtra("contentTop", contentTop);
		intent.putExtra("contentBottom", contentBottom);
		intent.putExtra("tickerText", tickerText);
		intent.setAction(BroadRecordType.SUBMITED_RESULT_NOTIFICATION);
		sendBroadcast(intent);
	}

	// }}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
