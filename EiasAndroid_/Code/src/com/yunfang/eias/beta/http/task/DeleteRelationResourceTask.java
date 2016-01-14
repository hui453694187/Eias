/**
 * 
 */
package com.yunfang.eias.beta.http.task;

import java.util.Hashtable;

import org.json.JSONObject;

import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.model.TaskInfo;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;

/**
 * @author Administrator
 *
 */
public class DeleteRelationResourceTask implements IRequestTask {

	private byte[] mData;

	@Override
	public void setContext(byte[] data) {
		// TODO 自动生成的方法存根
		this.mData=data;

	}
	
	public ResultInfo<Boolean> request(String serverUrl,TaskInfo taskInfo,String resId){
		ResultInfo<Boolean> result=new ResultInfo<Boolean>();
		//http://123.57.6.154:9000/
		String url =serverUrl+"ExtAPI/extend/Delete?"+
		"resourceID="+resId+
		"&systemName=外采系统"+
		"&belongID="+taskInfo.TaskID+
		"&belongPID="+taskInfo.ID;
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(1);
		CommonRequestPackage requestPackage = new CommonRequestPackage(url,
				RequestTypeEnum.DELETE,
				params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = "服务器繁忙！";
			DataLogOperator.taskHttp("SubmitTaskInfoTask=>提交任务失败(request)", e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultInfo<Boolean> getResponseData() {
		ResultInfo<Boolean> result=new ResultInfo<Boolean>();
		try{
			if(this.mData!=null){
				String resultStr=new String(this.mData);
				JSONObject resJson=new JSONObject(resultStr);
				if(resJson.getBoolean("ResultData")){
					result.Success=resJson.getBoolean("ResultData");
				}else{
					result.Success=false;
					result.Message ="删除图片失败！";
				}
			}else{
				result.Success=false;
				result.Message = "没有返回数据";
			}
		}catch(Exception e){
			result.Success=false;
			result.Message = "没有返回数据";
			DataLogOperator.taskHttp("DeleteRelationResourceTask=>提交任务失败(getResponseData)", e.getMessage());
		}
		
		return result;
	}

}
