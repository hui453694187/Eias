package com.yunfang.eias.viewmodel;

import android.widget.Button;
import android.widget.TextView;

import com.yunfang.eias.logic.AppHeader;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.ui.HomeActivity;
import com.yunfang.framework.model.ViewModelBase;

/**
 * 对应HomeActivty的视图
 * 
 * @author gorson
 * 
 */
public class TaskImportGuideViewModel extends ViewModelBase {

	// {{ 变量

	/**
	 * 压缩任务包对象
	 */
	public TaskInfo zipTaskInfo = new TaskInfo();
	
	/**
	 * 点击导入的任务对象
	 */
	public TaskInfo androidTaskInfo = new TaskInfo();

	/**
	 * 任务编号
	 */
	public String taskNum = "";
	
	/**
	 * 任务地址
	 */
	public String targetAddress = "";
	
	/**
	 * 小区名称
	 */
	public String residentialArea = "";
	
	/**
	 * 用途
	 */
	public String targetType = "";
	
	/**
	 * 使用勘察配置名称
	 */
	public String dataDefineName = "";
	
	/**
	 * 使用勘察配置编号 （服务端编号）
	 */
	public int DDID = 0;
	// }}

	// {{ 控件

	/**
	 * 任务编号
	 */
	public TextView  task_import_guide_num;
	
	/**
	 * 地址
	 */
	public TextView  task_import_guide_address;
	
	/**
	 * 使用的勘察配置表名称
	 */
	public TextView  task_import_guide_ddid;
	
	/**
	 * 用途
	 */
	public TextView  task_import_guide_use;
	
	/**
	 * 小区名称
	 */
	public TextView  task_import_guide_name;
		
	/**
	 * 选择文件完整路径
	 */
	public TextView  task_import_guide_fullname;
	
	/**
	 * 选择按钮
	 */
	public Button  task_import_guide_select;
	
	/**
	 * 导入
	 */
	public Button  task_import_guide_import;

	/**
	 * 主菜单
	 */
	public AppHeader appHeader;

	/**
	 * HomeActivity
	 */
	public HomeActivity homeActivity;
	// }}

}
