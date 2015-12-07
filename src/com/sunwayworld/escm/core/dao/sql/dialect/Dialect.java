package com.sunwayworld.escm.core.dao.sql.dialect;

import java.util.Date;

/**
 * 针对不同数据库的SQL语法，仅对那些不同数据库特有的语法做解析
 * 
 */
public interface Dialect {
	public static final String ROW_NUMBER = "RN_";
	
	/**
	 * 获取与Oracle数据库的Decode方法一致的SQL语句，用法：<br>
	 * decode("SHBZ", "'1'", "'审核中'", "'2'", "'审核通过'", "'未提交'")
	 * 
	 * @param column 要比较的表的列名
	 * @param strs 总的长度为奇数，奇数位是要与{@code column}比较的值，偶数位是前一个值对应的输出的值<br>
	 *             最后一个值是默认值，如果是非表里的字段，前后必须添加{@code '}符号
	 * @return SQL语句片段
	 */
	String decode(String column, String ... strs);
	
	/**
	 * 获取拼接字符串的SQL语句
	 * 
	 * @param strs 要拼接的字符串，可以是表的列名或字符串，如果是字符串前后要加上{@code '}符号
	 * @return SQL语句片段
	 */
	String concat(String ... strs);
	
	/**
	 * 获取表中指定的列为空时用另外一个值替代的SQL语句
	 * 
	 * @param column 要操作的表的列名
	 * @param defaultValue 默认的值，如果是字符串前后要加上{@code '}符号
	 * @return SQL语句片段
	 */
	String nvl(String column, String defaultValue);
	
	/**
	 * 分页信息
	 * 
	 * @param sql 原有SQL语句
	 * @param offset 查询的起始行数
	 * @param limit 要查询出的总量
	 * @return 添加分页信息的SQL语句
	 */
	String toLimitSql(String sql, int offset, int limit);
	
	/**
	 * 把指定的格式化的日期转换成SQL日期格式的字符串语句
	 * 
	 * @param formattedDate 格式化的日期，如果前后有{@code '}，格式为{@code yyyy-MM-dd}，否则为SQL语句的其他字段
	 * @return SQL字符串语句
	 */
	String toSqlDate(String formattedDate);
	
	/**
	 * 把指定的日期转换成SQL日期格式的字符串语句
	 * 
	 * @param date 要转换的日期
	 * @return SQL字符串语句
	 */
	String toSqlDate(Date date);
	
	/**
	 * 把指定的格式化的日期转换成SQL日期时间格式的字符串语句
	 * 
	 * @param formattedDateTime 格式化的时间日期，如果前后有{@code '}，格式为{@code yyyy-MM-dd HH:mm:ss}，否则为SQL语句的其他字段
	 * @return SQL字符串语句
	 */
	String toSqlDateTime(String formattedDateTime);
	
	/**
	 * 把指定的日期转换成SQL日期时间格式的字符串语句
	 * 
	 * @param date 要转换的时间日期
	 * @return SQL字符串语句
	 */
	String toSqlDateTime(Date date);
	
	/**
	 * 把指定的格式化的时间转换成SQL时间格式的字符串语句
	 * 
	 * @param formattedTime 格式化的时间，如果前后有{@code '}，格式为{@code HH:mm:ss}，否则为SQL语句的其他字段
	 * @return SQL字符串语句
	 */
	String toSqlTime(String formattedTime);
	
	/**
	 * 把指定的日期转换成SQL时间格式的字符串语句
	 * 
	 * @param date 要转换的时间
	 * @return SQL字符串语句
	 */
	String toSqlTime(Date date);
	
	/**
	 * 获取当前日期的SQL语句
	 * 
	 * @return SQL字符串语句
	 */
	String sysdate();
	
	/**
	 * 把表中指定日期列以{@code yyyy-MM-dd HH:mm:ss}格式转换成字符串
	 * 
	 * @param dateColumn 要转换的表中日期列
	 * @return SQL字符串语句
	 */
	String dateToChar(String dateColumn);
	
	/**
	 * 获取两个日期的差值，用秒来计算，并转换成数字
	 * 
	 * @param leftDateColumn 第一个日期列
	 * @param rightDatecolumn 第二个日期列
	 * @return SQL字符串语句
	 */
	String dateDiffInSeconds(String leftDateColumn, String rightDatecolumn);
}
