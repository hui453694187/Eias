package com.yunfang.eias.logic;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.base.MainService;
import com.yunfang.eias.dto.DialogTipsDTO;
import com.yunfang.eias.enumObj.LogType;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.http.task.BackgroundServiceTask;
import com.yunfang.eias.tables.DataLogWorker;
import com.yunfang.eias.ui.Adapter.DialogResultListAdapter;
import com.yunfang.eias.utils.OpenDialogHelper;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.enumObj.NetType;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.NetWorkUtil;
import com.yunfang.framework.utils.NotificationUtils;
import com.yunfang.framework.utils.SpUtil;
import com.yunfang.framework.utils.ToastUtil;
import com.yunfang.framework.utils.WinDisplay;

/**
 * 系统的主标题信息
 * 
 * @author 贺隽
 * 
 */

@SuppressLint("SimpleDateFormat")
public class AppHeader {

	// {{ 元素：按钮、属性

	LinearLayout headerView;

	/**
	 * WIFI 信号
	 */
	private Button home_btn_wifi;

	/**
	 * 2G 信号
	 */
	private Button home_btn_2g;

	/**
	 * 3G 信号
	 */
	private Button home_btn_3g;

	/**
	 * 4G 信号
	 */
	private Button home_btn_4g;

	/**
	 * 菜单按钮
	 */
	private TextView home_btn_info;

	/**
	 * 标题
	 */
	private TextView home_txt_title;

	/**
	 * 地图项
	 */
	private ImageView eias_app_header_map;

	/**
	 * 返回按钮
	 */
	private ImageButton main_header_btn_back;

	/**
	 * 返回容器
	 */
	private RelativeLayout main_header_lay_back;

	/** 更新提示对话框 */
	private Dialog dialog;

	/**
	 * 菜单项
	 */
	private AppHeaderMenu appHeaderMenu;

	/**
	 * 当前上下文
	 */
	private Context currentContext;

	/**
	 * 网络广播事件响应
	 */
	private BaseBroadcastReceiver broadcastReceiver;

	/**
	 * 消息广播
	 */
	public static final String TASK_AFTER_SUBMITED = "com.yunfang.eias.service.submited";

	/**
	 * 通知管理器
	 */
	NotificationManager notificationManager;

	/***
	 * 导入或者导入用到的结果弹出框
	 */
	public Dialog dialog_result;

	// }}

	/**
	 * 实现软件的头部控件的事件与功能
	 * 
	 * @param context
	 *            :当前上下文
	 * @param loadingWorker
	 *            :loading框
	 */
	public AppHeader(Context context, int viewID) {
		currentContext = context;
		headerView = (LinearLayout) ((Activity) context).findViewById(viewID);
		home_btn_wifi = (Button) headerView.findViewById(R.id.home_btn_wifi);
		home_btn_2g = (Button) headerView.findViewById(R.id.home_btn_2g);
		home_btn_3g = (Button) headerView.findViewById(R.id.home_btn_3g);
		home_btn_4g = (Button) headerView.findViewById(R.id.home_btn_4g);
		home_btn_info = (TextView) headerView.findViewById(R.id.home_btn_info);
		home_txt_title = (TextView) headerView
				.findViewById(R.id.home_txt_title);
		eias_app_header_map = (ImageView) headerView
				.findViewById(R.id.eias_app_header_map);
		main_header_btn_back = (ImageButton) headerView
				.findViewById(R.id.main_header_btn_back);
		main_header_lay_back = (RelativeLayout) headerView
				.findViewById(R.id.main_header_lay_back);
		home_btn_info.setText(EIASApplication.getCurrentUser().Name);

		appHeaderMenu = new AppHeaderMenu(currentContext);

		main_header_btn_back.setOnClickListener(back);
		home_btn_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				appHeaderMenu.mPopupWindow.showAsDropDown(home_btn_info);
			}
		});

		// home_txt_title.setOnClickListener(back);
		// 获取NotificationManager的引用
		String notificationService = Context.NOTIFICATION_SERVICE;
		// 初始化通知管理器
		notificationManager = (NotificationManager) context
				.getSystemService(notificationService);
		// 声明消息接收对象
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(String.valueOf(ConnectivityManager.CONNECTIVITY_ACTION));
		temp.add(String.valueOf(BroadRecordType.SUBMITED_RESULT_NOTIFICATION));
		broadcastReceiver = new BaseBroadcastReceiver(currentContext, temp);
		broadcastReceiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {
			@Override
			public void onReceive(final Context context, Intent intent) {
				String actionType = intent.getAction();
				switch (actionType) {
				case ConnectivityManager.CONNECTIVITY_ACTION:
					onAppHeaderReceive(context, intent);
					break;
				case BroadRecordType.SUBMITED_RESULT_NOTIFICATION: // 通知消息
					Integer notificationId = Integer.parseInt(intent
							.getStringExtra("notificationId"));
					String title = intent.getStringExtra("title");
					String contentTop = intent.getStringExtra("contentTop");
					String contentBottom = intent
							.getStringExtra("contentBottom");
					String tickerText = intent.getStringExtra("tickerText");
					NotificationUtils unils = new NotificationUtils();
					unils.showNotification(notificationId, title, contentTop,
							contentBottom, tickerText, true,
							Notification.FLAG_AUTO_CANCEL, null, null);
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 返回上一页面
	 */
	private OnClickListener back = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((Activity) currentContext).finish();
			((Activity) currentContext).overridePendingTransition(
					R.anim.slide_right_in, R.anim.slide_right_out);
		}
	};

	/**
	 * 信号发生改变时
	 * 
	 * @param context
	 *            :当前所在界面
	 * @param intent
	 *            :当前你想干的事
	 */
	private void onAppHeaderReceive(Context context, Intent intent) {
		NetType networkType = NetWorkUtil.getNetworkType();

		home_btn_2g.setVisibility(View.GONE);
		home_btn_3g.setVisibility(View.GONE);
		home_btn_4g.setVisibility(View.GONE);
		home_btn_wifi.setVisibility(View.GONE);
		Boolean beforeNetChangeStatus = EIASApplication.IsNetworking;
		EIASApplication.IsNetworking = true;
		switch (networkType) {
		case Type_2g: // 2g
			home_btn_2g.setVisibility(View.VISIBLE);
			break;
		case Type_3g: // 3g
			home_btn_3g.setVisibility(View.VISIBLE);
			break;
		case Type_4g: // 4g
			home_btn_4g.setVisibility(View.VISIBLE);
			break;
		case Type_wifi: // WIFI
			home_btn_wifi.setVisibility(View.VISIBLE);
			break;
		default:
			EIASApplication.IsNetworking = false;
			break;
		}
		// 检测是否有未完成上传的任务，并继续上传
		if (EIASApplication.IsNetworking && !beforeNetChangeStatus) {
			// 设置任务参数
			HashMap<String, Object> para = new HashMap<String, Object>();
			// 设置后台运行的任务
			BackgroundServiceTask task = new BackgroundServiceTask(
					MainService.RESTARTTASK_SUMIT, para);
			// 添加到任务池中
			MainService.setTask(task);
		}
	}

	/**
	 * 取消广播事件
	 */
	public void unRegisterReceiver() {
		broadcastReceiver.unregisterReceiver();
	}

	/**
	 * 刷新下拉列表
	 */
	public void setMenuItemsVisibility() {
		appHeaderMenu.setMenuItemsVisibility();
	}

	/**
	 * 显示/隐藏 返回
	 */
	public void setTitle(String title) {
		home_txt_title.setText(title);
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-10-20 下午12:55:22
	 * @Description: 标题的点击事件
	 * @param listener
	 */
	public void setTitleOnClickListener(OnClickListener listener) {
		home_txt_title.setOnClickListener(listener);
	}

	/**
	 * 显示/隐藏 返回
	 */
	public void visBackView(Boolean vis) {
		if (vis) {
			main_header_lay_back.setVisibility(View.VISIBLE);
		} else {
			main_header_lay_back.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示/隐藏 地图
	 */
	public void visMapView(Boolean vis) {
		if (vis) {
			eias_app_header_map.setVisibility(View.VISIBLE);
		} else {
			eias_app_header_map.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示/隐藏 用户
	 */
	public void visUserInfo(Boolean vis) {
		if (vis) {
			home_btn_info.setVisibility(View.VISIBLE);
		} else {
			home_btn_info.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏网络标识
	 * 
	 * @param vis
	 */
	public void visNetFlag(Boolean vis) {
		if (vis) {
			home_btn_2g.setVisibility(View.VISIBLE);
			home_btn_3g.setVisibility(View.VISIBLE);
			home_btn_4g.setVisibility(View.VISIBLE);
			home_btn_wifi.setVisibility(View.VISIBLE);
		} else {
			home_btn_2g.setVisibility(View.GONE);
			home_btn_3g.setVisibility(View.GONE);
			home_btn_4g.setVisibility(View.GONE);
			home_btn_wifi.setVisibility(View.GONE);
		}
	}

	// {{

	/**
	 * 提示是否需要更新
	 * 
	 * @param noLastVertsionTips
	 *            :不需要更新的提示信息
	 */
	public void downloadTipsDialog(String msg) {
		// 无论是否离线， 只要有网络，就提示更新信息
		if (EIASApplication.IsNetworking) {
			try {
				// 如果版本不一致就下载最新的
				// 检查服务端版本名是否为空，本地版本名于服务端版本是否一致， 不能为空， 不可以为null字符串
				boolean isNull = TextUtils
						.isEmpty(EIASApplication.version.ServerVersionName)
						|| EIASApplication.version.ServerVersionName
								.equals("null");
				// 本地版本名于服务端版本是否一致
				boolean different = !EIASApplication.version.LocalVersionName
						.equals(EIASApplication.version.ServerVersionName);
				if (!isNull && different) {
					showVersionUpdateDialog();
				} else {
					if (msg.length() > 0) {
						showDialog("反馈信息", msg);
					}
				}
			} catch (Exception e) {
				DataLogOperator.other("更新出错=>" + e.getMessage());
			}

		}
	}

	/***
	 * 刷新待提交列表 更新提示信息框
	 */
	public void updateTipsDialog(String msg) {
		//
		// 无论是否离线， 只要有网络，就提示更新信息
		if (EIASApplication.IsNetworking) {
			try {
				// 如果版本不一致就下载最新的
				// 检查服务端版本名是否为空，本地版本名于服务端版本是否一致， 不能为空， 不可以为null字符串
				boolean isNull = TextUtils
						.isEmpty(EIASApplication.version.ServerVersionName)
						|| EIASApplication.version.ServerVersionName
								.equals("null");
				// 本地版本名于服务端版本是否一致
				boolean different = !EIASApplication.version.LocalVersionName
						.equals(EIASApplication.version.ServerVersionName);
				if (!isNull && different) {
					OpenDialogHelper odh = new OpenDialogHelper(currentContext);
					String confirmTxt = "马上更新";
					String cancelTxt = "今天不再提示";
					String title = "确认(当前版本:"
							+ EIASApplication.version.LocalVersionName + ")";
					String msgTxt = "发现新版本["
							+ EIASApplication.version.ServerVersionName
							+ "]是否需要更新?";
					OnClickListener confirmL = new OnClickListener() {

						@Override
						public void onClick(View v) {
							Dialog dialog = (Dialog) v.getTag();
							try {
								// 下载更新
								downLoadUpdate();
							} catch (Exception e) {
								ToastUtil.longShow(currentContext, "下载最新版本失败");
							} finally {
								dialog.dismiss();
							}

						}
					};
					OnClickListener cancelL = new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 今天不再提示
							saveUpdateTips();
							Dialog dialog = (Dialog) v.getTag();
							dialog.dismiss();
						}
					};

					if (dialog == null) {
						dialog=odh.showCommDialog(confirmTxt, cancelTxt, title,
								msgTxt, confirmL, cancelL);
					}

					dialog.show();
				} else {
					if (msg.length() > 0) {
						showDialog("反馈信息", msg);
					}
				}
			} catch (Exception e) {
				DataLogOperator.other("更新出错=>" + e.getMessage());
			}

		}
	}

	/***
	 * 保存不提示日期
	 * 
	 * @param taskStatus
	 * @param sortType
	 */
	public void saveUpdateTips() {
		SpUtil sp = SpUtil.getInstance(EIASApplication.SORT_STATUS_SP);
		// 保存状态
		Date tady = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateTime = sdf.format(tady.getTime());
		sp.putString(EIASApplication.UPDATE_TIPS, dateTime);

	}
	
	/***
	 * 获取提示更新日期
	 * @return
	 */
	public boolean getUpdateTipsIsShow(){
		boolean show=true;
		SpUtil sp = SpUtil.getInstance(EIASApplication.SORT_STATUS_SP);
		String updateTime=sp.getString(EIASApplication.UPDATE_TIPS,"");
		Date tady = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateTime = sdf.format(tady.getTime());
		if(!updateTime.equals("")&&dateTime.equals(updateTime)){
			show=false;
		}
		return show;
	}

	/***
	 * 显示提示对话框
	 */
	private void showVersionUpdateDialog() {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					downLoadUpdate();
				} catch (Exception e) {
					ToastUtil.longShow(currentContext, "下载最新版本失败");
				}
			}
		};
		new AlertDialog.Builder(currentContext)
				.setTitle(
						"确认(当前版本:" + EIASApplication.version.LocalVersionName
								+ ")")
				.setMessage(
						"发现新版本[" + EIASApplication.version.ServerVersionName
								+ "]是否需要更新?").setPositiveButton("是", listener)
				.setNegativeButton("否", null).show();
	}

	/***
	 * 执行系统更新
	 */
	private void downLoadUpdate() throws Exception {
		// 设置任务参数
		HashMap<String, Object> para = new HashMap<String, Object>();
		// 设置后台运行的任务
		BackgroundServiceTask task = new BackgroundServiceTask(
				MainService.DOWN_LAST_VERSION, para);
		// 添加到任务池中
		MainService.setTask(task);
		UserInfo user = EIASApplication.getCurrentUser();
		// 用户点击更新， 记录日志，
		DataLogWorker.createDataLog(user, "用户[" + user.Name + "]点击了更新。",
				OperatorTypeEnum.VersionUpdate, LogType.UserOperation);
	}

	@SuppressWarnings("deprecation")
	public Boolean checkSDCardHasSize() {
		Boolean result = true;
		// 判断是否有插入存储卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 获取出厂内置SD卡 例如三星 和 小米
			File path = Environment.getExternalStorageDirectory();
			// 取得SDcard文件路径
			StatFs statfs = new StatFs(path.getPath());
			// 获取block的SIZE
			long blockSize = statfs.getBlockSize();
			// 获取BLOCK数量
			Double totalBlocks = (double) statfs.getBlockCount();
			// 己使用的Block的数量
			Double availaBlock = (double) statfs.getAvailableBlocks();
			// 剩余百分比
			int sdhasSize = (int) ((totalBlocks - availaBlock) / totalBlocks * 100);
			// 获取剩余容量小于指定大小时 提示
			String minSDCardSize = EIASApplication
					.getSystemSetting(BroadRecordType.KEY_SETTING_SDCARDSIZE);
			// 剩余容量是否小于指定大小
			if (availaBlock * blockSize / 1024 / 1024 < Integer
					.parseInt(minSDCardSize)) {
				final Dialog dialog_checksdcard = DialogUtil.commonDialog(
						currentContext, R.layout.dialog_view_task_sdcard);
				WindowManager.LayoutParams params = dialog_checksdcard
						.getWindow().getAttributes();
				Point point = WinDisplay.getWidthAndHeight(currentContext);
				switch (EIASApplication.PageSize) {
				case 6:// 手机
					params.width = (int) ((point.x) * (0.8));
					params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
					dialog_checksdcard.getWindow().setAttributes(params);
					break;
				case 15:// 平板
					params.width = (int) ((point.x) * (0.5));
					params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
					dialog_checksdcard.getWindow().setAttributes(params);
					break;
				}
				ProgressBar dialog_progressBar = (ProgressBar) dialog_checksdcard
						.findViewById(R.id.dialog_progressBar);
				dialog_progressBar.setMax(100);
				dialog_progressBar.setProgress(sdhasSize);
				Button dialog_check = (Button) dialog_checksdcard
						.findViewById(R.id.dialog_check);
				dialog_check.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog_checksdcard.dismiss();
					}
				});
				if (dialog_checksdcard.isShowing()) {
					dialog_checksdcard.dismiss();
				} else {
					dialog_checksdcard.show();
				}
				result = false;
			}
		}
		return result;
	}

	public void showDialog(String title, String msg) {
		if (activityIsFinishing()) {
			return;
		}
		final Dialog dialog_checksdcard = DialogUtil.commonDialog(
				currentContext, R.layout.dialog_view_info);
		WindowManager.LayoutParams params = dialog_checksdcard.getWindow()
				.getAttributes();
		Point point = WinDisplay.getWidthAndHeight(currentContext);
		switch (EIASApplication.PageSize) {
		case 6:// 手机
			params.width = (int) ((point.x) * (0.8));
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			dialog_checksdcard.getWindow().setAttributes(params);
			break;
		case 15:// 平板
			params.width = (int) ((point.x) * (0.5));
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			dialog_checksdcard.getWindow().setAttributes(params);
			break;
		}
		TextView dialog_view_info_concent = (TextView) dialog_checksdcard
				.findViewById(R.id.dialog_view_info_concent);
		TextView dialog_view_info_title = (TextView) dialog_checksdcard
				.findViewById(R.id.dialog_view_info_title);
		Button dialog_view_info_confirm = (Button) dialog_checksdcard
				.findViewById(R.id.dialog_view_info_confirm);

		dialog_view_info_title.setText(title);
		dialog_view_info_concent.setText(msg);

		dialog_view_info_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_checksdcard.dismiss();
			}
		});
		if (dialog_checksdcard.isShowing()) {
			dialog_checksdcard.dismiss();
		} else {
			dialog_checksdcard.show();
		}
	}

	public void showDialog(String title, String msg,
			OnClickListener confirmListener) {
		if (activityIsFinishing()) {
			return;
		}
		final Dialog dialog_checksdcard = DialogUtil.commonDialog(
				currentContext, R.layout.dialog_view_info);
		WindowManager.LayoutParams params = dialog_checksdcard.getWindow()
				.getAttributes();
		Point point = WinDisplay.getWidthAndHeight(currentContext);
		switch (EIASApplication.PageSize) {
		case 6:// 手机
			params.width = (int) ((point.x) * (0.8));
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			dialog_checksdcard.getWindow().setAttributes(params);
			break;
		case 15:// 平板
			params.width = (int) ((point.x) * (0.5));
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			dialog_checksdcard.getWindow().setAttributes(params);
			break;
		}
		TextView dialog_view_info_concent = (TextView) dialog_checksdcard
				.findViewById(R.id.dialog_view_info_concent);
		TextView dialog_view_info_title = (TextView) dialog_checksdcard
				.findViewById(R.id.dialog_view_info_title);
		Button dialog_view_info_confirm = (Button) dialog_checksdcard
				.findViewById(R.id.dialog_view_info_confirm);

		dialog_view_info_title.setText(title);
		dialog_view_info_concent.setText(msg);

		dialog_view_info_confirm.setOnClickListener(confirmListener);

		if (dialog_checksdcard.isShowing()) {
			dialog_checksdcard.dismiss();
		} else {
			dialog_checksdcard.show();
		}
	}

	// }}

	// {{ 错误提示结果

	/**
	 * 数据检查，数据导入 的提示 结果
	 * 
	 * @param title
	 * @param concent
	 * @param checksucess
	 * @param click
	 */
	public void showDialogResult(String title,
			ArrayList<DialogTipsDTO> notices, boolean visConfirm,
			OnClickListener confirmClick) {
		if (activityIsFinishing()) {
			return;
		}
		dialog_result = DialogUtil.commonDialog(currentContext,
				R.layout.dialog_view_task_result);
		WindowManager.LayoutParams params = dialog_result.getWindow()
				.getAttributes();
		Point point = WinDisplay.getWidthAndHeight(currentContext);
		switch (EIASApplication.PageSize) {
		case 6:// 手机
			params.width = (int) ((point.x) * (0.8));
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			dialog_result.getWindow().setAttributes(params);
			break;
		case 15:// 平板
			params.width = (int) ((point.x) * (0.5));
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			dialog_result.getWindow().setAttributes(params);
			break;
		}
		TextView dialog_title = (TextView) dialog_result
				.findViewById(R.id.dialog_view_task_result_title);
		ListView dialog_list = (ListView) dialog_result
				.findViewById(R.id.dialog_view_task_result_list);
		Button dialog_confirm = (Button) dialog_result
				.findViewById(R.id.dialog_view_task_result_confirm);
		Button dialog_cancel = (Button) dialog_result
				.findViewById(R.id.dialog_view_task_result_cancel);

		dialog_title.setText(title);
		DialogResultListAdapter mAdapter = new DialogResultListAdapter(
				currentContext, notices);
		dialog_list.setAdapter(mAdapter);

		if (visConfirm) {
			dialog_cancel.setText("不导出");
			dialog_cancel.setVisibility(View.VISIBLE);
			dialog_confirm.setVisibility(View.VISIBLE);
			if (confirmClick != null) {
				dialog_confirm.setOnClickListener(confirmClick);
			}
		} else {
			dialog_cancel.setText("知道了");
			dialog_cancel.setVisibility(View.VISIBLE);
		}
		dialog_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_result.dismiss();
			}
		});
		if (dialog_result.isShowing()) {
			dialog_result.dismiss();
		} else {
			dialog_result.show();
		}
	}

	/***
	 * @author kevin
	 * @date 2015-11-4 下午5:42:02
	 * @Description: 判断父容器，是否存在。
	 * @return boolean 返回类型
	 */
	private boolean activityIsFinishing() {
		boolean unFinishing = true;
		try {
			if (currentContext != null) {
				// 判断当前活动是否退出了。
				unFinishing = ((Activity) currentContext).isFinishing();
			} else {
				unFinishing = true;
			}
		} catch (Exception e) {
			unFinishing = true;
		}
		return unFinishing;
	}
	// }}
}
