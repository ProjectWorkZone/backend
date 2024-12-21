FROM eclipse-temurin:17-jdk AS builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN apt-get update && apt-get install -y dos2unix

RUN dos2unix ./gradlew

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jdk
COPY --from=builder build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080
ENTRYPOINT ["java", "-jar",  "/app.jar"]