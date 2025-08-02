
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 9411
ADD ./zipkin-server-3.5.1-exec.jar zipkin-Server.jar
ENTRYPOINT ["java", "-jar", "zipkin-Server.jar"]
