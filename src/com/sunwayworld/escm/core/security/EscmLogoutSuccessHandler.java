package com.sunwayworld.escm.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.sunwayworld.escm.base.model.UserBean;

/**
 * 登出成功时要执行的跳转处理类
 * 
 */
public class EscmLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
	@Autowired
	private UserCache userCache;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		super.onLogoutSuccess(request, response, authentication);
		
		final UserBean user = (UserBean)authentication.getPrincipal();
		
		userCache.removeUserFromCache(user.getId());
	}
}
