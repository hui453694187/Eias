package com.yunfang.eias.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yunfang.eias.R;
import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.ui.Adapter.OpenDialogAdapter;
import com.yunfang.eias.utils.OpenDialogHelper;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class OpenDialogResource extends TabActivity {
	private final int NONE = 0;
	public final int COPY = 1;
	public final int CUT = 2;
	@SuppressLint("SdCardPath")
	private final String mSDK = "/sdcard";
	private final String mPho = "/";
	public ListView listSdk;
	public ListView listPho;
	@SuppressWarnings("unused")
	private CheckBox checkBox;
	private TabHost tabHost;
	private LayoutInflater mInflater;
	private List<String> paths = new ArrayList<String>();
	private int menuState = 0;
	private Menu menuCls;
	private OpenDialogHelper tools = new OpenDialogHelper(this);
	final Context context = OpenDialogResource.this;
	private int workSatae = NONE;
	private String fpath = mSDK;
	@SuppressWarnings("unused")
	private static String key = null;
	/**
	 * 主菜单
	 */
	public AppHeader appHeader;

	/**
	 * 
	 * 数据初始化
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String taskNum = getIntent().getStringExtra("taskNum");

		setContentView(R.layout.open_dialog_list);
		mInflater = LayoutInflater.from(this);
		appHeader = new AppHeader(this, R.id.home_title);

		appHeader.visUserInfo(false);
		appHeader.visBackView(true);
		appHeader.setTitle("[" + taskNum + "]选择文件");

		checkBox = (CheckBox) mInflater.inflate(R.layout.open_dialog_list_item, null).findViewById(R.id.checkBox1);
		// //////选项卡
		tabHost = getTabHost();
		TabHost.TabSpec ts1 = tabHost.newTabSpec("tab1");
		ts1.setIndicator("sd卡内存");
		ts1.setContent(R.id.listView1);
		tabHost.addTab(ts1);
		listSdk = (ListView) findViewById(R.id.listView1);
		listSdk.setAdapter(getAdapter(mSDK));

		TabHost.TabSpec ts2 = tabHost.newTabSpec("tab2");
		ts2.setIndicator("手机卡");
		ts2.setContent(R.id.listView2);
		tabHost.addTab(ts2);
		listPho = (ListView) findViewById(R.id.listView2);
		listPho.setAdapter(getAdapter(mPho));

		tabHost.setCurrentTab(0);
		getFilesDir();
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				if (menuCls != null) {
					setCheckBox(false);
					menuState = 0;
					onCreateOptionsMenu(menuCls);
				}
			}
		});
		listSdk.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				if (menuState == 1) {
					CheckBox checkBox1 = (CheckBox) arg1.findViewById(R.id.checkBox1);
					if (checkBox1.getVisibility() != View.VISIBLE)
						return;
					if (checkBox1.isChecked())
						checkBox1.setChecked(false);
					else
						checkBox1.setChecked(true);
					return;
				}
				String info = (String) item.get("info");
				fpath = info;
				File f = new File(info);
				if (f.isDirectory()) {
					listSdk.setAdapter(getAdapter(info));
				} else {
					selectFile(f.getAbsoluteFile().toString());
					// openFile(f);
				}
			}
		});
		listPho.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				if (menuState == 1) {
					CheckBox checkBox1 = (CheckBox) arg1.findViewById(R.id.checkBox1);
					if (checkBox1.getVisibility() != View.VISIBLE)
						return;
					if (checkBox1.isChecked())
						checkBox1.setChecked(false);
					else
						checkBox1.setChecked(true);
					return;
				}
				String info = (String) item.get("info");
				fpath = info;
				File f = new File(info);
				if (f.isDirectory()) {
					listPho.setAdapter(getAdapter(info));
				} else {
					selectFile(f.getAbsoluteFile().toString());
					// openFile(f);
				}

			}
		});
	}

	/**
	 * 确认选择文件
	 */
	private void selectFile(String fileFullName) {
		Intent intent = new Intent();
		intent.putExtra("fileFullName", fileFullName);
		intent.setAction(BroadRecordType.TASK_IMPORT_FILE_SELECTED);
		sendBroadcast(intent);
		finish();
	}

	/**
	 * 
	 * 菜单生成
	 */
	@SuppressWarnings("unused")
	public boolean onCreateOptionsMenu(Menu menu) {
		menuCls = menu;

		if (menuState == 0) {
			menu.clear();
			String[] str = getResources().getStringArray(R.array.open_dialog_hubby);

			MenuItem itemNew = menu.add(1, 0, 3, str[0]).setIcon(R.drawable.open_dialog_folder_new);
			MenuItem itemSearch = menu.add(1, 1, 4, str[1]).setIcon(R.drawable.open_dialog_search);
			MenuItem itemWork = menu.add(1, 2, 1, str[2]).setIcon(R.drawable.open_dialog_op);
			MenuItem itemPaste = menu.add(1, 3, 2, str[3]).setIcon(R.drawable.open_dialog_paste);
			MenuItem itemBack = menu.add(1, 4, 5, str[4]).setIcon(R.drawable.open_dialog_edit_undo);

			if (paths.size() == 0) {
				itemPaste.setEnabled(false);
			} else if (paths.size() > 0) {
				itemPaste.setEnabled(true);
			}
			itemSearch.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				public boolean onMenuItemClick(MenuItem item) {
					tools.getsearch(OpenDialogResource.this);
					return false;
				}
			});
			itemWork.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				public boolean onMenuItemClick(MenuItem item) {
					setCheckBox(true);
					onCreateOptionsMenu(menuCls);
					return false;
				}
			});
			itemNew.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				public boolean onMenuItemClick(MenuItem item) {
					Toast.makeText(OpenDialogResource.this, fpath, Toast.LENGTH_LONG).show();

					if ("tab1".equals(tabHost.getCurrentTabTag())) {
						tools.newfile(OpenDialogResource.this, fpath, true);
						System.out.println("^&%$$%");

					} else {
						// fpath = getFilesDir().getPath();
						if (fpath.equals(mSDK))
							fpath = mPho;
						// System.out.println(get);
						tools.newfile(OpenDialogResource.this, fpath, false);
						listPho.setAdapter(getAdapter(fpath));
					}

					System.out.println(getFilesDir().getPath());
					return false;
				}
			});
			itemPaste.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@SuppressWarnings({ "unchecked" })
				public boolean onMenuItemClick(MenuItem item) {
					String dec = null;
					if ("tab1".equals(tabHost.getCurrentTabTag())) {
						HashMap<String, Object> decitem = (HashMap<String, Object>) listSdk.getItemAtPosition(0);
						String title = (String) decitem.get("title");
						if ("Back to ../".equals(title)) {
							String info = (String) decitem.get("info");
							File f = new File(info);
							if (f.isDirectory()) {
								dec = fpath;
							} else
								dec = mSDK;
						}
					} else {
						HashMap<String, Object> decitem = (HashMap<String, Object>) listPho.getItemAtPosition(0);
						String title = (String) decitem.get("title");
						if ("Back to ../".equals(title)) {
							String info = (String) decitem.get("info");
							File f = new File(info);
							if (f.isDirectory()) {
								dec = fpath;
							} else
								dec = mPho;
						}
					}
					if (workSatae == COPY) {
						for (String src : paths) {
							tools.CopyFiles(src, dec);
							System.out.println(src + "::" + dec);
						}
						workSatae = NONE;
					}
					if (workSatae == CUT) {
						for (String src : paths) {
							tools.CutFiles(src, dec);
							System.out.println(src + "--------" + dec);
						}
					}
					paths.clear();
					onCreateOptionsMenu(menuCls);

					listSdk.setAdapter(getAdapter(fpath));
					return false;
				}
			});
		}
		if (menuState == 1) {
			menu.clear();
			String[] str = getResources().getStringArray(R.array.open_dialog_menu2);
			MenuItem itemCut = menu.add(1, 3, 4, str[0]).setIcon(R.drawable.open_dialog_cut);
			MenuItem itemCopy = menu.add(1, 2, 3, str[1]).setIcon(R.drawable.open_dialog_copy);
			MenuItem itemDelete = menu.add(1, 4, 5, str[2]).setIcon(R.drawable.open_dialog_delete);
			MenuItem itemBack = menu.add(1, 4, 5, str[3]).setIcon(R.drawable.open_dialog_edit_undo);
			// 菜单事件
			OnMenuItemClickListener onClickListener = new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					setCheckBox(false);
					onCreateOptionsMenu(menuCls);
					return false;
				}
			};

			// 把菜单事件加到控件中
			itemCut.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				public boolean onMenuItemClick(MenuItem item) {
					workSatae = CUT;
					getpaths();
					setCheckBox(false);
					onCreateOptionsMenu(menuCls);
					return false;
				}
			});
			itemCopy.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				public boolean onMenuItemClick(MenuItem item) {
					workSatae = COPY;
					getpaths();
					setCheckBox(false);
					onCreateOptionsMenu(menuCls);
					return false;
				}
			});
			itemDelete.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				public boolean onMenuItemClick(MenuItem item) {
					getpaths();
					for (int i = 0; i < paths.size(); i++) {
						String path = paths.get(i);
						tools.deleFiles(new File(path));
						new File(path).delete();
					}
					paths.clear();
					Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
					listSdk.setAdapter(getAdapter(fpath));
					return false;
				}
			});
			itemBack.setOnMenuItemClickListener(onClickListener);

		}

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 获取勾选中的项对应的路径
	 */
	private void getpaths() {
		paths.clear();
		ListView lstres = null;
		if ("tab1".equals(tabHost.getCurrentTabTag()))
			lstres = (ListView) findViewById(R.id.listView1);// 结果列表
		else
			lstres = (ListView) findViewById(R.id.listView2);// 结果列表
		for (int i = 0; i < lstres.getChildCount(); i++) {
			LinearLayout ll = (LinearLayout) lstres.getChildAt(i);// 获得子级
			CheckBox chkone = (CheckBox) ll.findViewById(R.id.checkBox1);// 从子级中获得控件
			if (chkone.isChecked()) {
				TextView textView = (TextView) ll.findViewById(R.id.textView2);
				paths.add(textView.getText().toString());
			}
		}
	}

	private void setCheckBox(boolean b) {
		int num = 0;
		ListView listv = null;
		if ("tab2".equals(tabHost.getCurrentTabTag()))
			listv = listPho;
		else
			listv = listSdk;
		num = listv.getChildCount();
		if (b) {
			for (int i = 0; i < num; i++) {
				if (i == 0) {
					View v = listv.getChildAt(i);
					TextView textView = (TextView) v.findViewById(R.id.textView1);
					if ("Back to ../".equals(textView.getText())) {
						continue;
					}
				}
				View v = listv.getChildAt(i);
				CheckBox checkBox1 = (CheckBox) v.findViewById(R.id.checkBox1);
				checkBox1.setVisibility(View.VISIBLE);
			}
			menuState = 1;
		} else {
			for (int i = 0; i < num; i++) {
				if (i == 0) {
					View v = listv.getChildAt(i);
					TextView textView = (TextView) v.findViewById(R.id.textView1);
					if ("Back to ../".equals(textView.getText())) {
						continue;
					}
				}
				View v = listv.getChildAt(i);
				CheckBox checkBox1 = (CheckBox) v.findViewById(R.id.checkBox1);
				checkBox1.setChecked(false);
				checkBox1.setVisibility(View.INVISIBLE);
			}
			menuState = 0;
		}
	}

	private ListAdapter getAdapter(String mPho) {
		if (menuCls != null) {
			menuState = 0;
			onCreateOptionsMenu(menuCls);
		}
		OpenDialogAdapter Myadapter = new OpenDialogAdapter(this, mPho);
		return Myadapter;
	}

	private ListAdapter getAdapter(String mPho, String search) {
		if (menuCls != null) {
			menuState = 0;
			onCreateOptionsMenu(menuCls);
		}
		OpenDialogAdapter Myadapter = new OpenDialogAdapter(this, mPho, search);
		return Myadapter;
	}

	public String getSearchkey(String str) {
		if ("tab1".equals(tabHost.getCurrentTabTag()))
			listSdk.setAdapter(getAdapter(fpath, str));
		else
			listPho.setAdapter(getAdapter(fpath, str));

		return str;
	}

	public void flush() {
		if ("tab1".equals(tabHost.getCurrentTabTag())) {
			listSdk.setAdapter(getAdapter(fpath));
		} else
			listPho.setAdapter(getAdapter(fpath));
	}

	/**
	 * 打开文件
	 * 
	 */
	public void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		String type = getMIMEType(f);
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/**
	 * 判断文件MimeType的方法
	 * 
	 * @param
	 * @return
	 */
	public String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

		/* 依附档名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else {
			/* 如果无法直接打开，就跳出软件列表给用户选择 */
			type = "*";
		}
		type += "/*";
		return type;
	}
}