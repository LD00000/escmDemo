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
 * {@link java.sql.PreparedStatement}��Ҫִ�е�SQL�İ�װ��
 */
public class BoundSql implements Serializable {
	private static final long serialVersionUID = 9004216128304952283L;

	/**
	 * SQL ���
	 */
	private String sql;
	
	/**
	 * �汾�����ƺ�ֵ
	 */
	private Pair<String, Long> versionPair;
	
	/**
	 * ����PreparedStatement��ֵ
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
	 * ���ð汾
	 * 
	 * @param versionName �汾������
	 * @param version �汾��ֵ
	 */
	public void setVersion(final String versionName, final Long version) {
		versionPair = new Pair<String, Long>(versionName.toUpperCase(), version);
	}
	
	/**
	 * ��ȡSQL����
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
	 * ��ȡSQL�������ð汾��Ҫ���ư汾
	 * 
	 * @return Ҫִ�е�SQL
	 */
	public String getSql() {
		if (versionPair == null) { 
			return sql;
		}
		
		// ���º�ɾ����Ҫ�汾����
		String newSql = sql;
		
		// ����Ǹ��µĻ����汾����
		if (SqlType.UPDATE_SQL.equals(getSqlType())) {
			boolean updateVersionOnly = sql.matches(".+(\\s*|\t|\r|\n)+(?i)set(?-i)(\\s*|\t|\r|\n)+(?i)where(?-i)(\\s*|\t|\r|\n)+.+"); // �����°汾;
			
			String suffix = (updateVersionOnly ? "\n " : ",\n       "); // �����°汾ʱ���治�Ӷ���
			
			// �汾����
			newSql = sql.replaceFirst("(\\s*|\t|\r|\n)+(?i)set(?-i)(\\s*|\t|\r|\n)+", "\n   SET " + versionPair.getKey() + " = '" + (versionPair.getValue() + 1) + "'" + suffix);
			
			// ����Ҫ��Where���
			Pattern p = Pattern.compile("(\\s*|\t|\r|\n)+(?i)where(?-i)(\\s*|\t|\r|\n)+");
			Matcher matcher = p.matcher(newSql);
			if (matcher.find()) {
				newSql += "\n   AND " + versionPair.getKey() + " = '" + versionPair.getValue() + "' -- �汾����";
			}
		} else if (SqlType.DELETE_SQL.equals(getSqlType())) {
			// ����Ҫ��Where���
			Pattern p = Pattern.compile("(\\s*|\t|\r|\n)+(?i)where(?-i)(\\s*|\t|\r|\n)+");
			Matcher matcher = p.matcher(newSql);
			
			if (matcher.find()) {
				newSql += "\n   AND " + versionPair.getKey() + " = '" + versionPair.getValue() + "' -- �汾����";
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
