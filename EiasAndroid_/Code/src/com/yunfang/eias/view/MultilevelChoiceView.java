/**
 * 
 */
package com.yunfang.eias.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yunfang.eias.dto.TaskCategoryInfoByTypeDTO;
import com.yunfang.eias.ui.Adapter.MutilevelChoiceAdapter;
import com.yunfang.framework.R;

/**
 * @author kevin 二级， 多选按钮列表
 */
public class MultilevelChoiceView extends LinearLayout implements View.OnClickListener {

	/** 标题 */
	private TextView titleTv;
	/** 多选列表 */
	private ListView lv;
	/** 取消按钮 */
	private Button cancleBut;
	/** 确认按钮 */
	private Button comfiBut;
	/** 全选复选框 */
	private CheckBox selectAllCb;
	/** 全选 TextView */
	private TextView selectAllTv;
	/** 多选列表适配器 */
	private MutilevelChoiceAdapter mutilevelChoiceAdt;
	/** 按钮监听器 */
	private PopViewButListener popViewButListener;
	/** 是否全选 */
	private boolean isSelectAll = false;

	/**
	 * @param context
	 */
	public MultilevelChoiceView(Context context) {
		super(context);
		initView();
	}

	public MultilevelChoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	@SuppressLint("InflateParams")
	private void initView() {
		/* 实例化各个控件 */
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.custom_mutiplechoice_view, null);
		titleTv = (TextView) view.findViewById(R.id.mutiplechoice_title);
		lv = (ListView) view.findViewById(R.id.mutiplechoice_listview);
		comfiBut = (Button) view.findViewById(R.id.mutiplechoice_ok_btn);
		cancleBut = (Button) view.findViewById(R.id.mutiplechoice_cancel_btn);
		selectAllCb = (CheckBox) view.findViewById(R.id.mutiplechoice_selectall_btn);
		selectAllTv = (TextView) view.findViewById(R.id.selectall_item_tv);
		comfiBut.setOnClickListener(this);
		cancleBut.setOnClickListener(this);

		selectAllCb.toggle();

		if (isSelectAll) {
			selectAllTv.setText("全选");
		} else {
			selectAllTv.setText("取消全选");
		}

		selectAllCb.setOnClickListener(this);

		addView(view);

	}

	/** 全选 */
	private void selectAll() {
		mutilevelChoiceAdt.changStatus(true);
	}

	/** 取消全选不选 */
	private void deselectAll() {
		mutilevelChoiceAdt.changStatus(false);
	}

	public void setTitle(String title) {
		titleTv.setText(title);
	}

	public void setData(ArrayList<TaskCategoryInfoByTypeDTO> taskCategoryList) {
		mutilevelChoiceAdt = new MutilevelChoiceAdapter(this.getContext(), taskCategoryList);
		lv.setAdapter(mutilevelChoiceAdt);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mutiplechoice_ok_btn:// 确定
			this.popViewButListener.onOkButClickListener(//
					mutilevelChoiceAdt.getPrentSelect(),//
					mutilevelChoiceAdt.getChildSelect());
			break;
		case R.id.mutiplechoice_cancel_btn:// 取消
			this.popViewButListener.onCancelButClickListener();
			break;
		case R.id.mutiplechoice_selectall_btn:
			if (isSelectAll) {
				selectAll();
				selectAllTv.setText("取消全选");
			} else {
				deselectAll();
				selectAllTv.setText("全选");
			}
			isSelectAll = !isSelectAll;
			break;
		default:
			break;
		}

	}

	public void setPopViewButListener(PopViewButListener popViewButListener) {
		this.popViewButListener = popViewButListener;
	}

	public interface PopViewButListener {
		//确定按钮监听
		public void onOkButClickListener(SparseBooleanArray sba, SparseArray<SparseBooleanArray> sa);
		//取消按钮监听
		public void onCancelButClickListener();

	}
}
