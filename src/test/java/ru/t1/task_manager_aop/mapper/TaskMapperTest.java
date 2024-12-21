package ru.t1.task_manager_aop.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.enums.TaskStatus;
import ru.t1.task_manager_aop.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {
    private TaskRequestDto taskRequestDto;
    private Task task;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        String title = "title";
        String description = "description";
        taskRequestDto = new TaskRequestDto(userId, title, description);

        Long id = 1L;
        TaskStatus status = TaskStatus.CREATED;
        task = new Task(id, userId, title, description, status);
    }

    @Test
    @DisplayName("requestToTask(TaskRequestDto) Тест на успешный маппинг запроса в Task")
    void requestToTask() {
        Task actualTask = TaskMapper.requestToTask(taskRequestDto);

        Long expectedUserId = taskRequestDto.userId();
        String expectedTitle = taskRequestDto.title();
        String expectedDescription = taskRequestDto.description();
        TaskStatus expectedStatus = TaskStatus.CREATED;

        assertNull(actualTask.getId());
        assertEquals(expectedUserId, actualTask.getUserId());
        assertEquals(expectedTitle, actualTask.getTitle());
        assertEquals(expectedDescription, actualTask.getDescription());
        assertEquals(expectedStatus, actualTask.getStatus());
    }

    @Test
    @DisplayName("requestToTask(TaskRequestDto) Тест на успешный маппинг, должен вернутся объект класса Task")
    void requestToTask_thenReturnedTaskClass() {
        Task actualTask = TaskMapper.requestToTask(taskRequestDto);

        Class<Task> expectedClass = Task.class;
        assertEquals(expectedClass, actualTask.getClass());
    }

    @Test
    @DisplayName("requestToTask(Task) Тест на успешный маппинг задачи в TaskDto")
    void taskToDto() {
        TaskDto actualTaskDto = TaskMapper.taskToDto(task);

        assertEquals(task.getId(), actualTaskDto.id());
        assertEquals(task.getUserId(), actualTaskDto.userId());
        assertEquals(task.getTitle(), actualTaskDto.title());
        assertEquals(task.getDescription(), actualTaskDto.description());
        assertEquals(task.getStatus(), actualTaskDto.status());
    }

    @Test
    @DisplayName("taskToDto(Task) Тест на успешный маппинг, должен вернутся объект класса TaskDto")
    void taskToDto_thenReturnedTaskDtoClass() {
        TaskDto actualTask = TaskMapper.taskToDto(task);

        Class<TaskDto> expectedClass = TaskDto.class;
        assertEquals(expectedClass, actualTask.getClass());
    }
}
