package ru.t1.task_manager_aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.t1.task_manager_aop.dto.TaskDto;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TaskManagerAopApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerAopApplication.class, args);


	}

}
