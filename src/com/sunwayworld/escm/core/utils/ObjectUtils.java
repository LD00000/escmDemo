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
 * Object��صĹ�����
 */
public final class ObjectUtils {
	/**
	 * ת��Ŀ��{@code T}ʵ��Ϊ�ַ�����{@code null}תΪ{@code ""}<b>
	 * �÷�����Ҫ������HTML(jsp)��������ʾ
	 * 
	 * @param target Ҫת����{@code T}ʵ��
	 * @return ��ת�����ַ���
	 */
	public static final <T> String toString(final T target) {
		if (target == null) {
			return "";
		}
		
        return ConvertUtils.convert(target, String.class, "");
    }
	
	/**
	 * �ж��Ƿ�Ϊ��,ֻ�ж����¼������
	 * 1. Null
	 * 2. CharSequence or StringBuilder or StringBuffer = ""
	 * 3. Map.size = 0
	 * 4. Array.size = 0
	 * 5. Collection.size = 0
	 * 
	 * @param target ���жϵ�Object
	 * @return true �����
	 */
	public static final boolean isEmpty(final Object target) {
		if (target == null)
			return true;

		final Class<? extends Object> clazz = target.getClass();

		// �ж��ǲ���CharSequence
		if (CharSequence.class.isAssignableFrom(clazz)) {
			return ((CharSequence)target).length() == 0;
		}
		
		// �ж��ǲ���StringBuilder
		if (target instanceof StringBuilder)
			return ((StringBuilder) target).length()==0;
		
		// �ж��ǲ���StringBuffer
		if (target instanceof StringBuffer)
			return ((StringBuffer) target).length()==0;

		// �ж��ǲ���array
		if (clazz.isArray()) {
			if (Array.getLength(target)==0)
				return true;
			
			return false;
		}
		
		// �ж��ǲ���Collection
		if (Collection.class.isAssignableFrom(clazz)) {
			if (((Collection<?>) target).size()==0)
				return true;
			
			return false;
		}
		
		// �ж��ǲ���Map
		if (Map.class.isAssignableFrom(clazz)) {
			if (((Map<?,?>) target).size()==0)
				return true;
			
			return false;
		}
		
		return false;
	}
	
	/**
	 * �������ع�{@link Object}��{@code toString()}�������ع���ʽ����
	 * ������ȫ��{��Ա����ֵ,��Ա����ֵ,��Ա����ֵ....}
	 * ���г�Ա������ֵ������뿴 {@link #simpleToString(Object)}
	 * 
	 * @param target ���ڱ���ʾ��{@link String}
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
	 * ��ȿ�¡��������������ǿ����л���
	 * 
	 * @param <T> Ҫ��¡����
	 * @param target Ҫ����¡��ʵ��
	 * @return ��¡�ĳ�����ʵ��
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
	 * ����ָ�����Ͷ�Ӧ��"��"ֵ����<br>
	 * <ul>
	 * ��������={@code 0}<br>
	 * String={@code ""}<br>
	 * Number={@code 0}<br>
	 * ����={@code null}<br>
	 * </ul>
	 * 
	 * @param type ָ��������
	 * @return ��Ӧָ�����͵�"��"ֵ
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
	 * �ж�����ֵ�Ƿ���ȣ��ַ����պ�{@code null}�����
	 * 
	 * @param <T> ���ֵ������
	 * @param <V> ���ֵ������
	 * @param lhs ��ߵ�ֵ
	 * @param rhs �ұߵ�ֵ
	 * @return true ����ֵ��ͬ��������ͬ
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
		
		// Clob �� BlobҪ���⴦�������ͬ��Web Service�����װ��һ�µ�����
		if ((Clob.class.isAssignableFrom(lcls)
				&& Clob.class.isAssignableFrom(rcls))
				|| (Blob.class.isAssignableFrom(lcls)
						&& Blob.class.isAssignableFrom(rcls))) {
			return ConvertUtils.convert(lhs, String.class, "").equals(ConvertUtils.convert(rhs, String.class, ""));
		}
		
		// ��ͬһ��
		if (!lcls.equals(rcls)) {
			return false;
		}
		
		return lhs.equals(rhs);
	}
}
