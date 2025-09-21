package com.backend.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ServiceApplication {

	public static void main(String[] args) {
		System.out.println("HELLO WORLD" );
		SpringApplication.run(ServiceApplication.class, args);
		System.out.println("WHO ARE U?" );
	}

}
