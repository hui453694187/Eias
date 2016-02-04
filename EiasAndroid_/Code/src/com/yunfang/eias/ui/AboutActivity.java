/**
 * 
 */
package com.yunfang.eias.ui;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.base.BaseWorkerActivity;

/**
 * @author Administrator
 * 
 */
public class AboutActivity extends BaseWorkerActivity {

	// {{ 视图模型
	/**
	 * 
	 */
	public AppHeader header;
	// }}

	// {{ 执行后台任务编号

	// }}

	// {{ 创建界面
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_view_about);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		Button btn_about = (Button) findViewById(R.id.btn_about);
		TextView tv_version = (TextView) findViewById(R.id.dialog_tv_version);
		header = new AppHeader(this, R.id.home_title);
		tv_version.setText("当前版本 ： " + BaseApplication.getInstance().getPackageInfo().versionName);
		btn_about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		header.visBackView(true);
		header.visUserInfo(false);
		header.visNetFlag(false);
	}

	// }}

	// {{ 后台处理

	/*
	 * @see
	 * com.yunfang.framework.base.BaseWorkerActivity#handleBackgroundMessage
	 * (android.os.Message)
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {

	}

	// }}

	// {{ 调用方法

	// }}
}
