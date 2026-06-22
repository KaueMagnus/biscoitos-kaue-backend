package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.representante.RepresentanteRequest;
import com.biscoitoskaue.backend.dto.representante.RepresentanteResponse;
import com.biscoitoskaue.backend.entity.Usuario;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import com.biscoitoskaue.backend.exception.BusinessException;
import com.biscoitoskaue.backend.exception.ForbiddenException;
import com.biscoitoskaue.backend.exception.ResourceNotFoundException;
import com.biscoitoskaue.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepresentanteService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<RepresentanteResponse> listar(String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        return usuarioRepository.findByPerfil(PerfilUsuario.REPRESENTANTE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RepresentanteResponse cadastrar(RepresentanteRequest request, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);
        validarEmailDisponivel(request.email());

        Usuario representante = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .perfil(PerfilUsuario.REPRESENTANTE)
                .ativo(true)
                .build();

        return toResponse(usuarioRepository.save(representante));
    }

    @Transactional
    public RepresentanteResponse inativar(Long id, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        Usuario representante = usuarioRepository.findByIdAndPerfil(id, PerfilUsuario.REPRESENTANTE)
                .orElseThrow(() -> new ResourceNotFoundException("Representante não encontrado"));

        representante.setAtivo(false);

        return toResponse(usuarioRepository.save(representante));
    }

    private void validarAdmin(String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getPerfil() != PerfilUsuario.ADMIN) {
            throw new ForbiddenException("Somente administradores podem gerenciar representantes");
        }
    }

    private void validarEmailDisponivel(String email) {
        usuarioRepository.findByEmail(email)
                .ifPresent(usuario -> {
                    throw new BusinessException("E-mail já cadastrado");
                });
    }

    private RepresentanteResponse toResponse(Usuario usuario) {
        return new RepresentanteResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo()
        );
    }
}
