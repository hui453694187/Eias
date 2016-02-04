package com.yunfang.eias.ui.Adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfang.eias.R;

/**
 * @author kevin 分类项子项 数据适配器
 */
public class TypeItemGridViewAdapter extends BaseAdapter implements OnItemClickListener {

	/** 数据源 */
	private List<ItemType> itemTypeList;
	private LayoutInflater inflater;
	private Activity context;

	private final int select = R.drawable.redio_select;

	private final int unSelect = R.drawable.redio_un_select;

	/** 当前选中的子项索引 */
	private int currentSelectIndex = -1;

	/***
	 * 
	 * @author kevin
	 * @date 2015-11-13 下午4:34:20
	 * @Description: 重置数据源，并刷新界面 
	 * @param itemTypeList    
	 * @version V1.0
	 */
	public void setItemTypeList(List<ItemType> itemTypeList) {
		this.itemTypeList=itemTypeList;
		this.notifyDataSetChanged();
	}


	public TypeItemGridViewAdapter(Activity context) {
		this.context = context;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {

		return itemTypeList.size();
	}

	@Override
	public ItemType getItem(int position) {
		if (position < 0 || position >= itemTypeList.size()) {
			return null;
		}
		return itemTypeList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemType itenType = itemTypeList.get(position);
		ViewHolder childView = null;
		if (convertView == null) {
			childView = new ViewHolder();
			convertView = inflater.inflate(R.layout.type_item_grid_item, null);
			childView.radioImg = (ImageView) convertView.findViewById(R.id.radio_img);
			childView.typeTv = (TextView) convertView.findViewById(R.id.type_item_tv);
			convertView.setTag(childView);
		} else {
			childView = (ViewHolder) convertView.getTag();
		}

		// 是否当前选中的
		int backgroundId = currentSelectIndex == position ? select : unSelect;
		childView.radioImg.setBackgroundDrawable(context.getResources().getDrawable(backgroundId));
		// 当前子项名称， 和数量
		childView.typeTv.setText(itenType.itemName + " ( " + itenType.itemCount + " )");

		return convertView;
	}

	/**
	 * @return 当前选中的子项索引
	 */
	public int getCurrentSelectIndex() {
		return currentSelectIndex;
	}

	class ViewHolder {
		ImageView radioImg;
		TextView typeTv;
	}

	/***
	 * Item
	 * 
	 * @author kevin
	 */
	public class ItemType {
		/** 子项名 */
		private String itemName;
		/** 该项图片统计 */
		private int itemCount = 0;

		public void setItemCount(int itemCount) {
			this.itemCount = itemCount;
		}

		public void itemCountAdd() {
			this.itemCount++;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long id) {
		currentSelectIndex=currentSelectIndex==postion?-1:postion;
		this.notifyDataSetChanged();
	}

}
