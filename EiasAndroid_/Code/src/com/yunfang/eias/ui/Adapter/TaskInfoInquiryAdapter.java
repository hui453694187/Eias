/**
 * 
 */
package com.yunfang.eias.ui.Adapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.framework.utils.DateTimeUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Administrator
 * 
 */
public class TaskInfoInquiryAdapter extends BaseAdapter {

	/**
	 * 布局
	 */
	private LayoutInflater inflater;
	
	private RelativeLayout item_view_layout;

	/**
	 * 小区名称单选控件
	 */
	private RadioButton rdo_taskinfo;
	
	/**
	 * 小区名称
	 */
	private TextView txt_residentialareaName;

	/**
	 * 任务地址
	 */
	private TextView txt_taskinfo_addressandnumber;

	/**
	 * 收费
	 */
	private TextView txt_charge;

	/**
	 * 任务时间
	 */
	private TextView txt_time;

	/**
	 * 选中的位置
	 * */
	@SuppressWarnings("unused")
	private int selectedPosition = -1;

	/**
	 * 分类项
	 */
	private ArrayList<TaskInfo> taskInfos;

	/**
	 * 选中的分类项
	 * 
	 */
	public TaskInfo selectTaskinfo;

	/**
	 * 保存中的上次选中的分类项单选按钮
	 */
	private RadioButton rdo_lastCheckedTaskinfo;

	/**
	 * 单选框与其位置的字典集合
	 */
	private HashMap<RadioButton, Integer> map_rdocontrolToPosition = new HashMap<RadioButton, Integer>();

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public TaskInfoInquiryAdapter(Context context) {
		super();
		taskInfos = new ArrayList<TaskInfo>();
		inflater = LayoutInflater.from(context);
	}

	/**
	 * 刷新
	 * 
	 * @param taskInfos
	 */
	public void refersh(ArrayList<TaskInfo> taskInfos) {
		if (taskInfos != null && !taskInfos.isEmpty()) {
			this.taskInfos = taskInfos;
		}
		this.notifyDataSetChanged();
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return taskInfos.size();
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int index) {
		if (taskInfos != null && !taskInfos.isEmpty()) {
			return taskInfos.get(index);
		}
		return null;
	}

	/**
	 * 取得选中的任务分类项
	 * 
	 * @return
	 */
	public TaskInfo getCheckTaskInfo() {
		return selectTaskinfo;
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
		final TaskInfo model = taskInfos.get(position);
		if (model != null) {
			convertView = inflater.inflate(R.layout.task_info_inquiry_item,
					null);
			// 获取radio控件对象并添加点击事件
			rdo_taskinfo = (RadioButton) convertView
					.findViewById(R.id.rdo_taskinfo_residentialarea);
			rdo_taskinfo.setOnClickListener(rdoClickLister);
			// 获取listview的行对象（即layout）并添加点击事件
			item_view_layout = (RelativeLayout)convertView
					.findViewById(R.id.item_view_layout);
			item_view_layout.setOnClickListener(rdoClickLister);
			// 小区名称显示控件
			txt_residentialareaName = (TextView) convertView
					.findViewById(R.id.txt_residentialareaName);
			// 地址显示控件
			txt_taskinfo_addressandnumber = (TextView) convertView
					.findViewById(R.id.txt_taskinfo_addressandnumber);
			// 收费
			txt_charge = (TextView) convertView.findViewById(R.id.txt_charge);
			// 时间
			txt_time = (TextView) convertView.findViewById(R.id.txt_time);
		}
		txt_taskinfo_addressandnumber.setText("[" + model.TaskNum + "]"+model.TargetAddress);
		txt_residentialareaName.setText(model.ResidentialArea);
		if (model.Fee != null && model.Fee != "") {
			txt_charge.setText(model.Fee + "元");
		}else{
			txt_charge.setText( "未设置");
		}
		String receiveDate = model.ReceiveDate;
		if (receiveDate.length() >0 && !receiveDate.equals(EIASApplication.DefaultNullString)) {
			txt_time.setText(DateTimeUtil.converTime(model.ReceiveDate));
		}
		// 用于存储任务编号，并在点击事件中使用
		rdo_taskinfo.setTag(position);
		map_rdocontrolToPosition.put(rdo_taskinfo, position);
		return convertView;
	}

	/**
	 * 单选框选中事件
	 */
	@SuppressWarnings("rawtypes")
	private OnClickListener rdoClickLister = new OnClickListener() {
		@Override
		public void onClick(View v) {			
			// 将上次选中单选框的选中状态设置为false
			if (rdo_lastCheckedTaskinfo != null
					&& rdo_lastCheckedTaskinfo != (RadioButton) (v.findViewById(R.id.rdo_taskinfo_residentialarea))) {
				rdo_lastCheckedTaskinfo.setChecked(false);
			}
			// 若用户点击的是layout则在layout中找到radio控件
			// 若用户点击的是radio控件则直接取得该控件
			if(v.findViewById(R.id.rdo_taskinfo_residentialarea)!=null){
				rdo_lastCheckedTaskinfo = (RadioButton) (v.findViewById(R.id.rdo_taskinfo_residentialarea));
			}else{
				rdo_lastCheckedTaskinfo = (RadioButton) (v);
			}
			// 根据tag取得当前点击的任务项
			Iterator iter = map_rdocontrolToPosition.keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				Object val = map_rdocontrolToPosition.get(key);
				int index = Integer.parseInt(String.valueOf(val));
				if (Integer.parseInt(String.valueOf(rdo_lastCheckedTaskinfo.getTag())) == index) {
					selectTaskinfo = taskInfos.get(index);
				}
			}
			rdo_lastCheckedTaskinfo.setChecked(true);
		}
	};
}
