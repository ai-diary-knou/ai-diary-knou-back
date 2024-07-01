FROM gradle:jdk17
VOLUME /aidiary-api-gateway
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} aidiary-api-gateway.jar
ENTRYPOINT ["java", "-jar", "aidiary-api-gateway.jar"]