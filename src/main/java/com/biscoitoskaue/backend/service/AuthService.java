package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.auth.LoginRequest;
import com.biscoitoskaue.backend.dto.auth.LoginResponse;
import com.biscoitoskaue.backend.exception.BusinessException;
import com.biscoitoskaue.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.senha()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new BusinessException("E-mail ou senha inválidos");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, "Bearer");
    }
}
