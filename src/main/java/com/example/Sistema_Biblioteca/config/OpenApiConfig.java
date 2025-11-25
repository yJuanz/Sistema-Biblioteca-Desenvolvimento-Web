package com.example.Sistema_Biblioteca.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API do Sistema de Biblioteca",
        version = "1.0",
        contact = @Contact(name = "Seu Nome", email = "seu.email@exemplo.com"),
        description = "Documentação da API com autenticação JWT"
    ),
    // Isto diz ao Swagger para aplicar a segurança "bearerAuth" em TODOS os endpoints globalmente
    security = @SecurityRequirement(name = "bearerAuth")
)
// Isto define COMO é a segurança (Token JWT)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
    // Nenhuma bean extra necessária aqui, as anotações fazem tudo
}