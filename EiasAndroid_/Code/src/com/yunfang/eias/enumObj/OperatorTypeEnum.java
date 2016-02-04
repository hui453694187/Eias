package com.yunfang.eias.enumObj;

/**
 * 任务日志类型枚举
 * @author 贺隽
 *
 */
public enum OperatorTypeEnum {
	
	// {{ 枚举值
	/**
	 * 任务匹配
	 */
	TaskDataMatching("任务匹配", 0),
	/**
	 * 任务同步
	 */
	TaskDataSynchronization("任务同步", 1),
	/**
	 * 勘察同步
	 */
	DataDefineDataSynchronization("勘察同步", 2),
	/**
	 * 用户登录
	 */
	UserLogin("用户登录", 3),
	/**
	 * 用户退出
	 */
	UserLogout("用户退出", 4),
	/**
	 * 新建任务
	 */
	TaskCreate("新建任务", 5),
	/**
	 * 删除任务
	 */
	TaskDelete("删除任务", 6),
	/**
	 * 任务领取
	 */
	TaskReceive("任务领取", 7),
	/**
	 * 任务费用修改
	 */
	TaskFeeModify("任务费用", 8),
	/**
	 * 任务粘贴
	 */
	TaskDataCopy("任务粘贴", 9),
	/**
	 * 任务粘贴到新建任务中
	 */
	TaskDataCopyToNew("粘贴新任务", 10),
	/**
	 * 任务提交
	 */
	TaskSubmit("任务提交", 11),
	/**
	 * 任务重新提交
	 */
	TaskReSubmit("任务重新提交", 12),
	/**
	 * 任务回退
	 */
	TaskRollback("任务回退", 13),
	/**
	 * 任务暂停
	 */
	TaskPause("任务暂停", 14),
	/**
	 * 分类项创建
	 */
	CategoryDefineCreated("分类项创建", 15), 
	/**
	 * 分类项复制
	 */
	CategoryDefineDataCopy("分类项复制", 16), 
	/**
	 * 分类项复制到新建分类中
	 */
	CategoryDefineDataCopyToNew("复制到新分类中", 17), 
	/**
	 * 分类项清空
	 */
	CategoryDefineDataReset("分类项清空", 18), 
	/**
	 * 分类项名称修改
	 */
	CategoryDefineNameModified("分类项名称修改", 19), 
	/**
	 * 分类项删除
	 */
	CategoryDefineDeleted("分类项删除", 20),
	/**
	 * 访问服务器
	 */
	TaskHttp("访问服务器", 21),
	/**
	 * 文件上传
	 */
	FileUpload("文件上传", 22),
	/**
	 * 版本更新
	 */
	VersionUpdate("版本更新", 23),
	
	/**程序未捕获异常 */
	UnChatchExection("未捕获异常",24),
	/**
	 * 其他异常
	 */
	Other("其他异常", 25);
	// }}
	
	//{{ 获取最大值
	
	/**
	 * @return
	 */
	public static int length(){
		return OperatorTypeEnum.values().length;
	}
	
	//}}

	// {{ 获取枚举值类型
	/**
	 * 通过值得到枚举类型
	 * 
	 * @param value
	 * @return
	 */
	public static OperatorTypeEnum getEnumByValue(int value) {
		OperatorTypeEnum result = null;
		for (OperatorTypeEnum c : OperatorTypeEnum.values()) {
			if (c.getIndex() == value) {
				result = c;
				break;
			}
		}
		return result;
	}

	/**
	 * 通过名称得到枚举类型
	 * 
	 * @param value
	 * @return
	 */
	public static OperatorTypeEnum getEnumByName(String value) {
		OperatorTypeEnum result = null;
		for (OperatorTypeEnum c : OperatorTypeEnum.values()) {
			if (c.getName().equals(value)) {
				result = c;
				break;
			}
		}
		return result;
	}

	// }}

	// {{ 属性
	/**
	 * 成员变量名称
	 */
	private String name;

	/**
	 * 成员变量值
	 */
	private int index;

	// }}

	/**
	 * 构造方法
	 * 
	 * @param name
	 *            枚举项名称
	 * @param index
	 *            枚举项数字值
	 */
	private OperatorTypeEnum(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/**
	 * 通过枚举项数字值获得枚举项名称
	 * 
	 * @param index
	 *            枚举项数字值
	 * @return
	 */
	public static String getName(int index) {
		String result = "";
		for (OperatorTypeEnum c : OperatorTypeEnum.values()) {
			if (c.getIndex() == index) {
				result = c.name;
				break;
			}
		}
		return result;
	}

	/**
	 * 通过枚举项名称获得枚举项数字值
	 * 
	 * @param enumName
	 *            枚举项名称
	 * @return
	 */
	public static int getValue(String enumName) {
		int result = -1;
		for (OperatorTypeEnum c : OperatorTypeEnum.values()) {
			if (c.getName().equals(enumName)) {
				result = c.index;
				break;
			}
		}
		return result;
	}

	/**
	 * 获取枚举项名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置枚举项名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取枚举项数字值
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * 设置枚举项数字值
	 * 
	 * @return
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
