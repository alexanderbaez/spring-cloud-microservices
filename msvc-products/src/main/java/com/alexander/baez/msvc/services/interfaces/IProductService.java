package com.alexander.baez.msvc.services.interfaces;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;

import java.util.List;

public interface IProductService {

    List<Product> findAll();

    Product findById(Long id);

    Product createProduct(Product product);

    void deleteProduct(Long id);

    Product updateProduct(Long id, Product product);
}
