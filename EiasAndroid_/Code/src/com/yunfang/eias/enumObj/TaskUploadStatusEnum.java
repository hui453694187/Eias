package com.yunfang.eias.enumObj;

/**
 * 提交任务状态枚举
 * @author 贺隽
 *
 */
public enum TaskUploadStatusEnum {
	//{{ 枚举值
	/**
	 * 未提交
	 */
	UnSumbit("未提交",0),
	/**
	 * 提交等待中
	 */
	Submitwating("提交等待中",1),
	/**
	 * 提交中
	 */
	Submiting("提交中",2),
	/**
	 * 已提交
	 */
	Submited("已提交",3),
	/**
	 * 提交失败
	 */
	SubmitFailure("提交失败",4);
	//}}
	
	//{{ 属性
	/**
	 *   成员变量名称  
	 */
	private String name;  

	/**
	 *   成员变量值
	 */
	private int index;  
	//}}

	/**
	 * 构造方法  
	 * @param name  枚举项名称
	 * @param index 枚举项数字值
	 */
	private TaskUploadStatusEnum(String name, int index) {  
		this.name = name;  
		this.index = index;  
	}  

	/**
	 * 通过值得到枚举类型
	 * @param value
	 * @return
	 */
	public static TaskUploadStatusEnum getEnumByValue(int value){
		TaskUploadStatusEnum result = null;
		for (TaskUploadStatusEnum u : TaskUploadStatusEnum.values()) {  
			if (u.getIndex() == value) {  
				result =u;  
				break;
			}  
		} 
		return result;
	}

	/**
	 * 通过名称得到枚举类型
	 * @param value
	 * @return
	 */	
	public static TaskUploadStatusEnum getEnumByName(String value){
		TaskUploadStatusEnum result = null;
		for (TaskUploadStatusEnum u : TaskUploadStatusEnum.values()) {  
			if (u.getName().equals(value)) {  
				result =u;   
				break;
			}  
		} 
		return result;
	}
	
	
	/**
	 * 通过枚举项数字值获得枚举项名称
	 * @param index  枚举项数字值
	 * @return
	 */
	public static String getName(int index) {  
		String result = "";
		for (TaskUploadStatusEnum c : TaskUploadStatusEnum.values()) {  
			if (c.getIndex() == index) {  
				result = c.name;  
				break; 
			}  
		}  
		return result;  
	}  

	/**
	 * 通过枚举项名称获得枚举项数字值
	 * @param enumName  枚举项名称
	 * @return
	 */
	public static int getValue(String enumName) {  
		int result = -1;
		for (TaskUploadStatusEnum c : TaskUploadStatusEnum.values()) {  
			if (c.getName().equals(enumName)) {  
				result = c.index;  
				break; 
			}  
		}  
		return result;  
	}  

	/**  
	 * 获取枚举项名称
	 * @return
	 */
	public String getName() {  
		return name;  
	} 

	/**
	 * 设置枚举项名称
	 * @param name
	 */
	public void setName(String name) {  
		this.name = name;  
	}  

	/**
	 * 获取枚举项数字值
	 * @return
	 */
	public int getIndex() {  
		return index;  
	} 

	/**
	 * 设置枚举项数字值
	 * @return
	 */
	public void setIndex(int index) {  
		this.index = index;  
	}  
}

