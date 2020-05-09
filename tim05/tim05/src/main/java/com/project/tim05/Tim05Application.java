package com.project.tim05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Tim05Application {

	public static void main(String[] args) {
		System.setProperty("java.runtime.version", "1.8");
		SpringApplication.run(Tim05Application.class, args);
	}

}
