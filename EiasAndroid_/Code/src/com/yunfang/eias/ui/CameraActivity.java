package com.yunfang.eias.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.utils.ToastUtil;
import com.yunfang.framework.view.album.FilterImageView;
import com.yunfang.framework.view.camera.CameraContainer;
import com.yunfang.framework.view.camera.CameraContainer.TakePictureListener;
import com.yunfang.framework.view.camera.CameraView.FlashMode;

/**
 * @ClassName: CameraAty
 * @Description: 自定义照相机类
 * @author 贺隽
 * @date 2015-7-9 9:44:25
 * 
 */
public class CameraActivity extends BaseWorkerActivity implements View.OnClickListener, TakePictureListener {
	public final static String TAG = "CameraAty";
	private boolean mIsRecordMode = false;
	private String mSaveRoot;
	private String mThumbnailRoot;
	private CameraContainer mContainer;
	private FilterImageView mThumbView;
	private ImageButton mCameraShutterButton;
	private ImageButton mCameraBackButton;
	private ImageButton mRecordShutterButton;
	private ImageView mFlashView;
	private ImageButton mSwitchModeButton;

	// 自拍模式按钮
	private ImageView mSwitchCameraView;
	private ImageView mSettingView;
	private View mHeaderBar;
	private boolean isRecording = false;

	// {{ 事件定义

	/**
	 * shutter_camera点击事件
	 */
	private final int TASK_EVENT_SHUTTER = 2;

	/**
	 * thumbnail点击事件
	 */
	private final int TASK_EVENT_THUMBNAIL = 3;

	/**
	 * flash点击事件
	 */
	private final int TASK_EVENT_FLASH = 4;

	/**
	 * switch点击事件
	 */
	private final int TASK_EVENT_SWITCH = 5;

	/**
	 * SHUTTER_RECORD点击事件
	 */
	private final int TASK_EVENT_SHUTTER_RECORD = 6;

	/**
	 * SWITCH_CAMERA 点击事件
	 */
	private final int TASK_EVENT_SWITCH_CAMERA = 7;

	/**
	 * OTHER_SETTING点击事件
	 */
	private final int TASK_EVENT_OTHER_SETTING = 8;

	/**
	 * ONANIMTIONEND 回调事件
	 */
	private final int TASK_EVENT_ONANIMTIONEND = 9;

	// }}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);
		mHeaderBar = findViewById(R.id.camera_header_bar);
		mContainer = (CameraContainer) findViewById(R.id.container);
		mThumbView = (FilterImageView) findViewById(R.id.btn_thumbnail);
		mCameraShutterButton = (ImageButton) findViewById(R.id.btn_shutter_camera);
		mRecordShutterButton = (ImageButton) findViewById(R.id.btn_shutter_record);
		mSwitchCameraView = (ImageView) findViewById(R.id.btn_switch_camera);
		mFlashView = (ImageView) findViewById(R.id.btn_flash_mode);
		mSwitchModeButton = (ImageButton) findViewById(R.id.btn_switch_mode);
		mSettingView = (ImageView) findViewById(R.id.btn_other_setting);
		mSwitchModeButton.setVisibility(View.INVISIBLE);
		mCameraBackButton=(ImageButton)this.findViewById(R.id.camera_header_btn_back);
		mCameraBackButton.setOnClickListener(this);

		mThumbView.setOnClickListener(this);
		mCameraShutterButton.setOnClickListener(this);
		// mRecordShutterButton.setOnClickListener(this);
		mFlashView.setOnClickListener(this);
		mSwitchModeButton.setOnClickListener(this);
		mSwitchCameraView.setOnClickListener(this);
		mSwitchCameraView.setVisibility(View.GONE);
		mSettingView.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		mSaveRoot = bundle.getString("saveRoot");
		mThumbnailRoot = bundle.getString("thumbnailRoot");
		
		if(!mContainer.hasSystematically()){
			/** 没有获取到摄像头权限， 退出  */
			ToastUtil.longShow(this,"打开摄像头失败， 请检查是否禁用了外采调用摄像头权限");
			Log.d(TAG,"没有获取到摄像头权限， 退出");
			this.finish();
		}
	}

	/**
	 * 执行工作
	 * 
	 * @param msg显示加载信息
	 * @param taskId需要执行工作的编号
	 * @param obj可选参数
	 */
	public void doWorking(String msg, int taskId, Object obj) {
		if (msg.length() > 0) {
			loadingWorker.showLoading(msg);
		}
		Message taskMsg = new Message();
		taskMsg.what = taskId;
		taskMsg.obj = obj;
		mBackgroundHandler.sendMessage(taskMsg);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_shutter_camera:
			showToast("拍照中，请勿摇晃设备");
			doWorking("", TASK_EVENT_SHUTTER, "");
			/*
			 * mCameraShutterButton.setClickable(false);
			 * mContainer.takePicture(this);
			 */
			break;
		case R.id.btn_thumbnail:
			doWorking("", TASK_EVENT_THUMBNAIL, "");
			/*
			 * saveFiles(); finish();
			 */
			break;
		case R.id.btn_flash_mode:
			doWorking("", TASK_EVENT_FLASH, "");
			/*
			 * if (mContainer.getFlashMode() == FlashMode.ON) {
			 * mContainer.setFlashMode(FlashMode.OFF);
			 * mFlashView.setImageResource(R.drawable.camera_btn_flash_off); }
			 * else if (mContainer.getFlashMode() == FlashMode.OFF) {
			 * mContainer.setFlashMode(FlashMode.AUTO);
			 * mFlashView.setImageResource(R.drawable.camera_btn_flash_auto); }
			 * else if (mContainer.getFlashMode() == FlashMode.AUTO) {
			 * mContainer.setFlashMode(FlashMode.TORCH);
			 * mFlashView.setImageResource(R.drawable.camera_btn_flash_torch); }
			 * else if (mContainer.getFlashMode() == FlashMode.TORCH) {
			 * mContainer.setFlashMode(FlashMode.ON);
			 * mFlashView.setImageResource(R.drawable.camera_btn_flash_on); }
			 */
			break;
		case R.id.btn_switch_mode:
			// doWorking("",TASK_EVENT_SWITCH,"");
			if (mIsRecordMode) {
				mSwitchModeButton.setImageResource(R.drawable.camera_switch_camera);
				mCameraShutterButton.setVisibility(View.VISIBLE);
				mRecordShutterButton.setVisibility(View.GONE);
				// 拍照模式下显示顶部菜单
				mHeaderBar.setVisibility(View.VISIBLE);
				mIsRecordMode = false;
				mContainer.switchMode(0);
				stopRecord();
			} else {
				mSwitchModeButton.setImageResource(R.drawable.camera_switch_video);
				mCameraShutterButton.setVisibility(View.GONE);
				mRecordShutterButton.setVisibility(View.VISIBLE);
				// 录像模式下隐藏顶部菜单
				mHeaderBar.setVisibility(View.GONE);
				mIsRecordMode = true;
				mContainer.switchMode(5);
			}
			break;
		case R.id.btn_shutter_record:
			doWorking("", TASK_EVENT_SHUTTER_RECORD, "");
			/*
			 * if (!isRecording) { isRecording = mContainer.startRecord(); if
			 * (isRecording) {
			 * //mRecordShutterButton.setBackgroundResource(R.drawable
			 * .camera_btn_shutter_recording); } } else { stopRecord(); }
			 */
			break;
		case R.id.btn_switch_camera:
			doWorking("", TASK_EVENT_SWITCH_CAMERA, "");
			/* mContainer.switchCamera(); */
			break;
		case R.id.btn_other_setting:
			doWorking("", TASK_EVENT_OTHER_SETTING, "");
			/* mContainer.setWaterMark(); */
			break;
		case R.id.camera_header_btn_back:
			// 设置返回数据
			this.setResult(RESULT_OK, null);
			this.finish();
			break;
		default:
			break;
		}
	}

	private void stopRecord() {
		mContainer.stopRecord(this);
		isRecording = false;
		// mRecordShutterButton.setBackgroundResource(R.drawable.camera_btn_shutter_record);
	}

	@Override
	public void onTakePictureEnd(byte[] data) {
		EIASApplication.myPhoto = data;
	}

	@Override
	public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
		doWorking("", TASK_EVENT_ONANIMTIONEND, "");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent();
		intent.setAction(BroadRecordType.CAMERASERVER_BACK);
		sendBroadcast(intent);
		super.onDestroy();
	}

	/*
	 * 后端线程
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {

		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		case TASK_EVENT_SHUTTER:
			break;
		case TASK_EVENT_THUMBNAIL:
			break;
		case TASK_EVENT_FLASH:
			break;
		case TASK_EVENT_SWITCH:
			break;
		case TASK_EVENT_SHUTTER_RECORD:
			break;
		case TASK_EVENT_SWITCH_CAMERA:
			mContainer.switchCamera();
			break;
		case TASK_EVENT_OTHER_SETTING:
			break;
		case TASK_EVENT_ONANIMTIONEND:
			break;
		default:
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/*
	 * UI线程
	 */
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch (msg.what) {
		case TASK_EVENT_SHUTTER:
			mCameraShutterButton.setClickable(false);
			mContainer.takePicture(this);
			break;
		case TASK_EVENT_THUMBNAIL:
			finish();
			break;
		case TASK_EVENT_FLASH:
			if (mContainer.getFlashMode() == FlashMode.ON) {
				mContainer.setFlashMode(FlashMode.OFF);
				mFlashView.setImageResource(R.drawable.camera_btn_flash_off);
			} else if (mContainer.getFlashMode() == FlashMode.OFF) {
				mContainer.setFlashMode(FlashMode.AUTO);
				mFlashView.setImageResource(R.drawable.camera_btn_flash_auto);
			} else if (mContainer.getFlashMode() == FlashMode.AUTO) {
				mContainer.setFlashMode(FlashMode.TORCH);
				mFlashView.setImageResource(R.drawable.camera_btn_flash_torch);
			} else if (mContainer.getFlashMode() == FlashMode.TORCH) {
				mContainer.setFlashMode(FlashMode.ON);
				mFlashView.setImageResource(R.drawable.camera_btn_flash_on);
			}
			break;
		case TASK_EVENT_SWITCH:
			if (mIsRecordMode) {
				mSwitchModeButton.setImageResource(R.drawable.camera_switch_camera);
				mCameraShutterButton.setVisibility(View.VISIBLE);
				mRecordShutterButton.setVisibility(View.GONE);
				// 拍照模式下显示顶部菜单
				mHeaderBar.setVisibility(View.VISIBLE);
				mIsRecordMode = false;
				mContainer.switchMode(0);
				stopRecord();
			} else {
				mSwitchModeButton.setImageResource(R.drawable.camera_switch_video);
				mCameraShutterButton.setVisibility(View.GONE);
				mRecordShutterButton.setVisibility(View.VISIBLE);
				// 录像模式下隐藏顶部菜单
				mHeaderBar.setVisibility(View.GONE);
				mIsRecordMode = true;
				mContainer.switchMode(5);
			}
			break;

		case TASK_EVENT_SHUTTER_RECORD:
			if (!isRecording) {
				isRecording = mContainer.startRecord();
				if (isRecording) {
					// mRecordShutterButton.setBackgroundResource(R.drawable.camera_btn_shutter_recording);
				}
			} else {
				stopRecord();
			}
			break;
		case TASK_EVENT_SWITCH_CAMERA:
			// mContainer.switchCamera();
			break;
		case TASK_EVENT_OTHER_SETTING:
			mContainer.setWaterMark();
			break;
		case TASK_EVENT_ONANIMTIONEND:
			mCameraShutterButton.setClickable(true);
			
			Bundle bundle = new Bundle();
			bundle.putString("path1", mSaveRoot);
			bundle.putString("path2", mThumbnailRoot);
			
			Intent intent = new Intent();
			intent.setClass(CameraActivity.this, CameraPreviewActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		default:
			break;
		}
		loadingWorker.closeLoading();
	}

	/**
	 * 保存图片到任务子项中
	 * 
	 * @param fileName
	 */
	@SuppressWarnings("unused")
	private void savePhoto(String fileName) {
		String[] files = new String[] { fileName };
		Intent intent = new Intent();
		intent.setAction(BroadRecordType.CAMERASERVER_SEND);
		intent.putExtra("files", files);
		sendBroadcast(intent);
	}

}