package com.sunwayworld.escm.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.sunwayworld.escm.core.exception.InternalException;

/**
 * {@link Field}������صĹ�����
 */
public final class FieldUtils {
	
	private static final Logger logger = Logger.getLogger(FieldUtils.class);
	
	private FieldUtils() {}
	
	/**
	 * ͨ����Ա���������ƻ�ȡ��Ӧ��{@link Field}������Ҳ�ᱻ��ѯ���������ִ�Сд�ж�
	 * 
	 * @param clazz Ҫ���������, ����{@code null}
	 * @param fieldName ��Ա����������, ����Ϊ��
	 * @return {@link Field}  ��  {@code null}
	 */
	public static final Field getExactField(final Class<?> clazz, final String fieldName) {
		return getField(clazz, fieldName, false);
		
	}
	
	/**
	 * ͨ����Ա���������ƻ�ȡ��Ӧ��{@link Field}������Ҳ�ᱻ��ѯ�����Ʋ����ִ�Сд�ж�
	 * 
	 * @param clazz Ҫ���������, ����{@code null}
	 * @param fieldName ��Ա����������, ����Ϊ��
	 * @return {@link Field}  ��  {@code null}
	 */
	public static final Field getField(final Class<?> clazz, final String fieldName) {
		return getField(clazz, fieldName, true);
	}
	
	/**
	 * ͨ����Ա���������ͻ�ȡ��Ӧ��{@link Field}������Ҳ�ᱻ��ѯ
	 * 
	 * @param clazz Ҫ���������, ����{@code null}
	 * @param type ��Ա����������, ����{@code null}
	 * @return ��������������{@link Field} {@link List}
	 */
	public static final List<Field> getTypeFields(final Class<?> clazz, final Class<?> type) {
		final List<Field> fields = new ArrayList<Field>();
		
		for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
			for (Field field : cls.getDeclaredFields()) {
				if (type.isAssignableFrom(field.getType())) 
					fields.add(field);
			}
		}
		
		return fields;
	}
	
	/**
	 * ͨ��ע�͵�������ȡ��ע�͵�{@link Field}������Ҳ�ᱻ��ѯ
	 * 
	 * 
	 * @param clazz Ҫ���������, ����{@code null}
	 * @param annotation ע�͵�����, ����{@code null}
	 * @return ��������������{@link Field} {@link List}
	 */
	public static final List<Field> getAnnotationPresentFields(final Class<?> clazz, 
			final Class<? extends Annotation> annotation) {
		List<Field> fields = new ArrayList<Field>();
		
		for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
			for (Field field : cls.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation))
					fields.add(field);
			}
		}
		
		return fields;
	}
	
	/**
     * ��ȡ��Ա���� {@link Field} ��ֵ��
     * 
     * @param target Ҫ�����õ�{@link Object}, ����{@code null}
     * @param field Ҫ��ʹ�õĳ�Ա����, ����{@code null}
     * @return ��Ա������ֵ�� {@code null}
     */
    public static Object readField(final Object target, final Field field) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		
        try {
			return field.get(target);
		} catch (IllegalArgumentException iae) {
			throw new InternalException(iae);
		} catch (IllegalAccessException iae) {
			throw new InternalException(iae);
		}
    }
    
    /**
     * ��ȡ��Ա���� {@link Field} ��ֵ��ֻ�ܶ�ȡ��{@code static} {@link Field} ��ֵ
     * 
     * @param target Ҫ�����õ�ʵ��, ����{@code null}
     * @param fieldName ��Ա���� ������
     * @return ��Ա������ֵ�� {@code null}
     */
    public static Object readField(final Object target, final String fieldName) {
    	final Field field = getField(target.getClass(), fieldName);
    	
    	if (field == null) {
    		logger.warn("Object [" + target +"] doesn't have field [" + fieldName + "].");
			return null;
    	}   		
    			
        return readField(target, field);
    }
    
    /**
     * ����Ա����{@link Field}��ֵ��ֻ�ܸ�ֵ����{@code static} {@link Field}
     * 
     * @param target Ҫ�����õ�ʵ��, ����{@code null}
     * @param field Ҫ����ֵ�ĳ�Ա����{@link Field}
     * @param value Ҫ������ֵ
     */
    public static void writeField(final Object target, final Field field, final Object value) {
    	boolean isAccessble = field.isAccessible();
    	
    	if (!field.isAccessible()) {
    		field.setAccessible(true);
    	}
    	
    	try {
			field.set(target, ConvertUtils.convert(value, field.getType()));
    	} catch (IllegalArgumentException iae) {
    		throw new InternalException(iae);
		} catch (IllegalAccessException iae) {
			throw new InternalException(iae);
		} finally {
	    	field.setAccessible(isAccessble);
		}
    }
    
    /**
     * ����Ա����{@link Field}��ֵ��ֻ�ܸ�ֵ����{@code static} {@link Field}
     * 
     * @param target Ҫ�����õ�ʵ��, ����{@code null}
     * @param fieldName Ҫ����ֵ�ĳ�Ա����������{@link Field}
     * @param value Ҫ������ֵ
     */
    public static void writeField(final Object target, final String fieldName, final Object value) {
    	final Field field = getField(target.getClass(), fieldName);
    	
    	if (field == null) {
    		logger.warn("Object [" + target +"] doesn't have field [" + fieldName + "].");
			return;
    	}
    	
    	writeField(target, field, value);
    }
    
    /**
     * ��ȡ�������г�Ա�������������ƽ�������
     * 
     * @param clazz Ҫ��ȡ��Ա��������
     * @return ��������ĳ�Ա����
     */
    public static Field[] getSortedDeclaredFields(final Class<?> clazz) {
    	final Field[] declaredFields = clazz.getDeclaredFields();
    	
    	Arrays.sort(declaredFields, new Comparator<Field>()  {
			@Override
			public int compare(Field f1, Field f2) {
				return f1.getName().compareTo(f2.getName());
			}
    	});
    	
    	return declaredFields;
    }
    
    /**************************************************************************************************
     * ˽�з�����
     ***************************************************************************************************/
    /**
	 * ͨ����Ա���������ƻ�ȡ��Ӧ��{@link Field}������Ҳ�ᱻ��ѯ������ѡ���Ƿ��Сд����
	 * 
	 * @param clazz Ҫ���������, ����{@code null}
	 * @param fieldName ��Ա����������, ����Ϊ��
	 * @param ignoreCase �Ƿ��Ǵ�Сд����
	 * @return {@link Field}  ��  {@code null}
	 */
	private static final Field getField(final Class<?> clazz, final String fieldName, final boolean ignoreCase) {
		for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
			try {
				return cls.getDeclaredField(fieldName);
			} catch (final NoSuchFieldException nsfe) {
				if (!ignoreCase) // ����Ǵ�Сд���֣��Ͳ��������г�Ա����
					continue;
				
				if (logger.isDebugEnabled()) {
					logger.debug("Looking for field [" + fieldName
							+ "] failed from class ["
							+ clazz.getCanonicalName()
							+ "], trying to search by ignore case mode.");
				}
				for (Field field : cls.getDeclaredFields()) {
					if (fieldName.equalsIgnoreCase(field.getName()))
						return field;
				}
			} catch (final SecurityException se) {
				/* ignore */
			}
		}
		
		throw new InternalException("Class [" + clazz.getCanonicalName()  + "] and its super class doesn't have field [" + fieldName + "].");
	}
}
