package com.tweeter.app.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/****
 * This is created for swagger2 configuration and this is turned off in PROD based on environment variable
 *
 * @author kumas12
 *
 */
@EnableSwagger2
public class SwaggerConfig {
	private final String PROD_ENV_NAME = "prod";
	
	@Autowired
	Environment env;

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
        		.apiInfo(apiInfo())
                .enable(!StringUtils.equalsIgnoreCase(env.getProperty("com.tweeter.app.env"), PROD_ENV_NAME))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();    
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Tweeter Service")
            .description("This is a twitter inspired service that uses Sqlite Db for persisting data and does some very basic operations for posting messages and follow/unfollow users.")
            .version("1.0.0")
            .termsOfServiceUrl("http://swagger.io/terms/")
            .license("Apache 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
            .contact(new Contact("Kumar", "www.sathishkumar.com", "2contactsathish@gmail.com"))
            .build();
    }

}