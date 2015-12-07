package com.sunwayworld.escm.project.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sunwayworld.escm.core.dao.GenericBaseDaoTemplate;
import com.sunwayworld.escm.core.dao.Pagination;
import com.sunwayworld.escm.project.dao.ProjectDao;
import com.sunwayworld.escm.project.model.ProjectBean;
import com.sunwayworld.escm.project.model.ProjectQBean;
import com.sunwayworld.escm.project.sql.ProjectSqlBuilder;

@Repository
public class ProjectDaoImpl extends GenericBaseDaoTemplate<ProjectBean, String> implements
		ProjectDao {
	private ProjectSqlBuilder sqlBuilder = new ProjectSqlBuilder();
	
	@Override
	public List<ProjectBean> selectByCondition(ProjectQBean condition,
			Pagination pagination) {
		return this.selectByBoundSql(sqlBuilder.getSelectProjectByConditionSql(condition), pagination);
	}
}
