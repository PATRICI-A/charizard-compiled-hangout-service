package com.charizard.compiled.hangout_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI hangoutServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hangout Service API")
                        .description("API for managing patches (hangouts) and invitations")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Charizard Team")
                                .email("contact@charizard.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
