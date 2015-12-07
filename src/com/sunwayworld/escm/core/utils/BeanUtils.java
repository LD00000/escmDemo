package com.sunwayworld.escm.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Java Bean��صĹ�����
 */
public final class BeanUtils {
	private static final Logger logger = Logger.getLogger(BeanUtils.class);
	
	/**
	 * ����{@link ResultSet}��Column��ֵ����{@code Setter}������ֵ��Column�����ƶ�Ӧ��Ŀ��{@link Object}�ĳ�Ա������
	 * 
	 * @param resultSet �ṩֵ��{@link ResultSet}ʵ��
	 * @param accepter �������ݵ�{@link Object}ʵ��
	 */
	public static final void copyProperties(ResultSet resultSet, Object accepter) throws SQLException {
		final ResultSetMetaData rsmd = resultSet.getMetaData();
		final Class<?> cls = accepter.getClass();
		
		for (int i=1; i <= rsmd.getColumnCount(); i++) {
			final String name = rsmd.getColumnName(i);
			
			final Method writer = MethodUtils.getWriteMethod(cls, name);
			
			if (writer != null) {
				final Class<?> parameterType = writer.getParameterTypes()[0];
				
				Object paramValue = null;
				
				// �����ж�Date����
				if (parameterType.equals(Time.class)) {
					paramValue = resultSet.getTime(i);
				} else if (parameterType.equals(java.sql.Date.class)) {
					final Timestamp timestamp = resultSet.getTimestamp(i);
					
					if (timestamp != null) {
						paramValue = new java.sql.Date(resultSet.getTimestamp(i).getTime());
					}
				} else if (parameterType.equals(java.sql.Timestamp.class)) {
					paramValue = resultSet.getTimestamp(i);
				} else if (Date.class.isAssignableFrom(parameterType)) {
					final Timestamp timestamp = resultSet.getTimestamp(i);
					
					if (timestamp != null) {
						paramValue = new Date(resultSet.getTimestamp(i).getTime());
					}
				} else {
					paramValue = ConvertUtils.convert(resultSet.getObject(i), parameterType);
				}
				
				if (paramValue != null) {
					MethodUtils.invokeMethod(accepter, writer, paramValue);
				}
			} else {
				logger.debug("Column [" + name +"] doesn't have matched Property in Class [" + cls.getCanonicalName() + "].");
			}
		}
	}
	
	/**
	 * ����{@link HttpServletRequest}��Param��ֵ����{@code Setter}������ֵ��Param�����ƶ�Ӧ��Ŀ��{@link Object}�ĳ�Ա������
	 * �������{@code prefix}��{@code suffix}�ǿյĻ�����Ҫ��Param��������ȥ��ǰ��׺��Ƚ�
	 * 
	 * @param request �ṩֵ��{@link HttpServletRequest}ʵ��
	 * @param dest �������ݵ�{@link Object}ʵ��
	 * @param prefix ǰ׺
	 * @param suffix ��׺
	 */
	public static final void copyProperties(final HttpServletRequest request, final Object accepter, final String prefix, final String suffix) {
		final Enumeration<String> keys = request.getParameterNames();
		
		final String vPrefix = ((StringUtils.isBlank(prefix))? "" : prefix);
		final String vSuffix = ((StringUtils.isBlank(suffix))? "" : suffix);
		
		final Class<?> cls = accepter.getClass();
		
		while(keys.hasMoreElements()) {
			final String key = keys.nextElement();
			
			if (!StringUtils.startsWithIgnoreCase(key, vPrefix) || !StringUtils.endsWithIgnoreCase(key, vSuffix)) {
				continue;
			}
			
			final String name = StringUtils.removeEnd(StringUtils.removeStart(key.toLowerCase(), vPrefix.toLowerCase()), vSuffix.toLowerCase());
						
			if (StringUtils.isBlank(name))
				continue;
			
			final String value = request.getParameter(key);
			
			final Method writer = MethodUtils.getWriteMethod(cls, name);
			
			if (writer != null && value != null) {
				final Class<?> parameterType = writer.getParameterTypes()[0];
				
				Object paramValue = ConvertUtils.convert(value, parameterType);
				
				MethodUtils.invokeMethod(accepter, writer, paramValue);
			}
		}
	}
	
	/**
	 * ����{@link HttpServletRequest}��Param��ֵ����{@code Setter}������ֵ��Param�����ƶ�Ӧ��Ŀ��{@link Object}�ĳ�Ա������
	 * 
	 * @param request �ṩֵ��{@link HttpServletRequest}ʵ��
	 * @param dest �������ݵ�{@link Object}ʵ��
	 */
	public static final void copyProperties(final HttpServletRequest request, final Object accepter) {
		copyProperties(request, accepter, null, null);
	}
	
	/**
	 * ����{@link Object}�ĳ�Ա������ֵ��Ŀ��{@link Object}����ͬ���Ƶĵĳ�Ա�����ϣ������ִ�Сд��
	 * 
	 * @param provider �ṩֵ��{@link Object}ʵ��
	 * @param accepter �������ݵ�{@link Object}ʵ��
	 */
	public static final void copyProperties(Object provider, Object accepter) {		
		final Class<?> providerCls = provider.getClass();
		final Class<?> accepterCls = accepter.getClass();
		
		for (Class<?> cls = providerCls; cls != null; cls = cls.getSuperclass()) {
			for (Field providerField : cls.getDeclaredFields()) {
				final String propertyName = providerField.getName();
				
				final Method providerReader = MethodUtils.getReadMethod(cls, propertyName);
				
				if (providerReader == null) {
					continue;
				}
				
				final Method accepterWriter = MethodUtils.getWriteMethod(accepterCls, propertyName);
				
				if (accepterWriter == null) {
					continue;
				}
				
				
				final Class<?> parameterType = accepterWriter.getParameterTypes()[0];
				
				Object paramValue = ConvertUtils.convert(MethodUtils.invokeMethod(provider, providerReader), parameterType);
				
				if (paramValue == null) {
					/** ignore **/
				} else {
					MethodUtils.invokeMethod(accepter, accepterWriter, paramValue);
				}
			}
		}
	}
	
	/**
	 * ����{@link Map}��ֵ����{@code Setter}������ֵ��{@link Map}��{@code KEY}��Ӧ��Ŀ��{@link Object}�ĳ�Ա������
	 * �������{@code prefix}��{@code suffix}�ǿյĻ�����Ҫ��{@link Map}��{@code KEY}��ȥ��ǰ��׺��Ƚ�
	 * 
	 * @param provider �ṩֵ��{@link Map}ʵ��
	 * @param dest �������ݵ�{@link Object}ʵ��
	 * @param prefix ǰ׺
	 * @param suffix ��׺
	 */
	public static final <T> void copyProperties(final Map<String, T> provider, final Object accepter, final String prefix, final String suffix) {
		final String vPrefix = ((StringUtils.isBlank(prefix))? "" : prefix);
		final String vSuffix = ((StringUtils.isBlank(suffix))? "" : suffix);
		
		final Class<?> cls = accepter.getClass();
		
		final Iterator<Entry<String, T>> iterator = provider.entrySet().iterator();
		
		while(iterator.hasNext()) {
			final Entry<String, ?> entry = iterator.next();
			
			final String key = entry.getKey();
			
			if (!StringUtils.startsWithIgnoreCase(key, vPrefix) || !StringUtils.endsWithIgnoreCase(key, vSuffix)) {
				continue;
			}
			
			final String name = StringUtils.removeEnd(StringUtils.removeStart(key.toLowerCase(), vPrefix.toLowerCase()), vSuffix.toLowerCase());
						
			if (StringUtils.isBlank(name))
				continue;
			
			final T value = provider.get(key);
			
			final Method writer = MethodUtils.getWriteMethod(cls, name);
			
			if (writer != null) {
				final Class<?> parameterType = writer.getParameterTypes()[0];
				
				Object paramValue = ConvertUtils.convert(value, parameterType);
				
				if (paramValue == null) {
					/** ignore **/
				} else {
					MethodUtils.invokeMethod(accepter, writer, paramValue);
				}
			}
		}
	}
}
