package com.sunwayworld.escm.core.dao.sql.dialect;

import java.util.Date;

import com.sunwayworld.escm.core.utils.DateTimeUtils;

public class OracleDialect implements Dialect {
	@Override
	public String decode(String column, String... strs) {
		final StringBuilder segment = new StringBuilder("DECODE(").append(column);
		
		for (int i = 0; i < strs.length; i++) {
			if (i > 0 && (i % 4 == 0)) {
				segment.append(",\n");
			} else {
				segment.append(", ");
			}
			
			segment.append(strs[i]);
		}
		
		segment.append(")");
		
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
			
			segment.append(strs[i]);
		}
		
		return segment.toString();
	}
	
	@Override
	public String nvl(String column, String defaultValue) {
		return "NVL(" + column + ", " + defaultValue + ")";
	}
	
	@Override
	public String toLimitSql(final String sql, int offset, int limit) {
		return new StringBuilder("SELECT * FROM ( SELECT S_.*, ROWNUM ").append(ROW_NUMBER).append(" FROM (\n")
		                 .append(sql)
		                 .append("\n) S_ WHERE ROWNUM <=")
		                 .append(offset + limit)
		                 .append(") WHERE ").append(ROW_NUMBER).append(" >")
		                 .append(offset).toString();
	}
	
	@Override
	public String toSqlDate(String formattedDate) {
		return new StringBuilder("TO_DATE(")
		                 .append(formattedDate)
		                 .append(", 'yyyy-MM-dd')").toString();
	}
	
	@Override
	public String toSqlDate(final Date date) {
		return toSqlDate("'" + DateTimeUtils.formart(date, DateTimeUtils.DEFAULT_DATE_FORMAT) + "'");
	}
	
	@Override
	public String toSqlDateTime(String formattedDateTime) {
		return new StringBuilder("TO_DATE(")
		                 .append(formattedDateTime)
		                 .append(", 'yyyy-MM-dd HH24:mi:ss')").toString();
	}

	@Override
	public String toSqlDateTime(final Date date) {
		return toSqlDateTime("'" + DateTimeUtils.formart(date, DateTimeUtils.DEFAULT_DATETIME_FORMAT) + "'");
	}

	@Override
	public String toSqlTime(String formattedTime) {
		return new StringBuilder("TO_DATE(")
		                 .append(formattedTime)
		                 .append(", 'HH24:mi:ss')").toString();
	}
	
	@Override
	public String toSqlTime(final Date date) {
		return toSqlTime("'" + DateTimeUtils.formart(date, DateTimeUtils.DEFAULT_TIME_FORMAT) + "'");
	}

	@Override
	public String sysdate() {
		return "SYSDATE";
	}
	
	@Override
	public String dateToChar(String dateColumn) {
		return "TO_CHAR(" + dateColumn +", 'yyyy-MM-dd HH24:mi:ss')";
	}

	@Override
	public String dateDiffInSeconds(String leftDateColumn,
			String rightDatecolumn) {
		return "TO_NUMBER((" + leftDateColumn + " - " + rightDatecolumn + ") * 24 * 60 * 60)";
	}
	
}
