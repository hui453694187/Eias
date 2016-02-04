package com.yunfang.eias.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.yunfang.eias.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class DateTimePickerDialog implements OnDateChangedListener, OnTimeChangedListener {

	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;

	private OnSelectTimeListener onSelecctTimeListener;

	/**
	 * 日期时间弹出选择框构
	 * 
	 * @param activity
	 *            ：调用的父activity
	 */
	public DateTimePickerDialog(Activity activity) {
		this.activity = activity;
	}

	@SuppressLint("SimpleDateFormat")
	public void init(DatePicker datePicker, TimePicker timePicker, String oldDateTime) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		if (!TextUtils.isEmpty(oldDateTime)) {// 原来时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			Date sd = sdf.parse(oldDateTime);
			calendar.setTime(sd);
		}
		initDateTime = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框
	 * 
	 * @param dateTimeTextEdite
	 *            需要设置的日期时间文本编辑框
	 * @param type
	 *            : 0为日期时间类型:yyyy-MM-dd HH:mm:ss 1为日期类型:yyyy-MM-dd
	 *            2为时间类型:HH:mm:ss
	 * @return
	 * @throws ParseException
	 *             时间字符串格式错误
	 */
	@SuppressLint("InflateParams")
	public AlertDialog dateTimePicKDialog(final EditText dateTimeTextEdite, int type) throws ParseException {
		Calendar c = Calendar.getInstance();
		switch (type) {
		case 1:
			new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
				@SuppressLint("SimpleDateFormat")
				public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					dateTime = sdf.format(calendar.getTime());
					dateTimeTextEdite.setText(dateTime);
				}
			}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
			return null;
		case 2:
			new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
				@SuppressLint("SimpleDateFormat")
				public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					dateTime = sdf.format(calendar.getTime());
					dateTimeTextEdite.setText(dateTime);
				}
			}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
			return null;
		default:
			LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.datetime, null);
			datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
			timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
			init(datePicker, timePicker, dateTimeTextEdite.getText().toString());
			timePicker.setIs24HourView(true);
			timePicker.setOnTimeChangedListener(this);

			ad = new AlertDialog.Builder(activity).setIcon(R.drawable.search)//
					.setTitle(initDateTime)//
					.setView(dateTimeLayout)//
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dateTimeTextEdite.setText(dateTime);
							DateTimePickerDialog.this.onSelecctTimeListener.onSelectTime(dateTime);
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
						}
					}).show();

			onDateChanged(null, 0, 0, 0);
			return ad;
		}
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}

	public void setOnSelecctTimeListener(OnSelectTimeListener onSelecctTimeListener) {
		this.onSelecctTimeListener = onSelecctTimeListener;
	}

	public interface OnSelectTimeListener {

		public void onSelectTime(String time);

	}

}
