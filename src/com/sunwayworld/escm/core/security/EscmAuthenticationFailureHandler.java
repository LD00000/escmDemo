package com.sunwayworld.escm.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.sunwayworld.escm.Constant;
import com.sunwayworld.escm.base.dao.UserDao;
import com.sunwayworld.escm.base.model.UserBean;
import com.sunwayworld.escm.core.utils.StringUtils;

/**
 * ��¼ʧ��ʱҪִ�е���ת������
 * 
 */
public class EscmAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Autowired
	private UserDao userDao;
	
	private String usernameParameter; // ��¼�û��Ĳ�������
	
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		super.onAuthenticationFailure(request, response, exception);
		
		// �û���¼ʧ��ʱ������ʧ�ܴ���
		final String userId = request.getParameter(usernameParameter);
		
		if (!StringUtils.isBlank(userId)) {
			final Integer failCount = (Integer) userDao.selectOneColumnById(userId, "failCount");
			
			if (failCount <= Constant.MAX_LOGIN_FAIL_COUNT) {
				final UserBean user = new UserBean();
				user.setId(userId);
				user.setFailCount(failCount + 1);
				
				userDao.update(user);
			}
		}
	}

	public String getUsernameParameter() {
		return usernameParameter;
	}
	public void setUsernameParameter(String usernameParameter) {
		this.usernameParameter = usernameParameter;
	}
}
