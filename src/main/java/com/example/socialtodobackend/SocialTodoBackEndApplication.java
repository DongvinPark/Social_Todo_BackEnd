package com.example.socialtodobackend;

import com.example.socialtodobackend.kafka.KafkaListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocialTodoBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialTodoBackEndApplication.class, args);

		KafkaListeners.listenSupportMessage();
	}

}
