package com.example.projectPi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Street League API")
                        .description("API documentation for Street League - Sports Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Street League Team")
                                .email("contact@streetleague.com")));
    }
}
