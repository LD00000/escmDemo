package com.sunwayworld.escm.login.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sunwayworld.escm.core.security.EscmSecurityManager;

@Controller
public class LoginController {
	@RequestMapping({"/login.htm", "/"})
	public String login(
			@RequestParam(value = "fail", required = false) String fail,
			@RequestParam(value = "logout", required = false) String logout,
			ModelMap model, HttpServletRequest request) {
		// µÇÂ¼Ê§°Ü
		if (fail != null) {
			model.addAttribute("fail", "1");
		}
		
		// µÇ³ö
		if (logout != null) {
			model.addAttribute("logout", "1");
		}
		
		if (fail == null && logout == null) {
			if (EscmSecurityManager.isUserLogin()) {
				return "/secure/main";
			}
		}
		
		return "login";
	}
}
