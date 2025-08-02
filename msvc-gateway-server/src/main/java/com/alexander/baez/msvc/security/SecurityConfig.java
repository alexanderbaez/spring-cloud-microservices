package com.alexander.baez.msvc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;


import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    // esta configuracion utiliza Spring Security con WebFlux en una aplicacion reactiva.
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) throws  Exception{

        return httpSecurity.authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers("/authorized", "/login").permitAll()
                            .pathMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
                            .pathMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}")
                            .hasAnyRole("ADMIN", "USER")
                            .pathMatchers("/api/items/**", "/api/products/**", "/api/users/**").hasRole("ADMIN")   //admin solo puede acceder a los demas method, POST, PUT, DELETE
                            .anyExchange().authenticated();

                    //otra forma de que admin protega los method POST, PUT DELETE
//                    .pathMatchers(HttpMethod.DELETE, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
//                    .pathMatchers(HttpMethod.POST, "/api/items", "/api/products", "/api/users").hasRole("ADMIN")
//                    .pathMatchers(HttpMethod.PUT, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")

                }).cors(csrf -> csrf.disable())//deshabilita la proteccion CSRF, ya que en aplicaciones con WebFlux y APIs REST no es necesaria.
                .oauth2Login(withDefaults())
                .oauth2Client(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt( // Configura el servidor de recursos OAuth2 para validar tokens JWT
                        jwt -> jwt.jwtAuthenticationConverter(new Converter<Jwt, Mono<AbstractAuthenticationToken>>() {
                            // Define un conversor personalizado para transformar el token JWT en una autenticación válida

                            @Override
                            public Mono<AbstractAuthenticationToken> convert(Jwt source) {
                                // Método que toma el token JWT y extrae la información necesaria para la autenticación

                                Collection<String> roles = source.getClaimAsStringList("roles");
                                // Extrae los roles del usuario desde el claim "roles" dentro del JWT

                                Collection<GrantedAuthority> authorities = roles.stream()
                                        .map(SimpleGrantedAuthority::new) // Convierte cada rol en un objeto SimpleGrantedAuthority
                                        .collect(Collectors.toList()); // Almacena los roles en una lista

                                return Mono.just(new JwtAuthenticationToken(source, authorities));
                                // Retorna una instancia de JwtAuthenticationToken con el JWT y los roles asignados como autoridades
                            }
                        }))
                )
                .build(); // Construye la configuración de seguridad
    }
}


