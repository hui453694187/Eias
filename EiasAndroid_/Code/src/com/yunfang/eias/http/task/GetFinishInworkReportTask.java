package com.yunfang.eias.http.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;

/**
 * 获取任务信息列表的数据，只拿主表信息
 * 
 * @author 贺隽
 * 
 */
public class GetFinishInworkReportTask implements IRequestTask {

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
	public ResultInfo<ArrayList<String>> getResponseData() {
		ResultInfo<ArrayList<String>> result = new ResultInfo<ArrayList<String>>();
		if (mData != null) {
			String str = new String(mData);
			if (!TextUtils.isEmpty(str)) {
				try {
					result.Data = new ArrayList<>();
					JSONObject json = new JSONObject(str.toString());
					if(!json.getString("Data").equals("null")){
						JSONArray arr = new JSONArray(json.getString("Data"));  
						for (int i = 0; i < arr.length(); i++) {  
							result.Data.add(arr.get(i).toString());	
						}
					}
					result.Success = json.getBoolean("Success");
					result.Message = json.getString("Message");
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "服务器异常，获取数据失败";
					DataLogOperator.taskHttp("GetTaskListTask=>" + result.Message + "(getResponseData)", e.getMessage());
				}
			} else {
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}
		return result;
	}

	/**
	 * 获取任务数据
	 * 
	 * @param currentUser
	 *            :当前用户，里面存有用户的Token值，用于与后台交互时的身份论证
	 * @param date
	 *            ：最后的报告同步时间
	 * @return
	 */
	public ResultInfo<ArrayList<String>> request(UserInfo currentUser, String date) {
		ResultInfo<ArrayList<String>> result = new ResultInfo<ArrayList<String>>();
		String url = currentUser.LatestServer + "/apis/GetTasksByFinishInworkReport/";
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("token", currentUser.Token);
		params.put("date", date);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常，获取任务数据失败";
			DataLogOperator.taskHttp("GetTasksByFinishInworkReport=>获取任务数据失败(request)", e.getMessage());
		}
		return result;
	}
}
