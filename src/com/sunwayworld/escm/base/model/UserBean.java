package com.sunwayworld.escm.base.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sunwayworld.escm.Constant;
import com.sunwayworld.escm.core.dao.model.BaseModel;

/**
 * 用户表
 */
@Table(name = "EC_USER")
public class UserBean extends BaseModel implements UserDetails {
	@Transient
	private static final long serialVersionUID = 2010856430608839579L;

	@Id
	private String id; // 用户登录ID
	private String password; // 密码
	private String name; // 用户名称
	private Integer failCount; // 登录失败次数，登录成功后重置为0
	private Integer enable; // 是否启用（0-否 1-是）

	@Transient
	private List<UserRoleBean> roleList = new ArrayList<UserRoleBean>(); // 用户权限

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<UserRoleBean> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<UserRoleBean> roleList) {
		if (roleList != null) {
			this.roleList = roleList;
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		for (UserRoleBean userRole : roleList) {
			grantedAuthorities.add(new SimpleGrantedAuthority(userRole
					.getRole()));
		}
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.id;
	}

	@Override
	public boolean isAccountNonExpired() {
		return Constant.YES.equals(enable);
	}

	@Override
	public boolean isAccountNonLocked() {
		return failCount < Constant.MAX_LOGIN_FAIL_COUNT;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return Constant.YES.equals(enable);
	}

	@Override
	public boolean equals(Object target) {
		if (target == null || !(target instanceof UserBean)) {
			return false;
		}

		UserBean user = (UserBean) target;

		return this.getId().equals(user.getId())
				&& this.getPassword().equals(user.getPassword())
				&& this.isAccountNonExpired() == user.isAccountNonExpired()
				&& this.isAccountNonLocked() == user.isAccountNonLocked()
				&& this.isCredentialsNonExpired() == user
						.isCredentialsNonExpired()
				&& this.isEnabled() == user.isEnabled();
	}

	@Override
	public int hashCode() {
		int code = 9792;

		for (GrantedAuthority authority : getAuthorities()) {
			code = code * (authority.hashCode() % 7);
		}

		if (this.getPassword() != null) {
			code = code * (this.getPassword().hashCode() % 7);
		}

		if (this.getUsername() != null) {
			code = code * (this.getUsername().hashCode() % 7);
		}

		if (this.isAccountNonExpired()) {
			code = code * -2;
		}

		if (this.isAccountNonLocked()) {
			code = code * -3;
		}

		if (this.isCredentialsNonExpired()) {
			code = code * -5;
		}

		if (this.isEnabled()) {
			code = code * -7;
		}

		return code;
	}
}
