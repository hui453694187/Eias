package com.yunfang.eias.beta.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.base.BroadRecordType;
import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.enumObj.CategoryType;
import com.yunfang.eias.beta.enumObj.OperatorTypeEnum;
import com.yunfang.eias.beta.logic.AppHeader;
import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.logic.TaskItemControlOperator;
import com.yunfang.eias.beta.logic.TaskListMenuOperaotr;
import com.yunfang.eias.beta.logic.TaskOperator;
import com.yunfang.eias.beta.model.DataCategoryDefine;
import com.yunfang.eias.beta.model.DataDefine;
import com.yunfang.eias.beta.model.DataFieldDefine;
import com.yunfang.eias.beta.model.MediaDataInfo;
import com.yunfang.eias.beta.model.TaskCategoryInfo;
import com.yunfang.eias.beta.model.TaskDataItem;
import com.yunfang.eias.beta.model.TaskInfo;
import com.yunfang.eias.beta.tables.DataDefineWorker;
import com.yunfang.eias.beta.tables.TaskDataWorker;
import com.yunfang.eias.beta.ui.Adapter.MeidaListAdapter;
import com.yunfang.eias.beta.viewmodel.TaskItemViewModel;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.base.BaseWorkerFragmentActivity;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.ListUtil;

/**
 * 
 * 项目名称：yunfang.eias 类名称：TaskInfoActivity 类描述：用于显示任务下所属分类下所有子项信息的Activity 创建人：贺隽
 * 创建时间：2014-4-30 下午2:57:19
 * 
 * @version
 */
@SuppressLint("ShowToast")
public class TaskInfoActivity extends BaseWorkerFragmentActivity implements OnScrollListener {

	// {{ 属性

	public String currentInstanceFragmentName = "";

	/**
	 * 存储图片
	 */
	public ArrayList<MediaDataInfo> meidaInfos = new ArrayList<MediaDataInfo>();

	/**
	 * 主菜单的广播
	 */
	public AppHeader appHeader;

	/***/
	private TextView taskAddress;
	
	/**
	 * 当前所在fragment中的名字
	 */
	private String currentFragmentName = "";

	/**
	 * 最后一个打开的子项名称
	 */
	private String lastDataItemFragmentName = "";

	/**
	 * 记录分类项界面的名称
	 */
	private String categoryFragmentName = "";

	/**
	 * 当前所在的Fragment
	 */
	private Fragment currentFragment;

	/**
	 * 视图对象
	 */
	public TaskItemViewModel viewModel = new TaskItemViewModel();

	/**
	 * 主体的FrameLayout
	 */
	private FrameLayout frameLayout;

	/**
	 * 分类项界面
	 */
	public TaskCategoriesFragment categoriesFragment;

	/**
	 * 在屏幕开始碰触的水平位置
	 * */
	private float touchStartX;

	/**
	 * 在屏幕开始碰触的垂直位置
	 * */
	private float touchStartY;

	/**
	 * 移动的X位置
	 */
	public float moveX = 0;

	/**
	 * 移动的Y位置
	 */
	public float moveY = 0;

	/**
	 * 允许在屏幕触发左滑动和右滑动的滑动距离
	 * */
	public final float TOUCH_DISTANCE = 20;

	/**
	 * 是否补充资源
	 */
	public boolean additional = false;

	/**
	 * 
	 */
	public TaskListMenuOperaotr menuOperator;

	/**
	 * 资源适配数据
	 */
	public MeidaListAdapter meidaListAdapter;
	// }}

	// {{ 任务加载类型值
	/**
	 * 获取任务的分类项数据
	 */
	private final int TASK_GETTASKINFO = 0;

	/**
	 * 保存媒体信息
	 */
	private final int TASK_SAVE_MEDIA_INFO = 1;

	/**
	 * 保存媒体信息多个
	 */
	private final int TASK_SAVE_MEDIAS_INFO = 2;
	/** 有默认选中项保存图片信息 */
	private final int TASK_SAVE_MEDIA_DEFAULT_INFO = 3;

	/**
	 * 分类项位置索引
	 */
	public Integer categoryIndex = 0;

	// }}

	// {{ 进程调用重载类
	@Override
	protected void handleBackgroundMessage(Message msg) {
		viewModel.ToastMsg = "";
		// 准备发送给UI线程的消息对象
		Message uiMsg = new Message();
		uiMsg.what = msg.what;
		switch (msg.what) {
		case TASK_GETTASKINFO:
			Bundle bundle = getIntent().getExtras();
			Integer taskId = bundle.getInt("taskId");
			Integer identityId = bundle.getInt("identityId");
			ResultInfo<TaskInfo> temp = TaskOperator.getTaskInfo(taskId, identityId);
			if (temp != null && temp.Data != null && temp.Data.ID > 0) {
				// 如果任务所属报告完成就删除资源文件
				TaskOperator.removeTaskResource(temp.Data);
				// 如果当前任务没有默认值就添加默认值
				TaskOperator.setItemDefaultValue(temp.Data);

				viewModel.currentTaskCategoryInfos = temp.Data.Categories;
				viewModel.currentTask = temp.Data;
				String taskNum = viewModel.currentTask.TaskNum;
				getCurrentDataCategoryDefine(0);
				task_p_dir = TaskItemControlOperator.mkResourceDir(taskNum, EIASApplication.photo);
				task_a_dir = TaskItemControlOperator.mkResourceDir(taskNum, EIASApplication.audio);
				task_v_dir = TaskItemControlOperator.mkResourceDir(taskNum, EIASApplication.video);

				// 如果包含附加资源
				if (bundle.containsKey("additional") && bundle.getBoolean("additional")) {
					additional = true;
					ArrayList<TaskCategoryInfo> dataItems = new ArrayList<TaskCategoryInfo>();
					for (TaskCategoryInfo dataItem : temp.Data.Categories) {
						for (DataCategoryDefine defineItem : viewModel.currentDataDefine.Categories) {
							if (dataItem.CategoryID == defineItem.CategoryID && (defineItem.ControlType == CategoryType.PictureCollection)) {
								dataItems.add(dataItem);
							}
						}
					}
					viewModel.currentTaskCategoryInfos = dataItems;
					viewModel.currentTask.Categories = dataItems;
					temp.Data = viewModel.currentTask;
				}
			} else {
				// temp.Message = "请更新勘察表";
				temp.Success = false;
			}
			uiMsg.obj = temp;
			break;
		case TASK_SAVE_MEDIA_INFO: {
			Bundle param = msg.getData();
			ResultInfo<Boolean> saveMediaInfoResult = TaskOperator.saveMediaInfo(this, viewModel.currentTask, viewModel.currentCategory, param.getString("dataItemName"),
					param.getString("dataItemValue"), param.getString("beforeType"), param.getString("beforeValue"), param.getBoolean("delete"));

			uiMsg.setData(param);
			uiMsg.obj = saveMediaInfoResult;
			break;
		}
		case TASK_SAVE_MEDIAS_INFO: {
			Bundle param = msg.getData();
			String[] datas = param.getStringArray("datas");
			String categoryType = param.getString("categoryType");
			CategoryType mediaType = CategoryType.valueOf(categoryType);
			String filePath = getFilePath(mediaType);
			ResultInfo<Boolean> saveMediaInfoResult = new ResultInfo<Boolean>();
			File mediaFile = null;
			for (String item : datas) {
				/*
				 * // 保存图片信息到数据库， 刷新缓存等操作 MediaDataInfo mData = new
				 * MediaDataInfo(item, new File(filePath + File.separator +
				 * item)); mData.itemFileName = item + ";"; mData.ItemName =
				 * item; mData.ItemValue = item + ";"; mData.CategoryId = 0;
				 * meidaInfos.add(mData); saveMediaInfoResult =
				 * TaskOperator.saveMediaInfo(this, viewModel.currentTask,
				 * viewModel.currentCategory, mData.itemFileName,
				 * mData.ItemValue, "", "", false);
				 */
				mediaFile = new File(filePath + File.separator + item);
				MediaDataInfo mData = new MediaDataInfo("未分类", mediaFile);
				setDropDefaultSelect(mData, mediaFile);
			}
			uiMsg.setData(param);
			uiMsg.obj = saveMediaInfoResult;
			break;
		}
		case TASK_SAVE_MEDIA_DEFAULT_INFO:// 保存有默认选项的图片信息
			MediaDataInfo mediaInfo = (MediaDataInfo) msg.obj;
			this.saveTaskItemValue(CategoryType.PictureCollection, mediaInfo, mediaInfo.ItemName, true);

			break;
		default:
			break;
		}
		// 发信息给UI线程
		mUiHandler.sendMessage(uiMsg);
	}

	/**
	 * 获取指定文件类型的文件夹路径
	 * 
	 * @param categoryType
	 * @return
	 */
	public String getFilePath(CategoryType categoryType) {
		// 获取当前媒体所属类型的文件夹目录
		String filePath = "";
		String taskNum = viewModel.currentTask.TaskNum;
		switch (categoryType) {
		case VideoCollection:
			filePath = TaskItemControlOperator.mkResourceDir(taskNum, EIASApplication.video);
			break;
		case AudioCollection:
			filePath = TaskItemControlOperator.mkResourceDir(taskNum, EIASApplication.audio);
			break;
		case PictureCollection:
			filePath = TaskItemControlOperator.mkResourceDir(taskNum, EIASApplication.photo);
			break;
		default:
			break;
		}
		return filePath;
	}

	/**
	 * 获取当前需要显示的子项信息
	 * 
	 * @param index
	 *            :选择的分类项
	 */
	public void getCurrentDataCategoryDefine(Integer index) {
		viewModel.currentCategory = viewModel.currentTaskCategoryInfos.get(index);
		ResultInfo<DataDefine> defineInfo = DataDefineWorker.getCompleteDataDefine(viewModel.currentTask.DDID);
		if (defineInfo.Success && defineInfo.Data != null && defineInfo.Data.ID > 0) {
			viewModel.currentDataDefine = defineInfo.Data;
			viewModel.currentDataCategoryDefine = null;
			for (DataCategoryDefine categoryDefine : defineInfo.Data.Categories) {
				if (categoryDefine.CategoryID == viewModel.currentCategory.CategoryID) {
					viewModel.currentDataCategoryDefine = categoryDefine;
					// 如果是 音频、视频或者图片拿去下拉列表选择的值
					setCurrentDropDownListData(categoryDefine);
					break;
				}
			}
		}
	}

	/**
	 * 如果是 音频、视频或者图片拿去下拉列表选择的值
	 * 
	 * @param categoryDefine
	 *            :当前勘察表的分类项
	 */
	private void setCurrentDropDownListData(DataCategoryDefine categoryDefine) {
		viewModel.currentDropDownListData = new ArrayList<String>();
		// viewModel.currentDropDownListData
		// .add(TaskItemControlOperator.DefaultDropDownListValue);
		if (categoryDefine.ControlType == CategoryType.AudioCollection || categoryDefine.ControlType == CategoryType.PictureCollection || categoryDefine.ControlType == CategoryType.VideoCollection) {
			for (DataFieldDefine defineItem : categoryDefine.Fields) {
				// 手机端是否显示
				if (defineItem.ShowInPhone) {
					viewModel.currentDropDownListData.add(defineItem.Name);
				}
			}
		}
	}

	/**
	 * 前台界面回调方法
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch (msg.what) {
		case TASK_GETTASKINFO:
			ResultInfo<TaskInfo> getInfo = (ResultInfo<TaskInfo>) msg.obj;
			if (getInfo.Success) {
				showCategoryItems();
				//社会任务地址
				taskAddress.setText(viewModel.currentTask.TargetAddress);
				taskAddress.setVisibility(View.VISIBLE);
			} else {
				showToast(getInfo.Message);
				finish();
			}
			break;
		case TASK_SAVE_MEDIA_INFO:
		case TASK_SAVE_MEDIAS_INFO:
			Bundle param = msg.getData();
			String categoryType = param.getString("categoryType");
			ResultInfo<Boolean> saveMediaInfoResult = (ResultInfo<Boolean>) msg.obj;
			if (saveMediaInfoResult.Data != null && saveMediaInfoResult.Data) {
				try {
					refreshTaskCategory();
					CategoryType media = CategoryType.valueOf(categoryType);
					if (media == CategoryType.PictureCollection) {
						if (param.getBoolean("refresh")) {
							sortTaskItemValue();
						}
					} else {
						changFragment(CategoryType.valueOf(categoryType));
					}
				} catch (Exception e) {
					DataLogOperator.other("TaskInfoActivity.handleUiMessage.TASK_SAVE_MEDIAS_INFO:" + e.getMessage());
				}
			}
			/* showToast(saveMediaInfoResult.Message); */
			break;
		case TASK_SAVE_MEDIA_DEFAULT_INFO:// 保存完图片刷新界面
			this.meidaListAdapter.notifyDataSetChanged();
			break;
		}
		loadingWorker.closeLoading();
	}

	/**
	 * 按添加图片、未选择类型、选择类型 排序
	 */
	public void sortTaskItemValue() {
		Collections.sort(meidaInfos, new Comparator<MediaDataInfo>() {
			@Override
			public int compare(MediaDataInfo data1, MediaDataInfo data2) {
				String value1 = data1.CategoryId + "";
				String value2 = data2.CategoryId + "";
				return value1.compareTo(value2);
			}
		});
		meidaListAdapter.notifyDataSetChanged();
	}

	/**
	 * 刷新任务下面的分类项完成数据
	 */
	public void refreshTaskCategory() {
		Integer taskID = viewModel.currentTask.IsNew ? viewModel.currentTask.ID : viewModel.currentTask.TaskID;
		Integer baseCategoryID = viewModel.currentCategory.BaseCategoryID > 0 ? viewModel.currentCategory.BaseCategoryID : viewModel.currentCategory.ID;

		viewModel.currentTaskCategoryInfos = TaskDataWorker.queryTaskCategories(taskID, viewModel.currentTask.IsNew, false).Data;
		// 如果为补发资源模式
		additional();

		ArrayList<TaskDataItem> inputData = TaskDataWorker.queryTaskDataItemsByID(taskID, viewModel.currentCategory.CategoryID, baseCategoryID, false).Data;
		// TaskDataWorker.queryTaskDataItemsByID(taskID,viewModel.currentCategory.CategoryID,
		// viewModel.currentCategory.ID,false).Data;

		// 取得勘察表子项列表
		ResultInfo<ArrayList<DataFieldDefine>> dataFieldDefines = DataDefineWorker.queryDataFieldDefineByID(viewModel.currentTask.DDID, viewModel.currentCategory.CategoryID);
		ArrayList<DataFieldDefine> fieldDefines = new ArrayList<DataFieldDefine>();
		if (dataFieldDefines.Data != null) {
			fieldDefines = dataFieldDefines.Data;
		}

		// 过滤勘察表中不存在的任务子项
		ArrayList<TaskDataItem> filterTaskDataItems = TaskDataWorker.filterDataItem(inputData, fieldDefines);

		int valueCount = 0;
		for (TaskDataItem item : filterTaskDataItems) {
			if (item.Value.trim().length() > 0 && !item.Value.trim().equals(EIASApplication.DefaultDropDownListValue) && !item.Value.trim().equals(EIASApplication.DefaultHorizontalLineValue)
					&& !item.Value.trim().equals(EIASApplication.DefaultNullString)) {
				// ShowInPhone 为ture时,才在分类项中添加一个数量
				Boolean isAdd = false;
				for (int i = 0; i < fieldDefines.size(); i++) {
					if (item.Name.equals(fieldDefines.get(i).Name)) {
						if (fieldDefines.get(i).ShowInPhone) {
							isAdd = true;
							break;
						}
					}
				}
				if (isAdd) {
					valueCount += 1;
				}
			}
		}
		viewModel.currentCategory.DataDefineFinishCount = valueCount;
		for (TaskCategoryInfo category : viewModel.currentTaskCategoryInfos) {
			if (category.ID == viewModel.currentCategory.ID) {
				category.DataDefineFinishCount = valueCount;
				break;
			}
		}
	}

	/**
	 * 如果包含附加资源 310
	 */
	private void additional() {
		if (additional) {
			ArrayList<TaskCategoryInfo> dataItems = new ArrayList<TaskCategoryInfo>();
			for (TaskCategoryInfo dataItem : viewModel.currentTaskCategoryInfos) {
				for (DataCategoryDefine defineItem : viewModel.currentDataDefine.Categories) {
					if (dataItem.CategoryID == defineItem.CategoryID && (defineItem.ControlType == CategoryType.PictureCollection)) {
						dataItems.add(dataItem);
					}
				}
			}
			viewModel.currentTaskCategoryInfos = dataItems;
			viewModel.currentTask.Categories = dataItems;
		}
	}

	// }}

	// {{ 下面的数字位数 代表 菜单级别 请勿随便修改

	/**
	 * 拍照的标志
	 * */
	public final int TASK_ITEMS = 1;

	/**
	 * 进入图片列表
	 * */
	public final int TASK_PHOTOITEM = 10;

	/**
	 * 图库
	 * */
	public final int TASK_PHOTOLIB = 101;

	/**
	 * 拍照
	 * */
	public final int TASK_PHOTO = 102;

	/**
	 * 进入录音列表
	 * */
	public final int TASK_AUDIOITEM = 11;

	/**
	 * 音频库
	 * */
	public final int TASK_AUDIOLIB = 111;

	/**
	 * 录音
	 * */
	public final int TASK_AUDIO = 112;

	/**
	 * 进入视频列表
	 * */
	public final int TASK_VEDIOITEM = 12;

	/**
	 * 视频库
	 * */
	public final int TASK_VEDIOLIB = 121;

	/**
	 * 录像
	 * */
	public final int TASK_VEDIO = 123;

	// }}

	// {{ 路径属性

	/**
	 * 照片资源的根目录
	 * */
	public String task_p_dir = "";

	/**
	 * 音频资源的根目录
	 * */
	public String task_a_dir = "";

	/**
	 * 视频资源的根目录
	 * */
	public String task_v_dir = "";

	// }}

	// {{ 基类创建重载方法
	/**
	 * 界面创建时
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_info);
		receiverCameraBack();
		intView();
	}

	/**
	 * 释放资源
	 */
	@Override
	protected void onDestroy() {
		try {
			for (MediaDataInfo item : meidaInfos) {
				if (item.ThumbnailPhoto != null && !item.ThumbnailPhoto.isRecycled()) {
					item.ThumbnailPhoto.recycle();
				}
				item.ThumbnailPhoto = null;
			}
			appHeader.unRegisterReceiver();
		} catch (Exception e) {
			DataLogOperator.other("TaskInfoActivity.onDestroy.Exception:" + e.getLocalizedMessage());
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// }}

	// {{ 数据加载与显示

	/**
	 * 保存媒体信息
	 * 
	 * @param beforeType修改之前的类型
	 * @param beforeValue修改之前的文件名称
	 * @param dataItemName现在的类型
	 * @param dataItemValue现在的文件名称
	 * @param categoryType当前资源类型
	 * @param refresh是否立刻刷新
	 */
	public void doSaveMediaInfo(String beforeType, String beforeValue, String dataItemName, String dataItemValue, CategoryType categoryType, boolean refresh, boolean delete) {
		// loadingWorker.showLoading("操作中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_SAVE_MEDIA_INFO;
		Bundle param = new Bundle();
		param.putString("beforeType", beforeType);
		param.putString("beforeValue", beforeValue);
		param.putString("dataItemName", dataItemName);
		param.putString("dataItemValue", dataItemValue);
		param.putString("categoryType", categoryType.toString());
		param.putBoolean("delete", delete);
		param.putBoolean("refresh", refresh);
		TaskMsg.setData(param);
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 保存多个媒体信息
	 * 
	 * @param datas需要保存的文件名称数组
	 * @param categoryType当前资源类型
	 * @param refresh是否立刻刷新
	 */
	public void doSaveMediasInfo(String[] datas, CategoryType categoryType, boolean refresh) {
		loadingWorker.showLoading("操作中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_SAVE_MEDIAS_INFO;
		Bundle param = new Bundle();
		param.putStringArray("datas", datas);
		param.putString("categoryType", categoryType.toString());
		param.putBoolean("refresh", refresh);
		TaskMsg.setData(param);
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 加载任务相关信息
	 */
	private void loadData() {
		loadingWorker.showLoading("数据加载中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_GETTASKINFO;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 显示当前的选中分类项的子项信息
	 */
	private void showCategoryItems() {
		if (viewModel.currentCategory != null && viewModel.currentDataDefine != null && ListUtil.hasData(viewModel.currentDataDefine.Categories)) {
			DataCategoryDefine tempDefine = null;
			for (DataCategoryDefine define : viewModel.currentDataDefine.Categories) {
				if (viewModel.currentCategory.CategoryID == define.CategoryID) {
					tempDefine = define;
					break;
				}
			}

			if (tempDefine != null) {
				// changFragment(tempDefine.ControlType);
				toCategoriesFragment();
			}
		}
	}

	// }}

	/**
	 * 初始化控件
	 * */
	private void intView() {
		appHeader = new AppHeader(this, R.id.home_title);
		frameLayout = (FrameLayout) findViewById(R.id.task_Info_frameLayout);
		taskAddress=(TextView)findViewById(R.id.task_address_tv);
		
		frameLayout.setOnTouchListener(menuBodyOnTouchListener);
		menuOperator = new TaskListMenuOperaotr(this);
		loadData();
	}

	/**
	 * 任务列表的触屏事件
	 */
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener menuBodyOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return touchPage(event.getAction(), event.getX(), event.getY(), false);
		}
	};

	/**
	 * 滑动到指定的界面
	 * 
	 * @param action
	 *            :操作事件类型
	 * @param x
	 *            :x轴的坐标
	 * @param y
	 *            :y轴的坐标
	 * @param defaultResult
	 *            :默认显示的值
	 * @return
	 */
	public boolean touchPage(int action, float x, float y, boolean defaultResult) {
		boolean result = defaultResult;
		int toIndex = 1;// currentPageIndex;
		switch (action) {
		// 手指按下
		case MotionEvent.ACTION_DOWN:
			// 记录开始坐标
			touchStartX = x;
			touchStartY = y;
			moveX = 0;
			moveY = 0;
			break;
		// 手指抬起
		case MotionEvent.ACTION_UP:
			// 滑动距离必须是一定范围,X和Y的距离
			moveX = (x - touchStartX > 0 ? x - touchStartX : touchStartX - x);
			moveY = (y - touchStartY > 0 ? y - touchStartY : touchStartY - y);
			if ((moveY * 2 < moveX) && moveX > TOUCH_DISTANCE && moveY > TOUCH_DISTANCE / 2) {
				// 往左滑动
				if (x < touchStartX) {
					toIndex += 1;
				}
				// 往右滑动
				else if (touchStartX < x) {
					toIndex -= 1;
				}

				if (toIndex <= 0) {
					// toIndex = touchIdArray.length;
				}
				// else if (toIndex > touchIdArray.length) {
				// toIndex = 1;
				// }
				//
				// ((RadioButton)this.findViewById(touchIdArray[toIndex-1])).setChecked(true);
				result = true;
			}
			break;
		}
		return result;
	}

	/**
	 * 切换到分类界面
	 * 
	 */
	public void toCategoriesFragment() {

		currentFragmentName = "CategoriesFragment";
		currentInstanceFragmentName = "CategoriesFragment";
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = null;
		if (categoryFragmentName.length() > 0) {
			fragment = fm.findFragmentByTag(String.valueOf(categoryFragmentName));
		} else {
			fragment = fm.findFragmentByTag(String.valueOf(lastDataItemFragmentName));
		}
		if (fragment != null) {
			ft.hide(fragment);
		}
		fragment = fm.findFragmentByTag(currentFragmentName);
		if (fragment == null) {
			fragment = new TaskCategoriesFragment(viewModel);
			categoriesFragment = (TaskCategoriesFragment) fragment;
		} else {
			((TaskCategoriesFragment) fragment).intView();
		}

		if (fragment != null && fragment.isAdded()) {
			ft.show(fragment);
		} else {
			ft.add(R.id.task_Info_frameLayout, fragment, currentFragmentName);
		}
		ft.commitAllowingStateLoss();
	}

	/**
	 * 返回到之前的子项
	 * 
	 */
	public void backToDataItem() {
		CategoryType categoryType = CategoryType.valueOf(lastDataItemFragmentName);
		changFragment(categoryType);
	}

	/**
	 * 切换用户控件
	 * 
	 * @param 需要切换的用户控件
	 */
	public void changFragment(CategoryType type) {
		categoryFragmentName = "";
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (currentFragmentName.length() > 0) {
			currentFragment = fm.findFragmentByTag(String.valueOf(currentFragmentName));
			if (currentFragment != null) {
				ft.hide(currentFragment);
			}
		}
		currentFragmentName = type.toString();
		currentFragment = fm.findFragmentByTag(currentFragmentName);

		switch (type) {
		case Normal:
			currentFragment = new TaskItemsFragment();
			lastDataItemFragmentName = currentFragmentName;
			break;
		case PictureCollection:
			currentFragment = new ShowMediaListFragment(CategoryType.PictureCollection);
			lastDataItemFragmentName = currentFragmentName;
			break;
		case AudioCollection:
			currentFragment = new ShowMediaListFragment(CategoryType.AudioCollection);
			lastDataItemFragmentName = currentFragmentName;
			break;
		case VideoCollection:
			currentFragment = new ShowMediaListFragment(CategoryType.VideoCollection);
			lastDataItemFragmentName = currentFragmentName;
			break;
		default:
			break;
		}
		if (currentFragment != null && currentFragment.isAdded()) {
			ft.show(currentFragment);
		} else {
			ft.add(R.id.task_Info_frameLayout, currentFragment, currentFragmentName);
		}
		ft.commitAllowingStateLoss();
	}

	/**
	 * 切换分类项操作 如创建分类项、粘贴新建分类项、修改分类项名称
	 * 
	 * @param 需要切换的用户控件
	 */
	public void changFragment(OperatorTypeEnum type) {
		categoryFragmentName = "";
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (currentFragmentName.length() > 0) {
			currentFragment = fm.findFragmentByTag(String.valueOf(currentFragmentName));
			if (currentFragment != null) {
				ft.hide(currentFragment);
			}
		}
		currentFragmentName = type.toString();
		currentFragment = fm.findFragmentByTag(currentFragmentName);
		switch (type) {
		case CategoryDefineCreated:
			currentFragment = new CreateCategoryFragment(OperatorTypeEnum.CategoryDefineCreated);
			break;
		case CategoryDefineNameModified:
			currentFragment = new CreateCategoryFragment(OperatorTypeEnum.CategoryDefineNameModified);
			break;
		case CategoryDefineDataCopyToNew:
			currentFragment = new CreateCategoryFragment(OperatorTypeEnum.CategoryDefineDataCopyToNew);
			break;
		default:
			break;
		}
		categoryFragmentName = currentFragmentName;

		if (currentFragment != null && currentFragment.isAdded()) {
			ft.show(currentFragment);
		} else {
			ft.add(R.id.task_Info_frameLayout, currentFragment, currentFragmentName);
		}
		ft.commitAllowingStateLoss();
	}

	/**
	 * 切换用户控件
	 * 
	 * @param 需要切换的用户控件
	 */
	public void changMediaFragment(int requestCode, MediaDataInfo media, Boolean isNewFile) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (currentFragmentName.length() > 0) {
			currentFragment = fm.findFragmentByTag(String.valueOf(currentFragmentName));
			if (currentFragment != null) {
				ft.hide(currentFragment);
			}
		}
		currentFragmentName = String.valueOf(requestCode);
		currentFragment = fm.findFragmentByTag(currentFragmentName);

		switch (requestCode) {
		case TASK_PHOTOLIB:
		case TASK_PHOTO:
			currentFragment = new ShowPhotoFragment(media);
			break;
		case TASK_AUDIOLIB:
		case TASK_AUDIO:
			currentFragment = new ShowAudioFragment(media, isNewFile);
			break;
		case TASK_VEDIOLIB:
		case TASK_VEDIO:
			currentFragment = new ShowVideoFragment(media, isNewFile);
			break;

		default:
			break;
		}
		lastDataItemFragmentName = currentFragmentName;
		if (currentFragment != null && currentFragment.isAdded()) {
			ft.show(currentFragment);
		} else {
			ft.add(R.id.task_Info_frameLayout, currentFragment, currentFragmentName);
		}
		ft.commitAllowingStateLoss();
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	// {{ 媒体界面需要用到的公共方法

	/**
	 * 根据原图获取需要生成的缩略图路径
	 * 
	 * @param sourceFullName
	 * @return
	 */
	public String getThumbnailFullName(File file) {
		String tString = EIASApplication.getInstance().getString(R.string.thumbnail_dir);
		String pString = EIASApplication.getInstance().getString(R.string.project_dir);
		String t_dir = file.getParent().replace(pString, tString);
		FileUtil.mkDir(t_dir);
		String result = t_dir + File.separator + file.getName();
		return result;
	}

	/**
	 * 取得文件编号
	 * 
	 * @param selectedItemName
	 * @return
	 */
	public String getTempNumName(String selectedItemName) {
		int num = 1;
		for (TaskDataItem item : viewModel.currentCategory.Items) {
			if (item.Name.equals(selectedItemName) && item.Value != null && item.Value.replace("null", "").length() > 0) {
				num += item.Value.split(MediaDataInfo.Semicolon).length;
			}
		}
		return selectedItemName + " " + String.valueOf(num);
	}

	/**
	 * 取得子项原来的数据
	 * 
	 * @param selectedItemName
	 * @return
	 */
	public String getTempPastName(String selectedItemName) {
		String result = "";
		for (TaskDataItem item : viewModel.currentCategory.Items) {
			if (item.Name.equals(selectedItemName) && item.Value != null && !item.Value.equals("null")) {
				result = item.Value;
			}
		}
		return result;
	}

	/**
	 * 取得当前打开的子项名称
	 * 
	 * @param pictureName
	 *            子项名称
	 * @return 子项名称
	 */
	public String getSeleteDataItem(String itemValue) {
		String result = "";
		for (TaskDataItem item : viewModel.currentCategory.Items) {
			if (item.Value.contains(itemValue)) {
				result = item.Name;
			}
		}
		return result;
	}

	/**
	 * 取得随机新生成的文件路径
	 * 
	 * @param mediaRoot
	 *            :媒体文件目录 从任务编号开始
	 * @param suffix
	 *            :需要过滤的文件名后缀
	 * @return
	 */
	public String getTempUUIDNewName(String mediaRoot, String suffix) {
		String targetFileName = mediaRoot + File.separator + UUID.randomUUID() + suffix;
		return targetFileName;
	}

	/**
	 * 排序文件夹下的文件
	 * 
	 * @param files
	 *            :需要排序的文件夹列表
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList setFilesOrder(File[] files) {
		ArrayList<String> mPathString = new ArrayList<String>();
		for (File fileItem : files) {
			mPathString.add(fileItem.getAbsolutePath());
		}
		Collections.sort(mPathString);
		ArrayList<File> mediaFileArray = new ArrayList<File>();
		for (String path : mPathString) {
			mediaFileArray.add(new File(path));
		}

		return mediaFileArray;
	}

	/**
	 * 格式化文件名称 去掉后缀 如Ada-1.JPG 变为 Ada
	 * 
	 * @param file
	 *            :需要格式化的文件
	 * @return
	 */
	public String formatFileName(File file) {
		String result = "";
		if (file.exists()) {
			result = file.getName();
			result = result.substring(0, result.indexOf(MediaDataInfo.SuffixSymbol));
		}
		return result;
	}

	/**
	 * 获取当前分类下子项媒体全部的文件名称
	 * 
	 * @param dataItemName
	 *            :必须包含的名称
	 * @param mediaRoot
	 *            :所属根目录
	 * @param suffix
	 *            :匹配的后缀
	 * @return
	 */
	public String getDataItemValue(String dataItemName, String mediaRoot, String suffix) {
		String result = "";
		for (MediaDataInfo mInfo : meidaInfos) {
			if (mInfo.Path != null && mInfo.Path.endsWith(suffix) && mInfo.Title.contains(dataItemName)) {
				result += mInfo.Title + MediaDataInfo.Semicolon;
			}
		}
		return result;
	}

	/**
	 * 同一分类项下添加图片
	 * 
	 * @param value
	 *            子项值
	 * @param addItem
	 *            要添加的图片
	 * @return
	 */
	public String getDataItem(String value, String addItem) {
		String result = value + addItem + MediaDataInfo.Semicolon;
		return result;
	}

	/**
	 * 删除分类项下的图片
	 * 
	 * @param value
	 *            子项值
	 * @param deleteItem
	 *            要删除的图片
	 * @return
	 */
	public String removeDataItem(String value, String deleteItem) {
		String result = "";
		String[] items = value.split(MediaDataInfo.Semicolon);
		for (String item : items) {
			if (!item.contains(deleteItem)) {
				result += item + MediaDataInfo.Semicolon;
			}
		}
		return result;
	}

	/**
	 * 删除任务资源
	 * 
	 * @param source
	 *            :原图
	 * @return
	 */
	public Boolean deleteTaskResouce(File file) {
		Boolean result = false;
		try {
			if (file != null) {
				MediaDataInfo tempDataInfo = null;
				for (MediaDataInfo mInfo : meidaInfos) {
					if (mInfo.Path != null) {
						if (file.getName().equals(mInfo.file.getName())) {
							tempDataInfo = mInfo;
							break;
						}
					}
				}
				if (tempDataInfo != null) {
					meidaInfos.remove(tempDataInfo);
				}
				String tString = EIASApplication.getInstance().getString(R.string.thumbnail_dir);
				String pString = EIASApplication.getInstance().getString(R.string.project_dir);
				String tFullName = file.getAbsolutePath().replace(pString, tString);
				File fileThumbnail = new File(tFullName);
				if (fileThumbnail.exists()) {
					fileThumbnail.delete();
				}
				if (file.exists()) {
					file.delete();
				}
				result = true;
			}
		} catch (Exception e) {
			DataLogOperator.other("deleteTaskResouce=>" + e.getMessage());
		}
		return result;
	}

	/**
	 * 设置输入信息需要处理的事务
	 */
	public void setAutoCompleteData(AutoCompleteTextView txtAutoComplete, OnItemClickListener listener) {
		txtAutoComplete.setOnItemClickListener(listener);
		txtAutoComplete.setThreshold(1);
		txtAutoComplete.setAdapter(viewModel.searchAdapter);
		txtAutoComplete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AutoCompleteTextView txt = (AutoCompleteTextView) v;
				txt.showDropDown();
			}
		});
	}

	/**
	 * 记录补发图片
	 * 
	 * @param resourceId资源编号
	 * @param isRemove是否为移除
	 */
	public void additional(String resourceId, boolean isRemove) {
		if (additional) {
			TaskOperator.saveAdditional(viewModel.currentTask.TaskNum, resourceId, isRemove);
		}
	}

	// }}

	BaseBroadcastReceiver mainServerCreatedReceiver;

	/**
	 * 注册广播，响应拍照完成后
	 */
	public void receiverCameraBack() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(BroadRecordType.CAMERASERVER_SEND);
		temp.add(BroadRecordType.CAMERASERVER_BACK);
		mainServerCreatedReceiver = new BaseBroadcastReceiver(this, temp);
		mainServerCreatedReceiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String actionType = intent.getAction();
				switch (actionType) {
				case BroadRecordType.CAMERASERVER_SEND:
					Bundle bundle = intent.getExtras();
					String[] files = bundle.getStringArray("files");
					if (files.length > 0) {
						/*
						 * for (String item : files) { additional(item, false);
						 * }
						 */
						doSaveMediasInfo(files, CategoryType.PictureCollection, true);
					}
					break;
				case BroadRecordType.CAMERASERVER_BACK:
					// 按添加图片、未选择类型、选择类型 排序
					Collections.sort(meidaInfos, new Comparator<MediaDataInfo>() {
						@Override
						public int compare(MediaDataInfo data1, MediaDataInfo data2) {
							String value1 = data1.CategoryId + "";
							String value2 = data2.CategoryId + "";
							return value1.compareTo(value2);
						}
					});
					meidaListAdapter.notifyDataSetChanged();
				default:
					break;
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
			if (currentInstanceFragmentName.equals("TaskItemsFragment")) {
				toCategoriesFragment();
				return true;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 保存资源选择类型后的子项 增加新的资源子项
	 * 
	 * @param mType资源类型
	 * @param mInfo资源对象
	 * @param itemName当前选择的描述类型
	 * @param hasDefault是否有默认值
	 */
	public void saveTaskItemValue(CategoryType mType, MediaDataInfo mInfo, String itemName, boolean hasDefault) {
		// 记录当前Item的全部Value
		String currentValue = "";
		// 获取当前选择类型的全部Value
		String beforeItemValue = getBeforeItemValue(itemName);
		// 获取当前资源的Value
		String newItemValue = mInfo.itemFileName.trim();
		// 如果当前类型之前有值就拼接当前资源的Value 否则就只用当前资源的value
		if (beforeItemValue.length() > 0 && !beforeItemValue.contains(newItemValue)) {
			currentValue = beforeItemValue + newItemValue;
		} else {
			currentValue = newItemValue;
		}
		// 立刻更新内存中任务子项 如果是变跟选择类型
		if (mInfo.CategoryId > 0 && !mInfo.ItemName.equals(itemName)) {
			for (TaskDataItem item : viewModel.currentCategory.Items) {
				if (item.Name.equals(mInfo.ItemName)) {// 找到修改前的 把Value替换掉
					item.Value = item.Value.replace(newItemValue, "");
				} else if (item.Name.equals(itemName)) {
					item.Value = currentValue;
				}
			}
		} else {// 选择一个新的类型
			for (TaskDataItem item : viewModel.currentCategory.Items) {
				if (item.Name.equals(itemName) || item.Name.equals(newItemValue)) {
					item.CategoryID = viewModel.currentCategory.CategoryID;
					item.Name = itemName;
					item.Value = currentValue;
					break;
				}
			}
		}
		// 如果是补发图片
		additional(mInfo.file.getName(), false);
		ResultInfo<Boolean> saveResultInfo;
		if (!hasDefault) {
			// 保存当前操作的资源
			saveResultInfo = TaskOperator.saveMediaInfo(this, viewModel.currentTask, viewModel.currentCategory, itemName, currentValue, mInfo.ItemName, newItemValue, false);
		} else {
			// 有默认的值
			saveResultInfo = TaskOperator.saveMediaInfo(this, viewModel.currentTask, viewModel.currentCategory, itemName, currentValue, "", "", false);
		}

		// 如果保存成功就重新获取当前的分类数据
		if (saveResultInfo.Data != null && saveResultInfo.Data) {
			refreshTaskCategory();
		}

		// 同步相同类型Item的Value
		if (mInfo.CategoryId > 0 && !mInfo.ItemName.equals(itemName)) {
			for (MediaDataInfo item : meidaInfos) {
				if (mInfo.ItemName.equals(item.ItemName)) {
					item.ItemValue = item.ItemValue.replace(newItemValue, "");
				}
			}
		}
		// 修改内存中的当前Category下TaskDataItem中ItemVaule的值
		updateCategoryItemValue(itemName, currentValue);
		// 更新当前资源文件相同类型的ItemValue的值 方便删除时用
		updateMediaCurrentItem(mInfo, currentValue, itemName);
	}

	/**
	 * 更新当前资源文件相同类型的ItemValue的值 方便删除时用
	 * 
	 * @param mInfo
	 * @param currentValue
	 * @param itemName
	 */
	private void updateMediaCurrentItem(final MediaDataInfo mInfo, String currentValue, String itemName) {
		for (MediaDataInfo item : meidaInfos) {
			if (itemName.equals(item.ItemName)) {
				item.ItemValue = currentValue;
			}
		}
		mInfo.ItemName = itemName;
		mInfo.Title = itemName;
		mInfo.ItemValue = currentValue;
		mInfo.CategoryId = viewModel.currentCategory.CategoryID;
	}

	/**
	 * 修改内存中的当前Category下TaskDataItem中ItemVaule的值
	 * 
	 * @param itemName
	 * @param itemValue
	 */
	private void updateCategoryItemValue(String itemName, String itemValue) {
		for (TaskDataItem item : viewModel.currentCategory.Items) {
			if (item.Name.equals(itemName)) {
				item.Value = itemValue;
				break;
			}
		}
	}

	/**
	 * 获取当前子项修改前的值
	 * 
	 * @param itemName
	 * @return
	 */
	public String getBeforeItemValue(String itemName) {
		String result = "";
		for (TaskDataItem item : viewModel.currentCategory.Items) {
			if (item.Name.equals(itemName)) {
				result = item.Value.replaceAll("null", "");
				break;
			}
		}
		return result;
	}

	/**
	 * 删除指定的资源
	 * 
	 * @param mInfo
	 */
	public void removeTaskItemValue(MediaDataInfo mInfo) {
		try {
			String itemName = mInfo.ItemName;
			String itemValue = mInfo.ItemValue;
			String itemFileName = mInfo.itemFileName;
			itemValue = itemValue.replace(itemFileName, "");
			for (MediaDataInfo item : meidaInfos) {
				if (itemName.equals(item.ItemName)) {
					item.ItemValue = itemValue;
				}
			}
			additional(mInfo.file.getName(), true);
			doSaveMediaInfo(null, null, itemName, itemValue, CategoryType.PictureCollection, true, true);
			deleteTaskResouce(mInfo.file);
		} catch (Exception e) {
			DataLogOperator.other(viewModel.currentTask + "删除图片=>" + e.getMessage());
		}
	}

	/***
	 * 设置图片下拉框默认选项的值
	 * 
	 * @param file
	 *            要保存的图片File 对象
	 */
	protected void setDropDefaultSelect(MediaDataInfo mediaInfo, File file) {
		mediaInfo.itemFileName = file.getName() + ";";
		mediaInfo.ItemValue = mediaInfo.itemFileName + ";";
		mediaInfo.CategoryId = this.viewModel.currentCategory.CategoryID;
		//ShowMediaListFragment smf=(ShowMediaListFragment)this.getSupportFragmentManager().findFragmentByTag("");
		//String typeStr=smf.media_type_autoComplecTv.getText().toString();
		if (!TextUtils.isEmpty(this.viewModel.selectPicType)) {
			// 用户已选中有分类项
			mediaInfo.ItemName =viewModel.selectPicType;
			this.meidaInfos.add(mediaInfo);// 添加新子项到缓存
			doTaskSaveMediaDefaultInfo(mediaInfo);
		} else if (hasDefaultSelect()) {// 存在未分类选项，默认选中
			mediaInfo.ItemName = "未分类";
			this.meidaInfos.add(mediaInfo);// 添加新子项到缓存
			doTaskSaveMediaDefaultInfo(mediaInfo);
		} else {// 无默认分类项
			mediaInfo.ItemName = mediaInfo.itemFileName;
			mediaInfo.CategoryId = 0;
			this.meidaInfos.add(mediaInfo);
			this.additional(file.getName(), false);
			this.doSaveMediaInfo("", "", mediaInfo.ItemName, mediaInfo.ItemValue, CategoryType.PictureCollection, true, false);
		}

	}

	/** 发送文件保存通知 */
	private void doTaskSaveMediaDefaultInfo(MediaDataInfo mediaInfo) {
		Message mbgMsg = new Message();// 发消息通知后台线程保存图片信息
		mbgMsg.what = TASK_SAVE_MEDIA_DEFAULT_INFO;
		mbgMsg.obj = mediaInfo;
		this.mBackgroundHandler.sendMessage(mbgMsg);
	}

	/***
	 * 图片下拉框是否有默认选项 默认选项： "未分类"
	 * 
	 * @return
	 */
	protected boolean hasDefaultSelect() {
		boolean result = false;
		for (String dropItem : this.viewModel.currentDropDownListData) {
			if (dropItem.equals("未分类")) {
				result = true;
				break;
			}
		}
		return result;
	}

}
