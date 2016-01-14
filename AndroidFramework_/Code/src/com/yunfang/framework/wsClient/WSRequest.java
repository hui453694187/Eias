package com.yunfang.framework.wsClient;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import com.yunfang.framework.model.ResultInfo;

/**
 * WebService调用类
 * 
 * @author gorson
 * 
 */
public class WSRequest {
	/**
	 * 调用WebService
	 * 
	 * @param nameSpace
	 *            :命名空间
	 * @param wsAddress
	 *            :完整的WS地址
	 * @param MethodName
	 *            :调用的方法名称
	 * @param params
	 *            ：参数值
	 * @return SoapObject对象
	 * @throws HttpResponseException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	// public static SoapObject request(String nameSpace,String wsAddress,String
	// MethodName, Map<String, String> params)
	// throws HttpResponseException, IOException, XmlPullParserException {
	// // 1、指定webservice的命名空间和调用的方法名
	// SoapObject request = new SoapObject(nameSpace, MethodName);
	//
	// // 2、设置调用方法的参数值，如果没有参数，可以省略，
	// if (params != null) {
	// Iterator<Entry<String, String>> iter = params.entrySet().iterator();
	// while (iter.hasNext()) {
	// Map.Entry<String, String> entry = (Map.Entry<String, String>)
	// iter.next();
	// request.addProperty((String) entry.getKey(),
	// (String) entry.getValue());
	// }
	// }
	// // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
	// SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	// SoapEnvelope.VER12);
	// envelope.bodyOut = request;
	// // c#写的应用程序必须加上这句
	// envelope.dotNet = true;
	// HttpTransportSE ht = new HttpTransportSE(wsAddress);
	// // 使用call方法调用WebService方法
	// ht.call(null, envelope);
	// return (SoapObject) envelope.bodyIn;
	// }

	/**
	 * 调用WebService
	 * 
	 * @param SERVER_URL
	 *            :需要获取的内容WS地址
	 * @param params
	 *            :参数值
	 * @return
	 */
	public static String request(String SERVER_URL,
			ArrayList<NameValuePair> params) {
		String result = "";
		try {
			HttpPost request = new HttpPost(SERVER_URL); // 根据内容来源地址创建一个Http请求
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); // 设置参数的编码
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request); // 发送请求并获取反馈
			// 解析返回的内容
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
			} else {
				ResultInfo<String> errorrResultInfo = new ResultInfo<String>();
				errorrResultInfo.Success = false;
				errorrResultInfo.Message = "连接失败,错误代号："
						+ httpResponse.getStatusLine().getStatusCode();
				result = errorrResultInfo.toXMLString();
			}
		} catch (Exception ex) {
			ResultInfo<String> errorrResultInfo = new ResultInfo<String>();
			errorrResultInfo.Success = false;
			errorrResultInfo.Message = "连接错误" + ex.getMessage();
			try {
				result = errorrResultInfo.toXMLString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

}
