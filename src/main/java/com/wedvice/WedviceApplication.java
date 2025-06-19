package com.wedvice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WedviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WedviceApplication.class, args);
	}

}
