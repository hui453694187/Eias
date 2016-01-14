/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.util.Hashtable;

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

/**
 * @author Administrator
 *
 */
public class SetTaskRestartTask implements IRequestTask {

	private byte[] mData;
	
	@Override
	public void setContext(byte[] data) {
		this.mData=data;
	}
	
	public ResultInfo<Boolean> request(UserInfo currentUser,TaskInfo taskInfo){
		ResultInfo<Boolean> result=null;
		
		String url=currentUser.LatestServer + "/apis/SetTaskRestart";
		Hashtable<String,Object> param=new Hashtable<String, Object>();
		param.put("token", currentUser.Token);
		param.put("taskNum", taskInfo.TaskNum);
		CommonRequestPackage commReauest=new CommonRequestPackage(url, RequestTypeEnum.POST,param);
		try{
			YFHttpClient.request(commReauest, this);
			result=this.getResponseData();
		}catch(Exception e){
			if(result==null){
				result=new ResultInfo<Boolean>();
			}
			result.Success = false;
			result.Message ="服务器异常，启用任务失败";
			DataLogOperator.taskHttp("SetTaskRestartTask=>启用暂停任务失败(request)",e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result=new ResultInfo<Boolean>();
		if(mData!=null){
			String resultStr=new String(mData);
			try{
				if(!TextUtils.isEmpty(resultStr)){
					JSONObject jsonObj=new JSONObject(resultStr);
					if(jsonObj.getString("Success").equals("true")){
						result.Data=jsonObj.getBoolean("Data");
						result.Message=jsonObj.getString("Message");
					}else {
						result.Success=false;
						result.Message="启用失败!";
					}
				}
			}catch(Exception e){
				result.Success=false;
				result.Message="启用失败！";
			}
		}
		return result;
	}

}
