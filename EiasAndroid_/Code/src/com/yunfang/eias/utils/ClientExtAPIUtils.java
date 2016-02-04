/**
 * 
 */
package com.yunfang.eias.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author kevin 2016-1-28
 */
public class ClientExtAPIUtils {

	// {{获取输入流
	/**
	 * 获取输入流
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static DataInputStream getStream(String filePath) throws Exception {
		URL url1 = new URL(filePath);
		HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
		DataInputStream in = new DataInputStream(conn.getInputStream());
		return in;
	}

	// }}

	// {{调用资源库上传文件
	/**
	 * 调用资源库上传文件
	 * 
	 * @param url
	 * @param fileName
	 *            文件名
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String send(String url, String fileName, DataInputStream in)
			throws IOException {

		String result = "";

		/**
		 * 第一部分
		 */
		URL urlObj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		/**
		 * 设置关键值
		 */
		con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); // post方式不能使用缓存

		// 设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		// 设置边界
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);

		// 请求正文信息

		// 第一部分：
		StringBuilder sb = new StringBuilder();
		sb.append("--"); // ////////必须多两道线
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
				+ fileName + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		// 获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		out.write(head);

		// 文件正文部分
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		// 结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线

		out.write(foot);
		out.flush();
		out.close();

		/**
		 * 读取服务器响应，必须读取,否则提交不成功
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				con.getInputStream(), "utf-8"));
		if (con.getResponseCode() == 200) {
			try {
				String line = null;

				while ((line = reader.readLine()) != null) {
					result += line;
				}
			} catch (Exception e) {
				con.disconnect();
			}
		} else {
			con.disconnect();
		}
		return result;
	}
	// }}
}
