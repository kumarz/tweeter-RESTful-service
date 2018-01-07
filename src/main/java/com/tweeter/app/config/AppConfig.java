package com.tweeter.app.config;

import javax.annotation.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = { "com.tweeter.app.service", 
								"com.tweeter.app.service.impl",
								"com.tweeter.app.config", 
								"com.tweeter.app.controller", 
								"com.tweeter.app.model" })

@PropertySource(value = { "classpath:com/tweeter/app/service/${com.tweeter.app.env}.properties" })

@Import(value = { })

@Priority(value = 1)
public class AppConfig {

	@Autowired
	Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfig() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
