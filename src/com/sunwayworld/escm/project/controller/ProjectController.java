package com.sunwayworld.escm.project.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sunwayworld.escm.Constant;
import com.sunwayworld.escm.core.dao.Pagination;
import com.sunwayworld.escm.core.dao.PaginationList;
import com.sunwayworld.escm.core.security.EscmSecurityManager;
import com.sunwayworld.escm.core.utils.AjaxUtils;
import com.sunwayworld.escm.core.utils.BeanUtils;
import com.sunwayworld.escm.core.utils.ModelUtils;
import com.sunwayworld.escm.core.utils.NumberUtils;
import com.sunwayworld.escm.project.dao.ProjectDao;
import com.sunwayworld.escm.project.model.ProjectBean;
import com.sunwayworld.escm.project.model.ProjectQBean;

/**
 * 项目管理
 * 
 */
@Controller
@RequestMapping("/secure/project")
public class ProjectController {
	@Autowired
	private ProjectDao projectDao;
	
	@RequestMapping("/main")
	public String main(
			@RequestParam(value = "json", required = false) String json,
			HttpServletResponse response, HttpServletRequest request) {
		if (json != null) { // json请求
			final Pagination pagination = new Pagination(NumberUtils.toInt(request.getParameter(Constant.EASYUI_PAGINATION_PAGESIZE)),
					NumberUtils.toInt(request.getParameter(Constant.EASYUI_PAGINATION_CURRPAGE)));
			
			final ProjectQBean condition = new ProjectQBean();
			BeanUtils.copyProperties(request, condition, "", Constant.CONDITION_SUFFIX);
			
			final PaginationList<ProjectBean> projectList = (PaginationList<ProjectBean>)projectDao.selectByCondition(condition, pagination);
			final Pagination p = projectList.getPagination();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("total", p.getTotalRecord());
			jsonObject.accumulate("rows", projectList);
			
			AjaxUtils.response(response, jsonObject.toString());
			
			return null;
		}
		
		return "secure.project.main";
	}
	
	@RequestMapping("/insert")
	public String insert(
			@RequestParam(value = "json", required = false) String json,
			HttpServletResponse response, HttpServletRequest request) {
		final ProjectBean project = new ProjectBean();
		
		BeanUtils.copyProperties(request, project);
		
		project.setCreateUser(EscmSecurityManager.getLoginUserId()); // 制单人
		project.setCreateDate(new Date()); // 制单时间
		
		projectDao.insert(project);
		
		if (json != null) { // json请求
			AjaxUtils.responseSuccess(response);
			
			return null;
		}
		
		return "secure.project.main";
	}
	
	@RequestMapping("/update")
	public String update(
			@RequestParam(value = "json", required = false) String json,
			HttpServletResponse response, HttpServletRequest request) {
		final String id = request.getParameter("id");
		
		final ProjectBean project = ModelUtils.getProxy(projectDao.selectById(id));
		
		BeanUtils.copyProperties(request, project);
		
		projectDao.update(project);
		
		if (json != null) { // json请求
			AjaxUtils.responseSuccess(response);
			
			return null;
		}
		
		return "secure.project.main";
	}
	
	@RequestMapping("/delete")
	public String delete(
			@RequestParam(value = "json", required = false) String json,
			HttpServletResponse response, HttpServletRequest request) {
		final ProjectBean project = new ProjectBean();
		
		BeanUtils.copyProperties(request, project);
		
		projectDao.deleteById(project.getId());
		
		if (json != null) { // json请求
			AjaxUtils.responseSuccess(response);
			
			return null;
		}
		
		return "secure.project.main";
	}
}
