FROM openjdk:21-jdk
WORKDIR /app
COPY target/erpcron-1.0.0.jar erpcron-1.0.0.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","erpcron-1.0.0.jar"]