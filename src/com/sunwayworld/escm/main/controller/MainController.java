package com.sunwayworld.escm.main.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ��¼��ҳ
 * 
 */
@Controller
@RequestMapping("/secure")
public class MainController {
	@RequestMapping("/main")
	public String main(ModelMap model, HttpServletRequest request) {
		return "secure.main";
	}
}
