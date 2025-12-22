package edu.espe.springlab.config;

// Importaciones necesarias para configurar OpenAPI (Swagger) en una aplicación Spring Boot
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// Indica que esta clase es una clase de configuración de Spring
@Configuration
public class OpenApiConfig {

    // Define un bean de tipo OpenAPI para personalizar la documentación de la API
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Configura la información general de la API
                .info(new Info()
                        .title("Hotel Reactive API") // Título de la API
                        .description("API reactiva para gestión de hotel con Spring WebFlux y R2DBC") // Descripción
                        .version("1.0.0") // Versión de la API
                        .contact(new Contact() // Información de contacto del desarrollador
                                .name("Arico Cesar")
                                .email("coarico@espe.edu.ec")
                                .url("https://github.com/aeherrera16/Proyecto_Avanzada_Hotel  "))
                        .license(new License() // Licencia bajo la que se distribuye la API
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT  ")))
                // Define los servidores en los que la API está disponible
                .servers(List.of(
                        new Server()
                                .description("Servidor de desarrollo") // Entorno local
                                .url("http://localhost:8085"),
                        new Server()
                                .description("Servidor de producción") // Entorno en la nube
                                .url("https://hotel-reactive-api.herokuapp.com  ")));
    }
}