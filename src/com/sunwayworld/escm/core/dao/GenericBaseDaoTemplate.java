package com.sunwayworld.escm.core.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sunwayworld.escm.core.dao.model.BaseModel;
import com.sunwayworld.escm.core.dao.sql.BoundSql;
import com.sunwayworld.escm.core.dao.sql.BoundSqlBuilder;
import com.sunwayworld.escm.core.dao.sql.Order;
import com.sunwayworld.escm.core.dao.sql.dialect.DialectFactory;
import com.sunwayworld.escm.core.utils.MethodUtils;
import com.sunwayworld.escm.core.utils.NumberUtils;
import com.sunwayworld.escm.core.utils.SqlUtils;

@Repository
public abstract class GenericBaseDaoTemplate<T extends BaseModel, PK extends Serializable> implements GenericBaseDao<T, PK> {
	@Autowired
	protected JdbcTemplate template;
	
	private BoundSqlBuilder sqlBuilder = new BoundSqlBuilder();
	
	private Class<T> clazz;
	
	/**
	 * 默认构造函数，设置泛型类{@link #clazz}
	 */
	@SuppressWarnings("unchecked")
	public GenericBaseDaoTemplate() {
		final Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			clazz = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
		}
	}
	
	@Override
	public void insert(T model) {
		final BoundSql boundSql = sqlBuilder.buildInsertBoundSql(model);
		
		template.update(boundSql.getSql(), boundSql.getInValuesAsArray());
	}

	@Override
	public void update(T model) {
		final BoundSql boundSql = sqlBuilder.buildUpdateBoundSql(model, false);
		
		template.update(boundSql.getSql(), boundSql.getInValuesAsArray());
	}

	@Override
	public void deleteById(PK id) {
		final BoundSql boundSql = sqlBuilder.buildDeleteByIdBoundSql(clazz, id);
		
		template.update(boundSql.getSql(), id);
	}

	@Override
	public void deleteByCondition(T model) {
		final BoundSql boundSql = sqlBuilder.buildDeleteBoundSql(model);
		
		template.update(boundSql.getSql(), boundSql.getInValuesAsArray());
	}

	@Override
	public T selectById(PK id) {
		final BoundSql boundSql = sqlBuilder.buildSelectByIdBoundSql(clazz, id);
		
		/**
		 * {@link JdbcTemplate#queryForObject}如果没有找到就会抛出异常，所以用{@link JdbcTemplate#query}
		 */
		final List<T> result = template.query(boundSql.getSql(), boundSql.getInValuesAsArray(), new BeanPropertyRowMapper<T>(clazz));
		
		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	@Override
	public Object selectOneColumnById(PK id, String columnName) {
		final BoundSql boundSql = sqlBuilder.buildSelectByIdBoundSql(clazz, id, columnName);
		
		/**
		 * {@link JdbcTemplate#queryForObject}如果没有找到就会抛出异常，所以用{@link JdbcTemplate#query}
		 */
		final List<T> result = template.query(boundSql.getSql(), boundSql.getInValuesAsArray(), new BeanPropertyRowMapper<T>(clazz));
		
		if (result.isEmpty()) {
			return null;
		} else {
			return MethodUtils.readProperty(result.get(0), columnName);
		}
	}

	@Override
	public T selectOneByCondition(T condition) {
		final BoundSql boundSql = sqlBuilder.buildSelectBoundSql(condition, null);
		
		return template.queryForObject(boundSql.getSql(), boundSql.getInValuesAsArray(), new BeanPropertyRowMapper<T>(clazz));
	}

	@Override
	public List<T> selectByCondition(T condition, Order ... orders) {
		final BoundSql boundSql = sqlBuilder.buildSelectBoundSql(condition, orders);
		
		return template.query(boundSql.getSql(), boundSql.getInValuesAsArray(), new BeanPropertyRowMapper<T>(clazz));
	}

	@Override
	public PaginationList<T> selectByCondition(T condition, Order[] orders, Pagination pagination, final String ... sumColumnNames) {
		final BoundSql boundSql = sqlBuilder.buildSelectBoundSql(condition, orders);
		
		return selectByBoundSql(boundSql, pagination, sumColumnNames);
	}
	
	@Override
	public PaginationList<T> selectByBoundSql(BoundSql boundSql, Pagination pagination, String ... sumColumnNames) {
		final PaginationList<T> paginationList = new PaginationList<T>(pagination);
		
		// 查询总行数和总合计
		Map<String, Object> map = null;
		
		if (sumColumnNames != null && sumColumnNames.length > 0) {
			map = template.queryForMap(SqlUtils.getTotalSql(boundSql.getSql(), sumColumnNames), boundSql.getInValuesAsArray());
			
			paginationList.setSumInfo(map);
		}
		
		if (pagination != null) {
			if (sumColumnNames == null || sumColumnNames.length == 0) {
				map = template.queryForMap(SqlUtils.getTotalSql(boundSql.getSql()), boundSql.getInValuesAsArray());
			}
			
			pagination.setTotalRecord(NumberUtils.toInt(map.get(SqlUtils.COUNT_COLUMN)));
			
			boundSql.updateSql(DialectFactory.getDialect().toLimitSql(boundSql.getSql(), pagination.getPageStartRow(), pagination.getPageSize()));
		}
		
		final List<T> lists = template.query(boundSql.getSql(), boundSql.getInValuesAsArray(), new BeanPropertyRowMapper<T>(clazz));
		paginationList.addAll(lists);
		
		return paginationList;
	}
}
