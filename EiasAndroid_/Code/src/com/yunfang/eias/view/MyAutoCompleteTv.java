/**
 * 
 */
package com.yunfang.eias.view;

import com.yunfang.eias.R;
import com.yunfang.eias.utils.DensityHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AutoCompleteTextView;

/**
 * @author kevin
 *
 */
public class MyAutoCompleteTv extends AutoCompleteTextView implements TextWatcher{

	private Drawable delImg;
	
	private Context context;
	
	/** 图标大小 DP  */
	private final float iconSize=30f;
	
	/**
	 * @param context
	 */
	public MyAutoCompleteTv(Context context) {
		super(context);
		this.context=context;
		init();
	}
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyAutoCompleteTv(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
		init();
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public MyAutoCompleteTv(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init();
	}
	
	
	private void init(){
		delImg=getCompoundDrawables()[2];
		if(delImg==null){
			delImg=getResources().getDrawable(R.drawable.camera_btn_delete);
		}
		DensityHelper dh=DensityHelper.getInstance();
		int pxBound=dh.dip2px(this.context,iconSize);
		delImg.setBounds(0, 0, pxBound,pxBound);
		
		setClearIconVisible(true);
	}
	
	
	/** 
	 * @author kevin
	 * @date 2015-9-28 上午9:34:14
	 * @Description: 方法描述 
	 * @param b    是否显示图标
	 * @return void    返回类型 
	 */
	private void setClearIconVisible(boolean b) {
		Drawable right=b?delImg:null;
		this.setCompoundDrawables(null, null, right, null);
		
		
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP://手指抬起
			if(this.getCompoundDrawables()[2]!=null&&isClickIcon(event)){
				this.setText("");
			}
			break;
		case MotionEvent.ACTION_DOWN://按下
		default:
			break;
		}
		
		
		return super.onTouchEvent(event);
	}
	
	private boolean isClickIcon(MotionEvent event){
		boolean result=false;
		if(event.getX()>(this.getWidth()-this.getTotalPaddingRight())&&event.getX()<(this.getWidth()-this.getPaddingRight())){
			result=true;
		}
		return result;
	}
	
	
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		setClearIconVisible(text.length()>0);
	}
	
	
	

}
