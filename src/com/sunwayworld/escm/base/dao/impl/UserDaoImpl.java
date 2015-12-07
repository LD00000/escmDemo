package com.sunwayworld.escm.base.dao.impl;

import org.springframework.stereotype.Repository;

import com.sunwayworld.escm.base.dao.UserDao;
import com.sunwayworld.escm.base.model.UserBean;
import com.sunwayworld.escm.core.dao.GenericBaseDaoTemplate;

@Repository("userDao")
public class UserDaoImpl extends GenericBaseDaoTemplate<UserBean, String> implements UserDao {
}
