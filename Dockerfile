FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build.gradle ./
COPY gradle /gradle

RUN ./gradlew build -x test

COPY src /app/src

RUN ./gradlew build -x test

EXPOSE 8080

CMD ["java", "-jar", "build/libs/TaskManagementSystem-0.    0.1-SNAPSHOT.jar"]