package ru.t1.task_manager_aop.dto;

import ru.t1.task_manager_aop.enums.TaskStatus;

public record TaskUpdateDto(
        Long userId,
        String title,
        String description,
        TaskStatus status
) {
}
