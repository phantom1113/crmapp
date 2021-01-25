package com.myclass.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {
	private int id;
	
	@NotEmpty(message = "Name không được bỏ trống")
	private String name;
	
	@NotEmpty(message = "Description không được bỏ trống")
	private String desc;
	
	public RoleDto(int id, String name, String desc) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
	}
}
