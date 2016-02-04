package com.yunfang.eias.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.logic.AppHeaderMenu;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.model.ResultInfo;

/**
 * 创建任务
 * @author gorson
 *
 */
public class CreateTaskActivity extends BaseWorkerActivity {

	//{{ 控件
	/**
	 * 地址值
	 */
	EditText txtAddress;

	/**
	 * 任务编号
	 */
	TextView txtTaskNum;

	/**
	 * 返回按钮
	 */
	Button returnBtn;

	/**
	 * 创建按钮
	 */
	Button createBtn;

	/**
	 * 重置新建按钮
	 */
	Button resetBtn;

	/**
	 * 勘察表信息
	 */
	ResultInfo<ArrayList<DataDefine>> ddInfoes;

	/**
	 * 下拉选项
	 */
	Spinner spinnerTable; 
	
	/**
	 * 任务创建容器
	 */
	LinearLayout taskLayout ;

	/**
	 * 是否为复制勘察信息到新建任务中
	 */
	boolean is_copied_to_new_task;

	/**
	 * 任务名称
	 */
	String taskName;

	/**
	 * 任务地址
	 */
	String taskAddress;

	/**
	 * 选择的勘察类型的编号
	 */
	ArrayList<String> keys;

	/**
	 * 选择的勘察类型的名称
	 */
	ArrayList<String> values;	
	
	/** 选择的媒体分类子项 */
	HashMap<String,String[]> selected_child_map;

	/**
	 * 复制的任务编号
	 */
	int copied_task_id = -1;

	/**
	 * 复制的任务是否为本地创建的任务
	 */
	boolean is_created_by_user = true;

	/**
	 * 主菜单的广播
	 */
	private AppHeader appHeader;
	
	/**
	 * 自身的实例
	 */
	public static CreateTaskActivity instance = null;   
	


	/**
	 * 在屏幕开始碰触的水平位置
	 * */
	public float touchStartX;

	/**
	 * 在屏幕开始碰触的垂直位置
	 * */
	public float touchStartY;

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
	public float TOUCH_DISTANCE =-1;
	//}}

	//{{ 基类重载方法

	/**
	 * 监听键盘被按下的事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// 若按下手机自带返回键
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// 执行列表刷新方法
			createNewTaskBreak();
			return true;
		}
		else
		{
			return super.onKeyDown(keyCode, event);
		}
	}
	//}}

	//{{ 任务类型
	/**
	 * 创建任务
	 */
	private final int TASK_CREATETASK = 0;

	//}}

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		Intent data = getIntent();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.new_task);		
		init();			
	}

	//{{ 进程调用重载类
	@Override
	protected void handleBackgroundMessage(Message msg) {		
		Message resultMsg=new Message();
		resultMsg.what = msg.what;
		switch(msg.what)
		{
		case TASK_CREATETASK:
			int selectedIndex = spinnerTable.getSelectedItemPosition();
			String address = txtAddress.getText().toString().trim();
			DataDefine selectedDataDefine = ddInfoes.Data.get(selectedIndex);
			ResultInfo<Long> createLocalInfo = TaskDataWorker.createLocalTask(
					true,txtTaskNum.getText().toString(),address,selectedDataDefine.DDID, 
					selectedDataDefine.Version,	EIASApplication.getCurrentUser());

			if (createLocalInfo.Success && createLocalInfo.Data > 0 && copied_task_id > 0) {
				if (keys.size() == values.size()) {
					int taskId = createLocalInfo.Data.intValue();
					TaskInfo copiedTaskInfo = TaskDataWorker.queryTaskInfo(copied_task_id, is_created_by_user).Data;
					TaskInfo pastedTaskInfo = TaskDataWorker.queryTaskInfo(taskId, true).Data;
					HashMap<String, String> selectedCategoryItems = new HashMap<String, String>();
					for (int i = 0; i < keys.size(); i++) {
						selectedCategoryItems.put(keys.get(i),values.get(i));
					}
					ResultInfo<Boolean> pastedInfo = TaskOperator.pastedTaskInfo(
							copiedTaskInfo, selectedCategoryItems,selected_child_map ,pastedTaskInfo,
							is_copied_to_new_task ? OperatorTypeEnum.TaskDataCopyToNew : 
								OperatorTypeEnum.TaskDataCopy);
					createLocalInfo.Success = pastedInfo.Success;
					createLocalInfo.Message = pastedInfo.Message;
					if(!(pastedInfo.Success && pastedInfo.Data)){
						TaskDataWorker.deleteCompleteTaskDataInfos(taskId, true);
					}
				}
			}
			resultMsg.obj = createLocalInfo;
			//如果是复制出来粘贴的
			if (copied_task_id != -1) {

				createNewTaskBreak();
			}			
			break;
		default:
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch(msg.what){
		case TASK_CREATETASK:
			ResultInfo<Long> createResult = (ResultInfo<Long>)msg.obj;
			if(createResult.Success && createResult.Data>0){
				Toast.makeText(this, "成功创建任务", Toast.LENGTH_LONG).show();	
				createdSuccess = true;
			}else{
				createBtn.setEnabled(true);
				Toast.makeText(this, "新建任务失败，请重试。", Toast.LENGTH_LONG).show();	
			}
			break;
		default:
			showToast("没有找到任务执行的操作函数");
			break;
		}
		loadingWorker.closeLoading();
	}
	//}}

	//{{ 按钮点击事件
	private OnClickListener btnClickLister = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_CancelCreateNewTask:				
				createNewTaskBreak();
				break;
			case R.id.btn_CreateNewTask:				
				createTask();
				break;
			case R.id.btn_ResetTask:
				resetTask();
				break;
			default:
				break;
			}
		}
	};
	//}}


	/**
	 * 初始化控件
	 */
	private void init(){
		instance = this;
		AppHeaderMenu.openActivityName = CreateTaskActivity.class.getSimpleName(); 
		appHeader = new AppHeader(this,R.id.home_title);
		txtAddress = (EditText) findViewById(R.id.new_task_et_address);
		txtTaskNum = (TextView) findViewById(R.id.new_task_et_pid);		
		txtTaskNum.setFocusable(false);           //无焦点  
		txtTaskNum.setFocusableInTouchMode(false);  //触摸时也得不到焦点  
		txtTaskNum.setText(TaskOperator.GenProjectID());

		returnBtn = (Button)findViewById(R.id.btn_CancelCreateNewTask);
		returnBtn.setOnClickListener(btnClickLister);

		createBtn = (Button)findViewById(R.id.btn_CreateNewTask);
		createBtn.setOnClickListener(btnClickLister);

		resetBtn = (Button)findViewById(R.id.btn_ResetTask);
		resetBtn.setOnClickListener(btnClickLister);
		resetBtn.setEnabled(false);
		taskLayout = (LinearLayout) findViewById(R.id.new_task_layout);
		taskLayout.setOnTouchListener(menuBodyOnTouchListener);
		initDataDefineList();
		
		appHeader.visBackView(true);
		appHeader.visUserInfo(false);
		appHeader.visNetFlag(false);
		appHeader.setTitle("新建任务");
	}

	/**
	 * 初始化勘察表列表
	 */
	private void initDataDefineList(){

		int selectedIndex = 0;
		ddInfoes = DataDefineWorker.queryDataDefineByCompanyID(EIASApplication.getCurrentUser().CompanyID);
		String[] names = null;
		String selectedDefineName="";
		if(ddInfoes.Success && ddInfoes.Data!= null && ddInfoes.Data.size()>0){
			if(selectedDefineName.length()==0){
				names = new String[ddInfoes.Data.size()];
				int index = 0;
				for(DataDefine item : ddInfoes.Data){
					names[index]=item.Name;						
					if(item.IsDefault){
						selectedIndex = index;
					}
					index += 1;
				}
			}
			spinnerTable = (Spinner) findViewById(R.id.new_task_spinner_table);
			spinnerTable.setVisibility(View.VISIBLE);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerTable.setAdapter(adapter);				
			spinnerTable.setSelection(selectedIndex,true);
			copyToNewTaskInfo(names);
		}
		else {
			showToast("请在首页同步勘察表信息之后再创建任务");//没有勘察表,不能创建任务
			this.finish();
		} 
	}

	/**
	 * 如果是复制到新建任务中
	 * @param names 所有的勘察名称
	 */
	@SuppressWarnings("unchecked")
	private void copyToNewTaskInfo(String[] names) {
		is_copied_to_new_task = getIntent().getBooleanExtra(
				"is_copied_to_new_task", false);
		if (is_copied_to_new_task) {
			taskName = getIntent().getStringExtra("name");
			taskAddress = getIntent().getStringExtra("address");
			keys = getIntent().getStringArrayListExtra("keys");
			values = getIntent().getStringArrayListExtra("values");
			copied_task_id = getIntent().getIntExtra("copied_task_id", -1);
			
			selected_child_map=(HashMap<String,String[]>)getIntent().getSerializableExtra("selected_child_map");
			is_created_by_user = getIntent().getBooleanExtra(
					"is_created_by_user", true);

			txtAddress.setText(taskAddress);

			Boolean temp =false;
			for (int i = 0; i < names.length; i++) {
				if (names[i].equals(taskName)) {
					spinnerTable.setSelection(i, true);
					spinnerTable.setEnabled(false);
					temp = true;
					break;
				}
			}
			spinnerTable.setEnabled(false);
			if (!temp)
			{
				showToast("找不到该任务对应的勘察表");
				this.finish();
			}
		}
	}

	/**
	 * 创建任务
	 */
	private void createTask(){
		int selectedIndex = spinnerTable.getSelectedItemPosition();
		String address = txtAddress.getText().toString().trim();
		if(address.length()>0 && selectedIndex>-1){	
			createBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			loadingWorker.showLoading("任务创建中...");
			Message msg = new Message();
			msg.what = TASK_CREATETASK;
			mBackgroundHandler.sendMessage(msg);
		}else{
			showToast("请填写所有的内容值");
		}
	}

	/**
	 * 任务重置新建
	 */
	private void resetTask(){
		txtAddress.setText("");
		txtTaskNum.setText(TaskOperator.GenProjectID());
		createBtn.setEnabled(true);
		resetBtn.setEnabled(false);
	}

	/**
	 * 释放资源
	 */
	@Override
	protected void onDestroy() {
		instance = null;
		AppHeaderMenu.openActivityName = ""; 
		super.onDestroy();
		appHeader.unRegisterReceiver();
	}

	/**
	 * 创建任务结束后
	 */
	private void createNewTaskBreak(){
		finish();
		if(createdSuccess){
			Intent myIntent = new Intent();//创建Intent对象  
			myIntent.setAction(String.valueOf(BroadRecordType.AFTER_CREATED_TASK));
			sendBroadcast(myIntent);
		}
	}

	/**
	 * 任务列表的触屏事件
	 */
	private OnTouchListener menuBodyOnTouchListener = new OnTouchListener() {
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return touchPage(event.getAction(), event.getX(), event.getY(),
					true);
		}
	};

	/**
	 * 滑动到指定的界面
	 * 
	 * @param action:操作事件类型
	 * @param x:x轴的坐标
	 * @param y:y轴的坐标
	 * @param defaultResult:默认显示的值
	 * @return
	 */
	public boolean touchPage(int action, float x, float y, boolean defaultResult) {
		boolean result = defaultResult;
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
			if ((moveY * 2 < moveX) && moveX > TOUCH_DISTANCE
					&& moveY > TOUCH_DISTANCE / 2) {
				// 往左滑动
				if (x < touchStartX) {
					//this.finish();
				}
				// 往右滑动
				else if (touchStartX < x) {
					this.finish();
				}
				result = true;
			}
			break;
		}
		return result;
	}

	//{{ 属性

	private boolean createdSuccess = false;
	//}}
}
