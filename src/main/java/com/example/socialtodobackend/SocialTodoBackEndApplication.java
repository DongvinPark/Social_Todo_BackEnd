package com.example.socialtodobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SocialTodoBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialTodoBackEndApplication.class, args);
	}

}
