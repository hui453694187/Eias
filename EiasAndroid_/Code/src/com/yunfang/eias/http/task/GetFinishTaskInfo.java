/**
 * 
 */
package com.yunfang.eias.http.task;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;

/**
 * @author Administrator
 * 
 */
public class GetFinishTaskInfo implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte[] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-9-18 上午10:15:48
	 * @Description: 请求最新的任务子项信息
	 * @param currentUser
	 * @param taskInfo
	 * @return ResultInfo<TaskDataItem> 返回类型
	 * @throws
	 * @version V1.0
	 */
	public ResultInfo<List<TaskDataItem>> request(UserInfo currentUser, TaskInfo taskInfo) {
		ResultInfo<List<TaskDataItem>> result = null;
		String url = currentUser.LatestServer + "/apis/GetFinishTaskInfo";
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		params.put("token", currentUser.Token);
		params.put("taskNum", taskInfo.TaskNum);
		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.GET, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = this.getResponseData();
		} catch (Exception e) {
			if(result==null){
				result=new ResultInfo<List<TaskDataItem>>();
			}
			result.Success=false;
			result.Message ="服务器异常，获取任务子项信息失败";
			DataLogOperator.taskHttp("GetFinishTaskInfo=>同步已完成任务子项信息失败(getResponseData)",e.getMessage());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<List<TaskDataItem>> getResponseData() {
		ResultInfo<List<TaskDataItem>> result = new ResultInfo<List<TaskDataItem>>();
		if(mData!=null){
			String resultStr=new String(mData);
			try {
				JSONObject resultJsObj=new JSONObject(resultStr.toString());
				if("false".equals(resultJsObj.get("Success"))){
					result.Success=false;
					result.Message=resultJsObj.getString("Message");
					
				}else{
					JSONArray jsonArr=new JSONArray(resultJsObj.get("Data").toString());
					List<TaskDataItem> taskDataItems=new ArrayList<TaskDataItem>();
					for(int i=0;i<jsonArr.length();i++){
						JSONObject jobj=(JSONObject) jsonArr.get(i);
						TaskDataItem taskDataItem=JSONHelper.parseObject(jobj, TaskDataItem.class);
						
						taskDataItems.add(taskDataItem);
					}
					result.Data=taskDataItems;
				}
			} catch (Exception e) {
				result.Success=false;
				result.Message="服务器异常! 请重试。";
				e.printStackTrace();
				DataLogOperator.taskHttp("GetFinishTaskInfo=>同步已完成任务子项信息失败(getResponseData)",e.getMessage());
			}
		}else{
			result.Success=false;
			result.Message="请求服务器数据失败！";
		}
		

		return result;
	}

}
