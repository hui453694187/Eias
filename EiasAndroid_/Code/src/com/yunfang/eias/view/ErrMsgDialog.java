/**
 * 
 */
package com.yunfang.eias.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunfang.eias.R;

/**
 * @author kevin
 *   自定义异常提示对话框
 *
 */
public class ErrMsgDialog extends Dialog {

	private TextView dialog_view_info_concent;
	
	private Button dialog_view_info_confirm;
	
	/**
	 * @param context
	 */
	public ErrMsgDialog(Context context) {
		super(context,R.style.MyDialog);
		//super(context);
		setContextView(context);
		this.setCanceledOnTouchOutside(false);
	}

	/** 
	 * @author kevin
	 * @date 2015-10-9 上午9:24:28
	 * @version V1.0
	 */
	@SuppressLint("InflateParams")
	private void setContextView(Context context) {
		View dialogView=LayoutInflater.from(context).
				inflate(R.layout.dialog_view_info, null);
		
		dialog_view_info_concent=(TextView)dialogView.findViewById(R.id.dialog_view_info_concent);
		dialog_view_info_confirm=(Button)dialogView.findViewById(R.id.dialog_view_info_confirm);
		dialog_view_info_concent.setTextColor(context.getResources().getColor(R.color.black));
		dialog_view_info_concent.setTextSize(20);
		super.setContentView(dialogView);
	}
	
	public void setErrMsgInfo(String errMsg){
		dialog_view_info_concent.setText(errMsg);
	}
	
	public void setConfirmClickListener(android.view.View.OnClickListener confirmListener){
		dialog_view_info_confirm.setOnClickListener(confirmListener);
	}

}
