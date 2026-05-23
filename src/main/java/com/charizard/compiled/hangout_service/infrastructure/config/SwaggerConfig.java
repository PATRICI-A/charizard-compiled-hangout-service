package com.charizard.compiled.hangout_service.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para la documentación interactiva del microservicio.
 * Expone la API en {@code /swagger-ui/index.html} con esquema de seguridad JWT.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI hangoutServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hangout Service API")
                        .version("v1.0.0")
                        .description("""
                Microservice responsible for managing hangouts (parches) and invitations \
                within the Charizard platform. Handles the full lifecycle of a hangout: \
                creation, update, archiving, dissolution, and member management, \
                including invitation flows and capacity enforcement.

                **Authentication:** The API Gateway propagates the `X-User-Id` header \
                with the authenticated user's ID. All endpoints require a valid \
                **JWT Bearer token** in the `Authorization` header.

                **Roles supported:** `STUDENT` · `ADMIN` — \
                Students can create and join hangouts, send and respond to invitations, \
                and manage their own participation. Admins have elevated permissions \
                to oversee, archive, and dissolve any hangout regardless of ownership.

                **Hangout lifecycle:** A hangout transitions through the following states: \
                active → archived → dissolved. Only the captain (creator) or an Admin \
                can trigger state transitions. Members may leave at any time while \
                the hangout remains active.

                **Invitation flow:** The captain can invite Students to a hangout. \
                Invited users receive a pending invitation and may accept or reject it. \
                Duplicate invitations and invitations to already-active members are rejected. \
                Capacity limits are enforced at both invitation and join time.

                **Internal endpoints:** Routes under `/api/v1/internal` are consumed \
                exclusively by other microservices within the platform and are not \
                exposed through the public API gateway.
                """)
                        .contact(new Contact()
                                .name("Charizard Team — Escuela Colombiana de Ingeniería Julio Garavito")
                                .email("contact@charizard.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el JWT token generado. No incluyas 'Bearer ', Swagger lo agrega automáticamente.")));
    }
}
