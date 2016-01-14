/**
 * 日志接口
 */
package com.yunfang.framework.iUtils;

import java.util.ArrayList;

/**
 * 日志接口
 * @author Administrator
 *
 */
public interface ILogHelper
{
	/**
	 * 写入日志
	 */
	 void insertLog(String logValue);
	 
	 /**
	  * 获取日志
	  * @return
	  */
	 ArrayList<String> getLogs();
}
