package com.yunfang.eias.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yunfang.eias.enumObj.TaskCreateType;
import com.yunfang.eias.enumObj.TaskStatus;
import com.yunfang.eias.enumObj.UrgentStatusEnum;
import com.yunfang.eias.model.DataCategoryDefine;
import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.model.DataFieldDefine;
import com.yunfang.eias.model.TaskCategoryInfo;
import com.yunfang.eias.model.TaskInfo;
import com.yunfang.eias.tables.DataDefineWorker;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.utils.StringUtil;

/**
 * 
 * 项目名称：外业采集项目 类名称：TaskInfoDTO 类描述：勘察任务信息 用于给后台传递的参数对象 创建人：贺隽 创建时间：2014-6-25
 * 
 * @version 1.0.0.1
 */
public class TaskInfoDTO {

	// {{相关的属性

	/**
	 * 编号
	 */
	public long ID;

	/**
	 * 配置
	 */
	public long DDID;

	/** 9/22 新字段，任务对应的公司ID */
	public int CompanyId;
	
	/**
	 * 配置版本号
	 */
	public int DataDefineVersion;

	/**
	 * 创建时间
	 */
	public String CreatedDate;

	/**
	 * 最后修改时间
	 */
	public String ModifyDate;

	/**
	 * 任务编号
	 */
	public String TaskNum;

	/**
	 * 备注
	 */
	public String Remark="";
	
	/** 任务备注 */
	public String TaskRemark="";

	/**暂停原因*/
	public String Reason="";
	
	/**
	 * 领取时间
	 */
	public String ReceiveDate;

	/**
	 * 完成时间
	 */
	public String DoneDate;

	/**
	 * 状态
	 */
	public int Status;

	/**
	 * 估价对象编号
	 */
	public String TargetNumber;

	/**
	 * 完成数量
	 */
	public int Count;

	/**
	 * 紧急程度
	 */
	public String IsUrgent;

	/**
	 * 总分类项数量
	 */
	public int Total;

	/**
	 * 地址
	 */
	public String TargetAddress;

	/**
	 * 业主
	 */
	public String Owner;

	/**
	 * 业主电话
	 */
	public String OwnerTelePhone;

	/**
	 * 小区名称
	 */
	public String ResidentialArea;

	/**
	 * 楼主名称
	 */
	public String Building;

	/**
	 * 楼层
	 */
	public String Floor;

	/**
	 * 物业类型
	 */
	public String TargetName;

	/**
	 * 用途
	 */
	public String TargetType;

	/**
	 * 建筑面积
	 */
	public String TargetArea;

	/**
	 * 委托人单位
	 */
	public String ClientUnit;

	/**
	 * 委托人部门
	 */
	public String ClientDepartment;

	/**
	 * 委托人名称
	 */
	public String ClientName;

	/**
	 * 委托人联系电话
	 */
	public String ClientTelephone;

	/**
	 * 用户名
	 */
	public String User;

	/**
	 * 创建方式，用户自建、系统界面创建、系统统一创建、第三方系统创建等
	 */
	public int CreateType;

	/**
	 * 收费金额
	 */
	public String Fee;

	/**
	 * 收据号
	 */
	public String ReceiptNo;

	/**
	 * 预约日期
	 */
	public String BookedDate;

	/**
	 * 预约时间
	 */
	public String BookedTime;

	/**
	 * 联系人
	 */
	public String ContactPerson;

	/**
	 * 联系人电话
	 */
	public String ContactTel;

	/**
	 * 备注
	 */
	public String BookedRemark;

	/**
	 * 内业报告是否完成 2015-6-30
	 */
	public Boolean InworkReportFinish;

	/**
	 * 内业报告完成时间 2015-6-30
	 */
	public String InworkReportFinishDate;

	/**
	 * 是否有资源文件
	 */
	public Boolean HasResource;

	/**
	 * 加急金额 2015-7-24
	 */
	public double UrgentFee;

	/**
	 * 加急金额 2015-8-5
	 */
	public double AdjustFee;

	/**
	 * 加急金额 2015-7-24
	 */
	public double LiveSearchCharge;
	
	/***
	 * 2016-1-15
	 * 客户来源字段
	 */
	public String CustomerSource="";

	/**
	 * 任务下的分类信息
	 */
	public List<TaskCategoryInfoDTO> Categories = new ArrayList<TaskCategoryInfoDTO>();

	// }}

	// {{构造函数
	/**
	 * 无参构造，设置默认值
	 * */
	public TaskInfoDTO() {
		super();
	}

	/**
	 * 有参数构造
	 * */
	public TaskInfoDTO(TaskInfo task) {
		super();
		TaskNum = task.TaskNum;
		ID = task.TaskID;
		DDID = task.DDID;
		CompanyId=task.CompanyId;
		ReceiveDate = task.ReceiveDate;
		DoneDate = task.DoneDate;
		// Status = String.valueOf(task.Status.getIndex());
		Status = task.Status.getIndex();
		TargetNumber = task.TargetNumber;
		IsUrgent = String.valueOf(task.UrgentStatus.getIndex());
		// IsUrgent = task.UrgentStatus.getIndex();
		TargetAddress = task.TargetAddress;
		Owner = task.Owner;
		OwnerTelePhone = task.OwnerTel;
		ResidentialArea = task.ResidentialArea;
		Building = task.Building;
		Floor = task.Floor;
		TargetName = task.TargetName;
		TargetType = task.TargetType;
		TargetArea = task.TargetArea;
		ClientUnit = task.ClientUnit;
		ClientDepartment = task.ClientDep;
		ClientName = task.ClientName;
		ClientTelephone = task.ClientTel;
		User = task.User;
		Fee = task.Fee;
		ReceiptNo = task.ReceiptNo;
		CreatedDate = task.CreatedDate;
		Reason = task.Remark;
		TaskRemark=task.TaskRemark;
		DataDefineVersion = task.DataDefineVersion;
		CreateType = task.CreateType.getIndex();
		// 新增字段
		BookedDate = task.BookedDate;
		BookedTime = task.BookedTime;
		ContactPerson = task.ContactPerson;
		ContactTel = task.ContactTel;
		BookedRemark = task.BookedRemark;
		// 内业报告是否完成
		InworkReportFinish = task.InworkReportFinish;
		// 内业报告完成日期
		InworkReportFinishDate = task.InworkReportFinishDate;
		// 是否有资源文件
		HasResource = task.HasResource;
		// 加急金额
		UrgentFee = task.UrgentFee;
		// 应收费用
		AdjustFee = task.AdjustFee;
		// 预收费用
		LiveSearchCharge = task.LiveSearchCharge;
		//客户来源
		this.CustomerSource=task.CustomerSource;
		if (task.Categories != null) {
			for (TaskCategoryInfo category : task.Categories) {
				Categories.add(new TaskCategoryInfoDTO(category));
			}
		}

		// 获取勘察数据信息
		ResultInfo<DataDefine> dataDefine = DataDefineWorker.getCompleteDataDefine((int) DDID);
		//
		// 获的分类项 类型 和 子项输入类型
		if (dataDefine.Data != null) {
			for (DataCategoryDefine categoryDefine : dataDefine.Data.Categories) {
				for (TaskCategoryInfoDTO category : Categories) {
					if (categoryDefine.CategoryID == category.CategoryID) {
						category.CategoryType = categoryDefine.ControlType.getName();
						for (DataFieldDefine field : categoryDefine.Fields) {
							for (TaskDataItemDTO item : category.Items) {
								if (item.Name.equals(field.Name)) {
									item.ItemType = field.ItemType.getName();
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 把数据对象转换成模型对象
	 * 
	 * @param dto
	 * @return
	 */
	public TaskInfo getTaskInfo() {
		TaskInfo result = new TaskInfo();
		result.TaskNum = this.TaskNum;
		result.TaskID = (int) this.ID;
		result.DDID = (int) this.DDID;
		result.CompanyId=this.CompanyId;
		result.ReceiveDate = this.ReceiveDate;
		result.DoneDate = this.DoneDate;
		// result.Status =
		// TaskStatus.getEnumByValue(Integer.parseInt(this.Status));

		result.Status = TaskStatus.getEnumByValue(this.Status);
		result.TargetNumber = this.TargetNumber;
		switch (this.IsUrgent) {
		case "1":
		case "紧急":
			result.UrgentStatus = UrgentStatusEnum.getEnumByName("紧急");
			break;
		default:
			result.UrgentStatus = UrgentStatusEnum.getEnumByName("常规");
			break;
		}
		// result.UrgentStatus = UrgentStatusEnum.getEnumByValue(this.IsUrgent);
		result.TargetAddress = this.TargetAddress;
		result.Owner = this.Owner;
		result.OwnerTel = this.OwnerTelePhone;
		result.ResidentialArea = this.ResidentialArea;
		result.Building = this.Building;
		result.Floor = this.Floor;
		result.TargetName = this.TargetName;
		result.TargetType = this.TargetType;
		result.TargetArea = this.TargetArea;
		result.ClientUnit = this.ClientUnit;
		result.ClientDep = this.ClientDepartment;
		result.ClientName = this.ClientName;
		result.ClientTel = this.ClientTelephone;
		result.User = this.User;
		result.Fee = this.Fee;
		result.ReceiptNo = this.ReceiptNo;
		result.CreatedDate = this.CreatedDate;
		result.Remark = this.Reason;
		result.TaskRemark=this.TaskRemark;
		result.DataDefineVersion = this.DataDefineVersion;
		result.CreateType = TaskCreateType.getEnumByValue(this.CreateType);
		for (TaskCategoryInfoDTO dto : this.Categories) {
			result.Categories.add(dto.getTaskCategoryInfo());
		}
		// 新增字段
		result.BookedDate = this.BookedDate;
		result.BookedTime = this.BookedTime;
		result.ContactPerson = this.ContactPerson;
		result.ContactTel = this.ContactTel;
		result.BookedRemark = this.BookedRemark;

		// 内业报告是否完成
		result.InworkReportFinish = this.InworkReportFinish;
		result.InworkReportFinishDate = this.InworkReportFinishDate;

		// 是否有资源文件
		result.HasResource = this.HasResource;

		result.UrgentFee = this.UrgentFee;
		result.AdjustFee = this.AdjustFee;
		result.LiveSearchCharge = this.LiveSearchCharge;
		result.CustomerSource=this.CustomerSource;//客户来源

		return result;
	}

	/**
	 * 构建对象
	 * 
	 * @param obj
	 * @throws JSONException
	 */
	public TaskInfoDTO(JSONObject obj) throws JSONException {
		TaskNum = obj.optString("TaskNum");
		ID = obj.optInt("ID");
		DDID = obj.optInt("DDID");
		CompanyId=obj.optInt("CompanyId");
		ReceiveDate = obj.optString("ReceiveDate");
		DoneDate = obj.optString("DoneDate");
		Status = obj.optInt("Status");
		TargetNumber = obj.optString("TargetNumber");
		IsUrgent = obj.optString("UrgentStatus");
		if (IsUrgent == null || IsUrgent == "" || IsUrgent.isEmpty()) {
			IsUrgent = obj.optString("IsUrgent");
		}
		TargetAddress = obj.optString("TargetAddress");
		Owner = obj.optString("Owner");
		OwnerTelePhone = obj.optString("OwnerTelePhone");
		ResidentialArea = obj.optString("ResidentialArea");
		Building = obj.optString("Building");
		Floor = obj.optString("Floor");
		TargetName = obj.optString("TargetName");
		TargetType = obj.optString("TargetType");
		TargetArea = obj.optString("TargetArea");
		ClientUnit = obj.optString("ClientUnit");
		ClientDepartment = obj.optString("ClientDepartment");
		ClientName = obj.optString("ClientName");
		ClientTelephone = obj.optString("ClientTelephone");
		User = obj.optString("User");
		Fee = obj.optString("Fee");
		ReceiptNo = obj.optString("ReceiptNo");
		CreatedDate = obj.optString("CreatedDate");
		Remark = obj.optString("Remark");
		Reason = obj.optString("Reason");
		TaskRemark=obj.optString("TaskRemark");
		DataDefineVersion = obj.optInt("DataDefineVersion");
		if (obj.has("Categories")) {
			String categoriesStr = obj.getString("Categories");
			if (categoriesStr != null && categoriesStr.length() > 0) {
				ArrayList<TaskCategoryInfoDTO> tempCategories = new ArrayList<TaskCategoryInfoDTO>();
				JSONArray arr = new JSONArray(categoriesStr);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					tempCategories.add(new TaskCategoryInfoDTO(temp));
				}
				Categories = tempCategories;
			}
		}
		// 新增字段
		BookedDate = obj.optString("BookedDate");
		BookedTime = obj.optString("BookedTime");
		ContactPerson = obj.optString("ContactPerson");
		ContactTel = obj.optString("ContactTel");
		BookedRemark = obj.optString("BookedRemark");
		// 内业报告是否完成
		InworkReportFinish = StringUtil.parseBoolean(obj.optString("InworkReportFinish"));
		InworkReportFinishDate = obj.optString("InworkReportFinishDate");
		// 是否有资源文件
		HasResource = StringUtil.parseBoolean(obj.optString("HasResource"));

		UrgentFee = obj.optDouble("UrgentFee");
		AdjustFee = obj.optDouble("AdjustFee");
		LiveSearchCharge = obj.optDouble("LiveSearchCharge");
		this.CustomerSource=obj.optString("CustomerSource");
	}

	// }}
}
