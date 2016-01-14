package com.yunfang.framework.view;

import java.util.LinkedHashMap;

import com.yunfang.framework.R;
import com.yunfang.framework.view.MutipleChoiceAdapter.ViewHolder;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * 项目名称：yunfang.eias 类名称：CustomMultipleChoiceView 类描述： 自定义的带 全选/反选 功能的多选对话框
 * 创建人：lihc 创建时间：2014-4-30 下午3:24:05
 * 
 * @version
 */
public class CustomMultipleChoiceView extends LinearLayout {

	private MutipleChoiceAdapter mAdapter;
	private LinkedHashMap<String, String> mData;
	private TextView title;
	private ListView lv;
	private TextView selectall_txt;
	private onSelectedListener onSelectedListener;// 确定选择监听器
	private onCancelListener onCancelListener;// 取消选择监听器
	private boolean curWillCheckAll = false;// 当前点击按钮时是否将全选

	public CustomMultipleChoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CustomMultipleChoiceView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		/* 实例化各个控件 */
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.custom_mutiplechoice_view, null);
		lv = (ListView) view.findViewById(R.id.mutiplechoice_listview);
		Button bt_cancel = (Button) view
				.findViewById(R.id.mutiplechoice_cancel_btn);
		CheckBox bt_selectall = (CheckBox) view
				.findViewById(R.id.mutiplechoice_selectall_btn);
		bt_selectall.toggle();
		selectall_txt = (TextView) view.findViewById(R.id.selectall_item_tv);
		Button bt_ok = (Button) view.findViewById(R.id.mutiplechoice_ok_btn);
		title = (TextView) view.findViewById(R.id.mutiplechoice_title);

		if (curWillCheckAll) {
			selectall_txt.setText("全选");
		} else {
			selectall_txt.setText("反选");
		}

		// 全选按钮的回调接口
		bt_selectall.setOnClickListener(btnClickLister);
		bt_ok.setOnClickListener(btnClickLister);
		bt_cancel.setOnClickListener(btnClickLister);

		// 绑定listView的监听器
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
				ViewHolder holder = (ViewHolder) arg1.getTag();
				// 改变CheckBox的状态
				holder.cb.toggle();
				// 将CheckBox的选中状况记录下来
				mAdapter.getIsSelected().put(position, holder.cb.isChecked());
			}
		});
		addView(view);
	}

	public void setData(LinkedHashMap<String, String> data, boolean[] isSelected) {
		if (data == null) {
			throw new IllegalArgumentException("data is null");
		}
		this.mData = data;
		mAdapter = new MutipleChoiceAdapter(data, getContext());
		if (isSelected != null) {
			if (isSelected.length != data.size()) {
				throw new IllegalArgumentException(
						"data's length not equal the isSelected's length");
			} else {
				for (int i = 0; i < isSelected.length; i++) {
					mAdapter.getIsSelected().put(i, isSelected[i]);
				}
			}

		}
		// 绑定Adapter
		lv.setAdapter(mAdapter);
	}

	public void setTitle(String title) {
		if (this.title != null) {
			this.title.setText(title);
		}
	}

	// 确定
	public void setOnSelectedListener(onSelectedListener l) {
		this.onSelectedListener = l;
	}

	// 取消
	public void setOnCancelListener(onCancelListener l) {
		this.onCancelListener = l;
	}

	// 确定监听器接口
	public interface onSelectedListener {
		public void onSelected(SparseBooleanArray sparseBooleanArray);
	}

	// 取消监听器接口
	public interface onCancelListener {
		public void onCancel();
	}

	/**
	 * 全选
	 */
	public void selectAll() {
		if (mData != null) {
			for (int i = 0; i < mData.size(); i++) {
				mAdapter.getIsSelected().put(i, true);
			}
			// 刷新listview和TextView的显示
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 全不选
	 */
	public void deselectAll() {
		if (mData != null) {
			for (int i = 0; i < mData.size(); i++) {
				mAdapter.getIsSelected().put(i, false);
			}
			// 刷新listview和TextView的显示
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 反选
	 */
	public void reverseSelect() {
		if (mData != null) {
			for (int i = 0; i < mData.size(); i++) {
				mAdapter.getIsSelected().put(i,
						!mAdapter.getIsSelected().get(i));
			}
			// 刷新listview和TextView的显示
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 点击事件
	 */
	private OnClickListener btnClickLister = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// switch (v.getId()) {
			// case R.id.mutiplechoice_cancel_btn:
			// //取消选择的按钮
			// if(onSelectedListener != null && mAdapter != null){
			// onCancelListener.onCancel();
			// }
			// break;
			// case R.id.mutiplechoice_selectall_btn:
			// //全选/反选按钮
			// if(mData == null){
			// return;
			// }
			// if(curWillCheckAll){
			// selectAll();
			// }else{
			// deselectAll();
			// }
			// if(curWillCheckAll){
			// selectall_txt.setText("反选");
			// }else{
			// selectall_txt.setText("全选");
			// }
			// curWillCheckAll = !curWillCheckAll;
			// break;
			// case R.id.mutiplechoice_ok_btn:
			// //确定选择的按钮
			//
			// if(onSelectedListener != null && mAdapter != null){
			// onSelectedListener.onSelected(mAdapter.getIsSelected());
			// }
			// break;
			// default:
			// break;
			// }
			if (v.getId() == R.id.mutiplechoice_cancel_btn) {
				// 取消选择的按钮
				if (onSelectedListener != null && mAdapter != null) {
					onCancelListener.onCancel();
				}
			}
			if (v.getId() == R.id.mutiplechoice_selectall_btn) {
				// 全选/反选按钮
				if (mData == null) {
					return;
				}
				if (curWillCheckAll) {
					selectAll();
				} else {
					deselectAll();
				}
				if (curWillCheckAll) {
					selectall_txt.setText("反选");
				} else {
					selectall_txt.setText("全选");
				}
				curWillCheckAll = !curWillCheckAll;
			}
			if (v.getId() == R.id.mutiplechoice_ok_btn) {
				// 确定选择的按钮
				if (onSelectedListener != null && mAdapter != null) {
					onSelectedListener.onSelected(mAdapter.getIsSelected());
				}
			}
		}
	};
}
