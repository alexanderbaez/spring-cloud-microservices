package com.alexander.baez.msvc.oauth.services;

import com.alexander.baez.msvc.oauth.modelsDTOs.User;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service // Marca la clase como un componente de servicio para que Spring la maneje automáticamente
public class UserService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private WebClient client; // Inyecta un WebClient.Builder para realizar llamadas HTTP a otro microservicio
    //Inyectamos el Tracer para agregar los TAG
    @Autowired
    private Tracer tracer;

    /**
     * Implementa el método loadUserByUsername para buscar un usuario en la base de datos a través del microservicio "msvc-users".
     *
     * @param username Nombre de usuario que se busca.
     * @return Un objeto UserDetails con la información del usuario.
     * @throws UsernameNotFoundException Si el usuario no existe en la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Ingresando el proceso de login UserService::loadUserByUsername con {}",username);

        // Crea un mapa con el parámetro "username" para enviarlo en la URL
        Map<String, String> params = new HashMap<>();
        params.put("username", username);

        try {
            // Llama al microservicio "msvc-users" para obtener el usuario por su nombre de usuario
            User user = client
                    .get()
                    .uri("/username/{username}", params) // Llama al endpoint "/username/{username}"
                    .accept(MediaType.APPLICATION_JSON) // Especifica que espera recibir JSON
                    .retrieve()
                    .bodyToMono(User.class) // Convierte la respuesta JSON en un objeto User
                    .block(); // Bloquea la ejecución hasta recibir la respuesta

            // Convierte los roles del usuario en una lista de GrantedAuthority (Spring Security)
            List<GrantedAuthority> roles = user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName())) // Convierte cada rol en un SimpleGrantedAuthority
                    .collect(Collectors.toList()); // Guarda los roles en una lista

            logger.info("Se ha realizado el login con exito by username: {}",user);
            //agregamos el tag para brindarle mas info a Zipkin
            tracer.currentSpan().tag("success.login.message", "Se ha realizado el login con exito by username:" + username);

            // Retorna un objeto UserDetails con los datos del usuario
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(), // Nombre de usuario
                    user.getPassword(), // Contraseña (ya encriptada en la base de datos)
                    user.getEnabled(), // Si la cuenta está habilitada
                    true, // Si la cuenta no ha expirado
                    true, // Si las credenciales no han expirado
                    true, // Si la cuenta no está bloqueada
                    roles // Lista de roles/autoridades del usuario
            );

        } catch (WebClientResponseException e) {
            String error = "Error en el login, no existe el users '"+ username + "' en el sistema";
            logger.error(error);
            //agregamos el tag para darle mas detalle a Zipkin
            tracer.currentSpan().tag("error.login.message", error + " " + e.getMessage());
            // Si el usuario no existe, lanza una excepción con un mensaje personalizado
            throw new UsernameNotFoundException(error);
        }
    }
}
