package com.yunfang.eias.http.task;


import org.json.JSONObject;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
/***
 * 获取后台资源库地址配置
 * @author Kevin
 *
 */
public class GetResLibConfigTask implements IRequestTask {

	private byte[] data;

	@Override
	public void setContext(byte[] data) {
		this.data = data;

	}

	public ResultInfo<JSONObject> request() {
		ResultInfo<JSONObject> result = null;
		String server = EIASApplication.getCurrentUser().LatestServer;
		String url = server + "/apis/GetResourceLibraryInfo";

		CommonRequestPackage requestPackage = new CommonRequestPackage(url,
				RequestTypeEnum.GET, null);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result=new ResultInfo<JSONObject>();
			result.Success = false;
			result.Message = "服务器异常";
			DataLogOperator.taskHttp("SetTaskPauseTask=>获取资源库配置(request)",
					e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<JSONObject> getResponseData() {
		ResultInfo<JSONObject> result = new ResultInfo<JSONObject>();
		try {
			String resultStr = new String(this.data);
			JSONObject json = new JSONObject(resultStr);
			if (json.getBoolean("Success")) {
				JSONObject dataJson = json.getJSONObject("Data");
				result.Data = dataJson;
			} else {
				result.Success = false;
				result.Message = json.getString("Message");
			}

		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常！";

		}
		return result;
	}

}
