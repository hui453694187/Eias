package com.yunfang.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.yunfang.framework.base.BaseApplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * 文件类的一些操作
 * 
 * @author Ari
 */
@SuppressLint({ "DefaultLocale", "InlinedApi" })
public class FileUtil {

	// {{ 相关的属性
	// 缓冲区的大小
	private final static int BUFF_SIZE = 8192;
	// 标志
	@SuppressWarnings("unused")
	private final static String TAG = "FileUtil";

	// }}
	/**
	 * 判断某个缓存文件在sdcard中是否存在
	 * 
	 * 如果缓存文件缓存时间已过期，删除老的缓存文件
	 * 
	 * @param file文件完整路径
	 * @return
	 */
	public static boolean cacheFileExists(String path) {
		boolean flag = false;
		File file = new File(path);
		if (file.exists()) {
			long currentTimeMilles = System.currentTimeMillis();
			long lastModifieMilles = file.lastModified();

			if (currentTimeMilles - lastModifieMilles > 604800000) {
				file.delete();
			} else {
				flag = true;
			}
		}

		return flag;
	}

	/**
	 * 判断某个网络url地址对应的图片在本地是否有缓存文件
	 * 
	 * @param path缓存文件所在目录
	 * @param url图片url地址
	 * @return
	 */
	public static boolean imageCacheFileExists(String path, String url) {
		return cacheFileExists(getImageCacheFile(path, url));
	}

	/**
	 * 获取url对应图片缓存的全路径
	 * 
	 * @param path图片缓存目录
	 * @param url图片url地址
	 * @return
	 */
	public static String getImageCacheFile(String path, String url) {
		StringBuilder sb = new StringBuilder();
		sb.append(path);
		sb.append(Md5Util.MD5Encode(url));
		sb.append(".jpg");
		return sb.toString();
	}

	/**
	 * 
	 * 保存文件至SD卡
	 * 
	 * @param path保存的路径
	 * @param bytes文件数据
	 * @return
	 */
	public static boolean saveFile2SDcard(String path, byte[] bytes) {

		parentFolder(path);

		File file = new File(path);

		boolean flag = false;
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file),
					BUFF_SIZE);
			bos.write(bytes, 0, bytes.length);
			bos.close();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 文件或文件夹拷贝 如果是文件夹拷贝 目标文件必须也是文件夹
	 * 
	 * @param source源文件
	 * @param dst如果是移动文件
	 *            ，就是完整的文件路径，如果是移动目录，就是一个完整的目录结构
	 * @return
	 */
	public static boolean copy(String src, String dst) {

		File srcFile = new File(src);
		if (!srcFile.exists()) { // 源文件不存在
			return false;
		}

		// 目标文件最后一个字符
		Character lastChar = dst.charAt(dst.length() - 1);

		if (srcFile.isDirectory()) { // 整个文件夹拷贝

			if (lastChar != '\\' && lastChar != '/') { // 如果目标是一个文件而不是目录，返回false
				return false;
			}

			boolean flag = false;

			parentFolder(dst);
			File dstFile = new File(dst);

			File[] files = srcFile.listFiles();

			for (File f : files) {
				String newSrcPath = f.getAbsolutePath();
				String newDstPath = dstFile.getAbsolutePath() + File.separator
						+ f.getName();

				if (f.isDirectory()) {
					newSrcPath += File.separator;
					newDstPath += File.separator;
				}

				copy(newSrcPath, newDstPath);

				flag = true;
			}

			return flag;

		} else { // 单个文价拷贝
			File dstFile = null;

			if (lastChar.equals('\\') || lastChar.equals('/')) { // 目标地址是目录
				dst = dst + srcFile.getName();
			}

			parentFolder(dst);
			dstFile = new File(dst);

			InputStream is = null;
			OutputStream op = null;
			try {
				is = new FileInputStream(srcFile);
				op = new FileOutputStream(dstFile);

				BufferedInputStream bis = new BufferedInputStream(is);

				BufferedOutputStream bos = new BufferedOutputStream(op);

				byte[] bt = new byte[BUFF_SIZE];

				int len = -1;

				try {
					len = bis.read(bt);
					while (len != -1) {
						bos.write(bt, 0, len);
						len = bis.read(bt);
					}

					bis.close();
					bos.close();

				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

	}

	/**
	 * 判断某个文件所在的文件夹是否存在，不存在时直接创建
	 * 
	 * @param path
	 */
	public static void parentFolder(String path) {
		File file = new File(path);
		String parent = file.getParent();

		File parentFile = new File(parent + "/");
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDPath() {

		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {

			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			String tmpPath = sdDir.getAbsolutePath();// 获取根目录的绝对路径
			if (!tmpPath.endsWith(File.separator))
				tmpPath += File.separator;
			return tmpPath;
		}

		return null;
	}

	/**
	 * 获取应用在SD卡的路径
	 * 
	 * @param taskName
	 * @return
	 */
	private static String sAppSDPath = null;

	public static String getAppSDPath() {

		return sAppSDPath;
	}

	public static boolean initAppSDPath(String name) {

		String sdPath = getSDPath();
		if (TextUtils.isEmpty(sdPath) == false) {

			sdPath += name + File.separator;

			File file = new File(sdPath);
			if (file.exists() == false)
				file.mkdir();

			sAppSDPath = sdPath;
		}

		return true;
	}

	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveBytes(String filePath, byte[] data) {
		try {
			File file = new File(filePath);
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(data);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveBytes(File file, byte[] data) {

		try {
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(data);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static byte[] getBytes(String filePath) {

		try {

			File file = new File(filePath);
			if (!file.exists() || !file.canRead())
				return null;
			FileInputStream inStream = new FileInputStream(file);
			byte bytes[] = new byte[inStream.available()];
			inStream.read(bytes);
			inStream.close();

			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveObject(String filePath, Object object) {

		try {

			File file = new File(filePath);
			FileOutputStream outStream = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(outStream);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveObject(File file, Object object) {

		try {
			FileOutputStream outStream = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(outStream);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static Object getObject(String filePath) {

		try {

			File file = new File(filePath);
			if (!file.exists() || !file.canRead())
				return null;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			ois.close();
			fis.close();

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 创建文件夹
	 * 
	 * @param dirPath
	 * @return
	 */
	public static boolean mkDir(String dirPath) {

		File file = new File(dirPath);
		if (file.exists() == false) {

			return file.mkdirs();
		}

		return true;
	}

	/**
	 * 创建临时文件
	 * 
	 * @param fielPath
	 * @return
	 */
	public static File createTmpFile() {
		String rootPath = BaseApplication.getInstance().getFilesDir()
				.getAbsolutePath();
		if (rootPath.endsWith("/") == false) {
			rootPath += "/";
		}

		try {
			String tmpPath = rootPath + new Date().hashCode();
			File file = new File(tmpPath);
			file.createNewFile();

			return file;
		} catch (Exception e) {
		}

		return null;
	}

	/**
	 * 应用退出时，删除所有临时文件
	 * 
	 * @param context
	 */
	public static void deleteTmpFiles() {
		File rootFiles = BaseApplication.getInstance().getFilesDir();
		File files[] = rootFiles.listFiles();
		if (files != null) {

			int cnt = files.length;
			for (int i = 0; i < cnt; ++i) {

				File tmpFile = files[i];
				tmpFile.delete();
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static boolean delFile(String path) {
		if (path.equals("") || !SDcardUtil.isSdcardExists()) {
			return false;
		} else {
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				return file.delete();
			} else {
				return false;
			}
		}
	}

	/**
	 * 删除递归目录下的所有文件
	 * 
	 * @param dir
	 * @return
	 */
	public static void delDir(String dir) {

		File file = new File(dir);
		if (file.exists()) {
			if (file.isDirectory()) {

				if (file.list().length == 0) {// 删除文件夹
					file.delete();
				} else {
					File[] files = file.listFiles();
					for (File f : files) {
						delDir(f.getPath());
					}
					file.delete();
				}
			} else {// 删除文件
				delFile(file.getPath());
			}
		}
	}

	/**
	 * 移动文件
	 * 
	 * @param srcFileName
	 *            :源文件完整路径
	 * @param destDirName
	 *            :文件件
	 * @return 文件移动成功返回true，否则返回false
	 */
	public static boolean moveFileToDir(String srcFileName, String dir) {

		File srcFile = new File(srcFileName);
		if (!srcFile.exists() || !srcFile.isFile())
			return false;

		File destDir = new File(dir);
		if (!destDir.exists())
			destDir.mkdirs();

		return srcFile.renameTo(new File(dir + File.separator
				+ srcFile.getName()));
	}

	/**
	 * 移动文件
	 * 
	 * @param srcFileName
	 *            :源文件完整路径
	 * @param destDirName
	 *            :文件
	 * @return 文件移动成功返回true，否则返回false
	 */
	public static boolean moveFileToFile(String srcFileName,
			String targetFileFullName) {

		File srcFile = new File(srcFileName);
		if (!srcFile.exists() || !srcFile.isFile())
			return false;

		File destDir = new File(targetFileFullName);
		return srcFile.renameTo(destDir);
	}

	/**
	 * 移动目录
	 * 
	 * @param srcDirName
	 *            :源目录完整路径
	 * @param destDirName
	 *            :目的目录完整路径
	 * @return 目录移动成功返回true，否则返回false
	 */
	public static boolean moveDirectory(String srcDirName, String destDirName) {

		File srcDir = new File(srcDirName);
		if (!srcDir.exists() || !srcDir.isDirectory())
			return false;

		File destDir = new File(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();

		/**
		 * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹 注意移动文件夹时保持文件夹的树状结构
		 */
		File[] sourceFiles = srcDir.listFiles();
		for (File sourceFile : sourceFiles) {
			if (sourceFile.isFile())
				moveFileToDir(sourceFile.getAbsolutePath(),
						destDir.getAbsolutePath());
			else if (sourceFile.isDirectory())
				moveDirectory(
						sourceFile.getAbsolutePath(),
						destDir.getAbsolutePath() + File.separator
								+ sourceFile.getName());
			else
				;
		}
		return srcDir.delete();
	}

	/**
	 * 删除缓存到SD卡上的图片 只删除标记为tmp的图片
	 * 
	 */
	public static void clearCacheFile(String path) {
		File file = new File(path);

		if (file.exists() && file.isDirectory()) {
			String[] list;
			list = file.list();
			for (String name : list) {
				// "."
				if (name.matches(".+\\_tmp\\.jpg")) {
					delFile(path + name);
				}
			}
		}
	}

	/**
	 * 获取可清除的缓冲大小
	 */
	public static float getCacheSize(String dir) {
		File file = new File(dir);
		float size = 0F;
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] fs = file.listFiles();
				for (File file2 : fs) {
					if (file2.isFile()) {
						String name = file2.getAbsolutePath();
						if (name.matches(".+\\_tmp\\.jpg")) {
							float s = file2.length();
							size += s;
						}
					}

				}
			} else {
				float s = file.length();
				size += s;
			}

		}
		return size / 1024 / 1024;
	}

	/**
	 * 获取视频的播放时间长度
	 * 
	 * @param videoPath
	 *            :视频路径
	 * @return 时间长度字符串
	 */
	public static String getPalyTimeString(String videoPath) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		Date date = null;
		try {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(videoPath);
			// 取得视频的长度(单位为毫秒)
			String time = "";
			if (videoPath.length() > 0) {
				time = retriever
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			} else {
				time = "0";
			}
			date = new Date(Integer.valueOf(time));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			date = new Date(0);
		}catch (Exception e){
			e.printStackTrace();
			date = new Date(0);
		}
		return sdf.format(date);
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url本地路径
	 * @return Bitmap
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取出指定照片目录中的所有视频路径
	 * 
	 * @param root
	 *            :照片资源根目录
	 * 
	 */
	public static ArrayList<String> getRecursion(String root) {
		ArrayList<String> filePathList = new ArrayList<String>();
		File[] subFile = new File(root).listFiles();
		for (File fileItem : subFile) {
			if (fileItem.isDirectory()) {
				ArrayList<String> temp = getRecursion(fileItem
						.getAbsolutePath());
				filePathList.addAll(temp);
			} else {
				String filePath = fileItem.getAbsolutePath();
				filePathList.add(filePath);
			}
		}
		return filePathList;
	}

	/**
	 * 获取文件类型
	 * 
	 * @param name
	 * @return
	 */
	public static String getFileType(String name) {
		if (name == null) {
			return "";
		} else {
			String type = "";
			String end = name.substring(name.lastIndexOf(".") + 1,
					name.length()).toLowerCase(
					BaseApplication.getInstance().getResources()
							.getConfiguration().locale);
			if (end.equals("jpg") || end.equals("png")) {
				type = "image";
			} else if (end.equals("3gp") || end.equals("mp4")) {
				type = "video";
			} else if (end.equals("amr") || end.equals("wmv")
					|| end.equals("mp3")) {
				type = "audio";
			} else {
				type = "*";
			}
			type += "/*";
			return type;
		}
	}

	/**
	 * 把图片库的文件移到系统指定的目录下
	 * 
	 * @param context
	 *            :所在界面
	 * @param intent
	 *            :意图
	 * @param targetFileFuleName
	 *            :移到的目录
	 * @param deleteOriginalFile
	 *            :是否删除原始文件
	 * @param coverExistFile
	 *            :是否复盖已有文件
	 */
	@SuppressWarnings("deprecation")
	public static void movePhotoLibFileToCustomDir(Context context,
			Intent intent, String targetFileFuleName,
			Boolean deleteOriginalFile, Boolean coverExistFile) {
		try {
			Uri originalUri = intent.getData();
			String path;
			if (originalUri.toString().startsWith(
					"content://com.android.providers.media.documents")) {
				String docId = DocumentsContract.getDocumentId(originalUri);
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

				path = getFilePathByDocID(context, contentUri, split[1]);
			} else {
				String[] proj = { MediaStore.Images.Media.DATA };
				// 好像是android多媒体数据库的封装接口
				Cursor cursor = ((Activity) context).managedQuery(originalUri,
						proj,//
						null, null, null);
				// 获得用户选择的图片的索引值
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				path = cursor.getString(column_index);
			}
			if (new File(path).exists()) {
				File targetFile = new File(targetFileFuleName);
				if (!targetFile.exists()) {
					copy(path, targetFileFuleName);
				} else {
					if (coverExistFile) {
						if (delFile(targetFileFuleName)) {
							copy(path, targetFileFuleName);
						}
					}
				}
				if (deleteOriginalFile) {
					delFile(path);
				}
			} else {
				ToastUtil.longShow(BaseApplication.getInstance(), "源文件已经被转移");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			ToastUtil.longShow(BaseApplication.getInstance(), e.getMessage());
		}
	}

	/**
	 * 通过DocID获取实际的文件地址
	 */
	public static String getFilePathByDocID(Context context, Uri uri,
			String docId) {
		String result = "";

		Cursor cursor = null;
		String column = "_data";
		try {
			cursor = context.getContentResolver().query(uri,
					new String[] { column }, "_id=?", new String[] { docId },
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				result = cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return result;
	}

	/**
	 * 获取系统目录
	 * 
	 * @return
	 */
	public static String getRootPath() {
		return Environment.getExternalStorageDirectory() + "";
	}

	/**
	 * 获取系统根目录
	 * 
	 * @param companyStr
	 *            :公司名称
	 * @return
	 */
	public static String getSysPathOfCompany(String companyStr) {
		String result = getRootPath() + File.separator;
		if (companyStr != null && companyStr.length() > 0) {
			result += companyStr + File.separator;
		}
		return result;
	}

	/**
	 * 获取系统根目录
	 * 
	 * @param companyStr
	 *            :公司名称
	 * @param projectStr
	 *            ：项目名称
	 * @return
	 */
	public static String getSysPath(String companyStr, String projectStr) {
		String result = getRootPath() + File.separator;
		if (companyStr != null && companyStr.length() > 0) {
			result += companyStr + File.separator;
		}
		if (projectStr != null && projectStr.length() > 0) {
			result += projectStr + File.separator;
		}
		return result;
	}

	// /**
	// * 创建图片目录
	// *
	// * @param taskNum:任务编号
	// * @return
	// */
	// public static String createOrGetPhotoDir(String taskNum) {
	// String sysRoot = getSysPath();
	// String dirString = sysRoot + taskNum + File.separator + PHOTO_KEY;
	//
	// File file = new File(dirString);
	// if (!file.exists()) {
	// file.mkdirs();
	// }
	// return file.getAbsolutePath();
	// }
	//
	// /**
	// * 创建音频目录
	// *
	// * @param taskNum:任务编号
	// * @return
	// */
	// public static String createOrGetAudioDir(String taskNum) {
	// String company = BaseApplication.getInstance().getString(
	// R.string.app_name_company);
	// String project = BaseApplication.getInstance().getString(
	// R.string.app_name_en);
	// String sysRoot = getRootPath() + File.separator + company
	// + File.separator + project + File.separator;
	//
	// String dirString = sysRoot + taskNum + File.separator + AUDIO_KEY;
	//
	// File file = new File(dirString);
	// if (!file.exists()) {
	// file.mkdirs();
	// }
	// return file.getAbsolutePath();
	// }
	//
	// /**
	// * 创建视频目录
	// *
	// * @param taskNum:任务编号
	// * @return
	// */
	// public static String createOrGetVideoDir(String taskNum) {
	// String company = BaseApplication.getInstance().getString(
	// R.string.app_name_company);
	// String project = BaseApplication.getInstance().getString(
	// R.string.app_name_en);
	// String sysRoot = getRootPath() + File.separator + company
	// + File.separator + project + File.separator;
	//
	// String dirString = sysRoot + taskNum + File.separator + VIDEO_KEY;
	//
	// File file = new File(dirString);
	// if (!file.exists()) {
	// file.mkdirs();
	// }
	// return file.getAbsolutePath();
	// }

	/**
	 * 打开文件
	 * 
	 * @param file
	 *            :文件对象
	 */
	public static void openFile(File file) {
		if (file.exists()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			BaseApplication.getInstance().startActivity(intent);
		}
	}
}
