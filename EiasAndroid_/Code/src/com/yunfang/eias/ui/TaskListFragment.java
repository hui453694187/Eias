package com.yunfang.eias.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.base.MainService;
import com.yunfang.eias.dto.DialogTipsDTO;
import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.eias.enumObj.LogType;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.enumObj.SortType;
import com.yunfang.eias.enumObj.TaskMenuEnum;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.logic.HomeOperator;
import com.yunfang.eias.logic.TaskListMenuOperaotr;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.DataLogWorker;
import com.yunfang.eias.ui.Adapter.TaskListViewAdapter;
import com.yunfang.eias.view.PullToRefreshLayout;
import com.yunfang.eias.view.PullToRefreshLayout.OnRefreshListener;
import com.yunfang.eias.viewmodel.TaskListViewModel;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 
 * 项目名称：WaiCai 类名称：TaskListFragment 类描述：任务列表公共的Fragment 创建人：lihc 创建时间：2014-4-22
 * 上午11:17:57
 * 
 * @version
 */
public class TaskListFragment extends BaseWorkerFragment implements
		OnClickListener {

	// {{ 属性变量
	/**
	 * 待勘察任务对应的ListView
	 * */
	private ListView task_listview;

	private PullToRefreshLayout taskPullTorefreshLayout;

	/** 是否处于下拉刷新中 */
	private boolean isLoad;

	/**
	 * 任务查询输入框
	 * */
	private EditText serach_editText;

	/***
	 * 全选
	 */
	private CheckBox sub_title_select;

	/**
	 * 任务查询按钮
	 * */
	private ImageView serachBtn;

	/**
	 * 任务列表刷新功能
	 * */
	private Button reload_btn;

	/**
	 * 当前SurveyFragment对应的视图
	 * */
	public View mView;

	/**
	 * 任务ViewModel
	 * */
	public TaskListViewModel viewModel = new TaskListViewModel();

	/**
	 * 任务列表的Title
	 */
	public TextView titleTextView;

	/** 列表总数 */
	/*
	 * public TextView home_top_listCount;
	 */

	/**
	 * 任务列表查询控件
	 */
	private LinearLayout taskSearch;

	/**
	 * ListView的Item项装配器
	 * */
	private TaskListViewAdapter taskListViewAdapter = null;

	/**
	 * 网络广播事件响应
	 */
	private BaseBroadcastReceiver broadcastReceiver;
	// }}

	// {{ 任务值
	/**
	 * 执行获取任务数据标志
	 * */
	public final int TASK_GETTASKINFOES = 0;

	/**
	 * 领取任务
	 */
	public final int TASK_RECEIVETASK = 1;

	/**
	 * 设置项目收费信息
	 */
	public final int TASK_SETFEEINFO = 2;

	/**
	 * 暂停任务
	 */
	public final int TASK_SETPAUSE = 3;

	/**
	 * 编辑任务信息
	 */
	public final int TASK_EDIT_TASKINFO = 4;

	/**
	 * 复制点击任务的勘察类型
	 */
	public final int TASK_COPY_TASKINFO = 5;

	/**
	 * 粘贴任务
	 */
	public final int TASK_PASTED_TASKINFO = 6;

	/**
	 * 粘贴到新建任务
	 */
	public final int TASK_PASTED_NEWTASKINFO = 7;

	/**
	 * 领取任务并编辑
	 */
	public final int TASK_RECEIVETASK_AND_EDIT = 8;
	/** 批量删除任务 */
	public final int TASK_BATCH_DELETE = 9;

	/**
	 * 删除任务
	 */
	public final int TASK_DELETE = 10;

	/**
	 * 任务数据导出
	 */
	public final int TASK_EXPORT = 11;

	/**
	 * 任务数据导入
	 */
	public final int TASK_IMPORT_GUIDE = 12;

	/**
	 * 任务压缩包导出操作
	 */
	public final int TASK_EXPORT_ZIP = 13;

	/**
	 * 资源补发
	 */
	public final int TASK_ADDITIONAL_PICTRUE = 14;

	/**
	 * 预约信息
	 */
	public final int TASK_APPOINTMENT = 15;

	/**
	 * 删除资源
	 */
	public final int TASK_REMOVE_RESOURCE = 16;

	/** 同步一条已完成任务数据 */
	public final int SYNC_TASK_INFO = 17;

	/** 启用一条已经暂停的任务 */
	public final int TASK_RESTART_TASK = 18;

	/** 添加任务备注 */
	public final int TASK_REMARK = 20;

	/** 检查任务勘察表版本号与服务器是否一致 */
	public final int CHEK_TASK_VERSION = 21;

	/** 刷新待领取的同时检查版本更新，和勘察表版本 */
	public final int CHEK_VERSION = 22;

	/** 同步任务信息 */
	public final int HANDLE_SYN = 100;

	/**
	 * HomeFragment
	 */
	public HomeFragment homeFragment;

	private TaskListMenuOperaotr menuOperator;

	// }}

	/**
	 * 设置Title值
	 * 
	 * @param titleName
	 */
	private void setTitle(String titleName) {
		titleTextView.setText(titleName);
	}

	// {{ 基类创建重载方法

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		getLastSortType();
		synData();
	}

	/***
	 * 获取上次排序的类型
	 */
	private void getLastSortType() {
		viewModel.sortType = TaskOperator.getSortStatus(viewModel.taskStatus);
		if (viewModel.sortType != null) {
			setSearchBarRdgStatus(getIdBySortType(viewModel.sortType),
					viewModel.sortType);
		}

	}

	// }}

	// {{ 进程调用重载类

	/**
	 * 后台线程
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void handlerBackgroundHandler(Message msg) {
		viewModel.ToastMsg = "";
		// 准备发送给UI线程的消息对象
		Message uiMsg = new Message();
		uiMsg.what = msg.what;
		switch (msg.what) {
		case HANDLE_SYN: {
			synchroTaskInfo();
			break;
		}
		case TASK_GETTASKINFOES:
			uiMsg.obj = TaskOperator.getTaskInfoes(viewModel.currentIndex,
					viewModel.pageSize, serach_editText.getText().toString(),
					viewModel.taskStatus, viewModel.currentUser,
					viewModel.remoteTotal, viewModel.localTotal > 0 ? true
							: false, viewModel.onlyReportFinish,
					viewModel.sortType);
			// 加载待提交数据，同时检查更新, 和勘察表是否同步
			if (viewModel.taskStatus == TaskStatus.Todo) {
				mBackgroundHandler.sendEmptyMessage(CHEK_VERSION);
			}
			break;
		case TASK_RECEIVETASK:
			uiMsg.obj = TaskOperator.receiveTask(viewModel.currentUser,
					viewModel.currentSelectedTask);
			break;
		case TASK_RECEIVETASK_AND_EDIT:
			uiMsg.obj = TaskOperator.receiveTask(viewModel.currentUser,
					viewModel.currentSelectedTask);
			break;
		case TASK_SETFEEINFO:
			uiMsg.obj = TaskOperator.setTaskFee(viewModel.homeActivity,
					viewModel.currentUser, viewModel.currentSelectedTask);
			break;
		case TASK_APPOINTMENT:
			TaskMenuEnum menu = (TaskMenuEnum) msg.obj;
			ResultInfo<Boolean> tempResult = new ResultInfo<Boolean>();
			if (menu == TaskMenuEnum.预约信息) {
				tempResult = TaskOperator.setTaskAppointment(
						viewModel.homeActivity, viewModel.currentUser,
						viewModel.currentSelectedTask);
			}
			tempResult.Others = msg.obj;
			uiMsg.obj = tempResult;
			break;
		case TASK_SETPAUSE:
			uiMsg.obj = TaskOperator.setTaskRejectInfo(viewModel.currentUser,
					viewModel.currentSelectedTask);
			break;
		case TASK_DELETE:
			try {
				uiMsg.obj = TaskOperator.deleteLoaclTask(viewModel.currentUser,
						viewModel.currentSelectedTask);
				titleTextView.setTag((Integer) titleTextView.getTag() - 1);
			} catch (Exception e) {
				DataLogWorker.createDataLog(viewModel.currentUser, "修改任务总数出错",
						OperatorTypeEnum.Other, LogType.Exection);
			}
			break;
		case TASK_BATCH_DELETE:// 批量删除任务。
			List<TaskInfo> taskInfos = (List<TaskInfo>) msg.obj;
			List<TaskInfo> deleteTtask = new ArrayList<TaskInfo>();
			if (taskInfos != null && taskInfos.size() > 0) {
				for (TaskInfo taskInfo : taskInfos) {// 循环执行 删除任务
					ResultInfo<Integer> result = TaskOperator.deleteLoaclTask(
							viewModel.currentUser, taskInfo);
					if (result.Success && result.Data > 0) {
						deleteTtask.add(taskInfo);// 删除成功的任务
					}
				}
			}
			try {
				titleTextView.setTag((Integer) titleTextView.getTag()
						- deleteTtask.size());
			} catch (Exception e) {
				DataLogWorker.createDataLog(viewModel.currentUser, "修改任务总数出错",
						OperatorTypeEnum.Other, LogType.Exection);
			}
			uiMsg.obj = deleteTtask;
			break;
		case TASK_EDIT_TASKINFO:
			uiMsg.obj = openTaskInfo(viewModel.currentSelectedTask.TaskID,
					viewModel.currentSelectedTask.ID,
					viewModel.currentSelectedTask.TaskNum);
			break;
		case TASK_COPY_TASKINFO:
			// uiMsg.obj =
			// TaskOperator.showCopyCategoriesDialog(viewModel.homeActivity,
			// viewModel.currentSelectedTask, viewModel,mView);
			break;
		case TASK_PASTED_TASKINFO:
			/*
			 * uiMsg.obj = TaskOperator.pastedTaskInfo(
			 * viewModel.currentCopiedTask, viewModel.selectedCategoryItems,
			 * viewModel.selectedCategoryChildItems,
			 * viewModel.currentSelectedTask,
			 * OperatorTypeEnum.CategoryDefineDataCopy);
			 */
			//
			// 支持不同勘察表任务粘贴
			uiMsg.obj = TaskOperator.pastedTaskInfo2(
					viewModel.currentCopiedTask,
					viewModel.selectedCategoryItems,
					viewModel.selectedCategoryChildItems,
					viewModel.currentSelectedTask,
					OperatorTypeEnum.CategoryDefineDataCopy);
			break;
		case TASK_PASTED_NEWTASKINFO:
			uiMsg.obj = showCreateNewTaskActivity();
			break;
		case TASK_EXPORT:
			uiMsg.obj = TaskOperator
					.taskExportCheck(viewModel.currentSelectedTask.TaskNum);
			break;
		case TASK_IMPORT_GUIDE:

			break;
		case TASK_ADDITIONAL_PICTRUE:
			uiMsg.obj = openTaskInfo(viewModel.currentSelectedTask.TaskID,
					viewModel.currentSelectedTask.ID,
					viewModel.currentSelectedTask.TaskNum, true);
			break;
		case TASK_EXPORT_ZIP:
			viewModel.homeActivity.appHeader.dialog_result.dismiss();
			ResultInfo<ArrayList<DialogTipsDTO>> result = TaskOperator
					.taskExport(viewModel.currentSelectedTask.TaskNum);
			uiMsg.obj = result;
			break;
		case TASK_REMOVE_RESOURCE: {
			ResultInfo<String> removeResult = new ResultInfo<>();
			ArrayList<TaskInfo> allTaskInfo = viewModel.taskInfoes;
			ArrayList<TaskInfo> selectTaskInfo = new ArrayList<TaskInfo>();
			ArrayList<DialogTipsDTO> taskFormat = new ArrayList<DialogTipsDTO>();
			for (TaskInfo taskItem : allTaskInfo) {
				if (taskItem.isChecked) {
					selectTaskInfo.add(taskItem);
				}
			}
			for (TaskInfo selectTask : selectTaskInfo) {
				if (!selectTask.InworkReportFinish) {
					DialogTipsDTO tips = new DialogTipsDTO();
					tips.Concent = selectTask.TaskNum + "报告未完成";
					tips.Category = CategoryType.Normal;
					taskFormat.add(tips);
				}
			}
			if (taskFormat.size() > 0) {
				removeResult.Data = "";
				removeResult.Success = false;
				removeResult.Others = taskFormat;
			} else {
				int successCount = 0;
				for (TaskInfo taskItem : selectTaskInfo) {
					FileUtil.delDir(EIASApplication.projectRoot
							+ taskItem.TaskNum);
					File deleteDie = new File(EIASApplication.projectRoot
							+ taskItem.TaskNum);
					if (!deleteDie.exists()) {
						taskItem.HasResource = false;
						successCount += taskItem.onUpdate("TaskNum = '"
								+ taskItem.TaskNum + "'");
					} else {
						DialogTipsDTO tips = new DialogTipsDTO();
						tips.Concent = taskItem.TaskNum + "清除失败";
						tips.Category = CategoryType.Normal;
						taskFormat.add(tips);
					}
				}
				if (successCount == selectTaskInfo.size()) {
					removeResult.Data = "操作完成";
					removeResult.Message = "操作完成";
					removeResult.Success = true;
				} else {
					removeResult.Data = "操作失败";
					removeResult.Message = "操作完成";
					removeResult.Success = false;
					removeResult.Others = taskFormat;
				}
			}
			uiMsg.obj = removeResult;
			break;
		}
		case SYNC_TASK_INFO:
			// 发送已完成任务 数据， 后去需要同步的数据
			uiMsg.obj = TaskOperator
					.syncDoneTaskInfo(this.viewModel.currentSelectedTask);
			break;
		case TASK_RESTART_TASK:
			// 后台执行启用暂停的任务
			uiMsg.obj = TaskOperator.restartTask(
					EIASApplication.getCurrentUser(),
					this.viewModel.currentSelectedTask);
			break;

		case TASK_REMARK:
			// 执行任务备注请求
			boolean isDoing = true;
			if (viewModel.taskStatus != TaskStatus.Doing) {
				isDoing = false;
			}
			try {
				uiMsg.obj = TaskOperator.taskRemark(
						EIASApplication.getCurrentUser(),
						this.viewModel.currentSelectedTask, isDoing);
			} catch (Exception e) {
				ResultInfo<Boolean> errResult = new ResultInfo<Boolean>();
				errResult.Success = false;
				errResult.Message = "请稍后再试！^_^";
				DataLogOperator.taskHttp("TaskListFragment=>备注任务失败(438)",
						e.getMessage());
				uiMsg.obj = errResult;
				e.printStackTrace();
			}
			break;
		case CHEK_TASK_VERSION:// 对比服务器勘察表版本号，再提交，否则不可以提交
			uiMsg.obj = TaskOperator
					.checkServerDataDefinesVersion(this.viewModel.currentSelectedTask.DDID);
			break;
		case CHEK_VERSION:
			uiMsg.obj = HomeOperator
					.checkVersionAndDefinVersion(this.viewModel.currentUser);
			break;
		default:
			break;
		}
		// 发信息给UI线程
		mUiHandler.sendMessage(uiMsg);
	}

	/**
	 * 界面线程
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handUiMessage(Message msg) {
		boolean colseLoading = true;
		super.handUiMessage(msg);
		switch (msg.what) {
		case HANDLE_SYN: {
			loadData();
			viewModel.reload = false;
			break;
		}
		case TASK_GETTASKINFOES: {
			ResultInfo<ArrayList<TaskInfo>> result = (ResultInfo<ArrayList<TaskInfo>>) msg.obj;
			// 没有数据并且是只显示完成报告任务不刷新
			if (result.Others != null && result.Others.toString().contains("0")
					&& viewModel.onlyReportFinish) {
				// if (viewModel.onlyReportFinish && result.Data.size() <= 0) {
				viewModel.onlyReportFinish = false;
				viewModel.reload = false;
				viewModel.homeActivity.appHeader.showDialog("提示信息",
						"暂时没有已经完成报告的任务");
			} else {
				viewModel.GetDataSuccess = result.Success;
				if (result.Success) {
					if (result.Data != null && result.Data.size() >= 0) {
						// 根据任务获取类型添加任务信息
						addTaskInfoe(result);
						showSubmitingStatus();
					}
					viewModel.reload = true;
					// 获取任务总数，绑定在控件上
					int taskCount = 0;
					taskCount = setTaskCount(result.Others == null ? "0"
							: result.Others.toString());
					titleTextView.setTag(taskCount);

				} else {
					viewModel.GetDataSuccess = false;
					viewModel.currentIndex = viewModel.currentIndex > 1 ? viewModel.currentIndex - 1
							: 1;
					if (result.Message == null || result.Message.isEmpty()) {
						viewModel.ToastMsg = "任务领取失败";
					} else {
						// 若是提交中断网后刷新的待提交信息,则不显示错误信息
						if (viewModel.hideOfflineMsg
								&& viewModel.taskStatus == TaskStatus.Doing) {
							viewModel.hideOfflineMsg = false;
						} else {
							viewModel.ToastMsg = result.Message;
						}
					}
					viewModel.reload = false;
				}
			}
		}
			break;
		case TASK_RECEIVETASK: {
			ResultInfo<Long> resultReceive = (ResultInfo<Long>) msg.obj;
			viewModel.GetDataSuccess = resultReceive.Success;
			if (resultReceive.Success && resultReceive.Data > 0) {
				// ResultInfo<Boolean> appointmentResult =
				// TaskOperator.setTaskAppointment(viewModel.homeActivity,
				// viewModel.currentUser, viewModel.currentSelectedTask);
				viewModel.taskInfoes.remove(viewModel.currentSelectedTask);
				viewModel.currentSelectedTask = null;
				viewModel.ToastMsg = "任务领取成功";
				viewModel.reload = true;
				// if (!appointmentResult.Data) {
				// viewModel.ToastMsg += "," + appointmentResult.Message;
				// }
			} else {
				viewModel.GetDataSuccess = false;
				if (resultReceive.Message == null
						|| resultReceive.Message.isEmpty()) {
					viewModel.ToastMsg = "任务领取失败";
				} else {
					viewModel.ToastMsg = resultReceive.Message;
				}
				viewModel.reload = false;
			}
		}
			break;
		case TASK_RECEIVETASK_AND_EDIT: {
			ResultInfo<Long> resultReceiveAndEdit = (ResultInfo<Long>) msg.obj;
			viewModel.GetDataSuccess = resultReceiveAndEdit.Success;
			if (resultReceiveAndEdit.Success && resultReceiveAndEdit.Data > 0) {
				// ResultInfo<Boolean> appointmentResult =
				// TaskOperator.setTaskAppointment(viewModel.homeActivity,
				// viewModel.currentUser, viewModel.currentSelectedTask);
				TaskInfo task = viewModel.currentSelectedTask;
				viewModel.taskInfoes.remove(viewModel.currentSelectedTask);
				viewModel.currentSelectedTask = null;
				viewModel.ToastMsg = "任务领取成功";
				viewModel.reload = true;
				// if (!appointmentResult.Data) {
				// viewModel.ToastMsg += "," + appointmentResult.Message;
				// }
				viewModel.homeActivity.changFragment(2);
				openTaskInfo(task.TaskID, task.ID, task.TaskNum);
			} else {
				viewModel.GetDataSuccess = false;
				if (resultReceiveAndEdit.Message == null
						|| resultReceiveAndEdit.Message.isEmpty()) {
					viewModel.ToastMsg = "任务领取失败";
				} else {
					viewModel.ToastMsg = resultReceiveAndEdit.Message;
				}
				viewModel.reload = false;
			}
		}
			break;
		case TASK_SETFEEINFO: {
			ResultInfo<Boolean> resultSetFee = (ResultInfo<Boolean>) msg.obj;
			if (resultSetFee.Success && resultSetFee.Data) {
				viewModel.ToastMsg = "任务收费信息保存";
			} else {
				viewModel.ToastMsg = resultSetFee.Message;
			}
			viewModel.reload = false;
		}
			break;
		case TASK_APPOINTMENT: {
			colseLoading = false;
			ResultInfo<Boolean> result = (ResultInfo<Boolean>) msg.obj;
			TaskMenuEnum menu = (TaskMenuEnum) result.Others;
			switch (menu) {
			case 领取任务:
				/*
				 * doSomething("任务领取中", TASK_RECEIVETASK); break; case 领取任务并编辑:
				 * doSomething("任务领取中", TASK_RECEIVETASK_AND_EDIT); break;
				 */
			case 预约信息:
				colseLoading = true;
				viewModel.ToastMsg = result.Message;
				break;
			default:
				break;
			}
		}
			break;
		case TASK_SETPAUSE:
			ResultInfo<Boolean> resultSetReject = (ResultInfo<Boolean>) msg.obj;
			if (resultSetReject.Success && resultSetReject.Data) {
				// viewModel.currentSelectedTask = null;
				viewModel.GetDataSuccess = true;
				viewModel.ToastMsg = "任务已暂停";
				viewModel.reload = true;
			} else {
				viewModel.ToastMsg = resultSetReject.Message;
				viewModel.reload = false;
			}
			break;
		case TASK_DELETE:
			ResultInfo<Integer> resultDelete = (ResultInfo<Integer>) msg.obj;
			if (resultDelete.Success && resultDelete.Data > 0) {
				viewModel.taskInfoes.remove(viewModel.currentSelectedTask);
				// 删除的任务编号和已经复制的任务编号一样的时候就清空已经复制的
				if (viewModel.currentCopiedTask != null
						&& viewModel.currentSelectedTask.TaskNum
								.equals(viewModel.currentCopiedTask.TaskNum)) {
					viewModel.currentCopiedTask = null;
				}
				viewModel.currentSelectedTask = null;
				viewModel.GetDataSuccess = true;
				viewModel.ToastMsg = "任务已删除";
				viewModel.reload = true;
			} else {
				viewModel.ToastMsg = resultDelete.Message;
				viewModel.reload = false;
			}
			break;
		case TASK_BATCH_DELETE:
			List<TaskInfo> taskInfos = (List<TaskInfo>) msg.obj;
			if (taskInfos != null && taskInfos.size() > 0) {
				for (TaskInfo tempTask : taskInfos) {
					viewModel.taskInfoes.remove(tempTask);
					if (viewModel.currentCopiedTask != null
							&& viewModel.currentSelectedTask.TaskNum
									.equals(viewModel.currentCopiedTask.TaskNum)) {
						viewModel.currentCopiedTask = null;
					}
					viewModel.GetDataSuccess = true;
					viewModel.ToastMsg = "批量删除任务成功！";
					viewModel.reload = true;
				}
			}
			break;
		case TASK_EDIT_TASKINFO: {
			ResultInfo<Boolean> resultEditTaskInfo = (ResultInfo<Boolean>) msg.obj;
			if (resultEditTaskInfo.Success && resultEditTaskInfo.Data) {
				viewModel.currentSelectedTask = null;
				viewModel.GetDataSuccess = true;
				viewModel.reload = false;
			} else {
				viewModel.ToastMsg = resultEditTaskInfo.Message;
				viewModel.reload = false;
			}
			break;
		}
		case TASK_COPY_TASKINFO:
			// ResultInfo<Boolean> resultCopyTaskInfo = (ResultInfo<Boolean>)
			// msg.obj;
			// if (resultCopyTaskInfo.Success && resultCopyTaskInfo.Data) {
			// viewModel.currentSelectedTask = null;
			// viewModel.GetDataSuccess = true;
			// } else {
			// viewModel.ToastMsg = resultCopyTaskInfo.Message;
			// }
			// viewModel.reload = false;
			break;
		case TASK_PASTED_TASKINFO:
			ResultInfo<Boolean> resultPastedTaskInfo = (ResultInfo<Boolean>) msg.obj;
			if (resultPastedTaskInfo.Success && resultPastedTaskInfo.Data) {
				viewModel.currentSelectedTask = null;
				viewModel.GetDataSuccess = true;
			} else {
				viewModel.ToastMsg = resultPastedTaskInfo.Message;
			}
			viewModel.reload = false;
			ToastUtil.longShow(viewModel.homeActivity, "复制完成");
			break;
		case TASK_PASTED_NEWTASKINFO:
			ResultInfo<Boolean> resultPastedNewTaskInfo = (ResultInfo<Boolean>) msg.obj;
			if (resultPastedNewTaskInfo.Success && resultPastedNewTaskInfo.Data) {

				viewModel.currentSelectedTask = null;
				viewModel.GetDataSuccess = true;
				viewModel.reload = true;
			} else {
				viewModel.ToastMsg = resultPastedNewTaskInfo.Message;
				viewModel.reload = false;
			}
			break;
		case TASK_EXPORT:
			ResultInfo<ArrayList<DialogTipsDTO>> diaResult = (ResultInfo<ArrayList<DialogTipsDTO>>) msg.obj;
			// 验证成功没有问题
			if (diaResult.Success) {
				viewModel.homeActivity.appHeader.showDialogResult(
						diaResult.Message, diaResult.Data, true,
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								doSomething("导出中...", TASK_EXPORT_ZIP);
							}
						});
			} else {
				viewModel.homeActivity.appHeader.showDialogResult(
						diaResult.Message, diaResult.Data, false, null);
			}
			break;
		case TASK_IMPORT_GUIDE: {
			String dataDefineName = TaskOperator
					.getDataDefineName(viewModel.currentSelectedTask.DDID);
			Intent intentImportGuide = new Intent(getActivity(),
					TaskImportGuide.class);
			Bundle bundle = new Bundle();
			bundle.putString("taskNum", viewModel.currentSelectedTask.TaskNum);
			bundle.putString("targetAddress",
					viewModel.currentSelectedTask.TargetAddress);
			bundle.putString("residentialArea",
					viewModel.currentSelectedTask.ResidentialArea);
			bundle.putString("targetType",
					viewModel.currentSelectedTask.TargetType);
			bundle.putString("dataDefineName", dataDefineName);
			intentImportGuide.putExtras(bundle);
			getActivity().startActivity(intentImportGuide);
		}
			break;
		case TASK_ADDITIONAL_PICTRUE: {
			ResultInfo<Boolean> resultEditTaskInfo = (ResultInfo<Boolean>) msg.obj;
			if (resultEditTaskInfo.Success && resultEditTaskInfo.Data) {
				viewModel.currentSelectedTask = null;
				viewModel.GetDataSuccess = true;
				viewModel.reload = false;
			} else {
				viewModel.ToastMsg = resultEditTaskInfo.Message;
				viewModel.reload = false;
			}
			break;
		}
		case TASK_EXPORT_ZIP:
			ResultInfo<ArrayList<DialogTipsDTO>> exportResult = (ResultInfo<ArrayList<DialogTipsDTO>>) msg.obj;
			viewModel.homeActivity.appHeader.showDialogResult(
					exportResult.Message, exportResult.Data, false, null);
			break;
		case TASK_REMOVE_RESOURCE: {
			ResultInfo<String> removeResult = (ResultInfo<String>) msg.obj;
			if (removeResult.Success) {
				viewModel.homeActivity.appHeader.showDialog("提示信息",
						removeResult.Message);
			} else {
				ArrayList<DialogTipsDTO> notices = (ArrayList<DialogTipsDTO>) removeResult.Others;
				viewModel.homeActivity.appHeader.showDialogResult("提示信息",
						notices, false, null);
			}
			break;
		}
		case SYNC_TASK_INFO:
			// 同步已完成任务
			viewModel.reload = false;
			break;
		case TASK_RESTART_TASK:
			// 前台处理 启用任务
			ResultInfo<Boolean> restartResult = (ResultInfo<Boolean>) msg.obj;
			try {
				if (restartResult.Success && restartResult.Data) {
					viewModel.reload = true;
					viewModel.currentSelectedTask.Status = TaskStatus.Doing;
				} else {
					viewModel.reload = false;
					viewModel.ToastMsg = restartResult.Message;
				}
			} catch (Exception e) {
				e.printStackTrace();
				viewModel.reload = false;
				viewModel.ToastMsg = "操作繁忙，请稍后再试";
			}
			break;
		case TASK_REMARK:
			// 前台处理 任务备注
			ResultInfo<Boolean> remarktResult = (ResultInfo<Boolean>) msg.obj;
			if (remarktResult.Success && remarktResult.Data) {
				viewModel.reload = true;
			} else {
				viewModel.reload = false;
			}
			this.showToast(remarktResult.Message);
			break;
		case CHEK_TASK_VERSION:// 对比提交任务勘察表服务器版本结束
			ResultInfo<Boolean> checkResult = (ResultInfo<Boolean>) msg.obj;
			if (checkResult.Success && checkResult.Data) {
				int ddid = viewModel.currentSelectedTask.DDID;
				String taskNum = viewModel.currentSelectedTask.TaskNum;
				String fee = viewModel.currentSelectedTask.Fee;
				menuOperator.putTaskInfo(ddid, taskNum, fee);
			} else {
				viewModel.homeActivity.appHeader.showDialog("提示信息",
						checkResult.Message);
			}
			break;
		case CHEK_VERSION:
			viewModel.reload=false;
			colseLoading=false;
			boolean hasUpdateDatadefin = (boolean) msg.obj;
			// 检查更新
			// 今日是否再提示
			if (viewModel.homeActivity.appHeader.getUpdateTipsIsShow()) {
				viewModel.homeActivity.appHeader.updateTipsDialog("");
			}
			if (hasUpdateDatadefin) {// 切换到homeFragment
				// viewModel.homeActivity.toHomeFragment();
				showToast("有勘察表需要更新，请更新勘察表");
			}
			break;
		default:
			break;
		}
		if (viewModel.reload) {
			showData();
		} else {
			if (viewModel.ToastMsg != null && viewModel.ToastMsg.length() > 0) {
				showToast(viewModel.ToastMsg);
			}
		}
		if (viewModel.homeActivity != null && colseLoading) {
			viewModel.homeActivity.loadingWorker.closeLoading();
		}
		if (isLoad) {// 刷新中， 取消刷新
			taskPullTorefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			isLoad = false;
		}
	}

	/**
	 * @author kevin
	 * @date 2015-10-12 下午6:10:14
	 * @Description: 计算任务总数
	 * @return void 返回类型
	 */
	private int setTaskCount(String CountStr) {
		int count = 0;
		try {
			String[] counts = CountStr.split(",");
			int localCunt = 0;
			if (counts.length == 2) {
				localCunt = Integer.valueOf(counts[0]);
				this.viewModel.remoteTaskCount = Integer.valueOf(counts[1]);
			} else {
				localCunt = Integer.valueOf(counts[0]);
			}
			count = this.viewModel.remoteTaskCount + localCunt;
		} catch (Exception e) {
			count = 0;
			e.printStackTrace();
			DataLogOperator.taskHttp("setTaskCount=>任务数量统计：(setTaskCount)",
					e.getMessage());
		}
		return count;
	}

	/**
	 * 添加任务内容到全局变量中
	 * 
	 * @param result
	 *            任务结果列表
	 */
	private void addTaskInfoe(ResultInfo<ArrayList<TaskInfo>> result) {
		switch (viewModel.taskStatus) {
		case Doing:
			// 服务器和本地都有任务数量
			String totalStr = result.Others.toString();
			List<String> totalListStr = Arrays.asList(totalStr.split(","));
			if (totalListStr.size() == 1) {
				viewModel.localTotal = Integer.parseInt(totalListStr.get(0));
			} else {
				viewModel.localTotal = Integer.parseInt(totalListStr.get(0));
				viewModel.remoteTotal = Integer.parseInt(totalListStr.get(1));
			}
			break;
		case Done:
			// 本地有任务数量
			String[] counts = result.Others.toString().split(",");
			viewModel.localTotal = Integer.valueOf(counts[0]);
			break;
		case Todo:
			// 服务器有任务数量
			try {
				viewModel.remoteTotal = Integer.valueOf(result.Others + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case Submiting:
			// 纯粹分页数量，使用服务器或本地都可以，此处使用本地数据数量来存放。
			viewModel.localTotal = (Integer) result.Others;
			break;
		default:
			break;
		}

		if (viewModel.currentIndex > 1) {// 追加
			// 并且过滤重复项
			filterTaskInfo(result.Data);
		} else {// 重新填充数据
			viewModel.taskInfoes.clear();
			viewModel.taskInfoes.removeAll(viewModel.taskInfoes);
			viewModel.taskInfoes.addAll(result.Data);
		}

	}

	/**
	 * 过滤重复项
	 * 
	 * @param taskInfos
	 *            需要添加的任务分类项
	 */
	private void filterTaskInfo(ArrayList<TaskInfo> taskInfos) {
		for (TaskInfo addItem : taskInfos) {
			// 是否包含该任务项
			Boolean isContains = false;
			for (TaskInfo item : viewModel.taskInfoes) {
				if (addItem.ID == item.ID) {
					isContains = true;
					break;
				}
			}
			if (!isContains) {
				viewModel.taskInfoes.add(addItem);
			}
		}
	}

	private void showSubmitingStatus() {
		LinkedHashMap<String, TaskInfo> uploadTasks = MainService
				.getUploadTasks();
		if (uploadTasks.size() > 0) {
			for (TaskInfo element : viewModel.taskInfoes) {
				TaskInfo tempTask = uploadTasks.get(element.TaskNum);
				if (uploadTasks.containsKey(element.TaskNum)
						&& tempTask.Status == TaskStatus.Submiting) {
					element.Status = TaskStatus.Submiting;
				}
				/*
				 * if (uploadTasks.containsKey(element.TaskNum) &&
				 * uploadTasks.get(element.TaskNum).Status !=
				 * TaskStatus.Submiting) { element.Status =
				 * TaskStatus.Submiting; }
				 */
			}
		}
	}

	// }}

	// {{ 数据加载显示

	/**
	 * 加载控件信息
	 * */
	private void showData() {
		if (viewModel.GetDataSuccess) {
			showTaskInfoes();
			if (viewModel.ToastMsg.length() > 0) {
				showToast(viewModel.ToastMsg);
			}
		} else {
			if (viewModel.ToastMsg.length() > 0) {
				showToast(viewModel.ToastMsg);
			} else {
				showToast(R.string.hint_get_data_fail);
			}
		}
	}

	/**
	 * 显示任务信息
	 * */
	private void showTaskInfoes() {
		if (viewModel.currentIndex == 1) {
			taskListViewAdapter = null;
		}
		if (taskListViewAdapter == null && viewModel.taskStatus != null) {
			int recId = -1;
			switch (viewModel.taskStatus) {
			case Todo:// 待领取
				recId = R.layout.task_listview_item_todo;
				break;
			case Doing:// 待提交
				recId = R.layout.task_listview_item_doing;
				break;
			case Done:// 已完成
				recId = R.layout.task_listview_item_done;
				break;
			case Submiting:
				recId = R.layout.task_listview_item_submiting;
				break;
			default:
				break;
			}
			// TODO taskListViewAdapter null
			if (recId != -1 && taskListViewAdapter == null) {
				taskListViewAdapter = new TaskListViewAdapter(
						viewModel.homeActivity, recId, viewModel.taskInfoes);
			}
		}
		if (taskListViewAdapter != null) {
			if (viewModel.taskInfoes != null) {
				task_listview.setAdapter(taskListViewAdapter);
				taskListViewAdapter.refachView(viewModel.taskInfoes);
			} else {// 刷新数据
				taskListViewAdapter.refachView(viewModel.taskInfoes);
			}
			// 取消全选
			sub_title_select.setChecked(false);

			// 是否滚到底部
			if (!viewModel.onPullup) {

				task_listview.post(new Runnable() {
					@Override
					public void run() {
						task_listview.requestFocusFromTouch();// 获取焦点
						task_listview.setSelection(0);
					}
				});
			}
			viewModel.onPullup = false;
			// 显示统计出的记录数量
			setCountTitle();
		}
	}

	/**
	 * 
	 * @author kevin
	 * @date 2015-10-14 上午9:51:38
	 * @Description: 设置标题统计数量
	 * @return void 返回类型
	 * @version V1.0
	 */
	private void setCountTitle() {
		int count = titleTextView.getTag() == null ? 0 : (int) titleTextView
				.getTag();
		int currentCount = viewModel.taskInfoes.size();
		String cunnrentTitle = getCurrentTitle();
		if (count >= 0) {
			this.setTitle(cunnrentTitle + "(" + currentCount + "/" + count
					+ ")");
		}
		task_listview.setSelection(viewModel.listItemCurrentPosition);
	}

	// 获取当前标题字符串
	private String getCurrentTitle() {
		String title = "";
		if (viewModel.taskStatus != null) {
			switch (viewModel.taskStatus) {
			case Todo:
				title = "待堪察的列表";
				break;
			case Doing:
				title = "待提交的列表";
				break;
			case Done:
				title = "已完成的列表";
				break;
			case Submiting:
				title = "提交中的列表";
				break;
			default:
				break;
			}
		}
		return title;
	}

	/**
	 * 刷新列表
	 */
	public void refreshList(String taskNum) {
		for (TaskInfo element : viewModel.taskInfoes) {
			if (element.TaskNum.equals(taskNum)) {
				element.Status = TaskStatus.Submiting;
			}
		}
		taskListViewAdapter.refachView(viewModel.taskInfoes);// notifyDataSetChanged();
	}

	// }}

	// {{

	/**
	 * 初始化界面所有控件
	 * */
	private void initView() {
		viewModel.homeActivity = (HomeActivity) getActivity();
		mView = getView();

		taskSearch = (LinearLayout) mView.findViewById(R.id.task_search);
		serach_editText = (EditText) mView.findViewById(R.id.serach_editText);
		serachBtn = (ImageView) mView.findViewById(R.id.serach_btn);

		initSortLayoutByStatus();

		serachBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewModel.currentIndex = 1;
				// 刷新，重新加载数据，清空缓存，重新获取数据。
				viewModel.taskInfoes.retainAll(viewModel.taskInfoes);
				viewModel.taskInfoes.clear();
				loadData();
			}
		});

		viewModel.currentUser = EIASApplication.getCurrentUser();
		titleTextView = (TextView) mView.findViewById(R.id.home_top_title);

		sub_title_select = (CheckBox) mView.findViewById(R.id.sub_title_select);
		sub_title_select
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						for (TaskInfo element : viewModel.taskInfoes) {
							if (!element.InworkReportFinish
									&& !(element.Status == TaskStatus.Pause)) {
								element.isChecked = isChecked;
							}
						}
						taskListViewAdapter.notifyDataSetChanged();

					}
				});

		ArrayList<String> temp = new ArrayList<String>();
		if (viewModel.taskStatus != null) {
			switch (viewModel.taskStatus) {
			case Todo:
				setTitle("待堪察的列表");
				break;
			case Doing:
				sub_title_select.setVisibility(View.VISIBLE);
				setTitle("待提交的列表");
				temp.add(String.valueOf(BroadRecordType.AFTER_CREATED_TASK));
				temp.add(BroadRecordType.AFTER_SUBMITED);
				break;
			case Done:
				sub_title_select.setVisibility(View.VISIBLE);
				setTitle("已完成的列表");
				temp.add(BroadRecordType.AFTER_SUBMITED);
				break;
			case Submiting:
				taskSearch.setVisibility(View.GONE);
				setTitle("提交中的列表");
				temp.add(BroadRecordType.WAIT_TO_SUBMIT);
				temp.add(BroadRecordType.AFTER_SUBMITED);
				break;
			default:
				break;
			}
		}
		reload_btn = (Button) mView.findViewById(R.id.list_reload);
		reload_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isLoad) {// 处于下拉刷新中不可重复刷新
					return;
				}
				viewModel.currentIndex = 1;
				viewModel.taskInfoes.retainAll(viewModel.taskInfoes);
				viewModel.taskInfoes.clear();
				synData();
				// loadData();
			}
		});

		Button btn_menu = (Button) mView.findViewById(R.id.btn_menu);
		btn_menu.setVisibility(View.INVISIBLE);

		menuOperator = new TaskListMenuOperaotr(this, viewModel.homeActivity);
		initTaskList(menuOperator);
		viewModel.homeActivity.taskListMenuOperaotrs.put(viewModel.taskStatus,
				menuOperator);
		setDoingTaskinfoMenuCopyValue(menuOperator);
		broadcastReceiver = new BaseBroadcastReceiver(mActivity, temp);
		broadcastReceiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String actionType = intent.getAction();
				switch (actionType) {
				case BroadRecordType.AFTER_CREATED_TASK:
					loadData();
					break;
				case BroadRecordType.WAIT_TO_SUBMIT:
					loadData();
					break;
				case BroadRecordType.AFTER_SUBMITED:
					String msg = intent.getStringExtra("hideOfflineMsg");
					if (msg != null && msg != "" && msg.equals("true")) {
						viewModel.hideOfflineMsg = true;
					}
					viewModel.currentIndex = 1;
					loadData();
					break;
				default:
					break;
				}
			}
		});
	}

	/***
	 * 初始化排序栏
	 */
	private void initSortLayoutByStatus() {
		viewModel.order_by_create = (RadioButton) mView
				.findViewById(R.id.order_by_create);
		viewModel.order_by_receive = (RadioButton) mView
				.findViewById(R.id.order_by_receive);
		viewModel.order_by_status = (RadioButton) mView
				.findViewById(R.id.order_by_status);
		viewModel.order_by_finish = (RadioButton) mView
				.findViewById(R.id.order_by_finish);
		viewModel.order_by_booktime = (RadioButton) mView
				.findViewById(R.id.order_by_booktime);
		viewModel.create_img = (ImageView) mView.findViewById(R.id.create_img);
		viewModel.receive_img = (ImageView) mView
				.findViewById(R.id.receive_img);
		viewModel.status_img = (ImageView) mView.findViewById(R.id.status_img);
		viewModel.finish_img = (ImageView) mView.findViewById(R.id.finish_img);
		viewModel.booktime_img = (ImageView) mView
				.findViewById(R.id.booktime_img);
		View booktime_layout = mView.findViewById(R.id.booktime_layout);
		View finish_layout = mView.findViewById(R.id.finish_layout);
		// booktime

		viewModel.search_bar_rdg = (RadioGroup) mView
				.findViewById(R.id.search_bar_rdg);

		switch (viewModel.taskStatus) {
		case Todo:// 待领取
			viewModel.order_by_receive.setVisibility(View.INVISIBLE);
			viewModel.order_by_status.setVisibility(View.INVISIBLE);
			booktime_layout.setVisibility(View.GONE);
			finish_layout.setVisibility(View.GONE);
			viewModel.receive_img.setVisibility(View.INVISIBLE);
			viewModel.status_img.setVisibility(View.INVISIBLE);
			break;
		case Doing:// 待提交，无已完成时间
			finish_layout.setVisibility(View.GONE);
			booktime_layout.setVisibility(View.VISIBLE);
			break;
		case Done:
			viewModel.order_by_receive.setVisibility(View.GONE);
			viewModel.order_by_status.setVisibility(View.GONE);
			viewModel.order_by_create.setVisibility(View.GONE);

			booktime_layout.setVisibility(View.GONE);

			viewModel.receive_img.setVisibility(View.GONE);
			viewModel.status_img.setVisibility(View.GONE);
			viewModel.create_img.setVisibility(View.GONE);
			break;
		case Submiting:// 提交中任务不做筛选
			viewModel.search_bar_rdg.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		viewModel.order_by_create.setOnClickListener(this);
		viewModel.order_by_receive.setOnClickListener(this);
		viewModel.order_by_status.setOnClickListener(this);
		viewModel.order_by_finish.setOnClickListener(this);
		viewModel.order_by_booktime.setOnClickListener(this);
	}

	/**
	 * 若已完成中已有复制项，在初始化待勘察时应将该复制项赋值到待勘察中
	 * 
	 * @param menuOperator
	 *            任务列表长按菜单
	 */
	private void setDoingTaskinfoMenuCopyValue(TaskListMenuOperaotr menuOperator) {
		if (viewModel.taskStatus == TaskStatus.Doing) {
			TaskListMenuOperaotr taskListMenuOperaotr = viewModel.homeActivity.taskListMenuOperaotrs
					.get(TaskStatus.Done);
			if (taskListMenuOperaotr != null) {
				menuOperator.taskListFragment.viewModel.selectedCategoryItems = taskListMenuOperaotr.taskListFragment.viewModel.selectedCategoryItems;
				menuOperator.taskListFragment.viewModel.currentCopiedTask = taskListMenuOperaotr.taskListFragment.viewModel.currentCopiedTask;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		broadcastReceiver.unregisterReceiver();
	}

	/**
	 * 填充任务列表
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void initTaskList(final TaskListMenuOperaotr menuOperator) {
		taskPullTorefreshLayout = (PullToRefreshLayout) mView
				.findViewById(R.id.task_pull_refresh_layout);

		task_listview = (ListView) mView.findViewById(R.id.task_listview);

		// 监听触摸事件
		task_listview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return viewModel.homeActivity.touchPage(event.getAction(),
						event.getX(), event.getY(), false);
			}
		});

		// 监听长按事件
		task_listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					if (viewModel.taskStatus == TaskStatus.Submiting) {
						taskListViewAdapter.setSelectedPosition(position);
						taskListViewAdapter.notifyDataSetChanged();
						viewModel.currentSelectedTask = viewModel.taskInfoes
								.get(position);
						menuOperator.showDialog();
					} else {
						if (!TaskOperator.submiting(viewModel.taskInfoes
								.get(position).TaskNum)
								&& viewModel.taskInfoes.get(position).Status != TaskStatus.Submiting) {
							taskListViewAdapter.setSelectedPosition(position);
							taskListViewAdapter.notifyDataSetChanged();
							viewModel.currentSelectedTask = viewModel.taskInfoes
									.get(position);
							menuOperator.showDialog();
						} else {
							viewModel.homeActivity.appHeader.showDialog("提示信息",
									"无法操作正在提交的任务");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("com.yunfang.eias", ">>" + e.getMessage());
				}
				return true;
			}

		});

		// if (viewModel.taskStatus != TaskStatus.Submiting) {
		// 监听点击事件
		task_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// 记录点击的位置
				try {
					taskListViewAdapter.setSelectedPosition(position);
					taskListViewAdapter.notifyDataSetChanged();
					if (viewModel.homeActivity.moveX < viewModel.homeActivity.TOUCH_DISTANCE
							&& viewModel.homeActivity.moveY < viewModel.homeActivity.TOUCH_DISTANCE) {
						viewModel.currentSelectedTask = viewModel.taskInfoes
								.get(position);

						if (viewModel.taskStatus != null) {
							/*
							 * 过滤暂停任务，提示任务已被暂停。
							 * if(viewModel.currentSelectedTask.
							 * Status==TaskStatus.Pause){
							 * viewModel.homeActivity.
							 * appHeader.showDialog("提示信息", "任务已被暂停!"); return;
							 * }
							 */
							switch (viewModel.taskStatus) {
							case Todo:
								try {
									// 记录单击的位置
									taskListViewAdapter
											.setSelectedPosition(position);
									taskListViewAdapter.notifyDataSetChanged();
									viewModel.currentSelectedTask = viewModel.taskInfoes
											.get(position);
									menuOperator.showDialog();
								} catch (NullPointerException e) {
									ToastUtil.shortShow(viewModel.homeActivity,
											"任务已被领取");
								} catch (Exception es) {
									ToastUtil.shortShow(viewModel.homeActivity,
											"任务已被领取");
								}
								break;
							case Doing:
							case Done:
								if (!TaskOperator
										.submiting(viewModel.taskInfoes
												.get(position).TaskNum)
										&& viewModel.currentSelectedTask.Status != TaskStatus.Submiting) {
									// 当前任务列表是否要更新配置表
									if (!menuOperator
											.hasNewDataDefines(viewModel.currentSelectedTask.DDID)) {
										startTaskInfo(
												viewModel.currentSelectedTask.TaskID,
												viewModel.currentSelectedTask.ID,
												viewModel.currentSelectedTask.TaskNum);
									}
								} else {
									viewModel.homeActivity.appHeader
											.showDialog("提示信息", "无法操作正在提交的任务");
								}
								break;
							default:
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("com.yunfang.eias", "OnItemClick->>" + e.toString());
				}
			}
		});
		// 监听下拉刷新， 上啦加载更多
		taskPullTorefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				// 刷新列表
				viewModel.currentIndex = 1;
				isLoad = true;
				loadData();

			}

			@Override
			public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
				// 加载操作
				isLoad = true;

				int temp = 0;
				if (EIASApplication.IsOffline) {
					temp = (Integer) (viewModel.localTotal / viewModel.pageSize);
					temp += viewModel.localTotal % viewModel.pageSize > 0 ? 1
							: 0;
				} else {
					temp = (Integer) ((viewModel.remoteTotal + viewModel.localTotal) / viewModel.pageSize);
					temp += (viewModel.remoteTotal + viewModel.localTotal)
							% viewModel.pageSize > 0 ? 1 : 0;
				}
				if (viewModel.currentIndex >= temp) {
					showToast("已经是最后一页信息");
					/*
					 * if (viewModel.localTotal % viewModel.pageSize == 0) {
					 * viewModel.currentIndex = temp + 1; }
					 */
					isLoad = false;
					pullToRefreshLayout
							.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				} else {
					viewModel.currentIndex++;
					viewModel.listItemCurrentPosition = task_listview
							.getLastVisiblePosition();
					viewModel.onPullup = true;
					loadData();

				}
			}
		});

		// 监听滚动加载数据
		task_listview.setOnScrollListener(new OnScrollListener() {

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {// 滑到顶部

				} else {

				}
				if (visibleItemCount + firstVisibleItem == totalItemCount) {// 滑到底部
				}

			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (viewModel.taskStatus != TaskStatus.Submiting) {
					switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE:// 当屏幕停止滚动时
						viewModel.listItemCurrentPosition = view
								.getLastVisiblePosition();
						int vc = view.getCount();
						if (vc >= viewModel.pageSize
								&& viewModel.listItemCurrentPosition >= vc - 4) {// 滚动到最后一项
							int temp = 0;
							if (EIASApplication.IsOffline) {
								temp = (Integer) (viewModel.localTotal / viewModel.pageSize);
								temp += viewModel.localTotal
										% viewModel.pageSize > 0 ? 1 : 0;
							} else {
								temp = (Integer) ((viewModel.remoteTotal + viewModel.localTotal) / viewModel.pageSize);
								temp += (viewModel.remoteTotal + viewModel.localTotal)
										% viewModel.pageSize > 0 ? 1 : 0;
							}
							if (viewModel.currentIndex < temp) {
								viewModel.currentIndex++;
								viewModel.onPullup = true;
								loadData();
							} else {
								// showToast("已经是最后一页信息");
								/*if (viewModel.localTotal % viewModel.pageSize == 0) {
									viewModel.currentIndex = temp + 1;
								}*/
							}
						} else {
						}
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 当屏幕滚动且用户使用的触碰或手指还在屏幕上时
						/*
						 * int lastViesible=view .getLastVisiblePosition(); int
						 * vcs = view.getCount(); if(lastViesible==vcs-4){
						 * 
						 * } YFLog.d(lastViesible+"--"+vcs);
						 */
						break;
					case OnScrollListener.SCROLL_STATE_FLING:// 由于用户的操作，屏幕产生惯性滑动时

						/*
						 * if (view.getLastVisiblePosition() == view.getCount())
						 * { viewModel.homeActivity.loadingWorker
						 * .showLoading("数据加载中..."); }
						 */
						break;
					}
				}
			}
		});

		// }
	}

	private void startTaskInfo(int taskId, int identityId, String taskNum) {
		Intent intent = new Intent(getActivity(), TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("taskNum", taskNum);
		bundle.putInt("taskId", taskId);
		bundle.putInt("identityId", identityId);
		intent.putExtras(bundle);
		getActivity().startActivity(intent);
	}

	/**
	 * 打开任务下勘察分类信息界面
	 * 
	 * @param taskID
	 *            :后台任务编号
	 * @param identityId
	 *            :android:端自动增长编号
	 * @param taskNum
	 *            :android端任务编码
	 */
	private ResultInfo<Boolean> openTaskInfo(int taskId, int identityId,
			String taskNum) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		Intent intent = new Intent(getActivity(), TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("taskNum", taskNum);
		bundle.putInt("taskId", taskId);
		bundle.putInt("identityId", identityId);
		intent.putExtras(bundle);
		getActivity().startActivity(intent);
		result.Data = true;
		return result;
	}

	/**
	 * 打开任务下勘察分类信息界面 补发资源
	 * 
	 * @param taskID
	 *            :后台任务编号
	 * @param identityId
	 *            :android:端自动增长编号
	 * @param taskNum
	 *            :android端任务编码
	 */
	private ResultInfo<Boolean> openTaskInfo(int taskId, int identityId,
			String taskNum, boolean additional) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		Intent intent = new Intent(getActivity(), TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("taskNum", taskNum);
		bundle.putInt("taskId", taskId);
		bundle.putInt("identityId", identityId);
		bundle.putBoolean("additional", additional);
		intent.putExtras(bundle);
		getActivity().startActivity(intent);
		result.Data = true;
		return result;
	}

	/**
	 * 加载数据
	 * */
	public void loadData() {
		if (viewModel.taskInfoes == null || viewModel.taskInfoes.size() <= 0
				|| viewModel.taskStatus == TaskStatus.Todo) {
			viewModel.homeActivity.loadingWorker.showLoading("数据加载中...");
		}
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_GETTASKINFOES;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 同步数据
	 * */
	public void synData() {
		if (viewModel.taskInfoes == null || viewModel.taskInfoes.size() <= 0
				|| viewModel.taskStatus == TaskStatus.Todo
				|| viewModel.taskStatus == TaskStatus.Doing) {
			viewModel.homeActivity.loadingWorker.showLoading("数据加载中...");
		}
		Message TaskMsg = new Message();
		TaskMsg.what = HANDLE_SYN;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 操作任务事件
	 * 
	 * @param msg
	 *            :loading框的提示数据
	 * @param taskType
	 *            :任务类型
	 */
	public void doSomething(String msg, int taskType) {
		if (msg.length() > 0) {
			viewModel.homeActivity.loadingWorker.showLoading(msg);
		}
		Message TaskMsg = new Message();
		TaskMsg.what = taskType;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 操作任务事件
	 * 
	 * @param msg
	 *            :loading框的提示数据
	 * @param taskType
	 *            :任务类型
	 */
	public void doSomething(String msg, int taskType, Object obj) {
		if (msg.length() > 0) {
			viewModel.homeActivity.loadingWorker.showLoading(msg);
		}
		Message TaskMsg = new Message();
		TaskMsg.what = taskType;
		TaskMsg.obj = obj;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	// {{ 粘贴并创建新任务的对话框 showCreateNewTaskActivity

	/**
	 * 粘贴并创建新任务的对话框
	 * 
	 * @return
	 */
	public ResultInfo<Boolean> showCreateNewTaskActivity() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = false;

		try {

			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> values = new ArrayList<String>();

			Iterator<Entry<String, String>> iterator = viewModel.selectedCategoryItems
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> col = iterator.next();
				keys.add(col.getKey());// 任务分类项编号
				values.add(col.getValue());// 任务分类项名称
			}
			/*
			 * ArrayList<String> childKeys=new ArrayList<String>();
			 * ArrayList<String[]> childValue=new ArrayList<String[]>();
			 */

			/*
			 * for(String key:viewModel.selectedCategoryChildItems.keySet()){
			 * childKeys.add(key);
			 * childValue.add(viewModel.selectedCategoryChildItems.get(key)); }
			 */

			Intent intent = new Intent(viewModel.homeActivity,
					CreateTaskActivity.class);
			intent.putExtra("name",
					getDataDefineName(viewModel.currentCopiedTask.DDID));
			intent.putExtra("address",
					viewModel.currentCopiedTask.TargetAddress);
			intent.putExtra("is_copied_to_new_task", true);
			intent.putExtra("is_created_by_user",
					viewModel.currentCopiedTask.IsNew);
			intent.putExtra(
					"copied_task_id",
					viewModel.currentCopiedTask.IsNew ? viewModel.currentCopiedTask.ID
							: viewModel.currentCopiedTask.TaskID);
			intent.putStringArrayListExtra("keys", keys);
			intent.putStringArrayListExtra("values", values);

			intent.putExtra("selected_child_map",
					viewModel.selectedCategoryChildItems);
			viewModel.homeActivity.startActivity(intent);
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
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
	private String getDataDefineName(int ddid) {
		String result = "";
		ResultInfo<DataDefine> dataDefine = DataDefineWorker
				.queryDataDefineByDDID(ddid);
		if (dataDefine != null && dataDefine.Data != null) {
			result = dataDefine.Data.Name;
		}
		return result;
	}

	/**
	 * 同步任务报告完成信息
	 */
	private void synchroTaskInfo() {
		if (EIASApplication.IsNetworking && !EIASApplication.IsOffline) {
			try {
				if (viewModel.taskStatus == TaskStatus.Done) {
					//同步报告已完成任务状态， 删除任务资源
					TaskOperator.synchroReportIsFinish(viewModel.currentUser);
				} else if (viewModel.taskStatus == TaskStatus.Doing) {
					TaskOperator
							.syncRemoteData(0, 0, "", viewModel.currentUser);
				}
			} catch (Exception e) {
				DataLogOperator.other("synchroReportInfo=>" + e.getMessage());
			}
		}
	}

	// }}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		setSearchBarRdgStatus(v.getId(), null);

		viewModel.currentIndex = 1;
		loadData();

		// SP 保存排序状态
		TaskOperator.saveSortStatus(viewModel.taskStatus, viewModel.sortType);
	}

	/***
	 * 设置选中状态
	 * 
	 * @param id
	 */
	private void setSearchBarRdgStatus(int id, SortType sortType) {
		// 取消全选
		sub_title_select.setChecked(false);
		switch (id) {
		case R.id.order_by_create: {
			String tag = "";
			if (sortType != null) {
				tag = sortType.isAsc() ? "0" : "1";
			} else {
				viewModel.sortType = SortType.创建时间;
				tag = (String) viewModel.order_by_create.getTag();
			}

			boolean isAsc = tag.equals("0") ? true : false;
			viewModel.sortType.setAsc(isAsc);
			setStatus(tag, viewModel.create_img, viewModel.order_by_create);
			setStatus("1", viewModel.receive_img, viewModel.order_by_receive);
			setStatus("1", viewModel.status_img, viewModel.order_by_status);
			setStatus("1", viewModel.finish_img, viewModel.order_by_finish);
			setStatus("1", viewModel.booktime_img, viewModel.order_by_booktime);

			break;
		}
		case R.id.order_by_receive: {
			String tag = "";
			if (sortType != null) {
				tag = sortType.isAsc() ? "0" : "1";
			} else {
				viewModel.sortType = SortType.领取时间;
				tag = (String) viewModel.order_by_receive.getTag();
			}
			boolean isAsc = tag.equals("0") ? true : false;
			viewModel.sortType.setAsc(isAsc);

			setStatus(tag, viewModel.receive_img, viewModel.order_by_receive);
			setStatus("1", viewModel.create_img, viewModel.order_by_create);
			setStatus("1", viewModel.status_img, viewModel.order_by_status);
			setStatus("1", viewModel.finish_img, viewModel.order_by_finish);
			setStatus("1", viewModel.booktime_img, viewModel.order_by_booktime);

			break;
		}
		case R.id.order_by_status: {
			String tag = "";
			if (sortType != null) {
				tag = sortType.isAsc() ? "0" : "1";
			} else {
				viewModel.sortType = SortType.状态;
				tag = (String) viewModel.order_by_status.getTag();
			}
			boolean isAsc = tag.equals("0") ? true : false;
			viewModel.sortType.setAsc(isAsc);

			setStatus(tag, viewModel.status_img, viewModel.order_by_status);
			setStatus("1", viewModel.create_img, viewModel.order_by_create);
			setStatus("1", viewModel.receive_img, viewModel.order_by_receive);
			setStatus("1", viewModel.finish_img, viewModel.order_by_finish);
			setStatus("1", viewModel.booktime_img, viewModel.order_by_booktime);

			break;
		}
		case R.id.order_by_finish: {
			String tag = "";
			if (sortType != null) {
				tag = sortType.isAsc() ? "0" : "1";
			} else {
				viewModel.sortType = SortType.完成时间;
				tag = (String) viewModel.order_by_finish.getTag();
			}
			boolean isAsc = tag.equals("0") ? true : false;
			viewModel.sortType.setAsc(isAsc);

			setStatus(tag, viewModel.finish_img, viewModel.order_by_finish);
			setStatus("1", viewModel.receive_img, viewModel.order_by_receive);
			setStatus("1", viewModel.status_img, viewModel.order_by_status);
			setStatus("1", viewModel.create_img, viewModel.order_by_create);
			setStatus("1", viewModel.booktime_img, viewModel.order_by_booktime);

			break;
		}
		case R.id.order_by_booktime: {
			String tag = "";
			if (sortType != null) {
				tag = sortType.isAsc() ? "0" : "1";
			} else {
				viewModel.sortType = SortType.预约时间;
				tag = (String) viewModel.order_by_booktime.getTag();
			}
			boolean isAsc = tag.equals("0") ? true : false;
			viewModel.sortType.setAsc(isAsc);

			setStatus(tag, viewModel.booktime_img, viewModel.order_by_booktime);
			setStatus("1", viewModel.finish_img, viewModel.order_by_finish);
			setStatus("1", viewModel.receive_img, viewModel.order_by_receive);
			setStatus("1", viewModel.status_img, viewModel.order_by_status);
			setStatus("1", viewModel.create_img, viewModel.order_by_create);
			break;
		}

		default:
			break;
		}
		viewModel.search_bar_rdg.check(id);
	}

	private int getIdBySortType(SortType sortType) {
		switch (sortType) {
		case 创建时间:
			return R.id.order_by_create;
		case 完成时间:
			return R.id.order_by_finish;
		case 状态:
			return R.id.order_by_status;
		case 领取时间:
			return R.id.order_by_receive;
		case 预约时间:
			return R.id.order_by_booktime;
		default:
			return R.id.order_by_receive;
		}
	}

	private void setStatus(String tag, ImageView icon, RadioButton radioButton) {
		if (tag.equals("0")) {
			icon.setImageResource(R.drawable.down_bule);
			radioButton.setTag("1");
		} else {
			icon.setImageResource(R.drawable.down_gray);
			radioButton.setTag("0");
		}
	}

}
