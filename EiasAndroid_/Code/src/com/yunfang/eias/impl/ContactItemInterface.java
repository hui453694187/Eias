package com.yunfang.eias.impl;

/**
 * 排序接口
 * @author 贺隽
 *
 */
public interface ContactItemInterface
{
	/**
	 * 根据该字段来排序
	 * @return
	 */
	public String getItemForIndex();

	/**
	 * 该字段用来显示出来
	 * @return
	 */
	public String getDisplayInfo();
}
