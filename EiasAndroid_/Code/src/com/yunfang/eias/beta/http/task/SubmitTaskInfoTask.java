package com.yunfang.eias.beta.http.task;

import java.util.Hashtable;

import org.json.JSONObject;

import com.yunfang.eias.beta.dto.TaskInfoDTO;
import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;

/**
 * 提交任务信息
 * 
 * @author 贺隽
 */
public class SubmitTaskInfoTask implements IRequestTask {

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
	public ResultInfo<TaskInfoDTO> request(UserInfo currentUser, TaskInfoDTO taskInfo, boolean additional) {
		ResultInfo<TaskInfoDTO> result = new ResultInfo<TaskInfoDTO>();
		String taskJson = JSONHelper.toJSON(taskInfo);
		String url = currentUser.LatestServer;
		if (additional) {
			url += "/apis/AdditionalResource";//TaskInfoSubmitReceiveOnlyResource";//
		} else {
			url += "/apis/UpdateOIData";//TaskInfoSubmitReceive";//
		}
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("token", currentUser.Token);
		params.put("taskinfo", taskJson);
		params.put("id", taskInfo.ID);
		params.put("tasknum", taskInfo.TaskNum);
		params.put("createdtime", taskInfo.CreatedDate);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "请检查网络，提交任务失败";
			DataLogOperator.taskHttp("SubmitTaskInfoTask=>提交任务失败(request)", e.getMessage());
		}
		return result;
	}

	/**
	 * 获取任务勘察信息
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public ResultInfo<TaskInfoDTO> getResponseData() {
		ResultInfo<TaskInfoDTO> result = new ResultInfo<TaskInfoDTO>();

		if (mData != null) {
			String dataString = new String(mData);
			try {
				JSONObject json = new JSONObject(dataString.toString());
				result = JSONHelper.parseObject(json, result.getClass());
				if (result.Success) {
					dataString = json.getString("Data");
					json = new JSONObject(dataString);
					result.Data = new TaskInfoDTO(json);
				}
			} catch (Exception e) {
				result.Success = false;
				result.Message = e.getMessage();
				DataLogOperator.taskHttp("SubmitTaskInfoTask=>提交任务失败(getResponseData)", e.getMessage());
			}
		} else {
			result.Success = false;
			result.Message = "没有返回数据";
		}

		return result;
	}

}
