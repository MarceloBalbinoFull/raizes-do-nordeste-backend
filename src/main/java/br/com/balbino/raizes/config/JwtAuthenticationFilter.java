
package br.com.balbino.raizes.config;

import br.com.balbino.raizes.application.JwtService;
import br.com.balbino.raizes.domain.Usuario;
import br.com.balbino.raizes.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UsuarioRepository usuarios;

    public JwtAuthenticationFilter(
            JwtService jwt,
            UsuarioRepository usuarios) {
        this.jwt = jwt;
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            Long id = jwt.validarEObterId(header.substring(7));

            if (id != null) {

                Usuario usuario = usuarios.findById(id).orElse(null);

                if (usuario != null
                        && Boolean.TRUE.equals(usuario.getAtivo())
                        && usuario.getPerfil() != null) {

                    String perfil = usuario.getPerfil()
                            .trim()
                            .toUpperCase();

                    var auth = new UsernamePasswordAuthenticationToken(
                            usuario.getId(),
                            null,
                            List.of(new SimpleGrantedAuthority(perfil)));

                    SecurityContextHolder.getContext()
                            .setAuthentication(auth);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
