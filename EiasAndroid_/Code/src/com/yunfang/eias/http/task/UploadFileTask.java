package com.yunfang.eias.http.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.logic.TaskItemControlOperator;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.StringUtil;
import com.yunfang.framework.httpClient.IRequestTask;

/**
 * 上传文件
 * 
 * @author 贺隽
 */
public class UploadFileTask implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/**
	 * 获取指定勘察任务表完整信息
	 * 
	 * @param currentUser
	 *            :当前用户信息
	 * @param dataDefine
	 *            ：任务主表信息
	 * @return
	 */
	public ResultInfo<Boolean> request(UserInfo currentUser, TaskInfo taskInfo, ArrayList<TaskDataItem> taskDataItems, boolean additional) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		String createdTime = taskInfo.CreatedDate;
		createdTime = createdTime.substring(0, createdTime.indexOf(" "));
		String url = "";
		if (additional) {
			url = currentUser.LatestServer + "/apis/AdditionalResource?id=0&tasknum=" + taskInfo.TaskNum + "&createdtime=" + createdTime + "&token=" + currentUser.Token;
		} else {
			url = currentUser.LatestServer + "/apis/UpdateOIData?id=0&tasknum=" + taskInfo.TaskNum + "&createdtime=" + createdTime + "&token=" + currentUser.Token;
		}

		HttpParams parms = new BasicHttpParams();
		parms.setParameter("charset", "UTF-8");
		HttpConnectionParams.setConnectionTimeout(parms, 60 * 1000);
		HttpConnectionParams.setSoTimeout(parms, 6000 * 1000);
		HttpClient client = new DefaultHttpClient(parms);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setParams(parms);
		// 指定post方式提交编码
		httpPost.addHeader("charset", "UTF-8");
		try {
			List<String> jpgEntities = getMultipartEntitys(taskInfo, taskDataItems, ".jpg");
			List<String> amrEntities = getMultipartEntitys(taskInfo, taskDataItems, ".amr");
			List<String> mp4Entities = getMultipartEntitys(taskInfo, taskDataItems, ".mp4");
			Boolean jpgResult = resPonseFile(taskInfo, client, httpPost, jpgEntities, "图片");
			Boolean amrResult = resPonseFile(taskInfo, client, httpPost, amrEntities, "音频");
			Boolean mp4Result = resPonseFile(taskInfo, client, httpPost, mp4Entities, "视频");
			if (jpgResult && amrResult && mp4Result) {
				result.Success = true;
			} else {
				result.Success = false;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = "上传文件失败";
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(request)", e.getMessage());
		}
		return result;
	}

	/**
	 * 开始发送文件
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Boolean resPonseFile(TaskInfo taskInfo, HttpClient client, HttpPost httpPost, List<String> jpgEntities, String fileExt) {
		Boolean result = false;
		HttpResponse response;
		Integer successCount = 0;
		for (String value : jpgEntities) {
			File file = new File(value);
			try {
				if (file.exists()) {
					MultipartEntity reqEntity = new MultipartEntity();
					reqEntity.addPart(file.getName(), new FileBody(file, URLEncoder.encode(file.getName()), "", "UTF-8"));
					httpPost.setEntity(reqEntity);
					// 不支持续传
					response = client.execute(httpPost);
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == 200) {
						String resultStr = retrieveInputStream(response.getEntity());
						ResultInfo<Boolean> resultInfo = responseToResultInfo(resultStr);
						//上传图片是否成功   
						if (resultInfo.Success && resultInfo.Data) {
							successCount += 1;
						} else {
							return false;
						}
					} else {
						DataLogOperator.taskHttp("UploadFileTask=>" + file.getName(), "返回结果代码失败");
						return false;
					}
				}
			} catch (Exception e) {
				DataLogOperator.taskHttp("UploadFileTask=>" + file.getName(), e.getMessage());
				return false;
			}
		}
		if (successCount.equals(jpgEntities.size())) {
			result = true;
			DataLogOperator.fileUpload(taskInfo, fileExt, successCount.toString() + ":" + jpgEntities.size(), "");
		}
		return result;
	}

	/**
	 * 把上传的文件转换为实体列表
	 * 
	 * @param taskInfo
	 *            :任务信息 有任务编号就好
	 * @param taskDataItems
	 *            :任务完整的子项信息
	 * @parma fileExt:需要文件的后缀 只能是 .jpg、.amr、.mp4
	 * @return
	 */
	private List<String> getMultipartEntitys(TaskInfo taskInfo, ArrayList<TaskDataItem> taskDataItems, String fileExt) {

		List<String> result = new ArrayList<String>();

		for (TaskDataItem item : taskDataItems) {

			if (item.Value == null || item.Value.equals("null") || item.Value.length() <= 0)
				continue;

			String value = "";
			String[] tempFiles = item.Value.split(";");
			if (tempFiles.length > 0) {
				for (String filePath : tempFiles) {
					if (filePath.contains(".jpg") && fileExt.equals(".jpg")) {
						value = TaskItemControlOperator.mkResourceDir(taskInfo.TaskNum, EIASApplication.photo) + File.separator + filePath;
					} else if (filePath.contains(".amr") && fileExt.equals(".amr")) {
						value = TaskItemControlOperator.mkResourceDir(taskInfo.TaskNum, EIASApplication.audio) + File.separator + filePath;
					} else if (filePath.contains(".mp4") && fileExt.equals(".mp4")) {
						value = TaskItemControlOperator.mkResourceDir(taskInfo.TaskNum, EIASApplication.video) + File.separator + filePath;
					}
					if (value.length() > 0) {
						result.add(value);
					}
				}
			}
		}

		return result;
	}

	/**
	 * 回调方法
	 * 解析图片上传后返回的JSON数据 
	 * @param responseStr
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public ResultInfo<Boolean> responseToResultInfo(String responseStr) {
		ResultInfo result = new ResultInfo();
		try {
			JSONObject resultData = new JSONObject(responseStr);
			if (resultData == null) {
				result.Success = false;
				result.Message = "";
			} else {
				result.Success = resultData.getBoolean("Success");
				result.Message = resultData.getString("Message");
				result.Data = resultData.getBoolean("Data");
				if (resultData.has("Others")) {
					result.Others = resultData.getString("Others");
				} else {
					result.Others = "";
				}
				Log.i(UploadFileTask.class.getSimpleName(), resultData.getString("Data"));
			}
		} catch (JSONException e) {
			result.Success = false;
			result.Message = e.getMessage();
			e.printStackTrace();
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(responseToResultInfo)", e.getMessage());
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			e.printStackTrace();
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(responseToResultInfo)", e.getMessage());
		}
		return result;
	}

	/**
	 * 处理httpResponse信息,返回String
	 * 
	 * @param httpEntity
	 * @return String
	 */
	public String retrieveInputStream(HttpEntity httpEntity) {
		int length = (int) httpEntity.getContentLength();
		if (length < 0)
			length = 10000;
		StringBuffer stringBuffer = new StringBuffer(length);
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(UploadFileTask.class.getSimpleName(), e.getMessage());
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(retrieveInputStream)", e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(UploadFileTask.class.getSimpleName(), e.getMessage());
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(retrieveInputStream)", e.getMessage());
		} catch (IOException e) {
			Log.e(UploadFileTask.class.getSimpleName(), e.getMessage());
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(retrieveInputStream)", e.getMessage());
		}
		return stringBuffer.toString();
	}

	/**
	 * 获取任务勘察信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		if (mData != null) {
			String dataString = new String(mData);
			try {
				JSONObject json = new JSONObject(dataString.toString());
				result = JSONHelper.parseObject(json, result.getClass());
				result.Data = StringUtil.parseBoolean(json.getString("Data"));
			} catch (Exception e) {
				result.Success = false;
				result.Message = e.getMessage();
				DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(getResponseData)", e.getMessage());
			}
		} else {
			result.Success = false;
			result.Message = "没有返回数据";

		}
		return result;
	}
}
