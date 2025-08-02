package com.Alexander.Baez.msvc.persistence.repository;

import com.Alexander.Baez.msvc.persistence.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
}
