/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.dto.VersionDTO;
import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.FileUtil;

/**
 * 检测当前安卓版本和服务器版本是否一致
 * 
 * @author 贺隽
 * 
 */
public class CheckVersionTask implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	/*
	 * 设置数据
	 */
	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<VersionDTO> getResponseData() {
		ResultInfo<VersionDTO> result = new ResultInfo<VersionDTO>();
		if (mData != null) {
			String str = new String(mData);
			if (!TextUtils.isEmpty(str)) {
				try {
					JSONObject json = new JSONObject(str.toString());
					if (json.getString("Success").equals("false")) {
						result.Success = false;
						result.Message = json.getString("Message");
					} else {
						json = new JSONObject(json.getString("Data"));
						result.Data = new VersionDTO(json);
					}
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "服务器繁忙";
					DataLogOperator.taskHttp("CheckVersionTask=>" + result.Message + "(getResponseData)", e.getMessage());
				}
			} else {
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}
		return result;
	}

	/**
	 * 获取服务器最新版本信息
	 * 
	 * @param currentUser
	 * @return 返回执行是否成功
	 */
	public ResultInfo<VersionDTO> request(UserInfo currentUser) {
		ResultInfo<VersionDTO> result = new ResultInfo<VersionDTO>();
		String url = currentUser.LatestServer + "/apis/CheckVersion";
		Hashtable<String, Object> params = new Hashtable<String, Object>(4);
		params.put("token", currentUser.Token);
		params.put("apkname", EIASApplication.DownLoadApkName);
		params.put("versionname", EIASApplication.versionInfo.LocalVersionName);
		params.put("versioncode", EIASApplication.versionInfo.LocalVersionCode);
		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.GET, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
			// ResultInfo<VersionDTO> versionDto = getResponseData();
			// result.Others = versionDto;
			// result.Data = versionDto.Success;
			// result.Success = versionDto.Success;
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常！";
			DataLogOperator.taskHttp("CheckVersionTask=>" + result.Message + "(request)", e.getMessage());
		}
		/*if (result.Message.contains("lang")) {
			result.Message = "服务器异常！";
		}*/
		return result;
	}

	/**
	 * 下载最新版本
	 * 
	 * @param currentUser
	 *            :当前用户
	 * @return
	 * @throws NameNotFoundException
	 */
	public ResultInfo<Boolean> downLoadLastVersion(UserInfo currentUser) throws NameNotFoundException {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			final String httpUrl = currentUser.LatestServer + "/apk/" + EIASApplication.DownLoadApkName;
			new Thread() {
				public void run() {
					try {
						downFile(httpUrl, FileUtil.getSysPathOfCompany(BaseApplication.getInstance().getString(R.string.app_name_company)));
						DataLogOperator.version("");
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (Exception e) {
			DataLogOperator.version(e.getMessage());
			result.Message = "服务器异常，下载失败。";
		}
		return result;

	}

	@SuppressWarnings("resource")
	public int downFile(String url, String path) throws IOException {
		// 下载函数
		String filename = url.substring(url.lastIndexOf("/") + 1);
		// 获取文件名
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		int fileSize = conn.getContentLength();// 根据响应获取文件大小
		if (fileSize <= 0) {
			throw new RuntimeException("无法获知文件大小 ");
		}
		if (is == null) {
			throw new RuntimeException("stream is null");
		}
		FileOutputStream fos = new FileOutputStream(path + filename);
		// 把数据存入路径+文件名
		byte buf[] = new byte[1024];
		int downLoadFileSize = 0;
		do {
			// 循环读取
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			downLoadFileSize += numread;
		} while (true);
		try {
			is.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex.fillInStackTrace().getMessage());
		}
		return downLoadFileSize;
	}
}
