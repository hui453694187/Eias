/**
 * 
 */
package com.yunfang.eias.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.model.ResultInfo;

/**
 * @author chs 系统设置信息
 */
public class SystemSettingActivity extends BaseWorkerActivity {
	// {{ 变量

	public static String[] copyTypeChooseItem = { "复制", "剪切" };

	public static String[] photoTypeChooseItem = { "外采定制相机", "系统自带相机" };
	// }}

	// {{ 控件

	/**
	 * 主菜单
	 */
	private AppHeader appHeader;

	/**
	 * 最大图片大小
	 */
	private EditText et_setting_maximagesize;

	/**
	 * 提交重复次数
	 */
	private EditText et_setting_repeattime;

	/**
	 * 设置但是使用的SD卡小于指定值时提示
	 */
	private EditText et_setting_size;

	/**
	 * 保存按钮
	 */
	private Button btn_changesubmit;

	/**
	 * 赋值或粘贴下拉框
	 */
	// private Spinner et_setting_copyOrPaste;

	/** 赋值或粘贴单选按钮组 */
	private RadioGroup rg_setting_copyOrPaste;

	/** 拍照方式单选按钮组 */
	private RadioGroup rg_setting_photo;

	/**
	 * 拍照方式选择
	 */
	// private Spinner et_setting_photo;

	// }}

	// {{ 变量

	/**
	 * 检测版本
	 */
	private static final int CHANGE_SETTING = 0;

	// }}

	// {{ 初始化
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.system_setting);
		init();
	}

	/**
	 * 初始化控件
	 */
	private void init() {
		appHeader = new AppHeader(this, R.id.home_title);
		et_setting_maximagesize = (EditText) findViewById(R.id.et_setting_maximagesize);
		et_setting_repeattime = (EditText) findViewById(R.id.et_setting_repeattime);
		et_setting_size = (EditText) findViewById(R.id.et_setting_size);
		btn_changesubmit = (Button) findViewById(R.id.btn_changesubmit);
		/*
		 * et_setting_copyOrPaste = (Spinner)
		 * findViewById(R.id.et_setting_copyOrPaste); //et_setting_photo =
		 * (Spinner) findViewById(R.id.et_setting_photo);
		 */
		rg_setting_copyOrPaste = (RadioGroup) this.findViewById(R.id.rg_setting_copyOrPaste);
		rg_setting_photo = (RadioGroup) this.findViewById(R.id.rg_setting_photo);

		/** 获取原来系统设置 */
		String selectPaste = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_PICTURECOPYORPASTE);
		String selectPhotoType = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_PHOTOTYPE);
		setRadioButtonSelectedByValue(rg_setting_copyOrPaste, selectPaste);
		setRadioButtonSelectedByValue(rg_setting_photo, selectPhotoType);

		// 注释下拉列表功能
		/*
		 * ArrayAdapter copyTypeChooseAdapter = new ArrayAdapter(this,
		 * android.R.layout.simple_spinner_item, copyTypeChooseItem);
		 * et_setting_copyOrPaste.setAdapter(copyTypeChooseAdapter);
		 * setSpinnerItemSelectedByValue(et_setting_copyOrPaste, selectPaste);
		 */
		/*
		 * ArrayAdapter photoTypeChooseAdapter = new ArrayAdapter(this,
		 * android.R.layout.simple_spinner_item, photoTypeChooseItem);
		 * et_setting_photo.setAdapter(photoTypeChooseAdapter);
		 * setSpinnerItemSelectedByValue(et_setting_photo, selectPhotoType);
		 */

		btn_changesubmit.setOnClickListener(btnClickLister);
		et_setting_maximagesize.setText(EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_MAXIMAGESIZE));
		et_setting_repeattime.setText(EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_REPEATTIME));
		et_setting_size.setText(EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_SDCARDSIZE));

		appHeader.visBackView(true);
		appHeader.visUserInfo(false);
		appHeader.visNetFlag(false);
		appHeader.setTitle("系统设置");
	}

	/**
	 * 根据值, 设置spinner默认选中:
	 * 
	 * @param spinner
	 * @param value
	 */
	public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
		SpinnerAdapter apsAdapter = spinner.getAdapter(); // 得到SpinnerAdapter对象
		int k = apsAdapter.getCount();
		for (int i = 0; i < k; i++) {
			if (value.equals(apsAdapter.getItem(i).toString())) {
				spinner.setSelection(i, true);// 默认选中项
				break;
			}
		}
	}

	/***
	 * 根据值, 设置RadioGroup默认选中:
	 * 
	 * @param rg
	 * @param value
	 */
	public void setRadioButtonSelectedByValue(RadioGroup rg, String value) {
		for (int i = 0; i < rg.getChildCount(); i++) {
			String rbStr = ((RadioButton) rg.getChildAt(i)).getText().toString();
			if (rbStr.equals(value)) {
				((RadioButton) rg.getChildAt(i)).setChecked(true);
				break;
			}
		}

	}

	private OnClickListener btnClickLister = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_changesubmit:
				changeSubmit();
				break;
			}
		}
	};

	/**
	 * 保存按钮出发事件
	 */
	private void changeSubmit() {
		loadingWorker.showLoading("操作中...");
		Message msg = new Message();
		msg.what = CHANGE_SETTING;
		mBackgroundHandler.sendMessage(msg);
	}

	/**
	 * 修改变量前的验证
	 */
	private String formatVerification() {
		// 提示信息
		String resultMsg = "";
		String repeatTime = et_setting_repeattime.getText().toString();
		String maxImageSize = et_setting_maximagesize.getText().toString();
		String minSDcardSize = et_setting_size.getText().toString();
		if (repeatTime.length() == 0 || maxImageSize.length() == 0 && minSDcardSize.length() == 0) {
			resultMsg = "请把信息输入完整";
		}
		try {
			if (Integer.parseInt(minSDcardSize) < 100) {
				resultMsg = "剩余容量提示不能小于100";
			}
		} catch (NumberFormatException e) {
			resultMsg = "容量输入有误！";
		}
		return resultMsg;
	}

	// }}

	// {{ 处理后的方法
	/**
	 * 后台线程
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		super.handleUiMessage(msg);
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		case CHANGE_SETTING:
			ResultInfo<Boolean> result = new ResultInfo<Boolean>();
			// 验证输入格式
			String verificationResult = formatVerification();
			if (verificationResult == "") {
				String repeatTime = et_setting_repeattime.getText().toString();
				String maxImageSize = et_setting_maximagesize.getText().toString();
				String minSDCardsize = et_setting_size.getText().toString();

				int copyOrPasteId = rg_setting_copyOrPaste.getCheckedRadioButtonId();
				int settingPhotoId = rg_setting_photo.getCheckedRadioButtonId();
				String copyOrPaste = (String) ((RadioButton) this.findViewById(copyOrPasteId)).getText();
				String photoType = (String) ((RadioButton) this.findViewById(settingPhotoId)).getText();

				// 取得客户端缓存信息保存集合对象
				SharedPreferences sp = EIASApplication.getInstance().getSharedPreferences(BroadRecordType.KEY_SETTINGS, Activity.MODE_PRIVATE);
				// 提交任务重复次数
				sp.edit().putString(BroadRecordType.KEY_SETTING_REPEATTIME, repeatTime).commit();
				// 拍摄图片的最大size(设定后拍摄的照片不会大于这个值)
				sp.edit().putString(BroadRecordType.KEY_SETTING_MAXIMAGESIZE, maxImageSize).commit();
				// 设置但是使用的SD卡小于指定值时提示
				sp.edit().putString(BroadRecordType.KEY_SETTING_SDCARDSIZE, minSDCardsize).commit();
				// 设置复制或剪切项
				sp.edit().putString(BroadRecordType.KEY_SETTING_PICTURECOPYORPASTE, copyOrPaste).commit();
				// 设置相机类型
				sp.edit().putString(BroadRecordType.KEY_SETTING_PHOTOTYPE, photoType).commit();
				result.Success = true;
				resultMsg.obj = result;
			} else {
				result.Success = false;
				result.Message = verificationResult;
				resultMsg.obj = result;
			}
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/*
	 * UI线程
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
		case CHANGE_SETTING:
			if (result.Success) {
				showToast("保存成功");
			} else {
				showToast(result.Message);
			}
			break;
		}
		loadingWorker.closeLoading();
	}

	// }}

}
