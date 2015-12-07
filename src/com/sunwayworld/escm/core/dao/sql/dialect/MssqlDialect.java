package com.sunwayworld.escm.core.dao.sql.dialect;

import java.util.Date;

public class MssqlDialect implements Dialect {
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
				segment.append(" + ");
			}
			
			segment.append(nvl(strs[i], "''"));
		}
		
		return segment.toString();
	}
	
	@Override
	public String nvl(String column, String defaultValue) {
		return "ISNULL(" + column + ", " + defaultValue + ")";
	}
	
	@Override
	public String toLimitSql(String sql, int offset, int limit) {
		return null;
	}

	@Override
	public String toSqlDate(String formattedDate) {
		return null;
	}

	@Override
	public String toSqlDate(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSqlDateTime(String formattedDateTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSqlDateTime(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSqlTime(String formattedTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSqlTime(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sysdate() {
		return "SYSDATE";
	}

	@Override
	public String dateToChar(String dateColumn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String dateDiffInSeconds(String leftDateColumn,
			String rightDatecolumn) {
		// TODO Auto-generated method stub
		return null;
	}
}
