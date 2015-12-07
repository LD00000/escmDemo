package com.sunwayworld.escm.base.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sunwayworld.escm.core.dao.model.BaseModel;

/**
 * �û�Ȩ�ޱ�
 */
@Table(name="EC_USER_ROLE")
public class UserRoleBean extends BaseModel implements Serializable {
	@Transient
	private static final long serialVersionUID = 7072636683829374697L;
	
	@Id
	private Long id;
	private String userId; // �û�ID
	private String role; // Ȩ��
	
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
