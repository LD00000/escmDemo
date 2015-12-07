package com.sunwayworld.escm.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Collection;
import java.util.Map;

import com.sunwayworld.escm.core.exception.InternalException;

/**
 * Object相关的工具类
 */
public final class ObjectUtils {
	/**
	 * 转换目标{@code T}实例为字符串，{@code null}转为{@code ""}<b>
	 * 该方法主要用于在HTML(jsp)界面上显示
	 * 
	 * @param target 要转换的{@code T}实例
	 * @return 已转换的字符串
	 */
	public static final <T> String toString(final T target) {
		if (target == null) {
			return "";
		}
		
        return ConvertUtils.convert(target, String.class, "");
    }
	
	/**
	 * 判断是否为空,只判断如下几种情况
	 * 1. Null
	 * 2. CharSequence or StringBuilder or StringBuffer = ""
	 * 3. Map.size = 0
	 * 4. Array.size = 0
	 * 5. Collection.size = 0
	 * 
	 * @param target 被判断的Object
	 * @return true 如果空
	 */
	public static final boolean isEmpty(final Object target) {
		if (target == null)
			return true;

		final Class<? extends Object> clazz = target.getClass();

		// 判断是不是CharSequence
		if (CharSequence.class.isAssignableFrom(clazz)) {
			return ((CharSequence)target).length() == 0;
		}
		
		// 判断是不是StringBuilder
		if (target instanceof StringBuilder)
			return ((StringBuilder) target).length()==0;
		
		// 判断是不是StringBuffer
		if (target instanceof StringBuffer)
			return ((StringBuffer) target).length()==0;

		// 判断是不是array
		if (clazz.isArray()) {
			if (Array.getLength(target)==0)
				return true;
			
			return false;
		}
		
		// 判断是不是Collection
		if (Collection.class.isAssignableFrom(clazz)) {
			if (((Collection<?>) target).size()==0)
				return true;
			
			return false;
		}
		
		// 判断是不是Map
		if (Map.class.isAssignableFrom(clazz)) {
			if (((Map<?,?>) target).size()==0)
				return true;
			
			return false;
		}
		
		return false;
	}
	
	/**
	 * 仅用于重构{@link Object}的{@code toString()}方法，重构方式如下
	 * 类名的全称{成员变量值,成员变量值,成员变量值....}
	 * 其中成员变量的值的输出请看 {@link #simpleToString(Object)}
	 * 
	 * @param target 用于被显示成{@link String}
	 * @return String 
	 */
	public static final String reflectionToString(final Object target) {
		if (target == null)
			return null;
		
		final Class<?> clazz = target.getClass();
		
		final StringBuffer buffer = new StringBuffer(clazz.getName()).append("{\n");
		
		final Field[] fields = clazz.getDeclaredFields();
		
		boolean addNewline = false;
		
		for (int i = 0; i<fields.length ; i++) {
			final Field field = fields[i];
			
			if (!Modifier.isPrivate(field.getModifiers()))
				continue;
			
			if (addNewline) {
				buffer.append("\n");
			} else {
				addNewline = !addNewline;
			}
			
			buffer.append(field.getName()).append(":").append(ConvertUtils.convert(FieldUtils.readField(target, field), String.class));
		}
		
		buffer.append("}");
		
		return buffer.toString();
	}
	
	/**
	 * 深度克隆方法，对象必须是可序列化的
	 * 
	 * @param <T> 要克隆的类
	 * @param target 要被克隆的实例
	 * @return 克隆的出来的实例
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T clone(final T target) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(target);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			 return (T) ois.readObject();
		} catch (IOException ioe) {
			throw new InternalException(ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new InternalException(cnfe);
		}
	}
	
	/**
	 * 创建指定类型对应的"空"值，如<br>
	 * <ul>
	 * 基本类型={@code 0}<br>
	 * String={@code ""}<br>
	 * Number={@code 0}<br>
	 * 其他={@code null}<br>
	 * </ul>
	 * 
	 * @param type 指定的类型
	 * @return 对应指定类型的"空"值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T emptyObject(final Class<T> type) {
		if (type == null)
			return null;
		
		if (ClassUtils.isPrimitiveOrWrapper(type)) {
			return ConvertUtils.convert(0, type);
		}
		
		if (Number.class.isAssignableFrom(type)) {
			return ConvertUtils.convert(0, type);
		} else if (String.class.equals(type)) {
			return (T) "";
		}
		
		return null;
	}
	
	/**
	 * 判断两个值是否相等，字符串空和{@code null}是相等
	 * 
	 * @param <T> 左边值的类型
	 * @param <V> 左边值的类型
	 * @param lhs 左边的值
	 * @param rhs 右边的值
	 * @return true 左右值相同或内容相同
	 */
	@SuppressWarnings("unchecked")
	public static<T, V> boolean isEquals(final T lhs, final V rhs) {
		if (lhs == null || lhs.equals("")) {
			return rhs == null || rhs.equals("");
		} else {
			if (rhs == null || rhs.equals(""))
				return false;
		}
		
		final Class<T> lcls = (Class<T>) ClassUtils.primitiveToWrapper(lhs.getClass());
		final Class<V> rcls = (Class<V>) ClassUtils.primitiveToWrapper(rhs.getClass());
		
		// Clob 和 Blob要特殊处理，解决不同的Web Service对其封装不一致的问题
		if ((Clob.class.isAssignableFrom(lcls)
				&& Clob.class.isAssignableFrom(rcls))
				|| (Blob.class.isAssignableFrom(lcls)
						&& Blob.class.isAssignableFrom(rcls))) {
			return ConvertUtils.convert(lhs, String.class, "").equals(ConvertUtils.convert(rhs, String.class, ""));
		}
		
		// 非同一类
		if (!lcls.equals(rcls)) {
			return false;
		}
		
		return lhs.equals(rhs);
	}
}
