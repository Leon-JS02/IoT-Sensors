FROM openjdk:17-jdk-alpine
COPY SensorServer.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]