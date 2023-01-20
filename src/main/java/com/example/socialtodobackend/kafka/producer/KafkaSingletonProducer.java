package com.example.socialtodobackend.kafka.producer;

import com.example.socialtodobackend.configuration.KafkaPropertyConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

@Slf4j
public class KafkaSingletonProducer {

    private static final KafkaProducer<String, String> supportProducer = new KafkaProducer<>(
        KafkaPropertyConfig.getProducerProperties()
    );

    private static final KafkaProducer<String, String> nagProducer = new KafkaProducer<>(
        KafkaPropertyConfig.getProducerProperties()
    );



    public static Producer<String, String> getSupportProducer(){
        return supportProducer;
    }



    public static Producer<String, String> getNagProducer(){
        return nagProducer;
    }

}