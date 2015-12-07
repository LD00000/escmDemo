package com.sunwayworld.escm.project.model;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sunwayworld.escm.core.dao.model.BaseModel;

/**
 * ��Ŀ��
 * 
 */
@Table(name = "EC_PROJECT")
public class ProjectBean extends BaseModel {
	@Id
	private String id; // ��Ŀ���
	private String name; // ��Ŀ����
	private String createUser; // �Ƶ��˱���
	@Transient
	private String createName; // �Ƶ�������
	private Date createDate; // �Ƶ�����
	private Integer enable; // �Ƿ�����
	
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
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
}
