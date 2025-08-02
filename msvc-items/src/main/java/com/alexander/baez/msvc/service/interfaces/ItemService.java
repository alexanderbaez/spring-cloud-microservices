package com.alexander.baez.msvc.service.interfaces;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import com.alexander.baez.msvc.persistence.models.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> findAll();
    Optional<Item> findById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Product product, Long id);
    void deleteProduct(Long id);
}
