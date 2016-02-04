/**
 * 
 */
package com.yunfang.eias.utils;

import java.util.ArrayList;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.LogType;
import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.tables.DataLogWorker;
import com.yunfang.framework.iUtils.ILogHelper;

/**
 * 写日志实现类
 * @author Administrator
 *
 */
public class LogHelper implements ILogHelper
{

	/**
	 * 写入一条日志
	 */
	@Override
	public void insertLog(String logValue)
	{
		DataLogWorker.createDataLog(EIASApplication.getCurrentUser(),logValue,OperatorTypeEnum.UserLogin,LogType.UserOperation);
	}

	/**
	 * 获取日志
	 */
	@Override
	public ArrayList<String> getLogs()
	{
		return null;
	}

}
