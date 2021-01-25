package com.myclass.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.myclass.dto.UserDto;
import com.myclass.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	//Tìm user theo email
	@Query("SELECT new com.myclass.dto.UserDto(u.id, u.email, u.password, u.fullName, u.avatar, r.name) FROM User u JOIN u.role r WHERE email= :email")
	public UserDto findByEmail(@Param("email") String email);
	
	//Tìm danh sách user theo role name
	@Query("SELECT new com.myclass.dto.UserDto(u.id, u.email, u.password, u.fullName, u.avatar, r.name) FROM User u JOIN u.role r WHERE r.name= :roleName")
	public List<UserDto> findByRole(@Param("roleName") String roleName);
}
