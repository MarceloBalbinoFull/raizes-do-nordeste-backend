package br.com.balbino.raizes.application;

import br.com.balbino.raizes.api.UsuarioRequest;
import br.com.balbino.raizes.domain.Usuario;
import br.com.balbino.raizes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean ehGerente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> "GERENTE".equals(a.getAuthority()));
    }

    public Usuario salvar(UsuarioRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String perfil = request.getPerfil().trim().toUpperCase(Locale.ROOT);

        if (!List.of("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE", "COZINHA")
                .contains(perfil)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil invalido");
        }

        // GERENTE gerencia a equipe operacional, mas nao concede privilegios
        // de ADMIN/GERENTE a outras contas.
        if (ehGerente() && ("ADMIN".equals(perfil) || "GERENTE".equals(perfil))) {
            throw new AccessDeniedException(
                    "Gerente nao pode cadastrar administradores ou gerentes");
        }

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ja cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(perfil);
        usuario.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void excluir(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario nao encontrado"));

        // GERENTE nao pode excluir perfis privilegiados.
        if (ehGerente() && ("ADMIN".equalsIgnoreCase(usuario.getPerfil())
                || "GERENTE".equalsIgnoreCase(usuario.getPerfil()))) {
            throw new AccessDeniedException(
                    "Gerente nao pode excluir administradores ou gerentes");
        }

        usuarioRepository.delete(usuario);
    }
}
