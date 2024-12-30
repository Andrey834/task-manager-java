package ru.t1.task_manager_aop.service;

import org.springframework.data.domain.Pageable;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.dto.TaskUpdateDto;

import java.util.List;

public interface TaskService {

    TaskDto createTask(TaskRequestDto taskRequestDto);

    TaskDto updateTask(Long taskId, TaskUpdateDto taskUpdateDto);

    TaskDto getTaskById(Long id);

    List<TaskDto> getAllTask(Pageable pageable);

    void deleteTask(Long id);
}
