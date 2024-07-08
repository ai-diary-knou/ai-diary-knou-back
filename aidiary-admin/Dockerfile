FROM gradle:jdk17
VOLUME /aidiary-admin
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} aidiary-admin.jar
ENTRYPOINT ["nohup", "java", "-jar", "aidiary-admin.jar", "&"]