/**
 * 
 */
package com.yunfang.eias.dto;


import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.TaskCategoryInfo;

/**
 * @author kevin
 * 
 */
public class TaskCategoryInfoByTypeDTO {

	/** 任务分类信息 */
	private TaskCategoryInfo taskCatgoyInfo;

	/** 分类项的类型：图片集、位置、常规、视频集、音频集（枚举类型） */
	private DataCategoryDefine dataCategoryDefine;

	/**
	 * @return taskCatgoyInfo
	 */
	public TaskCategoryInfo getTaskCatgoyInfo() {
		return taskCatgoyInfo;
	}

	/***
	 * 
	 * @author kevin
	 * @date 2015-10-23 上午11:07:26
	 * @Description: 是否是媒体类型
	 * @return boolean    返回类型 
	 * @version V1.0
	 */
	public boolean isMediaType() {
		if(dataCategoryDefine!=null&&dataCategoryDefine.ControlType!=null){
			if (dataCategoryDefine.ControlType == CategoryType.AudioCollection || //
					dataCategoryDefine.ControlType == CategoryType.PictureCollection || //
					dataCategoryDefine.ControlType == CategoryType.VideoCollection) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @param taskCatgoyInfo
	 *            要设置的 taskCatgoyInfo
	 */
	public void setTaskCatgoyInfo(TaskCategoryInfo taskCatgoyInfo) {
		this.taskCatgoyInfo = taskCatgoyInfo;
	}

	/**
	 * @return dataCategoryDefine
	 */
	public DataCategoryDefine getDataCategoryDefine() {
		return dataCategoryDefine;
	}

	/**
	 * @param dataCategoryDefine
	 *            要设置的 dataCategoryDefine
	 */
	public void setDataCategoryDefine(DataCategoryDefine dataCategoryDefine) {
		this.dataCategoryDefine = dataCategoryDefine;
	}

}
