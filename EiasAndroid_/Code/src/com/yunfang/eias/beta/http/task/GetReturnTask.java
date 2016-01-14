/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;


import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * @author kevin
 *  获取当前已经暂停的任务任务编号
 */
public class GetReturnTask implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	/* 
	 * 
	 */
	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/***
	 * 
	 * @param user
	 * @return
	 */
	public ResultInfo<String> getReturnTadkInfo(UserInfo user) {
		ResultInfo<String> result = new ResultInfo<String>();
		String url = user.LatestServer + "/apis/GetReturnTask";
		Hashtable<String, Object> params = new Hashtable<String, Object>(4);
		params.put("token", user.Token);
		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message ="服务器异常，获取暂停任务编号失败";
			// 搜集错误日志
			DataLogOperator.taskHttp("CheckVersionTask=>" + "服务器异常，获取暂停任务编号失败" + "(request)", e.getMessage());
		}

		return result;
	}

	/*
	 * 处理返回的JSON 数据
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<String> getResponseData() {
		ResultInfo<String> result = new ResultInfo<String>();
		try {
			if (mData != null) {
				String resultStr = new String(mData);
				JSONObject resultJsonObj = new JSONObject(resultStr);
				if(resultJsonObj.getString("Success").equals("false")){
					result.Success=false;
				}else{
					result.Data=resultJsonObj.getString("Data");
				}
				result.Message=resultJsonObj.getString("Message");
			}
		} catch (JSONException e) {
			result.Success = false;
			result.Message ="服务器异常，获取暂停任务编号失败";
			DataLogOperator.taskHttp("GetReturnTask 获取暂停任务编号=>(getResponseData)", e.getMessage());
		}

		return result;
	}

}
