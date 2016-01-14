package com.yunfang.framework.httpClient;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class RESTActionRequest extends HttpEntityEnclosingRequestBase {
	/**
	 * 当前Http访问类型
	 */
	private RequestTypeEnum HTTP_METHOD;

	/**
	 * 构造函数
	 * 
	 * @param uri
	 * @param httpMethod 当前Http访问类型
	 */
	public RESTActionRequest(final String uri, RequestTypeEnum httpMethod) {
		super();
		setURI(URI.create(uri));
		HTTP_METHOD = httpMethod;
	}

	/**
	 * 构造函数
	 * @param uri
	 * @param httpMethod 当前Http访问类型
	 */
	public RESTActionRequest(final URI uri, RequestTypeEnum httpMethod) {
		super();
		setURI(uri);
		HTTP_METHOD = httpMethod;
	}

	/**
	 * 构造函数
	 * @param httpMethod 当前Http访问类型
	 */
	public RESTActionRequest(RequestTypeEnum httpMethod) {
		super();
		HTTP_METHOD = httpMethod;
	}

	/**
	 * 返回当前Http访问类型
	 */
	@Override
	public String getMethod() {		
		return HTTP_METHOD.getName();
	}
}
