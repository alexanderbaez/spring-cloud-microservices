package com.alexander.baez.msvc.persistence.models;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Product productDTO;
    private int quantity;

    public Double getTotal(){
        return productDTO.getPrice() * quantity;
    }
}
