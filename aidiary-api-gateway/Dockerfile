FROM gradle:jdk17
VOLUME /aidiary-api-gateway
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} aidiary-api-gateway.jar
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
ENTRYPOINT ["nohup", "java", "-jar", "aidiary-api-gateway.jar", "&"]