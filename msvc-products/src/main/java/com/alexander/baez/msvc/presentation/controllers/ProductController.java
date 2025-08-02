package com.alexander.baez.msvc.presentation.controllers;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import com.alexander.baez.msvc.services.interfaces.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    //inyentamos la dependencia con buenas practicas
    final IProductService iProductService;

    public ProductController (IProductService iProductService){
        this.iProductService = iProductService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
       List<Product> products = iProductService.findAll();
       logger.info("Ingresando al metodo del ProductController::getAllProducts");
       return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws InterruptedException {
        logger.info("Ingresando al metodo del ProductController::getProductById");

        //agregamos un error para probar el circuit breaker
        if (id.equals(10L)){
            throw new IllegalStateException("Producto no encontrado");
        }
        //aca realizamos una pausa para que la petision tarde mas del umbral requerido
        if (id.equals(7L)){
            TimeUnit.SECONDS.sleep(3L);
        }
        Product product = iProductService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        logger.info("Ingresando al metodo del ProductController::createProduct, creando: {}",product);
        return ResponseEntity.status(HttpStatus.CREATED).body(iProductService.createProduct(product));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product){
        logger.info("Ingresando al metodo del ProductController::updateProduct, editando: }",product);
        Product updateProduct = iProductService.updateProduct(id,product);
        return ResponseEntity.ok(updateProduct);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        this.iProductService.deleteProduct(id);
        logger.info("Ingresando al metodo del ProductController::deleteProduct, eliminado: {}",id);

        return ResponseEntity.noContent().build();
    }
}
