package com.yunfang.eias.ui.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.dto.DialogTipsDTO;

/**
 * 错误提示适配类
 * 
 * @author 贺隽
 * 
 */
public class DialogResultListAdapter extends BaseAdapter {
	/**
	 * 需要适配的数据
	 */
	private ArrayList<DialogTipsDTO> mData = new ArrayList<DialogTipsDTO>();

	/**
	 * 需要适配的布局
	 */
	private LayoutInflater inflater;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public DialogResultListAdapter(Context context,ArrayList<DialogTipsDTO> data) {
		super();		
		inflater = LayoutInflater.from(context);
		mData = data;		
	}

	/**
	 * 刷新
	 * 
	 * @param taskInfos
	 */
	public void refersh(ArrayList<DialogTipsDTO> data) {
		this.mData = data;
		this.notifyDataSetChanged();
	}
	
	/**
	 * 获取文件名称数量 +1表示最后的添加新的文件
	 */
	@Override
	public int getCount() {
		if (mData != null && !mData.isEmpty()) {
			return mData.size();
		}
		return 0;
	}

	/**
	 * 获取数据的对象
	 */
	@Override
	public DialogTipsDTO getItem(int index) {
		if (mData != null && !mData.isEmpty()) {
			return mData.get(index);
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
	 * 获取需要显示的视图
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DialogTipsDTO dialog = mData.get(position);
		ViewHolder viewHodler;
		if (convertView == null) {
			viewHodler = new ViewHolder();
			convertView = inflater.inflate(R.layout.dialog_view_task_result_item, null);
			viewHodler.item_img = (ImageView) convertView
					.findViewById(R.id.dialog_view_task_result_item_img);
			viewHodler.item_concent = (TextView) convertView
					.findViewById(R.id.dialog_view_task_result_item_concent);

			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHolder) convertView.getTag();
		}
		setDisplayInfo(dialog, viewHodler);
		return convertView;
	}

	/**
	 * 设置显示信息
	 * @param log
	 * @param viewHodler
	 */
	private void setDisplayInfo(DialogTipsDTO data, ViewHolder viewHodler) {
		if (viewHodler != null) {
			switch (data.Category) {
			case Normal:
			case LocalPosition:
				viewHodler.item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.item_img.getResources(),
						R.drawable.dialog_result_concent));
				break;
			case PictureCollection:
				viewHodler.item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.item_img.getResources(),
						R.drawable.dialog_result_photo));
				break;
			case VideoCollection:
				viewHodler.item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.item_img.getResources(),
						R.drawable.dialog_result_video));
				break;
			case AudioCollection:
				viewHodler.item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.item_img.getResources(),
						R.drawable.dialog_result_audio));
				break;
			default:
				break;
			}
			viewHodler.item_concent.setText(data.Concent);
		}
	}

	/**
	 * 视图句柄
	 * */
	class ViewHolder {
		ImageView item_img;
		TextView item_concent;
	}
}
