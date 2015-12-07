package com.sunwayworld.escm.core.dao.sql;

/**
 * ÅÅĞò
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
	 * ÕıĞò
	 * 
	 * @param columnName ÅÅĞòµÄ×Ö¶Î
	 * @return ÅÅĞòOrder
	 */
	public static final Order asc(final String columnName) {
		return new Order(columnName);
	}
	
	/**
	 * µ¹Ğò
	 * 
	 * @param columnName ÅÅĞòµÄ×Ö¶Î
	 * @return ÅÅĞòOrder
	 */
	public static final Order desc(final String columnName) {
		return new Order(columnName + " DESC");
	}
}
