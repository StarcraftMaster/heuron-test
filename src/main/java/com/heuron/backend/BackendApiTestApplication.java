package com.heuron.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BackendApiTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApiTestApplication.class, args);
	}

}
