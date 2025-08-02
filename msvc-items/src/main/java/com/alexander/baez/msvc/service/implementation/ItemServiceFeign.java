package com.alexander.baez.msvc.service.implementation;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import com.alexander.baez.msvc.clients.ProductFeignClient;
import com.alexander.baez.msvc.persistence.models.Item;
import com.alexander.baez.msvc.service.interfaces.ItemService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Service
@Qualifier("ItemServiceFeign")
public class ItemServiceFeign implements ItemService {

    private final ProductFeignClient productFeignClient;

    public ItemServiceFeign(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    @Override
    public List<Item> findAll() {
        try {
            List<Product> products = productFeignClient.getAllProducts();
            return products.stream()
                    .map(product -> new Item(product, new Random().nextInt(10) + 1))
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay productos disponibles", e);
        }
    }

    @Override
    public Optional<Item> findById(Long id) {
        try {
            Product product = productFeignClient.getProductById(id);
            return Optional.of(new Item(product, new Random().nextInt(10) + 1));
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado", e);
        }
    }

    @Override
    public Product createProduct(Product product) {
        try {
            return productFeignClient.createProduct(product).getBody();
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el producto", e);
        }
    }
    @Override
    public Product updateProduct(Product productDTO, Long id) {
        try {
            return productFeignClient.updateProduct(id, productDTO).getBody();
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado para actualizar", e);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al actualizar el producto", e);
        }
    }
    @Override
    public void deleteProduct(Long id) {
        try {
            productFeignClient.deleteProduct(id);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado para eliminar", e);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al eliminar el producto", e);
        }
    }
}
