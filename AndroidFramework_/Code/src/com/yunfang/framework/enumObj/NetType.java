package com.yunfang.framework.enumObj;


/**
 * 网络类型
 * @author gorson
 *
 */
public enum NetType{
	//{{ 枚举值
	/**
	 * 2G
	 */
	Type_2g("2G",0),
	/**
	 * 3G
	 */
	Type_3g("3G",1),
	/**
	 * 4G
	 */
	Type_4g("4G",2),
	
	/**
	 * wifi
	 */
	Type_wifi("wifi",3),
	
	/**
	 * unknown
	 */
	
	Type_unknown("unknown",4);
	
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
	private NetType(String name, int index) {  
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
		for (NetType c : NetType.values()) {  
			if (c.getIndex() == index) {  
				result = c.getName();
				break;
			}  
		}  
		return result;  
	}  
	
	/**
	 * 通过值得到枚举类型
	 * @param value
	 * @return
	 */
	public static NetType getEnumByValue(int value){
		NetType type = null;
		for (NetType c : NetType.values()) {  
			if (c.getIndex() == value) {  
				type =c; 
				break;
			}  
		} 
		return type;
	}

	/**
	 * 通过名称得到枚举类型
	 * @param value
	 * @return
	 */
	public static NetType getEnumByName(String value){
		NetType type = null;
		for (NetType c : NetType.values()) {  
			if (c.getName().equals(value)) {  
				type =c; 
				break;
			}  
		} 
		return type;
	}
	
	/**
	 * 通过枚举项名称获得枚举项数字值
	 * @param enumName  枚举项名称
	 * @return
	 */
	public static int getValue(String enumName) {  
		int result = -1;
		for (NetType c : NetType.values()) {  
			if (c.getName().equals(enumName)) {  
				result = c.getIndex();  
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
