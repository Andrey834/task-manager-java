package ru.t1.task_manager_aop.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@Testcontainers
public class KafkaTestContainer {

    @Container
    public static ConfluentKafkaContainer kafkaContainer
            = new ConfluentKafkaContainer("confluentinc/cp-kafka:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
}
