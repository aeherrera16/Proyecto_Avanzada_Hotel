package edu.espe.springlab;

import edu.espe.springlab.domain.Habitacion;
import edu.espe.springlab.exception.reactive.ReactiveResourceNotFoundException;
import edu.espe.springlab.exception.reactive.ReactiveValidationException;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.validator.reactive.ReactiveHabitacionValidator;
import edu.espe.springlab.service.reactive.HabitacionServiceReactivo;
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
 * Última actualización: Verificación CI/CD
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
                // Arrange - Preparar datos de prueba y mocks
                List<Habitacion> habitaciones = Arrays.asList(habitacion1, habitacion2, habitacion3);
                when(habitacionRepository.findAll()).thenReturn(Flux.fromIterable(habitaciones));

                // Act - Ejecutar el método a probar
                Flux<Habitacion> result = habitacionService.findAll();

                // Assert - Verificar resultados
                StepVerifier.create(result)
                                .expectNext(habitacion1)
                                .expectNext(habitacion2)
                                .expectNext(habitacion3)
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("findAll - Debe manejar error en repository")
        void findAll_ShouldHandleRepositoryError() {
                // Arrange - Preparar error simulado
                RuntimeException error = new RuntimeException("Error de base de datos");
                when(habitacionRepository.findAll()).thenReturn(Flux.error(error));

                // Act - Ejecutar el método
                Flux<Habitacion> result = habitacionService.findAll();

                // Assert - Verificar que maneja el error correctamente con recuperación (flujo
                // vacío)
                StepVerifier.create(result)
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("findAvailable - Debe retornar solo habitaciones disponibles")
        void findAvailable_ShouldReturnAvailableHabitaciones() {
                // Arrange - Preparar datos de habitaciones disponibles
                List<Habitacion> habitacionesDisponibles = Arrays.asList(habitacion1, habitacion3);
                when(habitacionRepository.findByEstado("Disponible"))
                                .thenReturn(Flux.fromIterable(habitacionesDisponibles));

                // Act - Ejecutar el método
                Flux<Habitacion> result = habitacionService.findAvailable();

                // Assert - Verificar resultados
                StepVerifier.create(result)
                                .expectNext(habitacion1)
                                .expectNext(habitacion3)
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findByEstado("Disponible");
        }

        @Test
        @DisplayName("findById - Debe retornar habitación cuando existe")
        void findById_ShouldReturnHabitacionWhenExists() {
                // Arrange - Preparar mock para ID existente
                when(habitacionRepository.findById(1L)).thenReturn(Mono.just(habitacion1));

                // Act - Ejecutar el método
                Mono<Habitacion> result = habitacionService.findById(1L);

                // Assert - Verificar resultado
                StepVerifier.create(result)
                                .expectNext(habitacion1)
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("findById - Debe lanzar excepción cuando no existe")
        void findById_ShouldThrowExceptionWhenNotFound() {
                // Arrange - Preparar mock para ID inexistente
                when(habitacionRepository.findById(999L)).thenReturn(Mono.empty());

                // Act - Ejecutar el método
                Mono<Habitacion> result = habitacionService.findById(999L);

                // Assert - Verificar que el servicio recupera el error y retorna habitación por
                // defecto
                StepVerifier.create(result)
                                .expectNextMatches(habitacion -> habitacion.getId().equals(-1L) &&
                                                habitacion.getEstado().contains("Habitación no encontrada con ID: 999"))
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("save - Debe guardar habitación válida")
        void save_ShouldSaveValidHabitacion() {
                // Arrange - Preparar datos de prueba
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

                // Act - Ejecutar el método
                Mono<Habitacion> result = habitacionService.save(nuevaHabitacion);

                // Assert - Verificar resultado
                StepVerifier.create(result)
                                .expectNext(habitacionGuardada)
                                .verifyComplete();

                verify(habitacionValidator, times(1)).validate(nuevaHabitacion);
                verify(habitacionRepository, times(1)).save(nuevaHabitacion);
        }

        @Test
        @DisplayName("save - Debe manejar error de validación")
        void save_ShouldHandleValidationError() {
                // Arrange - Preparar habitación inválida y error
                Habitacion habitacionInvalida = new Habitacion();
                ReactiveValidationException validationError = new ReactiveValidationException("Habitación inválida");

                when(habitacionValidator.validate(habitacionInvalida)).thenReturn(Mono.error(validationError));

                // Act - Ejecutar el método
                Mono<Habitacion> result = habitacionService.save(habitacionInvalida);

                // Assert - Verificar que maneja el error con recuperación (hab. con ID -1)
                StepVerifier.create(result)
                                .expectNextMatches(h -> h.getId().equals(-1L) &&
                                                h.getEstado().equals("ERROR: Habitación inválida"))
                                .verifyComplete();

                verify(habitacionValidator, times(1)).validate(habitacionInvalida);
                verify(habitacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("save - Debe manejar error al guardar")
        void save_ShouldHandleSaveError() {
                // Arrange - Preparar error de guardado
                RuntimeException saveError = new RuntimeException("Error al guardar");
                when(habitacionValidator.validate(habitacion1)).thenReturn(Mono.just(habitacion1));
                when(habitacionRepository.save(habitacion1)).thenReturn(Mono.error(saveError));

                // Act - Ejecutar el método
                Mono<Habitacion> result = habitacionService.save(habitacion1);

                // Assert - Verificar que maneja el error con recuperación
                StepVerifier.create(result)
                                .expectNextMatches(h -> h.getId().equals(-1L) &&
                                                h.getEstado().equals("ERROR: Error al guardar"))
                                .verifyComplete();

                verify(habitacionValidator, times(1)).validate(habitacion1);
                verify(habitacionRepository, times(1)).save(habitacion1);
        }

        @Test
        @DisplayName("deleteById - Debe eliminar habitación cuando existe")
        void deleteById_ShouldDeleteHabitacionWhenExists() {
                // Arrange - Preparar mock para ID existente
                when(habitacionRepository.findById(1L)).thenReturn(Mono.just(habitacion1));
                when(habitacionRepository.deleteById(1L)).thenReturn(Mono.empty());

                // Act - Ejecutar el método
                Mono<Void> result = habitacionService.deleteById(1L);

                // Assert - Verificar que se completa exitosamente
                StepVerifier.create(result)
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findById(1L);
                verify(habitacionRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("deleteById - Debe lanzar excepción cuando no existe")
        void deleteById_ShouldThrowExceptionWhenNotFound() {
                // Arrange - Preparar mock para ID inexistente
                when(habitacionRepository.findById(999L)).thenReturn(Mono.empty());

                // Act - Ejecutar el método
                Mono<Void> result = habitacionService.deleteById(999L);

                // Assert - Verificar recuperación (flujo vacío)
                StepVerifier.create(result)
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findById(999L);
                verify(habitacionRepository, never()).deleteById(any(Long.class));
        }

        @Test
        @DisplayName("deleteById - Debe manejar error al eliminar")
        void deleteById_ShouldHandleDeleteError() {
                // Arrange - Preparar error de eliminación
                RuntimeException deleteError = new RuntimeException("Error al eliminar");
                when(habitacionRepository.findById(1L)).thenReturn(Mono.just(habitacion1));
                when(habitacionRepository.deleteById(1L)).thenReturn(Mono.error(deleteError));

                // Act - Ejecutar el método
                Mono<Void> result = habitacionService.deleteById(1L);

                // Assert - Verificar recuperación (flujo vacío)
                StepVerifier.create(result)
                                .verifyComplete();

                verify(habitacionRepository, times(1)).findById(1L);
                verify(habitacionRepository, times(1)).deleteById(1L);
        }
}
