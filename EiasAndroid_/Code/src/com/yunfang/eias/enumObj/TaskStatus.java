package com.yunfang.eias.enumObj;

public enum TaskStatus {
	//{{ 枚举值
	/**
	 * 待领取
	 */
	Todo("待领取",0),
	/**
	 * 待提交
	 */
	Doing("待提交",1),
	/**
	 * 已完成
	 */
	Done("已完成",2),
	/**
	 * 提交中
	 */
	Submiting("提交中",3),
	/**
	 * 已暂停
	 */
	Pause("已暂停",-99);
	//}}

	//{{ 获取枚举值类型
	/**
	 * 通过值得到枚举类型
	 * @param value
	 * @return
	 */
	public static TaskStatus getEnumByValue(int value){
		TaskStatus result = null;
		for (TaskStatus c : TaskStatus.values()) {  
			if (c.getIndex() == value) {  
				result =c; 
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
	public static TaskStatus getEnumByName(String value){
		TaskStatus result = null;
		for (TaskStatus c : TaskStatus.values()) {  
			if (c.getName().equals(value)) {  
				result =c; 
				break; 
			}  
		} 
		return result;
	}
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
	private TaskStatus(String name, int index) {  
		this.name = name;  
		this.index = index;  
	}  

	/**
	 * 通过枚举项数字值获得枚举项名称
	 * @param index  枚举项数字值
	 * @return
	 */
	public static String getName(int index) { 
		String result = "";
		for (TaskStatus c : TaskStatus.values()) {  
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
		for (TaskStatus c : TaskStatus.values()) {  
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
