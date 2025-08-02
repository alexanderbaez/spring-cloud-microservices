package com.alexander.baez.msvc.oauth.modelsDTOs;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private Boolean enabled;
    private boolean admin;
    private String email;
    private List<Role> roles;
}
