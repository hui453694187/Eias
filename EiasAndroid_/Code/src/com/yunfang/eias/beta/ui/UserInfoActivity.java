/**
 * 
 */
package com.yunfang.eias.beta.ui;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.dto.VersionDTO;
import com.yunfang.eias.beta.http.task.CheckVersionTask;
import com.yunfang.eias.beta.http.task.UploadUserInfoTask;
import com.yunfang.eias.beta.logic.AppHeader;
import com.yunfang.eias.beta.logic.AppHeaderMenu;
import com.yunfang.eias.beta.logic.LoginInfoOperator;
import com.yunfang.eias.beta.logic.UserInfoOperator;
import com.yunfang.eias.beta.viewmodel.UserInfoViewModel;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.dto.UserInfoDTO;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.CameraUtils;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.view.RoundImage;

/**
 * @author chs 查看用户信息、修改密码和获取最新版本信息
 */
public class UserInfoActivity extends BaseWorkerActivity {

	// {{ 控件
	/**
	 * 用户图标
	 */
	private RoundImage userlogo;

	/**
	 * 用户名称
	 */
	private TextView username;

	/**
	 * 用户公司
	 */
	private TextView usercompany;

	/**
	 * 密码操作提示
	 */
	private TextView error_validate;

	/**
	 * 密码操作提示容器
	 */
	private LinearLayout formvalidate;

	/**
	 * 修改密码列表
	 */
	private LinearLayout user_list;

	/**
	 * 用户旧密码
	 */
	private EditText et_old_userpassword;

	/**
	 * 用户新密码
	 */
	private EditText et_new_userpassword;

	/**
	 * 用户重复新密码
	 */
	private EditText et_agin_userpassword;

	/**
	 * 密码修改
	 */
	private Button changesubmit;

	/**
	 * 密码修改
	 */
	private Button checkversion;

	/**
	 * 当前版本
	 */
	private TextView current_version;

	/**
	 * 主菜单的广播
	 */
	private AppHeader appHeader;

	/**
	 * 布局
	 */
	private RelativeLayout usrInfoLayout;
	// }}

	// {{ 变量
	/**
	 * 选项名称
	 */
	private String[] items = new String[] { "选择本地图片", "拍照" };

	/**
	 * 本地图片请求代码
	 */
	private static final int TASK_IMAGE_REQUEST_CODE = 0;

	/**
	 * 拍照请求代码
	 */
	private static final int TASK_CAMERA_REQUEST_CODE = 1;

	/**
	 * 取消请求代码
	 */
	private static final int TASK_RESULT_REQUEST_CODE = 2;

	/**
	 * 修改用户密码
	 */
	private static final int TASK_CHANGE_PASSWORD = 3;

	/**
	 * 修改用户头像
	 */
	private static final int TASK_CHANGE_USER_IMAGE = 4;

	/**
	 * 检测版本
	 */
	private static final int TASK_CHECK_VERSION = 5;

	/**
	 * 头像文件名称
	 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/**
	 * 头像临时文件
	 */
	private static final String IMAGE_FILE_TEMPNAME = "tempImage.jpg";

	/**
	 * 用户信息viewModel
	 */
	public UserInfoViewModel viewModel = new UserInfoViewModel();

	/**
	 * 自身的实例
	 */
	public static UserInfoActivity instance = null;

	// }}

	// {{ 处理后的方法

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
		case TASK_CHANGE_PASSWORD:
			String submitString = changesubmit.getText().toString();
			String oldPassword = et_old_userpassword.getText().toString();
			String newPassword = et_new_userpassword.getText().toString();
			String againPassword = et_agin_userpassword.getText().toString();
			resultMsg.obj = UserInfoOperator.changePassword(submitString, oldPassword, newPassword, againPassword);
			break;
		case TASK_CHANGE_USER_IMAGE:
			// 执行用户信息上传到服务器的task
			UserInfo user = EIASApplication.getCurrentUser();
			UploadUserInfoTask task = new UploadUserInfoTask();
			UserInfoDTO userDto = (UserInfoDTO) msg.obj;
			ResultInfo<Boolean> taskResult = task.request(user, userDto);
			if (taskResult.Data != null && taskResult.Data) {
				UserInfo userInfo = EIASApplication.getCurrentUser();
				userInfo.userVersion = userDto.UserVersion;
				LoginInfoOperator.saveTOLoaclUserInfo(userInfo);
			}
			resultMsg.obj = taskResult;
			break;
		case TASK_CHECK_VERSION:
			CheckVersionTask taskHttp = new CheckVersionTask();
			ResultInfo<VersionDTO> result = taskHttp.request(EIASApplication.getCurrentUser());
			if (result.Success && result.Data != null) {
				UserInfoOperator.setVersionInfo(result.Data);
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
		case TASK_CHANGE_PASSWORD:
			if (changesubmit.getText().equals("修改密码")) {
				user_list.setVisibility(View.VISIBLE);
				changesubmit.setText("保存密码");
			} else {
				error_validate.setTextColor(Color.RED);
				if (result.Success && result.Message != null && result.Message.length() > 0) {
					formvalidate.setVisibility(View.VISIBLE);
					error_validate.setText(result.Message);
				} else {
					formvalidate.setVisibility(View.GONE);
					error_validate.setText("");
				}
				// 修改成功则清空密码框
				resetPasswordText(result.Data);
			}
			break;
		case TASK_CHANGE_USER_IMAGE:
			if (result.Data) {
				userlogo.setImageBitmap(CameraUtils.getBitmap(viewModel.userDirectory + IMAGE_FILE_NAME));
			}
			break;
		case TASK_CHECK_VERSION:
			if (result.Success) {
				appHeader.downloadTipsDialog("已经是最新版了");
			}
			break;
		}
		if (!result.Message.isEmpty()) {
			showToast(result.Message);
		}
		loadingWorker.closeLoading();
	}

	/**
	 * 清空密码输入框
	 */
	private void resetPasswordText(Boolean changeSuccess) {
		if (changeSuccess) {
			et_old_userpassword.setText("");
			// error_validate.setTextColor(Color.parseColor("#5b90c6"));
			error_validate.setTextColor(this.getResources().getColor(R.color.lanse));
		}
		et_new_userpassword.setText("");
		et_agin_userpassword.setText("");
	}

	// }}日志外

	// {{ 初始化

	/**
	 * 创建执行方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.user_info);
		init();
		initUserInfo();
	}

	/**
	 * 初始化控件
	 */
	private void init() {
		instance = this;
		AppHeaderMenu.openActivityName = UserInfoActivity.class.getSimpleName();

		appHeader = new AppHeader(this, R.id.home_title);
		userlogo = (RoundImage) findViewById(R.id.userlogo);
		username = (TextView) findViewById(R.id.username);
		usercompany = (TextView) findViewById(R.id.usercompany);
		user_list = (LinearLayout) findViewById(R.id.user_list);
		et_old_userpassword = (EditText) findViewById(R.id.et_old_userpassword);
		et_new_userpassword = (EditText) findViewById(R.id.et_new_userpassword);
		et_agin_userpassword = (EditText) findViewById(R.id.et_agin_userpassword);
		changesubmit = (Button) findViewById(R.id.changesubmit);
		checkversion = (Button) findViewById(R.id.checkversion);
		current_version = (TextView) findViewById(R.id.current_version);
		error_validate = (TextView) findViewById(R.id.error_validate);
		formvalidate = (LinearLayout) findViewById(R.id.formvalidate);
		changesubmit.setOnClickListener(btnClickLister);
		checkversion.setOnClickListener(btnClickLister);
		if (EIASApplication.IsOffline) {
			changesubmit.setVisibility(View.GONE);
			//checkversion.setVisibility(View.GONE);
			userlogo.setOnClickListener(null);
		} else {
			changesubmit.setVisibility(View.VISIBLE);
			//checkversion.setVisibility(View.VISIBLE);
			userlogo.setOnClickListener(btnClickLister);
		}
		viewModel.TOUCH_DISTANCE = EIASApplication.getTouch_DISTANCE();
		usrInfoLayout = (RelativeLayout) findViewById(R.id.userInfo_layout);
		usrInfoLayout.setOnTouchListener(menuBodyOnTouchListener);

		appHeader.visBackView(true);
		appHeader.visUserInfo(false);
		appHeader.visNetFlag(false);
		appHeader.setTitle("个人信息");
	}

	/**
	 * 初始化用户信息
	 */
	private void initUserInfo() {
		viewModel.userInfo = EIASApplication.getCurrentUser();
		username.setText(viewModel.userInfo.Name);
		usercompany.setText(viewModel.userInfo.CompanyName);
		current_version.setText(EIASApplication.versionInfo.LocalVersionName);
		viewModel.userDirectory = EIASApplication.userRoot + EIASApplication.getCurrentUser().Account + File.separator;
		FileUtil.mkDir(viewModel.userDirectory);
		File file = new File(viewModel.userDirectory + EIASApplication.USER_IMAGE_NAME);
		if (file.exists()) {
			userlogo.setImageBitmap(CameraUtils.getBitmap(file.getAbsolutePath()));
		}
	}

	/**
	 * 任务列表的触屏事件
	 */
	private OnTouchListener menuBodyOnTouchListener = new OnTouchListener() {
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return touchPage(event.getAction(), event.getX(), event.getY(), true);
		}
	};

	/**
	 * 滑动到指定的界面
	 * 
	 * @param action
	 *            :操作事件类型
	 * @param x
	 *            :x轴的坐标
	 * @param y
	 *            :y轴的坐标
	 * @param defaultResult
	 *            :默认显示的值
	 * @return
	 */
	public boolean touchPage(int action, float x, float y, boolean defaultResult) {
		boolean result = defaultResult;
		switch (action) {
		// 手指按下
		case MotionEvent.ACTION_DOWN:
			// 记录开始坐标
			viewModel.touchStartX = x;
			viewModel.touchStartY = y;
			viewModel.moveX = 0;
			viewModel.moveY = 0;
			break;
		// 手指抬起
		case MotionEvent.ACTION_UP:
			// 滑动距离必须是一定范围,X和Y的距离
			viewModel.moveX = (x - viewModel.touchStartX > 0 ? x - viewModel.touchStartX : viewModel.touchStartX - x);
			viewModel.moveY = (y - viewModel.touchStartY > 0 ? y - viewModel.touchStartY : viewModel.touchStartY - y);
			if ((viewModel.moveY * 2 < viewModel.moveX) && viewModel.moveX > viewModel.TOUCH_DISTANCE && viewModel.moveY > viewModel.TOUCH_DISTANCE / 2) {
				// 往左滑动
				if (x < viewModel.touchStartX) {
					// this.finish();
				}
				// 往右滑动
				else if (viewModel.touchStartX < x) {
					this.finish();
				}
				result = true;
			}
			break;
		}
		return result;
	}

	// }}

	// {{ 按钮点击事件
	private OnClickListener btnClickLister = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (EIASApplication.IsNetworking) {
				switch (v.getId()) {
				case R.id.userlogo:
					changeLoge();
					break;
				case R.id.changesubmit:
					onClickChangePasswordBtn();
					break;
				case R.id.checkversion:
					onClickCheckversion();
				}
			} else {
				showToast("网络连接失败！！");
			}
		}
	};

	/**
	 * 修改密码点击触发的消息发送事件
	 */
	private void onClickCheckversion() {
		loadingWorker.showLoading("检测中...");
		Message msg = new Message();
		msg.what = TASK_CHECK_VERSION;
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 修改密码点击触发的消息发送事件
	 */
	private void onClickChangePasswordBtn() {
		loadingWorker.showLoading("操作中...");
		Message msg = new Message();
		msg.what = TASK_CHANGE_PASSWORD;
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 修改用户头像
	 */
	private void changeLoge() {
		new AlertDialog.Builder(this).setTitle("设置头像").setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				// 点击选择图片文件按钮
				case 0:
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery, TASK_IMAGE_REQUEST_CODE);
					break;
				// 点击拍照按钮
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(viewModel.userDirectory, IMAGE_FILE_TEMPNAME)));
					startActivityForResult(intentFromCapture, TASK_CAMERA_REQUEST_CODE);
					break;
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	// }}

	/**
	 * startActivityForResult返回时候调用
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		String tempPath = viewModel.userDirectory + IMAGE_FILE_TEMPNAME;
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case TASK_IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case TASK_CAMERA_REQUEST_CODE:
				File tempFile = new File(tempPath);
				startPhotoZoom(Uri.fromFile(tempFile));
				break;
			case TASK_RESULT_REQUEST_CODE:
				if (data != null) {
					String path = viewModel.userDirectory + IMAGE_FILE_NAME;
					UserInfoDTO userDto = UserInfoOperator.getImageToView(data, path, tempPath);
					sendUploadUserImageMsg(userDto);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 发送头像修改请求
	 * 
	 * @param userDto
	 */
	private void sendUploadUserImageMsg(UserInfoDTO userDto) {
		// 发出一个消息到后台处理
		loadingWorker.showLoading("头像修改中...");
		Message msg = new Message();
		msg.obj = userDto;
		msg.what = TASK_CHANGE_USER_IMAGE;
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 释放资源
	 */
	@Override
	protected void onDestroy() {
		AppHeaderMenu.openActivityName = "";
		instance = null;
		super.onDestroy();
		appHeader.unRegisterReceiver();
	}

}
