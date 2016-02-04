/**
 * 
 */
package com.yunfang.eias.http.task;

import java.util.Hashtable;
import org.json.JSONObject;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.framework.dto.UserInfoDTO;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;

/**
 * 上传用户信息
 * 
 * @author 陈惠森
 */
public class UploadUserInfoTask  implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	/* 
	 * @see com.yunfang.framework.http.IRequestTask#setContext(byte[])
	 */
	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}
	
	/**
	 * 上传图片信息
	 * @param currentUser 当前用户对象
	 * @param userInfoDto 用户信息
	 * @return
	 */
	public ResultInfo<Boolean> request(UserInfo currentUser,UserInfoDTO userInfoDto){
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		String url=  currentUser.LatestServer + "/apis/SaveUserInfo/";
		String userInfoDtoJson = JSONHelper.toJSON(userInfoDto);
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("userinfodto",userInfoDtoJson); 
		params.put("token",currentUser.Token); 
		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST,params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "请检查网络";
		}
		return result;
	}

	/* 
	 * 上传用户信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		if (mData != null) {
			String dataString = new String(mData);
			try {
				JSONObject json = new JSONObject(dataString.toString());
				if (json.getBoolean("Success"))
				{
					result = JSONHelper.parseObject(json, result.getClass());		
					result.Data = json.getBoolean("Data");
				}
				else {
					result.Success = false;
					result.Data =false;
					result.Message = json.getString("Message");
				}
			} catch (Exception e) {
				result.Success = false;
				result.Data =false;
				result.Message = "请检查网络";
				DataLogOperator.taskHttp("UploadUserInfoTask=>上传用户信息失败(getResponseData)",e.getMessage());
			}
		} else {
			result.Success = false;
			result.Data =false;
			result.Message = "没有返回数据";
		}
		return result;
	}
	
}
