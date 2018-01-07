package com.tweeter.app.config;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tweeter.app.dao.TweeterDao;
import com.tweeter.app.dao.impl.TweeterDaoImpl;
import com.tweeter.app.service.TweeterService;
import com.tweeter.app.service.impl.TweeterServiceImpl;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.tweeter.app.controller",
								"com.tweeter.app.config"})
@Import(value = { SwaggerConfig.class})
public class WebMVCConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	public TweeterDao tweeterDao() throws UnknownHostException{
		return new TweeterDaoImpl();
	}
	
	@Bean
	public TweeterService tweeterService(){
		return new TweeterServiceImpl();
	}

	@Bean
	public SwaggerConfig swaggerConfig(){
		return new SwaggerConfig();
	}

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
	}

}
