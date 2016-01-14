package com.yunfang.eias.beta.http.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;

import com.yunfang.eias.beta.dto.TaskInfoDTO;
import com.yunfang.eias.beta.enumObj.TaskStatus;
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
 * 获取任务信息列表的数据，只拿主表信息
 * @author gorson
 *
 */
public class GetTaskListTask implements IRequestTask {

	/**
	 * 响应数据
	 * */
	private byte [] mData;

	@Override
	public void setContext(byte[] data) {
		this.mData = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<ArrayList<TaskInfo>> getResponseData() {
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
					}
					else if(json.has("Others")){ 
						result.Others = json.getInt("Others");
						str = json.getString("Data");
						result.Data = new ArrayList<TaskInfo>();
						//result.Data= (ArrayList<TaskInfo>)JSONHelper.parseCollection(str,ArrayList.class,TaskInfo.class);
						ArrayList<TaskInfoDTO> tempDTO = new ArrayList<TaskInfoDTO>();
						tempDTO = (ArrayList<TaskInfoDTO>)JSONHelper.parseCollection(str,ArrayList.class,TaskInfoDTO.class);
						if (tempDTO!=null &&tempDTO.size()>0&&tempDTO.get(0)!=null)
						{
							for (TaskInfoDTO item : tempDTO)
							{	
								if(!item.equals(null)){
									result.Data.add(item.getTaskInfo());
								}
							}
						}
						
						if(result.Data!= null && result.Data.size()>0 && result.Data.get(0) != null){
							for(TaskInfo item : result.Data){
								item.ID = item.TaskID;
								item.IsNew = false;
							}
						}
						result.Message = json.getString("Message");
					}					
				} catch (JSONException e) {
					result.Success = false;
					result.Message ="获取任务列表失败，请检查网络";
					DataLogOperator.taskHttp("GetTaskListTask=>" + result.Message + "(getResponseData)",e.getMessage());
				}
			}else{
				result.Success = false;
				result.Message = "没有返回数据";
			}
		}
		return result;
	}

	/**
	 * 获取任务数据
	 * @param currentUser:当前用户，里面存有用户的Token值，用于与后台交互时的身份论证
	 * @param taskStatus：项目状态TaskStatus:待领取=0，待提交=1，已完成=2（自建任务时，状态默认为待提交，状态值为1）
	 * @param queryStr:任务编号或者地址值
	 * @param pageIndex:当前所在页码，从1开始
	 * @param pageSize：页行数
	 * @return
	 */
	public ResultInfo<ArrayList<TaskInfo>> request(UserInfo currentUser,TaskStatus taskStatus, String queryStr,int pageIndex,int pageSize) {
		ResultInfo<ArrayList<TaskInfo>> result = new ResultInfo<ArrayList<TaskInfo>>();
		//GetUrl("GetOData", "", token, "state=" + state	+ "&queryString=" + queryString + "&pageIndex="	+ pageIndex);
		String url=  currentUser.LatestServer + "/apis/GetOData/"; 
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);		
		params.put("token",currentUser.Token);
		params.put("status", taskStatus.getIndex()); 
		params.put("pageIndex", pageIndex);
		params.put("pageSize",pageSize);
		params.put("queryString", queryStr);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.GET,params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskHttp("GetTaskListTask=>获取任务数据失败(request)",e.getMessage());
		}
		return result;
	}
}
