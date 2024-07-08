package com.wwme.wwme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WwmeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WwmeApplication.class, args);
	}

}
