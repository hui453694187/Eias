package  com.yunfang.framework.utils;

import java.io.File;

import com.yunfang.framework.base.BaseApplication;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

/**
 * 下载文件操作
 * @author Administrator
 *
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DownLoadFileUtil
{
	/**
	 * 下载
	 */
	public static void downLoadFile(String downloadFileName ,String downloadUrl ,String title , String description,String downLoadApkName,DownloadManager mgr){
		try {
			File temp = new File(downloadFileName);
			if(temp.exists()){
				temp.delete();	
			}	
			//下载地址
			String httpUrl = downloadUrl;		
			Uri uri = Uri.parse(httpUrl);
			//如果内存卡里面没有DOWNLOAD路径就创建
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
			//创建下载管理器的访问对象
			DownloadManager.Request request = new DownloadManager.Request(uri);
			//只要手机有网就下载 目前没有考虑3G 的状态
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
			//设定当设备处于漫游模式时是否要下载
			request.setAllowedOverRoaming(false);
			//设置标题信息
			request.setTitle(title);
			//设置详细信息
			request.setDescription(description);
			//设置下载路径
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,downLoadApkName);
			//开始下载
			mgr.enqueue(request);				
		} catch (Exception e) {
			if (BaseApplication.downloadHelper!=null)
			{
				BaseApplication.downloadHelper.downloadError(e.getMessage());
			}
		}		
	}
}
