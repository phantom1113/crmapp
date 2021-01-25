package com.myclass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.myclass.constant.TemplateConstants;

@Controller
@RequestMapping("error")
public class ErrorController {
	@GetMapping(value = {"403"})
	public String index() {
		return TemplateConstants.TEMPLATE_ERROR_403;
	}
}
