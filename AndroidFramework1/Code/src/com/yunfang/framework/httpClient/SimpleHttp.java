package com.yunfang.framework.httpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 简单的常用请求（GIT / POST）
 * @author gorson
 *
 */
public class SimpleHttp {
	/**
	 * get请求
	 * @param url：请求的地址
	 * @return：如果为真，返回一个字节数组
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static byte [] RequestGet(String url) throws ClientProtocolException,IOException{
		HttpClient client=new DefaultHttpClient();
		HttpGet reg=new HttpGet(url);
		HttpResponse res=null;
		res=client.execute(reg);
		if(res.getStatusLine().getStatusCode()==200){
			return InputStream2bytes(res.getEntity().getContent());
		}
		return null;
	}
	/**
	 * post请求
	 * @param url：请求的地址
	 * @param json：字符串类型的json数据
	 * @return：如果为真，返回一个字节数组
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static byte[] RequestPost(String url,String json) throws MalformedURLException,IOException{
		HttpURLConnection httpcon=(HttpURLConnection)((new URL(url)).openConnection());
		httpcon.setDoOutput(true);
		httpcon.setRequestProperty("Content-Type", "application/json");
		httpcon.setRequestProperty("Accept", "application/json");
		httpcon.setRequestMethod("POST");
		httpcon.connect();
		byte [] outputBytes=json.getBytes("UTF-8");
		OutputStream os=httpcon.getOutputStream();
		os.write(outputBytes);
		os.close();
		
		int status=httpcon.getResponseCode();
		if(status==200){
			return InputStream2bytes(httpcon.getInputStream());
		}
		return null;
		
	}
	
	/**
	 * 输入流转byte数组
	 * @param is：输入流
	 * @return：返回一个字节数组
	 * @throws IOException
	 */
	private static byte [] InputStream2bytes(InputStream is) throws IOException{
		ByteArrayOutputStream byteStream=new ByteArrayOutputStream();
		byte [] buff=new byte[1024];
		int rc=0;
		while((rc=is.read(buff, 0, 1024))>0){
			byteStream.write(buff, 0, rc);
		}
		byte bytes []=byteStream.toByteArray();
		byteStream.close();
		return bytes;
	}

}
