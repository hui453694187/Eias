/**
 * 
 */
package com.yunfang.eias.ui.Adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.dto.TaskCategoryInfoByTypeDTO;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.framework.utils.ListUtil;

/**
 * @author kevin 多级复选框列表 适配器
 */
@SuppressLint({ "InflateParams" })
public class MutilevelChoiceAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	/** 子项列表是否展开 */
	private SparseArray<Boolean> itemIsShow;

	private ArrayList<TaskCategoryInfoByTypeDTO> taskCategoryList;

	/** 分类项是否选中 */
	private SparseBooleanArray isSelects;
	/** 二级多选列表，选中状态 key 未分类项 index, value 未子项选中状态列表 */
	private SparseArray<SparseBooleanArray> isChildSeleccts;

	@SuppressLint("UseSparseArrays")
	public MutilevelChoiceAdapter(Context context,//
			ArrayList<TaskCategoryInfoByTypeDTO> taskCategoryList) {
		inflater = LayoutInflater.from(context);
		this.taskCategoryList = new ArrayList<TaskCategoryInfoByTypeDTO>();
		this.taskCategoryList.addAll(taskCategoryList);
		isSelects = new SparseBooleanArray(taskCategoryList.size());
		isChildSeleccts = new SparseArray<SparseBooleanArray>();
		itemIsShow = new SparseArray<Boolean>();
		initData();
	}

	public SparseBooleanArray getPrentSelect() {
		return isSelects;
	}

	public SparseArray<SparseBooleanArray> getChildSelect() {
		return isChildSeleccts;
	}

	private void initData() {
		int i = 0;
		for (TaskCategoryInfoByTypeDTO tcif : this.taskCategoryList) {
			isSelects.put(i, true);
			if (tcif.isMediaType()) {// 是否是照片类型
				SparseBooleanArray childSelects = new SparseBooleanArray();
				for (int j = 0; j < tcif.getTaskCatgoyInfo().Items.size(); j++) {// 媒体类型，选中状态，默认为false
					childSelects.put(j, true);
				}
				isChildSeleccts.put(i, childSelects);
			}
			i++;
		}
	}

	/** 改变所有复选框状态 */
	public void changStatus(boolean isSelect) {
		for (int i = 0; i < this.getCount(); i++) {
			isSelects.put(i, isSelect);
			SparseBooleanArray childSelect = isChildSeleccts.get(i, null);
			changChildSelect(childSelect,isSelect);
		}
		this.notifyDataSetChanged();
	}
	
	/***
	 * 改变子分类项选中状态
	 * @param childSelect
	 * @param isSelect
	 */
	private void changChildSelect(SparseBooleanArray childSelect,boolean isSelect){
		if (childSelect != null) {
			for (int j = 0; j < childSelect.size(); j++) {
				childSelect.put(j, isSelect);
			}
		}
	}

	@Override
	public int getCount() {
		return taskCategoryList.size();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		/* if (convertView == null) { 点击事件冲突，不用ViewHolder */
		viewHolder = new ViewHolder();
		convertView = inflater.inflate(R.layout.mutilevel_choice_lv_item, null);
		viewHolder.remakNameTv = (TextView) convertView.findViewById(R.id.remak_name_tv);
		viewHolder.cb = (CheckBox) convertView.findViewById(R.id.item_checkBox);
		viewHolder.childLayout = (LinearLayout) convertView.findViewById(R.id.child_item_layout);
		viewHolder.drawImg = (ImageView) convertView.findViewById(R.id.drow_imgBut);
		// convertView.setTag(viewHolder);
		/*
		 * } else { viewHolder = (ViewHolder) convertView.getTag(); }
		 */
		TaskCategoryInfoByTypeDTO temp = taskCategoryList.get(position);
		TaskCategoryInfo taskCateInfo = temp.getTaskCatgoyInfo();
		String remakName = taskCateInfo.RemarkName;
		viewHolder.remakNameTv.setText(remakName);

		viewHolder.cb.setChecked(isSelects.get(position));
		// 记录这个CheckBox position
		viewHolder.cb.setTag(position);
		viewHolder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int cbPostion = (int) buttonView.getTag();
				isSelects.put(cbPostion, isChecked);
				SparseBooleanArray childSelect=null;
				childSelect=isChildSeleccts.get(cbPostion, null);
				if(childSelect!=null){
					changChildSelect(childSelect,isChecked);
					MutilevelChoiceAdapter.this.notifyDataSetChanged();
				}
			}
		});

		if (temp.isMediaType()) {// 是媒体类型分类项 有子列表
			int i = 0;//
			if (ListUtil.hasData(taskCateInfo.Items)) {// 是否有媒体子项
				for (TaskDataItem tdt : taskCateInfo.Items) {// 媒体类型，显示子项
					// 创建 子列表布局
					View childTemp = creatDataItemViwe(tdt, position, i);
					viewHolder.childLayout.addView(childTemp);
					i++;
				}
				// 子列表的显示和隐藏
				viewHolder.drawImg.setVisibility(View.VISIBLE);
				isShowOrHid(viewHolder.childLayout, viewHolder.drawImg, position);
				final int positionTemp = position;
				viewHolder.drawImg.setTag(viewHolder.childLayout);
				viewHolder.drawImg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						View cV = (View) v.getTag();
						boolean isShow = (boolean) cV.getTag();
						if (isShow) {// 隐藏
							cV.setVisibility(View.GONE);
							cV.setTag(false);
							v.setBackgroundResource(R.drawable.up);
						} else {// 显示
							cV.setVisibility(View.VISIBLE);
							cV.setTag(true);
							v.setBackgroundResource(R.drawable.dowm);
						}
						itemIsShow.put(positionTemp, !isShow);

					}
				});
			} else {
				viewHolder.drawImg.setVisibility(View.INVISIBLE);
			}
		} else {
			viewHolder.childLayout.setVisibility(View.GONE);
			viewHolder.drawImg.setVisibility(View.GONE);
		}

		return convertView;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-11-25 下午4:09:31
	 * @Description: 设置子列表显示还是隐藏
	 * @param childLayout
	 * @return void 返回类型
	 * @version V1.0
	 */
	private void isShowOrHid(LinearLayout childLayout, ImageView drawImage, int postion) {
		// 子列表的显示和隐藏
		boolean isShow = itemIsShow.get(postion, false);
		if (isShow) {
			childLayout.setVisibility(View.VISIBLE);
			drawImage.setBackgroundResource(R.drawable.dowm);
			childLayout.setTag(true);
		} else {
			childLayout.setVisibility(View.GONE);
			childLayout.setTag(false);
			drawImage.setBackgroundResource(R.drawable.up);
		}
	}

	private View creatDataItemViwe(TaskDataItem tdt, int position, int childIndex) {
		ChildViewHolder childView = new ChildViewHolder();
		View linearLayout = inflater.inflate(R.layout.mutilevel_choice_lv_item_child, null);
		childView.remakNameTv = (TextView) linearLayout.findViewById(R.id.remak_name);
		childView.cb = (CheckBox) linearLayout.findViewById(R.id.item_cB);
		linearLayout.setTag(childView);
		childView.remakNameTv.setText("- - - - " + tdt.Name);
		childView.cb.setTag(new int[] { position, childIndex });
		childView.cb.setChecked(isChildSeleccts.get(position).get(childIndex));

		childView.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int[] childIdx = (int[]) buttonView.getTag();
				isChildSeleccts.get(childIdx[0]).put(childIdx[1], isChecked);
			}
		});
		return linearLayout;
	}

	public class ViewHolder {
		private TextView remakNameTv;
		public CheckBox cb;
		private ImageView drawImg;
		private LinearLayout childLayout;
	}

	private class ChildViewHolder {
		private TextView remakNameTv;
		public CheckBox cb;
	}

}
