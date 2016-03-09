package com.yunfang.eias.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class DensityHelper {

    private static DensityHelper densityUtil;

    public static DensityHelper getInstance(){
        if(densityUtil==null){
            densityUtil=new DensityHelper();
        }
        return densityUtil;

    }
    
    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    
   

    /***
     * 单例
     */
    private DensityHelper(){
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	/**
	 * dp转px
	 * 
	 * @param context
	 * @param val
	 * @return
	 */
	public static int dp2px(Context context, float dpVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

    /**
     * sp转px
     * @param spVal
     * @return
     */
    public int sp2px(Context context,float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /***
     * px 转SP  TextView
     * @param pxVal
     * @return
     */
    public int px2sp(Context context,float pxVal){
        return (int)(pxVal/context.getResources().getDisplayMetrics().scaledDensity);
    }

    /***
     *  创建Pop对话框
     * @param context
     * @param layoutId  布局ID
     * @param backGroundId 背景资源ID
     * @param width 宽
     * @param height 高
     * @return
     */
    public PopupWindow createPopWindows(Context context
    		,int layoutId
            ,int backGroundId
            ,int width
            ,int height){

        LayoutInflater mInflater=LayoutInflater.from(context);
        View popView=mInflater.inflate(layoutId,null,false);
        PopupWindow pop=new PopupWindow(popView,width,height);
        pop.setBackgroundDrawable(context.getResources().getDrawable(backGroundId));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        return pop;
    }

}
