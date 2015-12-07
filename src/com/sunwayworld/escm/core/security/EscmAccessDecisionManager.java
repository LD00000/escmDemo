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
 * 用户是否拥有所请求资源的权限的管理类
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
		
		// 所请求的资源拥有的权限(一个资源对多个权限)
		Iterator<ConfigAttribute> iterator = configAttributes.iterator();
		
		while (iterator.hasNext()) {
			ConfigAttribute configAttribute = iterator.next();
			
			// 访问所请求资源所需要的权限
			String role = configAttribute.getAttribute();
			
			// 用户所拥有的权限authentication
			for (GrantedAuthority ga : authentication.getAuthorities()) {
				if(ga.getAuthority().equalsIgnoreCase(role)) {
					return;
				}
			}
		}
		
		// 没有权限
		throw new AccessDeniedException("您没有权限访问！ "); 
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
