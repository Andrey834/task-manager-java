package ru.t1.task_manager_aop.service;

import org.springframework.data.domain.Pageable;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.dto.TaskUpdateDto;

public interface TaskService {

    TaskDto createTask(TaskRequestDto taskRequestDto);

    void updateTask(Long taskId, TaskUpdateDto taskUpdateDto);

    TaskDto getTaskById(Long id);

    Iterable<TaskDto> getAllTask(Pageable pageable);

    void deleteTask(Long id);
}
