package com.yunfang.eias.ui.Adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunfang.eias.R;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskDataItem;

@SuppressLint("InflateParams")
public class TaskCategoryInfoAdapter extends BaseAdapter {
	/**
	 * 分类项
	 */
	private ArrayList<TaskCategoryInfo> categories;

	/**
	 * 勘察表分类项信息，包含子项
	 */
	private ArrayList<DataCategoryDefine> defineCategories;

	/**
	 * 界面
	 */
	private Context mContext;

	/**
	 * 布局
	 */
	private LayoutInflater inflater;

	/**
	 * 分类名称
	 */
	TextView txt_name;

	/**
	 * 总项填写进度
	 */
	TextView txt_number;

	/**
	 * 必填项填写进度
	 */
	TextView txt_required_number;

	/**
	 * 子项信息
	 */
	TextView txt_content;

	/**
	 * 选中的位置
	 * */
	@SuppressWarnings("unused")
	private int selectedPosition = -1;

	/**
	 * 控件类型
	 */
	private final int ITEM = 0x01;

	/**
	 * 构造方法
	 * @param context
	 */
	public TaskCategoryInfoAdapter(Context context) {
		super();
		this.categories = new ArrayList<TaskCategoryInfo>();
		this.mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	/**
	 * 刷新
	 * 
	 * @param list
	 */
	public void refersh(ArrayList<TaskCategoryInfo> list, ArrayList<DataCategoryDefine> definelist) {
		if (categories != null && list != null && !list.isEmpty()) {
			categories.clear();
			// 对list对象根据definelist的order值进行排序
			list = sortTaskCategoryInfo(list, definelist);
			categories.addAll(list);
			defineCategories = definelist;
		}
		notifyDataSetChanged();
	}

	/**
	 * 获取文件名称数量 +1表示最后的添加新的文件
	 */
	@Override
	public int getCount() {
		return categories.size();
	}

	/**
	 * 任务分类项根据勘察表分类项排序方法
	 * 
	 * @param list
	 *            任务分类项列表
	 * @param definelist
	 *            勘察表分类项列表
	 * @return 排序后的分类项列表
	 */
	private ArrayList<TaskCategoryInfo> sortTaskCategoryInfo(ArrayList<TaskCategoryInfo> list, ArrayList<DataCategoryDefine> definelist) {
		// 从小到大正序:
		Collections.sort(definelist, new Comparator<DataCategoryDefine>() {
			@Override
			public int compare(DataCategoryDefine categoryDefine1, DataCategoryDefine categoryDefine2) {
				Integer id1 = categoryDefine1.IOrder;
				Integer id2 = categoryDefine2.IOrder;
				// 可以按User对象的其他属性排序，只要属性支持compareTo方法
				return id1.compareTo(id2);
			}
		});
		// 其次(可重复的情况下)按照子项名称排序(此段代码无效,暂时修改为在基础库派发任务时对楼栋进行排序)
		/*
		 * Collections.sort(list, new Comparator<TaskCategoryInfo>() {
		 * 
		 * @Override public int compare(TaskCategoryInfo taskCategoryInfo1,
		 * TaskCategoryInfo taskCategoryInfo2) { String name1 =
		 * taskCategoryInfo1.RemarkName; String name2 =
		 * taskCategoryInfo2.RemarkName; //可以按User对象的其他属性排序，只要属性支持compareTo方法
		 * return name1.compareTo(name2); } });
		 */
		ArrayList<TaskCategoryInfo> taskCategoryInfos = new ArrayList<TaskCategoryInfo>();
		// 根据勘察表重新排序任务分类项
		for (int i = 0; i < definelist.size(); i++) {
			DataCategoryDefine define = definelist.get(i);
			for (int j = 0; j < list.size(); j++) {
				TaskCategoryInfo taskCategory = list.get(j);
				if (define.CategoryID == taskCategory.CategoryID) {
					// 根据勘察表子项排序任务子项
					ArrayList<TaskDataItem> sortTaskDataItem = sortTaskItem(taskCategory.Items, define.Fields);
					taskCategory.Items = sortTaskDataItem;
					taskCategoryInfos.add(taskCategory);
				}
			}
		}
		return taskCategoryInfos;
	}

	/**
	 * 任务子项根据勘察表子项排序方法
	 * 
	 * @param itemList
	 *            任务子项列表
	 * @param Fields
	 *            勘察表子项列表
	 * @return 排序后的任务子项列表
	 */
	private ArrayList<TaskDataItem> sortTaskItem(ArrayList<TaskDataItem> list, ArrayList<DataFieldDefine> Fields) {
		// 从小到大正序:
		Collections.sort(Fields, new Comparator<DataFieldDefine>() {
			@Override
			public int compare(DataFieldDefine dataFieldDefine1, DataFieldDefine dataFieldDefine2) {
				Integer id1 = dataFieldDefine1.IOrder;
				Integer id2 = dataFieldDefine2.IOrder;
				// 可以按User对象的其他属性排序，只要属性支持compareTo方法
				return id1.compareTo(id2);
			}
		});
		ArrayList<TaskDataItem> itemLists = new ArrayList<TaskDataItem>();
		// 根据勘察表重新排序任务分类项
		for (int i = 0; i < Fields.size(); i++) {
			DataFieldDefine field = Fields.get(i);
			for (int j = 0; j < list.size(); j++) {
				TaskDataItem taskDataItem = list.get(j);
				if (field.Name.equals(taskDataItem.Name)) {
					itemLists.add(taskDataItem);
				}
			}
		}
		return itemLists;
	}

	/**
	 * 获取文件路径数量
	 */
	@Override
	public Object getItem(int index) {
		if (categories != null && !categories.isEmpty()) {
			return categories.get(index);
		}
		return null;
	}

	/**
	 * 获取当前索引
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return ITEM;
	}

	/**
	 * 获取需要显示的视图
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TaskCategoryInfo model = categories.get(position);
		if (model != null) {
			convertView = inflater.inflate(R.layout.task_listview_items, null);
			txt_name = (TextView) convertView.findViewById(R.id.edit_type_listItem_title);
			txt_number = (TextView) convertView.findViewById(R.id.edit_type_listItem_numberinfo);
			txt_required_number = (TextView) convertView.findViewById(R.id.edit_type_listItem_required_numberinfo);
			txt_content = (TextView) convertView.findViewById(R.id.edit_type_listItem_content);

			// 子项名称
			String fieldNames = "";
			// 子项总数
			Integer dataDefineTotal = 0;
			// 必填项总数
			Integer requiredCount = 0;
			// 必填项已填数量
			Integer requiredDoneCount = 0;

			// 获取子项总数 和 当前子项名称
			for (DataCategoryDefine defineCategory : defineCategories) {
				if (defineCategory.CategoryID == model.CategoryID) {
					for (DataFieldDefine field : defineCategory.Fields) {
						if (field.ShowInPhone) { // 拼接当前分类要显示的所有子项名称
							fieldNames += field.Name + ",";
							dataDefineTotal += 1;
						}
						if (field.Required) { // 统计必填分类子项总数
							requiredCount += 1;
						}
					}
					break;
				}
			}
			fieldNames = fieldNames.length() > 0 ? fieldNames.substring(0, fieldNames.length() - 1) : fieldNames;

			if (requiredCount > 0) {
				// 除必填项已完成/除必填项总数
				int needDone = 0;
				// 填充必填项的总数和必填项的已填个数
				for (DataCategoryDefine defineCategory : defineCategories) {
					if (defineCategory.CategoryID == model.CategoryID) {
						for (TaskDataItem item : model.Items) {//任务属性子项
							for (DataFieldDefine element : defineCategory.Fields) {// 勘察配置表中的属性
								if (element.Name.equals(item.Name)) {//取出配置表中的这个属性
									if (!isNullOrWhiteSpace(item.Value)) {// 这个任务属性子项的值是否为空
										if (element.Required) {
											requiredDoneCount += 1;
										} else {
											needDone += 1;
										}
									}
								}
							}
						}
						break;
					}
				}
				txt_required_number.setTextColor(EIASApplication.getInstance().getResources().getColor(R.color.red));
				txt_required_number.setText("(" + (requiredDoneCount) + "/" + (requiredCount) + ")");
				txt_number.setText("(" + (needDone) + "/" + (dataDefineTotal - requiredCount) + ")");
			} else {
				txt_required_number.setText("");
				txt_number.setText("(" + (model.DataDefineFinishCount) + "/" + (dataDefineTotal) + ")");
			}
			txt_name.setText(model.RemarkName);
			txt_content.setText(fieldNames);
		}
		return convertView;
	}

	/**
	 * 是否为null或者空格和默认值
	 * @param content
	 * @return
	 */
	private boolean isNullOrWhiteSpace(String content) {
		boolean result = content != null && !content.trim().equals("") && !content.equals(EIASApplication.DefaultNullString) && content.trim().length() > 0
				&& !content.equals(EIASApplication.DefaultDropDownListValue);
		return !result;
	}

	/**
	 * 设置选中的位置
	 * 
	 * @param position
	 */
	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

}
