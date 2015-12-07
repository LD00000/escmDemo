package com.sunwayworld.escm.project.sql;

import com.sunwayworld.escm.core.dao.sql.BoundSql;
import com.sunwayworld.escm.core.dao.sql.BoundSqlBuilder;
import com.sunwayworld.escm.project.model.ProjectQBean;

public class ProjectSqlBuilder {
	/**
	 * ��ȡͨ��������ѯ����������Ŀ��SQL
	 * 
	 * @param condition ��ѯ����
	 * @return ��ѯ����������Ŀ��SQL
	 */
	public BoundSql getSelectProjectByConditionSql(final ProjectQBean condition) {
		StringBuilder sql = new StringBuilder("SELECT EC_PROJECT.*,\n")
		                              .append("       EC_USER.NAME CREATENAME\n")
		                              .append("  FROM EC_PROJECT, EC_USER\n")
		                              .append(" WHERE EC_PROJECT.CREATEUSER = EC_USER.ID\n");
		
		final BoundSqlBuilder sqlBuilder = new BoundSqlBuilder();
		BoundSql conditionBoundSql = sqlBuilder.buildConditionBoundSql(condition);
		
		if (conditionBoundSql != null) {
			sql.append(conditionBoundSql.getSql());
		}
		
		sql.append(" ORDER BY EC_PROJECT.CREATEDATE");
		
		if (conditionBoundSql != null) {
			return new BoundSql(sql.toString(), conditionBoundSql.getInValues());
		} else {
			return new BoundSql(sql.toString());
		}
	}
}
