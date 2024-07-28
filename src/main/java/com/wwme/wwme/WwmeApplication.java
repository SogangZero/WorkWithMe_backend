package com.wwme.wwme;

import jakarta.annotation.PostConstruct;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class WwmeApplication {

	@PostConstruct
	public void timeSet() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(WwmeApplication.class, args);
	}

}
