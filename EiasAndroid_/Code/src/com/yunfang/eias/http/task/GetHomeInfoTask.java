package com.yunfang.eias.http.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.UserTaskInfo;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;

public class GetHomeInfoTask implements IRequestTask {

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
	public ResultInfo<UserTaskInfo> getResponseData() {
		ResultInfo<UserTaskInfo> result = new ResultInfo<UserTaskInfo>();

		if (mData != null) {
			String str = new String(mData);
			if (!TextUtils.isEmpty(str)) {
				try {
					JSONObject json = new JSONObject(str.toString());
					result = JSONHelper.parseObject(json, result.getClass());
					if (json.getString("Success").equals("false")) {
						result.Success = false;
						result.Message = json.getString("Message");
					} else {
						if (json.has("Others")) {
							String othersStr = json.getString("Others");
							result.Others = JSONHelper.parseCollection(
									othersStr, ArrayList.class,
									DataDefine.class);
							if (result.Others != null) {
								ArrayList<DataDefine> defines = (ArrayList<DataDefine>) result.Others;
								if (defines != null && defines.size() > 0) {
									for (DataDefine define : defines) {
										define.DDID = define.ID;
										define.ID = -1;
									}
									result.Others = defines;
								}
							}
						}
						if (result.Success) {
							str = json.getString("Data");
							json = new JSONObject(str.toString());
							result.Data = JSONHelper.parseObject(json,
									UserTaskInfo.class);
						}
					}
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "请检查网络！";
					DataLogOperator.taskHttp("GetHomeInfoTask=>"
							+ "服务器异常，获取数据失败" + "(getResponseData)",
							e.getMessage());
				}
			} else {
				result.Success = false;
				result.Message = "请检查网络！";
			}
		}

		return result;
	}

	/**
	 * 
	 * @param currentUser
	 * @return
	 */
	public ResultInfo<UserTaskInfo> request(UserInfo currentUser) {
		ResultInfo<UserTaskInfo> result = new ResultInfo<UserTaskInfo>();

		String url = currentUser.LatestServer + "/newapi/GetHomeData/";
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("token", currentUser.Token);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url,
				RequestTypeEnum.GET, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "请检查网络";
			DataLogOperator.taskHttp("GetHomeInfoTask=>获取主页面信息失败(request)",
					e.getMessage());
		}

		return result;
	}
}
