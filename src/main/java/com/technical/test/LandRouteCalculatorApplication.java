package com.technical.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages = "com.technical.test")
@Configuration
public class LandRouteCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LandRouteCalculatorApplication.class, args);
	}

}
