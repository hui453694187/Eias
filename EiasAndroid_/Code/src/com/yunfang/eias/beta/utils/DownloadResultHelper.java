/**
 * 
 */
package com.yunfang.eias.beta.utils;

import java.io.File;

import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.logic.LoginInfoOperator;
import com.yunfang.framework.iUtils.IDownloadResultHelper;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.SpUtil;

/**
 *收到系统下载广播实现类
 * @author Administrator
 *
 */
public class DownloadResultHelper implements IDownloadResultHelper
{
	/**
	 * 下载完成
	 */
	@Override
	public void downloadComplete()
	{
		new Thread(){
		public void run(){
			EIASApplication.versionInfo.UpdatedTime = DateTimeUtil.getCurrentTime();
			SpUtil sp = SpUtil.getInstance(LoginInfoOperator.KEY_VERSIONINFO);
			sp.putString(LoginInfoOperator.KEY_LASTVERSIONINFO,JSONHelper.toJSON(EIASApplication.versionInfo));
			DataLogOperator.version("");
			}
		}.start();			
		File file = new File(EIASApplication.downloadFileName);
		FileUtil.openFile(file);
	}

	/**
	 * 通知下载
	 */
	@Override
	public void notificationClicked()
	{
		
	}

	/**
	 * 下载失败
	 */
	@Override
	public void downloadError(String e)
	{	
		DataLogOperator.version(e);		
	}
}
