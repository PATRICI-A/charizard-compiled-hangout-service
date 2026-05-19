package com.charizard.compiled.hangout_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal del microservicio de Parches (Hangout Service).
 * Inicializa el contexto de Spring Boot y habilita la programación
 * de tareas (@EnableScheduling) para el archivado automático de parches,
 * y los clientes Feign para integración con microservicios externos.
 */
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class HangoutServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HangoutServiceApplication.class, args);
	}

}
