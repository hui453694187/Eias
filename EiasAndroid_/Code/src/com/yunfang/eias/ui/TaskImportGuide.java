package com.yunfang.eias.ui;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.dto.DialogTipsDTO;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.logic.AppHeaderMenu;
import com.yunfang.eias.logic.TaskOperator;
import com.yunfang.eias.viewmodel.TaskImportGuideViewModel;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.model.ResultInfo;

/**
 * 导入任务
 * 
 * @author 贺隽
 * 
 */
public class TaskImportGuide extends BaseWorkerActivity {

	// {{ 变量声明

	/**
	 * 视图模型
	 */
	TaskImportGuideViewModel viewModel = new TaskImportGuideViewModel();

	// }}

	// {{ 任务编号

	/**
	 * 检查导入文件
	 */
	public final int TASK_IMPORT_CHECK = 0;

	// }}

	// {{ 创建

	/**
	 * 创建界面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.task_import_guide);
	
		init();
		receiver();
	}

	/**
	 * 释放资源
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppHeaderMenu.openActivityName = "";
		viewModel.appHeader.unRegisterReceiver();
	}

	/**
	 * 初始化控件
	 */
	private void init() {
		AppHeaderMenu.openActivityName = TaskImportGuide.class.getSimpleName();
		viewModel.appHeader = new AppHeader(this, R.id.home_title);

		Bundle bundle = getIntent().getExtras();
		viewModel.taskNum = bundle.getString("taskNum");
		viewModel.targetAddress = bundle.getString("targetAddress");
		viewModel.residentialArea = bundle.getString("residentialArea");
		viewModel.targetType = bundle.getString("targetType");
		viewModel.dataDefineName = bundle.getString("dataDefineName");

		viewModel.task_import_guide_num = (TextView) findViewById(R.id.task_import_guide_num);
		viewModel.task_import_guide_address = (TextView) findViewById(R.id.task_import_guide_address);
		viewModel.task_import_guide_name = (TextView) findViewById(R.id.task_import_guide_name);
		viewModel.task_import_guide_use = (TextView) findViewById(R.id.task_import_guide_use);
		viewModel.task_import_guide_ddid = (TextView) findViewById(R.id.task_import_guide_ddid);
		viewModel.task_import_guide_fullname = (TextView) findViewById(R.id.task_import_guide_fullname);
		viewModel.task_import_guide_select = (Button) findViewById(R.id.task_import_guide_select);
		viewModel.task_import_guide_import = (Button) findViewById(R.id.task_import_guide_import);

		viewModel.task_import_guide_num.setText(viewModel.taskNum);
		viewModel.task_import_guide_address.setText(viewModel.targetAddress);
		viewModel.task_import_guide_name.setText(viewModel.residentialArea);
		viewModel.task_import_guide_use.setText(viewModel.targetType);
		viewModel.task_import_guide_ddid.setText(viewModel.dataDefineName);

		viewModel.task_import_guide_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TaskImportGuide.this, OpenDialogResource.class);
				intent.putExtra("taskNum", viewModel.taskNum);
				startActivity(intent);
			}
		});

		viewModel.task_import_guide_import.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg = "";
				String fileFullName = viewModel.task_import_guide_fullname.getText().toString();
				String fileExt = getExtensionName(fileFullName);
				File file = new File(fileFullName);
				if (fileFullName.length() <= 0) {
					msg = "请选择要导入的文件";
				} else if (!file.exists()) {
					msg = "找不到文件";
				} else if (!fileExt.toLowerCase().equals("zip")) {
					msg = "文件必须是zip";
				} else if (!hasFreeSize(file)) {
					msg = "手机容量不够，请清理后再继续操作";
				}
				if (msg.length() > 0) {
					ArrayList<DialogTipsDTO> notices = new ArrayList<DialogTipsDTO>();
					notices.add(new DialogTipsDTO(msg));
					viewModel.appHeader.showDialogResult("提示信息", notices, false, null);
				} else {
					doWork("导入数据中。。。", TASK_IMPORT_CHECK, fileFullName);
				}
			}

			private String getExtensionName(String fileFullName) {
				if ((fileFullName != null) && (fileFullName.length() > 0)) {
					int dot = fileFullName.lastIndexOf('.');
					if ((dot > -1) && (dot < (fileFullName.length() - 1))) {
						return fileFullName.substring(dot + 1);
					}
				}
				return fileFullName;
			}

			@SuppressWarnings("deprecation")
			private boolean hasFreeSize(File file) {
				Boolean result = true;
				// 判断是否有插入存储卡
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					// 获取出厂内置SD卡 例如三星 和 小米
					File path = Environment.getExternalStorageDirectory();
					// 取得SDcard文件路径
					StatFs statfs = new StatFs(path.getPath());
					// 获取block的SIZE
					long blockSize = statfs.getBlockSize();
					// 己使用的Block的数量
					long freeBlock = statfs.getFreeBlocks();
					// 获取剩余容量小于指定大小时 提示
					String minSDCardSize = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_SDCARDSIZE);
					// 剩余容量是否小于指定大小
					if (freeBlock * blockSize / 1024 / 1024 < Integer.parseInt(minSDCardSize) // 单位是MB
							|| freeBlock * blockSize < file.length() * 2) {// 剩余空间是否可以解压
						result = false;
					}
				}
				return result;
			}

		});

		viewModel.appHeader.visBackView(true);
		viewModel.appHeader.visUserInfo(false);
		viewModel.appHeader.setTitle("任务数据导入");
	}

	// }}

	// {{ 进程调用重载类

	/**
	 * 用户登录
	 */
	private void doWork(String msg, int taskId, Object param) {
		if (msg.length() > 0) {
			loadingWorker.showLoading(msg);
		}
		Message loginMsg = new Message();
		loginMsg.what = taskId;
		loginMsg.obj = param;
		mBackgroundHandler.sendMessage(loginMsg);
	}

	/**
	 * 后台执行方法
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		case TASK_IMPORT_CHECK:
			resultMsg.obj = TaskOperator.taskImport(msg.obj.toString(), viewModel);
			break;
		default:
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/**
	 * 回调
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch (msg.what) {
		case TASK_IMPORT_CHECK:
			ResultInfo<ArrayList<DialogTipsDTO>> result = (ResultInfo<ArrayList<DialogTipsDTO>>) msg.obj;
			viewModel.appHeader.showDialogResult(result.Message, result.Data, false, null);
			break;
		default:
			break;
		}
		loadingWorker.closeLoading();
	}

	// }}

	// {{ 调用方法

	/**
	 * 响应选择文件之后
	 */
	public void receiver() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(BroadRecordType.TASK_IMPORT_FILE_SELECTED);
		BaseBroadcastReceiver receiver = new BaseBroadcastReceiver(getApplicationContext(), temp);
		receiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {
			@Override
			public void onReceive(final Context context, Intent intent) {
				String actionType = intent.getAction();
				switch (actionType) {
				case BroadRecordType.TASK_IMPORT_FILE_SELECTED:
					String fileFullName = intent.getStringExtra("fileFullName");
					viewModel.task_import_guide_fullname.setText(fileFullName);
					break;
				default:
					break;
				}
			}
		});
	}
	
	// }}
}
