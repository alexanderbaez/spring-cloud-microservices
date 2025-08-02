package com.alexander.baez.msvc.clients;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-products")
public interface ProductFeignClient {

    @GetMapping
    public List<Product> getAllProducts();

    @GetMapping(value = "/{id}")
    public Product getProductById(@PathVariable Long id);

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product);

    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product);

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id);
}
