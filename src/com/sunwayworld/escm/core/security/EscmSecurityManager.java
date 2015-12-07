package com.sunwayworld.escm.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.sunwayworld.escm.core.utils.StringUtils;

/**
 * Spring ��ȫ������
 * 
 */
public class EscmSecurityManager {
	/**
	 * �Ƿ����û���¼
	 */
	public static final boolean isUserLogin() {
		final SecurityContext context = SecurityContextHolder.getContext();
		
		final Authentication authentication = context.getAuthentication();
		
		if (authentication == null) {
			return false;
		}
		
		Object principal = authentication.getPrincipal();
		
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails)principal;
			
			return !StringUtils.isBlank(user.getUsername());
		}
		
		return false;
	}
	
	/**
	 * ��ȡ��¼�û���ID
	 */
	public static final String getLoginUserId() {
		final SecurityContext context = SecurityContextHolder.getContext();
		
		final Authentication authentication = context.getAuthentication();
		
		if (authentication == null) {
			return null;
		}
		
		Object principal = authentication.getPrincipal();
		
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails)principal;
		
			System.out.println(">>>>>>>>>>>>>>> : " + user.getUsername());
			
			return user.getUsername();
		}
		
		return null;
	}
}
