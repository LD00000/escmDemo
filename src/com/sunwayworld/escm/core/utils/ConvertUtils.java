package com.sunwayworld.escm.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

/**
 * {@link Object}ת����صĹ�����
 */
public final class ConvertUtils {	
	/**
	 * Ĭ�ϵ�Encoding����{@code char[]}��{@code byte[]}�Ļ�ת
	 */
	public static final String DefaultCharEncoding = "UTF-8";
	
	/**
	 * ת��{@link Object}Ϊ{@link T}���͵����ݣ����ת����{@code null}������Ĭ��ֵ���
	 * 
	 * @param value {@link Object}ʵ��
	 * @param clazz {@link T}����
	 * @param defaultValue Ĭ�ϵ�ֵ
	 * @return ת���ɵ�{@link T}����ʵ��
	 */
	public static final <T> T convert(final Object value, final Class<T> clazz, final T defaultValue) {
		final T convertedValue = convert(value, clazz);
		
		if (convertedValue == null) {
			return defaultValue;
		}
		
		return convertedValue;
	}
	
	/**
	 * ת��{@link Object}Ϊָ�����͵�ֵ�����ڻ����������Ϊ�գ�����Ĭ�ϵĴ���
	 * 
	 * @param value {@link Object}ʵ��
	 * @param className ��������
	 * @return ת����{@code className}��Ӧ������ʵ��
	 */
	public static final Object convert(final Object value, final String className) {
		return convert(value, ClassUtils.getClass(className));
	}
	
	/**
	 * ת��{@link Object}Ϊ{@link T}���͵�����
	 * 
	 * @param value {@link Object}ʵ��
	 * @param clazz {@link T}����
	 * @return ת���ɵ�{@link T}����ʵ��
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T convert(final Object value, final Class<T> clazz) {
		if (value == null || clazz == null) {
			return null;
		}
		
		final Class<T> cls = ClassUtils.primitiveToWrapper(clazz);
		
		if (cls.isAssignableFrom(value.getClass())) {
			return (T)value;
		}
		                                                              
		if (String.class.equals(cls)) {
			return (T) convertToString(value);
		}
		
		if (BigDecimal.class.equals(cls)) {
			return (T) convertToBigDecimal(value);
		}
		
		if (BigInteger.class.equals(cls)) {
			return (T) convertToBigInteger(value);
		}
		
		if (Long.class.equals(cls)) {
			return (T) convertToLong(value);
		}
		
		if (Double.class.equals(cls)) {
			return (T) convertToDouble(value);
		}
		
		if (Float.class.equals(cls)) {
			return (T) convertToFloat(value);
		}
		
		if (Integer.class.equals(cls)) {
			return (T) convertToInteger(value);
		}
		
		if (Short.class.equals(cls)) {
			return (T) convertToShort(value);
		}
		
		if (Character.class.equals(cls)) {
			return (T) convertToChracter(value);
		}
		
		if (Byte.class.equals(cls)) {
			return (T) convertToByte(value);
		}
		
		if (Date.class.equals(cls)) {
			return (T) convertToDate(value);
		}
		
		if (Calendar.class.equals(cls)) {
			return (T) convertToCalendar(value);
		}
		
		if (Time.class.equals(cls)) {
			return (T) convertToSqlTime(value);
		}
		
		if (Timestamp.class.equals(cls)) {
			return (T) convertToSqlTimestamp(value);
		}
		
		if (java.sql.Date.class.equals(cls)) {
			return (T) convertToSqlDate(value);
		}
		
		if (Boolean.class.equals(cls)) {
			return (T) convertToBoolean(value);
		}
		
		if (Blob.class.equals(cls)) {
			return (T) convertToBlob(value);
		}
		
		if (Clob.class.equals(cls)) {
			return (T) convertToClob(value);
		}
		
		if (byte[].class.equals(cls)) {
			return (T) convertToBytes(value);
		}
		
		if (char[].class.equals(cls)) {
			return (T) convertToChars(value);
		}
		
		if (Byte[].class.equals(cls)) {
			return (T) convertToWrapperByteArray(convertToBytes(value));
		}
		
		if (Character[].class.equals(cls)) {
			return (T) convertToWrapperCharArray(convertToChars(value));
		}
		
		if (long[].class.equals(cls)) {
			return (T) convertToLongArray((Long[])value);
		}
		
		if (int[].class.equals(cls)) {
			return (T) convertToIntArray((Integer[])value);
		}
		
		if (double[].class.equals(cls)) {
			return (T) convertToDoubleArray((Double[])value);
		}
		
		if (float[].class.equals(cls)) {
			return (T) convertToFloatArray((Float[])value);
		}
		
		if (short[].class.equals(cls)) {
			return (T) convertToShortArray((Short[])value);
		}
		
		if (long[].class.equals(value.getClass())) {
			return (T) convertToWrapperLongArray((long[])value);
		}
		
		if (int[].class.equals(value.getClass())) {
			return (T) convertToWrapperIntArray((int[])value);
		}
		
		if (double[].class.equals(value.getClass())) {
			return (T) convertToWrapperDoubleArray((double[])value);
		}
		
		if (float[].class.equals(value.getClass())) {
			return (T) convertToWrapperFloatArray((float[])value);
		}
		
		if (short[].class.equals(value.getClass())) {
			return (T) convertToWrapperShortArray((short[])value);
		}
		
		try {
			return cls.cast(value);
		} catch (ClassCastException ex) {
			return null;
		}
	}	
	
	/******************************************************************************************************
	 *  ת����String����
	 ******************************************************************************************************/	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link String}��ת����ʽ���£�
	 * 1. String -> String
	 * 2. StringBuffer -> String
	 * 3. Number -> String
	 * 4. Blob -> String
	 * 5. Clob -> String
	 * 6. Date -> String
	 * 7. Calendar -> String
	 * 8. Character -> String
	 * 9. Boolean -> String
	 * 10. byte[] -> String
	 * 11. char[] -> String
	 * 12. Ref -> String
	 * 13. InputStream -> String
	 * 14. Reader -> String
	 * 15. SQLXML -> String
	 * 16. ���� -> null
	 * 
	 * @param value {@code T}���͵�ʵ��
	 * @return ת����String����������Ծͷ���{@code null}
	 */
	private static final <T> String convertToString(final T value) {
		if (value == null)
			return null;
		
		if (ObjectUtils.isEmpty(value))
			return "";
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (String.class.isAssignableFrom(cls)) {
			return (String)value;
		}
		
		if (StringBuilder.class.isAssignableFrom(cls)) {
			return ((StringBuffer)value).toString();
		}
		
		if (StringBuffer.class.isAssignableFrom(cls)) {
			return ((StringBuffer)value).toString();
		}
		
		if (Number.class.isAssignableFrom(cls)) {
			return "" + (Number)value;
		}
		
		if (Blob.class.isAssignableFrom(cls)) {
			final Blob blob = (Blob)value;
			try {
				return new String(blob.getBytes(1, (int)(blob.length())));			
			} catch (SQLException sqle) {
			}
			
			return null;
		}
		
		if (Clob.class.isAssignableFrom(cls)) {
			Clob clob = (Clob)value;
			Reader reader = null;
			try {
				reader = clob.getCharacterStream();
				final char[] chars = new char[(int) (clob.length())];					
				reader.read(chars);			
				return String.valueOf(chars);
			} catch (SQLException sqle) {
			} catch (IOException ioe) {
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch(Exception e) {}
					reader = null;
				}
			}
			
			return null;
		}
		
		if (Ref.class.isAssignableFrom(cls)) {
			try {
				return convertToString(((Ref)value).getObject());
			} catch (SQLException sqle) {
				return null;
			}
		}
		
		if (SQLXML.class.isAssignableFrom(cls)) {
			try {
				return ((SQLXML)value).getString();
			} catch (SQLException sqle) {
				return null;
			}
		}
		
		if (Date.class.isAssignableFrom(cls)) {
			if (Timestamp.class.isAssignableFrom(cls)) {
				return DateTimeUtils.formatDateTime((Timestamp)value);
			}
			
			if (Time.class.isAssignableFrom(cls)) {
				return DateTimeUtils.formatTime((Time)value);
			}
			
			return DateTimeUtils.formatDateTime((Date)value);
		}
		
		if (Calendar.class.isAssignableFrom(cls)) {
			return DateTimeUtils.formatDateTime(((Calendar)value).getTime());
		}
		
		if (Character.class.isAssignableFrom(cls)) {
			return Character.toString((Character)value);
		}
		
		if (Boolean.class.isAssignableFrom(cls)) {
			return Boolean.toString((Boolean)value);
		}
		
		if (InputStream.class.isAssignableFrom(cls)) {
			return convertToString(new InputStreamReader((InputStream)value));
		}
		
		if (Reader.class.isAssignableFrom(cls)) {
			BufferedReader reader = new BufferedReader((Reader)value);
			
			StringBuilder sb = new StringBuilder();
			
			String line = "";
			
			try {
				while((line=reader.readLine())!= null) {
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				/* ignore */
			} finally {
				try {
					reader.close();
				} catch (IOException ioe) {}
				reader = null;
			}
			 
			 return sb.toString();
		}
		
		if (cls.isArray()) {
			if (Array.getLength(value) == 0)
				return "";
			
			if (byte[].class.isAssignableFrom(cls)) {
				return new String(((byte[])value));
			}
			
			if (Byte[].class.isAssignableFrom(cls)) {
				return new String(convertToByteArray((Byte[])value));
			}
			
			if (char[].class.isAssignableFrom(cls)) {
				return new String((char[])value);
			}
			
			if (Character[].class.isAssignableFrom(cls)) {
				return new String(convertToCharArray((Character[])value));
			}
			
			final StringBuilder sb = new StringBuilder();
			
			for (Object o : (Object[])value) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(ConvertUtils.convertToString(o));
			}
			
			return sb.toString();
		}
		
		if (Collection.class.isAssignableFrom(cls)) {
			return cls.getSimpleName() + Collections.list(Collections.enumeration((Collection<?>)value)).toString();
		}
		
		if (Map.class.isAssignableFrom(cls)) {
			return cls.getSimpleName() + Arrays.toString(((Map<?,?>)value).entrySet().toArray());
		}
		
		return value.toString();
	}
	
	/******************************************************************************************************
	 *  ת����Number����
	 *  
	 *  1.Number  -> Number (BigDecimal, BigInteger, Long, Double, Float, Integer, Short, Byte)
	 *  2.String  -> Number
	 *  3.Boolean -> Number (true : 1 false : 0)
	 *  4.����           -> null
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link BigDecimal}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link BigDecimal}ʵ����{@code null}
	 */
	private static final <T> BigDecimal convertToBigDecimal(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)
				|| String.class.isAssignableFrom(cls)) {
			try {
				return new BigDecimal("" + value);
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		if (Boolean.class.isAssignableFrom(cls)) {
			if ((Boolean)value)
				return new BigDecimal("1");
			else
				return new BigDecimal("0");
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link BigInteger}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link BigInteger}ʵ����{@code null}
	 */
	private static final <T> BigInteger convertToBigInteger(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)
				|| String.class.isAssignableFrom(cls)) {
			try {
				return new BigDecimal("" + value).toBigInteger();
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		if (Boolean.class.isAssignableFrom(cls)) {
			if ((Boolean)value)
				return new BigInteger("1");
			else
				return new BigInteger("0");
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Long}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Long}ʵ����{@code null}
	 */
	private static final <T> Long convertToLong(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls =ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)
				|| String.class.isAssignableFrom(cls)) {
			try {
				return new BigDecimal("" + value).longValue();
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		if (Boolean.class.isAssignableFrom(cls)) {			
			if ((Boolean)value)
				return 1L;
			else
				return 0L;
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Double}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Double}ʵ����{@code null}
	 */
	private static final <T> Double convertToDouble(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)
				|| String.class.isAssignableFrom(cls)) {
			try {
				return new BigDecimal("" + value).doubleValue();
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		if (Boolean.class.isAssignableFrom(cls)) {			
			if ((Boolean)value)
				return 1D;
			else
				return 0D;
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Float}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Float}ʵ����{@code null}
	 */
	private static final <T> Float convertToFloat(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)
				|| String.class.isAssignableFrom(cls)) {
			try {
				return new BigDecimal("" + value).floatValue();
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		
		if (Boolean.class.isAssignableFrom(cls)) {			
			if ((Boolean)value)
				return 1F;
			else
				return 0F;
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Integer}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Integer}ʵ����{@code null}
	 */
	private static final <T> Integer convertToInteger(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)
				|| String.class.isAssignableFrom(cls)) {
			try {
				return new BigDecimal("" + value).intValue();
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		if (Boolean.class.isAssignableFrom(cls)) {			
			if ((Boolean)value)
				return 1;
			else
				return 0;
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Short}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Short}ʵ����{@code null}
	 */
	private static final <T> Short convertToShort(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)
				|| String.class.isAssignableFrom(cls)) {
			try {
				return new BigDecimal("" + value).shortValue();
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		
		if (Boolean.class.isAssignableFrom(cls)) {			
			if ((Boolean)value)
				return 1;
			else
				return 0;
		}
		
		return null;
	}
	
	/******************************************************************************************************
	 *  ת����Character����
	 *  
	 *  1. String -> Character ֻ�г���Ϊ0��1������1��ת����Integer, Integerֻ�д�0��9�Ĳ�ת��
	 *  2. ����        -> ��ת����String���ж�
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Character}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Byte}ʵ����{@code null}
	 */
	private static final <T> Character convertToChracter(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		String strValue = convertToString(value);
		
		if (strValue != null) {
			int length = strValue.length();
			
			if (length == 0) {
				return Character.valueOf(' ');
			} else if (length == 1) {
				return Character.valueOf(strValue.charAt(0));
			} else { // ������ȳ���1����ôת����int
				Integer intValue = convertToInteger(strValue);
				
				if (intValue != null && intValue >= 0 && intValue <10) {
					return Character.forDigit(intValue, 10);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Byte}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Byte}ʵ����{@code null}
	 */
	private static final <T> Byte convertToByte(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Number.class.isAssignableFrom(cls)) {
			try {
				return new Byte("" + value);
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		if (String.class.isAssignableFrom(cls)) {			
			try {
				return new Byte(((String)value).trim());
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		
		if (Boolean.class.isAssignableFrom(cls)) {			
			if ((Boolean)value)
				return 1;
			else
				return 0;
		}
		
		return null;
	}
	
	/******************************************************************************************************
	 *  ת����Date����
	 *  
	 *  1. Date   -> Date (Date, Calendar, Sql Date, Sql Time, Sql Timestamp)
	 *  2. String -> Date
	 *  3. Long   -> Date
	 *  4. ����        -> null
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Date}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Date}ʵ����{@code null}
	 */
	@SuppressWarnings("unchecked")
	private static final <T> Date convertToDate(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Date.class.isAssignableFrom(cls)) {
			return new Date(((Date)value).getTime());
		}
		
		if (String.class.isAssignableFrom(cls)) {
			final String strValue = ((String)value).trim();
			
			try {
				return DateFormat.getDateTimeInstance().parse(strValue);
			} catch (ParseException pe) {}
			
			try {
				return DateFormat.getDateInstance().parse(strValue);
			} catch (ParseException pe) {}
			
			try {
				return DateFormat.getTimeInstance().parse(strValue);
			} catch (ParseException pe) {}
		}
		
		if (Long.class.isAssignableFrom(cls)) {
			return new Date((Long)value);
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Calendar}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Calendar}ʵ����{@code null}
	 */
	private static final <T> Calendar convertToCalendar(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Calendar.class.isAssignableFrom(cls)) {
			return (Calendar)value;
		}
		
		if (String.class.isAssignableFrom(cls)) {			
			final Date date = convertToDate(((String)value).trim());
			
			if (date != null) {
				final Calendar calendar = Calendar.getInstance();
				
				calendar.setTime(date);
				
				return calendar;
			}
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link java.sql.Date}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link java.sql.Date}ʵ����{@code null}
	 */
	private static final <T> java.sql.Date convertToSqlDate(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Date.class.isAssignableFrom(cls)) {
			return new java.sql.Date(((Date)value).getTime());
		}
		
		if (String.class.isAssignableFrom(cls)) {
			final Date date = convertToDate(((String)value).trim());
			
			if (date != null) {
				return new java.sql.Date(date.getTime());
			}
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Time}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Time}ʵ����{@code null}
	 */
	private static final <T> Time convertToSqlTime(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Date.class.isAssignableFrom(cls)) {
			return new Time(((Date)value).getTime());
		}
		
		if (String.class.isAssignableFrom(cls)) {
			try {
				return Time.valueOf(((String)value).trim());
			} catch (IllegalArgumentException ex) {
			}
		}
		
		return null;
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Timestamp}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Timestamp}ʵ����{@code null}
	 */
	private static final <T> Timestamp convertToSqlTimestamp(final T value) {
		if (ObjectUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Date.class.isAssignableFrom(cls)) {
			return new Timestamp(((Date)value).getTime());
		}
		
		if (String.class.isAssignableFrom(cls)) {				
			final String v = ((String)value).trim();
			
			try {
				return Timestamp.valueOf(v);
			} catch (IllegalArgumentException ex) {
			}
			
			try {
				final Date date = convertToDate(v);
				
				if (date != null)
					return new Timestamp(date.getTime());
			} catch (IllegalArgumentException e) {
			}
		}
		
		return null;
	}
	
	/******************************************************************************************************
	 *  ת����byte[]����
	 *  
	 *  1. byte[]      -> byte[]
	 *  2. Byte[]      -> byte[]
	 *  3. String      -> byte[]
	 *  4. char[]      -> byte[]
	 *  5. Character[] -> byte[]
	 *  6. Blob        -> byte[]
	 *  7. Clob        -> byte[]
	 *  8. ����                      -> null
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link byte[]}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link byte[]}ʵ����{@code null}
	 */
	private static final <T> byte[] convertToBytes(final T value) {
		if (value == null)
			return null;
		
		@SuppressWarnings("unchecked")
		Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (byte[].class.isAssignableFrom(cls)) {
			return (byte[])value;
		}
		
		if (Byte[].class.isAssignableFrom(cls)) {
			return convertToByteArray((Byte[])value);
		}
		
		if (String.class.isAssignableFrom(cls)) {
			return ((String)value).getBytes();
		}
		
		if (char[].class.isAssignableFrom(cls) ||
				Character[].class.isAssignableFrom(cls)) {
			char[] chars = null;
			if (char[].class.isAssignableFrom(cls))
				chars = (char[])value;
			else
				chars = convertToCharArray((Character[])value);
			
			
			if (chars.length ==0)
				return new byte[0];
			
			final Charset cs = Charset.forName(DefaultCharEncoding);
			final CharBuffer cb = CharBuffer.allocate(chars.length);
			cb.put(chars);
			cb.flip();

			return cs.encode(cb).array();
		}
		
		if (Blob.class.isAssignableFrom(cls)) {
			final Blob blob = (Blob)value;
			InputStream is = null;
			try {				
				is = blob.getBinaryStream();
				final byte[] bytes = new byte[(int)(blob.length())];					
				is.read(bytes, 0, (int)(blob.length()));					
				return bytes;			
			} catch (SQLException sqle) {
			} catch (IOException ioe) {
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch(Exception e) {}
					is = null;
				}
			}
			
			return null;
		}
		
		if (Clob.class.isAssignableFrom(cls)) {
			final Clob clob = (Clob)value;
			Reader reader = null;
			try {
				reader = clob.getCharacterStream();
				final char[] chars = new char[(int) (clob.length())];					
				reader.read(chars);			
				return convertToBytes(chars);
			} catch (SQLException sqle) {
			} catch (IOException ioe) {
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch(Exception e) {}
					reader = null;
				}
			}
		}
		
		return null;
	}
	
	/******************************************************************************************************
	 *  ת����char[]����
	 *  
	 *  1. char[]      -> char[]
	 *  2. Character[] -> char[]
	 *  3. String      -> char[]
	 *  4. byte[]      -> char[]
	 *  5. Byte[]      -> char[]
	 *  6. Clob    -> char[]
	 *  7. Blob    -> char[]
	 *  8. ����         -> null
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link char[]}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link char[]}ʵ����{@code null}
	 */
	private static final <T> char[] convertToChars(final T value) {
		if (value == null)
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (char[].class.isAssignableFrom(cls)) {
			return (char[])value;
		}
		
		if (Character[].class.isAssignableFrom(cls)) {
			return convertToCharArray((Character[])value);
		}
		
		if (String.class.isAssignableFrom(cls)) {
			return ((String)value).toCharArray();
		}
		
		if (byte[].class.isAssignableFrom(cls) ||
				Byte[].class.isAssignableFrom(cls)) {
			byte[] bytes = null;
			
			if (byte[].class.isAssignableFrom(cls))
				bytes = (byte[]) value;
			else
				bytes = convertToByteArray((Byte[])value);

			if (bytes.length == 0)
				return new char[0];

			final Charset cs = Charset.forName(DefaultCharEncoding);
			final ByteBuffer bb = ByteBuffer.allocate(bytes.length);
			bb.put(bytes);
			bb.flip();
			
			return cs.decode(bb).array();
		}
		
		if (Clob.class.isAssignableFrom(cls)) {
			final Clob clob = (Clob)value;
			Reader reader = null;
			try {
				reader = clob.getCharacterStream();
				final char[] chars = new char[(int) (clob.length())];					
				reader.read(chars);			
				return chars;
			} catch (SQLException sqle) {
			} catch (IOException ioe) {
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch(Exception e) {}
					reader = null;
				}
			}

			return null;
		}
		
		if (Blob.class.isAssignableFrom(cls)) {
			final Blob blob = (Blob)value;
			InputStream is = null;
			try {				
				is = blob.getBinaryStream();
				final byte[] bytes = new byte[(int)(blob.length())];					
				is.read(bytes, 0, (int)(blob.length()));					
				return convertToChars(bytes);			
			} catch (SQLException sqle) {
			} catch (IOException ioe) {
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch(Exception e) {}
					is = null;
				}
			}

			return null;
		}
		
		return null;
	}
	
	/******************************************************************************************************
	 *  ת����Boolean����
	 *  
	 *  1. Boolean  -> Boolean
	 *  2. String   -> Boolean  (YES -> TRUE  NO -> FALSE)
	 *  3. Number   -> Boolean (true : >0)
	 *  3. ����              -> null
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Boolean}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Boolean}ʵ����{@code null}
	 */
	private static final <T> Boolean convertToBoolean(final T value) {
		if (value == null)
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Boolean.class.isAssignableFrom(cls)) {
			return (Boolean)value;
		}
		
		if (String.class.isAssignableFrom(cls)) {
			final String v = ((String)value).trim().toLowerCase();
			
			if ("true".equals(v) || "yes".equals(v))
				return Boolean.TRUE;
			
			return Boolean.FALSE;
		}
		
		if (Number.class.isAssignableFrom(cls)) {
			return ((Number)value).doubleValue()>0;
		}
		
		return null;
	}
	
	/******************************************************************************************************
	 *  ת����Clob����
	 *  
	 *  1. Clob    -> Clob
	 *  2. String  -> Clob
	 *  3. byte[]  -> Clob
	 *  4. char[]  -> Clob
	 *  3. ����         -> null
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Blob}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Blob}ʵ����{@code null}
	 */
	private static final <T> Clob convertToClob(final T value) {
		if (value == null)
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Clob.class.isAssignableFrom(cls)) {
			return (Clob)value;
		}
		
		if (String.class.isAssignableFrom(cls)) {
			if (StringUtils.isEmpty((String)value)) {
				return null;
			}
			
			try {
				return new SerialClob(((String)value).toCharArray());
			} catch (SerialException e) {
			} catch (SQLException e) {
			}
			
			return null;
		}
		
		if (char[].class.isAssignableFrom(cls)) {
			try {
				return new SerialClob((char[])value);
			} catch (SerialException e) {
			} catch (SQLException e) {
			}
			
			return null;
		}
		
		if (byte[].class.isAssignableFrom(cls)) {
			try {
				return new SerialClob(convertToChars((byte[])value));
			} catch (SerialException e) {
			} catch (SQLException e) {
			}
			
			return null;
		}
				
		return null;
	}	
	
	/******************************************************************************************************
	 *  ת����Blob����
	 *  
	 *  1. Blob    -> Blob
	 *  2. String  -> Blob
	 *  3. byte[]  -> Blob
	 *  4. char[]  -> Blob
	 *  3. ����         -> null
	 ******************************************************************************************************/
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Blob}
	 * 
	 * @param value Ҫ��ת����ֵ
	 * @return {@link Blob}ʵ����{@code null}
	 */
	private static final <T> Blob convertToBlob(final T value) {
		if (value == null)
			return null;
		
		@SuppressWarnings("unchecked")
		final Class<T> cls = ClassUtils.primitiveToWrapper((Class<T>) value.getClass());
		
		if (Blob.class.isAssignableFrom(cls)) {
			return (Blob)value;
		}
		
		if (String.class.isAssignableFrom(cls)) {
			if (StringUtils.isEmpty((String)value)) {
				return null;
			}
			
			try {
				return new SerialBlob(((String)value).getBytes());
			} catch (SerialException e) {
			} catch (SQLException e) {
			}
			
			return null;
		}
		
		if (char[].class.isAssignableFrom(cls)) {
			try {
				return new SerialBlob(convertToBytes((char[])value));
			} catch (SerialException e) {
			} catch (SQLException e) {
			}
			
			return null;
		}
		
		if (byte[].class.isAssignableFrom(cls)) {
			try {
				return new SerialBlob((byte[])value);
			} catch (SerialException e) {
			} catch (SQLException e) {
			}
			
			return null;
		}
				
		return null;
	}
	
	/******************************************************************************************************
	 *  ת���ɻ����������͵�����
	 *  
	 *  1. Character[]    -> char[]
	 *  2. Byte[]         -> byte[]
	 *  3. Long[]         -> long[]
	 *  4. Integer[]      -> int[]
	 *  5. Double[]       -> double[]
	 *  6. Float[]        -> float[]
	 *  7. Short[]        -> short[]
	 ******************************************************************************************************/
	
	/**
	 * ת��{@link Character}Ϊ{@link char}����
	 * 
	 * @param array Ҫת����{@link Character[]}����
	 * @return {@link char}����
	 */
	public static final char[] convertToCharArray(final Character array[]) {
		if (array==null || array.length == 0)
			return new char[0];
		
		final char[] newArray = new char[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = array[i].charValue();
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link Byte}Ϊ{@link byte}����
	 * 
	 * @param array Ҫת����{@link Byte[]}����
	 * @return {@link byte}����
	 */
	public static final byte[] convertToByteArray(final Byte[] array) {
		if (array==null || array.length == 0)
			return new byte[0];
		
		final byte[] newArray = new byte[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = array[i].byteValue();
		}
		
		return newArray;
	}	
	
	/**
	 * ת��{@link Long}Ϊ{@link long}����
	 * 
	 * @param array Ҫת����{@link Long[]}����
	 * @return {@link long}����
	 */
	public static final long[] convertToLongArray(final Long[] array) {
		if (array==null || array.length == 0)
			return new long[0];
		
		final long[] newArray = new long[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = array[i].longValue();
		}
		
		return newArray;
	}	
	
	/**
	 * ת��{@link Integer}Ϊ{@link int}����
	 * 
	 * @param array Ҫת����{@link Integer[]}����
	 * @return {@link int}����
	 */
	public static final int[] convertToIntArray(final Integer[] array) {
		if (array==null || array.length == 0)
			return new int[0];
		
		final int[] newArray = new int[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = array[i].intValue();
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link Double}Ϊ{@link double}����
	 * 
	 * @param array Ҫת����{@link Double[]}����
	 * @return {@link double}����
	 */
	public static final double[] convertToDoubleArray(final Double[] array) {
		if (array==null || array.length == 0)
			return new double[0];
		
		final double[] newArray = new double[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = array[i].doubleValue();
		}
		
		return newArray;
	}	
	
	/**
	 * ת��{@link Float}Ϊ{@link float}����
	 * 
	 * @param array Ҫת����{@link Float[]}����
	 * @return {@link float}����
	 */
	public static final float[] convertToFloatArray(final Float[] array) {
		if (array==null || array.length == 0)
			return new float[0];
		
		final float[] newArray = new float[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = array[i].floatValue();
		}
		
		return newArray;
	}	
	
	/**
	 * ת��{@link Short}Ϊ{@link short}����
	 * 
	 * @param array Ҫת����{@link Short[]}����
	 * @return {@link short}����
	 */
	public static final short[] convertToShortArray(final Short[] array) {
		if (array==null || array.length == 0)
			return new short[0];
		
		final short[] newArray = new short[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = array[i].shortValue();
		}
		
		return newArray;
	}	
	
	
	/******************************************************************************************************
	 *  ת���ɻ����������͵�����
	 *  
	 *  1. char[]   -> Character[]
	 *  2. byte[]   -> Byte[]
	 *  3. long[]   -> Long[]
	 *  4. int[]    -> Integer[]
	 *  5. double[] -> Double[]
	 *  6. float[]  -> Float[]
	 *  7. short[]  -> Short[]
	 ******************************************************************************************************/
	
	/**
	 * ת��{@link char}Ϊ{@link Character}����
	 * 
	 * @param array Ҫת����{@link char[]}����
	 * @return {@link Character}����
	 */
	public static final Character[] convertToWrapperCharArray(final char[] array) {
		if (array==null || array.length == 0)
			return new Character[0];
		
		final Character[] newArray = new Character[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = Character.valueOf(array[i]);
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link byte}Ϊ{@link Byte}����
	 * 
	 * @param array Ҫת����{@link byte[]}����
	 * @return {@link Byte}����
	 */
	public static final Byte[] convertToWrapperByteArray(final byte[] array) {
		if (array==null || array.length == 0)
			return new Byte[0];
		
		final Byte[] newArray = new Byte[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = Byte.valueOf(array[i]);
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link long}Ϊ{@link Long}����
	 * 
	 * @param array Ҫת����{@link long[]}����
	 * @return {@link Long}����
	 */
	public static final Long[] convertToWrapperLongArray(final long[] array) {
		if (array==null || array.length == 0)
			return new Long[0];
		
		final Long[] newArray = new Long[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = Long.valueOf(array[i]);
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link int}Ϊ{@link Integer}����
	 * 
	 * @param array Ҫת����{@link int[]}����
	 * @return {@link Integer}����
	 */
	public static final Integer[] convertToWrapperIntArray(final int[] array) {
		if (array==null || array.length == 0)
			return new Integer[0];
		
		final Integer[] newArray = new Integer[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = Integer.valueOf(array[i]);
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link double}Ϊ{@link Double}����
	 * 
	 * @param array Ҫת����{@link double[]}����
	 * @return {@link Double}����
	 */
	public static final Double[] convertToWrapperDoubleArray(final double[] array) {
		if (array==null || array.length == 0)
			return new Double[0];
		
		final Double[] newArray = new Double[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = Double.valueOf(array[i]);
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link float}Ϊ{@link Float}����
	 * 
	 * @param array Ҫת����{@link float[]}����
	 * @return {@link Float}����
	 */
	public static final Float[] convertToWrapperFloatArray(final float[] array) {
		if (array==null || array.length == 0)
			return new Float[0];
		
		final Float[] newArray = new Float[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = Float.valueOf(array[i]);
		}
		
		return newArray;
	}
	
	/**
	 * ת��{@link short}Ϊ{@link Short}����
	 * 
	 * @param array Ҫת����{@link short[]}����
	 * @return {@link Short}����
	 */
	public static final Short[] convertToWrapperShortArray(final short[] array) {
		if (array==null || array.length == 0)
			return new Short[0];
		
		final Short[] newArray = new Short[array.length];
		
		for (int i = 0 ; i < array.length; i++) {
			newArray[i] = Short.valueOf(array[i]);
		}
		
		return newArray;
	}
	
}
