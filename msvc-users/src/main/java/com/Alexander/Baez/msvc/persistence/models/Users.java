package com.Alexander.Baez.msvc.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    private Boolean enabled;
    @Transient
    private boolean admin;
    @Column(nullable = false, unique = true)
    @Email
    private String email;

    //evitamos la iteracion de infomacion que nos traiga el json
    @JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
    @ManyToMany
    //enlazamos la tabla de roles con usuarios y ponemos las forenkie y que no se puede repetir
    @JoinTable(name = "user_to_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})})
    private List<Role> roles;

}
