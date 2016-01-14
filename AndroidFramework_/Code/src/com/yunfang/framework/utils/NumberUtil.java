package com.yunfang.framework.utils;

import java.text.DecimalFormat;

/**
 * 数字型辅助类
 * @author gorson
 *
 */
public class NumberUtil {
	/**
	 * 整型数据是否大于0
	 * @param i
	 * @return
	 */
	public static Boolean Greater0(Integer i){
		Boolean result = false;
		if(i != null && i > 0){
			result = true;
		}
		return result;
	}
	
	/** 
     * 给参数返回指定小数点后几位的四舍五入 
     * @param sourceData    传入的要舍取的元数据 
     * @param str 取舍的格式（主要用到"#.0"的格式，此为小数点后1位；"#.00"为小数点后2位，以此类推） 
     * @return 舍取后的 数据 
     */  
    public static double getDouble(double sourceData,String sf)  
    {  
        DecimalFormat df = new DecimalFormat(sf);  
        String str = df.format(sourceData);  
        return Double.parseDouble(str);  
    }  
      
    /** 
     * 给参数返回指定小数点后 a 位的四舍五入 
     * @param sourceData 要取舍的原数据 
     * @param a 小数点 后的 位数（如：10：小数点后1位；100：小数据后2位以此类推） 
     * @return 舍取后的 数据 
     */  
    public static float getFloatRound(double sourceData,int a)  
    {  
        int i = (int) Math.round(sourceData*a);     //小数点后 a 位前移，并四舍五入  
        float f2 = (float) (i/(float)a);        //还原小数点后 a 位  
        return f2;  
    }  
}
