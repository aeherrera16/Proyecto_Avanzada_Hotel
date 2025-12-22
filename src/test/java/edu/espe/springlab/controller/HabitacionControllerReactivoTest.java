package edu.espe.springlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.service.reactive.HabitacionServiceReactivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Pruebas de integración para HabitacionControllerReactivo usando WebTestClient
 * Verifica el comportamiento de los endpoints REST reactivos
 */
@WebFluxTest(HabitacionControllerReactivo.class)
@DisplayName("Pruebas de Integración de HabitacionControllerReactivo")
class HabitacionControllerReactivoTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private HabitacionServiceReactivo habitacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Habitacion habitacion1;
    private Habitacion habitacion2;
    private Habitacion habitacion3;

    @BeforeEach
    void setUp() {
        habitacion1 = new Habitacion();
        habitacion1.setId(1L);
        habitacion1.setNumero("101");
        habitacion1.setTipo("Simple");
        habitacion1.setPrecio(50.0);
        habitacion1.setEstado("Disponible");

        habitacion2 = new Habitacion();
        habitacion2.setId(2L);
        habitacion2.setNumero("102");
        habitacion2.setTipo("Doble");
        habitacion2.setPrecio(80.0);
        habitacion2.setEstado("Ocupada");

        habitacion3 = new Habitacion();
        habitacion3.setId(3L);
        habitacion3.setNumero("103");
        habitacion3.setTipo("Suite");
        habitacion3.setPrecio(150.0);
        habitacion3.setEstado("Disponible");
    }

    @Test
    @DisplayName("GET /api/reactive/habitaciones - Debe retornar todas las habitaciones")
    void getAllHabitaciones_ShouldReturnAllHabitaciones() throws Exception {
        // Given
        List<Habitacion> habitaciones = Arrays.asList(habitacion1, habitacion2, habitacion3);
        when(habitacionService.findAll()).thenReturn(Flux.fromIterable(habitaciones));

        // When & Then
        webTestClient.get()
                .uri("/api/reactive/habitaciones")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].numero").isEqualTo("101")
                .jsonPath("$[0].tipo").isEqualTo("Simple")
                .jsonPath("$[0].precio").isEqualTo(50.0)
                .jsonPath("$[0].estado").isEqualTo("Disponible")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[2].id").isEqualTo(3);
    }

    @Test
    @DisplayName("GET /api/reactive/habitaciones/disponibles - Debe retornar habitaciones disponibles")
    void getAvailableHabitaciones_ShouldReturnAvailableHabitaciones() throws Exception {
        // Given
        List<Habitacion> habitacionesDisponibles = Arrays.asList(habitacion1, habitacion3);
        when(habitacionService.findAvailable()).thenReturn(Flux.fromIterable(habitacionesDisponibles));

        // When & Then
        webTestClient.get()
                .uri("/api/reactive/habitaciones/disponibles")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].estado").isEqualTo("Disponible")
                .jsonPath("$[1].estado").isEqualTo("Disponible")
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("GET /api/reactive/habitaciones/{id} - Debe retornar habitación cuando existe")
    void getHabitacionById_ShouldReturnHabitacionWhenExists() throws Exception {
        // Given
        when(habitacionService.findById(1L)).thenReturn(Mono.just(habitacion1));

        // When & Then
        webTestClient.get()
                .uri("/api/reactive/habitaciones/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.numero").isEqualTo("101")
                .jsonPath("$.tipo").isEqualTo("Simple")
                .jsonPath("$.precio").isEqualTo(50.0)
                .jsonPath("$.estado").isEqualTo("Disponible");
    }

    @Test
    @DisplayName("GET /api/reactive/habitaciones/{id} - Debe retornar 404 cuando no existe")
    void getHabitacionById_ShouldReturn404WhenNotFound() throws Exception {
        // Given
        when(habitacionService.findById(999L)).thenReturn(Mono.error(
                new RuntimeException("Habitación no encontrada con ID: 999")));

        // When & Then
        webTestClient.get()
                .uri("/api/reactive/habitaciones/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /api/reactive/habitaciones - Debe crear nueva habitación")
    void createHabitacion_ShouldCreateNewHabitacion() throws Exception {
        // Given
        Habitacion nuevaHabitacion = new Habitacion();
        nuevaHabitacion.setNumero("104");
        nuevaHabitacion.setTipo("Simple");
        nuevaHabitacion.setPrecio(60.0);
        nuevaHabitacion.setEstado("Disponible");

        Habitacion habitacionGuardada = new Habitacion();
        habitacionGuardada.setId(4L);
        habitacionGuardada.setNumero("104");
        habitacionGuardada.setTipo("Simple");
        habitacionGuardada.setPrecio(60.0);
        habitacionGuardada.setEstado("Disponible");

        when(habitacionService.save(any(Habitacion.class))).thenReturn(Mono.just(habitacionGuardada));

        // When & Then
        webTestClient.post()
                .uri("/api/reactive/habitaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(nuevaHabitacion))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(4)
                .jsonPath("$.numero").isEqualTo("104")
                .jsonPath("$.tipo").isEqualTo("Simple")
                .jsonPath("$.precio").isEqualTo(60.0)
                .jsonPath("$.estado").isEqualTo("Disponible");
    }

    @Test
    @DisplayName("POST /api/reactive/habitaciones - Debe retornar 400 para datos inválidos")
    void createHabitacion_ShouldReturn400ForInvalidData() throws Exception {
        // Given
        Habitacion habitacionInvalida = new Habitacion();
        habitacionInvalida.setNumero(""); // Número vacío
        habitacionInvalida.setTipo("Simple");
        habitacionInvalida.setPrecio(-10.0); // Precio negativo

        when(habitacionService.save(any(Habitacion.class)))
                .thenReturn(Mono.error(new RuntimeException("Datos inválidos")));

        // When & Then
        webTestClient.post()
                .uri("/api/reactive/habitaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(habitacionInvalida))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("PUT /api/reactive/habitaciones/{id} - Debe actualizar habitación existente")
    void updateHabitacion_ShouldUpdateExistingHabitacion() throws Exception {
        // Given
        Habitacion habitacionActualizada = new Habitacion();
        habitacionActualizada.setId(1L);
        habitacionActualizada.setNumero("101");
        habitacionActualizada.setTipo("Doble");
        habitacionActualizada.setPrecio(90.0);
        habitacionActualizada.setEstado("Mantenimiento");

        when(habitacionService.save(any(Habitacion.class))).thenReturn(Mono.just(habitacionActualizada));

        // When & Then
        webTestClient.put()
                .uri("/api/reactive/habitaciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(habitacionActualizada))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.numero").isEqualTo("101")
                .jsonPath("$.tipo").isEqualTo("Doble")
                .jsonPath("$.precio").isEqualTo(90.0)
                .jsonPath("$.estado").isEqualTo("Mantenimiento");
    }

    @Test
    @DisplayName("PUT /api/reactive/habitaciones/{id} - Debe retornar 404 cuando no existe")
    void updateHabitacion_ShouldReturn404WhenNotFound() throws Exception {
        // Given
        Habitacion habitacionActualizada = new Habitacion();
        habitacionActualizada.setId(999L);
        habitacionActualizada.setNumero("999");
        habitacionActualizada.setTipo("Simple");
        habitacionActualizada.setPrecio(50.0);
        habitacionActualizada.setEstado("Disponible");

        when(habitacionService.save(any(Habitacion.class)))
                .thenReturn(Mono.error(new RuntimeException("Habitación no encontrada")));

        // When & Then
        webTestClient.put()
                .uri("/api/reactive/habitaciones/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(habitacionActualizada))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("DELETE /api/reactive/habitaciones/{id} - Debe eliminar habitación existente")
    void deleteHabitacion_ShouldDeleteExistingHabitacion() throws Exception {
        // Given
        when(habitacionService.deleteById(1L)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/reactive/habitaciones/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /api/reactive/habitaciones/{id} - Debe retornar 404 cuando no existe")
    void deleteHabitacion_ShouldReturn404WhenNotFound() throws Exception {
        // Given
        when(habitacionService.deleteById(999L))
                .thenReturn(Mono.error(new RuntimeException("Habitación no encontrada")));

        // When & Then
        webTestClient.delete()
                .uri("/api/reactive/habitaciones/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("GET /api/reactive/habitaciones - Debe manejar error del servicio")
    void getAllHabitaciones_ShouldHandleServiceError() throws Exception {
        // Given
        when(habitacionService.findAll()).thenReturn(Flux.error(new RuntimeException("Error del servicio")));

        // When & Then
        webTestClient.get()
                .uri("/api/reactive/habitaciones")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Test de timeout en endpoint")
    void endpoint_ShouldHandleTimeout() throws Exception {
        // Given
        when(habitacionService.findById(1L))
                .thenReturn(Mono.just(habitacion1).delayElement(java.time.Duration.ofMillis(200)));

        // When & Then - El test debería pasar ya que el timeout por defecto es mayor
        webTestClient.get()
                .uri("/api/reactive/habitaciones/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }
}
