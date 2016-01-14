package com.yunfang.framework.iUtils;
/**
 * 下载操作接口
 * @author Administrator
 *
 */
public interface IDownloadResultHelper
{

	/**
	 * 下载完成
	 */
	 void downloadComplete();
	 
	 /**
	  * 通知下载
	  */
	 void notificationClicked();
	 
	 /**
	  * 下载出错
	  */
	 void downloadError(String e);
}
