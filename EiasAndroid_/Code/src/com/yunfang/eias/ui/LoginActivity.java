package com.yunfang.eias.ui;

import java.util.ArrayList;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.base.MainService;
import com.yunfang.eias.logic.DatadefinesOperator;
import com.yunfang.eias.logic.LoginInfoOperator;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.maps.BaiduLocationHelper;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.view.ComboBox;

public class LoginActivity extends BaseWorkerActivity {

	/** 位置定位类 */
	BaiduLocationHelper locationHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.login_activity);
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

	// {{ 任务类型

	/**
	 * 在线登录
	 */
	private final int TASK_ONLINE_LOGIN = 1;

	/**
	 * 离线登录
	 */
	private final int TASK_OFFLINE_LOGIN = 2;
	/***
	 * 获取最新勘察配置信息
	 */
	private final int TASK_ONLINE_GET_NEWEST_DATADEFINES = 3;

	// }}

	// {{ 界面控件值

	EditText txtUserAccount;
	EditText txtUserPwd;
	CheckBox cbIsAuto;
	CheckBox cbRemeberPwd;
	Button buttonLogin;
	Button buttonOffline;
	ComboBox comboboxServer;
	TextView txtAPPPageSize;

	// }}

	/**
	 * 初始化控件
	 */
	private void init() {
		txtUserPwd = (EditText) findViewById(R.id.et_pwd);
		txtUserAccount = (EditText) findViewById(R.id.et_useraccount);

		cbIsAuto = (CheckBox) findViewById(R.id.cb_auto_login);
		cbRemeberPwd = (CheckBox) findViewById(R.id.cb_remeber_pwd);

		buttonOffline = (Button) findViewById(R.id.btn_Offline_login);
		buttonOffline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtUserAccount.getText().toString().trim().length() > 0
						&& txtUserPwd.getText().toString().trim().length() > 0) {
					loadingWorker.showLoading("系统登录中...");
					Message loginMsg = new Message();
					loginMsg.what = TASK_OFFLINE_LOGIN;
					mBackgroundHandler.sendMessage(loginMsg);
				} else {
					showToast("用户名和密码不能为空");
				}
			}
		});

		buttonLogin = (Button) findViewById(R.id.btn_login);
		buttonLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtUserAccount.getText().toString().trim().length() > 0
						&& txtUserPwd.getText().toString().trim().length() > 0
						&& comboboxServer.getText().trim().length() > 0) {
					if (MainService.checkNetworkAvailable(EIASApplication
							.getInstance())) {
						login();
					} else {
						showToast("请检查网络链接是否正常!");
					}
				} else {
					showToast("用户名、密码和服务器地址不能为空");
				}
			}
		});

		comboboxServer = (ComboBox) findViewById(R.id.comboboxServer);

		ArrayList<String> urls = new ArrayList<String>();
		for (Entry<String, String> item : EIASApplication.Services.entrySet()) {
			urls.add(item.getKey());
		}

		UserInfo latestUser = LoginInfoOperator.GetLatestLoginInfo();
		if (latestUser.Token.trim().length() > 0) {
			txtUserAccount.setText(latestUser.Account);
			if (latestUser.IsRememberPwd) {
				txtUserPwd.setText(latestUser.Password);
			}
			cbIsAuto.setChecked(latestUser.IsAuto);
			cbRemeberPwd.setChecked(latestUser.IsRememberPwd);
			/*
			 * if (!urls.contains(latestUser.LatestServerName)) {
			 * urls.add(latestUser.LatestServerName); }
			 */
			comboboxServer.setData(urls.toArray(new String[0]));
			if (urls.contains(latestUser.LatestServerName)) {// 上次登录地址在地址列表显示历史地址
				comboboxServer.setText(latestUser.LatestServerName);
			}else{//否则取列表第一个
				comboboxServer.setText(urls.get(0));
			}

			if (latestUser.IsAuto
					&& txtUserAccount.getText().toString().trim().length() > 0
					&& txtUserPwd.getText().toString().trim().length() > 0
					&& comboboxServer.getText().trim().length() > 0) {
				login();
			}
		} else {
			comboboxServer.setData(urls.toArray(new String[0]));
		}

		txtAPPPageSize = (TextView) findViewById(R.id.app_PageSize);
		EIASApplication.PageSize = Integer.parseInt(txtAPPPageSize.getText()
				.toString().trim());
	}


	/**
	 * 用户登录
	 */
	private void login() {
		loadingWorker.showLoading("系统登录中...");
		Message loginMsg = new Message();
		loginMsg.what = TASK_ONLINE_LOGIN;
		mBackgroundHandler.sendMessage(loginMsg);
	}

	/***
	 * 获取最新配置表， 同步本地数据库配置信息
	 */
	@SuppressWarnings("unused")
	private void getNewesDatadefines() {
		// loadingWorker.showLoading("检查勘察配置信息中...");
		Message getDatadefinde = new Message();
		getDatadefinde.what = TASK_ONLINE_GET_NEWEST_DATADEFINES;
		mBackgroundHandler.sendMessage(getDatadefinde);
	}

	// {{ 进程调用重载类
	/**
	 * 
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		ResultInfo<UserInfo> result = new ResultInfo<UserInfo>();
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		case TASK_ONLINE_LOGIN:// 在线登录
			result = LoginInfoOperator.login(txtUserAccount.getText()
					.toString().trim(), txtUserPwd.getText().toString().trim(),
					comboboxServer.getText().trim(), cbIsAuto.isChecked(),
					cbRemeberPwd.isChecked());
			break;
		case TASK_OFFLINE_LOGIN:// 离线登录
			result = LoginInfoOperator.loginByOffline(txtUserAccount.getText()
					.toString().trim(), txtUserPwd.getText().toString().trim(),
					comboboxServer.getText().trim(), cbIsAuto.isChecked(),
					cbRemeberPwd.isChecked());
			break;
		case TASK_ONLINE_GET_NEWEST_DATADEFINES:// 获取最新勘察配置表
			DatadefinesOperator.getNewestDatadefine(LoginInfoOperator
					.getCurrentUser());
			break;
		default:
			result = new ResultInfo<UserInfo>(false);
			result.Data = null;
			result.Message = "没有找到任务执行的操作函数";
			break;
		}
		resultMsg.obj = result;
		mUiHandler.sendMessage(resultMsg);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		boolean doNotClose = false;
		switch (msg.what) {
		case TASK_ONLINE_LOGIN:// 在线登录
			doNotClose = afterLogined((ResultInfo<UserInfo>) msg.obj,
					TASK_ONLINE_LOGIN);
			break;
		case TASK_OFFLINE_LOGIN:// 离线登录
			afterLogined((ResultInfo<UserInfo>) msg.obj, TASK_OFFLINE_LOGIN);
			break;
		case TASK_ONLINE_GET_NEWEST_DATADEFINES:// 删除不存在勘察表数据完成了 执行跳转
			Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
			startActivity(intent);
			LoginActivity.this.finish();
			break;
		default:
			showToast("没有找到任务执行的操作函数");
			break;
		}
		if (!doNotClose) {
			loadingWorker.closeLoading();
		}

	}

	// }}

	/**
	 * 用户登录结果处理
	 * 
	 * @param result
	 *            :用户登录结果信息
	 * @param
	 */
	private boolean afterLogined(ResultInfo<UserInfo> result, int loginStatus) {
		boolean loginResult = false;
		if (result.Success) {
			if (result.Data != null && result.Data.Token != null
					&& result.Data.Token.length() > 0
					&& !result.Data.Token.equals("null")) {
				//getNewesDatadefines();
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				loginResult = true;
			} else {
				showToast(result.Message);
			}
		} else {
			if (result.Message.contains("Value Connection")) {
				showToast("登录失败,无法连接服务器");
			} else if (result.Message.contains("Value Connect")) {
				showToast("登录失败,连接服务器超时");
			} else if (result.Message.contains("lang")) {
				showToast("登录失败，服务器地址有误，请重新选择服务器");
			} else {
				showToast("登录失败，" + result.Message);
			}
		}
		return loginResult;
	}

	/**
	 * 监听键盘被按下的事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 若按下手机自带返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
