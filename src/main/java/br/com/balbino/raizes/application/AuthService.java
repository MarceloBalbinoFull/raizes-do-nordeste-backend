
package br.com.balbino.raizes.application;

import br.com.balbino.raizes.api.LoginRequest;
import br.com.balbino.raizes.api.LoginResponse;
import br.com.balbino.raizes.domain.Usuario;
import br.com.balbino.raizes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(
            UsuarioRepository usuarios,
            PasswordEncoder encoder,
            JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarios
                .findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())
                || !encoder.matches(
                request.getSenha(),
                usuario.getSenhaHash())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciais inválidas");
        }

        return new LoginResponse(
                jwt.gerar(usuario),
                usuario.getId(),
                usuario.getPerfil());
    }
}
