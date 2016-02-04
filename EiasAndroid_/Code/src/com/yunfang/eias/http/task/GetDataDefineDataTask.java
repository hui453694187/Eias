package com.yunfang.eias.http.task;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.*;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.*;

/**
 * 获取一个完整的勘察匹配表信息
 * @author gorson
 *
 */
public class GetDataDefineDataTask implements IRequestTask {
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
	public ResultInfo<DataDefine> getResponseData() {
		ResultInfo<DataDefine> result = new ResultInfo<DataDefine>();

		if(mData!=null){
			String str=new String(mData);
			if(!TextUtils.isEmpty(str)){
				try {
					JSONObject json=new JSONObject(str.toString());
					if(json.getString("Success").equals("false")){
						result.Success = false; 
						result.Message = json.getString("Message");
					}
					else {
						json = new JSONObject(json.getString("Data"));
						result.Data = new DataDefine(json);
					}
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "服务器异常，获取勘察表失败";
					DataLogOperator.taskHttp("GetDataDefineDataTask=>获取勘察匹配表完整信息失败(getResponseData)", e.getMessage());
				}
			}else{
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}

		return result;
	}

	/**
	 * 获取指定勘察匹配表完整信息
	 * @param currentUser
	 * @param dataDefine
	 * @return	
	 */
	public ResultInfo<DataDefine> request(UserInfo currentUser,DataDefine dataDefine) {
		ResultInfo<DataDefine> result = new ResultInfo<DataDefine>();

		String url=  currentUser.LatestServer + "/apis/GetAllDataFieldDefine/"+dataDefine.DDID; 
		// 填充参数，key-value。key是接口要求传的变量名称	
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);		
		params.put("token",currentUser.Token);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.GET,params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message ="服务器异常，获取勘察表失败";
			DataLogOperator.taskHttp("GetDataDefineDataTask=>获取指定勘察匹配表完整信息失败(request)", e.getMessage());
		}

		return result;
	}
}
