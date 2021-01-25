package com.myclass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myclass.constant.MessageConstants;
import com.myclass.constant.TemplateConstants;


@Controller
@RequestMapping("account")
public class AuthenticationController {
	@GetMapping(value = {"login"})
	public String index(@RequestParam(required = false, name = "error") String error, Model model) {
		if(error != null) {
			model.addAttribute("error", MessageConstants.MESSAGE_LOGIN_FAIL);
		}
		return TemplateConstants.TEMPLATE_LOGIN;
	}
}
