package com.sunwayworld.escm.core.utils;

import java.lang.reflect.Field;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.sunwayworld.escm.core.dao.model.BaseModel;
import com.sunwayworld.escm.core.dao.model.BaseModelInterceptor;
import com.sunwayworld.escm.core.exception.InternalException;


/**
 * Model相关的工具类，Model是和数据库中的Table或View是一对一的关系
 */
public final class ModelUtils {
	
	/**
	 * 从Model的类获取对应的数据库的表名<br>
	 * 如果{@link Table}注释在类上，且注明了{@code name}的值，那么表名为该值<br>
	 * 否则用类名当做表名
	 * 
	 * @param modelClazz Model的类
	 * @return 对应的数据库的表名
	 */
	public static final String getTableName(final Class<?> modelClazz) {
		if (modelClazz.isAnnotationPresent(Table.class)) {
			Table table = modelClazz.getAnnotation(Table.class);
			
			if (!StringUtils.isBlank(table.name())) {
				return table.name().toUpperCase();
			}
		}
		
		return modelClazz.getName().toUpperCase();
	}
	
	/**
	 * 从Model的类获取对应主键的成员变量<br>
	 * 主键上有{@link Id}的注释
	 * 
	 * @param modelClazz Model的类
	 * @return 对应主键的成员变量
	 */
	public static final Field getPkProperty(final Class<?> modelClazz) {
		for (Field property : modelClazz.getDeclaredFields()) {
			if (property.isAnnotationPresent(Id.class)) {
				return property;
			}
		}
		
		throw new InternalException("Model Class doesn't have ID property.");
	}
	
	/**
	 * 从Model的类获取对应版本的成员变量<br>
	 * 版本上有{@link Version}的注释
	 * 
	 * @param modelClazz Model的类
	 * @return 对应版本的成员变量
	 */
	public static final Field getVersionProperty(final Class<?> modelClazz) {
		for (Field property : modelClazz.getDeclaredFields()) {
			if (property.isAnnotationPresent(Version.class)) {
				return property;
			}
		}
		
		throw new InternalException("Model Class doesn't have Version property.");
	}
	
	/**
	 * 获取对应Model的代理类，监控被修改的字段
	 * 
	 * @param model Model类的实例
	 * @return 代理类
	 */
	public static final <T extends BaseModel> T getProxy(T model) {
		if (ClassUtils.isProxy(model.getClass())) {
			return model;
		}
		
		return BaseModelInterceptor.getProxy(model);
	}
	
	/**
	 * 获取对应Model的代理类，监控被修改的字段
	 * 
	 * @param clazz Model类
	 * @return 代理类
	 */
	public static final <T extends BaseModel> T getProxy(Class<T> clazz) {
		return BaseModelInterceptor.getProxy(clazz);
	}
}
