package com.sunwayworld.escm.core.utils;

import java.math.BigDecimal;

/**
 * 数字处理 的工具类
 */
public final class NumberUtils {
	/**
	 * 格式化金额默认方法，小数点后保留2位，四舍五入，空位补0
	 * 
	 * @param number 要格式化的金额
	 * @return 格式化的金额
	 */
	public static final <T> String formatCurrency(final T number) {
		return formatNumber(number, 2);
	}
	
	/**
	 * 格式化万元金额，小数点后保留4位，四舍五入，空位补0
	 * 
	 * @param number 要格式化的金额，单位万元
	 * @return 格式化的金额
	 */
	public static final <T> String formatWanCurrency(final T number) {
		return formatNumber(number, 4);
	}
	
	/**
	 * 格式化单价默认方法，小数点后保留6位，四舍五入，空位补0
	 * 
	 * @param number 要格式化的单价
	 * @return 格式化的单价
	 */
	public static final <T> String formatUnitPrice(final T number) {
		return formatNumber(number, 6);
	}
	
	/**
	 * 格式化单价方法，小数点后显示只显示到有效数字，最多保留6位，四舍五入
	 * 
	 * @param number 要格式化的单价
	 * @param significantDigit 是否只显示有效数字
	 * @return 格式化的单价
	 */
	public static final <T> String formatSignificantUnitPrice(final T number) {
		return format(number, 6, true);
	}
	
	/**
	 * 格式化数量，小数点后保留4位，四舍五入，只显示有效数字
	 * 
	 * @param number 要格式化的数量
	 * @return 格式化的数量
	 */
	public static final <T> String formatQuantity(final T number) {
		return format(number, 4, true);
	}
	/**
	 * 格式化数字为字符串，保留小数点后2位，四舍五入，空位补0
	 * 
	 * @param number 要格式化的数字
	 * @return 格式化字符串
	 */
	public static final <T> String formatNumber(final T number) {
		return formatNumber(number, 2);
	}
	
	/**
	 * 格式化数字为字符串，保留小数点后一定的位数，四舍五入，空位补0
	 * 
	 * @param number 数字
	 * @param precision 输出的精度
	 * @return 格式化数字
	 */
	public static final <T> String formatNumber(final T number, final int precision) {
		return format(number, precision, false);
	}
	
	/**
	 * 格式化字符串为2进制数字
	 * 
	 * @param number 字符串类型的二进制数字
	 * @return {@link Integer}类型的数值
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
	 * 转换{@code T}类型的值为{@link Integer}，如果提供的{@code T}类型的值为空，那么返回{@code 0}
	 * 
	 * @param number 要转换的值
	 * @return {@link Integer}类型的数值
	 */
	public static final <T> int toInt(final T number) {
		if (number == null) {
			return 0;
		}
		
		return ConvertUtils.convert(number, int.class, 0);
	}
	
	/**
	 * 转换{@code T}类型的值为{@link Long}，如果提供的{@code T}类型的值为空，那么返回{@code 0L}
	 * 
	 * @param number 要转换的值
	 * @return {@link Long}类型的数值
	 */
	public static final <T> long toLong(final T number) {
		if (number == null) {
			return 0l;
		}
		
		return ConvertUtils.convert(number, long.class, 0l);
	}
	
	/**
	 * 转换{@code T}类型的值为{@link Double}，如果提供的{@Ling Object}为空，那么返回{@code 0}
	 * 
	 * @param number 要转换的值
	 * @return {@link Double}类型的数值
	 */
	public static final <T> double toDouble(final T number) {
		if (number == null) {
			return 0d;
		}
		
		return ConvertUtils.convert(number, double.class, 0d);
	}
	
	/**
	 * 转换{@code T}类型的值为{@link Double}，如果提供的{@Ling Object}为空，那么返回{@code 0}<br>
	 * 并根据提供的精度进行四舍五入操作
	 * 
	 * @param number 要转换的值
	 * @param precision 输出的精度
	 * @return {@link Double}类型的数值
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
	 * 转换{@code T}类型的值为{@link Float}，如果提供的{@code T}类型的值为空，那么返回{@code 0}
	 * 
	 * @param number 要转换的值
	 * @return {@link Float}类型的数值
	 */
	public static final <T> float toFloat(final T number) {
		if (number == null) {
			return 0f;
		}
		
		return ConvertUtils.convert(number, float.class, 0f);
	}
	/*****************************************************************************
	 * 私有方法
	 *****************************************************************************/
	/**
	 * 格式化数字，数字为{@code null}的用0来代替
	 * 
	 * @param number 数字
	 * @param precision 输出的精度
	 * @param significantDigit 是否只显示有效数字
	 * @return 格式化数字
	 */
	private static final <T> String format(final T number, final int precision, final boolean significantDigit) {
		if (significantDigit) { // 只显示有效数字，空位不补0
			final String formatedNumber = "" + toDouble(number, precision);
			
			if (StringUtils.endsWith(formatedNumber, ".0")) { // 要考虑小数点后没有数字的转换成字符串后的结尾是.0
				return StringUtils.replaceOnce(formatedNumber, ".0", "", 0);
			} else {
				return formatedNumber;
			}
		} else { // 补0
			return String.format("%." + precision +"f", toDouble(number, precision));
		}
	}
}
