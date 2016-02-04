package com.yunfang.eias.http.task;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.TaskInfo;
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
 * @author gorson
 *
 */
public class SetTaskFeeTask implements IRequestTask {
	/**
	 * 响应数据
	 * */
	private byte [] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		if(mData!=null){
			String str=new String(mData);
			if(!TextUtils.isEmpty(str)){
				try {
					JSONObject json=new JSONObject(str.toString());
					if(json.getString("Success").equals("false")){
						result.Success = false; 
						result.Message = json.getString("Message");
					}else{
						result=JSONHelper.parseObject(json, result.getClass());		
						result.Data = StringUtil.parseBoolean(json.getString("Data"));
					}
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "服务器异常，获取数据失败";
					DataLogOperator.taskHttp("SetTaskFeeTask=>设置收费信息失败(getResponseData)",e.getMessage());
				}
			}else{
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}

		return result;
	}

	/**
	 * 调用后台服务设置任务的收费信息
	 * @param currentUser：当前用户信息
	 * @param taskInfo：任务信息
	 * @return
	 */
	public ResultInfo<Boolean> request(UserInfo currentUser,TaskInfo taskInfo){
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();

		String url=  currentUser.LatestServer + "/apis/UpdateFeeByPID/"; 
		Hashtable<String, Object> params = new Hashtable<String, Object>(4);
		params.put("pid", taskInfo.TaskNum);
		params.put("receiptNo", taskInfo.ReceiptNo);
		params.put("fee", taskInfo.Fee);
		params.put("token",currentUser.Token);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.GET,params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常，获取数据失败";
			DataLogOperator.taskHttp("SetTaskFeeTask=>设置收费信息失败(request)",e.getMessage());
		}

		return result;
	}
}
