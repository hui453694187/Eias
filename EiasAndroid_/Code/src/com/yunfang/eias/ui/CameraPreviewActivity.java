package com.yunfang.eias.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.framework.base.BaseWorkerActivity;
import com.yunfang.framework.utils.BitmapHelperUtil;
import com.yunfang.framework.utils.FileOperateUtil;
import com.yunfang.framework.view.album.FilterImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * @ClassName: CameraPreviewActivity
 * @Description: 自定义照相机预览类
 * @author 贺隽
 * @date 2015-7-9 9:44:25
 * 
 */
public class CameraPreviewActivity extends BaseWorkerActivity implements View.OnClickListener {
	// 变量声明
	private String mSaveRoot;
	private String mThumbnailRoot;

	private Bitmap mPreviewBitmap;
	private Bitmap tempBitmap;

	/** 照片字节流处理类 */
	private DataHandler mDataHandler;

	// 拍照后保存
	private FilterImageView mPreviewImageView;
	private ImageButton mConfirmButton;
	private ImageButton mCancelButton;

	// {{ 事件定义

	// }}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_preview);
		mPreviewImageView = (FilterImageView) findViewById(R.id.camera_preview_imageView);
		mConfirmButton = (ImageButton) findViewById(R.id.camera_preview_confirm_img);
		mCancelButton = (ImageButton) findViewById(R.id.camera_preview_cancel_img);

		mConfirmButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		mSaveRoot = bundle.getString("path1");
		mThumbnailRoot = bundle.getString("path2");

		setPreview();
	}

	/**
	 * 预览图片不含旋转
	 */
	private void setPreview() {
		if (EIASApplication.myPhoto != null) {
			try {								
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inDither = true;
				// 设置这个，只得到Bitmap的属性信息放入opts，而不把Bitmap加载到内存中
				opt.inJustDecodeBounds = true;
				// 设置解码器以最佳方式解码
				opt.inPreferredConfig = Bitmap.Config.RGB_565; // ARGB_8888
				// 内存不足时可被回收
				opt.inPurgeable = true;
				// 设置为false,表示不仅Bitmap的属性，也要加载bitmap
				opt.inJustDecodeBounds = false;

				// 解析生成相机返回的图片
				tempBitmap = BitmapFactory.decodeByteArray(EIASApplication.myPhoto, 0, EIASApplication.myPhoto.length, opt);			
				mPreviewBitmap = Bitmap.createScaledBitmap(tempBitmap, EIASApplication.deviceInfo.ScreenWeight, EIASApplication.deviceInfo.ScreenHeight, true);
				mPreviewImageView.setImageBitmap(mPreviewBitmap);
			} catch (Exception e) {
				e.printStackTrace();
				showToast("解析相机数据失败");
			}
		} else {
			showToast("拍照失败，请重试");
		}
	}
	
	/**
	 * 预览图片包含旋转图片
	 */
	@SuppressWarnings("unused")
	private void setPreviewAndRotate() {
		if (EIASApplication.myPhoto != null) {
			String imgName = FileOperateUtil.createFileNameUUID(".jpg");
			File cachePath = getExternalCacheDir();
			File cacheFile = new File(cachePath.getAbsoluteFile() + File.separator + imgName);
			FileOutputStream fos;
			try {
				if (!cachePath.exists()) {
					cachePath.mkdirs();
				}
				fos = new FileOutputStream(cacheFile.getAbsolutePath());
				fos.write(EIASApplication.myPhoto);
				fos.flush();
				fos.close();
								
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inDither = true;
				// 设置这个，只得到Bitmap的属性信息放入opts，而不把Bitmap加载到内存中
				opt.inJustDecodeBounds = true;
				// 设置解码器以最佳方式解码
				opt.inPreferredConfig = Bitmap.Config.RGB_565; // ARGB_8888
				// 内存不足时可被回收
				opt.inPurgeable = true;
				// 设置为false,表示不仅Bitmap的属性，也要加载bitmap
				opt.inJustDecodeBounds = false;

				// 解析生成相机返回的图片
				tempBitmap = BitmapFactory.decodeByteArray(EIASApplication.myPhoto, 0, EIASApplication.myPhoto.length, opt);
				//int degree = BitmapHelperUtil.readPictureDegree(cacheFile.getAbsolutePath());
				//mPreviewBitmap = BitmapHelperUtil.rotateBitmap(tempBitmap, degree);				
				mPreviewBitmap = Bitmap.createScaledBitmap(tempBitmap, EIASApplication.deviceInfo.ScreenWeight, EIASApplication.deviceInfo.ScreenHeight, true);
				mPreviewImageView.setImageBitmap(mPreviewBitmap);
			} catch (Exception e) {
				e.printStackTrace();
				showToast("解析相机数据失败");
			}
			finally{
				cacheFile.delete();
			}
		} else {
			showToast("拍照失败，请重试");
		}
	}

	/**
	 * 计算缩放尺寸
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;

	}

	private int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/** 从给定的路径加载图片，并指定是否自动旋转方向 */
	public Bitmap loadBitmap(String imgpath, boolean adjustOritation) {
		if (!adjustOritation) {
			return BitmapFactory.decodeFile(imgpath);
		} else {
			// 创建bitmap options
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inDither = false;
			opt.inJustDecodeBounds = true;
			// 内存不足时可被回收
			opt.inPurgeable = true;
			BitmapFactory.decodeFile(imgpath, opt);
			int minSideLength = Math.min(1080, 1080);
			opt.inSampleSize = computeSampleSize(opt, minSideLength, 1080 * 1080);
			opt.inJustDecodeBounds = false;

			Bitmap bm = BitmapFactory.decodeFile(imgpath, opt);

			int digree = 0;
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(imgpath);
			} catch (IOException e) {
				e.printStackTrace();
				exif = null;
			}
			if (exif != null) {
				// 读取图片中相机方向信息
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
				// 计算旋转角度
				switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
				}
			}
			if (digree != 0) {
				// 旋转图片
				Matrix m = new Matrix();
				m.postRotate(digree);
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
			}
			return bm;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.camera_preview_confirm_img:
			savePhoto();
			break;
		default:
			break;
		}
		setCanceledHandle();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		setCanceledHandle();
		super.onDestroy();
	}

	/*
	 * 后端线程
	 */
	@Override
	protected void handleBackgroundMessage(Message msg) {
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		default:
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/*
	 * UI线程
	 */
	@Override
	protected void handleUiMessage(Message msg) {
		super.handleUiMessage(msg);
		switch (msg.what) {
		default:
			break;
		}
		loadingWorker.closeLoading();
	}

	/**
	 * 处理点击取消后
	 */
	private void setCanceledHandle() {
		if (mPreviewBitmap != null && !mPreviewBitmap.isRecycled()) {
			mPreviewBitmap.recycle();
		}
		if (tempBitmap != null && !tempBitmap.isRecycled()) {
			tempBitmap.recycle();
		}
		System.gc();
	}

	/**
	 * 保存图片到任务子项中
	 * 
	 * @param fileName
	 */
	private void savePhoto() {
		// 产生新的文件名
		String imgName = FileOperateUtil.createFileNameUUID(".jpg");
		String imagePath = mSaveRoot + File.separator + imgName;
		String thumbPath = mThumbnailRoot + File.separator + imgName;
		if (mDataHandler == null) {
			mDataHandler = new DataHandler();
		}
		mDataHandler.save(imagePath, thumbPath);

		String[] files = new String[] { imgName };
		Intent intent = new Intent();
		intent.setAction(BroadRecordType.CAMERASERVER_SEND);
		intent.putExtra("files", files);
		sendBroadcast(intent);
	}

	/**
	 * 拍照返回的byte数据处理类
	 * 
	 * @author 贺隽
	 * 
	 */
	private final class DataHandler {

		public DataHandler() {
			File folder = new File(mSaveRoot);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			folder = new File(mThumbnailRoot);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}

		/**
		 * 保存图片
		 * 
		 * @param 相机返回的文件流
		 * @return 解析流生成的缩略图
		 * @throws InterruptedException
		 */
		public void save(String imagePath, String thumbPath) {
			if (EIASApplication.myPhoto != null) {
				try {
					File file = new File(imagePath);
					try {
						// 存图片大图
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(EIASApplication.myPhoto);
						fos.flush();
						fos.close();
						BitmapHelperUtil.decodeThumbnail(imagePath, thumbPath);
					} catch (Exception e) {
						showToast("解析相机数据失败");
					}
				} catch (OutOfMemoryError e) {
					System.gc();
					showToast("内存不足，请清除一下内存后重新");
				}
			} else {
				showToast("拍照失败，请重试");
			}
		}

	}
}