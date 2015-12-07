package com.sunwayworld.escm.core.dao;

import java.io.Serializable;

/**
 * 分页信息
 */
@SuppressWarnings("serial")
public final class Pagination implements Serializable{
	/**
	 * 默认的每页显示的数目
	 */
	public static final int DEFAULT_PAGE_SIZE = 50;
	
	/**
	 * 记录的总数未被赋值
	 */
	private static final int TOTAL_RECORD_NOT_SPECIFIED = -1;
	
	/**
	 * 记录的总数
	 */
	private int totalRecord = TOTAL_RECORD_NOT_SPECIFIED;
	/**
	 * 页面的总数
	 */
	private int totalPage = 0;
	/**
	 * 当前的页面
	 */
	private int currPage = 1;
	/**
	 * 每页显示的数目
	 */
	private int pageSize = DEFAULT_PAGE_SIZE;
	
	public Pagination() {}
	
	public Pagination(int totalRecord, int pageSize, int currPage) {
		this.totalRecord = totalRecord;
		this.pageSize = pageSize;
		this.currPage = currPage;
		
		this.init();
	}
	
	public Pagination(int pageSize, int currPage) {
		this.totalRecord = TOTAL_RECORD_NOT_SPECIFIED;
		this.pageSize = pageSize;
		this.currPage = currPage;
		
		this.init();
	}
	
	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
		
		init();
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
		
		init();
	}

	public int getCurrPage() {
		return this.currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
		
		init();
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		
		init();
	}
	
	public void next() {
		++this.currPage;
		
		init();
	}
	
	public int getPageStartRow() {
		return this.pageSize * (this.currPage - 1);
	}
	
	public int getPageEndRow() {
		return pageSize * currPage;
	}
	
	/**
	 * 当前分页记录的数量
	 */
	public int getCurrentRecord() {
		if (this.totalPage == 0
				|| this.totalRecord == 0) {
			return 0;
		}
		
		if (this.currPage * this.pageSize <= this.totalRecord) {
			return this.pageSize;
		}
		
		return this.totalRecord - (this.currPage - 1) * this.pageSize;
	}
	
	/*********************************************************************************
	 * 私有方法
	 *********************************************************************************/
	/**
	 * 重置，初始化
	 */
	private void init() {
		if (pageSize < 1) {
			this.pageSize = DEFAULT_PAGE_SIZE;
		}
		
		if (currPage < 1) {
			this.currPage = 1;
		}
		
		if (totalRecord == TOTAL_RECORD_NOT_SPECIFIED // 记录的总数未被赋值
				|| totalRecord < 0) {// 记录的总数赋值有误
			return;
		}
		
		if (totalRecord == 0) {
			this.totalPage = 0;
		} else {
			this.totalPage = ((this.totalRecord % this.pageSize == 0) ? 
					this.totalRecord / this.pageSize // 总数可被每页显示的数目除尽
					: (this.totalRecord / this.pageSize + 1)); // 总数不可被每页显示的数目除尽
		}
		
		if (this.totalRecord != TOTAL_RECORD_NOT_SPECIFIED 
				&& this.totalPage < this.currPage) {
			if (this.totalPage > 0)
				this.currPage = this.totalPage;
			else
				this.currPage = 1;
		}
	}
}
