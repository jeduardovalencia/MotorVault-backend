package com.motorvault.backend.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Bean
    public OpenAPI motorVaultOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MotorVault API")
                        .description("Backend REST API for the MotorVault vehicle management platform")
                        .version(appVersion)
                        .contact(new Contact()
                                .name("MotorVault Team")
                                .email("dev@motorvault.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://motorvault.com")));
    }
}
