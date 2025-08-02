package com.alexander.baez.msvc.services.implemetation;

import com.Alexander.Baez.Libs.msvc.commons.models.Product;
import com.alexander.baez.msvc.persistence.repository.ProductRepository;
import com.alexander.baez.msvc.services.interfaces.IProductService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImple implements IProductService {
    //inyectamos con Autowired
//    @Autowired
//    private ProductRepository productRepository;

    //inyectamos utilizando las buenas practicas
    final private ProductRepository productRepository;
    //agregamos el Environment para manejar el puerto para el balanceo de carga
    final private Environment environment;
    //agregamos el constructor
    public ProductServiceImple(ProductRepository productRepository, Environment environment){
        this.productRepository = productRepository;
        this.environment= environment;
    }

    @Override
    @Transactional (readOnly = true)//colocamos el read de lectura
    public List<Product> findAll() {
        //verificamos
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay productos en la lista");
        }
        return products.stream().map(product -> {
            //le pasamos el puerto al producto
            product.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
            return product;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
            return product;
        }).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        if (!productRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado");
        }
        product.setId(id);
        return productRepository.save(product);
    }
}
