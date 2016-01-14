package com.yunfang.framework.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Json操作类
 * 
 * @author gorson
 * 
 */
public class JSONHelper {

	/**
	 * 将对象转换成Json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJSON(Object obj) {
		JSONStringer js = new JSONStringer();
		serialize(js, obj);
		return js.toString();
	}

	/**
	 * 序列化为JSON
	 * 
	 * @param js
	 * @param o
	 */
	private static void serialize(JSONStringer js, Object o) {
		if (ObjectUtil.isNull(o)) {
			try {
				js.value(null);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}

		Class<?> clazz = o.getClass();
		if (ObjectUtil.isObject(clazz)) { // 对象
			serializeObject(js, o);
		} else if (ObjectUtil.isArray(clazz)) { // 数组
			serializeArray(js, o);
		} else if (ObjectUtil.isCollection(clazz)) { // 集合
			Collection<?> collection = (Collection<?>) o;
			serializeCollect(js, collection);
		} else { // 单个值
			try {
				js.value(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 序列化数组
	 * 
	 * @param js
	 * @param array
	 */
	private static void serializeArray(JSONStringer js, Object array) {
		try {
			js.array();
			for (int i = 0; i < Array.getLength(array); ++i) {
				Object o = Array.get(array, i);
				serialize(js, o);
			}
			js.endArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 序列化集合
	 * 
	 * @param js
	 * @param collection
	 */
	private static void serializeCollect(JSONStringer js,
			Collection<?> collection) {
		try {
			js.array();
			for (Object o : collection) {
				serialize(js, o);
			}
			js.endArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 序列化对象
	 * 
	 * @param js
	 * @param obj
	 */
	private static void serializeObject(JSONStringer js, Object obj) {
		try {
			js.object();
			for (Field f : obj.getClass().getFields()) {
				Object o = f.get(obj);
				js.key(f.getName());
				serialize(js, o);
			}
			js.endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 反序列化简单对象
	 * 
	 * @param jo
	 * @param clazz
	 * @return
	 */
	public static <T> T parseObject(JSONObject jo, Class<T> clazz) {
		if (clazz == null || ObjectUtil.isNull(jo)) {
			return null;
		}
		T obj = createInstance(clazz);
		if (obj == null) {
			return null;
		}

		for (Field f : clazz.getFields()) {
			setField(obj, f, jo);
		}
		return obj;
	}

	/**
	 * 反序列化简单对象
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static <T> T parseObject(String jsonString, Class<T> clazz) {
		if (clazz == null || jsonString == null || jsonString.length() == 0) {
			return null;
		}
		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (ObjectUtil.isNull(jo)) {
			return null;
		}
		return parseObject(jo, clazz);
	}

	/**
	 * 反序列化数组对象
	 * 
	 * @param ja
	 * @param clazz
	 * @return
	 */
	public static <T> T[] parseArray(JSONArray ja, Class<T> clazz) {
		if (clazz == null || ObjectUtil.isNull(ja)) {
			return null;
		}
		int len = ja.length();

		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(clazz, len);

		for (int i = 0; i < len; ++i) {
			try {
				JSONObject jo = ja.getJSONObject(i);
				T o = parseObject(jo, clazz);
				array[i] = o;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	/**
	 * 反序列化数组对象
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static <T> T[] parseArray(String jsonString, Class<T> clazz) {
		if (clazz == null || jsonString == null || jsonString.length() == 0) {
			return null;
		}
		JSONArray jo = null;
		try {
			jo = new JSONArray(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (ObjectUtil.isNull(jo)) {
			return null;
		}
		return parseArray(jo, clazz);
	}

	/**
	 * 反序列化泛型集合
	 * 
	 * @param ja
	 * @param collectionClazz
	 * @param genericType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> parseCollection(JSONArray ja,
			Class<?> collectionClazz, Class<T> genericType) {
		if (collectionClazz == null || genericType == null
				|| ObjectUtil.isNull(ja)) {
			return null;
		}
		Collection<T> collection = (Collection<T>) createInstance(collectionClazz);
		for (int i = 0; i < ja.length(); ++i) {
			try {
				JSONObject jo = ja.getJSONObject(i);
				T o = parseObject(jo, genericType);
				collection.add(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return collection;
	}

	/**
	 * 反序列化泛型集合
	 * 
	 * @param jsonString
	 * @param collectionClazz
	 * @param genericType
	 * @return
	 */
	public static <T> Collection<T> parseCollection(String jsonString,
			Class<?> collectionClazz, Class<T> genericType) {
		if (collectionClazz == null || genericType == null
				|| jsonString == null || jsonString.length() == 0) {
			return null;
		}
		JSONArray jo = null;
		try {
			jo = new JSONArray(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (ObjectUtil.isNull(jo)) {
			return null;
		}
		return parseCollection(jo, collectionClazz, genericType);
	}

	/**
	 * 根据类型创建对象
	 * 
	 * @param clazz
	 * @return
	 */
	private static <T> T createInstance(Class<T> clazz) {
		if (clazz == null) {
			return null;
		}
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 设定字段的值
	 * 
	 * @param obj
	 * @param f
	 * @param jo
	 */
	private static void setField(Object obj, Field f, JSONObject jo) {
		String name = f.getName();
		Class<?> clazz = f.getType();
		try {
			if (ObjectUtil.isArray(clazz)) { // 数组
				Class<?> c = clazz.getComponentType();
				JSONArray ja = jo.optJSONArray(name);
				if (!ObjectUtil.isNull(ja)) {
					Object array = parseArray(ja, c);
					f.set(obj, array);
				}
			} else if (ObjectUtil.isCollection(clazz)) { // 泛型集合
				// 获取定义的泛型类型
				Class<?> c = null;
				Type gType = f.getGenericType();
				if (gType instanceof ParameterizedType) {
					ParameterizedType ptype = (ParameterizedType) gType;
					Type[] targs = ptype.getActualTypeArguments();
					if (targs != null && targs.length > 0) {
						Type t = targs[0];
						c = (Class<?>) t;
					}
				}
				JSONArray ja = jo.optJSONArray(name);
				if (!ObjectUtil.isNull(ja)) {
					Object o = parseCollection(ja, clazz, c);
					f.set(obj, o);
				}
			} else if (ObjectUtil.isSingle(clazz)) { // 值类型
				Object o = jo.opt(name);
				if (o != null) {
					Class<?> tempClass = o.getClass();
					String tempNaString = tempClass.getName();
					if (tempNaString.equalsIgnoreCase("org.json.JSONObject$1")) {
						if (!o.toString().equalsIgnoreCase("null")) {
							f.set(obj, o);
						}
					} else {
						f.set(obj, o);
					}
				}
			} else if (ObjectUtil.isObject(clazz)) { // 对象
				JSONObject j = jo.optJSONObject(name);
				if (!ObjectUtil.isNull(j)) {
					Object o = parseObject(j, clazz);
					f.set(obj, o);
				}
			} else {
				throw new Exception("unknow type!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
