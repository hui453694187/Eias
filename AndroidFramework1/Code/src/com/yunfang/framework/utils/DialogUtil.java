package com.yunfang.framework.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yunfang.framework.R;
import com.yunfang.framework.db.SQLiteHelper;

/**
 * dialog的封装类
 * 
 * @author gorson
 * 
 */
public class DialogUtil {

	/**
	 * @param context
	 *            上下文
	 * @param viewID
	 *            布局视图ID
	 * @return：返回一个对话框
	 */
	@SuppressWarnings("unused")
	public static Dialog commonDialog(Context context, int viewID) {
		final Dialog dialog = new Dialog(context, R.style.MyDialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(viewID, null);
		dialog.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		Point point = WinDisplay.getWidthAndHeight(context);
		params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
		params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	/**
	 * @param context
	 *            ：上下文
	 * @param viewID
	 *            :布局视图ID
	 * @return：返回一个对话框
	 */
	@SuppressWarnings("unused")
	public static Dialog getAboutDialog(Context context, View view) {
		final Dialog dialog = new Dialog(context, R.style.dialog_common);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		Point point = WinDisplay.getWidthAndHeight(context);
		params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
		params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	/**
	 * @param context
	 *            ：上下文
	 * @param viewID
	 *            :布局视图ID
	 * @return：返回一个任务信息的对话框
	 */
	public static Dialog getDetailDialog(Context context, View view) {
		final Dialog dialog = new Dialog(context, R.style.MyDialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		Point point = WinDisplay.getWidthAndHeight(context);
		params.width = (int) ((point.x) * (0.8));
		params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	/**
	 * 退出AlertDialog
	 * 
	 * @param context
	 * @return
	 */
	public static Dialog getQuitDialog(final Context context) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setTitle("确定要退出 ?");
		builder.setIcon(R.drawable.base_dialog_title_about_ic_normal);
		AlertDialog alertDialog = null;
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					SQLiteHelper.getInstance().close();
				} catch (Exception e) {
				}
				((Activity) context).finish();
			}
		});
		alertDialog = builder.create();
		return alertDialog;
	}

	/**
	 * 确认对话框
	 * 
	 * @param context
	 *            :当前上下文
	 * @param title
	 *            :标题
	 * @param confirm
	 *            :确认需要执行的事件
	 * @return
	 */
	public static void showConfirmationDialog(final Context context,
			String title, DialogInterface.OnClickListener confirm) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("询问信息");
		builder.setPositiveButton("确认", confirm);
		builder.setNegativeButton("取消", null);
		builder.setMessage(title);
		builder.show();
	}

	/**
	 * 弹出层
	 * 
	 * @param context
	 *            :上下文
	 * @param viewID
	 *            :布局视图ID
	 * @return：返回一个对话框
	 */
	public static Dialog loadingDialog(Context context, View view) {
		final Dialog dialog = new Dialog(context, R.style.loading_dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		Point point = WinDisplay.getWidthAndHeight(context);
		params.width = point.x;
		params.height = point.y;
		params.alpha = 0.7f;
		dialog.getWindow().setAttributes(params);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	/**
	 * 弹出指定层
	 * 
	 * @param context
	 * @param view
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param alpha
	 *            透明度
	 * @return
	 */
	public static Dialog showDialog(Context context, View view, int width,
			int height, float alpha) {
		final Dialog dialog = new Dialog(context, R.style.loading_dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		params.width = width;
		params.height = height;
		params.alpha = alpha;
		dialog.getWindow().setAttributes(params);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
}
