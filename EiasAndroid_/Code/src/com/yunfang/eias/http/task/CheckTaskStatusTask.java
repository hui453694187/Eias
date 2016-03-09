package com.yunfang.eias.http.task;

import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.JSONHelper;

public class CheckTaskStatusTask implements IRequestTask{

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/**
	 * 同步服务器任务状态
	 * 
	 * @param currentUser:当前用户信息
	 * @param dataDefine：任务主表信息
	 * @return
	 */
	public ResultInfo<HashMap<String, Integer>> request(String taskNums) {
		ResultInfo<HashMap<String, Integer>> result = new ResultInfo<HashMap<String, Integer>>();
		String url =EIASApplication.getCurrentUser().LatestServer + "/apis/CheckTaskStatus";// "http://123.57.152.44:8100/apis/CheckTaskStatus";//
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("pidstring", taskNums);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url,
				RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();	
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常，任务同步失败";
			//DataLogOperator.taskDataSynchronization(taskNums,e.getMessage());		
		}

		return result;
	}

	/**
	 * 获取任务勘察信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<HashMap<String, Integer>> getResponseData() {
		ResultInfo<HashMap<String, Integer>> result = new ResultInfo<HashMap<String, Integer>>();
		if (mData != null) {
			String dataString = new String(mData);
			try {
				JSONObject json = new JSONObject(dataString.toString());
				result = JSONHelper.parseObject(json, result.getClass());
				HashMap<String, Integer> map=new HashMap<>();
				if(result.Success){
					dataString = json.getString("Data");
					if(dataString != null && !dataString.equals("null")){
						if(json.getString("Success").equals("false")){
							result.Success = false; 
							result.Message = json.getString("Message");
						}else{
							JSONArray jsonArray = new JSONArray(dataString);
							for(int i=0;i<jsonArray.length();i++){
								JSONObject jsonObj=(JSONObject) jsonArray.get(i);
								String taskNumb=jsonObj.getString("PID");
								int status=jsonObj.getInt("Status");
								map.put(taskNumb, status);
							}
							result.Data=map;
						}
					}
				}
			} catch (Exception e) {
				result.Success = false;
				result.Message = "服务器异常，获取任务信息失败";
				DataLogOperator.taskHttp("GetTaskInfoTask=>获取任务勘察信息失败(getResponseData)",e.getMessage());
			}
		} else {
			result.Success = false;
			result.Message = "没有返回数据";

		}

		return result;
	}
}
