/**
 * 
 */
package com.yunfang.eias.beta.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.utils.DensityHelper;

/**
 * @author kevin
 * 
 */
@SuppressLint("ClickableViewAccessibility")
public class TimeEditText extends EditText {

	/** 刷新时间的图片，放在输入框的右侧 */
	private Drawable delImg;
	
	private Context context;

	private OnIconClickListener onIconClick;
	
	/** 图标大小 DP  */
	private final float iconSize=30f;
	/**
	 * @param context
	 */
	public TimeEditText(Context context) {
		super(context);
		this.context=context;
		init();

	}

	// 构造函数
	public TimeEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
		this.context=context;
		init();
	}

	// 构造函数
	public TimeEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
		init();
	}

	/**
	 * @author kevin
	 * @date 2015-9-23 下午4:08:40
	 * @Description: 初始化
	 */
	private void init() {
		// 获取输入框右边的图片
		delImg = getCompoundDrawables()[2];
		if (delImg == null) {
			delImg = getResources().getDrawable(R.drawable.log_title_refresh);
		}
		// 设置图片大边框
		DensityHelper dh=DensityHelper.getInstance();
		int pxBound=dh.dip2px(this.context,iconSize);
		delImg.setBounds(0, 0, pxBound,pxBound);//110

		setClearIconVisible(true);
		this.setKeyListener(null);
	}

	public void setOnIconClickListener(OnIconClickListener onIconClick) {
		this.onIconClick = onIconClick;
	}

	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? delImg : null;
		setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (getCompoundDrawables()[2] != null) {
				if (isClickIcon(event)) {
					if (this.onIconClick != null) {
						this.onIconClick.OnIconClick(delImg,this);
					}
					
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			if (getCompoundDrawables()[2] != null) {
				if (isClickIcon(event)) {
					
				}
			}
			break;
		default:

		}
		return super.onTouchEvent(event);
	}

	/***
	 * @author kevin
	 * @date 2015-9-23 下午5:25:17
	 * @Description: 是否点击在icon 上面
	 * @param event
	 * @return boolean    返回类型 
	 */
	private boolean isClickIcon(MotionEvent event) {
		boolean touchable = false;
		touchable = event.getX() > (getWidth() - getTotalPaddingRight())//
				&& (event.getX() < ((getWidth() - getPaddingRight())));
		return touchable;
	}

	public interface OnIconClickListener {

		public void OnIconClick(Drawable iconDrawable,EditText edt);
	}

}
