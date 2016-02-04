/**
 * 
 */
package com.yunfang.eias.ui;

import android.os.Bundle;
import android.os.Message;
import android.widget.ListView;

import com.yunfang.eias.R;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.framework.base.BaseWorkerActivity;

/**
 * 
 * @Description: 展示已经暂停的任务
 * @author kevin
 * @date 2015-9-21 下午3:19:50
 */
public class StopTaskListActivity extends BaseWorkerActivity {

	/** 顶部导航 */
	private AppHeader appHeader;
	
	@SuppressWarnings("unused")
	private ListView stopTaskLv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_stop_task_list);
		initView();
	}

	/** 
	 * @author kevin
	 * @date 2015-9-21 下午3:36:53
	 * @Description: 初始化控件    
	 * @version V1.0
	 */
	private void initView() {
		appHeader = new AppHeader(this,R.id.home_title);
		appHeader.visBackView(true);
		appHeader.visUserInfo(false);
		appHeader.visNetFlag(false);
		appHeader.setTitle("已暂停任务");
		
		stopTaskLv=(ListView)this.findViewById(R.id.stop_task_lv);
		
	}

	@Override
	protected void handleBackgroundMessage(Message msg) {
		
	}
	
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
	}
	
}
