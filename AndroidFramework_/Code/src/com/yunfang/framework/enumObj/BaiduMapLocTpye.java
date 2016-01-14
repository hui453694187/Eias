package com.yunfang.framework.enumObj;

/**
 * 获取坐标的方式
 * @author 贺隽
 *
 */
public enum BaiduMapLocTpye{
	//{{ 枚举值
	/**
	 * 直接获取坐标
	 */
	Location("Location",0),
	/**
	 * 通过地图获取坐标
	 */
	Map("Map",1);
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
	private BaiduMapLocTpye(String name, int index) {  
		this.name = name;  
		this.index = index;  
	}  

	/**
	 * 通过枚举项数字值获得枚举项名称
	 * @param index  枚举项数字值
	 * @return
	 */
	public static String getName(int index) {  
		String result =null;
		for (BaiduMapLocTpye c : BaiduMapLocTpye.values()) {  
			if (c.getIndex() == index) {  
				result = c.name; 
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
	public static BaiduMapLocTpye getEnumByValue(int value){
		BaiduMapLocTpye type = null;
		for (BaiduMapLocTpye c : BaiduMapLocTpye.values()) {  
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
	public static BaiduMapLocTpye getEnumByName(String value){
		BaiduMapLocTpye type = null;
		for (BaiduMapLocTpye c : BaiduMapLocTpye.values()) {  
			if (c.getName().equals(value)) {  
				type = c;  
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
		for (BaiduMapLocTpye c : BaiduMapLocTpye.values()) {  
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
