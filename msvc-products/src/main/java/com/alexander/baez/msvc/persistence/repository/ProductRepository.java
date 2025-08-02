package com.alexander.baez.msvc.persistence.repository;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
