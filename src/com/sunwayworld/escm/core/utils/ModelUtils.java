package com.sunwayworld.escm.core.utils;

import java.lang.reflect.Field;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.sunwayworld.escm.core.dao.model.BaseModel;
import com.sunwayworld.escm.core.dao.model.BaseModelInterceptor;
import com.sunwayworld.escm.core.exception.InternalException;


/**
 * Model��صĹ����࣬Model�Ǻ����ݿ��е�Table��View��һ��һ�Ĺ�ϵ
 */
public final class ModelUtils {
	
	/**
	 * ��Model�����ȡ��Ӧ�����ݿ�ı���<br>
	 * ���{@link Table}ע�������ϣ���ע����{@code name}��ֵ����ô����Ϊ��ֵ<br>
	 * ������������������
	 * 
	 * @param modelClazz Model����
	 * @return ��Ӧ�����ݿ�ı���
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
	 * ��Model�����ȡ��Ӧ�����ĳ�Ա����<br>
	 * ��������{@link Id}��ע��
	 * 
	 * @param modelClazz Model����
	 * @return ��Ӧ�����ĳ�Ա����
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
	 * ��Model�����ȡ��Ӧ�汾�ĳ�Ա����<br>
	 * �汾����{@link Version}��ע��
	 * 
	 * @param modelClazz Model����
	 * @return ��Ӧ�汾�ĳ�Ա����
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
	 * ��ȡ��ӦModel�Ĵ����࣬��ر��޸ĵ��ֶ�
	 * 
	 * @param model Model���ʵ��
	 * @return ������
	 */
	public static final <T extends BaseModel> T getProxy(T model) {
		if (ClassUtils.isProxy(model.getClass())) {
			return model;
		}
		
		return BaseModelInterceptor.getProxy(model);
	}
	
	/**
	 * ��ȡ��ӦModel�Ĵ����࣬��ر��޸ĵ��ֶ�
	 * 
	 * @param clazz Model��
	 * @return ������
	 */
	public static final <T extends BaseModel> T getProxy(Class<T> clazz) {
		return BaseModelInterceptor.getProxy(clazz);
	}
}
