package ru.t1.task_manager_aop.dto;

public record TaskUpdateDto(
        Long userId,
        String title,
        String description
) {
}
