package com.charizard.compiled.hangout_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hangout Service API")
                        .version("1.0.0")
                        .description("Microservicio de gestión de parches para EciBuddy")
                        .contact(new Contact()
                                .name("Charizard Compiled")));
    }
}
