package com.yunfang.eias.enumObj;

/****
 * 排序方式枚举
 * @author Administrator
 *
 */
public enum SortType {
	
	创建时间("CreatedDate",true),
	领取时间("ReceiveDate",true),
	状态("Status",true),
	完成时间("DoneDate",true),
	预约时间("BookDate",true);
	
	private String fieldName;
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/***
	 * true asc（升序） 
	 * false desc（降序）
	 */
	private boolean isAsc;
	
	SortType(String fieldName,boolean isAsc){
		this.fieldName=fieldName;
		this.setAsc(isAsc);
	}

	/**
	 * @return the isAsc
	 */
	public boolean isAsc() {
		return isAsc;
	}

	/**
	 * @param isAsc the isAsc to set
	 */
	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}
	
	public static SortType getTypeByName(String name){
		SortType result = null;
		for (SortType c : SortType.values()) {  
			if (c.getFieldName().equals(name)) {  
				result =c; 
				break; 
			}  
		} 
		return result;
	}

	
	
}
