package com.sunwayworld.escm.core.dao.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.sunwayworld.escm.core.utils.ClassUtils;
import com.sunwayworld.escm.core.utils.FieldUtils;
import com.sunwayworld.escm.core.utils.MethodUtils;
import com.sunwayworld.escm.core.utils.ObjectUtils;

public class BaseModelInterceptor implements MethodInterceptor {
	private static final Logger logger = Logger.getLogger(BaseModelInterceptor.class);
	
	private Object model;
	
	private boolean createdByInstance = false;
	
	public static final <T extends BaseModel> T getProxy(Class<T> clazz) {
		final T model = ClassUtils.newInstance(clazz);
		
		return getProxy(model, false);
	}
	
	public static final <T extends BaseModel> T getProxy(T model) {
		return getProxy(model, true);
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends BaseModel> T getProxy(T model, final boolean createdByInstance) {
		if (ClassUtils.isProxy(model.getClass())) {
			return model;
		}
		
		final BaseModelInterceptor interceptor = new BaseModelInterceptor(model, createdByInstance);
		
		Enhancer enhancer = new Enhancer();
		// 设置最后生成的代理类的父类
		enhancer.setSuperclass(model.getClass());
		// 设置切面回调
		enhancer.setCallback(interceptor);
		
		final T proxy = (T) enhancer.create();
		
		return proxy;
	}
	
	private BaseModelInterceptor(final Object model, final boolean createdByInstance) {
		this.model = model;
		this.createdByInstance = createdByInstance;
	}

	@Override
	public Object intercept(final Object proxy, final Method method, final Object[] args,
			final MethodProxy methodProxy) throws Throwable {
		final String methodName = method.getName();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Intercept  ModelInterceptor : " + method.getName() + " " + methodProxy.getSuperName());
		}
		
		if (methodName.startsWith("set")) {
			final Field property = FieldUtils.getField(model.getClass(), methodName.substring(3));
			
			if (property != null
					&& (args != null && args.length == 1)
					&& !property.isAnnotationPresent(Transient.class)) {
				final String propertyName = property.getName().toUpperCase();
				
				final BaseModel baseModel = (BaseModel)model;
				
				final Set<String> m$aop = baseModel.getM$aop();
				
				if (!m$aop.contains(propertyName)) {
					if (!createdByInstance) { // 如果是用类名创建代理时，只要是赋过值，就算变更
						baseModel.addM$aop(propertyName);
					} else {
						final Object originValue = FieldUtils.readField(model, property);
						
						if (!ObjectUtils.isEquals(originValue, args[0])) {
							baseModel.addM$aop(propertyName);
						}
					}
				}
			}
		}
		
		if ("finalize".equals(methodName)) {
			model = null;
			return methodProxy.invokeSuper(proxy, args);
		} else {
			return MethodUtils.invokeMethod(model, method, args);
		}
	}
}

