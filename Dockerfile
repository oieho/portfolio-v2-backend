FROM openjdk:11-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} docker-springboot.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dspring.config.location=classpath:/application.yml,/secretyml/application-secret.yml", "-jar", "/docker-springboot.jar"]