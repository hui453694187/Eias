package com.yunfang.eias.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.ui.ShowMediaListFragment;
import com.yunfang.eias.ui.TaskInfoActivity;

public class FloatWindowSmallView extends LinearLayout {

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
	 * 记录系统状态栏的高度 
	 */
	private static int statusBarHeight;

	/** 
	 * 用于更新小悬浮窗的位置 
	 */
	private WindowManager windowManager;

	/** 
	 * 小悬浮窗的参数 
	 */
	private WindowManager.LayoutParams mParams;

	/** 
	 * 
	 * 记录当前手指位置在屏幕上的移动的横坐标值 
	 */
	private float xMoveInScreen;

	/** 
	 * 记录当前手指位置在屏幕上的移动的纵坐标值 
	 */
	private float yMoveInScreen;

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

	private Activity context;

	private TaskInfoActivity activity;

	private ShowMediaListFragment fragment;

	public FloatWindowSmallView(Activity context, ShowMediaListFragment fragment) {
		super(context);
		this.context = context;
		this.fragment = fragment;

		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
		small_window_layout = findViewById(R.id.small_window_layout);
		viewWidth = small_window_layout.getLayoutParams().width;
		viewHeight = small_window_layout.getLayoutParams().height;
		TextView percentView = (TextView) findViewById(R.id.percent);
	}

	/**
	 * 控件在屏幕的绝对位置
	 */
	private float xViewInScreen;
	private float yViewInScreen;

	private View small_window_layout;

	/**
	 * 移动超过多少像素的时候算作移动
	 */
	private int isCanslide = 50;

	private float yOldInScreen;

	private float xOldInScreen;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度  
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - getStatusBarHeight();

			xViewInScreen = xDownInScreen - xInView;
			yViewInScreen = yDownInScreen - yInView;

			xOldInScreen = event.getRawX();
			yOldInScreen = event.getRawY() - getStatusBarHeight();

			Log.i(TAG, "ACTION_DOWN");

			Log.i(TAG, ", getX=" + mParams.x + ", getLeft=" + mParams.y);
			Log.i(TAG, ", xViewInScreen=" + xViewInScreen + ", yViewInScreen=" + yViewInScreen);
			break;
		case MotionEvent.ACTION_MOVE:
			float xMoveInScreen = event.getRawX();
			float yMoveInScreen = event.getRawY() - getStatusBarHeight();

			float xMove = xMoveInScreen - xDownInScreen;
			float yMove = yMoveInScreen - yDownInScreen;

			float xNowInScreen = mParams.x + xMove;
			float yNowInScreen = mParams.y + yMove;
			//			
			mParams.x = (int) (xNowInScreen);
			mParams.y = (int) (yNowInScreen);

			//更新目标现在的坐标
			xDownInScreen = xMoveInScreen;
			yDownInScreen = yMoveInScreen;

			windowManager.updateViewLayout(this, mParams);

			break;
		case MotionEvent.ACTION_UP:
			//移动少于超过规定的像素，才视为触发了单击事件。  
			if (Math.abs(event.getRawX() - xOldInScreen) < isCanslide && Math.abs(event.getRawY() - yOldInScreen) < isCanslide) {
				Log.i(TAG, "-默认点击事件----" + (event.getRawX() - xOldInScreen) + " " + (event.getRawY() - yOldInScreen));
				fragment.actionClickTakePicture();
			}
			break;
		default:
			break;
		}
		return true;
	}

	public void setOnClickListener(OnClickListener l) {
		Log.i(TAG, "------------点击事件------------");
	}

	public interface OnClickListener {

		void onClick(View v);
	}

	/** 
	 * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。 
	 *  
	 * @param params 
	 *            小悬浮窗的参数 
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/** 
	 * 更新小悬浮窗在屏幕中的位置。 
	 */
	private void updateViewPosition() {
		//		mParams.x = (int) (xInScreen - xInView);
		//		mParams.y = (int) (yInScreen - yInView);
		mParams.x = (int) (1080 / 2);
		mParams.y = (int) (1920 / 2);
		windowManager.updateViewLayout(this, mParams);
	}

	/** 
	 * 用于获取状态栏的高度。 
	 *  
	 * @return 返回状态栏高度的像素值。 
	 */
	private int getStatusBarHeight() {
		return 0;
		//        if (statusBarHeight == 0) {  
		//            try {  
		//                Class<?> c = Class.forName("com.android.internal.R$dimen");  
		//                Object o = c.newInstance();  
		//                Field field = c.getField("status_bar_height");  
		//                int x = (Integer) field.get(o);  
		//                statusBarHeight = getResources().getDimensionPixelSize(x);  
		//            } catch (Exception e) {  
		//                e.printStackTrace();  
		//            }  
		//        }  
		//        return statusBarHeight;  
	}
}