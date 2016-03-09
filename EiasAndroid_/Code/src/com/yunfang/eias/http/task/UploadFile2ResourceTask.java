/**
 * 
 */
package com.yunfang.eias.http.task;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.logic.TaskItemControlOperator;
import com.yunfang.eias.model.FileInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.utils.ClientExtAPIUtils;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * @author kevin 图片上传到资源库
 */
public class UploadFile2ResourceTask {

	private Map<String, String> resIdMap = new HashMap<>();

	/** 资源库地址 */
	private String resUrl = "";
	/** 是否使用新版资源库 */
	private boolean newResourceLibrary = false;

	public UploadFile2ResourceTask(String resUrl, boolean newResourceLibrary) {
		this.newResourceLibrary = newResourceLibrary;
		this.resUrl = resUrl;
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
	public ResultInfo<Map<String, String>> request2(UserInfo currentUser,
			TaskInfo taskInfo, //
			ArrayList<TaskDataItem> taskDataItems, boolean additional) {//
		ResultInfo<Map<String, String>> result = new ResultInfo<Map<String, String>>();
		try {
			List<FileInfo> jpgEntities = getMultipartEntitys(taskInfo,
					taskDataItems, ".jpg");
			List<FileInfo> amrEntities = getMultipartEntitys(taskInfo,
					taskDataItems, ".amr");
			List<FileInfo> mp4Entities = getMultipartEntitys(taskInfo,
					taskDataItems, ".mp4");
			Boolean jpgResult = upLoadFile(taskInfo, taskDataItems.get(0).Name,
					jpgEntities, "jpg");
			Boolean amrResult = upLoadFile(taskInfo, taskDataItems.get(0).Name,
					amrEntities, "amr");
			Boolean mp4Result = upLoadFile(taskInfo, taskDataItems.get(0).Name,
					mp4Entities, "mp4");

			// 只有所有类型的资源文件都上传成功才算资源提交成功， 否者提交失败，请求上传失败的已提交任务资源文件
			if (!jpgResult || !amrResult || !mp4Result) {
				result.Success = false;
			} else {
				result.Success = true;
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskHttp("UploadFileTask=>上传文件失败(request)",
					e.getMessage());
		} finally {
			// 无论失败，成功都应该反回，文件资源库信息
			result.Data = resIdMap;
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
	 * @return List<FileInfo> 文件信息列表
	 */
	private List<FileInfo> getMultipartEntitys(TaskInfo taskInfo,
			ArrayList<TaskDataItem> taskDataItems, String fileExt) {

		List<FileInfo> result = new ArrayList<FileInfo>();

		for (TaskDataItem item : taskDataItems) {

			if (item.Value == null || item.Value.equals("null")
					|| item.Value.length() <= 0)
				continue;

			String value = "";
			String typeName = item.Name;
			String[] tempFiles = item.Value.split(";");
			if (tempFiles.length > 0) {
				for (String filePath : tempFiles) {
					FileInfo taskFile = new FileInfo();
					if (filePath.contains(".jpg") && fileExt.equals(".jpg")) {
						taskFile.setFileSuffix("jpg");
						value = TaskItemControlOperator.mkResourceDir(
								taskInfo.TaskNum, EIASApplication.photo)
								+ File.separator + filePath;
					} else if (filePath.contains(".amr")
							&& fileExt.equals(".amr")) {
						taskFile.setFileSuffix("amr");
						value = TaskItemControlOperator.mkResourceDir(
								taskInfo.TaskNum, EIASApplication.audio)
								+ File.separator + filePath;
					} else if (filePath.contains(".mp4")
							&& fileExt.equals(".mp4")) {
						taskFile.setFileSuffix("mp4");
						value = TaskItemControlOperator.mkResourceDir(
								taskInfo.TaskNum, EIASApplication.video)
								+ File.separator + filePath;
					}
					// 填充文件默认的属性
					if (value.length() > 0) {
						taskFile.setFilePath(value);
						taskFile.setFileTaskInfo(taskInfo);
						taskFile.setFileTypeName(typeName);
						taskFile.setFileDataId(item.ID);
						// taskFile.setBaseCategoryID(item.BaseCategoryID);
						taskFile.setBaseCategoryID(item.CategoryID);
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
	@SuppressWarnings("deprecation")
	private Boolean upLoadFile(TaskInfo taskInfo, String typeTile,
			List<FileInfo> fileEntities, String format) {
		Boolean result = true;
		String url = "";
		String resultStr = "";
		String fileName = "";
		try {
			for (FileInfo fileInfo : fileEntities) {
				/*
				 * http://123.57.6.154:9000/ExtAPI/extend/Create旧版资源库 测试版 ？
				 * http://res.yunfangdata.com/ExtAPI/extend/Create //旧版资源库 正式版
				 * http
				 * ://123.56.118.10:8082/ResourcesPoolWrite/ExtAPI/extend/Creat
				 * 新版资源库 "http://192.168.3.83:9000"// 帅建服务器
				 */
				if (TextUtils.isEmpty(resUrl)) {// 资源库地址为空，提交资源失败
					return false;
				}
				url = resUrl// "http://123.56.118.10:8082/ResourcesPoolWrite"//
						+ "/ExtAPI/extend/Create?"
						+ "title="
						+ URLEncoder.encode(fileInfo.getFileTypeName()) + // 标题
						"&systemName=" + URLEncoder.encode("外采系统") + // 系统名
						"&belongID=" + taskInfo.TaskID + // 任务ID 对应后台任务ID
						"&belongPID=" + taskInfo.ID + // 任务ID 客户段任务ID
						// "&format="+format+
						"&belongDesc=" + taskInfo.TaskNum;// 任务编号//
				File file = new File(fileInfo.getFilePath());
				if (file.exists()) {
					fileName = file.getName();
				}
				String urlStr = new String(url.getBytes(), "UTF-8");
				// 读取本地文件转换为 DataInputStream
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(file));
				// InputStream bis=new FileInputStream(file);
				DataInputStream dip = new DataInputStream(bis);
				resultStr = ClientExtAPIUtils.send(urlStr, fileName, dip);

				int resId = this.newResourceLibrary ? newResResult(resultStr,//
						fileName) : oldResResult(resultStr, fileName);
				if (resId != -1 && resId != 0) {// 返回错误消息描述，且文件编号为0，
					fileInfo.setResId(resId);// 上传成功，获取资源库的文件ID
					// resId 记录到全局Map 中
					setResMap(fileInfo);
				} else {// 有一个文件上传失败， 取消任务提交
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
			DataLogOperator.taskHttp("UploadFileTask=>" + fileName, "图片上传出错："
					+ e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/***
	 * 处理旧资源库上传后的返回
	 * 
	 * @param resultString
	 *            返回结果JSON
	 * @param fileName
	 *            文件名
	 * @throws JSONException
	 */
	private int oldResResult(String resultString, String fileName)
			throws JSONException {
		try {
			JSONObject resultJsonObj = new JSONObject(resultString);
			JSONObject statJson = new JSONObject(resultJsonObj.get("Status")
					.toString());
			if (statJson.getBoolean("Success")) {
				int resId = resultJsonObj.getInt("ResultData");
				return resId;
			} else {// 有一个文件上传失败， 取消任务提交
				DataLogOperator.taskHttp("UploadFileTask=>" + fileName,
						"返回结果代码失败：" + statJson.getString("Message"));
				return -1;
			}
		} catch (Exception e) {
			DataLogOperator.taskHttp("UploadFileTask=>" + fileName, "返回结果代码失败："
					+ e.toString());
			return -1;
		}
	}

	/***
	 * 新版资源库返回结果处理
	 * 
	 * @param resultString
	 * @param fileName
	 * @throws JSONException
	 */
	private int newResResult(String resultString, String fileName)
			throws JSONException {
		try {
			JSONObject resultJsonObj = new JSONObject(resultString);
			if (resultJsonObj.getBoolean("success")) {
				int resId = resultJsonObj.getInt("data");
				return resId;
			} else {
				DataLogOperator.taskHttp("UploadFileTask=>" + fileName,
						"返回结果代码失败：" + resultJsonObj.getString("msg"));
				return -1;
			}
		} catch (Exception e) {
			DataLogOperator.taskHttp("UploadFileTask=>" + fileName, "返回结果代码失败："
					+ e.toString());
			return -1;
		}
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

	// /**
	// * 开始发送文件 MultipartEntity 组件来上传文件
	// *
	// * @return
	// */
	// @SuppressWarnings({ "deprecation", "unused" })
	// private Boolean resPonseFile(TaskInfo taskInfo, HttpClient client,
	// HttpPost httpPost, List<String> jpgEntities, String fileExt) {
	// Boolean result = false;
	// HttpResponse response;
	// Integer successCount = 0;
	// for (String value : jpgEntities) {
	// File file = new File(value);
	// try {
	// if (file.exists()) {
	// MultipartEntity reqEntity = new MultipartEntity();
	// reqEntity.addPart(file.getName(), new FileBody(file,
	// URLEncoder.encode(file.getName()), "", "UTF-8"));
	// httpPost.setEntity(reqEntity);
	// // 不支持续传
	// response = client.execute(httpPost);
	// int statusCode = response.getStatusLine().getStatusCode();
	// if (statusCode == 200) {
	// successCount += 1;
	// byte[] b = inputStreamToByte(response.getEntity()
	// .getContent());
	// String resultStr = new String(b);
	// } else {
	// byte[] b = inputStreamToByte(response.getEntity()
	// .getContent());
	// String resultStr = new String(b);
	// DataLogOperator
	// .taskHttp("UploadFileTask=>" + file.getName(),
	// "返回结果代码失败");
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// DataLogOperator.taskHttp("UploadFileTask=>" + file.getName(),
	// e.getMessage());
	// }
	// }
	// if (successCount.equals(jpgEntities.size())) {
	// result = true;
	// DataLogOperator.fileUpload(taskInfo, fileExt,
	// successCount.toString(), "");
	// }
	// return result;
	// }

	// /**
	// * inputstream流换成byte []
	// */
	// private static byte[] inputStreamToByte(InputStream in) {
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// int len = 0;
	// byte[] byteArray = null;
	// byte[] b = new byte[1024];
	// try {
	// while ((len = in.read(b, 0, b.length)) != -1) {
	// bos.write(b, 0, len);
	// }
	// byteArray = bos.toByteArray();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return byteArray;
	//
	// }

}
