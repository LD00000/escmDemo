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
 * ���ݿⵥ�����ɾ�Ĳ�{@link BoundSql}�Ĵ�����
 */
public class BoundSqlBuilder {
	
	/**
	 * ͨ��Model��������������ӵ�SQL��䣬����ֶε�˳��Ϊ��Ա�������Ƶ�˳��
	 * 
	 * @param modelClass Model����
	 * @return ������ӵ�SQL���
	 */
	public final <T> String buildBatchInsertSql(final Class<T> modelClass) {
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClass);
		
		// Sql���
		final StringBuilder sql = new StringBuilder("INSERT INTO ").append(ModelUtils.getTableName(clazz)).append("\n  (");
		
		final Field[] sortedDeclaredFields = FieldUtils.getSortedDeclaredFields(clazz);
		
		// SQL������Ҫ��ӵ��ֶ�����
		int count = 0;
		
		for (Field property : sortedDeclaredFields) {
			// ע�� Transient�ĳ�Ա���������Ӧ���ݿ���е��ֶ�
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
	 * ͨ��Model������һ��SQL�������䣬ֻ�зǿյĳ�Ա�����Ż����
	 * 
	 * @param model ������ӵ�Model����Ӧ���ݿ��б��ʵ��
	 * @return ִ����Ӳ�����{@link BoundSql}��{@code null}
	 */
	public final <T> BoundSql buildInsertBoundSql(final T model) {
		@SuppressWarnings("unchecked")
		final Class<T> clazz = ClassUtils.getOriginalClass((Class<T>) model.getClass());
		
		// Sql���
		final StringBuilder sql = new StringBuilder("INSERT INTO ").append(ModelUtils.getTableName(clazz)).append("\n  (");
		
		// ����IN��ֵ����������,����˳���
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL������Ҫ��ӵ��ֶ�����
		int count = 0;
		
		for (Field property : clazz.getDeclaredFields()) {
			// ע�� Transient�ĳ�Ա���������Ӧ���ݿ���е��ֶ�
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			Object inValue = MethodUtils.readProperty(model, propertyName);
			
			// ֻ��ӷǿյ��ֶ�
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
	 * ͨ��Ҫ���µ�Model�Ͳ�ѯ������Model������һ��SQL�ĸ������<br>
	 * <p>1. Model�Ǵ���ͨ��{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * �����ɵĴ�����ô���ɴ�������޸ĵ��ֶΣ���ԭ����ֵ��һ����Ϊ�����ֶλ��ѯ���� </p>
	 * <p>2. Model�ǷǴ����ࣺ�ǿ��ֶ�Ϊ�����ֶλ��ѯ����</p>
	 * 
	 * <p>
	 * ����Model��ע��{@code @Id}��{@code @Version}��{@code @Column(updatable=false)}���ֶ��ǲ��ᱻ���µ�<br>
	 * ��ѯModel��ע��{@code @Version}��{@code @Transient}���ֶ��ǲ��������ж�����
	 * ע��{@code @Version}�İ汾��ͨ������{@code bindVersion}Ϊ{@code true}������
	 * </p>
	 * 
	 * @param updateModel ���µ�Model����Ӧ���ݿ��б��ʵ��
	 * @param searchModel ��ѯ������Model����Ӧ���ݿ��б��ʵ��
	 * @param bindVersion �Ƿ�󶨰汾���汾ֵ��searchModel���ȡ
	 * @return ִ�и��²�����{@link BoundSql}��{@code null}
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
		
		
		// Ҫ���µ�SQL���
		final StringBuilder sql = new StringBuilder("UPDATE ").append(ModelUtils.getTableName(clazz)).append("\n   SET ");
		
		// ����IN��ֵ����������,����˳���
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL������Ҫ��ӵ��ֶ�����
		int count = 0;
		
		// ���Ҫ���µ��ֶ�
		for (Field property : clazz.getDeclaredFields()) {
			// ע�� Transient�ĳ�Ա���������Ӧ���ݿ���е��ֶ�
			if (property.isAnnotationPresent(Transient.class)
					|| property.isAnnotationPresent(Version.class)
					|| property.isAnnotationPresent(Id.class)
					|| (property.isAnnotationPresent(Column.class)
							&& !property.getAnnotation(Column.class).updatable())) {
				continue;
			}
						
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modifiedModelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // ���ֶ�û�б��޸ģ���Ӧ�ñ�����
				continue;
			}
			
			final Object value = MethodUtils.readProperty(updateModel, propertyName);
			
			if (!ClassUtils.isProxy(modifiedModelClazz)
					&& ObjectUtils.isEmpty(value)) { // ����ṩ��Model��Ϊ�Ǵ����ֻ࣬���·ǿ��ֶ�
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
		
		// SQL������Ҫ��ӵ��ж�����������
		count = 0;
		
		Field versionProperty = null;
		
		// ��Ӳ�ѯ����
		for (Field property : clazz.getDeclaredFields()) {
			// ע�� Transient�ĳ�Ա���������Ӧ���ݿ���е��ֶ�
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			if (property.isAnnotationPresent(Version.class)) {
				versionProperty = property;
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(conditionModelClazz)
					&& !conditionColumnNames.contains(propertyName.toUpperCase())) { // ���ֶ�û�б��޸�
				continue;
			}
			
			final Object value = MethodUtils.readProperty(searchModel, propertyName);
			
			if (!ClassUtils.isProxy(conditionModelClazz)
					&& ObjectUtils.isEmpty(value)) { // �ṩ��Model��Ϊ�Ǵ����࣬�ж�����ѡ�ǿ��ֶ�
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
	 * ͨ��Model������һ��SQL�ĸ�����䣬Model���Ӧ������ֵ��ע��{@link Id}�ĳ�Ա����������Ϊ��<br>
	 * <p>1. Model�Ǵ���ͨ��{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * �����ɵĴ�����ôֻ�������ɴ�������޸ĵ��ֶΣ���ԭ����ֵ��һ���� </p>
	 * <p>2. Model�ǷǴ����ֻࣺ���·ǿ��ֶ�</p>
	 * <p>���{@code bindVersion}Ϊ{@code true}����ô���²���ʱ�ǳ��԰󶨰汾����������ϼ����ж��Ҹ��°汾��ֵ<br>
	 * ֻ�е�Model���Ӧ�汾�ĳ�Ա������ע��{@link Version}�ĳ�Ա�����������Ҳ�Ϊ��ʱ�Ż���Ч</p>
	 * 
	 * <p>Model��ע��{@code @Id}��{@code @Version}��{@code @Column(updatable=false)}���ֶ��ǲ��ᱻ���µ�<br>
	 * ע��{@code @Version}�İ汾��ͨ������{@code bindVersion}Ϊ{@code true}������
	 * </p>
	 * 
	 * @param model ���ڸ��µ�Model����Ӧ���ݿ��б��ʵ��
	 * @param bindVersion �Ƿ�󶨰汾
	 * @return ִ�и��²�����{@link BoundSql}��{@code null}
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
		
		
		// Ҫ���µ�SQL���
		final StringBuilder sql = new StringBuilder("UPDATE ").append(ModelUtils.getTableName(clazz)).append("\n   SET ");
		
		// ����IN��ֵ����������,����˳���
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL������Ҫ��ӵ��ֶ�����
		int count = 0;
		
		Field pkProperty = null;
		Field versionProperty = null;
				
		for (Field property : clazz.getDeclaredFields()) {
			// ע�� Transient�ĳ�Ա���������Ӧ���ݿ���е��ֶ�
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
			
			// ���ֶβ��������
			if (property.isAnnotationPresent(Column.class)
					&& !property.getAnnotation(Column.class).updatable()) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // ���ֶ�û�б��޸ģ���Ӧ�ñ�����
				continue;
			}
			
			final Object value = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(value)) { // ����ṩ��Model��Ϊ�Ǵ����ֻ࣬���·ǿ��ֶ�
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
	 * ͨ��Model��������ͨ����ѯ�������ƻ������������µ�SQL��䣬�����ֶε�˳��Ϊָ���ĳ�Ա�������Ƶ�˳��<br>
	 * ʹ�ø�SQL���и��²��������{@code inValues}����밴����ӳ�Ա������ֵ������������������ֵ��������ֵ
	 * 
	 * @param modelClass Model����
	 * @param bindVersion �Ƿ�󶨰汾
	 * @param conditionColumnNames Ҫ�������µĲ�ѯ�����������ƣ����Ϊ����������Ϊ����
	 * @param updateColumnNames Ҫ�������µĳ�Ա��������
	 * @return �������µ�SQL���
	 */
	public final <T> String buildBatchUpdateSql(final Class<T> modelClass, final boolean bindVersion, final List<String> conditionColumnNames, final String ... updateColumnNames) {
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClass);
		
		if (ObjectUtils.isEmpty(updateColumnNames)) {
			return null;
		}
		
		// Sql���
		final StringBuilder sql = new StringBuilder("UPDATE ").append(ModelUtils.getTableName(clazz)).append("\n   SET ");
		
		// SQL������Ҫ��ӵ��ֶ�����
		int count = 0;
		
		String versionName = null; // �汾����
		
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
	 * ͨ��Model���������ֵ�����ɲ�ѯ������SQL���
	 * 
	 * @param modelClazz Model�࣬��Ӧ���ݿ��б��ʵ����
	 * @param idValue ������ֵ
	 * @param columns Ҫ��ѯ�����������Ϊ�գ��Ͳ�ȫ��
	 * @return ִ�в�ѯ������{@link BoundSql}
	 */
	public final <T> BoundSql buildSelectByIdBoundSql(final Class<T> modelClazz, final Object idValue, final String ... columns) {
		final Field pkProperty = ModelUtils.getPkProperty(modelClazz);
		
		final String tableName = ModelUtils.getTableName(modelClazz);
		
		// Sql���
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
	 * ͨ��Model���һ��������ֵ�����ɲ�ѯ������SQL���
	 * 
	 * @param modelClazz Model�࣬��Ӧ���ݿ��б��ʵ����
	 * @param definition �Ӳ�ѯ�Ķ���
	 * @param idValues һ��������ֵ
	 * @param columns Ҫ��ѯ�����������Ϊ�գ��Ͳ�ȫ��
	 * @return ִ�в�ѯ������{@link BoundSql}
	 */
	public final <T> BoundSql buildSelectByIdsBoundSql(final Class<T> modelClazz, final List<?> idValues, final String ... columns) {
		final Field pkProperty = ModelUtils.getPkProperty(modelClazz);
		
		final String tableName = ModelUtils.getTableName(modelClazz);
		
		// Sql���
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
	 * ͨ��Model���������ֵ������ɾ��������SQL���
	 * 
	 * @param modelClazz Model�࣬��Ӧ���ݿ��б��ʵ����
	 * @param idValue ������ֵ
	 * @return ִ��ɾ��������{@link BoundSql}
	 */
	public final <T> BoundSql buildDeleteByIdBoundSql(final Class<T> modelClazz, final Object idValue) {
		return new BoundSql(new StringBuilder("DELETE FROM ").append(ModelUtils.getTableName(modelClazz))
				                      .append(" WHERE ").append(ModelUtils.getPkProperty(modelClazz).getName().toUpperCase())
				                      .append(" = ?").toString(),
				                      idValue);
	}
	
	/**
	 * ͨ��Model���һ��������ֵ����������ɾ��������SQL���
	 * 
	 * @param modelClazz Model�࣬��Ӧ���ݿ��б��ʵ����
	 * @param idValues һ��������ֵ
	 * @return ִ������ɾ��������{@link BoundSql}
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
	 * ͨ��Model���������ֵ������ɾ��������SQL��䣬�Ұ󶨰汾
	 * 
	 * @param modelClazz Model�࣬��Ӧ���ݿ��б��ʵ����
	 * @param idValue ������ֵ
	 * @param versionValue �汾��ֵ
	 * @return ִ��ɾ��������{@link BoundSql}
	 * 
	 * @see #buildDeleteByIdBoundSql(Class, Object)
	 */
	public final <T> BoundSql buildDeleteByIdBoundSql(final Class<?> modelClazz, final Object idValue, final Long versionValue) {
		final BoundSql boundSql = buildDeleteByIdBoundSql(modelClazz, idValue);
		
		boundSql.setVersion(ModelUtils.getVersionProperty(modelClazz).getName().toUpperCase(), versionValue);
		
		return boundSql;
	}
	
	/**
	 * ���ɲ�ѯ���м�¼��SQL���
	 * 
	 * @param clazz Model��
	 * @param orders ����
	 * @param columns Ҫ��ѯ�����������Ϊ�գ��Ͳ�ȫ��
	 * @return ִ�в�ѯ������{@link BoundSql}��{@code null}
	 */
	public final <T> BoundSql buildSelectAllBoundSql(final Class<T> clazz, final Order[] orders, final String ... columns) {
		// Sql���
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
	 * ͨ���ж�������Model�����ɲ�ѯ������SQL���
	 * <p>1. Model�Ǵ���ͨ��{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * �����ɵĴ�����ô���ɴ�������޸ĵ��ֶΣ���ԭ����ֵ��һ����Ϊ�ж����� </p>
	 * <p>2. Model�ǷǴ����ࣺ�ǿ��ֶ�Ϊ�ж�����</p>
	 * 
	 * @param model �����ж�������Model����Ӧ���ݿ��б��ʵ��
	 * @param orders ����
	 * @param columns Ҫ��ѯ�����������Ϊ�գ��Ͳ�ȫ��
	 * @return ִ�в�ѯ������{@link BoundSql}��{@code null}
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
		
		// Sql���
		final StringBuilder sql;
		
		if (columns == null || columns.length == 0) {
			sql = new StringBuilder("SELECT T.*");
		} else {
			sql = new StringBuilder("SELECT ").append(ArrayUtils.toString(columns, ", "));
		}
		
		sql.append("\n")
		   .append("  FROM ").append(ModelUtils.getTableName(clazz)).append(" T");
		
		// ����IN��ֵ����������,����˳���
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL��ѯ��䣬���Ƚϵ��ֶ�����
		int count = 0;
				
		for (Field property : clazz.getDeclaredFields()) {
			// ע�� Transient�ĳ�Ա���������Ӧ���ݿ���е��ֶ�
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // ���ֶ�û�б��޸�
				continue;
			}
						
			Object inValue = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(inValue)) { // ����ṩ��Model��Ϊ�Ǵ����ֻ࣬�жϷǿ��ֶ�
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
	 * ͨ���ж�����Model������ɾ��������SQL���<br>
	 * <p>1. Model�Ǵ���ͨ��{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * �����ɵĴ�����ô���ɴ�������޸ĵ��ֶ�Ϊ����ԭ����ֵ��һ�����ж����� </p>
	 * <p>2. Model�ǷǴ����ࣺ�ǿ��ֶ�Ϊ�ж�����</p>
	 * 
	 * <p>���{@code bindVersion}Ϊ{@code true}����ôɾ������ʱ�ǳ��԰󶨰汾����������ϼ����ж�<br>
	 * ֻ�е�Model���Ӧ�汾�ĳ�Ա������ע��{@link Version}�ĳ�Ա�����������Ҳ�Ϊ��ʱ�Ż���Ч</p>
	 * 
	 * @param model Ҫɾ�����ж�����Model����Ӧ���ݿ��б��ʵ��
	 * @param bindVersion �Ƿ�󶨰汾
	 * @return ִ��ɾ��������{@link BoundSql}��{@code null}
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
		
		// Sql���
		final StringBuilder sql = new StringBuilder("DELETE FROM ").append(ModelUtils.getTableName(modelClazz)).append("\n WHERE ");
		
		// ����IN��ֵ����������,����˳���
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL��ѯ��䣬���Ƚϵ��ֶ�����
		int count = 0;
		
		Field versionProperty = null;
				
		for (Field property : clazz.getDeclaredFields()) {
			// ע�� Transient�ĳ�Ա���������Ӧ���ݿ���е��ֶ�
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			if (property.isAnnotationPresent(Version.class)) {
				versionProperty = property;
				
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // ���ֶ�û�б��޸ģ���Ӧ�ñ������ж�����
				continue;
			}
			
			final Object inValue = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(inValue)) { // ����ṩ��Model��Ϊ�Ǵ����࣬�ǿ��ֶ�Ϊ�ж�����
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
	 * ͨ���ж�����Model������ɾ��������SQL��䣬û�а汾����
	 * 
	 * @see #buildDeleteBoundSql(Object, boolean)
	 */
	public final <T> BoundSql buildDeleteBoundSql(final T model) {
		return buildDeleteBoundSql(model, false);
	}
	
	/**
	 * ͨ���ж�����Model�����ɲ�ѯ��������������SQL���<br>
	 * <p>1. Model�Ǵ���ͨ��{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * �����ɵĴ�����ô���ɴ�������޸ĵ��ֶ�Ϊ����ԭ����ֵ��һ�����ж����� </p>
	 * <p>2. Model�ǷǴ����ࣺ�ǿ��ֶ�Ϊ�ж�����</p>
	 * 
	 * @param model �����ж�������Model����Ӧ���ݿ��б��ʵ��
	 * @return ִ�в�ѯ������{@link BoundSql}
	 */
	public final <T> BoundSql buildCountBoundSql(final T model) {
		final Set<String> modifiedColumnNames = ((BaseModel)model).getM$aop();
		@SuppressWarnings("unchecked")
		final Class<T> modelClazz = (Class<T>) model.getClass();
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClazz);	
		
		if (ClassUtils.isProxy(modelClazz)
				&&	modifiedColumnNames.isEmpty()) { // ������û�б��޸ĵ��ֶΣ���ѯȫ������
			return new BoundSql("SELECT COUNT(1) FROM " + ModelUtils.getTableName(clazz));
		}
		
		// Sql���
		final StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM ").append(ModelUtils.getTableName(clazz)).append("\n WHERE ");
		
		// ����IN��ֵ����������,����˳���
		final List<Object> inValues = new ArrayList<Object>();
		
		// SQL��ѯ��䣬���Ƚϵ��ֶ�����
		int count = 0;
		
		for (Field property : clazz.getDeclaredFields()) {
			if (property.isAnnotationPresent(Version.class)) {
				continue;
			}
			
			final String propertyName = property.getName();
			
			if (ClassUtils.isProxy(modelClazz)
					&& !modifiedColumnNames.contains(propertyName.toUpperCase())) { // ���ֶ�û�б��޸ģ���Ӧ�ñ������ж�����
				continue;
			}
			
			final Object inValue = MethodUtils.readProperty(model, propertyName);
			
			if (!ClassUtils.isProxy(modelClazz)
					&& ObjectUtils.isEmpty(inValue)) { // ����ṩ��Model��Ϊ�Ǵ����࣬�ǿ��ֶ�Ϊ�ж�����
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
	 * ��ȡͨ����������������������ֵ����ѯ�����л�ָ���е�ֵ��SQL
	 * 
	 * @param tableName ����
	 * @param pkName ����������
	 * @param pkValue ������ֵ
	 * @param columnNames Ҫ��ѯ�����������Ϊ�����ѯȫ����
	 * @return ��ѯ������{@link BoundSql}
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
	 * ͨ���ж�����Model�������ж�������SQLƬ����䣬��AND��ͷ<br>
	 * <p>1. Model�Ǵ���ͨ��{@link com.sunwayworld.escm.core.utils.ModelUtils#getProxy}<br>
	 * �����ɵĴ�����ô���ɴ�������޸ĵ��ֶ�Ϊ����ԭ����ֵ��һ�����ж����� </p>
	 * <p>2. Model�ǷǴ����ࣺ�ǿ��ֶ�Ϊ�ж�����</p>
	 * 
	 * @param model �����ж�������Model����Ӧ���ݿ��б��ʵ��
	 * @return ��װ��{@link BoundSql}����AND��ͷ���ж�������SQLƬ��
	 */
	public final <T> BoundSql buildConditionBoundSql(final T model) {
		@SuppressWarnings("unchecked")
		final Class<T> modelClazz = (Class<T>) model.getClass();
		
		final Class<T> clazz = ClassUtils.getOriginalClass(modelClazz);	
		
		final boolean isProxy = ClassUtils.isProxy(modelClazz); // �Ƿ��Ǵ�����
		
		final Set<String> modifiedColumnNames = ((BaseModel)model).getM$aop();
		
		if (isProxy && modifiedColumnNames.isEmpty()) { // �����࣬û���κ��ֶα����
			return null;
		}
		
		// Sql���
		final StringBuilder sql = new StringBuilder("");
		
		// ����IN��ֵ����������,����˳���
		final List<Object> inValues = new ArrayList<Object>();
		
		for (Field property : clazz.getDeclaredFields()) {
			if (property.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			boolean isCondition = false; // �Ƿ������ڲ�ѯ����
			
			Object propertyValue = null;
			
			if (isProxy) { // ������
				if (modifiedColumnNames.contains(property.getName().toUpperCase())) { // ���ֶα����
					isCondition = true;
					
					propertyValue = MethodUtils.readProperty(model, property.getName());
				}
			} else {
				propertyValue = MethodUtils.readProperty(model, property.getName());
				
				if (propertyValue != null) {
					isCondition = true;
				}
			}
			
			if (isCondition) { // �����ڲ�ѯ����
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
					
					// �����ǰ��Ա������ֵΪ��ʱ���ж��Ƿ����Ĭ��ֵ������Ĭ��ֵ
					if (propertyValue == null) {
						String defaultValue = condition.defaultValue();
						if (!StringUtils.isBlank(defaultValue)) {
							propertyValue = ConvertUtils.convert(defaultValue, property.getType());
						}
					}
					
					// ��ѯ������д���˶�Ӧ������
					if (!StringUtils.isBlank(condition.column())) {
						column = condition.column();
					}
					
					if (propertyValue == null) {
						sql.append("   AND ").append(alias).append(column).append(" IS NULL\n");
					} else {
						final Match match = condition.match();
						
						if (Number.class.isAssignableFrom(property.getType()) // ��������
								|| Date.class.isAssignableFrom(property.getType())) { // ��������
							if (Match.EQUAL.equals(match)
									|| Match.CENTER.equals(match)) {
								sql.append("   AND ").append(alias).append(column).append(" = ?\n");
							} else if (Match.LEFT.equals(match)) {
								sql.append("   AND ").append(alias).append(column).append(" < ?\n");
							} else if (Match.RIGHT.equals(match)) {
								sql.append("   AND ").append(alias).append(column).append(" >= ?\n");
							}
							
							if (Date.class.isAssignableFrom(property.getType()) // ��������
									&& Match.LEFT.equals(match)) { // С�� 
								inValues.add(DateTimeUtils.addDay(ConvertUtils.convert(propertyValue, Date.class), 1)); // ����Ҫ��һ
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
