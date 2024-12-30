package ru.t1.task_manager_aop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.dto.TaskUpdateDto;
import ru.t1.task_manager_aop.mapper.TaskMapper;
import ru.t1.task_manager_aop.model.Task;
import ru.t1.task_manager_aop.repository.TaskRepository;
import ru.t1.task_manager_aop.service.KafkaService;
import ru.t1.task_manager_aop.service.TaskService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final KafkaService kafkaService;
    @Value(value = "${app.kafka.topics.updateTask}")
    private String updateTopic;

    @Override
    public TaskDto createTask(TaskRequestDto taskRequestDto) {
        final Task task = TaskMapper.requestToTask(taskRequestDto);
        final Task newTask = taskRepository.save(task);
        return TaskMapper.taskToDto(newTask);
    }

    @Override
    public TaskDto updateTask(Long taskId, TaskUpdateDto taskUpdateDto) {
        Task task = getTask(taskId);
        task.setUserId(taskUpdateDto.userId());
        task.setTitle(taskUpdateDto.title());
        task.setDescription(taskUpdateDto.description());

        if (!task.getStatus().equals(taskUpdateDto.status())) {
            task.setStatus(taskUpdateDto.status());

            kafkaService.sendMessage(updateTopic, TaskMapper.taskToDto(task));
        }

        return TaskMapper.taskToDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        final Task task = getTask(id);
        return TaskMapper.taskToDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTask(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(TaskMapper::taskToDto)
                .toList();
    }

    @Override
    public void deleteTask(Long id) {
        Task task = getTask(id);
        taskRepository.delete(task);
    }

    private Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID: " + id + " not found"));
    }
}
