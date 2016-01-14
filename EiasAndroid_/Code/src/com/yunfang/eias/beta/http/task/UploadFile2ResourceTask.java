/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.logic.TaskItemControlOperator;
import com.yunfang.eias.beta.model.FileInfo;
import com.yunfang.eias.beta.model.TaskDataItem;
import com.yunfang.eias.beta.model.TaskInfo;
import com.yunfang.eias.beta.utils.ClientExtAPIUtils;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * @author kevin 图片上传到资源库
 */
public class UploadFile2ResourceTask {

	/** 资源库地址 */
	private String resUrl = "";// "http://123.57.6.154:9000/";

	private Map<String, String> resIdMap = new HashMap<>();

	public UploadFile2ResourceTask(String resUrl) {

		/* this.resUrl = resUrl; */
		this.resUrl = "http://123.56.118.10:8082/ResourcesPoolWrite/";
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-10-16 下午3:59:26
	 * @Description: 请求资源库，上传文件
	 * @param currentUser
	 * @param taskInfo
	 * @param taskDataItems
	 * @param additional
	 * @return ResultInfo<Map<String,String>> 返回类型
	 */
	public ResultInfo<Map<String, String>> request2(UserInfo currentUser, TaskInfo taskInfo, //
			ArrayList<TaskDataItem> taskDataItems, boolean additional) {//
		ResultInfo<Map<String, String>> result = new ResultInfo<Map<String, String>>();
		try {
			List<FileInfo> jpgEntities = getMultipartEntitys(taskInfo, taskDataItems, ".jpg");
			List<FileInfo> amrEntities = getMultipartEntitys(taskInfo, taskDataItems, ".amr");
			List<FileInfo> mp4Entities = getMultipartEntitys(taskInfo, taskDataItems, ".mp4");
			Boolean jpgResult = upLoadFile(taskInfo, taskDataItems.get(0).Name, jpgEntities, "jpg");
			Boolean amrResult = upLoadFile(taskInfo, taskDataItems.get(0).Name, amrEntities, "amr");
			Boolean mp4Result = upLoadFile(taskInfo, taskDataItems.get(0).Name, mp4Entities, "mp4");

			// 只有所有类型的资源文件都上传成功才算资源提交成功， 否者提交失败，请求上传失败的已提交任务资源文件
			if (!jpgResult || !amrResult || !mp4Result) {
				result.Success = false;
			} else {
				result.Success = true;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(request)", e.getMessage());
		} finally {
			// 无论失败，成功都应该反回，文件资源库信息
			result.Data = resIdMap;
		}
		return result;
	}

	/*
	 * public ResultInfo<Boolean> request(UserInfo currentUser, TaskInfo
	 * taskInfo, // ArrayList<TaskDataItem> taskDataItems, boolean additional)
	 * {// ResultInfo<Boolean> result = new ResultInfo<Boolean>(); // 创建时间
	 * String createdTime = taskInfo.CreatedDate; createdTime =
	 * createdTime.substring(0, createdTime.indexOf(" ")); String url = ""; if
	 * (additional) { url = currentUser.ResourceLibraryDomainName +
	 * "/apis/AdditionalResource?id=0&tasknum=" + taskInfo.TaskNum +
	 * "&createdtime=" + createdTime + "&token=" + currentUser.Token; } else {
	 * // 资源库地址 url = currentUser.ResourceLibraryDomainName +
	 * "ExtAPI/extend/Create?" + "title=" + taskDataItems.get(0).Name +
	 * "&systemName=外采系统" + "&belongID=" + 222 + "&belongPID=333" +
	 * "&belongDesc=" + taskInfo.TaskNum;//+ "&format=jpg" } // post 的参数
	 * HttpParams parms = new BasicHttpParams(); parms.setParameter("charset",
	 * "UTF-8");
	 * 
	 * parms.setParameter("belongId",taskInfo.TargetNumber);//图片标示ID（任务ID）
	 * parms.setParameter("title","文件名称");//文件名称
	 * parms.setParameter("format",".jpg");// 文件格式
	 * parms.setParameter("belongDesc","");
	 * 
	 * 
	 * HttpConnectionParams.setConnectionTimeout(parms, 60 * 1000);
	 * HttpConnectionParams.setSoTimeout(parms, 60* 1000);//6000 HttpClient
	 * client = new DefaultHttpClient(parms);
	 * 
	 * HttpPost httpPost = new HttpPost(url); httpPost.setParams(parms);
	 * httpPost.addHeader("charset", "UTF-8");
	 * 
	 * 
	 * // 指定post方式提交编码 httpPost.setHeader("Connection", "Keep-Alive");
	 * httpPost.addHeader("charset", "UTF-8"); // 设置边界 String BOUNDARY =
	 * "----------" + System.currentTimeMillis();
	 * httpPost.addHeader("Content-Type",
	 * "multipart/form-data; boundary="+BOUNDARY);
	 * httpPost.addHeader("Content-Disposition", "form-data");
	 * 
	 * 
	 * // Content-Type
	 * 
	 * try { List<String> jpgEntities = getMultipartEntitys(taskInfo,
	 * taskDataItems, ".jpg"); List<String> amrEntities =
	 * getMultipartEntitys(taskInfo, taskDataItems, ".amr"); List<String>
	 * mp4Entities = getMultipartEntitys(taskInfo, taskDataItems, ".mp4");
	 * Boolean jpgResult = resPonseFile(taskInfo, client, httpPost, jpgEntities,
	 * "图片"); Boolean amrResult = resPonseFile(taskInfo, client, httpPost,
	 * amrEntities, "音频"); Boolean mp4Result = resPonseFile(taskInfo, client,
	 * httpPost, mp4Entities, "视频");
	 * 
	 * result.Success = false;// 测试 必须失败
	 * 
	 * if (!jpgResult||!amrResult||!mp4Result) { // result = getResponseData();
	 * //result.Success = true; result.Success = false; }
	 * 
	 * } catch (Exception e) { result.Success = false; result.Message =
	 * e.getMessage();
	 * DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(request)",
	 * e.getMessage()); } return result; }
	 */

	/**
	 * 把上传的文件转换为实体列表
	 * 
	 * @param taskInfo
	 *            :任务信息 有任务编号就好
	 * @param taskDataItems
	 *            :任务完整的子项信息
	 * @parma fileExt:需要文件的后缀 只能是 .jpg、.amr、.mp4
	 * @return List<FileInfo> 文件信息列表
	 */
	private List<FileInfo> getMultipartEntitys(TaskInfo taskInfo, ArrayList<TaskDataItem> taskDataItems, String fileExt) {

		List<FileInfo> result = new ArrayList<FileInfo>();

		for (TaskDataItem item : taskDataItems) {

			if (item.Value == null || item.Value.equals("null") || item.Value.length() <= 0)
				continue;

			String value = "";
			String typeName = item.Name;
			String[] tempFiles = item.Value.split(";");
			if (tempFiles.length > 0) {
				for (String filePath : tempFiles) {
					FileInfo taskFile = new FileInfo();
					if (filePath.contains(".jpg") && fileExt.equals(".jpg")) {
						taskFile.setFileSuffix("jpg");
						value = TaskItemControlOperator.mkResourceDir(taskInfo.TaskNum, EIASApplication.photo) + File.separator + filePath;
					} else if (filePath.contains(".amr") && fileExt.equals(".amr")) {
						taskFile.setFileSuffix("amr");
						value = TaskItemControlOperator.mkResourceDir(taskInfo.TaskNum, EIASApplication.audio) + File.separator + filePath;
					} else if (filePath.contains(".mp4") && fileExt.equals(".mp4")) {
						taskFile.setFileSuffix("mp4");
						value = TaskItemControlOperator.mkResourceDir(taskInfo.TaskNum, EIASApplication.video) + File.separator + filePath;
					}
					// 填充文件默认的属性
					if (value.length() > 0) {
						taskFile.setFilePath(value);
						taskFile.setFileTaskInfo(taskInfo);
						taskFile.setFileTypeName(typeName);
						taskFile.setFileDataId(item.ID);
						taskFile.setBaseCategoryID(item.BaseCategoryID);
						result.add(taskFile);
					}
				}
			}
		}

		return result;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-10-14 下午1:42:56
	 * @Description: 方法描述
	 * @param taskInfo
	 *            任务信息
	 * @param typeTile
	 * @param fileEntities
	 * @param format
	 *            文件格式 （ 后台接口暂时不需要这个参数 ）
	 * @return Boolean 是否所有文件都上传成功
	 */
	private Boolean upLoadFile(TaskInfo taskInfo, String typeTile, List<FileInfo> fileEntities, String format) {
		Boolean result = true;
		String url = "";
		String resultString = "";
		String fileName = "";
		try {
			for (FileInfo fileInfo : fileEntities) {
				// 资源库地址
				url = resUrl + "ExtAPI/extend/Create?" + // "http://123.57.6.154:9000/"
															// + resUrl
															// "http://192.168.3.83:9000/"
						"title=" + fileInfo.getFileTypeName() + // 标题
						"&systemName=外采系统" + // 系统名
						"&belongID=" + taskInfo.TaskID + // 任务ID 对应后台任务ID
						"&belongPID=" + taskInfo.ID + // 任务ID 客户段任务ID
						// "&format="+format+
						"&belongDesc=" + taskInfo.TaskNum;// 任务编号//
															// "&format="+format+
				File file = new File(fileInfo.getFilePath());
				if (file.exists()) {
					fileName = file.getName();
				}
				String urlStr = new String(url.getBytes(), "UTF-8");
				// 读取本地文件转换为 DataInputStream
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				// InputStream bis=new FileInputStream(file);
				DataInputStream dip = new DataInputStream(bis);
				resultString = ClientExtAPIUtils.send(urlStr, fileName, dip);

				JSONObject resultJsonObj = new JSONObject(resultString);
				JSONObject statJson = new JSONObject(resultJsonObj.get("Status").toString());
				if (statJson.getBoolean("Success")) {
					int resId = resultJsonObj.getInt("ResultData");
					fileInfo.setResId(resId);// 上传成功，获取资源库的文件ID
					// resId 记录到全局Map 中
					setResMap(fileInfo);
				} else {// 有一个文件上传失败， 取消任务提交
					DataLogOperator.taskHttp("UploadFileTask=>" + file.getName(), "返回结果代码失败：" + statJson.getString("Message"));
					return false;
				}
			}
			for (FileInfo fileInfo : fileEntities) {// 校验每个文件都获取到的 ResId
				if (!fileInfo.isUploadSussecces()) {
					return false;
				}
			}

		} catch (Exception e) {
			result = false;
			DataLogOperator.taskHttp("UploadFileTask=>" + fileName, "图片上传出错：" + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-10-15 下午4:13:17
	 * @Description: 记录上传成功后的文件信息
	 * @param fileInfo
	 *            文件信息
	 * @return void 返回类型
	 */
	private void setResMap(FileInfo fileInfo) {
		int baseCategoryId = fileInfo.getBaseCategoryID();
		int fileDtaId = fileInfo.getFileDataId();
		// 分类ID 和 分类项子项ID 作为KEY
		String key = baseCategoryId + "," + fileDtaId;
		int newResuId = fileInfo.getResId();// 资源ID
		if (resIdMap.containsKey(key)) {
			String oldValue = resIdMap.get(key);
			resIdMap.put(key, oldValue + ";" + newResuId);
		} else {
			resIdMap.put(key, newResuId + "");
		}
	}

	/**
	 * 开始发送文件 MultipartEntity 组件来上传文件
	 * 
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "unused" })
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
						successCount += 1;
						byte[] b = inputStreamToByte(response.getEntity().getContent());
						String resultStr = new String(b);
					} else {
						byte[] b = inputStreamToByte(response.getEntity().getContent());
						String resultStr = new String(b);
						DataLogOperator.taskHttp("UploadFileTask=>" + file.getName(), "返回结果代码失败");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				DataLogOperator.taskHttp("UploadFileTask=>" + file.getName(), e.getMessage());
			}
		}
		if (successCount.equals(jpgEntities.size())) {
			result = true;
			DataLogOperator.fileUpload(taskInfo, fileExt, successCount.toString(), "");
		}
		return result;
	}

	/**
	 * inputstream流换成byte []
	 */
	private static byte[] inputStreamToByte(InputStream in) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		byte[] byteArray = null;
		byte[] b = new byte[1024];
		try {
			while ((len = in.read(b, 0, b.length)) != -1) {
				bos.write(b, 0, len);
			}
			byteArray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;

	}

}
