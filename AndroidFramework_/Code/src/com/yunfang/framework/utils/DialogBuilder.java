package com.yunfang.framework.utils;

import com.yunfang.framework.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**   
*    
* 项目名称：yunfang.eias   
* 类名称：DialogBuilder   
* 类描述：用于创建对话框的工具类   
* 创建人：lihc   
* 创建时间：2014-4-30 下午3:22:59   
* @version        
*/ 
public class DialogBuilder {
	//{{ 变量
	public static final int LEFT_BUTTON = 1;
	public static final int RIGHT_BUTTON = 2;

	/**
	 * 显示的窗口
	 */
	private Dialog dialog;

	/**
	 * 当前上面文
	 */
	private	Context context;	

	/**
	 * 菜单的名称列表
	 */
	private String[] menuNames;

	/**
	 * 
	 */
	private Button leftBtn;
	//}}

	/**
	 * 构造函数
	 * @param context
	 */
	public DialogBuilder(Context context) {
		this.context = context;

		// 初始化对话框
		Dialog dialog = new Dialog(context, R.style.MyDialog);
		dialog.setContentView(R.layout.dialog);
		this.dialog = dialog;
	}

	/**
	 * 设置对话框标题
	 */
	public DialogBuilder setTitle(Object titleText) {
		TextView titleView = getView(R.id.title);
		titleView.setText(parseParam(titleText));
		return this;
	}

	/**
	 * 设置中间显示的文字
	 */
	public DialogBuilder setMessage(Object messageText) {
		TextView messageView = getView(R.id.message);
		messageView.setText(parseParam(messageText));
		return this;
	}

	//{{setView
	/**
	 * 设置中间要显示的View
	 */
	public DialogBuilder setView(View view) {
		// 删除中间的TextView
		LinearLayout messageLayout = getView(R.id.message_layout);
		TextView messageView = getView(R.id.message);
		messageLayout.removeView(messageView);

		// 添加新的View
		messageLayout.addView(view);
		return this;
	}

	/**
	 * 设置中间要显示的布局
	 */
	public DialogBuilder setView(int layoutId) {
		// 删除中间的TextView
		LinearLayout messageLayout = getView(R.id.message_layout);
		TextView messageView = getView(R.id.message);
		messageLayout.removeView(messageView);

		// 添加新的View
		// root :　实例化完布局文件后，会将布局文件的根节点添加到root中
		LayoutInflater.from(context).inflate(layoutId, messageLayout);
		return this;
	}
	//}}


	/**
	 * 设置按钮文字和监听器
	 * @param leftBtnText 左边按钮文字
	 * @param rightBtnText 右边按钮文字
	 */
	public DialogBuilder setButtons(Object leftBtnText, Object rightBtnText,final DialogInterface.OnClickListener listener) {
		// 设置左边按钮文字
		Button left = getView(R.id.left);
		left.setText(parseParam(leftBtnText));
		left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 先关闭对话框
				dialog.dismiss();

				if (listener != null) {
					listener.onClick(dialog, LEFT_BUTTON);
				}
			}
		});
		this.leftBtn = left;

		// 设置右边按钮文字
		Button right = getView(R.id.right);
		right.setText(parseParam(rightBtnText));
		right.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 先关闭对话框
				dialog.dismiss();

				if (listener != null) {
					listener.onClick(dialog, RIGHT_BUTTON);
				}
			}
		});
		return this;
	}

	/**
	 * 根据控件id取得对应的控件
	 * @param resId 控件id
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int resId) {
		return (T) dialog.findViewById(resId);
	} 

	/**
	 * 返回一个对话框
	 */
	public Dialog create() {
		if (leftBtn == null) { // 说明不需要按钮，移除按钮所在的布局
			LinearLayout root = getView(R.id.root);
			View btnsLayout = getView(R.id.btns_layout);
			root.removeView(btnsLayout);
		}

		return dialog;
	}

	/**
	 * 解析参数
	 */
	private String parseParam(Object param) {
		if (param instanceof Integer) {
			return context.getString((Integer)param);
		} else if (param instanceof String) {
			return param.toString();
		}
		return null;
	}


	/**
	 * 获取所有菜单的名称
	 * @return
	 */
	public String[] getMenuNames(){
		return menuNames;
	}

	//{{ 获取指定的菜单值
	/**
	 * 获取指定位置的菜单名称
	 * @param index：指定菜单的位置，从0开始
	 * @return
	 */
	public String getMenuName(int index){
		String result = "";

		if(menuNames!= null && menuNames.length>0 && menuNames.length>index){
			result = menuNames[index];
		}

		return result;
	}

	/**
	 * 获取菜单的Index，从0开始
	 * @param name：菜单名称
	 * @return
	 */
	public int getMenuIndex(String name){
		int result = -1;

		if(menuNames!= null && menuNames.length>0){
			int index = -1;
			for(String menu : menuNames){
				index += 1;
				if(menu.equals(name)){
					result = index;
					break;
				}
			}
		}

		return result;
	}
	//}}
	
	//{{ setItems 设置对话框中显示的列表值
	/**
	 * 设置对话框中显示的列表值
	 * @param showMenuNames：要显示的字符串数据
	 * @param onClickListener：显示的菜单点击事件
	 */
	public void setItems(final String[] showMenuNames, final DialogInterface.OnClickListener onClickListener) {
		// 1.添加一个ListView
		setView(R.layout.dialog_long_press);

		// 2.显示数据
		ListView listView = (ListView)getView(R.id.dialog_listview);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.dialog_long_press_item, showMenuNames);
		listView.setAdapter(adapter);

		// 3.监听ListView的item点击
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 先关闭对话框
				dialog.dismiss();
				// Toast.makeText(context, strings[position], 0).show();

				if (onClickListener != null) {// 通知监听器ListView的Item被点击了
					onClickListener.onClick(dialog, position);
				}
			}
		});

		menuNames=showMenuNames;
	}

	/**
	 * 设置对话框中显示的列表值
	 * @param arrayId：字符串数组的资源ID值
	 * @param onClickListener：显示的菜单点击事件
	 */
	public void setItems(int arrayId, DialogInterface.OnClickListener onClickListener) {
		setItems(context.getResources().getStringArray(arrayId), onClickListener);
	}
	//}}
	
	//{{
	
}
