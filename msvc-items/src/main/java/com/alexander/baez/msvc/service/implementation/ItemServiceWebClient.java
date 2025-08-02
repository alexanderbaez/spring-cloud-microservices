//package com.alexander.baez.msvc.service.implementation;
//
//import com.Alexander.Baez.Libs.msvc.commons.models.Product;
//import com.alexander.baez.msvc.persistence.models.Item;
//import com.alexander.baez.msvc.service.interfaces.ItemService;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.*;
//
//@Service // Marca esta clase como un servicio para que Spring la gestione
//@Qualifier("ItemServiceWebClient")
//public class ItemServiceWebClient implements ItemService {
//
//    private final WebClient client;
//
//    // Inyección de dependencia de WebClient.Builder para construir instancias de WebClient
//    public ItemServiceWebClient(WebClient client) {
//        this.client = client;
//    }
//
//    @Override
//    public List<Item> findAll() {
//        return this.client
//                .get() // Método HTTP GET
//                .uri("http://msvc-products") // URL del servicio de productos (con balanceo de carga)
//                .accept(MediaType.APPLICATION_JSON) // Indica que espera respuesta en formato JSON
//                .retrieve() // Ejecuta la petición y obtiene la respuesta
//                .bodyToFlux(Product.class) // Convierte la respuesta en un flujo reactivo de productos
//                .map(product -> new Item(product, new Random().nextInt(10) + 1)) // Mapea cada producto a un objeto Item con cantidad aleatoria
//                .collectList() // Convierte el flujo reactivo en una lista tradicional de Java
//                .block(); // Bloquea la ejecución hasta recibir la respuesta (no recomendado en entornos reactivos)
//    }
//
//    @Override
//    public Optional<Item> findById(Long id) {
//        Map<String, Long> params = new HashMap<>();
//        params.put("id", id); // Mapa con el ID como parámetro
//
//        return Optional.ofNullable(this.client
//                .get()
//                .uri("http://msvc-products/{id}", params) // URI con parámetro de ruta
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(Product.class) // Convierte la respuesta en un objeto Product
//                .map(product -> new Item(product, new Random().nextInt(10) + 1)) // Mapea el producto a un objeto Item con cantidad aleatoria
//                .block());
//    }
//
//    @Override
//    public Product createProduct(Product product) {
//        return this.client
//                .post() // Método HTTP POST para crear un nuevo producto
//                .uri("http://msvc-products") // URL del servicio
//                .contentType(MediaType.APPLICATION_JSON) // Indica que enviará datos en formato JSON
//                .bodyValue(product) // Envia el objeto Product en el cuerpo de la petición
//                .retrieve()
//                .bodyToMono(Product.class) // Convierte la respuesta en un objeto Product
//                .map(p -> new Product(p.getId(), p.getName(), p.getPrice())) // Mapea la respuesta si se necesita modificar antes de devolver
//                .block();
//    }
//
//    @Override
//    public Product updateProduct(Product product, Long id) {
//        return this.client
//                .put() // Método HTTP PUT para actualizar un producto existente
//                .uri("http://msvc-products/{id}", id) // URI con parámetro de ruta
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(product) // Envia el objeto Product en el cuerpo de la petición
//                .retrieve()
//                .bodyToMono(Product.class)
//                .map(p -> new Product(p.getId(), p.getName(), p.getPrice())) // Mapea la respuesta si se necesita modificar antes de devolver
//                .block();
//    }
//
//    @Override
//    public void deleteProduct(Long id) {
//        this.client
//                .delete() // Método HTTP DELETE para eliminar un producto
//                .uri("http://msvc-products/{id}", id) // URI con parámetro de ruta
//                .retrieve()
//                .bodyToMono(Void.class) // No espera una respuesta con cuerpo
//                .block();
//    }
//}
