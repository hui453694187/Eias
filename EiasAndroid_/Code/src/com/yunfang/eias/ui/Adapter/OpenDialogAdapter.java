package com.yunfang.eias.ui.Adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yunfang.eias.R;
import com.yunfang.eias.utils.OpenDialogHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SdCardPath")
public class OpenDialogAdapter extends BaseAdapter {
	/*
	 * 变量声明 mIcon1：回到根目录的图文件 mIcon2：回到上一层的图档 mIcon3：文件夹的图文件 mIcon4：文件的图档
	 */
	private LayoutInflater mInflater;
	private String mDir;
	private List<Map<String, Object>> items;
	@SuppressWarnings("unused")
	private String search = null;

	private boolean flag = true;

	public String getmDir() {
		return mDir;
	}

	public List<Map<String, Object>> getItems() {
		return items;
	}

	public void setItems(List<Map<String, Object>> items) {
		this.items = items;
	}

	/* MyAdapter的构造器，传入三个参数 */
	public OpenDialogAdapter(Context context, String str) {
		/* 参数初始化 */
		mInflater = LayoutInflater.from(context);
		mDir = str;
		items = getData();
		this.search = "";
	}

	public OpenDialogAdapter(Context context, String str, String search) {
		/* 参数初始化 */
		mInflater = LayoutInflater.from(context);
		mDir = str;
		items = getSearchData(search, str);
		this.search = search;
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			/* 使用自定义的file_row作为Layout */
			convertView = mInflater.inflate(R.layout.open_dialog_list_item, null);
			/* 初始化holder的text与icon */
			holder = new ViewHolder();
			holder.text01 = (TextView) convertView.findViewById(R.id.textView1);
			holder.text02 = (TextView) convertView.findViewById(R.id.textView2);
			holder.text03 = (TextView) convertView.findViewById(R.id.textView3);
			holder.icon = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.checkBox.setVisibility(View.INVISIBLE);

			this.notifyDataSetChanged();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text01.setText(items.get(position).get("title").toString());
		holder.text02.setText(items.get(position).get("info").toString());
		holder.text03.setText(items.get(position).get("infos").toString());
		holder.icon.setImageResource(items.get(position).get("img").hashCode());

		return convertView;
	}

	/**
	 * 获取列表
	 * 
	 * @param mDir
	 * @return
	 */
	public List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(mDir);
		File[] files = f.listFiles();
		// System.out.println("search:"+search);
		if (!mDir.equals("/sdcard") && !mDir.equals("/")) {
			map = new HashMap<String, Object>();
			map.put("title", "Back to ../");
			map.put("info", f.getParent());
			map.put("infos", OpenDialogHelper.formatDate(f.lastModified()));
			map.put("img", R.drawable.open_dialog_ex_folder);
			map.put("check", false);
			list.add(map);
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				map = new HashMap<String, Object>();

				if (files[i].isDirectory()) {
					map.put("title", files[i].getName());
					map.put("info", files[i].getPath());
					map.put("infos", OpenDialogHelper.formatDate(files[i].lastModified()));
					map.put("img", R.drawable.open_dialog_ex_folder);
					map.put("check", null);
					list2.add(map);
				} else {
					map.put("title", files[i].getName());
					map.put("infos", OpenDialogHelper.formatNumber((int) files[i].length()));
					map.put("info", files[i].getPath());
					map.put("img", R.drawable.open_dialog_ex_doc);
					map.put("check", false);
					list.add(map);
				}
			}
		}
		list.addAll(list2);
		return list;
	}

	/**
	 * 获取列表
	 * 
	 * @param mDir
	 * @return
	 */
	public List<Map<String, Object>> getSearchData(String key, String dir) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(dir);
		File[] files = f.listFiles();
		if (flag) {
			map = new HashMap<String, Object>();
			map.put("title", "Back to ../");
			map.put("info", f.getParent() + "/" + f.getName());
			map.put("infos", OpenDialogHelper.formatDate(f.lastModified()));
			map.put("img", R.drawable.open_dialog_ex_folder);
			map.put("check", false);
			list.add(map);
			flag = false;
		}

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				map = new HashMap<String, Object>();

				if (files[i].isDirectory()) {
					list.addAll(getSearchData(key, files[i].getPath()));
					map.put("title", files[i].getName());
					map.put("info", files[i].getPath());
					map.put("infos", files[i].getPath());
					map.put("img", R.drawable.open_dialog_ex_folder);
					map.put("check", false);
					if (files[i].getName().contains(key))
						list2.add(map);
				} else {
					map.put("title", files[i].getName());
					map.put("infos", files[i].getPath());
					map.put("info", files[i].getPath());
					map.put("img", R.drawable.open_dialog_ex_doc);
					map.put("check", false);
					if (files[i].getName().contains(key))
						list.add(map);
				}
			}
		}
		list.addAll(list2);
		return list;
	}

	private class ViewHolder {
		TextView text01;
		TextView text02;
		TextView text03;
		ImageView icon;
		CheckBox checkBox;
	}
}
