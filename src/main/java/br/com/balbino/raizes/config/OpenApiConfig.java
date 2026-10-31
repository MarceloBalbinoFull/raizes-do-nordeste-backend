
package br.com.balbino.raizes.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configurarOpenAPI() {

        String esquema = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Raízes do Nordeste - API")
                        .version("1.0.0")
                        .description(
                                "API REST para gerenciamento de " +
                                        "usuários, unidades, produtos, estoque, " +
                                        "pedidos, pagamentos, fidelidade e promoções. " +
                                        "Autenticação por JWT."
                        ))
                .components(new Components()
                        .addSecuritySchemes(
                                esquema,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(esquema)
                );
    }
}
