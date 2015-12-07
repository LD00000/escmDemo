package com.sunwayworld.escm.core.dao.sql.dialect;

import java.util.Date;

/**
 * ��Բ�ͬ���ݿ��SQL�﷨��������Щ��ͬ���ݿ����е��﷨������
 * 
 */
public interface Dialect {
	public static final String ROW_NUMBER = "RN_";
	
	/**
	 * ��ȡ��Oracle���ݿ��Decode����һ�µ�SQL��䣬�÷���<br>
	 * decode("SHBZ", "'1'", "'�����'", "'2'", "'���ͨ��'", "'δ�ύ'")
	 * 
	 * @param column Ҫ�Ƚϵı������
	 * @param strs �ܵĳ���Ϊ����������λ��Ҫ��{@code column}�Ƚϵ�ֵ��ż��λ��ǰһ��ֵ��Ӧ�������ֵ<br>
	 *             ���һ��ֵ��Ĭ��ֵ������ǷǱ�����ֶΣ�ǰ��������{@code '}����
	 * @return SQL���Ƭ��
	 */
	String decode(String column, String ... strs);
	
	/**
	 * ��ȡƴ���ַ�����SQL���
	 * 
	 * @param strs Ҫƴ�ӵ��ַ����������Ǳ���������ַ�����������ַ���ǰ��Ҫ����{@code '}����
	 * @return SQL���Ƭ��
	 */
	String concat(String ... strs);
	
	/**
	 * ��ȡ����ָ������Ϊ��ʱ������һ��ֵ�����SQL���
	 * 
	 * @param column Ҫ�����ı������
	 * @param defaultValue Ĭ�ϵ�ֵ��������ַ���ǰ��Ҫ����{@code '}����
	 * @return SQL���Ƭ��
	 */
	String nvl(String column, String defaultValue);
	
	/**
	 * ��ҳ��Ϣ
	 * 
	 * @param sql ԭ��SQL���
	 * @param offset ��ѯ����ʼ����
	 * @param limit Ҫ��ѯ��������
	 * @return ��ӷ�ҳ��Ϣ��SQL���
	 */
	String toLimitSql(String sql, int offset, int limit);
	
	/**
	 * ��ָ���ĸ�ʽ��������ת����SQL���ڸ�ʽ���ַ������
	 * 
	 * @param formattedDate ��ʽ�������ڣ����ǰ����{@code '}����ʽΪ{@code yyyy-MM-dd}������ΪSQL���������ֶ�
	 * @return SQL�ַ������
	 */
	String toSqlDate(String formattedDate);
	
	/**
	 * ��ָ��������ת����SQL���ڸ�ʽ���ַ������
	 * 
	 * @param date Ҫת��������
	 * @return SQL�ַ������
	 */
	String toSqlDate(Date date);
	
	/**
	 * ��ָ���ĸ�ʽ��������ת����SQL����ʱ���ʽ���ַ������
	 * 
	 * @param formattedDateTime ��ʽ����ʱ�����ڣ����ǰ����{@code '}����ʽΪ{@code yyyy-MM-dd HH:mm:ss}������ΪSQL���������ֶ�
	 * @return SQL�ַ������
	 */
	String toSqlDateTime(String formattedDateTime);
	
	/**
	 * ��ָ��������ת����SQL����ʱ���ʽ���ַ������
	 * 
	 * @param date Ҫת����ʱ������
	 * @return SQL�ַ������
	 */
	String toSqlDateTime(Date date);
	
	/**
	 * ��ָ���ĸ�ʽ����ʱ��ת����SQLʱ���ʽ���ַ������
	 * 
	 * @param formattedTime ��ʽ����ʱ�䣬���ǰ����{@code '}����ʽΪ{@code HH:mm:ss}������ΪSQL���������ֶ�
	 * @return SQL�ַ������
	 */
	String toSqlTime(String formattedTime);
	
	/**
	 * ��ָ��������ת����SQLʱ���ʽ���ַ������
	 * 
	 * @param date Ҫת����ʱ��
	 * @return SQL�ַ������
	 */
	String toSqlTime(Date date);
	
	/**
	 * ��ȡ��ǰ���ڵ�SQL���
	 * 
	 * @return SQL�ַ������
	 */
	String sysdate();
	
	/**
	 * �ѱ���ָ����������{@code yyyy-MM-dd HH:mm:ss}��ʽת�����ַ���
	 * 
	 * @param dateColumn Ҫת���ı���������
	 * @return SQL�ַ������
	 */
	String dateToChar(String dateColumn);
	
	/**
	 * ��ȡ�������ڵĲ�ֵ�����������㣬��ת��������
	 * 
	 * @param leftDateColumn ��һ��������
	 * @param rightDatecolumn �ڶ���������
	 * @return SQL�ַ������
	 */
	String dateDiffInSeconds(String leftDateColumn, String rightDatecolumn);
}
