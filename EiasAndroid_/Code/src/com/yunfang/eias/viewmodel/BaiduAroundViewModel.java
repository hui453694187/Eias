package com.yunfang.eias.viewmodel;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.yunfang.eias.R;
import com.yunfang.eias.ui.BaiduAroundActivity;
/**
 * 百度获取周边的
 *
 * @author zhu
 *
 * 2016年3月30日 下午6:03:56
 *
 */
public class BaiduAroundViewModel {
	private BaiduAroundActivity activity;
	public PopupWindow popupWindow;

	public BaiduAroundViewModel(BaiduAroundActivity activity){
		this.activity = activity;
	}
	
	  /** 
     * 创建PopupWindow 
     */  
	public void initPopuptWindow() {  
        // TODO Auto-generated method stub  
        // 获取自定义布局文件activity_popupwindow_left.xml的视图  
        View popupWindow_view = activity.getLayoutInflater().inflate(R.layout.popu_map_tips, null,  
                false);  
        popupWindow = new PopupWindow(popupWindow_view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);  
        // 设置动画效果  
//        popupWindow.setAnimationStyle(R.style.AnimationFade);  
        // 点击其他地方消失  
        popupWindow_view.setOnTouchListener(new OnTouchListener() {  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // TODO Auto-generated method stub  
                if (popupWindow != null && popupWindow.isShowing()) {  
                    popupWindow.dismiss();  
                    popupWindow = null;  
                }  
                return false;  
            }  
        });  
    }  
    /*** 
     * 获取PopupWindow实例 
     */  
	public void getPopupWindow() {  
        if (null != popupWindow) {  
            popupWindow.dismiss();  
            return;  
        } else {  
            initPopuptWindow();  
        }  
    }  
}
