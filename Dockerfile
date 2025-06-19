FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/bookapi-0.0.1-SNAPSHOT.jar bookapi.jar
EXPOSE 8083
ENTRYPOINT [ "java","-jar","bookapi.jar" ]
