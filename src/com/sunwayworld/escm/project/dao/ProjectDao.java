package com.sunwayworld.escm.project.dao;

import java.util.List;

import com.sunwayworld.escm.core.dao.GenericBaseDao;
import com.sunwayworld.escm.core.dao.Pagination;
import com.sunwayworld.escm.project.model.ProjectBean;
import com.sunwayworld.escm.project.model.ProjectQBean;


public interface ProjectDao extends GenericBaseDao<ProjectBean, String> {
	/**
	 * 通过条件和分页信息查询满足条件项目
	 * 
	 * @param condition 查询条件
	 * @param pagination 分页信息
	 * @return 查询满足条件项目
	 */
	public List<ProjectBean> selectByCondition(final ProjectQBean condition, final Pagination pagination);
}
