package com.sunwayworld.escm.core.dao.sql.dialect;

import java.util.Date;

import com.sunwayworld.escm.core.utils.DateTimeUtils;

public class MysqlDialect implements Dialect {
	@Override
	public String decode(String column, String... strs) {
		final StringBuilder segment = new StringBuilder("CASE\n");
		
		for (int i = 0; i < strs.length; i++) {
			if (i%2 == 1) {
				segment.append(" THEN ").append(strs[i]).append("\n");
			} else {
				if (i < strs.length - 1) { // 不是最后一个
					segment.append("  WHEN ").append(column).append(" = ").append(strs[i]);
				} else {
					segment.append("  ELSE ").append(strs[i]).append("\n");
				}
			}
		}
		segment.append("END");
		
		return segment.toString();
	}
	
	@Override
	public String concat(String... strs) {
		if (strs == null || strs.length == 0)
			return "";
		
		final StringBuilder segment = new StringBuilder();
		
		for(int i = 0; i < strs.length; i++ ) {
			if (i > 0) {
				segment.append(" || ");
			}
			
			segment.append(nvl(strs[i], "''"));
		}
		
		return segment.toString();
	}
	
	@Override
	public String nvl(String column, String defaultValue) {
		return "IFNULL(" + column + ", " + defaultValue + ")";
	}
	
	@Override
	public String toLimitSql(final String sql, int offset, int limit) {
		return new StringBuilder(sql)
		                 .append("\n LIMIT ")
		                 .append(offset)
		                 .append(" , ")
		                 .append(limit).toString();
	}

	@Override
	public String toSqlDate(String formattedDate) {
		return new StringBuilder("STR_TO_DATE('")
		                 .append(formattedDate)
		                 .append(", '%Y-%m-%d')").toString();
	}

	
	@Override
	public String toSqlDate(final Date date) {
		return toSqlDate("'" +DateTimeUtils.formatDate(date) + "'");
	}
	
	@Override
	public String toSqlDateTime(String formattedDateTime) {
		return new StringBuilder("STR_TO_DATE(")
		                 .append(formattedDateTime)
		                 .append(", '%Y-%m-%d %H:%i:%s')").toString();
	}

	@Override
	public String toSqlDateTime(final Date date) {
		return toSqlDateTime("'" + DateTimeUtils.formatDateTime(date) + "'");
	}

	@Override
	public String toSqlTime(String formattedTime) {
		return new StringBuilder("STR_TO_DATE(")
		                 .append(formattedTime)
		                 .append(", '%H:%i:%s')").toString();
	}

	@Override
	public String toSqlTime(final Date date) {
		return toSqlTime("'" + DateTimeUtils.formatTime(date) + "'");
	}

	@Override
	public String sysdate() {
		return "NOW()";
	}

	@Override
	public String dateToChar(String dateColumn) {
		return "DATE_FORMAT(" + dateColumn +", '%Y-%m-%d %H:%i:%s')";
	}

	@Override
	public String dateDiffInSeconds(String leftDateColumn,
			String rightDatecolumn) {
		return null;
	}
}
