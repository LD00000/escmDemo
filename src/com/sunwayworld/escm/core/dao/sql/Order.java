package com.sunwayworld.escm.core.dao.sql;

/**
 * ����
 */
public class Order {
	private String order;
	
	private Order(String order) {
		this.order = order;
	}
	
	public final String getOrder() {
		return this.order;
	}
	
	/**
	 * ����
	 * 
	 * @param columnName ������ֶ�
	 * @return ����Order
	 */
	public static final Order asc(final String columnName) {
		return new Order(columnName);
	}
	
	/**
	 * ����
	 * 
	 * @param columnName ������ֶ�
	 * @return ����Order
	 */
	public static final Order desc(final String columnName) {
		return new Order(columnName + " DESC");
	}
}
