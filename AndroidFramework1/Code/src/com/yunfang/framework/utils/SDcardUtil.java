package com.yunfang.framework.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

/**
 * sdcard 工具类
 * @author gorson
 *
 */
public class SDcardUtil {
	/**
	 * 判断sdcard是否存在
	 * @return
	 */
	public static boolean isSdcardExists(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 得到sdcard的根目录
	 * @return
	 */
	public static File getRoot(){
		File file=Environment.getExternalStorageDirectory();
		return file;
	}
	
	/**
	 * 得到sdcard的剩余空间 （k为单位）
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getFreeSpace(){
		File root=getRoot();
		StatFs stat=new StatFs(root.getPath());
		return stat.getAvailableBlocks();
	}

}
