package com.sunwayworld.escm.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunwayworld.escm.core.dao.sql.dialect.DialectFactory;

/**
 * SQL语句相关的工具类
 */
public final class SqlUtils {
	/** SQL 查询总行数的列名 **/
	public static final String COUNT_COLUMN = "COUNT";
	
	/** 用于查询SQL中是否含有ORDER BY的正则表达式，防止重新编译正则表达式 **/
	private static final Pattern orderByPattern = Pattern.compile("\\s+(?i)order\\s+(?i)by\\s+[^)]+");
	
	/**
	 * 转换目标{@code T}实例为防注入的SQL字符串，{@code null}转为{@code ""}且{@code '}转为中文{@code ‘}<br>
	 * 该方法主要用于生成SQL语句
	 * 
	 * @param target 要转换的{@code T}实例
	 * @return 已转换的防注入的SQL字符串
	 */
	public static final <T> String toSql(final T target) {
		if (target == null) {
			return "";
		}
		
		final Class<?> clazz = target.getClass();
		
		if (String.class.isAssignableFrom(clazz)
				|| Clob.class.isAssignableFrom(clazz)
				|| Blob.class.isAssignableFrom(clazz)) {
			final char[] chars = ConvertUtils.convert(target, String.class, "").toCharArray();
			
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] == '\'') {
					chars[i] = '‘';
				}
			}
			
			return String.valueOf(chars);
		}
		
		return ConvertUtils.convert(target, String.class, "");
	}
	
	/**
	 * 通过type的int值，从{@link Types}获取SQL的实际类型名称
	 * 
	 * @param intType sql type的int值
	 * @return 实际类型名，如果存在的话
	 */
	public static final String getSqlType(int intType) {
		for (Field property : Types.class.getFields()) {
			if (Modifier.isStatic(property.getModifiers()) 
					&& Modifier.isPublic(property.getModifiers())) {
				
				try {
					if (intType == property.getInt(null)) {
						return property.getName();
					}
				} catch (IllegalArgumentException e) { /* ignore */
				} catch (IllegalAccessException e) { /* ignore */ }
			}
		}
		
		return "VARCHAR";
	}
	
	/**
	 * SQL语句里删除排序语句，用于查询总行数，合计等
	 * 
	 * @param sql 要被处理的SQL语句
	 * @return 删除排序后的SQL语句
	 */
	public static final String removeOrder(final String sql) {
		if (StringUtils.isBlank(sql)) {
			return sql;
		}
		
		final Matcher matcher = orderByPattern.matcher(sql);
		
		return matcher.replaceFirst("");
	}
	
	/**
	 * 获取SQL查询出来的总行数和部分选取出来的列的总合计的新SQL
	 * 
	 * @param sql 要被查询的SQL
	 * @param sumColumnNames 要取合计的列名
	 * @return 新的用于查询总行数和总合计的SQL
	 */
	public static final String getTotalSql(final String sql, final String ... sumColumnNames) {
		if (sumColumnNames == null || sumColumnNames.length == 0) {
			return "SELECT COUNT(*) " + COUNT_COLUMN + " FROM (\n" + SqlUtils.removeOrder(sql) + "\n) -- 查询满足条件的总数量";
		}
		
		StringBuilder newSql = new StringBuilder("SELECT COUNT(*) ").append(COUNT_COLUMN);
				
		for (String columnName : sumColumnNames) {
			newSql.append(", SUM(").append(DialectFactory.getDialect().nvl(columnName.toUpperCase(), "0")).append(") SUM").append(columnName.toUpperCase());
		}
		
		newSql.append(" FROM (\n") 
		      .append(SqlUtils.removeOrder(sql)).append("\n")
		      .append(") -- 查询满足条件的总数量和部分列的合计");
		
		return newSql.toString();
	}
}
