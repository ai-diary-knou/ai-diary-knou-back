FROM gradle:jdk17
VOLUME /aidiary-knou-back
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} aidiary-knou-back.jar
ENTRYPOINT ["nohup", "java", "-jar", "-Dspring.profiles.active=", "aidiary-knou-back.jar", "&"]