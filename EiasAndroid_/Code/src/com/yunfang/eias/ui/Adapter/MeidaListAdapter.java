package com.yunfang.eias.ui.Adapter;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.eias.model.MediaDataInfo;
import com.yunfang.eias.ui.ShowMediaListFragment;
import com.yunfang.eias.ui.TaskInfoActivity;
import com.yunfang.framework.view.album.FilterImageView;

/**
 * 媒体数据适配
 * 
 * @author 贺隽
 * 
 */
public class MeidaListAdapter extends BaseAdapter {
	/**
	 * 用来存储媒体文件的选中情况
	 */
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

	/**
	 * 所在界面
	 */
	protected LayoutInflater mInflater;

	/**
	 * 资源类型
	 */
	private CategoryType mType;

	private boolean visCheck = false;

	/**
	 * 界面
	 */
	private TaskInfoActivity mTaskInfoActivity;

	private ShowMediaListFragment fragment;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public MeidaListAdapter(ShowMediaListFragment fragment, Context context, CategoryType cType) {
		super();
		mInflater = LayoutInflater.from(context);
		mTaskInfoActivity = (TaskInfoActivity) context;
		mType = cType;
		this.fragment = fragment;

	}

	/**
	 * 获取数量
	 */
	@Override
	public int getCount() {
		return mTaskInfoActivity.meidaInfos.size();
	}

	/**
	 * 获取文件路径数量
	 */
	@Override
	public Object getItem(int index) {
		if (mTaskInfoActivity.meidaInfos != null && !mTaskInfoActivity.meidaInfos.isEmpty()) {
			return mTaskInfoActivity.meidaInfos.get(index);
		}
		return null;
	}

	/**
	 * 获取当前索引
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 显示或者隐藏勾选
	 * 
	 * @param vis
	 */
	public void visCheck(boolean vis) {
		visCheck = vis;
		notifyDataSetChanged();
	}

	/**
	 * 获取是否显示勾选框
	 * 
	 * @return
	 */
	public boolean getVisCheck() {
		return visCheck;
	}

	/**
	 * 获取需要显示的视图
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final MediaDataInfo mInfo = mTaskInfoActivity.meidaInfos.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.task_info_add_photo_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (FilterImageView) convertView.findViewById(R.id.photo_imageView);
			viewHolder.mImageText = (TextView) convertView.findViewById(R.id.photoName_txt);
			viewHolder.mAutoText = (AutoCompleteTextView) convertView.findViewById(R.id.media_auto);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.photo_checkBox);
			LayoutParams params = viewHolder.mImageView.getLayoutParams();
			params.height = (EIASApplication.deviceInfo.ScreenWeight - 40) / 4;
			params.width = (EIASApplication.deviceInfo.ScreenWeight - 40) / 4;
			viewHolder.mImageView.setLayoutParams(params);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.photo_select);
		}
		viewHolder.mAutoText.setAdapter(mTaskInfoActivity.viewModel.searchAdapter);

		if (position == 0) {
			if (mType == CategoryType.PictureCollection) {
				viewHolder.mImageText.setText(Html.fromHtml("单击拍照/长按选取"));
			} else {
				viewHolder.mImageText.setText("添加");
			}
			viewHolder.mImageView.setImageResource(R.drawable.photo_select);
			viewHolder.mCheckBox.setVisibility(View.GONE);
		} else {
			if (mInfo.Path != null && mInfo.file.exists()) {
				viewHolder.mImageView.setImageBitmap(mInfo.ThumbnailPhoto);
			} else {
				viewHolder.mImageView.setImageResource(R.drawable.ico_no_find);
			}

			if (mInfo.CategoryId > 0) {
				viewHolder.mAutoText.setTag(mInfo.ItemName);
				viewHolder.mImageText.setText(mInfo.ItemName);
			} else {
				viewHolder.mAutoText.setTag("");
				viewHolder.mImageText.setText("");
			}
			viewHolder.mImageText.setTag(viewHolder.mAutoText);
			// 如果在当前位置显示了AutoCompleteTextView 当前的ITEM 不会触发click事件 所以这里默认隐藏
			if (mType == CategoryType.PictureCollection) {
				viewHolder.mImageText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						v.setVisibility(View.GONE);

						AutoCompleteTextView txt = (AutoCompleteTextView) v.getTag();
						txt.setVisibility(View.VISIBLE);
						txt.showDropDown();
						txt.setFocusable(true);
					}
				});
			}
			if (visCheck) {
				viewHolder.mCheckBox.setVisibility(View.VISIBLE);
			} else {
				viewHolder.mCheckBox.setVisibility(View.GONE);
			}

			viewHolder.mCheckBox.setChecked(mInfo.check);
			viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					try {
						mTaskInfoActivity.meidaInfos.get(position).check = isChecked;
					} catch (IndexOutOfBoundsException e) {
						// DataLogOperator.taskHttp("MeidaListAdapter数组越界","");
						e.printStackTrace();
					}
				}
			});

			viewHolder.mAutoText.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						viewHolder.mImageText.setVisibility(View.VISIBLE);
						v.setVisibility(View.GONE);
					} else {
						viewHolder.mImageText.setVisibility(View.GONE);
						AutoCompleteTextView txt = (AutoCompleteTextView) v;
						txt.setVisibility(View.VISIBLE);
						txt.showDropDown();
					}
				}
			});

			// 设置选择类型
			viewHolder.mAutoText.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
					viewHolder.mAutoText.setVisibility(View.GONE);
					viewHolder.mImageText.setText(arg0.getItemAtPosition(arg2).toString());
					viewHolder.mImageText.setVisibility(View.VISIBLE);
					mTaskInfoActivity.saveTaskItemValue(mType, mInfo, arg0.getItemAtPosition(arg2).toString(), false);
					fragment.refreshItemTypeView();
				}
			});
		}
		return convertView;
	}

	/**
	 * 自定义视图
	 * 
	 * @author 贺隽
	 * 
	 */
	public static class ViewHolder {
		/**
		 * 文件名称
		 */
		public TextView mImageText;
		/**
		 * 预览图
		 */
		public FilterImageView mImageView;
		/**
		 * 选择框
		 */
		public CheckBox mCheckBox;
		/**
		 * 选择类型
		 */
		public AutoCompleteTextView mAutoText;
	}
}
