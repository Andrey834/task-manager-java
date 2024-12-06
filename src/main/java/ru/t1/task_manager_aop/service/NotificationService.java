package ru.t1.task_manager_aop.service;

import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.model.Task;

public interface NotificationService {

    void sendEmail(TaskDto taskDto);
}
