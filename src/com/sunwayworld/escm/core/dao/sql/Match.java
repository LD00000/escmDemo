package com.sunwayworld.escm.core.dao.sql;

/**
 * SQL查询条件中，查询方式
 */
public enum Match {
	/**
	 * 相等
	 */
	EQUAL, // 相等
	/**
	 * 字符串：左匹配  LIKE 'xxx' || %
	 * 时间：大于等于  >= TO_DATE(xxx, '格式')
	 * 数字： 大于等于 >= xxx
	 */
	LEFT, // 字符串：左匹配、时间大于等于、数字大于等于
	/**
	 * 字符串：中间匹配  LIKE '%' || 'xxx' || '%'
	 * 其它等同于 {@link Match#EQUAL}
	 */
	CENTER,
	/**
	 * 字符串：右匹配  LIKE '%' || 'xxx'
	 * 时间：小于  < TO_DATE(xxx, '格式')
	 * 数字： 小于 < xxx
	 */
	RIGHT
}
