package com.example.projectPi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Note: Swagger UI is handled automatically by springdoc-openapi-starter-webmvc-ui
 * No manual resource handling needed for Swagger
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // Swagger UI is automatically configured by springdoc-openapi-starter-webmvc-ui
    // Available at: /streetleague/swagger-ui.html
    // API Docs JSON at: /streetleague/v3/api-docs
}
