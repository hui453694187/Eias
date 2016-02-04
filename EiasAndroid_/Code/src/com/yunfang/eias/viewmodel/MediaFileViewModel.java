package com.yunfang.eias.viewmodel;

import java.io.File;
import com.yunfang.eias.model.MediaDataInfo;
import com.yunfang.framework.model.ViewModelBase;

/**
 * 对应HomeActivty的视图
 * 
 * @author 贺隽
 * 
 */
public class MediaFileViewModel extends ViewModelBase {
	/**
	 * 临时存储的文件
	 */
	public File tempFile;

	/**
	 * 当前的文件
	 */
	public File currentFile;

	/**
	 * 上一次的文件
	 */
	public File beforeFile;

	/**
	 * 文件信息
	 */
	public MediaDataInfo mData = null;
}
