package com.yunfang.eias.beta.db;

import java.util.ArrayList;
import com.yunfang.framework.db.IDBArchitecture;

/**
 * 用于记录外采系统数据库所有表的SQL脚本
 * @author gorson
 *
 */
public class DBTableScripts implements IDBArchitecture {

	//{{ getGenTableScripts 获取数据库中创建所有表的SQL脚本

	/**
	 * 获取当前版本号
	 * @return
	 */
	public int getCurrentVersion() {		
		return 16;
	}
	
	/**
	 * 获取数据库中创建所有表的SQL脚本
	 * @return
	 */
	public ArrayList<String> getGenTableScripts(){
		ArrayList<String> result = new ArrayList<String>();

		//1.勘察配置基本信息表     DataDefine
		result.add(getDataDefineScript());

		//2.勘察配置表分类项信息表  DataCategoryDefine
		result.add(getDataCategoryDefineScript());

		//3.勘察配置表属性信息表   DataFieldDefine
		result.add(getDataFieldDefineScript());

		//4.勘察任务信息表   TaskInfo
		result.add(getTaskInfoScript());

		//5.勘察任务分类项信息表   TaskCategoryInfo
		result.add(getTaskCategoryInfoScript());

		//6.勘察任务属性数据记录表  TaskDataItem
		result.add(getTaskDataItemScript());

		//7.日志记录表   DataLog
		result.add(getDataLogScript());

		return result;
	}

	/**
	 * 获取  勘察配置基本信息表  的建表脚本 
	 * @return
	 */
	private String getDataDefineScript(){
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("CREATE TABLE IF NOT EXISTS DataDefine(");
		sqlBuider.append("ID integer primary key autoincrement");
		sqlBuider.append(",DDID integer");//对应后台管理系统中此勘察配置表的ID值
		sqlBuider.append(",Version integer");//版本号
		sqlBuider.append(",Name varchar(100)");//勘察配置表名称
		sqlBuider.append(",CompanyID integer");//所在公司的ID值
		sqlBuider.append(",IsDefault integer");//是否默认推荐
		sqlBuider.append(",DefineType varchar(100)");//勘察配置表名称
		sqlBuider.append(",CrmDepId varchar(1000)");//内容：如果是选项的话，选项值都这里
		sqlBuider.append(")");
		return sqlBuider.toString();
	}

	/**
	 * 获取  勘察配置表分类项信息表  的建表脚本
	 * @return
	 */
	private String getDataCategoryDefineScript(){
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("CREATE TABLE IF NOT EXISTS DataCategoryDefine(");
		sqlBuider.append("ID integer primary key autoincrement");
		sqlBuider.append(",ControlType integer");//分类项的类型CategoryType：图片集=0、位置=1、常规=2
		sqlBuider.append(",DDID integer");//所属勘察配置表的ID
		sqlBuider.append(",DefaultShow integer");//是否默认显示BooleanEnum,Ture=0，False=1
		sqlBuider.append(",Public integer");//是否公开可见BooleanEnum,Ture=0，False=1
		sqlBuider.append(",Name varchar(100)");//分类项名称：相同勘察配置表下名称不可重复
		sqlBuider.append(",Repeat integer");//是否可以重复BooleanEnum,Ture=0，False=1
		sqlBuider.append(",RepeatMax integer");//最大可重复数，默认值为0，0为不限制
		sqlBuider.append(",RepeatLimit integer");//最小重复数，默认值为0，0为不限制
		sqlBuider.append(",VersionNumber integer");//版本号(以后扩展使用)
		sqlBuider.append(",Active integer");//是否可用BooleanEnum,Ture=0，False=1
		sqlBuider.append(",CategoryID integer");//后台管理系统中对应当前勘察表下对应名称分类项的ID值
		sqlBuider.append(",Total integer");//属于当前分类项下的属性总数
		sqlBuider.append(",IOrder integer");//分类项在任务列表中的顺序号
		sqlBuider.append(")");
		return sqlBuider.toString();
	}

	/**
	 * 获取 勘察配置表属性信息表  的建表脚本
	 * @return
	 */
	private String getDataFieldDefineScript() {
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("CREATE TABLE IF NOT EXISTS DataFieldDefine(");
		sqlBuider.append("ID integer primary key autoincrement");
		sqlBuider.append(",CategoryID integer");//分类项的ID（对应该DataCategoryDefine表中的CategoryID值）
		sqlBuider.append(",DDID integer");//所属勘察配置表的ID，即属性项在后台管理系统中对应的ID值
		sqlBuider.append(",Content varchar(1000)");//内容：如果是选项的话，选项值都这里
		sqlBuider.append(",ItemType integer");//分类项的类型DataItemType：文本、多行文本、自定义文本、下拉框、多选框、图片、时间、GPS、当前用户名、当前用户联系方式
		sqlBuider.append(",Name varchar(100)");//属性名称
		sqlBuider.append(",Value varchar(255)");//属性默认值
		sqlBuider.append(",InputFormat varchar(20)");//输入内容的格式，例如：数字、字符串、时间等
		sqlBuider.append(",InputRange varchar(255)");//输入值范围
		sqlBuider.append(",Required integer");//是否为必填项BooleanEnum,Ture=0，False=1
		sqlBuider.append(",IOrder integer");//属性在所在分类项中的顺序号
		sqlBuider.append(",BaseID integer");//对应后台服务器中当前属性对应的ID值
		sqlBuider.append(",Hint varchar(100)");//输入提示
		sqlBuider.append(",ShowInPhone integer");//是否在手机端显示
		sqlBuider.append(")");
		return sqlBuider.toString();
	}

	/**
	 * 获取  勘察任务信息表  的建表脚本 
	 * @return
	 */
	private String getTaskInfoScript() {
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("CREATE TABLE IF NOT EXISTS TaskInfo(");
		sqlBuider.append("ID integer primary key autoincrement");
		sqlBuider.append(",TaskNum varchar(100)");//任务编号
		sqlBuider.append(",TaskID integer");//任务ID，后台管理系统中对应任务编号的任务ID
		sqlBuider.append(",DDID	integer");//任务勘察类型
		sqlBuider.append(",ReceiveDate varchar(30)");//领取时间（自建任务时，需要客户端自动填入当前时间）
		sqlBuider.append(",DoneDate varchar(30)");//完成时间
		sqlBuider.append(",Status integer");//项目状态TaskStatus:待领取=0，待提交=1，已完成=2（自建任务时，状态默认为待提交，状态值为1）
		sqlBuider.append(",TargetNumber varchar(50)");//估价物编号
		sqlBuider.append(",UrgentStatus integer");//紧急程度UrgentStatus:Normal=0，Urgent=1
		sqlBuider.append(",TargetAddress varchar(255)");//地址
		sqlBuider.append(",Owner varchar(20)");//业主
		sqlBuider.append(",OwnerTel	varchar(30)");//业主电话
		sqlBuider.append(",ResidentialArea varchar(100)");//小区名称
		sqlBuider.append(",Building varchar(20)");//楼栋名称
		sqlBuider.append(",Floor varchar(20)");//所在楼层
		sqlBuider.append(",TargetName varchar(100)");//目标名称
		sqlBuider.append(",TargetType varchar(20)");//用途
		sqlBuider.append(",TargetArea varchar(20)");//建筑面积
		sqlBuider.append(",ClientUnit varchar(30)");//委托人单位
		sqlBuider.append(",ClientDep varchar(30)");//委托人部门
		sqlBuider.append(",ClientName varchar(30)");//委托人名称
		sqlBuider.append(",ClientTel varchar(30)");//委托人联系电话
		sqlBuider.append(",User	varchar(30)");//领取人
		sqlBuider.append(",Fee varchar(20)");//收费金额
		sqlBuider.append(",ReceiptNo varchar(30)");//收据号
		sqlBuider.append(",CreatedDate varchar(30)");//创建时间
		sqlBuider.append(",Remark varchar(1000)");//标注
		sqlBuider.append(",IsNew integer");//是否为新建，BooleanEnum,Ture=0，False=1，标记是否为用户自建任务，0为非自建，1为自建
		sqlBuider.append(",DataDefineVersion integer");//需要的勘察表分类信息版本号
		sqlBuider.append(",CreateType integer");//创建方式TaskCreateType，用户自建=0、系统界面创建=1、批量创建=2、第三方系统创建=3
		sqlBuider.append(",UploadStatus integer");//上传状态：UploadStatus:未上传=0,上传成功=1，上传失败=2
		sqlBuider.append(",UploadTimes integer");//上传次数，默认值为：0
		sqlBuider.append(",LatestUploadDate varchar(30)");//最后上传时间(默认当前系统的时间)		
		//版本9加入新字段
		sqlBuider.append(",BookedDate varchar(30)");//预约日期(默认当前系统的日期)		
		sqlBuider.append(",BookedTime varchar(30)");//预约时间(默认当前系统的时间)		
		sqlBuider.append(",ContactPerson varchar(50)");//联系人	
		sqlBuider.append(",ContactTel varchar(150)");//联系人电话
		sqlBuider.append(",BookedRemark varchar(300)");//备注
		//版本10加入新字段
		sqlBuider.append(",InworkReportFinish integer DEFAULT 0");//任务对应的内页报告是否完成
		sqlBuider.append(",InworkReportFinishDate varchar(30)");//任务对应的内页报告完成时间
		sqlBuider.append(",HasResource integer DEFAULT 0");//是否有资源 
		//版本11加入新字段
		sqlBuider.append(",UrgentFee DOUBLE(9,2) DEFAULT 0");//加急金额
		//版本12加入新字段		
		sqlBuider.append(",AdjustFee DOUBLE(9,2) DEFAULT 0");//应收费用		
		sqlBuider.append(",LiveSearchCharge DOUBLE(9,2) DEFAULT 0");//预收费用
		//版本13加入新字段
		sqlBuider.append(",CompanyId integer DEFAULT 0");//公司编号
		//版本15加入新字段
		sqlBuider.append(",TaskRemark varchar(300)");//任务备注
		
		sqlBuider.append(")");
		return sqlBuider.toString();
	}

	/**
	 * 获取  勘察任务分类项信息表  的建表脚本 
	 * @return
	 */
	private String getTaskCategoryInfoScript() {
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("CREATE TABLE IF NOT EXISTS TaskCategoryInfo(");
		sqlBuider.append("ID integer primary key autoincrement");
		sqlBuider.append(",TaskID integer");//勘察任务ID号，后台管理系统中对应任务编号的任务ID
		sqlBuider.append(",DataDefineFinishCount integer");//当前勘察属性已完成数量
		sqlBuider.append(",DataDefineTotal integer");//当前勘察属性总共数量
		sqlBuider.append(",RemarkName varchar(100)");//标识名称，同一勘察任务下，不可重复
		sqlBuider.append(",BaseCategoryID integer");//勘察任务所属于勘察表的可重复分类项的ID，对应后台管理系统中的ID值		
		sqlBuider.append(",CategoryID integer");//Android端中此任务对应的ID值
		sqlBuider.append(",CreatedDate varchar(30)");//创建时间
		sqlBuider.append(")");
		return sqlBuider.toString();
	}

	/**
	 * 获取  勘察任务属性数据记录表   的建表脚本 
	 * @return
	 */
	private String getTaskDataItemScript() {
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("CREATE TABLE IF NOT EXISTS TaskDataItem(");
		sqlBuider.append("ID integer primary key autoincrement");
		sqlBuider.append(",BaseCategoryID integer");//勘察配置表下分类项的ID，对应TaskCategoryInfos表中的BaseCategoryID值
		sqlBuider.append(",Name	varchar(100)");//属性名称
		sqlBuider.append(",Value varchar(255)");//采集值
		sqlBuider.append(",IOrder integer");//属性在所在分类项中的顺序号
		sqlBuider.append(",TaskID integer");//勘察任务ID值(后台管理端)
		sqlBuider.append(",CategoryID integer");//分类项标识，用于区分可重复项中的哪一个具体的重复值，对应TaskCategoryInfos表中的ID值
		sqlBuider.append(",BaseID integer");//对应后台管理系统中的ID值
		sqlBuider.append(")");
		return sqlBuider.toString();
	}

	/**
	 * 获取 日志记录表  的建表脚本
	 * @return
	 */
	private String getDataLogScript() {
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("CREATE TABLE IF NOT EXISTS DataLog(");
		sqlBuider.append("ID integer primary key autoincrement");
		sqlBuider.append(",UserID varchar(100)");//用户ID
		sqlBuider.append(",OperatorType integer");//操作类型  OperatorType
		sqlBuider.append(",LogContent varchar(2000)");//日志操作记录
		sqlBuider.append(",CreatedDate varchar(30)");//产生日志的时间
		// 版本16 加入新字段 
		sqlBuider.append(",LogType integer");//操作类型  OperatorType
		sqlBuider.append(")");
		return sqlBuider.toString();
	} 	

	//}}

	/**
	 * 获取数据库文件名称
	 * @return
	 */
	public String getDBName() {
		return "eias.db";
	}

	/**
	 * 删除数据库中所有表，测试时使用，正式发布时禁止使用
	 * @return
	 */
	public ArrayList<String> removeAllTables(){
		ArrayList<String> result = new ArrayList<String>();
//		result.add("DROP TABLE IF EXISTS DataFieldDefine");
//		result.add("DROP TABLE IF EXISTS DataCategoryDefine");
//		result.add("DROP TABLE IF EXISTS DataDefine");
//		result.add("DROP TABLE IF EXISTS TaskDataItem");
//		result.add("DROP TABLE IF EXISTS TaskCategoryInfo");
//		result.add("DROP TABLE IF EXISTS TaskInfo");
//		result.add("DROP TABLE IF EXISTS DataLog");
		return result;
	}

	//{{ getDBUpdateScripts 获取数据库中对各版本间升级的SQL脚本
	/**
	 * 获取数据库中对各版本间升级的SQL脚本
	 * @return
	 */
	public ArrayList<String> getDBUpdateScripts(int oldVersion, int newVersion) {		
		ArrayList<String> result = new ArrayList<String>();

		switch (oldVersion+1)
		{
		//case的条件是新的版本
		case 8:
			result.add("ALTER TABLE DataFieldDefine ADD COLUMN Hint varchar(100)");
		case 9:
			// 由于sqlite删除字段比较麻烦，为防止错误，暂不删Remarks这个字段
			// result.add("ALTER TABLE TaskInfo DROP COLUMN Remarks");
			result.add("ALTER TABLE TaskInfo ADD COLUMN BookedRemark varchar(300)");
		case 10:
			//内业报告是否完成
			result.add("ALTER TABLE TaskInfo ADD COLUMN InworkReportFinish integer DEFAULT 0");
			//内业报告完成时间
			result.add("ALTER TABLE TaskInfo ADD COLUMN InworkReportFinishDate varchar(30)");
			//是否有资源
			result.add("ALTER TABLE TaskInfo ADD COLUMN HasResource DEFAULT 0");
		case 11:
			//加急金额
			result.add("ALTER TABLE TaskInfo ADD COLUMN UrgentFee DOUBLE(9,2) DEFAULT 0");
		case 12:
			//应收费用
			result.add("ALTER TABLE TaskInfo ADD COLUMN AdjustFee DOUBLE(9,2) DEFAULT 0");
			//预收费用
			result.add("ALTER TABLE TaskInfo ADD COLUMN LiveSearchCharge DOUBLE(9,2) DEFAULT 0");
		case 13:
			//公司ID
			result.add("ALTER TABLE TaskInfo ADD COLUMN CompanyId integer DEFAULT 0");
		case 14:
			//勘察表的所属部门
			result.add("ALTER TABLE DataDefine ADD COLUMN CrmDepId varchar(1000)");
		case 15:
			result.add("ALTER TABLE TaskInfo ADD COLUMN TaskRemark varchar(300)");
		case 16 :
			//日志类型
			result.add("ALTER TABLE DataLog ADD COLUMN LogType INTEGER DEFAULT 0");
		default:
			break;
		}
		return result;
	}			
	//}}
}
