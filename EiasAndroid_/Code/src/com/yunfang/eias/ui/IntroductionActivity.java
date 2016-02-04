package com.yunfang.eias.ui;

import java.util.ArrayList;

import com.yunfang.eias.R;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.logic.AppHeaderMenu;
import com.yunfang.eias.logic.IntroductionOperator;
import com.yunfang.eias.model.Introduction;
import com.yunfang.eias.ui.Adapter.IntroductionViewAdapter;
import com.yunfang.eias.viewmodel.LogListViewModel;
import com.yunfang.eias.enumObj.IntroductionTypeEnum;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.view.ComboBox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

/**
 * 功能介绍页面
 * @author 贺隽
 */
public class IntroductionActivity extends BaseWorkerActivity {

	//{{ 相关常量

	/**
	 * 获取日志
	 * */
	public final int TASK_GETDATA = 1;

	//}}

	// {{ 相关变量

	/**
	 * 下拉列表数据
	 */
	public String[] spinnerTypes = null;

	/**
	 * 日志ViewModel
	 * */
	public LogListViewModel viewModel = new LogListViewModel();

	/**
	 * 功能介绍列表
	 */
	public ArrayList<Introduction> introductions;

	//}}

	//{{ 相关控件

	/**
	 * 主菜单的广播
	 */
	private AppHeader appHeader;

	/**
	 * 对应的ListView
	 * */
	private ListView introduction_listview;

	/**
	 * 类型的下拉列表
	 */
	private ComboBox introduction_spinner_type;

	/**
	 * 返回
	 * */
	private View introduction_back;

	/**
	 * 数据适配
	 * */
	private IntroductionViewAdapter introductionViewAdapter = null;
	
	/**
	 * 自身的实例
	 */
	public static IntroductionActivity instance = null;   

	//}}

	//{{ 界面创建和释放

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.introduction);
		initView();
		initData();		
	}

	/**
	 * 释放资源
	 */
	@Override
	protected void onDestroy() {
		instance = null;
		AppHeaderMenu.openActivityName = "";
		appHeader.unRegisterReceiver();
		super.onDestroy();
	}
	//}}

	//{{ 初始化控件数据

	/**
	 * 初始化控件
	 */
	private void initView(){		
		findView();
		setListener();
		fillSpinnerType();
	}

	/**
	 * 找到控件
	 */
	private void findView(){
		AppHeaderMenu.openActivityName = IntroductionActivity.class.getSimpleName(); 
		instance = this;
		
		appHeader = new AppHeader(this, R.id.home_title);
		introduction_spinner_type = (ComboBox) findViewById(R.id.introduction_spinner_type);
		introduction_back = (View) findViewById(R.id.introduction_back);
		introduction_listview= (ListView) findViewById(R.id.introduction_listview);
		
		introduction_back.setVisibility(View.GONE);
		appHeader.visBackView(true);
		appHeader.visUserInfo(false);
		appHeader.visNetFlag(false);
		appHeader.setTitle("功能介绍");
	}

	/**
	 * 设置事件
	 */
	private void setListener() {
		introduction_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 声明消息接收对象
		ArrayList<String> temp = new ArrayList<String>();		
		temp.add(ComboBox.ACTION);
		BaseBroadcastReceiver onChangeReceiver = new BaseBroadcastReceiver(getApplicationContext(),temp);
		onChangeReceiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {			
			@Override
			public void onReceive(final Context context, Intent intent) {
				String actionType = intent.getAction();		
				switch(actionType){
				case ComboBox.ACTION:
					initData();
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 填充下拉列表里面的值
	 */
	private void fillSpinnerType() {
		spinnerTypes = new String[IntroductionTypeEnum.length()];
		for (int i = 0; i < IntroductionTypeEnum.length(); i++) {
			spinnerTypes[i] = IntroductionTypeEnum.getName(i);
		}
		introduction_spinner_type.setData(spinnerTypes);
		introduction_spinner_type.setPosition(0);		
		introduction_spinner_type.setEditTextEnabled(false);
	}
	//}}

	//{{ 数据处理

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
		case TASK_GETDATA:			
			//loadingWorker.showLoading("数据加载中...");
			uiMsg.obj = IntroductionOperator.getDatas(IntroductionTypeEnum.getEnumByName(introduction_spinner_type.getText()));
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
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch (msg.what) {
		case TASK_GETDATA:			
			showImageList(msg);
			break;
		default:
			break;
		}
		//loadingWorker.closeLoading();
		System.gc();
	}

	//}}

 	//{{ 后台方法处理触发
	/**
	 * 初始化数据
	 */
	private void initData(){
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_GETDATA;
		mBackgroundHandler.sendMessage(TaskMsg);
	}
	//}}

	//{{ 回调前台方法处理

	/**
	 * 显示图片列表信息
	 * @param msg
	 */
	@SuppressWarnings("unchecked")
	private void showImageList(Message msg) {
		ResultInfo<ArrayList<Introduction>> result = (ResultInfo<ArrayList<Introduction>>) msg.obj;
		introductionViewAdapter = new IntroductionViewAdapter(this, result.Data);
		introduction_listview.setAdapter(introductionViewAdapter);
		introductionViewAdapter.notifyDataSetChanged();
	}

	//}}
}
