package com.yunfang.eias.enumObj;
/**
 * 勘察匹配中，每个属性项对应的类型值
 * @author gorson
 *
 */
public enum DataItemType {
	//{{ 枚举值
	/**
	 * 文本
	 */
	Text("T",0),
	/**
	 * 多行文本
	 */
	MultiText("M",1),
	/**
	 * 自定义文本
	 */
	CustomerText("CT",2),
	/**
	 * 下拉框
	 */
	DropDownList("D",3),
	/**
	 * 多选框
	 */
	CheckedBoxList("CBO",4),
	/**
	 * 图片
	 */
	Picture("P",5),
	/**
	 * 时间值
	 */
	DateTimeValue("DT",6),
	/**
	 * 坐标值，[x,y]
	 */
	Coordinate("GPS",7),
	/**
	 * 当前用户名
	 */
	UserName("UserName",8),
	/**
	 * 当前用户联系方式
	 */
	UserTel("UserPhone",9),
	/**
	 * 音频文件列表
	 */
	Audio("Audio",10),
	/**
	 * 视频文件列表
	 */
	Video("Video",11),
	/**
	 * 地图，[x,y]
	 */
	Map("Map",12);
	//}}

	//{{ 属性
	/**
	 *   成员变量名称 :
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
	private DataItemType(String name, int index) {  
		this.name = name;  
		this.index = index;  
	}  

	/**
	 * 通过值得到枚举类型
	 * @param value
	 * @return
	 */
	public static DataItemType getEnumByValue(int value){
		DataItemType result = null;
		for (DataItemType d : DataItemType.values()) {  
			if (d.getIndex() == value) {  
				result =d;  
			}  
		} 
		return result;
	}

	/**
	 * 通过名称得到枚举类型
	 * @param value
	 * @return
	 */	
	public static DataItemType getEnumByName(String value){
		DataItemType result = null;
		for (DataItemType d : DataItemType.values()) {  
			if (d.getName().equals(value)) {  
				result =d;
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
		for (DataItemType c : DataItemType.values()) {  
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
		for (DataItemType c : DataItemType.values()) {  
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
