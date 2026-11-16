package com.consentflow.api;

import org.springframework.boot.SpringApplication;

public class TestConsentflowApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsentflowApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
