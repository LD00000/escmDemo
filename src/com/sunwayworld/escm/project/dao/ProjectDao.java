package com.sunwayworld.escm.project.dao;

import java.util.List;

import com.sunwayworld.escm.core.dao.GenericBaseDao;
import com.sunwayworld.escm.core.dao.Pagination;
import com.sunwayworld.escm.project.model.ProjectBean;
import com.sunwayworld.escm.project.model.ProjectQBean;


public interface ProjectDao extends GenericBaseDao<ProjectBean, String> {
	/**
	 * ͨ�������ͷ�ҳ��Ϣ��ѯ����������Ŀ
	 * 
	 * @param condition ��ѯ����
	 * @param pagination ��ҳ��Ϣ
	 * @return ��ѯ����������Ŀ
	 */
	public List<ProjectBean> selectByCondition(final ProjectQBean condition, final Pagination pagination);
}
