package edu.espe.springlab.service.reactive;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.exception.reactive.ReactiveResourceNotFoundException;
import edu.espe.springlab.exception.reactive.ReactiveValidationException;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.validator.reactive.ReactiveHabitacionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para HabitacionServiceReactivo usando StepVerifier
 * Verifica el comportamiento reactivo de los servicios de habitaciones
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de HabitacionServiceReactivo")
class HabitacionServiceReactivoTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private ReactiveHabitacionValidator habitacionValidator;

    @InjectMocks
    private HabitacionServiceReactivo habitacionService;

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
    @DisplayName("findAll - Debe retornar todas las habitaciones")
    void findAll_ShouldReturnAllHabitaciones() {
        // Given
        List<Habitacion> habitaciones = Arrays.asList(habitacion1, habitacion2, habitacion3);
        when(habitacionRepository.findAll()).thenReturn(Flux.fromIterable(habitaciones));

        // When & Then
        StepVerifier.create(habitacionService.findAll())
                .expectNext(habitacion1)
                .expectNext(habitacion2)
                .expectNext(habitacion3)
                .verifyComplete();

        verify(habitacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - Debe manejar error en repository")
    void findAll_ShouldHandleRepositoryError() {
        // Given
        RuntimeException error = new RuntimeException("Error de base de datos");
        when(habitacionRepository.findAll()).thenReturn(Flux.error(error));

        // When & Then
        StepVerifier.create(habitacionService.findAll())
                .expectErrorMatches(throwable -> 
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error de base de datos"))
                .verify();

        verify(habitacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAvailable - Debe retornar solo habitaciones disponibles")
    void findAvailable_ShouldReturnAvailableHabitaciones() {
        // Given
        List<Habitacion> habitacionesDisponibles = Arrays.asList(habitacion1, habitacion3);
        when(habitacionRepository.findByEstado("Disponible")).thenReturn(Flux.fromIterable(habitacionesDisponibles));

        // When & Then
        StepVerifier.create(habitacionService.findAvailable())
                .expectNext(habitacion1)
                .expectNext(habitacion3)
                .verifyComplete();

        verify(habitacionRepository, times(1)).findByEstado("Disponible");
    }

    @Test
    @DisplayName("findById - Debe retornar habitación cuando existe")
    void findById_ShouldReturnHabitacionWhenExists() {
        // Given
        when(habitacionRepository.findById(1L)).thenReturn(Mono.just(habitacion1));

        // When & Then
        StepVerifier.create(habitacionService.findById(1L))
                .expectNext(habitacion1)
                .verifyComplete();

        verify(habitacionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - Debe lanzar excepción cuando no existe")
    void findById_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(habitacionRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(habitacionService.findById(999L))
                .expectErrorMatches(throwable ->
                        throwable instanceof ReactiveResourceNotFoundException &&
                        throwable.getMessage().contains("Habitación no encontrada con ID: 999"))
                .verify();

        verify(habitacionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("save - Debe guardar habitación válida")
    void save_ShouldSaveValidHabitacion() {
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

        when(habitacionValidator.validate(nuevaHabitacion)).thenReturn(Mono.just(nuevaHabitacion));
        when(habitacionRepository.save(nuevaHabitacion)).thenReturn(Mono.just(habitacionGuardada));

        // When & Then
        StepVerifier.create(habitacionService.save(nuevaHabitacion))
                .expectNext(habitacionGuardada)
                .verifyComplete();

        verify(habitacionValidator, times(1)).validate(nuevaHabitacion);
        verify(habitacionRepository, times(1)).save(nuevaHabitacion);
    }

    @Test
    @DisplayName("save - Debe manejar error de validación")
    void save_ShouldHandleValidationError() {
        // Given
        Habitacion habitacionInvalida = new Habitacion();
        ReactiveValidationException validationError = new ReactiveValidationException("Habitación inválida");

        when(habitacionValidator.validate(habitacionInvalida)).thenReturn(Mono.error(validationError));

        // When & Then
        StepVerifier.create(habitacionService.save(habitacionInvalida))
                .expectErrorMatches(throwable ->
                        throwable instanceof ReactiveValidationException &&
                        throwable.getMessage().equals("Habitación inválida"))
                .verify();

        verify(habitacionValidator, times(1)).validate(habitacionInvalida);
        verify(habitacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("save - Debe manejar error al guardar")
    void save_ShouldHandleSaveError() {
        // Given
        RuntimeException saveError = new RuntimeException("Error al guardar");
        when(habitacionValidator.validate(habitacion1)).thenReturn(Mono.just(habitacion1));
        when(habitacionRepository.save(habitacion1)).thenReturn(Mono.error(saveError));

        // When & Then
        StepVerifier.create(habitacionService.save(habitacion1))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error al guardar"))
                .verify();

        verify(habitacionValidator, times(1)).validate(habitacion1);
        verify(habitacionRepository, times(1)).save(habitacion1);
    }

    @Test
    @DisplayName("deleteById - Debe eliminar habitación cuando existe")
    void deleteById_ShouldDeleteHabitacionWhenExists() {
        // Given
        when(habitacionRepository.findById(1L)).thenReturn(Mono.just(habitacion1));
        when(habitacionRepository.deleteById(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(habitacionService.deleteById(1L))
                .verifyComplete();

        verify(habitacionRepository, times(1)).findById(1L);
        verify(habitacionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById - Debe lanzar excepción cuando no existe")
    void deleteById_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(habitacionRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(habitacionService.deleteById(999L))
                .expectErrorMatches(throwable ->
                        throwable instanceof ReactiveResourceNotFoundException &&
                        throwable.getMessage().contains("Habitación no encontrada con ID: 999"))
                .verify();

        verify(habitacionRepository, times(1)).findById(999L);
        verify(habitacionRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("deleteById - Debe manejar error al eliminar")
    void deleteById_ShouldHandleDeleteError() {
        // Given
        RuntimeException deleteError = new RuntimeException("Error al eliminar");
        when(habitacionRepository.findById(1L)).thenReturn(Mono.just(habitacion1));
        when(habitacionRepository.deleteById(1L)).thenReturn(Mono.error(deleteError));

        // When & Then
        StepVerifier.create(habitacionService.deleteById(1L))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error al eliminar"))
                .verify();

        verify(habitacionRepository, times(1)).findById(1L);
        verify(habitacionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test de backpressure con StepVerifier")
    void testBackpressureBehavior() {
        // Given
        Flux<Habitacion> habitacionFlux = Flux.range(1, 1000)
                .map(i -> {
                    Habitacion h = new Habitacion();
                    h.setId((long) i);
                    h.setNumero(String.valueOf(100 + i));
                    h.setTipo("Simple");
                    h.setPrecio(50.0);
                    h.setEstado("Disponible");
                    return h;
                });

        when(habitacionRepository.findAll()).thenReturn(habitacionFlux);

        // When & Then - Verificar que el flujo puede manejar muchos elementos
        StepVerifier.create(habitacionService.findAll().take(10))
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    @DisplayName("Test de timeout con StepVerifier")
    void testTimeoutBehavior() {
        // Given
        when(habitacionRepository.findById(1L))
                .thenReturn(Mono.just(habitacion1).delayElement(java.time.Duration.ofMillis(100)));

        // When & Then - Verificar que la operación completa dentro del tiempo esperado
        StepVerifier.create(habitacionService.findById(1L))
                .expectNext(habitacion1)
                .verifyComplete();
    }
}
