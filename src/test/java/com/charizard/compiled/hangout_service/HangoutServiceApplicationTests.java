package com.charizard.compiled.hangout_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class HangoutServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
