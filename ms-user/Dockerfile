FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8083
ARG JAR_FILE=build/libs/msuser-1.0.0.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]