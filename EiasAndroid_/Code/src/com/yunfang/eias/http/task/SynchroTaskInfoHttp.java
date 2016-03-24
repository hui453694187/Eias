package com.yunfang.eias.http.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;

import com.yunfang.eias.dto.TaskInfoDTO;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.framework.httpClient.CommonRequestPackage;
import com.yunfang.framework.httpClient.IRequestTask;
import com.yunfang.framework.httpClient.RequestTypeEnum;
import com.yunfang.framework.httpClient.YFHttpClient;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;

/**
 * 获取任务信息列表的数据，拿分类和子项
 * 
 * @author 贺隽
 * 
 */
public class SynchroTaskInfoHttp implements IRequestTask {

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
	public ResultInfo<ArrayList<TaskInfoDTO>> getResponseData() {
		ResultInfo<ArrayList<TaskInfoDTO>> result = new ResultInfo<ArrayList<TaskInfoDTO>>();
		if (mData != null) {
			String str = new String(mData);
			if (!TextUtils.isEmpty(str)) {
				try {
					JSONObject json = new JSONObject(str.toString());
					result = JSONHelper.parseObject(json, result.getClass());
					if (json.getString("Success").equals("false")) {
						result.Success = false;
					} else if (json.has("Data")) {
						str = json.getString("Data");
						ArrayList<TaskInfoDTO> taskInfosDto = new ArrayList<TaskInfoDTO>();
						JSONArray taskArray = new JSONArray(str);
						for (int i = 0; i < taskArray.length(); i++) {
							JSONObject temp = (JSONObject) taskArray.get(i);
							taskInfosDto.add(new TaskInfoDTO(temp));
						}
						result.Data = taskInfosDto;
					}
					if (json.has("Others")) {
						result.Others = json.getString("Others");
					}
					result.Message = json.getString("Message");
				} catch (JSONException e) {
					result.Success = false;
					result.Message = "获取任务列表失败，请检查网络";
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
	 * 
	 * @param currentUser
	 *            :当前用户，里面存有用户的Token值，用于与后台交互时的身份论证
	 * @param taskStatus
	 *            ：项目状态TaskStatus:待领取=0，待提交=1，已完成=2（自建任务时，状态默认为待提交，状态值为1）
	 * @param queryStr
	 *            :任务编号或者地址值
	 * @param pageIndex
	 *            :当前所在页码，从1开始
	 * @param pageSize
	 *            ：页行数
	 * @return 本地不存在的任务数据
	 */
	public ResultInfo<ArrayList<TaskInfoDTO>> request(UserInfo currentUser, TaskStatus taskStatus, String serverTask, String queryStr, int pageIndex, int pageSize) {
		ResultInfo<ArrayList<TaskInfoDTO>> result = new ResultInfo<ArrayList<TaskInfoDTO>>();
		String url = currentUser.LatestServer + "/apis/GetTaskInfos";
		// 填充参数，key-value。key是接口要求传的变量名称
		Hashtable<String, Object> params = new Hashtable<String, Object>(6);
		params.put("token", currentUser.Token);
		params.put("status", taskStatus.getIndex());
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		params.put("queryString", queryStr);
		params.put("serverTask", serverTask);

		CommonRequestPackage requestPackage = new CommonRequestPackage(url, RequestTypeEnum.POST, params);
		try {
			YFHttpClient.request(requestPackage, this);
			result = getResponseData();
		} catch (Exception e) {
			result.Success = false;
			result.Message = e.getMessage();
			DataLogOperator.taskHttp("GetTaskListTask=>获取任务数据失败(request)", e.getMessage());
		}
		return result;
	}
}
