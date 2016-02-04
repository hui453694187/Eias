package com.yunfang.eias.enumObj;

public enum TaskCreateType {
	// {{ 枚举值
	/**
	 * 用户自建
	 */
	CreatedByUser("用户自建", 0),
	/**
	 * 系统界面创建
	 */
	CreatedBySystem("系统创建", 1),
	/**
	 * 系统批量创建
	 */
	BatchGeneration("批量创建", 2),
	/**
	 * 第三方系统创建
	 */
	CreatedByAnotherSystem("第三方系统创建", 3);
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

	// {{ 获取枚举值类型
	/**
	 * 通过值得到枚举类型
	 * @param value
	 * @return
	 */
	public static TaskCreateType getEnumByValue(int value) {
		TaskCreateType result = null;
		for (TaskCreateType t : TaskCreateType.values()) {
			if (t.getIndex() == value) {
				result = t;
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
	public static TaskCreateType getEnumByName(String value) {
		TaskCreateType result = null;
		for (TaskCreateType t : TaskCreateType.values()) {
			if (t.getName().equals(value)) {
				result = t;
				break;
			}
		}
		return result;
	}

	// }}

	/**
	 * 构造方法
	 * 
	 * @param name
	 *            枚举项名称
	 * @param index
	 *            枚举项数字值
	 */
	private TaskCreateType(String name, int index) {
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
		for (TaskCreateType c : TaskCreateType.values()) {
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
		int result =-1;
		for (TaskCreateType c : TaskCreateType.values()) {
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
