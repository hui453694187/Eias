package com.yunfang.eias.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yunfang.eias.R;

public class NewTouchView extends LinearLayout {

	private View small_window_layout;

	private static final String TAG = "FloatWindowSmallView";
	/** 
	 * 记录小悬浮窗的宽度 
	 */
	public static int viewWidth;

	/** 
	 * 记录小悬浮窗的高度 
	 */
	public static int viewHeight;

	/** 
	 * 小悬浮窗的参数 
	 */
	private LinearLayout.LayoutParams mParams;

	/** 
	 * 记录手指按下时在屏幕上的横坐标的值 
	 */
	private float xDownInScreen;

	/** 
	 * 记录手指按下时在屏幕上的纵坐标的值 
	 */
	private float yDownInScreen;

	/** 
	 * 记录手指按下时在小悬浮窗的View上的横坐标的值 
	 */
	private float xInView;

	/** 
	 * 记录手指按下时在小悬浮窗的View上的纵坐标的值 
	 */
	private float yInView;
	/**
	 * 移动小于多少像素的时候算作点击事件
	 */
	private int isCanslide = 50;

	private float yOldInScreen;

	private float xOldInScreen;
	/**
	 * 控件在屏幕的绝对位置
	 */
	private float xViewInScreen;
	private float yViewInScreen;
	private View view1110;
	private OnNewClickListener mOnNewClick;
	/**
	 * 屏幕宽度
	 */
	private int screenW;
	/**
	 * 屏幕高度
	 */
	private int screenH;
	


	public NewTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenW = wm.getDefaultDisplay().getWidth();
		screenH = wm.getDefaultDisplay().getHeight();
//		Log.i(TAG, "screenW = " + screenW + " screenH = " + screenH);
		
		
		view1110 = LayoutInflater.from(context).inflate(R.layout.float_window_small, this);

		small_window_layout = findViewById(R.id.small_window_layout);
		
		viewWidth = small_window_layout.getLayoutParams().width;
		viewHeight = small_window_layout.getLayoutParams().height;
		
		
		MarginLayoutParams margin = new MarginLayoutParams(small_window_layout.getLayoutParams());
		mParams = new LinearLayout.LayoutParams(margin);
		
		
	}

	public NewTouchView(Context context) {
		super(context);
		initView(context);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度  
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY();

			xViewInScreen = xDownInScreen - xInView;
			yViewInScreen = yDownInScreen - yInView;

			xOldInScreen = event.getRawX();
			yOldInScreen = event.getRawY();

			
			break;
		case MotionEvent.ACTION_MOVE:
			float xMoveInScreen = event.getRawX();
			float yMoveInScreen = event.getRawY();
		

			float xMove = xMoveInScreen - xDownInScreen;
			float yMove = yMoveInScreen - yDownInScreen;

			float xNowInScreen = mParams.leftMargin + xMove;
			float yNowInScreen = mParams.topMargin + yMove;
			//			//			
			//控件滑动超出屏幕范围的处理
			xNowInScreen = (xNowInScreen < 0) ? 0 : xNowInScreen;
			xNowInScreen = (xNowInScreen > screenW - viewWidth) ? screenW - viewWidth : xNowInScreen;

			yNowInScreen = (yNowInScreen < 0) ? 0 : yNowInScreen;
			yNowInScreen = (yNowInScreen > screenH - viewHeight) ? screenH - viewHeight : yNowInScreen;

//			Log.i(TAG, "xNowInScreen = " + xNowInScreen + " yNowInScreen = " + yNowInScreen);
			
			mParams.leftMargin = (int) (xNowInScreen);
			mParams.topMargin = (int) (yNowInScreen);
		
			//更新目标现在的坐标
			xDownInScreen = xMoveInScreen;
			yDownInScreen = yMoveInScreen;

			//			
			//			mParams.setMargins((int)event.getRawX(),(int)event.getRawY(), 
			//					(int)event.getRawX() + viewWidth, (int)event.getRawY() +viewHeight); 
			small_window_layout.setLayoutParams(mParams);

			break;
		case MotionEvent.ACTION_UP:
			//移动少于超过规定的像素，才视为触发了单击事件。  
			if (Math.abs(event.getRawX() - xOldInScreen) < isCanslide && Math.abs(event.getRawY() - yOldInScreen) < isCanslide) {
//				Log.i(TAG, "-默认点击事件----" + (event.getRawX() - xOldInScreen) + " " + (event.getRawY() - yOldInScreen));
				mOnNewClick.OnNewClick();
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 控件的点击时间
	 *
	 * @author zhu
	 *
	 * 2016年3月24日 上午11:20:09
	 *
	 */
	public void setOnNewClickListener(OnNewClickListener onNewClick) {
		mOnNewClick = onNewClick;
	}

	/**
	 * 控件的点击时间
	 *
	 * @author zhu
	 *
	 * 2016年3月24日 上午11:20:09
	 *
	 */
	public interface OnNewClickListener {
		public void OnNewClick();
	}


	/**
	 * 设置控件能滑动的屏幕高度
	 * @param screenH
	 */
	public void setScreenH(int screenH) {
		this.screenH = screenH;
	}
}
