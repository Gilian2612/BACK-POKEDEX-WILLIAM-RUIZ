package com.wilddex.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI (sección 38.1 DOSW).
 * Documenta la API con título, versión y esquema de seguridad JWT.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WildDex API")
                        .version("1.0.0")
                        .description("API REST para la Pokédex WildDex — Proyecto DOSW 2026")
                        .contact(new Contact()
                                .name("William Santiago Ruiz Medina")
                                .email("santiagowsrm01@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                .components(new Components()
                        .addSecuritySchemes("Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT")));
    }
}
