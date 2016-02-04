package com.yunfang.eias.http.task;

import java.util.Hashtable;

import org.json.JSONObject;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.*;
import com.yunfang.framework.model.*;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;

/**
 * 获取一个完整的任务信息
 * 
 * @author 贺隽
 */
public class GetTaskInfoTask implements IRequestTask {

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
	 * @param currentUser:当前用户信息
	 * @param dataDefine：任务主表信息
	 * @return
	 */
	public ResultInfo<TaskInfo> request(UserInfo currentUser, TaskInfo taskInfo) {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();

		String url = currentUser.LatestServer + "/apis/ReceiveTask";
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("token", currentUser.Token);
		params.put("taskId", taskInfo.TaskID);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url,
				RequestTypeEnum.GET, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();			
			DataLogOperator.taskDataSynchronization(taskInfo,"");
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常，任务同步失败";
			DataLogOperator.taskDataSynchronization(taskInfo,e.getMessage());		
		}

		return result;
	}

	/**
	 * 获取任务勘察信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<TaskInfo> getResponseData() {
		ResultInfo<TaskInfo> result = new ResultInfo<TaskInfo>();
		if (mData != null) {
			String dataString = new String(mData);
			try {
				JSONObject json = new JSONObject(dataString.toString());
				result = JSONHelper.parseObject(json, result.getClass());
				if(result.Success){
					dataString = json.getString("Data");
					if(dataString != null && !dataString.equals("null")){
						if(json.getString("Success").equals("false")){
							result.Success = false; 
							result.Message = json.getString("Message");
						}else{
							json = new JSONObject(dataString);
							result.Data = new TaskInfo(json);
							if(result.Data!= null ){
								result.Data.ID = result.Data.TaskID;
								result.Data.IsNew = false;
							}
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
