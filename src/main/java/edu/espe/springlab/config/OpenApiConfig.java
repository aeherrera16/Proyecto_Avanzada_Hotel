package edu.espe.springlab.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Reactive API")
                        .description("API reactiva para gestión de hotel con Spring WebFlux y R2DBC")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Arico Cesar")
                                .email("coarico@espe.edu.ec")
                                .url("https://github.com/aeherrera16/Proyecto_Avanzada_Hotel"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .description("Servidor de desarrollo")
                                .url("http://localhost:8080"),
                        new Server()
                                .description("Servidor de producción")
                                .url("https://hotel-reactive-api.herokuapp.com")));
    }
}
