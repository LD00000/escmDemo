package com.sunwayworld.escm.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.sunwayworld.escm.base.dao.UserDao;
import com.sunwayworld.escm.base.model.UserBean;

/**
 * ��¼�ɹ�ʱҪִ�е���ת������
 * 
 */
public class EscmAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserCache userCache;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		super.onAuthenticationSuccess(request, response, authentication);
		
		final UserBean user = (UserBean)authentication.getPrincipal();
		
		System.out.println(">>>>>>>>>>>>>>>  onAuthenticationSuccess : " + user.getId());
		
		// ����ʧ�ܴ���
		if (user.getFailCount() > 0) {
			final UserBean updateUser = new UserBean();
			updateUser.setId(user.getId());
			updateUser.setFailCount(0);
			userDao.update(updateUser);
			
			user.setFailCount(0);
		}
		
		// ��¼�û����뻺��
		userCache.putUserInCache(user);
	}
}
