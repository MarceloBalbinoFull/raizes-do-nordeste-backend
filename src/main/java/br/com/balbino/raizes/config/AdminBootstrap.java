
package br.com.balbino.raizes.config;

import br.com.balbino.raizes.domain.Usuario;
import br.com.balbino.raizes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class AdminBootstrap {

    @Bean
    CommandLineRunner criarAdminInicial(
            UsuarioRepository usuarios,
            PasswordEncoder encoder,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.password:}") String senha) {

        return args -> {

            if (email.isBlank() || senha.isBlank()) {
                return;
            }

            if (senha.length() < 12) {
                throw new IllegalStateException(
                        "APP_ADMIN_PASSWORD deve ter 12 ou mais caracteres");
            }

            String normalizado = email.trim()
                    .toLowerCase(Locale.ROOT);

            if (usuarios.findByEmailIgnoreCase(normalizado)
                    .isPresent()) {
                throw new IllegalStateException(
                        "E-mail de bootstrap já existe; " +
                                "use outro ou remova APP_ADMIN_EMAIL/PASSWORD");
            }

            if (usuarios.findAll().stream()
                    .anyMatch(u ->
                            "ADMIN".equalsIgnoreCase(u.getPerfil()))) {
                return;
            }

            Usuario admin = new Usuario();
            admin.setNome("Administrador inicial");
            admin.setEmail(normalizado);
            admin.setSenhaHash(encoder.encode(senha));
            admin.setPerfil("ADMIN");
            admin.setAtivo(true);

            usuarios.save(admin);
        };
    }
}
