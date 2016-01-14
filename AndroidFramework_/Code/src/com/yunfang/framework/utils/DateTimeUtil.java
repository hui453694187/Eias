package com.yunfang.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间相关的操作类
 * 
 * @author gorson
 * 
 */
public class DateTimeUtil {

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日期格式
																			// 2013-04-02
																			// 14:22:22
		return sdf.format(new Date());// 当前时间
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime_CN() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// 日期格式
																			// 2013-04-02
																			// 14:22:22
		return sdf.format(new Date());// 当前时间
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime_NoSymbol() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 日期格式
																		// 2013-04-02
																		// 14:22:22
		return sdf.format(new Date());// 当前时间
	}

	/**
	 * 根据long值得到当前时间
	 * 
	 * @param time
	 * @return
	 */
	public static String getCustomtTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日期格式
																			// 2013-04-02
																			// 14:22:22
		return sdf.format(time);// 自定义格式化时间
	}

	/**
	 * 字符串转换到时间格式
	 * 
	 * @param dateStr
	 *            :需要转换的字符串
	 * @param formatStr
	 *            :需要格式的目标字符串 举例 yyyy-MM-dd
	 * @return Date:返回转换后的时间
	 * @throws ParseException
	 *             :转换异常
	 */
	public static Date StringToDate(String dateStr, String formatStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 时间格式化
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String converTime(String dateStr) {

		return converTime(dateStr, "yyyy-MM-dd HH:mm");
	}

	/**
	 * 按指定格式转换时间
	 * 
	 * @param dateStr
	 * @param formatStr
	 * @return
	 */
	public static String converTime(String dateStr, String formatStr) {

		Date date = StringToDate(dateStr, formatStr);
		return converTime(date.getTime());
	}

	/**
	 * 把时间转换为 近时间 如 近几分钟、小时、天
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String converTime(long timestamp) {
		long currentSeconds = System.currentTimeMillis();
		long timeGap = (currentSeconds - timestamp) / 1000;// 与现在时间相差秒数
		String timeStr = null;
		if (timeGap > 24 * 60 * 60) {// 1天以上
			timeStr = timeGap / (24 * 60 * 60) + "天前";
		} else if (timeGap > 60 * 60) {// 1小时-24小时
			timeStr = timeGap / (60 * 60) + "小时前";
		} else if (timeGap > 60) {// 1分钟-59分钟
			timeStr = timeGap / 60 + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	/**
	 * 使用时间 2个之间的差值 转换近时间
	 * 
	 * @param dateStr
	 * @param dateStr1
	 * @return
	 */
	public static String UsedTime(String dateStr, String dateStr1) {

		Date dt = StringToDate(dateStr, "yyyy-MM-dd HH:mm");
		Date dt1 = StringToDate(dateStr1, "yyyy-MM-dd HH:mm");

		String result = "";
		long timeGap = (dt1.getTime() - dt.getTime()) / 60000;// 与现在时间相差分钟数
		long num1 = timeGap / (24 * 60);
		if (num1 > 0) {
			result = result + num1 + "天";
		}
		long num2 = (timeGap % (24 * 60));
		num1 = num2 / 60;
		if (num1 > 0) {
			result = result + num1 + "小时";
		}
		num2 = (num2 % 60);
		if (num2 > 0) {
			result = result + num2 + "分钟";
		}
		return result;
	}

	/**
	 * 获取中文格式时间
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
		Date date = new Date(timestamp * 1000);
		sdf.format(date);
		return sdf.format(date);
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date strToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date; 
	}
	
	/**
	 * 字符串转换成yyyy-MM-dd HH:mm:ss日期字符串
	 * 
	 * @param str
	 * @return String
	 */
	public static String dateFormat(String str) {
		String result = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);			
			result = format.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}
