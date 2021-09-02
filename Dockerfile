FROM openjdk:11-jre

VOLUME /tmp
COPY target/de4a-connector-mock-0.2.0-SNAPSHOT.jar de4a-connector-mock.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar","/de4a-connector-mock.jar"]
