package org.example.jwtdemo.service;

import org.example.jwtdemo.dto.request.SignInForm;
import org.example.jwtdemo.dto.request.SignUpForm;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> register(SignUpForm signUpForm);

    ResponseEntity<?> login(SignInForm signInForm);
}
