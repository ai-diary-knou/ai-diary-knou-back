FROM gradle:jdk17
VOLUME /aidiary-user
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} aidiary-user.jar
ENTRYPOINT ["java", "-jar", "aidiary-user.jar", "-Dspring-boot.run.arguments=--datasource.username=${DATASOURCE_USERNAME}, --datasource.password=${DATASOURCE_PASSWORD}"]