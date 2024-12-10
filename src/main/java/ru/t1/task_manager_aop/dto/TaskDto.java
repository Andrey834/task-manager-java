package ru.t1.task_manager_aop.dto;

import lombok.Builder;
import ru.t1.task_manager_aop.enums.TaskStatus;

@Builder
public record TaskDto(
        Long id,
        Long userId,
        String title,
        String description,
        TaskStatus status
) {
}
