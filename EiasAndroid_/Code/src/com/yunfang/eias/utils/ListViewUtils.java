package com.yunfang.eias.utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 
 *
 * @author zhu
 *
 * 2016年3月22日 上午10:09:21
 *
 */
public class ListViewUtils {

	private static final String TAG = "ListViewUtils";

	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter  
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目  
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高  
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度  
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度  
		// params.height最后得到整个ListView完整显示需要的高度  
		listView.setLayoutParams(params);
	}

	@SuppressLint("NewApi")
	public void setGridViewHeightBasedOnChildren(GridView gridView, int numColumns, View parent) {
		// 获取ListView对应的Adapter  
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int len = listAdapter.getCount();
		Log.i(TAG, "numColumns = " + numColumns + " len = " + len);

		//如果GridView 有多列,那么只统计第一列的高度
		for (int i = 0; i < len; i += numColumns) { // listAdapter.getCount()返回数据项的数目  
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0); // 计算子项View 的宽高  
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度 
//			totalHeight += 12; // 统计所有子项的总高度 

			Log.i(TAG, "i = " + i + "gridView.getHeight(); = " + listItem.getHeight() + " listItem.getMeasuredHeight() = " + listItem.getMeasuredHeight());

		}
		
		Log.i(TAG, "totalHeight = " + totalHeight);
		ViewGroup.LayoutParams params = parent.getLayoutParams();
		params.height = totalHeight;
		// listView.getDividerHeight()获取子项间分隔符占用的高度  
		// params.height最后得到整个ListView完整显示需要的高度  
		parent.setLayoutParams(params);
	}
	
	@SuppressLint("NewApi")
	public void setGridViewOneHeight(GridView gridView, int numColumns, View parent) {
		// 获取ListView对应的Adapter  
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		
		int totalHeight = 0;
		int len = listAdapter.getCount();
		Log.i(TAG, "numColumns = " + numColumns + " len = " + len);
		if (len <= 0) {
			return;
		}
		View listItem = listAdapter.getView(0, null, gridView);
		listItem.measure(0, 0); // 计算子项View 的宽高  
		totalHeight = listItem.getMeasuredHeight(); // 统计所有子项的总高度 
		
		Log.i(TAG, "totalHeight = " + totalHeight);
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight;
		// listView.getDividerHeight()获取子项间分隔符占用的高度  
		// params.height最后得到整个ListView完整显示需要的高度  
		gridView.setLayoutParams(params);
	}
}
