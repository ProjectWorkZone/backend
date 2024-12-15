FROM eclipse-temurin:17-jdk AS builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# dos2unix 설치
RUN apt-get update && apt-get install -y dos2unix

# gradlew 파일을 Unix 스타일로 변환
RUN dos2unix ./gradlew

# Gradle 빌드 실행
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jdk
COPY --from=builder build/libs/*.jar workzone.jar

ENV SPRING_PROFILES_ACTIVE=develop

EXPOSE 8080
ENTRYPOINT ["java", "-jar",  "/workzone.jar"]