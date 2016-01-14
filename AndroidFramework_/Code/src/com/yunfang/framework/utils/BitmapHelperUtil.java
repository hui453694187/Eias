package com.yunfang.framework.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.yunfang.framework.base.BaseApplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

/**
 * 图片压缩处理类
 * 
 * @author 贺隽
 * 
 */
public class BitmapHelperUtil {

	/**
	 * 压缩图片质量，直到小到100k
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 100表示不压缩
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩图片是否大于100K，则继续压缩
		while (baos.toByteArray().length / 1024 > 100) {
			// 重置baos，即清空baos
			baos.reset();

			// 压缩图片质量到options%,并且将压缩后的图片放入baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);

		return bitmap;
	}

	/**
	 * 求出压缩比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);

			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

		}
		return inSampleSize;
	}

	/**
	 * 根据传入的widht,和heigth，压缩图片
	 * 
	 * @param photoPath
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	@SuppressWarnings({ "unused", "resource" })
	public static File compressFromPath(String photoPath, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoPath, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		if (options.inSampleSize < 1) {
			return null;
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean b = bitmap.compress(CompressFormat.PNG, 100, baos);
		if (b) {
			byte[] buffer = baos.toByteArray();
			File newFile = new File(photoPath);
			try {
				OutputStream output = new FileOutputStream(newFile);
				BufferedOutputStream buffrBufferedOutputStream = new BufferedOutputStream(output);
				buffrBufferedOutputStream.write(buffer);
				return newFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			return null;
		}

		return null;
	}

	/**
	 * 在源文件文件夹压缩并保存成缩略图
	 * 
	 * @param sourceFullName
	 *            :原图片完整文件名
	 * @param targetFullName
	 *            :生成缩略图存放完整文件名
	 * @param tag
	 *            :标记 如tag为_min a.jpg=>a_min.jpg
	 */
	public static File decodeThumbnail(String sourceFullName, String targetFullName) {
		File result = new File(targetFullName);
		File targetDir = new File(result.getParent());
		try {
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// 获取这个图片的宽和高 此时返回bm为空
			Bitmap bitmap = BitmapFactory.decodeFile(sourceFullName, options);
			options.inJustDecodeBounds = false;
			// 计算缩放比
			int be = (int) (options.outHeight / (float) 200);
			options.inSampleSize = be <= 0 ? 1 : be;
			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
			bitmap = BitmapFactory.decodeFile(sourceFullName, options);
			int degree = readPictureDegree(sourceFullName);
			bitmap = rotateBitmap(bitmap, degree);
			FileOutputStream out = new FileOutputStream(result);
			// 30 是压缩率，表示压缩70%; 如果不压缩是100，表示压缩率为0
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out)) {
				out.flush();
				out.close();
			}
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 处理旋转图片
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 把处理好的图片重置为水平样式显示
	 * 
	 * @param bitmap
	 * @param rotate
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
		if (bitmap == null)
			return null;

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	/**
	 * 把图片资源文件转换为Bitmap
	 * 
	 * @param resId
	 *            :图片资源文件编号
	 * @return
	 */
	public static Bitmap readBitMap(int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// opt.inJustDecodeBounds = false;
		// opt.inSampleSize = 50;
		// 獲取資源圖片
		InputStream is = BaseApplication.getInstance().getApplicationContext().getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 将base64转换成bitmap图片
	 * 
	 * @param string
	 *            :base64字符串
	 * @return bitmap
	 */
	public static Bitmap stringBase64toBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;

		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
	 * 
	 * @param src
	 * @param dstWidth
	 * @param dstHeight
	 * @return
	 */
	private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
		Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
		if (src != dst) { // 如果没有缩放，那么不回收
			src.recycle(); // 释放Bitmap的native像素数组
		}
		return dst;
	}

	/**
	 * 从Resources中加载图片
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 计算inSampleSize
			options.inJustDecodeBounds = false;
			Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
			return createScaleBitmap(src, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
		} catch (Exception e) {
			
		}
		return null;
	}

	/**
	 * 从sd卡上加载图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathName, options);
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			Bitmap src = BitmapFactory.decodeFile(pathName, options);
			return createScaleBitmap(src, reqWidth, reqHeight);
		} catch (OutOfMemoryError e) {

		}
		return null;
	}
}
