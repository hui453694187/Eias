package com.yunfang.eias.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.LogType;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.http.task.UploadLogInfoTask;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.model.DataLog;
import com.yunfang.eias.view.ErrMsgDialog;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
/***
 * 
 * @author Kevin
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	@SuppressLint("SimpleDateFormat")
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化 绑定到当前线程
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		if (!handleException(ex) && mDefaultHandler != null) {
			Log.d("errLog", "!handleException(ex)");
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			Log.d("errLog", "uploadException");
			uploadException(ex);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				// 自定义弹出对话框
				// showTipsDialog();

				final ErrMsgDialog errMsgDialog = new ErrMsgDialog(EIASApplication.getInstance().//
						getActivityManager().//
						currentActivity());//
				errMsgDialog.setConfirmClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						EIASApplication.getInstance().getActivityManager().finishAllActivity();
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(1); // AppManager.AppExit(activity);
						errMsgDialog.dismiss();
					}
				});

				errMsgDialog.setErrMsgInfo("程序发生异常，点击确认退出!");
				errMsgDialog.show();

				/*
				 * new AlertDialog.Builder(EIASApplication.getInstance().//
				 * getActivityManager().// currentActivity()).//
				 * setMessage("程序发生异常，点击确认退出!").// setPositiveButton("确定", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { //需要退出当前栈中的所有活动
				 * EIASApplication.getInstance().getActivityManager
				 * ().finishAllActivity();
				 * android.os.Process.killProcess(android.os.Process.myPid());
				 * System.exit(1); // AppManager.AppExit(activity); }
				 * }).create().show();
				 */

				Looper.loop();
			}
		}.start();
		// ;
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文 本地不保存异常日志
		// saveCrashInfo2File(ex);
		return true;
	}

	@SuppressWarnings("unused")
	private void showTipsDialog() {
		/*
		 * Activity activity;// = AppManager.currentActivity(); activity =
		 * EIASApplication.getInstance().getActivityManager().currentActivity();
		 */
		try {
			// AppManager.currentActivity();
			AppHeader header = new AppHeader(EIASApplication.getInstance().//
					getActivityManager().//
					currentActivity(),//
					R.id.home_title);
			header.showDialog("提示", "程序发生异常，点击确认退出!", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 结束所有存在的activity
					EIASApplication.getInstance().getActivityManager().finishAllActivity();

					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(1);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			EIASApplication.getInstance().getActivityManager().finishAllActivity();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				// Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * @author kevin
	 * @date 2015-9-28 上午10:53:52
	 * @Description: 上传异常信息到后台
	 * @param ex
	 *            异常信息
	 * @version V1.0
	 */
	private void uploadException(Throwable ex) {
		Log.d("errLog", "开始收集异常信息！");
		StringBuffer sb = null;
		sb = getExceptionStr(ex);
		String localVersionName = EIASApplication.version.LocalVersionName;
		String excInfo = "客户端版本:" + localVersionName + "\r\n" + sb.toString();

		excationUpLoad(EIASApplication.getCurrentUser(), excInfo, OperatorTypeEnum.UnChatchExection);
	}

	private void excationUpLoad(final UserInfo currentUser, final String errInfo, final OperatorTypeEnum operatorType) {

		new Thread() {
			public void run() {
				try {
					// 是否成功写入服务器
					Boolean isSuccessSaveInService = false;
					final DataLog log = new DataLog();
					log.UserID = currentUser.Account;
					log.LogContent = errInfo;
					log.OperatorType = operatorType;
					log.logType = LogType.Exection;
					// 有网络的情况下直接将日志信息写入服务端
					if (EIASApplication.IsNetworking) {
						UploadLogInfoTask setLogTask = new UploadLogInfoTask();
						ResultInfo<Boolean> uploadInfo = setLogTask.request(currentUser, log);
						isSuccessSaveInService = (uploadInfo.Data && uploadInfo.Success);
					}
					// 上传失败，或者无网情况，则写入本地数据库
					if (!isSuccessSaveInService) {
						log.ID = (int) log.onInsert();
					}
				} catch (Exception e) {
					if(e!=null){
						Log.e(TAG, "未捕获异常，日志处理出错！Line(271)");
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	/**
	 * 保存错误信息到文件中 本地存储闪退异常信息
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	@SuppressWarnings("unused")
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = null;
		sb = getExceptionStr(ex);
		try {
			return write2Local(sb);
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file..", e);
		}
		return null;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-11-12 下午2:09:40
	 * @Description: 拼接异常信息，设备信息 字串
	 * @param Throwable
	 *            ex 异常信息
	 * @return StringBuffer 返回类型
	 */
	private StringBuffer getExceptionStr(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append("\r\n 客户端崩溃，未捕获的异常信息:" + result);

		return sb;
	}

	@SuppressLint("SdCardPath")
	private String write2Local(StringBuffer sb) throws IOException {
		long timestamp = System.currentTimeMillis();
		String time = formatter.format(new Date());
		String fileName = "crash-" + time + "-" + timestamp + ".log";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = "/sdcard/crash/";
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(path + fileName);
			fos.write(sb.toString().getBytes());
			fos.close();
		}

		return fileName;
	}
}
