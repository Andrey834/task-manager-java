package ru.t1.task_manager_aop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.task_manager_aop.dto.TaskDto;
import ru.t1.task_manager_aop.dto.TaskRequestDto;
import ru.t1.task_manager_aop.dto.TaskUpdateDto;
import ru.t1.task_manager_aop.service.TaskService;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public TaskDto create(@RequestBody TaskRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @GetMapping
    public Iterable<TaskDto> getAll(@RequestParam(name = "from", required = false, defaultValue = "0")
                                    int from,
                                    @RequestParam(name = "size", required = false, defaultValue = "10")
                                    int size,
                                    @RequestParam(name = "sort", required = false, defaultValue = "ASC")
                                    String sort
    ) {

        Pageable pageable = PageRequest.of(
                from,
                size,
                Sort.by(Sort.Direction.fromString(sort),
                        "id")
        );

        return taskService.getAllTask(pageable);
    }

    @GetMapping("/{taskId}")
    public TaskDto get(@PathVariable("taskId") Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @PutMapping("/{taskId}")
    public void update(@PathVariable("taskId") Long taskId,
                       @RequestBody TaskUpdateDto taskUpdateDto) {
        taskService.updateTask(taskId, taskUpdateDto);
    }

    @DeleteMapping("/{taskId}")
    public void delete(@PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
    }

}
