FROM gradle:8.8-jdk17-alpine AS BUILD_STAGE

COPY --chown=gradle:gradle . /home/gradle

RUN gradle build


FROM openjdk:17-jdk-alpine

ENV ARTIFACT_NAME=*.jar
ENV APP_HOME=/app

COPY --from=BUILD_STAGE /home/gradle/build/libs/$ARTIFACT_NAME $APP_HOME/

WORKDIR $APP_HOME

ENTRYPOINT exec java -jar ${ARTIFACT_NAME}