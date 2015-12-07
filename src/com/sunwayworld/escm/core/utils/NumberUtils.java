package com.sunwayworld.escm.core.utils;

import java.math.BigDecimal;

/**
 * ���ִ��� �Ĺ�����
 */
public final class NumberUtils {
	/**
	 * ��ʽ�����Ĭ�Ϸ�����С�������2λ���������룬��λ��0
	 * 
	 * @param number Ҫ��ʽ���Ľ��
	 * @return ��ʽ���Ľ��
	 */
	public static final <T> String formatCurrency(final T number) {
		return formatNumber(number, 2);
	}
	
	/**
	 * ��ʽ����Ԫ��С�������4λ���������룬��λ��0
	 * 
	 * @param number Ҫ��ʽ���Ľ���λ��Ԫ
	 * @return ��ʽ���Ľ��
	 */
	public static final <T> String formatWanCurrency(final T number) {
		return formatNumber(number, 4);
	}
	
	/**
	 * ��ʽ������Ĭ�Ϸ�����С�������6λ���������룬��λ��0
	 * 
	 * @param number Ҫ��ʽ���ĵ���
	 * @return ��ʽ���ĵ���
	 */
	public static final <T> String formatUnitPrice(final T number) {
		return formatNumber(number, 6);
	}
	
	/**
	 * ��ʽ�����۷�����С�������ʾֻ��ʾ����Ч���֣���ౣ��6λ����������
	 * 
	 * @param number Ҫ��ʽ���ĵ���
	 * @param significantDigit �Ƿ�ֻ��ʾ��Ч����
	 * @return ��ʽ���ĵ���
	 */
	public static final <T> String formatSignificantUnitPrice(final T number) {
		return format(number, 6, true);
	}
	
	/**
	 * ��ʽ��������С�������4λ���������룬ֻ��ʾ��Ч����
	 * 
	 * @param number Ҫ��ʽ��������
	 * @return ��ʽ��������
	 */
	public static final <T> String formatQuantity(final T number) {
		return format(number, 4, true);
	}
	/**
	 * ��ʽ������Ϊ�ַ���������С�����2λ���������룬��λ��0
	 * 
	 * @param number Ҫ��ʽ��������
	 * @return ��ʽ���ַ���
	 */
	public static final <T> String formatNumber(final T number) {
		return formatNumber(number, 2);
	}
	
	/**
	 * ��ʽ������Ϊ�ַ���������С�����һ����λ�����������룬��λ��0
	 * 
	 * @param number ����
	 * @param precision ����ľ���
	 * @return ��ʽ������
	 */
	public static final <T> String formatNumber(final T number, final int precision) {
		return format(number, precision, false);
	}
	
	/**
	 * ��ʽ���ַ���Ϊ2��������
	 * 
	 * @param number �ַ������͵Ķ���������
	 * @return {@link Integer}���͵���ֵ
	 */
	public static final int toBinaryNumber(final String number) {
		if (number == null) {
			return 0;
		}
		
		try {
			return Integer.valueOf(number, 2);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Integer}������ṩ��{@code T}���͵�ֵΪ�գ���ô����{@code 0}
	 * 
	 * @param number Ҫת����ֵ
	 * @return {@link Integer}���͵���ֵ
	 */
	public static final <T> int toInt(final T number) {
		if (number == null) {
			return 0;
		}
		
		return ConvertUtils.convert(number, int.class, 0);
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Long}������ṩ��{@code T}���͵�ֵΪ�գ���ô����{@code 0L}
	 * 
	 * @param number Ҫת����ֵ
	 * @return {@link Long}���͵���ֵ
	 */
	public static final <T> long toLong(final T number) {
		if (number == null) {
			return 0l;
		}
		
		return ConvertUtils.convert(number, long.class, 0l);
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Double}������ṩ��{@Ling Object}Ϊ�գ���ô����{@code 0}
	 * 
	 * @param number Ҫת����ֵ
	 * @return {@link Double}���͵���ֵ
	 */
	public static final <T> double toDouble(final T number) {
		if (number == null) {
			return 0d;
		}
		
		return ConvertUtils.convert(number, double.class, 0d);
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Double}������ṩ��{@Ling Object}Ϊ�գ���ô����{@code 0}<br>
	 * �������ṩ�ľ��Ƚ��������������
	 * 
	 * @param number Ҫת����ֵ
	 * @param precision ����ľ���
	 * @return {@link Double}���͵���ֵ
	 */
	public static final <T> double toDouble(final T number, int precision) {
		if (number == null) {
			return 0d;
		}
		
		double d = toDouble(number);
		
		if (precision == 0) {
			return d;
		}
		
		return new BigDecimal(Double.toString(d)).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * ת��{@code T}���͵�ֵΪ{@link Float}������ṩ��{@code T}���͵�ֵΪ�գ���ô����{@code 0}
	 * 
	 * @param number Ҫת����ֵ
	 * @return {@link Float}���͵���ֵ
	 */
	public static final <T> float toFloat(final T number) {
		if (number == null) {
			return 0f;
		}
		
		return ConvertUtils.convert(number, float.class, 0f);
	}
	/*****************************************************************************
	 * ˽�з���
	 *****************************************************************************/
	/**
	 * ��ʽ�����֣�����Ϊ{@code null}����0������
	 * 
	 * @param number ����
	 * @param precision ����ľ���
	 * @param significantDigit �Ƿ�ֻ��ʾ��Ч����
	 * @return ��ʽ������
	 */
	private static final <T> String format(final T number, final int precision, final boolean significantDigit) {
		if (significantDigit) { // ֻ��ʾ��Ч���֣���λ����0
			final String formatedNumber = "" + toDouble(number, precision);
			
			if (StringUtils.endsWith(formatedNumber, ".0")) { // Ҫ����С�����û�����ֵ�ת�����ַ�����Ľ�β��.0
				return StringUtils.replaceOnce(formatedNumber, ".0", "", 0);
			} else {
				return formatedNumber;
			}
		} else { // ��0
			return String.format("%." + precision +"f", toDouble(number, precision));
		}
	}
}
