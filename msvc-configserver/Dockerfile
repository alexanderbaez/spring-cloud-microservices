# Utiliza como imagen base Amazon Corretto con JDK 21 sobre Alpine Linux (liviano y optimizado para producción)
FROM amazoncorretto:21-alpine-jdk

# Establece el directorio de trabajo dentro del contenedor en /app
# Todas las instrucciones posteriores (ADD, COPY, RUN, ENTRYPOINT, etc.) se ejecutarán desde esta ruta
WORKDIR /app

# Expone el puerto 8888, que es el puerto en el que se ejecuta el Config Server de Spring
EXPOSE 8888

# Copia el archivo JAR de la aplicación desde el host al contenedor y lo renombra como msvc-configserver.jar
ADD ./target/msvc-configserver-0.0.1-SNAPSHOT.jar msvc-configserver.jar

# Define el comando que se ejecutará al iniciar el contenedor: correr la app Java con el JAR copiado
ENTRYPOINT ["java", "-jar", "msvc-configserver.jar"]
