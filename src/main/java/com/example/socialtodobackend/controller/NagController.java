package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.dto.APIDataResponse;
import com.example.socialtodobackend.dto.user.UserDto;
import com.example.socialtodobackend.kafka.producer.KafkaSingletonProducer;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.NagService;
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
public class NagController {

    private final NagService nagService;
    private final AlarmService alarmService;


    @PutMapping("/create/nag")
    public void pressNag(
        @AuthenticationPrincipal Long nagSentUserPKId,
        @RequestParam Long publicTodoPKId,
        @RequestParam Long todoAuthorUserPKId
    ){
        //잔소리 누르기 처리 이벤트를 카프카 이벤트로 프로듀스 한다.
        String nagMessage = CommonUtils.KAFKA_MESSAGE_TYPE_NAG + "," + nagSentUserPKId + "," + publicTodoPKId + "," + todoAuthorUserPKId;

        Producer<String, String> nagProducer = KafkaSingletonProducer.getNagProducer();
        nagProducer.send( new ProducerRecord<>(AWSSecretValues.MSK_NAG_TOPIC, nagMessage));

        log.info("토픽에 메시지 보내기 완료.");
    }


}
