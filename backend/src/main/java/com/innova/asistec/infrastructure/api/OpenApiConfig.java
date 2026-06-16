package com.innova.asistec.infrastructure.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI asistecOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Asistec API")
                        .description("Sistema de Asistencia Escolar - Innova Schools")
                        .version("1.0.0"));
    }
}
