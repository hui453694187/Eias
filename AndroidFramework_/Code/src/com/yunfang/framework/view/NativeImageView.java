package com.yunfang.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 显示图片的控件
 * @author 贺隽
 *
 */
public class NativeImageView extends ImageView {
	
	/**
	 * 图片测量监控
	 */
	private OnMeasureListener onMeasureListener;
	
	/**
	 * 设置测量监控
	 * @param onMeasureListener
	 */
	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}

	/**
	 * 构造函数
	 * @param context:当前上下文
	 * @param attrs:特性
	 */
	public NativeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 构造函数
	 * @param context:当前上下文
	 * @param attrs:特性
	 * @param defStyle:默认样式
	 */
	public NativeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 测量事件 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//将图片测量的大小回调到onMeasureSize()方法中
		if(onMeasureListener != null){
			onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	/**
	 * 测量接口
	 * @author 贺隽
	 *
	 */
	public interface OnMeasureListener{
		public void onMeasureSize(int width, int height);
	}
	
}
