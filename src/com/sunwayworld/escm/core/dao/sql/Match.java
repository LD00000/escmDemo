package com.sunwayworld.escm.core.dao.sql;

/**
 * SQL��ѯ�����У���ѯ��ʽ
 */
public enum Match {
	/**
	 * ���
	 */
	EQUAL, // ���
	/**
	 * �ַ�������ƥ��  LIKE 'xxx' || %
	 * ʱ�䣺���ڵ���  >= TO_DATE(xxx, '��ʽ')
	 * ���֣� ���ڵ��� >= xxx
	 */
	LEFT, // �ַ�������ƥ�䡢ʱ����ڵ��ڡ����ִ��ڵ���
	/**
	 * �ַ������м�ƥ��  LIKE '%' || 'xxx' || '%'
	 * ������ͬ�� {@link Match#EQUAL}
	 */
	CENTER,
	/**
	 * �ַ�������ƥ��  LIKE '%' || 'xxx'
	 * ʱ�䣺С��  < TO_DATE(xxx, '��ʽ')
	 * ���֣� С�� < xxx
	 */
	RIGHT
}
