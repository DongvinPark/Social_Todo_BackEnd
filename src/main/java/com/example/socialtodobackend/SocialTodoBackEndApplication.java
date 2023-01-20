package com.example.socialtodobackend;

import com.example.socialtodobackend.kafka.KafkaListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocialTodoBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialTodoBackEndApplication.class, args);

		//컨트롤러 단의 프로듀서에서 보내는 메시지들을 스프링 부트 앱의 시작 후 무한 루프를 돌면서 리스닝 함.
		KafkaListener.listenSupportMessage();
	}

}