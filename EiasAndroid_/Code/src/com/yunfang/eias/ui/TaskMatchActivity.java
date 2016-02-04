package com.yunfang.eias.ui;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.http.task.GetTaskSearchListTask;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.ui.Adapter.TaskInfoInquiryAdapter;
import com.yunfang.eias.viewmodel.TaskMatchViewModel;
import com.yunfang.framework.base.BaseWorkerFragmentActivity;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.ListUtil;

/**
 * 根据小区名称/地址/任务编号等查询任务页面
 * 
 * @author 陈惠森
 * 
 */
public class TaskMatchActivity extends BaseWorkerFragmentActivity {
	// {{参数

	public TaskMatchViewModel viewModel = new TaskMatchViewModel();

	// }}

	// {{控件

	/**
	 * 系统头部控件
	 */
	private AppHeader appHeader;

	/**
	 * 显示列表
	 */
	private ListView listView;

	/**
	 * 任务适配器
	 */
	private TaskInfoInquiryAdapter adapter;

	/**
	 * 查询内容写入文本框
	 */
	private EditText txt_taskinfo_inquiry;

	/**
	 * 查询按钮
	 */
	private View inquiryView;

	/**
	 * 选中按钮
	 */
	private Button submitBtn;

	/**
	 * 上一页
	 */
	private Button previouspageBtn;

	/**
	 * 下一页
	 */
	private Button nextpageBtn;

	/**
	 * 返回
	 */
	private View view_break;

	/**
	 * 页码操作及提交控件装载容器
	 */
	private RelativeLayout layout_submitcontrols;

	/**
	 * 每页显示数量
	 */
	EditText txt_pageSize;

	// }}

	// {{ 任务类型
	/**
	 * 查询任务列表
	 */
	private final int TASK_QUERY = 0;

	/**
	 * 点击按钮 开始任务匹配
	 */
	private final int TASK_MATCHDATA = 2;

	/**
	 * 下一页
	 */
	private final int TASK_NEXTPAGE = 3;

	/**
	 * 上一页
	 */
	private final int TASK_PREVIOUSPAGE = 4;

	// }}

	// {{ 界面创建时

	// {{ 基类创建重载方法

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// }}

	/**
	 * 初始化控件
	 */
	private void init() {
		viewModel.taskNum = getIntent().getStringExtra("taskNum");
		viewModel.ddId = getIntent().getIntExtra("ddid", 0);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.task_info_inquiry);

		txt_taskinfo_inquiry = (EditText) findViewById(R.id.taskinfo_inquiry_et);
		inquiryView = (View) findViewById(R.id.view_taskinfo_inquiry_Inquiry);
		submitBtn = (Button) findViewById(R.id.btn_taskinfo_inquiry_submit);
		previouspageBtn = (Button) findViewById(R.id.btn_previouspage);
		nextpageBtn = (Button) findViewById(R.id.btn_nextpage);
		listView = (ListView) findViewById(R.id.taskinfo_listview);
		layout_submitcontrols = (RelativeLayout) findViewById(R.id.lay_submitcontrols);
		txt_pageSize = (EditText) findViewById(R.id.txt_pageSize);
		view_break = (View) findViewById(R.id.view_break);

		previouspageBtn.setOnClickListener(btnClickLister);
		nextpageBtn.setOnClickListener(btnClickLister);
		inquiryView.setOnClickListener(btnClickLister);
		submitBtn.setOnClickListener(btnClickLister);
		view_break.setOnClickListener(btnClickLister);

		txt_pageSize.setText(String.valueOf(viewModel.pageSize));
		txt_pageSize.setVisibility(View.GONE);
		layout_submitcontrols.setVisibility(View.GONE);

		appHeader = new AppHeader(this, R.id.home_title);
		appHeader.visBackView(true);
		appHeader.visUserInfo(false);
		appHeader.setTitle(viewModel.taskNum);
	}

	/**
	 * 取得当前每页数量
	 * 
	 * @return 是否成功
	 */
	private Boolean getPagesize() {
		Boolean result = true;
		// 取得页码
		if (txt_pageSize.getText().toString().trim() != "") {
			try {
				viewModel.pageSize = Integer.parseInt(txt_pageSize.getText().toString().trim());
			} catch (Exception e) {
				showToast("请输入正确页码!");
				result = false;
			}
		}
		return result;
	}

	/**
	 * 后台线程
	 * 
	 * @param msg
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		// 准备发送给UI线程的消息对象
		Message uiMsg = new Message();
		uiMsg.what = msg.what;
		switch (msg.what) {
		case TASK_QUERY:
			// 加载第一页数据并赋值
			viewModel.pageIndex = 1;
			// 取得页码
			if (!getPagesize()) {
				break;
			}
			ResultInfo<ArrayList<TaskInfo>> queryData = getTaskInfos();
			uiMsg.obj = queryData;
			viewModel.taskInfos = queryData.Data;
			break;
		// 下一页
		case TASK_NEXTPAGE:
			// 取得页码
			if (!getPagesize()) {
				break;
			}
			int temp = (Integer) (viewModel.total / viewModel.pageSize);
			temp += viewModel.total % viewModel.pageSize > 0 ? 1 : 0;
			if (viewModel.pageIndex != temp) {
				viewModel.pageIndex++;
				ResultInfo<ArrayList<TaskInfo>> nextData = getTaskInfos();
				uiMsg.obj = nextData;
				viewModel.taskInfos = nextData.Data;
			}
			break;
		// 上一页
		case TASK_PREVIOUSPAGE:
			// 取得页码
			if (!getPagesize()) {
				break;
			}
			if (viewModel.pageIndex > 1) {
				viewModel.pageIndex--;
				ResultInfo<ArrayList<TaskInfo>> preData = getTaskInfos();
				uiMsg.obj = preData;
				viewModel.taskInfos = preData.Data;
			}
			break;
		case TASK_MATCHDATA:
			ResultInfo<Boolean> result = new ResultInfo<Boolean>();
			viewModel.serverTaskInfo = adapter.selectTaskinfo;
			if (viewModel.serverTaskInfo != null) {
				viewModel.locTaskInfo = new TaskInfo();
				viewModel.locTaskInfo.TaskNum = viewModel.taskNum;
				result = TaskOperator.copyTaskInfoByServerData(viewModel.serverTaskInfo, viewModel.locTaskInfo);
			} else {
				result.Message = "请选择一个任务";
			}
			uiMsg.obj = result;
			break;
		}
		mUiHandler.sendMessage(uiMsg);
	}

	/**
	 * 前台线程
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleUiMessage(Message msg) {
		switch (msg.what) {
		// 查询任务列表UI事件
		case TASK_QUERY:
			try {
				ResultInfo<ArrayList<TaskInfo>> queryData = (ResultInfo<ArrayList<TaskInfo>>) msg.obj;
				if (viewModel.taskInfos.size() > 0) {
					// 显示页码操作及提交控件装载容器
					layout_submitcontrols.setVisibility(View.VISIBLE);
					previouspageBtn.setEnabled(false);
					previouspageBtn.setTextColor(Color.parseColor("#9A9A9A"));
				} else {
					// 隐藏页码操作及提交控件装载容器
					layout_submitcontrols.setVisibility(View.INVISIBLE);
					if (queryData != null) {
						showToast(queryData.Message);
					}
				}
				if ((Integer) (viewModel.total / viewModel.pageSize) < 2) {
					nextpageBtn.setEnabled(false);
					nextpageBtn.setTextColor(Color.parseColor("#9A9A9A"));
				} else {
					nextpageBtn.setEnabled(true);
					nextpageBtn.setTextColor(Color.parseColor("#000000"));
				}
				adapter = new TaskInfoInquiryAdapter(this);
				listView.setAdapter(adapter);
				adapter.refersh(viewModel.taskInfos);
			} catch (Exception e) {
				layout_submitcontrols.setVisibility(View.INVISIBLE);
				showToast("查询不到！");
			}
			break;
		// 下一页
		case TASK_NEXTPAGE:
			ResultInfo<ArrayList<TaskInfo>> nextData = (ResultInfo<ArrayList<TaskInfo>>) msg.obj;
			if (nextData != null) {
				int temp = (Integer) (viewModel.total / viewModel.pageSize);
				temp += viewModel.total % viewModel.pageSize > 0 ? 1 : 0;
				if (viewModel.pageIndex == temp) {
					nextpageBtn.setEnabled(false);
					nextpageBtn.setTextColor(Color.parseColor("#9A9A9A"));
				} else {
					previouspageBtn.setEnabled(true);
					previouspageBtn.setTextColor(Color.parseColor("#000000"));
				}
				refershUiTaskinfoData();
				if (nextData.Message != null && !nextData.Message.equals("null") && nextData.Message.length() > 0) {
					showToast(nextData.Message);
				}
			} else {
				showToast("没有找到数据");
			}
			break;
		// 上一页
		case TASK_PREVIOUSPAGE:
			ResultInfo<ArrayList<TaskInfo>> preData = (ResultInfo<ArrayList<TaskInfo>>) msg.obj;
			if (viewModel.pageIndex > 1) {
				nextpageBtn.setEnabled(true);
				nextpageBtn.setTextColor(Color.parseColor("#000000"));
			} else {
				previouspageBtn.setEnabled(false);
				previouspageBtn.setTextColor(Color.parseColor("#9A9A9A"));
			}
			refershUiTaskinfoData();
			if (preData.Message != null && !preData.Message.equals("null") && preData.Message.length() > 0) {
				showToast(preData.Message);
			}
			break;
		// 查询提交选中任务UI事件
		case TASK_MATCHDATA:
			ResultInfo<Boolean> result = (ResultInfo<Boolean>) msg.obj;
			showToast(result.Message);
			break;
		default:
			showToast("没有找到任务执行的操作函数");
			break;
		}
		loadingWorker.closeLoading();
	}

	/**
	 * 取得任务列表信息
	 * 
	 * @return
	 */
	private ResultInfo<ArrayList<TaskInfo>> getTaskInfos() {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		UserInfo user = EIASApplication.getCurrentUser();
		String searchTxt = txt_taskinfo_inquiry.getText().toString().trim();
		GetTaskSearchListTask task = new GetTaskSearchListTask();
		ResultInfo<ArrayList<TaskInfo>> resultTaskInfos = task.request(user, searchTxt, viewModel.ddId, viewModel.pageIndex, viewModel.pageSize);
		// 得到数据总条数
		if (resultTaskInfos != null && resultTaskInfos.Others != null) {
			viewModel.total = Integer.parseInt(String.valueOf(resultTaskInfos.Others));
		}
		result.Message = resultTaskInfos.Message;
		if (resultTaskInfos.Data != null) {
			result.Data = resultTaskInfos.Data;
			if (!ListUtil.hasData(result.Data)) {
				result.Message = "查询不到数据";
			}
		}

		return result;
	}

	/**
	 * 刷新界面数据
	 */
	private void refershUiTaskinfoData() {
		adapter = new TaskInfoInquiryAdapter(this);
		adapter.refersh(viewModel.taskInfos);
		listView.setAdapter(adapter);
	}

	// {{ 按钮点击事件
	private OnClickListener btnClickLister = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 查询事件
			case R.id.view_taskinfo_inquiry_Inquiry:
				taskInfoInquiry();
				break;
			// 选中事件
			case R.id.btn_taskinfo_inquiry_submit:
				taskInfoSubmit();
				break;
			// 上一页
			case R.id.btn_previouspage:
				previouspage();
				break;
			// 下一页
			case R.id.btn_nextpage:
				nextpage();
				break;
			case R.id.view_break:
				break;
			default:
				break;
			}
		}
	};

	// }}

	/**
	 * 查询任务列表
	 */
	private void taskInfoInquiry() {
		// 查询值
		String inquiryValue = txt_taskinfo_inquiry.getText().toString().trim();
		// 查询框中是否有值
		if (inquiryValue.length() > 0) {
			loadingWorker.showLoading("查询中...");
			Message msg = new Message();
			msg.what = TASK_QUERY;
			mBackgroundHandler.sendMessage(msg);
		} else {
			showToast("请填写查询内容");
		}
	}

	private void nextpage() {
		// 查询值
		String inquiryValue = txt_taskinfo_inquiry.getText().toString().trim();
		// 查询框中是否有值
		if (inquiryValue.length() > 0) {
			loadingWorker.showLoading("查询中...");
			Message msg = new Message();
			msg.what = TASK_NEXTPAGE;
			mBackgroundHandler.sendMessage(msg);
		} else {
			showToast("请填写查询内容");
		}
	}

	private void previouspage() {
		// 查询值
		String inquiryValue = txt_taskinfo_inquiry.getText().toString().trim();
		// 查询框中是否有值
		if (inquiryValue.length() > 0) {
			loadingWorker.showLoading("查询中...");
			Message msg = new Message();
			msg.what = TASK_PREVIOUSPAGE;
			mBackgroundHandler.sendMessage(msg);
		} else {
			showToast("请填写查询内容");
		}
	}

	/**
	 * 选中提交的任务
	 */
	private void taskInfoSubmit() {
		loadingWorker.showLoading("数据获取中...");
		Message msg = new Message();
		msg.what = TASK_MATCHDATA;
		mBackgroundHandler.sendMessage(msg);
	}
}
