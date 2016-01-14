package com.yunfang.framework.httpClient;

import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;


public interface IRequestPackage {
	/**
	 * 获取header设置信息
	 * @return
	 */
	Hashtable<String, String > GetRequestHeaders();

	/**
	 * 获取GET方法参数 
	 * @return
	 */
	String GetRequestParamsInGetType();

	/**
	 * 获取Post方法参数
	 * @return
	 */
	List<BasicNameValuePair> GetRequestParamsInPostType();

	/**
	 * 获取POST请求Entity, 根据不同的参数，需要将数据转换成对应RequestEntity的子类
	 * @return
	 */
	HttpEntity GetRequetEntityInPostType();


	/**
	 * 获取请求链接
	 * @return
	 */
	String GetUrl();

	/**
	 * 获取请求类型  
	 * 
	 * @return
	 */
	RequestTypeEnum GetRequestType();

	/**
	 * 获取请求参数设置，如超 时时间
	 * @return
	 */
	Hashtable<String, Object> GetSettings();

	/**
	 * 设置请求参数
	 * */
	void SetParams(Hashtable<String, Object> mParams);
}
