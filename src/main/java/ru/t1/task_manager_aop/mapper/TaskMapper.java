package ru.t1.task_manager_aop.mapper;

import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.enums.TaskStatus;
import ru.t1.task_manager_aop.model.Task;

public class TaskMapper {

    public static Task requestToTask(TaskRequestDto taskRequestDto) {
        return Task.builder()
                .userId(taskRequestDto.userId())
                .title(taskRequestDto.title())
                .description(taskRequestDto.description())
                .status(TaskStatus.CREATED)
                .build();
    }

    public static TaskDto taskToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }
}
