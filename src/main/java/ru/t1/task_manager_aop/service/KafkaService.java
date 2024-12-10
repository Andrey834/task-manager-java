package ru.t1.task_manager_aop.service;

public interface KafkaService {

    void sendMessage(String topic, Object message);
}
