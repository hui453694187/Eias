package com.yunfang.eias.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yunfang.eias.R;

/**
 * 带按钮的输入框
 * 主要用于跳转到百度地图获取周边
 *
 * @author zhu
 *
 * 2016年3月28日 下午6:15:00
 *
 */
public class ButtonEditText extends LinearLayout{
	private EditText editText;
	private Button button;



	public EditText getEditText() {
		return editText;
	}

	public Button getButton() {
		return button;
	}

	public ButtonEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ButtonEditText(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.buttom_edittext, this);
		editText = (EditText) view.findViewById(R.id.editText);
		button = (Button) view.findViewById(R.id.button);
	}


}
