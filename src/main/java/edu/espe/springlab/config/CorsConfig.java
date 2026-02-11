package edu.espe.springlab.config;

// Importaciones necesarias para configurar CORS en una aplicación reactiva (Spring WebFlux)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// Indica que esta clase proporciona configuración de Spring
@Configuration
public class CorsConfig {

    // Define un filtro CORS como bean para permitir solicitudes desde otros orígenes
    @Bean
    public CorsWebFilter corsWebFilter() {
        // Crea una configuración CORS personalizada
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(false);// No permite enviar credenciales (cookies, auth headers, etc.)
        corsConfig.addAllowedOriginPattern("*"); // Permite solicitudes desde cualquier origen
        corsConfig.addAllowedHeader("*"); // Permite todos los encabezados en las solicitudes
        corsConfig.addAllowedMethod("*"); // Permite todos los métodos HTTP (GET, POST, PUT, etc.)
        corsConfig.setMaxAge(3600L); // Tiempo de caché de la configuración CORS en segundos (1 hora)

        // Crea una fuente de configuración basada en URLs
        UrlBasedCorsConfigurationSource source = 
            new UrlBasedCorsConfigurationSource();
        // Aplica la configuración CORS a todas las rutas (/**)
        source.registerCorsConfiguration("/**", corsConfig);

        // Devuelve el filtro CORS listo para usarse en la aplicación reactiva
        return new CorsWebFilter(source);
    }
}
