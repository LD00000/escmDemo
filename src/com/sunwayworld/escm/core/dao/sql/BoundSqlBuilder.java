package com.sunwayworld.escm.core.dao.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.sunwayworld.escm.core.dao.model.BaseModel;
import com.sunwayworld.escm.core.utils.ArrayUtils;
import com.sunwayworld.escm.core.utils.ClassUtils;
import com.sunwayworld.escm.core.utils.ConvertUtils;
import com.sunwayworld.escm.core.utils.DateTimeUtils;
import com.sunwayworld.escm.core.utils.FieldUtils;
import com.sunwayworld.escm.core.utils.MethodUtils;
import com.sunwayworld.escm.core.utils.ModelUtils;
import com.sunwayworld.escm.core.utils.NumberUtils;
import com.sunwayworld.escm.core.utils.ObjectUtils;
import com.sunwayworld.escm.core.utils.StringUtils;

/**
 * 数据库单表的增删改查{@link BoundSql}的创建类
 */
public class BoundSqlBuilder {
	
	/**
	 * 通过Model类名生成批量添加的SQL语句，添加字段的顺序为成员变量名称的顺序
	 * 
	 * @param modelClass Model类名
	 * @return 批量添加的SQL语句
	 */
	public final <T> String buildBatchInsertSql(final Class<T> modelClass) {
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClass);
		
		// Sql语句
		final StringBuilder sql = new StringBuilder("INSERT INTO ").append(ModelUtils.getTableName(clazz)).append("\n  (");
		
		final Field[] sortedDeclaredFields = FieldUtils.getSortedDeclaredFields(clazz);
		
		// SQL添加语句要添加的字段数量
		int count = 0;
		
		for (Field property : sortedDeclaredFields) {
			// 注释 Transient的成员变量不会对应数据库表中的字段
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (count++ > 0) {
				sql.append(", ");
			}
			
			sql.append(propertyName.toUpperCase());
		}
		
		if (count == 0) {
			return null;
		}
		
		sql.append(")\nVALUES\n  ( ?").append(StringUtils.repeat(", ?", count - 1)).append(")");
		
		return sql.toString();
	}
	
	/**
	 * 通过Model来创建一个SQL的添加语句，只有非空的成员变量才会添加
	 * 
	 * @param model 用于添加的Model，对应数据库中表的实体
	 * @return 执行添加操作的{@link BoundSql}或{@code null}
	 */
	public final <T> BoundSql buildInsertBoundSql(final T model) {
		@SuppressWarnings("unchecked")
		final Class<T> clazz = ClassUtils.getOriginalClass((Class<T>) model.getClass());
		
		// Sql语句
		final StringBuilder sql = new StringBuilder("INSERT INTO ").append(ModelUtils.getTableName(clazz)).append("\n  (");
		
		// 所有IN的值储存在这里,是有顺序的
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL添加语句要添加的字段数量
		int count = 0;
		
		for (Field property : clazz.getDeclaredFields()) {
			// 注释 Transient的成员变量不会对应数据库表中的字段
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			Object inValue = MethodUtils.readProperty(model, propertyName);
			
			// 只添加非空的字段
			if (ObjectUtils.isEmpty(inValue)) {
				continue;
			}
			
			if (count++ > 0) {
				sql.append(", ");
			}
			
			sql.append(propertyName.toUpperCase());
			
			inValues.add(inValue);
		}
		
		if (count == 0) {
			return null;
		}
		
		sql.append(")\nVALUES\n  ( ?").append(StringUtils.repeat(", ?", count - 1)).append(")");
		
		return new BoundSql(sql.toString(), inValues.toArray());
	}
	
	/**
	 * 通过要更新的Model和查询条件的Model来创建一个SQL的更新语句<br>
	 * <p>1. Model是代理：通过{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * 来生成的代理，那么生成代理类后被修改的字段（与原来的值不一样）为更新字段或查询条件 </p>
	 * <p>2. Model是非代理类：非空字段为更新字段或查询条件</p>
	 * 
	 * <p>
	 * 更新Model里注释{@code @Id}，{@code @Version}，{@code @Column(updatable=false)}的字段是不会被更新的<br>
	 * 查询Model里注释{@code @Version}和{@code @Transient}的字段是不会用于判断条件
	 * 注释{@code @Version}的版本仅通过设置{@code bindVersion}为{@code true}来更新
	 * </p>
	 * 
	 * @param updateModel 更新的Model，对应数据库中表的实体
	 * @param searchModel 查询条件的Model，对应数据库中表的实体
	 * @param bindVersion 是否绑定版本，版本值在searchModel里获取
	 * @return 执行更新操作的{@link BoundSql}或{@code null}
	 */
	public final <T extends BaseModel> BoundSql buildUpdateBoundSql(final T updateModel, final T searchModel, final boolean bindVersion) {
		final Set<String> modifiedColumnNames = ((BaseModel)updateModel).getM$aop();
		@SuppressWarnings("unchecked")
		final Class<T> modifiedModelClazz = (Class<T>) updateModel.getClass();
		
		if (ClassUtils.isProxy(modifiedModelClazz)
				&& modifiedColumnNames.isEmpty()) {
			return null;
		}
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modifiedModelClazz);
		
		
		// 要更新的SQL语句
		final StringBuilder sql = new StringBuilder("UPDATE ").append(ModelUtils.getTableName(clazz)).append("\n   SET ");
		
		// 所有IN的值储存在这里,是有顺序的
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL添加语句要添加的字段数量
		int count = 0;
		
		// 添加要更新的字段
		for (Field property : clazz.getDeclaredFields()) {
			// 注释 Transient的成员变量不会对应数据库表中的字段
			if (property.isAnnotationPresent(Transient.class)
					|| property.isAnnotationPresent(Version.class)
					|| property.isAnnotationPresent(Id.class)
					|| (property.isAnnotationPresent(Column.class)
							&& !property.getAnnotation(Column.class).updatable())) {
				continue;
			}
						
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modifiedModelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // 该字段没有被修改，不应该被更新
				continue;
			}
			
			final Object value = MethodUtils.readProperty(updateModel, propertyName);
			
			if (!ClassUtils.isProxy(modifiedModelClazz)
					&& ObjectUtils.isEmpty(value)) { // 如果提供的Model类为非代理类，只更新非空字段
				continue;
			}
			
			if (count++ > 0) {
				sql.append(",\n       ");
			}
			
			sql.append(propertyName.toUpperCase()).append(" = ?");
			inValues.add(value);
		}
		
		if (count == 0) {
			return null;
		}
		
		sql.append("\n WHERE ");
		
		final Set<String> conditionColumnNames = ((BaseModel)searchModel).getM$aop();
		@SuppressWarnings("unchecked")
		final Class<T> conditionModelClazz = (Class<T>) searchModel.getClass();
		
		if (ClassUtils.isProxy(conditionModelClazz)
				&& conditionColumnNames.isEmpty()) {
			return null;
		}
		
		// SQL添加语句要添加的判断条件的数量
		count = 0;
		
		Field versionProperty = null;
		
		// 添加查询条件
		for (Field property : clazz.getDeclaredFields()) {
			// 注释 Transient的成员变量不会对应数据库表中的字段
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			if (property.isAnnotationPresent(Version.class)) {
				versionProperty = property;
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(conditionModelClazz)
					&& !conditionColumnNames.contains(propertyName.toUpperCase())) { // 该字段没有被修改
				continue;
			}
			
			final Object value = MethodUtils.readProperty(searchModel, propertyName);
			
			if (!ClassUtils.isProxy(conditionModelClazz)
					&& ObjectUtils.isEmpty(value)) { // 提供的Model类为非代理类，判断条件选非空字段
				continue;
			}
			
			if (count++ > 0) {
				sql.append("\n   AND ");
			}
			
			sql.append(propertyName.toUpperCase()).append(" = ?");
			inValues.add(value);
		}
		
		if (count == 0) {
			return null;
		}
		
		final BoundSql boundSql = new BoundSql(sql.toString(), inValues.toArray());
		
		if (bindVersion && versionProperty != null) {
			final String versionPropertyName = versionProperty.getName();
			final Object versionValue = MethodUtils.readProperty(searchModel, versionPropertyName);
			
			boundSql.setVersion(versionPropertyName.toUpperCase(), NumberUtils.toLong(versionValue));
		}
		
		return boundSql;
	}
	
	/**
	 * 通过Model来创建一个SQL的更新语句，Model里对应主键的值（注释{@link Id}的成员变量）不能为空<br>
	 * <p>1. Model是代理：通过{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * 来生成的代理，那么只更新生成代理类后被修改的字段（与原来的值不一样） </p>
	 * <p>2. Model是非代理类：只更新非空字段</p>
	 * <p>如果{@code bindVersion}为{@code true}，那么更新操作时是尝试绑定版本，条件语句上加入判断且更新版本的值<br>
	 * 只有当Model里对应版本的成员变量（注释{@link Version}的成员变量）存在且不为空时才会生效</p>
	 * 
	 * <p>Model里注释{@code @Id}，{@code @Version}，{@code @Column(updatable=false)}的字段是不会被更新的<br>
	 * 注释{@code @Version}的版本仅通过设置{@code bindVersion}为{@code true}来更新
	 * </p>
	 * 
	 * @param model 用于更新的Model，对应数据库中表的实体
	 * @param bindVersion 是否绑定版本
	 * @return 执行更新操作的{@link BoundSql}或{@code null}
	 */
	public final <T extends BaseModel> BoundSql buildUpdateBoundSql(final T model, final boolean bindVersion) {
		final Set<String> modifiedColumnNames = ((BaseModel)model).getM$aop();
		@SuppressWarnings("unchecked")
		final Class<T> modelClazz = (Class<T>) model.getClass();
		
		if (ClassUtils.isProxy(modelClazz)
				&&	(!bindVersion && modifiedColumnNames.isEmpty())) {
			return null;
		}
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClazz);
		
		
		// 要更新的SQL语句
		final StringBuilder sql = new StringBuilder("UPDATE ").append(ModelUtils.getTableName(clazz)).append("\n   SET ");
		
		// 所有IN的值储存在这里,是有顺序的
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL添加语句要添加的字段数量
		int count = 0;
		
		Field pkProperty = null;
		Field versionProperty = null;
				
		for (Field property : clazz.getDeclaredFields()) {
			// 注释 Transient的成员变量不会对应数据库表中的字段
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			if (property.isAnnotationPresent(Id.class)) {
				pkProperty = property;
				
				continue;
			}
			
			if (property.isAnnotationPresent(Version.class)) {
				versionProperty = property;
				
				continue;
			}
			
			// 该字段不允许更新
			if (property.isAnnotationPresent(Column.class)
					&& !property.getAnnotation(Column.class).updatable()) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // 该字段没有被修改，不应该被更新
				continue;
			}
			
			final Object value = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(value)) { // 如果提供的Model类为非代理类，只更新非空字段
				continue;
			}
			
			if (count++ > 0) {
				sql.append(",\n       ");
			}
			
			sql.append(propertyName.toUpperCase()).append(" = ?");
			inValues.add(value);
		}
		
		if (inValues.isEmpty() && (!bindVersion || versionProperty == null)) {
			return null;
		}
		
		final String pkPropertyName = pkProperty.getName();
		
		sql.append("\n WHERE ").append(pkPropertyName.toUpperCase()).append(" = ?");
		inValues.add(MethodUtils.readProperty(model, pkPropertyName));
		
		final BoundSql boundSql = new BoundSql(sql.toString(), inValues.toArray());
		
		if (bindVersion && versionProperty != null) {
			final String versionPropertyName = versionProperty.getName();
			final Object versionValue = MethodUtils.readProperty(model, versionPropertyName);
			
			boundSql.setVersion(versionPropertyName.toUpperCase(), NumberUtils.toLong(versionValue));
		}
		
		return boundSql;
	}
	
	/**
	 * 通过Model类名生成通过查询变量名称或主键批量更新的SQL语句，更新字段的顺序为指定的成员变量名称的顺序<br>
	 * 使用该SQL进行更新操作所需的{@code inValues}里必须按需添加成员变量的值且最后添加条件变量的值或主键的值
	 * 
	 * @param modelClass Model类名
	 * @param bindVersion 是否绑定版本
	 * @param conditionColumnNames 要批量更新的查询条件变量名称，如果为空则用主键为条件
	 * @param updateColumnNames 要批量更新的成员变量名称
	 * @return 批量更新的SQL语句
	 */
	public final <T> String buildBatchUpdateSql(final Class<T> modelClass, final boolean bindVersion, final List<String> conditionColumnNames, final String ... updateColumnNames) {
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClass);
		
		if (ObjectUtils.isEmpty(updateColumnNames)) {
			return null;
		}
		
		// Sql语句
		final StringBuilder sql = new StringBuilder("UPDATE ").append(ModelUtils.getTableName(clazz)).append("\n   SET ");
		
		// SQL添加语句要添加的字段数量
		int count = 0;
		
		String versionName = null; // 版本名称
		
		if (bindVersion) {
			final Field versionProperty = ModelUtils.getVersionProperty(modelClass);
			
			if (versionProperty != null) {
				versionName = versionProperty.getName().toUpperCase();
				
				sql.append(versionName).append(" = ").append(versionName).append(" + 1");
				
				count++;
			}
		}
		
		for (String updateColumn : updateColumnNames) {
			if (updateColumn.equalsIgnoreCase(versionName)) {
				continue;
			}
			
			if (count++ > 0) {
				sql.append(",\n       ");
			}
			
			sql.append(updateColumn.toUpperCase()).append(" = ?");
		}
		
		sql.append("\n");
		
		if (conditionColumnNames == null
				|| conditionColumnNames.isEmpty()) {
			sql.append(" WHERE ").append(ModelUtils.getPkProperty(clazz).getName().toUpperCase()).append(" = ?");
		} else {
			count = 0;
			
			sql.append(" WHERE ");
			
			if (count++ > 0) {
				sql.append("\n")
				   .append("   AND ");
			}
			
			for (String columnName : conditionColumnNames) {
				sql.append(columnName.toUpperCase()).append(" = ?");
			}
		}
		
		if (bindVersion
				&& versionName != null) {
			sql.append("\n")
			   .append("   AND ").append(versionName).append(" = ?");
		}
		
		return sql.toString();
	}
	
	/**
	 * 通过Model类和主键的值来生成查询操作的SQL语句
	 * 
	 * @param modelClazz Model类，对应数据库中表的实体类
	 * @param idValue 主键的值
	 * @param columns 要查询的列名，如果为空，就查全部
	 * @return 执行查询操作的{@link BoundSql}
	 */
	public final <T> BoundSql buildSelectByIdBoundSql(final Class<T> modelClazz, final Object idValue, final String ... columns) {
		final Field pkProperty = ModelUtils.getPkProperty(modelClazz);
		
		final String tableName = ModelUtils.getTableName(modelClazz);
		
		// Sql语句
		final StringBuilder sql;
		
		if (columns == null || columns.length == 0) {
			sql = new StringBuilder("SELECT T.*");
		} else {
			sql = new StringBuilder("SELECT ").append(ArrayUtils.toString(columns, ", "));
		}
		
		sql.append("\n")
		   .append("  FROM ").append(tableName).append(" T\n")
		   .append(" WHERE ").append(pkProperty.getName().toUpperCase()).append(" = ?");
		
		return new BoundSql(sql.toString(), idValue);
	}
	
	/**
	 * 通过Model类和一组主键的值来生成查询操作的SQL语句
	 * 
	 * @param modelClazz Model类，对应数据库中表的实体类
	 * @param definition 子查询的定义
	 * @param idValues 一组主键的值
	 * @param columns 要查询的列名，如果为空，就查全部
	 * @return 执行查询操作的{@link BoundSql}
	 */
	public final <T> BoundSql buildSelectByIdsBoundSql(final Class<T> modelClazz, final List<?> idValues, final String ... columns) {
		final Field pkProperty = ModelUtils.getPkProperty(modelClazz);
		
		final String tableName = ModelUtils.getTableName(modelClazz);
		
		// Sql语句
		final StringBuilder sql;
		
		if (columns == null || columns.length == 0) {
			sql = new StringBuilder("SELECT T.*");
		} else {
			sql = new StringBuilder("SELECT ").append(ArrayUtils.toString(columns, ", "));
		}
		
		sql.append("\n")
		   .append("  FROM ").append(tableName).append(" T\n")
		   .append(" WHERE ").append(pkProperty.getName().toUpperCase()).append(" IN ( ?").append(StringUtils.repeat(", ?", idValues.size() - 1)).append(")");
		
		return new BoundSql(sql.toString(), idValues.toArray());
	}
	/**
	 * 通过Model类和主键的值来生成删除操作的SQL语句
	 * 
	 * @param modelClazz Model类，对应数据库中表的实体类
	 * @param idValue 主键的值
	 * @return 执行删除操作的{@link BoundSql}
	 */
	public final <T> BoundSql buildDeleteByIdBoundSql(final Class<T> modelClazz, final Object idValue) {
		return new BoundSql(new StringBuilder("DELETE FROM ").append(ModelUtils.getTableName(modelClazz))
				                      .append(" WHERE ").append(ModelUtils.getPkProperty(modelClazz).getName().toUpperCase())
				                      .append(" = ?").toString(),
				                      idValue);
	}
	
	/**
	 * 通过Model类和一列主键的值来生成批量删除操作的SQL语句
	 * 
	 * @param modelClazz Model类，对应数据库中表的实体类
	 * @param idValues 一列主键的值
	 * @return 执行批量删除操作的{@link BoundSql}
	 */
	public final <T> BoundSql buildBatchDeleteByIdBoundSql(final Class<T> modelClazz, final List<?> idValues) {
		if (idValues.size() == 1) {
			return buildDeleteByIdBoundSql(modelClazz, idValues.get(0));
		}
		
		return new BoundSql(new StringBuilder("DELETE FROM ").append(ModelUtils.getTableName(modelClazz)).append("\n")
				                      .append(" WHERE ").append(ModelUtils.getPkProperty(modelClazz).getName().toUpperCase())
				                      .append(" IN ( ?").append(StringUtils.repeat(", ?", idValues.size() - 1)).append(")").toString(),
				                      idValues.toArray());
	}
	
	/**
	 * 通过Model类和主键的值来生成删除操作的SQL语句，且绑定版本
	 * 
	 * @param modelClazz Model类，对应数据库中表的实体类
	 * @param idValue 主键的值
	 * @param versionValue 版本的值
	 * @return 执行删除操作的{@link BoundSql}
	 * 
	 * @see #buildDeleteByIdBoundSql(Class, Object)
	 */
	public final <T> BoundSql buildDeleteByIdBoundSql(final Class<?> modelClazz, final Object idValue, final Long versionValue) {
		final BoundSql boundSql = buildDeleteByIdBoundSql(modelClazz, idValue);
		
		boundSql.setVersion(ModelUtils.getVersionProperty(modelClazz).getName().toUpperCase(), versionValue);
		
		return boundSql;
	}
	
	/**
	 * 生成查询所有记录的SQL语句
	 * 
	 * @param clazz Model类
	 * @param orders 排序
	 * @param columns 要查询的列名，如果为空，就查全部
	 * @return 执行查询操作的{@link BoundSql}或{@code null}
	 */
	public final <T> BoundSql buildSelectAllBoundSql(final Class<T> clazz, final Order[] orders, final String ... columns) {
		// Sql语句
		final StringBuilder sql;
		
		if (columns == null || columns.length == 0) {
			sql = new StringBuilder("SELECT * FROM ").append(ModelUtils.getTableName(clazz));
		} else {
			sql = new StringBuilder("SELECT ").append(ArrayUtils.toString(columns, ", ")).append("\n")
					        .append("  FROM ").append(ModelUtils.getTableName(clazz));
		}
		
		if (!ObjectUtils.isEmpty(orders)) {
			sql.append("\n")
			   .append(" ORDER BY ");
			
			for (int i = 0; i < orders.length; i ++) {
				if (i > 0) {
					sql.append(", ");
				}
				
				sql.append(orders[i].getOrder());
			}
		}
		
		return new BoundSql(sql.toString());
	}
	
	/**
	 * 通过判断条件的Model来生成查询操作的SQL语句
	 * <p>1. Model是代理：通过{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * 来生成的代理，那么生成代理类后被修改的字段（与原来的值不一样）为判断条件 </p>
	 * <p>2. Model是非代理类：非空字段为判断条件</p>
	 * 
	 * @param model 用于判断条件的Model，对应数据库中表的实体
	 * @param orders 排序
	 * @param columns 要查询的列名，如果为空，就查全部
	 * @return 执行查询操作的{@link BoundSql}或{@code null}
	 */
	public final <T> BoundSql buildSelectBoundSql(final T model, final Order[] orders, final String ... columns) {
		final Set<String> modifiedColumnNames = ((BaseModel)model).getM$aop();
		
		@SuppressWarnings("unchecked")
		final Class<T> modelClazz = (Class<T>) model.getClass();
		
		if (ClassUtils.isProxy(modelClazz)
				&&	modifiedColumnNames.isEmpty()) {
			return null;
		}
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClazz);
		
		// Sql语句
		final StringBuilder sql;
		
		if (columns == null || columns.length == 0) {
			sql = new StringBuilder("SELECT T.*");
		} else {
			sql = new StringBuilder("SELECT ").append(ArrayUtils.toString(columns, ", "));
		}
		
		sql.append("\n")
		   .append("  FROM ").append(ModelUtils.getTableName(clazz)).append(" T");
		
		// 所有IN的值储存在这里,是有顺序的
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL查询语句，所比较的字段数量
		int count = 0;
				
		for (Field property : clazz.getDeclaredFields()) {
			// 注释 Transient的成员变量不会对应数据库表中的字段
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // 该字段没有被修改
				continue;
			}
						
			Object inValue = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(inValue)) { // 如果提供的Model类为非代理类，只判断非空字段
				continue;
			}
			
			if (count++ > 0) {
				sql.append("\n   AND ");
			} else {
				sql.append("\n WHERE ");
			}
			
			sql.append(propertyName.toUpperCase()).append(" = ?");
			
			inValues.add(inValue);
		}
		
		if (!ObjectUtils.isEmpty(orders)) {
			sql.append("\n")
			.append(" ORDER BY ");
			
			for (int i = 0; i < orders.length; i ++) {
				if (i > 0) {
					sql.append(", ");
				}
				
				sql.append(orders[i].getOrder());
			}
		}
		
		if (inValues.isEmpty()) {
			return new BoundSql(sql.toString());
		} else {
			return new BoundSql(sql.toString(), inValues.toArray());
		}
	}
	
	/**
	 * 通过判断条件Model来生成删除操作的SQL语句<br>
	 * <p>1. Model是代理：通过{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * 来生成的代理，那么生成代理类后被修改的字段为（与原来的值不一样）判断条件 </p>
	 * <p>2. Model是非代理类：非空字段为判断条件</p>
	 * 
	 * <p>如果{@code bindVersion}为{@code true}，那么删除操作时是尝试绑定版本，条件语句上加入判断<br>
	 * 只有当Model里对应版本的成员变量（注释{@link Version}的成员变量）存在且不为空时才会生效</p>
	 * 
	 * @param model 要删除的判断条件Model，对应数据库中表的实体
	 * @param bindVersion 是否绑定版本
	 * @return 执行删除操作的{@link BoundSql}或{@code null}
	 */
	public final <T> BoundSql buildDeleteBoundSql(final T model, final boolean bindVersion) {
		final Set<String> modifiedColumnNames = ((BaseModel)model).getM$aop();
		@SuppressWarnings("unchecked")
		final Class<T> modelClazz = (Class<T>) model.getClass();
		
		if (ClassUtils.isProxy(modelClazz)
				&&	modifiedColumnNames.isEmpty()) {
			return null;
		}
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClazz);
		
		// Sql语句
		final StringBuilder sql = new StringBuilder("DELETE FROM ").append(ModelUtils.getTableName(modelClazz)).append("\n WHERE ");
		
		// 所有IN的值储存在这里,是有顺序的
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL查询语句，所比较的字段数量
		int count = 0;
		
		Field versionProperty = null;
				
		for (Field property : clazz.getDeclaredFields()) {
			// 注释 Transient的成员变量不会对应数据库表中的字段
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			if (property.isAnnotationPresent(Version.class)) {
				versionProperty = property;
				
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // 该字段没有被修改，不应该被用做判断条件
				continue;
			}
			
			final Object inValue = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(inValue)) { // 如果提供的Model类为非代理类，非空字段为判断条件
				continue;
			}
			
			if (count++ > 0) {
				sql.append("\n   AND ");
			}
			
			sql.append(propertyName.toUpperCase()).append(" = ?");
			
			inValues.add(inValue);
		}
		
		if (count == 0) {
			return null;
		}
		
		final BoundSql boundSql = new BoundSql(sql.toString(), inValues.toArray());
		
		if (bindVersion && versionProperty != null) {
			final String versionColumn =  versionProperty.getName();
			boundSql.setVersion(versionColumn.toUpperCase(), NumberUtils.toLong(MethodUtils.readProperty(model, versionColumn)));
		}
		
		return boundSql;
	}
	
	/**
	 * 通过判断条件Model来生成删除操作的SQL语句，没有版本控制
	 * 
	 * @see #buildDeleteBoundSql(Object, boolean)
	 */
	public final <T> BoundSql buildDeleteBoundSql(final T model) {
		return buildDeleteBoundSql(model, false);
	}
	
	/**
	 * 通过判断条件Model来生成查询满足条件数量的SQL语句<br>
	 * <p>1. Model是代理：通过{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * 来生成的代理，那么生成代理类后被修改的字段为（与原来的值不一样）判断条件 </p>
	 * <p>2. Model是非代理类：非空字段为判断条件</p>
	 * 
	 * @param model 用于判断条件的Model，对应数据库中表的实体
	 * @return 执行查询操作的{@link BoundSql}
	 */
	public final <T> BoundSql buildCountBoundSql(final T model) {
		final Set<String> modifiedColumnNames = ((BaseModel)model).getM$aop();
		@SuppressWarnings("unchecked")
		final Class<T> modelClazz = (Class<T>) model.getClass();
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClazz);	
		
		if (ClassUtils.isProxy(modelClazz)
				&&	modifiedColumnNames.isEmpty()) { // 代理类没有被修改的字段，查询全部数量
			return new BoundSql("SELECT COUNT(1) FROM " + ModelUtils.getTableName(clazz));
		}
		
		// Sql语句
		final StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM ").append(ModelUtils.getTableName(clazz)).append("\n WHERE ");
		
		// 所有IN的值储存在这里,是有顺序的
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL查询语句，所比较的字段数量
		int count = 0;
		
		for (Field property : clazz.getDeclaredFields()) {
			if (property.isAnnotationPresent(Version.class)) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // 该字段没有被修改，不应该被用做判断条件
				continue;
			}
			
			final Object inValue = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(inValue)) { // 如果提供的Model类为非代理类，非空字段为判断条件
				continue;
			}
			
			if (count++ > 0) {
				sql.append("\n   AND ");
			}
			
			sql.append(propertyName.toUpperCase()).append(" = ?");
			
			inValues.add(inValue);
		}
		
		if (count == 0) {
			return new BoundSql("SELECT COUNT(1) FROM " + ModelUtils.getTableName(clazz));
		}
		
		return new BoundSql(sql.toString(), inValues.toArray());
	}
	
	/**
	 * 获取通过表名、主键名和主键的值来查询所有列或指定列的值的SQL
	 * 
	 * @param tableName 表名
	 * @param pkName 主键的名称
	 * @param pkValue 主键的值
	 * @param columnNames 要查询的列名，如果为空则查询全部列
	 * @return 查询操作的{@link BoundSql}
	 */
	public final BoundSql buildSelectBoundSql(final String tableName, final String pkName,
			final String pkValue, final String ... columnNames) {
		StringBuilder sql = new StringBuilder("SELECT");
		
		if (columnNames == null
				|| columnNames.length == 0) {
			sql.append("*\n");
		} else {
			for (int i = 0; i < columnNames.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				
				sql.append(" ").append(columnNames[i]);
			}
		}
		
		sql.append("  FROM ").append(tableName.toUpperCase()).append("\n")
		   .append(" WHERE ").append(pkName.toUpperCase()).append(" = ?");
		
		return new BoundSql(sql.toString(), pkValue);
	}
	
	/**
	 * 通过判断条件Model来生成判断条件的SQL片段语句，以AND开头<br>
	 * <p>1. Model是代理：通过{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * 来生成的代理，那么生成代理类后被修改的字段为（与原来的值不一样）判断条件 </p>
	 * <p>2. Model是非代理类：非空字段为判断条件</p>
	 * 
	 * @param model 用于判断条件的Model，对应数据库中表的实体
	 * @return 包装成{@link BoundSql}的以AND开头的判断条件的SQL片段
	 */
	public final <T> BoundSql buildConditionBoundSql(final T model) {
		@SuppressWarnings("unchecked")
		final Class<T> modelClazz = (Class<T>) model.getClass();
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClazz);	
		
		final boolean isProxy = ClassUtils.isProxy(modelClazz); // 是否是代理类
		
		final Set<String> modifiedColumnNames = ((BaseModel)model).getM$aop();
		
		if (isProxy && modifiedColumnNames.isEmpty()) { // 代理类，没有任何字段变更过
			return null;
		}
		
		// Sql语句
		final StringBuilder sql = new StringBuilder("");
		
		// 所有IN的值储存在这里,是有顺序的
		final List<Object> inValues = new ArrayList<Object>();
		
		for (Field property : clazz.getDeclaredFields()) {
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			boolean isCondition = false; // 是否是用于查询条件
			
			Object propertyValue = null;
			
			if (isProxy) { // 代理类
				if (modifiedColumnNames.contains(property.getName().toUpperCase())) { // 该字段变更过
					isCondition = true;
					
					propertyValue = MethodUtils.readProperty(model, property.getName());
				}
			} else {
				propertyValue = MethodUtils.readProperty(model, property.getName());
				
				if (propertyValue != null) {
					isCondition = true;
				}
			}
			
			if (isCondition) { // 是用于查询条件
				final Condition condition = property.getAnnotation(Condition.class);
				
				String column = property.getName().toUpperCase();
				
				if (condition == null) {
					sql.append("   AND ").append(column).append(" = ?\n");
					inValues.add(propertyValue);
				} else {
					String alias = "";
					if (!StringUtils.isBlank(condition.alias())) {
						alias = condition.alias() + ".";
					}
					
					// 如果当前成员变量的值为空时，判断是否存在默认值，并赋默认值
					if (propertyValue == null) {
						String defaultValue = condition.defaultValue();
						if (!StringUtils.isBlank(defaultValue)) {
							propertyValue = ConvertUtils.convert(defaultValue, property.getType());
						}
					}
					
					// 查询条件里写明了对应的列名
					if (!StringUtils.isBlank(condition.column())) {
						column = condition.column();
					}
					
					if (propertyValue == null) {
						sql.append("   AND ").append(alias).append(column).append(" IS NULL\n");
					} else {
						final Match match = condition.match();
						
						if (Number.class.isAssignableFrom(property.getType()) // 数字类型
								|| Date.class.isAssignableFrom(property.getType())) { // 日期类型
							if (Match.EQUAL.equals(match)
									|| Match.CENTER.equals(match)) {
								sql.append("   AND ").append(alias).append(column).append(" = ?\n");
							} else if (Match.LEFT.equals(match)) {
								sql.append("   AND ").append(alias).append(column).append(" < ?\n");
							} else if (Match.RIGHT.equals(match)) {
								sql.append("   AND ").append(alias).append(column).append(" >= ?\n");
							}
							
							if (Date.class.isAssignableFrom(property.getType()) // 日期类型
									&& Match.LEFT.equals(match)) { // 小于 
								inValues.add(DateTimeUtils.addDay(ConvertUtils.convert(propertyValue, Date.class), 1)); // 日期要加一
							} else {
								inValues.add(propertyValue);
							}
						} else {
							if (Match.EQUAL.equals(match)) {
								sql.append("   AND ").append(alias).append(column).append(" = ?\n");
							} else {
								sql.append("   AND ").append(alias).append(column).append(" LIKE ?\n");
							}
							
							if (Match.EQUAL.equals(match)) {
								inValues.add(propertyValue);
							} else if (Match.CENTER.equals(match)) {
								inValues.add("%" + ConvertUtils.convert(propertyValue, String.class) + "%");
							} else if (Match.LEFT.equals(match)) {
								inValues.add(ConvertUtils.convert(propertyValue, String.class) + "%");
							} else if (Match.RIGHT.equals(match)) {
								inValues.add("%" + ConvertUtils.convert(propertyValue, String.class));
							}
						}
					}
				}
			}
		}
		
		if (sql.length() > 0) {
			return new BoundSql(sql.toString(), inValues.toArray());
		} else {
			return null;
		}
	}
}
