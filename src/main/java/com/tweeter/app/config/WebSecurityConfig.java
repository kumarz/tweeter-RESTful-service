package com.tweeter.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.tweeter.app.security.MyBasicAuthenticationEntryPoint;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
   @Override
   protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.inMemoryAuthentication()
      .withUser("admin").password("tweeter123").roles("ADMIN").and();
      //.withUser("mrkumar").password("tweeter123").roles("USER");
   }

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().authorizeRequests().antMatchers("/tweeter/**").hasRole("ADMIN")
      .and().httpBasic();
	    http.httpBasic().and()
	      .authorizeRequests()
	        .antMatchers(HttpMethod.GET, "/**").hasRole("ADMIN");
	        //.antMatchers(HttpMethod.POST, "/tweeter/**").hasRole("ADMIN");
   }
   
	/* To allow Pre-flight [OPTIONS] request from browser */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}
}