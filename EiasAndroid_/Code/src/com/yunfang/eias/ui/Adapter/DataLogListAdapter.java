package com.yunfang.eias.ui.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.iiseeuu.asyncimage.image.ChainImageProcessor;
import com.iiseeuu.asyncimage.image.ImageProcessor;
import com.iiseeuu.asyncimage.image.MaskImageProcessor;
import com.iiseeuu.asyncimage.image.ScaleImageProcessor;
import com.iiseeuu.asyncimage.widget.AsyncImageView;
import com.yunfang.eias.R;
import com.yunfang.eias.model.DataLog;
import com.yunfang.framework.utils.DateTimeUtil;

/**
 * 日志的数据适配类
 * 
 * @author 贺隽
 * 
 */
public class DataLogListAdapter extends BaseAdapter {
	/**
	 * 需要适配的数据
	 */
	private ArrayList<DataLog> datas;

	/**
	 * 需要适配的布局
	 */
	private LayoutInflater inflater;

	/**
	 * 异步加载图片
	 */
	private ImageProcessor mImageProcessor;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public DataLogListAdapter(Context context,ArrayList<DataLog> logs) {
		super();		
		inflater = LayoutInflater.from(context);
		prepareImageProcessor(context);
		datas = logs;
	}

	/**
	 * 异步加载图片
	 * 
	 * @param context
	 */
	private void prepareImageProcessor(Context context) {
		final int thumbnailSize = context.getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		final int thumbnailRadius = context.getResources()
				.getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

		mImageProcessor = new ChainImageProcessor(new ScaleImageProcessor(
				thumbnailSize, thumbnailSize, ScaleType.FIT_XY),
				new MaskImageProcessor(thumbnailRadius));
	}


	/**
	 * 获取文件名称数量 +1表示最后的添加新的文件
	 */
	@Override
	public int getCount() {
		return datas.size();
	}

	/**
	 * 获取数据的对象
	 */
	@Override
	public DataLog getItem(int index) {
		if (datas != null && !datas.isEmpty()) {
			return datas.get(index);
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
		final DataLog log = datas.get(position);
		ViewHolder viewHodler;
		if (convertView == null) {
			viewHodler = new ViewHolder();
			convertView = inflater.inflate(R.layout.log_listview_item, null);
			viewHodler.log_item_img = (AsyncImageView) convertView
					.findViewById(R.id.log_item_img);
			viewHodler.log_item_img.setImageProcessor(mImageProcessor);
			viewHodler.log_item_tips = (TextView) convertView
					.findViewById(R.id.log_item_tips);
			viewHodler.log_item_time = (TextView) convertView
					.findViewById(R.id.log_item_time);
	/*		viewHodler.log_item_concent2 = (TextView) convertView
					.findViewById(R.id.log_item_concent2);*/
			viewHodler.log_item_name = (TextView) convertView
					.findViewById(R.id.log_item_name);
			viewHodler.log_item_concent = (TextView) convertView
					.findViewById(R.id.log_item_concent);

			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHolder) convertView.getTag();
		}
		setDisplayInfo(log, viewHodler);
		return convertView;
	}

	/**
	 * 设置显示信息
	 * @param log
	 * @param viewHodler
	 */
	private void setDisplayInfo(final DataLog log, ViewHolder viewHodler) {
		if (viewHodler != null) {
			switch (log.OperatorType) {
			case TaskDataMatching:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskdatamatching));
				break;
			case TaskDataSynchronization:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskdatasynchronization));
				break;
			case DataDefineDataSynchronization:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_datadefinedatasynchronization));
				break;
			case UserLogin:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_userlogin));
				break;
			case UserLogout:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_userlogout));
				break;
			case TaskCreate:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskcreated));
				break;
			case TaskDelete:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskdeleted));
				break;
			case TaskReceive:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskreceive));
				break;
			case TaskFeeModify:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskfeemodify));
				break;
			case TaskDataCopy:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskdatacopy));
				break;
			case TaskDataCopyToNew:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskdatacopytonew));
				break;
			case TaskSubmit:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_tasksubmit));
				break;
			case TaskReSubmit:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskresubmit));
				break;
			case TaskRollback:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskrollback));
				break;
			case TaskPause:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskpause));
				break;
			case CategoryDefineCreated:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_categorydefinedatacreated));
				break;
			case CategoryDefineDataCopy:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskcategorydefinedatacopy));
				break;
			case CategoryDefineDataCopyToNew:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_categorydefinedatacopytonew	));
				break;
			case CategoryDefineDataReset:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_categorydefinedatareset));
				break;
			case CategoryDefineNameModified:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_categorydefinenamemodified));
				break;
			case CategoryDefineDeleted:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_categorydefinedeleted));
				break;				
			case FileUpload:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_fileupload));
				break;
			case TaskHttp:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_taskhttp));
				break;
			case VersionUpdate:
				viewHodler.log_item_img.setImageBitmap(BitmapFactory.decodeResource(viewHodler.log_item_img.getResources(),
						R.drawable.log_versionupdate));
				break;
			default:
				break;
			}
			viewHodler.log_item_tips.setText("");			
			viewHodler.log_item_time.setText(DateTimeUtil.converTime(log.CreatedDate));
			//viewHodler.log_item_concent2.setText("");
			viewHodler.log_item_name.setText(log.OperatorType.getName());
			viewHodler.log_item_concent.setText(log.LogContent);
		}
	}

	/**
	 * 视图句柄
	 * */
	class ViewHolder {
		AsyncImageView log_item_img;
		TextView log_item_tips;
		TextView log_item_time;
		TextView log_item_concent2;
		TextView log_item_name;
		TextView log_item_concent;
	}
}
