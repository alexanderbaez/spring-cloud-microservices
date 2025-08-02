package com.alexander.baez.msvc.oauth.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Configura la seguridad del servidor de autorización OAuth2.
     * Establece las reglas de seguridad para los endpoints de autenticación.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                // Aplica las configuraciones de seguridad al servidor de autorización
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults()) // Habilita OpenID Connect 1.0
                )
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated()
                )
                // Manejo de excepciones: Redirige a /login si no está autenticado
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    /**
     * Configura la seguridad general de la aplicación, estableciendo autenticación
     * mediante formulario para manejar inicios de sesión.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()) //deshabilita el token del formulario
                .formLogin(Customizer.withDefaults()); // Habilita autenticación con formulario

        return http.build();
    }

//    /**
//     * Define un servicio de autenticación en memoria con usuarios predefinidos.
//     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails userDetails = User.builder()
//                .username("alexander")
//                .password("{noop}12345") // {noop} indica que la contraseña no está encriptada
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{noop}12345")
//                .roles("USER", "ADMIN") // Usuario con rol de ADMIN y USER
//                .build();
//
//        return new InMemoryUserDetailsManager(userDetails, admin);
//    }

    /**
     * Configura los clientes registrados en el servidor de autorización OAuth2.
     * Aquí se define un cliente en memoria llamado "gateway-app".
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("gateway-app")
                .clientSecret(passwordEncoder.encode("12345")) //engriptamos el password
//              .clientSecret("{noop}12345") // {noop} indica que la contraseña no esta encriptada
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/client-app") // URI de redireccion para la autenticacion
                .redirectUri("http://127.0.0.1:8080/authorized")
                .postLogoutRedirectUri("http://127.0.0.1:8080/login")
                .scope(OidcScopes.OPENID) // habilita OpenID Connect
                .scope(OidcScopes.PROFILE) // permite acceso al perfil del usuario
                // Cambia la duración del token de acceso y del token de refresco
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(2)) // Token de acceso válido por 2 horas
                        .refreshTokenTimeToLive(Duration.ofDays(1)) // Token de refresco válido por 1 día
                        .build())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build()) // requiere consentimiento del usuario
                .build();
        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    /**
     * Configura la clave RSA utilizada para firmar los tokens de acceso JWT.
     * Genera un par de claves públicas y privadas para la seguridad del servidor de autorización.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * Genera un par de claves RSA (pública y privada) de 2048 bits.
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * Configura el decodificador JWT para validar tokens firmados con la clave RSA generada.
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Configura los ajustes generales del servidor de autorización OAuth2.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * Personaliza los tokens JWT generados por el servidor de autorización.
     * Agrega información adicional (claims) en los tokens, como los roles del usuario autenticado.
     */
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            // Verifica si el token que se está generando es un "ACCESS_TOKEN"
            if (context.getTokenType().getValue() == OAuth2TokenType.ACCESS_TOKEN.getValue()) {

                // Obtiene la información del usuario autenticado
                Authentication principal = context.getPrincipal();

                // Agrega un claim personalizado llamado "roles" con los roles del usuario
                context.getClaims().claim("roles", principal.getAuthorities()
                        .stream() // Convierte la lista de Authorities en una lista de Strings
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())); // Convierte la lista en un formato adecuado para el JWT
            }
        };
    }

}