package com.yunfang.eias.beta.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.TextView;

import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.base.BroadRecordType;
import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.dto.AdditionalResource;
import com.yunfang.eias.beta.enumObj.CategoryType;
import com.yunfang.eias.beta.logic.TaskOperator;
import com.yunfang.eias.beta.model.DataCategoryDefine;
import com.yunfang.eias.beta.model.DataDefine;
import com.yunfang.eias.beta.model.DataFieldDefine;
import com.yunfang.eias.beta.model.MediaDataInfo;
import com.yunfang.eias.beta.model.TaskCategoryInfo;
import com.yunfang.eias.beta.model.TaskDataItem;
import com.yunfang.eias.beta.model.TaskInfo;
import com.yunfang.eias.beta.tables.DataDefineWorker;
import com.yunfang.eias.beta.tables.TaskDataWorker;
import com.yunfang.eias.beta.ui.Adapter.MeidaListAdapter;
import com.yunfang.eias.beta.ui.Adapter.SearchAdapter;
import com.yunfang.eias.beta.ui.Adapter.TypeItemGridViewAdapter;
import com.yunfang.eias.beta.ui.Adapter.TypeItemGridViewAdapter.ItemType;
import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.CameraUtils;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 
 * 项目名称：yunfang.eias 类名称：ShowMediaListFragment 类描述： 多媒体列表界面的Fragment 创建人：李华春
 * 创建时间：2014-5-6 下午2:09:03 修改人：贺隽 修改时间：2014-5-4 下午2:09:03
 * 
 * @version
 */
@SuppressLint({ "ValidFragment", "InflateParams" })
public class ShowMediaListFragment extends BaseWorkerFragment implements OnScrollListener {

	// {{ 变量

	/**
	 * 多媒体类型
	 */
	private CategoryType mediaType;

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
	private TextView home_top_title;

	/**
	 * 菜单按钮
	 * */
	private Button btn_menu;

	/**
	 * 保存按钮
	 * */
	private Button list_reload;

	/**
	 * 文件数组
	 */
	public File mediaFile;

	/**
	 * 文件信息
	 */
	public MediaDataInfo mediaInfo;

	/**
	 * 文件名称数组
	 */
	public String[] mediaNameArray;

	/**
	 * 文件路径数组
	 */
	public String[] mediaPathArray;

	/**
	 * 呈现文件的控件
	 */
	private GridView mediaGrid;

	/**
	 * 上一步按钮(点击后保存数据并且直接跳到上一个分类子项)
	 */
	private Button btn_previous_Category;

	/**
	 * 下一步(点击后保存数据并且直接跳到下一个分类子项)
	 */
	private Button btn_next_Category;

	/**
	 * 
	 */
	private Button home_top_additional;

	/**
	 * 多选时用于删除
	 */
	private Button media_bottom_bar_delete;

	/**
	 * 全选
	 */
	private CheckBox header_bar_leave_select_all;

	/**
	 * 多选后图片选择类型提示信息
	 */
	private Button media_bottom_select_tips;
	
	/** 媒体类型自动适配框 *//*
	public MyAutoCompleteTv media_type_autoComplecTv;
	
	*//** 默认类型选择视图 *//*
	private View defult_pic_type_linearLayout;*/
	/** 子项类型统计 视图*/
	private GridView type_item_gridView;
	/** 子项统计视图适配器*/
	private TypeItemGridViewAdapter typeItemGvAdapter;
	/**
	 * 勘察表分类项
	 */
	private DataCategoryDefine dataCategoryDefine;

	/**
	 * 分类项索引
	 */
	private Integer categoryIndex = 0;

	/**
	 * 需要操作资源的索引
	 */
	private int onItemIndex = -1;
	
	// }}

	// {{ 下面的数字位数 代表 菜单级别 请勿随便修改

	/**
	 * 拍照的标志
	 * */
	public final int TASK_ITEMS = 1;

	/**
	 * 进入图片列表
	 * */
	public final int TASK_PHOTOITEM = 10;

	/**
	 * 自定义图库
	 */
	public static final int TASK_ALBUM = 100;
	/**
	 * 图库
	 * */
	public final int TASK_PHOTOLIB = 101;

	/**
	 * 拍照
	 * */
	public final int TASK_PHOTO = 102;

	/**
	 * 进入录音列表
	 * */
	public final int TASK_RECORDITEM = 11;

	/**
	 * 音频库
	 * */
	public final int TASK_AUDIOLIB = 111;

	/**
	 * 录音
	 * */
	public final int TASK_AUDIO = 112;

	/**
	 * 进入视频列表
	 * */
	public final int TASK_VEDIOITEM = 12;

	/**
	 * 视频库
	 * */
	public final int TASK_VEDIOLIB = 121;

	/**
	 * 录像
	 * */
	public final int TASK_VEDIO = 123;

	/**
	 * 保存并移动到上一个分类项子项编辑页面
	 */
	private final int TASK_SAVE_MOVEPREVIOUSCATEGORY = 3;

	/**
	 * 保存并移动到下一个分类项子项编辑页面
	 */
	private final int TASK_SAVE_MOVENEXTCATEGORY = 4;

	/**
	 * 保存当前的任务子项列表信息
	 */
	private final int TASK_SAVETASKITEMS = 124;

	// }}

	// {{ 方法

	/**
	 * 构造函数
	 */
	public ShowMediaListFragment() {
		if (mediaInfo == null) {
			mediaInfo = new MediaDataInfo("", new File(""));
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param mediaType
	 *            :媒体类型
	 */
	public ShowMediaListFragment(CategoryType mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * 添加用户控件
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		taskInfoActivity = (TaskInfoActivity) getActivity();
		taskInfoActivity.currentInstanceFragmentName = "TaskItemsFragment";
		return inflater.inflate(R.layout.task_info_photo_add, null);
	}

	/**
	 * 加载普通控件
	 * 
	 * @param savedInstanceState
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
		refreshMediaList();
	}

	/**
	 * 获取控件
	 */
	private void getControls() {

		mView = getView();

		home_top_title = (TextView) mView.findViewById(R.id.home_top_title);
		list_reload = (Button) mView.findViewById(R.id.list_reload);
		btn_menu = (Button) mView.findViewById(R.id.btn_menu);
		mediaGrid = (GridView) mView.findViewById(R.id.photo_show_gridView);
		home_top_additional = (Button) mView.findViewById(R.id.home_top_additional);
		media_bottom_bar_delete = (Button) mView.findViewById(R.id.media_bottom_bar_delete);
		media_bottom_select_tips = (Button) mView.findViewById(R.id.media_bottom_select_tips);
		header_bar_leave_select_all = (CheckBox) mView.findViewById(R.id.header_bar_leave_select_all);
		
		/*defult_pic_type_linearLayout=mView.findViewById(R.id.defult_pic_type_linearLayout);*/
		type_item_gridView=(GridView)mView.findViewById(R.id.type_item_gridView);
		// 判断当前媒体类型，是否执行加载类型选择视图
		if(mediaType==CategoryType.PictureCollection){
			/*media_type_autoComplecTv=(MyAutoCompleteTv)mView.findViewById(R.id.media_type_autoComplecTv);*/
			/*initPicAutoComplecTv();*/
		}else{//非图片类型，隐藏
			/*defult_pic_type_linearLayout.setVisibility(View.GONE);*/
			type_item_gridView.setVisibility(View.GONE);
		}
		
			
		list_reload.setVisibility(View.GONE);

		btn_menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				taskInfoActivity.toCategoriesFragment();
			}
		});

		// 单击
		mediaGrid.setOnItemClickListener(mediaGridOnItemClickListener);
		// 长按
		mediaGrid.setOnItemLongClickListener(mediaGridOnItemLongClickListener);

		if (EIASApplication.IsNetworking && !EIASApplication.IsOffline) {
			list_reload = (Button) mView.findViewById(R.id.list_reload);
			list_reload.setBackgroundResource(R.drawable.log_title_upload);
			list_reload.setOnClickListener(backOnClickListener);
		}

		taskInfoActivity.appHeader.visBackView(true);
		taskInfoActivity.appHeader.setTitle(taskInfoActivity.viewModel.currentTask.TaskNum);

		// 上一步以及下一步
		btn_previous_Category = (Button) mView.findViewById(R.id.btn_previous_Category);
		btn_next_Category = (Button) mView.findViewById(R.id.btn_next_Category);
		btn_previous_Category.setVisibility(View.VISIBLE);
		btn_next_Category.setVisibility(View.VISIBLE);
		for (TaskCategoryInfo item : taskInfoActivity.viewModel.currentTaskCategoryInfos) {
			if (item.ID == taskInfoActivity.viewModel.currentCategory.ID) {
				break;
			}
			categoryIndex++;
		}

		// String step = "(" + (categoryIndex + 1) + "/" +
		// taskInfoActivity.viewModel.currentTaskCategoryInfos.size() + "步)";
		home_top_title.setText(taskInfoActivity.viewModel.currentCategory.RemarkName);

		// 上一步按钮触发事件
		btn_previous_Category.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAndMovePreviousCategory();
			}
		});

		// 下一步按钮触发事件
		btn_next_Category.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAndMoveNextCategory();
			}
		});

		if (taskInfoActivity.additional) {
			home_top_additional.setVisibility(View.VISIBLE);
			home_top_additional.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TaskOperator.additionalResource(taskInfoActivity.viewModel.currentTask);
				}
			});
		}

		// 取消多选
		mView.findViewById(R.id.header_bar_leave_selection).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				leaveEdit();
			}
		});
		// 全选
		header_bar_leave_select_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (MediaDataInfo element : taskInfoActivity.meidaInfos) {
					if (element.Path != null && element.Path.length() > 0) {
						element.check = isChecked;
					}
				}
				taskInfoActivity.meidaListAdapter.notifyDataSetChanged();
			}
		});
		// 删除选择的资源
		media_bottom_bar_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msgStr="是否删除图片";
				if(mediaType==CategoryType.AudioCollection){
					msgStr="是否删除音频";
				}else if(mediaType==CategoryType.VideoCollection){
					msgStr="是否删除视频";
				}
				
				DialogUtil.showConfirmationDialog(taskInfoActivity, msgStr, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteSelectMedia();
						dialog.cancel();
					}
				});
			}
			/***
			 * @author kevin
			 * @date 2015-11-6 下午2:52:44
			 * @Description: 删除选中的媒体项目     
			 * @return void    返回类型 
			 */
			private void deleteSelectMedia(){
				ArrayList<MediaDataInfo> selectItems = new ArrayList<MediaDataInfo>();
				for (MediaDataInfo item : taskInfoActivity.meidaInfos) {
					if (item.Path != null && item.Path.length() > 0 && item.check) {
						selectItems.add(item);
					}
				}
				if (selectItems.size() > 0) {
					for (MediaDataInfo mediaDataInfo : selectItems) {
						taskInfoActivity.removeTaskItemValue(mediaDataInfo);
					}
					leaveEdit();
					taskInfoActivity.meidaListAdapter.notifyDataSetChanged();
					refreshItemTypeView();
				} else {
					taskInfoActivity.appHeader.showDialog("提示信息", "请至少勾选一项");
				}
			}
		});
		media_bottom_select_tips.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<MediaDataInfo> selectItems = new ArrayList<MediaDataInfo>();
				for (MediaDataInfo item : taskInfoActivity.meidaInfos) {
					if (item.Path != null && item.Path.length() > 0 && item.check) {
						selectItems.add(item);
					}
				}
				if (selectItems.size() > 0) {
					showSelectDialog();
				} else {
					taskInfoActivity.appHeader.showDialog("提示信息", "请至少勾选一项");
				}
			}
		});
	}

	/** 
	 * @author kevin
	 * @date 2015-10-8 上午9:04:46
	 * @Description: 初始化类型选择控件     
	 * @version V1.0
	 */
	/*private void initPicAutoComplecTv() {
		SearchAdapter<String> typeAcTvAdt= new SearchAdapter<String>(taskInfoActivity,// 
				 R.layout.auto_text_item_style,// 
				 taskInfoActivity.viewModel.currentDropDownListData,//
				 SearchAdapter.ALL);
		media_type_autoComplecTv.setAdapter(typeAcTvAdt);
		media_type_autoComplecTv.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					media_type_autoComplecTv.showDropDown();
				}
			}
		});
		media_type_autoComplecTv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				media_type_autoComplecTv.showDropDown();
			}
			
		});
	}*/


	/**
	 * 刷新需要呈现的媒体文件
	 */
	private void refreshMediaList() {
		if (taskInfoActivity.viewModel.currentTask.InworkReportFinish) {
			taskInfoActivity.appHeader.showDialog("提示信息", "当前任务报告已经完成无法继续操作资源文件");
		} else {
			taskInfoActivity.loadingWorker.showLoading("文件列表加载中...");
			Message TaskMsg = new Message();
			TaskMsg.what = taskInfoActivity.TASK_ITEMS;
			mBackgroundHandler.sendMessage(TaskMsg);
		}
	}

	/**
	 * 需要添加新的媒体文件
	 */
	private OnItemClickListener mediaGridOnItemClickListener = new OnItemClickListener() {

		/**
		 * 监控事件
		 */
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int index, long length) {
			onItemIndex = index;
			if (!TaskOperator.submiting(taskInfoActivity.viewModel.currentTask.TaskNum)) {
				// 如果有空间就打开对应的资源列表
				if (taskInfoActivity.appHeader.checkSDCardHasSize()) {
					//选择或者拍摄新的图片
					if (index == 0) {
						if (CategoryType.PictureCollection == mediaType) {
							// 类型输入框中的值是否正确， 正确就保存到ViewModel
							if(!chackSelectType()){
								//选择的不合法，清空缓存
								taskInfoActivity.viewModel.selectPicType="";
								ToastUtil.longShow(getActivity(), "当前类型不存在！");
								return;
							}
							String photoType = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_PHOTOTYPE);
							if (photoType.equals(SystemSettingActivity.photoTypeChooseItem[0])) {
								getFileOfNewMeida2();
							} else {
								getFileOfNewMeida();
							}
						} else if (CategoryType.AudioCollection == mediaType) {
							taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_AUDIO, mediaInfo, true);
						} else if (CategoryType.VideoCollection == mediaType) {
							taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_VEDIO, mediaInfo, true);
						}
					} else {
						// 打开图片
						if (CategoryType.PictureCollection == mediaType) {
							CameraUtils.openMedia(taskInfoActivity.meidaInfos.get(index).file);
						} else if (CategoryType.AudioCollection == mediaType) {
							taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_AUDIO, taskInfoActivity.meidaInfos.get(index), false);
						} else if (CategoryType.VideoCollection == mediaType) {
							taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_VEDIO, taskInfoActivity.meidaInfos.get(index), false);
						}
					}
				}
			} else {
				ToastUtil.longShow(getActivity(), "当前任务正在提交中，将不能继续操作文件!");
			}
		}

		/**
		 * 自定义照相机
		 */
		private void getFileOfNewMeida2() {
			String thumbnail = getString(R.string.thumbnail_dir);
			String saveRoot = getString(R.string.project_dir);
			Intent intent = new Intent(taskInfoActivity, CameraActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("saveRoot", taskInfoActivity.task_p_dir);
			bundle.putString("thumbnailRoot", taskInfoActivity.task_p_dir.replaceAll(saveRoot, thumbnail));
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
		}
	};

	/**
	 * 长按触发
	 */
	private OnItemLongClickListener mediaGridOnItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
			if (!TaskOperator.submiting(taskInfoActivity.viewModel.currentTask.TaskNum)) {
				// 如果有空间就打开对应的资源列表
				//图片集合才可以长按
				boolean isPic=mediaType==CategoryType.PictureCollection;
				if (taskInfoActivity.appHeader.checkSDCardHasSize()) {
					// 选择或者拍摄新的图片
					if (index == 0&&isPic) {
						// 判断类型输入框中的值是否合法，合法记录在ViewModle
						if(!chackSelectType()){
							//选择的不合法，清空缓存
							taskInfoActivity.viewModel.selectPicType="";
							ToastUtil.longShow(getActivity(), "当前类型不存在！");
							return true;
						}
						// 跳转到 选择图片界面
						Intent i = new Intent();
						i.setClass(taskInfoActivity, MultiSelectAlbumActivity.class);
						startActivityForResult(i, TASK_ALBUM);
						//getFileOfMediaLib();
					} else if(index != 0){
						if (!taskInfoActivity.meidaListAdapter.getVisCheck()) {
							enterEdit();
						} else {
							// onItemIndex = index;
							// DialogBuilder dialogBuilder = new
							// DialogBuilder(taskInfoActivity);
							// dialogBuilder.setTitle("请选择操作");
							// dialogBuilder.setItems(R.array.photo_item_select,
							// dialogListener());
							// dialogBuilder.create().show();
						}
					}
				}
			} else {
				ToastUtil.longShow(getActivity(), "当前任务正在提交中，将不能继续操作文件!");
			}
			return true;
		}
	};

	/**
	 * 点击具体的功能
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private android.content.DialogInterface.OnClickListener dialogListener() {
		return new DialogInterface.OnClickListener() {
			/**
			 * 开始执行功能
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:// 查看图片
					CameraUtils.openMedia(taskInfoActivity.meidaInfos.get(onItemIndex).file);
					break;
				case 1:// 删除图片
					taskInfoActivity.removeTaskItemValue(taskInfoActivity.meidaInfos.get(onItemIndex));
				}
			}
		};
	}

	/**
	 * 建立新的媒体文件
	 */
	private void getFileOfNewMeida() {
		if (CategoryType.PictureCollection == mediaType) {
			mediaFile = CameraUtils.getUUIDFile(taskInfoActivity.task_p_dir, MediaDataInfo.suffixJpg);
			if (mediaFile != null) {
				startActivityForResult(CameraUtils.startGetPicFromPhoto(mediaFile), taskInfoActivity.TASK_PHOTO);
			}// 只有在文件类型为图片的情况下才会有选择菜单 其他的会直接进去界面
		} else if (CategoryType.AudioCollection == mediaType) {
			taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_AUDIO, mediaInfo, true);
		} else if (CategoryType.VideoCollection == mediaType) {
			taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_VEDIO, mediaInfo, true);
		}
	}

/*	*//**
	 * 从媒体文件取
	 *//*
	@SuppressWarnings("unused")
	private void getFileOfMediaLib() {
		Intent intent = CameraUtils.startGetPicPhotoAlbum();
		startActivityForResult(intent, taskInfoActivity.TASK_PHOTOLIB);
	}*/

	/**
	 * 返回方法
	 */
	@SuppressWarnings({ "static-access" })
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == taskInfoActivity.RESULT_OK) {
			Boolean isContinue = false;
			if (mediaFile != null) {
				mediaInfo = new MediaDataInfo("请选择类型", mediaFile);
			}
			switch (requestCode) {
			case TASK_PHOTO:// 系统相机拍照返回
				if (mediaInfo != null) {
					// 判断当前分类项中是否有未分类的选项
					taskInfoActivity.setDropDefaultSelect(mediaInfo,mediaFile);
					isContinue = true;
				}
				break;
			case TASK_AUDIO:
				taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_AUDIO, mediaInfo, true);
				break;
			case TASK_VEDIO:
				taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_VEDIO, mediaInfo, true);
				break;
			case TASK_PHOTOLIB:// 系统图库返回
				// 为false时为复制
				boolean isPaste = false;
				String copyOrPaste = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_PICTURECOPYORPASTE);
				if (copyOrPaste != null && copyOrPaste.equals("剪切")) {
					isPaste = true;
				}
				String fileFuleName = taskInfoActivity.task_p_dir + File.separator + UUID.randomUUID() + ".jpg";

				FileUtil.movePhotoLibFileToCustomDir(taskInfoActivity, intent, fileFuleName, isPaste, true);
				File file = new File(fileFuleName);
				if (!file.exists()) {
					taskInfoActivity.appHeader.showDialog("提示信息", "非常抱歉，资源文件获取失败，请重试");
				} else {
					mediaInfo = new MediaDataInfo("请选择种类", file);
					if (mediaInfo != null) {
						mediaInfo.itemFileName = file.getName() + ";";
						mediaInfo.ItemValue = mediaInfo.itemFileName + ";";

						if (taskInfoActivity.hasDefaultSelect()) {
							mediaInfo.ItemName = "未分类";
							mediaInfo.CategoryId = taskInfoActivity.viewModel.currentCategory.CategoryID;
						} else {
							mediaInfo.ItemName = mediaInfo.itemFileName;
							mediaInfo.CategoryId = 0;
						}
						taskInfoActivity.meidaInfos.add(mediaInfo);
						taskInfoActivity.additional(file.getName(), false);
						taskInfoActivity.doSaveMediaInfo("", "", file.getName() + ";", file.getName() + ";", CategoryType.PictureCollection, true, false);
					}
					// taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_PHOTO,
					// mediaInfo, true);
				}
				break;

			case TASK_ALBUM:// 从自定义图库返回
				ArrayList<String> result = intent.getStringArrayListExtra(MultiSelectAlbumActivity.RESULT_IMG_PATH);
				// 为false时为复制
				boolean isPastes = false;
				String copyOrPastes = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_PICTURECOPYORPASTE);
				if (copyOrPastes != null && copyOrPastes.equals("剪切")) {
					isPaste = true;
				}
				// 循环没个图片路径， 移动到指定文件夹， 并添加到主界面
				for (String path : result) {
					String fileFuleNames = taskInfoActivity.task_p_dir + File.separator + UUID.randomUUID() + ".jpg";
					// 移动文件到指定文件夹
					if (new File(path).exists()) {
						File targetFile = new File(fileFuleNames);
						if (!targetFile.exists()) {
							FileUtil.copy(path, fileFuleNames);
						} else {
							if (true) {
								if (FileUtil.delFile(fileFuleNames)) {
									FileUtil.copy(path, fileFuleNames);
								}
							}
						}
						if (isPastes) {
							FileUtil.delFile(path);
						}
					} else {
						ToastUtil.longShow(BaseApplication.getInstance(), "源文件已经被转移");
					}
					File files = new File(fileFuleNames);
					if (!files.exists()) {
						taskInfoActivity.appHeader.showDialog("提示信息", "非常抱歉，资源文件获取失败，请重试");
					} else {
						mediaInfo = new MediaDataInfo("请选择种类", files);
						if (mediaInfo != null) {
							taskInfoActivity.setDropDefaultSelect(mediaInfo,files);
						}
					}
				}
				break;

			case TASK_AUDIOLIB:
				taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_AUDIO, mediaInfo, true);
				break;
			case TASK_VEDIOLIB:
				taskInfoActivity.changMediaFragment(taskInfoActivity.TASK_VEDIO, mediaInfo, true);
				break;
			default:
				break;
			}
			mediaFile = null;
			if (isContinue) {
				getFileOfNewMeida();
			}
			refreshItemTypeView();
		}
	}

	/**
	 * 保存Bitmap到本地
	 * 
	 * @param fileFullName
	 * @param bm
	 */
	public void saveBitmap(String fileFullName, Bitmap bm) {
		File f = new File(fileFullName);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提交任务
	 */
	private OnClickListener backOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			taskInfoActivity.loadingWorker.showLoading("数据操作中...");
			Message TaskMsg = new Message();
			TaskMsg.what = TASK_SAVETASKITEMS;
			mBackgroundHandler.sendMessage(TaskMsg);
		}
	};

	@Override
	protected void handlerBackgroundHandler(Message msg) {
		// 准备发送给UI线程的消息对象
		Message uiMsg = new Message();
		uiMsg.what = msg.what;
		switch (msg.what) {
		case TASK_ITEMS:
			getMedias();
			initTypeItmeData();
			break;
		case TASK_SAVE_MOVEPREVIOUSCATEGORY:
			// 执行保存操作
			if (categoryIndex > 0) {
				categoryIndex--;
			} else {
				categoryIndex = taskInfoActivity.viewModel.currentTaskCategoryInfos.size() - 1;
			}
			setDataCategoryDefine(categoryIndex);
			// 修改内存中的子项信息
			taskInfoActivity.getCurrentDataCategoryDefine(categoryIndex);
			break;
		case TASK_SAVE_MOVENEXTCATEGORY:
			// 执行保存操作
			if (categoryIndex < taskInfoActivity.viewModel.currentTaskCategoryInfos.size() - 1) {
				categoryIndex++;
			} else {
				categoryIndex = 0;
			}
			// 赋值勘察表分类项信息
			setDataCategoryDefine(categoryIndex);
			// 修改内存中的子项信息
			taskInfoActivity.getCurrentDataCategoryDefine(categoryIndex);
			break;
		case TASK_SAVETASKITEMS:

			break;
		default:
			break;
		}
		// 发信息给UI线程
		mUiHandler.sendMessage(uiMsg);
	}

	/**
	 * 获取媒体文件列表
	 */
	public void getMedias() {
		taskInfoActivity.meidaInfos = new ArrayList<MediaDataInfo>();
		MediaDataInfo addMedia = new MediaDataInfo("", new File(""));
		addMedia.CategoryId = -1;
		taskInfoActivity.meidaInfos.add(addMedia);
		ResultInfo<TaskInfo> taskInfo = TaskDataWorker.getCompleteTaskInfoById(taskInfoActivity.viewModel.currentTask.IsNew ? taskInfoActivity.viewModel.currentTask.ID
				: taskInfoActivity.viewModel.currentTask.TaskID, taskInfoActivity.viewModel.currentTask.IsNew);

		// 获取当前媒体所属类型的文件夹目录
		String filePath = taskInfoActivity.getFilePath(mediaType);

		// 获取勘察数据信息
		ResultInfo<DataDefine> dataDefine = DataDefineWorker.getCompleteDataDefine(taskInfoActivity.viewModel.currentTask.DDID);
		if (taskInfoActivity.additional) {
			getCurrentAdditional(taskInfo, filePath, dataDefine);
		} else {
			getCurrentMedias(taskInfo, filePath, dataDefine);
		}
		taskInfoActivity.viewModel.searchAdapter = new SearchAdapter<String>(taskInfoActivity, R.layout.auto_text_item_style, taskInfoActivity.viewModel.currentDropDownListData, SearchAdapter.ALL);// 速度优先
	}

	/**
	 * 获取当前全部资源文件
	 * 
	 * @param taskInfo
	 * @param filePath
	 * @param dataDefine
	 */
	private void getCurrentMedias(ResultInfo<TaskInfo> taskInfo, String filePath, ResultInfo<DataDefine> dataDefine) {
		Boolean isDone = false;
		for (DataCategoryDefine category : dataDefine.Data.Categories) {
			if (category.ControlType == mediaType) {
				for (TaskCategoryInfo taskCategory : taskInfo.Data.Categories) {
					if (haslike(category, taskCategory)) {
						Collections.reverse(taskCategory.Items);
						for (TaskDataItem dataItem : taskCategory.Items) {
							// ShowInPhone 为ture时,才在分类项中添加一个数量
							Boolean isAdd = false;
							if (dataItem.CategoryID <= 0) {
								isAdd = true;
							} else {
								for (DataFieldDefine defineItem : category.Fields) {
									if (dataItem.Name.equals(defineItem.Name)) {
										if (defineItem.ShowInPhone) {
											isAdd = true;
											break;
										}
									}
								}
							}
							if (isAdd) {
								if (dataItem.Value != null && !dataItem.Value.equals("null") && dataItem.Value.length() > 0) {
									String[] files = dataItem.Value.split(";");
									for (String file : files) {
										if (file.length() > 0) {
											MediaDataInfo mData = new MediaDataInfo(dataItem.Name, new File(filePath + File.separator + file));
											mData.itemFileName = file + ";";
											mData.ItemName = dataItem.Name;
											mData.CategoryId = dataItem.CategoryID;
											mData.ItemValue = dataItem.Value;
											taskInfoActivity.meidaInfos.add(mData);
										}
									}
								}
							}
						}
						isDone = true;
					}
				}
				if (isDone) {
					break;
				}
			}
		}
	}

	/**
	 * 获取附加的资源文件
	 * 
	 * @param taskInfo
	 * @param filePath
	 * @param dataDefine
	 */
	private void getCurrentAdditional(ResultInfo<TaskInfo> taskInfo, String filePath, ResultInfo<DataDefine> dataDefine) {
		AdditionalResource ar = TaskOperator.getAdditional(taskInfoActivity.viewModel.currentTask.TaskNum);
		if (ar == null) {
			ar = new AdditionalResource();
		}
		Boolean isDone = false;
		for (DataCategoryDefine category : dataDefine.Data.Categories) {
			if (category.ControlType == mediaType) {
				for (TaskCategoryInfo taskCategory : taskInfo.Data.Categories) {
					if (haslike(category, taskCategory)) {
						Collections.reverse(taskCategory.Items);
						for (TaskDataItem dataItem : taskCategory.Items) {
							if (dataItem.Value != null && !dataItem.Value.equals("null") && dataItem.Value.length() > 0) {
								String[] files = dataItem.Value.split(";");
								for (String file : files) {
									if (file.length() > 0 && ar.Resources.contains(file)) {
										MediaDataInfo mData = new MediaDataInfo(dataItem.Name, new File(filePath + File.separator + file));
										mData.itemFileName = file + ";";
										mData.ItemName = dataItem.Name;
										mData.CategoryId = dataItem.CategoryID;
										mData.ItemValue = dataItem.Value;
										taskInfoActivity.meidaInfos.add(mData);
									}
								}
							}
						}
						isDone = true;
					}
				}
				if (isDone) {
					break;
				}
			}
		}
	}

	/**
	 * 如果是对于的分类项
	 * 
	 * @param category
	 * @param taskCategory
	 * @return
	 */
	private boolean haslike(DataCategoryDefine category, TaskCategoryInfo taskCategory) {
		return taskCategory.CategoryID == category.CategoryID
				&& category.CategoryID == taskInfoActivity.viewModel.currentCategory.CategoryID
				&& (taskCategory.BaseCategoryID > 0 ? taskCategory.BaseCategoryID : taskCategory.ID) == (taskInfoActivity.viewModel.currentCategory.BaseCategoryID > 0 ? taskInfoActivity.viewModel.currentCategory.BaseCategoryID
						: taskInfoActivity.viewModel.currentCategory.ID);
	}

	@Override
	protected void handUiMessage(Message msg) {
		super.handUiMessage(msg);
		switch (msg.what) {
		case TASK_ITEMS:
			taskInfoActivity.meidaListAdapter = new MeidaListAdapter(this,taskInfoActivity, mediaType);
			mediaGrid.setAdapter(taskInfoActivity.meidaListAdapter);
			mediaGrid.setOnScrollListener(this);
			taskInfoActivity.sortTaskItemValue();
			// 绘制子项视图
			type_item_gridView.setAdapter(typeItemGvAdapter);
			//适配器处理子项视图点击事件
			type_item_gridView.setOnItemClickListener(typeItemGvAdapter);
			break;
		case TASK_SAVE_MOVEPREVIOUSCATEGORY:
			if (dataCategoryDefine != null) {
				taskInfoActivity.changFragment(dataCategoryDefine.ControlType);
			}
			break;
		case TASK_SAVE_MOVENEXTCATEGORY:
			if (dataCategoryDefine != null) {
				taskInfoActivity.changFragment(dataCategoryDefine.ControlType);
			}
			break;
		case TASK_SAVETASKITEMS:
			Integer ddid = taskInfoActivity.viewModel.currentTask.DDID;
			String taskNum = taskInfoActivity.viewModel.currentTask.TaskNum;
			String fee = taskInfoActivity.viewModel.currentTask.Fee;
			taskInfoActivity.menuOperator.putTaskInfo(ddid, taskNum, fee);
			break;
		default:
			break;
		}
		if (taskInfoActivity != null) {
			taskInfoActivity.loadingWorker.closeLoading();
		}
	}

	@Override
	public void onScroll(AbsListView listView, int scrollState, int arg2, int arg3) {
		if (listView == this.mediaGrid) {
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView listView, int scrollState) {
		if (listView == this.mediaGrid) {
		}

	}

	/**
	 * 保存并跳转到上一个分类项子项
	 */
	private void saveAndMovePreviousCategory() {
		taskInfoActivity.loadingWorker.showLoading("跳转中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_SAVE_MOVEPREVIOUSCATEGORY;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 保存并跳转到下一个分类项子项
	 */
	private void saveAndMoveNextCategory() {
		taskInfoActivity.loadingWorker.showLoading("跳转中...");
		Message TaskMsg = new Message();
		TaskMsg.what = TASK_SAVE_MOVENEXTCATEGORY;
		mBackgroundHandler.sendMessage(TaskMsg);
	}

	/**
	 * 找出当前任务分类对应的勘察表分类项
	 * 
	 * @param Index
	 *            任务分类项的索引
	 */
	private void setDataCategoryDefine(Integer Index) {
		// 得到下一个任务分类项
		TaskCategoryInfo taskCategoryInfo = taskInfoActivity.viewModel.currentTaskCategoryInfos.get(Index);
		// 根据任务分类项取得勘察表分类项
		ResultInfo<DataCategoryDefine> categoryDefine = DataDefineWorker.getDataCategoryDefineByCategoryId(taskCategoryInfo.CategoryID);
		if (categoryDefine != null && categoryDefine.Data != null) {
			dataCategoryDefine = categoryDefine.Data;
		}
	}

	private void enterEdit() {
		((CheckBox) mView.findViewById(R.id.header_bar_leave_select_all)).setChecked(false);
		taskInfoActivity.meidaListAdapter.visCheck(true);
		mediaGrid.setPadding(4, 4, 4, 108);
		mView.findViewById(R.id.task_list_title).setVisibility(View.GONE);
		mView.findViewById(R.id.media_header_bar).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.media_bottom_bar).setVisibility(View.VISIBLE);
	}

	private void leaveEdit() {
		for (MediaDataInfo element : taskInfoActivity.meidaInfos) {
			element.check = false;
		}
		taskInfoActivity.meidaListAdapter.visCheck(false);
		mediaGrid.setPadding(4, 4, 4, 4);
		mView.findViewById(R.id.task_list_title).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.media_header_bar).setVisibility(View.GONE);
		mView.findViewById(R.id.media_bottom_bar).setVisibility(View.GONE);
	}

	/**
 * 
 */
	@SuppressLint("ClickableViewAccessibility")
	private void showSelectDialog() {
		LayoutInflater inflater = LayoutInflater.from(taskInfoActivity);
		View dialog_view_task_submit = inflater.inflate(R.layout.dialog_media_singlechoice, null);
		final Dialog detailDialog = DialogUtil.getDetailDialog(taskInfoActivity, dialog_view_task_submit);

		final AutoCompleteTextView single_choice_auto = (AutoCompleteTextView) dialog_view_task_submit.findViewById(R.id.single_choice_auto);
		Button single_choice_confirm = (Button) dialog_view_task_submit.findViewById(R.id.single_choice_confirm);
		Button single_choice_cancel = (Button) dialog_view_task_submit.findViewById(R.id.single_choice_cancel);

		single_choice_auto.setAdapter(taskInfoActivity.viewModel.searchAdapter);
		single_choice_auto.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				AutoCompleteTextView txt = (AutoCompleteTextView) v;
				txt.setVisibility(View.VISIBLE);
				txt.showDropDown();
				return false;
			}
		});

		single_choice_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String selectTypeString = single_choice_auto.getText().toString();
				if (selectTypeString.length() > 0 && taskInfoActivity.viewModel.currentDropDownListData.contains(selectTypeString)) {
					ArrayList<MediaDataInfo> selectItems = new ArrayList<MediaDataInfo>();
					for (MediaDataInfo item : taskInfoActivity.meidaInfos) {
						if (item.Path != null && item.Path.length() > 0 && item.check) {
							selectItems.add(item);
						}
					}
					if (selectItems.size() > 0) {
						for (MediaDataInfo mediaDataInfo : selectItems) {
							taskInfoActivity.saveTaskItemValue(CategoryType.PictureCollection, mediaDataInfo, selectTypeString,false);
						}
						leaveEdit();
						taskInfoActivity.meidaListAdapter.notifyDataSetChanged();
						refreshItemTypeView();
					}
					detailDialog.dismiss();
				} else {
					taskInfoActivity.appHeader.showDialog("提示信息", "没有选择类型或者选择类型不对");
				}
			}
		});
		single_choice_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				detailDialog.dismiss();
			}
		});

		if (detailDialog.isShowing()) {
			detailDialog.dismiss();
		} else {
			detailDialog.show();
		}
	}
	
	/***
	 * 
	 * @author kevin
	 * @date 2015-9-28 上午9:02:16
	 * @Description: 检查下拉框的类型输入是否合法    
	 * @return Boolean 选择的类型是否合法
	 */
	private boolean chackSelectType(){
		boolean result=false;
		//String selectType=media_type_autoComplecTv.getText().toString();
		ItemType itemType=typeItemGvAdapter.getItem(typeItemGvAdapter.getCurrentSelectIndex());
		String selectType=itemType!=null?itemType.getItemName():"";
		
		if(selectType.trim().length()<=0){
			taskInfoActivity.viewModel.selectPicType="";
			return true;
		}
		for(String type:taskInfoActivity.viewModel.currentDropDownListData){
			if(type.equals(selectType)){
				//保存当前选择的类型到缓存
				taskInfoActivity.viewModel.selectPicType=type;
				result=true;
				break;
			}
		}
		return result;
	}
	
	/***
	 * @author kevin
	 * @date 2015-11-13 下午2:22:47
	 * @Description: 统计当前已经有的图片数量，并初始化子项视图适配器     
	 * @return void    返回类型 
	 * @version V1.0
	 */
	private void initTypeItmeData(){
		if(typeItemGvAdapter==null){
			typeItemGvAdapter=new TypeItemGridViewAdapter(taskInfoActivity);
		}
		List<ItemType> itemTypes=countItemType();
		typeItemGvAdapter.setItemTypeList(itemTypes);
	}
	
	/***
	 * 
	 * @author kevin
	 * @date 2015-11-13 下午4:33:17
	 * @Description: 统计子项数量 
	 * @return List<ItemType>    返回类型 
	 * @version V1.0
	 */
	private List<ItemType> countItemType(){
		List<ItemType> temps=new ArrayList<ItemType>();
		for(String type:taskInfoActivity.viewModel.currentDropDownListData){
			ItemType itemType=typeItemGvAdapter.new ItemType();
			itemType.setItemName(type);
			for(MediaDataInfo media:taskInfoActivity.meidaInfos){
				if(media.ItemName.equals(type)){
					itemType.itemCountAdd();// 统计加一
				}
			}
			temps.add(itemType);
		}
		return temps;
	}
	
	/***
	 * 
	 * @author kevin
	 * @date 2015-11-13 下午4:40:17
	 * @Description: 更新图片类型子项表格视图  
	 * @version V1.0
	 */
	public void refreshItemTypeView(){
		if(mediaType==CategoryType.PictureCollection){
			initTypeItmeData();
		}
	}

}
