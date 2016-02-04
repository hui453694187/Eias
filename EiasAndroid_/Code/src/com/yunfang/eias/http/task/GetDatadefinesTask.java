package com.yunfang.eias.http.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;


import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;

/**
 * 获取最新的勘察配置表
 * 
 * @author kevin
 */
public class GetDatadefinesTask implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/**
	 * 获取指定勘察任务表完整信息
	 * 
	 * @param currentUser
	 *            :当前用户信息
	 * @param dataDefine
	 *            ：任务主表信息
	 * @return
	 */
	public ResultInfo<ArrayList<DataDefine>> request(UserInfo currentUser) {
		ResultInfo<ArrayList<DataDefine>> result = new ResultInfo<ArrayList<DataDefine>>();

		String url = currentUser.LatestServer + "/apis/GetDatadefines/";
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("token", currentUser.Token);
		// params.put("companyId",currentUser.CompanyID);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
			// DataLogOperator.taskDataSynchronization(taskInfo,"");
		} catch (Exception e) {
			result.Success = false;
			result.Message ="服务器异常，服务器异常，获取勘察表失败";
			DataLogOperator.taskHttp("GetDataDefineDataTask=>获取所有勘察匹配表失败(request)", e.getMessage());
		}

		return result;
	}

	/**
	 * 获取任务勘察信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<ArrayList<DataDefine>> getResponseData() {
		ResultInfo<ArrayList<DataDefine>> result = new ResultInfo<ArrayList<DataDefine>>();
		if (mData != null) {
			String dataString = new String(mData);
			try {
				JSONObject json = new JSONObject(dataString.toString());
				result = JSONHelper.parseObject(json, result.getClass());
				if (result.Success) {
					dataString = json.getString("Data");
					if (dataString != null && !dataString.equals("null")) {
						if (json.getString("Success").equals("false")) {
							result.Success = false;
							result.Message = json.getString("Message");
						} else {
							JSONArray jsonArray = new JSONArray(dataString);
							ArrayList<DataDefine> tempDTO = new ArrayList<DataDefine>();
							tempDTO = (ArrayList<DataDefine>) JSONHelper.parseCollection(jsonArray, ArrayList.class, DataDefine.class);
							
							if (tempDTO != null && tempDTO.size() > 0 && tempDTO.get(0) != null) {
								result.Data = tempDTO;
							}
						}
					}
				}
			} catch (Exception e) {
				result.Success = false;
				result.Message ="服务器异常，获取勘察配置表数据失败";
				DataLogOperator.taskHttp("GetTaskInfoTask=>获取勘察配置表失败(getResponseData)", e.getMessage());
			}
		} else {
			result.Success = false;
			result.Message = "没有返回数据";

		}

		return result;
	}
}
