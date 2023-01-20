package com.example.socialtodobackend.kafka.consumer;

import com.example.socialtodobackend.configuration.KafkaPropertyConfig;
import com.example.socialtodobackend.utils.AWSSecretValues;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;

@Slf4j
public class KafkaSingletonConsumer {
    //컨슈머의 토픽 구독 설정을 여기서 추가해줘야 한다.
    private static final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
        KafkaPropertyConfig.getSupportConsumerProperties()
    );


    public static KafkaConsumer<String, String> getSupportConsumer(){
        consumer.subscribe(Arrays.asList(AWSSecretValues.MSK_SUPPORT_TOPIC));
        return consumer;
    }


}
