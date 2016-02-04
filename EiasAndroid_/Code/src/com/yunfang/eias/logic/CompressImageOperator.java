/**
 * 
 */
package com.yunfang.eias.logic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.yunfang.eias.base.BroadRecordType;
import com.yunfang.eias.base.EIASApplication;
import com.yunfang.framework.model.ResultInfo;

/**
 * @author SEN
 * 图片压缩类
 */
public  class CompressImageOperator {

	// {{ 图片压缩相关方法
	
	/**
	 * 图片压缩方法,图片大小压缩到不超过指定的大小(按照系统默认的大小，并直接覆盖原图)
	 * @param filePath 文件路径
	 * @return 压缩结果
	 */
	public static ResultInfo<Boolean>qualityCompressImage(String filePath){
		// 取得用户设定的最大图片大小
		String maxImageSizeStr = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_MAXIMAGESIZE);
		long maxImageSize = Long.parseLong(maxImageSizeStr)*1024;
		return compressImage(filePath,maxImageSize,"");
	}
	
	/**
	 * 图片压缩方法,图片大小压缩到不超过指定的大小(按照系统默认的大小)
	 * @param filePath 文件路径
	 * @param outputFilePath 压缩后新文件输出路径
	 * @return 压缩结果
	 */
	public static ResultInfo<Boolean>qualityCompressImage(String filePath,String outputFilePath){
		// 取得用户设定的最大图片大小
		String maxImageSizeStr = EIASApplication.getSystemSetting(BroadRecordType.KEY_SETTING_MAXIMAGESIZE);
		long maxImageSize = Long.parseLong(maxImageSizeStr)*1024;
		return compressImage(filePath,maxImageSize,outputFilePath);
	}
	
	/**
	 * 图片压缩方法,图片大小压缩到不超过指定的大小
	 * @param filePath 文件路径
	 * @param customMaxImageSize 用户自定义文件大小(单位：K)
	 * @return 压缩结果
	 */
	public static ResultInfo<Boolean>qualityCompressImage(String filePath,long customMaxImageSize){
		return compressImage(filePath,customMaxImageSize,"");
	}
	
	/**
	 * 图片压缩方法,图片大小压缩到不超过指定的大小
	 * @param filePath 文件路径
	 * @param customMaxImageSize 用户自定义文件大小(单位：K)
	 * @param outputFilePath 压缩后新文件输出路径
	 * @return 压缩结果
	 */
	public static ResultInfo<Boolean>qualityCompressImage(String filePath,long customMaxImageSize,String outputFilePath){
		return compressImage(filePath,customMaxImageSize,outputFilePath);
	}
	
	/**
	 * 分辨率压缩,按比例压缩分辨率。
	 * @param filePath 文件路径
	 * @param compressPercent 
	 * @return 压缩是否成功
	 */
	public static ResultInfo<Boolean> resolutionCompressImage(String filePath,Float compressPercent){
	        // 声明缩放比率变量
	        Matrix matrix = new Matrix();
	        // 按比例缩小图片
	        matrix.postScale(compressPercent,compressPercent);
	        // 压缩图片公共方法
	        return compressImage(filePath,matrix,"");
	}

	/**
	 * 分辨率压缩,按比例压缩分辨率。
	 * @param filePath 文件路径
	 * @param compressPercent 
	 * @param outputFilePath 压缩后新文件输出路径
	 * @return 压缩是否成功
	 */
	public static ResultInfo<Boolean> resolutionCompressImage(String filePath,Float compressPercent,String outputFilePath){
	        // 声明缩放比率变量
	        Matrix matrix = new Matrix();
	        // 按比例缩小图片
	        matrix.postScale(compressPercent,compressPercent);
	        // 压缩图片公共方法
	        return compressImage(filePath,matrix,outputFilePath);
	}
	
	/**
	 * 分辨率压缩,压缩到指定分辨率大小。
	 * @param filePath 文件路径
	 * @param width 压缩后的宽度
	 * @param height 压缩后的高度
	 * @return 压缩是否成功
	 */
	public static ResultInfo<Boolean> resolutionCompressImage(String filePath,Integer newWidth,Integer newHeight){

			// 找到图片对象
			Bitmap image = BitmapFactory.decodeFile(filePath);
	        // 声明缩放比率变量
	        Matrix matrix = new Matrix();
	        // 计算宽高缩放率
	        float scaleWidth = (float) newWidth / (float)image.getWidth();
	        float scaleHeight = (float) newHeight / (float)image.getHeight();
	        // 按比例缩小图片
	        matrix.postScale(scaleWidth,scaleHeight);
	        // 压缩图片公共方法
	        return compressImage(filePath,matrix,"");
	}
	
	/**
	 * 分辨率压缩,压缩到指定分辨率大小。
	 * @param filePath 文件路径
	 * @param width 压缩后的宽度
	 * @param height 压缩后的高度
	 * @param outputFilePath 压缩后新文件输出路径
	 * @return 压缩是否成功
	 */
	public static ResultInfo<Boolean> resolutionCompressImage(String filePath,Integer newWidth,Integer newHeight,String outputFilePath){
			// 找到图片对象
			Bitmap image = BitmapFactory.decodeFile(filePath);
	        // 声明缩放比率变量
	        Matrix matrix = new Matrix();
	        // 计算宽高缩放率
	        float scaleWidth = (float) newWidth / (float)image.getWidth();
	        float scaleHeight = (float) newHeight / (float)image.getHeight();
	        // 按比例缩小图片
	        matrix.postScale(scaleWidth,scaleHeight);
	        // 压缩图片公共方法
	        return compressImage(filePath,matrix,outputFilePath);
	}

	/**
	 * 图片压缩方法,图片大小压缩到不超过指定的大小
	 * @param filePath 文件路径
	 */
	private static ResultInfo<Boolean> compressImage(String filePath,long customMaxImageSize,String outputFilePath){
		ResultInfo<Boolean> result= new ResultInfo<Boolean>();
		// 若有指定输出路径,则先声明输出路径的文件。
		fileCheck(outputFilePath);
		result.Data = true;
		Bitmap image = BitmapFactory.decodeFile(filePath);
		// 取得配置的图片最大值
		Integer quality = getQualityPercent(image,customMaxImageSize);
		if(quality!=100 || outputFilePath!=""){
            try {
      			FileOutputStream out = new FileOutputStream(outputFilePath==""?filePath:outputFilePath);
      			image.compress(Bitmap.CompressFormat.JPEG,quality, out);
      			out.flush();
      			out.close();
      			// 压缩完毕
      		}catch (Exception e) {
      		    e.printStackTrace();
      			result.Message = "文件压缩失败";
      			result.Data = false;
      		}	      
		}        
		if(!image.isRecycled()){  
			//释放资源，否则会内存溢出  
        	image.recycle();
        }
		return result;
	}
	
	/**
	 * 图片分辨率压缩方法
	 * @param filePath 图片路径
	 * @param matrix 压缩参数
	 * @return 操作结果
	 */
	private static ResultInfo<Boolean> compressImage(String filePath,Matrix matrix,String outputFilePath){
		ResultInfo<Boolean> result= new ResultInfo<Boolean>();
		// 若有指定输出路径,则先声明输出路径的文件。
		fileCheck(outputFilePath);
		result.Data=true;
		Bitmap image = BitmapFactory.decodeFile(filePath);
		try{
	        // 压缩图片操作
	        Bitmap newBitmap = Bitmap.createBitmap(image, 0, 0,  image.getWidth(),image.getHeight(), matrix, true);
	        // 声明输出路径
	  		FileOutputStream out = new FileOutputStream(outputFilePath==""?filePath:outputFilePath);
	  		// 保存压缩后的图片
	  		newBitmap.compress(Bitmap.CompressFormat.JPEG,100, out);
  		}catch (Exception ex) {
  			ex.printStackTrace();
  			result.Message ="图片压缩失败:"+ex.getMessage();
  			result.Data = false;
  		}
        return result;
	}

	/**
	 * 需要压缩到指定大小时的质量百分比
	 * @param image 图片对象
	 * @param customMaxImageSize 自定义指定大小(为0时默认使用系统中设定的指定大小)
	 * @return 质量百分比
	 */
	private static Integer getQualityPercent(Bitmap image,long customMaxImageSize){
		Map<Double,Integer> scaleMap = new HashMap<Double,Integer>();
		scaleMap.put(0.87, 99); 
		scaleMap.put(0.66, 98); 
		scaleMap.put(0.54, 97); 
		scaleMap.put(0.45, 96); 
		scaleMap.put(0.40, 95); 
		scaleMap.put(0.33, 94); 
		scaleMap.put(0.30, 93); 
		scaleMap.put(0.25, 92); 
		scaleMap.put(0.24, 91); 
		scaleMap.put(0.23, 90); 
		scaleMap.put(0.21, 89); 
		scaleMap.put(0.20, 88); 
		scaleMap.put(0.19, 87); 
		scaleMap.put(0.18, 86); 
		scaleMap.put(0.17, 85); 
		scaleMap.put(0.16, 83); 
		scaleMap.put(0.15, 80); 
		scaleMap.put(0.14, 78); 
		scaleMap.put(0.13, 76); 
		scaleMap.put(0.12, 74); 
		scaleMap.put(0.11, 72); 
		scaleMap.put(0.10, 70); 
		scaleMap.put(0.09, 68); 
		scaleMap.put(0.08, 63); 
		scaleMap.put(0.07, 59); 
		scaleMap.put(0.06, 46); 
		scaleMap.put(0.05, 30); 
		scaleMap.put(0.04, 18); 
		scaleMap.put(0.03, 1); 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        long length = baos.toByteArray().length/1024;
        baos.reset();
        if(length>customMaxImageSize){
        	options = 99;
        	double scale = (double)customMaxImageSize/(double)length;
            for (double key : scaleMap.keySet()) {
            	if(scale<=key){
            		if(options>scaleMap.get(key)){
                		options = scaleMap.get(key);
            		}
            	}
            }
        }
        return options;
	}
	
	/**
	 * 文件检测方法，不存在文件则创建
	 * @param filePath 新指定文件路径
	 */
	private static void fileCheck(String filePath){
		if(filePath!=""){
			try {
				String folder = filePath.substring(0,filePath.lastIndexOf("/"));
				File file = new File(folder);
				// 没有目录则创建目录
				if (!file.exists()){
					  file.mkdir();
				}
				file = new File(filePath);
				// 没有文件则创建文件
				if(!file.exists()){
					file.createNewFile();
				}
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
	}
	
	// }}
}
