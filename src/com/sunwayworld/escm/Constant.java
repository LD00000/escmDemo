package com.sunwayworld.escm;

public abstract class Constant {
	/** 允许登录失败的次数 **/
	public static final Integer MAX_LOGIN_FAIL_COUNT = 5;
	
	/** 角色的默认前缀 **/
	public static final String ROLE_DEFAULT_PREFIX = "ROLE_";
	
	/** EASYUI分页里每页中的记录数 **/
	public static final String EASYUI_PAGINATION_PAGESIZE = "rows";
	
	/** EASYUI分页里当前页数 **/
	public static final String EASYUI_PAGINATION_CURRPAGE = "page";
	
	/** 分割符 **/
	public static final String SEPARATOR = "$";
	/** 查询条件后缀 **/
	public static final String CONDITION_SUFFIX = SEPARATOR + "COND";
	
	/** 界面 Content 类型 **/
	public static final String CONTENT_TYPE = "text/plain; charset=UTF-8"; 
	
	public static final Integer YES = 1;
	public static final Integer NO = 0;
}
