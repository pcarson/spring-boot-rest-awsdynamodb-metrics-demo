package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
/**
 * NB If this test is run as part of a maven build then local dynamo will be provided by maven.
 * To run this test standalone, a running localstack instance is required.
 */
class DemoApplicationIntegrationTest {

	@Test
	void contextLoads() {
	}

}
