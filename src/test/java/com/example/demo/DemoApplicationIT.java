package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
/**
 * NB a copy of dynamo local has to be running to run this test.
 * the maven build will trigger its own copy - otherwise use localstack.
 */
class DemoApplicationIT {

	@Test
	void contextLoads() {
	}

}
