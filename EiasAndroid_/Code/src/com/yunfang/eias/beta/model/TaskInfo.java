package com.yunfang.eias.beta.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.dto.TaskCategoryInfoDTO;
import com.yunfang.eias.beta.dto.TaskInfoDTO;
import com.yunfang.eias.beta.enumObj.TaskCreateType;
import com.yunfang.eias.beta.enumObj.TaskStatus;
import com.yunfang.eias.beta.enumObj.TaskUploadStatusEnum;
import com.yunfang.eias.beta.enumObj.UrgentStatusEnum;
import com.yunfang.framework.db.TableWorkerBase;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.StringUtil;

/**
 * 
 * 项目名称：WaiCai 类名称：TaskDataItem 类描述：勘察任务信息表对应的实体类 创建人：lihc 创建时间：2014-4-9
 * 上午10:42:59
 * 
 * @version
 */
@SuppressWarnings("serial")
public class TaskInfo extends TableWorkerBase {

	// {{相关的属性
	/**
	 * 自增ID
	 * */
	public int ID;

	/**
	 * 任务编号
	 * */
	public String TaskNum;

	/**
	 * 任务ID，后台管理系统中对应任务编号的任务ID
	 * */
	public int TaskID;

	/**
	 * 任务勘察类型
	 * */
	public int DDID;

	/** 公司ID */
	public int CompanyId;

	/**
	 * 领取时间（自建任务时，需要客户端自动填入当前时间）
	 * */
	public String ReceiveDate;

	/**
	 * 完成时间
	 * */
	public String DoneDate;

	/**
	 * 项目状态TaskStatus:待领取=0，待提交=1，已完成=2（自建任务时，状态默认为待提交，状态值为1）(枚举类型)
	 * */
	public TaskStatus Status;

	/**
	 * 估价物编号
	 * */
	public String TargetNumber;

	/**
	 * 紧急程度Normal=0，Urgent=1(枚举类型)
	 * */
	public UrgentStatusEnum UrgentStatus;

	/**
	 * 地址
	 * */
	public String TargetAddress;

	/**
	 * 业主
	 * */
	public String Owner;

	/**
	 * 业主电话
	 * */
	public String OwnerTel;

	/**
	 * 小区名称
	 * */
	public String ResidentialArea;

	/**
	 * 楼栋名称
	 * */
	public String Building;

	/**
	 * 所在楼层
	 * */
	public String Floor;

	/**
	 * 目标名称
	 * */
	public String TargetName;

	/**
	 * 用途
	 * */
	public String TargetType;

	/**
	 * 建筑面积
	 * */
	public String TargetArea;

	/**
	 * 委托人单位
	 * */
	public String ClientUnit;

	/**
	 * 委托人部门
	 * */
	public String ClientDep;

	/**
	 * 委托人名称
	 * */
	public String ClientName;

	/**
	 * 委托人联系电话
	 * */
	public String ClientTel;

	/**
	 * 领取人
	 * */
	public String User;

	/**
	 * 收费金额
	 * */
	public String Fee;

	/**
	 * 收据号
	 * */
	public String ReceiptNo;

	/**
	 * 创建时间
	 * */
	public String CreatedDate;

	/**
	 * 暂停标注
	 * */
	public String Remark;
	
	/** 任务备注 */
	public String TaskRemark;

	/**
	 * 是否为新建，标记是否为用户自建任务，0为非自建，1为自建(枚举类型)
	 * */
	public Boolean IsNew;

	/**
	 * 需要的勘察表分类信息版本号
	 */
	public int DataDefineVersion;

	/**
	 * 创建方式，用户自建=0、系统界面创建=1、批量创建=2、第三方系统创建=3(枚举类型)
	 * */
	public TaskCreateType CreateType;

	/**
	 * 上传状态：UnSumbit:未提交=0,提交等待中=1，提交中=2,已提交=3,提交失败=4(枚举类型)
	 * */
	public TaskUploadStatusEnum UploadStatusEnum;

	/**
	 * 上传次数，默认值为：0
	 * */
	public int UploadTimes;

	/**
	 * 最后上传时间(默认当前系统的时间)
	 * */
	public String LatestUploadDate;

	// 关于预约的新增字段begin

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
	 * 预约备注
	 */
	public String BookedRemark;

	// 关于预约的新增字段end

	// 内业报告是否完成 start 2015-6-30

	/**
	 * 内业报告是否完成
	 */
	public Boolean InworkReportFinish;

	/**
	 * 内业报告完成时间
	 */
	public String InworkReportFinishDate;

	// 内业报告是否完成 end 2015-6-30

	// 是否包含资源文件 start 2015-7-2

	/**
	 * 内业报告是否完成
	 */
	public Boolean HasResource;

	// 是否包含资源文件 end 2015-7-2

	// 加急费用 start 2015-7-24

	/**
	 * 加急费用
	 */
	public double UrgentFee;

	// 加急费用 end 2015-7-24

	// 2015-8-5 strat

	/**
	 * 应收费用
	 */
	public double AdjustFee;

	/**
	 * 预收费用
	 */
	public double LiveSearchCharge;

	// 2015-8-5 end

	/**
	 * 是否选中 这个不在数据库中存在 勾选使用
	 */
	public Boolean isChecked = false;

	/**
	 * 任务下的分类信息
	 */
	public ArrayList<TaskCategoryInfo> Categories = new ArrayList<TaskCategoryInfo>();

	// }}

	// {{构造函数
	/**
	 * 无参构造，设置默认值
	 * */
	public TaskInfo() {
		super();
		TaskNum = "";
		TaskID = -1;
		DDID = -1;
		CompanyId = 0;
		ReceiveDate = DateTimeUtil.getCurrentTime();// 当前时间
		DoneDate = null;// 当前时间
		Status = TaskStatus.Todo;
		TargetNumber = "";
		UrgentStatus = UrgentStatusEnum.Normal;
		TargetAddress = "";
		Owner = "";
		OwnerTel = "";
		ResidentialArea = "";
		Building = "";
		Floor = "";
		TargetName = "";
		TargetType = "";
		TargetArea = "";
		ClientUnit = "";
		ClientDep = "";
		ClientName = "";
		ClientTel = "";
		if (EIASApplication.getCurrentUser() != null) {
			User = EIASApplication.getCurrentUser().Name;
		}
		Fee = "";
		ReceiptNo = "";
		CreatedDate = DateTimeUtil.getCurrentTime();
		Remark = "";
		TaskRemark="";
		IsNew = true;
		CreateType = TaskCreateType.CreatedByUser;
		UploadStatusEnum = TaskUploadStatusEnum.Submitwating;
		UploadTimes = 0;
		DataDefineVersion = -1;
		LatestUploadDate = null;
		ContactTel = "";
		ContactPerson = "";
		InworkReportFinish = false;
		InworkReportFinishDate = "";
		HasResource = false;
		UrgentFee = 0d;
		AdjustFee = 0d;
		LiveSearchCharge = 0d;
	}

	/**
	 * 构建对象
	 * 
	 * @param obj
	 * @throws JSONException
	 */
	public TaskInfo(JSONObject obj) throws JSONException {
		TaskNum = obj.optString("TaskNum");
		TaskID = obj.optInt("ID");
		DDID = obj.optInt("DDID");
		CompanyId = obj.optInt("CompanyId");
		ReceiveDate = obj.optString("ReceiveDate");
		DoneDate = obj.optString("DoneDate");
		Status = TaskStatus.getEnumByValue(obj.optInt("Status"));
		TargetNumber = obj.optString("TargetNumber");
		String tempUrgent = obj.optString("UrgentStatus");
		if (tempUrgent == null || tempUrgent == "" || tempUrgent.isEmpty()) {
			tempUrgent = obj.optString("IsUrgent");
		}
		switch (tempUrgent) {
		case "1":
		case "紧急":
			UrgentStatus = UrgentStatusEnum.getEnumByName("紧急");
			break;
		default:
			UrgentStatus = UrgentStatusEnum.getEnumByName("一般");
			break;
		}
		// UrgentStatus =
		// UrgentStatusEnum.getEnumByValue(obj.optInt("UrgentStatus"));
		TargetAddress = obj.optString("TargetAddress");
		Owner = obj.optString("Owner");
		OwnerTel = obj.optString("OwnerTelePhone");
		ResidentialArea = obj.optString("ResidentialArea");
		Building = obj.optString("Building");
		Floor = obj.optString("Floor");
		TargetName = obj.optString("TargetName");
		TargetType = obj.optString("TargetType");
		TargetArea = obj.optString("TargetArea");
		ClientUnit = obj.optString("ClientUnit");
		ClientDep = obj.optString("ClientDepartment");
		ClientName = obj.optString("ClientName");
		ClientTel = obj.optString("ClientTelephone");
		User = obj.optString("User");
		Fee = obj.optString("Fee");
		ReceiptNo = obj.optString("ReceiptNo");
		CreatedDate = obj.optString("CreatedDate");
		Remark = obj.optString("Remark");
		TaskRemark=obj.optString("TaskRemark");
		IsNew = false;// BooleanEnum.getEnumByName(obj.optBoolean("IsNew")?"是":"否");
		DataDefineVersion = obj.optInt("DataDefineVersion");
		UploadTimes = obj.optInt("UploadTimes");
		LatestUploadDate = obj.optString("LatestUploadDate");

		// new
		ContactPerson = obj.optString("ContactPerson");
		ContactTel = obj.optString("ContactTel");
		CreateType = TaskCreateType.getEnumByValue(obj.optInt("CreateType"));

		// 内业报告相关属性 2015-6-30
		InworkReportFinish = obj.optBoolean("InworkReportFinish");
		InworkReportFinishDate = obj.optString("InworkReportFinishDate");

		// 资源相关属性 2015-7-2
		HasResource = obj.optBoolean("HasResource");

		// 加急金额 2015-7-24
		UrgentFee = obj.optDouble("UrgentFee");

		// 2015-8-5
		AdjustFee = obj.optDouble("AdjustFee");
		LiveSearchCharge = obj.optDouble("LiveSearchCharge");

		if (obj.has("Categories")) {
			String categoriesStr = obj.getString("Categories");
			if (categoriesStr != null && categoriesStr.length() > 0) {
				ArrayList<TaskCategoryInfo> tempCategories = new ArrayList<TaskCategoryInfo>();
				JSONArray arr = new JSONArray(categoriesStr);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					tempCategories.add(new TaskCategoryInfo(temp));
				}
				Categories = tempCategories;
			}
		}
	}

	// }}

	// {{ getContentValues 获取勘察任务信息表（TaskInfo）要插入的参数
	/**
	 * 将勘察任务信息表所有的参数封装成ContentValues
	 * */
	private ContentValues taskInfo_values;

	/**
	 * 获取勘察任务信息表（TaskInfo）要插入的参数
	 * */
	@Override
	public ContentValues getContentValues() {

		taskInfo_values = new ContentValues();
		taskInfo_values.put("TaskNum", TaskNum);
		taskInfo_values.put("TaskID", TaskID);
		taskInfo_values.put("DDID", DDID);
		taskInfo_values.put("CompanyId", CompanyId);
		taskInfo_values.put("ReceiveDate", ReceiveDate);
		taskInfo_values.put("DoneDate", DoneDate);
		taskInfo_values.put("Status", Status.getIndex());
		taskInfo_values.put("TargetNumber", TargetNumber);
		taskInfo_values.put("UrgentStatus", UrgentStatus.getIndex());
		taskInfo_values.put("TargetAddress", TargetAddress);
		taskInfo_values.put("Owner", Owner);
		taskInfo_values.put("OwnerTel", OwnerTel);
		taskInfo_values.put("ResidentialArea", ResidentialArea);
		taskInfo_values.put("Building", Building);
		taskInfo_values.put("Floor", Floor);
		taskInfo_values.put("TargetName", TargetName);
		taskInfo_values.put("TargetType", TargetType);
		taskInfo_values.put("TargetArea", TargetArea);
		taskInfo_values.put("ClientUnit", ClientUnit);
		taskInfo_values.put("ClientDep", ClientDep);
		taskInfo_values.put("ClientName", ClientName);
		taskInfo_values.put("ClientTel", ClientTel);
		taskInfo_values.put("User", User);
		taskInfo_values.put("Fee", Fee);
		taskInfo_values.put("ReceiptNo", ReceiptNo);
		taskInfo_values.put("CreatedDate", CreatedDate);
		taskInfo_values.put("IsNew", IsNew);
		taskInfo_values.put("DataDefineVersion", DataDefineVersion);
		taskInfo_values.put("CreateType", CreateType.getIndex());
		if (UploadStatusEnum != null) {
			taskInfo_values.put("UploadStatus", UploadStatusEnum.getIndex());
		}
		taskInfo_values.put("UploadTimes", UploadTimes);
		taskInfo_values.put("LatestUploadDate", LatestUploadDate);
		taskInfo_values.put("BookedDate", BookedDate);
		taskInfo_values.put("BookedTime", BookedTime);
		taskInfo_values.put("ContactPerson", ContactPerson);
		taskInfo_values.put("ContactTel", ContactTel);
		taskInfo_values.put("BookedRemark", BookedRemark);
		taskInfo_values.put("Remark", Remark);
		taskInfo_values.put("TaskRemark",TaskRemark);//任务备注 20151028
		// 内业报告是否完成 2015-6-30
		taskInfo_values.put("InworkReportFinish", InworkReportFinish);
		taskInfo_values.put("InworkReportFinishDate", InworkReportFinishDate);
		// 资源相关 2015-7-2
		taskInfo_values.put("HasResource", HasResource);
		// 加急收费金额 2015-7-24
		taskInfo_values.put("UrgentFee", UrgentFee);
		// 2015-8-5
		taskInfo_values.put("AdjustFee", AdjustFee);
		taskInfo_values.put("LiveSearchCharge", LiveSearchCharge);
		return taskInfo_values;
	}

	// }}

	@Override
	public String getTableName() {
		return "TaskInfo";
	}

	@Override
	public String toString() {
		return "TaskInfo [ID=" + ID + ", TaskNum=" + TaskNum + ", TaskID=" + TaskID + ", DDID=" + DDID + ", ReceiveDate=" + ReceiveDate + ", DoneDate=" + DoneDate + ", Status=" + Status
				+ "CompanyId=" + CompanyId + ", TargetNumber=" + TargetNumber + ", UrgentStatus=" + UrgentStatus + ", TargetAddress=" + TargetAddress + ", Owner=" + Owner + ", OwnerTel=" + OwnerTel
				+ ", ResidentialArea=" + ResidentialArea + ", Building=" + Building + ", Floor=" + Floor + ", TargetName=" + TargetName + ", TargetType=" + TargetType + ", TargetArea=" + TargetArea
				+ ", ClientUnit=" + ClientUnit + ", ClientDep=" + ClientDep + ", ClientName=" + ClientName + ", ClientTel=" + ClientTel + ", User=" + User + ", Fee=" + Fee + ", ReceiptNo="
				+ ReceiptNo + ", CreatedDate=" + CreatedDate + ", Remark=" + Remark +",TaskRemark="+TaskRemark+ ", IsNew=" + IsNew + ", CategoryVersion=" + DataDefineVersion + ", CreateType=" + CreateType + ", UploadStatus="
				+ UploadStatusEnum + ", UploadTimes=" + UploadTimes + ", LatestUploadDate=" + LatestUploadDate
				// 新增字段
				+ ", BookedDate=" + BookedDate + ", BookedTime=" + BookedTime + ", ContactPerson=" + ContactPerson + ", ContactTel=" + ContactTel + ", BookedRemark=" + BookedRemark
				// 内业报告是否完成 start 2015-6-30
				+ ", InworkReportFinish=" + InworkReportFinish + ", InworkReportFinishDate=" + InworkReportFinishDate
				// 资源相关 2015-7-2
				+ ", HasResource=" + HasResource
				// 加急金额 2015-7-24
				+ ", UrgentFee=" + UrgentFee
				// 2015-8-5
				+ ", AdjustFee=" + AdjustFee + ", LiveSearchCharge=" + LiveSearchCharge + "]";

	}

	@Override
	public String getPrimaryKeyName() {
		return "ID";
	}

	@Override
	public void setValueByCursor(Cursor cursor) {
		this.ID = cursor.getInt(cursor.getColumnIndex("ID"));
		this.TaskNum = cursor.getString(cursor.getColumnIndex("TaskNum"));
		this.TaskID = cursor.getInt(cursor.getColumnIndex("TaskID"));
		this.DDID = cursor.getInt(cursor.getColumnIndex("DDID"));
		//2015-9-22 公司ID
		this.CompanyId = cursor.getInt(cursor.getColumnIndex("CompanyId"));
		this.ReceiveDate = cursor.getString(cursor.getColumnIndex("ReceiveDate"));
		this.DoneDate = cursor.getString(cursor.getColumnIndex("DoneDate"));
		this.Status = TaskStatus.getEnumByValue((cursor.getInt(cursor.getColumnIndex("Status"))));
		this.TargetNumber = cursor.getString(cursor.getColumnIndex("TargetNumber"));
		this.UrgentStatus = UrgentStatusEnum.getEnumByValue(cursor.getInt(cursor.getColumnIndex("UrgentStatus")));
		this.TargetAddress = cursor.getString(cursor.getColumnIndex("TargetAddress"));
		this.Owner = cursor.getString(cursor.getColumnIndex("Owner"));
		this.OwnerTel = cursor.getString(cursor.getColumnIndex("OwnerTel"));
		this.ResidentialArea = cursor.getString(cursor.getColumnIndex("ResidentialArea"));
		this.Building = cursor.getString(cursor.getColumnIndex("Building"));
		this.Floor = cursor.getString(cursor.getColumnIndex("Floor"));
		this.TargetName = cursor.getString(cursor.getColumnIndex("TargetName"));
		this.TargetType = cursor.getString(cursor.getColumnIndex("TargetType"));
		this.TargetArea = cursor.getString(cursor.getColumnIndex("TargetArea"));
		this.ClientUnit = cursor.getString(cursor.getColumnIndex("ClientUnit"));
		this.ClientDep = cursor.getString(cursor.getColumnIndex("ClientDep"));
		this.ClientName = cursor.getString(cursor.getColumnIndex("ClientName"));
		this.ClientTel = cursor.getString(cursor.getColumnIndex("ClientTel"));
		this.User = cursor.getString(cursor.getColumnIndex("User"));
		this.Fee = cursor.getString(cursor.getColumnIndex("Fee"));
		this.ReceiptNo = cursor.getString(cursor.getColumnIndex("ReceiptNo"));
		this.CreatedDate = cursor.getString(cursor.getColumnIndex("CreatedDate"));
		this.Remark = cursor.getString(cursor.getColumnIndex("Remark"));
		//任务备注 20151028
		this.TaskRemark=cursor.getString(cursor.getColumnIndex("TaskRemark"));
		this.IsNew = StringUtil.parseBoolean(cursor.getString(cursor.getColumnIndex("IsNew")));
		this.DataDefineVersion = cursor.getInt(cursor.getColumnIndex("DataDefineVersion"));
		this.CreateType = TaskCreateType.getEnumByValue(cursor.getInt(cursor.getColumnIndex("CreateType")));
		this.UploadStatusEnum = TaskUploadStatusEnum.getEnumByValue(cursor.getInt(cursor.getColumnIndex("UploadStatus")));
		this.UploadTimes = cursor.getInt(cursor.getColumnIndex("UploadTimes"));
		this.LatestUploadDate = cursor.getString(cursor.getColumnIndex("LatestUploadDate"));
		// 新增字段
		this.BookedDate = cursor.getString(cursor.getColumnIndex("BookedDate"));
		this.BookedTime = cursor.getString(cursor.getColumnIndex("BookedTime"));
		this.ContactPerson = cursor.getString(cursor.getColumnIndex("ContactPerson"));
		this.ContactTel = cursor.getString(cursor.getColumnIndex("ContactTel"));
		this.BookedRemark = cursor.getString(cursor.getColumnIndex("BookedRemark"));
		// 内业报告是否完成 start 2015-6-30
		this.InworkReportFinish = StringUtil.parseBoolean(cursor.getString(cursor.getColumnIndex("InworkReportFinish")));
		this.InworkReportFinishDate = cursor.getString(cursor.getColumnIndex("InworkReportFinishDate"));
		// 资源相关 2015-7-2
		// this.HasResource =
		// StringUtil.parseBoolean(cursor.getString(cursor.getColumnIndex("HasResource")));
		this.UrgentFee = cursor.getDouble(cursor.getColumnIndex("UrgentFee"));
		// 2015-8-5
		this.AdjustFee = cursor.getDouble(cursor.getColumnIndex("AdjustFee"));
		this.LiveSearchCharge = cursor.getDouble(cursor.getColumnIndex("LiveSearchCharge"));
		
	}

	/**
	 * 有参数构造
	 * */
	@SuppressWarnings("static-access")
	public TaskInfo(TaskInfoDTO task) {
		TaskNum = task.TaskNum;
		TaskID = (int) task.ID;
		DDID = (int) task.DDID;
		CompanyId=task.CompanyId;
		ReceiveDate = task.ReceiveDate;
		DoneDate = task.DoneDate;
		Status = TaskStatus.getEnumByValue(task.Status);
		TargetNumber = task.TargetNumber;
		UrgentStatus = UrgentStatus.getEnumByName(task.IsUrgent);
		// IsUrgent = task.UrgentStatus.getIndex();
		TargetAddress = task.TargetAddress;
		Owner = task.Owner;
		OwnerTel = task.OwnerTelePhone;
		ResidentialArea = task.ResidentialArea;
		Building = task.Building;
		Floor = task.Floor;
		TargetName = task.TargetName;
		TargetType = task.TargetType;
		TargetArea = task.TargetArea;
		ClientUnit = task.ClientUnit;
		ClientDep = task.ClientDepartment;
		ClientName = task.ClientName;
		ClientTel = task.ClientTelephone;
		User = task.User;
		Fee = task.Fee;
		ReceiptNo = task.ReceiptNo;
		CreatedDate = task.CreatedDate;
		Remark = task.Remark;
		TaskRemark=task.TaskRemark;
		DataDefineVersion = task.DataDefineVersion;
		CreateType = TaskCreateType.getEnumByValue(task.CreateType);
		// 新增字段
		BookedDate = task.BookedDate;
		BookedTime = task.BookedTime;
		ContactPerson = task.ContactPerson;
		ContactTel = task.ContactTel;
		BookedRemark = task.BookedRemark;
		// 内业报告是否完成 start 2015-6-30
		InworkReportFinish = task.InworkReportFinish;
		InworkReportFinishDate = task.InworkReportFinishDate;
		// 资源相关 2015-7-2
		HasResource = task.HasResource;
		// 加急金额 2015-7-24
		UrgentFee = task.UrgentFee;
		// 2015-8-5
		AdjustFee = task.AdjustFee;
		LiveSearchCharge = task.LiveSearchCharge;
		if (task.Categories != null) {
			for (TaskCategoryInfoDTO category : task.Categories) {
				Categories.add(new TaskCategoryInfo(category));
			}
		}
	}
}
