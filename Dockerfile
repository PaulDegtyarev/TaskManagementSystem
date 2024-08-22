FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build.gradle /app/
COPY gradle.properties /app/
COPY settings.gradle /app/
COPY src /app/src/

RUN gradle build --no-daemon

COPY build/libs/*.jar /target/

EXPOSE 8080
ENV POSTGRES database
ADD /target/manager.jar manager.jar
ENTRYPOINT exec java $JAVA_OPTS -jar manager.jar --spring.datasource.url=jdbc:postgresql://$POSTGRES:5432/sample