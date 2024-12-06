package ru.t1.task_manager_aop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender emailSender;

    @Override
    public void sendEmail(TaskDto taskDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("parent-romashka@yandex.ru");
        message.setTo("andry834@yandex.ru");
        message.setSubject("UPDATE STATUS TASK");
        message.setText(String.format("Статус задачи №%d обновился -> %s", taskDto.id(), taskDto.status()));

        try {
            emailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }
}
