package org.example.jwtdemo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.jwtdemo.dto.reponse.JwtResponse;
import org.example.jwtdemo.dto.reponse.ResponseMessage;
import org.example.jwtdemo.dto.request.SignInForm;
import org.example.jwtdemo.dto.request.SignUpForm;
import org.example.jwtdemo.model.Role;
import org.example.jwtdemo.model.RoleName;
import org.example.jwtdemo.model.User;
import org.example.jwtdemo.security.jwt.JwtProvider;
import org.example.jwtdemo.security.userprincal.UserPrinciple;
import org.example.jwtdemo.service.AuthService;
import org.example.jwtdemo.service.IRoleService;
import org.example.jwtdemo.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final IUserService userService;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public ResponseEntity<?> register(SignUpForm signUpForm) {
        if (Boolean.TRUE.equals(userService.existsByUsername(signUpForm.getUsername()))) {
            return new ResponseEntity<>(new ResponseMessage("no user"), HttpStatus.OK);
        }
        if (Boolean.TRUE.equals(userService.existsByEmail(signUpForm.getEmail()))) {
            return new ResponseEntity<>(new ResponseMessage("no email"), HttpStatus.OK);
        }

        User user = new User(signUpForm.getName(), signUpForm.getUsername(), signUpForm.getEmail(),
                passwordEncoder.encode(signUpForm.getPassword()));
        Set<Role> roles = getRoles(signUpForm);

        user.setRoles(roles);
        userService.save(user);
        return new ResponseEntity<>(new ResponseMessage("create success!!!"), HttpStatus.OK);
    }

    private Set<Role> getRoles(SignUpForm signUpForm) {
        Set<String> strRoles = signUpForm.getRoles();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role.trim().toLowerCase()) {
                case "admin":
                    Role adminRole = roleService.findByName(RoleName.ADMIN)
                                                .orElseThrow(() -> new RuntimeException("Role NOT FOUND"));
                    roles.add(adminRole);
                    break;
                case "pm":
                    Role pmRole = roleService.findByName(RoleName.PM)
                                             .orElseThrow(() -> new RuntimeException("Role NOT FOUND"));
                    roles.add(pmRole);
                    break;
                case "user":
                    Role userRole = roleService.findByName(RoleName.USER)
                                               .orElseThrow(() -> new RuntimeException("Role NOT FOUND"));
                    roles.add(userRole);
                    break;
                default:
                    throw new RuntimeException("Role NOT FOUND");
            }
        });
        return roles;
    }

    @Override
    public ResponseEntity<?> login(SignInForm signInForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.createToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(token, userPrinciple.getName(), userPrinciple.getAuthorities()));
    }
}
