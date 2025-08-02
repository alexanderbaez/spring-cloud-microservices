package com.alexander.baez.msvc.filters.factory;


//Filtro personalizado con WEBFLUX
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<ConfigurationCookie> {

    private final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);
    //Agregamos un constructor para configuracion con ConfigurationCookie
    public SampleCookieGatewayFilterFactory(){
        super(ConfigurationCookie.class);
    }
    //vamos a ordenarlos pero sin el metedo getOrder sino con  new orderedGatewayFilter
    @Override
    public GatewayFilter apply(ConfigurationCookie config) {
        return new OrderedGatewayFilter((exchange, chain) -> {

           logger.info("ejecutando PRE gateway filter factory: "+ config.getMessage());

           return chain.filter(exchange).then(Mono.fromRunnable(()->{
               Optional.ofNullable(config.getValue()).ifPresent(cookie -> {//config.value. ahora es cookie
                   exchange.getResponse().addCookie(ResponseCookie.from(config.getName(), cookie).build());//pasamos el nombre y el valor
               });
                logger.info("ejecutando POST gateway filter factory: "+ config.getMessage());
           }));
        }, 100);
    }
}




//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
////filtro personalizado con MVC
//@Component
//public class SampleCookieGatewayFilterFactory implements Filter {
//
//    private static final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);
//
//    @Override
//    public void doFilter(jakarta.servlet.ServletRequest servletRequest,
//                         jakarta.servlet.ServletResponse servletResponse,
//                         FilterChain filterChain) throws IOException, ServletException {
//
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        logger.info("PRE filtro MVC: agregando cookie");
//
//        // Agregar cookie
//        Cookie cookie = new Cookie("nombre", "valor");
//        cookie.setPath("/");
//        response.addCookie(cookie);
//
//        filterChain.doFilter(request, response);
//
//        logger.info("POST filtro MVC: cookie agregada");
//    }
//}
