package org.example.jwtdemo.service;

import org.example.jwtdemo.model.Role;
import org.example.jwtdemo.model.RoleName;

import java.util.Optional;

public interface IRoleService {
    Optional<Role> findByName(RoleName name);
}
