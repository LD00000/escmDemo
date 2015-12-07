package com.sunwayworld.escm.core.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Array相关的工具类
 */
public final class ArrayUtils {
	
	/**
	 * 判断一个数组是否含有一个值
	 * 
	 * @param array 要查询的数组
	 * @param value 要查询的值
	 * @return true 如果含有
	 */
	public static final <T> boolean contains(final T[] array, final T value) {
		if (array == null || array.length==0)
			return false;
			
		for (T v : array) {
			if (ObjectUtils.isEquals(v, value))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 判断一个字符串数组是否含有一个字符串，不区分大小写
	 * 
	 * @param array 要查询的字符串数组
	 * @param value 要查询的字符串
	 * @return true 如果含有
	 */
	public static final boolean containsIgnoreCase(final String[] array, final String value) {
		if (array == null || array.length==0)
			return false;
		
		for (String str : array) {
			if ((str == null && value == null) ||
					(str != null && str.equalsIgnoreCase(value)))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 把数组转换成列表
	 * 
	 * @param array 要转换的数组
	 * @return 转换后的列表
	 */
	public static final <T> List<T> arrayToList(final T[] array) {
		final List<T> list = new ArrayList<T>();
		
		for (T instance : array) {
			list.add(instance);
		}
		
		return list;
	}
	
	/**
	 * 把数组转换成列表，还要进行类型转换
	 * 
	 * @param listClass 列表中元素的类型
	 * @param array 要转换的数组
	 * @return 转换后的列表
	 */
	public static final <T, V> List<T> arrayToList(final Class<T> listClass, final V[] array) {
		final List<T> list = new ArrayList<T>();
		
		for (V instance : array) {
			list.add(ConvertUtils.convert(instance, listClass));
		}
		
		return list;
	}
	
	/**
	 * 把数组转换成字符串，中间用分隔符分开
	 * 
	 * @param array 数组
	 * @param separator 分隔符
	 * @return 字符串
	 */
	public static final <T> String toString(T[] array, String separator) {
		if (array == null || array.length == 0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				builder.append(separator);
			}
			builder.append(ConvertUtils.convert(array[i], String.class, ""));
		}
		
		return builder.toString();
	}
	
	/**
	 * 连接两组数组
	 * 
	 * @param first 第一个数组
	 * @param second 第二个数组
	 * @return 连接后的新数组
	 */
	public static final <T> T[] concat(final T[] first, final T[] second) {
		if (first == null || first.length == 0) {
			return second;
		}
		
		if (second == null || second.length == 0) {
			return first;
		}
		
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(first.getClass().getComponentType(), first.length + second.length);
		
		System.arraycopy(first, 0, array, 0, first.length);
		System.arraycopy(second, 0, array, first.length, second.length);
		
		return array;
	}
}
