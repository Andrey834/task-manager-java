package ru.t1.task_manager_aop.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.dto.TaskUpdateDto;
import ru.t1.task_manager_aop.enums.TaskStatus;
import ru.t1.task_manager_aop.model.Task;
import ru.t1.task_manager_aop.repository.TaskRepository;
import ru.t1.task_manager_aop.service.KafkaService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    private static final String UPDATE_TOPIC = "update-task-status";

    @Mock
    private TaskRepository repository;
    @Mock
    private KafkaService kafkaService;
    @InjectMocks
    private TaskServiceImpl taskService;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskService, "updateTopic", UPDATE_TOPIC);

        Long taskId1 = 1L;
        Long userId1 = 1L;
        String testTitle1 = "testTitle1";
        String testDescription1 = "testDescription1";
        TaskStatus status1 = TaskStatus.CREATED;
        task1 = new Task(taskId1, userId1, testTitle1, testDescription1, status1);

        Long taskId2 = 2L;
        Long userId2 = 2L;
        String testTitle2 = "testTitle2";
        String testDescription2 = "testDescription2";
        TaskStatus status2 = TaskStatus.CREATED;
        task2 = new Task(taskId2, userId2, testTitle2, testDescription2, status2);
    }

    @Test
    @DisplayName("createTask(TaskRequestDto) Тест на успешное добавление новой задачи")
    void createTask() {
        when(repository.save(any())).thenReturn(task1);

        TaskRequestDto taskRequestDto = new TaskRequestDto(task1.getUserId(), task1.getTitle(), task1.getDescription());
        TaskDto actualTaskDto = taskService.createTask(taskRequestDto);

        assertEquals(task1.getId(), actualTaskDto.id());
        assertEquals(task1.getUserId(), actualTaskDto.userId());
        assertEquals(task1.getTitle(), actualTaskDto.title());
        assertEquals(task1.getDescription(), actualTaskDto.description());
        assertEquals(task1.getStatus(), actualTaskDto.status());
    }

    @Test
    @DisplayName("updateTask(Long, TaskUpdateDto) Тест на успешное обновление задачи")
    void updateTask() {
        when(repository.findById(task2.getId())).thenReturn(Optional.of(task2));

        Long taskId = task2.getId();
        Long userId = task2.getUserId();
        String updatedTitle = "updatedTitle";
        String updatedDescription = "updatedDescription";
        TaskStatus status = TaskStatus.IN_PROGRESS;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(userId, updatedTitle, updatedDescription, status);
        TaskDto actualTaskDto = taskService.updateTask(taskId, taskUpdateDto);

        assertEquals(taskId, actualTaskDto.id());
        assertEquals(userId, actualTaskDto.userId());
        assertEquals(updatedTitle, actualTaskDto.title());
        assertEquals(updatedDescription, actualTaskDto.description());
        assertEquals(status, actualTaskDto.status());
    }

    @ParameterizedTest(name = "{0} -> {1}: событие в Kafka отправлено = {2}")
    @CsvSource({
            "CREATED, IN_PROGRESS, true",
            "IN_PROGRESS, COMPLETED, true",
            "IN_PROGRESS, REJECTED, true",
            "CREATED, CREATED, false",
            "COMPLETED, COMPLETED, false",
    })
    @DisplayName("updateTask(Long, TaskUpdateDto) публикует событие в Kafka только при реальной смене статуса")
    void updateTask_publishesKafkaEventOnlyOnStatusChange(TaskStatus currentStatus,
                                                            TaskStatus newStatus,
                                                            boolean shouldSendEvent) {
        task2.setStatus(currentStatus);
        when(repository.findById(task2.getId())).thenReturn(Optional.of(task2));

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(
                task2.getUserId(), task2.getTitle(), task2.getDescription(), newStatus);

        taskService.updateTask(task2.getId(), taskUpdateDto);

        if (shouldSendEvent) {
            verify(kafkaService, times(1)).sendMessage(eq(UPDATE_TOPIC), any(TaskDto.class));
        } else {
            verify(kafkaService, never()).sendMessage(any(), any());
        }
    }

    @Test
    @DisplayName("getTaskById(Long) Тест на успешное получение задачи")
    void getTaskById() {
        when(repository.findById(task1.getId())).thenReturn(Optional.of(task1));

        TaskDto actualTaskDto = taskService.getTaskById(task1.getId());

        assertEquals(task1.getId(), actualTaskDto.id());
        assertEquals(task1.getUserId(), actualTaskDto.userId());
        assertEquals(task1.getTitle(), actualTaskDto.title());
        assertEquals(task1.getDescription(), actualTaskDto.description());
        assertEquals(task1.getStatus(), actualTaskDto.status());
    }

    @Test
    @DisplayName("getTaskById(Long) Тест на получение ошибки из-за отсутствующей задачи с указанным ID")
    void getTaskById_whenInvalidTaskId_thenHasException() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskService.getTaskById(task1.getId());
        });

    }

    @Test
    @DisplayName("getAllTask(Pageable) Тест на получение всех задач")
    void getAllTask() {
        List<Task> tasks = List.of(task1, task2);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Task> page = new PageImpl<>(tasks);
        when(repository.findAll(pageable)).thenReturn(page);

        List<TaskDto> actualList = taskService.getAllTask(pageable);

        int expectedSize = tasks.size();
        assertEquals(expectedSize, actualList.size());
    }

    @Test
    @DisplayName("deleteTask(Long) Тест на получение ошибки из-за отсутствующей задачи с указанным ID")
    void deleteTask_whenInvalidTaskId_thenHasException() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskService.deleteTask(task1.getId());
        });
    }
}
