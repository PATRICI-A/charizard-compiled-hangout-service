package com.charizard.compiled.hangout_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HangoutServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HangoutServiceApplication.class, args);
	}

}
