package com.sunwayworld.escm.core.dao.sql;

/**
 * Bean�ĳ�Ա�������ڲ�ѯ����ʱ����ѯ������ƥ��ģʽ
 */
public @interface Condition {
	Match match() default Match.LEFT;
	String alias() default ""; // Ҫƥ��ı����������Alias
	String column() default ""; // Ҫƥ�������
	String defaultValue() default "";
}
