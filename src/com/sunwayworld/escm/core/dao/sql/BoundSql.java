package com.sunwayworld.escm.core.dao.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunwayworld.escm.core.Pair;
import com.sunwayworld.escm.core.utils.StringUtils;

/**
 * {@link java.sql.PreparedStatement}所要执行的SQL的包装类
 */
public class BoundSql implements Serializable {
	private static final long serialVersionUID = 9004216128304952283L;

	/**
	 * SQL 语句
	 */
	private String sql;
	
	/**
	 * 版本的名称和值
	 */
	private Pair<String, Long> versionPair;
	
	/**
	 * 用于PreparedStatement的值
	 */
	private List<Object> inValues = new ArrayList<Object>();
	
	public BoundSql(final String sql, final Object ... inValues) {
		this.sql = sql;
		
		if (inValues != null) {
			Collections.addAll(this.inValues, inValues);
		}
	}
	
	public BoundSql(final String sql, final List<Object> inValues) {
		this.sql = sql;
		if (inValues != null) {
			inValues.addAll(inValues);
		}
	}
	
	public void updateSql(final String sql) {
		this.sql = sql;
	}
	
	public List<Object> getInValues() {
		return inValues;
	}
	
	public Object[] getInValuesAsArray() {
		return inValues.toArray();
	}

	/**
	 * 设置版本
	 * 
	 * @param versionName 版本的列名
	 * @param version 版本的值
	 */
	public void setVersion(final String versionName, final Long version) {
		versionPair = new Pair<String, Long>(versionName.toUpperCase(), version);
	}
	
	/**
	 * 获取SQL类型
	 * 
	 * @return {@link SqlType}
	 */
	public SqlType getSqlType() {
		if (StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), "INSERT ")) {
			return SqlType.INSERT_SQL;
		} else if (StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), "UPDATE ")) {
			return SqlType.UPDATE_SQL;
		} else if (StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), "DELETE ")) {
			return SqlType.DELETE_SQL;
		} else {
			return SqlType.SELECT_SQL;
		}
	}
	
	/**
	 * 获取SQL，已设置版本的要控制版本
	 * 
	 * @return 要执行的SQL
	 */
	public String getSql() {
		if (versionPair == null) { 
			return sql;
		}
		
		// 更新和删除需要版本控制
		String newSql = sql;
		
		// 如果是更新的话，版本增加
		if (SqlType.UPDATE_SQL.equals(getSqlType())) {
			boolean updateVersionOnly = sql.matches(".+(\\s*|\t|\r|\n)+(?i)set(?-i)(\\s*|\t|\r|\n)+(?i)where(?-i)(\\s*|\t|\r|\n)+.+"); // 仅更新版本;
			
			String suffix = (updateVersionOnly ? "\n " : ",\n       "); // 仅更新版本时后面不加逗号
			
			// 版本增加
			newSql = sql.replaceFirst("(\\s*|\t|\r|\n)+(?i)set(?-i)(\\s*|\t|\r|\n)+", "\n   SET " + versionPair.getKey() + " = '" + (versionPair.getValue() + 1) + "'" + suffix);
			
			// 必须要有Where语句
			Pattern p = Pattern.compile("(\\s*|\t|\r|\n)+(?i)where(?-i)(\\s*|\t|\r|\n)+");
			Matcher matcher = p.matcher(newSql);
			if (matcher.find()) {
				newSql += "\n   AND " + versionPair.getKey() + " = '" + versionPair.getValue() + "' -- 版本控制";
			}
		} else if (SqlType.DELETE_SQL.equals(getSqlType())) {
			// 必须要有Where语句
			Pattern p = Pattern.compile("(\\s*|\t|\r|\n)+(?i)where(?-i)(\\s*|\t|\r|\n)+");
			Matcher matcher = p.matcher(newSql);
			
			if (matcher.find()) {
				newSql += "\n   AND " + versionPair.getKey() + " = '" + versionPair.getValue() + "' -- 版本控制";
			}
		}
		
		return newSql;
	}
	
	@Override
	public String toString() {
		final SqlType type = getSqlType();
		
		StringBuilder sb = new StringBuilder();
		
		if (SqlType.SELECT_SQL.equals(type)) {
			sb.append("=============== SELECT SQL START ===============\n");
		} else if (SqlType.INSERT_SQL.equals(type)) {
			sb.append("=============== INSERT SQL START ===============\n");
		} else if (SqlType.UPDATE_SQL.equals(type)) {
			sb.append("=============== UPDATE SQL START ===============\n");
		} else if (SqlType.DELETE_SQL.equals(type)) {
			sb.append("=============== DELETE SQL START ===============\n");
		} 		

		sb.append(sql);
		
		if (!inValues.isEmpty()) {
			sb.append("\n>>> In Values :");
			for (int i = 0; i < inValues.size(); i++) {
				
				if (i != 0)
					sb.append(" , ");
				
				Object inValue = inValues.get(i);
				
				if (inValue == null) {
					sb.append("''");
				} else {
					String strValue = inValue.toString();
					
					if (strValue.length() > 20) {
						strValue = strValue.substring(0, 20) + " ...";
					}
					
					sb.append(strValue).append("(").append(inValue.getClass().getName()).append(")");
				}
			}
		}
		
		if (SqlType.SELECT_SQL.equals(type)) {
			sb.append("\n===============  SELECT SQL END  ===============");
		} else if (SqlType.INSERT_SQL.equals(type)) {
			sb.append("\n===============  INSERT SQL END  ===============");
		} else if (SqlType.UPDATE_SQL.equals(type)) {
			sb.append("\n===============  UPDATE SQL END  ===============");
		} else if (SqlType.DELETE_SQL.equals(type)) {
			sb.append("\n===============  DELETE SQL END  ===============");
		}
		
		return sb.toString();
	}
}
