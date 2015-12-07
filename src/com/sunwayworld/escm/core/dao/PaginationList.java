package com.sunwayworld.escm.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sunwayworld.escm.core.utils.ConvertUtils;
import com.sunwayworld.escm.core.utils.FieldUtils;
import com.sunwayworld.escm.core.utils.NumberUtils;

/**
 * 查询分页信息
 * 
 * @param <T>
 */
public class PaginationList<T> extends ArrayList<T> {
	private static final long serialVersionUID = -2665305751128106956L;

	private static final Logger logger = Logger.getLogger(PaginationList.class);
	
	/** 分页信息 **/
	private Pagination pagination;
	/** 查询全部的合计信息 **/
	private final Map<String, Double> sumInfo = new HashMap<String, Double>();
	/** 查询查询出来的合计信息 **/
	private final Map<String, Double> sumSelectedInfo = new HashMap<String, Double>();
	
	public PaginationList() {
		super();
	}
	
	public PaginationList(Pagination pagination) {
		super();
		
		this.pagination = pagination;
	}
	
	public PaginationList(int currPage, int pageSize) {
		super();
		
		pagination = new Pagination(pageSize, currPage);
	}	
	
	/*******************************************************************************************************
	 * 分页信息
	 *******************************************************************************************************/
	public void setTotalRecord(int totalRecord) {
		if (pagination != null)
			pagination.setTotalRecord(totalRecord);
	}
	
	public void setTotalPage(int totalPage) {
		if (pagination != null) {
			pagination.setTotalPage(totalPage);
		}
	}
	public Pagination getPagination() {
		if (pagination == null) {
			return new Pagination(this.size(), this.size(), 1);
		}
		
		return pagination; 
	}
	public Map<String, Double> getSumInfo() {
		return this.sumInfo;
	}
	
	/*******************************************************************************************************
	 * 求和信息
	 *******************************************************************************************************/
	public <P> void setSumInfo(final Map<String, P> sumInfo) {
		if (sumInfo.isEmpty()) {
			return;
		}
		
		final Iterator<Entry<String, P>> iterator = sumInfo.entrySet().iterator();
		
		while(iterator.hasNext()) {
			final Entry<String, P> entry = iterator.next();
			
			final String key = entry.getKey();
			
			if (key != null && key.startsWith("SUM")) {
				this.sumInfo.put(key, NumberUtils.toDouble(entry.getValue()));
			}
		}
	}
	
	public void setSelectedSumInfo(final String ... keys) {
		if (this.size() == 0) {
			logger.debug("The content of PagenationList is empty, cannot setSelectedSumInfo, it may be happended by calling setSelectedSumInfo before query.");
			
			return;
		}
		
		if (keys != null && keys.length > 0) {
			final Double[] sumValues = new Double[keys.length];
			
			for (int i = 0; i < sumValues.length; i++) { // 初始化
				sumValues[i] = 0d;
			}
			
			for (T instance : this) {
				for (int i = 0; i < keys.length; i++) {
					sumValues[i] += ConvertUtils.convert(FieldUtils.readField(instance, keys[i]), Double.class, 0d);
				}
			}
			
			for (int i = 0; i < keys.length; i++) {
				sumSelectedInfo.put("SUM" + keys[i].toUpperCase(), sumValues[i]);
			}
		}
	}
}
