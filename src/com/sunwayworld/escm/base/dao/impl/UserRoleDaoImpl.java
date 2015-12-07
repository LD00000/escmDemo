package com.sunwayworld.escm.base.dao.impl;

import org.springframework.stereotype.Repository;

import com.sunwayworld.escm.base.dao.UserRoleDao;
import com.sunwayworld.escm.base.model.UserRoleBean;
import com.sunwayworld.escm.core.dao.GenericBaseDaoTemplate;

@Repository
public class UserRoleDaoImpl extends GenericBaseDaoTemplate<UserRoleBean, Long> implements UserRoleDao {
}
