package com.sunwayworld.escm.core.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * {@link Date}��صĹ�����
 */
public final class DateTimeUtils {
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
	
	/**
	 * ���ں�ʱ��ĸ�ʽ������
	 * 
	 * @param dateTime Ҫ����ʽ����{@link Date}
	 * @return ��ʽ��Ϊ{@link #DEFAULT_DATETIME_FORMAT}��{@link String}
	 */
	public static final String formatDateTime(final Date dateTime) {
		if (dateTime == null)
			return "";
		
		return new SimpleDateFormat(DEFAULT_DATETIME_FORMAT).format(dateTime);
	}
	
	/**
	 * ���ڵĸ�ʽ������
	 * 
	 * @param date Ҫ����ʽ����{@link Date}
	 * @return ��ʽ��Ϊ{@link #DEFAULT_DATE_FORMAT}��{@link String}
	 */
	public static final String formatDate(final Date date) {
		if (date == null)
			return "";
		
		return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date);
	}
	
	/**
	 * ʱ��ĸ�ʽ������
	 * 
	 * @param time Ҫ����ʽ����{@link Date}
	 * @return ��ʽ��Ϊ{@link #DEFAULT_DATE_FORMAT}��{@link String}
	 */
	public static final String formatTime(final Date time) {
		if (time == null)
			return "";
		
		return new SimpleDateFormat(DEFAULT_TIME_FORMAT).format(time);
	}
	
	/**
	 * ����ָ���ĸ�ʽ����ʽ�����ں�ʱ��ķ���
	 * 
	 * @param dateTime Ҫ����ʽ����{@link Date}
	 * @return ��ʽ��Ϊ{@link #DEFAULT_DATETIME_FORMAT}��{@link String}
	 */
	public static final String formart(final Date time, final String pattern) {
		return new SimpleDateFormat(pattern).format(time);
	}
	
	/**
	 * ָ�����������������
	 * 
	 * @param date ָ��������
	 * @param amount Ҫ���ӵ����
	 * @return �޸ĺ������
	 */
	public static final Date addYear(final Date date, final int amount) {
		return add(date, amount, Calendar.YEAR);
	}
	
	/**
	 * ָ���������������·�
	 * 
	 * @param date ָ��������
	 * @param amount Ҫ���ӵ��·�
	 * @return �޸ĺ������
	 */
	public static final Date addMonth(final Date date, final int amount) {
		return add(date, amount, Calendar.MONTH);
	}
	
	/**
	 * ָ������������������
	 * 
	 * @param date ָ��������
	 * @param amount Ҫ���ӵ�����
	 * @return �޸ĺ������
	 */
	public static final Date addDay(final Date date, final int amount) {
		return add(date, amount, Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * ��ָ��������ת���ɴ�January 1, 1970, 00:00:00 GMT��ʼ������
	 * 
	 * @param date ָ��������
	 * @return ����
	 */
	public static final Long getDays(final Date date) {
		if (date == null) {
			return 0L;
		}
		
		return date.getTime() / (1000 * 60 * 60 * 24);
	}
	
	/**
	 * ָ���������ϣ���ָ���������ֵ
	 * 
	 * @param date ָ��������
	 * @param amount Ҫ���ӵ�ֵ
	 * @param field ���ڵ����Calendar��ȡֵ
	 * @return �޸ĺ������
	 */
	private static final Date add(final Date date, final int amount, final int field) {
		if (date == null)
			return null;
		
		Calendar instance = Calendar.getInstance();
		
		instance.setTime(date);
		
		instance.add(field, amount);
		
		return instance.getTime();
	}
	
	/**
	 * ҳ���ѯ������Ҫ����ݣ���õĽ��Ϊ��ǰ��ݵ�ǰintValue�굽��intValue��ļ���
	 * 
	 * @param intValue ��Ҫ��ѯ��ݵķ�Χ������intValue=5 ��ǰ��Ϊ2015�� ��list����2010~2020 
	 * @return ����һ�����ϣ������а�����ݵ�ֵ
	 */
	public static final ArrayList<String> getQueryDateAsLsit(int intValue) {
		ArrayList<String> aList =  new ArrayList<String>();
		Date date = new Date();
		for(int i = Integer.parseInt("-" + intValue); i < (intValue + 1); i++) {
			aList.add(formart(addYear(date, i), "yyyy"));
		}
		return aList;
	}
	
}
