package com.myclass.controller;

import javax.servlet.http.HttpServletRequest;
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
import com.myclass.dto.ProjectDto;
import com.myclass.entity.CustomUserDetail;
import com.myclass.service.ProjectService;
import com.myclass.service.TaskService;

@Controller
@RequestMapping("project")
public class ProjectController {
	private ProjectService projectService;
	private TaskService taskService;

	public ProjectController(ProjectService projectService, TaskService taskService) {
		super();
		this.projectService = projectService;
		this.taskService = taskService;
	}

	@GetMapping(value = { "" })
	public String index(Model model, HttpSession session, HttpServletRequest request) {
		//Lấy ds thông tinh người dùng
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		String roleName = ((CustomUserDetail) principal).getRoleName();
		int userId = ((CustomUserDetail) principal).getId();
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("roleName", roleName);
		model.addAttribute("fullName", fullName);
		model.addAttribute("avatar", avatar);
		//Lấy ds project theo từng loại người dùng
		if (request.isUserInRole("ROLE_MANAGER")) {
			model.addAttribute("projects", projectService.findByManagerId(userId));
		} else if (request.isUserInRole("ROLE_ADMIN")) {
			model.addAttribute("projects", projectService.findAll());
		}
		if (session.getAttribute("msgSuccess") != null) {
			model.addAttribute("msgSuccess", session.getAttribute("msgSuccess").toString());
			session.removeAttribute("msgSuccess");
		} else if (session.getAttribute("msgError") != null) {
			model.addAttribute("msgError", session.getAttribute("msgError").toString());
			session.removeAttribute("msgError");
		}
		return TemplateConstants.TEMPLATE_PROJECT;
	}

	@GetMapping(value = { "add" })
	public String add(Model model) {
		//Lấy ds thông tinh người dùng
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("fullName", fullName);
		model.addAttribute("avatar", avatar);
		model.addAttribute("project", new ProjectDto());
		return TemplateConstants.TEMPLATE_PROJECT_ADD;
	}

	@PostMapping(value = { "add" })
	public String add(@Validated @ModelAttribute("project") ProjectDto dto, BindingResult result, HttpSession session,
			Model model) {
		//Lấy ds thông tinh người dùng
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		int userId = ((CustomUserDetail) principal).getId();
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("fullName", fullName);
		model.addAttribute("avatar", avatar);
		dto.setUserId(userId);
		//Kiểm tra xem dữ post lên và xử lí
		if (result.hasErrors()) {
			return TemplateConstants.TEMPLATE_PROJECT_ADD;
		} else if (projectService.add(dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_PROJECT_ADD_SUCCESS);
			return "redirect:/project";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_PROJECT_ADD_FAIL);
		session.removeAttribute("msgError");
		return TemplateConstants.TEMPLATE_PROJECT_ADD;
	}

	@GetMapping(value = { "delete" })
	public String delete(@RequestParam("id") int id, HttpSession session) {
		if (projectService.deleteById(id) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_PROJECT_DELETE_SUCCESS);
			return "redirect:/project";
		}
		session.setAttribute("msgError", MessageConstants.MESSAGE_ROLE_DELETE_FAIL);
		return "redirect:/project";
	}

	@GetMapping(value = { "edit" })
	public String edit(@RequestParam("id") int id, Model model) {
		//Lấy ds thông tinh người dùng
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("fullName", fullName);
		model.addAttribute("avatar", avatar);
		model.addAttribute("project", projectService.findById(id));
		return TemplateConstants.TEMPLATE_PROJECT_EDIT;
	}

	@PostMapping(value = { "edit" })
	public String edit(@Validated @ModelAttribute("project") ProjectDto dto, BindingResult result, HttpSession session,
			Model model) {
		//Lấy ds thông tinh người dùng
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		int userId = ((CustomUserDetail) principal).getId();
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("fullName", fullName);
		model.addAttribute("avatar", avatar);
		dto.setUserId(userId);
		//Kiểm tra xem dữ post lên và xử lí
		if (result.hasErrors()) {
			model.addAttribute("project", projectService.findById(dto.getId()));
			model.addAttribute("msgError", MessageConstants.MESSAGE_PROJECT_EDIT_FAIL);
			return TemplateConstants.TEMPLATE_PROJECT_EDIT;
		} else if (projectService.edit(dto.getId(), dto) == 0) {
			session.setAttribute("msgSuccess", MessageConstants.MESSAGE_PROJECT_EDIT_SUCCESS);
			return "redirect:/project";
		}
		model.addAttribute("msgError", MessageConstants.MESSAGE_PROJECT_EDIT_FAIL);
		session.removeAttribute("msgError");
		return TemplateConstants.TEMPLATE_PROJECT_EDIT;
	}

	@GetMapping(value = { "detail" })
	public String detail(@RequestParam("id") int id, Model model) {
		//Lấy ds thông tinh người dùng
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String fullName = ((CustomUserDetail) principal).getFullName();
		String avatar = "/upload/" + ((CustomUserDetail) principal).getAvatar();
		model.addAttribute("fullName", fullName);
		// Tính toán lấy thông số thông kê và danh sách cộng việc của một project (ds
		// công việc được phân loại theo trạng thái và người dùng
		model.addAttribute("statisticalNumbers", taskService.countStatusTaskInProject(id));
		model.addAttribute("allTaskInProject", taskService.getAllTaskInProject(id));
		model.addAttribute("avatar", avatar);
		return TemplateConstants.TEMPLATE_PROJECT_DETAIL;
	}
}
