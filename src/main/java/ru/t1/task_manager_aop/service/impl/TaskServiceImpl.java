package ru.t1.task_manager_aop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.task_manager_aop.annotation.LogReturningObject;
import ru.t1.task_manager_aop.annotation.TimeTracking;
import ru.t1.task_manager_aop.annotation.ValidationTask;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.dto.TaskUpdateDto;
import ru.t1.task_manager_aop.mapper.TaskMapper;
import ru.t1.task_manager_aop.model.Task;
import ru.t1.task_manager_aop.repository.TaskDao;
import ru.t1.task_manager_aop.service.TaskService;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskDao taskDao;

    @Override
    @ValidationTask
    @TimeTracking
    @LogReturningObject
    public TaskDto createTask(TaskRequestDto taskRequestDto) {
        final Task task = TaskMapper.requestToTask(taskRequestDto);
        final Task newTask = taskDao.save(task);
        return TaskMapper.taskToDto(newTask);
    }

    @Override
    @TimeTracking
    public void updateTask(Long taskId, TaskUpdateDto taskUpdateDto) {
        Task task = getTask(taskId);
        task.setUserId(taskUpdateDto.userId());
        task.setTitle(taskUpdateDto.title());
        task.setDescription(taskUpdateDto.description());
    }

    @Override
    @Transactional(readOnly = true)
    @TimeTracking
    @LogReturningObject
    public TaskDto getTaskById(Long id) {
        final Task task = getTask(id);
        return TaskMapper.taskToDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    @TimeTracking
    @LogReturningObject
    public Iterable<TaskDto> getAllTask(Pageable pageable) {
        return taskDao.findAll(pageable)
                .map(TaskMapper::taskToDto)
                .toList();
    }

    @Override
    @TimeTracking
    public void deleteTask(Long id) {
        Task task = getTask(id);
        taskDao.delete(task);
    }

    private Task getTask(Long id) {
        return taskDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID: " + id + " not found"));
    }
}