FROM gradle:jdk17
VOLUME /aidiary-knou-back
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} aidiary-knou-back.jar
ENTRYPOINT ["java", "-jar", "aidiary-knou-back.jar"]