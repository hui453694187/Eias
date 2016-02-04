/**
 * 
 */
package com.yunfang.eias.ui.Adapter;

import java.util.ArrayList;

import com.yunfang.eias.R;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Administrator
 * 
 */
public class TaskUnWriteAdapter extends BaseAdapter {

	/**
	 * 布局
	 */
	private LayoutInflater inflater;

	/**
	 * 子类项个数
	 */
	private TextView txt_number;

	/**
	 * 分类项名称
	 */
	private TextView txt_name;
	
	/**
	 * 子项名称集合
	 */
	private TextView txt_itemsname;

	/**
	 * 分类项
	 */
	private ArrayList<TaskCategoryInfo> datas;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public TaskUnWriteAdapter(Context context, ArrayList<TaskCategoryInfo> logs) {
		super();
		datas = logs;
		inflater = LayoutInflater.from(context);
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return datas.size();
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int index) {
		if (datas != null && !datas.isEmpty()) {
			return datas.get(index);
		}
		return null;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TaskCategoryInfo log = datas.get(position);
		if (log != null) {
			convertView = inflater.inflate(
					R.layout.dialog_view_task_item_submit, null);
			txt_name = (TextView)convertView
					.findViewById(R.id.dialog_view_name);
			txt_number = (TextView) convertView
					.findViewById(R.id.dialog_view_task_itemscount);
			txt_itemsname = (TextView) convertView
					.findViewById(R.id.dialog_view_task_itemsname);
		}
		txt_name.setText(log.RemarkName);
		txt_number.setText("("+String.valueOf(log.Items.size())+"个)");
		String name = "";
		for(TaskDataItem item:log.Items){
			name+=item.Name+",";
		}
		if(log.Items.size()>0){
			name = name.substring(0, name.length()-1);
		}
		txt_itemsname.setText(name);
		return convertView;
	}
}
