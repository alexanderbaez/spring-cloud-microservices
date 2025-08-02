
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 8761
ADD ./target/Eureka-Server-0.0.1-SNAPSHOT.jar Eureka-Server.jar
ENTRYPOINT ["java", "-jar", "Eureka-Server.jar"]
