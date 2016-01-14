package com.yunfang.eias.beta.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yunfang.eias.beta.R;
import com.yunfang.eias.beta.base.BroadRecordType;
import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.logic.DataLogOperator;
import com.yunfang.eias.beta.logic.HomeOperator;
import com.yunfang.eias.beta.logic.UserInfoOperator;
import com.yunfang.eias.beta.model.DataDefine;
import com.yunfang.eias.beta.model.UserTaskInfo;
import com.yunfang.eias.beta.ui.Adapter.DataDefineListAdapter;
import com.yunfang.eias.beta.viewmodel.HomeViewModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yunfang.framework.base.BaseApplication;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.base.BaseBroadcastReceiver.afterReceiveBroadcast;
import com.yunfang.framework.base.BaseWorkerFragment;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.BitmapHelperUtil;
import com.yunfang.framework.utils.CameraUtils;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.view.RoundImage;

/**
 * 主界面信息
 * @author 贺隽
 *
 */
@SuppressLint("InflateParams")
public class HomeFragment extends BaseWorkerFragment {
	
	// {{ 变量
	/**
	 * Activity对象
	 */
	private HomeActivity mHomeActivity;

	/**
	 * 当前Fragment视图
	 */
	private View mView;

	/**
	 * 滚动条
	 */
	@SuppressWarnings("unused")
	private ScrollView mScrollView;

	/**
	 * 刷新按钮
	 */
	private Button btn_refresh;

	/**
	 * 新版本任务数量显示控件 待领取任务信息
	 */
	private TextView un_task,un_task_number_all, un_urgent_task_number,
			un_nomral_task_number;
	
	/**
	 * 已经领取任务信息
	 */
	private TextView en_task,en_task_number_all, en_urgent_task_number,
			en_nomral_task_number;
	
	/**
	 * 手机容量
	 */
	private TextView home_scard_title,home_scard_rest_size,home_scard_total_size;

	/**
	 * 新版本任务数量显示控件(括号内数字)
	 */
	private TextView tp_un_task_num_all, tp_en_task_num_all;
	 
	/**
	 * 头像
	 */
	private RoundImage userlogo;

	/**
	 * 当前视图类
	 */
	public HomeViewModel currentViewModel = new HomeViewModel();
	
	/**
	 * 勘察表更新列表
	 */
	private ListView dataDefine_listview;
	
	/**
	 * 展示信息
	 */
	private TextView txt_showMsg;

	/**
	 * ListView的Item项装配器
	 * */
	private DataDefineListAdapter dataDefinesAdapter = null;

	/**
	 * 全部同步按钮
	 */
	private Button btn_consistentDatadefine;

	/**
	 * 上次点击勘察项
	 */
	private View oldView;
	
	/**
	 * 切换为离线之后
	 */
	private BaseBroadcastReceiver changedOfflineReceiver;
	
	// }}

	// {{ 任务类型

	/**
	 * 获取用户任务数据和需要匹配的勘察表数据
	 */
	private final int TASK_GET_HOMEINFO = 0;

	/**
	 * 同步指定勘察表数据
	 */
	private final int TASK_UPDATE_DATADEFINE = 1;

	/**
	 * 需要同步的升级配置信息表
	 */
	private final int TASK_UPDATE_ALLDATADEFINE = 2;
	
	/** 上传本地数据库崩溃日志 */
	private final int TASK_UPLOAD_DB_DATALOG=3;

	// }}

	// {{ 进程调用重载类
	
	@Override
	protected void handlerBackgroundHandler(Message msg) {
		currentViewModel.ToastMsg = "";
		Message message = new Message();
		message.what = msg.what;
		switch (msg.what) {
		case TASK_UPDATE_DATADEFINE:
			message.obj = HomeOperator.fillOneDataDefine(
					currentViewModel.currentUser,
					currentViewModel.currentUpdateDataDefine);
			break;
		case TASK_UPDATE_ALLDATADEFINE:
			message.obj = HomeOperator.fillAllDataDefines(
					currentViewModel.currentUser,
					currentViewModel.currentUpdateDataDefines);
			break;
		case TASK_GET_HOMEINFO:
			message.obj = HomeOperator.getHomeData(currentViewModel.currentUser);
			break;
		case TASK_UPLOAD_DB_DATALOG:
			//检查本地未捕获异常，提交服务器。
			DataLogOperator.upLoadUnCrashExection4Db(currentViewModel.currentUser);
			break;
		default:
			break;
		}
		mUiHandler.sendMessage(message);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handUiMessage(Message msg) {
		super.handUiMessage(msg);
		switch (msg.what) {
		case TASK_UPDATE_DATADEFINE:
			ResultInfo<Boolean> updatedResult = (ResultInfo<Boolean>) msg.obj;
			if (updatedResult.Success && updatedResult.Data) {
				currentViewModel.currentUpdateDataDefines.remove(currentViewModel.currentUpdateDataDefine);
				currentViewModel.currentUpdateDataDefine = null;
				currentViewModel.ToastMsg = "数据同步成功";
			} else {
				currentViewModel.GetDataSuccess = false;
				currentViewModel.ToastMsg = updatedResult.Message;
			}
			break;
		case TASK_UPDATE_ALLDATADEFINE:
			ResultInfo<ArrayList<DataDefine>> updatedAllResult = (ResultInfo<ArrayList<DataDefine>>) msg.obj;
			if (updatedAllResult.Success
					&& updatedAllResult.Data != null
					&& updatedAllResult.Data.size() == currentViewModel.currentUpdateDataDefines
							.size()) {
				currentViewModel.currentUpdateDataDefines = new ArrayList<DataDefine>();
				currentViewModel.currentUpdateDataDefine = null;
				currentViewModel.ToastMsg = "勘察表全部同步成功";
			} else {
				if (updatedAllResult.Data != null
						&& updatedAllResult.Data.size() > 0) {
					for (DataDefine define : updatedAllResult.Data) {
						currentViewModel.currentUpdateDataDefines
								.remove(define);
						currentViewModel.ToastMsg = "部分勘察表同步失败";
					}
					currentViewModel.GetDataSuccess = true;
				} else {
					currentViewModel.GetDataSuccess = false;
					currentViewModel.ToastMsg = updatedAllResult.Message;
				}
			}
			dataDefinesAdapter.refersh(currentViewModel.currentUpdateDataDefines);
			dataDefinesAdapter.notifyDataSetChanged();
			break;
		case TASK_GET_HOMEINFO:
			ResultInfo<UserTaskInfo> homeData = (ResultInfo<UserTaskInfo>) msg.obj;
			currentViewModel.GetDataSuccess = homeData.Success;
			if (homeData.Success) {
				currentViewModel.currentUserTaskInfo = homeData.Data;
				currentViewModel.currentUpdateDataDefines = (ArrayList<DataDefine>) homeData.Others;
			} else {
				currentViewModel.GetDataSuccess = false;
				currentViewModel.ToastMsg = homeData.Message;
			}
			break;
		case TASK_UPLOAD_DB_DATALOG:
			return;
		default:
			break;
		}
		EIASApplication.currentUpdateDataDefines = currentViewModel.currentUpdateDataDefines;
		showData();
		if (mHomeActivity != null) {
			mHomeActivity.loadingWorker.closeLoading();
		}
	}

	

	// }}

	// {{ 数据加载显示

	/**
	 * 加载控件信息
	 */
	private void showData() {
		if (currentViewModel.GetDataSuccess) {
			showUserTaskInfo();
			showDataDefineList();
			if (currentViewModel.ToastMsg.length() > 0) {
				showToast(currentViewModel.ToastMsg);
			}
		} else {
			if (currentViewModel.ToastMsg.length() > 0) {
				showToast(currentViewModel.ToastMsg);
			} else {
				showToast(R.string.hint_get_data_fail);
			}
		}
	}

	/**
	 * 加载数据信息
	 */
	private void showUserTaskInfo() {
		if (currentViewModel.currentUserTaskInfo != null) {
			if(EIASApplication.IsOffline){
				tp_un_task_num_all.setText("待领取项目");
				tp_en_task_num_all.setText("已领取项目");
			}else{
				tp_un_task_num_all.setText("待领取项目("
						+ currentViewModel.currentUserTaskInfo.NonReceivedTotals
						+ "" + ")");
				tp_en_task_num_all.setText("已领取项目("
						+ currentViewModel.currentUserTaskInfo.ReceivedTotals
						+ "" + ")");
			}
			un_task_number_all.setText(currentViewModel.currentUserTaskInfo.NonReceivedTotals+"");
			un_urgent_task_number.setText(currentViewModel.currentUserTaskInfo.NonReceivedUrgent+"");
			un_nomral_task_number.setText(currentViewModel.currentUserTaskInfo.NonReceivedNormal+"");
			//已经领取总数
			en_task_number_all.setText(currentViewModel.currentUserTaskInfo.ReceivedTotals+"");
			//已领取紧急任务数量
			en_urgent_task_number.setText(currentViewModel.currentUserTaskInfo.ReceivedUrgent+"");
			//已领取常规任务数量
			en_nomral_task_number.setText(currentViewModel.currentUserTaskInfo.ReceivedNormal+"");
		} else {
			if(EIASApplication.IsOffline){
				tp_un_task_num_all.setText("待领取项目");
				tp_en_task_num_all.setText("已领取项目");
			}else{
				tp_un_task_num_all.setText("待领取项目("+ EIASApplication.DefaultHorizontalLineValue + ")");
				tp_en_task_num_all.setText("已领取项目("+ EIASApplication.DefaultHorizontalLineValue + ")");
			}		
			un_task_number_all.setText(EIASApplication.DefaultHorizontalLineValue);
			un_urgent_task_number.setText(EIASApplication.DefaultHorizontalLineValue);
			un_nomral_task_number.setText(EIASApplication.DefaultHorizontalLineValue);
			en_task_number_all.setText(EIASApplication.DefaultHorizontalLineValue);
			en_urgent_task_number.setText(EIASApplication.DefaultHorizontalLineValue);
			en_nomral_task_number.setText(EIASApplication.DefaultHorizontalLineValue);
		}
		//修改成离线不隐藏统计信息了
		//invisibleTaskInfoByOffline();
		
	}

	/**
	 * 显示勘察表更新信息
	 */
	private void showDataDefineList() {
		btn_consistentDatadefine.setVisibility(View.GONE);
		// 有勘察表需要更新时
		if (currentViewModel.currentUpdateDataDefines != null
				&& currentViewModel.currentUpdateDataDefines.size() > 0) {
			// 显示需要同步的勘察表信息
			binDataDefinesAdapter();
			// 当需要更新的数据条数大于1时显示更新按钮
			// 显示更新所有勘察表的按钮
			if (currentViewModel.currentUpdateDataDefines.size() > 1) {
				btn_consistentDatadefine.setVisibility(View.VISIBLE);
			}
			txt_showMsg.setVisibility(View.GONE);
		}
		// 没有勘察表需要更新时
		else {
			// 离线登录时
			if (EIASApplication.IsOffline) {
				currentViewModel.currentUpdateDataDefines = new ArrayList<DataDefine>();
				EIASApplication.currentUpdateDataDefines = currentViewModel.currentUpdateDataDefines;
				binDataDefinesAdapter();
				// 显示没有勘察匹配表可更新的信息
				txt_showMsg.setText("未与服务器对比勘察配置表信息");
				txt_showMsg.setVisibility(View.VISIBLE);
			}
			// 在线登录时
			else {
				// 显示没有勘察匹配表可更新的信息
				txt_showMsg.setText("勘察配置表已经是最新版本");
				txt_showMsg.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 绑定勘察表信息适配器
	 */
	private void binDataDefinesAdapter() {
		dataDefine_listview.setAdapter(dataDefinesAdapter);
		dataDefinesAdapter.refersh(currentViewModel.currentUpdateDataDefines);
		dataDefinesAdapter.notifyDataSetChanged();
	}

	// }}

	// {{ 基类创建重载方法
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.home_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		loadData();
		upLoadExctionInfo();
	}
	
	//重新进入主页面需要重新检查图片
	@Override
	public void onResume(){
		loadUserLogo();
		super.onResume();
	}
	// }}

	/**
	 * 初始化界面所有控件
	 */
	private void initView() {
		mHomeActivity = (HomeActivity) getActivity();
		mView = getView();
		// 初始化适配器
		dataDefinesAdapter = new DataDefineListAdapter(mHomeActivity);
		btn_refresh = (Button) mView.findViewById(R.id.btn_refresh);
		btn_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadData();
			}
		});
		btn_consistentDatadefine = (Button) mView
				.findViewById(R.id.btn_consistentDatadefine);
		btn_consistentDatadefine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mView
						.getContext());
				builder.setTitle("询问信息");
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mHomeActivity.loadingWorker
										.showLoading("数据同步中...");
								Message loginMsg = new Message();
								loginMsg.what = TASK_UPDATE_ALLDATADEFINE;
								mBackgroundHandler.sendMessage(loginMsg);
							}
						});
				builder.setNegativeButton("取消", null);
				builder.setMessage("确认同步以上  "
						+ currentViewModel.currentUpdateDataDefines.size()
						+ " 个勘察匹配表信息吗？");
				builder.show();
			}
		});
		userlogo = (RoundImage) mView.findViewById(R.id.userlogo);
		txt_showMsg = (TextView) mView.findViewById(R.id.txt_showMsg);
		un_task= (TextView) mView.findViewById(R.id.un_task);
		un_task_number_all = (TextView) mView.findViewById(R.id.un_task_number_all);
		un_urgent_task_number = (TextView) mView.findViewById(R.id.un_urgent_task_number);
		un_nomral_task_number = (TextView) mView.findViewById(R.id.un_nomral_task_number);
		en_task= (TextView) mView.findViewById(R.id.en_task);
		en_task_number_all = (TextView) mView.findViewById(R.id.en_task_number_all);
		en_urgent_task_number = (TextView) mView.findViewById(R.id.en_urgent_task_number);
		en_nomral_task_number = (TextView) mView.findViewById(R.id.en_nomral_task_number);

		tp_un_task_num_all = (TextView) mView.findViewById(R.id.tp_un_task_num_all);
		tp_en_task_num_all = (TextView) mView.findViewById(R.id.tp_en_task_num_all);		 
		
		home_scard_title = (TextView) mView.findViewById(R.id.home_scard_title);
		home_scard_rest_size = (TextView) mView
				.findViewById(R.id.home_scard_rest_size);
		home_scard_total_size = (TextView) mView
				.findViewById(R.id.home_scard_total_size);

		currentViewModel.currentUser = EIASApplication.getCurrentUser();

		dataDefine_listview = (ListView) mView.findViewById(R.id.DataDefine_listview);

		// 监听触摸事件
		dataDefine_listview.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mHomeActivity.touchPage(event.getAction(), event.getX(),
						event.getY(), false);
			}
		});
		// 监听点击事件
		dataDefine_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				String old_name = "";
				if(oldView != null){
					TextView txt_old_name = (TextView) oldView.findViewById(R.id.update_survey_name);
					old_name = (String) txt_old_name.getText();
				}
				TextView txt_name = (TextView) view.findViewById(R.id.update_survey_name);
				// 若点击项不是旧点击项
				if(!old_name.equals(txt_name.getText()) ){
					// 修改选中项背景以及字体颜色
					view.setBackgroundColor(mHomeActivity.getResources().getColor(R.color.lanse));
					txt_name.setTextColor(mHomeActivity.getResources().getColor(android.R.color.white));
					// 将上次点击项还原颜色
						if (oldView != null) {
							TextView txt_old_name = (TextView) oldView.findViewById(R.id.update_survey_name);
							oldView.setBackgroundColor(mHomeActivity.getResources().getColor(R.color.bg_update_survey_list));
							txt_old_name.setTextColor(mHomeActivity.getResources().getColor(android.R.color.black));
						}
				}
				oldView = view;
				// 记录点击的位置
				final DataDefine updateDataDefine = currentViewModel.currentUpdateDataDefines.get(position);
				if (updateDataDefine != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
					builder.setTitle("询问信息");
					builder.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									mHomeActivity.loadingWorker
											.showLoading("数据同步中...");
									Message loginMsg = new Message();
									currentViewModel.currentUpdateDataDefine = updateDataDefine;
									loginMsg.what = TASK_UPDATE_DATADEFINE;
									mBackgroundHandler.sendMessage(loginMsg);
								}
							});
					builder.setNegativeButton("取消", null);
					builder.setMessage("确认同步【" + updateDataDefine.Name + "】信息吗？");
					builder.show();
				}
			}
		});		
		userlogo.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();    
				intent.setClass(mHomeActivity,UserInfoActivity.class);
				mHomeActivity.startActivity(intent);
			}
		});
		
		checkSDCardSize();
		loadUserLogo();
		//修改成离线不隐藏统计信息了 lee
		//invisibleTaskInfoByOffline();
		receiverMainServerCreated();
		//HomeOperator.synchroReportInfo(currentViewModel.currentUser);
	}

	/**
	 * 检查SDCard容量
	 */
	@SuppressWarnings("deprecation")
	private void checkSDCardSize() {
		// 判断是否有插入存储卡
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 获取出厂内置SD卡 例如三星 和 小米
			File path = Environment.getExternalStorageDirectory();
			// 取得SDcard文件路径
			StatFs statfs = new StatFs(path.getPath());
			// 获取block的SIZE
			long blockSize = statfs.getBlockSize();
			// 获取BLOCK数量
			long totalBlocks =  statfs.getBlockCount();
			// 己使用的Block的数量
			long availaBlock = statfs.getAvailableBlocks();
			
			long rest = availaBlock * blockSize / 1024 / 1024;
			long total = totalBlocks * blockSize / 1024 / 1024;

			home_scard_rest_size.setText(rest + "MB");
			home_scard_total_size.setText(total + "MB");
			// 获取剩余容量小于指定大小时 提示
			String minSDCardSize = EIASApplication
					.getSystemSetting(BroadRecordType.KEY_SETTING_SDCARDSIZE);
			// 剩余容量是否小于指定大小
			if (rest < Integer.parseInt(minSDCardSize)) {
				Double restPoint = (double) ((double)availaBlock /  (double)totalBlocks * 100);
				DecimalFormat df = new DecimalFormat("0.##");
				home_scard_title.setText("手机容量剩余不足" + df.format(restPoint)
						+ "%,请尽快清理");
				home_scard_title.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.red));
				home_scard_rest_size.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.red));
			}else{
				home_scard_title.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.theme));
				home_scard_rest_size.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.theme));
			}
		}		
	}

	/**
	 * 在离线的情况下隐藏部分任务信息
	 */
	@SuppressWarnings("unused")
	private void invisibleTaskInfoByOffline() {
		if(EIASApplication.IsOffline){
			un_task.setVisibility(View.INVISIBLE);
			un_task_number_all.setVisibility(View.INVISIBLE);
			en_task.setVisibility(View.INVISIBLE);
			en_task_number_all.setVisibility(View.INVISIBLE);
		}
	}

	
	/** 图片加载类 */
	private ImageLoader imageLoader;
	/** imageLoader 参数配置 */
	@SuppressWarnings("unused")
	private DisplayImageOptions option;
	
	/***
	 * 初始化图片加载类
	 */
	private void initImageLoader() {
		imageLoader = ImageLoader.getInstance();
		// 配置ImageLoader
		option = new DisplayImageOptions.Builder()//
				.showImageOnLoading(R.drawable.friends_sends_pictures_no)//
				.showImageForEmptyUri(R.drawable.friends_sends_pictures_no)//url爲空會显示该图片
				.showImageOnFail(R.drawable.friends_sends_pictures_no)//加载图片出现问题
				.cacheInMemory(true).cacheOnDisk(true)//缓存到磁盘，和内存
				.considerExifParams(true)////图片圆角显示，值为整数.displayer(new RoundedBitmapDisplayer(5))
				.imageScaleType(ImageScaleType.EXACTLY)//
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	/**
	 * 读取用户图片
	 */
	private void loadUserLogo(){
		UserInfo user=EIASApplication.getCurrentUser();
		if(TextUtils.isEmpty(user.ImagePath)){
			String imagePath = EIASApplication.userRoot + user.Account
					+ File.separator;
			FileUtil.mkDir(imagePath);
			File file = new File(imagePath + EIASApplication.USER_IMAGE_NAME);
			if (file.exists()){
				userlogo.setImageBitmap(CameraUtils.getBitmap(file.getAbsolutePath()));
			}
		}else{// 头像路径存在，使用url 获头像
			if(imageLoader==null){
				initImageLoader();
			}
			//imageLoader.displayImage(user.LatestServer + user.ImagePath, userlogo, option);// 加载图片
			imageLoader.loadImage(user.LatestServer + user.ImagePath, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					userlogo.setImageBitmap(arg2);
					try {
						saveUserImage(UserInfoOperator.getImageStr(arg2));
						userlogo.setDrawingCacheEnabled(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	
	
	
	/**
	 * 保存下载后的用户头像
	 * 
	 * @param tempResult
	 * @throws FileNotFoundException
	 */
	private void saveUserImage(String bitmapStr)
			throws FileNotFoundException {
		String IMAGE_FILE_NAME = "faceImage.jpg";
		/*String imagePath = EIASApplication.userRoot
				+ tempResult.Data.UserAccount + File.separator;*/
		String imagePath = EIASApplication.userRoot + EIASApplication.getCurrentUser().Account + File.separator;
		File file = new File(imagePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		Bitmap photo = BitmapHelperUtil
				.stringBase64toBitmap(bitmapStr);
		FileOutputStream out = new FileOutputStream(imagePath + IMAGE_FILE_NAME);
		photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
	}
	
	/**
	 * 触屏的事件
	 */
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener menuBodyOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return mHomeActivity.touchPage(event.getAction(), event.getX(),
					event.getY(), true);
		}
	};

	/**
	 * 加载数据
	 */
	public void loadData() {
		mHomeActivity.loadingWorker.showLoading("数据获取中...");
		Message loginMsg = new Message();
		loginMsg.what = TASK_GET_HOMEINFO;
		mBackgroundHandler.sendMessage(loginMsg);
	}
	
	
	/***
	 * 
	 * @author kevin
	 * @date 2015-11-12 下午4:26:44
	 * @Description: 后台启动上传日志任务     
	 */
	private void upLoadExctionInfo(){
		Message loginMsg = new Message();
		loginMsg.what = TASK_UPLOAD_DB_DATALOG;
		mBackgroundHandler.sendMessage(loginMsg);
	}
	
	
	/**
	 * 响应切换为离线状态之后
	 */
	public void receiverMainServerCreated() {
		ArrayList<String> temp = new ArrayList<String>();		
		temp.add(BroadRecordType.CHANGED_OFFLINE);		
		changedOfflineReceiver = new BaseBroadcastReceiver(mHomeActivity,temp);
		changedOfflineReceiver.setAfterReceiveBroadcast(new afterReceiveBroadcast() {			
			@Override
			public void onReceive(final Context context, Intent intent) {
				String actionType =intent.getAction();		
				switch(actionType){
				case BroadRecordType.CHANGED_OFFLINE:// 这里切换成离线状态
					//删除其他页面
					BaseApplication application = (BaseApplication) mHomeActivity.getApplication(); 
					application.getActivityManager().popAllActivityExceptOne(mHomeActivity.getClass()); 
					//刷新下拉列表
					mHomeActivity.setMenuItemsVisibility();
					loadData();
					break;
				default:
					break;
				}
			}
		});
	}
	
	
}
