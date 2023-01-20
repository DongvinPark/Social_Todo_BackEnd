package com.example.socialtodobackend.kafka;

import com.example.socialtodobackend.kafka.consumer.KafkaSingletonConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

@Slf4j
@RequiredArgsConstructor
public class KafkaListener {

    public static void listenSupportMessage(){
        KafkaConsumer<String, String> consumer = KafkaSingletonConsumer.getSupportConsumer();

        try{
            log.info("메시지 확인 후 컨슘 시작");
            while(true){
                ConsumerRecords<String, String> records = consumer.poll(100);
                for(ConsumerRecord<String, String> record : records){
                    log.info("스태틱 메시지 컨테이너 큐에 메시지 삽입. 메시지 값 : " + record.value());

                    StaticMessageContainer.staticMessageQueue.addLast(record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }


}
