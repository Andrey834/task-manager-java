package ru.t1.task_manager_aop.dto;

public record TaskRequestDto(
        Long userId,
        String title,
        String description
) {
}
