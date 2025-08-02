package com.alexander.baez.msvc.presentacion.controllers;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import com.alexander.baez.msvc.persistence.models.Item;
import com.alexander.baez.msvc.service.interfaces.ItemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${configuration.text}")
    private String text;

    //tenemos que inyectar un objeto que represente el ambiente
    @Autowired
    private Environment environment;

    public ItemController (@Qualifier("ItemServiceFeign") ItemService itemService, CircuitBreakerFactory circuitBreakerFactory){
        this.itemService = itemService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }
    //metodo para mostrar la configuracion por postman
    @GetMapping(value = "/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String port){
        Map<String,String> json = new HashMap<>();
        json.put("text",text);
        json.put("port",port);
        //tambien pasamos la informacion a la consola
        logger.info(port);
        logger.info(text);
        //agregamos mas configuraciones profiles
        if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("dev")){
            json.put("author.name", environment.getProperty("configuration.author.name"));
            json.put("author.email", environment.getProperty("configuration.author.email"));
        }
        return ResponseEntity.ok(json);
    }


    //mostramos los filter que estamos manejando de gateway en application.yml
    @GetMapping
    public ResponseEntity<List<Item>> getAllItem(@RequestParam(name = "name", required = false)
           String name, @RequestHeader(name="token-request", required = false) String token){
       //utilizando los log
        logger.info("Llamada a metodo del controlador traer todos los items::getAllItem()");
        logger.info("Request Parameter: {}",name);
        logger.info("Token: {}",token);

        List<Item> items = itemService.findAll();
        if (items.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id){
        Optional<Item> itemOptional = circuitBreakerFactory.create("items").run(()->itemService.findById(id), e->{
            //imprimimos el error
            System.out.println(e.getMessage());
            logger.error(e.getMessage());

            //en vez de que conecte con otra Api creamos el producto
            //creamos producto ficticio
            Product product = new Product();
            product.setCreateAt(LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony");
            product.setPrice(500.00);

            return Optional.of(new Item(product,5));
        });
        return itemOptional.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        logger.info("Creando Producto: {}",product);
        Product createdProduct = itemService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable Long id) {
        logger.info("Actualizando Producto: {}",product);
        Product updatedProduct = itemService.updateProduct(product, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Eliminado Producto: {}",id);
        itemService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
//-------------------------------------------------------------------------------------------------------------------------------

    //Creamos el metodo para utilizar el CircuitBreaker con @anotaciones
    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackMethodProduct")
    @GetMapping(value = "/item/{id}")
    public ResponseEntity<Item> getItemById2(@PathVariable Long id){
        Optional<Item> itemOptional = itemService.findById(id);

        if (itemOptional.isPresent()){
            return ResponseEntity.ok(itemOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    //creamos el metodo para el camino alternativo del CircuitBreaker
    public ResponseEntity<Item> getFallBackMethodProduct(Throwable e){
        //imprimimos el error
        System.out.println(e.getMessage());
        logger.error(e.getMessage());

        Product product = new Product();
        product.setCreateAt(LocalDate.now());
        product.setId(1L);
        product.setName("Camara Sony");
        product.setPrice(500.00);
        return ResponseEntity.ok(new Item(product,5));
    }
//-----------------------------------------------------------------------------------------------------

    //creamos el metodo para utilizar el TimeLimiter junto a CircuitBreaker
    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackMethodProduct2")
    @TimeLimiter(name = "items")
    @GetMapping(value = "/item2/{id}")
    public CompletableFuture<?> getItemById3(@PathVariable Long id){
        return CompletableFuture.supplyAsync(() -> {
            Optional<Item> itemOptional = itemService.findById(id);

            if (itemOptional.isPresent()){
                return ResponseEntity.ok(itemOptional.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }

    //creamos el metodo para el camino alternativo de TimeLimiter
    public CompletableFuture<?> getFallBackMethodProduct2(Throwable e){

        return CompletableFuture.supplyAsync(() -> {
            //imprimimos el error
            System.out.println(e.getMessage());
            logger.error(e.getMessage());

            Product product = new Product();
            product.setCreateAt(LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony");
            product.setPrice(500.00);
            return ResponseEntity.ok(new Item(product, 5));
        });
    }


}
