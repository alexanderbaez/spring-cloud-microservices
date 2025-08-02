package com.alexander.baez.msvc;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AppConfigCircuitBreaker {

    @Bean
    Customizer<Resilience4JCircuitBreakerFactory> customizerCircuitBreaker(){
        return (factory) -> factory.configureDefault(id -> {
            return new Resilience4JConfigBuilder(id).circuitBreakerConfig(CircuitBreakerConfig
                    .custom()
                    .slidingWindowSize(10)//vamos a controlar en un intervalo de 10 peticiones
                    .failureRateThreshold(50)//50% el umbral de error
                    .waitDurationInOpenState(Duration.ofSeconds(10L))//10 segundo de espera entre estados
                    .permittedNumberOfCallsInHalfOpenState(5)
                    //configuramos las llamadas lentas
                    .slowCallDurationThreshold(Duration.ofSeconds(2L))//lo configuramos en 2 segundos para que ejecute antes que el TimeOut
                    .slowCallRateThreshold(50)//configuramos el umbral de llamada lentas.
                    .build())
                    //configuramos el tiempo permitido que puede tardar una llamada
                    .timeLimiterConfig(TimeLimiterConfig.custom()
                    .timeoutDuration(Duration.ofSeconds(4))//cuando tarde 3 seg lanza un timeOut
                    .build())
                    .build();
        });
    }
}
