package com.yunfang.eias.dto;

import com.yunfang.eias.enumObj.CategoryType;

/**   
 *    
 * 项目名称：外业采集项目   
 * 类名称：DataLogDto   
 * 类描述：日志记录 用于给后台传递的参数对象 必须要和 Visual Studio 中的DataLogDTO 一致
 * 创建人：陈惠森 
 * 创建时间：2014-7-17
 * @version 1.0.0.1
 */ 
public class DialogTipsDTO {
	// {{相关的属性
	
	
	/**
	 * 提示信息
	 * */
	public String Concent;

	/**
	 * 反馈类型
	 * */
	public CategoryType Category;

	// }}

	//{{ 构造函数

	public DialogTipsDTO(){
	}
	
	public DialogTipsDTO(String mConcent){
		Category = CategoryType.Normal;
		Concent = mConcent;
	}
	
	public DialogTipsDTO(String mConcent,CategoryType mCategory){
		Category = mCategory;
		Concent = mConcent;
	}
	//}}
}
