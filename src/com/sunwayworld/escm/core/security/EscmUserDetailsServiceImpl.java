package com.sunwayworld.escm.core.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sunwayworld.escm.base.dao.UserDao;
import com.sunwayworld.escm.base.dao.UserRoleDao;
import com.sunwayworld.escm.base.model.UserBean;
import com.sunwayworld.escm.base.model.UserRoleBean;

/**
 * Spring Security 进行用户权限管理的Service
 * 
 */
@Service
public class EscmUserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Autowired
	private UserCache userCache;
	
	@Override
	public UserDetails loadUserByUsername(String userId)
			throws UsernameNotFoundException {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> loadUserByUsername : " + userId);
		
		UserBean user = (UserBean)userCache.getUserFromCache(userId);
		
		if (user == null) {
			user = userDao.selectById(userId);
			
			if (user == null) {
				throw new UsernameNotFoundException("当前用户不存在!");
			}
			
			final UserRoleBean condition = new UserRoleBean();
			condition.setUserId(userId);
			
			final List<UserRoleBean> userRoleList = userRoleDao.selectByCondition(condition);
			
			user.setRoleList(userRoleList);
		}
		
		return user;
	}

}
