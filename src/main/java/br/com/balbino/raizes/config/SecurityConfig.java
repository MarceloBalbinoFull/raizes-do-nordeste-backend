package br.com.balbino.raizes.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((req, res, ex) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, ex) ->
                                res.sendError(HttpServletResponse.SC_FORBIDDEN)))
                .authorizeHttpRequests(auth -> auth

                        // Documentacao e login
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login")
                        .permitAll()

                        // Usuarios: o GERENTE pode gerenciar equipe, com
                        // restricoes adicionais no UsuarioService.
                        .requestMatchers(HttpMethod.POST, "/usuarios")
                        .hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/*")
                        .hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers(HttpMethod.GET, "/usuarios", "/usuarios/**")
                        .hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers("/usuarios", "/usuarios/**")
                        .denyAll()

                        // Unidades: consulta operacional, alteracao administrativa.
                        .requestMatchers(HttpMethod.GET, "/unidades", "/unidades/**")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE", "COZINHA")
                        .requestMatchers("/unidades", "/unidades/**")
                        .hasAnyAuthority("ADMIN", "GERENTE")

                        // Estoque: atendente consulta, mas nao altera.
                        .requestMatchers(HttpMethod.GET, "/estoque", "/estoque/**")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE")
                        .requestMatchers("/estoque", "/estoque/**")
                        .hasAnyAuthority("ADMIN", "GERENTE")

                        // Promocoes
                        .requestMatchers("/promocoes", "/promocoes/**")
                        .hasAnyAuthority("ADMIN", "GERENTE")

                        // Produtos
                        .requestMatchers(HttpMethod.GET, "/produtos", "/produtos/**")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers("/produtos", "/produtos/**")
                        .hasAnyAuthority("ADMIN", "GERENTE")

                        // Pedidos: as operacoes do CLIENTE tem verificacao de
                        // proprietario no PedidoService.
                        .requestMatchers(HttpMethod.POST, "/pedidos")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/pedidos", "/pedidos/**")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE", "COZINHA")
                        .requestMatchers(HttpMethod.PATCH, "/pedidos/*/cancelar")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers(HttpMethod.PATCH, "/pedidos/*/status")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "COZINHA")
                        .requestMatchers("/pedidos", "/pedidos/**")
                        .denyAll()

                        // Pagamento mock: operacao de equipe, nao do CLIENTE.
                        .requestMatchers("/pagamentos", "/pagamentos/**")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE")

                        // Fidelidade: consultas, adesao, consentimento e resgate
                        // do proprio CLIENTE. A propriedade e conferida no servico.
                        .requestMatchers(HttpMethod.POST, "/fidelidade")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/fidelidade/*")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers(HttpMethod.PATCH, "/fidelidade/*/consentimento")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/fidelidade/*/resgates")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE", "CLIENTE")
                        // Apenas equipe pode creditar pontos.
                        .requestMatchers(HttpMethod.POST, "/fidelidade/*/pontos")
                        .hasAnyAuthority("ADMIN", "GERENTE", "ATENDENTE")
                        .requestMatchers("/fidelidade", "/fidelidade/**")
                        .denyAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
