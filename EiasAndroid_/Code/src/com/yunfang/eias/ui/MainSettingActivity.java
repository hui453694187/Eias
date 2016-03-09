/**
 * 
 */
package com.yunfang.eias.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.dto.VersionDTO;
import com.yunfang.eias.http.task.CheckVersionTask;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.logic.LoginInfoOperator;
import com.yunfang.eias.viewmodel.MainSettingViewModel;
import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.ToastUtil;

/**
 * @author Administrator
 * 
 */
public class MainSettingActivity extends BaseWorkerActivity {

	// {{ 视图模型

	/**
	 * 视图模型
	 */
	MainSettingViewModel vm = new MainSettingViewModel();
	// }}

	// {{ 执行后台任务编号

	/**
	 * 检测版本
	 */
	private static final int TASK_CHECK_VERSION = 1;

	// }}

	// {{ 创建界面
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.main_settings);
		findView();
		setListener();
		setView();
	}

	/**
	 * 
	 */
	private void findView() {
		vm.header = new AppHeader(this, R.id.mainsetting_title);
		vm.mainsetting_about = (RelativeLayout) findViewById(R.id.mainsetting_about);
		vm.mainsetting_log = (RelativeLayout) findViewById(R.id.mainsetting_log);
		vm.mainsetting_read_eias = (RelativeLayout) findViewById(R.id.mainsetting_read_eias);
		vm.mainsetting_setting = (RelativeLayout) findViewById(R.id.mainsetting_setting);
		vm.mainsetting_update = (RelativeLayout) findViewById(R.id.mainsetting_update);
		vm.mainsetting_user_info = (RelativeLayout) findViewById(R.id.mainsetting_user_info);
		vm.mainsetting_logout = (RelativeLayout) findViewById(R.id.mainsetting_logout);
	}

	/**
	 * 
	 */
	private void setListener() {
		vm.mainsetting_about.setOnClickListener(listener);
		vm.mainsetting_log.setOnClickListener(listener);
		vm.mainsetting_read_eias.setOnClickListener(listener);
		vm.mainsetting_setting.setOnClickListener(listener);
		vm.mainsetting_update.setOnClickListener(listener);
		vm.mainsetting_user_info.setOnClickListener(listener);
		vm.mainsetting_logout.setOnClickListener(listener);
	}

	private void setView() {
		vm.header.setTitle("我");
		vm.header.visBackView(true);
		vm.header.visUserInfo(false);
		vm.header.visNetFlag(false);
		vm.mainsetting_read_eias.setVisibility(View.GONE);
	}

	// }}

	// {{ 后台处理

	/**
	 * 后台线程
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		super.handleUiMessage(msg);
		// 准备发送给UI线程的消息对象
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		case TASK_CHECK_VERSION:
			ResultInfo<VersionDTO> result =new ResultInfo<VersionDTO>();
			CheckVersionTask taskHttp = new CheckVersionTask();
			if (EIASApplication.IsOffline || !EIASApplication.IsNetworking) {// 没有网络
				result.Success=false;
				result.Message="请检查网络";
			} else {
				result = taskHttp
						.request(EIASApplication.getCurrentUser());
				if (result.Success && result.Data != null) {
					VersionDTO dto = (VersionDTO) result.Data;
					EIASApplication.version.ServerReleasedTime = dto.LastUpdateTime;
					EIASApplication.version.ServerVersionCode = dto.VersionCode;
					EIASApplication.version.ServerVersionDescription = dto.UpdateContent;
					EIASApplication.version.ServerVersionName = dto.VersionName;
				}
			}
			resultMsg.obj = result;
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/**
	 * 回调到界面
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		// 准备发送给UI线程的消息对象
		Message resultMsg = new Message();
		ResultInfo<Boolean> result = (ResultInfo<Boolean>) msg.obj;
		resultMsg.what = msg.what;
		switch (msg.what) {
		case TASK_CHECK_VERSION:
			if (result.Success) {
				vm.header.downloadTipsDialog("已经是最新版了");
			}
			break;
		}
		if (!result.Message.isEmpty()) {
			showToast(result.Message);
		}
		loadingWorker.closeLoading();
	}

	// }}

	// {{ 调用方法
	/**
	 * 
	 */
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.mainsetting_about:
				intent.setClass(MainSettingActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
			case R.id.mainsetting_log:
				intent.setClass(MainSettingActivity.this, DataLogActivity.class);
				startActivity(intent);
				break;
			case R.id.mainsetting_read_eias:
				intent.setClass(MainSettingActivity.this,
						IntroductionActivity.class);
				startActivity(intent);
				break;
			case R.id.mainsetting_setting:
				intent.setClass(MainSettingActivity.this,
						SystemSettingActivity.class);
				startActivity(intent);
				break;
			case R.id.mainsetting_update:
				onClickCheckversion();
				break;
			case R.id.mainsetting_logout:
				logout();
				break;
			case R.id.mainsetting_user_info:
				intent.setClass(MainSettingActivity.this,
						UserInfoActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 退出的对话框
	 */
	private void logout() {
		ResultInfo<Boolean> changeResult = LoginInfoOperator.logout();
		if (changeResult.Success && changeResult.Data) {
			startActivity(new Intent(this, LoginActivity.class));
			// 关闭所有Activity
			BaseApplication application = (BaseApplication) ((Activity) this)
					.getApplication();
			application.getActivityManager().finishAllActivity();
		} else {
			ToastUtil.longShow(this, changeResult.Message);
		}
	}

	/**
	 * 修改密码点击触发的消息发送事件
	 */
	private void onClickCheckversion() {
		loadingWorker.showLoading("检测中...");
		Message msg = new Message();
		msg.what = TASK_CHECK_VERSION;
		mBackgroundHandler.sendMessage(msg);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(vm!=null&&vm.header!=null){
			vm.header.unRegisterReceiver();
		}
		
	}
	// }}
}
