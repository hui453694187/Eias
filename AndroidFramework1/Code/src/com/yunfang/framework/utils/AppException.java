package com.yunfang.framework.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import com.yunfang.framework.utils.YFLog;

import android.os.Environment;

/**
 * 描述：应用程序异常类：用于捕获异常和提示错误信息　
 * @author gorson
 *
 */
@SuppressWarnings("serial")
public class AppException extends Exception {
	
	//=========================相关的属性start=========================================//
	/**
	 *  网络连接异常
	 */
	public final static byte TYPE_CONNECT=0x01;
	
	/**
	 * 网络读取数据异常
	 */
	public final static byte TYPE_SOCKET=0x02;
	
	/**
	 * 错误响应码
	 */
	public final static byte TYPE_HTTP_CODE=0x03;
	
	/**
	 *  网络异常
	 */
	public final  static byte TYPE_HTTP_ERROR=0x04;
	
	/**
	 * xml解析出错　
	 */
	public final static byte TYPE_XML=0x05;
	
	/**
	 * IO操作异常
	 */
	public final static byte TYPE_IO=0x06;
	
	/**
	 * 运行时异常
	 */
	public final static byte TYPE_RUN=0x07;
	
	/**
	 * SD卡根目录的路径
	 * */
	protected static String mSavePath=Environment.getExternalStorageDirectory().toString();
	
	/**
	 * 异常类型
	 * */
	@SuppressWarnings("unused")
	private int mType;
	
	/**
	 * 异常码
	 * */
	@SuppressWarnings("unused")
	private int mCode;
	
	//=========================相关的属性end=========================================//
	
	/**
	 * @param type：异常类型
	 * @param code：异常码
	 * @param exception：异常对象
	 */
	private AppException(int  type,int code,Exception exception){
		this.mType=type;
		this.mCode=code;
		if(YFLog.isDebug()){
			saveErrorLog(exception);
		}
		
	}
	/**
	 * 保存错误的日志
	 * @param ex：异常对象
	 */
	@SuppressWarnings("deprecation")
	public static void saveErrorLog(Exception ex){
		if(ex==null){
			return;
		}
		ex.printStackTrace();
		String errlog="error.log";
		//日志保存的目录
		String savePath="";
		//日志文件的路径
		String logFilePath="";
		FileWriter fw=null;
		PrintWriter pw=null;
		
		String externalStorageState = Environment.getExternalStorageState();
		if(externalStorageState.equals(Environment.MEDIA_MOUNTED)){
			savePath=mSavePath;
			File file=new File(savePath);
			if(!file.exists()){
				file.mkdirs();
			}
			logFilePath=savePath+errlog;
			//没有SD卡，无法写文件
			if(logFilePath==""){
				return;
			}
			try {
			File logFile=new File(logFilePath);
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			fw=new FileWriter(logFile, true);
			pw=new PrintWriter(fw);
			pw.println("-------------------------"+(new Date().toLocaleString())+"--------------");
			ex.printStackTrace(pw);
			pw.close();
			fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(pw!=null){
					pw.close();
				}
				if(fw!=null){
					try {
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
			
		}
		
	}
	
	/**
	 *  保存异常日志
	 */
	public static void saveErrorLog(String errorStr){
		Exception ex=new Exception(errorStr);
		saveErrorLog(ex);
	}
	
	/**
	 *  HTTP错误响应码
	 */
	public static AppException http(int code){
		return new AppException(TYPE_HTTP_CODE, code, new Exception("error http responsecode:"+code));
	}
	/**
	 * 网络异常
	 * @param e
	 * @return
	 */
	public static AppException http(Exception e){
		return new AppException(TYPE_HTTP_ERROR, 0, e);
	}
	
	/**
	 * 读取数据异常
	 */
	public static AppException socket(Exception e){
		return new AppException(TYPE_SOCKET,0,e);
	}
	
	/**
	 * IO操作异常
	 */
	public static AppException io(Exception e){
		if(e instanceof UnknownHostException||e instanceof ConnectException){
			return new AppException(TYPE_CONNECT, 0, e);
		}else if(e instanceof IOException){
			return new AppException(TYPE_IO, 0, e);
		}
		
		return run(e);
	}
	
	/**
	 * XML解析异常
	 */
	public static AppException xml(Exception e){
		return new AppException(TYPE_XML, 0, e);
	}
	
	/**
	 * 网络异常
	 */
	public static AppException network(Exception e){
		if(e instanceof UnknownHostException|| e instanceof ConnectException){
			return new AppException(TYPE_CONNECT, 0, e);
		}else if(e instanceof SocketException){
			return socket(e);
		}
		return http(e);
	}
	
	/**
	 * 运行时异常
	 */
	public static AppException  run(Exception e){
		return new AppException(TYPE_RUN, 0, e);
	}
	
	
	
	
}
