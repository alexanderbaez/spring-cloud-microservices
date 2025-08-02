package com.Alexander.Baez.Libs.msvc.commons.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    @Column(name = "create_At")
    private LocalDate createAt;

    //estamos haciendo el balanceo de carga de puertos
    @Transient
    private int port;


    public Product(Long id, String name, Double price) {
    }
}
