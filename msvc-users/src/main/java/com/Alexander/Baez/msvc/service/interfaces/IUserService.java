package com.Alexander.Baez.msvc.service.interfaces;

import com.Alexander.Baez.msvc.persistence.models.Users;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<Users> findAllUsers();
    Optional<Users> findUserById(Long id);
    Optional<Users> findUserByUsername(String username);
    Optional<Users> findUserByEmail(String email);
    Users saveUser(Users user);
    Optional<Users> updateUser(Long id, Users user);
    void deleteUser(Long id);
}
