package ru.t1.task_manager_aop.service;

import ru.t1.task_manager_aop.dto.TaskDto;

public interface NotificationService {

    void sendEmail(TaskDto taskDto);
}
