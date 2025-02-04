FROM openjdk:21

COPY target/InterstellarRoutePlanner-0.0.1-SNAPSHOT.jar InterstellarRoutePlanner-0.0.1-SNAPSHOT.jar

EXPOSE 8080


ENTRYPOINT ["java", "-jar", "/InterstellarRoutePlanner-0.0.1-SNAPSHOT.jar"]