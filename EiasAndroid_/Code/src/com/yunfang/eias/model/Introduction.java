/**
 * 
 */
package com.yunfang.eias.model;

import com.yunfang.eias.enumObj.IntroductionTypeEnum;
import com.yunfang.framework.utils.BitmapHelperUtil;

import android.graphics.Bitmap;

/**
 * @author 贺隽 功能界面的模型类
 */
public class Introduction {

	//{{ 相关属性

	/**
	 * 标题
	 */
	public String Title;

	/**
	 * 内容
	 */
	public String Description;

	/**
	 * 图片DATA
	 */
	public Bitmap ImageData;

	/**
	 * 需要介绍的功能类型
	 */
	public IntroductionTypeEnum Type;

	//}}

	//{{ 构造函数
	
	/**
	 * 无参构造函数
	 */
	public Introduction() {

	}

	/**
	 * 带参构造函数.
	 * 
	 * @param title:表示如果是新版本就填写 信息 如 2.0.0.2 新增功能;
	 * @param content:描述信息;
	 * @param introductionType:介绍类型;
	 * @param imageViewId:图片编号
	 */
	public Introduction(String title, String content,IntroductionTypeEnum introductionType, int imageViewId) {
		try {
			Title = title;
			Description = content;
			Type = introductionType;
			ImageData = BitmapHelperUtil.readBitMap(imageViewId);
		} catch (Exception e) {

		}
	}
	
	//}}

}
