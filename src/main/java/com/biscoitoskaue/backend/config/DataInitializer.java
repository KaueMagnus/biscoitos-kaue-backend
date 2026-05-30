package com.biscoitoskaue.backend.config;

import com.biscoitoskaue.backend.entity.Usuario;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import com.biscoitoskaue.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUsers(UsuarioRepository usuarioRepository) {
        return args -> {
            if (usuarioRepository.findByEmail("admin@biscoitoskaue.com").isEmpty()) {
                Usuario admin = Usuario.builder()
                        .nome("Administrador")
                        .email("admin@biscoitoskaue.com")
                        .senha(passwordEncoder.encode("123456"))
                        .perfil(PerfilUsuario.ADMIN)
                        .ativo(true)
                        .build();

                usuarioRepository.save(admin);
            }

            if (usuarioRepository.findByEmail("representante@biscoitoskaue.com").isEmpty()) {
                Usuario representante = Usuario.builder()
                        .nome("Representante")
                        .email("representante@biscoitoskaue.com")
                        .senha(passwordEncoder.encode("123456"))
                        .perfil(PerfilUsuario.REPRESENTANTE)
                        .ativo(true)
                        .build();

                usuarioRepository.save(representante);
            }
        };
    }
}