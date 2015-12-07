package com.sunwayworld.escm.core.dao.sql.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 获取数据库方言的工厂类
 */
@Repository
public class DialectFactory {
	@Autowired
	private DataSource dataSource;
	
	private static Dialect dialect;
	
	private DialectFactory() {}
	
	public static final synchronized Dialect getDialect() {
		if (dialect == null) {
			synchronized (Dialect.class) {
				if (dialect == null) {
					DialectFactory factory = new DialectFactory();
					
					factory.detectDialect();
				}
			}
		}
		
		return dialect;
	}
	
	private final void detectDialect() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			final DatabaseMetaData dbmd = conn.getMetaData();
			
			final String dbProductName = dbmd.getDatabaseProductName();
			
			if ("Microsoft SQL Server".equalsIgnoreCase(dbProductName)) {
				dialect = new MssqlDialect();
			} else if ("MySQL".equalsIgnoreCase(dbProductName)) {
				dialect = new MysqlDialect();
			} else if ("Oracle".equalsIgnoreCase(dbProductName)) {
				dialect = new OracleDialect();
			} else { // 默认是Oracle
				dialect = new OracleDialect();
			}
		} catch (Exception ex) {
			dialect = new OracleDialect();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {}
				conn = null;
			}
		}
	}
}
