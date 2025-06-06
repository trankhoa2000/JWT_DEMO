package org.example.jwtdemo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.jwtdemo.model.Role;
import org.example.jwtdemo.model.RoleName;
import org.example.jwtdemo.repository.IRoleRepository;
import org.example.jwtdemo.service.IRoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements IRoleService {

    private final IRoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(RoleName name) {
        return roleRepository.findByName(name);
    }
}
