package com.sunwayworld.escm.core.dao.model;

import java.util.HashSet;
import java.util.Set;

/**
 * ����ģ�͵Ļ���
 */
public abstract class BaseModel {
	/**
	 * ���ڴ���Domain�ﱻ�޸ĵĳ�Ա��������
	 */
	private Set<String> m$aop = new HashSet<String>();

	public Set<String> getM$aop() {
		return m$aop;
	}

	public void addM$aop(String methodName) {
		m$aop.add(methodName);
	}
}
