package com.yunfang.eias.logic;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;

import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.TaskDataWorker;
import com.yunfang.eias.ui.TaskCategoriesFragment;
import com.yunfang.eias.ui.TaskItemsFragment;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.DialogBuilder;
import com.yunfang.framework.utils.DialogUtil;
import com.yunfang.framework.utils.ListUtil;
import com.yunfang.framework.utils.ToastUtil;

/**
 * 分类项的菜单
 * 
 * @author 贺隽
 *
 */
public class TaskInfoMenuOperaotr
{

	//{{ 变量
	/**
	 * 对话框Titile
	 */
	private String title="请选择操作";

	/**
	 * 对话框
	 */
	private DialogBuilder dialogBuilder;

	/**
	 * TaskItemsFragment
	 */
	public TaskItemsFragment itemFragment;

	/**
	 * TaskCategoriesFragment
	 */
	public TaskCategoriesFragment categoriesFragment;

	/**
	 * 分类项的菜单
	 */
	ArrayList<String> menus;

	/**
	 * 当前上下文
	 */
	private Context currentContext;
	//}}

	// {{ 列表点击选项

	/**
	 * 返回
	 */
	public final int ITEM_BREAK = 0;

	/**
	 * 编辑内容
	 */
	public final int ITEM_EDITCATEGORY = 1;
	
	/**
	 * 清空某个分类下的所有子项信息
	 */
	public final int ITEM_CLEARTASKITEMSBYCATEGORY = 2;
	
	/**
	 * 修改名称
	 */
	public final int ITEM_EDITCATEGORYNAME = 3;
	
	/**
	 * 复制目录下所有项
	 */
	public final int ITEM_COPYCATEGORY = 4;
	
	/**
	 * 粘贴
	 */
	public final int ITEM_PASTEDCATEGORY = 5;
	
	/**
	 * 粘贴到新建项
	 */
	public final int ITEM_PRSTEDNEWCATEGORY= 6;
	
	/**
	 * 删除分类项，包含其下的子项信息
	 */
	public final int ITEM_DELETECATEGORY = 7;
	// }}

	//{{ 方法

	/**
	 * 构造函数
	 * @param context：当前上下文
	 * @param status：任务列表状态
	 */
	public TaskInfoMenuOperaotr(TaskItemsFragment currentFragment){
		itemFragment = currentFragment;	
		currentContext = currentFragment.getActivity();
		dialogBuilder = new DialogBuilder(currentContext);
		dialogBuilder.setTitle(title);
	}

	/**
	 * 构造函数
	 * @param context：当前上下文
	 * @param status：任务列表状态
	 */
	public TaskInfoMenuOperaotr(TaskCategoriesFragment currentFragment){
		categoriesFragment = currentFragment;	
		currentContext = currentFragment.getActivity();
		dialogBuilder = new DialogBuilder(currentContext);
		dialogBuilder.setTitle(title);
	}


	/**
	 * 菜单响应事件
	 * @param which
	 */
	private void menuItemClick(String operate){
		switch(getOperateIndex(operate)){ 
		case ITEM_BREAK://返回
			break;
		case ITEM_EDITCATEGORY://编辑内容	
			categoriesFragment.viewModel.taskInfoActivity.getCurrentDataCategoryDefine(categoriesFragment.viewModel.position);
			categoriesFragment.viewModel.taskInfoActivity
			.changFragment(categoriesFragment.viewModel.currentDataCategoryDefine.ControlType);
			break;
		case ITEM_CLEARTASKITEMSBYCATEGORY://清空
			if(!TaskOperator.submiting(categoriesFragment.viewModel.currentTask.TaskNum)){
				clearItem();
			}else{
				ToastUtil.longShow(categoriesFragment.getActivity(), "当前任务正在提交中，将不会清空当前分类项!");
			}
			break; 
		case ITEM_EDITCATEGORYNAME://修改名称
			categoriesFragment.viewModel.taskInfoActivity.changFragment(OperatorTypeEnum.CategoryDefineNameModified);
			break;
		case ITEM_COPYCATEGORY: //复制此目录下所有项 
			categoriesFragment.viewModel.copyCategory = categoriesFragment.viewModel.currentCategory;
			break;
		case ITEM_PASTEDCATEGORY://粘贴[]中所有项
			if(!TaskOperator.submiting(categoriesFragment.viewModel.currentTask.TaskNum)){
				pastedCategory();
			}else{
				ToastUtil.longShow(categoriesFragment.getActivity(), "当前任务正在提交中，将不会粘贴到当前分类项!");
			}
			break;
		case ITEM_PRSTEDNEWCATEGORY://粘贴到新建项
			categoriesFragment.viewModel.taskInfoActivity.changFragment(OperatorTypeEnum.CategoryDefineDataCopyToNew);
			break;
		case ITEM_DELETECATEGORY://删除分类项
			if(!TaskOperator.submiting(categoriesFragment.viewModel.currentTask.TaskNum)){
				deleteCategory();
			}else{
				ToastUtil.longShow(categoriesFragment.getActivity(), "当前任务正在提交中，将不会删除当前分类项!");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 根据菜单名称取得菜单代号
	 * @param operateName
	 * @return
	 */
	private int getOperateIndex(String operateName){
		String selectItem = categoriesFragment.viewModel.currentCategory.RemarkName;
		if(operateName.equals("返回")){
			return ITEM_BREAK;
		}else if(operateName.equals("编辑内容")){
			return ITEM_EDITCATEGORY;
		}else if(operateName.contains("清空")){
			return ITEM_CLEARTASKITEMSBYCATEGORY;
		}else if(operateName.equals("修改名称")){
			return ITEM_EDITCATEGORYNAME;
		}else if(operateName.equals("删除")){
			return ITEM_DELETECATEGORY;
		}else if(operateName.equals("复制["
				+ categoriesFragment.viewModel.currentCategory.RemarkName
				+ "]中的所有项")){
			return ITEM_COPYCATEGORY;
		}
		else{
			if( categoriesFragment.viewModel.copyCategory!=null){
				if(operateName.equals("粘贴["
						+ categoriesFragment.viewModel.copyCategory.RemarkName
						+ "]所有项到[" + selectItem + "]中")){
					return ITEM_PASTEDCATEGORY;
				}else if(operateName.equals("粘贴["
						+ categoriesFragment.viewModel.copyCategory.RemarkName
						+ "]所有项到建新分类项中")){
					return ITEM_PRSTEDNEWCATEGORY;
				}
			}
			return 0;
		}
	}


	/**
	 * 粘贴分类项信息
	 */
	public void pastedCategory(){
		categoriesFragment.doSomething("粘贴分类项", categoriesFragment.TASK_PASTEDCATEGORIE);
	}

	/**
	 * 删除分类项
	 */
	public void deleteCategory(){
		// 初始化删除确定询问弹出框
		DialogUtil.showConfirmationDialog(currentContext, 
				"您确认要删除["+categoriesFragment.viewModel.currentCategory.RemarkName+"]分类项吗？", 
				new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				categoriesFragment.doSomething("删除分类项", categoriesFragment.TASK_DELETECATEGORIE);
			}
		});
	}

	/**
	 * 清空子项
	 */
	public void clearItem(){
		categoriesFragment.doSomething("清空分类项子项", categoriesFragment.TASK_CLEARITEMS);
	}

	/**
	 * 获取当前Dialog
	 * @return
	 */
	public void showDialog(){				
		menus = new ArrayList<String>();
		menus.add("返回");
		menus.add("编辑内容");
		menus.add("清空["+categoriesFragment.viewModel.currentCategory.RemarkName+"]子项所有信息");
		if (categoriesFragment.viewModel.currentDataCategoryDefine.Repeat) {
			menus.add("修改名称");
			menus.add("复制["
					+ categoriesFragment.viewModel.currentCategory.RemarkName
					+ "]中的所有项");
			if (categoriesFragment.viewModel.copyCategory != null &&
					categoriesFragment.viewModel.currentCategory.CategoryID
					==categoriesFragment.viewModel.copyCategory.CategoryID) {
				String copyNewItem = "粘贴到建新分类项中";
				copyNewItem = "粘贴["
						+ categoriesFragment.viewModel.copyCategory.RemarkName
						+ "]所有项到建新分类项中";
				// 是否可以粘贴新建项
				if(isCanCreateOrDelete(true)){
					menus.add(copyNewItem);
				}
				String selectItem = categoriesFragment.viewModel.currentCategory.RemarkName;
				if (categoriesFragment.viewModel.copyCategory != null
						&& !(categoriesFragment.viewModel.currentCategory.ID
								==categoriesFragment.viewModel.copyCategory.ID)) {
					String copyItem = "粘贴";
					copyItem = "粘贴["
							+ categoriesFragment.viewModel.copyCategory.RemarkName
							+ "]所有项到[" + selectItem + "]中";
					menus.add(copyItem);
				}
			}
		}
		if(isCanCreateOrDelete(false)){
			menus.add("删除");
		}
		String[] menuItems = (String[]) menus.toArray(new String[menus.size()]);		
		dialogBuilder.setItems(menuItems,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				menuItemClick(menus.get(which));
			}
		});	
		dialogBuilder.create().show();		
	}

	/**
	 * 检测该项是否可以删除或检测是否可以创建
	 * @param isCreateType    true：创建  false：删除
	 * @return
	 */
	private Boolean isCanCreateOrDelete(Boolean isCreateType){
		Boolean result = false;
		// 取得可以删除的任务分类项信息
		ArrayList<DataCategoryDefine> canBeDeleteCategory = getCanBeDeleteCategory(isCreateType);
		for(DataCategoryDefine dataCategoryDefine : canBeDeleteCategory){
			// 若可删除项中包含该ID项
			if (dataCategoryDefine.CategoryID == categoriesFragment.viewModel.currentCategory.CategoryID) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 取得可以删除或可以创建的任务分类项信息
	 * @param isCreateType    true：创建  false：删除
	 * @return
	 */
	private ArrayList<DataCategoryDefine> getCanBeDeleteCategory(Boolean isCreateType){
		ArrayList<DataCategoryDefine> result = new ArrayList<DataCategoryDefine>();
		Integer identityId = categoriesFragment.viewModel.currentTask.ID;
		Integer taskId = categoriesFragment.viewModel.currentTask.TaskID;
		Boolean isNew = categoriesFragment.viewModel.currentTask.IsNew;
		ResultInfo<TaskInfo> taskInfo = TaskDataWorker.queryTaskInfo(
				isNew ? identityId : taskId, isNew);
		if (taskInfo.Data != null) {
			TaskInfo taskinfo = taskInfo.Data;
			ResultInfo<ArrayList<DataCategoryDefine>> tempCategoryDefine = TaskOperator
					.getCanBeAddOrDeleteCategories(taskinfo,isCreateType);
			if (tempCategoryDefine.Data!=null&&ListUtil.hasData(tempCategoryDefine.Data)) {
				result.addAll(tempCategoryDefine.Data);
			}
		}
		return result;
	}

	public void setDialog(int index)
	{
		dialogBuilder.getView(index).setEnabled(false);
	}

	//}}
}
