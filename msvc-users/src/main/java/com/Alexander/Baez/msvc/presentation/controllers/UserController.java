package com.Alexander.Baez.msvc.presentation.controllers;

import com.Alexander.Baez.msvc.persistence.models.Users;
import com.Alexander.Baez.msvc.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService iUserService;

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        logger.info("Ingresando al metodo de UserController::getAllUsers: obteniendo usuarios {}");
        return ResponseEntity.ok(iUserService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        logger.info("Ingresando al metodo de UserController::getUserById: obteniendo con id {}",id);
        return iUserService.findUserById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable String username) {
        logger.info("Ingresando al metodo de UserController::getUserByUsername: login con {}", username);
        return iUserService.findUserByUsername(username).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Users> getUserByEmail(@PathVariable String email) {
        logger.info("Ingresando al metodo de UserController::getUserByEmail: obteniendo usuario por email {}", email);
        return iUserService.findUserByEmail(email).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        logger.info("Ingresando al metodo de UserController::createUser: creando {}",user);
        return new ResponseEntity<>(iUserService.saveUser(user), HttpStatus.CREATED);
    }
    //creamos el put pero solo se va a poder modificar el mail y el nombre
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users user) {
        logger.info("Ingresando al metodo de UserController::updateUser: actualizando {}",user);

        return iUserService.updateUser(id,user).map(users -> ResponseEntity.status(HttpStatus.CREATED).body(users))
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Ingresando al metodo de UserController::deleteUser: eliminando con id {}",id);
        iUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}