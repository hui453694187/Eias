package com.yunfang.eias.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.enumObj.CategoryType;
import com.yunfang.eias.model.MediaDataInfo;
import com.yunfang.eias.viewmodel.MediaFileViewModel;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.utils.BitmapHelperUtil;
import com.yunfang.framework.utils.CameraUtils;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 
 * @author 贺隽
 * 
 */
@SuppressLint("ValidFragment")
public class ShowPhotoFragment extends BaseWorkerFragment {

	// {{ 变量

	/**
	 * Activity对象
	 */
	private TaskInfoActivity taskInfoActivity;

	/**
	 * 视图模型
	 */
	private MediaFileViewModel vm = new MediaFileViewModel();

	// }}

	// {{ 界面控件

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
	 * 重拍
	 * */
	private Button btn_rephoto;

	/**
	 * 重选
	 * */
	private Button btn_reslect;

	/**
	 * 删除
	 * */
	private Button btn_save;

	/**
	 * 显示图片的控件
	 * */
	private ImageView imageView;

	/**
	 * 名称
	 * */
	private TextView txt_name;

	/**
	 * 文件大小
	 * */
	private TextView txt_filesize;

	/**
	 * 创建时间
	 * */
	private TextView txt_createdtime;

	/**
	 * 播放
	 * */
	private ImageView img_play;

	/**
	 * 自动完成文本框
	 */
	private AutoCompleteTextView txtAutoComplete;
	// }}

	// {{ 下面的数字位数 代表 菜单级别 请勿随便修改

	/**
	 * 图库
	 * */
	public final int TASK_PHOTOLIB = 101;

	/**
	 * 拍照
	 * */
	public final int TASK_PHOTO = 102;

	// }}

	// {{ 方法

	/**
	 * 构造函数
	 * 
	 * @param isNewFile
	 */
	@SuppressLint("ValidFragment")
	public ShowPhotoFragment(MediaDataInfo data) {
		vm.mData = data;
		if (vm.mData.itemFileName.length() > 0) {
			vm.beforeFile = new File(data.Path);
		} else {
			vm.currentFile = new File(data.Path);
		}
	}

	/**
	 * 获取用户控件填充到界面
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_info_photo_show, null);
	}

	/**
	 * 获取控件和设置控件
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
		title_txt.setText("图片信息");
		if (vm.mData.itemFileName.length() > 0) {
			txtAutoComplete.setText(vm.mData.ItemName);
			txt_name.setText(vm.mData.Title);
			if (vm.mData.isLose) {
				txtAutoComplete.setEnabled(false);
			} else {
				imageView.setImageBitmap(vm.mData.ThumbnailPhoto);
				txt_filesize.setText(vm.mData.Size);
				txt_createdtime.setText(vm.mData.CreatedTime);
			}
		} else {
			txt_name.setText("请选择图片项");
			imageView.setImageBitmap(vm.mData.ThumbnailPhoto);
			txt_filesize.setText(vm.mData.Size);
			txt_createdtime.setText(vm.mData.CreatedTime);
		}
	}

	/**
	 * 找到控件
	 */
	private void getControls() {
		taskInfoActivity = (TaskInfoActivity) getActivity();
		mView = getView();
		title_txt = (TextView) mView.findViewById(R.id.title_txt);
		back_txt = (TextView) mView.findViewById(R.id.back_txt);
		delete_txt = (TextView) mView.findViewById(R.id.delete_txt);
		imageView = (ImageView) mView.findViewById(R.id.media_preview);
		btn_rephoto = (Button) mView.findViewById(R.id.media_rephoto);
		btn_reslect = (Button) mView.findViewById(R.id.media_reselect);
		btn_save = (Button) mView.findViewById(R.id.media_save);
		img_play = (ImageView) mView.findViewById(R.id.media_paly);
		txt_name = (TextView) mView.findViewById(R.id.media_name);
		txt_filesize = (TextView) mView.findViewById(R.id.media_filesize);
		txt_createdtime = (TextView) mView.findViewById(R.id.media_createdtime);
		txtAutoComplete = (AutoCompleteTextView) mView.findViewById(R.id.image_gridview_newitem_auto);
	}

	/**
	 * 设置绑定事件
	 */
	private void setListener() {
		back_txt.setOnClickListener(OnClickListener);
		delete_txt.setOnClickListener(OnClickListener);
		imageView.setOnClickListener(OnClickListener);
		img_play.setOnClickListener(OnClickListener);
		btn_reslect.setOnClickListener(OnClickListener);
		btn_rephoto.setOnClickListener(OnClickListener);
		btn_save.setOnClickListener(OnClickListener);
		taskInfoActivity.setAutoCompleteData(txtAutoComplete, autoLister);
	}

	/**
	 * 切换选择项后
	 */
	private OnItemClickListener autoLister = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String dataItemName = txtAutoComplete.getText().toString();
			String tempNewName = taskInfoActivity.getTempNumName(dataItemName);
			txt_name.setText(tempNewName);
		}
	};

	/**
	 * 事件
	 */
	private OnClickListener OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_txt:
				if (vm.currentFile != null) {
					taskInfoActivity.deleteTaskResouce(vm.currentFile);
				}
				taskInfoActivity.changFragment(CategoryType.PictureCollection);
				break;
			case R.id.delete_txt:
				DialogUtil.showConfirmationDialog(taskInfoActivity, "您确认要删除这个图片文件吗？", deleteConfirmOnClickListener);
				break;
			case R.id.media_rephoto:
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String targetFileFuleName = taskInfoActivity.task_p_dir + File.separator + timeStamp + MediaDataInfo.suffixJpg;
				vm.tempFile = new File(targetFileFuleName);
				// 重拍
				startActivityForResult(CameraUtils.startGetPicFromPhoto(vm.tempFile), taskInfoActivity.TASK_PHOTO);
				break;
			case R.id.media_reselect:
				// 重选
				startActivityForResult(CameraUtils.startGetPicPhotoAlbum(), taskInfoActivity.TASK_PHOTOLIB);
				break;
			case R.id.media_save:
				saveMedia();
				break;
			default:
				vm.tempFile = vm.currentFile == null ? vm.beforeFile : vm.currentFile;
				if (vm.tempFile != null && vm.tempFile.exists()) {
					CameraUtils.openMedia(vm.tempFile);
				} else {
					ToastUtil.longShow(taskInfoActivity, "没有找到当前文件");
				}
				break;
			}
		}

	};

	private void saveMedia() {
		if (txtAutoComplete.getText().toString().length() <= 0) {
			ToastUtil.longShow(taskInfoActivity, "请选择图片项");
		} else if (taskInfoActivity.viewModel.currentDropDownListData.contains(txtAutoComplete.getText().toString())) {
			vm.tempFile = vm.currentFile == null ? vm.beforeFile : vm.currentFile;
			if (vm.tempFile != null && vm.tempFile.exists()) {
				// if (compressImage(vm.tempFile)) {
				String dataItemName = txtAutoComplete.getText().toString();
				String dataItemValue = taskInfoActivity.getTempPastName(dataItemName);
				// 不为空表示为修改
				String target = "";
				if (vm.mData.itemFileName.length() > 0) {
					target = vm.tempFile.getParent() + File.separator + vm.mData.itemFileName;
				} else {
					String uuid = UUID.randomUUID().toString();
					target = vm.tempFile.getParent() + File.separator + uuid + MediaDataInfo.suffixJpg;
					dataItemValue = taskInfoActivity.getDataItem(dataItemValue, uuid + MediaDataInfo.suffixJpg);
				}
				File targetFile = new File(target);
				if (targetFile.exists()) {
					taskInfoActivity.deleteTaskResouce(targetFile);
				}
				vm.tempFile.renameTo(targetFile);
				String t_FullName = taskInfoActivity.getThumbnailFullName(targetFile);
				BitmapHelperUtil.decodeThumbnail(targetFile.getAbsolutePath(), t_FullName);
				deletTaskResouce();
				taskInfoActivity.meidaInfos.add(new MediaDataInfo(vm.mData.Title, vm.tempFile));
				taskInfoActivity.doSaveMediaInfo(vm.mData.ItemName, vm.mData.itemFileName, dataItemName, dataItemValue, CategoryType.PictureCollection, true, false);
				taskInfoActivity.additional(targetFile.getName(), false);
				// } else {
				// ToastUtil.longShow(taskInfoActivity, "图片压缩失败，建议重试");
				// }
			} else {
				ToastUtil.longShow(taskInfoActivity, "没有找到当前文件,请拍摄或者选择后再保存");
			}
		} else {
			ToastUtil.longShow(taskInfoActivity, "请选择正确的图片项");
		}
	}

	/**
	 * 图片压缩方法(限制所保存的图片大小不超过指定值)
	 * 
	 * @param tempFile所压缩图片
	 * @return 是否压缩成功
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private Boolean compressImage(File tempFile) {
		String maxImageSizeStr = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_MAXIMAGESIZE);
		long maxImageSize = Long.parseLong(maxImageSizeStr) * 1024;
		Map<Double, Integer> scaleMap = new HashMap<Double, Integer>();
		scaleMap.put(0.87, 99);
		scaleMap.put(0.66, 98);
		scaleMap.put(0.54, 97);
		scaleMap.put(0.45, 96);
		scaleMap.put(0.40, 95);
		scaleMap.put(0.33, 94);
		scaleMap.put(0.30, 93);
		scaleMap.put(0.25, 92);
		scaleMap.put(0.24, 91);
		scaleMap.put(0.20, 88);
		scaleMap.put(0.15, 80);
		scaleMap.put(0.10, 70);
		scaleMap.put(0.05, 30);
		String filePath = tempFile.toString();
		Bitmap image = null;
		try {
			image = BitmapFactory.decodeFile(filePath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			long length = tempFile.length() / 2048;
			if (length > maxImageSize) {
				options = 99;
				double scale = (double) maxImageSize / (double) length;
				for (double key : scaleMap.keySet()) {
					if (scale <= key) {
						if (options > scaleMap.get(key)) {
							options = scaleMap.get(key);
						}
					}
				}
			}
			FileOutputStream out = new FileOutputStream(filePath);
			image.compress(Bitmap.CompressFormat.JPEG, options, out);
			// 压缩完毕
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.longShow(taskInfoActivity, "文件压缩失败,建议清理内存后重试");
			// File file = new File(filePath);
			// file.delete();
			return false;
		} finally {
			if (!image.isRecycled()) {
				image.recycle();// 记得释放资源，否则会内存溢出
			}
		}
		return true;
	}

	/**
	 * 移除临时的资源图片
	 */
	private void deletTaskResouce() {
		if (vm.beforeFile != null && vm.beforeFile.getPath().equals(vm.tempFile.getPath())) {
			taskInfoActivity.deleteTaskResouce(vm.beforeFile);
		}
		if (vm.currentFile != null && vm.currentFile.getPath().equals(vm.tempFile.getPath())) {
			taskInfoActivity.deleteTaskResouce(vm.currentFile);
		}
	}

	/**
	 * 回调函数
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK) {
			if (vm.currentFile != null) {
				taskInfoActivity.deleteTaskResouce(vm.currentFile);
			}
			switch (requestCode) {
			case TASK_PHOTO:
				vm.currentFile = vm.tempFile;
				break;
			case TASK_PHOTOLIB:
				// 为false时为复制
				boolean copyOrPasteBoolean = false;
				String copyOrPaste = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_PICTURECOPYORPASTE);
				if (copyOrPaste != null && copyOrPaste.equals("剪切")) {
					copyOrPasteBoolean = true;
				}
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String targetFileFuleName = taskInfoActivity.task_p_dir + File.separator + timeStamp + MediaDataInfo.suffixJpg;
				FileUtil.movePhotoLibFileToCustomDir(taskInfoActivity, intent, targetFileFuleName, copyOrPasteBoolean, true);
				vm.currentFile = new File(targetFileFuleName);
				break;
			default:
				break;
			}
			MediaDataInfo m = new MediaDataInfo(vm.mData.Title, vm.currentFile);
			imageView.setImageBitmap(m.ThumbnailPhoto);
			txt_filesize.setText(m.Size);
			txt_createdtime.setText(m.CreatedTime);
		}
	}

	/**
	 * 确认删除文件
	 */
	DialogInterface.OnClickListener deleteConfirmOnClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			vm.tempFile = vm.beforeFile == null ? vm.currentFile : vm.beforeFile;
			String pictureName = taskInfoActivity.formatFileName(vm.tempFile);
			String dataItemName = txtAutoComplete.getText().toString();
			if (taskInfoActivity.deleteTaskResouce(vm.tempFile)) {
				imageView.setImageBitmap(null);
				txt_name.setText("");
				txt_filesize.setText("");
				txt_createdtime.setText("");
				String value = taskInfoActivity.getTempPastName(dataItemName);
				if (value.length() > 0) {
					String dataItemValue = taskInfoActivity.removeDataItem(value, pictureName);
					taskInfoActivity.doSaveMediaInfo(null, null, dataItemName, dataItemValue, CategoryType.PictureCollection, true, true);
					taskInfoActivity.additional(vm.tempFile.getName(), true);
				}
			} else {
				ToastUtil.longShow(taskInfoActivity, "文件已经不存在");
			}
		}
	};

	/**
	 * 把用户控件添加到界面之后需要执行的对象
	 */
	@Override
	protected void handlerBackgroundHandler(Message msg) {
		Message message = new Message();
		message.what = msg.what;
		switch (msg.what) {
		case 0:
			message.obj = "";
			break;
		default:
			break;
		}
	}

	// }}
}
