package com.yunfang.eias.ui;

import java.io.File;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import com.yunfang.eias.R;
import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.eias.model.MediaDataInfo;
import com.yunfang.eias.model.TaskDataItem;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.utils.CameraUtils;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 
 * 项目名称:yunfang.eias 类名称:ShowVideoFragment 类描述:视频信息展示界面 创建人:lihc 创建时间:2014-5-9
 * 下午3:32:56 修改人:贺隽 修改时间:2014-5-27 下午2:32:56
 * 
 * @version 1.0.0.9
 */
@SuppressLint({ "InlinedApi", "ValidFragment" })
public class ShowVideoFragment extends BaseWorkerFragment {
	// {{ 界面控件

	/**
	 * Activity对象
	 */
	private TaskInfoActivity taskInfoActivity;

	/**
	 * 当前Fragment视图
	 */
	private View mView;

	/**
	 * 标题
	 * */
	private TextView title_txt;

	/**
	 * 返回按钮
	 * */
	private TextView back_txt;

	/**
	 * 提交按钮
	 * */
	private TextView delete_txt;

	/**
	 * 删除按钮
	 * */
	private Button btn_save;

	/**
	 * 开始录
	 * */
	private Button btn_start;

	/**
	 * 显示图片的控件
	 * */
	private static ImageView videoReviewImage;

	/**
	 * 视频名称控件
	 * */
	private TextView txt_name;

	/**
	 * 文件大小控件
	 * */
	private TextView videoFileSize_txt;

	/**
	 * 播放时长控件
	 * */
	private TextView videoLength_txt;

	/**
	 * 拍摄时间控件
	 * */
	private TextView takeVideoTime_txt;

	/**
	 * 播放视频按钮控件
	 * */
	private ImageView ico_video;

	/**
	 * 是否为添加新文件
	 */
	private Boolean isNewFile;

	/**
	 * 上一次录制的文件
	 */
	private File beforeFile;
	/**
	 * 上一次的文件名称
	 */
	private String beforeType;

	/**
	 * 上一次的文件名称值
	 */
	private String beforeValue;

	/**
	 * 临时存储的文件
	 */
	private File tempFile;

	/**
	 * 当前的文件
	 */
	private File currentFile;

	/**
	 * 显示的媒体文件名称
	 */
	private String title;
	/**
	 * 自动完成文本框
	 */
	private AutoCompleteTextView txtAutoComplete;

	// }}

	// {{ 方法

	/**
	 * 构造函数
	 * 
	 * @param mediaFile
	 *            :媒体文件对象
	 * @param isNewFile
	 *            :是否为新添加的文件
	 */
	public ShowVideoFragment(MediaDataInfo mediaFile, Boolean isNewFile) {
		this.isNewFile = isNewFile;
		title = "请选择视频类型";
		if (mediaFile != null && mediaFile.Path != null) {
			title = mediaFile.Title;
			this.beforeFile = new File(mediaFile.Path);
		}
	}

	/**
	 * 加载用户控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_info_video_show, null);
	}

	/**
	 * 加载用户控件上的控件
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	/**
	 * 初始化控件
	 * */
	private void initView() {
		getControls();
		setListener();
		setDisplayObject(currentFile == null ? beforeFile : currentFile);
		setFileInfo(currentFile == null ? beforeFile : currentFile, true);
	}

	/**
	 * 获取控件
	 */
	private void getControls() {
		taskInfoActivity = (TaskInfoActivity) getActivity();
		mView = getView();
		title_txt = (TextView) mView.findViewById(R.id.title_txt);
		back_txt = (TextView) mView.findViewById(R.id.back_txt);
		delete_txt = (TextView) mView.findViewById(R.id.delete_txt);
		videoReviewImage = (ImageView) mView.findViewById(R.id.media_preview);
		txt_name = (TextView) mView.findViewById(R.id.media_name);
		videoFileSize_txt = (TextView) mView.findViewById(R.id.media_filesize);
		videoLength_txt = (TextView) mView.findViewById(R.id.media_palytime);
		takeVideoTime_txt = (TextView) mView.findViewById(R.id.media_createdtime);
		ico_video = (ImageView) mView.findViewById(R.id.media_paly);
		btn_start = (Button) mView.findViewById(R.id.media_start);
		btn_save = (Button) mView.findViewById(R.id.media_save);
		txtAutoComplete = (AutoCompleteTextView) mView.findViewById(R.id.image_gridview_newitem_auto);
	}

	/**
	 * 绑定事件
	 */
	private void setListener() {
		back_txt.setOnClickListener(backOnClickListener);
		delete_txt.setOnClickListener(deleteVideoOnClickListener);
		ico_video.setOnClickListener(palyVideoOnClickListener);
		videoReviewImage.setOnClickListener(palyVideoOnClickListener);
		btn_save.setOnClickListener(confirmOnClickListener);
		btn_start.setOnClickListener(startRecordOnClickListener);
	}

	/**
	 * 设置需要显示出来的东西
	 * 
	 * @param file
	 *            :需要显示的文件信息
	 */
	private void setDisplayObject(File file) {
		// 点击一个文件进入之后下拉列表选中对应的值
		if (!isNewFile && beforeFile != null) {
			String selectItem = taskInfoActivity.getSeleteDataItem(beforeFile.getName());
			txtAutoComplete.setText(selectItem);
			beforeType = selectItem;
			beforeValue = beforeFile.getName();
		}

		btn_start.setText(isNewFile ? "录视频" : "重录视频");
		title_txt.setText("视频信息");
		txt_name.setText(title);

		OnItemClickListener autoLister = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setMediaName();
				setFileInfo(currentFile, false);
			}
		};

		taskInfoActivity.setAutoCompleteData(txtAutoComplete, autoLister);
	}

	/**
	 * 取得当前打开的子项名称
	 * 
	 * @param pictureName
	 *            视频名称
	 * @return 子项名称
	 */
	@SuppressWarnings("unused")
	private String getSeleteDataItem(String pictureName) {
		String result = "";
		for (TaskDataItem item : taskInfoActivity.viewModel.currentCategory.Items) {
			if (item.Value.contains(pictureName)) {
				result = item.Name;
			}
		}
		return result;
	}

	/**
	 * 设置文件信息
	 * 
	 * @param file
	 *            :需要显示的文件信息对象
	 * @param isSetName
	 *            :是否重置显示的文件名称
	 */
	private void setFileInfo(File file, Boolean isSetName) {
		// 更改文件就要加载的
		if (file != null) {
			MediaDataInfo m = new MediaDataInfo(title, file);
			videoFileSize_txt.setText(m.Size);
			takeVideoTime_txt.setText(m.CreatedTime);
			videoReviewImage.setImageBitmap(m.ThumbnailPhoto);
			videoLength_txt.setText(m.Duration);
			if (isSetName) {
				txt_name.setText(m.Title);
			}
		}
	}

	/**
	 * 设置文件显示的名称
	 */
	private void setMediaName() {
		if (isNewFile && currentFile != null) {
			txt_name.setText(getNewName(currentFile, ""));
			taskInfoActivity.meidaInfos.add(new MediaDataInfo(title, currentFile));
		} else {
			// String selectName = planets_spinner.getSelectedItem()
			// .toString();
			// String tempNewName = taskInfoActivity.getTempNewName(
			// taskInfoActivity.VIDEO_ROOT, currentSuffix, beforeFile,
			// selectName);
			// txt_name.setText(new File(tempNewName).getName());

			String dataItemName = txtAutoComplete.getText().toString();
			String tempNewName = taskInfoActivity.getTempNumName(dataItemName);

			txt_name.setText(tempNewName);
		}
	}

	/**
	 * 获取文件的新名字
	 * 
	 * @param file
	 *            :需要操作的文件对象
	 * @param targetFileName
	 *            :想要的新名字 如果为空会产生+1的新名称
	 * @return
	 */
	private String getNewName(File file, String targetFullName) {
		if (file != null && txtAutoComplete.getText().toString().length() > 0) {
			// String selectName = planets_spinner.getSelectedItem().toString();
			if (targetFullName.length() <= 0) {
				// targetFullName = taskInfoActivity.getTempNewName(
				// taskInfoActivity.VIDEO_ROOT, currentSuffix, file,
				// selectName);
				targetFullName = taskInfoActivity.getTempUUIDNewName(taskInfoActivity.task_v_dir, MediaDataInfo.suffixMp4);
			}

			File targetFile = new File(targetFullName);
			if (!targetFullName.equals(file.getAbsolutePath())) {
				file.renameTo(targetFile);
			}
			currentFile = targetFile;
			// return targetFile.getName();

			String dataItemName = txtAutoComplete.getText().toString();
			String tempNewName = taskInfoActivity.getTempNumName(dataItemName);
			// return showFile.getName();
			return tempNewName;
			// return targetFile.getName();
		}
		return "";
	}

	/**
	 * 开始录制
	 */
	private void startRecord() {
		tempFile = CameraUtils.customFilePath(taskInfoActivity.task_v_dir, ".mp4");
		if (tempFile != null) {
			startActivityForResult(CameraUtils.startVideo(tempFile), taskInfoActivity.TASK_VEDIO);
		}
	}

	/**
	 * 返回到选项界面
	 */
	private OnClickListener backOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (currentFile != null) {
				currentFile.delete();
			}
			taskInfoActivity.changFragment(CategoryType.VideoCollection);
		}
	};

	/**
	 * 保存并返回到选项界面
	 */
	private OnClickListener confirmOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (txtAutoComplete.getText().toString().length() <= 0) {
				ToastUtil.longShow(taskInfoActivity, "请选择视频项");
			} else if (taskInfoActivity.viewModel.currentDropDownListData.contains(txtAutoComplete.getText().toString())) {
				tempFile = currentFile == null ? beforeFile : currentFile;
				if (tempFile != null && tempFile.exists()) {
					if (txt_name.getText().toString().indexOf("_") <= -1) {
						// 如果点击视频(!isNew)进去视频信息页面后再点击录视频，不管录多少个，点击保存时只保存最后一个
						if (!isNewFile) {
							String beforeFileName = beforeFile.getAbsolutePath();
							if (currentFile != null) {
								beforeFile.delete();
							}
							getNewName(tempFile, beforeFileName);
						}
						String videoName = currentFile.getName();
						String dataItemName = txtAutoComplete.getText().toString();
						String dataItemValue = taskInfoActivity.getTempPastName(dataItemName);
						// String dataItemValue =
						// taskInfoActivity.getDataItem(value,audioName);
						// String dataItemName =
						// taskInfoActivity.formatFileName(currentFile);
						// String dataItemValue =
						// taskInfoActivity.getDataItemValue(dataItemName,
						// taskInfoActivity.VIDEO_ROOT, currentSuffix);
						if (!dataItemValue.contains(videoName)) {
							dataItemValue = taskInfoActivity.getDataItem(dataItemValue, videoName);
						}
						taskInfoActivity.doSaveMediaInfo(beforeType, beforeValue, dataItemName, dataItemValue, CategoryType.VideoCollection, true, false);
					} else {
						ToastUtil.longShow(taskInfoActivity, "请在下拉列表中选择一项作为该文件的标识");
					}
				} else {
					ToastUtil.longShow(taskInfoActivity, "没有找到当前文件");
				}
			} else {
				ToastUtil.longShow(taskInfoActivity, "请选择正确的视频项");
			}
		}

	};

	/**
	 * 播放视频
	 */
	OnClickListener palyVideoOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			tempFile = currentFile == null ? beforeFile : currentFile;
			if (tempFile != null && tempFile.exists()) {
				CameraUtils.openMedia(tempFile);
			} else {
				ToastUtil.longShow(taskInfoActivity, "没有找到当前文件");
			}
		}
	};

	/**
	 * 录制视频
	 */
	OnClickListener startRecordOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startRecord();
		}
	};

	/**
	 * 开始删除视频
	 */
	OnClickListener deleteVideoOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			tempFile = currentFile == null ? beforeFile : currentFile;
			if (tempFile != null && tempFile.exists()) {
				DialogUtil.showConfirmationDialog(taskInfoActivity, "您确认要删除这个视频文件吗？", deleteConfirmOnClickListener);
			} else {
				ToastUtil.longShow(taskInfoActivity, "没有找到当前文件");
			}
		}
	};

	/**
	 * 确认删除视频
	 */
	DialogInterface.OnClickListener deleteConfirmOnClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String videoName = taskInfoActivity.formatFileName(tempFile);
			String dataItemName = txtAutoComplete.getText().toString();
			if (tempFile.delete()) {
				txt_name.setText("");
				takeVideoTime_txt.setText("");
				videoLength_txt.setText("");
				videoFileSize_txt.setText("");
				String value = taskInfoActivity.getTempPastName(dataItemName);
				if (value.length() > 0) {
					String dataItemValue = taskInfoActivity.removeDataItem(value, videoName);
					// videoReviewImage.setImageResource(R.drawable.ic_launcher);
					// String dataItemValue =
					// taskInfoActivity.getDataItemValue(dataItemName,
					// taskInfoActivity.VIDEO_ROOT, currentSuffix);
					taskInfoActivity.doSaveMediaInfo(null, null, dataItemName, dataItemValue, CategoryType.VideoCollection, true, true);
				}
			} else {
				ToastUtil.longShow(taskInfoActivity, "文件已经不存在");
			}
		}
	};

	/**
	 * 回调函数
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (currentFile != null) {
				currentFile.delete();
			}
			if (requestCode == taskInfoActivity.TASK_VEDIO) {
				currentFile = tempFile;
			}
			if (txtAutoComplete.getText().toString().length() > 0) {
				if (isNewFile) {
					setMediaName();
				}
				setFileInfo(currentFile, false);
			} else {
				setFileInfo(currentFile, true);
			}
			btn_start.setText("重录视频");
		}
	}

	@Override
	protected void handlerBackgroundHandler(Message msg) {
		// TODO 自动生成的方法存根

	}

	// }}

}
