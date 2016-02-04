package com.yunfang.eias.ui.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.model.Introduction;

/**
 * 功能介绍数据适配
 * @author 贺隽
 *
 */
public class IntroductionViewAdapter extends BaseAdapter
{
	/**
	 * 媒体文件列表
	 */
	private List<Introduction> datas;

	/**
	 * 所在界面
	 */
	protected LayoutInflater mInflater;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public IntroductionViewAdapter(Context context, List<Introduction> datas)
	{
		super();
		this.datas = datas;
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * 获取数量
	 */
	@Override
	public int getCount()
	{
		return datas.size();
	}

	/**
	 * 获取文件路径数量
	 */
	@Override
	public Object getItem(int index)
	{
		if (datas != null && !datas.isEmpty()) { return datas.get(index); }
		return null;
	}

	/**
	 * 获取当前索引
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/**
	 * 获取需要显示的视图
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		try {
			Introduction mInfo = datas.get(position);
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.introduction_item, null);
				viewHolder = new ViewHolder();
				viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.introduction_item_image);
				viewHolder.mTitle = (TextView) convertView.findViewById(R.id.introduction_item_title);		
				viewHolder.mConcent = (TextView) convertView.findViewById(R.id.introduction_item_content);					
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.mImageView.setImageResource(R.drawable.log_bg);
			}	
			viewHolder.mImageView.setImageBitmap(mInfo.ImageData);
			viewHolder.mConcent.setText(mInfo.Description);	
			if(mInfo.Title != null && mInfo.Title.length() > 0){
				viewHolder.mTitle.setText(mInfo.Title);	
				viewHolder.mTitle.setVisibility(View.VISIBLE);
			}else{
				viewHolder.mTitle.setVisibility(View.GONE);
			}			
		} catch (Exception e) {
			
		}
		return convertView;
	}

	/**
	 * 自定义视图
	 * @author 贺隽
	 *
	 */
	public static class ViewHolder{
		/**
		 * 标题 暂时没用到
		 */
		public TextView mTitle;
		/**
		 * 介绍信息
		 */
		public TextView mConcent;
		/**
		 * 预览图
		 */
		public ImageView mImageView;
	}
}
