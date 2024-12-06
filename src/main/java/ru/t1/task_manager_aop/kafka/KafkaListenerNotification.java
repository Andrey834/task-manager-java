package ru.t1.task_manager_aop.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.service.NotificationService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerNotification {
    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topics.updateTask}", groupId = "${spring.application.name}")
    public void listener(TaskDto taskDto, Acknowledgment ack) {
        notificationService.sendEmail(taskDto);
        log.info("Received message [{}]", taskDto);
    }
}
