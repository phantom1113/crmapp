package com.myclass.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.myclass.dto.TaskDto;
import com.myclass.entity.CustomUserDetail;
import com.myclass.service.ProjectService;
import com.myclass.service.StatusService;
import com.myclass.service.TaskService;
import com.myclass.service.UserService;

@Controller
@RequestMapping("task")
public class TaskController {
	@Autowired
	ProjectService projectService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	TaskService taskService;
	
	@Autowired
	StatusService statusService;
	
	@GetMapping(value = {""})
	public String index(Model model, HttpSession session, HttpServletRequest request) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		String roleName =((CustomUserDetail) principal).getRoleName();
		int userId = ((CustomUserDetail) principal).getId();
		model.addAttribute("roleName", roleName);
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		//Get task theo từng loại người dùng
		if(request.isUserInRole("ROLE_MANAGER")) {
			model.addAttribute("tasks", taskService.getAllOfManager(userId));
		} else if (request.isUserInRole("ROLE_USER")) {
			model.addAttribute("tasks", taskService.getAllOfMember(userId));
		}
		model.addAttribute("avatar", avatar);
		if(session.getAttribute("msgSuccess") != null) {
			model.addAttribute("msgSuccess", session.getAttribute("msgSuccess").toString());
			session.removeAttribute("msgSuccess");
		} else if(session.getAttribute("msgError") != null) {
			model.addAttribute("msgError", session.getAttribute("msgError").toString());
			session.removeAttribute("msgError");
		}
		return TemplateConstants.TEMPLATE_TASK;
	}
	
	@GetMapping(value = {"add"})
	public String add(Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		int userId = ((CustomUserDetail) principal).getId();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		model.addAttribute("users", userService.findUserRoleName("ROLE_USER"));
		model.addAttribute("task", new TaskDto());
		model.addAttribute("projects", projectService.findByManagerId(userId));
		return TemplateConstants.TEMPLATE_TASK_ADD;
	}
	
	@PostMapping(value = { "add" })
	public String add(@Validated @ModelAttribute("task") TaskDto dto, BindingResult result, Model model, HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		int userId = ((CustomUserDetail) principal).getId();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		dto.setManagerId(userId);
		//Kiểm tra xem dữ post lên và xử lí
		if(result.hasErrors()) {
			model.addAttribute("projects", projectService.findByManagerId(userId));
			model.addAttribute("users", userService.findUserRoleName("ROLE_USER"));
			return TemplateConstants.TEMPLATE_TASK_ADD;
		}
		else if(taskService.add(dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_TASK_ADD_SUCCESS);
			return "redirect:/task";
		}
		model.addAttribute("projects", projectService.findByManagerId(userId));
		model.addAttribute("users", userService.findUserRoleName("ROLE_USER"));
		model.addAttribute("msgError", MessageConstants.MESSAGE_TASK_ADD_FAIL);
		session.removeAttribute("msgError");
		return TemplateConstants.TEMPLATE_TASK_ADD;
	}
	
	@GetMapping(value = { "edit" })
	public String edit(@RequestParam("id") int id, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		int userId = ((CustomUserDetail) principal).getId();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		model.addAttribute("task", taskService.findById(id));
		model.addAttribute("projects", projectService.findByManagerId(userId));
		model.addAttribute("users", userService.findUserRoleName("ROLE_USER"));
		return TemplateConstants.TEMPLATE_TASK_EDIT;
	}
	
	@PostMapping(value = {"edit"})
	public String edit(@Validated @ModelAttribute("task") TaskDto dto, BindingResult result, HttpSession session, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		int userId = ((CustomUserDetail) principal).getId();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		dto.setManagerId(userId);
		//Kiểm tra xem dữ post lên và xử lí
		if(result.hasErrors()) {
			model.addAttribute("task", taskService.findById(dto.getId()));
			model.addAttribute("projects", projectService.findByManagerId(userId));
			model.addAttribute("users", userService.findUserRoleName("ROLE_USER"));
			model.addAttribute("msgError", MessageConstants.MESSAGE_TASK_EDIT_FAIL);
			return TemplateConstants.TEMPLATE_TASK_EDIT;
		}
		else if(taskService.edit(dto.getId(),dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_TASK_EDIT_SUCCESS);
			return "redirect:/task";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_TASK_EDIT_FAIL);
		session.removeAttribute("msgError");
		model.addAttribute("task", taskService.findById(dto.getId()));
		model.addAttribute("projects", projectService.findByManagerId(userId));
		model.addAttribute("users", userService.findUserRoleName("ROLE_USER"));
		return TemplateConstants.TEMPLATE_TASK_EDIT;
	}
	
	@GetMapping(value = {"delete"})
	public String delete(@RequestParam("id") int id, HttpSession session) {
		if(taskService.deleteById(id) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_TASK_DELETE_SUCCESS);
			return "redirect:/task";
		}
		session.setAttribute("msgError", MessageConstants.MESSAGE_TASK_DELETE_FAIL);
		return "redirect:/task";
	}
	
	@GetMapping(value = { "updateStatus" })
	public String updateStatus(@RequestParam("id") int id, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("task", taskService.findById(id));
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		model.addAttribute("statuses", statusService.findAll());
		return TemplateConstants.TEMPLATE_TASK_UPDATE_STATUS;
	}
	
	@PostMapping(value = { "updateStatus" })
	public String updateStatus(@ModelAttribute("task") TaskDto dto, Model model, HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName =((CustomUserDetail) principal).getFullName();
		model.addAttribute("fullName", fullName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		if(taskService.updateStatusTask(dto.getStatusId(), dto.getId()) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_TASK_UPDATE_STATUS_SUCCESS);
			return "redirect:/task";
		}
		session.setAttribute("msgError", MessageConstants.MESSAGE_TASK_UPDATE_STATUS_FAIL);
		session.removeAttribute("msgError");
		return TemplateConstants.TEMPLATE_TASK_UPDATE_STATUS;
	}
	
	@GetMapping(value = {"profile"})
	public String profile(Model model, HttpServletRequest request) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		String email = ((CustomUserDetail) principal).getUsername();
		int userId = ((CustomUserDetail) principal).getId();
		String roleName =((CustomUserDetail) principal).getRoleName();
		model.addAttribute("fullName", fullName);
		model.addAttribute("email", email);
		model.addAttribute("roleName", roleName);
		String avatar ="/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("avatar", avatar);
		if(request.isUserInRole("ROLE_USER")) {
			model.addAttribute("statisticalNumbers", taskService.countStatusTaskInUser(userId));
			model.addAttribute("allTaskInUser", taskService.getAllOfMember(userId));
			return TemplateConstants.TEMPLATE_TASK_PROFILE;
		}
		if(request.isUserInRole("ROLE_MANAGER")) {
			model.addAttribute("statisticalNumbers", taskService.countStatusTaskInManager(userId));
			model.addAttribute("tasks", taskService.getAllOfManager(userId));
			return TemplateConstants.TEMPLATE_TASK_STATISTIC;
		}
		model.addAttribute("statisticalNumbers", taskService.countStatusTask());
		model.addAttribute("tasks", taskService.getAll());
		return TemplateConstants.TEMPLATE_TASK_STATISTIC;
	}
}
