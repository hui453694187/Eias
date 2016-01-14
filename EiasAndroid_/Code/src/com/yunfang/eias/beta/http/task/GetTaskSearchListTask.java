/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.model.TaskInfo;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;

/**
 * 根据任务信息中的小区名称、地址和任务编号的内容获取到符合的小区信息列表
 * @author Administrator
 *
 */
public class GetTaskSearchListTask  implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte [] mData;
	
	/* （非 Javadoc）
	 * @see com.yunfang.framework.http.IRequestTask#setContext(byte[])
	 */
	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	/*  
	 * @see com.yunfang.framework.http.IRequestTask#getResponseData()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<ArrayList<TaskInfo>>  getResponseData() {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		if(mData!=null){
			String str=new String(mData);
			if(!TextUtils.isEmpty(str)){
				try {
					JSONObject json=new JSONObject(str.toString());
					result=JSONHelper.parseObject(json, result.getClass());	
					if(json.getString("Success").equals("false")){
						result.Success = false; 
						result.Message = json.getString("Message");
					}else{
						result.Others = json.getInt("Others");
						str = json.getString("Data");					
						result.Data= (ArrayList<TaskInfo>)JSONHelper.parseCollection(str,ArrayList.class,TaskInfo.class);
						if(result.Data!= null && result.Data.size()>0 && result.Data.get(0) != null){
							for(TaskInfo item : result.Data){
								item.TaskID = item.ID;
								item.ID = -1;
								item.IsNew = false;
							}
						}
					}
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "服务器异常，获取数据失败";
					DataLogOperator.taskHttp("GetTaskSearchListTask=>"+result.Message+"(getResponseData)",e.getMessage());
				}
			}else{
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}
		return result;
	}
	

	/**
	 * 获取查询的任务数据
	 * @param residentialareaName:小区名称
	 * @param residentialareaAddress：地址
	 * @param pid:项目编号
	 * @param targetName:目标名称
	 * @param ddId:勘察表类型
	 * @param pageIndex:当前所在页码，从1开始
	 * @param pageSize：页行数
	 * @return
	 */
	public ResultInfo<ArrayList<TaskInfo>> request(
			UserInfo currentUser,
			String searchText ,
            long ddId,
            int pageIndex,
            int pageSize) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		String url=  currentUser.LatestServer + "/apis/GetSeachTaskList/"; 
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);		
		params.put("token", currentUser.Token);
		params.put("searchText",searchText);
		params.put("ddId", ddId);
		params.put("pageIndex", pageIndex);
		params.put("pageSize",pageSize);
		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.GET,params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器异常，查询数据失败";
			DataLogOperator.taskHttp("GetTaskSearchListTask=>获取查询的任务数据(request)",e.getMessage());
		}
		return result;
	}
}
