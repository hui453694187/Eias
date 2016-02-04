/**
 * 
 */
package com.yunfang.eias.viewmodel;

import java.util.ArrayList;

import android.view.View;
import android.widget.ArrayAdapter;

import com.yunfang.eias.enumObj.OperatorTypeEnum;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.ui.TaskInfoActivity;

/**
 * @author Administrator
 * @author sen
 */
public class TaskCategoriesViewModel {
	// {{ 相关变量

		/**
		 * Activity对象
		 */
		public TaskInfoActivity taskInfoActivity;

		/**
		 * 当前Fragment视图
		 */
		public View mView;

		/**
		 * 远程服务端ID值
		 */
		public int identityId;

		/**
		 * Android端ID值
		 */
		public int taskId;

		/**
		 * 判断是否为用户在安卓端创建
		 */
		public Boolean isCreatedByUesr;

		/**
		 *  Android端中此任务分类对应的ID值
		 */
		public int categoryId;
		
		/**
		 * 任务分类项的唯一标示
		 */
		public int categoryIdentityId;
		
		/**
		 * 复制项的Android端中此任务对应的ID值
		 */
		public int copycategoryId;

		/**
		 * 复制项的分类项ID
		 */
		public int copyidentityId;
		
		/**
		 * 任务下的分类项名称
		 */
		public String remarkName;

		/**
		 * 完整勘察表的分类项名称
		 */
		public String name;

		/*
		 * 存储可以添加的分类项
		 */
		public String[] category = null;

		/**
		 * 默认选中的下拉框值
		 */
		public int selectCategoryInfoIndex=0;

		/**
		 * 修改的名称分类项信息
		 */
		public String categoryDefines = null;

		/*
		 * 完整的分类项
		 */
		public ArrayList<DataCategoryDefine> dataCategoryDefines = null;

		/*
		 * 存储可以添加的分类项数据适配
		 */
		public ArrayAdapter<String> categoryAdapter = null;	

		/**
		 * 需要操作的类型
		 */
		public OperatorTypeEnum  operation;

		// }}	
}
