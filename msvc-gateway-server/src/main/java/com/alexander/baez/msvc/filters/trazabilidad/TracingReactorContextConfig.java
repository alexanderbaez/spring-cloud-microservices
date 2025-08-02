package com.alexander.baez.msvc.filters.trazabilidad;


import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class TracingReactorContextConfig {

    //es crucial para que el contexto de trazabilidad de Micrometer se propague en WebFlux (Gateway incluido).
    @PostConstruct
    public void enableContextLossDebugging() {
        Hooks.enableAutomaticContextPropagation();
    }
}