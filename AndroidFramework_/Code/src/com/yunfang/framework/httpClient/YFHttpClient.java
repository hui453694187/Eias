package com.yunfang.framework.httpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * http用户请求端
 * 
 * @author gorson
 * 
 */
public class YFHttpClient {

	// {{ 相关属性
	/**
	 * 标志
	 * */
	// private final static String TAG=YFHttpClient.class.getSimpleName();

	/**
	 * 最大的重试次数
	 * */
	private final static int MAX_RETRY_NUM = 1;

	/**
	 * 状态码
	 * */
	// private static int statusCode;

	/**
	 * 用户代理
	 * */
	// private static String sUserAgent;

	// }}

	private YFHttpClient() {

	}

	/**
	 * 发起网络请求，与取服务器交互
	 * 
	 * @param requestPackage
	 * @param requestTask
	 */
	public static void request(IRequestPackage requestPackage,
			IRequestTask requestTask) {
		request(requestPackage, requestTask, true);
	}

	/**
	 * 发起网络请求，与取服务器交互
	 * 
	 * @param requestPackage
	 * @param requestTask
	 * @param isRetry
	 *            是否重试
	 */
	public static void request(IRequestPackage requestPackage,
			IRequestTask requestTask, boolean isRetry) {
		HttpClient httpClient = null;
		HttpResponse httpResponse = null;
		int tryNum = 0;
		boolean isSuccess = true;
		do {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(
					httpParams,
					Integer.parseInt(requestPackage.GetSettings()
							.get("conn-timeout").toString()));
			HttpConnectionParams.setConnectionTimeout(
					httpParams,
					Integer.parseInt(requestPackage.GetSettings()
							.get("socket-timeout").toString()));
			// 开启重定向
			HttpClientParams.setRedirecting(httpParams, true);
			httpClient = new DefaultHttpClient(httpParams);
			try {
				String url = "";
				HttpEntity entity = null;
				switch (requestPackage.GetRequestType()) {
				case GET:
					url = requestPackage.GetUrl()
							+ requestPackage.GetRequestParamsInGetType();
					HttpGet httpGet = new HttpGet(url);
					httpGet.setHeader("User-Agent", "Mozilla/4.5");
					httpResponse = httpClient.execute(httpGet);
					break;
				case POST:
					HttpPost httpPost = new HttpPost(requestPackage.GetUrl());
					entity = new UrlEncodedFormEntity(
							requestPackage.GetRequestParamsInPostType(),
							"UTF-8");
					httpPost.setEntity(entity);
					httpResponse = httpClient.execute(httpPost);
					break;
				case DELETE:
					url = requestPackage.GetUrl()
							+ requestPackage.GetRequestParamsInGetType();
					RESTActionRequest httpRequest = new RESTActionRequest(url,
							requestPackage.GetRequestType());
					// json 处理
					httpRequest.setHeader("Content-Type",
							"application/json; charset=UTF-8");
					httpRequest.setHeader("X-Requested-With", "XMLHttpRequest");
					entity = new UrlEncodedFormEntity(
							requestPackage.GetRequestParamsInPostType(),
							"UTF-8");
					// 设置HttpDelete的请求参数
					httpRequest.setEntity(entity);
					httpRequest.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
					httpRequest.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, 20000);
					httpResponse = httpClient.execute(httpRequest);
					break;
				case PUT:
					break;
				default:
					break;
				}

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					byte b[] = inputStreamToByte(httpResponse.getEntity()
							.getContent());
					if (b != null) {
						requestTask.setContext(b);
					}
				} else {
					requestTask.setContext(httpResponse.getStatusLine()
							.toString().getBytes());
				}
				break;
			} catch (ClientProtocolException e) {
				tryNum++;
				isSuccess = false;
				if (tryNum < MAX_RETRY_NUM) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					continue;
				}
				// e.printStackTrace();
			} catch (IOException e) {
				tryNum++;
				isSuccess = false;
				if (tryNum < MAX_RETRY_NUM) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					continue;
				}
				requestTask.setContext(e.getMessage().getBytes());
				// e.printStackTrace();
			} catch (Exception e) {
				tryNum++;
				isSuccess = false;
				requestTask.setContext(e.getMessage().getBytes());
			} finally {
				try {
					if (isSuccess) {
						httpResponse.getEntity().consumeContent();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					requestTask.setContext(e.getMessage().getBytes());
				} catch (Exception e) {
					requestTask.setContext(e.getMessage().getBytes());
				}
				httpClient = null;
			}
		} while (isRetry && tryNum <= MAX_RETRY_NUM);
	}

	/**
	 * inputstream流换成byte []
	 */
	private static byte[] inputStreamToByte(InputStream in) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		byte[] byteArray = null;
		byte[] b = new byte[1024];
		try {
			while ((len = in.read(b, 0, b.length)) != -1) {
				bos.write(b, 0, len);
			}
			byteArray = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteArray;

	}
}
