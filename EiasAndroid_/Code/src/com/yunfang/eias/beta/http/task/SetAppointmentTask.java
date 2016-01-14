package com.yunfang.eias.beta.http.task;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.StringUtil;

/**
 * 设置任务收费信息
 * 
 * @author gorson
 * 
 */
public class SetAppointmentTask implements IRequestTask {
	/**
	 * 响应数据
	 * */
	private byte[] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		if (mData != null) {
			String str = new String(mData);
			if (!TextUtils.isEmpty(str)) {
				try {
					JSONObject json = new JSONObject(str.toString());
					if (json.getString("Success").equals("false")) {
						result.Success = false;
						result.Message = json.getString("Message");
					} else {
						result = JSONHelper.parseObject(json, result.getClass());
						result.Data = StringUtil.parseBoolean(json.getString("Data"));
					}
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "服务器异常，收费信息获取失败";
					DataLogOperator.taskHttp("SetTaskFeeTask=>设置收费信息失败(getResponseData)", e.getMessage());
				}
			} else {
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}

		return result;
	}

	/**
	 * 设置后台预约信息
	 * @param currentUser
	 * @param taskNum
	 * @param name
	 * @param tel
	 * @param dateTime
	 * @param remark
	 * @return
	 */
	public ResultInfo<Boolean> request(UserInfo currentUser, String taskNum, String name, String tel, String dateTime, String remark) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		String url = currentUser.LatestServer + "/apis/SetAppointmentDateTime/";
		Hashtable<String, Object> params = new Hashtable<String, Object>(4);
		params.put("pid", taskNum);
		params.put("contactPerson", name);
		params.put("contactTel", tel);
		params.put("appointmentDateTime", dateTime);
		params.put("remark", remark);
		params.put("token", currentUser.Token);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常，收费信息获取失败";
			DataLogOperator.taskHttp("SetTaskFeeTask=>设置收费信息失败(request)", e.getMessage());
		}

		return result;
	}
}
