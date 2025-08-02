package com.Alexander.Baez.msvc.persistence.repository;

import com.Alexander.Baez.msvc.persistence.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);
}
