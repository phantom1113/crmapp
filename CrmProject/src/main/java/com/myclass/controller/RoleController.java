package com.myclass.controller;

import java.util.List;

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
import com.myclass.dto.RoleDto;
import com.myclass.entity.CustomUserDetail;
import com.myclass.service.RoleService;


@Controller
@RequestMapping("role")
public class RoleController {
	private RoleService roleService;
	
	public RoleController(RoleService roleService) {
		super();
		this.roleService = roleService;
	}

	@GetMapping(value = {""})
	public String index(Model model, HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		List<RoleDto> dtos = roleService.findAll();
		if(session.getAttribute("msgSuccess") != null) {
			model.addAttribute("msgSuccess", session.getAttribute("msgSuccess").toString());
			session.removeAttribute("msgSuccess");
		} else if(session.getAttribute("msgError") != null) {
			model.addAttribute("msgError", session.getAttribute("msgError").toString());
			session.removeAttribute("msgError");
		}
		model.addAttribute("roles", dtos);
		return TemplateConstants.TEMPLATE_ROLE;
	}
	
	@GetMapping(value = {"add"})
	public String add(Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		model.addAttribute("role", new RoleDto());
		return TemplateConstants.TEMPLATE_ROLE_ADD;
	}
	
	@PostMapping(value = {"add"})
	public String add(@Validated @ModelAttribute("role") RoleDto dto, BindingResult result, HttpSession session, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		//Kiểm tra xem dữ post lên và xử lí
		if(result.hasErrors()) {
			return TemplateConstants.TEMPLATE_ROLE_ADD;
		}
		else if(roleService.add(dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_ROLE_ADD_SUCCESS);
			return "redirect:/role";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_ROLE_ADD_FAIL);
		return TemplateConstants.TEMPLATE_ROLE_ADD;
	}
	
	@GetMapping(value = {"delete"})
	public String delete(@RequestParam("id") int id, HttpSession session) {
		if(roleService.deleteById(id) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_ROLE_DELETE_SUCCESS);
			return "redirect:/role";
		}
		session.setAttribute("msgError", MessageConstants.MESSAGE_ROLE_DELETE_FAIL);
		return "redirect:/role";
	}
	
	@GetMapping(value = {"edit"})
	public String edit(@RequestParam("id") int id, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		model.addAttribute("role", roleService.findById(id));
		return TemplateConstants.TEMPLATE_ROLE_EDIT;
	}
	
	@PostMapping(value = {"edit"})
	public String edit(@Validated @ModelAttribute("role") RoleDto dto, BindingResult result, HttpSession session, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		//Kiểm tra xem dữ post lên và xử lí
		if(result.hasErrors()) {
			model.addAttribute("role", roleService.findById(dto.getId()));
			model.addAttribute("msgError", MessageConstants.MESSAGE_ROLE_EDIT_FAIL);
			session.removeAttribute("msgError");
			return TemplateConstants.TEMPLATE_ROLE_EDIT;
		}
		else if(roleService.edit(dto.getId(),dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_ROLE_EDIT_SUCCESS);
			return "redirect:/role";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_ROLE_EDIT_FAIL);
		return TemplateConstants.TEMPLATE_ROLE_EDIT;
	}
}
