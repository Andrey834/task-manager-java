package ru.t1.task_manager_aop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.t1.task_manager_aop.containers.PostgresTestContainer;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.dto.TaskUpdateDto;
import ru.t1.task_manager_aop.enums.TaskStatus;
import ru.t1.task_manager_aop.model.Task;
import ru.t1.task_manager_aop.repository.TaskRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TaskControllerTest extends PostgresTestContainer {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final String prefixPath = "/tasks";

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        taskRepository.saveAll(getTaskList());
    }

    @Test
    @DisplayName("create(TaskRequestDto) Тест на успешное добавление новой задачи")
    void create_whenDataValid_thenReturnTaskDto() throws Exception {
        Long userId = 1L;
        String title = "title";
        String description = "description";
        TaskRequestDto taskRequestDto = new TaskRequestDto(userId, title, description);

        mockMvc.perform(post(prefixPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("create(TaskRequestDto) Тест на ошибку при добавлении новой задачи с пустыми полями")
    void create_whenDataInvalid_thenReturnException() throws Exception {
        TaskRequestDto taskRequestDto = new TaskRequestDto(null, null, null);

        int actualSize = taskRepository.findAll().size();
        int expectedSize = 2;
        assertEquals(expectedSize, actualSize);

        mockMvc.perform(post(prefixPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequestDto))
        ).andExpect(status().isBadRequest());

        actualSize = taskRepository.findAll().size();
        assertEquals(expectedSize, actualSize);
    }


    @Test
    @DisplayName("getAll() Тест на получение списка задач")
    void getAll() throws Exception {
        String jsonResult = mockMvc
                .perform(get(prefixPath)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CollectionType collectionType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, TaskDto.class);
        List<TaskDto> actualList = objectMapper.readValue(jsonResult, collectionType);

        int expectedSize = getTaskList().size();
        assertEquals(expectedSize, actualList.size(), "Размер списка задач должен быть " + expectedSize);
    }

    @Test
    @DisplayName("getAll() Тест на получение списка задач c пагинацией")
    void getAll_withPageable() throws Exception {
        int expectedSize = 1;

        String jsonResult = mockMvc
                .perform(get(prefixPath)
                        .param("from", "1")
                        .param("size", String.valueOf(expectedSize))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CollectionType collectionType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, TaskDto.class);
        List<TaskDto> actualList = objectMapper.readValue(jsonResult, collectionType);

        assertEquals(expectedSize, actualList.size(), "Размер списка задач должен быть " + expectedSize);
    }

    @Test
    @DisplayName("getTask(Long) Тест на успешное получение задачи по ID")
    void getTask() throws Exception {
        Task task = taskRepository.findAll().getFirst();

        String jsonResult = mockMvc
                .perform(get(prefixPath + "/" + task.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDto actual = objectMapper.readValue(jsonResult, TaskDto.class);

        assertEquals(task.getId(), actual.id());
        assertEquals(task.getUserId(), actual.userId());
        assertEquals(task.getTitle(), actual.title());
        assertEquals(task.getDescription(), actual.description());
        assertEquals(task.getStatus(), actual.status());
    }

    @Test
    @DisplayName("update(TaskUpdateDto) Тест на успешное обновление задачи по ID")
    void update() throws Exception {
        Task task = taskRepository.findAll().getFirst();

        String updTitle = "updatedTitle";
        String updDesc = "updatedDescription";

        TaskUpdateDto updateDto = new TaskUpdateDto(task.getUserId(), updTitle, updDesc, task.getStatus());

        String jsonResult = mockMvc.perform(put(prefixPath + "/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDto actual = objectMapper.readValue(jsonResult, TaskDto.class);

        assertEquals(task.getId(), actual.id());
        assertEquals(task.getUserId(), actual.userId());
        assertEquals(updTitle, actual.title());
        assertEquals(updDesc, actual.description());
        assertEquals(task.getStatus(), actual.status());
    }

    @Test
    @DisplayName("update(TaskUpdateDto) Тест на успешное удаление задачи по ID")
    void deleteTask() throws Exception {
        List<Task> taskList = taskRepository.findAll();
        int expectedSize = getTaskList().size();
        assertEquals(expectedSize, taskList.size());

        Long taskId = taskList.getFirst().getId();

        mockMvc.perform(delete(prefixPath + "/" + taskId)
                .contentType(MediaType.APPLICATION_JSON));

        taskList = taskRepository.findAll();
        expectedSize = 1;
        assertEquals(expectedSize, taskList.size());
    }

    @Test
    @DisplayName("update(TaskUpdateDto) Тест на удаление отсутствующей задачи по ID")
    void deleteTask_whenInvalidId_thenReturnBadRequest() throws Exception {
        mockMvc.perform(delete(prefixPath + "/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private List<Task> getTaskList() {
        Task task1 = new Task();
        task1.setUserId(1L);
        task1.setTitle("title");
        task1.setDescription("description");
        task1.setStatus(TaskStatus.CREATED);

        Task task2 = new Task();
        task2.setUserId(2L);
        task2.setTitle("title2");
        task2.setDescription("description2");
        task2.setStatus(TaskStatus.CREATED);

        return List.of(task1, task2);
    }
}
