package com.myclass.constant;

import org.springframework.stereotype.Component;

//Link template của các trang
@Component
public class TemplateConstants {

	// Home Template
	public static final String TEMPLATE_HOME = "home/index";

	// Role Template
	public static final String TEMPLATE_ROLE = "role/index";
	public static final String TEMPLATE_ROLE_ADD = "role/add";
	public static final String TEMPLATE_ROLE_DELETE = "role/del";
	public static final String TEMPLATE_ROLE_EDIT = "role/edit";
	
	//User Template
	public static final String TEMPLATE_USER = "user/index";
	public static final String TEMPLATE_USER_ADD = "user/add";
	public static final String TEMPLATE_USER_EDIT = "user/edit";
	public static final String TEMPLATE_USER_DELETE = "user/delete";
	
	//Project Template
	public static final String TEMPLATE_PROJECT = "project/index";
	public static final String TEMPLATE_PROJECT_ADD = "project/add";
	public static final String TEMPLATE_PROJECT_EDIT = "project/edit";
	public static final String TEMPLATE_PROJECT_DETAIL = "project/detail";
	public static final String TEMPLATE_PROJECT_DELETE = "project/delete";
	
	//Task Template
	public static final String TEMPLATE_TASK = "task/index";
	public static final String TEMPLATE_TASK_ADD = "task/add";
	public static final String TEMPLATE_TASK_EDIT = "task/edit";
	public static final String TEMPLATE_TASK_DETAIL = "task/detail";
	public static final String TEMPLATE_TASK_DELETE = "task/delete";
	public static final String TEMPLATE_TASK_PROFILE = "task/profile";
	public static final String TEMPLATE_TASK_STATISTIC = "task/statistic";
	public static final String TEMPLATE_TASK_UPDATE_STATUS = "task/update-status";
	
	//Login Template
	public static final String TEMPLATE_LOGIN = "login/index";
	
	//Error Template
	public static final String TEMPLATE_ERROR_403= "error/403";
}
