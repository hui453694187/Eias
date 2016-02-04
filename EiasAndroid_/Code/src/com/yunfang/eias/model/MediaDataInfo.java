package com.yunfang.eias.model;

import java.io.File;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.BitmapHelperUtil;
import com.yunfang.framework.utils.StringUtil;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video;

public class MediaDataInfo {

	// {{ 相关常量
	/**
	 * 后缀符号
	 */
	public final static String SuffixSymbol = ".";

	/**
	 * 中横线
	 */
	public final static String HorizontalLine = "-";

	/**
	 * 分号
	 */
	public final static String Semicolon = ";";

	/**
	 * 图片后缀
	 */
	public final static String suffixJpg = ".jpg";

	/**
	 * 录音后缀
	 */
	public final static String suffixAmr = ".amr";

	/**
	 * 视频后缀
	 */
	public final static String suffixMp4 = ".mp4";

	// }}

	// {{ 相关常量

	/**
	 * 编号
	 */
	public int ID = 0;

	/**
	 * 标题
	 */
	public String Title = "";

	/**
	 * 文件集合
	 */
	public String Album = "";

	/**
	 * 创建者
	 */
	public String Creator = "";

	/**
	 * 路径
	 */
	public String Path = "";

	/**
	 * 缩略图路径
	 */
	public String ThumbnailPath = "";

	/**
	 * 显示名称
	 */
	public String DisplayName = "";

	/**
	 * 文件类型
	 */
	public String MimeType = "";

	/**
	 * 大小
	 */
	public String Size = "";

	/**
	 * 播放时长
	 */
	public String Duration = "";

	/**
	 * 播放时长
	 */
	public String CreatedTime = "";

	/**
	 * 缩略图
	 */
	public Bitmap ThumbnailPhoto = null;

	/**
	 * 子项值 当前的
	 */
	public String itemFileName = "";

	/**
	 * 子项名称
	 */
	public String ItemName = "";

	/**
	 * 子项值 全部的
	 */
	public String ItemValue = "";

	/**
	 * 所属分类项
	 */
	public int CategoryId = 0;

	/**
	 * 是否丢失文件
	 */
	public boolean isLose = false;

	/**
	 * 文件对象
	 */
	public File file;
	
	/**
	 * 是否被勾选
	 */
	public boolean check;

	// }}

	// {{ 构成函数

	/**
	 * 构成函数
	 * 
	 * @param file
	 *            :文件对象
	 */
	public MediaDataInfo(String title, File file) {
		super();
		this.Title = title;
		this.file = file;
		if (this.file != null) {
			this.Path = file.getPath();
			if (file.exists()) {
				this.ID = file.hashCode();
				this.Album = file.getParent();
				this.Creator = EIASApplication.getCurrentUser().Name;
				this.DisplayName = file.getName().substring(0, file.getName().indexOf("."));
				this.MimeType = file.getName().substring(file.getName().indexOf(".") + 1);
				this.Size = StringUtil.FormatFileSize(file.length());
				this.CreatedTime = DateTimeUtil.getCustomtTime(file.lastModified());
				if (Path.endsWith(suffixJpg)) {
					String tString = EIASApplication.getInstance().getString(R.string.thumbnail_dir);
					String pString = EIASApplication.getInstance().getString(R.string.project_dir);
					String tFullName = Path.replace(pString, tString);
					File thumbnail = new File(tFullName);
					if(!thumbnail.exists()){
						BitmapHelperUtil.decodeThumbnail(Path, tFullName);
					}
					ThumbnailPhoto = BitmapHelperUtil.decodeSampledBitmapFromFd(tFullName, 213, 213);
				} else if (Path.endsWith(suffixAmr)) {					
					ThumbnailPhoto = BitmapHelperUtil.decodeSampledBitmapFromResource(EIASApplication.getInstance().getResources(),R.drawable.bg_mic_max, 213, 213);
					Duration = FileUtil.getPalyTimeString(file.getAbsolutePath());
				} else if (Path.endsWith(suffixMp4)) {
					ThumbnailPhoto = ThumbnailUtils.createVideoThumbnail(Path, Video.Thumbnails.MINI_KIND);
					Duration = FileUtil.getPalyTimeString(file.getAbsolutePath());
				}
			} else {
				isLose = false;
			}
		}
	}
	// }}

}