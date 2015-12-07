package com.sunwayworld.escm.sql.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SQLπ‹¿Ì
 * 
 */
@Controller
@RequestMapping("/secure/sql")
public class SqlController {
	@RequestMapping("/main")
	public String main(ModelMap model, HttpServletRequest request) {
		return "secure.sql.main";
	}
}
