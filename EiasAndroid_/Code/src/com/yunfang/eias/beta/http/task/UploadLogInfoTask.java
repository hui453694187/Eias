/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.beta.dto.DataLogDTO;
import com.yunfang.eias.beta.model.DataLog;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.StringUtil;

/** 记录手机端的日志信息到服务端中
 * @author Administrator
 *
 */
public class UploadLogInfoTask  implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte [] mData;
	
	/* 
	 * @see com.yunfang.framework.http.IRequestTask#setContext(byte[])
	 */
	@Override
	public void setContext(byte[] data) {
		// TODO 自动生成的方法存根
		this.mData = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean>  getResponseData() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		if(mData!=null){
			String str=new String(mData);
			if(!TextUtils.isEmpty(str)){
				try {
					JSONObject json=new JSONObject(str.toString());
					result=JSONHelper.parseObject(json, result.getClass());		
					result.Data = StringUtil.parseBoolean(json.getString("Data"));
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "网络繁忙";
				}
			}else{
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}
		return result;
	}
	
	public ResultInfo<Boolean> request(UserInfo currentUser,DataLog log){
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		DataLogDTO logDto = new DataLogDTO(log);
		String logDtoJson = JSONHelper.toJSON(logDto);
		String url=  currentUser.LatestServer + "/apis/SaveClientLogInfo";
		Hashtable<String, Object> params = new Hashtable<String, Object>(2);
		params.put("token",currentUser.Token);
		params.put("logdto",logDtoJson);
		params.put("userid", currentUser.ID);
		
		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST,params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "网络繁忙";
		}
		return result;
	}
	
}
