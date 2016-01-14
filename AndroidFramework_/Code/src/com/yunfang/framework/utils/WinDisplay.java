package com.yunfang.framework.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 获取屏幕信息
 * 
 * @author gorson
 * 
 */
@SuppressWarnings("unused")
public class WinDisplay {

	public static Point getWidthAndHeight(Context context) {
		Point point = new Point();
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float density = metrics.density;
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		point.x = width;
		point.y = height;
		return point;
	}

}
