package com.sunwayworld.escm.project.model;

import com.sunwayworld.escm.core.dao.model.BaseModel;
import com.sunwayworld.escm.core.dao.sql.Condition;
import com.sunwayworld.escm.core.dao.sql.Match;

/**
 * 项目表的查询条件
 * 
 */
public class ProjectQBean extends BaseModel {
	@Condition(match=Match.LEFT, alias="EC_PROJECT")
	private String id; // 项目简称
	@Condition(match=Match.CENTER, alias="EC_PROJECT")
	private String name; // 项目名称
	@Condition(match=Match.LEFT, alias="EC_PROJECT")
	private String createUser; // 制单人编码
	@Condition(match=Match.LEFT, alias="EC_USER", column="NAME")
	private String createName; // 制单人名称
	@Condition(match=Match.LEFT, alias="EC_PROJECT")
	private Integer enable; // 是否启用
	
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
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
}
