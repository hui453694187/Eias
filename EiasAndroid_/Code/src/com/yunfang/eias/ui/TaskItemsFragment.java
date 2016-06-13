package com.yunfang.eias.ui;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.TaskItemControlOperator;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.ListUtil;

/**
 * 
 * 项目名称：WaiCai 类名称：TaskItemsFragment 类描述：用于显示任务下所属分类下所有子项信息的Fragment 创建人： 贺隽
 * 创建时间：2014-4-30 下午4:17:57
 * 
 * @version
 */
public class TaskItemsFragment extends BaseWorkerFragment {
	// {{ 属性

	/**
	 * Activity对象
	 */
	public TaskInfoActivity taskInfoActivity;

	/**
	 * 当前Fragment视图
	 */
	private View mView;

	/**
	 * 线性布局框
	 */
	private LinearLayout layout;

	/**
	 * 子项信息
	 */
	private ArrayList<TaskDataItem> taskDataItems;

	/**
	 * 属于当前分类项下的勘察表子项列表
	 */
	private ArrayList<DataFieldDefine> defineItems;

	/**
	 * 控件生成对象
	 */
	private TaskItemControlOperator controlOperator;

	/**
	 * 切换分类项
	 */
	private Button btn_menu;

	/**
	 * 勘察表分类项
	 */
	private DataCategoryDefine dataCategoryDefine;

	/**
	 * 保存数据
	 */
	private Button btn_save_data;

	/**
	 * 上一步按钮(点击后保存数据并且直接跳到上一个分类子项)
	 */
	private Button btn_previous_Category;

	/**
	 * 下一步(点击后保存数据并且直接跳到下一个分类子项)
	 */
	private Button btn_next_Category;

	/**
	 * 补发资源
	 */
	private Button home_top_additional;

	/**
	 * 分类项索引
	 */
	private Integer categoryIndex = 0;

	/**
	 * 二层导航左侧按钮
	 */
	// private Button btn_Next;
	// }}

	// {{ 任务值
	/**
	 * 执行获取任务数据
	 * */
	public final int TASK_GETTASKDATAITEMS = 0;

	/**
	 * 提交任务数据
	 * */
	public final int TASK_SUBMIT_DATA = 1;

	/**
	 * 保存当前的任务子项列表信息
	 */
	private final int TASK_SAVETASKITEMS = 2;

	/**
	 * 保存并移动到上一个分类项子项编辑页面
	 */
	private final int TASK_SAVE_MOVEPREVIOUSCATEGORY = 3;

	/**
	 * 保存并移动到下一个分类项子项编辑页面
	 */
	private final int TASK_SAVE_MOVENEXTCATEGORY = 4;

	// }}

	// {{ 进程调用重载类
	@Override
	protected void handlerBackgroundHandler(Message msg) {
		// 准备发送给UI线程的消息对象
		Message uiMsg = new Message();
		uiMsg.what = msg.what;
		switch (msg.what) {
		case TASK_GETTASKDATAITEMS:
			if (taskInfoActivity != null && taskInfoActivity.viewModel != null && taskInfoActivity.viewModel.currentDataCategoryDefine != null
					&& ListUtil.hasData(taskInfoActivity.viewModel.currentDataCategoryDefine.Fields)) {
				if (!ListUtil.hasData(taskDataItems)) {
					TaskInfo tempTask = taskInfoActivity.viewModel.currentTask;
					TaskCategoryInfo tempCategory = taskInfoActivity.viewModel.currentCategory;
					int taskID = tempTask.IsNew ? tempTask.ID : tempTask.TaskID;
					// int baseCategoryID = tempTask.IsNew ?tempCategory.ID:
					// tempCategory.BaseCategoryID ;
					int baseCategoryID = tempCategory.BaseCategoryID > 0 ? tempCategory.BaseCategoryID : tempCategory.ID;
					// int baseCategoryID= tempCategory.ID;
					ResultInfo<ArrayList<TaskDataItem>> resultItems = TaskDataWorker.queryTaskDataItemsByID(taskID, tempCategory.CategoryID, baseCategoryID, false);
					if (resultItems.Success && ListUtil.hasData(resultItems.Data)) {
						taskDataItems = resultItems.Data;
					}
				}
				if (!ListUtil.hasData(defineItems)) {
					defineItems = taskInfoActivity.viewModel.currentDataCategoryDefine.Fields;
				}
			} else {
				defineItems = null;
				taskDataItems = null;
			}
			break;
		case TASK_SAVETASKITEMS:
			ResultInfo<Integer> saveInfo = saveTaskItems();
			uiMsg.obj = saveInfo;
			break;
		default:
			break;
		case TASK_SAVE_MOVEPREVIOUSCATEGORY:
			// 执行保存操作
			// ResultInfo<Integer> saveResulto = saveTaskItems();
			// uiMsg.obj = saveResulto;
			// if (saveResulto.Data != -1) {
			//
			// }
			if (categoryIndex > 0) {
				categoryIndex--;
			} else {
				categoryIndex = taskInfoActivity.viewModel.currentTaskCategoryInfos.size() - 1;
			}
			setDataCategoryDefine(categoryIndex);
			// 修改内存中的子项信息
			taskInfoActivity.getCurrentDataCategoryDefine(categoryIndex);
			break;
		case TASK_SAVE_MOVENEXTCATEGORY:
			// 执行保存操作
			// ResultInfo<Integer> saveResultt = saveTaskItems();
			// uiMsg.obj = saveResultt;
			// if (saveResultt.Data != -1) {
			//
			// }
			if (categoryIndex < taskInfoActivity.viewModel.currentTaskCategoryInfos.size() - 1) {
				categoryIndex++;
			} else {
				categoryIndex = 0;
			}
			// 赋值勘察表分类项信息
			setDataCategoryDefine(categoryIndex);
			// 修改内存中的子项信息
			taskInfoActivity.getCurrentDataCategoryDefine(categoryIndex);
			break;
		}
		// 发信息给UI线程
		mUiHandler.sendMessage(uiMsg);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handUiMessage(Message msg) {
		super.handUiMessage(msg);
		switch (msg.what) {
		case TASK_GETTASKDATAITEMS:
			controlOperator.showItems(defineItems, taskDataItems);
			break;
		case TASK_SAVETASKITEMS:
			ResultInfo<Integer> saveInfo = (ResultInfo<Integer>) msg.obj;
			// Data为-1时不再进行其余操作,Data为0时继续其余操作。
			if (!EIASApplication.IsOffline) {
				if (saveInfo.Data != -1) {
					Integer ddid = taskInfoActivity.viewModel.currentTask.DDID;
					String taskNum = taskInfoActivity.viewModel.currentTask.TaskNum;
					String fee = taskInfoActivity.viewModel.currentTask.Fee;
					taskInfoActivity.menuOperator.putTaskInfo(ddid, taskNum, fee);
				}
			}
			if (saveInfo.Message != null && saveInfo.Message.length() > 0 && saveInfo.Message != "null") {
				if (saveInfo.Data != -1) {
					showToast(saveInfo.Message);
				}
			}
			break;
		case TASK_SAVE_MOVEPREVIOUSCATEGORY:
		case TASK_SAVE_MOVENEXTCATEGORY:
			// ResultInfo<Integer> preSaveInfo = (ResultInfo<Integer>) msg.obj;
			if (dataCategoryDefine != null) {
				taskInfoActivity.changFragment(dataCategoryDefine.ControlType);
			}
			// if (preSaveInfo.Message != null && preSaveInfo.Message.length() >
			// 0 && preSaveInfo.Message != "null") {
			// showToast(preSaveInfo.Message);
			// }
			break;
		}
		if (taskInfoActivity != null) {
			taskInfoActivity.loadingWorker.closeLoading();
		}
	}

	// }}

	/**
	 * 找出当前任务分类对应的勘察表分类项
	 * 
	 * @param Index
	 *            任务分类项的索引
	 */
	private void setDataCategoryDefine(Integer Index) {
		// 得到下一个任务分类项
		TaskCategoryInfo taskCategoryInfo = taskInfoActivity.viewModel.currentTaskCategoryInfos.get(Index);
		// 根据任务分类项取得勘察表分类项
		ResultInfo<DataCategoryDefine> categoryDefine = DataDefineWorker.getDataCategoryDefineByCategoryId(taskCategoryInfo.CategoryID);
		if (categoryDefine != null && categoryDefine.Data != null) {
			dataCategoryDefine = categoryDefine.Data;
		}
	}

	/**
	 * 保存分类项子项内容
	 * 
	 * @param isCommit
	 *            是否提交任务
	 * @return
	 */
	public ResultInfo<Integer> saveTaskItems() {
		ResultInfo<Integer> saveInfo = new ResultInfo<Integer>();
		if (!TaskOperator.submiting(taskInfoActivity.viewModel.currentTask.TaskNum)) {
			Integer taskID = taskInfoActivity.viewModel.currentTask.IsNew ? taskInfoActivity.viewModel.currentTask.ID : taskInfoActivity.viewModel.currentTask.TaskID;

			ArrayList<TaskDataItem> inputData = controlOperator.getInputDatas(taskID,
					taskInfoActivity.viewModel.currentCategory.BaseCategoryID > 0 ? taskInfoActivity.viewModel.currentCategory.BaseCategoryID : taskInfoActivity.viewModel.currentCategory.ID);
			saveInfo = TaskDataWorker.saveManyTaskDataItem(inputData);
			if (saveInfo.Success && saveInfo.Data > 0) {
				int valueCount = 0;
				String bMapDefaultValue = EIASApplication.DefaultBaiduMapTipsValue + EIASApplication.DefaultBaiduMapUnLocTipsValue;
				for (TaskDataItem item : inputData) {
					if (item.Value.trim().length() > 0 && !item.Value.trim().equals(EIASApplication.DefaultHorizontalLineValue) && !item.Value.trim().equals(EIASApplication.DefaultNullString)
							&& !item.Value.trim().equals(bMapDefaultValue)) {
						valueCount += 1;
					}
				}
				taskInfoActivity.viewModel.currentCategory.DataDefineFinishCount = valueCount;
				for (TaskCategoryInfo category : taskInfoActivity.viewModel.currentTaskCategoryInfos) {
					if (category.ID == taskInfoActivity.viewModel.currentCategory.ID) {
						category.DataDefineFinishCount = valueCount;
						break;
					}
				}
			}
		} else {
			saveInfo.Data = 0;
			saveInfo.Success = false;
			saveInfo.Message = "当前任务正在提交中，将不会保存信息!";
		}
		return saveInfo;
	}

	/**
	 * 加载用户控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		taskInfoActivity = (TaskInfoActivity) getActivity();
		taskInfoActivity.currentInstanceFragmentName = "TaskItemsFragment";
		return inflater.inflate(R.layout.task_items, null);
	}

	/**
	 * 加载用户控件上的控件
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		intView();
	}

	/**
	 * 初始化控件
	 * */
	@SuppressWarnings("deprecation")
	private void intView() {
		mView = getView();
		layout = (LinearLayout) mView.findViewById(R.id.task_items_view);
		btn_save_data = (Button) mView.findViewById(R.id.list_reload);
		btn_save_data.setVisibility(View.GONE);
		// btn_Next = (Button)mView.findViewById(R.id.next_Category);
		btn_previous_Category = (Button) mView.findViewById(R.id.btn_previous_Category);
		btn_next_Category = (Button) mView.findViewById(R.id.btn_next_Category);
		home_top_additional = (Button) mView.findViewById(R.id.home_top_additional);
		TextView subTitle = ((TextView) mView.findViewById(R.id.home_top_title));

		taskInfoActivity.appHeader.visBackView(true);
		taskInfoActivity.appHeader.setTitle(taskInfoActivity.viewModel.currentTask.TaskNum);

		btn_menu = (Button) mView.findViewById(R.id.btn_menu);

		Drawable saveDrawable, updateDrawable;
		Resources res = getResources();
		if (EIASApplication.IsNetworking && !EIASApplication.IsOffline) {
			updateDrawable = res.getDrawable(R.drawable.log_title_upload_statue);
			updateDrawable.setBounds(0, 0, updateDrawable.getMinimumWidth(), updateDrawable.getMinimumHeight());
			btn_save_data.setCompoundDrawables(updateDrawable, null, null, null);
			btn_save_data.setBackgroundDrawable(updateDrawable);
		} else {
			saveDrawable = res.getDrawable(R.drawable.log_title_save_statue);
			saveDrawable.setBounds(0, 0, saveDrawable.getMinimumWidth(), saveDrawable.getMinimumHeight());
			btn_save_data.setCompoundDrawables(saveDrawable, null, null, null);
			btn_save_data.setBackgroundDrawable(saveDrawable);
		}

		for (TaskCategoryInfo item : taskInfoActivity.viewModel.currentTaskCategoryInfos) {
			if (item.ID == taskInfoActivity.viewModel.currentCategory.ID) {
				break;
			}
			categoryIndex++;
		}

		// tring step = "(" + (categoryIndex + 1) + "/" +
		// taskInfoActivity.viewModel.currentTaskCategoryInfos.size() + "步)";
		subTitle.setText(taskInfoActivity.viewModel.currentCategory.RemarkName);

		btn_next_Category.setVisibility(View.VISIBLE);
		btn_previous_Category.setVisibility(View.VISIBLE);

		/*
		 * // 已经是第一个类型分类项 if(categoryIndex<=0){
		 * btn_previous_Category.setEnabled(false); } else
		 * if(categoryIndex>=taskInfoActivity
		 * .viewModel.currentTaskCategoryInfos.size()-1){
		 * btn_next_Category.setEnabled(false); }
		 */
		// 上一步按钮触发事件
		btn_previous_Category.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAndMovePreviousCategory();
			}
		});

		// 下一步按钮触发事件
		btn_next_Category.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAndMoveNextCategory();
			}
		});

		btn_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				taskInfoActivity.toCategoriesFragment();
			}
		});
		btn_save_data.setOnClickListener(commitClickListener);
		Integer taskID = taskInfoActivity.viewModel.currentTask.IsNew ? taskInfoActivity.viewModel.currentTask.ID : taskInfoActivity.viewModel.currentTask.TaskID;
		Integer categoryId = taskInfoActivity.viewModel.currentCategory.BaseCategoryID > 0 ? taskInfoActivity.viewModel.currentCategory.BaseCategoryID : taskInfoActivity.viewModel.currentCategory.ID;
		controlOperator = new TaskItemControlOperator(taskInfoActivity, layout, taskID, categoryId);
		loadItems();

		if (taskInfoActivity.additional) {
			home_top_additional.setVisibility(View.VISIBLE);
			home_top_additional.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TaskOperator.additionalResource(taskInfoActivity.viewModel.currentTask);
				}
			});
		}
	}

	/**
	 * 获取控件
	 */
	public void loadItems() {
		taskInfoActivity.loadingWorker.showLoading("数据加载中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_GETTASKDATAITEMS;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 提交任务
	 */
	private OnClickListener commitClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			taskInfoActivity.loadingWorker.showLoading("数据操作中...");
			Message TaskMsg = new Message();
			TaskMsg.what = TASK_SAVETASKITEMS;
			mBackgroundHandler.sendMessage(TaskMsg);
		}
	};

	/**
	 * 保存并跳转到上一个分类项子项
	 */
	private void saveAndMovePreviousCategory() {
		taskInfoActivity.loadingWorker.showLoading("跳转中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_SAVE_MOVEPREVIOUSCATEGORY;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 保存并跳转到下一个分类项子项
	 */
	private void saveAndMoveNextCategory() {
		taskInfoActivity.loadingWorker.showLoading("跳转中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_SAVE_MOVENEXTCATEGORY;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

}
