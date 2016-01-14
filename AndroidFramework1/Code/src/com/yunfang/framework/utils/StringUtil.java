package com.yunfang.framework.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 字符串辅助类
 * 
 * @author gorson
 * 
 */
public class StringUtil {

	// /**
	// * 获取访问服务器的根地址
	// * eg: http://www.yunfang.com/api/
	// * @return
	// */
	// public static String GetWebSiteRoot(){
	// return
	// BaseApplication.getInstance().getResources().getString(R.string.website_root);
	// }

	/**
	 * 格式化文件大小 用多少M 多少K 多少B 显示
	 * 
	 * @param fileSize
	 *            :文件大小
	 * @return 格式化好的字符串
	 */
	public static String FormatFileSize(long fileSize) {		
		String result = "";
		Double size = (double) fileSize;
		DecimalFormat format = new DecimalFormat("###.00");
		if (size >= 1024 * 1024) {
			size = (size / (1024 * 1024.00f));
			result = format.format(size) + "M";
		} else if (size >= 1024) {
			size = (size / (1024.00f));
			result = format.format(size) + "K";
		} else {
			result = size + "B";
		}
		return result;
	}

	/**
	 * 日期变量转成对应的星期字符串
	 * 
	 * @param dateString
	 *            字符串格式的时间
	 * @return
	 * @throws ParseException
	 */
	public static String dateToWeek(String dateString) throws ParseException {
		String result = "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = df.parse(dateString);
		String[] WEEK = { "星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayIndex < 1 || dayIndex > WEEK.length) {
			return null;
		}
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM月dd日");
		result = sDateFormat.format(date) + "(" + WEEK[dayIndex - 1] + ")";

		return result;
	}

	/**
	 * 格式化时间 例如 6.5 变为 6:30
	 * 
	 * @param dateString
	 *            字符串格式的时间
	 * @return
	 */
	public static String toTimeString(String dateString) {
		String result = "";
		int index = dateString.indexOf(".");
		if (index == -1) {
			result = dateString + ":" + "00";
		} else {
			String min = dateString.substring(index + 1);
			int intMin = Integer.parseInt(min) * 6;
			String formatMin = intMin < 10 ? "0" + intMin : "" + intMin;
			result = dateString.substring(0, index) + ":" + formatMin;
		}
		return result;
	}

	/**
	 * 字符串转成布尔型
	 * 
	 * @param value
	 *            ：True或1为true
	 * @return
	 */
	public static Boolean parseBoolean(String value) {
		Boolean result = false;

		if (value.toLowerCase().equals("true") || value.equals("1")) {
			result = true;
		}

		return result;
	}

	/**
	 * 获取指定值在数组中的位置
	 * 
	 * @param srcArray
	 * @param compareValue
	 * @return
	 */
	public static int getIndexForArray(String[] srcArray, String compareValue) {
		int index = -1;
		for (int i = 0; i < srcArray.length; i++) {
			String tmp = srcArray[i];
			if (tmp.endsWith("}")) {
				tmp = tmp.substring(0, tmp.indexOf("{"));
			}
			if (tmp.equals(compareValue)) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * 判断当前字符串是否为NUll或是空值
	 * @param str
	 * @return true:Null或是0长度,false:不为Null,有实际长度，也可以为一个空格
	 */
	public static Boolean IsNullOrEmpty(String str){
		Boolean result = true;
		if(str != null && str.length()>0){
			result = false;
		}
		return result;
	}
}
