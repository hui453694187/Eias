/**
 * 
 */
package com.yunfang.eias.ui;

import java.util.ArrayList;

import com.yunfang.eias.R;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.ToastUtil;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.eias.viewmodel.TaskCategoriesViewModel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 创建新的分类项 或者
 * 
 * @author 陈惠森
 * 
 */
@SuppressLint("ValidFragment")
public class CreateCategoryFragment extends BaseWorkerFragment {
	// {{ 控件

	/**
	 * 下拉选项(可添加的勘察表分类项)
	 */
	Spinner categorySpinner;

	/**
	 * 分类名称
	 */
	TextView txtcategory_ed_name;

	/**
	 * 创建按钮
	 */
	Button btn_CreateCategory;

	/**
	 * 新建按钮
	 */
	Button btn_NewCategory;

	/**
	 * 返回按钮
	 */
	Button btn_CancelCreateCategory;

	/**
	 * 保存按钮
	 */
	Button btn_SaveCategoryName;

	/**
	 * 粘贴到新建项按钮
	 */
	Button btn_PastedNewCategory;

	// }}

	// {{ 相关变量

	public TaskCategoriesViewModel viewmodel;

	// }}

	/**
	 * 构造方法
	 */
	public CreateCategoryFragment() {

	}

	/**
	 * 构造方法
	 */
	public CreateCategoryFragment(OperatorTypeEnum operation) {
		viewmodel = new TaskCategoriesViewModel();
		viewmodel.operation = operation;
	}

	/**
	 * 加载用户控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.add_category, null);
	}

	/**
	 * 加载用户控件上的控件
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		getControls();
		setDispalyInfo();
	}

	/**
	 * 初始化页面参数
	 */
	private void setDispalyInfo() {
		// 获取传递的参数
		viewmodel.identityId = viewmodel.taskInfoActivity.viewModel.currentTask.ID;
		viewmodel.taskId = viewmodel.taskInfoActivity.viewModel.currentTask.TaskID;
		viewmodel.categoryIdentityId = viewmodel.taskInfoActivity.viewModel.currentCategory.ID;
		viewmodel.categoryId = viewmodel.taskInfoActivity.viewModel.currentCategory.CategoryID;
		viewmodel.isCreatedByUesr = viewmodel.taskInfoActivity.viewModel.currentTask.IsNew;
		viewmodel.name = viewmodel.taskInfoActivity.viewModel.currentDataCategoryDefine.Name != null ? viewmodel.taskInfoActivity.viewModel.currentDataCategoryDefine.Name
				: "";
		if (viewmodel.taskInfoActivity.viewModel.copyCategory != null) {
			viewmodel.copycategoryId = viewmodel.taskInfoActivity.viewModel.copyCategory.CategoryID;
			viewmodel.copyidentityId = viewmodel.taskInfoActivity.viewModel.copyCategory.ID;
			viewmodel.remarkName = viewmodel.taskInfoActivity.viewModel.copyCategory.RemarkName != null ? viewmodel.taskInfoActivity.viewModel.copyCategory.RemarkName
					: "";
		}
		switch (viewmodel.operation) {
		// 创建任务分类项
		case CategoryDefineCreated:
			// 设置控件状态
			btn_NewCategory.setEnabled(false);
			btn_SaveCategoryName.setVisibility(View.GONE);
			btn_PastedNewCategory.setVisibility(View.GONE);
			getCreateDataCategoryList();
			setDataCategoryList(true);
			txtcategory_ed_name.setText(categorySpinner.getSelectedItem() == null ? "" : categorySpinner.getSelectedItem().toString());
			break;
		// 保存分类
		case CategoryDefineNameModified:
			// 取得要修改分类项的相关信息
			viewmodel.remarkName = viewmodel.taskInfoActivity.viewModel.currentCategory.RemarkName != null ? viewmodel.taskInfoActivity.viewModel.currentCategory.RemarkName
					: "";
			// 设置控件状态
			btn_CancelCreateCategory.setVisibility(View.GONE);
			btn_CreateCategory.setVisibility(View.GONE);
			btn_NewCategory.setVisibility(View.GONE);
			btn_PastedNewCategory.setVisibility(View.GONE);
			getSaveDataCategoryList();
			setDataCategoryList(false);
			// 设置下拉只可视不可用
			categorySpinner.setEnabled(false);
			txtcategory_ed_name.setText(viewmodel.remarkName);
			break;
		// 粘贴并创建任务分类项
		case CategoryDefineDataCopyToNew:
			// 设置控件状态
			btn_CancelCreateCategory.setVisibility(View.GONE);
			btn_CreateCategory = (Button) mView.findViewById(R.id.btn_CreateCategory);
			btn_CreateCategory.setVisibility(View.GONE);
			btn_SaveCategoryName.setVisibility(View.GONE);
			btn_NewCategory.setVisibility(View.GONE);
			// btn_NewCategory.setVisibility(View.INVISIBLE);
			getSaveDataCategoryList();
			setDataCategoryList(false);
			// 设置下拉只可视不可用
			categorySpinner.setEnabled(false);
			txtcategory_ed_name.setText(viewmodel.name);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置下拉框默认值
	 */
	private void setSpinnerSelectValue() {
		if (viewmodel.name != null) {
			if (viewmodel.name.length() > 0) {
				for (int i = 0; i < viewmodel.category.length; i++) {
					if (viewmodel.category[i].equals(viewmodel.name)) {
						categorySpinner.setSelection(i);
						break;
					}
				}
			}
		}
	}

	/**
	 * 初始化控件
	 */
	private void getControls() {
		viewmodel.taskInfoActivity = (TaskInfoActivity) getActivity();
		mView = getView();

		categorySpinner = (Spinner) mView.findViewById(R.id.add_category_spinner_table);
		txtcategory_ed_name = (TextView) mView.findViewById(R.id.add_category_et_pid);
		btn_SaveCategoryName = (Button) mView.findViewById(R.id.btn_SaveCategoryName);
		btn_CreateCategory = (Button) mView.findViewById(R.id.btn_CreateCategory);
		btn_NewCategory = (Button) mView.findViewById(R.id.btn_NewCategory);
		btn_PastedNewCategory = (Button) mView.findViewById(R.id.btn_PastedNewCategory);
		btn_CancelCreateCategory = (Button) mView.findViewById(R.id.btn_CancelCreateNewTask);

		btn_NewCategory.setOnClickListener(btnClickLister);
		btn_PastedNewCategory.setOnClickListener(btnClickLister);
		btn_SaveCategoryName.setOnClickListener(btnClickLister);
		btn_CreateCategory.setOnClickListener(btnClickLister);
		btn_CancelCreateCategory.setOnClickListener(btnClickLister);
		categorySpinner.setOnItemSelectedListener(btnSelectLister);
	}

	/**
	 * 修改名称时获取勘察表分类项
	 */
	private void getSaveDataCategoryList() {
		// 获取一个任务信息
		ResultInfo<TaskInfo> taskInfo = TaskDataWorker.queryTaskInfo(viewmodel.isCreatedByUesr ? viewmodel.identityId : viewmodel.taskId,
				viewmodel.isCreatedByUesr);
		if (taskInfo.Data != null) {
			TaskInfo taskinfo = taskInfo.Data;
			// 取得可以添加的勘察表分类项
			ResultInfo<ArrayList<DataCategoryDefine>> tempCategoryDefine = TaskOperator.getAllCategories(taskinfo);
			if (ListUtil.hasData(tempCategoryDefine.Data)) {
				viewmodel.dataCategoryDefines = tempCategoryDefine.Data;
				// 声明存放变量
				if (ListUtil.hasData(viewmodel.dataCategoryDefines)) {
					int length = viewmodel.dataCategoryDefines.size();
					viewmodel.category = new String[length];
					int index = 0;
					for (DataCategoryDefine dataCategoryDefine : viewmodel.dataCategoryDefines) {
						// 赋值得到选中项所在位置
						viewmodel.category[index++] = dataCategoryDefine.Name;
					}
				}
			} else {
				viewmodel.category = new String[0];
			}
		}
	}

	/**
	 * 初始化加载勘察表分类项列表
	 */
	private void getCreateDataCategoryList() {
		// 获取一个任务信息
		ResultInfo<TaskInfo> taskInfo = TaskDataWorker.queryTaskInfo(viewmodel.isCreatedByUesr ? viewmodel.identityId : viewmodel.taskId,
				viewmodel.isCreatedByUesr);
		if (taskInfo.Data != null) {
			TaskInfo taskinfo = taskInfo.Data;
			// 取得可以添加的勘察表分类项
			ResultInfo<ArrayList<DataCategoryDefine>> tempCategoryDefine = TaskOperator.getCanBeAddOrDeleteCategories(taskinfo, true);
			if (ListUtil.hasData(tempCategoryDefine.Data)) {
				viewmodel.dataCategoryDefines = tempCategoryDefine.Data;
				// 声明存放变量
				if (ListUtil.hasData(viewmodel.dataCategoryDefines)) {
					int length = viewmodel.dataCategoryDefines.size();
					viewmodel.category = new String[length];
					int index = 0;
					for (DataCategoryDefine dataCategoryDefine : viewmodel.dataCategoryDefines) {
						viewmodel.category[index++] = dataCategoryDefine.Name;
					}
				}
			} else {
				viewmodel.category = new String[0];
			}
		}
	}

	/**
	 * 设置下拉列表中的分类项数据
	 */
	private void setDataCategoryList(Boolean isCreate) {
		viewmodel.categoryAdapter = new ArrayAdapter<String>(viewmodel.taskInfoActivity, android.R.layout.simple_spinner_item, viewmodel.category);
		viewmodel.categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(viewmodel.categoryAdapter);
		if (!isCreate) {
			// 设置下拉值
			setSpinnerSelectValue();
		}
	}

	// {{ 按钮点击事件
	private OnClickListener btnClickLister = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_CancelCreateNewTask:
				viewmodel.taskInfoActivity.refreshTaskCategory();
				viewmodel.taskInfoActivity.categoriesFragment.refreshListView();
				viewmodel.taskInfoActivity.toCategoriesFragment();
				break;
			case R.id.btn_CreateCategory:
				createCategory();
				break;
			case R.id.btn_NewCategory:
				newCategory();
				break;
			case R.id.btn_SaveCategoryName:
				saveCategoryName();
				break;
			case R.id.btn_PastedNewCategory:
				pastedNewCategory();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 添加列表选中事件
	 */
	private OnItemSelectedListener btnSelectLister = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (viewmodel.operation.equals(OperatorTypeEnum.CategoryDefineCreated) || viewmodel.operation.equals(OperatorTypeEnum.CategoryDefineDataReset)) {
				txtcategory_ed_name.setText(categorySpinner.getSelectedItem() == null ? "" : categorySpinner.getSelectedItem().toString());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	// }}

	/**
	 * 粘贴新建分类项信息
	 */
	private void pastedNewCategory() {
		int selectedIndex = categorySpinner.getSelectedItemPosition();
		String categoryName = txtcategory_ed_name.getText().toString().trim();
		if (categoryName.length() > 0 && selectedIndex > -1) {
			loadingWorker.showLoading("任务分类创建中...");
			Message msg = new Message();
			msg.what = OperatorTypeEnum.CategoryDefineDataCopyToNew.getIndex();
			mBackgroundHandler.sendMessage(msg);
		} else {
			showToast("请填写任务分类名称");
		}
	}

	/**
	 * 修改分类项名称
	 */
	private void saveCategoryName() {
		int selectedIndex = categorySpinner.getSelectedItemPosition();
		String categoryName = txtcategory_ed_name.getText().toString().trim();
		if (categoryName.length() > 0 && selectedIndex > -1) {
			btn_NewCategory.setEnabled(true);
			btn_CreateCategory.setEnabled(false);
			loadingWorker.showLoading("任务名称修改中...");
			Message msg = new Message();
			msg.what = OperatorTypeEnum.CategoryDefineNameModified.getIndex();
			mBackgroundHandler.sendMessage(msg);
		} else {
			showToast("请填写所有的内容值");
		}
	}

	/**
	 * 创建勘察表分类项
	 */
	private void createCategory() {
		int selectedIndex = categorySpinner.getSelectedItemPosition();
		String categoryName = txtcategory_ed_name.getText().toString().trim();
		if (categoryName.length() > 0 && selectedIndex > -1) {
			// btn_NewCategory.setEnabled(true);
			// btn_CreateCategory.setEnabled(false);
			loadingWorker.showLoading("任务创建中...");
			Message msg = new Message();
			msg.what = OperatorTypeEnum.CategoryDefineCreated.getIndex();
			mBackgroundHandler.sendMessage(msg);
		} else {
			showToast("请填写所有的内容值");
		}
	}

	/**
	 * 创建勘察表分类项
	 */
	private void newCategory() {
		// 清空分类名称
		txtcategory_ed_name.setText("");
		// 设置创建可见
		btn_CreateCategory.setEnabled(true);
		// 设置新建不可见
		btn_NewCategory.setEnabled(false);

		loadingWorker.showLoading("加载中...");
		Message msg = new Message();
		msg.what = OperatorTypeEnum.CategoryDefineDataReset.getIndex();
		;
		mBackgroundHandler.sendMessage(msg);
	}

	// {{ 进程调用重载类

	/**
	 * 粘贴创建任务分类项
	 * 
	 * @param resultMsg
	 */
	private void pastedNewCategory(Message resultMsg) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		// 判断该分类名称是否存在
		if (isExitCategoryName(0)) {
			result.Success = true;
			result.Data = false;
			result.Message = "分类名已经存在";
			resultMsg.obj = result;
			return;
		}
		// 得到要复制的勘察表分类项
		TaskCategoryInfo copyTaskCategoryInfo = TaskDataWorker.getTaskCategoryInfo(viewmodel.isCreatedByUesr, viewmodel.copyidentityId, viewmodel.taskId,
				viewmodel.copycategoryId, viewmodel.remarkName);
		// 得到选中位置
		int selectedIndex = categorySpinner.getSelectedItemPosition();
		// 得到选中的分类项
		DataCategoryDefine selectedCategoryDefine = viewmodel.dataCategoryDefines.get(selectedIndex);
		String newRemarkName = txtcategory_ed_name.getText().toString().trim();
		// new一个新项并赋值
		Integer dataDefineTotal = 0;
		for (DataFieldDefine item : selectedCategoryDefine.Fields) {
			if (item.ShowInPhone) {
				dataDefineTotal += 1;
			}
		}
		
		TaskCategoryInfo taskCategoryInfo = new TaskCategoryInfo();
		taskCategoryInfo.TaskID = viewmodel.isCreatedByUesr ? viewmodel.identityId : viewmodel.taskId;
		taskCategoryInfo.RemarkName = newRemarkName;
		// taskCategoryInfo.BaseCategoryID = selectedCategoryDefine.ID;
		taskCategoryInfo.BaseCategoryID = -1;
		taskCategoryInfo.CreatedDate = DateTimeUtil.getCurrentTime();
		taskCategoryInfo.DataDefineFinishCount = -1;
		taskCategoryInfo.DataDefineTotal = dataDefineTotal > 0 ? dataDefineTotal : -1;
		taskCategoryInfo.CategoryID = selectedCategoryDefine.CategoryID;
		TaskDataItem pastedItem = null;
		// 保存新建项
		Integer newId = (int) taskCategoryInfo.onInsert();
		// 取得任务分类项中的子项值
		ResultInfo<ArrayList<TaskDataItem>> taskDataItem = TaskDataWorker.queryTaskDataItemsByID(copyTaskCategoryInfo.TaskID, copyTaskCategoryInfo.CategoryID,
				copyTaskCategoryInfo.ID, false);
		try {
			// 将任务子项复制给新建项
			if (newId > 0 && taskDataItem.Data != null) {
				for (TaskDataItem copiedItem : taskDataItem.Data) {
					pastedItem = new TaskDataItem();
					pastedItem.TaskID = copiedItem.TaskID;
					pastedItem.CategoryID = taskCategoryInfo.CategoryID;
					pastedItem.IOrder = copiedItem.IOrder;
					pastedItem.Name = copiedItem.Name;
					pastedItem.Value = copiedItem.Value;
					pastedItem.BaseCategoryID = newId;
					pastedItem.BaseID = -1;
					pastedItem.onInsert();
				}
				result.Data = true;
				result.Message = "新建成功";
				result.Others = newId;

				DataLogOperator.categoryDefineDataCopyToNew(viewmodel.taskInfoActivity.viewModel.currentTask, copyTaskCategoryInfo, taskCategoryInfo, "");

			} else {
				result.Data = false;
				result.Message = "新建失败";
			}
		} catch (Exception e) {
			DataLogOperator.categoryDefineDataCopyToNew(viewmodel.taskInfoActivity.viewModel.currentTask, copyTaskCategoryInfo, taskCategoryInfo,
					e.getMessage());
		}
		resultMsg.obj = result;
	}

	/**
	 * 创建任务分类项
	 * 
	 * @param resultMsg
	 */
	private void createCategory(Message resultMsg) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		// 判断该分类名称是否存在
		if (isExitCategoryName(0)) {
			result.Success = true;
			result.Data = false;
			result.Message = "分类名已经存在";
			resultMsg.obj = result;
			return;
		}
		// 得到选中位置
		int selectedIndex = categorySpinner.getSelectedItemPosition();
		// 得到选中的分类项
		DataCategoryDefine selectedCategoryDefine = viewmodel.dataCategoryDefines.get(selectedIndex);
		String remarkName = txtcategory_ed_name.getText().toString().trim();
		// new一个新项并赋值
		TaskCategoryInfo taskCategoryInfo = null;
		try {
			Integer dataDefineTotal = 0;
			for (DataFieldDefine item : selectedCategoryDefine.Fields) {
				if (item.ShowInPhone) {
					dataDefineTotal += 1;
				}
			}
			taskCategoryInfo = new TaskCategoryInfo();
			taskCategoryInfo.TaskID = viewmodel.isCreatedByUesr ? viewmodel.identityId : viewmodel.taskId;
			taskCategoryInfo.RemarkName = remarkName;
			// taskCategoryInfo.BaseCategoryID = selectedCategoryDefine.ID;
			taskCategoryInfo.BaseCategoryID = -1;
			taskCategoryInfo.CreatedDate = DateTimeUtil.getCurrentTime();
			taskCategoryInfo.DataDefineFinishCount = -1;
			taskCategoryInfo.DataDefineTotal = dataDefineTotal > 0 ? dataDefineTotal : -1;
			taskCategoryInfo.CategoryID = selectedCategoryDefine.CategoryID;
			// 插入数据库并返回ID
			Integer newId = (int) taskCategoryInfo.onInsert();
			if (newId > 0) {
				// 取得任务类型所对应的勘察表子项
				ResultInfo<ArrayList<DataFieldDefine>> fielddafineResult = DataDefineWorker.queryDataFieldDefineByID(selectedCategoryDefine.DDID,
						selectedCategoryDefine.CategoryID);
				if (ListUtil.hasData(fielddafineResult.Data)) {
					for (DataFieldDefine define : fielddafineResult.Data) {
						TaskDataItem data = new TaskDataItem();
						data.BaseCategoryID = newId;
						data.BaseID = define.ID;
						data.CategoryID = define.CategoryID;
						data.IOrder = define.IOrder;
						data.Name = define.Name;
						data.TaskID = taskCategoryInfo.TaskID;
						data.Value = "";
						data.onInsert();
					}
				}
			}
			result.Data = true;
			result.Message = "新建成功";
			resultMsg.obj = result;
			DataLogOperator.categoryDefineCreated(viewmodel.taskInfoActivity.viewModel.currentTask, taskCategoryInfo, "");
		} catch (Exception e) {
			DataLogOperator.categoryDefineCreated(viewmodel.taskInfoActivity.viewModel.currentTask, taskCategoryInfo, e.getMessage());
		}
	}

	/**
	 * 保存任务分类项
	 * 
	 * @param resultMsg
	 */
	private void saveCategory(Message resultMsg) {
		// 得到要修改的勘察表分类项
		TaskCategoryInfo taskCategoryInfo = TaskDataWorker.getTaskCategoryInfo(viewmodel.isCreatedByUesr, viewmodel.categoryIdentityId, viewmodel.taskId,
				viewmodel.categoryId, viewmodel.remarkName);
		if (taskCategoryInfo != null) {
			String oldName = taskCategoryInfo.RemarkName;
			// 修改名称
			taskCategoryInfo.RemarkName = txtcategory_ed_name.getText().toString().trim();
			ResultInfo<Boolean> result = null;
			try {
				// 保存入数据库
				result = TaskOperator.saveCategoryInfo(taskCategoryInfo);
				DataLogOperator.categoryDefineNameModified(viewmodel.taskInfoActivity.viewModel.currentTask, taskCategoryInfo, oldName, "");
			} catch (Exception e) {
				DataLogOperator.categoryDefineNameModified(viewmodel.taskInfoActivity.viewModel.currentTask, taskCategoryInfo, oldName, e.getMessage());
			}
			resultMsg.obj = result;
		} else {
			ResultInfo<Boolean> result = new ResultInfo<Boolean>();
			result.Data = false;
			result.Message = "找不到修改项,请重试!";
			resultMsg.obj = result;
		}
	}

	/**
	 * 判断当前分类名称数据库中是否存在
	 * 
	 * @param id
	 *            分类项ID
	 * @return
	 */
	private Boolean isExitCategoryName(Integer id) {
		Boolean result = false;
		String remarkName = txtcategory_ed_name.getText().toString().trim();
		TaskCategoryInfo taskCategoryInfo = new TaskCategoryInfo();
		taskCategoryInfo.ID = id;
		taskCategoryInfo.TaskID = viewmodel.isCreatedByUesr ? viewmodel.identityId : viewmodel.taskId;
		taskCategoryInfo.RemarkName = remarkName;
		result = TaskOperator.isExitCategoryInfo(taskCategoryInfo);
		return result;
	}

	/**
	 * 后台线程
	 */
	@Override
	protected void handlerBackgroundHandler(Message msg) {
		// 准备发送给UI线程的消息对象
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		viewmodel.operation = OperatorTypeEnum.getEnumByValue(msg.what);
		ResultInfo<Boolean> temp = new ResultInfo<Boolean>();
		switch (viewmodel.operation) {
		case CategoryDefineCreated:
			if (!TaskOperator.submiting(viewmodel.taskInfoActivity.viewModel.currentTask.TaskNum)) {
				createCategory(resultMsg);
			} else {
				temp.Data = false;
				temp.Message = "当前任务正在提交中,将不能创建分类项";
				resultMsg.obj = temp;
			}
			break;
		case CategoryDefineDataReset:
			getCreateDataCategoryList();
			break;
		case CategoryDefineNameModified:
			if (!TaskOperator.submiting(viewmodel.taskInfoActivity.viewModel.currentTask.TaskNum)) {
				saveCategory(resultMsg);
			} else {
				temp.Data = false;
				temp.Message = "当前任务正在提交中,将不能修改分类项名称";
				resultMsg.obj = temp;
			}
			break;
		case CategoryDefineDataCopyToNew:
			if (!TaskOperator.submiting(viewmodel.taskInfoActivity.viewModel.currentTask.TaskNum)) {
				pastedNewCategory(resultMsg);
			} else {
				temp.Data = false;
				temp.Message = "当前任务正在提交中,将不能复制分类项";
				resultMsg.obj = temp;
			}
			break;
		default:
			showToast("没有找到任务执行的操作函数");
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/**
	 * 界面线程
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handUiMessage(Message msg) {
		super.handUiMessage(msg);
		viewmodel.operation = OperatorTypeEnum.getEnumByValue(msg.what);
		ResultInfo<Boolean> result = (ResultInfo<Boolean>) msg.obj;
		switch (viewmodel.operation) {
		case CategoryDefineCreated:
			if (result != null && result.Data) {
				// 重新设置下拉框值
				setDataCategoryList(false);
				btn_NewCategory.setEnabled(true);
				btn_CreateCategory.setEnabled(false);
			} else {
				ToastUtil.longShow(getActivity(), result.Message);
			}
			break;
		case CategoryDefineDataReset:
			setDataCategoryList(true);
			break;
		case CategoryDefineNameModified:
			if (result != null && result.Data) {
				// 重新设置下拉框值
				setDataCategoryList(false);
				viewmodel.taskInfoActivity.refreshTaskCategory();
				viewmodel.taskInfoActivity.categoriesFragment.refreshListView();
				viewmodel.taskInfoActivity.toCategoriesFragment();
			} else {
				ToastUtil.longShow(getActivity(), result.Message);
			}
			break;
		case CategoryDefineDataCopyToNew:
			// 新建成功后不可再新建
			if (result != null && result.Data) {
				btn_PastedNewCategory.setEnabled(false);
				viewmodel.taskInfoActivity.refreshTaskCategory();
				viewmodel.taskInfoActivity.categoriesFragment.refreshListView();
				viewmodel.taskInfoActivity.toCategoriesFragment();
			} else {
				ToastUtil.longShow(getActivity(), result.Message);
			}
			break;
		default:
			break;
		}
		if (result != null) {
			ToastUtil.longShow(viewmodel.taskInfoActivity, result.Message);
		}
		loadingWorker.closeLoading();
	}
	// }}
}
