package com.yunfang.framework.model;

/**
 * 设备信息
 * @author 贺隽
 *
 */
public class DeviceInfo {
	
	//{{ 相关的属性
	/**
	 * 设备屏幕宽度
	 */
	public int ScreenWeight = 0;
	
	/**
	 * 设备屏幕高度
	 */
	public int ScreenHeight = 0;
	
	/**
	 * 设备编号
	 */
	public String DeviceId = "";
	
	/**
	 * 设备软件版本号
	 */
	public String DeviceSoftwareVersion = "";
	
	/**
	 * 设备线号
	 */
	public String Line1Number = "";
	
	/**
	 * 设备网络国家
	 */
	public String NetworkCountryIso = "";
	
	/**
	 * 设备网络操作
	 */
	public String NetworkOperator = "";
	
	/**
	 * 设备网络操作名称
	 */
	public String NetworkOperatorName = "";
	
	/**
	 * 设备网络支持供应商
	 */
	public int NetworkType = 0;
	
	/**
	 * 设备手机类型
	 */
	public int PhoneType = 0;
	
	/**
	 * 设备SIM卡国家ISO
	 */
	public String SimCountryIso = "";
	
	/**
	 * 设备SIM卡操作
	 */
	public String SimOperator = "";
	
	/**
	 * 设备屏幕高度
	 */
	public String SimOperatorName = "";
	
	/**
	 * 设备SIM序列号
	 */
	public String SimSerialNumber = "";
	
	/**
	 * 设备SIM状态
	 */
	public int SimState = 0;
	
	/**
	 * 设备子用户编号
	 */
	public String SubscriberId = "";
	
	/**
	 * 设备语句邮件数量
	 */
	public String VoiceMailNumber = "";
	
	//}}
	
	/**
	 * 无参数构造函数
	 */
	public DeviceInfo(){
		
	}
	
}
