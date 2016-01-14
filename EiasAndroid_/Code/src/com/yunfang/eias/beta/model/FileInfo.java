/**
 * 
 */
package com.yunfang.eias.beta.model;

/**
 * @author kevin
 *  需要上传的文件信息
 */
public class FileInfo {

	/** 文件对应的任务 */
	private TaskInfo fileTaskInfo;
	/** 资源文件的完整路径 */
	private String filePath="";
	/** 文件分类名称*/
	private String fileTypeName="";
	/** 文件后缀 */
	private String fileSuffix="";
	/** 是否上传成功 */
	private boolean uploadSussecces=false;
	/** 上传资源库成功后反会的 资源ID */
	private int resId=-1;
	/** 对应该任务分类下的子项ID */
	private int fileDataId;
	/**对应该任务的 分类ID*/ 
	private int BaseCategoryID;
	
	/**
	 * @return fileSuffix
	 */
	public String getFileSuffix() {
		return fileSuffix;
	}

	/**
	 * @return resId
	 */
	public int getResId() {
		return resId;
	}

	/**
	 * @param resId 要设置的 resId
	 */
	public void setResId(int resId) {
		this.resId=resId;
	}

	/**
	 * @param fileSuffix 要设置的 fileSuffix
	 */
	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	/**
	 * @return filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param 文件完整路径
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return fileTaskInfo
	 */
	public TaskInfo getFileTaskInfo() {
		return fileTaskInfo;
	}

	/**
	 * @param fileTaskInfo 要设置的 fileTaskInfo
	 */
	public void setFileTaskInfo(TaskInfo fileTaskInfo) {
		this.fileTaskInfo = fileTaskInfo;
	}

	/**
	 * @return uploadSussecces
	 *  资源ID 不为-1即，即上传成功
	 */
	public boolean isUploadSussecces() {
		uploadSussecces=resId!=-1;
		return uploadSussecces;
	}

	/**
	 * @param uploadSussecces 要设置的 uploadSussecces
	 */
	public void setUploadSussecces(boolean uploadSussecces) {
		this.uploadSussecces = uploadSussecces;
	}

	/**
	 * @return fileTypeName
	 */
	public String getFileTypeName() {
		return fileTypeName;
	}

	/**
	 * @param fileTypeName 要设置的 fileTypeName
	 */
	public void setFileTypeName(String fileTypeName) {
		this.fileTypeName = fileTypeName;
	}

	/**
	 * @return fileDataId
	 */
	public int getFileDataId() {
		return fileDataId;
	}

	/**
	 * @param fileDataId 要设置的 fileDataId
	 */
	public void setFileDataId(int fileDataId) {
		this.fileDataId = fileDataId;
	}

	/**
	 * @return baseCategoryID
	 */
	public int getBaseCategoryID() {
		return BaseCategoryID;
	}

	/**
	 * @param baseCategoryID 要设置的 baseCategoryID
	 */
	public void setBaseCategoryID(int baseCategoryID) {
		BaseCategoryID = baseCategoryID;
	}
	
}
