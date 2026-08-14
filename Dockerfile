FROM maven:3.9.9-amazoncorretto-21 AS build
WORKDIR /app

# кэшируем зависимости отдельным слоем — меняются реже, чем исходники
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
# юнит- и интеграционные тесты (Testcontainers) гоняются отдельным шагом в CI,
# где есть прямой доступ к Docker-демону, а не внутри самой сборки образа
RUN mvn -B package -DskipTests

FROM amazoncorretto:21.0.5-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
