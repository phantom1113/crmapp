package com.myclass.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@ComponentScan("com.myclass")
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsService userDetailService;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.authorizeRequests()
		.antMatchers("/file/upload", "/account/login").permitAll()
		.antMatchers("/role/**").hasAnyRole("ADMIN")
		.antMatchers("/user/add","/user/delete","/user/edit").hasAnyRole("ADMIN")
		.antMatchers("/user/**").hasAnyRole("ADMIN","MANAGER")
		.antMatchers("/project/edit**","/project/delete**","/project/add**").hasAnyRole("MANAGER")
		.antMatchers("/project/**").hasAnyRole("ADMIN","MANAGER")
		.antMatchers("/task/edit**","/task/delete**","/task/add**").hasAnyRole("MANAGER")
		.antMatchers("/task/**").hasAnyRole("MANAGER","USER")
		.antMatchers("/**").authenticated()
		.and().formLogin()
		.loginPage("/account/login")
		.usernameParameter("email")
		.passwordParameter("password")
		.defaultSuccessUrl("/")
		.failureUrl("/account/login?error=fail_login")
		.and().logout()
		.logoutUrl("/logout")
		.clearAuthentication(true)
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID")
		.logoutSuccessUrl("/")
		.and().exceptionHandling()
		.accessDeniedPage("/error/403");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
				"/configuration/security", "/swagger-ui.html", "/webjars/**");
	}
}
