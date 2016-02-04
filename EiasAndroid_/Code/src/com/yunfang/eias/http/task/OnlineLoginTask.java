package com.yunfang.eias.http.task;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.dto.LoginEasyDto;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.logic.LoginInfoOperator;
import com.yunfang.framework.dto.UserInfoDTO;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.JSONHelper;

/**
 * 用户在线登录的任务
 * @author gorson
 *
 */
public class OnlineLoginTask implements IRequestTask {
	/**
	 * 响应数据
	 * */
	private byte [] mData;

	/**
	 * 用户在线登录任务
	 * @param userAccount  用户账号
	 * @param pwd  用户密码
	 * @param serverUrl 服务器要地址
	 * @return
	 */
	public ResultInfo<UserInfoDTO> request(String userAccount,String pwd,String serverUrl) {
		ResultInfo<UserInfoDTO> result = new ResultInfo<UserInfoDTO>();

		String url=  serverUrl + "/apis/LoginApi/"; 
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(4);
		params.put("UserAccount", userAccount);
		params.put("UserPwd", pwd);
		params.put("UserMobile", "");		
		String userVersion = LoginInfoOperator.getLoginInfo(userAccount).userVersion;
		params.put("UserVersion", userVersion);	
		params.put("apkname", EIASApplication.DownLoadApkName);
		params.put("versionname", EIASApplication.version.LocalVersionName);
		params.put("versioncode", EIASApplication.version.LocalVersionCode);		
		params.put("token","");

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.GET,params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "获取用户信息失败";
			DataLogOperator.taskHttp("OnlineLoginTask=>获取用户信息失败(request)",e.getMessage());
		}

		return result;
	}

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<UserInfoDTO> getResponseData() {
		ResultInfo<UserInfoDTO> result = new ResultInfo<UserInfoDTO>();

		if(mData!=null){
			String str=new String(mData);
			if(!TextUtils.isEmpty(str)){
				try {
					JSONObject json=new JSONObject(str.toString());
					JSONObject jsonVersion = new JSONObject(str.toString());					
					result=JSONHelper.parseObject(json, result.getClass());
					if(result.Success){
						json=new JSONObject(json.getString("Data"));
						result.Data= new UserInfoDTO(json);
						if(jsonVersion.has("Others"))
						{
							jsonVersion=new JSONObject(jsonVersion.getString("Others"));
							result.Others= new LoginEasyDto(jsonVersion);
						}
					}else{
						result.Success = false; 
						result.Message = json.getString("Message");
					}
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "请检查网络，获取用户信息失败";
					DataLogOperator.taskHttp("OnlineLoginTask=>获取用户信息失败(getResponseData)",e.getMessage());
				}
			}else{
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}
		return result;
	}
}
