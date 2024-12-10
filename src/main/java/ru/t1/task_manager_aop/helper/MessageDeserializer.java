package ru.t1.task_manager_aop.helper;

import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

@Component
public class MessageDeserializer<T>  extends JsonDeserializer<T>{

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            throw new DeserializationException("ОШИБКА! Десериализация", data, false, e);
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            throw new DeserializationException("ОШИБКА! Десериализация", data, false, e);
        }
    }
}
