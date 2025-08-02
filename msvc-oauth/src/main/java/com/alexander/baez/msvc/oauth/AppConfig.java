package com.alexander.baez.msvc.oauth;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration // Indica que esta clase contiene configuraciones de Spring Boot
public class AppConfig {

    /**
     * Configura un cliente HTTP WebClient para comunicarse con el microservicio de usuarios (msvc-users).
     *
     * @return WebClient.Builder con balanceo de carga habilitado.
     */
    @Bean
    // Habilita balanceo de carga cuando hay múltiples instancias del servicio
    WebClient webClient(WebClient.Builder builder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return builder
                .baseUrl("http://msvc-users")
                .filter(lbFunction)
                .build();
        // Define que este cliente se conectará al servicio "msvc-users" registrado en Eureka
    }

    /**
     * Configura un encriptador de contraseñas para almacenar y verificar las credenciales de los usuarios.
     *
     * @return Una instancia de BCryptPasswordEncoder.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // BCrypt es un algoritmo de encriptación seguro para almacenar contraseñas de usuarios
    }
}