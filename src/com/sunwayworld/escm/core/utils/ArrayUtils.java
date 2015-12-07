package com.sunwayworld.escm.core.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Array��صĹ�����
 */
public final class ArrayUtils {
	
	/**
	 * �ж�һ�������Ƿ���һ��ֵ
	 * 
	 * @param array Ҫ��ѯ������
	 * @param value Ҫ��ѯ��ֵ
	 * @return true �������
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
	 * �ж�һ���ַ��������Ƿ���һ���ַ����������ִ�Сд
	 * 
	 * @param array Ҫ��ѯ���ַ�������
	 * @param value Ҫ��ѯ���ַ���
	 * @return true �������
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
	 * ������ת�����б�
	 * 
	 * @param array Ҫת��������
	 * @return ת������б�
	 */
	public static final <T> List<T> arrayToList(final T[] array) {
		final List<T> list = new ArrayList<T>();
		
		for (T instance : array) {
			list.add(instance);
		}
		
		return list;
	}
	
	/**
	 * ������ת�����б���Ҫ��������ת��
	 * 
	 * @param listClass �б���Ԫ�ص�����
	 * @param array Ҫת��������
	 * @return ת������б�
	 */
	public static final <T, V> List<T> arrayToList(final Class<T> listClass, final V[] array) {
		final List<T> list = new ArrayList<T>();
		
		for (V instance : array) {
			list.add(ConvertUtils.convert(instance, listClass));
		}
		
		return list;
	}
	
	/**
	 * ������ת�����ַ������м��÷ָ����ֿ�
	 * 
	 * @param array ����
	 * @param separator �ָ���
	 * @return �ַ���
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
	 * ������������
	 * 
	 * @param first ��һ������
	 * @param second �ڶ�������
	 * @return ���Ӻ��������
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
