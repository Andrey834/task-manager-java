package ru.t1.task_manager_aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.t1.task_manager_aop.containers.PostgresTestContainer;

@SpringBootTest
class TaskManagerAopApplicationTest extends PostgresTestContainer {

    @Test
    @DisplayName("Инициализация контекста")
    void contextLoads() {
    }
}
