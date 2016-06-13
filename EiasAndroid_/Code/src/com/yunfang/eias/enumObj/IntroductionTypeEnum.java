package com.yunfang.eias.enumObj;

/**
 * 任务日志类型枚举
 * @author 贺隽
 *
 */
public enum IntroductionTypeEnum {
	
	// {{ 枚举值
	/**
	 * 界面模块说明
	 */
	AbountMain("界面模块说明 ", 0),
	/**
	 * 勘察功能说明
	 */
	AbountTask("勘察功能说明", 1),
	/**
	 * 其他功能说明
	 */
	AbountOther("其他功能说明", 2);
	// }}
	
	//{{ 获取最大值
	
	/**
	 * 注意！！！！！！！！！！！！！！这里用到了循环所以每次加枚举的时候 请把新加的一个枚举放到下面去
	 * AbountOther.index + 1;  如果要加 请把 AbountOther替换最新加的枚举
	 * @return
	 */
	public static int length(){
		return AbountOther.index + 1;
	}
	
	//}}

	// {{ 获取枚举值类型
	/**
	 * 通过值得到枚举类型
	 * 
	 * @param value
	 * @return
	 */
	public static IntroductionTypeEnum getEnumByValue(int value) {
		IntroductionTypeEnum result = null;
		for (IntroductionTypeEnum c : IntroductionTypeEnum.values()) {
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
	public static IntroductionTypeEnum getEnumByName(String value) {
		IntroductionTypeEnum result = null;
		for (IntroductionTypeEnum c : IntroductionTypeEnum.values()) {
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
	private IntroductionTypeEnum(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/**
	 * 通过枚举项数字值获得枚举项名称
	 * 
	 * @param index 枚举项数字值
	 * @return
	 */
	public static String getName(int index) {
		String result = "";
		for (IntroductionTypeEnum c : IntroductionTypeEnum.values()) {
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
		for (IntroductionTypeEnum c : IntroductionTypeEnum.values()) {
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
