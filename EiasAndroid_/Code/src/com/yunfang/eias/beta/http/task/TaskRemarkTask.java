/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.util.Hashtable;

import org.json.JSONObject;

import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.model.TaskInfo;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * @author kevin 添加任务备注
 */
public class TaskRemarkTask implements IRequestTask {

	private byte[] mData = null;

	public ResultInfo<Boolean> request(UserInfo user, TaskInfo taskInfo) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			String url = user.LatestServer + "/apis/SetTaskRemark";

			Hashtable<String, Object> parameterMap = new Hashtable<String, Object>();
			parameterMap.put("Token", user.Token);
			parameterMap.put("taskRemark", taskInfo.TaskRemark);
			parameterMap.put("taskNum", taskInfo.TaskNum);

			CommonRequestPackage crp = new CommonRequestPackage(url, RequestTypeEnum.POST, parameterMap);

			YFHttpClient.request(crp, this);
			result = this.getResponseData();
		} catch (NullPointerException e) {
			result.Success = false;
			result.Message = "任务操作中！";
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常！";
		}
		return result;
	}

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			if (this.mData != null) {
				String resultStr = new String(this.mData);
				JSONObject jsonObjRes = new JSONObject(resultStr);
				if (!jsonObjRes.getString("Success").equals("false")) {
					result.Success = true;
					result.Data = jsonObjRes.getBoolean("Data");
					result.Message = jsonObjRes.getString("Message");
				} else {
					result.Success = false;
					result.Data=false;
					result.Message = jsonObjRes.getString("Message");
				}
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器繁忙，稍后再试！";
			DataLogOperator.taskHttp("TaskRemarkTask=>备注任务失败(getResponseData)", e.getMessage());
		}

		return result;
	}

}
