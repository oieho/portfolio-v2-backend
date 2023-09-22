FROM openjdk:11-jdk
LABEL maintainer="oiehomail@gmail.com"
ARG JAR_FILE=build/libs/backend-1-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} docker-springboot.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dspring.config.location=classpath:/application.yml,/secretyml/application-secret.yml", "-jar", "/docker-springboot.jar"]