package com.sunwayworld.escm.core.security;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * �û��Ƿ�ӵ����������Դ��Ȩ�޵Ĺ�����
 * 
 */
public class EscmAccessDecisionManager implements AccessDecisionManager {

	@Override
	public void decide(Authentication authentication, Object arg1,
			Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
			InsufficientAuthenticationException {
		if(configAttributes == null) {
			return;
		}
		
		// ���������Դӵ�е�Ȩ��(һ����Դ�Զ��Ȩ��)
		Iterator<ConfigAttribute> iterator = configAttributes.iterator();
		
		while (iterator.hasNext()) {
			ConfigAttribute configAttribute = iterator.next();
			
			// ������������Դ����Ҫ��Ȩ��
			String role = configAttribute.getAttribute();
			
			// �û���ӵ�е�Ȩ��authentication
			for (GrantedAuthority ga : authentication.getAuthorities()) {
				if(ga.getAuthority().equalsIgnoreCase(role)) {
					return;
				}
			}
		}
		
		// û��Ȩ��
		throw new AccessDeniedException("��û��Ȩ�޷��ʣ� "); 
	}

	@Override
	public boolean supports(ConfigAttribute configAttribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
