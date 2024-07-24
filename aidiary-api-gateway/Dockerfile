FROM gradle:jdk17
VOLUME /aidiary-api-gateway
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} aidiary-api-gateway.jar
ENTRYPOINT ["nohup", "java", "-jar",  "-Dspring.profiles.active=dev", "aidiary-api-gateway.jar", "&"]