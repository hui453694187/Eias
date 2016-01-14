package com.yunfang.framework.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
/**
 * popwindow的封装类
 * @author gorson
 *
 */
public class PopWinDialog {
	
	/**
	 * @param context
	 * @param viewId：布局视图的资源ID
	 * @return
	 */
	public static PopupWindow getPopWin(Context context,int viewId){
		Point point = WinDisplay.getWidthAndHeight(context);
		int x = point.x;
		int y = point.y;
		View view = LayoutInflater.from(context).inflate(viewId, null);
		PopupWindow mPopupWindow=new PopupWindow(view, x, y, true);
		mPopupWindow.setContentView(view);
		return mPopupWindow;
	}

}
