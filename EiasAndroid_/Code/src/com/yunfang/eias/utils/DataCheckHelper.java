/**
 * 
 */
package com.yunfang.eias.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 导入/导出 任务调用通用方法
 * 
 * @author 贺隽
 * 
 */
public class DataCheckHelper {
	/**
	 * 获取文件大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static long getFileSizes(File f) throws Exception {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		if (null != flist) {
			for (File file : flist) {
				if (file.isDirectory()) {
					size = size + getFileSize(file);
				} else {
					size = size + file.length();
				}
			}
		}
		return size;
	}

	/**
	 * 转换文件大小单位(BYTE/KB/MB/GB)
	 * 
	 * @param fileS
	 * @return
	 */
	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1024 * 1024) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1024 * 1024 * 1024) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 递归求取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public static long getlist(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (File file : flist) {
			if (file.isDirectory()) {
				size = size + getlist(file);
				size--;
			}
		}
		return size;
	}

	// 读取文本文件中的内容
	public static String ReadTxtFile(File file) {
		String content = ""; // 文件内容字符串
		try {
			InputStream instream = new FileInputStream(file);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				// 分行读取
				while ((line = buffreader.readLine()) != null) {
					content += line + "\n";
				}
				instream.close();
			}
		} catch (java.io.FileNotFoundException e) {

		} catch (IOException e) {

		}
		return content;
	}

	/**
	 * 比较2个文件是否为同一个文件
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean fileEquals(File source, File target) {
		boolean result = false;
//		byte[] sourceByte = getBytesFromFile(source);
//		byte[] targetByte = getBytesFromFile(target);
		if(source.length() == target.length()){// && sourceByte.equals(targetByte)){
			result = true;
		}
		return result;
	}

	/**
	 * 文件转化为字节数组
	 * 
	 * @param f
	 * @return
	 */
	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 把字节数组保存为一个文件
	 * 
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}
	
	/**
	 * 显示丢失的文件
	 * @param file
	 */
	public static File displayNoFile(File file){
		File display = null;
		if (file == null || !file.exists()) {	
			try {		
				display = new File(EIASApplication.root + "缓存/temp.jpg");
				if(!display.exists()){
					Bitmap bitmap = BitmapFactory.decodeResource(EIASApplication.getInstance().getResources(), R.drawable.ico_no_find);
					FileOutputStream fos = null;
					fos = new FileOutputStream(display);
					if (null != fos) {
						bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
						fos.flush();
						fos.close();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			return file;
		}
		return display;
	}
}
