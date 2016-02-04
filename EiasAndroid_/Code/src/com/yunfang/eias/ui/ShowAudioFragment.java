package com.yunfang.eias.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
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

import com.yunfang.eias.R;
import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.eias.logic.DataLogOperator;
import com.yunfang.eias.model.MediaDataInfo;
import com.yunfang.eias.utils.DataCheckHelper;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.view.LoadingUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 
 * 项目名称：yunfang.eias 类名称：ShowAudioFragment 类描述：音频信息展示界面 创建人：lihc 修改人：贺隽
 * 创建时间：2014-5-9 下午5:46:36
 * 
 * @version
 */
@SuppressLint({ "ValidFragment", "SimpleDateFormat", "InflateParams" })
public class ShowAudioFragment extends BaseWorkerFragment {

	// {{ 界面控件

	/**
	 * 媒体播放器
	 */
	private MediaPlayer mediaPlayer;

	/**
	 * 录音机
	 */
	private MediaRecorder mediaRecorder;

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
	private TextView audio_txt_title;

	/**
	 * 返回按钮
	 * */
	private TextView audio_txt_back;

	/**
	 * 删除按钮
	 * */
	private TextView audio_txt_delete;

	/**
	 * 保存按钮
	 * */
	private Button audio_btn_save;

	/**
	 * 开始录音按钮
	 * */
	private Button audio_btn_start;

	/**
	 * 停止录音按钮
	 * */
	private Button audio_btn_end;

	/**
	 * 显示图片的控件
	 * */
	private ImageView previewImageView;

	/**
	 * 音频名称控件
	 * */
	private TextView txt_name;

	/**
	 * 文件大小控件
	 * */
	private TextView recordFileSize_txt;

	/**
	 * 播放时长控件
	 * */
	private TextView recordLength_txt;

	/**
	 * 录音时间控件
	 * */
	private TextView takeRecordTime_txt;

	/**
	 * 播放录音按钮控件
	 * */
	private ImageView ico_record;

	/**
	 * 录音时的提示过程
	 */
	private LoadingUtil loadingUtil;

	/**
	 * 是否为添加新文件
	 */
	private Boolean isNewFile;

	/**
	 * 当前音频是否在播放
	 */
	private Boolean pause;

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

	/**
	 * 文件信息
	 */
	private MediaDataInfo mediaFile;

	/**
	 * 返回
	 */
	private final int TASK_BACK = 1;

	/**
	 * 保存
	 */
	private final int TASK_SUBMIT = 2;

	/**
	 * 开始录音
	 */
	private final int TASK_START_RECORD = 3;

	/**
	 * 结束录音
	 */
	private final int TASK_STOP_RECORD = 4;

	/**
	 * 播放录音
	 */
	private final int TASK_PLAY_AUDIO = 5;

	/**
	 * 删除
	 */
	private final int TASK_DELETE_AUDIO = 8;

	// }}

	/**
	 * 构造函数
	 * 
	 * @param mediaFile
	 *            :媒体文件对象
	 * @param isNewFile
	 *            :是否为新添加的文件
	 */
	public ShowAudioFragment(MediaDataInfo mediaFile, Boolean isNewFile) {
		this.mediaFile = mediaFile;
		this.isNewFile = isNewFile;
		this.title = "请选择录音类型";
		if (this.mediaFile != null && this.mediaFile.Path != null) {
			this.title = mediaFile.Title;
			this.beforeFile = new File(mediaFile.Path);
			this.tempFile = new File(mediaFile.Path);
			this.pause = true;
		} else {
			this.pause = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_info_audio_show, null);
	}

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
		setRecordState(false);
		setDisplayObject();
		setFileInfo(currentFile == null ? beforeFile : currentFile, true);
	}

	/**
	 * 获取控件
	 */
	private void getControls() {
		taskInfoActivity = (TaskInfoActivity) getActivity();
		mView = getView();
		audio_txt_title = (TextView) mView.findViewById(R.id.title_txt);
		audio_txt_back = (TextView) mView.findViewById(R.id.back_txt);
		audio_txt_delete = (TextView) mView.findViewById(R.id.delete_txt);
		previewImageView = (ImageView) mView.findViewById(R.id.media_preview);
		audio_btn_save = (Button) mView.findViewById(R.id.media_save);
		audio_btn_start = (Button) mView.findViewById(R.id.media_start);
		audio_btn_end = (Button) mView.findViewById(R.id.media_end);

		ico_record = (ImageView) mView.findViewById(R.id.media_paly);
		txt_name = (TextView) mView.findViewById(R.id.media_name);
		recordFileSize_txt = (TextView) mView.findViewById(R.id.media_filesize);
		recordLength_txt = (TextView) mView.findViewById(R.id.media_palytime);
		takeRecordTime_txt = (TextView) mView.findViewById(R.id.media_createdtime);
		txtAutoComplete = (AutoCompleteTextView) mView.findViewById(R.id.image_gridview_newitem_auto);
	}

	/**
	 * 设置绑定事件
	 */
	private void setListener() {
		audio_txt_back.setOnClickListener(onClickListener);
		audio_txt_delete.setOnClickListener(onClickListener);
		previewImageView.setOnClickListener(onClickListener);
		ico_record.setOnClickListener(onClickListener);
		audio_btn_save.setOnClickListener(onClickListener);
		audio_btn_start.setOnClickListener(onClickListener);
		audio_btn_end.setOnClickListener(onClickListener);
	}

	/**
	 * 在播放或者录音的过程中不能返回或者直接保存
	 * 
	 * @param isEnabled
	 *            :是否禁用或者启用按钮
	 */
	private void setSumbitAndBackEnable(Boolean isEnabled) {
		audio_txt_back.setEnabled(isEnabled);
		audio_txt_delete.setEnabled(isEnabled);
		audio_btn_save.setEnabled(isEnabled);
		audio_btn_start.setEnabled(isEnabled);
	}

	/**
	 * 设置录音按钮状态
	 * 
	 * @param isStart
	 */
	private void setRecordState(Boolean isStart) {
		if (isStart) {
			audio_btn_start.setEnabled(false);
			audio_btn_end.setEnabled(true);
			audio_btn_save.setEnabled(false);
		} else {
			audio_btn_start.setEnabled(true);
			audio_btn_end.setEnabled(false);
			audio_btn_save.setEnabled(true);
		}
	}

	/**
	 * 设置需要显示出来的东西
	 */
	private void setDisplayObject() {
		mediaPlayer = new MediaPlayer();
		audio_btn_start.setText(isNewFile ? "开始录音" : "重新录音");
		audio_txt_title.setText("点击图标录音");
		txt_name.setText(title);
		if (loadingUtil == null) {
			loadingUtil = new LoadingUtil(taskInfoActivity, previewImageView, 400);
		}

		// 点击一个文件进入之后下拉列表选中对应的值
		if (!isNewFile && beforeFile != null) {
			String selectItem = taskInfoActivity.getSeleteDataItem(beforeFile.getName());
			txtAutoComplete.setText(selectItem);
			beforeType = selectItem;
			beforeValue = beforeFile.getName();
		}

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
			recordLength_txt.setText(m.Duration);
			takeRecordTime_txt.setText(m.CreatedTime);
			recordFileSize_txt.setText(m.Size);
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
			if (targetFullName.length() <= 0) {
				targetFullName = taskInfoActivity.getTempUUIDNewName(taskInfoActivity.task_a_dir, MediaDataInfo.suffixAmr);
			}
			File targetFile = new File(targetFullName);
			if (!targetFullName.equals(file.getAbsolutePath())) {
				file.renameTo(targetFile);
			}
			currentFile = targetFile;
			String dataItemName = txtAutoComplete.getText().toString();
			String tempNewName = taskInfoActivity.getTempNumName(dataItemName);
			return tempNewName;
		}
		return "";
	}

	/**
	 * 播放音乐
	 * 
	 * @param playPosition
	 */
	private boolean play(int playPosition) {
		try {
			mediaPlayer.reset();// 把各项参数恢复到初始状态
			/**
			 * 通过MediaPlayer.setDataSource()
			 * 的方法,将URL或文件路径以字符串的方式传入.使用setDataSource ()方法时,要注意以下三点:
			 * 1.构建完成的MediaPlayer 必须实现Null 对像的检查.
			 * 2.必须实现接收IllegalArgumentException 与IOException
			 * 等异常,在很多情况下,你所用的文件当下并不存在. 3.若使用URL 来播放在线媒体文件,该文件应该要能支持pragressive
			 * 下载.
			 */

			tempFile = currentFile == null ? beforeFile : currentFile;
			if (tempFile != null && tempFile.exists()) {
				mediaPlayer.setDataSource(currentFile == null ? beforeFile.getAbsolutePath() : currentFile.getAbsolutePath());
				mediaPlayer.prepare();// 进行缓冲
				mediaPlayer.setOnPreparedListener(new PreparedListener(playPosition));
				mediaPlayer.setOnCompletionListener(playRecordEnd);
				audio_txt_title.setText("正在播放录音");
			} else {
				ToastUtil.longShow(taskInfoActivity, "没有找到当前文件");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			//当录音权限被其他软件或系统禁止 或者其他原因音频文件为空时，会抛出运行时异常，这里做提示
			ToastUtil.longShow(taskInfoActivity, R.string.recordErr_info);
			return false;
		}
		return true;
	}

	/**
	 * 按钮操作
	 */
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Message loginMsg = new Message();
			switch (v.getId()) {
			case R.id.back_txt:
				loginMsg.what = TASK_BACK;
				break;
			case R.id.media_save:
				loginMsg.what = TASK_SUBMIT;
				break;
			case R.id.media_preview:
			case R.id.media_paly:
				loginMsg.what = TASK_PLAY_AUDIO;
				break;
			case R.id.media_start:
				loginMsg.what = TASK_START_RECORD;
				break;
			case R.id.media_end:
				loginMsg.what = TASK_STOP_RECORD;
				break;
			case R.id.delete_txt:
				loginMsg.what = TASK_DELETE_AUDIO;
				break;
			default:
				break;
			}
			mBackgroundHandler.sendMessage(loginMsg);
		}
	};

	/**
	 * 播放录音完之后
	 */
	private OnCompletionListener playRecordEnd = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			if (loadingUtil != null) {
				loadingUtil.clearAnimation();
			}
			setSumbitAndBackEnable(true);
			pause = true;
			tempFile = null;
			audio_txt_title.setText("录音播放完成");
		}
	};

	/**
	 * 确认删除文件
	 */
	DialogInterface.OnClickListener deleteConfirmOnClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String audioName = taskInfoActivity.formatFileName(tempFile);
			String dataItemName = txtAutoComplete.getText().toString();
			if (tempFile.delete()) {
				mediaPlayer.release();
				txt_name.setText("");
				recordFileSize_txt.setText("");
				recordLength_txt.setText("");
				takeRecordTime_txt.setText("");
				String value = taskInfoActivity.getTempPastName(dataItemName);
				if (value.length() > 0) {
					String dataItemValue = taskInfoActivity.removeDataItem(value, audioName);
					// String dataItemValue =
					// taskInfoActivity.getDataItemValue(dataItemName,
					// taskInfoActivity.AUDIO_ROOT, MediaDataInfo.suffixAmr);
					taskInfoActivity.doSaveMediaInfo(beforeType, beforeValue, dataItemName, dataItemValue, CategoryType.AudioCollection, true, true);
				}
			} else {
				ToastUtil.longShow(taskInfoActivity, "文件已经不存在");
			}
		}
	};

	@Override
	protected void handlerBackgroundHandler(Message msg) {
		Message resultMsg = new Message();
		resultMsg.what = msg.what;
		switch (msg.what) {
		case TASK_BACK:
			if (currentFile != null) {
				currentFile.delete();
			}
			break;
		case TASK_SUBMIT:
			tempFile = currentFile == null ? beforeFile : currentFile;
			break;
		case TASK_PLAY_AUDIO:
			break;
		case TASK_START_RECORD:
			break;
		case TASK_STOP_RECORD:
			break;
		case TASK_DELETE_AUDIO:
			tempFile = currentFile == null ? beforeFile : currentFile;
			break;
		default:
			break;
		}
		mUiHandler.sendMessage(resultMsg);
	}

	/**
	 * 开始录音
	 */
	private void startRecorder() {
		try {
			if (mediaRecorder == null) {
				mediaRecorder = new MediaRecorder();
			}
			// 设置音频来源(一般为麦克风)
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频输出格式（默认的输出格式）
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			// 设置音频编码方式（默认的编码方式）
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			// 创建一个临时的音频输出文件
			tempFile = File.createTempFile(timeStamp, ".amr", new File(taskInfoActivity.task_a_dir));
			mediaRecorder.setOutputFile(tempFile.getAbsolutePath());
			mediaRecorder.prepare();
			mediaRecorder.start();

			setSumbitAndBackEnable(false);
			setRecordState(true);
			loadingUtil.startAnimation();
			pause = false;

			audio_txt_title.setText("正在录音中...");
		} catch (Exception e) {
			if (e != null) {
				DataLogOperator.taskHttp("ShowAudioFragment=>开始录音(startRecorder)", e.getMessage());
			}
			ToastUtil.longShow(taskInfoActivity, R.string.recordErr_info);
		}
	}

	@Override
	protected void handUiMessage(Message msg) {
		super.handUiMessage(msg);
		switch (msg.what) {
		case TASK_BACK:
			taskInfoActivity.changFragment(CategoryType.AudioCollection);
			break;
		case TASK_SUBMIT:
			if (txtAutoComplete.getText().toString().length() <= 0) {
				ToastUtil.longShow(taskInfoActivity, "请选择音频项");
			} else if (taskInfoActivity.viewModel.currentDropDownListData.contains(txtAutoComplete.getText().toString())) {
				tempFile = currentFile == null ? beforeFile : currentFile;
				if (tempFile != null && tempFile.exists()) {
					//TODO  判断当前文件大小是否为0 大小为零不可保存， 同时删除本地文件
					if (txt_name.getText().toString().indexOf("_") <= -1) {
						// 如果点击视频(!isNew)进去视频信息页面后再点击录视频，不管录多少个，点击保存时只保存最后一个
						if (!isNewFile) {
							String beforeFileName = beforeFile.getAbsolutePath();
							if (currentFile != null) {
								beforeFile.delete();
							}
							getNewName(tempFile, beforeFileName);
						}
						String audioName = currentFile.getName();
						String dataItemName = txtAutoComplete.getText().toString();
						String dataItemValue = taskInfoActivity.getTempPastName(dataItemName);
						// String dataItemValue =
						// taskInfoActivity.getDataItem(value,audioName);
						if (!dataItemValue.contains(audioName)) {
							dataItemValue = taskInfoActivity.getDataItem(dataItemValue, audioName);
						}
						// String dataItemValue =
						// taskInfoActivity.getDataItemValue(dataItemName,
						// taskInfoActivity.AUDIO_ROOT,
						// MediaDataInfo.suffixAmr);
						taskInfoActivity.doSaveMediaInfo(beforeType, beforeValue, dataItemName, dataItemValue, CategoryType.AudioCollection, true, false);
					} else {
						ToastUtil.longShow(taskInfoActivity, "请在下拉列表中选择一项作为该文件的标识");
					}
				} else {
					ToastUtil.longShow(taskInfoActivity, "没有找到当前文件");
				}
			} else {
				ToastUtil.longShow(taskInfoActivity, "请选择正确的音频项");
			}
			break;
		case TASK_PLAY_AUDIO:
			// 音频地址
			if (tempFile != null && tempFile.exists()) {
				if (pause == null) {
					pause = true;
				}
				if (mediaPlayer.isPlaying()) {
					// 如果正在播放
					mediaPlayer.pause();// 暂停
					mediaPlayer.stop();
					loadingUtil.clearAnimation();
					pause = true;
					setSumbitAndBackEnable(true);
					tempFile = null;
					audio_txt_title.setText("停止播放录音");
				} else if (pause) {
					// 如果处于暂停状态
					boolean isPlay = play(0); // 播放音乐
					if (isPlay) {
						loadingUtil.startAnimation();
						pause = false;
						setSumbitAndBackEnable(pause);
					}

				} else {
					stopRecorder();
				}
			} else {// 录音文件不存在，则重新录音
				startRecorder();
			}
			break;
		case TASK_START_RECORD:
			startRecorder();
			break;
		case TASK_STOP_RECORD:
			stopRecorder();
			break;
		case TASK_DELETE_AUDIO:
			if (tempFile != null && tempFile.exists()) {
				DialogUtil.showConfirmationDialog(taskInfoActivity, "您确认要删除这个音频文件吗？", deleteConfirmOnClickListener);
			} else {
				ToastUtil.longShow(taskInfoActivity, "没有找到当前文件");
			}
			break;
		default:
			break;
		}
		if (taskInfoActivity != null) {
			taskInfoActivity.loadingWorker.closeLoading();
		}
	}

	/**
	 * 停止录音
	 */
	private void stopRecorder() {
		try {
			if (tempFile != null && mediaRecorder != null) {
				if (currentFile != null) {
					currentFile.delete();
				}
				currentFile = tempFile;
				mediaRecorder.stop();
				loadingUtil.clearAnimation();
				setRecordState(false);
				setSumbitAndBackEnable(true);
				pause = true;
				audio_txt_title.setText("已停止录音");
				//TODO 判断音频文件大小，如果音频文件大小为0 则视为录音失败，不保存录音文件
				long fileSize=DataCheckHelper.getFileSizes(currentFile);
				if(fileSize==0){//文件大小为零，不保存录音，  提示录音失败
					ToastUtil.longShow(taskInfoActivity, R.string.recordErr_info);
					currentFile.delete();
					return;
				}
				if (txtAutoComplete.getText().toString().length() > 0) {
					if (isNewFile) {
						setMediaName();
					}
					setFileInfo(currentFile, false);
				} else {
					setFileInfo(currentFile, true);
				}
				tempFile = currentFile;
			}
		} catch (Exception e) {
			ToastUtil.longShow(taskInfoActivity, R.string.recordErr_info);
		}
	}

	@Override
	public void onDestroy() {
		mediaPlayer.release();
		mediaPlayer = null;
		super.onDestroy();
	}

	// }}

	/**
	 * 播放监听
	 * 
	 * @author 贺隽
	 * 
	 */
	private final class PreparedListener implements android.media.MediaPlayer.OnPreparedListener {
		private int playPosition;

		public PreparedListener(int playPosition) {
			this.playPosition = playPosition;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start();// 开始播放
			if (playPosition > 0) {
				mediaPlayer.seekTo(playPosition);
			}
		}

	}

}
