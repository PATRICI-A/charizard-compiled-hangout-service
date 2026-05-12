package com.charizard.compiled.hangout_service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuración que habilita la ejecución asíncrona de métodos
 * con la anotación {@link org.springframework.scheduling.annotation.Async}.
 * Utilizada por los event listeners para procesar eventos de dominio
 * sin bloquear el flujo principal.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
