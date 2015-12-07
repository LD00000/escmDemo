package com.sunwayworld.escm.core.dao;

import java.io.Serializable;
import java.util.List;

import com.sunwayworld.escm.core.dao.model.BaseModel;
import com.sunwayworld.escm.core.dao.sql.BoundSql;
import com.sunwayworld.escm.core.dao.sql.Order;

public interface GenericBaseDao<T extends BaseModel, PK extends Serializable> {
	public void insert(T model);
	public void update(T model);
	
	public void deleteById(PK pk);
	public void deleteByCondition(T model);
	
	public T selectById(PK id);
	public Object selectOneColumnById(PK id, String columnName);
	public T selectOneByCondition(T condition);
	public List<T> selectByCondition(T condition, Order ... orders);
	public PaginationList<T> selectByCondition(T condition, Order[] orders, Pagination pagination, String ... sumColumnNames);
	public PaginationList<T> selectByBoundSql(BoundSql boundSql, Pagination pagination, String ... sumColumnNames);
}
