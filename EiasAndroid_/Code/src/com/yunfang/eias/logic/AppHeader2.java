package com.yunfang.eias.logic;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunfang.eias.R;

/**
 * 系统的主标题信息
 * 
 * @author 贺隽
 * 
 */

public class AppHeader2 {

	// {{ 控件
	
	/**
	 * WIFI 信号
	 */
	private Button btn_menu;

	/**
	 * 2G 信号
	 */
	private Button btn_menu_add;

	/**
	 * 3G 信号
	 */
	private Button btn_previous_Category;

	/**
	 * 4G 信号
	 */
	private Button btn_next_Category;
	
	/**
	 * 4G 信号
	 */
	private Button list_reload;

	/**
	 * 菜单按钮
	 */
	private TextView home_top_title;

	// }}

	/**
	 * 实现软件的头部控件的事件与功能
	 * 
	 * @param context
	 *            :当前上下文
	 * @param loadingWorker
	 *            :loading框
	 */
	public AppHeader2(Context context, int viewID) {
		RelativeLayout titleView = (RelativeLayout) ((Activity) context).findViewById(viewID);
		btn_menu = (Button) titleView.findViewById(R.id.btn_menu);
		btn_menu_add = (Button) titleView.findViewById(R.id.btn_menu_add);
		btn_previous_Category = (Button) titleView.findViewById(R.id.btn_previous_Category);
		btn_next_Category = (Button) titleView.findViewById(R.id.btn_next_Category);
		list_reload = (Button) titleView.findViewById(R.id.list_reload);
		home_top_title = (TextView) titleView.findViewById(R.id.home_top_title);
	}
	
	/**
	 * 
	 * @param click
	 */
	public void setCategoriesListener(OnClickListener click){
		btn_menu.setOnClickListener(click);
	}
	
	/**
	 * 
	 * @param click
	 */
	public void setCategoryInsertListener(OnClickListener click){
		btn_menu_add.setOnClickListener(click);
	}
	
	/**
	 * 
	 * @param click
	 */
	public void setCategoryNextListener(OnClickListener click){
		btn_next_Category.setOnClickListener(click);
	}
	
	/**
	 * 
	 * @param click
	 */
	public void setCategoryPreviousListener(OnClickListener click){
		btn_previous_Category.setOnClickListener(click);
	}
	
	/**
	 * 
	 * @param click
	 */
	public void setCategoryReloadListener(OnClickListener click){
		list_reload.setOnClickListener(click);
	}

	/**
	 *  
	 */
	public void setTitle(String title) {
		home_top_title.setText(title);
	}

	/**
	 *  
	 */
	public void visTitleView(Boolean vis) {
		if (vis) {
			home_top_title.setVisibility(View.VISIBLE);
		} else {
			home_top_title.setVisibility(View.GONE);
		}
	}

	/**
	 *  
	 */
	public void visReloadView(Boolean vis) {
		if (vis) {
			list_reload.setVisibility(View.VISIBLE);
		} else {
			list_reload.setVisibility(View.GONE);
		}
	}

	/**
	 *  
	 */
	public void visAddView(Boolean vis) {
		if (vis) {
			btn_menu_add.setVisibility(View.VISIBLE);
		} else {
			btn_menu_add.setVisibility(View.GONE);
		}
	}
	
	/**
	 *  
	 * @param vis
	 */
	public void visMenuView(Boolean vis){
		if(vis){
			btn_menu.setVisibility(View.VISIBLE);
		}else{
			btn_menu.setVisibility(View.GONE);
		}
	}

	/** 
	 * @param vis
	 */
	public void visNextView(Boolean vis){
		if(vis){
			btn_next_Category.setVisibility(View.VISIBLE);
		}else{
			btn_next_Category.setVisibility(View.GONE);
		}
	}
	
	/**
	 *  
	 * @param vis
	 */
	public void visPreviousView(Boolean vis){
		if(vis){
			btn_previous_Category.setVisibility(View.VISIBLE);
		}else{
			btn_previous_Category.setVisibility(View.GONE);
		}
	}
	// }}
}
