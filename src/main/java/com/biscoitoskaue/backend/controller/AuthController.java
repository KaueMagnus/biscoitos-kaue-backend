package com.biscoitoskaue.backend.controller;

import com.biscoitoskaue.backend.dto.auth.LoginRequest;
import com.biscoitoskaue.backend.dto.auth.LoginResponse;
import com.biscoitoskaue.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}