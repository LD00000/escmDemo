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
 * {@link Field}反射相关的工具类
 */
public final class FieldUtils {
	
	private static final Logger logger = Logger.getLogger(FieldUtils.class);
	
	private FieldUtils() {}
	
	/**
	 * 通过成员变量的名称获取对应的{@link Field}，父类也会被查询，名称区分大小写判断
	 * 
	 * @param clazz 要被反射的类, 不能{@code null}
	 * @param fieldName 成员变量的名称, 不能为空
	 * @return {@link Field}  或  {@code null}
	 */
	public static final Field getExactField(final Class<?> clazz, final String fieldName) {
		return getField(clazz, fieldName, false);
		
	}
	
	/**
	 * 通过成员变量的名称获取对应的{@link Field}，父类也会被查询，名称不区分大小写判断
	 * 
	 * @param clazz 要被反射的类, 不能{@code null}
	 * @param fieldName 成员变量的名称, 不能为空
	 * @return {@link Field}  或  {@code null}
	 */
	public static final Field getField(final Class<?> clazz, final String fieldName) {
		return getField(clazz, fieldName, true);
	}
	
	/**
	 * 通过成员变量的类型获取对应的{@link Field}，父类也会被查询
	 * 
	 * @param clazz 要被反射的类, 不能{@code null}
	 * @param type 成员变量的类型, 不能{@code null}
	 * @return 满足条件的所有{@link Field} {@link List}
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
	 * 通过注释的类名获取被注释的{@link Field}，父类也会被查询
	 * 
	 * 
	 * @param clazz 要被反射的类, 不能{@code null}
	 * @param annotation 注释的类名, 不能{@code null}
	 * @return 满足条件的所有{@link Field} {@link List}
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
     * 读取成员变量 {@link Field} 的值。
     * 
     * @param target 要被调用的{@link Object}, 不能{@code null}
     * @param field 要被使用的成员变量, 不能{@code null}
     * @return 成员变量的值或 {@code null}
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
     * 读取成员变量 {@link Field} 的值，只能读取非{@code static} {@link Field} 的值
     * 
     * @param target 要被调用的实例, 不能{@code null}
     * @param fieldName 成员变量 的名称
     * @return 成员变量的值或 {@code null}
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
     * 给成员变量{@link Field}赋值，只能赋值给非{@code static} {@link Field}
     * 
     * @param target 要被调用的实例, 不能{@code null}
     * @param field 要被赋值的成员变量{@link Field}
     * @param value 要被赋的值
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
     * 给成员变量{@link Field}赋值，只能赋值给非{@code static} {@link Field}
     * 
     * @param target 要被调用的实例, 不能{@code null}
     * @param fieldName 要被赋值的成员变量的名称{@link Field}
     * @param value 要被赋的值
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
     * 获取类中所有成员变量，并按名称进行排序
     * 
     * @param clazz 要获取成员变量的类
     * @return 所有排序的成员变量
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
     * 私有方法类
     ***************************************************************************************************/
    /**
	 * 通过成员变量的名称获取对应的{@link Field}，父类也会被查询，可以选择是否大小写区分
	 * 
	 * @param clazz 要被反射的类, 不能{@code null}
	 * @param fieldName 成员变量的名称, 不能为空
	 * @param ignoreCase 是否是大小写区分
	 * @return {@link Field}  或  {@code null}
	 */
	private static final Field getField(final Class<?> clazz, final String fieldName, final boolean ignoreCase) {
		for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
			try {
				return cls.getDeclaredField(fieldName);
			} catch (final NoSuchFieldException nsfe) {
				if (!ignoreCase) // 如果是大小写区分，就不搜索所有成员变量
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
