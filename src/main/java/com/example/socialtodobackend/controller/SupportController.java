package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.kafka.producer.KafkaSingletonProducer;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.SupportService;
import com.example.socialtodobackend.utils.AWSSecretValues;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SupportController {

    private final SupportService supportService;
    private final AlarmService alarmService;


    @PutMapping("/create/support")
    public void pressSupport(
        @AuthenticationPrincipal Long supportSentUserPKId,
        @RequestParam Long publicTodoPKId,
        @RequestParam Long todoAuthorUserPKId
    ){
        //응원 누르기 처리 이벤트를 카프카 이벤트로 프로듀스 한다.
        String supportMessage = CommonUtils.KAFKA_MESSAGE_TYPE_SUPPORT + "," + supportSentUserPKId + "," + publicTodoPKId + "," + todoAuthorUserPKId;

        Producer<String, String> supportProducer = KafkaSingletonProducer.getSupportProducer();
        supportProducer.send( new ProducerRecord<>(AWSSecretValues.MSK_SUPPORT_TOPIC, supportMessage));

        log.info("토픽에 메시지 보내기 완료.");
    }


}