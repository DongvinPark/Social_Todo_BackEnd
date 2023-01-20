package com.example.socialtodobackend.configuration;

import com.example.socialtodobackend.utils.AWSSecretValues;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaPropertyConfig {

    public static Properties getProducerProperties(){
        Properties producerProperty = new Properties();
        producerProperty.put("bootstrap.servers", AWSSecretValues.AMAZON_MSK_BOOTSTRAP_SERVERS);
        producerProperty.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperty.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        log.info("프로듀서 프로퍼티 설정 완료");
        return producerProperty;
    }



    public static Properties getSupportConsumerProperties(){
        Properties consumerProperty = new Properties();
        consumerProperty.put("bootstrap.servers", AWSSecretValues.AMAZON_MSK_BOOTSTRAP_SERVERS);
        consumerProperty.put("group.id","peter-consumer");
        consumerProperty.put("enable.auto.commit","true");
        consumerProperty.put("auto.offset.reset","latest");
        consumerProperty.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperty.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");

        log.info("응원 컨슈머 프로퍼티 설정 완료");
        return consumerProperty;
    }


}
