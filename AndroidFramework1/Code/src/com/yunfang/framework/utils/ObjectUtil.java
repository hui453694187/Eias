package com.yunfang.framework.utils;

import android.annotation.SuppressLint;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;

/**
 * 对象辅助类
 * 
 * @author gorson
 * 
 */
public class ObjectUtil {
	/**
	 * 根据类型创建对象
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T createInstance(Class<T> clazz) {
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
	 * 根据属性名获取属性值
	 * 
	 * @param fieldName
	 *            :属性名称
	 * @param o
	 *            ：对象
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressLint("DefaultLocale")
	public static Object getFieldValueByName(String fieldName, Object o)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException {
		Object value = null;
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase(Locale.getDefault());
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			value = method.invoke(o, new Object[] {});			
		} catch (Exception e) {
			value = o.getClass().getField(fieldName).get(o);			
		}
		return value;
	}

	/**
	 * 根据属性设置属性值
	 * @param fieldName：属性名称
	 * @param fieldValue：属性值
	 * @param o：对象
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 */
	public static void setFieldValueByName(String fieldName,Object fieldValue,Object o) 
			throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{
		Object value = fieldValue;		
		try {
			String typeName = o.getClass().getField(fieldName).getType().getSimpleName();
			switch(typeName){
			case "Integer":
			case "int":
				value = Integer.parseInt(fieldValue.toString());
				break;
			case "Boolean":
			case "boolean":
				value = Boolean.parseBoolean(fieldValue.toString());
				break;
			}

			String firstLetter = fieldName.substring(0, 1).toUpperCase(Locale.getDefault());
			String getter = "set" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getDeclaredMethod(getter,o.getClass().getField(fieldName).getType());
			method.invoke(o, value);			
		} catch (Exception e) {
			o.getClass().getField(fieldName).set(o,value);			
		}
	}

	// {{ getFieldNames 获取属性名和特性名称
	/**
	 * 获取所有公有的属性名和特性名称
	 * 
	 * @param o
	 * @return
	 */
	public static String[] getFieldNames(Object o) {
		return getFieldNames(o, true);
	}

	/**
	 * 获取所有的属性名和特性名称
	 * 
	 * @param o
	 *            ：对象
	 * @param justPulicProperties
	 *            ：true表示只获取Pulic的属性和特性，false表示获取所有的属性和特性
	 * @return
	 */
	public static String[] getFieldNames(Object o, boolean justPulicProperties) {
		String[] fieldNames = null;
		Field[] fields = null;

		if (justPulicProperties) {
			fields = o.getClass().getFields();
		} else {
			fields = o.getClass().getDeclaredFields();
		}

		if (fields != null) {
			fieldNames = new String[fields.length];
			for (int i = 0; i < fields.length; i++) {
				fieldNames[i] = fields[i].getName();
			}
		}

		return fieldNames;
	}

	/**
	 * 获取属性名(name)和属性类型(type)组成的HashMap<String,String>
	 * @param o：对象
	 * @param justPulicProperties：true表示只获取Pulic的属性和特性，false表示获取所有的属性和特性
	 * @return
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static HashMap<String,Class<?>> getFieldNameAndTypes(Object o, boolean justPulicProperties) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
		HashMap<String,Class<?>> result = new HashMap<String,Class<?>>();

		Field[] fields = null;

		if (justPulicProperties) {
			fields = o.getClass().getFields();
		} else {
			fields = o.getClass().getDeclaredFields();
		}

		String fieldName = "";
		Class<?> fieldTypeName ;
		for (Field field: fields) {	
			fieldName = field.getName(); 
			fieldTypeName = field.getType();

			if(fieldTypeName.getSimpleName().equals(Object.class.getSimpleName())){
				Object temp = getFieldValueByName(fieldName, o);
				if(temp != null){
					fieldTypeName = temp.getClass();
				}
			}

			result.put(fieldName , fieldTypeName);
		}

		return result;
	}	

	/**
	 * 获取属性名(name)和属性类型(type)组成的HashMap<String,String>
	 * @param o：对象
	 * @return
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static HashMap<String,Class<?>> getFieldNameAndTypes(Object o) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{
		return getFieldNameAndTypes(o,true);
	}

	/**
	 * 获取一个对象中所有的公有属性和特性名称
	 * @param obj
	 * @return key为小写的属性或特性名称，Value为正常的属性或特性名称
	 */
	public static HashMap<String,String> getFieldContractNames(Object obj){	
		return getFieldContractNames(obj,true);
	}

	/**
	 *  获取一个对象中所有的属性和特性名称
	 * @param obj
	 * @param justPulicProperties ：true表示只获取Pulic的属性和特性，false表示获取所有的属性和特性
	 * @return key为小写的属性或特性名称，Value为正常的属性或特性名称
	 */
	public static HashMap<String,String> getFieldContractNames(Object obj, boolean justPulicProperties){		
		HashMap<String, String> fields = new HashMap<String, String>();

		String[] fieldNames = ObjectUtil.getFieldNames(obj,justPulicProperties);
		for(int i=0;i<fieldNames.length;i++){
			fields.put(fieldNames[i].toLowerCase(Locale.getDefault()),fieldNames[i]);
		}

		return fields;
	}
	// }}

	//{{ getPropertyValues 获取对象的属性值和对应值，返回一个对象数组
	/**
	 * 获取对象的属性值和对应值，返回一个对象数组
	 * 
	 * @param o
	 *            ：对象
	 * @param justPulicProperties
	 *            ：true表示只获取Pulic的属性和特性，false表示获取所有的属性和特性
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 */
	public static HashMap<String, Object> getPropertyValues(Object o,
			boolean justPulicProperties) throws IllegalAccessException,
			IllegalArgumentException, NoSuchFieldException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		String[] fieldNames = getFieldNames(o, justPulicProperties);
		if (fieldNames != null) {
			for (int i = 0; i < fieldNames.length; i++) {
				result.put(fieldNames[i], getFieldValueByName(fieldNames[i], o));
			}
		}

		return result;
	}

	/**
	 * 获取对象的属性值和对应值，返回一个对象数组
	 * @param o
	 *            ：对象
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 */
	public static HashMap<String, Object> getPropertyValues(Object o) 
			throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{		
		return getPropertyValues(o,true);
	}
	//}}

	/**
	 * 获取对象的名称，只有名称，不包含包名路径
	 * 
	 * @param o
	 *            ：对象
	 * @return
	 */
	public static String getObjectTypeSimpleName(Object o) {
		return o.getClass().getSimpleName();
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj) {
		if (obj instanceof JSONObject) {
			return JSONObject.NULL.equals(obj);
		}
		return obj == null;
	}

	/**
	 * 判断是否是值类型
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isSingle(Class<?> clazz) {
		return isBoolean(clazz) || isNumber(clazz) || isString(clazz);
	}

	/**
	 * 是否布尔值
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isBoolean(Class<?> clazz) {
		return (clazz != null)
				&& ((Boolean.TYPE.isAssignableFrom(clazz)) || (Boolean.class
						.isAssignableFrom(clazz)));
	}

	/**
	 * 是否数值
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isNumber(Class<?> clazz) {
		return (clazz != null)
				&& ((Byte.TYPE.isAssignableFrom(clazz))
						|| (Short.TYPE.isAssignableFrom(clazz))
						|| (Integer.TYPE.isAssignableFrom(clazz))
						|| (Long.TYPE.isAssignableFrom(clazz))
						|| (Float.TYPE.isAssignableFrom(clazz))
						|| (Double.TYPE.isAssignableFrom(clazz)) || (Number.class
								.isAssignableFrom(clazz)));
	}

	/**
	 * 判断是否是字符串
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isString(Class<?> clazz) {
		return (clazz != null)
				&& ((String.class.isAssignableFrom(clazz))
						|| (Character.TYPE.isAssignableFrom(clazz)) || (Character.class
								.isAssignableFrom(clazz)));
	}

	/**
	 * 判断是否是对象
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isObject(Class<?> clazz) {
		return clazz != null && !isSingle(clazz) && !isArray(clazz)
				&& !isCollection(clazz);
	}

	/**
	 * 判断是否是数组
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isArray(Class<?> clazz) {
		return clazz != null && clazz.isArray();
	}

	/**
	 * 判断是否是集合
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isCollection(Class<?> clazz) {
		return clazz != null && Collection.class.isAssignableFrom(clazz);
	}
}
