package ru.t1.task_manager_aop.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic updateStatusTopic(@Value(value = "${app.kafka.topics.updateTask}") String topic) {
        return TopicBuilder.name(topic).build();
    }
}
