package com.sunwayworld.escm.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.sunwayworld.escm.core.exception.InternalException;

/**
 * {@link Method}������صĹ�����
 */
public final class MethodUtils {
	
	private static final Logger logger = Logger.getLogger(MethodUtils.class);
	
	/**
	 * ͨ����Ա���������ƺͲ�������������ȡ��Ӧ��{@link Method}�������������ִ�Сд
     *
     * @param clazz ��Ա������������ȡ
     * @param methodName ��Ա����������
     * @param parameterTypes ��Ա��������Ĳ���������
     * @return ��Ա���� ��  {@code null} 
     */
	public static Method getExactMethod(final Class<?> clazz, final String methodName, final Class<?> ... parameterTypes) {
		return getMethod(clazz, methodName, false, parameterTypes);
    }
	
	/**
	 * ͨ����Ա���������ƺͲ�������������ȡ��Ӧ��{@link Method}���������Ʋ����ִ�Сд
     *
     * @param clazz ��Ա������������ȡ
     * @param methodName ��Ա����������
     * @param parameterTypes ��Ա��������Ĳ���������
     * @return ��Ա���� ��  {@code null} 
     */
	public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?> ... parameterTypes) {
		return getMethod(clazz, methodName, true, parameterTypes);
    }
	
	/**
	 * ��ȡ��Ա�����Ķ�ȡ������Ҳ����{@code Getter}����
	 * 
	 * @param clazz ��Ա������������ȡ
	 * @param propertyName ��Ա����������
	 * @return ��Ա���� ��  {@code null} 
	 */
	public static Method getReadMethod(final Class<?> clazz, final String propertyName) {
		Method reader = getMethod(clazz, "get" + propertyName);
		
		if (reader == null) {
			reader = getMethod(clazz, "is" + propertyName);
		}
		
		return reader;
	}
	
	/**
	 * ��ȡ��Ա������д�뷽����Ҳ����{@code Setter}����
	 * 
	 * @param clazz ��Ա������������ȡ
	 * @param propertyName ��Ա����������
	 * @return ��Ա���� ��  {@code null} 
	 */
	public static Method getWriteMethod(final Class<?> clazz, final String propertyName) {
		return getMethod(clazz, "set" + propertyName);
	}
	
    /**
	 * ���ó�Ա����
	 *   
	 * @param object ��Ա���������ʵ������
	 * @param method ��Ա����
	 * @param args ��Ա��������Ĳ���
	 * @return �����Ľ��
	 */
    public static Object invokeMethod(final Object object, final Method method, final Object ... args) {    	
    	Class<?>[] types = method.getParameterTypes();
    	 
       	final Object[] cArgs = new Object[args== null? 0 : args.length];
    		
    	if (args != null && args.length>0) {
    		for (int i = 0; i < args.length; i++) {
    			cArgs[i] = ConvertUtils.convert(args[i], types[i]);
			}
    	}
    	
    	if (!method.isAccessible())
    		method.setAccessible(true);
    	
    	try {
			return method.invoke(object, cArgs);
		} catch (IllegalArgumentException iae) {
			throw new InternalException(iae);
		} catch (IllegalAccessException iae) {
			throw new InternalException(iae);
		} catch (InvocationTargetException ite) {
			throw new InternalException(ite);
		}
    }
    
    /**
     * ��{@code Getter}������ȡ��Ա������ֵ
     * 
     * @param object ��Ա���������ʵ������
     * @param propertyName ��Ա����������
     * @return
     */
    public static Object readProperty(final Object object, final String propertyName) {
    	final Method method = getReadMethod(object.getClass(), propertyName);
    	
    	if (method == null) {
    		return null;
    	}
    	
    	return invokeMethod(object, method);
    }
    
    /*********************************************************************************************
     * ˽�й��߷���
     *********************************************************************************************/
    /**
	 * ͨ����Ա���������ƺͲ�������������ȡ��Ӧ��{@link Method}���������ִ�Сд�ǿ�ѡ��
     *
     * @param clazz ��Ա������������ȡ
     * @param methodName ��Ա����������
     * @param ignoreCase �Ƿ��Сд����
     * @param parameterTypes ��Ա��������Ĳ��������ͣ���������ִ�Сд���ᱻ����
     * @return ��Ա���� ��  {@code null} 
     */
	private static Method getMethod(final Class<?> clazz, final String methodName, boolean ignoreCase, final Class<?> ... parameterTypes) {		
		final Method[] methods = clazz.getMethods();
		
		for (Method  method : methods) {
			if (ignoreCase) {
				if (method.getName().equalsIgnoreCase(methodName)) {
					return method;
				}
			} else {
				if (method.getName().equals(methodName)) {
					return method;
				}
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Class " + clazz + " doesn't have method " + methodName);
		}
		
		return null;
    }
}
