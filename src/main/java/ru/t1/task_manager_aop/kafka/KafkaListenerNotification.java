package ru.t1.task_manager_aop.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.service.NotificationService;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerNotification {
    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topics.updateTask}", groupId = "${spring.application.name}")
    @RetryableTopic(backoff = @Backoff(delay = 2000, maxDelay = 10000, multiplier = 2))
    public void listener(@Payload List<TaskDto> messages, Acknowledgment ack) {
        log.info("Consumer started data processing");
        try {
            log.debug("Received message {}", messages);
            messages.forEach(notificationService::sendEmail);
        } finally {
            ack.acknowledge();
        }
        log.info("Consumer finished data processing");
    }
}
