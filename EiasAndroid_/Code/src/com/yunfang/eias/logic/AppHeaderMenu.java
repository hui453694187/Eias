package com.yunfang.eias.logic;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.LoginInfoOperator;
import com.yunfang.eias.ui.CreateTaskActivity;
import com.yunfang.eias.ui.DataLogActivity;
import com.yunfang.eias.ui.IntroductionActivity;
import com.yunfang.eias.ui.LoginActivity;
import com.yunfang.eias.ui.MainSettingActivity;
import com.yunfang.eias.ui.SystemSettingActivity;
import com.yunfang.eias.ui.UserInfoActivity;
import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.ToastUtil;
import com.yunfang.framework.utils.WinDisplay;

/*
 * 主程序的菜单 包含离线和在线两种类型
 */
@SuppressLint("InflateParams")
public class AppHeaderMenu {
	// {{ 变量
	public static String openActivityName = "";
	
	/**
	 * 登录
	 */
	private Button pop_btn_login;

	/**
	 * 退出系统
	 */
	private Button pop_btn_quit;

	/**
	 * 新建任务
	 */
	private Button pop_btn_newtask;
	

	/**
	 * 日志记录
	 */
	private Button pop_btn_daily;
	
	/**
	 * 系统设置
	 */
	private Button pop_btn_setting;
	
	/**
	 * 功能介绍
	 */
	private Button pop_btn_Introduction;

	/**
	 * 关于系统
	 */
	private Button pop_btn_about;

	/**
	 * 更换用户
	 */
	private Button pop_btn_change_user;

	/**
	 * 人人信息
	 */
	private Button pop_btn_user_info;

	/**
	 * 离线
	 */
	private Button pop_btn_chang_offline;

	/**
	 * 弹出提示框
	 */
	public PopupWindow mPopupWindow;

	/**
	 * 显菜单视图
	 */
	private View mPopView;

	/*
	 * 关于的对话框
	 */
	private Dialog dialog_about;

	/*
	 * 版本号显示框
	 */
	private TextView tv_version;

	/*
	 * 退出程序对话框
	 */
	private Dialog dialog_quit;

	/**
	 * 离线勘察对话框
	 */
	private Dialog dialog_offline;

	/**
	 * 新建任务对话框
	 */
	//private Dialog dialog_createTask;

	/**
	 * 当前上下文
	 */
	private Context currentContext;

	// }}

	/**
	 * 设置程序的主菜单
	 * 
	 * @param context
	 *            :当前上下文
	 * 
	 * */
	public AppHeaderMenu(Context context) {
		currentContext = context;
		LayoutInflater inflater = LayoutInflater.from(currentContext);
		mPopView = inflater.inflate(R.layout.home_pop_layout_menu, null);
		mPopupWindow = new PopupWindow(mPopView,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setContentView(mPopView);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(-0000));
		mPopupWindow.setAnimationStyle(R.style.popupwindow_animation);

		ColorDrawable cd = new ColorDrawable(-0000);
		mPopupWindow.setBackgroundDrawable(cd);

		initButtons();
		setMenuItemsVisibility();
	}

	
	/**
	 * 填充按钮与点击事件
	 */
	private void initButtons() {
		pop_btn_newtask = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_newtask);
		pop_btn_daily = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_daily);
		pop_btn_about = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_about);
		pop_btn_setting = (Button)mPopView
				.findViewById(R.id.home_pop_menu_btn_setting);
		pop_btn_quit = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_quit);
		pop_btn_change_user = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_chang_user);
		pop_btn_user_info = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_user_info);
		pop_btn_chang_offline = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_chang_offline);
		pop_btn_login = (Button) mPopView
				.findViewById(R.id.home_pop_menu_btn_login);
		pop_btn_Introduction = (Button)mPopView
				.findViewById(R.id.home_pop_menu_btn_Introduction);

		pop_btn_newtask.setOnClickListener(menuItemClickLister);
		pop_btn_daily.setOnClickListener(menuItemClickLister);
		pop_btn_about.setOnClickListener(menuItemClickLister);
		pop_btn_setting.setOnClickListener(menuItemClickLister);
		pop_btn_quit.setOnClickListener(menuItemClickLister);
		pop_btn_change_user.setOnClickListener(menuItemClickLister);
		pop_btn_user_info.setOnClickListener(menuItemClickLister);
		pop_btn_chang_offline.setOnClickListener(menuItemClickLister);
		pop_btn_login.setOnClickListener(menuItemClickLister);
		pop_btn_Introduction.setOnClickListener(menuItemClickLister);
	}

	/**
	 * 根据当前用户的登录状态显示不同的菜单
	 */
	public void setMenuItemsVisibility() {
		// 在线状态
		if (!EIASApplication.IsOffline) {
			pop_btn_change_user.setVisibility(View.VISIBLE);
			pop_btn_user_info.setVisibility(View.VISIBLE);
			pop_btn_chang_offline.setVisibility(View.VISIBLE);

			pop_btn_login.setVisibility(View.GONE);
		} else {// 离线状态
			pop_btn_change_user.setVisibility(View.GONE);
			pop_btn_user_info.setVisibility(View.GONE);
			pop_btn_chang_offline.setVisibility(View.GONE);

			pop_btn_login.setVisibility(View.VISIBLE);
		}
		
		if(IntroductionActivity.instance != null && !openActivityName.equals(IntroductionActivity.class.getSimpleName())){
			IntroductionActivity.instance.finish();
			IntroductionActivity.instance = null;
		}
		else if(UserInfoActivity.instance != null && !openActivityName.equals(UserInfoActivity.class.getSimpleName())) {
			UserInfoActivity.instance.finish();		
			UserInfoActivity.instance = null;
		}
		else if(CreateTaskActivity.instance != null && !openActivityName.equals(CreateTaskActivity.class.getSimpleName())){
			CreateTaskActivity.instance.finish();
			CreateTaskActivity.instance = null;
		}
		else if(DataLogActivity.instance != null && !openActivityName.equals(DataLogActivity.class.getSimpleName())) {
			DataLogActivity.instance.finish();	
			DataLogActivity.instance = null;
		}

		//让已经打开的界面在菜单中隐藏掉
		if (UserInfoActivity.instance != null){
			pop_btn_user_info.setVisibility(View.GONE);
		} else {
			pop_btn_user_info.setVisibility(View.VISIBLE);
		}
		if(CreateTaskActivity.instance != null) {
			pop_btn_newtask.setVisibility(View.GONE);
		} else {
			pop_btn_newtask.setVisibility(View.VISIBLE);
		}		
		if (DataLogActivity.instance != null) {
			pop_btn_daily.setVisibility(View.GONE);
		} else {
			pop_btn_daily.setVisibility(View.VISIBLE);
		}
		if(IntroductionActivity.instance != null) {
			pop_btn_Introduction.setVisibility(View.GONE);
		} else {
			pop_btn_Introduction.setVisibility(View.VISIBLE);
		}		
		//菜单修改		
		pop_btn_daily.setVisibility(View.GONE);
		pop_btn_setting.setVisibility(View.GONE);		
		pop_btn_about.setVisibility(View.GONE);
		pop_btn_quit.setVisibility(View.GONE);
		pop_btn_change_user.setVisibility(View.GONE);
	}

	/**
	 * 菜单项的点击事件
	 */
	private OnClickListener menuItemClickLister = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.home_pop_menu_btn_login:
				showLoginActivity();
				break;
			case R.id.home_pop_menu_btn_user_info:
				showUserInfoActivity();
				break;
			case R.id.home_pop_menu_btn_newtask:
				showCreateTaskActivity();
				break;
			case R.id.home_pop_menu_btn_about:
				showAboutDialog();
				break;
			case R.id.home_pop_menu_btn_daily:
				mPopupWindow.dismiss();
				currentContext.startActivity(new Intent(currentContext,
						DataLogActivity.class));
				break;
			case R.id.home_pop_menu_btn_setting:
				showSettingDialog();
				break;
			case R.id.home_pop_menu_btn_quit:
				showQuitDialog();
				break;
			case R.id.home_pop_menu_btn_chang_user:
				ResultInfo<Boolean> changeResult = LoginInfoOperator.logout();
				if (changeResult.Success && changeResult.Data) {
					currentContext.startActivity(new Intent(currentContext,
							LoginActivity.class));
					//((Activity) currentContext).finish();
					//退出所有Activity
					BaseApplication application = (BaseApplication) ((Activity) currentContext).getApplication(); 
					application.getActivityManager().finishAllActivity(); 
				} else {
					ToastUtil.longShow(currentContext, changeResult.Message);
				}
				mPopupWindow.dismiss();
				break;
			case R.id.home_pop_menu_btn_chang_offline:
				showOfflineDialog();
				break;
			case R.id.home_pop_menu_btn_Introduction:
				showIntroductionDialog();
				break;
			/*case R.id.home_pop_menu_btn_stop_task:// 跳转到暂停任务列表状态
				showStopTaskList();
				break;*/
			default:
				break;
			}
		}
	};
	
	/**
	 * 
	 * @author kevin
	 * @date 2015-9-21 下午3:41:15
	 * @Description: 跳转到已暂停任务列表界面     
	 * @version V1.0
	 *//*
	private void showStopTaskList(){
		mPopupWindow.dismiss();对错地方了
		Intent i=new Intent();
		i.setClass(currentContext,StopTaskListActivity.class);
		currentContext.startActivity(i);
	}*/
	
	/**
	 * 显示功能介绍
	 */
	private void showIntroductionDialog(){
		mPopupWindow.dismiss();
		Intent intent = new Intent();    
		intent.setClass(currentContext,IntroductionActivity.class);
		currentContext.startActivity(intent);
	}
	
	/**
	 * 显示用户信息
	 */
	private void showUserInfoActivity(){
		mPopupWindow.dismiss();
		Intent intent = new Intent();    
		intent.setClass(currentContext,MainSettingActivity.class);
		currentContext.startActivity(intent);		
	}

	/**
	 * 显示系统设置信息
	 */
	private void showSettingDialog(){
		mPopupWindow.dismiss();
		Intent intent = new Intent();    
		intent.setClass(currentContext,SystemSettingActivity.class);
		currentContext.startActivity(intent);		
	}

	/**
	 * 显示创建任务的对话框
	 */
	private void showCreateTaskActivity() {
		mPopupWindow.dismiss();
		Intent intent = new Intent();    
		intent.setClass(currentContext,CreateTaskActivity.class);
		currentContext.startActivity(intent); 
	}

	/**
	 * 跳转到登录界面
	 */
	private void showLoginActivity() {
		currentContext.startActivity(new Intent(currentContext,
				LoginActivity.class));
		((Activity) currentContext).finish();
		mPopupWindow.dismiss();
	}

	/**
	 * 离线勘察对话框
	 */
	private void showOfflineDialog() {
		if (dialog_offline == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					currentContext);
			builder.setCancelable(true);
			builder.setTitle("确定要离线勘察吗 ?");
			builder.setIcon(R.drawable.quit);
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					EIASApplication.IsOffline = true;
					Intent intent = new Intent();
					intent.setAction(BroadRecordType.CHANGED_OFFLINE);			
					currentContext.sendBroadcast(intent);
					ToastUtil.longShow(currentContext, "已经切换成离线勘察状态");
				}
			});
			dialog_offline = builder.create();
		}
		if (dialog_offline.isShowing()) {
			dialog_offline.dismiss();
		} else {
			dialog_offline.show();
		}
		mPopupWindow.dismiss();
	}

	/**
	 * 退出的对话框
	 */
	private void showQuitDialog() {
		if (dialog_quit == null) {
			dialog_quit = DialogUtil.getQuitDialog(currentContext);
		}
		if (dialog_quit.isShowing()) {
			dialog_quit.dismiss();
		} else {
			dialog_quit.show();
		}
		mPopupWindow.dismiss();
	}
	
	/**
	 * 显示系统关于信息
	 */
	private void showAboutDialog() {
		if (dialog_about == null) {
			dialog_about = DialogUtil.commonDialog(currentContext,
					R.layout.dialog_view_about);
			WindowManager.LayoutParams params = dialog_about.getWindow()
					.getAttributes();
			Point point = WinDisplay.getWidthAndHeight(currentContext);
			switch (EIASApplication.PageSize) {
			case 6:// 手机
				params.width = (int) ((point.x) * (0.8));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				dialog_about.getWindow().setAttributes(params);
				break;
			case 15:// 平板
				params.width = (int) ((point.x) * (0.5));
				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
				dialog_about.getWindow().setAttributes(params);
				break;
			}
			Button btn_about = (Button) dialog_about
					.findViewById(R.id.btn_about);
			btn_about.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog_about.dismiss();
				}
			});
			tv_version = (TextView) dialog_about
					.findViewById(R.id.dialog_tv_version);
			tv_version
			.setText("当前版本 ： "
					+ BaseApplication.getInstance().getPackageInfo().versionName);
		}
		if (dialog_about.isShowing()) {
			dialog_about.dismiss();
		} else {
			dialog_about.show();
		}
		mPopupWindow.dismiss();
	}
	
	
}
