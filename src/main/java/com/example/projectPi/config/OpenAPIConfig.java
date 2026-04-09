package com.example.projectPi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI 3.0 pour la documentation Swagger UI
 * Accessible à : http://localhost:8081/streetleague/swagger-ui.html
 * API JSON : http://localhost:8081/streetleague/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Project Pi - Sports Management API")
                .version("1.0.0")
                .description("API complète pour la gestion des matchs et événements sportifs\n\n" +
                    "**Fonctionnalités:**\n" +
                    "- 🏆 Gestion des matchs avec statuts et scores\n" +
                    "- 📅 Gestion des événements (tournois, championnats, matchs amicaux)\n" +
                    "- 🎯 Détection des conflits de calendrier\n" +
                    "- 📊 Statistiques d'équipes et classements\n" +
                    "- 👥 Gestion des utilisateurs (arbitres, joueurs, entraîneurs)\n" +
                    "- 🔍 Recherche et filtrage avancés")
                .contact(new Contact()
                    .name("Project Pi Team")
                    .email("contact@projectpi.com")
                    .url("https://projectpi.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8081/streetleague")
                    .description("Local Development Server (Street League Context)"),
                new Server()
                    .url("https://api.projectpi.com")
                    .description("Production Server")
            ));
    }
}
