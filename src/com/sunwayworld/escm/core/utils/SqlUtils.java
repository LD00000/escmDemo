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
 * SQL�����صĹ�����
 */
public final class SqlUtils {
	/** SQL ��ѯ������������ **/
	public static final String COUNT_COLUMN = "COUNT";
	
	/** ���ڲ�ѯSQL���Ƿ���ORDER BY��������ʽ����ֹ���±���������ʽ **/
	private static final Pattern orderByPattern = Pattern.compile("\\s+(?i)order\\s+(?i)by\\s+[^)]+");
	
	/**
	 * ת��Ŀ��{@code T}ʵ��Ϊ��ע���SQL�ַ�����{@code null}תΪ{@code ""}��{@code '}תΪ����{@code ��}<br>
	 * �÷�����Ҫ��������SQL���
	 * 
	 * @param target Ҫת����{@code T}ʵ��
	 * @return ��ת���ķ�ע���SQL�ַ���
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
					chars[i] = '��';
				}
			}
			
			return String.valueOf(chars);
		}
		
		return ConvertUtils.convert(target, String.class, "");
	}
	
	/**
	 * ͨ��type��intֵ����{@link Types}��ȡSQL��ʵ����������
	 * 
	 * @param intType sql type��intֵ
	 * @return ʵ����������������ڵĻ�
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
	 * SQL�����ɾ��������䣬���ڲ�ѯ���������ϼƵ�
	 * 
	 * @param sql Ҫ�������SQL���
	 * @return ɾ��������SQL���
	 */
	public static final String removeOrder(final String sql) {
		if (StringUtils.isBlank(sql)) {
			return sql;
		}
		
		final Matcher matcher = orderByPattern.matcher(sql);
		
		return matcher.replaceFirst("");
	}
	
	/**
	 * ��ȡSQL��ѯ�������������Ͳ���ѡȡ�������е��ܺϼƵ���SQL
	 * 
	 * @param sql Ҫ����ѯ��SQL
	 * @param sumColumnNames Ҫȡ�ϼƵ�����
	 * @return �µ����ڲ�ѯ���������ܺϼƵ�SQL
	 */
	public static final String getTotalSql(final String sql, final String ... sumColumnNames) {
		if (sumColumnNames == null || sumColumnNames.length == 0) {
			return "SELECT COUNT(*) " + COUNT_COLUMN + " FROM (\n" + SqlUtils.removeOrder(sql) + "\n) -- ��ѯ����������������";
		}
		
		StringBuilder newSql = new StringBuilder("SELECT COUNT(*) ").append(COUNT_COLUMN);
				
		for (String columnName : sumColumnNames) {
			newSql.append(", SUM(").append(DialectFactory.getDialect().nvl(columnName.toUpperCase(), "0")).append(") SUM").append(columnName.toUpperCase());
		}
		
		newSql.append(" FROM (\n") 
		      .append(SqlUtils.removeOrder(sql)).append("\n")
		      .append(") -- ��ѯ�����������������Ͳ����еĺϼ�");
		
		return newSql.toString();
	}
}
