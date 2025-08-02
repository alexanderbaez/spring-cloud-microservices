//package com.alexander.baez.msvc.clients; // Define el paquete donde se encuentra la configuración de WebClient
//
//import org.springframework.beans.factory.annotation.Value;
////import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration // Indica que esta clase es una configuración de Spring
//public class WebClientConfig {
//    //configuracion con trazabilidad
//    @Value("${config.baseurl.endpoint.msvc-products}")
//    private String url;
//
//    @Bean
//    WebClient webClient(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction lbFuntion) {
//        return webClientBuilder.baseUrl(url).filter(lbFuntion).build();
//    }

//configuracion sin Trazabilidad

//    @Value("${config.baseurl.endpoint.msvc-products}")
//    private String url;
//    @Bean // Registra un bean en el contexto de Spring para que pueda ser inyectado en otras partes de la aplicación
//    @LoadBalanced // Habilita el balanceo de carga para distribuir las peticiones entre múltiples instancias de un servicio
//    WebClient.Builder webClient() {
//        return WebClient.builder(); // Retorna un constructor de WebClient para realizar peticiones HTTP de forma reactiva
//    }
//}
