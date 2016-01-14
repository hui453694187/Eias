package com.yunfang.framework.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.yunfang.framework.R;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**   
 *    
 * 项目名称：yunfang.eias   
 * 类名称：MutipleChoiceAdapter   
 * 类描述：多选对话框的ListView装配item的适配器   
 * 创建人：lihc   
 * 创建时间：2014-4-30 下午3:28:57   
 * @version        
 */ 
public class MutipleChoiceAdapter extends BaseAdapter {

	// 填充数据的list  
	private HashMap<String, String> list;  
	// 用来控制CheckBox的选中状况  
	private SparseBooleanArray isSelected;  
	// 用来导入布局  
	private LayoutInflater inflater;  

	public MutipleChoiceAdapter(HashMap<String, String> list, Context context) {
		this.list = list;  
		inflater = LayoutInflater.from(context);  
		isSelected = new SparseBooleanArray();  
		// 初始化数据  
		initData();  
	}

	// 初始化isSelected的数据  
	private void initData() {  
		for (int i = 0; i < list.size(); i++) {  
			getIsSelected().put(i, false);  
		}  
	}  

	@Override  
	public int getCount() {  
		return list.size();  
	}  

	@Override  
	public Entry<String, String> getItem(int position) { 
		Entry<String, String> result = null; 
		
		int i=0;
		Iterator<Entry<String, String>> iter = list.entrySet().iterator(); 
		while (iter.hasNext()) { 
			if(i == position){
				result = iter.next(); 
				break;	
			}
			iter.next(); 
			i+=1;
		} 
		
		return result;  
	}  

	@Override  
	public long getItemId(int position) {  
		return position;  
	}  

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {  
		ViewHolder holder = null;  
		if (convertView == null) {  
			// 获得ViewHolder对象  
			holder = new ViewHolder();  
			// 导入布局并赋值给convertview  
			convertView = inflater.inflate(R.layout.custom_mutiplechoice_view_list_item, null);  
			holder.tv = (TextView) convertView.findViewById(R.id.item_tv);  
			holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);  
			// 为view设置标签  
			convertView.setTag(holder);  
		} else {  
			// 取出holder  
			holder = (ViewHolder) convertView.getTag();  
		}  
		// 设置list中TextView的显示  
		holder.tv.setText(getItem(position).getValue());  
		// 根据isSelected来设置checkbox的选中状况  
		holder.cb.setChecked(getIsSelected().get(position));  
		return convertView;  
	}  

	public SparseBooleanArray getIsSelected() {  
		return isSelected;  
	}  

	public void setIsSelected(SparseBooleanArray isSelected) {  
		this.isSelected = isSelected;  
	}  

	public static class ViewHolder {  
		TextView tv;  
		public CheckBox cb;  
	}  
}
