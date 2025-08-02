# Spring Cloud Microservices

Este proyecto implementa una arquitectura de **microservicios** con **Spring Boot 3** y **Spring Cloud**, siguiendo los conceptos aprendidos en el curso de Microservicios.  
Incluye **registro de servicios, balanceo de carga, tolerancia a fallos, seguridad con OAuth2**, y **despliegue en contenedores Docker y AWS EC2**.

---

## 🚀 Tecnologías y herramientas utilizadas

- **Java 21**  
- **Spring Boot 3**  
- **Spring Cloud**  
  - Eureka Server (Service Registry)  
  - Spring Cloud Gateway (API Gateway)  
  - Spring Cloud LoadBalancer (Balanceo de carga)  
  - Spring Cloud Config Server (Configuración centralizada)  
- **Resilience4J** (Tolerancia a fallos y Circuit Breaker)  
- **Spring Security / OAuth 2.1 + JWT** (Seguridad de endpoints)  
- **Micrometer Tracing + Zipkin** (Trazabilidad distribuida)  
- **Spring Data JPA + Hibernate** (Persistencia)  
- **MySQL 8** (Base de datos)  
- **WebClient y Feign** (Consumo de microservicios REST)  
- **Docker & Docker Compose** (Contenedores y orquestación)  
- **AWS EC2** (Despliegue en la nube)

---

## 📦 Microservicios incluidos

- **msvc-products** → Gestión de productos.  
- **msvc-items** → Manejo de items y comunicación con productos.  
- **msvc-gateway-server** → API Gateway con rutas dinámicas y seguridad.  
- **msvc-eureka-server** → Registro de servicios para descubrimiento dinámico.  
- **msvc-config-server** → Configuración centralizada para microservicios.  
- **zipkin-server** → Trazabilidad de requests entre servicios.

---

## ⚙️ Funcionalidades principales

- Registro y descubrimiento de servicios con **Eureka**.  
- Balanceo de carga dinámico con **Spring Cloud LoadBalancer**.  
- Seguridad en endpoints con **OAuth2 y JWT**.  
- Configuración centralizada con **Config Server**.  
- Escalado horizontal de microservicios con **Docker Compose**:  
  ```bash
  docker compose up --scale msvc-products=3
