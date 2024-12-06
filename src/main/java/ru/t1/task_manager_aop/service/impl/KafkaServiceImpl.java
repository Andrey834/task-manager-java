package ru.t1.task_manager_aop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.t1.task_manager_aop.service.KafkaService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendMessage(String topic, Object message) {
        CompletableFuture<SendResult<String, Object>> completableFuture = kafkaTemplate
                .send(topic, message);

        completableFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Send message={}, with offset={}", message, result.getRecordMetadata().offset());
            } else {
                log.info("Сan't send a message=,{}, reason : {}", message, ex.getMessage());
            }
        });
    }
}
