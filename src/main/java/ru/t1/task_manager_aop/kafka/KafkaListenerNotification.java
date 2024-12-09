package ru.t1.task_manager_aop.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.service.NotificationService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerNotification {
    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topics.updateTask}", groupId = "${spring.application.name}")
    @RetryableTopic(backoff = @Backoff(delay = 2000, maxDelay = 10000, multiplier = 2))
    public void listener(TaskDto taskDto) {
        notificationService.sendEmail(taskDto);
        log.info("Received message [{}]", taskDto);
    }
}
