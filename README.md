# Task Manager — REST API с событийными уведомлениями через Kafka

Сервис управления задачами: REST CRUD-операции + асинхронное уведомление по email при смене статуса задачи через Apache Kafka.

## Архитектура

```
Client → TaskController → TaskService → PostgreSQL

Смена статуса задачи:
TaskService → Kafka (topic: update-task-status) → KafkaListener → NotificationService → Email
```

Producer и consumer живут в одном приложении (учебная демонстрация паттерна), но спроектированы так, будто это разные сервисы: изменение статуса не отправляет email напрямую — оно публикует событие в Kafka, а обработка (в том числе retry при сбое) полностью изолирована в consumer.

## Технологии

- **Java 21** (включены Virtual Threads — `spring.threads.virtual.enabled: true`)
- **Spring Boot 3** — Web, Data JPA, Validation
- **Spring Kafka** — продюсер и consumer
- **PostgreSQL** + **Liquibase** — версионируемые миграции схемы
- **Spring Mail** — email-уведомления
- **Docker / Docker Compose** — приложение, PostgreSQL, Kafka + Zookeeper, Kafka UI
- **JUnit 5, Testcontainers** (PostgreSQL и Kafka) — интеграционные тесты с реальными брокером и БД, а не эмуляцией

## Kafka — что именно реализовано

- **Producer**: `KafkaTemplate` с JSON-сериализацией, асинхронная отправка с логированием результата (offset при успехе, причина ошибки при неудаче) через `CompletableFuture`;
- **Consumer**: `@KafkaListener` с **ручным подтверждением** (`AckMode.MANUAL`) — сообщение считается обработанным только после реального выполнения (отправки email), не раньше;
- **Retry / отказоустойчивость**: `@RetryableTopic` с экспоненциальным backoff (`delay = 2000ms, multiplier = 2, maxDelay = 10000ms`) — при сбое обработки Spring Kafka автоматически создаёт retry-топики и повторяет попытки по нарастающей паузе;
- **Отказоустойчивая десериализация**: кастомный `ErrorHandlingDeserializer`, чтобы битое сообщение не роняло весь listener;
- **Настройка consumer'а под нагрузку**: явные `sessionTimeout`, `maxPollRecords`, `maxPartitionFetchBytes`.

## REST API

| Метод | URL | Описание |
|---|---|---|
| `POST` | `/tasks` | Создать задачу |
| `GET` | `/tasks?from=&size=&sort=` | Список задач с пагинацией и сортировкой |
| `GET` | `/tasks/{id}` | Получить задачу по ID |
| `PUT` | `/tasks/{id}` | Обновить задачу (смена статуса триггерит Kafka-событие) |
| `DELETE` | `/tasks/{id}` | Удалить задачу |

## Локальный запуск

```bash
cp .env.example .env   # заполнить реальными значениями
docker compose up -d
```

Поднимутся: приложение, PostgreSQL, Kafka + Zookeeper, [Kafka UI](http://localhost:9879) для визуальной отладки топиков и сообщений.

## Тесты

```bash
mvn test
```

Интеграционные тесты поднимают настоящие PostgreSQL и Kafka в Docker-контейнерах через Testcontainers — проверяется реальное поведение брокера и БД, а не мок.
