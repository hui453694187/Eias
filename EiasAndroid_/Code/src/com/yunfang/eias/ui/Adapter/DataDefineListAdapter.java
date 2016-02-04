/**
 * 
 */
package com.yunfang.eias.ui.Adapter;

import java.util.ArrayList;
import com.yunfang.eias.R;
import com.yunfang.eias.model.DataDefine;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author LHM
 * 勘察表数据适配类
 */
public class DataDefineListAdapter extends BaseAdapter{
	/**
	 * 需要适配的数据
	 */
	private ArrayList<DataDefine> dataDefines;
	
	/**
	 * 需要适配的布局
	 */
	private LayoutInflater inflater;
	
	/**
	 * 勘察表类型
	 */
	private TextView txt_name;
	
	/**
	 * 构造函数
	 */
	public DataDefineListAdapter(Context context){
		super();
		dataDefines = new ArrayList<DataDefine>();
		inflater = LayoutInflater.from(context);
	}
	

	/**
	 * 刷新
	 * 
	 * @param taskInfos
	 */
	public void refersh(ArrayList<DataDefine> dataDefines) {
		if (dataDefines != null) {
			this.dataDefines = dataDefines;
		}
		this.notifyDataSetChanged();
	}
	

	/* 
	 * 设置单个勘察表分类项
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		final DataDefine model = dataDefines.get(position);
		if (model != null) {
			convertView = inflater.inflate(R.layout.update_survey_list_item,
					null);
			//获取名字控件  同步【"+item.Name+"】勘察配置表
			txt_name = (TextView) convertView.findViewById(R.id.update_survey_name);
			txt_name.setTag(position);
		}
		txt_name.setText("同步【"+model.Name+"】勘察配置表");
		// TODO 自动生成的方法存根
		return convertView;
	}
	
	/* 
	 * 取得勘察表数量
	 */
	@Override
	public int getCount()
	{
		// TODO 自动生成的方法存根
		return dataDefines.size();
	}

	/* 
	 *  取得当前项
	 */
	@Override
	public Object getItem(int position)
	{
		// TODO 自动生成的方法存根
		return position;
	}

	/**
	 * 取得当前位置
	 */
	@Override
	public long getItemId(int position)
	{
		// TODO 自动生成的方法存根
		return position;
	}


}
