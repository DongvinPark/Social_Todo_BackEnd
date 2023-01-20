package com.example.socialtodobackend.web.controller;

import com.example.socialtodobackend.exception.SingletonException;
import com.example.socialtodobackend.kafka.StaticMessageContainer;
import com.example.socialtodobackend.service.AlarmService;
import com.example.socialtodobackend.service.NagService;
import com.example.socialtodobackend.service.SupportService;
import com.example.socialtodobackend.utils.CommonUtils;
import java.util.LinkedList;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {

    private final AlarmService alarmService;
    private final SupportService supportService;
    private final NagService nagService;



    //구분해야 한다. 쉽표로 파싱한 문자열 배열이 첫 인덱스 문자열이 응원인지, 잔소리인지.
    @PutMapping("/consume-support-message")
    public void consumeSupportMessage(){

        LinkedList<String> messageQueue = StaticMessageContainer.staticMessageQueue;

        while(true){
            if(!messageQueue.isEmpty()){

                String message = messageQueue.pollFirst();

                String[] valueArray = Objects.requireNonNull(
                    message
                ).split(",");

                if(valueArray.length!=4){
                    throw SingletonException.INVALID_KAFKA_MESSAGE;
                }

                String messageType = valueArray[0];
                Long supportOrNagSentUserPKId = Long.parseLong(valueArray[1]);
                Long publicTodoPKId = Long.parseLong(valueArray[2]);
                Long todoAuthorUserPKId = Long.parseLong(valueArray[3]);

                if(messageType.equals(CommonUtils.KAFKA_MESSAGE_TYPE_SUPPORT)){

                    // 이 두 가지가 바로 응원 메시지 컨슈머에서 메시지별로 처리해 줘야 하는 일들이다.
                    supportService.addSupport(supportOrNagSentUserPKId, publicTodoPKId);
                    alarmService.sendSupportInfoAlarm(supportOrNagSentUserPKId, publicTodoPKId, todoAuthorUserPKId);
                    log.info("응원 메시지 큐 처리 작업 완료.");

                } else if (messageType.equals(CommonUtils.KAFKA_MESSAGE_TYPE_NAG)) {

                    //이 두 가지가 바로 nagConsumer에서 메시지별로 처리 해줘야 하는 일들이다.
                    nagService.addNag(supportOrNagSentUserPKId, publicTodoPKId);
                    alarmService.sendNagInfoAlarm(supportOrNagSentUserPKId, publicTodoPKId, todoAuthorUserPKId);
                    log.info("잔소리 메시지 큐 처리 작업 완료.");

                } else {
                    throw SingletonException.INVALID_KAFKA_MESSAGE;
                }

            }
        }//while
    }


}
