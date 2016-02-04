package com.yunfang.eias.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;

import com.yunfang.eias.R;
import com.yunfang.eias.ui.OpenDialogResource;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
 

public class OpenDialogHelper {

	public String search = "";
	private View myView;
	private TextView textviem;
	private EditText myEditText;
	Context mContext = null;

	public OpenDialogHelper(Context context){
		mContext = context;
	}

	/**
	 * 重命名
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void Rename(final Context cont,final String oldtitle,final String oldpath) {

		// 更改文件名
		LayoutInflater factory = LayoutInflater.from(cont);
		/* 初始化myChoiceView，使用rename_alert_dialog为layout */
		myView = factory.inflate(R.layout.open_dialog_rename, null);
		myEditText = (EditText) myView.findViewById(R.id.mEdit);
		myEditText.setText(oldtitle);
		AlertDialog renameDialog = new AlertDialog.Builder(cont).create();
		renameDialog.setView(myView);

		/* 设置更改档名点击确认后的Listener */
		renameDialog.setButton("确定", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				final String modName = myEditText.getText().toString();
				final String pFile = oldpath.substring(0, oldpath.lastIndexOf("/")) + "/";
				final String newPath = pFile + modName;
				final File file = new File(oldpath);

				/* 判断档名是否已存在 */
				if (new File(newPath).exists()) {
					/* 排除修改档名时没修改直接送出的状况 */
					if (modName.equals(oldtitle)) {
						/* 跳出Alert警告档名重复，并确认是否修改 */
						new AlertDialog.Builder(cont).setTitle("注意!").setMessage("档名已经存在，是否要覆盖?")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										System.out.println(modName + "-----" + newPath);
										/* 档名重复仍然修改会覆改掉已存在的文件 */
										file.renameTo(new File(newPath));
									}
								}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								}).show();
					}
				} else {
					/* 档名不存在，直接做修改动作 */
					file.renameTo(new File(newPath));
					System.out.println(modName + "////" + newPath);
				}
			}
		});
		renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		renameDialog.show();
	}

	/**
	 * 
	 * 删除
	 */
	public void Delete(File f1) {
		f1.delete();
	}

	public void deleFiles(File f) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].isDirectory()) {
					deleFiles(fs[i]);
				} else {
					Delete(fs[i]);
				}
			}
		} else {
			System.out.println(f.getName() + "是文件不是目录");
			Delete(f);

		}
		f.delete();
	}

	/**
	 * 复制
	 * 
	 * @param src
	 * @param dec
	 */
	public void CopyFiles(String src, String dec) {
		whorkFiles(src, dec, true);
	}

	/**
	 * 剪切
	 * 
	 * @param src
	 * @param dec
	 */
	public void CutFiles(String src, String dec) {
		whorkFiles(src, dec, false);
	}

	public Boolean whorkFiles(String src, String dec, Boolean isCopy) {
		long l1 = System.currentTimeMillis();
		if (dec.equals(src) || dec.contains(src)) {
			new AlertDialog.Builder(mContext).setTitle("注意!").setMessage("不能复制到当前目录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
			return false;
		}
		if (isCopy) {
			File f = new File(src);
			String path = dec;
			if (f.isDirectory()) {
				String new_path = path + "\\" + f.getName();
				File new_f = new File(new_path);
				new_f.mkdir();
				getCopyFiles(f, new_path);
			} else
				getCopyFiles(f, path);
		} else {
			File f = new File(src);
			String path = dec;
			if (f.isDirectory()) {
				String new_path = path + "\\" + f.getName();
				File new_f = new File(new_path);
				new_f.mkdir();
				getCutFiles(f, new_path);
			} else
				getCutFiles(f, path);

		}
		long l2 = System.currentTimeMillis();
		System.out.println("耗时" + (l2 - l1) + "毫秒");
		return true;
	}

	/**
	 * 复制调用的函数
	 * 
	 * @param f
	 * @param path
	 */
	public void getCopyFiles(File f, String path) {
		if (path.equals(f.getPath()) || path.contains(f.getPath())) {
			new AlertDialog.Builder(mContext).setTitle("注意!").setMessage(f.getName() + "不能复制到当前目录")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
			return;
		}
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].isDirectory()) {
					String new_path = path + "\\" + fs[i].getName();
					File new_f = new File(new_path);
					new_f.mkdir();
					getCopyFiles(fs[i], new_path);
				} else {
					File new_f = new File(path + "\\" + fs[i].getName());
					copy(fs[i], new_f);
				}
			}
		} else {
			System.out.println(f.getName() + "是文件不是目录");
			File new_f = new File(path + "\\" + f.getName());
			copy(f, new_f);
		}
	}

	/**
	 * 剪切调用的函数
	 * 
	 * @param f
	 * @param path
	 */
	public void getCutFiles(File f, String path) {
		if (path.equals(f.getPath()) || path.contains(f.getPath())) {
			new AlertDialog.Builder(mContext).setTitle("注意!").setMessage(f.getName() + "不能复制到当前目录")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
			return;
		}
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].isDirectory()) {
					String new_path = path + "\\" + fs[i].getName();
					File new_f = new File(new_path);
					if (!new_f.exists()) {
						new_f.mkdir();
					}
					getCutFiles(fs[i], new_path);
				} else {
					File new_f = new File(path + "\\" + fs[i].getName());
					cut(fs[i], new_f);
				}
			}
			f.delete();
		} else {
			System.out.println(f.getName() + "是文件不是目录");
			File new_f = new File(path + "\\" + f.getName());
			cut(f, new_f);
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param f1
	 * @param f2
	 */
	public void copy(final File f1, final File f2) {
		try {
			if (f2.exists()) {
				new AlertDialog.Builder(mContext).setTitle("注意!").setMessage(f1.getName() + "文件已经存在,是否覆盖")
						.setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								try {
									FileInputStream op = new FileInputStream(f1);
									FileChannel fc = op.getChannel();
									// RandomAccessFile fos =new
									// RandomAccessFile(f2,"rwd");
									MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, f1.length());
									FileOutputStream fos2 = new FileOutputStream(f2);
									FileChannel fc2 = fos2.getChannel();
									fc2.write(mbb);
									op.close();
									fos2.close();
								} catch (Exception e) {
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								return;
							}
						}).show();
			}

			else {
				FileInputStream op = new FileInputStream(f1);
				FileChannel fc = op.getChannel();
				// RandomAccessFile fos =new RandomAccessFile(f2,"rwd");
				MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, f1.length());
				FileOutputStream fos2 = new FileOutputStream(f2);
				FileChannel fc2 = fos2.getChannel();
				fc2.write(mbb);
				op.close();
				fos2.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 剪切文件
	 * 
	 * @param f1
	 * @param f2
	 */
	public void cut(final File f1, final File f2) {
		try {
			if (f2.exists()) {
				new AlertDialog.Builder(mContext).setTitle("注意!").setMessage(f1.getName() + "文件已经存在,是否覆盖")
						.setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								try {
									FileInputStream op = new FileInputStream(f1);
									FileChannel fc = op.getChannel();
									// RandomAccessFile fos =new
									// RandomAccessFile(f2,"rwd");
									MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, f1.length());
									FileOutputStream fos2 = new FileOutputStream(f2);
									FileChannel fc2 = fos2.getChannel();
									fc2.write(mbb);
									op.close();
									fos2.close();
									f1.delete();
								} catch (Exception e) {
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								return;
							}
						}).show();
			}

			else {
				FileInputStream op = new FileInputStream(f1);
				FileChannel fc = op.getChannel();
				MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, f1.length());
				FileOutputStream fos2 = new FileOutputStream(f2);
				FileChannel fc2 = fos2.getChannel();
				fc2.write(mbb);
				op.close();
				fos2.close();
				f1.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 新建文件夹
	@SuppressWarnings("deprecation")
	public void newfile(Context context, String path, boolean bool) {

		final String fpath = path;
		final Context context1 = context;
		final boolean bool1 = bool;
		LayoutInflater factory = LayoutInflater.from(context);
		/* 初始化myChoiceView，使用rename_alert_dialog为layout */
		myView = factory.inflate(R.layout.open_dialog_rename, null);
		textviem = (TextView) myView.findViewById(R.id.mText);
		textviem.setText("新建文件夹");
		AlertDialog newnameDialog = new AlertDialog.Builder(context).create();
		newnameDialog.setView(myView);

		/* 设置更改档名点击确认后的Listener */
		newnameDialog.setButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				myEditText = (EditText) myView.findViewById(R.id.mEdit);
				StringBuffer sb = new StringBuffer();
				if (bool1) {
					sb.append(fpath);
					sb.append(File.separator);
					sb.append(myEditText.getText());
				} else {
					sb.append(fpath);
					sb.append(File.separator);
					sb.append(myEditText.getText());
				}
				File f = new File(sb.toString());
				if (f.exists()) {
					new AlertDialog.Builder(context1).setTitle("注意!").setMessage("文件夹已经存在").setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();

				} else {
					f.mkdir();
				}
				((OpenDialogResource) context1).flush();
			}
		});
		newnameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		newnameDialog.show();
	}

	public void getsearch(final Context context) {
		LayoutInflater factory = LayoutInflater.from(context);
		/* 初始化myChoiceView，使用rename_alert_dialog为layout */
		myView = factory.inflate(R.layout.open_dialog_rename, null);
		myEditText = (EditText) myView.findViewById(R.id.mEdit);
		textviem = (TextView) myView.findViewById(R.id.mText);
		textviem.setText("请输入搜索信息：");
		AlertDialog renameDialog = new AlertDialog.Builder(context).setView(myView).setPositiveButton("确定", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				search = myEditText.getText().toString();
				System.out.println(search + "---------8++");
				((OpenDialogResource) context).getSearchkey(search);
			}
		}).setNegativeButton("取消", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();

		renameDialog.show();
	}

	public void findFiles(File f, String path) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].isDirectory()) {
					String new_path = path + "\\" + fs[i].getName();
					File new_f = new File(new_path);
					new_f.mkdir();
					getCopyFiles(fs[i], new_path);
				} else {
					File new_f = new File(path + "\\" + fs[i].getName());
					copy(fs[i], new_f);
				}
			}
		} else {
			System.out.println(f.getName() + "是文件不是目录");
			File new_f = new File(path + "\\" + f.getName());
			copy(f, new_f);
		}
	}

	public static String formatDate(long date) {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date);
	}

	public static String formatNumber(int bytes) {
		String unit = "未知大小";
		if (bytes != -1) {
			float value = bytes;
			if (value < 1024) {
				unit = " B";
			}
			if (value >= 1024) {
				value = value / 1024;
				unit = " KB";
			}
			if (value >= 1024) {
				value = value / 1024;
				unit = " MB";
			}
			if (value >= 1024) {
				value = value / 1024;
				unit = " GB";
			}
			if (value >= 1024) {
				value = value / 1024;
				unit = " TB";
			}

			return String.format("%.2f %s", value, unit);
		} else {
			return unit;
		}
	}
}
