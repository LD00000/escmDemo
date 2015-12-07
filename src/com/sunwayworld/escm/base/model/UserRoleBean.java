package com.sunwayworld.escm.base.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sunwayworld.escm.core.dao.model.BaseModel;

/**
 * 用户权限表
 */
@Table(name="EC_USER_ROLE")
public class UserRoleBean extends BaseModel implements Serializable {
	@Transient
	private static final long serialVersionUID = 7072636683829374697L;
	
	@Id
	private Long id;
	private String userId; // 用户ID
	private String role; // 权限
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
