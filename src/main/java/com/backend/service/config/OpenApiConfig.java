package com.backend.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI projectDiamondOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Diamond API")
                        .description("API documentation for Project Diamond Backend Service")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Project Diamond Team")
                                .email("contact@projectdiamond.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
