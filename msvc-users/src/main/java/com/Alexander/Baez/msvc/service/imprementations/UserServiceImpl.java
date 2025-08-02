package com.Alexander.Baez.msvc.service.imprementations;

import com.Alexander.Baez.msvc.persistence.models.Role;
import com.Alexander.Baez.msvc.persistence.models.Users;
import com.Alexander.Baez.msvc.persistence.repository.RoleRepository;
import com.Alexander.Baez.msvc.persistence.repository.UserRepository;
import com.Alexander.Baez.msvc.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;
    //inyectamos el repositorio de Roles
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Users> findUserById(Long id) {
        return userRepository.findById(id);
    }
    //buscamos por el nombre
    @Override
    @Transactional(readOnly = true)
    public Optional<Users> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Users> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Users saveUser(Users user) {
        //encriptamos la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        List<Role> roles = getRoles(user);
        user.setRoles(roles);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<Users> updateUser(Long id, Users user) {

        Optional<Users> updateUser = this.findUserById(id);

        return updateUser.map(users -> {
            users.setUsername(user.getUsername());
            users.setEmail(user.getEmail());
            //verificamos si esta,
            if (user.getEnabled() == null){
                users.setEnabled(true);
            }else {
                users.setEnabled(user.getEnabled());
            }
            //List<Role> roles = getRoles(user);
            users.setRoles(getRoles(user));

            return Optional.of(userRepository.save(users));
        }).orElseGet(()-> Optional.empty());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private List<Role> getRoles(Users users) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
        roleOptional.ifPresent(roles::add);//si esta lo agrega a la lista.

        if (users.isAdmin()){
            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            adminRoleOptional.ifPresent(roles::add);//si esta lo agrega a la lista.
        }
        return roles;
    }
}
