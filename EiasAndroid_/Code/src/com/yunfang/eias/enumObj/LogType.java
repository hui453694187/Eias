package com.yunfang.eias.enumObj;

/***
 * 
 * @author kevin 日志类型枚举
 */
public enum LogType {

	UserOperation(1, "用户操作"),

	Exection(2, "异常"),

	DebugLog(3, "调试日志");

	/** 枚举索引 */
	private int index;

	/** 枚举名称 */
	private String name;

	public String getName() {
		return this.name;
	}

	public int getIndex() {
		return this.index;
	}

	/***
	 * 枚举构造方法
	 * 
	 * @param index
	 * @param name
	 */
	LogType(int index, String name) {
		this.index = index;
		this.name = name;
	}

	/***
	 * 根据名称获取枚举索引
	 * 
	 * @param name
	 * @return
	 */
	public int getEnumIndexByName(String name) {
		int result = -1;
		for (LogType l : LogType.values()) {
			if (l.getName().equals(name)) {
				result = l.getIndex();
				break;
			}
		}
		return result;
	}

	/***
	 * 获取枚举名称
	 * 
	 * @param index
	 * @return
	 */
	public String getEnumNmaeByIndex(int index) {
		String result = "";
		for (LogType l : LogType.values()) {
			if (l.getIndex() == index) {
				result = l.getName();
				break;
			}
		}
		return result;
	}
	/***
	 * 根据名称获取枚举
	 * @param name
	 * @return
	 */
	public static LogType getEnumByName(String name) {
		for (LogType l : LogType.values()) {
			if (l.getName().equals(name)) {
				return l;
			}
		}
		return null;
	}
	/***
	 * 根据索引获取枚举
	 * @param index
	 * @return
	 */
	public static LogType getEnumByIndex(int index) {
		for (LogType l : LogType.values()) {
			if (l.getIndex() == index) {
				return l;
			}
		}
		return null;
	}
}
