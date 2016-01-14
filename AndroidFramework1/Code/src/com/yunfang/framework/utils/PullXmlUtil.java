package com.yunfang.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.yunfang.framework.base.BaseApplication;

import android.util.Xml;

/**
 * 使用xml pull解释器操作XML
 * 
 * @author gorson
 * 
 */
public class PullXmlUtil {	
	// {{ parseObj XML转为单个对象

	/**
	 * XML文件流转成对象
	 * 
	 * @param xmlStream
	 *            ：XML文件流
	 * @param clazz
	 *            ：转换类型
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	public static <T> T parseObj(InputStream xmlStream, Class<T> clazz)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException, XmlPullParserException, IOException,
			ClassNotFoundException, InstantiationException {
		if (clazz == null || xmlStream == null) {
			return null;
		}
		T result = ObjectUtil.createInstance(clazz);
		if (result == null) {
			return null;
		}

		return parseObj(xmlStream, result);
	}

	/**
	 * XML文件流转成对象
	 * 
	 * @param xmlStream
	 *            ：XML文件流
	 * @param result
	 *            ：返回对象
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	public static <T> T parseObj(InputStream xmlStream, T result)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException, XmlPullParserException, IOException,
			ClassNotFoundException, InstantiationException {
		if (result == null) {
			return null;
		}

		return parseObj(result, xmlStream);
	}

	/**
	 * XML字條串转成对象
	 * 
	 * @param xmlStr
	 *            ：XML字符串
	 * @param result
	 *            ：返回对象
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	public static <T> T parseObj(String xmlStr, T result)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException, XmlPullParserException, IOException,
			ClassNotFoundException, InstantiationException {
		if (result == null) {
			return null;
		}
		return parseObj(result, xmlStr);
	}

	/**
	 * XML字條串转成对象
	 * 
	 * @param xmlStr
	 *            ：XML字符串
	 * @param clazz
	 *            ：转换类型
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	public static <T> T parseObj(String xmlStr, Class<T> clazz)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException, XmlPullParserException, IOException,
			ClassNotFoundException, InstantiationException {
		if (clazz == null || xmlStr == null || xmlStr.length() == 0) {
			return null;
		}
		T result = ObjectUtil.createInstance(clazz);
		if (result == null) {
			return null;
		}
		return parseObj(xmlStr, result);
	}

	/**
	 * 将XML值赋值到对象
	 * 
	 * @param result
	 *            ：结果对象
	 * @param xmlStream
	 *            ：XML文件流
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	private static <T> T parseObj(T result, InputStream xmlStream)
			throws XmlPullParserException, IOException, IllegalAccessException,
			IllegalArgumentException, NoSuchFieldException,
			ClassNotFoundException, InstantiationException {
		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(xmlStream, "UTF-8"); // 设置输入流 并指明编码方式
		return parseObj(result, parser, "");
	}

	/**
	 * 将XML值赋值到对象
	 * 
	 * @param result
	 *            ：结果对象
	 * @param xmlStr
	 *            :XML字符串
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	private static <T> T parseObj(T result, String xmlStr)
			throws XmlPullParserException, IOException, IllegalAccessException,
			IllegalArgumentException, NoSuchFieldException,
			ClassNotFoundException, InstantiationException {
		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(new StringReader(xmlStr));
		return parseObj(result, parser, "");
	}

	/**
	 * 将XML值赋值到对象
	 * 
	 * @param result
	 *            ：结果对象
	 * @param parser
	 *            :XML PULL解释对象
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> T parseObj(T result, XmlPullParser parser,
			String endTagName) throws XmlPullParserException, IOException,
			IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException, ClassNotFoundException,
			InstantiationException {
		HashMap<String, String> fields = ObjectUtil
				.getFieldContractNames(result);
		HashMap<String, Class<?>> fieldTypes = ObjectUtil
				.getFieldNameAndTypes(result);
		String className = ObjectUtil.getObjectTypeSimpleName(result)
				.toLowerCase(Locale.getDefault());

		int eventType = parser.getEventType();
		String nodeName = "";

		Class<?> tempType = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				nodeName = parser.getName().toLowerCase(Locale.getDefault());
				if (nodeName.equals(className)) {
					String tempAttr = "";
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						tempAttr = parser.getAttributeName(i).toLowerCase(
								Locale.getDefault());
						if (fields.containsKey(tempAttr)) {
							ObjectUtil.setFieldValueByName(
									fields.get(tempAttr),
									parser.getAttributeValue(i), result);
						}
					}
				} else {
					if(nodeName.equals(endTagName) && className.equals("object") && fields.size()==0 && fieldTypes.size()==0){
						eventType = parser.next();
						result = (T)parser.getText(); 
					}
					if (fields.containsKey(nodeName)) {
						tempType = fieldTypes.get(fields.get(nodeName));
						if (ObjectUtil.isCollection(tempType)) {
							// String tempCollectionStr = parser.getText();
							Class<?> tempSubClass = getCollectionTClass(parser);
							if (tempSubClass != null) {
								Collection tempCollection = (Collection) ObjectUtil.createInstance(tempType);
								tempCollection = parseCollection(
										tempCollection, parser, tempSubClass,
										nodeName);
								ObjectUtil.setFieldValueByName(
										fields.get(nodeName), tempCollection,
										result);
							}
						} else if (ObjectUtil.isObject(tempType)) {
							Object tempObj = tempType.newInstance();
							tempObj = parseObj(tempObj, parser, nodeName);
							ObjectUtil.setFieldValueByName(
									fields.get(nodeName), tempObj, result);
						} else {
							eventType = parser.next();
							ObjectUtil.setFieldValueByName(
									fields.get(nodeName), parser.getText(),
									result);
						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if (!endTagName.equals("")) {
					nodeName = parser.getName()
							.toLowerCase(Locale.getDefault());
					if (nodeName.equals(endTagName)) {
						return result;
					}
				}
				break;
			}
			eventType = parser.next();
		}

		return result;
	}

	/**
	 * 通过XML中的节点，找到对应的类
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static Class<?> getCollectionTClass(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		XmlPullParser tempParser = Xml.newPullParser();
		tempParser = parser;
		int tempEventType = tempParser.getEventType();
		String tempNodeName = "";
		Class<?> subClass = null;
		ArrayList<String> packageInfoNames = BaseApplication.getInstance()
				.getPackageInfoNames();
		while (tempEventType != XmlPullParser.END_DOCUMENT && subClass == null) {
			switch (tempEventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				tempNodeName = tempParser.getName();

				switch (tempNodeName.toLowerCase(Locale.getDefault())) {
				case "string":
					subClass = String.class;
					break;
				case "integer":
				case "int":
					subClass = Integer.class;
					break;
				case "date":
					subClass = Date.class;
					break;
				case "boolean":
					subClass = Boolean.class;
					break;
				default:
					break;
				}

				if (subClass == null) {
					for (int i = 0; i < packageInfoNames.size(); i++) {
						try {
							subClass = Class.forName(packageInfoNames.get(i)
									+ "." + tempNodeName);
							break;
						} catch (Exception e) {

						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			tempEventType = tempParser.next();
		}
		return subClass;
	}

	// }}

	// {{ parseCollection XML转为集合对象
	/**
	 * 
	 * @param xmlStr
	 *            :XML字符串值
	 * @param collectionClazz
	 *            ：集合类型
	 * @param clazz
	 *            ：泛型的类型
	 * @return
	 * @throws XmlPullParserException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> parseCollection(String xmlStr,
			Class<?> collectionClazz, Class<T> clazz)
					throws XmlPullParserException, IllegalAccessException,
					IllegalArgumentException, NoSuchFieldException, IOException {
		if (collectionClazz == null || clazz == null || xmlStr == null
				|| xmlStr.length() == 0) {
			return null;
		}
		Collection<T> result = (Collection<T>) ObjectUtil.createInstance(collectionClazz);
		if (result == null) {
			return null;
		}
		return parseCollection(result, xmlStr, clazz);
	}

	/**
	 * XML转为集合对象
	 * 
	 * @param xmlStream
	 *            ：XML文件流
	 * @param collectionClazz
	 *            ：集合类型
	 * @param clazz
	 *            ：泛型的类型
	 * @return
	 * @throws XmlPullParserException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> parseCollection(InputStream xmlStream,
			Class<?> collectionClazz, Class<T> clazz)
					throws XmlPullParserException, IllegalAccessException,
					IllegalArgumentException, NoSuchFieldException, IOException {
		if (collectionClazz == null || clazz == null || xmlStream == null) {
			return null;
		}
		Collection<T> result = (Collection<T>) ObjectUtil.createInstance(collectionClazz);
		if (result == null) {
			return null;
		}
		return parseCollection(result, xmlStream, clazz);
	}

	/**
	 * XML转为集合对象
	 * 
	 * @param result
	 *            ：返回结果值
	 * @param xmlStr
	 *            :XML字符串值
	 * @param clazz
	 *            ：泛型的类型
	 * @return
	 * @throws XmlPullParserException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IOException
	 */
	private static <T> Collection<T> parseCollection(Collection<T> result,
			String xmlStr, Class<T> clazz) throws XmlPullParserException,
			IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException, IOException {
		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(new StringReader(xmlStr));
		return parseCollection(result, parser, clazz, "");
	}

	/**
	 * XML转为集合对象
	 * 
	 * @param result
	 *            ：返回结果值
	 * @param xmlStream
	 *            :XML文件流
	 * @param clazz
	 *            ：泛型的类型
	 * @return
	 * @throws XmlPullParserException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IOException
	 */
	private static <T> Collection<T> parseCollection(Collection<T> result,
			InputStream xmlStream, Class<T> clazz)
					throws XmlPullParserException, IllegalAccessException,
					IllegalArgumentException, NoSuchFieldException, IOException {
		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(xmlStream, "UTF-8"); // 设置输入流 并指明编码方式
		return parseCollection(result, parser, clazz, "");
	}

	/**
	 * XML转为集合对象
	 * 
	 * @param result
	 *            ：返回结果值
	 * @param parser
	 *            ：XML解释器
	 * @param clazz
	 *            ：泛型的类型
	 * @param endTagName
	 *            ：结束节点
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 */
	@SuppressWarnings("unchecked")
	private static <T> Collection<T> parseCollection(Collection<T> result,
			XmlPullParser parser, Class<T> clazz, String endTagName)
					throws XmlPullParserException, IOException, IllegalAccessException,
					IllegalArgumentException, NoSuchFieldException {
		T obj = ObjectUtil.createInstance(clazz);
		HashMap<String, String> fields = new HashMap<>();
		if (obj != null) {
			fields = ObjectUtil.getFieldContractNames(obj);
		}
		String className = clazz.getSimpleName().toLowerCase(
				Locale.getDefault());
		int eventType = parser.getEventType();
		// if(eventType)
		String preText = parser.getText();		
		boolean first = true;
		String nodeName = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				first = false;
				break;
			case XmlPullParser.START_TAG:
				first = false;
				nodeName = parser.getName().toLowerCase(Locale.getDefault());
				boolean deal = false;
				if (nodeName.equals(className)
						|| (nodeName.equals("int") && className
								.equals("integer"))) {
					obj = ObjectUtil.createInstance(clazz);
					switch (nodeName) {
					case "integer":
					case "int":
					case "string":
					case "boolean":
					case "date":
						eventType = parser.next();
						obj = (T) parser.getText();
						deal = true;
						break;
					default:
						break;
					}
					if (!deal) {
						String tempAttr = "";
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							tempAttr = parser.getAttributeName(i).toLowerCase(
									Locale.getDefault());
							if (fields.containsKey(tempAttr)) {
								ObjectUtil.setFieldValueByName(
										fields.get(tempAttr),
										parser.getAttributeValue(i), obj);
							}
						}
					}
				} else {
					if (fields.containsKey(nodeName)) {
						eventType = parser.next();
						ObjectUtil.setFieldValueByName(fields.get(nodeName),
								parser.getText(), obj);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				nodeName = parser.getName().toLowerCase(Locale.getDefault());
				if (nodeName.equals(className)
						|| (nodeName.equals("int") && className
								.equals("integer"))) {
					if (first) {						
						if(preText == null){
							String tempAttr;
							for (int i = 0; i < parser.getAttributeCount(); i++) {
								tempAttr = parser.getAttributeName(i).toLowerCase(
										Locale.getDefault());
								if (fields.containsKey(tempAttr)) {
									ObjectUtil.setFieldValueByName(
											fields.get(tempAttr),
											parser.getAttributeValue(i), obj);
								}
							}
						}else{
							obj = (T) preText;
						}
					}
					result.add(obj);
				} else if (nodeName.equals(endTagName)) {
					return result;
				}
				first = false;
				break;
			}
			eventType = parser.next();
		}

		return result;
	}

	// }}

	// {{ serialize 将对象转为XML字符串

	/**
	 * /** 单个对象转XML
	 * 
	 * @param t
	 *            :对象
	 * @param needClassNode
	 *            ：是否需要有类级节点
	 * @param serializer
	 * @param writer
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> String serializeObject(T t, boolean needClassNode,
			XmlSerializer serializer, StringWriter writer) throws Exception {
		boolean endDoc = false;
		if (serializer == null) {
			writer = new StringWriter();
			serializer = Xml.newSerializer();
			serializer.setOutput(writer); // 设置输出方向为writer
			serializer.startDocument("UTF-8", true);
			endDoc = true;
		}
		String className = ObjectUtil.getObjectTypeSimpleName(t);
		if (needClassNode) {
			serializer.startTag("", className);
		}
		HashMap<String, Object> values = ObjectUtil.getPropertyValues(t);
		Iterator<Entry<String, Object>> iter = values.entrySet().iterator();
		Object tempObj = null;
		Class<?> tempClazz = null;
		String tempValueStr = null;
		while (iter.hasNext()) {
			Map.Entry<String, Object> value = (Map.Entry<String, Object>) iter
					.next();
			serializer.startTag("", value.getKey());
			tempObj = value.getValue();
			tempClazz = tempObj.getClass();
			tempValueStr = "";
			if (tempObj != null) {
				tempValueStr = value.getValue().toString();
				if (ObjectUtil.isCollection(tempClazz)) {
					tempValueStr = serializeCollection((Collection) tempObj,
							false, serializer, writer)
							.replace(
									"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>",
									"");
				} else if (ObjectUtil.isObject(tempClazz)) {
					tempValueStr = serializeObject(tempObj, false, serializer,
							writer)
							.replace(
									"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>",
									"");
				} else {
					serializer.text(tempValueStr);
				}
			}

			serializer.endTag("", value.getKey());
		}
		if (needClassNode) {
			serializer.endTag("", className);
		}

		if (endDoc) {
			serializer.endDocument();
		}

		return writer.toString();
	}

	/**
	 * 单个对象转XML
	 * 
	 * @param t
	 *            ：对象
	 * @return
	 * @throws Exception
	 */
	public static <T> String serializeObject(T t) throws Exception {
		return serializeObject(t, true, null, null);
	}

	/**
	 * 
	 * @param collection
	 * @param needClassNode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> String serializeCollection(Collection<T> collection,
			boolean needClassNode, XmlSerializer serializer, StringWriter writer)
					throws Exception {
		boolean endDoc = false;
		if (serializer == null) {
			serializer = Xml.newSerializer(); // 由android.util.Xml创建一个XmlSerializer实例
			writer = new StringWriter();
			serializer.setOutput(writer); // 设置输出方向为writer
			serializer.startDocument("UTF-8", true);
			endDoc = true;
		}
		if (collection != null && collection.size() > 0) {
			String className = ObjectUtil.getObjectTypeSimpleName(collection
					.toArray()[0]);
			String collectionName = className + "List";

			boolean singleObj = false;

			if (needClassNode) {
				serializer.startTag("", collectionName);
			}

			Object tempObj = null;
			Class<?> tempClazz = null;
			String tempValueStr = null;

			// 加一个for循环
			for (T obj : collection) {
				className = ObjectUtil.getObjectTypeSimpleName(obj);
				switch (className.toLowerCase(Locale.getDefault())) {
				case "integer":
				case "int":
					className = "int";
				case "string":
				case "boolean":
				case "date":
					singleObj = true;
				}

				serializer.startTag("", className);

				if (singleObj) {
					serializer.text(obj.toString());
				} else {
					HashMap<String, Object> values = ObjectUtil
							.getPropertyValues(obj);
					Iterator<Entry<String, Object>> iter = values.entrySet()
							.iterator();
					while (iter.hasNext()) {
						Map.Entry<String, Object> value = (Map.Entry<String, Object>) iter
								.next();
						serializer.startTag("", value.getKey());
						tempObj = value.getValue();
						tempClazz = tempObj.getClass();
						tempValueStr = "";
						if (value.getValue() != null) {
							tempValueStr = value.getValue().toString();
							if (ObjectUtil.isCollection(tempClazz)) {
								tempValueStr = serializeCollection(
										(Collection) tempObj, false,
										serializer, writer)
										.replace(
												"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>",
												"");
							}
							if (ObjectUtil.isObject(tempClazz)) {
								tempValueStr = serializeObject(tempObj, false,
										serializer, writer)
										.replace(
												"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>",
												"");
							}
						}
						serializer.text(tempValueStr);
						serializer.endTag("", value.getKey());
					}
				}
				serializer.endTag("", className);
			}
			if (needClassNode) {
				serializer.endTag("", collectionName);
			}
			if (endDoc) {
				serializer.endDocument();
			}
		}
		return writer.toString();
	}

	/**
	 * 集合转成XML字符串
	 * 
	 * @param collection
	 *            ：集合列表对象
	 * @return
	 * @throws Exception
	 */
	public static <T> String serializeCollection(Collection<T> collection)
			throws Exception {
		return serializeCollection(collection, true, null, null);
	}
	// }}
}
