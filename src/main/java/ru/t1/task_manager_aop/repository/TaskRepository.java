package ru.t1.task_manager_aop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.task_manager_aop.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
