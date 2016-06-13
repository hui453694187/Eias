package com.yunfang.eias.logic;

import java.util.ArrayList;

import com.yunfang.eias.R;
import com.yunfang.eias.enumObj.IntroductionTypeEnum;
import com.yunfang.eias.model.Introduction;
import com.yunfang.framework.model.ResultInfo;

/**
 * 
 * 项目名称：外采系统 
 * 类名称：IntroductionOperator 
 * 类描述：功能介绍操作逻辑类 
 * 创建人：贺隽
 * 创建时间：2014-07-29 14:10
 */
public class IntroductionOperator {	

	//{{ 

	/**
	 * 获取功能介绍数据
	 * @param introductionType:功能类型
	 * @return
	 */
	public static ResultInfo<ArrayList<Introduction>> getDatas(IntroductionTypeEnum introductionType) {
		ResultInfo<ArrayList<Introduction>> result = new ResultInfo<ArrayList<Introduction>>();
		try {
			switch (introductionType) {
			case AbountMain:
				result.Data = getAbountMain();
				break;
			case AbountTask:
				result.Data = getAbountTask();
				break;
			case AbountOther:
				result.Data = getAbountOther();
				break;
			default:
				break;
			}
			result.Success = true;
		} catch (Exception e) {
			result.Success = false;
			result.Message = "获取功能介绍数据失败";
		}
		return result;
	}

	/**
	 * 获取主要的功能介绍
	 * @return
	 */
	private static ArrayList<Introduction> getAbountMain() {
		ArrayList<Introduction> result = new ArrayList<Introduction>();
		result.add(new Introduction("","输入您的用户名和密码吧，只有被召唤的人才允许进入，在线登录才能提交任务，离线登录会有部分功能受限！",IntroductionTypeEnum.AbountMain,R.drawable.about_main_00));
		result.add(new Introduction("","原来您要做的东西都在有统计哦,赶紧看看吧！",IntroductionTypeEnum.AbountMain,R.drawable.about_main_01));
		result.add(new Introduction("","同步全部勘察表您值得拥有最全最新的勘察表；",IntroductionTypeEnum.AbountMain,R.drawable.about_main_02));
		result.add(new Introduction("","当然您也可以选择只更新您想要的勘察表；",IntroductionTypeEnum.AbountMain,R.drawable.about_main_03));
		result.add(new Introduction("","右上角点击姓名您将获得一个菜单，来试试吧；",IntroductionTypeEnum.AbountMain,R.drawable.about_main_04));
		result.add(new Introduction("","切换下面的选项或者滑动屏幕我们将来来到待勘察的任务，这里将告诉你这些是需要您领取的，记住哦！",IntroductionTypeEnum.AbountMain,R.drawable.about_main_05));
		result.add(new Introduction("","待提交这里都会记录您要去到现场勘查的任务，时间很宝贵，加油把！",IntroductionTypeEnum.AbountMain,R.drawable.about_main_06));
		result.add(new Introduction("","这里是您的荣耀榜哦，您勘察过的所有记录都会记录到这里，来看看您都做了些什么吧！",IntroductionTypeEnum.AbountMain,R.drawable.about_main_07));
		result.add(new Introduction("","我们才不会让你提交任务的时候让你等上半天，这里会处理您提交的所有任务，任务是否提交成功我们都会悄悄的告诉您，不用太担心！",IntroductionTypeEnum.AbountMain,R.drawable.about_main_08));
		return result;
	}
	
	/**
	 * 获取关于任务的功能介绍
	 * @return
	 */
	private static ArrayList<Introduction> getAbountTask() {
		ArrayList<Introduction> result = new ArrayList<Introduction>();
		result.add(new Introduction("","长按任务就会出现菜单，记得领取任务，这才能勘察哦！",IntroductionTypeEnum.AbountTask,R.drawable.about_task_01));
		result.add(new Introduction("","在待提交任务列表中我们长按任务会出现不同的菜单；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_02));
		result.add(new Introduction("","看看任务的信息吧!",IntroductionTypeEnum.AbountTask,R.drawable.about_task_03));
		result.add(new Introduction("","有钱收了哇，看看是多少呢，快填把；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_04));
		result.add(new Introduction("","点击编辑任务之后，我们就要去收集需要勘察的信息了,如果添加完了可以点击右上角的上箭头上传到数据到服务器哦；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_05));
		result.add(new Introduction("","点击定位坐标就可以选择勘察的地点哦；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_06));
		result.add(new Introduction("","这里是分组项哦，可以自由选择那些可以先填写；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_07));
		result.add(new Introduction("","还是照张相片把还是选择之前照好的呢，想想吧！",IntroductionTypeEnum.AbountTask,R.drawable.about_task_08));
		result.add(new Introduction("","好像照的还行，记得选择一个描述哦；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_09));
		result.add(new Introduction("","保存之后就可以看到整个媒体列表了，看看那张还需要需要重新照的或者不需要的呢！",IntroductionTypeEnum.AbountTask,R.drawable.about_task_10));
		result.add(new Introduction("","分组项利也可以长按出现菜单哦；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_11));
		result.add(new Introduction("","如果发现类似的房间可以点击复制信息哦；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_12));
		result.add(new Introduction("","您看点击粘贴就可以吧之前勘察的信息粘贴进去哦，多方便；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_13));
		result.add(new Introduction("","觉得分组项不够么，再加，想这么加就怎么加；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_14));
		result.add(new Introduction("","我们连任务也可以复制哦，选择你需要的分组项把；",IntroductionTypeEnum.AbountTask,R.drawable.about_task_15));
		result.add(new Introduction("","点击【任务匹配】还可以跑到服务器里面去拿数据哦",IntroductionTypeEnum.AbountTask,R.drawable.about_task_16));
		result.add(new Introduction("","在已完成中也可以出现菜单哦!",IntroductionTypeEnum.AbountTask,R.drawable.about_task_17));
		return result;
	}
	
	/**
	 * 获取关于其他功能的介绍
	 * @return
	 */
	private static ArrayList<Introduction> getAbountOther() {
		ArrayList<Introduction> result = new ArrayList<Introduction>();
		result.add(new Introduction("","呀！觉得自己头像不够帅么，没事再来一张吧！",IntroductionTypeEnum.AbountOther,R.drawable.about_other_01));
		result.add(new Introduction("","以前的密码太简单了，想改改密码么；",IntroductionTypeEnum.AbountOther,R.drawable.about_other_02));
		result.add(new Introduction("","咦，临时要勘察任务发现没有对应的领取任务么，没事点击菜单【新建任务】过来吧，我们等着您；",IntroductionTypeEnum.AbountOther,R.drawable.about_other_03));
		result.add(new Introduction("","被您发现了，这台设备的所有操作都会记录并发送到服务器，如果您的操作记录在这里没有找到，尝试在web版找找哦！",IntroductionTypeEnum.AbountOther,R.drawable.about_other_04));
		result.add(new Introduction("","嗯嗯，这里是我的版本信息，如果您有什么意见或者是建议记得联系我们哦！",IntroductionTypeEnum.AbountOther,R.drawable.about_other_05));
		return result;
	}

	//}}

}
