package com.yunfang.framework.httpClient;

/**
 * 任务接口
 * @author gorson
 *
 */
public interface IRequestTask {
	/**
	 * 设置返回的字符串
	 * @param data
	 */
	void setContext(byte [] data);
	
	/**
	 * 获取返回对象集合
	 * @param t
	 */
	<T> T getResponseData();
}
