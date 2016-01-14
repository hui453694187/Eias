package com.yunfang.framework.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.yunfang.framework.base.BaseApplication;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * 
 * 项目名称：yunfang.eias 类名称：CameraUtils 类描述：照片（拍照或从相册取）、录音、录像处理类 创建人：lihc
 * 创建时间：2014-4-23 下午6:38:42
 * 
 * @version
 */
public class CameraUtils {

	/**
	 * 创建自定义的文件路径
	 * 
	 * @param folder
	 *            :文件夹的名称
	 * @param ext
	 *            :后缀，包括"."(如：.jpg)
	 * @param taskId
	 *            :任务编号
	 * @return:
	 */
	public static File customFilePath(String folder, String ext) {

		StringBuilder sb = new StringBuilder();
		// 判断SD卡是否存在，并且是否具有读写权限
		if (SDcardUtil.isSdcardExists()) {
			File dir = new File(folder);
			if (!dir.exists()) {
				dir.mkdir();
			}

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			sb.append(folder);
			sb.append(File.separator);
			sb.append(timeStamp);
			sb.append(ext);
			File file = new File(sb.toString().trim());
			return file;
		} else {
			ToastUtil.longShow(BaseApplication.getInstance(), "SD卡不存在，或者不具有读写权限");
			return null;
		}
	}

	/**
	 * 创建自定义的文件路径
	 * 
	 * @param folder
	 *            :文件夹的名称
	 * @param ext
	 *            :后缀，包括"."(如：.jpg)
	 * @param taskId
	 *            :任务编号
	 * @return:
	 */
	public static File getUUIDFile(String folder, String ext) {

		StringBuilder sb = new StringBuilder();
		// 判断SD卡是否存在，并且是否具有读写权限
		if (SDcardUtil.isSdcardExists()) {
			File dir = new File(folder);
			if (!dir.exists()) {
				dir.mkdir();
			}
			sb.append(folder);
			sb.append(File.separator);
			sb.append(UUID.randomUUID());
			sb.append(ext);
			File file = new File(sb.toString().trim());
			return file;
		} else {
			ToastUtil.longShow(BaseApplication.getInstance(), "SD卡不存在，或者不具有读写权限");
			return null;
		}
	}

	/**
	 * 启动拍照的Intent,并自定义拍照存放路径
	 * 
	 * @param context
	 *            :上下文
	 * @return:返回拍照存放的自定义路径
	 */
	public static Intent startGetPicFromPhoto(File file) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		// 照片的质量（0低质量，1高质量）
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}

	/**
	 * 启动录音的Intent,并自定义录音存放路径
	 * 
	 * @param context
	 *            :上下文
	 * @return
	 */
	public static Intent startAudio(File file) {
		// 录音
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		intent.setType("audio/amr");
		return intent;
	}

	/**
	 * 启动录像的Intent,并自定义录像存放路径
	 * 
	 * @param context
	 *            :上下文
	 * @return
	 */
	public static Intent startVideo(File file) {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		// 视频的质量（0低质量，1高质量）
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}

	/**
	 * 启动打开相册的Intent,并自定义所选取照片的存放路径
	 * 
	 * @param context
	 *            ：上下文
	 * @param taskNum
	 *            ：任务编号
	 * @return
	 */
	public static Intent startGetPicPhotoAlbum() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
		}
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		return intent;
	}

	/**
	 * 获取图册路径
	 * 
	 * @param data
	 * @return
	 */
	public static String getPhotoAlbumPath(Intent data) {
		Uri imageUri = data.getData();
		return getFilePathFromUri(imageUri);
	}

	/**
	 * 获取物理路径
	 * 
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromUri(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = ((Activity) BaseApplication.getInstance().getApplicationContext()).managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 获取文件路径
	 * 
	 * @param contentUri
	 * @return
	 */
	public static String getFilePathFromUri(Uri contentUri) {
		String path = null;
		Uri fileUri = contentUri;
		String scheme = fileUri.getScheme();
		if (scheme.equals("content")) {
			path = getRealPathFromUri(contentUri);
		} else if (scheme.equals("file")) {
			path = fileUri.getPath();
		}
		return path;

	}

	/**
	 * 获取临时图片缩略图
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		// 图片抖动处理
		opt.inDither = true;
		// 设置这个，只得到Bitmap的属性信息放入opts，而不把Bitmap加载到内存中
		opt.inJustDecodeBounds = true;
		// 设置让解码器以最佳方式解码
		opt.inPreferredConfig = Bitmap.Config.RGB_565; // ARGB_8888
		// 内存不足时可被回收
		opt.inPurgeable = true;
		// 设置为false,表示不仅Bitmap的属性，也要加载bitmap
		opt.inJustDecodeBounds = false;
		// 解析图片
		Bitmap bm = BitmapFactory.decodeFile(path, opt);
		return bm;
	}

	/**
	 * 获取临时图片缩略图大小
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
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

	/**
	 * 获取临时图片缩略图大小
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
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

	// =====================================================

	/**
	 * 获取多媒体资源路径（相册，录音，录像）
	 * 
	 * @param uri
	 *            :资源对应的URI
	 * @return 注意：从相册中中取照片路径,如果版本号>=19，从相册中取照片的路径的办法，会有所不同，所以必须先要判断版本号，再做相应不同的处理
	 *         加上以上权限，<uses-permission
	 *         android:name="android.permission.READ_EXTERNAL_STORAGE"/>
	 */
	public static String getPath(Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(BaseApplication.getInstance(), uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				String id = DocumentsContract.getDocumentId(uri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = "_id=?";
				String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            :The context.
	 * @param uri
	 *            :The Uri to query.
	 * @param selection
	 *            :(Optional) Filter used in the query.
	 * @param selectionArgs
	 *            :(Optional) Selection arguments used in the query.
	 * @return: The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = BaseApplication.getInstance().getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            :The Uri to check.
	 * @return: Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            :The Uri to check.
	 * @return: Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            :The Uri to check.
	 * @return: Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            :The Uri to check.
	 * @return: Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	/**
	 * 利用MediaMetadataRetriever按照时间截取视频 并转换为Bitmap存放于SDCard
	 * 
	 * @param videoPath
	 *            :视频的路径
	 */
	public static Bitmap getBitmapsFromVideo(File video) {
		Bitmap bitmap = null;
		if (video != null) {
			// 视频默认目录
			// Environment.getExternalStorageDirectory()
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(video.getAbsolutePath());
			// 取得视频的长度(单位为毫秒)
			String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			// 取得视频的长度(单位为秒)
			int seconds = Integer.valueOf(time) / 1000;
			// 只需要第二张
			seconds = 2;
			// 得到每一秒时刻的bitmap比如第一秒,第二秒
			bitmap = retriever.getFrameAtTime(seconds * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			Integer extIndex = video.getName().indexOf(".");
			String path = video.getParent() + File.separator + video.getName().substring(0, extIndex + 1) + "jpg";
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				bitmap.compress(CompressFormat.JPEG, 80, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 打开 图片、音频、视频
	 * 
	 * @param fileFullName
	 *            :文件完整路径
	 */
	public static void openMedia(File file) {
		if (file.exists()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			String type = FileUtil.getFileType(file.getAbsolutePath());
			intent.setDataAndType(Uri.fromFile(file), type);
			BaseApplication.getInstance().startActivity(intent);
		}
	}
}
