package com.yunfang.framework.httpClient;

public enum RequestTypeEnum {
	//{{ 枚举值
	/**
	 * GET方式
	 */
	GET("GET",1),
	/**
	 * POST方式
	 */
	POST("POST",2),
	/**
	 * REST中的DELETE提交方式
	 */
	DELETE("DELETE",3),
	/**
	 * REST中的PUT提交方式
	 */
	PUT("PUT",4);
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
	private RequestTypeEnum(String name, int index) {  
		this.name = name;  
		this.index = index;  
	}  

	/**
	 * 通过枚举项数字值获得枚举项名称
	 * @param index  枚举项数字值
	 * @return
	 */
	public static String GetName(int index) {  
		for (RequestTypeEnum c : RequestTypeEnum.values()) {  
			if (c.getIndex() == index) {  
				return c.name;  
			}  
		}  
		return null;  
	}  

	/**
	 * 通过枚举项名称获得枚举项数字值
	 * @param enumName  枚举项名称
	 * @return
	 */
	public static int GetValue(String enumName) {  
		for (RequestTypeEnum c : RequestTypeEnum.values()) {  
			if (c.getName().equals(enumName)) {  
				return c.index;  
			}  
		}  
		return -1;  
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
