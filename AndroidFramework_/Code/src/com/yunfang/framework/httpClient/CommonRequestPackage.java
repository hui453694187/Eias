package com.yunfang.framework.httpClient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.yunfang.framework.enumObj.NetType;
import com.yunfang.framework.utils.NetWorkUtil;

/**
 * Request访问公共类
 * 
 * @author gorson
 * 
 */
public class CommonRequestPackage implements IRequestPackage {
	// {{ 变量
	/**
	 * 请求参数集
	 * */
	protected Hashtable<String, Object> mParams;

	/**
	 * 请求Url地址
	 */
	protected String mUrl;

	/**
	 * 访问类型
	 */
	protected RequestTypeEnum mRequestType;

	// }}

	// {{ 构造函数
	/**
	 * 构造函数
	 * 
	 * @param url
	 *            访问的URL值
	 * @param requestType
	 *            访问类型
	 */
	public CommonRequestPackage(String url, RequestTypeEnum requestType) {
		mUrl = url;
		mRequestType = requestType;
	}

	/**
	 * 构造函数
	 * 
	 * @param url
	 *            访问的URL值
	 * @param requestType
	 *            访问类型
	 * @param params
	 *            参数信息
	 */
	public CommonRequestPackage(String url, RequestTypeEnum requestType,
			Hashtable<String, Object> params) {
		mUrl = url;
		mRequestType = requestType;
		mParams = params;
	}

	// }}

	@Override
	public Hashtable<String, String> GetRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetRequestParamsInGetType() {
		if (mParams != null && mParams.size() > 0) {
			StringBuilder builder = new StringBuilder();
			// 后台用php时，使用?,后台使用python时，使用/
			builder.append("?");
			final Set<String> keys = mParams.keySet();
			for (String key : keys) {
				builder.append(key).append("=")
						.append(mParams.get(key).toString().trim()).append("&");
			}
			builder.append("randomT=").append(new Date().getTime());
			// builder.deleteCharAt(builder.length()-1);
			return builder.toString();
		}
		return "";
	}

	@Override
	public List<BasicNameValuePair> GetRequestParamsInPostType() {
		List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>();
		Iterator<String> iterators = mParams.keySet().iterator();
		while (iterators.hasNext()) {
			String key = iterators.next();
			BasicNameValuePair pair = new BasicNameValuePair(key, mParams.get(
					key).toString());
			result.add(pair);
		}
		return result;
	}

	@Override
	public HttpEntity GetRequetEntityInPostType() {
		HttpEntity httpEntity = null;
		// TODO Auto-generated method stub
		try {
			httpEntity = new UrlEncodedFormEntity(GetRequestParamsInPostType(),
					"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return httpEntity;
	}

	@Override
	public String GetUrl() {
		return mUrl;
	}

	@Override
	public RequestTypeEnum GetRequestType() {
		return mRequestType;
	}

	@Override
	public Hashtable<String, Object> GetSettings() {
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		String networkType = NetWorkUtil.getNetworkType().getName();
		if (NetType.Type_wifi.getName().equals(networkType)
				|| NetType.Type_4g.getName().equals(networkType)) {
			params.put("conn-timeout", 15 * 1000);
			params.put("socket-timeout", 15 * 1000);
		} else {
			params.put("conn-timeout", 20 * 1000);
			params.put("socket-timeout", 20 * 1000);
		}
		return params;
	}

	/**
	 * 设置请求参数
	 * */
	@Override
	public void SetParams(Hashtable<String, Object> mParams) {
		this.mParams = mParams;
	}
}
