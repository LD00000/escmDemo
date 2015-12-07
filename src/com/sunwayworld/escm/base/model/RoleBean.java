package com.sunwayworld.escm.base.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sunwayworld.escm.core.dao.model.BaseModel;

/**
 * 权限定义表
 */
@Table(name="EC_USER")
public class RoleBean extends BaseModel implements Serializable {
	@Transient
	private static final long serialVersionUID = 3349927103049098720L;
	
	@Id
	public String role;

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
