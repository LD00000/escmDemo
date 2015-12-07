package com.sunwayworld.escm.core.dao.model;

import java.util.HashSet;
import java.util.Set;

/**
 * 所有模型的基类
 */
public abstract class BaseModel {
	/**
	 * 用于储存Domain里被修改的成员变量名称
	 */
	private Set<String> m$aop = new HashSet<String>();

	public Set<String> getM$aop() {
		return m$aop;
	}

	public void addM$aop(String methodName) {
		m$aop.add(methodName);
	}
}
