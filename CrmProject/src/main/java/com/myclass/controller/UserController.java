package com.myclass.controller;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myclass.constant.MessageConstants;
import com.myclass.constant.TemplateConstants;
import com.myclass.dto.UserDto;
import com.myclass.dto.UserEditDto;
import com.myclass.entity.CustomUserDetail;
import com.myclass.service.RoleService;
import com.myclass.service.UserService;

@Controller
@RequestMapping("user")
public class UserController {
	private UserService userService;
	private RoleService roleService;
	
	public UserController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService =roleService;
	}

	@GetMapping(value = {""})
	public String index(Model model, HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		String roleName =((CustomUserDetail) principal).getRoleName();
		model.addAttribute("roleName", roleName);
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		if(session.getAttribute("msgSuccess") != null) {
			model.addAttribute("msgSuccess", session.getAttribute("msgSuccess").toString());
			session.removeAttribute("msgSuccess");
		} else if(session.getAttribute("msgError") != null) {
			model.addAttribute("msgError", session.getAttribute("msgError").toString());
			session.removeAttribute("msgError");
		}
		model.addAttribute("users", userService.getAll());
		return TemplateConstants.TEMPLATE_USER;
	}
	
	@GetMapping(value = { "add" })
	public String add(Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		model.addAttribute("user", new UserDto());
		model.addAttribute("roles", roleService.findAll());
		return TemplateConstants.TEMPLATE_USER_ADD;
	}

	@PostMapping(value = { "add" })
	public String add(@Validated @ModelAttribute("user") UserDto dto, BindingResult result, Model model, HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		if(result.hasErrors()) {
			model.addAttribute("roles", roleService.findAll());
			return TemplateConstants.TEMPLATE_USER_ADD;
		}
		else if(userService.add(dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_USER_ADD_SUCCESS);
			return "redirect:/user";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_USER_ADD_FAIL);
		model.addAttribute("roles", roleService.findAll());
		session.removeAttribute("msgError");
		return TemplateConstants.TEMPLATE_USER_ADD;
	}
	
	@GetMapping(value = { "edit" })
	public String edit(@RequestParam("id") int id, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		model.addAttribute("user", userService.findByIdForEdit(id));
		model.addAttribute("roles", roleService.findAll());
		return TemplateConstants.TEMPLATE_USER_EDIT;
	}
	
	@PostMapping(value = { "edit" })
	public String edit(@Validated @ModelAttribute("user") UserEditDto dto,BindingResult result ,Model model, HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		if(result.hasErrors()) {
			model.addAttribute("roles", roleService.findAll());
			model.addAttribute("user", userService.findByIdForEdit(dto.getId()));
			model.addAttribute("msgError", MessageConstants.MESSAGE_USER_EDIT_FAIL);
			session.removeAttribute("msgError");
			return TemplateConstants.TEMPLATE_USER_EDIT;
		}
		else if(userService.edit(dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_USER_EDIT_SUCCESS);
			return "redirect:/user";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_USER_EDIT_FAIL);
		session.removeAttribute("msgError");
		return TemplateConstants.TEMPLATE_USER_EDIT;
	}
	
	@GetMapping(value = {"delete"})
	public String delete(@RequestParam("id") int id, Model model, HttpSession session) {
		if(userService.deleteById(id) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_USER_DELETE_SUCCESS);
			return "redirect:/user";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_USER_DELETE_FAIL);
		session.removeAttribute("msgError");
		userService.deleteById(id);
		return "redirect:/user";
	}
}
