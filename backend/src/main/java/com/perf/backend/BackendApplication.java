package com.perf.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.perf.backend.mapper")
public class BackendApplication {

	public static void main(String[] args) {
		System.out.println("OPENROUTER_API_KEY = " + System.getenv("OPENROUTER_API_KEY"));
		SpringApplication.run(BackendApplication.class, args);
	}

}
