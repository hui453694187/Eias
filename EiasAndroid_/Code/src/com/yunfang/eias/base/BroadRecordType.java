/**
 * 
 */
package com.yunfang.eias.base;

/**
 * 任务提交事件
 * @author sen
 *
 */
public class BroadRecordType {
	
	//{{ 全局事件名称定义
	
	//{{ com.yunfang.eias.service.MainService
	/**
	 * 精确评估页面中的小区地址/名称查询后触发事件
	 */
	public static final String FILTER_AFTER_CHANGEADDRESSORNAME ="com.yunfang.eias.ui.estimatesearchresactivity.changeaddressorname";

	/**
	 * 定位完成之后
	 */
	public static final String BAIDU_MAP_LOCATION = "com.yunfang.eias.ui.baidumapocation";
	/**
	 * 所有城市界面选中城市后触发返回主界面
	 */
	public static final String AFTER_SELECT_CITY ="com.yunfang.eias.ui.selectcity";
	
	/**
	 * 提交完成任务后的消息事件名称(待提交-1，已完成+1，提交中-1)
	 */
	public static final String AFTER_SUBMITED = "com.yunfang.eias.service.submited";

	/**
	 * 将任务提交到后台进行提交（提交中+1）
	 */
	public static final String WAIT_TO_SUBMIT = "com.yunfang.eias.service.submit";

	/**
	 *  任务提交结果通知
	 */
	public static final String SUBMITED_RESULT_NOTIFICATION = "com.yunfang.eias.service.submitedresultnotification";
	
	/**
	 * 开始下载最新版本
	 */
	public static final String DOWNLOAD_LAST_VERSION = "com.yunfang.eias.service.downloadlastversion";	
	
	/**
	 * 主服务创建完成之后
	 */
	public static final String MAINSERVER_CREATED = "com.yunfang.eias.service.servicecreated";
	
	/**
	 * 切换为离线时
	 */
	public static final String CHANGED_OFFLINE = "com.yunfang.eias.service.changedoffline";
	
	//}}

	//{{ com.yunfang.eias.ui.CreateTaskActivity
	
	/**
	 * 创建完任务后的消息事件名称
	 */
	public static final String AFTER_CREATED_TASK= "com.yunfang.eias.ui.taskaftercreatedtask";
	
	//}}
	
	//}}
	
	//{{ 系统设置变量全局名称定义
	
	/**
	 * 记录缓存数据集合
	 */
	public static final String KEY_SETTINGS= "Key_settings";

	/**
	 * 提交任务重复次数
	 */
	public static final String KEY_SETTING_REPEATTIME = "Key_setting_repeattime";

	/**
	 * 拍摄图片的最大size(设定后拍摄的照片不会大于这个值)
	 */
	public static final String KEY_SETTING_MAXIMAGESIZE = "Key_setting_maximagesize";
	
	/**
	 * SDCard小于100M需要提示用户 空间不足
	 */
	public static final String KEY_SETTING_SDCARDSIZE = "Key_setting_sdcardminsize";

	/**
	 * 图片从图库中获取时是剪切还是复制操作
	 */
	public static final String KEY_SETTING_PICTURECOPYORPASTE = "Key_setting_picturecopyorpaste";
	
	/**
	 * 选择拍照的方式 是系统自带的还是外采定制的
	 */
	public static final String KEY_SETTING_PHOTOTYPE = "Key_setting_phototype";
	
	//}}
	
	//{{ 选择文件后
	
	/**
	 * 任务导入选择文件之后
	 */
	public static final String TASK_IMPORT_FILE_SELECTED = "com.yunfang.eias.service.taskimportfileselected";
	/**
	 * 登录后响应事件
	 */
	public static final String LOGIN_DONE_EVENT  ="com.yunfang.estimate.ui.estimateloginactivity.logindone";

	//}}

	//{{ 拍照
	
	/**
	 * 拍了一张照片后
	 */
	public static final String CAMERASERVER_SEND = "com.yunfang.eias.service.camerasend";
	
	/**
	 * 拍照完成点击相册
	 */
	public static final String CAMERASERVER_BACK = "com.yunfang.eias.service.cameraback";
	
	//}}
}
