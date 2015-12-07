package com.sunwayworld.escm.core.dao.sql;

/**
 * Bean的成员变量用于查询条件时，查询条件的匹配模式
 */
public @interface Condition {
	Match match() default Match.LEFT;
	String alias() default ""; // 要匹配的表名或表名的Alias
	String column() default ""; // 要匹配的列名
	String defaultValue() default "";
}
