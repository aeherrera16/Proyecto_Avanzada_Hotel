package edu.espe.springlab.service.reactive;

import edu.espe.springlab.domain.Reserva;
import edu.espe.springlab.repository.ReservaRepository;
import edu.espe.springlab.repository.HuespedRepository;
import edu.espe.springlab.repository.HabitacionRepository;
import edu.espe.springlab.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias para el servicio de Reservas
 * Siguiendo el patrón AAA (Arrange, Act, Assert)
 * Autor: Herrera (Estudiante)
 */
@ExtendWith(MockitoExtension.class)
public class ReservaServiceReactivoTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HuespedRepository huespedRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private ReservaServiceReactivo reservaService;

    private Reserva reservaBase;

    @BeforeEach
    void setUp() {
        // ARRANGE: Preparar el objeto base que usaremos en las pruebas
        reservaBase = new Reserva();
        reservaBase.setId(1L);
        reservaBase.setHuespedId(1L);
        reservaBase.setHabitacionId(1L);
        reservaBase.setFechaEntrada(LocalDate.now().plusDays(1));
        reservaBase.setFechaSalida(LocalDate.now().plusDays(5));
        reservaBase.setPrecioTotal(150.0);
        reservaBase.setEstado("Confirmada");
    }

    @Nested
    @DisplayName("Pruebas de Búsqueda (Escenarios Positivos y de Recuperación)")
    class BusquedaTests {

        @Test
        @DisplayName("Debe retornar todas las reservas exitosamente")
        void testFindAll() {
            // Arrange
            when(reservaRepository.findAll()).thenReturn(Flux.just(reservaBase));

            // Act & Assert
            StepVerifier.create(reservaService.findAll())
                    .expectNext(reservaBase)
                    .verifyComplete();

            verify(reservaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe encontrar una reserva por ID")
        void testFindById_Success() {
            // Arrange
            when(reservaRepository.findById(1L)).thenReturn(Mono.just(reservaBase));

            // Act & Assert
            StepVerifier.create(reservaService.findById(1L))
                    .expectNext(reservaBase)
                    .verifyComplete();

            verify(reservaRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Debe retornar reserva por defecto cuando no existe (Recuperación)")
        void testFindById_NotFound_ReturnsDefault() {
            // Arrange: Simulamos que el repositorio no encuentra nada
            when(reservaRepository.findById(99L)).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(reservaService.findById(99L))
                    .expectNextMatches(reserva -> {
                        // Verificamos el comportamiento de recuperación definido en el servicio
                        return reserva.getId().equals(-1L) &&
                                reserva.getEstado().contains("Reserva no encontrada");
                    })
                    .verifyComplete();

            verify(reservaRepository, times(1)).findById(99L);
        }
    }

    @Nested
    @DisplayName("Pruebas de Persistencia y Validación")
    class PersistenciaTests {

        @Test
        @DisplayName("Debe guardar una reserva válida exitosamente")
        void testSave_Success() {
            // Arrange
            when(reservaRepository.save(any(Reserva.class))).thenReturn(Mono.just(reservaBase));

            // Act & Assert
            StepVerifier.create(reservaService.save(reservaBase))
                    .expectNext(reservaBase)
                    .verifyComplete();

            verify(reservaRepository, times(1)).save(any(Reserva.class));
        }

        @Test
        @DisplayName("Debe fallar al guardar con fecha de salida anterior a la entrada")
        void testSave_InvalidDates() {
            // Arrange
            reservaBase.setFechaSalida(reservaBase.getFechaEntrada().minusDays(2));

            // Act & Assert
            StepVerifier.create(reservaService.save(reservaBase))
                    .expectErrorMatches(t -> t instanceof RuntimeException &&
                            t.getMessage().equals("La fecha de salida debe ser posterior a la fecha de entrada"))
                    .verify();

            verify(reservaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe fallar al guardar con fecha de entrada en el pasado")
        void testSave_PastDate() {
            // Arrange
            reservaBase.setFechaEntrada(LocalDate.now().minusDays(1));

            // Act & Assert
            StepVerifier.create(reservaService.save(reservaBase))
                    .expectErrorMatches(t -> t instanceof RuntimeException &&
                            t.getMessage().equals("La fecha de entrada no puede ser en el pasado"))
                    .verify();

            verify(reservaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe fallar al guardar con precio igual a cero")
        void testSave_ZeroPrice() {
            // Arrange
            reservaBase.setPrecioTotal(0.0);

            // Act & Assert
            StepVerifier.create(reservaService.save(reservaBase))
                    .expectErrorMatches(t -> t instanceof RuntimeException &&
                            t.getMessage().equals("El precio total debe ser mayor a 0"))
                    .verify();

            verify(reservaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe fallar al guardar con estado no permitido")
        void testSave_InvalidStatus() {
            // Arrange
            reservaBase.setEstado("INVALIDO");

            // Act & Assert
            StepVerifier.create(reservaService.save(reservaBase))
                    .expectErrorMatches(t -> t instanceof RuntimeException &&
                            t.getMessage().startsWith("Estado no válido"))
                    .verify();

            verify(reservaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Pruebas de Eliminación")
    class EliminacionTests {

        @Test
        @DisplayName("Debe eliminar una reserva existente")
        void testDeleteById_Success() {
            // Arrange
            when(reservaRepository.findById(1L)).thenReturn(Mono.just(reservaBase));
            when(reservaRepository.deleteById(1L)).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(reservaService.deleteById(1L))
                    .verifyComplete();

            verify(reservaRepository, times(1)).deleteById(1L);
        }
    }
}
