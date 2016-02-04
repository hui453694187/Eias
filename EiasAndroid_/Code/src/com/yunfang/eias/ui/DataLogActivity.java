package com.yunfang.eias.ui;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.logic.AppHeaderMenu;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.DataLog;
import com.yunfang.eias.ui.Adapter.DataLogListAdapter;
import com.yunfang.eias.viewmodel.LogListViewModel;
import com.yunfang.framework.base.BaseWorkerFragmentActivity;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.view.ComboBox;

/**
 * 
 * 项目名称：外业采集项目 类名称：DataLogActivity 类描述：用于显示日志的Activity 创建人：贺隽 创建时间：2014-6-18
 * 14:57:19
 * 
 * @version 1.0.0.1
 */
public class DataLogActivity extends BaseWorkerFragmentActivity implements
OnScrollListener {

	// {{ 相关变量

	/**
	 * 自身的实例
	 */
	public static DataLogActivity instance = null;  

	/**
	 * 主菜单的广播
	 */
	private AppHeader appHeader;

	/**
	 * 日志类型的下拉列表
	 */
	private ComboBox cboOperatorType;

	/**
	 * 数据适配
	 * */
	private DataLogListAdapter dataLogListAdapter = null;

	/**
	 * 下拉列表数据
	 */
	public String[] spinnerTypes = null;

	/**
	 * 日志ViewModel
	 * */
	public LogListViewModel viewModel = new LogListViewModel();

	/**
	 * 获取日志
	 * */
	public final int TASK_GETLOG = 1;

	/**
	 * 删除清空日志
	 * */
	public final int TASK_DELETELOG = 2;

	// }}

	// {{ 相关控件

	/**
	 * 日志对应的ListView
	 * */
	private ListView log_listview;

	/**
	 * 日志查询输入框
	 * */
	private EditText log_concent;

	/**
	 * 日志查询
	 * */
	private View log_search;

	/**
	 * 日志查询
	 * */
	private View log_back;

	/**
	 * 清空日志
	 */
	private Button log_delete;
	// }}

	// {{ 初始化

	/**
	 * 创建方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_listview);
		instance = this;
		AppHeaderMenu.openActivityName = DataLogActivity.class.getSimpleName(); 

		log_listview = (ListView) findViewById(R.id.log_listview);
		log_concent = (EditText) findViewById(R.id.log_concent);
		log_search = (View) findViewById(R.id.log_search);
		log_back = (View) findViewById(R.id.log_back);
		log_delete= (Button) findViewById(R.id.log_delete);
		cboOperatorType = (ComboBox) findViewById(R.id.log_spinner_type);
		appHeader = new AppHeader(this, R.id.home_title);
		
		log_back.setVisibility(View.GONE);
		appHeader.visBackView(true);
		appHeader.setTitle("日志信息");
		appHeader.visUserInfo(false);
		appHeader.visNetFlag(false);

		setListener();
		fillSpinnerType();
		loadData();
	}

	/**
	 * 填充下拉列表里面的值
	 */
	private void fillSpinnerType() {
		spinnerTypes = new String[OperatorTypeEnum.length() + 1];
		spinnerTypes[0] = EIASApplication.DefaultDropDownListValue;
		for (int i = 0; i < OperatorTypeEnum.length(); i++) {
			spinnerTypes[i + 1] = OperatorTypeEnum.getName(i);
		}
		cboOperatorType.setData(spinnerTypes);
		cboOperatorType.setPosition(0);		
		cboOperatorType.setEditTextEnabled(false); 
	}

	/**
	 * 设置事件
	 */
	private void setListener() {
		log_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewModel.currentIndex = 1;
				loadData();
			}
		});

		log_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		log_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogUtil.showConfirmationDialog(instance, "您确认要清空当前用户的日志吗？",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						loadingWorker.showLoading("清空日志中...");
						Message TaskMsg = new Message();
						TaskMsg.what = TASK_DELETELOG;
						mBackgroundHandler.sendMessage(TaskMsg);
					}
				});
			}
		});

		// 监听滚动加载数据
		log_listview.setOnScrollListener(new OnScrollListener() {

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {// 滑到顶部

				} else {

				}
				if (visibleItemCount + firstVisibleItem == totalItemCount) {// 滑到底部

				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 当屏幕停止滚动时
					viewModel.listItemCurrentPosition = view
					.getLastVisiblePosition();
					int vc = view.getCount();
					if (viewModel.listItemCurrentPosition == vc - 1) {
						int temp = 0;
						temp = (Integer) (viewModel.localTotal / EIASApplication.PageSize);
						temp += viewModel.localTotal % EIASApplication.PageSize > 0 ? 1 : 0;
		if (viewModel.currentIndex == temp) {
			showToast("已经是最后一页信息");
		} else {
			viewModel.currentIndex++;
			loadData();
		}
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 当屏幕滚动且用户使用的触碰或手指还在屏幕上时

					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 由于用户的操作，屏幕产生惯性滑动时
					if (view.getLastVisiblePosition() == view.getCount()) {
						loadingWorker.showLoading("数据加载中...");
					}
					break;
				}
			}
		});
	}

	// }}

	// {{ 后台方法

	/**
	 * 加载数据
	 * */
	public void loadData() {
		loadingWorker.showLoading("数据加载中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_GETLOG;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	// }}

	// {{ 处理后的方法

	/**
	 * 后台线程
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		viewModel.ToastMsg = "";
		// 准备发送给UI线程的消息对象
		Message uiMsg = new Message();
		uiMsg.what = msg.what;
		switch (msg.what) {
		case TASK_GETLOG:
			// 发送给UI线程的消息所绑定的对象
			if (viewModel.currentIndex == 1) {
				viewModel.logs = new ArrayList<DataLog>();
				viewModel.listItemCurrentPosition = 0;
				viewModel.localTotal = 0;
			}
			uiMsg.obj = DataLogOperator.getLogs(viewModel.currentIndex,
					viewModel.pageSize, log_concent.getText().toString(),
					OperatorTypeEnum.getValue(cboOperatorType.getText()));
			break;
		case TASK_DELETELOG:
			uiMsg.obj = DataLogOperator.deleteCurrentUserLog();
			break;
		default:
			break;
		}
		// 发信息给UI线程
		mUiHandler.sendMessage(uiMsg);
	}

	/**
	 * 回调到界面
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch (msg.what) {
		case TASK_GETLOG:
			ResultInfo<ArrayList<DataLog>> result = (ResultInfo<ArrayList<DataLog>>) msg.obj;
			viewModel.GetDataSuccess = result.Success;
			if (result.Success) {
				if (result.Data != null && result.Data.size() > 0) {
					viewModel.localTotal = (Integer) result.Others;
					viewModel.logs.addAll(result.Data);
					log_delete.setVisibility(View.VISIBLE);
				}else{
					log_delete.setVisibility(View.GONE);
				}
				
				viewModel.reload = true;
			} else {
				viewModel.GetDataSuccess = false;
				viewModel.currentIndex = viewModel.currentIndex > 1 ? viewModel.currentIndex - 1 : 1;
				viewModel.ToastMsg = "数据获取失败";
				viewModel.reload = false;
			}
			break;
		case TASK_DELETELOG:
			ResultInfo<Boolean> deleteResult = (ResultInfo<Boolean>) msg.obj;
			viewModel.reload = deleteResult.Data;
			loadData();
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
		loadingWorker.closeLoading();
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
			dataLogListAdapter = null;
		}
		if (dataLogListAdapter == null) {
			dataLogListAdapter = new DataLogListAdapter(this, viewModel.logs);
		}
		if (dataLogListAdapter != null) {
			if (viewModel.logs != null) {
				log_listview.setAdapter(dataLogListAdapter);
			} else {
				dataLogListAdapter.notifyDataSetChanged();
			}
			log_listview.setSelection(viewModel.listItemCurrentPosition);
		}
	}

	/**
	 * 释放资源
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppHeaderMenu.openActivityName = ""; 
		instance = null;
		appHeader.unRegisterReceiver();
	}

	// }}

	// {{ 滚动条的方法

	/**
	 * 滚动条滑动
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO 自动生成的方法存根

	}

	/**
	 * 滚动条状态变动
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO 自动生成的方法存根

	}

	// }}
}
