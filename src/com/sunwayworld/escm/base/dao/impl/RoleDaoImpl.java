package com.sunwayworld.escm.base.dao.impl;

import org.springframework.stereotype.Repository;

import com.sunwayworld.escm.base.dao.RoleDao;
import com.sunwayworld.escm.base.model.RoleBean;
import com.sunwayworld.escm.core.dao.GenericBaseDaoTemplate;

@Repository
public class RoleDaoImpl extends GenericBaseDaoTemplate<RoleBean, String> implements RoleDao {
}