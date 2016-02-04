package com.yunfang.eias.http.task;

import java.util.Hashtable;

import org.json.JSONObject;


import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.StringUtil;

/**
 * 推送当前坐标发送到服务器
 * 
 * @author 贺隽
 */
public class PushCoordinateTask implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/**
	 * 推送当前坐标发送到服务器
	 * @param currentUser:当前用户
	 * @param lat:纬度
	 * @param lng:经度
	 * @return
	 */
	public ResultInfo<Boolean> request(UserInfo currentUser, double lat,double lng,String city) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();		
		
		String url = currentUser.LatestServer + "/apis/GetCoordinate";
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(9);
		params.put("token", currentUser.Token);
		params.put("id", currentUser.ID);
		params.put("account", currentUser.Account);
		params.put("name", currentUser.Name);
		params.put("lat", lat);
		params.put("lng", lng);
		params.put("city", city);
		params.put("time", DateTimeUtil.getCurrentTime());
		params.put("maptype", "baidumap");
		params.put("device", EIASApplication.deviceInfo.DeviceId);
		CommonRequestPackage requestPackage = new CommonRequestPackage(url,
				RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();			
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常";
		}

		return result;
	}

	/**
	 * 获取任务勘察信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		if (mData != null) {
			String dataString = new String(mData);
			try {
				JSONObject json = new JSONObject(dataString.toString());
				result = JSONHelper.parseObject(json, result.getClass());
				if(result.Success){
					dataString = json.getString("Data");
					if(dataString != null && !dataString.equals("null")){
						json = new JSONObject(dataString);
						result.Data = StringUtil.parseBoolean(json.getString("Data"));
					}
				}else{
					result.Success = false; 
					result.Message = json.getString("Message");
				}
			} catch (Exception e) {
				result.Success = false;
				result.Message = "服务器异常，发送坐标失败";
				DataLogOperator.taskHttp("GetTaskInfoTask=>推送当前坐标发送到服务器失败(getResponseData)",e.getMessage());
			}
		} else {
			result.Success = false;
			result.Message = "没有返回数据";

		}
		return result;
	}
}
