package com.yunfang.eias.beta.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.utils.DensityHelper;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.utils.StringUtil;

/**
 * @author kevin
 * 
 */
public class MultiSelectAlbumActivity extends BaseWorkerActivity {

	/** 完成选择按钮 */
	private TextView confirmBtn;
	/** 切换相册按钮 */
	private Button selectAlumBtn;
	/** 相册显示控件 */
	private GridView albumGrideView;
	/** 选择相册列表 */
	private ListView albumLv;
	/** 预览图片按钮 */
	private TextView previewSelectTv;

	/** 当前显示的图片 */
	private FolderImage currentShowImage;
	/** 所有图片 */
	private FolderImage allImage;
	/** 选中图片 */
	private List<String> selectImgList;
	/** 相册列表 */
	private List<FolderImage> imgFloderList = new ArrayList<FolderImage>();

	private ContentResolver mContentResolver;
	/** 最大选择图片数 */
	private static final Integer MAX_SELECT = 9;
	/** 图片路径返回标记 */
	public static final String RESULT_IMG_PATH = "IMG_PATHS";

	/** 自身引用 */
	private MultiSelectAlbumActivity context;
	/** 图片girdView 适配器 */
	private GirdViewAdapter gridAdapter;
	private AlbumFolderAdapter albumFoldAdt;
	/** 图片加载类 */
	private ImageLoader imageLoader;
	/** imageLoader 参数配置 */
	private DisplayImageOptions option;

	private PopupWindow previewPopup;
	private PreviewPopHolder pvPopHolder;
	private PreviewVpgAdapter previewAdt;

	private Map<String, Integer> tempFloderMap = new HashMap<String, Integer>();
	private DensityHelper densityUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_multi_select_album);
		initImageLoader();
		mContentResolver = getContentResolver();
		initView();
		context = this;

		getAllAlbumPath();

	}

	/***
	 * 初始化图片加载类
	 */
	private void initImageLoader() {
		imageLoader = ImageLoader.getInstance();
		// 配置ImageLoader
		option = new DisplayImageOptions.Builder()//
				.showImageOnLoading(R.drawable.friends_sends_pictures_no)//
				.showImageForEmptyUri(R.drawable.friends_sends_pictures_no)//
				.showImageOnFail(R.drawable.friends_sends_pictures_no)//
				.cacheInMemory(true).cacheOnDisk(true)//
				.considerExifParams(true)//
				.imageScaleType(ImageScaleType.EXACTLY)//
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		selectImgList = new ArrayList<String>();
		allImage = new FolderImage();
		allImage.setDir("/所有图片");
		currentShowImage = allImage;// 默认显示所有图片
		imgFloderList.add(allImage);

		previewSelectTv = (TextView) this.findViewById(R.id.tv_preview_select);
		confirmBtn = (TextView) this.findViewById(R.id.tv_confirm);
		selectAlumBtn = (Button) this.findViewById(R.id.btn_select);
		albumGrideView = (GridView) this.findViewById(R.id.grid_album);
		albumLv = (ListView) this.findViewById(R.id.lv_album);

		gridAdapter = new GirdViewAdapter();
		albumGrideView.setAdapter(gridAdapter);

		albumFoldAdt = new AlbumFolderAdapter();
		albumLv.setAdapter(albumFoldAdt);

		ViewOnItemClick onIntemClick = new ViewOnItemClick();
		albumGrideView.setOnItemClickListener(onIntemClick);
		albumLv.setOnItemClickListener(onIntemClick);
	}

	/***
	 * item 点击事件
	 * 
	 * @author kevin
	 * 
	 */
	private class ViewOnItemClick implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			int i = parent.getId();
			switch (i) {
			case R.id.grid_album:// 图片gridView
				initPreviewPop(position, currentShowImage.imgList);
				break;
			case R.id.lv_album:// 相册文件夹列表
				FolderImage foldImg = imgFloderList.get(position);
				currentShowImage = foldImg;
				gridAdapter.notifyDataSetChanged();
				hideListAnimation();
				selectAlumBtn.setText(currentShowImage.getFloderName());
				break;
			default:
				break;
			}

		}
	}

	/***
	 * 弹出预览图片
	 */
	private void initPreviewPop(int position, List<ImageItem> showImgList) {
		View popView = null;
		if (previewPopup == null) {
			pvPopHolder = new PreviewPopHolder();
			densityUtil = DensityHelper.getInstance();
			int w = EIASApplication.deviceInfo.ScreenWeight;
			int h = EIASApplication.deviceInfo.ScreenHeight;
			previewPopup = densityUtil.createPopWindows(context, R.layout.popup_preview_pic, R.color.white, w, h);
			popView = initPopView();
			// 点击的图片是否是选中
			ImageItem img = currentShowImage.imgList.get(position);
			pvPopHolder.chackBnt.setSelected(selectImgList.contains(img.getPath()));

			previewAdt = new PreviewVpgAdapter(showImgList);
			pvPopHolder.previewVp.setAdapter(previewAdt);
			//
			popView.setTag(pvPopHolder);
		} else {
			// 刷新数据
			previewAdt.setCurrenImgList(showImgList);
			popView = previewPopup.getContentView();
			pvPopHolder = (PreviewPopHolder) popView.getTag();
		}
		setConfirmText(selectImgList.size() > 0, pvPopHolder.confirmTv);
		pvPopHolder.previewVp.setCurrentItem(position);
		int currentIndex = pvPopHolder.previewVp.getCurrentItem() + 1;
		pvPopHolder.countTv.setText(currentIndex + "/" + showImgList.size());
		previewPopup.showAtLocation((View) confirmBtn.getParent(), Gravity.CENTER, 0, 0);
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-9-30 上午11:13:22
	 * @Description: 初始化图片预览界面 ，监听控件事件。
	 * @return View 窗口视图
	 */
	private View initPopView() {
		View popView = previewPopup.getContentView();
		pvPopHolder.previewVp = (ViewPager) popView.findViewById(R.id.vpg_preview);
		pvPopHolder.backBnt = (Button) popView.findViewById(R.id.btn_back);
		pvPopHolder.chackBnt = (Button) popView.findViewById(R.id.btn_check);
		pvPopHolder.countTv = (TextView) popView.findViewById(R.id.tv_count_pic);
		pvPopHolder.confirmTv = (TextView) popView.findViewById(R.id.tv_confirm);

		PopViewListener popListener = new PopViewListener();
		previewPopup.setOnDismissListener(popListener);
		pvPopHolder.backBnt.setOnClickListener(popListener);
		pvPopHolder.chackBnt.setOnClickListener(popListener);
		pvPopHolder.previewVp.setOnPageChangeListener(popListener);
		return popView;
	}

	/***
	 * 弹出窗口，事件处理
	 * 
	 * @author kevin
	 */
	private class PopViewListener implements OnClickListener, OnDismissListener, OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			int count = pvPopHolder.previewVp.getAdapter().getCount();
			pvPopHolder.countTv.setText((position + 1) + "/" + count);
			// 当前显示的图片 path
			PreviewVpgAdapter pvAdt = (PreviewVpgAdapter) pvPopHolder.previewVp.getAdapter();
			String path = pvAdt.getItemByPostion(position).getPath();
			pvPopHolder.chackBnt.setSelected(selectImgList.contains(path));
		}

		@Override
		public void onDismiss() {
			setConfirmText(selectImgList.size() > 0, confirmBtn);
			gridAdapter.notifyDataSetChanged();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:
				previewPopup.dismiss();
				break;
			case R.id.btn_check:
				int currenSelect = pvPopHolder.previewVp.getCurrentItem();
				PreviewVpgAdapter pvAdt = (PreviewVpgAdapter) pvPopHolder.previewVp.getAdapter();
				ImageItem img = pvAdt.getItemByPostion(currenSelect);
				chackImg(pvPopHolder.chackBnt, img, pvPopHolder.confirmTv);
				break;
			default:
				break;
			}
		}

	}

	/***
	 * 
	 * @author kevin 图片预览适配器
	 */
	private class PreviewVpgAdapter extends PagerAdapter {

		private SparseArray<View> sparseList = new SparseArray<View>();

		private List<ImageItem> currenImgList = new ArrayList<ImageItem>();

		private int mChildCount;

		public PreviewVpgAdapter(List<ImageItem> pathList) {

			if (this.currenImgList != null) {
				this.currenImgList.clear();
			}
			// 不可以直接赋值，需要值传递
			this.currenImgList.addAll(pathList);
		}

		/***
		 * 刷新图片数据
		 * 
		 * @param pathList
		 */
		private void setCurrenImgList(List<ImageItem> pathList) {
			this.currenImgList.clear();
			/** 需要copy 一份新的list */
			this.currenImgList.addAll(pathList);
			this.notifyDataSetChanged();

		}

		@Override
		public void notifyDataSetChanged() {
			mChildCount = getCount();
			super.notifyDataSetChanged();
		}

		@Override
		public int getItemPosition(Object object) {
			if (mChildCount > 0) {
				mChildCount--;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}

		@Override
		public int getCount() {
			return currenImgList.size();
		}

		/***
		 * 获取制定下标的imageItem
		 * 
		 * @param postion
		 * @return
		 */
		public ImageItem getItemByPostion(int postion) {
			return currenImgList.get(postion);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageView img = (ImageView) View.inflate(context, R.layout.popup_preview_vpg_item, null);
			String path = currenImgList.get(position).getPath();
			imageLoader.displayImage("file://" + path, img, option);// 加载图片
			sparseList.put(position, img);
			((ViewPager) container).addView(img);
			return img;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(sparseList.get(position));
			sparseList.remove(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(sparseList.get(position));
			sparseList.remove(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	/***
	 * 预览图片界面控件
	 * 
	 * @author kevin
	 * 
	 */
	private class PreviewPopHolder {
		private ViewPager previewVp;
		private Button backBnt, chackBnt;
		private TextView countTv, confirmTv;
	}

	/***
	 * 返回事件
	 * 
	 * @param view
	 */
	public void onBack(View view) {
		this.finish();
	}

	/***
	 * 确定选择
	 * 
	 * @param view
	 */
	public void confirm(View view) {
		// 数据是使用Intent返回
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putStringArrayListExtra(RESULT_IMG_PATH, (ArrayList<String>) selectImgList);
		// 设置返回数据
		this.setResult(RESULT_OK, intent);
		// 关闭Activity
		this.finish();

	}

	/***
	 * 预览已选图片
	 * 
	 * @param view
	 */
	public void previewSelect(View view) {
		List<ImageItem> selectImg = new ArrayList<ImageItem>();
		for (String path : selectImgList) {
			selectImg.add(new ImageItem(path));
		}
		initPreviewPop(0, selectImg);
		pvPopHolder.chackBnt.setSelected(true);// 预览的第一项默认选中
	}

	/***
	 * 相册选择
	 * 
	 * @param view
	 */
	public void select(View view) {
		if (albumLv.getVisibility() == View.VISIBLE) {
			hideListAnimation();
		} else {
			albumLv.setVisibility(0);
			showListAnimation();
			albumFoldAdt.notifyDataSetChanged();
		}
	}

	/**
	 * 显示相册列表
	 */
	public void showListAnimation() {
		TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 1f, 1, 0f);
		ta.setDuration(200);
		albumLv.startAnimation(ta);
	}

	/**
	 * 隐藏相册列表
	 */
	public void hideListAnimation() {
		TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 0f, 1, 1f);
		ta.setDuration(200);
		albumLv.startAnimation(ta);
		ta.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束，隐藏相册文件将爱列表
				albumLv.setVisibility(View.GONE);
			}
		});
	}

	@Override
	protected void handleBackgroundMessage(Message msg) {

	}

	/***
	 * 获取所有的图片路径 装入集合中
	 */
	private void getAllAlbumPath() {
		Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns.DATA }, "", null, MediaStore.MediaColumns.DATE_ADDED
				+ " DESC");
		if (mCursor.moveToFirst()) {
			int _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
			do {
				// 获取图片路径
				String path = mCursor.getString(_date);
				// 所有图片的第一张
				if (StringUtil.IsNullOrEmpty(allImage.firstImg)) {
					allImage.firstImg = path;
				}
				allImage.imgList.add(new ImageItem(path));
				File parentFile = new File(path).getParentFile();
				if (parentFile == null) {
					continue;
				}
				// 图片所在文件夹绝对路径
				String floadePath = parentFile.getAbsolutePath();
				FolderImage tempFloder = null;
				if (!tempFloderMap.containsKey(floadePath)) {
					tempFloder = new FolderImage();// 创建一个相册文件夹
					tempFloder.setDir(floadePath);
					tempFloder.setFirstImg(path);// 第一张图片路径
					imgFloderList.add(tempFloder);// 添加一个相册到相册列表
					tempFloderMap.put(floadePath, imgFloderList.indexOf(tempFloder));
				} else {
					tempFloder = imgFloderList.get(tempFloderMap.get(floadePath));
				}
				tempFloder.imgList.add(new ImageItem(path));// 加入图片进入相册
			} while (mCursor.moveToNext());
			mCursor.close();
			tempFloderMap = null;
		}

	}

	/***
	 * 
	 * @author kevin 相册图片
	 */
	private class FolderImage {
		/** 文件夹路径 */
		private String dir;
		/** 文件件名 */
		private String floderName;
		/** 第一张图片 */
		private String firstImg;
		/** 图片路径列表 */
		private List<ImageItem> imgList;

		FolderImage() {
			imgList = new ArrayList<ImageItem>();
		}

		@SuppressWarnings("unused")
		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
			int lastIndexOf = this.dir.lastIndexOf("/");
			this.floderName = this.dir.substring(lastIndexOf);
		}

		public String getFloderName() {
			return floderName;
		}

		public String getFirstImg() {
			return firstImg;
		}

		public void setFirstImg(String firstImg) {
			this.firstImg = firstImg;
		}

		public List<ImageItem> getImgList() {
			return imgList;
		}

		@SuppressWarnings("unused")
		public void setImgList(List<ImageItem> imgList) {
			this.imgList = imgList;
		}
	}

	/***
	 * @author kevin 一张图片
	 */
	private class ImageItem {

		private String path;

		public ImageItem(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		/***
		 * 重写equals 方法 对比 路径值
		 */
		@Override
		public boolean equals(Object obj) {

			if (obj instanceof ImageItem) {
				ImageItem imgItem = (ImageItem) obj;
				return this.path.equals(imgItem.path);
			}
			return false;
		}

		/***
		 * 重写hashCode 方法用于hashMap 的对比Key
		 */
		@Override
		public int hashCode() {
			return this.path.hashCode();
		}

	}

	/***
	 * gird适配器
	 * 
	 * @author kevin
	 * 
	 */
	private class GirdViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return currentShowImage.imgList.size();
		}

		@Override
		public Object getItem(int position) {
			return currentShowImage.imgList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder childView = null;
			if (convertView == null) {
				childView = new ViewHolder();
				convertView = View.inflate(context, R.layout.multi_select_album_grid_item, null);
				childView.imgItem = (ImageView) convertView.findViewById(R.id.img_item);
				childView.checkBut = (Button) convertView.findViewById(R.id.but_check);
				convertView.setTag(childView);
			} else {
				childView = (ViewHolder) convertView.getTag();
			}
			final ImageItem imgPath = currentShowImage.imgList.get(position);
			// 加载图片
			imageLoader.displayImage("file://" + imgPath.getPath(), childView.imgItem, option);
			childView.checkBut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					chackImg(v, imgPath, confirmBtn);

				}
			});
			// 设置是否选中状态
			childView.checkBut.setSelected(selectImgList.contains(imgPath.getPath()));
			return convertView;
		}

		private class ViewHolder {
			private ImageView imgItem;
			private Button checkBut;
		}

	}

	/***
	 * 
	 * @param v
	 * @param imgPath
	 */
	private void chackImg(View v, ImageItem imgPath, TextView confirm) {
		if (!v.isSelected() && selectImgList.size() + 1 > MAX_SELECT) {
			Toast.makeText(context, "最多选择" + MAX_SELECT + "张图片", Toast.LENGTH_LONG).show();
			return;
		}
		if (selectImgList.contains(imgPath.getPath())) {
			selectImgList.remove(imgPath.getPath());
		} else {
			selectImgList.add(imgPath.getPath());
		}

		boolean isSelect = selectImgList.size() > 0;// 是否有选中的图片
		previewSelectTv.setClickable(isSelect);
		setConfirmText(isSelect, confirm);
		v.setSelected(selectImgList.contains(imgPath.getPath()));
	}

	@SuppressWarnings("deprecation")
	private void setConfirmText(boolean isSelect, TextView confirm) {
		confirm.setEnabled(isSelect);
		confirm.setClickable(isSelect);
		String confirmOk = "完成 " + (selectImgList.size()) + "/" + MAX_SELECT;
		String confirmStr = isSelect ? confirmOk : "完成";
		confirm.setText(confirmStr);
		int bagId = isSelect ? R.drawable.btn_album_select_done : R.drawable.btn_album_select_click_enable;
		// confirm.setBackground(context.getResources().getDrawable(bagId));
		confirm.setBackgroundDrawable(context.getResources().getDrawable(bagId));
	}

	/***
	 * 相册列表适配器
	 * 
	 * @author kevin
	 */
	private class AlbumFolderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return imgFloderList.size();
		}

		@Override
		public Object getItem(int position) {
			return imgFloderList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder childView = null;
			if (convertView == null) {
				childView = new ViewHolder();
				convertView = View.inflate(context, R.layout.mulit_select_folder_listview_item, null);
				childView.headImg = (ImageView) convertView.findViewById(R.id.img_folder_head);
				childView.folderNameTv = (TextView) convertView.findViewById(R.id.tv_folder_name);
				childView.imgCountTv = (TextView) convertView.findViewById(R.id.tv_img_count);
				childView.chooseImg = (ImageView) convertView.findViewById(R.id.img_choose);
				convertView.setTag(childView);
			} else {
				childView = (ViewHolder) convertView.getTag();
			}
			FolderImage floder = imgFloderList.get(position);
			imageLoader.displayImage("file://" + floder.getFirstImg(), childView.headImg, option);
			childView.folderNameTv.setText(floder.getFloderName());
			childView.imgCountTv.setText(floder.getImgList().size() + "张图片");
			childView.chooseImg.setVisibility(currentShowImage == floder ? View.VISIBLE : View.GONE);
			return convertView;
		}

		class ViewHolder {
			ImageView headImg, chooseImg;
			TextView folderNameTv, imgCountTv;

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
