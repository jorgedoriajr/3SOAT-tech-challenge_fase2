FROM openjdk:17-alpine

MAINTAINER 2023_3SOAT-Dev_Rise_G8

COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]