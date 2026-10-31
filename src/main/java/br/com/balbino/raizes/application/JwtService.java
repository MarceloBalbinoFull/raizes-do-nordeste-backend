
package br.com.balbino.raizes.application;

import br.com.balbino.raizes.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private static final long DURACAO_SEGUNDOS = 3600;

    public JwtService(
            @Value("${app.jwt.secret}") String segredo) {

        if (segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "APP_JWT_SECRET precisa ter pelo menos 32 bytes UTF-8");
        }

        this.chave = Keys.hmacShaKeyFor(
                segredo.getBytes(StandardCharsets.UTF_8));
    }

    public String gerar(Usuario usuario) {

        Instant agora = Instant.now();

        return Jwts.builder()
                .subject(usuario.getId().toString())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(
                        agora.plusSeconds(DURACAO_SEGUNDOS)))
                .signWith(chave)
                .compact();
    }

    public Long validarEObterId(String token) {

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.valueOf(claims.getSubject());

        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }
}
