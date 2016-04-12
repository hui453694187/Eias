package com.yunfang.eias.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.base.MainService;
import com.yunfang.eias.dto.DialogTipsDTO;
import com.yunfang.eias.dto.TaskCategoryInfoByTypeDTO;
import com.yunfang.eias.enumObj.TaskMenuEnum;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.enumObj.TaskUploadStatusEnum;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.eias.ui.HomeActivity;
import com.yunfang.eias.ui.TaskInfoActivity;
import com.yunfang.eias.ui.TaskListFragment;
import com.yunfang.eias.ui.Adapter.TaskUnWriteAdapter;
import com.yunfang.eias.view.MultilevelChoiceView;
import com.yunfang.eias.view.MultilevelChoiceView.PopViewButListener;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DialogBuilder;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.ToastUtil;
import com.yunfang.framework.utils.WinDisplay;
import com.yunfang.framework.view.CustomMultipleChoiceView;
import com.yunfang.framework.view.CustomMultipleChoiceView.onCancelListener;
import com.yunfang.framework.view.CustomMultipleChoiceView.onSelectedListener;

/**
 * 处理任务列表中长按菜单
 * 
 * @author gorson
 * 
 */
@SuppressLint({ "NewApi", "InflateParams" })
public class TaskListMenuOperaotr {
	// {{ 菜单项数据

	/**
	 * 要显示的菜单项
	 */
	private String[] menuItems;

	// }}

	// {{ 变量
	/**
	 * 对话框Titile
	 */
	private String title = "请选择操作";

	/**
	 * 对话框
	 */
	private DialogBuilder dialogBuilder;

	/**
	 * TaskListFragment
	 */
	public TaskListFragment taskListFragment;

	/**
	 * 收费信息的窗口
	 */
	private Dialog feeDialog;

	/**
	 * 设置预约信息窗口
	 */
	private Dialog appointmentDialog;

	/**
	 * 查看任务信息的窗口
	 */
	private Dialog detailDialog;

	/**
	 * 暂停任务的窗口
	 */
	private Dialog pauseDialog;

	/***
	 * 任务备注的窗口
	 */
	private Dialog taskRemarkDialog;

	/**
	 * 当前选中任务的分类项信息 Key:Android端ID值 Value:分类项名称
	 */
	private LinkedHashMap<String, String> categoryItems;

	/** 当前选中的任务分类DTO */
	private ArrayList<TaskCategoryInfoByTypeDTO> categoryArray;

	/**
	 * 提交任务信息所需适配器
	 */
	private TaskUnWriteAdapter taskunwriteadapter;

	/**
	 * 多选对话框
	 * */
	private PopupWindow stationSelectDialog;

	/**
	 * HomeActivity对象
	 */
	private HomeActivity homeActivity;

	/**
	 * taskInfoActivity对象
	 */
	private TaskInfoActivity taskInfoActivity;

	/**
	 * 当前上下文
	 */
	private Context currentContext;

	// }}

	/**
	 * 构造函数
	 * 
	 * @param listFragment
	 *            :任务列表对象
	 */
	public TaskListMenuOperaotr(TaskListFragment listFragment,
			HomeActivity activity) {
		taskListFragment = listFragment;
		currentContext = listFragment.getActivity();
		this.homeActivity = activity;
	}

	/**
	 * 构造函数
	 * 
	 * @param listFragment
	 *            :任务列表对象
	 */
	public TaskListMenuOperaotr(TaskInfoActivity activity) {
		this.taskInfoActivity = activity;
	}

	/**
	 * 获取当前Dialog
	 * 
	 * @return
	 */
	public void showDialog() {
		if (setMenuItems()) {
			if (menuItems != null) {
				dialogBuilder = new DialogBuilder(currentContext);
				dialogBuilder.setTitle(title);
				dialogBuilder.setItems(menuItems,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									menuItemClick(which);
								} catch (Exception e) {
									if (e != null) {
										DataLogOperator
												.taskHttp(
														"TaskListMenuOperaotr=>点击任务菜单(menuItemClick)",
														e.getMessage());
										ToastUtil.shortShow(
												taskListFragment.getActivity(),
												"任务被操作中");
									}
								}
							}
						});
			}
			dialogBuilder.create().show();
		}
	}

	/**
	 * 设置需要显示的任务菜单
	 * 
	 * @return 是否需要弹出选项
	 */
	private Boolean setMenuItems() {
		Boolean result = true;
		ArrayList<String> tempItems = new ArrayList<String>();
		menuItems = null;
		if (taskListFragment.viewModel.taskStatus != null) {
			switch (taskListFragment.viewModel.taskStatus) {
			case Todo:// 待领取
				tempItems.add(TaskMenuEnum.领取任务.toString());
				/* tempItems.add(TaskMenuEnum.领取任务并编辑.toString()); */
				tempItems.add(TaskMenuEnum.查看任务信息.toString());
				tempItems.add(TaskMenuEnum.任务备注.toString());
				break;
			case Doing:// 待提交
				// 多选状态
				if (taskListFragment.viewModel.currentSelectedTask.isChecked) {
					if (!EIASApplication.IsOffline
							&& EIASApplication.IsNetworking) {
						tempItems.add(TaskMenuEnum.提交选中任务.toString());
					}
					tempItems.add(TaskMenuEnum.删除已选中本地任务.toString());
				} else {
					// 长按已暂停任务
					if (taskListFragment.viewModel.currentSelectedTask.Status == TaskStatus.Pause) {
						// 暂停任务
						tempItems.add(TaskMenuEnum.启用任务.toString());
						if (EIASApplication.IsNetworking
								&& !EIASApplication.IsOffline) {
							tempItems.add(TaskMenuEnum.任务匹配.toString());
						}
					} else {// 非暂停的，正常任务。
						if (EIASApplication.IsNetworking
								&& !EIASApplication.IsOffline) {
							tempItems.add(TaskMenuEnum.提交本地任务.toString());
							tempItems.add(TaskMenuEnum.任务匹配.toString());
							// 离线登录，或者无网络不可以暂停任务
							if (!taskListFragment.viewModel.currentSelectedTask.IsNew) {
								// 非暂停任务， 且不是自建任务
								tempItems.add(TaskMenuEnum.暂停任务.toString());
							}
						}
					}

					tempItems.add(TaskMenuEnum.任务备注.toString());
					tempItems.add(TaskMenuEnum.查看任务信息.toString());
					tempItems.add(TaskMenuEnum.预约信息.toString());
					tempItems.add(TaskMenuEnum.编辑内容.toString());
					tempItems.add(TaskMenuEnum.收费信息.toString());

					tempItems.add(TaskMenuEnum.复制.toString());
					tempItems.add(TaskMenuEnum.数据检查并导出.toString());
					tempItems.add(TaskMenuEnum.任务数据导入.toString());
					TaskStatus taskStatus=taskListFragment.viewModel.currentSelectedTask.Status;
					if (taskListFragment.viewModel.currentSelectedTask.IsNew||
							taskStatus == TaskStatus.Unbelong||taskStatus==TaskStatus.Revocation) {
						tempItems.add(TaskMenuEnum.删除本地任务.toString());
					}
				}
				break;
			case Done:// 已完成
				// 多选状态
				TaskStatus selectTask=taskListFragment.viewModel.currentSelectedTask.Status;
				if (taskListFragment.viewModel.currentSelectedTask.isChecked) {
					if (!EIASApplication.IsOffline				//状态为已完成撤单
							&& EIASApplication.IsNetworking&&!(selectTask==TaskStatus.DoneRevocation)) {// 没有网络不可以提交
						tempItems.add(TaskMenuEnum.重新提交选中任务.toString());
					}
					tempItems.add(TaskMenuEnum.删除选中任务资源文件.toString());
					tempItems.add(TaskMenuEnum.删除已选中本地任务.toString());
				}
				// 非多选状态
				else {
					if (EIASApplication.IsNetworking		//状态为已完成撤单
							&& !EIASApplication.IsOffline&&!(selectTask==TaskStatus.DoneRevocation)) {
						tempItems.add(TaskMenuEnum.重新提交本地任务.toString());
					}
					tempItems.add(TaskMenuEnum.查看任务信息.toString());
					tempItems.add(TaskMenuEnum.预约信息.toString());
					tempItems.add(TaskMenuEnum.编辑内容.toString());
					tempItems.add(TaskMenuEnum.同步服务器数据.toString());
					tempItems.add(TaskMenuEnum.图片补发.toString());
					String feeMsg = taskListFragment.viewModel.currentSelectedTask.Fee;
					// 判断收费信息是否有值
					if (feeMsg == null || feeMsg.equals("")
							|| feeMsg.equals("null")) {
						tempItems.add(TaskMenuEnum.收费信息.toString());
					}
					tempItems.add(TaskMenuEnum.复制.toString());
					tempItems.add(TaskMenuEnum.数据检查并导出.toString());
					tempItems.add(TaskMenuEnum.任务数据导入.toString());
					if (taskListFragment.viewModel.onlyReportFinish) {
						tempItems.add(TaskMenuEnum.显示全部完成任务.toString());
					} else {
						tempItems.add(TaskMenuEnum.只显示完成报告任务.toString());
					}
					//已完成任务，被撤销任务，可以删除
					TaskStatus doenTaskStatus=taskListFragment.viewModel.currentSelectedTask.Status;
					if (taskListFragment.viewModel.currentSelectedTask.IsNew||doenTaskStatus==TaskStatus.DoneRevocation) {
						tempItems.add(TaskMenuEnum.删除本地任务.toString());
					}

				}
				break;
			case Submiting:
				if (taskListFragment.viewModel.currentSelectedTask.UploadStatusEnum != TaskUploadStatusEnum.Submiting) {
					if (EIASApplication.IsNetworking
							&& !EIASApplication.IsOffline) {
						if (taskListFragment.viewModel.currentSelectedTask.UploadStatusEnum == TaskUploadStatusEnum.SubmitFailure) {
							tempItems.add(TaskMenuEnum.重新提交本地任务.toString());
						}
					}
					tempItems.add(TaskMenuEnum.取消提交任务.toString());
				} else {
					result = false;
				}
			default:
				break;
			}

			// 对比被复制任务的勘察表ID 和被粘贴的任务勘察表ID 是否一致，才可以被复制粘贴
			if (taskListFragment.viewModel.currentCopiedTask != null
					&& taskListFragment.viewModel.currentSelectedTask != null
			/*
			 * && taskListFragment.viewModel.currentSelectedTask.DDID == //修改
			 * 为就算勘察表ID 不同，也可以出现粘贴按钮
			 * taskListFragment.viewModel.currentCopiedTask.DDID
			 */) {

				tempItems.add(TaskMenuEnum.粘贴创建新任务.toString());
				if (taskListFragment.viewModel.currentSelectedTask.ID != taskListFragment.viewModel.currentCopiedTask.ID) {
					tempItems.add(TaskMenuEnum.粘贴.toString());
				}
			}
			menuItems = tempItems.toArray(new String[0]);
		}
		return result;
	}

	/**
	 * 菜单响应事件
	 * 
	 * @param which
	 */
	private void menuItemClick(int index) throws Exception {
		TaskMenuEnum menu = TaskMenuEnum.valueOf(dialogBuilder
				.getMenuName(index));
		Integer ddid = taskListFragment.viewModel.currentSelectedTask.DDID;
		String taskNum = taskListFragment.viewModel.currentSelectedTask.TaskNum;
		int dataDefine = taskListFragment.viewModel.currentSelectedTask.DataDefineVersion;
		switch (menu) {
		case 删除本地任务:
			if (!TaskOperator
					.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				String msgStr = "您确认要删除编号为["
						+ taskListFragment.viewModel.currentSelectedTask.TaskNum
						+ "]的任务吗？";
				deleteTaskInfoDialog(msgStr, taskListFragment.TASK_DELETE);
			} else {
				ToastUtil.longShow(taskListFragment.getActivity(),
						"当前任务正在提交中，将不会清空当前任务!");
			}
			break;
		case 删除已选中本地任务:
			/** 所有的任务信息， 找出选中的任务 */
			ArrayList<TaskInfo> deletTaskInfos = new ArrayList<TaskInfo>();
			/** 远程任务，非本地自建 */
			ArrayList<TaskInfo> remoteTasks = new ArrayList<TaskInfo>();
			/** 是否可以执行批量删除 */
			boolean canCommit = true;
			for (TaskInfo taskInfo : taskListFragment.viewModel.taskInfoes) {
				if (taskInfo.isChecked) {
					if (!TaskOperator.submiting(taskInfo.TaskNum)) {// 判断选中的任务中是否有提交中的任务，
						if (taskInfo.IsNew) {// 是本地自建任务，添加进删除列表
							deletTaskInfos.add(taskInfo);// 选中的任务， 加入删除列表
						} else {
							remoteTasks.add(taskInfo);
						}
					} else {// 跳出
						ToastUtil.longShow(taskListFragment.getActivity(),
								"当前任务正在提交中，将不会清空当前任务!");
						canCommit = false;
						break;
					}

				}
			}
			if (canCommit && deletTaskInfos.size() > 0) {
				/** 批量删除任务 */
				deleteTadkInfoDialog("您确认要删除选中的这些任务？",
						taskListFragment.TASK_BATCH_DELETE, deletTaskInfos);// 发出批量删除任务的通知给后台执行
			}
			// 只选中了远程任务
			if (remoteTasks.size() > 0 && deletTaskInfos.size() <= 0) {
				ToastUtil.longShow(taskListFragment.getActivity(),
						"选中任务中没有本地任务");
			}

			break;
		case 复制:
			if (!hasNewDataDefines(ddid)) {
				showCopyCategoriesDialog2();
				// showCopyCategoriesDialog();
			}
			break;
		case 收费信息:
			showSetFeeDialog();
			break;
		case 暂停任务:
			if (!TaskOperator
					.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				showPauseDialog();
			} else {
				ToastUtil.longShow(taskListFragment.getActivity(),
						"当前任务正在提交中，将不会暂停当前任务!");
			}
			break;
		case 任务备注:
			if (!TaskOperator
					.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				// 显示填写备注的对话框框
				showTaskRemarkDialog();
			} else {
				ToastUtil.longShow(taskListFragment.getActivity(),
						"当前任务正在提交中，请提交结束后添加备注!");
			}

			break;
		case 查看任务信息:
			showCurrentSelectedTaskInfo();
			break;
		case 粘贴:
			if (!TaskOperator
					.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				pastedCategories(false);
			} else {
				ToastUtil.longShow(taskListFragment.getActivity(),
						"当前任务正在提交中，将不会粘贴到当前任务!");
			}
			break;
		case 粘贴创建新任务:
			pastedCategories(true);
			break;
		case 编辑内容:
			if (!hasNewDataDefines(ddid)) {
				taskListFragment.doSomething("加载中",
						taskListFragment.TASK_EDIT_TASKINFO);
			}
			break;
		case 同步服务器数据:
			// 同步服务器这条任务的数据 不为空则覆盖
			// TaskInfo
			// taskInfoRlaseh=taskListFragment.viewModel.currentSelectedTask;

			taskListFragment.doSomething("正在同步本条任务数据",
					taskListFragment.SYNC_TASK_INFO);

			break;
		case 领取任务:
			taskListFragment.doSomething("加载中",
					taskListFragment.TASK_RECEIVETASK);
			break;
		case 提交本地任务:
		case 重新提交本地任务:
			if (!hasNewDataDefines(ddid)
					&& !checkTaskReportFinish(taskListFragment.viewModel.currentSelectedTask)) {
				serverHasNewDataDefines(ddid, dataDefine);
				/* putTaskInfo(ddid, taskNum, fee); */
			}
			break;
		case 提交选中任务:
		case 重新提交选中任务:
			ArrayList<TaskInfo> allTaskInfo = taskListFragment.viewModel.taskInfoes;
			ArrayList<TaskInfo> needSubmitTaskInfo = new ArrayList<TaskInfo>();
			Boolean isSubmit = true;
			// 验证任务是否都可以上传
			for (TaskInfo element : allTaskInfo) {
				if (hasNewDataDefines(element.DDID)) {
					isSubmit = false;
					break;
				}
			}
			// 验证任务是否都可以上传
			if (isSubmit) {
				for (int i = 0; i < allTaskInfo.size(); i++) {
					TaskInfo taskInfo = allTaskInfo.get(i);
					// 不是提交中
					boolean unSubmitiong = !TaskOperator
							.submiting(taskInfo.TaskNum)
							&& taskInfo.Status != TaskStatus.Submiting;
					if (taskInfo.isChecked && unSubmitiong) {
						needSubmitTaskInfo.add(taskInfo);
					}
				}
				commitTasks(needSubmitTaskInfo);
			}
			break;
		case 任务匹配:
			if (!hasNewDataDefines(ddid)) {
				taskListFragment.viewModel.homeActivity.openMatchAty(taskNum,
						ddid);
			}
			break;
		case 取消提交任务:
			MainService
					.removeUploadTasks(taskListFragment.viewModel.currentSelectedTask.TaskNum);
			break;
		case 数据检查并导出: {
			if (!hasNewDataDefines(ddid)) {
				taskExportInfo();
			}
			break;
		}
		case 任务数据导入: {
			if (!hasNewDataDefines(ddid)) {
				taskImportInfo();
			}
			break;
		}
		case 图片补发: {
			if (!hasNewDataDefines(ddid)) {
				additionalPictrue();
			}
			break;
		}
		case 预约信息: {
			showAppointmentDialog(TaskMenuEnum.预约信息);
			break;
		}
		case 只显示完成报告任务: {
			queryFinishReport();
			break;
		}
		case 显示全部完成任务: {
			queryFinishTask();
			break;
		}
		case 删除选中任务资源文件: {
			removeTaskResource();
			break;
		}
		case 启用任务:
			// 启用已经暂停的任务 ，向服务器发起请求，启用暂停的任务。
			taskListFragment.doSomething("正在启用！",
					taskListFragment.TASK_RESTART_TASK);

			break;
		default:
			break;
		}
	}

	/**
	 * 进入图片补发
	 */
	private void additionalPictrue() {
		taskListFragment.doSomething("",
				taskListFragment.TASK_ADDITIONAL_PICTRUE);
	}

	/**
	 * 删除本地任务
	 */
	private void deleteTaskInfoDialog(String msgStr, final int TASK_NUMBER) {
		DialogUtil.showConfirmationDialog(currentContext, msgStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						taskListFragment.doSomething("删除中", TASK_NUMBER);// taskListFragment.TASK_DELETE
					}
				});
	}

	/***
	 * 批量删除本地任务任务
	 * 
	 * @param msgStr
	 * @param TASK_NUMBER
	 * @param taskInfos
	 *            需要删除的任务列表
	 */
	private void deleteTadkInfoDialog(String msgStr, final int TASK_NUMBER,
			final List<TaskInfo> taskInfos) {
		DialogUtil.showConfirmationDialog(currentContext, msgStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						taskListFragment.doSomething("删除中", TASK_NUMBER,
								taskInfos);// taskListFragment.TASK_DELETE
					}
				});
	};

	/**
	 * 跳去任务无法勘察的操作界面
	 */
	private void showPauseDialog() {
		if (pauseDialog == null) {
			pauseDialog = DialogUtil.commonDialog(currentContext,
					R.layout.dialog_view_task_pause);
			android.view.WindowManager.LayoutParams params = pauseDialog
					.getWindow().getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.8));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				pauseDialog.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				pauseDialog.getWindow().setAttributes(params);
				break;
			}
			Button btnConfirm = (Button) pauseDialog
					.findViewById(R.id.dialog_button_ok);
			Button btnCancel = (Button) pauseDialog
					.findViewById(R.id.dialog_button_cancel);
			final EditText txtReason = (EditText) pauseDialog
					.findViewById(R.id.dialog_view_pause_reason);
			InputFilter[] filters = {new LengthFilter(200)};  
			txtReason.setFilters(filters); 
			txtReason
					.setText(taskListFragment.viewModel.currentSelectedTask.Remark);
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					pauseDialog.dismiss();
					taskListFragment.viewModel.currentSelectedTask.Remark = txtReason
							.getText().toString().trim();
					taskListFragment.doSomething("保存任务暂停原因",
							taskListFragment.TASK_SETPAUSE);
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					pauseDialog.dismiss();
				}
			});
		} else {
			EditText txtReason = (EditText) pauseDialog
					.findViewById(R.id.dialog_view_pause_reason);
			txtReason
					.setText(taskListFragment.viewModel.currentSelectedTask.Remark);
		}
		pauseDialog.show();
	}

	/**
	 * 
	 * @author kevin
	 * @date 2015-10-28 下午2:36:22
	 * @Description: 填写备注信息
	 */
	private void showTaskRemarkDialog() {
		EditText txtReason = null;
		if (taskRemarkDialog == null) {
			taskRemarkDialog = DialogUtil.commonDialog(currentContext,
					R.layout.dialog_view_task_pause);
			android.view.WindowManager.LayoutParams params = taskRemarkDialog
					.getWindow().getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.8));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				taskRemarkDialog.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				taskRemarkDialog.getWindow().setAttributes(params);
				break;
			default:
				params.width = (int) ((point.x) * (0.8));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				taskRemarkDialog.getWindow().setAttributes(params);
			}
			TextView title = (TextView) taskRemarkDialog
					.findViewById(R.id.dialog_title_tv);
			title.setText("填写备注信息");
			Button btnConfirm = (Button) taskRemarkDialog
					.findViewById(R.id.dialog_button_ok);
			Button btnCancel = (Button) taskRemarkDialog
					.findViewById(R.id.dialog_button_cancel);
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						EditText txtReason = (EditText) taskRemarkDialog
								.findViewById(R.id.dialog_view_pause_reason);
						String remarkStr = txtReason.getText().toString()
								.trim();
						String msgStr = "保存任务备注...";
						if (remarkStr.length() <= 0) {
							msgStr = "正在清除备注....";
						}
						taskRemarkDialog.dismiss();
						if (taskListFragment.viewModel.currentSelectedTask.Status != TaskStatus.Doing
								&& !EIASApplication.IsNetworking) {
							// 除了待提交， 其他情况，备注需要有网路才可以执行
							ToastUtil.shortShow(currentContext,
									"抱歉，请检查你的网络  +_+~");
							return;
						}
						taskListFragment.viewModel.currentSelectedTask.TaskRemark = remarkStr;
						taskListFragment.doSomething(msgStr,
								taskListFragment.TASK_REMARK);
					} catch (Exception e) {
						ToastUtil.shortShow(currentContext, "任务已被领取！");
					}
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					taskRemarkDialog.dismiss();
				}
			});

		}
		txtReason = (EditText) taskRemarkDialog
				.findViewById(R.id.dialog_view_pause_reason);
		txtReason.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				200) });
		String currentRemark = taskListFragment.viewModel.currentSelectedTask.TaskRemark;
		txtReason.setText(currentRemark);

		taskRemarkDialog.show();
	}

	/**
	 * 收费信息
	 * 
	 * @param tempObject
	 */
	private void showSetFeeDialog() {
		if (feeDialog == null) {
			feeDialog = DialogUtil.commonDialog(currentContext,
					R.layout.dialog_view_fee);
			WindowManager.LayoutParams params = feeDialog.getWindow()
					.getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.8));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				feeDialog.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				feeDialog.getWindow().setAttributes(params);
				break;
			}

			Button btnConfirm = (Button) feeDialog
					.findViewById(R.id.dialog_button_ok);
			Button btnCancel = (Button) feeDialog
					.findViewById(R.id.dialog_button_cancel);

			btnConfirm.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (!TaskOperator
							.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
						EditText txtFee = (EditText) feeDialog
								.findViewById(R.id.dialog_view_fee_txtFee);
						EditText txtReceiptNo = (EditText) feeDialog
								.findViewById(R.id.dialog_view_fee_txtReceiptNo);
						taskListFragment.viewModel.currentSelectedTask.ReceiptNo = String
								.valueOf(txtReceiptNo.getText());
						taskListFragment.viewModel.currentSelectedTask.Fee = txtFee
								.getText().toString().trim();
						taskListFragment.doSomething("保存任务收费信息",
								taskListFragment.TASK_SETFEEINFO);
						feeDialog.dismiss();
					} else {
						ToastUtil.longShow(taskListFragment.getActivity(),
								"当前任务正在提交中，将不能设置任务收费信息!");
					}
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					feeDialog.dismiss();
				}
			});
		}
		if (taskListFragment.viewModel.currentSelectedTask != null) {
			EditText txtFee = (EditText) feeDialog
					.findViewById(R.id.dialog_view_fee_txtFee);
			EditText txtReceiptNo = (EditText) feeDialog
					.findViewById(R.id.dialog_view_fee_txtReceiptNo);
			String showReceiptNo = taskListFragment.viewModel.currentSelectedTask.ReceiptNo == null
					|| taskListFragment.viewModel.currentSelectedTask.ReceiptNo
							.equals("null") ? ""
					: taskListFragment.viewModel.currentSelectedTask.ReceiptNo
							.toString();
			txtReceiptNo.setText(showReceiptNo);
			String showFee = taskListFragment.viewModel.currentSelectedTask.Fee
					.equals("null") ? ""
					: taskListFragment.viewModel.currentSelectedTask.Fee
							.toString();
			txtFee.setText(showFee);
		}
		feeDialog.show();
	}

	/**
	 * 显示预约信息窗口
	 * 
	 * @param task任务信息
	 */
	private void showAppointmentDialog(final TaskMenuEnum menu) {
		if (appointmentDialog == null) {
			appointmentDialog = DialogUtil.commonDialog(currentContext,
					R.layout.dialog_view_appointment);
			WindowManager.LayoutParams params = appointmentDialog.getWindow()
					.getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.9));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				appointmentDialog.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				appointmentDialog.getWindow().setAttributes(params);
				break;
			}
		}
		Button btnConfirm = (Button) appointmentDialog
				.findViewById(R.id.dialog_button_ok);
		Button btnCancel = (Button) appointmentDialog
				.findViewById(R.id.dialog_button_cancel);
		final EditText dialog_view_appointment_name = (EditText) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_name);
		final EditText dialog_view_appointment_phone = (EditText) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_phone);
		final ImageView dialog_view_appointment_phone_call = (ImageView) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_phone_call);
		final EditText dialog_view_appointment_date = (EditText) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_date);
		final EditText dialog_view_appointment_time = (EditText) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_time);
		final EditText dialog_view_appointment_remark = (EditText) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_remark);
		final DatePicker dialog_view_appointment_date2 = (DatePicker) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_date2);
		final TimePicker dialog_view_appointment_time2 = (TimePicker) appointmentDialog
				.findViewById(R.id.dialog_view_appointment_time2);

		resizePikcer(dialog_view_appointment_date2);// 调整datepicker大小
		// resizePikcer(dialog_view_appointment_time2);//调整timepicker大小

		//点击呼叫电话号码
		dialog_view_appointment_phone_call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = dialog_view_appointment_phone.getText().toString().trim();
				if (!TextUtils.isEmpty(phone)) {
					//呼叫电话
					Intent intent = new Intent(Intent.ACTION_DIAL);
					Uri data = Uri.parse("tel:" + phone);
					intent.setData(data);
					homeActivity.startActivity(intent);
				}else {
					homeActivity.showToast("请先输入号码");
				}
				
			}
		});
		
		dialog_view_appointment_time2.setIs24HourView(true);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!TaskOperator
						.submiting(taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
					taskListFragment.viewModel.currentSelectedTask.ContactPerson = dialog_view_appointment_name
							.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.ContactTel = dialog_view_appointment_phone
							.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.BookedDate = dialog_view_appointment_date
							.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.BookedTime = dialog_view_appointment_time
							.getText().toString().trim();
					taskListFragment.viewModel.currentSelectedTask.BookedRemark = dialog_view_appointment_remark
							.getText().toString().trim();
					taskListFragment.doSomething("保存预约信息中。。。",
							taskListFragment.TASK_APPOINTMENT, menu);
					appointmentDialog.dismiss();
				} else {
					ToastUtil.longShow(taskListFragment.getActivity(),
							"当前任务正在提交中，将不能设置预约信息!");
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				appointmentDialog.dismiss();
			}
		});

		dialog_view_appointment_time2
				.setOnTimeChangedListener(new OnTimeChangedListener() {
					public void onTimeChanged(TimePicker view, int hourOfDay,
							int minute) {
						dialog_view_appointment_time.setText(hourOfDay + ":"
								+ minute);
					}

				});

		OnDateChangedListener dateListener = new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				dialog_view_appointment_date.setText(year + "-"
						+ (monthOfYear + 1) + "-" + dayOfMonth);
			}
		};

		if (taskListFragment.viewModel.currentSelectedTask != null) {
			TaskInfo task = TaskOperator
					.getLocTaskInfoCompleteByTaskNum(taskListFragment.viewModel.currentSelectedTask.TaskNum);
			dialog_view_appointment_name
					.setText(taskListFragment.viewModel.currentSelectedTask.ContactPerson == null ? ""
							: taskListFragment.viewModel.currentSelectedTask.ContactPerson);
			dialog_view_appointment_phone
					.setText(taskListFragment.viewModel.currentSelectedTask.ContactTel == null ? ""
							: taskListFragment.viewModel.currentSelectedTask.ContactTel);
			dialog_view_appointment_date.setText(task.BookedDate);
			dialog_view_appointment_time.setText(task.BookedTime);
			dialog_view_appointment_remark.setText(task.BookedRemark);

			if (task.BookedDate != null && task.BookedDate.length() > 0) {
				String[] date = task.BookedDate.split("-");
				dialog_view_appointment_date2.init(Integer.parseInt(date[0]),
						Integer.parseInt(date[1]), Integer.parseInt(date[2]),
						dateListener);
			} else {
				final Calendar c = Calendar.getInstance();
				Integer mYear = c.get(Calendar.YEAR);
				Integer mMonth = c.get(Calendar.MONTH);
				Integer mDay = c.get(Calendar.DAY_OF_MONTH);
				dialog_view_appointment_date2.init(mYear, mMonth, mDay,
						dateListener);
				dialog_view_appointment_date.setText(mYear + "-" + (mMonth + 1)
						+ "-" + mDay);
			}
			if (task.BookedTime != null && task.BookedTime.length() > 0) {
				String[] time = task.BookedTime.split(":");
				dialog_view_appointment_time2.setCurrentHour(Integer
						.parseInt(time[0]));
				dialog_view_appointment_time2.setCurrentMinute(Integer
						.parseInt(time[1]));
			} else {
				final Calendar c = Calendar.getInstance();
				Integer mHour = c.get(Calendar.HOUR_OF_DAY);
				Integer mMinute = c.get(Calendar.MINUTE);
				dialog_view_appointment_time2.setCurrentHour(mHour);
				dialog_view_appointment_time2.setCurrentMinute(mMinute);
				dialog_view_appointment_time.setText(mHour + ":" + mMinute);
			}
		}
		appointmentDialog.show();
	}

	public void resizePikcer(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}

	/**
	 * 得到viewGroup里面的numberpicker组件
	 * 
	 * @param viewGroup
	 * @return
	 */
	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}
		return npList;
	}

	/*
	 * 调整numberpicker大小
	 */
	private void resizeNumberPicker(NumberPicker np) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT);
		params.weight = 1f;
		params.setMargins(10, 0, 10, 0);
		np.setLayoutParams(params);
	}

	/**
	 * 把复制好的任务勘察信息粘贴到新的指定任务下面
	 */
	private void pastedCategories(final Boolean isCopyToNewTask) {
		if (taskListFragment.viewModel.currentSelectedTask != null) {
			if (!isCopyToNewTask
					&& //
					taskListFragment.viewModel.currentCopiedTask.TaskNum
							.equals(//
							taskListFragment.viewModel.currentSelectedTask.TaskNum)) {
				ToastUtil.longShow(currentContext, "不能把任务粘贴给自己");
			}/*
			 * // 2016/1/6 调整， 不再对比勘察表是否一致， 不一致同样可以粘贴 else if (!isCopyToNewTask
			 * && taskListFragment.viewModel.currentCopiedTask.DDID !=
			 * 
			 * taskListFragment.viewModel.currentSelectedTask.DDID) {
			 * ToastUtil.longShow(currentContext, "任务所属勘察表不一致，无法继续"); }
			 */else {
				StringBuilder titleString = new StringBuilder();
				if (isCopyToNewTask) {
					titleString.append("您确认把复制的勘察数据粘贴到新建任务中吗?");
				} else {
					// 被复制任务和被粘贴任务不属于同一个勘察表， 的提示信息
					if (taskListFragment.viewModel.currentSelectedTask.DDID != taskListFragment.viewModel.currentCopiedTask.DDID) {
						titleString.append("你复制的任务和正在被粘贴的任务勘察表不一致，部分数据可能无法赋值，");
					}
					titleString
							.append("您确认继续粘贴到编号为["
									+ taskListFragment.viewModel.currentSelectedTask.TaskNum
									+ "]任务中吗");
				}
				DialogUtil.showConfirmationDialog(currentContext,
						titleString.toString(),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

								if (isCopyToNewTask) {
									taskListFragment.doSomething("加载中...",

									taskListFragment.TASK_PASTED_NEWTASKINFO);
								} else {
									taskListFragment.doSomething("粘贴中...",

									taskListFragment.TASK_PASTED_TASKINFO);
								}
							}
						});
			}

		}
	}

	/**
	 * 点击查看任务信息的操作
	 */
	@SuppressLint("InflateParams")
	private void showCurrentSelectedTaskInfo() {
		LayoutInflater inflater = LayoutInflater.from(currentContext);
		View dialog_view_detail = inflater.inflate(
				R.layout.dialog_view_task_detail, null);
		detailDialog = DialogUtil.getDetailDialog(currentContext,
				dialog_view_detail);
		final TaskInfo taskInfo = taskListFragment.viewModel.currentSelectedTask;
		// TextView dialog_title = (TextView)
		// dialog_view_detail.findViewById(R.id.dialog_title);
		TextView taskNum_txt = (TextView) dialog_view_detail
				.findViewById(R.id.taskNum_txt);
		TextView address_txt = (TextView) dialog_view_detail
				.findViewById(R.id.address_txt);
		TextView residentialArea_txt = (TextView) dialog_view_detail
				.findViewById(R.id.residentialArea_txt);
		TextView building_txt = (TextView) dialog_view_detail
				.findViewById(R.id.building_txt);
		TextView floor_txt = (TextView) dialog_view_detail
				.findViewById(R.id.floor_txt);
		TextView targetArea_txt = (TextView) dialog_view_detail
				.findViewById(R.id.targetArea_txt);
		TextView contacter_txt = (TextView) dialog_view_detail
				.findViewById(R.id.contacter_txt);
		TextView contacterTel_txt = (TextView) dialog_view_detail
				.findViewById(R.id.contacterTel_txt);
		TextView owner_txt = (TextView) dialog_view_detail
				.findViewById(R.id.owner_txt);
		TextView ownerTel_txt = (TextView) dialog_view_detail
				.findViewById(R.id.ownerTel_txt);
		TextView targetName_txt = (TextView) dialog_view_detail
				.findViewById(R.id.targetName_txt);
		TextView clientUnit_txt = (TextView) dialog_view_detail
				.findViewById(R.id.clientUnit_txt);
		TextView clientDep_txt = (TextView) dialog_view_detail
				.findViewById(R.id.clientDep_txt);
		TextView clientName_txt = (TextView) dialog_view_detail
				.findViewById(R.id.clientName_txt);
		TextView clientTel_txt = (TextView) dialog_view_detail
				.findViewById(R.id.clientTel_txt);
		TextView remark_txt = (TextView) dialog_view_detail
				.findViewById(R.id.remark_txt);
		Button btn_confirm = (Button) dialog_view_detail
				.findViewById(R.id.btn_confirm);
		TextView definename_tv = (TextView) dialog_view_detail
				.findViewById(R.id.definename_tv);
		// 新加字段，任务来源
		TextView customer_source_tv = (TextView) dialog_view_detail
				.findViewById(R.id.customer_source_tv);

		setTaskTextView(taskNum_txt, taskInfo.TaskNum);
		/*
		 * if (taskNum_txt != null && taskInfo.TaskNum.length() > 0) {
		 * taskNum_txt.setText("任务编号：" + taskInfo.TaskNum); }
		 */
		setTaskTextView(address_txt, taskInfo.TargetAddress);
		/*
		 * if (address_txt != null && taskInfo.TargetAddress.length() > 0) {
		 * address_txt.setText("地址：" + taskInfo.TargetAddress); }
		 */
		setTaskTextView(residentialArea_txt, taskInfo.ResidentialArea);
		/*
		 * if (residentialArea_txt != null && taskInfo.ResidentialArea.length()
		 * > 0) { residentialArea_txt.setText("小区名称：" +
		 * taskInfo.ResidentialArea); }
		 */
		setTaskTextView(building_txt, taskInfo.Building);
		/*
		 * if (building_txt != null && taskInfo.Building.length() > 0) {
		 * building_txt.setText("楼栋名称：" + taskInfo.Building); }
		 */
		setTaskTextView(floor_txt, taskInfo.Floor);
		/*
		 * if (floor_txt != null && taskInfo.Floor.length() > 0) {
		 * floor_txt.setText("所在楼层：" + taskInfo.Floor); }
		 */
		setTaskTextView(targetArea_txt, taskInfo.TargetArea);
		/*
		 * if (targetArea_txt != null && taskInfo.TargetArea.length() > 0) {
		 * targetArea_txt.setText("建筑面积：" + taskInfo.TargetArea); }
		 */
		// 联系人
		setTaskTextView(contacter_txt, taskInfo.ContactPerson);
		/*
		 * if (contacter_txt != null && taskInfo.ContactPerson.length() > 0) {
		 * contacter_txt.setText("联系人：" + taskInfo.ContactPerson); }
		 */
		setTaskTextView(contacterTel_txt, taskInfo.ContactTel);
		/*
		 * if (contacterTel_txt != null && taskInfo.ContactTel.length() > 0) {
		 * contacterTel_txt.setText("联系人电话：" + taskInfo.ContactTel); }
		 */
		// 业主 Owner 业主 OwnerTel 业主电话
		setTaskTextView(owner_txt, taskInfo.Owner);
		/*
		 * if (owner_txt != null && taskInfo.Owner.length() > 0) {
		 * owner_txt.setText("业主：" + taskInfo.Owner); }
		 */
		setTaskTextView(ownerTel_txt, taskInfo.OwnerTel);
		/*
		 * if (ownerTel_txt != null && taskInfo.OwnerTel.length() > 0) {
		 * ownerTel_txt.setText("业主电话：" + taskInfo.OwnerTel); }
		 */
		setTaskTextView(targetName_txt, taskInfo.TargetName);
		/*
		 * if (targetName_txt != null && taskInfo.TargetName.length() > 0) {
		 * targetName_txt.setText("目标名称：" + taskInfo.TargetName); }
		 */
		setTaskTextView(clientUnit_txt, taskInfo.ClientUnit);
		/*
		 * if (clientUnit_txt != null && taskInfo.ClientUnit.length() > 0) {
		 * clientUnit_txt.setText("委托人单位：" + taskInfo.ClientUnit); }
		 */
		setTaskTextView(clientDep_txt, taskInfo.ClientDep);
		/*
		 * if (clientDep_txt != null && taskInfo.ClientDep.length() > 0) {
		 * clientDep_txt.setText("委托人部门：" + taskInfo.ClientDep); }
		 */
		setTaskTextView(clientName_txt, taskInfo.ClientName);
		/*
		 * if (clientName_txt != null && taskInfo.ClientName.length() > 0) {
		 * clientName_txt.setText("委托人名称：" + taskInfo.ClientName); }
		 */
		setTaskTextView(clientTel_txt, taskInfo.ClientTel);
		/*
		 * if (clientTel_txt != null && taskInfo.ClientTel.length() > 0) {
		 * clientTel_txt.setText("委托人联系电话：" + taskInfo.ClientTel); }
		 */

		setTaskTextView(remark_txt, taskInfo.Remark);
		/*
		 * if (remark_txt != null && taskInfo.Remark.length() > 0) {
		 * remark_txt.setText("标注：" + taskInfo.Remark); }
		 */
		// 用户来源：
		setTaskTextView(customer_source_tv, taskInfo.CustomerSource);

		DataDefine dataDefine = taskInfo.DDID > 0 ? getTaskDataDefine(taskInfo.DDID)
				: null;
		if (dataDefine != null) {
			setTaskTextView(definename_tv, dataDefine.Name);
		} else {
			definename_tv.setVisibility(View.GONE);
		}

		btn_confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				detailDialog.dismiss();
			}
		});

		if (detailDialog.isShowing()) {
			detailDialog.dismiss();
		} else {
			detailDialog.show();
		}
	}

	/***
	 * @author kevin
	 * @date 2015-10-8 上午10:49:09
	 * @Description: 根据任务DDI 查询对应勘察配置表（主表） 若查询不到返回空，返回值需要做空值判断。
	 * @param taskDdid
	 * @return
	 */
	private DataDefine getTaskDataDefine(int taskDdid) {
		ResultInfo<DataDefine> dataDefineResult = null;
		dataDefineResult = DataDefineWorker.queryDataDefineByDDID(taskDdid);
		if (dataDefineResult != null && dataDefineResult.Success
				&& dataDefineResult.Data != null) {
			return dataDefineResult.Data;
		}
		return null;
	}

	/***
	 * @author kevin
	 * @date 2015-10-8 上午10:49:09
	 * @Description: 设置任务信息各字段的值
	 * @param textView
	 *            控件
	 * @param value
	 *            控件值
	 * @version V1.0
	 */
	private void setTaskTextView(TextView textView, String value) {
		if (textView != null && value != null
				&& !TextUtils.isEmpty(value.trim()) && !value.equals("null")) {
			textView.setText(textView.getText() + value);
		} else {
			textView.setVisibility(View.GONE);
		}
	}

	/**
	 * 数据检查
	 */
	private void taskExportInfo() {
		taskListFragment.doSomething("检查中...", taskListFragment.TASK_EXPORT);
	}

	/**
	 * 跳转到数据导入
	 */
	private void taskImportInfo() {
		taskListFragment.doSomething("", taskListFragment.TASK_IMPORT_GUIDE);
	}

	// {{ showCopyCategoriesDialog

	private void showCopyCategoriesDialog2() {
		if (taskListFragment.viewModel.currentSelectedTask != null) {
			TaskInfo selectedTask = taskListFragment.viewModel.currentSelectedTask;
			Boolean isCreatedByUser = selectedTask.IsNew;
			ResultInfo<ArrayList<TaskCategoryInfoByTypeDTO>> taskCategoryByType = null;
			taskCategoryByType = TaskDataWorker.getTaskCatgoryInfoAndItem(
					selectedTask.TaskNum, isCreatedByUser ? selectedTask.ID
							: selectedTask.TaskID);
			if (taskCategoryByType.Success) {
				categoryItems = new LinkedHashMap<String, String>();
				for (TaskCategoryInfoByTypeDTO taskCategory : taskCategoryByType.Data) {
					categoryItems
							.put(String.valueOf(taskCategory
									.getTaskCatgoyInfo().ID), taskCategory
									.getTaskCatgoyInfo().RemarkName);
				}
				categoryArray = taskCategoryByType.Data;
				LayoutInflater inflater = LayoutInflater.from(currentContext);
				View view = inflater.inflate(R.layout.dialog_multiplechoice,
						null);
				MultilevelChoiceView multilevel = (MultilevelChoiceView) view
						.findViewById(R.id.CustomMultipleChoiceView);
				multilevel.setData(taskCategoryByType.Data);
				multilevel.setPopViewButListener(new PopViewButListener() {

					@Override
					public void onOkButClickListener(SparseBooleanArray sba,//
							SparseArray<SparseBooleanArray> sa) {

						ArrayList<TaskCategoryInfoByTypeDTO> tcibt = new ArrayList<TaskCategoryInfoByTypeDTO>();
						ArrayList<TaskCategoryInfoByTypeDTO> categorDto = new ArrayList<TaskCategoryInfoByTypeDTO>();
						Collections.addAll(categorDto,
								new TaskCategoryInfoByTypeDTO[categoryArray
										.size()]);
						Collections.copy(categorDto, categoryArray);
						for (int i = 0; i < sba.size(); i++) {
							TaskCategoryInfoByTypeDTO temp = categorDto.get(i);
							// 是否选中
							if (sba.get(i)) {
								TaskCategoryInfoByTypeDTO newCategory = getSelectCategory(
										i, temp, sa);
								tcibt.add(newCategory);
							}
						}
						if (tcibt.size() > 0) {
							// 记录选中的复制项
							HashMap<String, String> selectedItems = new HashMap<String, String>();
							// 记录选中项的子项复制项
							HashMap<String, String[]> selectedChildItems = new HashMap<String, String[]>();
							for (TaskCategoryInfoByTypeDTO t : tcibt) {
								TaskCategoryInfo tcinfo = t.getTaskCatgoyInfo();
								if (t.isMediaType()) {// 是媒体类型
									if (tcinfo.Items.size() > 0) {// 媒体类型是否选择了子项
										String[] cateItemName = new String[tcinfo.Items
												.size()];
										for (int i = 0; i < cateItemName.length; i++) {
											cateItemName[i] = tcinfo.Items
													.get(i).Name;
										}
										selectedChildItems.put(
												String.valueOf(tcinfo.ID),
												cateItemName);
										selectedItems.put(
												String.valueOf(tcinfo.ID),
												tcinfo.RemarkName);
									}
								} else {// 不是媒体类型直接添加
									selectedItems.put(
											String.valueOf(tcinfo.ID),
											tcinfo.RemarkName);
								}
							}
							// 保存选中复制的分类
							taskListFragment.viewModel.selectedCategoryItems = selectedItems;
							// 保存选中复制的分类的子项
							taskListFragment.viewModel.selectedCategoryChildItems = selectedChildItems;
							taskListFragment.viewModel.currentCopiedTask = taskListFragment.viewModel.currentSelectedTask;
							setDoingTaskinfoMenuCopyValue(selectedItems);
							ToastUtil
									.longShow(currentContext, "复制完成，您可以选择粘贴操作");
						} else {
							taskListFragment.viewModel.currentCopiedTask = null;
							ToastUtil.longShow(currentContext, "没有指定任务复制项");
						}
						stationSelectDialog.dismiss();
					}

					@Override
					public void onCancelButClickListener() {
						stationSelectDialog.dismiss();

					}
				});
				// 加载适应屏幕的PopupWindow
				loadSuitableDialog(view);
				stationSelectDialog.showAtLocation(taskListFragment.mView,
						Gravity.CENTER, 0, 0);
			} else {
				ToastUtil.longShow(currentContext, "查不到当前任务的分类项目!");
			}
		}

	}

	/**
	 * 
	 * @author kevin
	 * @date 2015-10-23 上午11:00:17
	 * @Description: 获取已选中分类项 DTO对象， 过滤子项的选择
	 * @param parpsc
	 *            父分类项下标
	 * @param temp
	 *            分类项DTO
	 * @param sa
	 *            所有子项选中状态
	 * @return TaskCategoryInfoByTypeDTO 新的DTO
	 */
	private TaskCategoryInfoByTypeDTO getSelectCategory(int parpsc,
			TaskCategoryInfoByTypeDTO temp, SparseArray<SparseBooleanArray> sa) {
		TaskCategoryInfoByTypeDTO taskCate = new TaskCategoryInfoByTypeDTO();
		List<TaskDataItem> itemTemp = new ArrayList<TaskDataItem>();
		itemTemp.addAll(temp.getTaskCatgoyInfo().Items);// 原来的ITEM
		// 得到选中的图片类型子项
		ArrayList<TaskDataItem> dataItems = new ArrayList<TaskDataItem>();
		TaskCategoryInfo tcif = temp.getTaskCatgoyInfo();

		if (temp.isMediaType() && ListUtil.hasData(itemTemp)) {// 是不是媒体类型//是否有子项
			int i = 0;
			for (TaskDataItem item : itemTemp) {
				if (sa.get(parpsc).get(i, false)) {// 子项是否有勾选
					dataItems.add(item);
				}
				i++;
			}
			tcif.Items = dataItems;
		}

		taskCate.setTaskCatgoyInfo(tcif);
		taskCate.setDataCategoryDefine(temp.getDataCategoryDefine());
		return taskCate;
	}

	/**
	 * 执行点击复制后的操作
	 */
	@SuppressLint("InflateParams")
	private void showCopyCategoriesDialog() {
		if (taskListFragment.viewModel.currentSelectedTask != null) {
			TaskInfo selectedTask = taskListFragment.viewModel.currentSelectedTask;
			Boolean isCreatedByUser = selectedTask.IsNew;
			selectedTask.Categories = TaskDataWorker.queryTaskCategories(
					isCreatedByUser ? selectedTask.ID : selectedTask.TaskID,
					isCreatedByUser, false).Data;

			categoryItems = new LinkedHashMap<String, String>();
			for (TaskCategoryInfo taskCategory : selectedTask.Categories) {
				categoryItems.put(String.valueOf(taskCategory.ID),
						taskCategory.RemarkName);
			}

			LayoutInflater inflater = LayoutInflater.from(currentContext);
			View view = inflater.inflate(R.layout.dialog_multiplechoice, null);
			CustomMultipleChoiceView mutipleChoiceView = (CustomMultipleChoiceView) view
					.findViewById(R.id.CustomMultipleChoiceView);
			mutipleChoiceView.setData(categoryItems, null);
			mutipleChoiceView.selectAll();
			mutipleChoiceView.setTitle("选择要复制的分组");
			// 加载适应屏幕的PopupWindow
			loadSuitableDialog(view);
			// 确定
			mutipleChoiceView.setOnSelectedListener(new onSelectedListener() {
				@Override
				public void onSelected(SparseBooleanArray sparseBooleanArray) {
					stationSelectDialog.dismiss();
					// 记录选中的复制项
					HashMap<String, String> selectedItems = new HashMap<String, String>();
					Object[] keys = categoryItems.keySet().toArray();
					for (int i = 0; i < sparseBooleanArray.size(); i++) {
						if (sparseBooleanArray.get(i)) {
							selectedItems.put(keys[i].toString(),
									categoryItems.get(keys[i]));
						}
					}
					// 保存选中复制的分类
					taskListFragment.viewModel.selectedCategoryItems = selectedItems;
					if (selectedItems.size() > 0) {
						// 点确定之后，把当前要的任务保存
						taskListFragment.viewModel.currentCopiedTask = taskListFragment.viewModel.currentSelectedTask;
						setDoingTaskinfoMenuCopyValue(selectedItems);
						ToastUtil.longShow(currentContext, "复制完成，您可以选择粘贴操作");
					} else {
						taskListFragment.viewModel.currentCopiedTask = null;
						ToastUtil.longShow(currentContext, "没有指定任务复制项");
					}
				}
			});
			// 取消
			mutipleChoiceView.setOnCancelListener(new onCancelListener() {
				@Override
				public void onCancel() {
					stationSelectDialog.dismiss();
				}
			});
		}
		stationSelectDialog.showAtLocation(taskListFragment.mView,
				Gravity.CENTER, 0, 0);
	}

	/**
	 * 将已完成的复制值赋值给待提交中的复制值
	 */
	private void setDoingTaskinfoMenuCopyValue(
			HashMap<String, String> selectedItems) {
		TaskListMenuOperaotr taskListMenuOperaotr = taskListFragment.viewModel.homeActivity.taskListMenuOperaotrs
				.get(TaskStatus.Doing);
		if (taskListMenuOperaotr != null) {
			taskListMenuOperaotr.taskListFragment.viewModel.selectedCategoryItems = selectedItems;
			if (selectedItems.size() > 0) {
				taskListMenuOperaotr.taskListFragment.viewModel.currentCopiedTask = taskListFragment.viewModel.currentSelectedTask;
			}
		}
	}

	/**
	 * 加载适应屏幕的视图
	 * 
	 * @param view
	 *            :绑定的视图
	 */
	private void loadSuitableDialog(View view) {
		// 取得屏幕的宽高
		Point point = WinDisplay.getWidthAndHeight(currentContext);
		switch (EIASApplication.PageSize) {
		case 6:// 手机
			if (categoryItems.size() >= 0 && categoryItems.size() < 6) {
				stationSelectDialog = new PopupWindow(view,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						true);
			} else {
				stationSelectDialog = new PopupWindow(view,
						LayoutParams.WRAP_CONTENT, (int) ((point.y) * (0.7)),
						true);
			}
			break;
		case 15:// 平板
			if (categoryItems.size() >= 0 && categoryItems.size() < 8) {
				stationSelectDialog = new PopupWindow(view,
						(int) ((point.x) * (0.65)), LayoutParams.WRAP_CONTENT,
						true);
			} else {
				stationSelectDialog = new PopupWindow(view,
						(int) ((point.x) * (0.65)), (int) ((point.y) * (0.6)),
						true);
			}
			break;
		}
	}

	// }}

	// {{ 提交当前任务

	/**
	 * 批量上传任务
	 * 
	 * @param finalIsRepeat
	 *            是否重复
	 * @param ddid
	 *            勘察表ID
	 * @param unWriteItems
	 *            不写入项
	 * @param taskInfo
	 *            任务信息
	 */
	private void commitTasks(final List<TaskInfo> submitTaskInfos) {
		try {
			String feeMsg = "";
			String taskReportMsg = "";
			for (TaskInfo taskItem : submitTaskInfos) {
				if (taskItem.Fee == null || taskItem.Fee.equals("")
						|| taskItem.Fee.equals("null")) {
					feeMsg += taskItem.TargetAddress + ",";
				}
				if (taskItem.InworkReportFinish) {
					taskReportMsg += "[" + taskItem.TaskNum + "]";
				}
			}
			if (!taskReportMsg.equals("")) {
				taskListFragment.viewModel.homeActivity.appHeader.showDialog(
						"提示信息", "编号为" + taskReportMsg + "的任务已经完成报告，不能继续提交");
			} else {
				// 判断收费是否存在
				if (feeMsg != null && !feeMsg.equals("")
						&& !feeMsg.equals("null")) {
					// 移除最后一个逗号
					feeMsg = feeMsg.substring(0, feeMsg.length() - 1);
					if (!homeActivity.isFinishing()) {
						DialogUtil.showConfirmationDialog(currentContext,
								feeMsg + " 的收费信息尚未填写,是否确定提交任务?",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										for (TaskInfo taskInfo : submitTaskInfos) {
											checkTaskFile(taskInfo.DDID,
													taskInfo.TaskNum);
										}
									}
								});
					}

				} else {
					for (TaskInfo taskInfo : submitTaskInfos) {
						checkTaskFile(taskInfo.DDID, taskInfo.TaskNum);
					}
				}
			}
		} catch (Exception e) {
			DataLogOperator.other("多任务提交失败(commitTasks):" + e.getMessage());
		}
	}

	/**
	 * 检查并上传任务
	 * 
	 * @param ddid任务使用的勘察表
	 * @param taskNum任务编号
	 * @param fee费用
	 */
	public void putTaskInfo(final Integer ddid, final String taskNum, String fee) {
		// 判断收费是否存在
		if (fee == null || fee.equals("") || fee.equals("null")) {
			DialogUtil.showConfirmationDialog(currentContext,
					"收费信息尚未填写，是否确定提交任务?",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkTaskFile(ddid, taskNum);
						}
					});
		} else {
			checkTaskFile(ddid, taskNum);
		}
	}

	/**
	 * 上传任务方法
	 * 
	 * @param ddid
	 *            勘察表ID
	 * @param taskNum
	 *            任务编号
	 */
	private void checkTaskFile(Integer ddid, String taskNum) {
		try {
			TaskInfo taskInfo = TaskOperator
					.getLocTaskInfoCompleteByTaskNum(taskNum);
			String taskRoot = EIASApplication.projectRoot + taskInfo.TaskNum;
			// 检测任务信息是否正确
			ArrayList<DialogTipsDTO> taskFormat = TaskOperator.taskCheckFormat(
					taskInfo, taskRoot);
			// 若任务信息不正确则直接提示并返回
			if (taskFormat != null && taskFormat.size() > 0) {
				if (homeActivity != null) {
					homeActivity.appHeader.showDialogResult("请修正以下信息：",
							taskFormat, false, null);
				} else if (taskInfoActivity != null) {
					taskInfoActivity.appHeader.showDialogResult("请修正以下信息：",
							taskFormat, false, null);
				}
			} else {
				ArrayList<TaskCategoryInfo> unWriteItems = TaskOperator
						.checkHasValue(ddid, taskInfo);
				if (unWriteItems.size() > 0) {
					LayoutInflater inflater = LayoutInflater
							.from(currentContext);
					View dialog_view_task_submit = inflater.inflate(
							R.layout.dialog_view_task_submit, null);

					final Dialog detailDialog = DialogUtil.getDetailDialog(
							currentContext, dialog_view_task_submit);

					TextView dialog_taskNumInfo = (TextView) dialog_view_task_submit
							.findViewById(R.id.dialog_taskNumInfo);
					Button btn_confirm = (Button) dialog_view_task_submit
							.findViewById(R.id.btn_confirm);
					ListView bdialog_tipsinfo = (ListView) dialog_view_task_submit
							.findViewById(R.id.bdialog_tipsinfo);

					taskunwriteadapter = new TaskUnWriteAdapter(currentContext,
							unWriteItems);
					bdialog_tipsinfo.setAdapter(taskunwriteadapter);
					dialog_taskNumInfo.setText("任务编号[" + taskInfo.TaskNum
							+ "]小区名:" + taskInfo.ResidentialArea + "地址:"
							+ taskInfo.TargetAddress);
					btn_confirm.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							detailDialog.dismiss();
						}
					});
					if (detailDialog.isShowing()) {
						detailDialog.dismiss();
					} else {
						detailDialog.show();
					}
				} else {
					taskListFragment.refreshList(taskInfo.TaskNum);
					// 开始提交任务
					TaskOperator.taskSubmiting(taskInfo);
				}
			}
		} catch (Exception e) {
			DataLogOperator.other("检查文件(checkTaskFile):" + e.getMessage());
		}
	}

	/**
	 * 是否有新的勘察表
	 * 
	 * @param ddid勘察配置表ID
	 * @return
	 */
	public boolean hasNewDataDefines(int ddid) {
		for (DataDefine dataDefine : EIASApplication.currentUpdateDataDefines) {
			if (dataDefine.DDID == ddid) {
				homeActivity.appHeader.showDialog("提示信息", "请同步勘察表["
						+ dataDefine.Name + "]");
				return true;
			}
		}
		return false;
	}

	/***
	 * @author kevin
	 * @param DDID
	 *            勘察配置表ID
	 * @param DataDfineVersion
	 *            勘察表版本号
	 * @return
	 */
	private void serverHasNewDataDefines(int DDID, int DataDfineVersion) {

		taskListFragment.doSomething("检查中...",
				taskListFragment.CHEK_TASK_VERSION);
		/*
		 * 
		 * if (result.Success && result.Data) { return true; }
		 * homeActivity.appHeader.showDialog("提示信息", result.Message);
		 * 
		 * return false;
		 */
	}

	/**
	 * 已经完成报告的任务不能提交
	 * 
	 * @param taskInfo
	 * @return
	 */
	private boolean checkTaskReportFinish(TaskInfo taskInfo) {
		if (taskInfo.InworkReportFinish) {
			homeActivity.appHeader.showDialog("提示信息", taskInfo.TaskNum
					+ "已经完成报告的任务不能提交!");
			return true;
		}
		return false;
	}

	// }}

	// {{ 移除任务资源

	/**
	 * 移除任务资源
	 */
	private void removeTaskResource() {
		taskListFragment.doSomething("", taskListFragment.TASK_REMOVE_RESOURCE);
	}

	// }}

	// {{ 查询已经完成报告的任务

	/**
	 * 查询已经完成报告的任务
	 */
	private void queryFinishReport() {
		taskListFragment.viewModel.onlyReportFinish = true;
		taskListFragment.loadData();
	}

	// }}

	// {{差全部已完成任务

	/**
	 * 全部已完成任务
	 */
	private void queryFinishTask() {
		taskListFragment.viewModel.onlyReportFinish = false;
		taskListFragment.loadData();
	}

	// }}
}