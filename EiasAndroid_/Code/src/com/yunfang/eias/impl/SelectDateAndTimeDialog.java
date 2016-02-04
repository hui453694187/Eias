package com.yunfang.eias.impl;

import java.util.Calendar;

import com.yunfang.eias.base.EIASApplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class SelectDateAndTimeDialog extends Activity {
	// {{ 视图模型

	public static final String SERVICESDATEDIALOG = "com.yunfang.eias.service.select.date.dialog";
	public static final String SERVICESTIMEDIALOG = "com.yunfang.eias.service.select.time.dialog";

	public final String PARAM_DATE = "date";
	public final String PARAM_TIME = "time";

	public final int DATE_DIALOG_ID = 1;
	public final int TIME_DIALOG_ID = 3;

	private final int SHOW_TIMEPICK = 2;
	private final int SHOW_DATAPICK = 0;

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	Context mContext;
	// }}

	// {{ 调用方法

	/**
	 * 初始化控件和UI视图
	 */
	public SelectDateAndTimeDialog(Context context) {

		mContext = context;
		
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		setDateTime();
		setTimeOfDay();
	}

	public void showDateDialog() {
		Message msg = new Message();
		msg.what = SHOW_DATAPICK;
		SelectDateAndTimeDialog.this.dateandtimeHandler.sendMessage(msg);
	}

	public void showTimeDialog() {
		Message msg = new Message();
		msg.what = SHOW_DATAPICK;
		SelectDateAndTimeDialog.this.dateandtimeHandler.sendMessage(msg);
	}

	/**
	 * 设置日期
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay() {
		String date = new StringBuilder().append(mYear).append("-").append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
				.append((mDay < 10) ? "0" + mDay : mDay).toString();

		Intent intent = new Intent();
		intent.setAction(SERVICESDATEDIALOG);
		intent.putExtra(PARAM_DATE, date);
		sendBroadcast(intent);
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateDisplay();
		}
	};

	/**
	 * 设置时间
	 */
	private void setTimeOfDay() {
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
	}

	/**
	 * 更新时间显示
	 */
	private void updateTimeDisplay() {
		String time = new StringBuilder().append(mHour).append(":").append((mMinute < 10) ? "0" + mMinute : mMinute).toString();

		Intent intent = new Intent();
		intent.setAction(SERVICESTIMEDIALOG);
		intent.putExtra(PARAM_TIME, time);
		sendBroadcast(intent);
	}

	/**
	 * 时间控件事件
	 */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;

			updateTimeDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DATE_DIALOG_ID:
			dialog = new DatePickerDialog(EIASApplication.getInstance(), mDateSetListener, mYear, mMonth, mDay);
			break;
		case TIME_DIALOG_ID:
			dialog = new TimePickerDialog(EIASApplication.getInstance(), mTimeSetListener, mHour, mMinute, true);
			break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			//((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case TIME_DIALOG_ID:
			//((TimePickerDialog) dialog).updateTime(mHour, mMinute);
			break;
		}
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	@SuppressWarnings({ "deprecation" })
	Handler dateandtimeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			case SHOW_TIMEPICK:
				showDialog(TIME_DIALOG_ID);
				break;
			}
		}

	};
	// }}
}