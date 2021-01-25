package com.myclass.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.myclass.constant.TemplateConstants;
import com.myclass.entity.CustomUserDetail;
import com.myclass.service.TaskService;

@Controller
@RequestMapping("")
public class HomeController {
	private TaskService taskService;

	public HomeController(TaskService taskService) {
		super();
		this.taskService = taskService;
	}

	@GetMapping(value = { "" })
	public String index(Model model, HttpServletRequest request) {
		//Lấy thông tin user đăng nhập
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		int userId = ((CustomUserDetail) principal).getId();
		model.addAttribute("fullName", fullName);
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		//Tính các thông sô thông kê các task của mỗi nhóm người dùng
		if (request.isUserInRole("ROLE_USER")) {
			model.addAttribute("statisticalNumbers", taskService.countStatusTaskInUser(userId));
			return TemplateConstants.TEMPLATE_HOME;
		}
		model.addAttribute("statisticalNumbers", taskService.countStatusTask());
		if (request.isUserInRole("ROLE_MANAGER")) {
			model.addAttribute("statisticalNumbers", taskService.countStatusTaskInManager(userId));
			return TemplateConstants.TEMPLATE_HOME;
		}
		model.addAttribute("statisticalNumbers", taskService.countStatusTask());
		return TemplateConstants.TEMPLATE_HOME;
	}
}
