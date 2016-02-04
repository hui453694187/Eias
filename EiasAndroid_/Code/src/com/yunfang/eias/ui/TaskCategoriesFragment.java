package com.yunfang.eias.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.yunfang.eias.R;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.logic.TaskInfoMenuOperaotr;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.ui.Adapter.TaskCategoryInfoAdapter;
import com.yunfang.eias.viewmodel.TaskItemViewModel;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.ListUtil;

@SuppressLint("ValidFragment")
public class TaskCategoriesFragment extends BaseWorkerFragment {
	// {{ 属性

	/**
	 * 显示列表
	 */
	private ListView listView;

	/**
	 * 分类项
	 */
	private TaskCategoryInfoAdapter adapter;

	/**
	 * 任务ViewModel
	 * */
	public TaskItemViewModel viewModel = new TaskItemViewModel();

	/**
	 * 当前Fragment视图
	 */
	private View mView;

	/**
	 * 左侧按钮
	 */
	private Button btn_menu_add;

	/**
	 * 左侧按钮
	 */
	private Button btn_menu;

	/**
	 * 右侧按钮
	 */
	private Button btn_back;

	/**
	 * 
	 */
	private Button home_top_additional;
	
	/**
	 * 中间显示的信息
	 */
	private TextView home_top_title;

	/**
	 * 菜单
	 */
	private TaskInfoMenuOperaotr menuOperator;

	/**
	 * 构造方法
	 */
	public TaskCategoriesFragment() {

	}

	/**
	 * 构造方法
	 */
	public TaskCategoriesFragment(TaskItemViewModel viewModel) {
		this.viewModel = viewModel;
	}

	// }}

	// {{ 任务值
	/**
	 * 清空任务子项
	 * */
	public final int TASK_CLEARITEMS = 0;

	/**
	 * 删除分类项
	 */
	public final int TASK_DELETECATEGORIE = 1;

	/**
	 * 粘贴分类项
	 */
	public final int TASK_PASTEDCATEGORIE = 2;

	/**
	 * 修改分类项名称
	 */
	public final int TASK_MODIFYNAME = 3;

	// }}

	// {{ 方法

	/**
	 * 加载用户控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (viewModel.taskInfoActivity == null) {
			viewModel.taskInfoActivity = (TaskInfoActivity) getActivity();
			viewModel.taskInfoActivity.currentInstanceFragmentName = "TaskCategoriesFragment";
		}
		return inflater.inflate(R.layout.task_categories, null);
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
	public void intView() {
		getControls();
		setView();
	}

	/**
	 * 设置控件的信息和显示状态
	 */
	private void setView() {
		// 取得可以添加的勘察表分类项
		ResultInfo<ArrayList<DataCategoryDefine>> tempCategoryDefine = TaskOperator.getCanBeAddOrDeleteCategories(viewModel.currentTask, true);
		if (!ListUtil.hasData(tempCategoryDefine.Data)) {
			btn_menu_add.setVisibility(View.GONE);
		} else {
			btn_menu_add.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取控件
	 */
	@SuppressLint("CutPasteId")
	private void getControls() {

		mView = getView();
		menuOperator = new TaskInfoMenuOperaotr(this);

		adapter = new TaskCategoryInfoAdapter(viewModel.taskInfoActivity);

		listView = (ListView) mView.findViewById(R.id.task_items_lv);
		btn_back = (Button) mView.findViewById(R.id.list_reload);
		btn_menu = (Button) mView.findViewById(R.id.btn_menu);
		btn_menu_add = (Button) mView.findViewById(R.id.btn_menu_add);
		home_top_title = (TextView) mView.findViewById(R.id.home_top_title);
		home_top_additional = (Button) mView.findViewById(R.id.home_top_additional);
		
		Button btn_list_reload = (Button) mView.findViewById(R.id.list_reload);

		btn_menu.setVisibility(View.GONE);
		btn_menu_add.setVisibility(View.VISIBLE);
		btn_list_reload.setVisibility(View.GONE);

		listView.setOnScrollListener(viewModel.taskInfoActivity);
		btn_back.setText("返回");

		viewModel.taskInfoActivity.appHeader.visBackView(true);
		viewModel.taskInfoActivity.appHeader.setTitle(viewModel.currentTask.TaskNum);

		if (!viewModel.taskInfoActivity.additional) {
			home_top_title.setText("分类项信息");
		} else {
			home_top_title.setText("您可以添加补发文件了");
			home_top_additional.setVisibility(View.VISIBLE);
			home_top_additional.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					TaskOperator.additionalResource(viewModel.currentTask);
				}
			});
		}

		// 返回到之前的子项界面
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				viewModel.taskInfoActivity.backToDataItem();
			}
		});
		// 在任务中添加新的分类
		btn_menu_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				viewModel.taskInfoActivity.changFragment(OperatorTypeEnum.CategoryDefineCreated);
			}
		});

		initTaskList();
		viewModel.taskInfoActivity.refreshTaskCategory();
		refreshListView();
	}

	/**
	 * 填充任务列表
	 */
	private void initTaskList() {

		// 监听触摸事件
		listView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return viewModel.taskInfoActivity.touchPage(event.getAction(), event.getX(), event.getY(), false);
			}
		});

		// 监听点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				viewModel.taskInfoActivity.getCurrentDataCategoryDefine(position);
				viewModel.taskInfoActivity.changFragment(viewModel.currentDataCategoryDefine.ControlType);
			}
		});

		// 监听长按事件
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				viewModel.position = position;
				adapter.setSelectedPosition(position);// 记录长按的位置
				adapter.notifyDataSetChanged();

				viewModel.currentCategory = viewModel.currentTaskCategoryInfos.get(position);
				viewModel.taskInfoActivity.getCurrentDataCategoryDefine(position);
				menuOperator.showDialog();
				return true;
			}

		});

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
		viewModel.taskInfoActivity.loadingWorker.showLoading(msg);
		Message TaskMsg = new Message();
		TaskMsg.what = taskType;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 后台线程
	 */
	@Override
	protected void handlerBackgroundHandler(Message msg) {
		viewModel.ToastMsg = "";
		// 准备发送给UI线程的消息对象
		Message uiMsg = new Message();
		uiMsg.what = msg.what;
		switch (msg.what) {
		case TASK_CLEARITEMS:// 清空分类项子项
			clearCategorieItems(uiMsg);
			break;
		case TASK_DELETECATEGORIE:// 删除分类项
			ResultInfo<Boolean> result = TaskOperator.deleteCategorie(viewModel.currentTask, viewModel.currentCategory);
			uiMsg.obj = result;
			break;
		case TASK_PASTEDCATEGORIE:// 粘贴分类项
			pastedCategorie(uiMsg);
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
		super.handUiMessage(msg);
		ResultInfo<Boolean> result = (ResultInfo<Boolean>) msg.obj;
		switch (msg.what) {
		case TASK_CLEARITEMS: // 清空分类项子项
			break;
		case TASK_DELETECATEGORIE:// 删除分类项
			viewModel.currentTaskCategoryInfos.remove(viewModel.currentCategory);
			break;
		case TASK_PASTEDCATEGORIE:// 粘贴分类项
			break;
		default:
			break;
		}
		viewModel.taskInfoActivity.refreshTaskCategory();
		refreshListView();
		showToast(result.Message);
		viewModel.taskInfoActivity.loadingWorker.closeLoading();
	}

	/**
	 * 刷新列表
	 */
	public void refreshListView() {
		adapter.refersh(viewModel.currentTaskCategoryInfos, viewModel.currentDataDefine.Categories);
		listView.setAdapter(adapter);
	}

	/**
	 * 清空分类子项
	 * 
	 * @param uiMsg
	 */
	private void clearCategorieItems(Message uiMsg) {
		TaskCategoryInfo taskCategoryInfo = new TaskCategoryInfo();
		taskCategoryInfo.ID = viewModel.currentCategory.ID;
		taskCategoryInfo.TaskID = viewModel.currentCategory.TaskID;
		taskCategoryInfo.CategoryID = viewModel.currentCategory.CategoryID;
		taskCategoryInfo.BaseCategoryID=viewModel.currentCategory.BaseCategoryID;
		taskCategoryInfo.RemarkName = viewModel.currentCategory.RemarkName != null ? viewModel.currentCategory.RemarkName : "";
		ResultInfo<Boolean> result = TaskOperator.clearCategoryItems(viewModel.currentTask, taskCategoryInfo);
		uiMsg.obj = result;
	}

	/**
	 * 粘贴分类项
	 * 
	 * @param uiMsg
	 */
	private void pastedCategorie(Message uiMsg) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		// 复制的分类项
		TaskCategoryInfo copiedCategorie = viewModel.copyCategory;
		// 粘贴的分类项
		TaskCategoryInfo pastedCategorie = viewModel.currentCategory;
		// 当前任务对应的勘察表数据
		DataCategoryDefine dataCategoryDefine = viewModel.currentDataCategoryDefine;
		// 任务信息，包含任务下的分类项信息
		TaskInfo currentTask = viewModel.currentTask;
		// currentTask.TaskID = currentTask.ID;
		TaskOperator.mappingDataItems(copiedCategorie, pastedCategorie, dataCategoryDefine, currentTask, currentTask, OperatorTypeEnum.TaskDataCopy);
		result.Data = true;
		result.Message = "粘贴成功";
		uiMsg.obj = result;
	}

	// }}
}
