package com.sunwayworld.escm.core.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * µÇÂ¼ÑéÖ¤¹ýÂËÆ÷
 * 
 */
public class EscmUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	@Override
	public Authentication attemptAuthentication(
			final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException {
		return super.attemptAuthentication(request, response);
	}
}
